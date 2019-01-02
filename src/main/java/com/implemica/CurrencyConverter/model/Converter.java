package com.implemica.CurrencyConverter.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Currency;

/**
 * Class for convert currencies.
 *
 * @author Dmytro K.
 * @version 25.12.2018 18:00
 */
@Data @AllArgsConstructor
public class Converter {

   private Currency usersCurrency;

   private Currency desiredCurrency;

   private float value;

}
