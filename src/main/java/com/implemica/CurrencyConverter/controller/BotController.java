package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * This class gets users input from telegram bot and processes it via Converter class
 *
 * @author Daria S.
 * @version 11.01.2019 17:48
 * @see BotService
 * @see com.implemica.CurrencyConverter.model.Converter
 */
@Component
public class BotController extends TelegramLongPollingBot {
   /**
    * Logger for this class
    */
   private Logger logger = LoggerFactory.getLogger(BotController.class.getName());
   /**
    * Bot name in Telegram.
    */
   private static final String BOT_NAME = "currConvBot";
   /**
    * It uses for getting and sending messages via Bot API
    */
   private static final String BOT_TOKEN = "760246131:AAHZf7R9NZbVxxiDh4Dtn_76CH5-8LSpEG4";

   /**
    * Unique string, which uses for identification messages, which has non-text content.
    */
   private static final String UNIQUE = BotService.UNIQUE;

   /**
    * Logic of bot, how it processes user's input.
    */
   private final BotService bot;

   /**
    * Sender messages to user
    */
   private SendMessage sendMessage;

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
    * Gets users input and processes it. Writes conversation to .csv file.
    *
    * @param update represents an incoming update from Telegram
    */
   @Override
   public void onUpdateReceived(Update update) {
      Message request = update.getMessage();
      String command = request.getText();
      String response;

      //get information about user
      User user = getInformationAboutUser(request);

      //dialog with user
      if (!request.hasText()) {
         command = UNIQUE;
      }

      //response to user
      response = bot.onUpdateReceived(command, user);
      sendMessage(request, response);
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
    * User initialisation: id, name, last name, userName.
    *
    * @param message represents an incoming message from Telegram
    * @return new User
    */
   User getInformationAboutUser(Message message) {
      org.telegram.telegrambots.meta.api.objects.User botUser = message.getFrom();
      int userId = botUser.getId();
      User user;
      String userFirstName = botUser.getFirstName();
      String userLastName = botUser.getLastName();
      String userName = botUser.getUserName();

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

}

