package com.implemica.CurrencyConverter.controller;


import com.implemica.CurrencyConverter.model.Converter;
import com.implemica.CurrencyConverter.service.ConverterService;
import com.implemica.CurrencyConverter.validator.BotValidator;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Currency;

import static com.implemica.CurrencyConverter.validator.BotValidator.formatNumber;
import static com.implemica.CurrencyConverter.validator.BotValidator.parseNumber;

/**
 * This class contains Bot's logic
 *
 * @author Daria S.
 * @version 02.01.19 12:39
 */
@Component
public class Bot {
   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = "Hello! Could I help you?";


   private static final String CONVERT_MESSAGE = ". You can use /convert to make me new convert currencies";
   /**
    * Stop message to user
    */
   private static final String STOP_MESSAGE = "OK" + CONVERT_MESSAGE;

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


   /**
    * Data, which has to be added to .csv file
    */
   private final ConverterService converterService;

   @Autowired
   public Bot(ConverterService converterService) {
      this.converterService = converterService;
   }

   /**
    * Gets users input and processes it. Writes conversation to .csv file.
    */
   String onUpdateReceived(String command) {
      String message;
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
         firstCurrency = BotValidator.toUpperCase(command);
         message = SECOND_CONVERT_MESSAGE_1 + firstCurrency + SECOND_CONVERT_MESSAGE_2;
         convertStep++;
      } else if (convertStep == 2) {
         secondCurrency = BotValidator.toUpperCase(command);
         message = THIRD_CONVERT_MESSAGE + firstCurrency + " to " + secondCurrency;
         convertStep++;
      } else if (convertStep == 3) {
         message = convertValue(command);
         convertStep = 0;
      } else {
         message = "Sorry, but I don't understand what \"" + command + "\" means" + CONVERT_MESSAGE;
         convertStep = 0;
      }

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
         message = e.getMessage() + CONVERT_MESSAGE;
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
      }
      return message;
   }
}
