package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.Converter;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Currency;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for testing ConverterService.
 *
 * @see ConverterService
 *
 * @author Dmytro K.
 * @author Dasha S.
 */
public class ConverterTest {

   private static ConverterService converterService;

   private static final String MESSAGE_UNSUPPORTED_CURRENCY = "One or two currencies not supported.";

   private static final String API_MESSAGE_WITH_ONE_UNSUPPORTED_CURRENCY = "Currency not supported:";

   /**
    * Array of available currencies that can be converted between themselves by {@link ConverterService}.
    */
   private static String[] existingCurrency = new String[] {"LAK", "UAH", "AWG", "GEL", "ALL", "ZAR", "BND", "JMD", "RUB",
           "BAM", "SZL", "GNF", "NZD", "SYP", "MKD", "BZD", "KWD", "SLL", "ETB", "BYN", "AZN", "XPF", "BBD", "CDF",
           "RWF", "SOS", "BDT", "ILS", "EGP", "IQD", "RON", "COP", "SEK", "MMK", "SAR", "DJF", "HTG", "PKR",
           "GTQ", "BYR", "PHP", "TOP", "TND", "VEF", "PEN", "CVE", "NIO", "HUF", "SCR", "THB", "FJD", "MRO",
           "AOA", "XAF", "BOB", "KZT", "LSL", "TMT", "HRK", "BGN", "LVL", "OMR", "MYR", "VUV", "KES", "XCD", "ARS", "GBP",
           "SDG", "MUR", "VND", "MNT", "GMD", "BSD", "HKD", "GIP", "PGK", "KGS", "LYD", "CAD", "BWP", "IDR", "LRD",
           "JPY", "NAD", "MVR", "ISK", "PAB", "AMD", "BHD", "NOK", "SRD", "IRR", "GYD", "TWD", "ZMW", "XOF",
           "MWK", "KMF", "KRW", "TZS", "LTL", "DKK", "HNL", "AUD", "MAD", "CRC", "MDL", "TRY", "LBP", "INR", "CLP", "GHS",
           "NGN", "SBD", "LKR", "BIF", "CHF", "DOP", "YER", "PLN", "TJS", "CZK", "MXN", "WST", "UGX", "SVC",
           "SGD", "PYG", "JOD", "AFN", "NPR", "ANG", "QAR", "USD", "ERN", "CUP", "MOP", "CNY", "TTD", "KHR", "DZD",
           "UZS", "EUR", "AED", "UYU", "MZN", "BRL"};

   @BeforeAll
   static void setUp() {
      converterService = new ConverterService();
   }

   /**
    * Function test popular currencies in Ukraine.
    *
    * @throws CurrencyConverterException if currency does not support.
    * @throws IOException if has problem with internet connection.
    */
   @Test void convertPopularCurrencies() throws CurrencyConverterException, IOException {
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
   }

   /**
    * Test currency which can convert to USD. This list was taken by google.com
    *
    * @throws CurrencyConverterException if currency does not support.
    * @throws IOException if has problem with internet connection.
    */
   @Test void checkCurrencyTransferToUSD() throws CurrencyConverterException, IOException {
      checkConvert("BYR", "USD");
      checkConvert("PAB", "USD");
      checkConvert("LVL", "USD");
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
      checkConvert("BTN", "USD");
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
      checkConvert("BMD", "USD");
      checkConvert("SAR", "USD");
      checkConvert("LSL", "USD");
      checkConvert("NGN", "USD");
      checkConvert("GNF", "USD");
      checkConvert("AED", "USD");
      checkConvert("ALL", "USD");
      checkConvert("KYD", "USD");
      checkConvert("STD", "USD");
      checkConvert("CRC", "USD");
      checkConvert("BDT", "USD");
      checkConvert("SGD", "USD");
      checkConvert("KPW", "USD");
      checkConvert("MUR", "USD");
      checkConvert("ILS", "USD");
      checkConvert("UGX", "USD");
      checkConvert("SHP", "USD");
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
      checkConvert("FKP", "USD");
      checkConvert("UZS", "USD");
      checkConvert("GHS", "USD");
      checkConvert("BAM", "USD");
      checkConvert("ERN", "USD");
      checkConvert("MGA", "USD");
      checkConvert("LAK", "USD");
      checkConvert("TJS", "USD");
      checkConvert("KRW", "USD");
      checkConvert("LTL", "USD");
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
   }


