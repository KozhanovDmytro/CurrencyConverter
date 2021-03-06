package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.model.ConvertStep;
import com.implemica.CurrencyConverter.model.State;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * This class tests BotController class
 *
 * @author Daria S.
 * @version 31.01.2019 17:06
 */
@ExtendWith(MockitoExtension.class)
public class BotControllerTest {

   /**
    * Start command
    */
   private static final String start = "/start";
   /**
    * Stop command
    */
   private static final String stop = "/stop";
   /**
    * Convert command
    */
   private static final String convert = "/convert";
   /**
    * Some non-command word
    */
   private static final String hello = "hello";

   /**
    * Message to the user with the suggestion of a new conversion
    */
   private static final String CONVERT_MESSAGE = "\nYou can make a new currency conversion: \n\n" +
           "➡️ using /convert command \nor\n➡️ single line command " +
           "(E. g. : 10 USD in UAH)";
   /**
    * Bot's response for start command
    */
   private static final String startMessage = "\uD83D\uDC4B Hello! I can help you to convert currencies."
           + CONVERT_MESSAGE + "\n\nSo, how can I help you?";

   /**
    * Bot's response for stop command
    */
   private static final String stopMessage = "🆗." + CONVERT_MESSAGE;
   /**
    * Bot's response for convert command
    */
   private static final String firstConvertMessage = "Please, type in the currency to convert from (E. g. : USD)";
   /**
    * Bot's response for non-command word
    */
   private static final String wrongMessage = "❗Sorry, but your request is incorrect." + CONVERT_MESSAGE;
   /**
    * Bot's response for message, which contains incorrect content
    */
   private static final String wrongContentMessage = "❗Sorry, but this message contains " +
           "incorrect content. Please, don't send me messages, which I can't handle." + CONVERT_MESSAGE;

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
    * A dummy implementation for BotService class, represents bot's logic
    */
   @Mock
   private BotService botService;

   /**
    * A dummy implementation for Update class, incoming update from telegram user
    */
   @Mock
   private Update update;

   /**
    * A dummy implementation for Message class, message from telegram user
    */
   @Mock
   private Message message;

   /**
    * A dummy implementation for CallbackQuery class, callback query from telegram user
    */
   @Mock
   private CallbackQuery callbackQuery;

   /**
    * Stores users and id chats, where they communicate with bot
    */
   private static Map<User, Long> listOfChats = State.listOfChats;

   /**
    * Stores all statesOfUsers, which use bot and their last command
    */
   private static Map<Integer, State> statesOfUsers = State.statesOfUsers;

   /**
    * A dummy implementation for Telegram User class
    */
   @Mock
   private org.telegram.telegrambots.meta.api.objects.User telegramUser;

   /**
    * A dummy implementation for Telegram bot
    */
   @Mock
   private org.telegram.telegrambots.meta.api.objects.User bot;


   /**
    * BotController object, in which mocked object's have to be injected
    */
   @InjectMocks
   private BotController controller;


   /**
    * Creates new BotController, which depends on mocked botService
    */
   @BeforeEach
   void setUp() {
      controller = new BotController(botService);
   }

   /**
    * Tests, that methods returns bots username and token correctly
    */
   @Test
   void test() {
      assertEquals(BOT_NAME, controller.getBotUsername());
      assertEquals(BOT_TOKEN, controller.getBotToken());
   }

   /**
    * Tests, that if user has all information about himself and his message contains specified text, and if method
    * {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()} and
    * {@link BotService#processCommand(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest0() {
      //update from user
      setMessageBehavior("473982.78 usd to usd", true);

      //user with all information
      createUser(378219, "jane", "brown", "cloudy");

      verifyController("473982.78 usd to usd", "\uD83D\uDCB0 473982.78 usd is 473982.78 usd");
   }


   /**
    * Tests, that if user has all information about himself and his message contains specified text, and if method
    * {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()} and
    * {@link BotService#processCommand(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest1() {
      //update from user
      setMessageBehavior(start, true);

      //user with all information
      createUser(145, "ludvig", "fourteenth", "fox");

      verifyController(start, startMessage);
   }

   /**
    * Tests, that if information about user doesn't contain his last name and his message contains specified text,
    * and if method {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()}
    * and {@link BotService#processCommand(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest2() {
      //update from user
      setMessageBehavior(stop, true);

      //user without last name
      createUser(1289, "Laban", null, "fox");

      verifyController(stop, stopMessage);
   }

   /**
    * Tests, that if information about user doesn't contain his username name and his message contains specified text,
    * and if method {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()}
    * and {@link BotService#processCommand(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest3() {
      //update from user
      setMessageBehavior(convert, true);

      //user without username
      createUser(1111, "dad", "Larsson", null);

      verifyController(convert, firstConvertMessage);
   }

   /**
    * Tests, that if information about user doesn't contain his last name and username, and his message contains specified text,
    * and if method {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()}
    * and {@link BotService#processCommand(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest4() {
      //update from user
      setMessageBehavior(hello, true);

      //user without last name and username
      createUser(74, "squirrel", null, null);

      verifyController(hello, wrongMessage);
   }

   /**
    * Tests, that if user has all information about himself and his message doesn't contains text, and if method
    * {@link BotController#onUpdateReceived(Update)} is called, then methods {@link Update#getMessage()} and
    * {@link BotService#processCommand(String, User)} is called with specified parameters
    */
   @Test
   void onUpdateReceivedTest5() {
      //update from user
      setMessageBehavior(null, false);

      createUser(38210, "tutta", "carlson", "chicken");
      verifyController(BotService.WRONG_CONTENT, wrongContentMessage);
   }

