package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.Currency;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.math.BigDecimal;

public class JavaMoney implements ConverterAPI {

   private static final String API_NAME_JAVA_MONEY = "Java money api";

   /**
    * Converts by java money api.
    *
    * @return result of conversion.
    * @param from currency to convert from
    * @param to currency for conversion to
    * @param value value for conversion.
    */
   @Override public BigDecimal convert(Currency from, Currency to, BigDecimal value) {
      MonetaryAmount userMoney = Monetary.getDefaultAmountFactory()
              .setCurrency(from.getCurrencyCode())
              .setNumber(1.0f).create();

      CurrencyConversion conversion = MonetaryConversions.getConversion(to.getCurrencyCode());
      MonetaryAmount converted = userMoney.with(conversion);

      Float one = converted.getNumber().floatValue();

      writeToLog(API_NAME_JAVA_MONEY, from, to, value);
      return convertByOne(value, one);
   }
}
