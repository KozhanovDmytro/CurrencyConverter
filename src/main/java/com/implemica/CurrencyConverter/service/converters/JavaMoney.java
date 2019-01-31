package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.UsersRequest;

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
    * @param usersRequest contains currencies and value for conversion.
    */
   @Override public BigDecimal convert(UsersRequest usersRequest) throws Exception {
      MonetaryAmount userMoney = Monetary.getDefaultAmountFactory()
              .setCurrency(usersRequest.getCurrencyFrom().getCurrencyCode())
              .setNumber(1.0f).create();

      CurrencyConversion conversion = MonetaryConversions.getConversion(usersRequest.getCurrencyTo().getCurrencyCode());
      MonetaryAmount converted = userMoney.with(conversion);

      Float one = converted.getNumber().floatValue();

      writeToLog(API_NAME_JAVA_MONEY, usersRequest);
      return convertByOne(usersRequest, one);
   }
}