   /**
    * Tests reaction for inlineKeyboard press
    */
   @Test
   void inlineKeyboardTest1() {
      //user press the button
      setCallbackQueryBehavior("USD");
      verifyControllerWithKeyboard("USD", 124, 73281, "ann", null, null);
   }

   /**
    * Tests reaction for inlineKeyboard press
    */
   @Test
   void inlineKeyboardTest2() {
      setCallbackQueryBehavior("EUR");
      verifyControllerWithKeyboard("EUR", 13, 9821, "michael", "green", null);
   }

   /**
    * Tests reaction for inlineKeyboard press
    */
   @Test
   void inlineKeyboardTest3() {
      setCallbackQueryBehavior("UAH");
      verifyControllerWithKeyboard("UAH", 7801, 21809, "jake", "black", "lucky");
   }

   /**
    * Tests reaction for inlineKeyboard press
    */
   @Test
   void inlineKeyboardTest4() {
      setCallbackQueryBehavior("BTC");
      verifyControllerWithKeyboard("BTC", 89302, 588, "samanta", null, "sam");
   }

   /**
    * Defines the return value when update and message methods of the mocked objects are been called
    *
    * @param messageText text of user's message
    * @param hasText     true, if message has correct content (text), false - otherwise
    */
   private void setMessageBehavior(String messageText, boolean hasText) {
      when(update.hasMessage()).thenReturn(true);
      when(update.getMessage()).thenReturn(message);
      when(message.hasText()).thenReturn(hasText);

      if (hasText) {
         when(message.getText()).thenReturn(messageText);
      }

      when(message.getFrom()).thenReturn(telegramUser);
   }

   /**
    * Defines the return value when update and callbackQuery methods of the mocked objects are been called
    *
    * @param text text which gets from callbackQuery
    */
   private void setCallbackQueryBehavior(String text) {
      when(update.hasMessage()).thenReturn(false);
      when(update.getCallbackQuery()).thenReturn(callbackQuery);
      when(callbackQuery.getMessage()).thenReturn(message);
      when(callbackQuery.getData()).thenReturn(text);
      when(message.getFrom()).thenReturn(bot);
   }


   /**
    * Defines the return value when Telegram User methods of the mocked object is been called
    *
    * @param id       user's id
    * @param name     user's name
    * @param lastName user's last name
    * @param userName user's username
    */
   private void createUser(int id, String name, String lastName, String userName) {
      when(telegramUser.getId()).thenReturn(id);
      when(telegramUser.getFirstName()).thenReturn(name);
      when(telegramUser.getLastName()).thenReturn(lastName);
      when(telegramUser.getUserName()).thenReturn(userName);

      statesOfUsers.put(id, new State("", "", ConvertStep.ZERO));
   }

   /**
    * Create bot for user by chatId
    *
    * @param chatId id of chat user with bot
    */
   private void createBot(long chatId) {
      when(bot.getId()).thenReturn(555555);
      when(bot.getFirstName()).thenReturn("bot");
      when(bot.getLastName()).thenReturn(null);
      when(bot.getUserName()).thenReturn(null);
      when(message.getChatId()).thenReturn(chatId);
   }


   /**
    * Creates User. Defines the return value when processCommand method of the mocked object botService is been called.
    * Calls {@link BotController#onUpdateReceived(Update)} method and verifies, that methods {@link Update#getMessage()}
    * and {@link BotService#processCommand(String, User)} were called with specified parameters.
    *
    * @param messageText  message from User
    * @param responseText response to User
    */
   private void verifyController(String messageText, String responseText) {
      User user = controller.getInformationAboutUser(message);
      doReturn(responseText).when(botService).processCommand(messageText, user);

      controller.onUpdateReceived(update);

      verify(update).getMessage();
      verify(botService).processCommand(messageText, user);
   }

   /**
    * Creates User. Defines the return value when processCommand method of the mocked object botService is been called.
    * Calls {@link BotController#onUpdateReceived(Update)} method and verifies, that methods {@link Update#getMessage()}
    * and {@link BotService#processCommand(String, User)} were called with specified parameters.
    *
    * @param messageText message from User
    * @param chatId      id of chat
    * @param userId      user's id
    * @param firstName   user's first name
    * @param lastName    user's last name
    * @param userName    user's username
    */
   private void verifyControllerWithKeyboard(String messageText, long chatId, int userId, String firstName, String lastName, String userName) {
      createBot(chatId);
      User user = new User(userId, firstName, lastName, userName);

      listOfChats.put(user, chatId);
      statesOfUsers.put(userId, new State("", "", ConvertStep.ZERO));

      doReturn(BotControllerTest.wrongMessage).when(botService).processCommand(messageText, user);

      controller.onUpdateReceived(update);

      verify(update).getCallbackQuery();
      verify(botService).processCommand(messageText, user);
   }
}
