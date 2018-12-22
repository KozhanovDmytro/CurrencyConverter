package com.implemica.CurrencyConverter.model;

import lombok.Data;

@Data
public class Transaction {

   private Converter converter;

   private User user;

   public Transaction(Converter converter, User user) {
      this.converter = converter;
      this.user = user;
   }

//   TODO write to .csv file

}
