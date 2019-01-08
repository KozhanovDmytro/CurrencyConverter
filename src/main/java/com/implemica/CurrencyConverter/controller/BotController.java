package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * This class gets users input from telegram bot and processes it via Converter class
 *
 * @author Daria S.
 * @version 27.12.2018 12:27
 */
@Component
public class BotController extends TelegramLongPollingBot {


   private static final String CONVERT_MESSAGE = "You can use /convert to make me new convert currencies";
   private static final String BOT_NAME = "CurrencyConverterImplemicaBot";
   private static final String BOT_TOKEN = "736932538:AAFW981ptLJ4g1lbsVTn7HebaojMKLClEDg";

   /**
    * Bot's logic
    */
   @Autowired
   private BotService bot;

   /**
    * Gets users input and processes it. Writes conversation to .csv file.
    */
   @Override
   public void onUpdateReceived(Update update) {

      String command = update.getMessage().getText();
      String message;

      //get information about user
      User user = getInformationAboutUser(update);

      //dialog with user
      if (!update.getMessage().hasText()) {
         message = "Sorry, but this message contains incorrect content. Please, don't send me messages, which I can't handle. " + CONVERT_MESSAGE;
         command = "Users message has incorrect content.";
      } else {
         message = bot.onUpdateReceived(command, user);
      }
      sendMessage(update, message);
   }

   @Override
   public String getBotUsername() {
      return BOT_NAME;
//      return "daras_bot";
   }

   @Override
   public String getBotToken() {
      return BOT_TOKEN;
//      return "717479855:AAHstabH8JLkZ3oM7cXtleuGd_I1W38Z1Jg";
   }

   /**
    * Shows some message to user
    */
   private void sendMessage(Update update, String message) {
      SendMessage sendMessage = new SendMessage();
      sendMessage.setText(message);
      sendMessage.setChatId(update.getMessage().getChatId());
      try {
         execute(sendMessage);
      } catch (TelegramApiException e) {
         e.printStackTrace();
      }
   }

   /**
    * User initialisation: id, name, last name
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

