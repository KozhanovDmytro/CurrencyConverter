package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.UsersRequest;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for testing ConverterService.
 *
 * @author Dmytro K.
 * @author Daria S.
 * @see ConverterService
 */
public class ConverterServiceTest {

   /** Service, which uses for conversion. */
   private static ConverterService converterService = new ConverterService();

   /** Text of exception, if one or both currencies not supported. */
   private static final String MESSAGE_UNSUPPORTED_CURRENCY = "One or two currencies not supported.";

   /** Text of exception, if currency not supported. */
   private static final String API_MESSAGE_WITH_ONE_UNSUPPORTED_CURRENCY = "Currency not supported:";

   /** Array of available currencies that can be converted between themselves by {@link ConverterService}. */
   private static String[] existingCurrency = new String[] {"UAH", "AWG", "GEL", "ALL", "ZAR", "BND", "JMD", "BRL",
           "RUB", "BAM", "SZL", "GNF", "NZD", "SYP", "MKD", "BZD", "KWD", "SLL", "ETB", "BYN", "AZN", "XPF", "BBD",
           "CDF", "RWF", "SOS", "BDT", "ILS", "EGP", "IQD", "RON", "COP", "SEK", "MMK", "SAR", "DJF", "HTG", "PKR",
           "GTQ", "PHP", "TOP", "TND", "VEF", "PEN", "CVE", "NIO", "HUF", "SCR", "THB", "FJD", "MRO", "AOA", "XAF",
           "BOB", "KZT", "LSL", "TMT", "HRK", "BGN", "OMR", "MYR", "VUV", "KES", "XCD", "ARS", "GBP", "SDG", "MUR",
           "VND", "MNT", "GMD", "BSD", "HKD", "GIP", "PGK", "KGS", "LYD", "CAD", "BWP", "IDR", "LRD", "JPY", "NAD",
           "MVR", "ISK", "PAB", "AMD", "BHD", "NOK", "SRD", "IRR", "GYD", "TWD", "ZMW", "XOF", "MWK", "KMF", "KRW",
           "TZS", "DKK", "HNL", "AUD", "MAD", "CRC", "MDL", "TRY", "LBP", "INR", "CLP", "GHS", "NGN", "SBD", "LKR",
           "BIF", "CHF", "DOP", "YER", "PLN", "TJS", "CZK", "MXN", "WST", "UGX", "SVC", "SGD", "PYG", "JOD", "AFN",
           "NPR", "ANG", "QAR", "USD", "ERN", "CUP", "MOP", "CNY", "TTD", "KHR", "DZD", "UZS", "EUR", "AED", "UYU",
           "MZN"};

   /** Array of unsupported currencies which cannot be converted.  */
   private static String[] unsupportedCurrency = new String[]{"GWP", "SKK", "SIT", "MZM", "IEP", "NLG", "ZWN", "GHC",
           "MGF", "ESP", "ZWR", "EEK", "USN", "TRL", "XBD", "CYP", "LUF", "SRG", "XPT", "ADP", "TPE", "COU", "BEF",
           "AFA", "ROL", "DEM", "BOV", "ATS", "XUA", "CHE", "PTE", "VEB", "AYM", "ZWD", "USS", "CSD", "XTS", "BYB",
           "XFU", "XSU", "TMM", "AZM", "XFO", "SDD", "YUM", "XTS", "MTL", "FIM", "CHW", "XBA", "XXX", "UYI", "XBC",
           "GRD", "RUR", "XBB", "BGL"};

  
   /** Executor needed for speed up some tests by multithreading. */
   private ThreadPoolExecutor executor = new ThreadPoolExecutor(145, 21025, 2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(145));


   /**
    * Function test popular currencies in Ukraine.
    */
   @Test
   void convertPopularCurrencies() throws InterruptedException, CurrencyConverterException, UnknownHostException {
      checkConvert("UAH", "RUB");

      checkConvert("UAH", "UAH");
      checkConvert("UAH", "EUR");
      checkConvert("UAH", "USD");

      checkConvert("RUB", "RUB");
      checkConvert("RUB", "UAH");
      checkConvert("RUB", "EUR");
      checkConvert("RUB", "USD");

      checkConvert("EUR", "RUB");
      checkConvert("EUR", "UAH");
      checkConvert("EUR", "EUR");
      checkConvert("EUR", "USD");

      checkConvert("USD", "RUB");
      checkConvert("USD", "UAH");
      checkConvert("USD", "EUR");
      checkConvert("USD", "USD");

      shutDownExecutor(1);
   }

