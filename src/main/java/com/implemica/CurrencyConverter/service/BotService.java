package com.implemica.CurrencyConverter.service;


import com.implemica.CurrencyConverter.dao.DialogDao;
import com.implemica.CurrencyConverter.model.Converter;
import com.implemica.CurrencyConverter.model.Dialog;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.validator.BotValidator;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Currency;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.implemica.CurrencyConverter.validator.BotValidator.formatNumber;
import static com.implemica.CurrencyConverter.validator.BotValidator.parseNumber;

/**
 * This class contains Bot's logic
 *
 * @author Daria S.
 * @version 08.01.19 14:32
 */
@Service
public class BotService {

   private static final String BOT_TOKEN = "736932538:AAFW981ptLJ4g1lbsVTn7HebaojMKLClEDg";

   /**
    * Data, which has to be added to .csv file
    */
   private final DialogDao dialogDao;

   private final SimpMessagingTemplate template;
   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = "Hello! Could I help you?";


   private static final String CONVERT_MESSAGE = " You can use /convert to make me new convert currencies";
   /**
    * Stop message to user
    */
   private static final String STOP_MESSAGE = "OK." + CONVERT_MESSAGE;

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

   private String firstCurrency = "";
   private String secondCurrency = "";
   private int convertStep = 0;


   private final Logger log = Logger.getLogger(this.getClass().getName());
   /**
    * Data, which has to be added to .csv file
    */
   private final ConverterService converterService;

   @Autowired
   public BotService(ConverterService converterService, DialogDao dialogDao, SimpMessagingTemplate template) {
      this.converterService = converterService;
      this.dialogDao = dialogDao;
      this.template = template;
   }

   /**
    * Gets users input and processes it. Writes conversation to .csv file.
    */
   public String onUpdateReceived(String command, User user) {
      String message;
      if (command.equals(BOT_TOKEN)) {
         command = "Users message has incorrect content.";
         message = "Sorry, but this message contains incorrect content. Please, don't send me messages, which I can't handle. " + CONVERT_MESSAGE;
         convertStep = 0;
      } else {
         if (command.equals(START)) {
            message = START_MESSAGE;
            convertStep = 0;
         } else if (command.equals(STOP)) {
            message = STOP_MESSAGE;
            convertStep = 0;
         } else if (command.equals(CONVERT)) {
            message = FIRST_CONVERT_MESSAGE;
            convertStep = 1;
         } else if (convertStep == 1) {
            firstCurrency = BotValidator.toUpperCase(command);
            message = SECOND_CONVERT_MESSAGE_1 + firstCurrency + SECOND_CONVERT_MESSAGE_2;
            convertStep = 2;
         } else if (convertStep == 2) {
            secondCurrency = BotValidator.toUpperCase(command);
            message = THIRD_CONVERT_MESSAGE + firstCurrency + " to " + secondCurrency;
            convertStep = 3;
         } else if (convertStep == 3) {
            message = convertValue(command);
            convertStep = 0;
         } else {
            message = "Sorry, but I don't understand what \"" + command + "\" means." + CONVERT_MESSAGE;
            convertStep = 0;
         }
      }

      Date dateNow  = new Date();

      Dialog dialog = new Dialog(dateNow, user, command, message);

      dialogDao.write(dialog);
      sendToWebSocketFollowers(dialog);

      return message;
   }

   /**
    * Returns result of conversion from first currency to second currency
    */

   private String convertValue(String value) {
      String message;
      int count = 0;
      try {
         Currency usersCurrency = Currency.getInstance(firstCurrency);
         count++;
         Currency desiredCurrency = Currency.getInstance(secondCurrency);
         Converter converter = new Converter(usersCurrency, desiredCurrency, parseNumber(value));

         Float convertedValue = converterService.convert(converter);
         message = value + " " + firstCurrency + " is " + formatNumber(convertedValue) + " " + secondCurrency;
      } catch (CurrencyConverterException e) {
         message = "One or two currencies not supported." + CONVERT_MESSAGE;
      } catch (ParseException e) {
         message = "Sorry, but \"" + value + "\" is not a valid number. Conversion is impossible. " + CONVERT_MESSAGE;
      } catch (IllegalArgumentException e) {
         String wrongCurrency;
         if (count == 0) {
            wrongCurrency = firstCurrency;
         } else {
            wrongCurrency = secondCurrency;
         }
         message = "Sorry, but currency is not valid: " + wrongCurrency + CONVERT_MESSAGE;
      } catch (IOException e) {
         log.log(Level.SEVERE, e.getMessage() + " is not responding.");
         message = "Server is not responding." + CONVERT_MESSAGE;
      }
      return message;
   }


   /**
    * Function sends an instance {@link Dialog} to WebSocket chanel followers.
    *
    * @param dialog dialog
    * @author Dmytro K.
    */
   private void sendToWebSocketFollowers(Dialog dialog) {
      template.convertAndSend("/listen/bot", dialog);
      log.log(Level.INFO, "send to web socket followers. ");
   }

}
