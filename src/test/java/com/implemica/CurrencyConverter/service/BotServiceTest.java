package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.configuration.SpringConfiguration;
import com.implemica.CurrencyConverter.configuration.WebSocketConfiguration;
import com.implemica.CurrencyConverter.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests BotService class
 *
 * @author Daria S.
 * @version 08.02.2019 14:32
 */
@SpringBootTest(classes = {BotService.class, ConverterService.class})
@Import({SpringConfiguration.class, WebSocketConfiguration.class})
public class BotServiceTest {

   /**
    * Test bot instance
    */
   @Autowired
   private BotService testBotService;

   /**
    * Test user instance
    */
   private User testUser = new User(12, "testUser");
   /**
    * Unique string, which uses for messages, which has non text content.
    */
   private static final String UNIQUE = BotService.UNIQUE;

   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = "Hello! Could I help you?";

   /**
    * Message to the user with the suggestion of a new conversion
    */
   private static final String CONVERT_MESSAGE = " You can use /convert to make me new convert currencies";
   /**
    * Stop message to the user
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
   /**
    * Bot's response for /convert command
    */
   private static final String FIRST_CONVERT_MESSAGE = "Please, type in the currency to convert from (example: USD)";
   /**
    * Start of bot's response after entering first currency
    */
   private static final String SECOND_CONVERT_MESSAGE_1 = "OK, you wish to convert from ";
   /**
    * End of bot's response after entering first currency
    */
   private static final String SECOND_CONVERT_MESSAGE_2 = " to what currency? (example: EUR)";
   /**
    * Bot's response after entering second currency
    */
   private static final String THIRD_CONVERT_MESSAGE = "Enter the amount to convert from ";

   /**
    * Bot's response for not-text requests
    */
   private static final String UNREADABLE_CONTENT_MESSAGE = "Sorry, but this message contains incorrect content. Please, don't send me messages, which I can't handle. " + CONVERT_MESSAGE;
   /**
    * Regular expression, which matches, that string contains only the positive number
    */
   private static final String POSITIVE_NUMBER_REGEX = "^\\d+([.,])?\\d*$";
   /**
    * Defines the regular expression, which is needed to check that string is positive number
    */
   private static final Pattern isPositiveNumber = Pattern.compile(POSITIVE_NUMBER_REGEX);

   /**
    * Tests, that bot's response to /start command is correct
    */
   @Test
   void startCommand() {
      compute(START, START_MESSAGE);
   }

   /**
    * Tests, that bot's response to /stop command is correct
    */
   @Test
   void stopCommand() {
      compute(STOP, STOP_MESSAGE);
   }

   /**
    * Tests, that bot's response to text, which is not command is correct
    */
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