   /**
    * Test currency which can convert to USD. This list was taken by google.com
    */
   @Test
   void checkCurrencyTransferToUSD() throws InterruptedException, ExecutionException {
      List<Callable<BigDecimal>> tasks = new ArrayList<>();

      for (String curr : existingCurrency) {
         tasks.add(() -> checkConvert(curr, USD));
      }

      invokeAllTasks(tasks);

      shutDownExecutor(35);
   }

   /**
    * Test currency which can convert from USD. This list was taken by google.com
    */
   @Test
   void checkCurrencyTransferFromUSD() throws InterruptedException, ExecutionException {
      List<Callable<BigDecimal>> tasks = new ArrayList<>();

      for (String curr : existingCurrency) {
         tasks.add(() -> checkConvert(USD, curr));
      }

      invokeAllTasks(tasks);

      shutDownExecutor(30);
   }

   /**
    * Test currency which can convert to UAH and doesn't contain in {@link #existingCurrency}.
    */
   @Test
   void checkCurrencyTransferToUAH() throws CurrencyConverterException, UnknownHostException {
      checkConvert("MGA", "UAH");
      checkConvert("RSD", "UAH");
   }

   /**
    * Test currency which can convert from UAH and doesn't contain in {@link #existingCurrency}.
    */
   @Test
   void checkCurrencyTransferFromUAH() throws CurrencyConverterException, UnknownHostException {
      checkConvert("UAH", "MGA");
      checkConvert("UAH", "RSD");
   }

   /**
    * Tests currencies, which cannot be converted to USD.
    */
   @Test
   void checkUnsupportedCurrency() throws ExecutionException, InterruptedException {
      List<Callable<BigDecimal>> tasks = new ArrayList<>();

      for (String curr : unsupportedCurrency) {
         tasks.add(() -> checkNonConvertibility(curr, USD));
      }

      invokeAllTasks(tasks);

      shutDownExecutor(35);
   }

   /**
    * Test request to APIs and his response with big numbers.
    */
   @Test
   void bigRequestValuesTest() throws CurrencyConverterException, IOException {
      Currency eur = Currency.getInstance("USD");
      Currency usd = Currency.getInstance("EUR");

      BigDecimal result = BigDecimal.ONE;

      BigDecimal from = new BigDecimal(1.0);
      BigDecimal to = new BigDecimal(0.7);

      BigDecimal coefficient = new BigDecimal(1000000000);

      for (int i = 0; i < 10; i++) {
         UsersRequest usersRequest = new UsersRequest(eur, usd, result);

         BigDecimal resultByBankUa = converterService.convertByBankUaCom(usersRequest);
         BigDecimal resultByJavaMoney = converterService.convertByJavaMoney(usersRequest);
         BigDecimal resultByCurrencyLayer = converterService.convertByCurrencyLayerCom(usersRequest);
         BigDecimal resultByFloatRatesCom = converterService.convertByFloatRatesCom(usersRequest);
         BigDecimal resultByFreeCurrencyConverterApiCom = converterService.convertByFreeCurrencyConverterApiCom(usersRequest);

         checkRange(resultByBankUa, from, to);
         checkRange(resultByJavaMoney, from, to);
         checkRange(resultByCurrencyLayer, from, to);
         checkRange(resultByFloatRatesCom, from, to);
         checkRange(resultByFreeCurrencyConverterApiCom, from, to);

         from = from.multiply(coefficient);
         to = to.multiply(coefficient);
         result = result.multiply(coefficient);
      }
   }

   /**
    * Tests, that if currency converts in itself, then amount of it doesn't change
    */
   @Test
   void checkIdenticalCurrency() throws CurrencyConverterException, UnknownHostException {
      for (String currency : existingCurrency) {
         BigDecimal random = new BigDecimal(new Random().nextFloat() * 1000);
         checkConvertForIdenticalCurrencies(currency, random);
      }
   }

   /**
    * Tests for zero.
    */
   @Test
   void checkForZero() throws CurrencyConverterException, UnknownHostException {
      for (String currency : existingCurrency) {
         checkForZero(currency);
      }
   }

