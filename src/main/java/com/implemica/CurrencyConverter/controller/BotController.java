package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.dao.TransactionDao;
import com.implemica.CurrencyConverter.model.Converter;
import com.implemica.CurrencyConverter.model.Transaction;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.validator.BotValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.money.UnknownCurrencyException;
import javax.money.convert.CurrencyConversionException;
import java.util.Date;

/**
 * This class gets users input from telegram bot and processes it via Converter class
 *
 * @author Daria S.
 * @version 27.12.2018 12:27
 */
@Component
public class BotController extends TelegramLongPollingBot {

   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = "Hello! Could I help you?";


   private static final String CONVERT_MESSAGE = "You can use /convert to make me new convert currencies";
   /**
    * Stop message to user
    */
   private static final String STOP_MESSAGE = "OK. " + CONVERT_MESSAGE;

   /**
    * Bot's command to start conversation
    */
   private static final String START = "/start";
   /**
    * Bot's command to start convert currencies
    */
   private static final String CONVERT = "/convert";
   /**
    * Bot's command to stop conversation
    */
   private static final String STOP = "/stop";
   private static final String FIRST_CONVERT_MESSAGE = "Please, type in the currency to convert from (example: USD)";
   private static final String SECOND_CONVERT_MESSAGE_1 = "OK, you wish to convert from ";
   private static final String SECOND_CONVERT_MESSAGE_2 = " to what currency? (example: EUR)";
   private static final String THIRD_CONVERT_MESSAGE = "Enter the amount to convert from ";
   private static final String BOT_NAME = "CurrencyConverterImplemicaBot";
   private static final String BOT_TOKEN = "736932538:AAFW981ptLJ4g1lbsVTn7HebaojMKLClEDg";

   private String firstCurrency;
   private String secondCurrency;
   private int convertStep;

   /**
    * Information about User
    */
   private User user;

   /**
    * Data, which has to be added to .csv file
    */
   @Autowired
   private TransactionDao transactionDao;

   @Autowired
   private Converter converter;

   @Autowired
   private SimpMessagingTemplate template;

   /**
    * Gets users input and processes it. Writes conversation to .csv file.
    */
   @Override
   public void onUpdateReceived(Update update) {
      String command = update.getMessage().getText();
      String message;

      //get information about user
      getInformationAboutUser(update);

      //dialog with user
      if (!update.getMessage().hasText()) {
         message = "Sorry, but this message contains incorrect content. Please, don't send me messages, which I can't handle. " + CONVERT_MESSAGE;
         command = "Users message has incorrect content.";
      } else {
         if (command.equals(START)) {
            message = START_MESSAGE;
            convertStep = 0;
         } else if (command.equals(STOP)) {
            message = STOP_MESSAGE;
            convertStep = 0;
         } else if (command.equals(CONVERT)) {
            message = FIRST_CONVERT_MESSAGE;
            convertStep++;
         } else if (convertStep == 1) {
            firstCurrency = BotValidator.toUpperCase(update.getMessage().getText());
            message = SECOND_CONVERT_MESSAGE_1 + firstCurrency + SECOND_CONVERT_MESSAGE_2;
            convertStep++;
         } else if (convertStep == 2) {
            secondCurrency = BotValidator.toUpperCase(update.getMessage().getText());
            message = THIRD_CONVERT_MESSAGE + firstCurrency + " to " + secondCurrency;
            convertStep++;
         } else if (convertStep == 3) {
            message = convertValue(update);
            convertStep = 0;
         } else {
            message = "Sorry, but I don't understand what \"" + command + "\" means. " + CONVERT_MESSAGE;
            convertStep = 0;
         }
      }
      sendMessage(update, message);
      Date dateNow = new Date();

      Transaction transaction = new Transaction(dateNow, user, command, message);

      transactionDao.write(transaction);
      template.convertAndSend("/topic/greetings", transaction);
   }

   @Override
   public String getBotUsername() {
      return BOT_NAME;
   }

   @Override
   public String getBotToken() {
      return BOT_TOKEN;
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
         if (userLastName != null) {
            user.setUserLastName(userLastName);
         }
      } else {
         user = new User(userId, userFirstName, userLastName, userName);
      }

   }

   /**
    * Returns result of conversion from first currency to second currency
    */
   private String convertValue(Update update) {
      String value = update.getMessage().getText();
      String message;
      if (BotValidator.isCorrectNumber(value)) {
         try {
            Float convertedValue = converter.convert(firstCurrency, secondCurrency, Float.parseFloat(value));
            message = value + " " + firstCurrency + " is " + convertedValue + " " + secondCurrency;
         } catch (UnknownCurrencyException ex) {
            message = ex.getMessage();
         } catch (CurrencyConversionException ex) {
            message = "Sorry, I can't convert from " + firstCurrency + " to " + secondCurrency;
         }
      } else {
         message = "Sorry, but \"" + value + "\" is not a valid number. Conversion is impossible. " + CONVERT_MESSAGE;
      }
      return message;
   }

}

