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

/**
 * Class for convert currencies.
 *
 * @author Dmytro K.
 * @version 25.12.2018 18:00
 */
@Data public class Converter {

   private String usersCurrency;

   private String desiredCurrency;

   private Float value;

   public Converter() {}

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
      MonetaryAmount userMoney = Monetary .getDefaultAmountFactory()
                                          .setCurrency(usersCurrency)
                                          .setNumber(value).create();

      CurrencyConversion conversion = MonetaryConversions.getConversion(desiredCurrency);
      MonetaryAmount convertedAmountUSDtoEUR = userMoney.with(conversion);

      return convertedAmountUSDtoEUR.getNumber().floatValue();
   }
}