   /**
    * Tests conversion all currencies from {@link #existingCurrency} between themselves
    */
   @Test
   @Disabled("this test takes 15 min. ")
   void checkConversionsWithAllPossibleCurrencies() throws InterruptedException, ExecutionException {
      List<Callable<BigDecimal>> tasks = new ArrayList<>();

      for (int i = 0; i < existingCurrency.length; i++) {
         for (int j = i; j < existingCurrency.length; j++) {
            String curr1 = existingCurrency[i];
            String curr2 = existingCurrency[j];

            tasks.add(() -> checkConvert(curr1, curr2));
         }
      }

      invokeAllTasks(tasks);

      shutDownExecutor(900);
   }

   /**
    * Tests that result of conversion will be close to zero.
    *
    * @param currency code of currency
    * @return result of conversion.
    * @throws CurrencyConverterException problem with conversion.
    * @throws UnknownHostException troubles with internet connection.
    */
   private BigDecimal checkForZero(String currency) throws CurrencyConverterException, UnknownHostException {
      BigDecimal result = convert(currency, "USD", BigDecimal.ZERO);

      assertTrue(result.compareTo(new BigDecimal("1e-5")) <= 0);
      assertTrue(result.compareTo(new BigDecimal("-1e-5")) >= 0);

      return result;
   }

   /**
    * Asserts, that if first currency converts to second, result of conversion is not null
    *
    * @param userCurrency    currency  to convert from
    * @param desiredCurrency currency  to convert to
    */
   private BigDecimal checkConvert(String userCurrency, String desiredCurrency) throws CurrencyConverterException, UnknownHostException {
      BigDecimal result = convert(userCurrency, desiredCurrency, BigDecimal.ONE);

      assertNotNull(result);

      return result;
   }

   private BigDecimal convert(String userCurrency, String desiredCurrency, BigDecimal usersValue) throws CurrencyConverterException, UnknownHostException {
      UsersRequest usersRequest = new UsersRequest(Currency.getInstance(userCurrency),
              Currency.getInstance(desiredCurrency),
              usersValue);

      return converterService.convert(usersRequest);
   }


   /**
    * Asserts that one or both currencies are not supported
    *
    * @param userCurrency    currency  to convert from
    * @param desiredCurrency currency  to convert to
    */
   private BigDecimal checkNonConvertibility(String userCurrency, String desiredCurrency) {
      BigDecimal result = null;
      try {
         result = convert(userCurrency, desiredCurrency, BigDecimal.ONE);
         throw new RuntimeException(userCurrency + " to " + desiredCurrency + " was converted. ");
      } catch (CurrencyConverterException e) {
         assertTrue(isRightMessage(e));
      } catch (UnknownHostException e) {
         e.printStackTrace();
      }
      return result;
   }

   private void checkRange(BigDecimal result, BigDecimal from, BigDecimal to) {
      assertTrue(result.compareTo(from) >= 0);
      assertTrue(result.compareTo(to) <= 0);
   }

   /**
    * Checks, that exception text contains expected text
    *
    * @param e exception, text of message of which has to be checked
    * @return true, if exception text contains expected text
    */
   private boolean isRightMessage(CurrencyConverterException e) {
      return e.getMessage().contains(MESSAGE_UNSUPPORTED_CURRENCY) || e.getMessage().contains(API_MESSAGE_WITH_ONE_UNSUPPORTED_CURRENCY);
   }

   private BigDecimal checkConvertForIdenticalCurrencies(String userCurrency, BigDecimal expectedValue) throws CurrencyConverterException, UnknownHostException {
      UsersRequest usersRequest = new UsersRequest(Currency.getInstance(userCurrency),
              Currency.getInstance(userCurrency),
              expectedValue);

      BigDecimal result = converterService.convert(usersRequest);

      assertEquals(expectedValue, result);

      return result;
   }

   /**
    * Run all tasks and get result of all tasks.
    *
    * @param tasks conversion
    * @throws InterruptedException if problem with threads was occur.
    * @throws ExecutionException problem with conversion.
    */
   private void invokeAllTasks(List<Callable<BigDecimal>> tasks) throws InterruptedException, ExecutionException {
      List<Future<BigDecimal>> results = executor.invokeAll(tasks);

      for (Future<BigDecimal> result : results) {
         result.get();
      }
   }

   /**
    * Waits for all threads and stops {@link this#executor}
    */
   private void shutDownExecutor(long maxSeconds) throws InterruptedException {
      executor.shutdown();
      executor.awaitTermination(maxSeconds, TimeUnit.SECONDS);
   }

   /* constants */

   private final String USD = "USD";
}