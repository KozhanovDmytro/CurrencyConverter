package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.service.ConverterService;
import org.junit.jupiter.api.Test;

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

   private final ConverterService converterService = new ConverterService();
   private Bot testBot = new Bot(converterService);

   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = "Hello! Could I help you?";

   /**
    * Converting messages
    */
   private static final String CONVERT_MESSAGE = ". You can use /convert to make me new convert currencies";
   private static final String FIRST_CONVERT_MESSAGE = "Please, type in the currency to convert from (example: USD)";
   private static final String SECOND_CONVERT_MESSAGE_1 = "OK, you wish to convert from ";
   private static final String SECOND_CONVERT_MESSAGE_2 = " to what currency? (example: EUR)";
   private static final String THIRD_CONVERT_MESSAGE = "Enter the amount to convert from ";
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


   private static final Pattern isPositiveNumber = Pattern.compile("^\\d+(\\.\\d+)?$");

   @Test
   void startCommand() {
      compute(START, START_MESSAGE);
   }

   @Test
   void stopCommand() {
      compute(STOP, STOP_MESSAGE);
   }

   @Test
   void notCommandTest() {
      enterOtherWords("hello");
      enterOtherWords("start");
      enterOtherWords("help");
      enterOtherWords("/help");
      enterOtherWords("convert");
      enterOtherWords("stop");
      enterOtherWords("10 usd to uah");
      enterOtherWords("Eur");
      enterOtherWords("MY name is John");
      enterOtherWords("/clear");
   }

   @Test
   void twoCommandsTest() {

      twoCommands(START, START_MESSAGE, START, START_MESSAGE);
      twoCommands(START, START_MESSAGE, STOP, STOP_MESSAGE);
      twoCommands(START, START_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);
      commandAndWord(START, START_MESSAGE, "start");
      commandAndWord(START, START_MESSAGE, "stop");
      commandAndWord(START, START_MESSAGE, "convert");
      commandAndWord(START, START_MESSAGE, "word");
      commandAndWord(START, START_MESSAGE, "10 usd to uah");

      twoCommands(STOP, STOP_MESSAGE, START, START_MESSAGE);
      twoCommands(STOP, STOP_MESSAGE, STOP, STOP_MESSAGE);
      twoCommands(STOP, STOP_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);
      commandAndWord(STOP, STOP_MESSAGE, "start");
      commandAndWord(STOP, STOP_MESSAGE, "stop");
      commandAndWord(STOP, STOP_MESSAGE, "convert");
      commandAndWord(STOP, STOP_MESSAGE, "hello");
      commandAndWord(STOP, STOP_MESSAGE, "500 dollars to euro");

      wordAndCommand("start", START, START_MESSAGE);
      wordAndCommand("hello", STOP, STOP_MESSAGE);
      wordAndCommand("1000 uah to usd", CONVERT, FIRST_CONVERT_MESSAGE);

      twoCommands(CONVERT, FIRST_CONVERT_MESSAGE, START, START_MESSAGE);
      twoCommands(CONVERT, FIRST_CONVERT_MESSAGE, STOP, STOP_MESSAGE);
      twoCommands(CONVERT, FIRST_CONVERT_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);
      //if after '/convert' we type not command word, then we go to next conversion step. See wrongCurrencyTest

   }

   //we can interrupt conversion only if we give new command
   @Test
   void interruptConvertOnSecondStepTest() {
      //correct currency
      interruptConvertOnSecondStep("uah", "UAH", START, START_MESSAGE);
      interruptConvertOnSecondStep("Rub", "RUB", STOP, STOP_MESSAGE);
      interruptConvertOnSecondStep("EUR", "EUR", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();

      //wrong currency
      interruptConvertOnSecondStep("123", "123", START, START_MESSAGE);
      interruptConvertOnSecondStep("ZZZ", "ZZZ", STOP, STOP_MESSAGE);
      interruptConvertOnSecondStep("Bot", "BOT", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();

      //unsupported currency
      interruptConvertOnSecondStep("xxx", "XXX", START, START_MESSAGE);
      interruptConvertOnSecondStep("uss", "USS", STOP, STOP_MESSAGE);
      interruptConvertOnSecondStep("uss", "USS", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();
   }

   @Test
   void interruptConvertOnThirdStepTest() {
      //both correct currency
      interruptConvertOnThirdStep("Pln", "PLN", "Eur", "EUR", START, START_MESSAGE);
      interruptConvertOnThirdStep("BgN", "BGN", "TRy", "TRY", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("All", "ALL", "PHP", "PHP", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();

      //first correct, second unsupported
      interruptConvertOnThirdStep("Cad", "CAD", "xxx", "XXX", START, START_MESSAGE);
      interruptConvertOnThirdStep("CZk", "CZK", "uss", "USS", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("IQD", "IQD", "X X X", "XXX", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();

      //first correct, second wrong
      interruptConvertOnThirdStep("mxn", "MXN", "sun", "SUN", START, START_MESSAGE);
      interruptConvertOnThirdStep("Uzs", "UZS", "java", "JAVA", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("MOP", "MOP", "Telegram", "TELEGRAM", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();


      //first unsupported, second correct
      interruptConvertOnThirdStep("Uss", "USS", "bif", "BIF", START, START_MESSAGE);
      interruptConvertOnThirdStep("Xxx", "XXX", "cop", "COP", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("USs", "USS", "Ars", "ARS", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();

      //both unsupported
      interruptConvertOnThirdStep("Uss", "USS", "xxx", "XXX", START, START_MESSAGE);
      interruptConvertOnThirdStep("Xxx", "XXX", "Uss", "USS", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("USs", "USS", "USS", "USS", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();

      //first unsupported, second wrong
      interruptConvertOnThirdStep("Uss", "USS", "rus", "RUS", START, START_MESSAGE);
      interruptConvertOnThirdStep("Xxx", "XXX", "eng", "ENG", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("X XX", "XXX", "ua", "UA", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();


      //first wrong, second correct
      interruptConvertOnThirdStep("shift", "SHIFT", "Uah", "UAH", START, START_MESSAGE);
      interruptConvertOnThirdStep("10 usd", "10USD", "Usd", "USD", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("byb", "BYB", "PLN", "PLN", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();

      //first wrong, second unsupported
      interruptConvertOnThirdStep("hi", "HI", "Xxx", "XXX", START, START_MESSAGE);
      interruptConvertOnThirdStep("bye", "BYE", "Uss", "USS", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("111", "111", "XXX", "XXX", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();

      //both wrong
      interruptConvertOnThirdStep("lets", "LETS", "oh", "OH", START, START_MESSAGE);
      interruptConvertOnThirdStep("speak", "SPEAK", "no", "NO", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("english", "ENGLISH", "hurrah", "HURRAH", CONVERT, FIRST_CONVERT_MESSAGE);
      stop();


   }


   @Test
   void rightScriptTest() {
      rightScript("usd", "USD", "uah", "UAH", "1");
      rightScript("brl", "BRL", "Usd", "USD", "77");
      rightScript("uSd", "USD", "eUr", "EUR", "10,5");
      rightScript("ruB", "RUB", "caD", "CAD", "33");
      rightScript("CnY", "CNY", "CzK", "CZK", "12000,75");
      rightScript("DKk", "DKK", "NZd", "NZD", "13000000001");
      rightScript("bGN", "BGN", "tRY", "TRY", "0.8");
      rightScript("RUB", "RUB", "UAH", "UAH", "15");
      rightScript("p l n", "PLN", "u s d", "USD", "55.4");
      rightScript("i  n   R", "INR", "e U r", "EUR", "811");
   }


   @Test
   void wrongAmountTest() {
      withWrongAmount("xof", "XOF", "hnl", "HNL", "-2");
      withWrongAmount("Lak", "LAK", "Awg", "AWG", "-9,7");
      withWrongAmount("gEl", "GEL", "aLl", "ALL", "-18.6");
      withWrongAmount("zaR", "ZAR", "jmD", "JMD", "16f");
      withWrongAmount("BAm", "BAM", "SZl", "SZL", "14u");
      withWrongAmount("ItL", "ITL", "GnF", "GNF", "1,1,1");
      withWrongAmount("sYP", "SYP", "mKD", "MKD", "4.3.4");
      withWrongAmount("BZD", "BZD", "KWD", "KWD", "apple");
      withWrongAmount(" Sll ", "SLL", "E tb", "ETB", "2+2");
      withWrongAmount("A Z N", "AZN", "X P F", "XPF", "p0q");
   }

   @Test
   void wrongCurrency() {
      //wrong first currency, right amount
      withWrongCurrency("sss", "SSS", "aed", "AED", "13", false);
      withWrongCurrency("Hi", "HI", "Xau", "XAU", "7,5", false);
      withWrongCurrency("heLLo", "HELLO", "aOa", "AOA", "123,69", false);
      withWrongCurrency("applE", "APPLE", "sdG", "SDG", "9140000", false);
      withWrongCurrency("ORAnge", "ORANGE", "MRo", "MRO", "1.1", false);
      withWrongCurrency("333", "333", "DzD", "DZD", "165", false);
      withWrongCurrency("NOW", "NOW", "tTD", "TTD", "102", false);
      withWrongCurrency("COnvERT", "CONVERT", "DKK", "DKK", "54.5", false);
      withWrongCurrency("Good bye", "GOODBYE", "S vc", "SVC", "99.99", false);
      withWrongCurrency("PLEASE STOP", "PLEASESTOP", "KP W", "KPW", "87,7", false);

      //wrong first currency, wrong amount
      withWrongCurrency("sss", "SSS", "aed", "AED", "-2", false);
      withWrongCurrency("Hi", "HI", "Xau", "XAU", "three", false);
      withWrongCurrency("heLLo", "HELLO", "aOa", "AOA", "50 dollars", false);
      withWrongCurrency("applE", "APPLE", "sdG", "SDG", "-4.3", false);
      withWrongCurrency("ORAnge", "ORANGE", "MRo", "MRO", "7.3.", false);
      withWrongCurrency("333", "333", "DzD", "DZD", "90f", false);
      withWrongCurrency("NOW", "NOW", "tTD", "TTD", "11$", false);
      withWrongCurrency("COnvERT", "CONVERT", "DKK", "DKK", "-9", false);
      withWrongCurrency("Good bye", "GOODBYE", "S vc", "SVC", "45c", false);
      withWrongCurrency("PLEASE STOP", "PLEASESTOP", "KP W", "KPW", "-75ru", false);


      //wrong second currency, right amount
      withWrongCurrency("brl", "BRL", "aaa", "AAA", "67", true);
      withWrongCurrency("Cuc", "CUC", "Currency", "CURRENCY", "21.2", true);
      withWrongCurrency("bOb", "BOB", "156", "156", "150", true);
      withWrongCurrency("gtQ", "GTQ", "rtF", "RTF", "916,7", true);
      withWrongCurrency("SOs", "SOS", "MIne", "MINE", "15000", true);
      withWrongCurrency("JoD", "JOD", "DEfaUlt", "DEFAULT", "7000", true);
      withWrongCurrency("iQD", "IQD", "seCOND", "SECOND", "35000,9", true);
      withWrongCurrency("MWK", "MWK", "MMM", "MMM", "998.98", true);
      withWrongCurrency("C L P", "CLP", "li s t", "LIST", "46.6", true);
      withWrongCurrency("P l     n", "PLN", "stop", "STOP", "2", true);

      //wrong second currency, wrong amount
      withWrongCurrency("brl", "BRL", "aaa", "AAA", "14brl", true);
      withWrongCurrency("Cuc", "CUC", "Currency", "CURRENCY", "-0", true);
      withWrongCurrency("bOb", "BOB", "156", "156", "547.2.22", true);
      withWrongCurrency("gtQ", "GTQ", "rtF", "RTF", "-12", true);
      withWrongCurrency("SOs", "SOS", "MIne", "MINE", "043A", true);
      withWrongCurrency("JoD", "JOD", "DEfaUlt", "DEFAULT", "----2", true);
      withWrongCurrency("iQD", "IQD", "seCOND", "SECOND", "7+0", true);
      withWrongCurrency("MWK", "MWK", "MMM", "MMM", "18*4", true);
      withWrongCurrency("C L P", "CLP", "li s t", "LIST", "clp", true);
      withWrongCurrency("P l     n", "PLN", "stop", "STOP", "amount", true);


      //wrong both currencies, right amount
      withWrongCurrency("pig", "PIG", "cat", "CAT", "26,5", false);
      withWrongCurrency("Bag", "BAG", "Hat", "HAT", "1000000", false);
      withWrongCurrency("aPp", "APP", "aPi", "API", "136.6", false);
      withWrongCurrency("soME", "SOME", "thIS", "THIS", "199", false);
      withWrongCurrency("NEw", "NEW", "OLd", "OLD", "35", false);
      withWrongCurrency("MAN", "MAN", "WOMAN", "WOMAN", "88888", false);
      withWrongCurrency("bitcoin", "BITCOIN", "etherium", "ETHERIUM", "133.33", false);
      withWrongCurrency("MONEY", "MONEY", "COINS", "COINS", "87,777", false);
      withWrongCurrency("B o o k", "BOOK", "P la ce", "PLACE", "914.7", false);
      withWrongCurrency("  convert", "CONVERT", "uaa", "UAA", "0", false);

      //wrong both currencies, wrong amount
      withWrongCurrency("pig", "PIG", "cat", "CAT", "-500", false);
      withWrongCurrency("Bag", "BAG", "Hat", "HAT", "$78", false);
      withWrongCurrency("aPp", "APP", "aPi", "API", "16..5", false);
      withWrongCurrency("soME", "SOME", "thIS", "THIS", "-", false);
      withWrongCurrency("NEw", "NEW", "OLd", "OLD", "5%", false);
      withWrongCurrency("MAN", "MAN", "WOMAN", "WOMAN", "1000-99", false);
      withWrongCurrency("bitcoin", "BITCOIN", "etherium", "ETHERIUM", "B711.1", false);
      withWrongCurrency("MONEY", "MONEY", "COINS", "COINS", "io", false);
      withWrongCurrency("B o o k", "BOOK", "P la ce", "PLACE", "abc", false);
      withWrongCurrency("  convert", "CONVERT", "uaa", "UAA", "82j", false);

      //first currency is unsupported
      withWrongCurrency("xxx", "XXX", "000","000", "6", true);
      withWrongCurrency("Uss", "USS", "opt","OPT", "-9.6", true);

      //second currency is unsopported
      withWrongCurrency("007","007","XXX","XXX","5500",false);
      withWrongCurrency("dog","DOG","u s s","USS","-76",false);



   }

   @Test
   void unsupportedCurrency() {
      //first currency is unsupported, right amount
      withUnsupportedCurrency("xxx", "XXX", "rub", "RUB", "1000", false);
      withUnsupportedCurrency("Uss", "USS", "Usd", "USD", "2.5", false);

      //first currency is unsupported, wrong amount
      withWrongAmount("xxx", "XXX", "rub", "RUB", "-34543");
      withWrongAmount("Uss", "USS", "Usd", "USD", "511a");


      //second currency is unsupported, right amount
      withUnsupportedCurrency("rub", "RUB", "xxx", "XXX", "7,6", true);
      withUnsupportedCurrency("Usd", "USD", "Uss", "USS", "762", true);

      //second currency is unsupported, wrong amount
      withWrongAmount("rub", "RUB", "xxx", "XXX", "one hundred");
      withWrongAmount("Usd", "USD", "Uss", "USS", "-4.99q");


      //both currencies is unsupported, right amount
      withUnsupportedCurrency("Xxx", "XXX", "Uss", "USS", "107.5", false);
      withUnsupportedCurrency("uSS", "USS", "XXX", "XXX", "20000", false);

      //both currencies is unsupported, wrong amount
      withWrongAmount("Xxx", "XXX", "Uss", "USS", "9*9");
      withWrongAmount("uSS", "USS", "XXX", "XXX", "10+45");
   }


   private void interruptConvertOnThirdStep(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                            String secondCurrencyInUpperCase, String command, String response) {
      script(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase);
      compute(command, response);
   }

   private void interruptConvertOnSecondStep(String firstCurrency, String firstCurrencyInUpperCase, String command, String response) {
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(firstCurrency, SECOND_CONVERT_MESSAGE_1 + firstCurrencyInUpperCase + SECOND_CONVERT_MESSAGE_2);
      compute(command, response);
   }

   private void wordAndCommand(String word, String command, String response) {
      enterOtherWords(word);
      compute(command, response);
   }

   private void commandAndWord(String command, String response, String word) {
      compute(command, response);
      enterOtherWords(word);
   }

   private void twoCommands(String firstCommand, String firstResponse, String secondCommand, String secondResponse) {
      compute(firstCommand, firstResponse);
      compute(secondCommand, secondResponse);
   }

   private void withUnsupportedCurrency(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                        String secondCurrencyInUpperCase, String amount, boolean firstIsCorrect) {
      rightScriptWithIncorrectCurrency(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase, amount,
              firstIsCorrect, true);
   }

   private void withWrongCurrency(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                  String secondCurrencyInUpperCase, String amount, boolean firstIsCorrect) {
      rightScriptWithIncorrectCurrency(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase, amount,
              firstIsCorrect, false);
   }

   private void rightScriptWithIncorrectCurrency(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                                 String secondCurrencyInUpperCase, String amount, boolean firstIsCorrect, boolean isUnsupported) {
      String startOfMessage = "Sorry, but currency is not valid: ";
      if (isUnsupported) {
         startOfMessage = "Currency not supported: ";
      }
      String wrongCurrency = firstCurrencyInUpperCase;
      if (firstIsCorrect) {
         wrongCurrency = secondCurrencyInUpperCase;
      }
      String message = startOfMessage + wrongCurrency + CONVERT_MESSAGE;
      rightScriptWithWrongValue(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase, amount, message);
   }


   private void withWrongAmount(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                String secondCurrencyInUpperCase, String amount) {
      String message = "Sorry, but \"" + amount + "\" is not a valid number. Conversion is impossible. " + CONVERT_MESSAGE;
      rightScriptWithWrongValue(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase, amount, message);
   }

   private void rightScriptWithWrongValue(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                          String secondCurrencyInUpperCase, String amount, String message) {
      script(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase);
      compute(amount, message);
   }


   private void rightScript(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                            String secondCurrencyInUpperCase, String amount) {
      script(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase);
      checkResult(amount, firstCurrencyInUpperCase, secondCurrencyInUpperCase);
   }

   private void script(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                       String secondCurrencyInUpperCase) {
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(firstCurrency, SECOND_CONVERT_MESSAGE_1 + firstCurrencyInUpperCase + SECOND_CONVERT_MESSAGE_2);
      compute(secondCurrency, THIRD_CONVERT_MESSAGE + firstCurrencyInUpperCase + " to " + secondCurrencyInUpperCase);
   }


   private void enterOtherWords(String message) {
      compute(message, "Sorry, but I don't understand what \"" + message + "\" means" + CONVERT_MESSAGE);
   }

   private void compute(String usersMessage, String botsResponse) {
      assertEquals(botsResponse, testBot.onUpdateReceived(usersMessage));
   }

   private void checkResult(String amount, String firstCurrency, String secondCurrency) {
      String start = amount + " " + firstCurrency + " is ";
      String botsResponse = testBot.onUpdateReceived(amount);
      assertTrue(botsResponse.startsWith(start));
      assertTrue(botsResponse.endsWith(secondCurrency));
      String value = botsResponse.replace(start, "").replace(" " + secondCurrency, "");
      Matcher matcher = isPositiveNumber.matcher(value);
      assertTrue(matcher.matches());
   }

   private void stop() {
      testBot = new Bot(converterService);
   }
}