package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.Converter;
import com.tunyk.currencyconverter.BankUaCom;
import com.tunyk.currencyconverter.api.Currency;
import com.tunyk.currencyconverter.api.CurrencyConverter;
import com.tunyk.currencyconverter.api.CurrencyConverterException;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

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
      Currency usersCurrency = Currency.fromString(converter.getUsersCurrency().getCurrencyCode());
      Currency desiredCurrency = Currency.fromString(converter.getDesiredCurrency().getCurrencyCode());

      CurrencyConverter currencyConverter = new BankUaCom(usersCurrency, desiredCurrency);
      return currencyConverter.convertCurrency(converter.getValue());
   }

   private Float convertByJavaMoney(Converter converter) {
      MonetaryAmount userMoney = Monetary.getDefaultAmountFactory()
              .setCurrency(converter.getUsersCurrency().getCurrencyCode())
              .setNumber(converter.getValue()).create();

      CurrencyConversion conversion = MonetaryConversions.getConversion(converter.getDesiredCurrency().getCurrencyCode());
      MonetaryAmount convertedAmountUSDtoEUR = userMoney.with(conversion);

      return convertedAmountUSDtoEUR.getNumber().floatValue();
   }

   private Float convertByBingCom(Converter converter) {
//      read JSON from https://finance.services.appex.bing.com/Market.svc/ChartDataV5?symbols=245.20.RUBUAHLITE&chartType=1y&prime=true
//       take take the arithmetic mean
//      multiply it and return
      return null;
   }
}
