package com.implemica.CurrencyConverter.model;

import com.implemica.CurrencyConverter.service.ConverterService;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConverterTest {

   private static ConverterService converterService;

   private static String[] existingCurrency = new String[] {"LAK", "UAH", "AWG", "GEL", "ALL", "ZAR", "BND", "JMD", "RUB", "BAM", "SZL", "ITL", "GNF", "NZD", "SYP", "MKD", "BZD", "KWD", "SLL", "ETB", "BYN", "AZN", "XPF", "ZMK", "BBD", "CDF", "RWF", "SOS", "BDT", "ILS", "EGP", "CUC", "BTN", "IQD", "RON", "COP", "SEK", "MMK", "SAR", "MXV", "DJF", "HTG", "PKR", "SHP", "KYD", "GTQ", "BYR", "PHP", "TOP", "TND", "VEF", "PEN", "CVE", "NIO", "HUF", "SCR", "THB", "FJD", "MRO", "AOA", "XAF", "BOB", "KZT", "LSL", "TMT", "HRK", "BGN", "LVL", "OMR", "MYR", "VUV", "KES", "XCD", "ARS", "GBP", "SDG", "MUR", "VND", "FRF", "MNT", "GMD", "BSD", "HKD", "GIP", "PGK", "KGS", "LYD", "CAD", "BWP", "IDR", "ZWL", "LRD", "JPY", "NAD", "CLF", "MVR", "ISK", "PAB", "AMD", "BHD", "NOK", "SRD", "KPW", "IRR", "GYD", "TWD", "FKP", "ZMW", "XOF", "MWK", "KMF", "KRW", "TZS", "LTL", "DKK", "HNL", "AUD", "MAD", "CRC", "MDL", "TRY", "LBP", "INR", "CLP", "GHS", "BMD", "XAU", "NGN", "SBD", "LKR", "BIF", "CHF", "DOP", "YER", "PLN", "TJS", "CZK", "MXN", "WST", "UGX", "SVC", "SGD", "XDR", "PYG", "JOD", "AFN", "NPR", "ANG", "QAR", "USD", "ERN", "CUP", "MOP", "CNY", "TTD", "KHR", "DZD", "UZS", "EUR", "AED", "XPD", "UYU", "MZN", "BRL"};

   @BeforeAll
   static void setUp() {
      converterService = new ConverterService();
   }

   @Test
   void convert() throws CurrencyConverterException {
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
   void checkCurrencyTransferToUSD() throws CurrencyConverterException {
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
   void convertWith() throws CurrencyConverterException {
      for (String curr1 : existingCurrency) {
         for (String curr2 : existingCurrency) {
            checkConvert(curr1, curr2);
         }
      }
   }

   @Test
   void checkSupport() {
      ArrayList<Currency> arrayList = new ArrayList<>();
      for (Currency currency : Currency.getAvailableCurrencies()) {
         try {
            checkConvert(currency.getCurrencyCode(), "USD");
            arrayList.add(currency);
         } catch (CurrencyConverterException e) {

         }
      }
      System.out.println(arrayList.size());
      System.out.println(Arrays.toString(arrayList.toArray()));
   }

   private void checkConvert(String userCurrency, String desiredCurrency) throws CurrencyConverterException {
      Converter converter = new Converter(Currency.getInstance(userCurrency),
                                          Currency.getInstance(desiredCurrency),
                                          1f);

      Float value = converterService.convert(converter);
      assertNotNull(value);
   }
}