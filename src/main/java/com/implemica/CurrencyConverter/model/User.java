package com.implemica.CurrencyConverter.model;


import lombok.Data;

/**
 * Information about user
 * @author daria solodkova
 */
@Data
public class User {
   private int userId;
   private String userFirstName;
   private String userLastName = "";
   private String userName = "";

   /**Constructs User, which didn't indicate their last name and userName*/
   public User(int userId, String userFirstName) {
      this.userId = userId;
      this.userFirstName = userFirstName;
   }

   /**Constructs User, with all information*/
   public User(int userId, String userFirstName, String userLastName, String userName) {
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
