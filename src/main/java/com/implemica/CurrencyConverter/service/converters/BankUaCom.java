package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.Currency;
import com.tunyk.currencyconverter.api.CurrencyConverter;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import com.tunyk.currencyconverter.api.CurrencyNotSupportedException;

import java.math.BigDecimal;

/**
 * Class for conversion currencies by bank-ua.com
 *
 * @author Dmytro K.
 */
public class BankUaCom implements ConverterAPI {

   /** Api name. */
   private static final String API_NAME_BANK_UA_COM = "bank-ua.com";

   @Override
   public BigDecimal convert(Currency from, Currency to, BigDecimal value) throws CurrencyConverterException {
      com.tunyk.currencyconverter.api.Currency usersCurrency = getCurrencyByUtilCurrency(from);
      com.tunyk.currencyconverter.api.Currency desiredCurrency = getCurrencyByUtilCurrency(to);

      CurrencyConverter currencyConverter = new com.tunyk.currencyconverter.BankUaCom(usersCurrency, desiredCurrency);

      Float one = currencyConverter.convertCurrency(1.0f);

      writeToLog(API_NAME_BANK_UA_COM, from, to, value);
      return convertByOne(value, one);
   }

   /**
    * Function converts {@link java.util.Currency} to {@link Currency}
    *
    * @param currency contains currencies and value for conversion.
    * @return instance of {@link Currency}
    * @throws CurrencyNotSupportedException if currency does not support.
    */
   private com.tunyk.currencyconverter.api.Currency getCurrencyByUtilCurrency(Currency currency) throws CurrencyNotSupportedException {
      return com.tunyk.currencyconverter.api.Currency.fromString(currency.name());
   }
}
