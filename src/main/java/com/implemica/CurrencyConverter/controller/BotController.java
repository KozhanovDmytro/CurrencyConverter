package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.dao.impl.TransactionDaoImpl;
import com.implemica.CurrencyConverter.model.Converter;
import com.implemica.CurrencyConverter.model.Transaction;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.validator.BotValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.money.UnknownCurrencyException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * This class gets users input from telegram bot and processes it via Converter class
 *
 * @author daria solodkova
 */
@Component
public class BotController extends TelegramLongPollingBot {

   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = "Hello! Could I help you?";
   /**
    * Stop message to user
    */
   private static final String STOP_MESSAGE = "OK. You can use /convert to make me convert currencies";

   /**
    * Bot's command to start conversation
    */
   private static final String START_COMMAND = "/start";
   /**
    * Bot's command to start convert currencies
    */
   private static final String CONVERT_COMMAND = "/convert";
   /**
    * Bot's command to stop conversation
    */
   private static final String STOP_COMMAND = "/stop";

   private boolean gotFirstCurrency = false;
   private boolean gotSecondCurrency = false;
   private boolean gotValue = false;
   private String firstCurrency;
   private String secondCurrency;

   /**
    * Information about User
    */
   private User user;

   /**
    * Data, which has to be added to .csv file
    */
   TransactionDaoImpl transaction;

   @Autowired
   private Converter converter;

   /**
    * Gets users input and processes it. Writes conversation to .csv file.
    */
   @Override
   public void onUpdateReceived(Update update) {
      String command = update.getMessage().getText();
      String message;

      //get information about user
      getInformationAboutUser(update);
      Date dateNow = new Date();

      //dialog with user
      if (command.equals(START_COMMAND)) {
         message = START_MESSAGE;
         gotFirstCurrency = false;
         gotSecondCurrency = false;
         gotValue = false;
      } else if (command.equals(STOP_COMMAND)) {
         message = STOP_MESSAGE;
         gotFirstCurrency = false;
         gotSecondCurrency = false;
         gotValue = false;
      } else if (command.equals(CONVERT_COMMAND)) {
         message = "Please, type in the currency to convert from (example: USD)";
         gotFirstCurrency = true;
      } else if (gotFirstCurrency) {
         firstCurrency = BotValidator.toUpperCase(update.getMessage().getText());
         message = "OK, you wish to convert from " + firstCurrency + " to what currency? (example: EUR)";
         gotFirstCurrency = false;
         gotSecondCurrency = true;
      } else if (gotSecondCurrency) {
         secondCurrency = BotValidator.toUpperCase(update.getMessage().getText());
         message = "Enter the amount to convert from " + firstCurrency + " to " + secondCurrency;
         gotSecondCurrency = false;
         gotValue = true;
      } else if (gotValue) {
         String value = update.getMessage().getText();
         String amount = value.replace(',', '.');
         if (BotValidator.isCorrectNumber(value)) {
            try {
               Float convertedValue = converter.convert(firstCurrency, secondCurrency, Float.parseFloat(amount));
               message = value + " " + firstCurrency + " is " + convertedValue + " " + secondCurrency;
            } catch (UnknownCurrencyException ex) {
               message = ex.getMessage();
            }
         } else {
            message = "Sorry, but \"" + value + "\" is not a valid number. Conversion is impossible.";
         }
         firstCurrency = "";
         secondCurrency = "";
         gotValue = false;
      } else {
         String word = update.getMessage().getText();
         message = "Sorry, but I don't understand what does \"" + word + "\" mean.";
         gotFirstCurrency = false;
         gotSecondCurrency = false;
         gotValue = false;
      }
      sendMessage(update, message);
      dateNow = new Date();

      try {
         transaction = new TransactionDaoImpl();
         transaction.write(new Transaction(dateNow, user, command, message));
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   @Override
   public String getBotUsername() {
      return "CurrencyConverterImplemicaBot";
   }

   @Override
   public String getBotToken() {
      return "736932538:AAFW981ptLJ4g1lbsVTn7HebaojMKLClEDg";
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
   private void getInformationAboutUser(Update update) {
      int userId = update.getMessage().getFrom().getId();
      String userFirstName = update.getMessage().getFrom().getFirstName();
      String userLastName = update.getMessage().getFrom().getLastName();
      String userName = update.getMessage().getFrom().getUserName();

      if (userName == null || userLastName == null) {
         user = new User(userId, userFirstName);
         if (userName != null) {
            user.setUserName(userName);
         }
         if(userLastName!=null){
            user.setUserLastName(userLastName);
         }
      } else {
         user = new User(userId, userFirstName, userLastName, userName);
      }

   }

}