   /**
    * Tests, that bot's response to incorrect content is correct, no matter what time it was sent
    */
   @Test
   void unreadableContent() {
      //wrong content
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after command
      compute(START, START_MESSAGE);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      compute(STOP, STOP_MESSAGE);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after second conversion step
      interruptConvertOnSecondStep("usd", "USD", UNIQUE, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnSecondStep("xxx", "XXX", UNIQUE, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnSecondStep("hello", "HELLO", UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after third conversion step
      interruptConvertOnThirdStep("pln", "PLN", "uah", "UAH", UNIQUE, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("cad", "CAD", "uss", "USS", UNIQUE, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("mop", "MOP", "say", "SAY", UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      interruptConvertOnThirdStep("sit", "SIT", "eur", "EUR", UNIQUE, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("trl", "TRL", "xbb", "XBB", UNIQUE, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("xbc", "XBC", "come", "COME", UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      interruptConvertOnThirdStep("how", "HOW", "rub", "RUB", UNIQUE, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("are", "ARE", "afa", "AFA", UNIQUE, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("you", "YOU", "talk", "TALK", UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after conversion
      rightScript("Shp", "SHP", "Usd", "USD", "30.6");
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("Myr", "MYR", "KhR", "KHR", "-67");
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withUnsupportedCurrency("PLN", "PLN", "ITL", "ITL", "0.1", true);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("Gel", "GEL", "ITL", "ITL", "0.11,");
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("UAH", "UAH", "rubli", "RUBLI", "2000", true);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("Xof", "XOF", "hi", "HI", "abc", true);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withUnsupportedCurrency("xts", "XTS", "dop", "DOP", "301", false);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("cou", "COU", "ern", "ERN", "-17");
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withUnsupportedCurrency("xts", "XTS", "csd", "CSD", "804", false);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("mtl", "MTL", "USS", "USS", "-10000");
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("xxx", "XXX", "val", "VAL", "13.3", true);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("xua", "XUA", "I", "I", "0 01", true);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("russian", "RUSSIAN", "uah", "UAH", "300", false);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("tttttttt", "TTTTTTTT", "isk", "ISK", "-34", false);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("lalala", "LALALA", "tMM", "TMM", "21", false);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("go", "GO", "xts", "XTS", "-0.1", false);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("dog", "DOG", "bird", "BIRD", "100000", false);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("spoon", "SPOON", "plate", "PLATE", "4 cups", false);
      compute(UNIQUE, UNREADABLE_CONTENT_MESSAGE);

   }


   /**
    * Tests, that bot reaction for two consecutive commands is correct
    */
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

   /**
    * Tests, that bot reaction for interruption conversion on second step by some command is correct
    */
   @Test
   void interruptConvertOnSecondStepTest() {
      //we can interrupt conversion only if we give new command

      //correct currency
      interruptConvertOnSecondStep("uah", "UAH", START, START_MESSAGE);
      interruptConvertOnSecondStep("Rub", "RUB", STOP, STOP_MESSAGE);
      interruptConvertOnSecondStep("EUR", "EUR", CONVERT, FIRST_CONVERT_MESSAGE);


      //wrong currency
      interruptConvertOnSecondStep("123", "123", START, START_MESSAGE);
      interruptConvertOnSecondStep("ZZZ", "ZZZ", STOP, STOP_MESSAGE);
      interruptConvertOnSecondStep("Bot", "BOT", CONVERT, FIRST_CONVERT_MESSAGE);


      //unsupported currency
      interruptConvertOnSecondStep("tmm", "TMM", START, START_MESSAGE);
      interruptConvertOnSecondStep("zwd", "ZWD", STOP, STOP_MESSAGE);
      interruptConvertOnSecondStep("xua", "XUA", CONVERT, FIRST_CONVERT_MESSAGE);
   }

   /**
    * Tests, that bot reaction for interruption conversion on third step by some command is correct
    */
   @Test
   void interruptConvertOnThirdStepTest() {
      //both correct currency
      interruptConvertOnThirdStep("Pln", "PLN", "Eur", "EUR", START, START_MESSAGE);
      interruptConvertOnThirdStep("BgN", "BGN", "TRy", "TRY", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("All", "ALL", "PHP", "PHP", CONVERT, FIRST_CONVERT_MESSAGE);


      //first correct, second unsupported
      interruptConvertOnThirdStep("Cad", "CAD", "iep", "IEP", START, START_MESSAGE);
      interruptConvertOnThirdStep("CZk", "CZK", "xbb", "XBB", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("IQD", "IQD", "N L G", "NLG", CONVERT, FIRST_CONVERT_MESSAGE);


      //first correct, second wrong
      interruptConvertOnThirdStep("mxn", "MXN", "sun", "SUN", START, START_MESSAGE);
      interruptConvertOnThirdStep("Uzs", "UZS", "java", "JAVA", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("MOP", "MOP", "Telegram", "TELEGRAM", CONVERT, FIRST_CONVERT_MESSAGE);


      //first unsupported, second correct
      interruptConvertOnThirdStep("Mtl", "MTL", "bif", "BIF", START, START_MESSAGE);
      interruptConvertOnThirdStep("Eec", "EEC", "cop", "COP", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("Mgf", "MGF", "Ars", "ARS", CONVERT, FIRST_CONVERT_MESSAGE);


      //both unsupported
      interruptConvertOnThirdStep("Usn", "USN", "byb", "BYB", START, START_MESSAGE);
      interruptConvertOnThirdStep("Fim", "FIM", "Trl", "TRL", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("CHW", "CHW", "USS", "USS", CONVERT, FIRST_CONVERT_MESSAGE);


      //first unsupported, second wrong
      interruptConvertOnThirdStep("Sit", "SIT", "rus", "RUS", START, START_MESSAGE);
      interruptConvertOnThirdStep("Luf", "LUF", "eng", "ENG", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("A ZM", "AZM", "ua", "UA", CONVERT, FIRST_CONVERT_MESSAGE);


      //first wrong, second correct
      interruptConvertOnThirdStep("shift", "SHIFT", "Uah", "UAH", START, START_MESSAGE);
      interruptConvertOnThirdStep("10 usd", "10USD", "Usd", "USD", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("bought", "BOUGHT", "PLN", "PLN", CONVERT, FIRST_CONVERT_MESSAGE);


      //first wrong, second unsupported
      interruptConvertOnThirdStep("hi", "HI", "Rur", "RUR", START, START_MESSAGE);
      interruptConvertOnThirdStep("bye", "BYE", "Uyi", "UYI", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("111", "111", "Grd", "GRD", CONVERT, FIRST_CONVERT_MESSAGE);


      //both wrong
      interruptConvertOnThirdStep("lets", "LETS", "oh", "OH", START, START_MESSAGE);
      interruptConvertOnThirdStep("speak", "SPEAK", "no", "NO", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("english", "ENGLISH", "hurrah", "HURRAH", CONVERT, FIRST_CONVERT_MESSAGE);


   }

   /**
    * Tests, that bot reaction for command after all steps of conversion is correct
    */
   @Test
   void commandAfterConversionTest() {
      //with right currencies and right amount
      rightScript("Shp", "SHP", "Usd", "USD", "30.6");
      compute(START, START_MESSAGE);


      rightScript("EUr", "EUR", "UAh", "UAH", "150,50");
      compute(STOP, STOP_MESSAGE);


      rightScript("RUB", "RUB", "CAD", "CAD", "542");
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //with right currencies and wrong amount
      withWrongAmount("Myr", "MYR", "KhR", "KHR", "-67");
      compute(START, START_MESSAGE);


      withWrongAmount("Mxv", "MXV", "XAU", "XAU", "one");
      compute(STOP, STOP_MESSAGE);


      withWrongAmount("LTL", "LTL", "QAR", "QAR", "167f");
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second unsupported, right amount
      withUnsupportedCurrency("PLN", "PLN", "ITL", "ITL", "0.1", true);
      compute(START, START_MESSAGE);


      withUnsupportedCurrency("EUr", "EUR", "Bov", "BOV", "150,50", true);
      compute(STOP, STOP_MESSAGE);


      withUnsupportedCurrency("UAH", "UAH", "XUA", "XUA", "542", true);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second unsupported, wrong amount
      withWrongAmount("Gel", "GEL", "ITL", "ITL", "0.11,");
      compute(START, START_MESSAGE);


      withWrongAmount("Cny", "CNY", "Che", "CHE", "-98");
      compute(STOP, STOP_MESSAGE);


      withWrongAmount("USD", "USD", "SRG", "SRG", "87$");
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second wrong, right amount
      withWrongCurrency("UAH", "UAH", "rubli", "RUBLI", "2000", true);
      compute(START, START_MESSAGE);


      withWrongCurrency("bgn", "BGN", "uusd", "UUSD", "12000", true);
      compute(STOP, STOP_MESSAGE);


      withWrongCurrency("Try", "TRY", "euro", "EURO", "10", true);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second wrong, wrong amount
      withWrongCurrency("Xof", "XOF", "hi", "HI", "abc", true);
      compute(START, START_MESSAGE);


      withWrongCurrency("SYP", "SYP", "big", "BIG", "-12", true);
      compute(STOP, STOP_MESSAGE);


      withWrongCurrency("USD", "USD", "from", "FROM", "12.5%", true);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second correct, right amount
      withUnsupportedCurrency("xts", "XTS", "dop", "DOP", "301", false);
      compute(START, START_MESSAGE);


      withUnsupportedCurrency("SRG", "SRG", "UYU", "UYU", "7", false);
      compute(STOP, STOP_MESSAGE);


      withUnsupportedCurrency("zWR", "ZWR", "INR", "INR", "800", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second correct, wrong amount
      withWrongAmount("cou", "COU", "ern", "ERN", "-17");
      compute(START, START_MESSAGE);


      withWrongAmount("esp", "ESP", "lvl", "LVL", "a");
      compute(STOP, STOP_MESSAGE);


      withWrongAmount("zWR", "ZWR", "INR", "INR", "111..");
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //both currencies unsupported, right amount
      withUnsupportedCurrency("xts", "XTS", "csd", "CSD", "804", false);
      compute(START, START_MESSAGE);


      withUnsupportedCurrency("SRG", "SRG", "aym", "AYM", "90.7", false);
      compute(STOP, STOP_MESSAGE);


      withUnsupportedCurrency("zWR", "ZWR", "Iep", "IEP", "505,5", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //both currencies unsupported, wrong amount
      withWrongAmount("mtl", "MTL", "USS", "USS", "-10000");
      compute(START, START_MESSAGE);


      withWrongAmount("rol", "ROL", "RUr", "RUR", "12 6");
      compute(STOP, STOP_MESSAGE);


      withWrongAmount("sKK", "SKK", "Xbb", "XBB", "08,,1");
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second wrong, right amount
      withWrongCurrency("xxx", "XXX", "val", "VAL", "13.3", true);
      compute(START, START_MESSAGE);


      withWrongCurrency("Fim", "FIM", "little", "LITTLE", "444", true);
      compute(STOP, STOP_MESSAGE);


      withWrongCurrency("CSD", "CSD", "sad", "SAD", "222222222222", true);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second wrong, wrong amount
      withWrongCurrency("xua", "XUA", "I", "I", "0 01", true);
      compute(START, START_MESSAGE);


      withWrongCurrency("Gwp", "GWP", "find", "FIND", "-10", true);
      compute(STOP, STOP_MESSAGE);


      withWrongCurrency("Nlg", "NLG", "you", "YOU", "16p", true);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency wrong, second correct, right amount
      withWrongCurrency("russian", "RUSSIAN", "uah", "UAH", "300", false);
      compute(START, START_MESSAGE);


      withWrongCurrency("spanish", "SPANISH", "usd", "USD", "10.5", false);
      compute(STOP, STOP_MESSAGE);


      withWrongCurrency("canadian", "CANADIAN", "eur", "EUR", "600", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency wrong, second correct, wrong amount
      withWrongCurrency("tttttttt", "TTTTTTTT", "isk", "ISK", "-34", false);
      compute(START, START_MESSAGE);


      withWrongCurrency("aaaa", "AAAA", "tmt", "TMT", "7.7.", false);
      compute(STOP, STOP_MESSAGE);


      withWrongCurrency("Poln", "POLN", "pyg", "PYG", "9pln", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency wrong, second unsupported, right amount
      withWrongCurrency("lalala", "LALALA", "tMM", "TMM", "21", false);
      compute(START, START_MESSAGE);


      withWrongCurrency("tururu", "TURURU", "luf", "LUF", "18,40", false);
      compute(STOP, STOP_MESSAGE);


      withWrongCurrency("bah", "BAH", "azm", "AZM", "0.52", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency wrong, second unsupported, wrong amount
      withWrongCurrency("go", "GO", "xts", "XTS", "-0.1", false);
      compute(START, START_MESSAGE);


      withWrongCurrency("wait", "WAIT", "veb", "VEB", "veb", false);
      compute(STOP, STOP_MESSAGE);


      withWrongCurrency("stop", "STOP", "eec", "EEC", "9 9", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //both currencies wrong, right amount
      withWrongCurrency("dog", "DOG", "bird", "BIRD", "100000", false);
      compute(START, START_MESSAGE);


      withWrongCurrency("snow", "SNOW", "water", "WATER", "30", false);
      compute(STOP, STOP_MESSAGE);


      withWrongCurrency("part", "PART", "whole", "WHOLE", "58", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //both currencies wrong, wrong amount
      withWrongCurrency("spoon", "SPOON", "plate", "PLATE", "4 cups", false);
      compute(START, START_MESSAGE);


      withWrongCurrency("child", "CHILD", "man", "MAN", "-2", false);
      compute(STOP, STOP_MESSAGE);


      withWrongCurrency("notes", "NOTES", "book", "BOOK", "4000f", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);

   }

   /**
    * Tests, that bot's reaction for few /convert command is equal to expected
    */
   @Test
   void fewConvertCommand() {
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      rightScript("usd", "USD", "uah", "UAH", "10");


      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      rightScript("rub", "RUB", "EUR", "EUR", "4");
   }

   /**
    * Tests, that if both currencies and amount are correct, that bot's response is equal to expected
    */
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

   /**
    * Tests, that if both currencies are correct and amount is wrong, that bot's response is equal to expected
    */
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

   /**
    * Tests, that if one or two currencies are wrong, that bot's response is equal to expected for wrong and right amount
    */
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
      withWrongCurrency("adp", "ADP", "000", "000", "6", true);
      withWrongCurrency("Ats", "ATS", "opt", "OPT", "-9.6", true);

      //second currency is unsupported
      withWrongCurrency("007", "007", "AFA", "AFA", "5500", false);
      withWrongCurrency("dog", "DOG", "p t e", "PTE", "-76", false);
   }

   /**
    * Tests, that if one or two currencies are unsupported, that bot's response is equal to expected for wrong and right amount
    */
   @Test
   void unsupportedCurrency() {
      //first currency is unsupported, right amount
      withUnsupportedCurrency("mzm", "MZM", "rub", "RUB", "1000", false);
      withUnsupportedCurrency("Xpt", "XPT", "Usd", "USD", "2.5", false);

      //first currency is unsupported, wrong amount
      withWrongAmount("rol", "ROL", "rub", "RUB", "-34543");
      withWrongAmount("Ghc", "GHC", "Usd", "USD", "511a");


      //second currency is unsupported, right amount
      withUnsupportedCurrency("rub", "RUB", "skk", "SKK", "7,6", true);
      withUnsupportedCurrency("Usd", "USD", "Xbc", "XBC", "762", true);

      //second currency is unsupported, wrong amount
      withWrongAmount("rub", "RUB", "veb", "VEB", "one hundred");
      withWrongAmount("Usd", "USD", "Aym", "AYM", "-4.99q");


      //both currencies is unsupported, right amount
      withUnsupportedCurrency("Xbd", "XBD", "Bef", "BEF", "107.5", false);
      withUnsupportedCurrency("CYP", "CYP", "XXX", "XXX", "20000", false);

      //both currencies is unsupported, wrong amount
      withWrongAmount("Xsu", "XSU", "Dem", "DEM", "9*9");
      withWrongAmount("Xfo", "XFO", "XBA", "XBA", "10+45");
   }


   /**
    * Checks, that bot's reaction for interruption conversion on third step by some command is equal to expected
    *
    * @param firstCurrency             the currency to convert from
    * @param firstCurrencyInUpperCase  the currency to convert from in upper case
    * @param secondCurrency            the currency to convert to
    * @param secondCurrencyInUpperCase the currency to convert to in upper case
    * @param command                   command after conversion
    * @param response                  expected bot's response for command
    */
   private void interruptConvertOnThirdStep(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                            String secondCurrencyInUpperCase, String command, String response) {
      script(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase);
      compute(command, response);
   }

   /**
    * Checks, that bot's reaction for interruption conversion on second step by some command is equal to expected
    *
    * @param firstCurrency            the currency to convert from
    * @param firstCurrencyInUpperCase the currency to convert from in upper case
    * @param command                  command after conversion
    * @param response                 expected bot's response for command
    */
   private void interruptConvertOnSecondStep(String firstCurrency, String firstCurrencyInUpperCase, String command, String response) {
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(firstCurrency, SECOND_CONVERT_MESSAGE_1 + firstCurrencyInUpperCase + SECOND_CONVERT_MESSAGE_2);
      compute(command, response);
   }

   /**
    * Checks, that bot's reaction entering some word and then command is equal to expected
    *
    * @param word     some word, which is not a command and gotten first
    * @param command  command after gotten word
    * @param response expected bot's response for command
    */
   private void wordAndCommand(String word, String command, String response) {
      enterOtherWords(word);
      compute(command, response);
   }

   /**
    * Checks, that bot's reaction entering command and then some word is equal to expected
    *
    * @param command  command for bot
    * @param response expected bot's response for command
    * @param word     some word, which is not a command and gotten after command
    */
   private void commandAndWord(String command, String response, String word) {
      compute(command, response);
      enterOtherWords(word);
   }

   /**
    * Checks, that bot's reaction for two consecutive commands is equal to expected
    *
    * @param firstCommand   first command to bot
    * @param firstResponse  bot's response for first command
    * @param secondCommand  next command to bot
    * @param secondResponse expected bot's response for next command
    */
   private void twoCommands(String firstCommand, String firstResponse, String secondCommand, String secondResponse) {
      compute(firstCommand, firstResponse);
      compute(secondCommand, secondResponse);
   }

   /**
    * Checks, that bot's reaction in conversion for unsupported currencies is equal to expected
    *
    * @param firstCurrency             the currency to convert from
    * @param firstCurrencyInUpperCase  the currency to convert from in upper case
    * @param secondCurrency            the currency to convert to
    * @param secondCurrencyInUpperCase the currency to convert to in upper case
    * @param amount                    amount of first currency
    * @param firstIsCorrect            true, if first currency is right and supported, false otherwise
    */
   private void withUnsupportedCurrency(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                        String secondCurrencyInUpperCase, String amount, boolean firstIsCorrect) {
      rightScriptWithIncorrectCurrency(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase, amount,
              firstIsCorrect, true);
   }

   /**
    * Checks, that bot's reaction in conversion for wrong currencies is equal to expected
    *
    * @param firstCurrency             the currency to convert from
    * @param firstCurrencyInUpperCase  the currency to convert from in upper case
    * @param secondCurrency            the currency to convert to
    * @param secondCurrencyInUpperCase the currency to convert to in upper case
    * @param amount                    amount of first currency
    * @param firstIsCorrect            false, if first currency is some non-currency word, true otherwise
    */
   private void withWrongCurrency(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                  String secondCurrencyInUpperCase, String amount, boolean firstIsCorrect) {
      rightScriptWithIncorrectCurrency(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase, amount,
              firstIsCorrect, false);
   }

   /**
    * Checks, that bot's reaction in conversion for incorrect currencies is equal to expected
    *
    * @param firstCurrency             the currency to convert from
    * @param firstCurrencyInUpperCase  the currency to convert from in upper case
    * @param secondCurrency            the currency to convert to
    * @param secondCurrencyInUpperCase the currency to convert to in upper case
    * @param amount                    amount of first currency
    * @param firstIsCorrect            true, if first currency is right
    * @param isUnsupported             true,if one or two currencies are unsupported
    */
   private void rightScriptWithIncorrectCurrency(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                                 String secondCurrencyInUpperCase, String amount, boolean firstIsCorrect, boolean isUnsupported) {
      String startOfMessage = "Sorry, but currency is not valid: ";
      String wrongCurrency;
      if (isUnsupported) {
         startOfMessage = "Currency not supported: ";
      }
      if (firstIsCorrect) {
         wrongCurrency = secondCurrencyInUpperCase;
      } else {
         wrongCurrency = firstCurrencyInUpperCase;
      }

      String message = startOfMessage + wrongCurrency + CONVERT_MESSAGE;

      rightScriptWithWrongValue(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase, amount, message);

   }

   /**
    * Checks, that bot's reaction in conversion for wrong amount is equal to expected
    *
    * @param firstCurrency             the currency to convert from
    * @param firstCurrencyInUpperCase  the currency to convert from in upper case
    * @param secondCurrency            the currency to convert to
    * @param secondCurrencyInUpperCase the currency to convert to in upper case
    * @param amount                    amount of first currency
    */
   private void withWrongAmount(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                String secondCurrencyInUpperCase, String amount) {
      String message = "Sorry, but \"" + amount + "\" is not a valid number. Conversion is impossible. " + CONVERT_MESSAGE;
      rightScriptWithWrongValue(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase, amount, message);
   }

   /**
    * Checks, that bot reaction for conversion, which contains wrong value is equal to expected
    *
    * @param firstCurrency             the currency to convert from
    * @param firstCurrencyInUpperCase  the currency to convert from in upper case
    * @param secondCurrency            the currency to convert to
    * @param secondCurrencyInUpperCase the currency to convert to in upper case
    * @param amount                    amount of first currency
    * @param message                   expected bot's response for given amount
    */
   private void rightScriptWithWrongValue(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                                          String secondCurrencyInUpperCase, String amount, String message) {
      script(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase);
      compute(amount, message);
   }

   /**
    * Checks, that bot reaction for consecutive /convert command and last result are equal to expected
    *
    * @param firstCurrency             the currency to convert from
    * @param firstCurrencyInUpperCase  the currency to convert from in upper case
    * @param secondCurrency            the currency to convert to
    * @param secondCurrencyInUpperCase the currency to convert to in upper case
    * @param amount                    amount of first currency
    */
   private void rightScript(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                            String secondCurrencyInUpperCase, String amount) {
      script(firstCurrency, firstCurrencyInUpperCase, secondCurrency, secondCurrencyInUpperCase);
      checkResult(amount, firstCurrencyInUpperCase, secondCurrencyInUpperCase);
   }

   /**
    * Checks, that bot's responses on conversion steps are equal to expected
    *
    * @param firstCurrency             the currency to convert from
    * @param firstCurrencyInUpperCase  the currency to convert from in upper case
    * @param secondCurrency            the currency to convert to
    * @param secondCurrencyInUpperCase the currency to convert to in upper case
    */
   private void script(String firstCurrency, String firstCurrencyInUpperCase, String secondCurrency,
                       String secondCurrencyInUpperCase) {
      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(firstCurrency, SECOND_CONVERT_MESSAGE_1 + firstCurrencyInUpperCase + SECOND_CONVERT_MESSAGE_2);
      compute(secondCurrency, THIRD_CONVERT_MESSAGE + firstCurrencyInUpperCase + " to " + secondCurrencyInUpperCase);
   }

   /**
    * Checks, that bot's reaction for non-command word is equal to expected
    *
    * @param message some word, which is not a command
    */
   private void enterOtherWords(String message) {
      compute(message, "Sorry, but I don't understand what \"" + message + "\" means." + CONVERT_MESSAGE);
   }

   /**
    * Checks, that bot's response for user's request is correct
    *
    * @param usersMessage user's request to bot
    * @param botsResponse expected bot's response to user
    */
   private void compute(String usersMessage, String botsResponse) {
      assertEquals(botsResponse, testBotService.onUpdateReceived(usersMessage, testUser));
   }

   /**
    * Checks, that on last step bot's response contains positive value
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    * @param amount         amount of first currency
    */
   private void checkResult(String amount, String firstCurrency, String secondCurrency) {
      String start = amount + " " + firstCurrency + " is ";
      String botsResponse = testBotService.onUpdateReceived(amount, testUser);
      assertTrue(botsResponse.startsWith(start));
      assertTrue(botsResponse.endsWith(secondCurrency));
      String value = botsResponse.replace(start, "").replace(" " + secondCurrency, "");
      Matcher matcher = isPositiveNumber.matcher(value);
      assertTrue(matcher.matches());
   }

}