   @Test
   void checkCurrencyTransferFromUSD() throws IOException, CurrencyConverterException {
      checkConvert("USD", "BYR");
      checkConvert("USD", "PAB");
      checkConvert("USD", "LVL");
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
      checkConvert("USD", "BTN");
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
      checkConvert("USD", "BMD");
      checkConvert("USD", "SAR");
      checkConvert("USD", "LSL");
      checkConvert("USD", "NGN");
      checkConvert("USD", "GNF");
      checkConvert("USD", "AED");
      checkConvert("USD", "ALL");
      checkConvert("USD", "KYD");
      checkConvert("USD", "STD");
      checkConvert("USD", "CRC");
      checkConvert("USD", "BDT");
      checkConvert("USD", "SGD");
      checkConvert("USD", "KPW");
      checkConvert("USD", "MUR");
      checkConvert("USD", "ILS");
      checkConvert("USD", "UGX");
      checkConvert("USD", "SHP");
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
      checkConvert("USD", "FKP");
      checkConvert("USD", "UZS");
      checkConvert("USD", "GHS");
      checkConvert("USD", "BAM");
      checkConvert("USD", "ERN");
      checkConvert("USD", "MGA");
      checkConvert("USD", "LAK");
      checkConvert("USD", "TJS");
      checkConvert("USD", "KRW");
      checkConvert("USD", "LTL");
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
   }

   @Test
   void checkCurrencyTransferToUAH() throws IOException, CurrencyConverterException {
      checkConvert("BTN", "UAH");
      checkConvert("BMD", "UAH");
      checkConvert("KYD", "UAH");
      checkConvert("STD", "UAH");
      checkConvert("KPW", "UAH");
      checkConvert("SHP", "UAH");
      checkConvert("MGA", "UAH");
      checkConvert("RSD", "UAH");
   }

   @Test
   void checkCurrencyTransferFromUAH() throws IOException, CurrencyConverterException {
      checkConvert("UAH", "BTN");
      checkConvert("UAH", "BMD");
      checkConvert("UAH", "KYD");
      checkConvert("UAH", "STD");
      checkConvert("UAH", "KPW");
      checkConvert("UAH", "SHP");
      checkConvert("UAH", "MGA");
      checkConvert("UAH", "RSD");
   }
   /**
    * Test currency which cannot be converted to USD.
    */
   @Test void checkUnsupportedCurrency() {
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
   }

   @Test
   void checkIdenticalCurrency() throws IOException, CurrencyConverterException {
      for (Currency currency : Currency.getAvailableCurrencies()) {
         checkConvertForIdenticalCurrencies(currency.getCurrencyCode(), new Random().nextFloat());
      }
   }

   @Test
   @Disabled("this test takes 2 hours 25 min. ")
   void checkConversionsWithAllPossibleCurrencies() throws IOException, CurrencyConverterException {
      for (int i = 0; i < existingCurrency.length; i++) {
         for (int j = i; j < existingCurrency.length; j++) {
            checkConvert(existingCurrency[i], existingCurrency[j]);
         }
      }
   }

   private void checkConvert(String userCurrency, String desiredCurrency) throws CurrencyConverterException, IOException {
      Converter converter = new Converter(Currency.getInstance(userCurrency),
              Currency.getInstance(desiredCurrency),
              1f);

      Float value = converterService.convert(converter);
      assertNotNull(value);
   }

   private void checkNonConvertibility(String userCurrency, String desiredCurrency) {
      try {
         checkConvert(userCurrency, desiredCurrency);
      } catch (IOException e) {
         // if problem with internet connection was occur
      } catch (CurrencyConverterException e) {
         assertTrue(isRightMessage(e));
      }
   }

   private boolean isRightMessage(CurrencyConverterException e) {
      return e.getMessage().contains(MESSAGE_UNSUPPORTED_CURRENCY) || e.getMessage().contains(API_MESSAGE_WITH_ONE_UNSUPPORTED_CURRENCY);
   }

   private void checkConvertForIdenticalCurrencies(String userCurrency, Float expectedValue) throws CurrencyConverterException, IOException {
      Converter converter = new Converter(Currency.getInstance(userCurrency),
              Currency.getInstance(userCurrency),
              expectedValue);

      assertEquals(expectedValue, converterService.convert(converter));
   }
}