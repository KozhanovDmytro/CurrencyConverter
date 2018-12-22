package com.implemica.CurrencyConverter.model;


import com.tunyk.currencyconverter.BankUaCom;
import com.tunyk.currencyconverter.api.Currency;
import com.tunyk.currencyconverter.api.CurrencyConverter;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import lombok.Data;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

@Data
public class Converter {

   private String usersCurrency;

   private String desiredCurrency;

   private Float value;

   public Float convert(String usersCurrency, String desiredCurrency, Float value) {
      this.usersCurrency = usersCurrency;
      this.desiredCurrency = desiredCurrency;
      this.value = value;

      Float result;
      try {
         result = convertByBankUaCom();
      } catch (CurrencyConverterException e) {
         result = convertByJavaMoney();
      }

      return result;
   }

   private Float convertByBankUaCom() throws CurrencyConverterException {
      Currency usersCurrency = Currency.fromString(this.usersCurrency);
      Currency desiredCurrency = Currency.fromString(this.desiredCurrency);

      CurrencyConverter currencyConverter = new BankUaCom(usersCurrency, desiredCurrency);
      return currencyConverter.convertCurrency(value);
   }

   private Float convertByJavaMoney() {
      MonetaryAmount oneDollar = Monetary.getDefaultAmountFactory().setCurrency(usersCurrency)
              .setNumber(value).create();

      CurrencyConversion conversionEUR = MonetaryConversions.getConversion(desiredCurrency);

      MonetaryAmount convertedAmountUSDtoEUR = oneDollar.with(conversionEUR);

      return convertedAmountUSDtoEUR.getNumber().floatValue();
   }
}
