package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.configuration.SpringConfiguration;
import com.implemica.CurrencyConverter.configuration.WebSocketConfiguration;
import com.implemica.CurrencyConverter.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;
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
    * Money sign for result message
    */
   private static final String MONEY_SIGN = "\uD83D\uDCB0";
   /**
    * Test user instance
    */
   private User testUser = new User(12, "testUser");
   /**
    * Unique string, which uses for messages, which has non text content.
    */
   private static final String WRONG_CONTENT = BotService.UNIQUE;
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
    * Bot's response for not-text requests
    */
   private static final String UNREADABLE_CONTENT_MESSAGE = "❗Sorry, but this message contains " +
           "incorrect content. Please, don't send me messages, which I can't handle." + CONVERT_MESSAGE;
   /**
    * Bot's response for incorrect request from user
    */
   private static final String INCORRECT_REQUEST_MESSAGE = "❗Sorry, but your request is incorrect." + CONVERT_MESSAGE;
   /**
    * Regular expression, which matches, that string contains only the positive number
    */
   private static final String POSITIVE_NUMBER_REGEX = "^\\d+([.,])?\\d*$";
   /**
    * Defines the regular expression, which is needed to check that string is positive number
    */
   private static final Pattern isPositiveNumber = Pattern.compile(POSITIVE_NUMBER_REGEX);

   /**
    * Array of available currencies that can be converted between themselves by {@link ConverterService}.
    */
   private static String[] existingCurrency = new String[]{"UAH", "AWG", "GEL", "ALL", "ZAR", "BND", "JMD", "BRL",
           "RUB", "BAM", "SZL", "GNF", "NZD", "SYP", "MKD", "BZD", "KWD", "SLL", "ETB", "BYN", "AZN", "XPF", "BBD",
           "CDF", "RWF", "SOS", "BDT", "ILS", "EGP", "IQD", "RON", "COP", "SEK", "MMK", "SAR", "DJF", "HTG", "PKR",
           "GTQ", "PHP", "TOP", "TND", "VEF", "PEN", "CVE", "NIO", "HUF", "SCR", "THB", "FJD", "MRO", "AOA", "XAF",
           "BOB", "KZT", "LSL", "TMT", "HRK", "BGN", "OMR", "MYR", "VUV", "KES", "XCD", "ARS", "GBP", "SDG", "MUR",
           "VND", "MNT", "GMD", "BSD", "HKD", "GIP", "PGK", "KGS", "LYD", "CAD", "BWP", "IDR", "LRD", "JPY", "NAD",
           "MVR", "ISK", "PAB", "AMD", "BHD", "NOK", "SRD", "IRR", "GYD", "TWD", "ZMW", "XOF", "MWK", "KMF", "KRW",
           "TZS", "DKK", "HNL", "AUD", "MAD", "CRC", "MDL", "TRY", "LBP", "INR", "CLP", "GHS", "NGN", "SBD", "LKR",
           "BIF", "CHF", "DOP", "YER", "PLN", "TJS", "CZK", "MXN", "WST", "UGX", "SVC", "SGD", "PYG", "JOD", "AFN",
           "NPR", "ANG", "QAR", "USD", "ERN", "CUP", "MOP", "CNY", "TTD", "KHR", "DZD", "UZS", "EUR", "AED", "UYU",
           "MZN", "BTN", "ETH"};

   /**
    * Format for amount of currency
    */
   private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.##");
   /**
    * For creating random numbers
    */
   private static final Random RANDOM = new Random();

   /**
    * Tests, that bot's response to /start command is correct
    */
   @Test
   void startCommand() {
      assertCommand(START, START_MESSAGE);
   }

   /**
    * Tests, that bot's response to /stop command is correct
    */
   @Test
   void stopCommand() {
      assertCommand(STOP, STOP_MESSAGE);
   }

   /**
    * Tests, that bot's response to users is correct, if they calls it at the same time
    */
   @Test
   void twoUsersTest() {
      User user1 = new User(123, "ludvig");
      User user2 = new User(321, "darell");

      assertEquals(FIRST_CONVERT_MESSAGE, testBotService.processCommand("/convert", user1));
      assertEquals(INCORRECT_REQUEST_MESSAGE, testBotService.processCommand("usd", user2));
      assertEquals(SECOND_CONVERT_MESSAGE_1 + "UAH" + SECOND_CONVERT_MESSAGE_2, testBotService.processCommand("uah", user1));
      assertEquals(FIRST_CONVERT_MESSAGE, testBotService.processCommand("/convert", user2));
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
      enterOtherWords("Eur");
      enterOtherWords("MY name is John");
      enterOtherWords("/clear");
   }

   /**
    * Tests, that if both currencies and amount are correct, that bot's response is equal to expected
    */
   @Test
   void rightScriptTest() {
      rightScript("usd", "uah", "1");
      rightScript("brl", "Usd", "77");
      rightScript("uSd", "eUr", "10,5");
      rightScript("ruB", "caD", "33");
      rightScript("CnY", "CzK", "12000,75");
      rightScript("DKk", "NZd", "13000000001");
      rightScript("bGN", "tRY", "0.8");
      rightScript("RUB", "UAH", "15");
      rightScript("pln  ", "usd ", "55.4");
      rightScript(" inR", "eUr ", "811");
   }

   @Test
   void oneLineTest() {
      oneLineRequest("10 usd to uah");
      oneLineRequest("16 rub in usd");
      oneLineRequest("7,4 pln to bob");
      oneLineRequest("1000 bgn in jpy");
      oneLineRequest("0 eur to lkr");
      oneLineRequest("16872 inr in afn");
      oneLineRequest("23.43 cad to xof");
      oneLineRequest("9021,2 php in irr");
      oneLineRequest("3821 irr to clp");
      oneLineRequest("321 omr in tzs");

   }

   /**
    * Tests, that rounding is correct
    */
   @Test
   void roundingTest() {
      //small values
      checkRounding("jpy", "0.000000001", "0");
      checkRounding("Zmw", "0.00000001", "0");
      checkRounding("XcD", "0.0000001", "0");
      checkRounding("lkR", "0.000001", "0");
      checkRounding("MRo", "0.00001", "0");
      checkRounding("bob", "0.0001", "0");
      checkRounding("CAD", "0.001", "0");
      checkRounding("eur", "0.004", "0");
      checkRounding("Uah", "0.0049", "0");
      checkRounding("INr", "0.005", "0.01");
      checkRounding("BgN", "0.009", "0.01");
      checkRounding("THB", "0.01", "0.01");
      checkRounding("tRY", "0.0149", "0.01");
      checkRounding("tWd", "0.015", "0.02");
      checkRounding("phP", "0.019", "0.02");
      checkRounding("Huf", "0.094", "0.09");
      checkRounding("RUb", "0.095", "0.1");
      checkRounding("PLN", "0.099", "0.1");
      checkRounding("sgd", "0.991", "0.99");
      checkRounding("InR", "0.9949", "0.99");
      checkRounding("dKK", "0.995", "1");
      checkRounding("sEk", "0.999", "1");
      checkRounding("Jpy", "9.9949", "9.99");
      checkRounding("noK", "9.995", "10");
      checkRounding("MYR", "9.999", "10");


      checkRounding("BRL", "7,000000001", "7");
      checkRounding("Clp", "7,00000001", "7");
      checkRounding("iQd", "7,0000001", "7");
      checkRounding("omR", "7,000001", "7");
      checkRounding("AFn", "7,00001", "7");
      checkRounding("xOF", "7,0001", "7");
      checkRounding("ils", "7,001", "7");
      checkRounding("Gbp", "7,004", "7");
      checkRounding("bRl", "7,0049", "7");
      checkRounding("huF", "7,005", "7.01");
      checkRounding("HKd", "7,009", "7.01");
      checkRounding("UAH", "7,01", "7.01");
      checkRounding("DkK", "7,0149", "7.01");
      checkRounding("eUR", "7,015", "7.02");
      checkRounding("IDR", "7,019", "7.02");
      checkRounding("aud", "7,094", "7.09");
      checkRounding("Kpw", "7,095", "7.1");
      checkRounding("mXn", "7,099", "7.1");
      checkRounding("nzD", "7,991", "7.99");
      checkRounding("NOk", "7,9949", "7.99");
      checkRounding("PkR", "7,995", "8");
      checkRounding("pLN", "7,999", "8");
      checkRounding("TWD", "39,9949", "39.99");
      checkRounding("thb", "39,995", "40");
      checkRounding("CHf", "39,999", "40");


      //big values
      checkRounding("uah", "78566666666666666666666690.000000001","78566666666666666666666690");
      checkRounding("Iqd", "78566666666666666666666690.00000001", "78566666666666666666666690");
      checkRounding("mZn", "78566666666666666666666690.0000001", "78566666666666666666666690");
      checkRounding("clP", "78566666666666666666666690.000001", "78566666666666666666666690");
      checkRounding("OMr", "78566666666666666666666690.00001", "78566666666666666666666690");
      checkRounding("IrR", "78566666666666666666666690.0001", "78566666666666666666666690");
      checkRounding("bOB", "78566666666666666666666690.001", "78566666666666666666666690");
      checkRounding("BYR", "78566666666666666666666690.004", "78566666666666666666666690");
      checkRounding("lkr", "78566666666666666666666690.0049", "78566666666666666666666690");
      checkRounding("Tzs", "78566666666666666666666690.005", "78566666666666666666666690.01");
      checkRounding("mRo", "78566666666666666666666690.009", "78566666666666666666666690.01");
      checkRounding("xcD", "78566666666666666666666690.01", "78566666666666666666666690.01");
      checkRounding("PHp", "78566666666666666666666690.0149", "78566666666666666666666690.01");
      checkRounding("AfN", "78566666666666666666666690.015", "78566666666666666666666690.02");
      checkRounding("bRL", "78566666666666666666666690.019", "78566666666666666666666690.02");
      checkRounding("INR", "78566666666666666666666690.094", "78566666666666666666666690.09");
      checkRounding("zmw", "78566666666666666666666690.095", "78566666666666666666666690.1");
      checkRounding("Xof", "78566666666666666666666690.099", "78566666666666666666666690.1");
      checkRounding("pLn", "78566666666666666666666690.991", "78566666666666666666666690.99");
      checkRounding("rUb", "78566666666666666666666690.9949", "78566666666666666666666690.99");
      checkRounding("trY", "78566666666666666666666690.995", "78566666666666666666666691");
      checkRounding("BTN", "78566666666666666666666690.999", "78566666666666666666666691");
      checkRounding("ChF", "5697432893257285276192932147919832,9949", "5697432893257285276192932147919832.99");
      checkRounding("hKD", "5697432893257285276192932147919832,995", "5697432893257285276192932147919833");
      checkRounding("KWD", "5697432893257285276192932147919832,999", "5697432893257285276192932147919833");
      
      
      checkRounding("CVE", "9999999999999999999999999,000000001","9999999999999999999999999");
      checkRounding("Awg", "9999999999999999999999999,00000001", "9999999999999999999999999");
      checkRounding("gEl", "9999999999999999999999999,0000001", "9999999999999999999999999");
      checkRounding("alL", "9999999999999999999999999,000001", "9999999999999999999999999");
      checkRounding("JMd", "9999999999999999999999999,00001", "9999999999999999999999999");
      checkRounding("BaM", "9999999999999999999999999,0001", "9999999999999999999999999");
      checkRounding("sYP", "9999999999999999999999999,001", "9999999999999999999999999");
      checkRounding("sll", "9999999999999999999999999,004", "9999999999999999999999999");
      checkRounding("Bbd", "9999999999999999999999999,0049", "9999999999999999999999999");
      checkRounding("Tzs", "9999999999999999999999999,005", "9999999999999999999999999.01");
      checkRounding("LYD", "9999999999999999999999999,009", "9999999999999999999999999.01");
      checkRounding("NgN", "9999999999999999999999999,01", "9999999999999999999999999.01");
      checkRounding("uYu", "9999999999999999999999999,0149", "9999999999999999999999999.01");
      checkRounding("ETh", "9999999999999999999999999,015", "9999999999999999999999999.02");
      checkRounding("bTN", "9999999999999999999999999,019", "9999999999999999999999999.02");
      checkRounding("jpY", "9999999999999999999999999,094", "9999999999999999999999999.09");
      checkRounding("KYd", "9999999999999999999999999,095", "9999999999999999999999999.1");
      checkRounding("hkd", "9999999999999999999999999,099", "9999999999999999999999999.1");
      checkRounding("chF", "9999999999999999999999999,991", "9999999999999999999999999.99");
      checkRounding("USD", "9999999999999999999999999,9949", "9999999999999999999999999.99");
      checkRounding("bgn", "9999999999999999999999999,995", "10000000000000000000000000");
      checkRounding("ruB", "9999999999999999999999999,999", "10000000000000000000000000");
      checkRounding("BHD", "7892767232313213989321748321974,9949", "7892767232313213989321748321974.99");
      checkRounding("aud", "7892767232313213989321748321974,995", "7892767232313213989321748321975");
      checkRounding("xOf", "7892767232313213989321748321974,999", "7892767232313213989321748321975");
   }

   /**
    * Tests, that result of conversion stable currencies lies between given borders
    */
   @Test
   void stableCurrencyWithAmountTest() {
      checkStableCurrencyWithAmount("gbp", "eur", "1000000000", "1", "1.2");
      checkStableCurrencyWithAmount("eur", "gbp", "0.078", "0.7", "1");

      checkStableCurrencyWithAmount("Gbp", "Usd", "1.0008", "1", "1.5");
      checkStableCurrencyWithAmount("Usd", "Gbp", "55555555555", "0.6", "1");

      checkStableCurrencyWithAmount("GBp", "CAd", "3627163921.13213", "1.5", "2");
      checkStableCurrencyWithAmount("CAd", "GBp", "728131321321321.2131", "0.3", "0.7");

      checkStableCurrencyWithAmount("GBP", "JPY", "0.001", "130", "160");
      checkStableCurrencyWithAmount("JPY", "GBP", "37281983.3213", "0.004", "0.009");



      checkStableCurrencyWithAmount("aud", "eur", "75000000000", "0.5", "0.8");
      checkStableCurrencyWithAmount("eur", "aud", "2.0004", "1.3", "1.8");

      checkStableCurrencyWithAmount("AUd", "Usd", "9877777777.873201", "0.5", "0.8");
      checkStableCurrencyWithAmount("Usd", "AUd", "1.093210", "1.3", "1.6");

      checkStableCurrencyWithAmount("Aud", "Hkd", "1432291043.2", "5.3", "5.9");
      checkStableCurrencyWithAmount("Hkd", "Aud", "1.00382910", "0.1", "0.3");

      checkStableCurrencyWithAmount("AUD", "JPY", "0.00356", "73", "84");
      checkStableCurrencyWithAmount("JPY", "AUD", "787652319.8392", "0.009", "0.02");
   }



   /**
    * Tests, that result of conversion stable currencies lies between given borders
    */
   @Test
   void stableCurrencyTest() {
      checkStableCurrency("usd", "eur", "0.7", "1");
      checkStableCurrency("eur", "usd", "1", "1.5");

      checkStableCurrency("kwd", "usd", "3", "3.5");
      checkStableCurrency("usd", "kwd", "0.1", "0.5");

      checkStableCurrency("gbp", "usd", "1", "1.5");
      checkStableCurrency("usd", "gbp", "0.6", "1");

      checkStableCurrency("usd", "cad", "1", "1.5");
      checkStableCurrency("cad", "usd", "0.5", "1");
   }

   /**
    * Tests that the same number is returned for the same currency
    */
   @Test
   void rightScriptWithIdenticalCurrencyTest() {
      for (String currency : existingCurrency) {
         rightScriptWithIdenticalCurrency(currency);
      }
   }

   /**
    * Tests that the same number is returned for the same currency
    */
   @Test
   void oneLineRequestWithIdenticalCurrencyTest() {
      for (String currency : existingCurrency) {
         oneLineRequestWithIdenticalCurrency(currency);
      }
   }

   /**
    * Tests, that if amount equals zero, then returned zero
    */
   @Test
   void zeroTest() {
      for (String currency : existingCurrency) {
         zeroAmount(currency, existingCurrency[RANDOM.nextInt(existingCurrency.length)]);
      }

   }

   /**
    * Tests, that if amount equals zero, then returned zero
    */
   @Test
   void zeroOneLineTest() {
      for (String currency : existingCurrency) {
         zeroAmountOneLine(currency, existingCurrency[RANDOM.nextInt(existingCurrency.length)]);
      }

   }


   /**
    * Tests, that bot reaction for command after all steps of conversion is correct
    */
   @Test
   void commandAfterConversionTest() {

      //with right currencies and right amount
      rightScript("Pln", "Usd", "30.6");
      assertCommand(START, START_MESSAGE);

      rightScript("EUr", "UAh", "150,50");
      assertCommand(STOP, STOP_MESSAGE);

      rightScript("Inr", "Rub", "29");
      oneLineRequest("12 uah to eur");

      rightScript("RUB", "CAD", "542");
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);


      //with right currencies and wrong amount
      withWrongAmount("Myr", "KhR", "-67");
      assertCommand(START, START_MESSAGE);

      withWrongAmount("Mxv", "XAU", "one");
      assertCommand(STOP, STOP_MESSAGE);

      withWrongAmount("php", "uah", "-1");
      oneLineRequest("99 Pln in Uah");

      withWrongAmount("LTL", "QAR", "167f");
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second unsupported, right amount
      withUnsupportedCurrency("PLN", "ITL", "0.1", true);
      assertCommand(START, START_MESSAGE);

      withUnsupportedCurrency("EUr", "Bov", "150,50", true);
      assertCommand(STOP, STOP_MESSAGE);

      withUnsupportedCurrency("pln", "Iep", "77.2", true);
      oneLineRequest("0.75 usd in rub");

      withUnsupportedCurrency("UAH", "XUA", "542", true);
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second unsupported, wrong amount
      withWrongAmount("Gel", "ITL", "0.11,");
      assertCommand(START, START_MESSAGE);

      withWrongAmount("Cny", "Che", "-98");
      assertCommand(STOP, STOP_MESSAGE);

      withWrongAmount("Xcd", "XXX", "4$");
      oneLineRequest("100 eur to eur");

      withWrongAmount("USD", "SRG", "87$");
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second wrong
      wrongSecondCurrency("Inr", "rubli");
      assertCommand(START, START_MESSAGE);

      wrongSecondCurrency("Bgn", "udd");
      assertCommand(STOP, STOP_MESSAGE);

      wrongSecondCurrency("Eur", "78");
      oneLineRequest("54.8 Inr in usd");

      wrongSecondCurrency("Uah", "usd$");
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second correct, right amount
      withUnsupportedCurrency("xts", "dop", "301", false);
      assertCommand(START, START_MESSAGE);

      withUnsupportedCurrency("SRG", "UYU", "7", false);
      assertCommand(STOP, STOP_MESSAGE);

      withUnsupportedCurrency("Rur", "Rub", "122", false);
      oneLineRequest("2000 lsl in zmw");

      withUnsupportedCurrency("zWR", "INR", "800", false);
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second correct, wrong amount
      withWrongAmount("cou", "ern", "-17");
      assertCommand(START, START_MESSAGE);

      withWrongAmount("esp", "lvl", "a");
      assertCommand(STOP, STOP_MESSAGE);

      withWrongAmount("Bef", "Uah", "2+1");
      oneLineRequest("118 eur to php");

      withWrongAmount("zWR", "INR", "111..");
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);


      //both currencies unsupported, right amount
      withUnsupportedCurrency("xts", "csd", "804", false);
      assertCommand(START, START_MESSAGE);

      withUnsupportedCurrency("SRG", "aym", "90.7", false);
      assertCommand(STOP, STOP_MESSAGE);

      withUnsupportedCurrency("Xua", "Xbb", "500.6", false);
      oneLineRequest("1 rub in pln");

      withUnsupportedCurrency("zWR", "Iep", "505,5", false);
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);


      //both currencies unsupported, wrong amount
      withWrongAmount("mtl", "USS", "-10000");
      assertCommand(START, START_MESSAGE);

      withWrongAmount("rol", "RUr", "12 6");
      assertCommand(STOP, STOP_MESSAGE);

      withWrongAmount("Gwp", "Che", "211 219.");
      oneLineRequest("9,9 lkr to mzn");

      withWrongAmount("sKK", "Xbb", "08,,1");
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second wrong, right amount
      wrongSecondCurrency("fim", "affa");
      assertCommand(START, START_MESSAGE);

      wrongSecondCurrency("pte", "/help");
      assertCommand(STOP, STOP_MESSAGE);

      wrongSecondCurrency("veb", "dollars");
      oneLineRequest("562 bob in pab");

      wrongSecondCurrency("zwn", "convert");
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency wrong
      wrongFirstCurrency("starting");
      assertCommand(START, START_MESSAGE);

      wrongFirstCurrency("pounds");
      assertCommand(STOP, STOP_MESSAGE);

      wrongFirstCurrency("16$");
      oneLineRequest("78921 pln to lkr");

      wrongFirstCurrency("25¥");
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);

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
      commandAndWord(START, START_MESSAGE, "10 euro convert to rub");

      twoCommands(STOP, STOP_MESSAGE, START, START_MESSAGE);
      twoCommands(STOP, STOP_MESSAGE, STOP, STOP_MESSAGE);
      twoCommands(STOP, STOP_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);

      commandAndWord(STOP, STOP_MESSAGE, "start");
      commandAndWord(STOP, STOP_MESSAGE, "stop");
      commandAndWord(STOP, STOP_MESSAGE, "convert");
      commandAndWord(STOP, STOP_MESSAGE, "hello");
      commandAndWord(STOP, STOP_MESSAGE, "500 dollars convert to euro");

      wordAndCommand("start", START, START_MESSAGE);
      wordAndCommand("hello", STOP, STOP_MESSAGE);

      twoCommands(CONVERT, FIRST_CONVERT_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);
      twoCommands(CONVERT, FIRST_CONVERT_MESSAGE, START, START_MESSAGE);
      twoCommands(CONVERT, FIRST_CONVERT_MESSAGE, STOP, STOP_MESSAGE);
      //if after '/convert' we type not command word, then we go to next conversion step. See wrongCurrencyTest

      //if line conversion before another command
      oneLineRequestAndCommand("18 usd to pln", START, START_MESSAGE);
      oneLineRequestAndCommand("400 IRR in EUR", CONVERT, FIRST_CONVERT_MESSAGE);
      oneLineRequestAndCommand("12,3 jpy to irr", STOP, STOP_MESSAGE);
      oneLineRequestAndCommand("65.5 rub in php", "thanks", INCORRECT_REQUEST_MESSAGE);
      oneLineRequestAndCommand("511 brl to xcd", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      incorrectOneLineRequestAndCommand("-367 tzs in inr", SORRY_BUT + "-367" + IS_NOT_A_VALID_NUMBER
              + CONVERT_MESSAGE, START, START_MESSAGE);
      incorrectOneLineRequestAndCommand("hundred rub to xxx", SORRY_BUT + "hundred" + IS_NOT_A_VALID_NUMBER
              + CONVERT_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);
      incorrectOneLineRequestAndCommand("13$ usd in rur", SORRY_BUT + "13$" + IS_NOT_A_VALID_NUMBER
              + CONVERT_MESSAGE, STOP, STOP_MESSAGE);
      incorrectOneLineRequestAndCommand("15..7 irr to php", SORRY_BUT + "15..7" + IS_NOT_A_VALID_NUMBER
              + CONVERT_MESSAGE, "hello", INCORRECT_REQUEST_MESSAGE);
      incorrectOneLineRequestAndCommand("0.01. xof in xfo", SORRY_BUT + "0.01." + IS_NOT_A_VALID_NUMBER
              + CONVERT_MESSAGE, WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      incorrectOneLineRequestAndCommand("44 xbb to xcd", "❗Sorry. Currency not supported: XBB\n" + CONVERT_MESSAGE,
              START, START_MESSAGE);
      incorrectOneLineRequestAndCommand("105.7 usd in uss", "❗Sorry. Currency not supported: USS\n" + CONVERT_MESSAGE,
              CONVERT, FIRST_CONVERT_MESSAGE);
      incorrectOneLineRequestAndCommand("58721 uah to gwp", "❗Sorry. Currency not supported: GWP\n" + CONVERT_MESSAGE,
              STOP, STOP_MESSAGE);
      incorrectOneLineRequestAndCommand("65,3 iep in usn", "❗Sorry. Currency not supported: IEP\n" + CONVERT_MESSAGE,
              "convert", INCORRECT_REQUEST_MESSAGE);
      incorrectOneLineRequestAndCommand("711 jpy to xxx", "❗Sorry. Currency not supported: XXX\n" + CONVERT_MESSAGE,
              WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      incorrectOneLineRequestAndCommand("16 flowers in uah", SORRY_BUT + "FLOWERS" +
              IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE, START, START_MESSAGE);
      incorrectOneLineRequestAndCommand("101.1 rub to dollars", SORRY_BUT + "DOLLARS" +
              IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);
      incorrectOneLineRequestAndCommand("7,7 letter in book", SORRY_BUT + "LETTER" +
              IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE, STOP, STOP_MESSAGE);
      incorrectOneLineRequestAndCommand("568 kitten to afn", SORRY_BUT + "KITTEN" +
              IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE, "hi", INCORRECT_REQUEST_MESSAGE);
      incorrectOneLineRequestAndCommand("9,9 rain in cloud", SORRY_BUT + "RAIN" +
              IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE, WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      incorrectOneLineRequestAndCommand("12 uah convertTo usd", INCORRECT_REQUEST_MESSAGE, START, START_MESSAGE);
      incorrectOneLineRequestAndCommand("-213 irr from uer", INCORRECT_REQUEST_MESSAGE, STOP, STOP_MESSAGE);
      incorrectOneLineRequestAndCommand("0.001 likes on profile", INCORRECT_REQUEST_MESSAGE, "moon", INCORRECT_REQUEST_MESSAGE);
      incorrectOneLineRequestAndCommand("78,9 bob of inr", INCORRECT_REQUEST_MESSAGE, WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      incorrectOneLineRequestAndCommand("100 php of java", INCORRECT_REQUEST_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);


      //if line conversion after another command
      commandAndOneLineRequest(START, START_MESSAGE, "8,8 INR to BYN");
      commandAndOneLineRequest(STOP, STOP_MESSAGE, "32 usd in uah");
      commandAndOneLineRequest("say something", INCORRECT_REQUEST_MESSAGE, "80.6 pln in inr");
      commandAndOneLineRequest(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE, "140.01 rub to rub");

      commandAndIncorrectOneLineRequest(START, START_MESSAGE, "-678 afn in usd", SORRY_BUT +
              "-678" + IS_NOT_A_VALID_NUMBER + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(STOP, STOP_MESSAGE, "177.. xxx to xxx", SORRY_BUT +
              "177.." + IS_NOT_A_VALID_NUMBER + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(CONVERT, FIRST_CONVERT_MESSAGE, "one uss in lkr", SORRY_BUT +
              "one" + IS_NOT_A_VALID_NUMBER + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest("conversion", INCORRECT_REQUEST_MESSAGE, "67h uah to omr",
              SORRY_BUT + "67h" + IS_NOT_A_VALID_NUMBER + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE, "0,.1 brl in pln",
              SORRY_BUT + "0,.1" + IS_NOT_A_VALID_NUMBER + CONVERT_MESSAGE);

      commandAndIncorrectOneLineRequest(START, START_MESSAGE, "37 Xua to Clp", "❗Sorry. Currency not supported: XUA\n"
              + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(STOP, STOP_MESSAGE, "32,3 PLN in XBC", "❗Sorry. Currency not supported: XBC\n"
              + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(CONVERT, FIRST_CONVERT_MESSAGE, "3000 Gbp to Xbb", "❗Sorry. Currency not supported: XBB\n"
              + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest("lucky", INCORRECT_REQUEST_MESSAGE, "209.9 rub in XFO",
              "❗Sorry. Currency not supported: XFO\n" + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE, "459,2 uah to uss",
              "❗Sorry. Currency not supported: USS\n" + CONVERT_MESSAGE);

      commandAndIncorrectOneLineRequest(START, START_MESSAGE, "56 ladies to eur", SORRY_BUT +
              "LADIES" + IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(STOP, STOP_MESSAGE, "18 computers in office", SORRY_BUT +
              "COMPUTERS" + IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(CONVERT, FIRST_CONVERT_MESSAGE, "21 uah to euro",
              SORRY_BUT + "EURO" + IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest("try", INCORRECT_REQUEST_MESSAGE, "888 flats in money",
              SORRY_BUT + "FLATS" + IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE, "4,3 rows to text",
              SORRY_BUT + "ROWS" + IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE);

      commandAndIncorrectOneLineRequest(START, START_MESSAGE, "16 rup fo 13 ua", INCORRECT_REQUEST_MESSAGE);
      commandAndIncorrectOneLineRequest(STOP, STOP_MESSAGE, "68721 mro convert in uah", INCORRECT_REQUEST_MESSAGE);
      commandAndIncorrectOneLineRequest("sorry", INCORRECT_REQUEST_MESSAGE, "777 cats on the table",
              INCORRECT_REQUEST_MESSAGE);
      commandAndIncorrectOneLineRequest(CONVERT, FIRST_CONVERT_MESSAGE, "21.2. second of hour",
              SORRY_BUT + "21.2. second of hour" + IS_NOT_A_VALID_CURRENCY + FIRST_CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE, "5 pln inn uah",
              INCORRECT_REQUEST_MESSAGE);
   }

   /**
    * Tests, that bot's reaction for few /convert command is equal to expected
    */
   @Test
   void fewConvertCommand() {
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      rightScript("usd", "uah", "10");


      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      rightScript("rub", "EUR", "4");
   }

   /**
    * Tests, that bot reaction for interruption conversion on second step by some command is correct
    */
   @Test
   void interruptConvertOnSecondStepTest() {
      //correct currency
      interruptConvertOnSecondStep("uah", START, START_MESSAGE);
      interruptConvertOnSecondStep("Rub", STOP, STOP_MESSAGE);
      interruptConvertOnSecondStep("EUR", CONVERT, FIRST_CONVERT_MESSAGE);

      //unsupported currency
      interruptConvertOnSecondStep("tmm", START, START_MESSAGE);
      interruptConvertOnSecondStep("zwd", STOP, STOP_MESSAGE);
      interruptConvertOnSecondStep("xua", CONVERT, FIRST_CONVERT_MESSAGE);

      //interrupt by one line request
      interruptConvertOnSecondStepByOneLine("pln", "10 BGN in EUR");
      interruptConvertOnSecondStepByOneLine("XXX", "10 INR in PHP");
      interruptConvertOnSecondStepByOneLine("Cop", "10 Uah in Cad");

   }

   /**
    * Tests, that bot reaction for interruption conversion on third step by some command is correct
    */
   @Test
   void interruptConvertOnThirdStepTest() {
      //it is possible, if both currency is valid
      //both correct currency
      interruptConvertOnThirdStep("Pln", "Eur", START, START_MESSAGE);
      interruptConvertOnThirdStep("BgN", "TRy", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("All", "PHP", CONVERT, FIRST_CONVERT_MESSAGE);
      interruptConvertOnThirdStep("PKR", "AUD", "10 CLP to CLP", "\uD83D\uDCB010 CLP is 10 CLP");

      //first correct, second unsupported
      interruptConvertOnThirdStep("Cad", "iep", START, START_MESSAGE);
      interruptConvertOnThirdStep("CZk", "xbb", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("IQD", "NLG", CONVERT, FIRST_CONVERT_MESSAGE);
      interruptConvertOnThirdStep("HKD", "RUR", "0 nok in huf", "\uD83D\uDCB00 NOK is 0 HUF");

      //first unsupported, second correct
      interruptConvertOnThirdStep("Mtl", "bif", START, START_MESSAGE);
      interruptConvertOnThirdStep("Xbb", "cop", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("Mgf", "Ars", CONVERT, FIRST_CONVERT_MESSAGE);
      interruptConvertOnThirdStep("XBC", "KPW", "3,4 TRY to TRY", "\uD83D\uDCB03,4 TRY is 3.4 TRY");

      //both unsupported
      interruptConvertOnThirdStep("Usn", "byb", START, START_MESSAGE);
      interruptConvertOnThirdStep("Fim", "Trl", STOP, STOP_MESSAGE);
      interruptConvertOnThirdStep("CHW", "USS", CONVERT, FIRST_CONVERT_MESSAGE);
      interruptConvertOnThirdStep("Xbb", "Rol", "0 Sek in Jpy", "\uD83D\uDCB00 SEK is 0 JPY");

   }

   /**
    * Tests, that bot's response to incorrect content is correct, no matter what time it was sent
    */
   @Test
   void unreadableContent() {
      //wrong content
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after command
      assertCommand(START, START_MESSAGE);
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      assertCommand(STOP, STOP_MESSAGE);
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after second conversion step
      interruptConvertOnSecondStep("usd", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnSecondStep("xxx", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after third conversion step
      interruptConvertOnThirdStep("pln", "uah", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("cad", "uss", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      interruptConvertOnThirdStep("sit", "eur", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("trl", "xbb", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);


      //wrong content after conversion
      rightScript("EUR", "Usd", "30.6");
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("Myr", "KhR", "-67");
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withUnsupportedCurrency("PLN", "ITL", "0.1", true);
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("Gel", "ITL", "0.11,");
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      wrongSecondCurrency("uah", "rubli");
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withUnsupportedCurrency("xts", "DOP", "301", false);
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("cou", "ern", "-17");
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withUnsupportedCurrency("xts", "csd", "804", false);
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("mtl", "USS", "-10000");
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      wrongSecondCurrency("xxx", "val");
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      wrongFirstCurrency("russian");
      assertCommand(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

   }


   /**
    * Tests, that one line request works correctly
    */
   @Test
   void oneLineRequestWithMistakesTest() {
      //wrong amount
      oneLineRequestWithWrongAmount("-7 Dkk to Jpy");
      oneLineRequestWithWrongAmount("13p bob in mzn");

      //second currency is unsupported
      oneLineRequestWithWrongCurrency("17,9 byr to xxx", true, true);
      oneLineRequestWithWrongCurrency("10000 pln in adp", true, true);

      //second currency is unsupported and wrong amount
      oneLineRequestWithWrongAmount("-125 eur to fim");
      oneLineRequestWithWrongAmount("one xcd in che");

      //second currency is wrong
      oneLineRequestWithWrongCurrency("16.5 pln to dollars", true, false);
      oneLineRequestWithWrongCurrency("3 uah in apple", true, false);

      //second currency and amount are wrong
      oneLineRequestWithWrongCurrency("-82 mro to credit", true, false);
      oneLineRequestWithWrongCurrency("a lkr in me", true, false);

      //wrong word between currencies
      assertCommand("17 php from eur", INCORRECT_REQUEST_MESSAGE);

      //wrong word between currencies and wrong amount
      assertCommand("2.3. txr is xcd", INCORRECT_REQUEST_MESSAGE);

      //wrong word between currencies and unsupported second currency
      assertCommand("18 jpy 1 php", INCORRECT_REQUEST_MESSAGE);

      //wrong word between currencies, unsupported second currency and wrong amount
      assertCommand("0..0 tzs ta luf", INCORRECT_REQUEST_MESSAGE);

      //wrong word between currencies and wrong second currency
      assertCommand("1 eur for cat", INCORRECT_REQUEST_MESSAGE);

      //word between currencies, second currency and amount are wrong
      assertCommand("m. zmw tto you", INCORRECT_REQUEST_MESSAGE);

      //first currency is unsupported
      oneLineRequestWithWrongCurrency("34 XBB to RUB", false, true);
      oneLineRequestWithWrongCurrency("52.9 Xfo in Usd", false, true);

      //first currency is unsupported and wrong amount
      oneLineRequestWithWrongAmount("seven IEP to INR");
      oneLineRequestWithWrongAmount("-16 uss in zmw");

      //both currencies are unsupported
      oneLineRequestWithWrongCurrency("0,5 xua to rur", false, true);
      oneLineRequestWithWrongCurrency("2 adp in luf", false, true);

      //both currencies are unsupported and wrong amount
      oneLineRequestWithWrongAmount("37m gwp to uss");
      oneLineRequestWithWrongAmount("2$ usn in xbb");

      //first currency is unsupported, second is wrong
      oneLineRequestWithWrongCurrency("64 rur to $", true, false);
      oneLineRequestWithWrongCurrency("7.0 xfo in xxfo", true, false);

      //first currency is unsupported, second is wrong and wrong amount
      oneLineRequestWithWrongCurrency("78.. IEP to PLAN", true, false);
      oneLineRequestWithWrongCurrency("56..0. CHE in Ukraine", true, false);

      //first currency is unsupported, wrong word between currencies
      assertCommand("21,1 PTE but usd", INCORRECT_REQUEST_MESSAGE);

      //first currency is unsupported, wrong word between currencies, wrong amount
      assertCommand("-300 uss inn rub", INCORRECT_REQUEST_MESSAGE);

      //both currencies are unsupported, wrong word between currencies
      assertCommand("54 Rur are Xua", INCORRECT_REQUEST_MESSAGE);

      //both currencies are unsupported, wrong word between currencies, wrong amount
      assertCommand("from xbb 77 iep", INCORRECT_REQUEST_MESSAGE);

      //first currency is unsupported, wrong word between currencies, wrong second currency
      assertCommand("87.7 Xxx s currency", INCORRECT_REQUEST_MESSAGE);

      //first currency is unsupported, wrong word between currencies, wrong second currency, wrong amount
      assertCommand("-15..5 Che cv convert", INCORRECT_REQUEST_MESSAGE);

      //first currency is wrong
      oneLineRequestWithWrongCurrency("16 bird to RUB", false, false);
      oneLineRequestWithWrongCurrency("90000,6 man in Usd", false, false);

      //first currency is wrong and wrong amount
      oneLineRequestWithWrongCurrency("two people to PLN", false, false);
      oneLineRequestWithWrongCurrency("-2 ticket in Mro", false, false);

      //first currency is wrong, second is unsupported
      oneLineRequestWithWrongCurrency("64 rur to $", true, false);
      oneLineRequestWithWrongCurrency("7.0 xfo in xxfo", true, false);

      //first currency is wrong, second is unsupported and wrong amount
      oneLineRequestWithWrongCurrency("78.. IEP to PLAN", true, false);
      oneLineRequestWithWrongCurrency("56..0. CHE in Ukraine", true, false);

      //both currencies are wrong
      oneLineRequestWithWrongCurrency("107 butterfly to flower", false, false);
      oneLineRequestWithWrongCurrency("66 leaf in tree", false, false);

      //both currencies are wrong and wrong amount
      oneLineRequestWithWrongCurrency("4.. bear to wolf", false, false);
      oneLineRequestWithWrongCurrency("89- thing in money", false, false);

      //first currency is wrong, wrong word between currencies
      assertCommand("43 parrots into lkr", INCORRECT_REQUEST_MESSAGE);

      //first currency is wrong, wrong word between currencies, wrong amount
      assertCommand("03.03.12 sunny and usd", INCORRECT_REQUEST_MESSAGE);

      //first currency is wrong, second is unsupported, wrong word between currencies
      assertCommand("73,9 pieces with xxx", INCORRECT_REQUEST_MESSAGE);

      //first currency is wrong, second is unsupported , wrong word between currencies, wrong amount
      assertCommand("-12 kilos of pte", INCORRECT_REQUEST_MESSAGE);

      //both currencies are wrong, wrong word between currencies
      assertCommand("823.75 percent of 45", INCORRECT_REQUEST_MESSAGE);

      //all is wrong
      assertCommand("one letter for you", INCORRECT_REQUEST_MESSAGE);

   }


   @Test
   void wrongFirstCurrencyTest() {
      wrongFirstCurrency("gryvna");
      wrongFirstCurrency("dollars");
      wrongFirstCurrency("hello");
      wrongFirstCurrency("u s d");
      wrongFirstCurrency("plm");
   }


   @Test
   void wrongSecondCurrencyTest() {
      wrongSecondCurrency("uah", "why");
      wrongSecondCurrency("Rub", "dollars");
      wrongSecondCurrency("XBC", "u sd");
      wrongSecondCurrency("Rur", "euro");
      wrongSecondCurrency("Usd", "sdu");
   }


   /**
    * Tests, that if both currencies are correct and amount is wrong, that bot's response is equal to expected
    */
   @Test
   void wrongAmountTest() {
      withWrongAmount("xof", "hnl", "-2");
      withWrongAmount("Lak", "Awg", "-9,7");
      withWrongAmount("gEl", "aLl", "-18.6");
      withWrongAmount("zaR", "jmD", "16f");
      withWrongAmount("BAm", "SZl", "14u");
      withWrongAmount("ItL", "GnF", "1,1,1");
      withWrongAmount("sYP", "mKD", "4.3.4");
      withWrongAmount("BZD", "KWD", "apple");
      withWrongAmount(" Sll ", "Etb", "2+2");
      withWrongAmount("AZN ", "XPF", "p0q");
   }


   /**
    * Tests, that if one or two currencies are unsupported, that bot's response is equal to expected for wrong and right amount
    */
   @Test
   void unsupportedCurrency() {
      //first currency is unsupported, right amount
      withUnsupportedCurrency("mzm", "rub", "1000", false);
      withUnsupportedCurrency("Xpt", "Usd", "2.5", false);

      //first currency is unsupported, wrong amount
      withWrongAmount("rol", "rub", "-34543");
      withWrongAmount("Ghc", "Usd", "511a");


      //second currency is unsupported, right amount
      withUnsupportedCurrency("rub", "skk", "7,6", true);
      withUnsupportedCurrency("Usd", "Xbc", "762", true);

      //second currency is unsupported, wrong amount
      withWrongAmount("rub", "veb", "one hundred");
      withWrongAmount("Usd", "Aym", "-4.99q");


      //both currencies is unsupported, right amount
      withUnsupportedCurrency("Xbd", "Bef", "107.5", false);
      withUnsupportedCurrency("CYP", "XXX", "20000", false);

      //both currencies is unsupported, wrong amount
      withWrongAmount("Xsu", "Dem", "9*9");
      withWrongAmount("Xfo", "XBA", "10+45");
   }


   /**
    * Checks, that bot's reaction for interruption conversion on third step by some command is equal to expected
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    * @param command        command after conversion
    * @param response       expected bot's response for command
    */
   private void interruptConvertOnThirdStep(String firstCurrency, String secondCurrency,
                                            String command, String response) {
      script(firstCurrency, secondCurrency);
      assertCommand(command, response);
   }

   /**
    * Checks, that bot's reaction for interruption conversion on second step by one line command is equal to expected
    *
    * @param firstCurrency the currency to convert from
    * @param request       correct one line request
    */
   private void interruptConvertOnSecondStepByOneLine(String firstCurrency, String request) {
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(firstCurrency, SECOND_CONVERT_MESSAGE_1 + firstCurrency.toUpperCase().trim() + SECOND_CONVERT_MESSAGE_2);
      oneLineRequest(request);
   }

   /**
    * Checks, that bot's reaction for interruption conversion on second step by some command is equal to expected
    *
    * @param firstCurrency the currency to convert from
    * @param command       command after conversion
    * @param response      expected bot's response for command
    */
   private void interruptConvertOnSecondStep(String firstCurrency, String command, String response) {
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(firstCurrency, SECOND_CONVERT_MESSAGE_1 + firstCurrency.trim().toUpperCase() + SECOND_CONVERT_MESSAGE_2);
      assertCommand(command, response);
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
      assertCommand(command, response);
   }

   /**
    * Checks, that bot's reaction entering command and then some word is equal to expected
    *
    * @param command  command for bot
    * @param response expected bot's response for command
    * @param word     some word, which is not a command and gotten after command
    */
   private void commandAndWord(String command, String response, String word) {
      assertCommand(command, response);
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
      assertCommand(firstCommand, firstResponse);
      assertCommand(secondCommand, secondResponse);
   }

   /**
    * Checks, that bot's reaction in conversion for unsupported currencies is equal to expected
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    * @param amount         amount of first currency
    * @param firstIsCorrect true, if first currency is right and supported, false otherwise
    */
   private void withUnsupportedCurrency(String firstCurrency, String secondCurrency,
                                        String amount, boolean firstIsCorrect) {
      String startOfMessage = "❗Sorry. Currency not supported: ";
      String wrongCurrency;

      if (firstIsCorrect) {
         wrongCurrency = secondCurrency;
      } else {
         wrongCurrency = firstCurrency;
      }

      String message = startOfMessage + wrongCurrency.trim().toUpperCase() + "\n" + CONVERT_MESSAGE;

      rightScriptWithWrongValue(firstCurrency, secondCurrency, amount, message);
   }

   /**
    * Checks, that bot's reaction in conversion for wrong amount is equal to expected
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    * @param amount         amount of first currency
    */
   private void withWrongAmount(String firstCurrency, String secondCurrency, String amount) {
      String message = SORRY_BUT + amount + IS_NOT_A_VALID_NUMBER +
              THIRD_CONVERT_MESSAGE + firstCurrency.trim().toUpperCase() + " to " + secondCurrency.trim().toUpperCase();

      rightScriptWithWrongValue(firstCurrency, secondCurrency, amount, message);
   }

   /**
    * Checks, that bot reaction for conversion, which contains wrong value is equal to expected
    *
    * @param firstCurrency the currency to convert from
    * @param amount        amount of first currency
    * @param message       expected bot's response for given amount
    */
   private void rightScriptWithWrongValue(String firstCurrency, String secondCurrency, String amount, String message) {
      script(firstCurrency, secondCurrency);
      assertCommand(amount, message);
   }

   /**
    * Checks, that bot reaction for consecutive /convert command and last result are equal to expected
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    * @param amount         amount of first currency
    */
   private void rightScript(String firstCurrency, String secondCurrency, String amount) {
      script(firstCurrency, secondCurrency);
      checkResult(amount, firstCurrency, secondCurrency);
   }

   /**
    * Checks, that bot's responses on conversion steps are equal to expected
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    */
   private void script(String firstCurrency, String secondCurrency) {
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(firstCurrency, SECOND_CONVERT_MESSAGE_1 + firstCurrency.trim().toUpperCase() + SECOND_CONVERT_MESSAGE_2);
      assertCommand(secondCurrency, THIRD_CONVERT_MESSAGE + firstCurrency.trim().toUpperCase() + " to "
              + secondCurrency.trim().toUpperCase());
   }

   /**
    * Checks, that bot's reaction for non-command word is equal to expected
    *
    * @param message some word, which is not a command
    */
   private void enterOtherWords(String message) {
      assertCommand(message, INCORRECT_REQUEST_MESSAGE);
   }

   /**
    * One line request with wrong currency
    *
    * @param request        currency request
    * @param firstIsCorrect true, if first is correct
    * @param isUnsupported  true, if we check unsupported conversion
    */
   private void oneLineRequestWithWrongCurrency(String request, boolean firstIsCorrect, boolean isUnsupported) {
      String[] words = request.split("\\s+");

      String wrongCurrency = words[1];

      if (firstIsCorrect) {
         wrongCurrency = words[3];
      }

      String message = "❗Sorry. Currency not supported: " + wrongCurrency.toUpperCase() + "\n" + CONVERT_MESSAGE;

      if (!isUnsupported) {
         message = SORRY_BUT + wrongCurrency.toUpperCase() + IS_NOT_A_VALID_CURRENCY + CONVERT_MESSAGE;
      }

      assertCommand(request, message);
   }

   /**
    * Tests that after incorrect one line request, next command works correctly
    *
    * @param request         currency request
    * @param response        response to incorrect request
    * @param command         next command
    * @param commandResponse response to command
    */
   private void incorrectOneLineRequestAndCommand(String request, String response, String command, String commandResponse) {
      assertCommand(request, response);
      assertCommand(command, commandResponse);
   }

   /**
    * Tests that after some command, incorrect one line request gets correct response. If command is /convert, then
    * tests that we get next step of conversion message
    *
    * @param command         first command
    * @param response        response to command
    * @param request         currency request
    * @param requestResponse response to one line request
    */
   private void commandAndIncorrectOneLineRequest(String command, String response, String request, String requestResponse) {
      assertCommand(command, response);
      assertCommand(request, requestResponse);

   }

   /**
    * Tests that after some command, one line request works correctly, if command is not /convert. In this case tests, that
    * we get next step of conversion message
    *
    * @param command  first command
    * @param response response to command
    * @param request  currency request
    */
   private void commandAndOneLineRequest(String command, String response, String request) {
      assertCommand(command, response);

      if (!command.equals(CONVERT)) {
         oneLineRequest(request);

      } else {
         assertCommand(request, SECOND_CONVERT_MESSAGE_1 + request.replaceAll(" ", "").toUpperCase()
                 + SECOND_CONVERT_MESSAGE_2);

         testBotService.processCommand(STOP, testUser);
      }
   }

   /**
    * Tests that after right one line request, next command works correctly
    *
    * @param request  currency request
    * @param command  next command to bot
    * @param response bot's response to user
    */
   private void oneLineRequestAndCommand(String request, String command, String response) {
      oneLineRequest(request);
      assertCommand(command, response);
   }

   /**
    * One line request with wrong amount
    *
    * @param request currency request
    */
   private void oneLineRequestWithWrongAmount(String request) {
      String[] words = request.split("\\s+");
      String amount = words[0];
      assertCommand(request, SORRY_BUT + amount + IS_NOT_A_VALID_NUMBER + CONVERT_MESSAGE);
   }

   /**
    * Right one line request to bot
    *
    * @param request currency request
    */
   private void oneLineRequest(String request) {
      String botsResponse = testBotService.processCommand(request, testUser);

      String[] words = request.split("\\s+");
      String start = MONEY_SIGN + words[0] + " " + words[1].toUpperCase() + " is ";

      assertTrue(botsResponse.startsWith(start));
      assertTrue(botsResponse.endsWith(words[3].toUpperCase()));

      String value = botsResponse.replace(start, "").replace(" " + words[3].toUpperCase(), "");
      Matcher matcher = isPositiveNumber.matcher(value);

      assertTrue(matcher.matches());

   }

   /**
    * Checks, that bot's response for user's request is correct
    *
    * @param usersMessage user's request to bot
    * @param botsResponse expected bot's response to user
    */
   private void assertCommand(String usersMessage, String botsResponse) {
      assertEquals(botsResponse, testBotService.processCommand(usersMessage, testUser));
   }

   /**
    * Checks, that on last step bot's response contains positive value
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    * @param amount         amount of first currency
    */
   private void checkResult(String amount, String firstCurrency, String secondCurrency) {
      firstCurrency = firstCurrency.trim().toUpperCase();
      secondCurrency = secondCurrency.trim().toUpperCase();

      String start = MONEY_SIGN + amount + " " + firstCurrency + " is ";

      String botsResponse = testBotService.processCommand(amount, testUser);

      assertTrue(botsResponse.startsWith(start));
      assertTrue(botsResponse.endsWith(secondCurrency));

      String value = botsResponse.replace(start, "").replace(" " + secondCurrency, "");
      Matcher matcher = isPositiveNumber.matcher(value);

      assertTrue(matcher.matches());
   }


   /**
    * Checks, that if first currency is incorrect, that we come back to previous step
    *
    * @param firstCurrency the currency to convert from
    */
   private void wrongFirstCurrency(String firstCurrency) {
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(firstCurrency, SORRY_BUT + firstCurrency + IS_NOT_A_VALID_CURRENCY + FIRST_CONVERT_MESSAGE);
   }

   /**
    * Checks, that if second currency is incorrect, that we come back to previous step
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    */
   private void wrongSecondCurrency(String firstCurrency, String secondCurrency) {
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);

      String message = SECOND_CONVERT_MESSAGE_1 + firstCurrency.trim().toUpperCase() + SECOND_CONVERT_MESSAGE_2;

      assertCommand(firstCurrency, message);
      assertCommand(secondCurrency, SORRY_BUT + secondCurrency + IS_NOT_A_VALID_CURRENCY + message);
   }


   /**
    * Checks, that if we convert currency to itself, that we get the same amount
    *
    * @param currency currency, which has to be checked
    */
   private void rightScriptWithIdenticalCurrency(String currency) {
      BigDecimal number = BigDecimal.valueOf(RANDOM.nextFloat() * 1000);
      String amount = number.toPlainString();

      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setDecimalSeparator('.');
      DECIMAL_FORMATTER.setDecimalFormatSymbols(symbols);
      DECIMAL_FORMATTER.setRoundingMode(RoundingMode.HALF_UP);
      String result = DECIMAL_FORMATTER.format(number);

      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(currency, SECOND_CONVERT_MESSAGE_1 + currency + SECOND_CONVERT_MESSAGE_2);
      assertCommand(currency, THIRD_CONVERT_MESSAGE + currency + " to " + currency);
      assertCommand(amount, MONEY_SIGN + amount + " " + currency + " is " + result + " " + currency);
   }

   /**
    * Checks, that if amount is zero, result of conversion would be zero
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    */
   private void zeroAmount(String firstCurrency, String secondCurrency) {
      assertCommand(CONVERT, FIRST_CONVERT_MESSAGE);
      assertCommand(firstCurrency, SECOND_CONVERT_MESSAGE_1 + firstCurrency + SECOND_CONVERT_MESSAGE_2);
      assertCommand(secondCurrency, THIRD_CONVERT_MESSAGE + firstCurrency + " to " + secondCurrency);
      assertCommand("0", MONEY_SIGN + "0 " + firstCurrency + " is 0 " + secondCurrency);
   }

   /**
    * Checks, that if we convert currency to itself, that we get the same amount
    *
    * @param currency currency, which has to be checked
    */
   private void oneLineRequestWithIdenticalCurrency(String currency) {
      String[] words = {" in ", " to "};
      int n = RANDOM.nextInt(2);

      BigDecimal number = BigDecimal.valueOf(RANDOM.nextFloat() * 1000);
      String amount = number.toPlainString();

      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setDecimalSeparator('.');
      DECIMAL_FORMATTER.setDecimalFormatSymbols(symbols);
      DECIMAL_FORMATTER.setRoundingMode(RoundingMode.HALF_UP);
      String result = DECIMAL_FORMATTER.format(number);

      String request = amount + " " + currency + words[n] + currency;

      assertCommand(request, MONEY_SIGN + amount + " " + currency + " is " + result + " " + currency);
   }

   /**
    * Checks, that if amount is zero, result of conversion would be zero
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    */
   private void zeroAmountOneLine(String firstCurrency, String secondCurrency) {
      String[] words = {" in ", " to "};
      int n = RANDOM.nextInt(2);

      String request = "0 " + firstCurrency + words[n] + secondCurrency;

      assertCommand(request, MONEY_SIGN + "0 " + firstCurrency + " is " + "0 " + secondCurrency);
   }

   /**
    * Checks, that result of conversion lies between given borders. Amount is equal 1.
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    * @param amount         given amount to convert
    * @param leftBorder     left border of the interval
    * @param rightBorder    right border of the interval
    */
   private void checkStableCurrencyWithAmount(String firstCurrency, String secondCurrency, String amount, String leftBorder, String rightBorder) {
      String value = getValue(testBotService.processCommand(amount + " " + firstCurrency + " to " + secondCurrency, testUser));
      checkIntervalWithValues(amount, value, leftBorder, rightBorder);
   }

   /**
    * Checks, that result of conversion lies between given borders. Amount is equal 1.
    *
    * @param firstCurrency  the currency to convert from
    * @param secondCurrency the currency to convert to
    * @param leftBorder     left border of the interval
    * @param rightBorder    right border of the interval
    */
   private void checkStableCurrency(String firstCurrency, String secondCurrency, String leftBorder, String rightBorder) {
      String value = getValue(testBotService.processCommand("1 " + firstCurrency + " to " + secondCurrency, testUser));
      checkInterval(value, leftBorder, rightBorder);
   }

   /**
    * Gets amount of currency after conversion
    *
    * @param result result of conversion
    */
   private String getValue(String result) {
      return result.split("\\s+")[3];
   }

   /**
    * Checks, that given value lies between left border and right border
    *
    * @param value       given number
    * @param leftBorder  left border of the interval
    * @param rightBorder right border of the interval
    */
   private void checkIntervalWithValues(String amount, String value, String leftBorder, String rightBorder) {

      BigDecimal v = new BigDecimal(value);
      BigDecimal a = new BigDecimal(amount);
      BigDecimal left = new BigDecimal(leftBorder).multiply(a);
      BigDecimal right = new BigDecimal(rightBorder).multiply(a);

      assertTrue(v.compareTo(left) >= 0 && v.compareTo(right) <= 0);

   }


   /**
    * Checks, that given value lies between left border and right border
    *
    * @param value       given number
    * @param leftBorder  left border of the interval
    * @param rightBorder right border of the interval
    */
   private void checkInterval(String value, String leftBorder, String rightBorder) {

      BigDecimal v = new BigDecimal(value);
      BigDecimal left = new BigDecimal(leftBorder);
      BigDecimal right = new BigDecimal(rightBorder);

      assertTrue(v.compareTo(left) >= 0 && v.compareTo(right) <= 0);
   }

   /**
    * Checks, that rounded amount of given currency is equal to expected
    *
    * @param currency self-convertible currency
    *                 "
    */
   private void checkRounding(String currency, String value, String roundingValue) {
      currency = currency.trim().toUpperCase();
      String request = value + " " + currency + " in " + currency;
      String response = MONEY_SIGN + value + " " + currency + " is " + roundingValue + " " + currency;
      assertCommand(request, response);
   }
}