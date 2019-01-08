package com.implemica.CurrencyConverter.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Currency;

/**
 * Class for convert currencies.
 *
 * @author Dmytro K.
 * @version 08.01.2019 05:03
 */
@Data
@AllArgsConstructor
public class Converter {

   /** User's money which will be converted. */
   private Currency usersCurrency;

   /** Desired currency in which the conversion will be made */
   private Currency desiredCurrency;

   /** Amount of {@link this#usersCurrency}*/
   private float value;

   @Override public String toString() {
      return   usersCurrency.getCurrencyCode() + " -> " +
               desiredCurrency.getCurrencyCode() + " value: " +
               value;
   }
}
