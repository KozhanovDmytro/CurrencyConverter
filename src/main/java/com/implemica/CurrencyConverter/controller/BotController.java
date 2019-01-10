package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class gets users input from telegram bot and processes it via Converter class
 *
 * @see BotService
 * @see com.implemica.CurrencyConverter.model.Converter
 *
 * @author Daria S.
 * @version 27.12.2018 12:27
 */
@Component
public class BotController extends TelegramLongPollingBot {
   /**
    * Logger for this class
    */
   private Logger log = Logger.getLogger(BotController.class.getName());
   /**
    * Bot name in Telegram.
    */
   private static final String BOT_NAME = "CurrencyConverterImplemicaBot";
   /**
    * It uses for getting and sending messages via Bot API
    */
   private static final String BOT_TOKEN = "736932538:AAFW981ptLJ4g1lbsVTn7HebaojMKLClEDg";

   /**
    * Unique string, which uses for messages, which has non text content.
    */
   private static final String UNIQUE = BotService.UNIQUE;

   /**
    * Logic of bot, how it processes user's input.
    */
   private final BotService bot;

   /**
    * Creates new telegram bot's controller
    *
    * @param bot stores bot's logic
    */
   @Autowired
   public BotController(BotService bot) {
      this.bot = bot;
   }

   /**
    * Gets users input and processes it. Writes conversation to .csv file.
    *
    * @param update represents an incoming update from Telegram
    */
   @Override
   public void onUpdateReceived(Update update) {
      Message newMessage = update.getMessage();
      String command = newMessage.getText();
      String message;

      //get information about user
      User user = getInformationAboutUser(update);

      //dialog with user
      if (!newMessage.hasText()) {
         command = UNIQUE;
      }

      //response to user
      message = bot.onUpdateReceived(command, user);
      sendMessage(update, message);
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
    * @param update  represents an incoming update from Telegram
    * @param message message, which has to be sent to user
    */
   private void sendMessage(Update update, String message) {
      SendMessage sendMessage = new SendMessage();
      sendMessage.setText(message);
      sendMessage.setChatId(update.getMessage().getChatId());
      try {
         execute(sendMessage);
      } catch (TelegramApiException e) {
         log.log(Level.SEVERE, e.getMessage());
      }
   }

   /**
    * User initialisation: id, name, last name, userName.
    *
    * @param update represents an incoming update from Telegram
    * @return new User
    */
   private User getInformationAboutUser(Update update) {
      org.telegram.telegrambots.meta.api.objects.User botUser = update.getMessage().getFrom();
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

