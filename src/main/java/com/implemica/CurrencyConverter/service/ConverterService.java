package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.Converter;
import com.tunyk.currencyconverter.BankUaCom;
import com.tunyk.currencyconverter.api.Currency;
import com.tunyk.currencyconverter.api.CurrencyConverter;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

@Data
public class ConverterService {

   public Float convert(Converter converter) {

      Float result;
      try {
         result = convertByBankUaCom(converter);
      } catch (CurrencyConverterException e) {
         result = convertByJavaMoney(converter);
      }

      return result;
   }

   private Float convertByBankUaCom(Converter converter) throws CurrencyConverterException {
      Currency usersCurrency = Currency.fromString(converter.getUsersCurrency());
      Currency desiredCurrency = Currency.fromString(converter.getDesiredCurrency());

      CurrencyConverter currencyConverter = new BankUaCom(usersCurrency, desiredCurrency);
      return currencyConverter.convertCurrency(converter.getValue());
   }

   private Float convertByJavaMoney(Converter converter) {
      MonetaryAmount userMoney = Monetary.getDefaultAmountFactory()
              .setCurrency(converter.getUsersCurrency())
              .setNumber(converter.getValue()).create();

      CurrencyConversion conversion = MonetaryConversions.getConversion(converter.getDesiredCurrency());
      MonetaryAmount convertedAmountUSDtoEUR = userMoney.with(conversion);

      return convertedAmountUSDtoEUR.getNumber().floatValue();
   }
}
