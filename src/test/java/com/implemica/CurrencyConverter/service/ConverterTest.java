package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.Converter;
import com.implemica.CurrencyConverter.service.ConverterService;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConverterTest {

   private static ConverterService converterService;

   private static Converter testData;

   private static String[] existingCurrency = new String[] {/*"LAK", "UAH", "AWG", "GEL", "ALL", "ZAR", "BND", "JMD", "RUB",*/
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
      testData = new Converter(Currency.getInstance("USD"), Currency.getInstance("EUR"), 1.0f);
   }

   @Test
   void convert() throws CurrencyConverterException, IOException {
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

   @Test
   void checkCurrencyTransferToUSD() throws CurrencyConverterException, IOException {
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
   void checkUnsupportedCurrency() {
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
         float randomValue = new Random().nextFloat();
         checkConvertForIdenticalCurrencies(currency.getCurrencyCode(), randomValue);
      }
   }

   @Test
   @Disabled("this test takes 2 hours 25 min. ")
   void checkConversionsWithAllPossibleCurrencies() throws IOException, CurrencyConverterException {
      ArrayList<CurrencyConverterException> list = new ArrayList<>();
      for (int i = 0; i < existingCurrency.length; i++) {
         if(existingCurrency[i].equals("CDF")) {
            break;
         }
         for (int j = i; j < existingCurrency.length; j++) {
            checkConvert(existingCurrency[i], existingCurrency[j]);
         }
      }
      System.out.println(Arrays.toString(list.toArray()));
   }

   @Test
   @Disabled
   void checkSupport() {
      ArrayList<Currency> arrayList = new ArrayList<>();
      for (Currency currency : Currency.getAvailableCurrencies()) {
         try {
            checkConvert(currency.getCurrencyCode(), "USD");
            arrayList.add(currency);
         } catch (CurrencyConverterException | IOException e) {

         }
      }
      System.out.println(arrayList.size());
      System.out.println(Arrays.toString(arrayList.toArray()));
   }

   private void checkConvert(String userCurrency, String desiredCurrency) throws CurrencyConverterException, IOException {
      Converter converter = new Converter(Currency.getInstance(userCurrency),
                                          Currency.getInstance(desiredCurrency),
                                          1f);

      Float value = converterService.convert(converter);
      assertNotNull(value);
   }

   private void checkNonConvertibility(String userCurrency, String desiredCurrency) {
      assertThrows(CurrencyConverterException.class, () -> checkConvert(userCurrency, desiredCurrency));
   }

   private void checkConvertForIdenticalCurrencies(String userCurrency, float expectedValue) throws CurrencyConverterException, IOException {
      Converter converter = new Converter(Currency.getInstance(userCurrency),
              Currency.getInstance(userCurrency),
              expectedValue);

      assertEquals(expectedValue, converterService.convert(converter));
   }
}