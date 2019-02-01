package com.implemica.CurrencyConverter.service;


import com.implemica.CurrencyConverter.dao.DialogDao;
import com.implemica.CurrencyConverter.model.*;
import com.implemica.CurrencyConverter.validator.BotValidator;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
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

import static com.implemica.CurrencyConverter.model.ConvertStep.*;
import static com.implemica.CurrencyConverter.validator.BotValidator.formatNumber;
import static com.implemica.CurrencyConverter.validator.BotValidator.parseNumber;

/**
 * This class contains Bot's logic
 *
 * @author Daria S.
 * @version 31.01.19 16:50
 */
@Service
public class BotService {

   /**
    * Unique string, which uses for messages, which has non text content.
    */
   public static final String WRONG_CONTENT = UUID.randomUUID().toString();

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
   private ConvertStep convertStep = ZERO;

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
    * Gets Users input and processes it, writes conversation to storage and sends their to webSocket
    *
    * @param command request from user
    * @param user    user, who sent message
    * @return bot's response to user
    */
   public String processCommand(String command, User user) {
      int userId = user.getUserId();

      checkState(userId);

      String message;

      if (command.equals(WRONG_CONTENT)) {
         command = NOT_TEXT_CONTENT;
         message = INCORRECT_CONTENT_MESSAGE;
         convertStep = ZERO;

      } else if (isOneLineRequest(command)) {
         message = convertByLine(command);
         convertStep = ZERO;

      } else if (command.equals(START)) {
         message = START_MESSAGE;
         convertStep = ZERO;

      } else if (command.equals(STOP)) {
         message = STOP_MESSAGE;
         convertStep = ZERO;

      } else if (command.equals(CONVERT)) {
         message = FIRST_CONVERT_MESSAGE;
         convertStep = FIRST;

      } else if (convertStep.equals(FIRST)) {
         firstCurrency = BotValidator.toUpperCase(command);

         if (isValidCurrency(firstCurrency)) {
            message = SECOND_CONVERT_MESSAGE_1 + firstCurrency + SECOND_CONVERT_MESSAGE_2;
            convertStep = SECOND;

         } else {
            message = SORRY_BUT + command + IS_NOT_A_VALID_CURRENCY + FIRST_CONVERT_MESSAGE;
         }

      } else if (convertStep.equals(SECOND)) {
         secondCurrency = BotValidator.toUpperCase(command);

         if (isValidCurrency(secondCurrency)) {
            message = THIRD_CONVERT_MESSAGE + firstCurrency + " to " + secondCurrency;
            convertStep = THIRD;

         } else {
            message = SORRY_BUT + command + IS_NOT_A_VALID_CURRENCY +
                    SECOND_CONVERT_MESSAGE_1 + firstCurrency + SECOND_CONVERT_MESSAGE_2;
         }

      } else if (convertStep.equals(THIRD)) {

         if (isValidAmount(command)) {
            message = convertValue(command);
            convertStep = ZERO;

         } else {
            message = SORRY_BUT + command + IS_NOT_A_VALID_NUMBER +
                    THIRD_CONVERT_MESSAGE + firstCurrency + " to " + secondCurrency;
         }

      } else {
         message = INCORRECT_REQUEST_MESSAGE;
         convertStep = ZERO;
      }

      writeDataToStorage(user, command, message);
      saveUserStates(user);
      return message;
   }


   /**
    * Writes changes in users states to map and to storage
    *
    * @param user     user, which sent message
    * @param request  request from user
    * @param response response from bot
    */
   private void writeDataToStorage(User user, String request, String response) {

      Date dateNow = new Date();

      Dialog dialog = new Dialog(dateNow, user, request, response);
      dialogDao.write(dialog);
      sendToWebSocket(dialog);
   }

   /**
    * Save user's state
    *
    * @param user user, which states has to be saved
    */
   private void saveUserStates(User user) {
      int userId = user.getUserId();
      State state = new State(firstCurrency, secondCurrency, convertStep);
      states.put(userId, state);
   }


   /**
    * Gets state of conversion for given user, namely the currency to convert to, the currency to convert from and
    * step of conversion. If User already communicated with the bot, gets their state from {@link State#statesOfUsers},
    * else sets state as initial. Defines firstCurrency, secondCurrency and convertStep depending on the gotten state.
    *
    * @param userId id of given user
    */
   private void checkState(int userId) {
      if (states.containsKey(userId)) {

         State state = states.get(userId);

         firstCurrency = state.getFirstCurrency();
         secondCurrency = state.getSecondCurrency();
         convertStep = state.getConvertStep();

      } else {

         firstCurrency = "";
         secondCurrency = "";
         convertStep = ZERO;
      }

   }

   /**
    * Converts given currencies, if they were given by one line
    *
    * @param line given line
    * @return message to user with result of conversion from first currency to second currency
    */
   private String convertByLine(String line) {
      String[] request = line.split("\\s+");
      String message = "";
      firstCurrency = BotValidator.toUpperCase(request[1]);

      String wrongValueMessage = "";
      if (isValidCurrency(firstCurrency)) {
         secondCurrency = BotValidator.toUpperCase(request[3]);

         if (isValidCurrency(secondCurrency)) {
            String amount = request[0];

            if (isValidAmount(amount)) {
               message = convertValue(amount);

            } else {
               wrongValueMessage = amount + IS_NOT_A_VALID_NUMBER;
            }
         } else {
            wrongValueMessage = secondCurrency + IS_NOT_A_VALID_CURRENCY;
         }
      } else {
         wrongValueMessage = firstCurrency + IS_NOT_A_VALID_CURRENCY;
      }
      if (!wrongValueMessage.isEmpty()) {
         message = String.format("%s%s%s", SORRY_BUT, wrongValueMessage, CONVERT_MESSAGE);
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
      if (length == WORDS_COUNT) {
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
         Currency usersCurrency = Currency.valueOf(firstCurrency);
         Currency desiredCurrency = Currency.valueOf(secondCurrency);
         BigDecimal number = parseNumber(value);

         BigDecimal convertedValue = converterService.convert(usersCurrency, desiredCurrency, number);
         message = MONEY_SIGN + value + " " + firstCurrency + " is " + formatNumber(convertedValue) + " " + secondCurrency;

      } catch (CurrencyConverterException e) {
         message = "❗Sorry. " + e.getMessage() + "\n" + CONVERT_MESSAGE;

      } catch (ParseException e) {
         message = SORRY_BUT + value + IS_NOT_A_VALID_NUMBER + CONVERT_MESSAGE;

      } catch (IOException e) {
         logger.error(e.getMessage() + " is not responding.");
         message = "❗Sorry, but server is not responding. Please, try again later.";
      }
      return message;
   }


   /**
    * Function sends an instance {@link Dialog} to WebSocket chanel followers.
    *
    * @param dialog dialog
    * @author Dmytro K.
    */
   private void sendToWebSocket(Dialog dialog) {
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
         Currency.valueOf(usersCurrency);
      } catch (IllegalArgumentException ex) {
         return false;
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
      return BotValidator.isValidNumber(amount);
   }
}