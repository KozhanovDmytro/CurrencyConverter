package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.service.ConverterService;
import com.implemica.CurrencyConverter.validator.BotValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.util.NumberUtils;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests
 *
 * @author Daria S.
 * @version 01.02.2019 14:42
 */
class BotTest {

   final ConverterService converterService = new ConverterService();
   private Bot testBot = new Bot(converterService);

   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = "Hello! Could I help you?";

   /**
    * Converting messages
    */
   private static final String CONVERT_MESSAGE = "You can use /convert to make me new convert currencies";
   private static final String FIRST_CONVERT_MESSAGE = "Please, type in the currency to convert from (example: USD)";
   private static final String SECOND_CONVERT_MESSAGE_1 = "OK, you wish to convert from ";
   private static final String SECOND_CONVERT_MESSAGE_2 = " to what currency? (example: EUR)";
   private static final String THIRD_CONVERT_MESSAGE = "Enter the amount to convert from ";
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


   private static final Pattern isNumber = Pattern.compile("^\\d+(\\.\\d+)?$");

   @Test
   void startCommand() {
      compute(START, START_MESSAGE);
   }

   @Test
   void stopCommand() {
      compute(STOP, STOP_MESSAGE);
   }

   @Test
   void rightScriptTest() {
      rightScript("usd", "uah", "1");
      rightScript("Byn", "Usd", "77");
      rightScript("uSd", "eUr", "10,5");
      rightScript("ruB", "caD", "33");
      rightScript("CnY", "CzK", "12000,75");
      rightScript("DKk", "NZd", "13000000001");
      rightScript("bGN", "tRY", "0.8");
      rightScript("RUB", "UAH", "15");
      rightScript("p l n", "u s d", "55.4");
      rightScript("I     n   r", "U ah", "811");
   }

   @Test
   void wrongAmountTest(){
      //negative number
      rightScriptWithWrongLastValue("xof", "hnl", "-2");
      rightScriptWithWrongLastValue("Lak", "Awg", "-9,7");
      rightScriptWithWrongLastValue("gEl", "aAl", "-18.6");
      rightScriptWithWrongLastValue("zaR", "jmD", "16f");
      rightScriptWithWrongLastValue("BAm", "SZl", "14u");
      rightScriptWithWrongLastValue("ItL", "GnF", "1,1,1");
      rightScriptWithWrongLastValue("sYP", "mKD", "4.3.4");
      rightScriptWithWrongLastValue("BZD", "KWD", "apple");
      rightScriptWithWrongLastValue(" Sll ", "E tb", "2+2");
      rightScriptWithWrongLastValue("A Z N", "X P F", "p0q");
   }


   private void rightScriptWithWrongLastValue(String firstCurrency, String secondCurrency, String amount) {
      script(firstCurrency, secondCurrency);
      compute(amount,"Sorry, but \"" + amount + "\" is not a valid number. Conversion is impossible. " + CONVERT_MESSAGE);
   }

   private void rightScript(String firstCurrency, String secondCurrency, String amount) {
      script(firstCurrency, secondCurrency);
      checkResult(amount, firstCurrency, secondCurrency);
   }

   private void script(String firstCurrency, String secondCurrency) {
      firstCurrency = BotValidator.toUpperCase(firstCurrency);
      secondCurrency = BotValidator.toUpperCase(secondCurrency);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(firstCurrency, SECOND_CONVERT_MESSAGE_1 + firstCurrency + SECOND_CONVERT_MESSAGE_2);
      compute(secondCurrency, THIRD_CONVERT_MESSAGE + firstCurrency + " to " + secondCurrency);
   }


   private void compute(String usersMessage, String botsResponse) {
      assertEquals(botsResponse, testBot.onUpdateReceived(usersMessage));
   }

   private void checkResult(String amount, String firstCurrency, String secondCurrency) {
      firstCurrency = BotValidator.toUpperCase(firstCurrency);
      secondCurrency = BotValidator.toUpperCase(secondCurrency);
      String start = amount + " " + firstCurrency + " is ";
      String botsResponse = testBot.onUpdateReceived(amount);
      assertTrue(botsResponse.startsWith(start));
      assertTrue(botsResponse.endsWith(secondCurrency));
      String value = botsResponse.replace(start, "").replace(" " + secondCurrency, "");
      Matcher matcher = isNumber.matcher(value);
      assertTrue(matcher.matches());
   }
}