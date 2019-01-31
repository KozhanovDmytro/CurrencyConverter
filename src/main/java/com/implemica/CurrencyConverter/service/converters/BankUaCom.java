package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.UsersRequest;
import com.tunyk.currencyconverter.api.Currency;
import com.tunyk.currencyconverter.api.CurrencyConverter;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import com.tunyk.currencyconverter.api.CurrencyNotSupportedException;

import java.math.BigDecimal;

public class BankUaCom implements ConverterAPI {

   private static final String API_NAME_BANK_UA_COM = "bank-ua.com";

   /**
    * Converts by bank-ua.com API.
    *
    * @param usersRequest contains currencies and value for conversion.
    * @throws CurrencyConverterException if currency does not support.
    * @return result of conversion.
    */
   @Override public BigDecimal convert(UsersRequest usersRequest) throws CurrencyConverterException {
      Currency usersCurrency = getCurrencyByUtilCurrency(usersRequest.getCurrencyFrom());
      Currency desiredCurrency = getCurrencyByUtilCurrency(usersRequest.getCurrencyTo());

      CurrencyConverter currencyConverter = new com.tunyk.currencyconverter.BankUaCom(usersCurrency, desiredCurrency);

      Float one = currencyConverter.convertCurrency(1.0f);

      writeToLog(API_NAME_BANK_UA_COM, usersRequest);
      return convertByOne(usersRequest, one);
   }

   /**
    * Function converts {@link java.util.Currency} to {@link Currency}
    *
    * @param currency contains currencies and value for conversion.
    * @return instance of {@link Currency}
    * @throws CurrencyNotSupportedException if currency does not support.
    */
   private Currency getCurrencyByUtilCurrency(com.implemica.CurrencyConverter.model.Currency currency) throws CurrencyNotSupportedException {
      return Currency.fromString(currency.getCurrencyCode());
   }

}
