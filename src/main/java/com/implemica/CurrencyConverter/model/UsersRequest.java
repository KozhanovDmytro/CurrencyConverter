package com.implemica.CurrencyConverter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.knowm.xchange.currency.Currency;

import java.math.BigDecimal;

/**
 * Class for conversion currencies.
 *
 * @author Dmytro K.
 * @version 08.01.2019 05:03
 */
@Getter
@AllArgsConstructor
public class UsersRequest {

   /** User's currency which will be converted. */
   private Currency currencyFrom;

   /** Desired currency in which the conversion will be made. */
   private Currency currencyTo;

   /** Amount of {@link #currencyFrom} */
   private BigDecimal value;

   /**
    * Returns the string of information of conversion.
    *
    * @return information of conversion
    */
   @Override
   public String toString() {
      return currencyFrom.getCurrencyCode() + " -> " +
              currencyTo.getCurrencyCode() + " value: " +
              value;
   }
}
