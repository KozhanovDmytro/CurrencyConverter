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
   private static final String WRONG_CONTENT = BotService.UNIQUE;

   /**
    * Greeting message to user
    */
   private static final String START_MESSAGE = BotService.START_MESSAGE;

   /**
    * Message to the user with the suggestion of a new conversion
    */
   private static final String CONVERT_MESSAGE = BotService.CONVERT_MESSAGE;
   /**
    * Stop message to the user
    */
   private static final String STOP_MESSAGE = BotService.STOP_MESSAGE;

   /**
    * Bot's command to start conversation
    */
   private static final String START = BotService.START;
   /**
    * Bot's command to start convert currencies
    */
   private static final String CONVERT = BotService.CONVERT;
   /**
    * Bot's command to stop conversation
    */
   private static final String STOP = BotService.STOP;
   /**
    * Bot's response for /convert command
    */
   private static final String FIRST_CONVERT_MESSAGE = BotService.FIRST_CONVERT_MESSAGE;
   /**
    * Start of bot's response after entering first currency
    */
   private static final String SECOND_CONVERT_MESSAGE_1 = BotService.SECOND_CONVERT_MESSAGE_1;
   /**
    * End of bot's response after entering first currency
    */
   private static final String SECOND_CONVERT_MESSAGE_2 = BotService.SECOND_CONVERT_MESSAGE_2;
   /**
    * Bot's response after entering second currency
    */
   private static final String THIRD_CONVERT_MESSAGE = BotService.THIRD_CONVERT_MESSAGE;

   /**
    * Bot's response for not-text requests
    */
   private static final String UNREADABLE_CONTENT_MESSAGE = BotService.INCORRECT_CONTENT_MESSAGE;
   /**
    * Bot's response for incorrect request from user
    */
   private static final String INCORRECT_REQUEST_MESSAGE = BotService.INCORRECT_REQUEST_MESSAGE;
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
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after command
      compute(START, START_MESSAGE);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      compute(STOP, STOP_MESSAGE);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      compute(CONVERT, FIRST_CONVERT_MESSAGE);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after second conversion step
      interruptConvertOnSecondStep("usd", "USD", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnSecondStep("xxx", "XXX", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnSecondStep("hello", "HELLO", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after third conversion step
      interruptConvertOnThirdStep("pln", "PLN", "uah", "UAH", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("cad", "CAD", "uss", "USS", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("mop", "MOP", "say", "SAY", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      interruptConvertOnThirdStep("sit", "SIT", "eur", "EUR", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("trl", "TRL", "xbb", "XBB", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("xbc", "XBC", "come", "COME", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      interruptConvertOnThirdStep("how", "HOW", "rub", "RUB", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("are", "ARE", "afa", "AFA", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);
      interruptConvertOnThirdStep("you", "YOU", "talk", "TALK", WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      //wrong content after conversion
      rightScript("EUR", "EUR", "Usd", "USD", "30.6");
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("Myr", "MYR", "KhR", "KHR", "-67");
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withUnsupportedCurrency("PLN", "PLN", "ITL", "ITL", "0.1", true);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("Gel", "GEL", "ITL", "ITL", "0.11,");
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("UAH", "UAH", "rubli", "RUBLI", "2000", true);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("Xof", "XOF", "hi", "HI", "abc", true);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withUnsupportedCurrency("xts", "XTS", "dop", "DOP", "301", false);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("cou", "COU", "ern", "ERN", "-17");
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withUnsupportedCurrency("xts", "XTS", "csd", "CSD", "804", false);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongAmount("mtl", "MTL", "USS", "USS", "-10000");
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("xxx", "XXX", "val", "VAL", "13.3", true);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("xua", "XUA", "I", "I", "0 01", true);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("russian", "RUSSIAN", "uah", "UAH", "300", false);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("tttttttt", "TTTTTTTT", "isk", "ISK", "-34", false);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("lalala", "LALALA", "tMM", "TMM", "21", false);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("go", "GO", "xts", "XTS", "-0.1", false);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("dog", "DOG", "bird", "BIRD", "100000", false);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      withWrongCurrency("spoon", "SPOON", "plate", "PLATE", "4 cups", false);
      compute(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

   }


   /**
    * Tests, that one line request works correctly
    */
   @Test
   void oneLineRequestTest() {
      //all is correct
      oneLineRequest("10 usd to uah");
      oneLineRequest("16 rub in usd");

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
      compute("17 php from eur", INCORRECT_REQUEST_MESSAGE);

      //wrong word between currencies and wrong amount
      compute("2.3. txr is xcd", INCORRECT_REQUEST_MESSAGE);

      //wrong word between currencies and unsupported second currency
      compute("18 jpy 1 php", INCORRECT_REQUEST_MESSAGE);

      //wrong word between currencies, unsupported second currency and wrong amount
      compute("0..0 tzs ta luf", INCORRECT_REQUEST_MESSAGE);

      //wrong word between currencies and wrong second currency
      compute("1 eur for cat", INCORRECT_REQUEST_MESSAGE);

      //word between currencies, second currency and amount are wrong
      compute("m. zmw tto you", INCORRECT_REQUEST_MESSAGE);

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
      compute("21,1 PTE but usd", INCORRECT_REQUEST_MESSAGE);

      //first currency is unsupported, wrong word between currencies, wrong amount
      compute("-300 uss inn rub", INCORRECT_REQUEST_MESSAGE);

      //both currencies are unsupported, wrong word between currencies
      compute("54 Rur are Xua", INCORRECT_REQUEST_MESSAGE);

      //both currencies are unsupported, wrong word between currencies, wrong amount
      compute("from xbb 77 iep", INCORRECT_REQUEST_MESSAGE);

      //first currency is unsupported, wrong word between currencies, wrong second currency
      compute("87.7 Xxx s currency", INCORRECT_REQUEST_MESSAGE);

      //first currency is unsupported, wrong word between currencies, wrong second currency, wrong amount
      compute("-15..5 Che cv convert", INCORRECT_REQUEST_MESSAGE);

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
      compute("43 parrots into lkr", INCORRECT_REQUEST_MESSAGE);

      //first currency is wrong, wrong word between currencies, wrong amount
      compute("03.03.12 sunny and usd", INCORRECT_REQUEST_MESSAGE);

      //first currency is wrong, second is unsupported, wrong word between currencies
      compute("73,9 pieces with xxx", INCORRECT_REQUEST_MESSAGE);

      //first currency is wrong, second is unsupported , wrong word between currencies, wrong amount
      compute("-12 kilos of pte", INCORRECT_REQUEST_MESSAGE);

      //both currencies are wrong, wrong word between currencies
      compute("823.75 percent of 45", INCORRECT_REQUEST_MESSAGE);

      //all is wrong
      compute("one letter for you", INCORRECT_REQUEST_MESSAGE);

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

      incorrectOneLineRequestAndCommand("-367 tzs in inr", "Sorry, but \"-367\" is not a valid number." +
              " Conversion is impossible." + CONVERT_MESSAGE, START, START_MESSAGE);
      incorrectOneLineRequestAndCommand("hundred rub to xxx", "Sorry, but \"hundred\" is not a valid number." +
              " Conversion is impossible." + CONVERT_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);
      incorrectOneLineRequestAndCommand("13$ usd in rur", "Sorry, but \"13$\" is not a valid number." +
              " Conversion is impossible." + CONVERT_MESSAGE, STOP, STOP_MESSAGE);
      incorrectOneLineRequestAndCommand("15..7 irr to php", "Sorry, but \"15..7\" is not a valid number." +
              " Conversion is impossible." + CONVERT_MESSAGE, "hello", INCORRECT_REQUEST_MESSAGE);
      incorrectOneLineRequestAndCommand("0.01. xof in xfo", "Sorry, but \"0.01.\" is not a valid number." +
              " Conversion is impossible." + CONVERT_MESSAGE, WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      incorrectOneLineRequestAndCommand("44 xbb to xcd", "Currency not supported: XBB" + CONVERT_MESSAGE,
              START, START_MESSAGE);
      incorrectOneLineRequestAndCommand("105.7 usd in uss", "Currency not supported: USS" + CONVERT_MESSAGE,
              CONVERT, FIRST_CONVERT_MESSAGE);
      incorrectOneLineRequestAndCommand("58721 uah to gwp", "Currency not supported: GWP" + CONVERT_MESSAGE,
              STOP, STOP_MESSAGE);
      incorrectOneLineRequestAndCommand("65,3 iep in usn", "Currency not supported: IEP" + CONVERT_MESSAGE,
              "convert", INCORRECT_REQUEST_MESSAGE);
      incorrectOneLineRequestAndCommand("711 jpy to xxx", "Currency not supported: XXX" + CONVERT_MESSAGE,
              WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      incorrectOneLineRequestAndCommand("16 flowers in uah", "Sorry, but currency is not valid: FLOWERS" +
              CONVERT_MESSAGE, START, START_MESSAGE);
      incorrectOneLineRequestAndCommand("101.1 rub to dollars", "Sorry, but currency is not valid: DOLLARS" +
              CONVERT_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);
      incorrectOneLineRequestAndCommand("7,7 letter in book", "Sorry, but currency is not valid: LETTER" +
              CONVERT_MESSAGE, STOP, STOP_MESSAGE);
      incorrectOneLineRequestAndCommand("568 kitten to afn", "Sorry, but currency is not valid: KITTEN" +
              CONVERT_MESSAGE, "hi", INCORRECT_REQUEST_MESSAGE);
      incorrectOneLineRequestAndCommand("9,9 rain in cloud", "Sorry, but currency is not valid: RAIN" +
              CONVERT_MESSAGE, WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      incorrectOneLineRequestAndCommand("12 uah convertTo usd", INCORRECT_REQUEST_MESSAGE, START, START_MESSAGE);
      incorrectOneLineRequestAndCommand("-213 irr from uer", INCORRECT_REQUEST_MESSAGE, STOP, STOP_MESSAGE);
      incorrectOneLineRequestAndCommand("100 php of java", INCORRECT_REQUEST_MESSAGE, CONVERT, FIRST_CONVERT_MESSAGE);
      incorrectOneLineRequestAndCommand("0.001 like on profile", INCORRECT_REQUEST_MESSAGE, "moon", INCORRECT_REQUEST_MESSAGE);
      incorrectOneLineRequestAndCommand("78,9 bob of inr", INCORRECT_REQUEST_MESSAGE, WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE);

      //if line conversion after another command
      commandAndOneLineRequest(START, START_MESSAGE, "8,8 INR to BYN");
      commandAndOneLineRequest(STOP, STOP_MESSAGE, "32 usd in uah");
      commandAndOneLineRequest(CONVERT, FIRST_CONVERT_MESSAGE, "15 rub to eur");
      commandAndOneLineRequest("say something", INCORRECT_REQUEST_MESSAGE, "80.6 pln in inr");
      commandAndOneLineRequest(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE, "140.01 rub to rub");

      commandAndIncorrectOneLineRequest(START, START_MESSAGE, "-678 afn in usd", "Sorry, but " +
              "\"-678\" is not a valid number. Conversion is impossible." + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(STOP, STOP_MESSAGE, "177.. xxx to xxx", "Sorry, but " +
              "\"177..\" is not a valid number. Conversion is impossible." + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(CONVERT, FIRST_CONVERT_MESSAGE, "one uss in lkr", SECOND_CONVERT_MESSAGE_1 +
              "ONEUSSINLKR" + SECOND_CONVERT_MESSAGE_2);
      commandAndIncorrectOneLineRequest("conversion", INCORRECT_REQUEST_MESSAGE, "67h uah to omr",
              "Sorry, but \"67h\" is not a valid number. Conversion is impossible." + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE, "0,.1 brl in pln",
              "Sorry, but \"0,.1\" is not a valid number. Conversion is impossible." + CONVERT_MESSAGE);

      commandAndIncorrectOneLineRequest(START, START_MESSAGE, "37 Xua to Clp", "Currency not supported: XUA"
              + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(STOP, STOP_MESSAGE, "32,3 PLN in XBC", "Currency not supported: XBC"
              + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(CONVERT, FIRST_CONVERT_MESSAGE, "3000 Inr to Xbb", SECOND_CONVERT_MESSAGE_1 +
              "3000INRTOXBB" + SECOND_CONVERT_MESSAGE_2);
      commandAndIncorrectOneLineRequest("lucky", INCORRECT_REQUEST_MESSAGE, "209.9 rub in XFO",
              "Currency not supported: XFO" + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE, "459,2 uah to uss",
              "Currency not supported: USS" + CONVERT_MESSAGE);

      commandAndIncorrectOneLineRequest(START, START_MESSAGE, "56 ladies to eur", "Sorry, but " +
              "currency is not valid: LADIES" + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(STOP, STOP_MESSAGE, "18 computers in office", "Sorry, but " +
              "currency is not valid: COMPUTERS" + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(CONVERT, FIRST_CONVERT_MESSAGE, "21 uah to euro",
              SECOND_CONVERT_MESSAGE_1 + "21UAHTOEURO" + SECOND_CONVERT_MESSAGE_2);
      commandAndIncorrectOneLineRequest("try", INCORRECT_REQUEST_MESSAGE, "888 flats in money",
              "Sorry, but currency is not valid: FLATS" + CONVERT_MESSAGE);
      commandAndIncorrectOneLineRequest(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE, "4,3 rows to text",
              "Sorry, but currency is not valid: ROWS" + CONVERT_MESSAGE);

      commandAndIncorrectOneLineRequest(START, START_MESSAGE, "16 rup fo 13 ua", INCORRECT_REQUEST_MESSAGE);
      commandAndIncorrectOneLineRequest(STOP, STOP_MESSAGE, "68721 mro convert in uah", INCORRECT_REQUEST_MESSAGE);
      commandAndIncorrectOneLineRequest(CONVERT, FIRST_CONVERT_MESSAGE, "21.2. second of hour",
              SECOND_CONVERT_MESSAGE_1 + "21.2.SECONDOFHOUR" + SECOND_CONVERT_MESSAGE_2);
      commandAndIncorrectOneLineRequest("sorry", INCORRECT_REQUEST_MESSAGE, "777 cats on the table",
              INCORRECT_REQUEST_MESSAGE);
      commandAndIncorrectOneLineRequest(WRONG_CONTENT, UNREADABLE_CONTENT_MESSAGE, "5 pln inn uah",
              INCORRECT_REQUEST_MESSAGE);
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
      rightScript("Pln", "PLN", "Usd", "USD", "30.6");
      compute(START, START_MESSAGE);

      rightScript("EUr", "EUR", "UAh", "UAH", "150,50");
      compute(STOP, STOP_MESSAGE);

      rightScript("Inr", "INR", "Rub", "RUB", "29");
      oneLineRequest("12 uah to eur");

      rightScript("RUB", "RUB", "CAD", "CAD", "542");
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //with right currencies and wrong amount
      withWrongAmount("Myr", "MYR", "KhR", "KHR", "-67");
      compute(START, START_MESSAGE);

      withWrongAmount("Mxv", "MXV", "XAU", "XAU", "one");
      compute(STOP, STOP_MESSAGE);

      withWrongAmount("php", "PHP", "uah", "UAH", "-1");
      oneLineRequest("99 Pln in Uah");

      withWrongAmount("LTL", "LTL", "QAR", "QAR", "167f");
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second unsupported, right amount
      withUnsupportedCurrency("PLN", "PLN", "ITL", "ITL", "0.1", true);
      compute(START, START_MESSAGE);

      withUnsupportedCurrency("EUr", "EUR", "Bov", "BOV", "150,50", true);
      compute(STOP, STOP_MESSAGE);

      withUnsupportedCurrency("pln", "PLN", "Iep", "IEP", "77.2",true);
      oneLineRequest("0.75 usd in rub");

      withUnsupportedCurrency("UAH", "UAH", "XUA", "XUA", "542", true);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second unsupported, wrong amount
      withWrongAmount("Gel", "GEL", "ITL", "ITL", "0.11,");
      compute(START, START_MESSAGE);

      withWrongAmount("Cny", "CNY", "Che", "CHE", "-98");
      compute(STOP, STOP_MESSAGE);

      withWrongAmount("Xcd", "XCD", "XXX", "XXX", "4$");
      oneLineRequest("100 eur to eur");

      withWrongAmount("USD", "USD", "SRG", "SRG", "87$");
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second wrong, right amount
      withWrongCurrency("UAH", "UAH", "rubli", "RUBLI", "2000", true);
      compute(START, START_MESSAGE);

      withWrongCurrency("bgn", "BGN", "uusd", "UUSD", "12000", true);
      compute(STOP, STOP_MESSAGE);

      withWrongCurrency("Php", "PHP", "Rubli", "RUBLI", "13.2",true);
      oneLineRequest("54.8 Inr in usd");

      withWrongCurrency("Try", "TRY", "euro", "EURO", "10", true);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency correct, second wrong, wrong amount
      withWrongCurrency("Xof", "XOF", "hi", "HI", "abc", true);
      compute(START, START_MESSAGE);

      withWrongCurrency("SYP", "SYP", "big", "BIG", "-12", true);
      compute(STOP, STOP_MESSAGE);

      withWrongCurrency("Irr", "IRR", "mum", "MUM", "127h",true);
      oneLineRequest("9.4 byn to xof");

      withWrongCurrency("USD", "USD", "from", "FROM", "12.5%", true);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second correct, right amount
      withUnsupportedCurrency("xts", "XTS", "dop", "DOP", "301", false);
      compute(START, START_MESSAGE);

      withUnsupportedCurrency("SRG", "SRG", "UYU", "UYU", "7", false);
      compute(STOP, STOP_MESSAGE);

      withUnsupportedCurrency("Rur", "RUR", "Rub", "RUB", "122",false);
      oneLineRequest("2000 lsl in zmw");

      withUnsupportedCurrency("zWR", "ZWR", "INR", "INR", "800", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second correct, wrong amount
      withWrongAmount("cou", "COU", "ern", "ERN", "-17");
      compute(START, START_MESSAGE);

      withWrongAmount("esp", "ESP", "lvl", "LVL", "a");
      compute(STOP, STOP_MESSAGE);

      withWrongAmount("Bef", "BEF", "Uah", "UAH", "2+1");
      oneLineRequest("118 eur to php");

      withWrongAmount("zWR", "ZWR", "INR", "INR", "111..");
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //both currencies unsupported, right amount
      withUnsupportedCurrency("xts", "XTS", "csd", "CSD", "804", false);
      compute(START, START_MESSAGE);

      withUnsupportedCurrency("SRG", "SRG", "aym", "AYM", "90.7", false);
      compute(STOP, STOP_MESSAGE);

      withUnsupportedCurrency("Xua", "XUA", "Xbb", "XBB", "500.6",false);
      oneLineRequest("1 rub in pln");

      withUnsupportedCurrency("zWR", "ZWR", "Iep", "IEP", "505,5", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //both currencies unsupported, wrong amount
      withWrongAmount("mtl", "MTL", "USS", "USS", "-10000");
      compute(START, START_MESSAGE);

      withWrongAmount("rol", "ROL", "RUr", "RUR", "12 6");
      compute(STOP, STOP_MESSAGE);

      withWrongAmount("Gwp", "GWP", "Che", "CHE", "211 219.");
      oneLineRequest("9,9 lkr to mzn");

      withWrongAmount("sKK", "SKK", "Xbb", "XBB", "08,,1");
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second wrong, right amount
      withWrongCurrency("xxx", "XXX", "val", "VAL", "13.3", true);
      compute(START, START_MESSAGE);

      withWrongCurrency("Fim", "FIM", "little", "LITTLE", "444", true);
      compute(STOP, STOP_MESSAGE);

      withWrongCurrency("pte", "PTE", "pet", "PET", "0",true);
      oneLineRequest("562 bob in pab");

      withWrongCurrency("CSD", "CSD", "sad", "SAD", "222222222222", true);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency unsupported, second wrong, wrong amount
      withWrongCurrency("xua", "XUA", "I", "I", "0 01", true);
      compute(START, START_MESSAGE);

      withWrongCurrency("Gwp", "GWP", "find", "FIND", "-10", true);
      compute(STOP, STOP_MESSAGE);

      withWrongCurrency("Fim", "FIM", "Film", "FILM", "-2a",true);
      oneLineRequest("83 xcd to uah");

      withWrongCurrency("Nlg", "NLG", "you", "YOU", "16p", true);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency wrong, second correct, right amount
      withWrongCurrency("russian", "RUSSIAN", "uah", "UAH", "300", false);
      compute(START, START_MESSAGE);

      withWrongCurrency("spanish", "SPANISH", "usd", "USD", "10.5", false);
      compute(STOP, STOP_MESSAGE);

      withWrongCurrency("star", "STAR", "eur", "EUR", "700",false);
      oneLineRequest("111 irr in inr");

      withWrongCurrency("canadian", "CANADIAN", "eur", "EUR", "600", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency wrong, second correct, wrong amount
      withWrongCurrency("tttttttt", "TTTTTTTT", "isk", "ISK", "-34", false);
      compute(START, START_MESSAGE);

      withWrongCurrency("aaaa", "AAAA", "tmt", "TMT", "7.7.", false);
      compute(STOP, STOP_MESSAGE);

      withWrongCurrency("treasure", "TREASURE", "CUP", "CUP", "-21.1",false);
      oneLineRequest("843 cup to usd");

      withWrongCurrency("Poln", "POLN", "pyg", "PYG", "9pln", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency wrong, second unsupported, right amount
      withWrongCurrency("lalala", "LALALA", "tMM", "TMM", "21", false);
      compute(START, START_MESSAGE);

      withWrongCurrency("tururu", "TURURU", "luf", "LUF", "18,40", false);
      compute(STOP, STOP_MESSAGE);

      withWrongCurrency("song", "SONG", "uss", "USS", "10",false);
      oneLineRequest("782 jpy in eur");

      withWrongCurrency("bah", "BAH", "azm", "AZM", "0.52", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //first currency wrong, second unsupported, wrong amount
      withWrongCurrency("go", "GO", "xts", "XTS", "-0.1", false);
      compute(START, START_MESSAGE);

      withWrongCurrency("wait", "WAIT", "veb", "VEB", "veb", false);
      compute(STOP, STOP_MESSAGE);

      withWrongCurrency("pencil", "PENCIL", "XBC", "XBC", "44.4",false);
      oneLineRequest("300 uah to rub");

      withWrongCurrency("stop", "STOP", "eec", "EEC", "9 9", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //both currencies wrong, right amount
      withWrongCurrency("dog", "DOG", "bird", "BIRD", "100000", false);
      compute(START, START_MESSAGE);

      withWrongCurrency("snow", "SNOW", "water", "WATER", "30", false);
      compute(STOP, STOP_MESSAGE);

      withWrongCurrency("question", "QUESTION", "answer", "ANSWER", "333.333",false);
      oneLineRequest("28,7 omr in php");

      withWrongCurrency("part", "PART", "whole", "WHOLE", "58", false);
      compute(CONVERT, FIRST_CONVERT_MESSAGE);


      //both currencies wrong, wrong amount
      withWrongCurrency("spoon", "SPOON", "plate", "PLATE", "4 cups", false);
      compute(START, START_MESSAGE);

      withWrongCurrency("child", "CHILD", "man", "MAN", "-2", false);
      compute(STOP, STOP_MESSAGE);

      withWrongCurrency("nurse", "NURSE", "doctor", "DOCTOR", "-1", false);
      oneLineRequest("78921 pln to lkr");

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
      String message = "Sorry, but \"" + amount + "\" is not a valid number. Conversion is impossible." + CONVERT_MESSAGE;
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
      compute(message, INCORRECT_REQUEST_MESSAGE);
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
      String message = "Currency not supported: ";
      if (!isUnsupported) {
         message = "Sorry, but currency is not valid: ";
      }
      compute(request, message + wrongCurrency.toUpperCase() + CONVERT_MESSAGE);
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
      compute(request, response);
      compute(command, commandResponse);
      if (command.equals(CONVERT)) {
         testBotService.onUpdateReceived(STOP, testUser);
      }
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
      compute(command, response);
      compute(request, requestResponse);
      if (command.equals(CONVERT)) {
         testBotService.onUpdateReceived(STOP, testUser);
      }
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
      compute(command, response);
      if (!command.equals(CONVERT)) {
         oneLineRequest(request);
      } else {
         compute(request, SECOND_CONVERT_MESSAGE_1 + request.replaceAll(" ", "").toUpperCase()
                 + SECOND_CONVERT_MESSAGE_2);
         testBotService.onUpdateReceived(STOP, testUser);
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
      compute(command, response);
      if (command.equals(CONVERT)) {
         testBotService.onUpdateReceived(STOP, testUser);
      }
   }

   /**
    * One line request with wrong amount
    *
    * @param request currency request
    */
   private void oneLineRequestWithWrongAmount(String request) {
      String[] words = request.split("\\s+");
      String amount = words[0];
      compute(request, "Sorry, but \"" + amount + "\" is not a valid number. Conversion is impossible." + CONVERT_MESSAGE);
   }

   /**
    * Right one line request to bot
    *
    * @param request currency request
    */
   private void oneLineRequest(String request) {
      String botsResponse = testBotService.onUpdateReceived(request, testUser);
      String[] words = request.split("\\s+");
      String start = words[0] + " " + words[1].toUpperCase() + " is ";
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