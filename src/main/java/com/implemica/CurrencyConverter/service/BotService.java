package com.implemica.CurrencyConverter.service;


import com.implemica.CurrencyConverter.dao.DialogDao;
import com.implemica.CurrencyConverter.model.Dialog;
import com.implemica.CurrencyConverter.model.State;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.model.UsersRequest;
import com.implemica.CurrencyConverter.validator.BotValidator;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.knowm.xchange.currency.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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
    * Start of message for mistakes
    */
   private static final String SORRY_BUT = "❗Sorry, but \"";
   /**
    * End of message about incorrect currency
    */
   private static final String IS_NOT_A_VALID_CURRENCY = "\" is not a valid currency.";
   /**
    * End of message about incorrect amount
    */
   private static final String IS_NOT_A_VALID_NUMBER = "\" is not a valid number.";
   /**
    * Money sign for result message
    */
   private static final String MONEY_SIGN = "\uD83D\uDCB0";

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
   private static final String CONVERT_MESSAGE = "\nYou can make a new currency conversion: \n\n" +
           "➡️ using /convert command \nor\n➡️ single line command " +
           "(E. g. : 10 USD in UAH)";
   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = "\uD83D\uDC4B Hello! I can help you to convert currencies."
           + CONVERT_MESSAGE + "\n\nSo, how can I help you?";
   /**
    * Stop message to the user
    */
   private static final String STOP_MESSAGE = "🆗." + CONVERT_MESSAGE;

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
   /**
    * Bot's response for /convert command
    */
   private static final String FIRST_CONVERT_MESSAGE = "Please, type in the currency to convert from (E. g. : USD)";
   /**
    * Start of bot's response after entering first currency
    */
   private static final String SECOND_CONVERT_MESSAGE_1 = "What currency do you want to convert from ";
   /**
    * End of bot's response after entering first currency
    */
   private static final String SECOND_CONVERT_MESSAGE_2 = " to? (E. g. : EUR)";
   /**
    * Bot's response after entering second currency
    */
   private static final String THIRD_CONVERT_MESSAGE = "Enter the amount to convert from ";
   /**
    * Bot's response for incorrect request from user
    */
   private static final String INCORRECT_REQUEST_MESSAGE = "❗Sorry, but your request is incorrect." + CONVERT_MESSAGE;

   /**
    * Bot's response for non-text message
    */
   private static final String INCORRECT_CONTENT_MESSAGE = "❗Sorry, but this message contains " +
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
    * Count of words in one line request
    */
   private static final int WORDS_COUNT = 4;
   /**
    * Logger for this class
    */
   private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

   /**
    * Stores Users and their commands
    */
   private Map<Integer, State> states = State.statesOfUsers;

   /**
    * Converter for currencies
    */
   private final ConverterService converterService;

   /**
    * Date format for writing to file
    */
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
    * Gets statesOfUsers input and processes it, writes conversation to .csv file and sends their to webSocket
    *
    * @param command request from user
    * @param user    user, which sent message
    * @return bot's response to user
    */
   public String processCommand(String command, User user) {
      int userId = user.getUserId();

      getState(userId);

      String message;

      if (command.equals(UNIQUE)) {
         command = NOT_TEXT_CONTENT;
         message = INCORRECT_CONTENT_MESSAGE;
         convertStep = 0;

      } else if (isOneLineRequest(command)) {
         message = convertByLine(command);
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

         if (isValidCurrency(firstCurrency)) {
            message = SECOND_CONVERT_MESSAGE_1 + firstCurrency + SECOND_CONVERT_MESSAGE_2;
            convertStep = 2;

         } else {
            message = SORRY_BUT + command + IS_NOT_A_VALID_CURRENCY + FIRST_CONVERT_MESSAGE;
         }

      } else if (convertStep == 2) {
         secondCurrency = BotValidator.toUpperCase(command);

         if (isValidCurrency(secondCurrency)) {
            message = THIRD_CONVERT_MESSAGE + firstCurrency + " to " + secondCurrency;
            convertStep = 3;
         } else {
            message = SORRY_BUT + command + IS_NOT_A_VALID_CURRENCY +
                    SECOND_CONVERT_MESSAGE_1 + firstCurrency + SECOND_CONVERT_MESSAGE_2;
         }

      } else if (convertStep == 3) {

         if (isValidAmount(command)) {
            message = convertValue(command);
            convertStep = 0;

         } else {
            message = SORRY_BUT + command + IS_NOT_A_VALID_NUMBER +
                    THIRD_CONVERT_MESSAGE + firstCurrency + " to " + secondCurrency;
         }

      } else {
         message = INCORRECT_REQUEST_MESSAGE;
         convertStep = 0;
      }

      writeData(user, command, message);
      return message;
   }


   /**
    * Writes changes in users states to map and to .csv file
    *
    * @param user     user, which sent message
    * @param request  request from user
    * @param response response from bot
    */
   private void writeData(User user, String request, String response) {
      int userId = user.getUserId();
      State state = new State(firstCurrency, secondCurrency, convertStep);
      states.put(userId, state);

      Date dateNow = new Date();

      Dialog dialog = new Dialog(dateNow, user, request, response);
      dialogDao.write(dialog);
      sendToWebSocketFollowers(dialog);
   }

   /**
    * Gets state of conversion for given user
    *
    * @param userId id of given user
    */
   private void getState(int userId) {
      State state;

      if (states.containsKey(userId)) {
         state = states.get(userId);

      } else {
         state = new State("", "", 0);
      }

      firstCurrency = state.getFirstCurrency();
      secondCurrency = state.getSecondCurrency();
      convertStep = state.getConvertStep();
   }

   /**
    * Converts given currencies, if they were given by one line
    *
    * @param line given line
    * @return message to user with result of conversion from first currency to second currency
    */
   private String convertByLine(String line) {
      String[] request = line.split("\\s+");
      String message;
      firstCurrency = BotValidator.toUpperCase(request[1]);

      if (isValidCurrency(firstCurrency)) {
         secondCurrency = BotValidator.toUpperCase(request[3]);

         if (isValidCurrency(secondCurrency)) {
            String amount = request[0];

            if (isValidAmount(amount)) {
               message = convertValue(amount);

            } else {
               message = SORRY_BUT + amount + IS_NOT_A_VALID_NUMBER + CONVERT_MESSAGE;
            }
         } else {
            message = SORRY_BUT + secondCurrency + IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE;
         }
      } else {
         message = SORRY_BUT + firstCurrency + IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE;
      }
      return message;
   }

   /**
    * Checks, that user's input may be one line request or not
    *
    * @param line user's input
    * @return true, if line contains 4 words and correct binding word between currencies
    */
   private boolean isOneLineRequest(String line) {
      String[] request = line.split("\\s+");

      int length = request.length;

      String word = "";
      if(length == WORDS_COUNT){
         word = request[2];
      }

      return length == WORDS_COUNT && (word.equalsIgnoreCase("to") || word.equalsIgnoreCase("in"));
   }

   /**
    * Converts given currencies from first one to second.
    *
    * @param value amount of first currency
    * @return message to user with result of conversion from first currency to second currency
    */
   private String convertValue(String value) {
      String message;
      try {
         Currency usersCurrency = Currency.getInstance(firstCurrency);
         Currency desiredCurrency = Currency.getInstance(secondCurrency);
         UsersRequest usersRequest = new UsersRequest(usersCurrency, desiredCurrency, parseNumber(value));

         BigDecimal convertedValue = converterService.convert(usersRequest);
         message = MONEY_SIGN + value + " " + firstCurrency + " is " + formatNumber(convertedValue) + " " + secondCurrency;

      } catch (CurrencyConverterException e) {
         message = "❗Sorry. " + e.getMessage() + "\n" + CONVERT_MESSAGE;

      } catch (ParseException e) {
         message = SORRY_BUT + value + IS_NOT_A_VALID_NUMBER + CONVERT_MESSAGE;

      } catch (IOException e) {
         logger.error(e.getMessage() + " is not responding.");
         message = "❗Sorry. Server is not responding. Try again later.";
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

   /**
    * Checks, that given String is a valid currency
    *
    * @param usersCurrency String, which has to be checked
    * @return true, if usersCurrency can be convert to {@link Currency}, false - otherwise.
    */
   private boolean isValidCurrency(String usersCurrency) {
      try {
         java.util.Currency.getInstance(usersCurrency);
      } catch (IllegalArgumentException ex) {
         return Currency.getAvailableCurrencyCodes().contains(usersCurrency);
      }
      return true;
   }

   /**
    * Checks, that given amount is correct number
    *
    * @param amount amount of currency, which has to be checked
    * @return true, if amount is positive number or zero, false - otherwise.
    */
   private boolean isValidAmount(String amount) {
      try {
         parseNumber(amount);
      } catch (ParseException e) {
         return false;
      }
      return true;
   }
}