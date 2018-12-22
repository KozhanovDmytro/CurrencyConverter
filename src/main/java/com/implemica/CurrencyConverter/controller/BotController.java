package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.validator.BotValidator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
    * Gets users input and processes it
    */
   @Override
   public void onUpdateReceived(Update update) {
      String command = update.getMessage().getText();

      if (command.equals(START_COMMAND)) {
         sendMessage(update, START_MESSAGE);
         gotFirstCurrency = false;
         gotSecondCurrency = false;
         gotValue = false;
      } else if (command.equals(STOP_COMMAND)) {
         sendMessage(update, STOP_MESSAGE);
         gotFirstCurrency = false;
         gotSecondCurrency = false;
         gotValue = false;
      } else if (command.equals(CONVERT_COMMAND)) {
         sendMessage(update, "Please, type in the currency to convert from (example: USD)");
         gotFirstCurrency = true;
      } else if (gotFirstCurrency) {
         firstCurrency = BotValidator.toUpperCase(update.getMessage().getText());
         sendMessage(update, "OK, you wish to convert from " + firstCurrency + " to what currency? (example: EUR)");
         gotFirstCurrency = false;
         gotSecondCurrency = true;
      } else if (gotSecondCurrency) {
         secondCurrency = BotValidator.toUpperCase(update.getMessage().getText());
         sendMessage(update, "Enter the amount to convert from " + firstCurrency + " to " + secondCurrency);
         gotSecondCurrency = false;
         gotValue = true;
      } else if (gotValue) {
         String value = update.getMessage().getText();
         String message;
         if (BotValidator.isNumber(value)) {
            //todo change "undefined" to call convert method from Converter
            String convertedValue = "undefined";
            message = value + " " + firstCurrency + " is " + convertedValue + " " + secondCurrency;
         } else {
            message = "Sorry, but \"" + value + "\" is not a number. Conversion is impossible.";
         }
         sendMessage(update, message);
         firstCurrency = "";
         secondCurrency = "";
         gotValue = false;
      } else {
         String word = update.getMessage().getText();
         sendMessage(update, "Sorry, but I don't understand what does \"" + word + "\" mean.");
         gotFirstCurrency = false;
         gotSecondCurrency = false;
         gotValue = false;
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


}

