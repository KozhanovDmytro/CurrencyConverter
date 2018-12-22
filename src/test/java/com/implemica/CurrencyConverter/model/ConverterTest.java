package com.implemica.CurrencyConverter.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConverterTest {

   private static Converter converter;

   /**
    * Array of existing currency got by ISO 4217
    *
    * link: https://ru.wikipedia.org/wiki/ISO_4217
    */
   private static Set<Currency> existingCurrency;

   @BeforeAll
   public static void setUp() {
      converter = new Converter();
      existingCurrency = Currency.getAvailableCurrencies();
   }

   @Test
   public void convert() {
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
   public void supportedCurrency() {
      int count = 0;
      for (Currency userCurrency : existingCurrency) {
         try {
            converter.convert("USD", userCurrency.toString(), 1f);
            count++;
         } catch (Exception ignored) {}
      }
      System.out.println(count);
   }

   private void checkConvert(String userCurrency, String desiredCurrency) {
      Float value = converter.convert(userCurrency, desiredCurrency, 1f);
      assertNotNull(value);
      System.out.println(userCurrency + " to " + desiredCurrency + " " + value);
   }
}