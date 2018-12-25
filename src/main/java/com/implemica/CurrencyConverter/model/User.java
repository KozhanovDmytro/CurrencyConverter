package com.implemica.CurrencyConverter.model;

import java.util.Currency;

public class User {
   private int userId;
   private String userName = "";
   private String userFirstName;
   private String userLastName = "";

   public User(int userId, String userFirstName) {
      this.userId = userId;
      this.userFirstName = userFirstName;
   }

   public User(int userId, String userFirstName, String userLastName) {
      this.userId = userId;
      this.userFirstName = userFirstName;
      this.userLastName = userLastName;
   }

   public User(int userId, String userName, String userFirstName, String userLastName) {
      this.userId = userId;
      this.userName = userName;
      this.userFirstName = userFirstName;
      this.userLastName = userLastName;
   }

   @Override
   public String toString() {
      return userId + " " + userFirstName + " " + userLastName + " " + userName;
   }
}
