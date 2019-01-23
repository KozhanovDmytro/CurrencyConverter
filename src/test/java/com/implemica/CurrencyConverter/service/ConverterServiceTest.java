package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.UsersRequest;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Currency;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
   private static ConverterService converterService;

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

   private Float ZERO = 0.0f;

   private ThreadPoolExecutor executor = new ThreadPoolExecutor(145, 21025, 2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(145));

   private Logger logger = LoggerFactory.getLogger(this.getClass());

   /**
    * Initialisation of ConverterService
    */
   @BeforeAll
   static void setUp() {
      converterService = new ConverterService();
   }


   /**
    * Function test popular currencies in Ukraine.
    */
   @Test
   void convertPopularCurrencies() throws InterruptedException {
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
   void checkCurrencyTransferToUSD() throws InterruptedException {
      checkConvert("PAB", "USD");
      checkConvert("SEK", "USD");
      checkConvert("XCD", "USD");
      checkConvert("DKK", "USD");
      checkConvert("GTQ", "USD");
      checkConvert("MRO", "USD");
      checkConvert("GBP", "USD");
      checkConvert("INR", "USD");
      checkConvert("PKR", "USD");
      checkConvert("BRL", "USD");
      checkConvert("XAF", "USD");
      checkConvert("HKD", "USD");
      checkConvert("MXN", "USD");
      checkConvert("VEF", "USD");
      checkConvert("EUR", "USD");
      checkConvert("ETB", "USD");
      checkConvert("SYP", "USD");
      checkConvert("KES", "USD");
      checkConvert("SBD", "USD");
      checkConvert("MKD", "USD");
      checkConvert("JPY", "USD");
      checkConvert("GYD", "USD");
      checkConvert("HNL", "USD");
      checkConvert("KHR", "USD");
      checkConvert("HRK", "USD");
      checkConvert("OMR", "USD");
      checkConvert("CZK", "USD");
      checkConvert("MDL", "USD");
      checkConvert("JMD", "USD");
      checkConvert("PEN", "USD");
      checkConvert("KWD", "USD");
      checkConvert("BYN", "USD");
      checkConvert("SZL", "USD");
      checkConvert("LRD", "USD");
      checkConvert("NOK", "USD");
      checkConvert("ZMW", "USD");
      checkConvert("BOB", "USD");
      checkConvert("SCR", "USD");
      checkConvert("MYR", "USD");
      checkConvert("ZAR", "USD");
      checkConvert("BIF", "USD");
      checkConvert("AWG", "USD");
      checkConvert("CNY", "USD");
      checkConvert("WST", "USD");
      checkConvert("CDF", "USD");
      checkConvert("LBP", "USD");
      checkConvert("AOA", "USD");
      checkConvert("USD", "USD");
      checkConvert("DOP", "USD");
      checkConvert("NPR", "USD");
      checkConvert("AZN", "USD");
      checkConvert("ARS", "USD");
      checkConvert("MMK", "USD");
      checkConvert("TMT", "USD");
      checkConvert("GIP", "USD");
      checkConvert("IQD", "USD");
      checkConvert("SDG", "USD");
      checkConvert("BWP", "USD");
      checkConvert("SOS", "USD");
      checkConvert("NIO", "USD");
      checkConvert("HTG", "USD");
      checkConvert("VND", "USD");
      checkConvert("TWD", "USD");
      checkConvert("QAR", "USD");
      checkConvert("GMD", "USD");
      checkConvert("MOP", "USD");
      checkConvert("TND", "USD");
      checkConvert("BHD", "USD");
      checkConvert("DJF", "USD");
      checkConvert("THB", "USD");
      checkConvert("MWK", "USD");
      checkConvert("ISK", "USD");
      checkConvert("SVC", "USD");
      checkConvert("COP", "USD");
      checkConvert("NAD", "USD");
      checkConvert("MVR", "USD");
      checkConvert("YER", "USD");
      checkConvert("ANG", "USD");
      checkConvert("RON", "USD");
      checkConvert("SRD", "USD");
      checkConvert("KGS", "USD");
      checkConvert("JOD", "USD");
      checkConvert("AFN", "USD");
      checkConvert("NZD", "USD");
      checkConvert("BND", "USD");
      checkConvert("SLL", "USD");
      checkConvert("PYG", "USD");
      checkConvert("UAH", "USD");
      checkConvert("TOP", "USD");
      checkConvert("PHP", "USD");
      checkConvert("VUV", "USD");
      checkConvert("XOF", "USD");
      checkConvert("GEL", "USD");
      checkConvert("AUD", "USD");
      checkConvert("HUF", "USD");
      checkConvert("UYU", "USD");
      checkConvert("CAD", "USD");
      checkConvert("BSD", "USD");
      checkConvert("RWF", "USD");
      checkConvert("PGK", "USD");
      checkConvert("MZN", "USD");
      checkConvert("MNT", "USD");
      checkConvert("AMD", "USD");
      checkConvert("TRY", "USD");
      checkConvert("RUB", "USD");
      checkConvert("SAR", "USD");
      checkConvert("LSL", "USD");
      checkConvert("NGN", "USD");
      checkConvert("GNF", "USD");
      checkConvert("AED", "USD");
      checkConvert("ALL", "USD");
      checkConvert("CRC", "USD");
      checkConvert("BDT", "USD");
      checkConvert("SGD", "USD");
      checkConvert("MUR", "USD");
      checkConvert("ILS", "USD");
      checkConvert("UGX", "USD");
      checkConvert("IDR", "USD");
      checkConvert("BGN", "USD");
      checkConvert("CVE", "USD");
      checkConvert("KMF", "USD");
      checkConvert("LYD", "USD");
      checkConvert("CLP", "USD");
      checkConvert("EGP", "USD");
      checkConvert("CUP", "USD");
      checkConvert("LKR", "USD");
      checkConvert("TTD", "USD");
      checkConvert("BBD", "USD");
      checkConvert("PLN", "USD");
      checkConvert("UZS", "USD");
      checkConvert("GHS", "USD");
      checkConvert("BAM", "USD");
      checkConvert("ERN", "USD");
      checkConvert("MGA", "USD");
      checkConvert("LAK", "USD");
      checkConvert("TJS", "USD");
      checkConvert("KRW", "USD");
      checkConvert("KZT", "USD");
      checkConvert("RSD", "USD");
      checkConvert("DZD", "USD");
      checkConvert("MAD", "USD");
      checkConvert("XPF", "USD");
      checkConvert("BZD", "USD");
      checkConvert("FJD", "USD");
      checkConvert("CHF", "USD");
      checkConvert("TZS", "USD");
      checkConvert("IRR", "USD");

      shutDownExecutor(30);
   }

   /**
    * Test currency which can convert from USD. This list was taken by google.com
    */
   @Test
   void checkCurrencyTransferFromUSD() throws InterruptedException {
      checkConvert("USD", "PAB");
      checkConvert("USD", "SEK");
      checkConvert("USD", "XCD");
      checkConvert("USD", "DKK");
      checkConvert("USD", "GTQ");
      checkConvert("USD", "MRO");
      checkConvert("USD", "GBP");
      checkConvert("USD", "INR");
      checkConvert("USD", "PKR");
      checkConvert("USD", "BRL");
      checkConvert("USD", "XAF");
      checkConvert("USD", "HKD");
      checkConvert("USD", "MXN");
      checkConvert("USD", "VEF");
      checkConvert("USD", "EUR");
      checkConvert("USD", "ETB");
      checkConvert("USD", "SYP");
      checkConvert("USD", "KES");
      checkConvert("USD", "SBD");
      checkConvert("USD", "MKD");
      checkConvert("USD", "JPY");
      checkConvert("USD", "GYD");
      checkConvert("USD", "HNL");
      checkConvert("USD", "KHR");
      checkConvert("USD", "HRK");
      checkConvert("USD", "OMR");
      checkConvert("USD", "CZK");
      checkConvert("USD", "MDL");
      checkConvert("USD", "JMD");
      checkConvert("USD", "PEN");
      checkConvert("USD", "KWD");
      checkConvert("USD", "BYN");
      checkConvert("USD", "SZL");
      checkConvert("USD", "LRD");
      checkConvert("USD", "NOK");
      checkConvert("USD", "ZMW");
      checkConvert("USD", "BOB");
      checkConvert("USD", "SCR");
      checkConvert("USD", "MYR");
      checkConvert("USD", "ZAR");
      checkConvert("USD", "BIF");
      checkConvert("USD", "AWG");
      checkConvert("USD", "CNY");
      checkConvert("USD", "WST");
      checkConvert("USD", "CDF");
      checkConvert("USD", "LBP");
      checkConvert("USD", "AOA");
      checkConvert("USD", "USD");
      checkConvert("USD", "DOP");
      checkConvert("USD", "NPR");
      checkConvert("USD", "AZN");
      checkConvert("USD", "ARS");
      checkConvert("USD", "MMK");
      checkConvert("USD", "TMT");
      checkConvert("USD", "GIP");
      checkConvert("USD", "IQD");
      checkConvert("USD", "SDG");
      checkConvert("USD", "BWP");
      checkConvert("USD", "SOS");
      checkConvert("USD", "NIO");
      checkConvert("USD", "HTG");
      checkConvert("USD", "VND");
      checkConvert("USD", "TWD");
      checkConvert("USD", "QAR");
      checkConvert("USD", "GMD");
      checkConvert("USD", "MOP");
      checkConvert("USD", "TND");
      checkConvert("USD", "BHD");
      checkConvert("USD", "DJF");
      checkConvert("USD", "THB");
      checkConvert("USD", "MWK");
      checkConvert("USD", "ISK");
      checkConvert("USD", "SVC");
      checkConvert("USD", "COP");
      checkConvert("USD", "NAD");
      checkConvert("USD", "MVR");
      checkConvert("USD", "YER");
      checkConvert("USD", "ANG");
      checkConvert("USD", "RON");
      checkConvert("USD", "SRD");
      checkConvert("USD", "KGS");
      checkConvert("USD", "JOD");
      checkConvert("USD", "AFN");
      checkConvert("USD", "NZD");
      checkConvert("USD", "BND");
      checkConvert("USD", "SLL");
      checkConvert("USD", "PYG");
      checkConvert("USD", "UAH");
      checkConvert("USD", "TOP");
      checkConvert("USD", "PHP");
      checkConvert("USD", "VUV");
      checkConvert("USD", "XOF");
      checkConvert("USD", "GEL");
      checkConvert("USD", "AUD");
      checkConvert("USD", "HUF");
      checkConvert("USD", "UYU");
      checkConvert("USD", "CAD");
      checkConvert("USD", "BSD");
      checkConvert("USD", "RWF");
      checkConvert("USD", "PGK");
      checkConvert("USD", "MZN");
      checkConvert("USD", "MNT");
      checkConvert("USD", "AMD");
      checkConvert("USD", "TRY");
      checkConvert("USD", "RUB");
      checkConvert("USD", "SAR");
      checkConvert("USD", "LSL");
      checkConvert("USD", "NGN");
      checkConvert("USD", "GNF");
      checkConvert("USD", "AED");
      checkConvert("USD", "ALL");
      checkConvert("USD", "CRC");
      checkConvert("USD", "BDT");
      checkConvert("USD", "SGD");
      checkConvert("USD", "MUR");
      checkConvert("USD", "ILS");
      checkConvert("USD", "UGX");
      checkConvert("USD", "IDR");
      checkConvert("USD", "BGN");
      checkConvert("USD", "CVE");
      checkConvert("USD", "KMF");
      checkConvert("USD", "LYD");
      checkConvert("USD", "CLP");
      checkConvert("USD", "EGP");
      checkConvert("USD", "CUP");
      checkConvert("USD", "LKR");
      checkConvert("USD", "TTD");
      checkConvert("USD", "BBD");
      checkConvert("USD", "PLN");
      checkConvert("USD", "UZS");
      checkConvert("USD", "GHS");
      checkConvert("USD", "BAM");
      checkConvert("USD", "ERN");
      checkConvert("USD", "MGA");
      checkConvert("USD", "LAK");
      checkConvert("USD", "TJS");
      checkConvert("USD", "KRW");
      checkConvert("USD", "KZT");
      checkConvert("USD", "RSD");
      checkConvert("USD", "DZD");
      checkConvert("USD", "MAD");
      checkConvert("USD", "XPF");
      checkConvert("USD", "BZD");
      checkConvert("USD", "FJD");
      checkConvert("USD", "CHF");
      checkConvert("USD", "TZS");
      checkConvert("USD", "IRR");

      shutDownExecutor(30);
   }

   /**
    * Test currency which can convert to UAH and doesn't contain in {@link #existingCurrency}.
    *
    */
   @Test
   void checkCurrencyTransferToUAH() {
      checkConvert("MGA", "UAH");
      checkConvert("RSD", "UAH");
   }

   /**
    * Test currency which can convert from UAH and doesn't contain in {@link #existingCurrency}.
    *
    */
   @Test
   void checkCurrencyTransferFromUAH() {
      checkConvert("UAH", "MGA");
      checkConvert("UAH", "RSD");
   }

   /**
    * Tests currencies, which cannot be converted to USD.
    */
   @Test
   void checkUnsupportedCurrency() throws InterruptedException {
      checkNonConvertibility("GWP", "USD");
      checkNonConvertibility("SKK", "USD");
      checkNonConvertibility("SIT", "USD");
      checkNonConvertibility("MZM", "USD");
      checkNonConvertibility("IEP", "USD");
      checkNonConvertibility("NLG", "USD");
      checkNonConvertibility("ZWN", "USD");
      checkNonConvertibility("GHC", "USD");
      checkNonConvertibility("MGF", "USD");
      checkNonConvertibility("ESP", "USD");
      checkNonConvertibility("ZWR", "USD");
      checkNonConvertibility("EEK", "USD");
      checkNonConvertibility("USN", "USD");
      checkNonConvertibility("TRL", "USD");
      checkNonConvertibility("XBD", "USD");
      checkNonConvertibility("CYP", "USD");
      checkNonConvertibility("LUF", "USD");
      checkNonConvertibility("SRG", "USD");
      checkNonConvertibility("XPT", "USD");
      checkNonConvertibility("ADP", "USD");
      checkNonConvertibility("TPE", "USD");
      checkNonConvertibility("COU", "USD");
      checkNonConvertibility("BEF", "USD");
      checkNonConvertibility("AFA", "USD");
      checkNonConvertibility("ROL", "USD");
      checkNonConvertibility("DEM", "USD");
      checkNonConvertibility("BOV", "USD");
      checkNonConvertibility("ATS", "USD");
      checkNonConvertibility("XUA", "USD");
      checkNonConvertibility("CHE", "USD");
      checkNonConvertibility("PTE", "USD");
      checkNonConvertibility("VEB", "USD");
      checkNonConvertibility("AYM", "USD");
      checkNonConvertibility("ZWD", "USD");
      checkNonConvertibility("USS", "USD");
      checkNonConvertibility("CSD", "USD");
      checkNonConvertibility("XTS", "USD");
      checkNonConvertibility("BYB", "USD");
      checkNonConvertibility("XFU", "USD");
      checkNonConvertibility("XSU", "USD");
      checkNonConvertibility("TMM", "USD");
      checkNonConvertibility("AZM", "USD");
      checkNonConvertibility("XFO", "USD");
      checkNonConvertibility("SDD", "USD");
      checkNonConvertibility("YUM", "USD");
      checkNonConvertibility("XTS", "UAH");
      checkNonConvertibility("MTL", "USD");
      checkNonConvertibility("FIM", "USD");
      checkNonConvertibility("CHW", "USD");
      checkNonConvertibility("XBA", "USD");
      checkNonConvertibility("XXX", "USD");
      checkNonConvertibility("UYI", "USD");
      checkNonConvertibility("XBC", "USD");
      checkNonConvertibility("GRD", "USD");
      checkNonConvertibility("RUR", "USD");
      checkNonConvertibility("XBB", "USD");
      checkNonConvertibility("BGL", "USD");
      checkNonConvertibility("BYR", "USD");
      checkNonConvertibility("LVL", "USD");
      checkNonConvertibility("BTN", "USD");
      checkNonConvertibility("BMD", "USD");
      checkNonConvertibility("KYD", "USD");
      checkNonConvertibility("STD", "USD");
      checkNonConvertibility("KPW", "USD");
      checkNonConvertibility("SHP", "USD");
      checkNonConvertibility("FKP", "USD");
      checkNonConvertibility("LTL", "USD");

      shutDownExecutor(30);
   }

   @Test
   void checkValueFromApis() throws CurrencyConverterException, IOException {
      Currency eur = Currency.getInstance("EUR");
      Currency usd = Currency.getInstance("USD");
      float value = 1.0f;

      UsersRequest usersRequest = new UsersRequest(eur, usd, value);

      Float resultByBankUa = converterService.convertByBankUaCom(usersRequest);
      Float resultByJavaMoney = converterService.convertByJavaMoney(usersRequest);
      Float resultByFloatRatesCom = converterService.convertByFloatRatesCom(usersRequest);
      Float resultByFreeCurrencyConverterApiCom = converterService.convertByFreeCurrencyConverterApiCom(usersRequest);


      checkRange(resultByBankUa, 1.1f, 1.2f);
      checkRange(resultByJavaMoney, 1.1f, 1.2f);
      checkRange(resultByFloatRatesCom, 1.1f, 1.2f);
      checkRange(resultByFreeCurrencyConverterApiCom, 1.1f, 1.2f);
   }

   /**
    * Tests, that if currency converts in itself, then amount of it doesn't change
    */
   @Test
   void checkIdenticalCurrency() throws InterruptedException {
      for (String currency : existingCurrency) {
         executor.execute(() -> checkConvertForIdenticalCurrencies(currency, new Random().nextFloat()));
      }

      shutDownExecutor(30);
   }

   @Test
   void checkForZero() throws InterruptedException {
      for (String currency : existingCurrency) {
         executor.execute(() -> checkForZero(currency));
      }

      shutDownExecutor(30);
   }

   private void shutDownExecutor(long maxSeconds) throws InterruptedException {
      executor.awaitTermination(maxSeconds, TimeUnit.SECONDS);
      executor.shutdown();
   }

   private void checkForZero(String currency) {
      try {
         assertEquals(ZERO, convert(currency, "USD", 0.0f));
      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (CurrencyConverterException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Tests conversion all currencies from {@link #existingCurrency} between themselves
    *
    */
   @Test
   void checkConversionsWithAllPossibleCurrencies() throws InterruptedException {
      for (String curr1: existingCurrency) {
         executor.execute(() -> checkOneCurrencyWithAll(curr1));
      }

      shutDownExecutor(300);
   }

   private void checkOneCurrencyWithAll(String curr1) {
      for (String curr2: existingCurrency) {
         try {
            assertNotNull(convert(curr1, curr2, 1.0f));
         } catch (CurrencyConverterException e) {
            throw new RuntimeException(e);
         } catch (UnknownHostException e) {
            e.printStackTrace();
         };
      }
   }

   /**
    * Asserts, that if first currency converts to second, result of conversion is not null
    *
    * @param userCurrency    currency  to convert from
    * @param desiredCurrency currency  to convert to
    */
   private void checkConvert(String userCurrency, String desiredCurrency) {
      executor.execute(() -> {
         try {
            assertNotNull(convert(userCurrency, desiredCurrency, 1.0f));
         } catch (CurrencyConverterException e) {
            throw new RuntimeException(e);
         } catch (UnknownHostException e) {
            e.printStackTrace();
         }
      });
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
   private void checkNonConvertibility(String userCurrency, String desiredCurrency) {
      executor.execute(() -> {
         try {
            convert(userCurrency, desiredCurrency, 1.0f);
         } catch (CurrencyConverterException e) {
            assertTrue(isRightMessage(e));
         } catch (UnknownHostException e) {
            e.printStackTrace();
         }
      });
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

   private void checkConvertForIdenticalCurrencies(String userCurrency, Float expectedValue) {
      UsersRequest usersRequest = new UsersRequest(Currency.getInstance(userCurrency),
              Currency.getInstance(userCurrency),
              expectedValue);

      try {
         assertEquals(expectedValue, converterService.convert(usersRequest));
      } catch (UnknownHostException e) {
         checkConvertForIdenticalCurrencies(userCurrency, expectedValue);
      } catch (CurrencyConverterException e) {
         throw new RuntimeException(e);
      }
   }
}