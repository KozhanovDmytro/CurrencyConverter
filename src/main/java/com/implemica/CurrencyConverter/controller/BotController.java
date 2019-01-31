package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.model.ConvertStep;
import com.implemica.CurrencyConverter.model.State;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.model.UsersRequest;
import com.implemica.CurrencyConverter.service.BotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

import static com.implemica.CurrencyConverter.model.ConvertStep.*;
import static java.lang.Math.toIntExact;


/**
 * This class gets statesOfUsers input from telegram bot and processes it via Converter class
 *
 * @author Daria S.
 * @version 11.01.2019 17:48
 * @see BotService
 * @see UsersRequest
 */
@Component
public class BotController extends TelegramLongPollingBot {

   /**
    * Bot name in Telegram.
    */
   @Value("${telegram.botName}")
   private String BOT_NAME;

   /**
    * It uses for getting and sending messages via Bot API
    */
   @Value("${telegram.botToken}")
   private String BOT_TOKEN;

   /**
    * Logger for this class
    */
   private Logger logger = LoggerFactory.getLogger(BotController.class.getName());
   /**
    * Unique string, which uses for identification messages, which has non-text content.
    */
   private static final String UNIQUE = BotService.WRONG_CONTENT;

   /**
    * Logic of bot, how it processes user's input.
    */
   private final BotService bot;

   /**
    * Sender messages to user
    */
   private SendMessage sendMessage;

   /**
    * Id of chat
    */
   private long chatId;

   /**
    * Stores users and id chats, where they communicate with bot
    */
   private static Map<User, Long> listOfChats = State.listOfChats;

   /**
    * Stores users states
    */
   private static Map<Integer, State> usersStates = State.statesOfUsers;

   /**
    * Creates new telegram bot's controller
    *
    * @param bot stores bot's logic
    */
   @Autowired
   public BotController(BotService bot) {
      this.bot = bot;
      sendMessage = new SendMessage();
   }

   /**
    * Gets statesOfUsers input and processes it. Writes conversation to storage
    *
    * @param update represents an incoming update from Telegram
    */
   @Override
   public void onUpdateReceived(Update update) {
      Message message;
      User user;
      String command;
      boolean isBot = false;

      if (update.hasMessage()) {
         message = update.getMessage();
         command = getCommand(message);

      } else {
         CallbackQuery callbackQuery = update.getCallbackQuery();
         command = callbackQuery.getData();

         message = callbackQuery.getMessage();

         long message_id = message.getMessageId();
         chatId = message.getChatId();

         isBot = true;
         String answer = "You chose " + command;
         sendEditMessage(message_id, answer);
      }

      user = getInformationAboutUser(message);
      if (isBot) {
         user = chooseUser(user);
      }

      String response = bot.processCommand(command, user);
      sendMessage(message, response);

      int userId = user.getUserId();
      ConvertStep step = getStep(userId);

      if (step.equals(FIRST) || step.equals(SECOND)) {
         SendMessage s = new SendMessage().setChatId(chatId).setText("You can choose popular currencies: ");
         createKeyboard(s);
      }

      listOfChats.put(user, chatId);
   }

   /**
    * Returns command to processing
    *
    * @param message incoming message
    * @return String command
    */
   private String getCommand(Message message) {
      String command;
      chatId = message.getChatId();
      if (message.hasText()) {
         command = message.getText();
      } else {
         command = UNIQUE;
      }
      return command;
   }

   /**
    * @return bot's name in Telegram
    */
   @Override
   public String getBotUsername() {
      return BOT_NAME;
   }

   /**
    * @return bot's token in Telegram
    */
   @Override
   public String getBotToken() {
      return BOT_TOKEN;
   }

   /**
    * Sends message to user in Telegram
    *
    * @param request  represents an incoming message from Telegram
    * @param response message, which has to be sent to user
    */
   private void sendMessage(Message request, String response) {
      sendMessage.setText(response);
      sendMessage.setChatId(request.getChatId());
      try {
         execute(sendMessage);
      } catch (TelegramApiException e) {
         logger.error(e.getMessage());
      }
   }

   /**
    * Changes message's text to specified
    *
    * @param messageId id if message, which has to be changed
    * @param text      new text of message
    */
   private void sendEditMessage(long messageId, String text) {
      EditMessageText new_message = new EditMessageText()
              .setChatId(chatId)
              .setMessageId(toIntExact(messageId))
              .setText(text);
      try {
         execute(new_message);
      } catch (TelegramApiException e) {
         logger.error(e.getMessage());
      }
   }

   /**
    * User initialisation: id, name, last name, userName.
    *
    * @param message represents an incoming message from Telegram
    * @return new User
    */
   User getInformationAboutUser(Message message) {
      org.telegram.telegrambots.meta.api.objects.User telegramUser = message.getFrom();

      int userId = telegramUser.getId();
      String userFirstName = telegramUser.getFirstName();
      String userLastName = telegramUser.getLastName();
      String userName = telegramUser.getUserName();

      User user;
      if (userName == null || userLastName == null) {
         user = new User(userId, userFirstName);
         if (userName != null) {
            user.setUserName(userName);
         }
         if (userLastName != null) {
            user.setUserLastName(userLastName);
         }
      } else {
         user = new User(userId, userFirstName, userLastName, userName);
      }

      return user;
   }

   /**
    * Create inline keyboard with popular currencies
    *
    * @param sendMessage new sender messages to user
    */
   private void createKeyboard(SendMessage sendMessage) {
      List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

      List<InlineKeyboardButton> row = new ArrayList<>();

      row.add(new InlineKeyboardButton().setText("USD").setCallbackData("USD"));
      row.add(new InlineKeyboardButton().setText("EUR").setCallbackData("EUR"));
      row.add(new InlineKeyboardButton().setText("BTC").setCallbackData("BTC"));
      row.add(new InlineKeyboardButton().setText("UAH").setCallbackData("UAH"));
      buttons.add(row);

      InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
      markupKeyboard.setKeyboard(buttons);
      sendMessage.setReplyMarkup(markupKeyboard);
      try {
         execute(sendMessage);
      } catch (TelegramApiException e) {
         logger.error(e.getMessage());
      }
   }

   /**
    * Finds user, which talk to bot
    *
    * @param user bot user
    */
   private User chooseUser(User user) {
      Set<Map.Entry<User, Long>> entrySet = listOfChats.entrySet();
      for (Map.Entry<User, Long> pair : entrySet) {
         long id = pair.getValue();
         if (chatId == id) {
            user = pair.getKey();
         }
      }
      return user;
   }

   /**
    * Finds User by their key and gets their conversion step
    *
    * @param id users id
    * @return step of conversion for given user
    */
   private ConvertStep getStep(int id) {
      State state = usersStates.get(id);
      return state.getConvertStep();
   }
}

