package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.UsersRequest;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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
      List<Callable<Float>> tasks = new ArrayList<>();

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
      List<Callable<Float>> tasks = new ArrayList<>();

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
      List<Callable<Float>> tasks = new ArrayList<>();

      for (String curr : unsupportedCurrency) {
         tasks.add(() -> checkNonConvertibility(curr, USD));
      }

      invokeAllTasks(tasks);

      shutDownExecutor(35);
   }

   @Test
   void checkValueFromApis() throws CurrencyConverterException, IOException {
      Currency eur = Currency.getInstance("USD");
      Currency usd = Currency.getInstance("EUR");
      float value = 1.0f;

      UsersRequest usersRequest = new UsersRequest(eur, usd, value);

      Float resultByBankUa = converterService.convertByBankUaCom(usersRequest);
      Float resultByJavaMoney = converterService.convertByJavaMoney(usersRequest);
      Float resultByCurrencyLayer = converterService.convertByCurrencyLayerCom(usersRequest);
      Float resultByFloatRatesCom = converterService.convertByFloatRatesCom(usersRequest);
      Float resultByFreeCurrencyConverterApiCom = converterService.convertByFreeCurrencyConverterApiCom(usersRequest);

      Float from = 0.86f;
      Float to = 0.89f;

      checkRange(resultByBankUa, from, to);
      checkRange(resultByJavaMoney, from, to);
      checkRange(resultByCurrencyLayer, from, to);
      checkRange(resultByFloatRatesCom, from, to);
      checkRange(resultByFreeCurrencyConverterApiCom, from, to);
   }

   /**
    * Tests, that if currency converts in itself, then amount of it doesn't change
    */
   @Test
   void checkIdenticalCurrency() throws InterruptedException, ExecutionException {
      List<Callable<Float>> tasks = new ArrayList<>();

      for (String currency : existingCurrency) {
         tasks.add(() -> checkConvertForIdenticalCurrencies(currency, new Random().nextFloat()));
      }

      invokeAllTasks(tasks);

      shutDownExecutor(30);
   }

   @Test
   void checkForZero() throws InterruptedException, ExecutionException {
      List<Callable<Float>> tasks = new ArrayList<>();

      for (String currency : existingCurrency) {
         tasks.add(() -> checkForZero(currency));
      }

      invokeAllTasks(tasks);

      shutDownExecutor(20);
   }

   /**
    * Tests conversion all currencies from {@link #existingCurrency} between themselves
    */
   @Test
   @Disabled("this test takes 15 min. ")
   void checkConversionsWithAllPossibleCurrencies() throws InterruptedException, ExecutionException {
      List<Callable<Float>> tasks = new ArrayList<>();

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

   private Float checkForZero(String currency) throws CurrencyConverterException, UnknownHostException {
      Float result = convert(currency, "USD", 0.0f);

      assertEquals(ZERO, result);

      return result;
   }

   /**
    * Asserts, that if first currency converts to second, result of conversion is not null
    *
    * @param userCurrency    currency  to convert from
    * @param desiredCurrency currency  to convert to
    */
   private Float checkConvert(String userCurrency, String desiredCurrency) throws CurrencyConverterException, UnknownHostException {
      Float result = convert(userCurrency, desiredCurrency, 1.0f);

      assertNotNull(result);

      return result;
   }

   private Float convert(String userCurrency, String desiredCurrency, float usersValue) throws CurrencyConverterException, UnknownHostException {
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
   private Float checkNonConvertibility(String userCurrency, String desiredCurrency) {
      Float result = null;
      try {
         result = convert(userCurrency, desiredCurrency, 1.0f);
         throw new RuntimeException(userCurrency + " to " + desiredCurrency + " was converted. ");
      } catch (CurrencyConverterException e) {
         assertTrue(isRightMessage(e));
      } catch (UnknownHostException e) {
         e.printStackTrace();
      }
      return result;
   }

   private void checkRange(Float result, Float from, Float to) {
      assertTrue(from.compareTo(result) <= 0);
      assertTrue(to.compareTo(result) >= 0);
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

   private Float checkConvertForIdenticalCurrencies(String userCurrency, Float expectedValue) throws CurrencyConverterException, UnknownHostException {
      UsersRequest usersRequest = new UsersRequest(Currency.getInstance(userCurrency),
              Currency.getInstance(userCurrency),
              expectedValue);

      Float result = converterService.convert(usersRequest);

      assertEquals(expectedValue, result);

      return result;
   }

   private void invokeAllTasks(List<Callable<Float>> tasks) throws InterruptedException, ExecutionException {
      List<Future<Float>> results = executor.invokeAll(tasks);

      for (Future<Float> result : results) {
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

   private final Float ZERO = 0.0f;
   private final String USD = "USD";
   private final String UAH = "UAH";
}