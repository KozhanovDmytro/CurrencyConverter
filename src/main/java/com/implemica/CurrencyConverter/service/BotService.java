package com.implemica.CurrencyConverter.service;


import com.implemica.CurrencyConverter.dao.DialogDao;
import com.implemica.CurrencyConverter.model.Converter;
import com.implemica.CurrencyConverter.model.Dialog;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.validator.BotValidator;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

import static com.implemica.CurrencyConverter.validator.BotValidator.formatNumber;
import static com.implemica.CurrencyConverter.validator.BotValidator.parseNumber;

/**
 * This class contains Bot's logic
 *
 * @author Daria S.
 * @author Dmytro K.
 * @version 08.01.19 14:32
 */
@Service
public class BotService {

   /**
    * Unique string, which uses for messages, which has non text content.
    */
   public static final String UNIQUE = UUID.randomUUID().toString();

   /**
    * Dialog with user
    */
   private final DialogDao dialogDao;

   /**
    * Provides methods for sending messages to webSocket
    */
   private final SimpMessagingTemplate template;

   /**
    * Message to the user with the suggestion of a new conversion
    */
   public static final String CONVERT_MESSAGE = " You can use /convert command or type me a request " +
           "(Example: 10 USD in UAH) to make me new convert currencies";
   /**
    * Greeting message to user
    */
   public static final String START_MESSAGE = "Hello! I can help you to convert currencies." + CONVERT_MESSAGE;
   /**
    * Stop message to the user
    */
   public static final String STOP_MESSAGE = "OK." + CONVERT_MESSAGE;

   /**
    * Bot's command to start conversation
    */
   public static final String START = "/start";
   /**
    * Bot's command to start convert currencies
    */
   public static final String CONVERT = "/convert";

   /**
    * Bot's command to stop conversation
    */
   public static final String STOP = "/stop";
   /**
    * Bot's response for /convert command
    */
   public static final String FIRST_CONVERT_MESSAGE = "Please, type in the currency to convert from (example: USD)";
   /**
    * Start of bot's response after entering first currency
    */
   public static final String SECOND_CONVERT_MESSAGE_1 = "OK, you wish to convert from ";
   /**
    * End of bot's response after entering first currency
    */
   public static final String SECOND_CONVERT_MESSAGE_2 = " to what currency? (example: EUR)";
   /**
    * Bot's response after entering second currency
    */
   public static final String THIRD_CONVERT_MESSAGE = "Enter the amount to convert from ";
   /**
    * Bot's response for incorrect request from user
    */
   public static final String INCORRECT_REQUEST_MESSAGE = "Sorry, but your request is incorrect." + CONVERT_MESSAGE;

   /**
    * Bot's response for non-text message
    */
   public static final String INCORRECT_CONTENT_MESSAGE = "Sorry, but this message contains " +
           "incorrect content. Please, don't send me messages, which I can't handle." + CONVERT_MESSAGE;
   /**
    * Message for log, that user sent incorrect content
    */
   private static final String NOT_TEXT_CONTENT = "Users message has incorrect content.";
   /**
    * The currency to convert from
    */
   private String firstCurrency = "";
   /**
    * The currency to convert to
    */
   private String secondCurrency = "";
   /**
    * Step of conversion
    */
   private int convertStep = 0;

   /**
    * Logger for this class
    */
   private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
   /**
    * Converter for currencies
    */
   private final ConverterService converterService;

   public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

   /**
    * Creates new BotService containing specified converter, writer to storage and sender
    *
    * @param converterService converter for currencies
    * @param dialogDao        write dialog into storage
    * @param template         send messages to webSocket
    */
   @Autowired
   public BotService(ConverterService converterService, DialogDao dialogDao, SimpMessagingTemplate template) {
      this.converterService = converterService;
      this.dialogDao = dialogDao;
      this.template = template;
   }

   /**
    * Gets users input and processes it, writes conversation to .csv file and sends their to webSocket
    *
    * @param command request from user
    * @param user    user, which sent message
    * @return bot's response to user
    */
   public String onUpdateReceived(String command, User user) {
      String message;
      if (command.equals(UNIQUE)) {
         command = NOT_TEXT_CONTENT;
         message = INCORRECT_CONTENT_MESSAGE;
         convertStep = 0;
      } else if (command.equals(START)) {
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
         message = convertByLine(command);
         convertStep = 0;
      }

      Date dateNow = new Date();

      Dialog dialog = new Dialog(dateNow, user, command, message);
      dialogDao.write(dialog);
      sendToWebSocketFollowers(dialog);

      return message;
   }

   /**
    * Converts given currencies, if they were given by one line
    *
    * @param line given line
    * @return message to user with result of conversion from first currency to second currency
    */
   private String convertByLine(String line) {
      String[] request = line.split("\\s+");
      int length = request.length;
      String message;
      if (length != 4 || (!request[2].equalsIgnoreCase("to") && !request[2].equalsIgnoreCase("in"))) {
         message = INCORRECT_REQUEST_MESSAGE;
      } else {
         firstCurrency = BotValidator.toUpperCase(request[1]);
         secondCurrency = BotValidator.toUpperCase(request[3]);
         message = convertValue(request[0]);
      }

      return message;
   }


   /**
    * Converts given currencies from first one to second.
    *
    * @param value amount of first currency
    * @return message to user with result of conversion from first currency to second currency
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
         message = e.getMessage() + CONVERT_MESSAGE;
      } catch (ParseException e) {
         message = "Sorry, but \"" + value + "\" is not a valid number. Conversion is impossible." + CONVERT_MESSAGE;
      } catch (IllegalArgumentException e) {
         String wrongCurrency;
         if (count == 0) {
            wrongCurrency = firstCurrency;
         } else {
            wrongCurrency = secondCurrency;
         }
         message = "Sorry, but currency is not valid: " + wrongCurrency + CONVERT_MESSAGE;
      } catch (IOException e) {
         logger.error(e.getMessage() + " is not responding.");
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
      logger.info("send to web socket followers. ");
   }

}
