package com.implemica.CurrencyConverter.model;

import lombok.Data;

@Data
public class Transaction {

   private String usersCurrency;

   private String desiredCurrency;

   private User user;

   public Transaction(Converter converter, User user) {
      this.usersCurrency = converter.getUsersCurrency();
      this.desiredCurrency = converter.getDesiredCurrency();
      this.user = user;
   }
}
