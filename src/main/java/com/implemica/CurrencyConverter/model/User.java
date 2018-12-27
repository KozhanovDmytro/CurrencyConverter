package com.implemica.CurrencyConverter.model;


import lombok.Data;

/**
 * Information about user
 *
 * @author Daria S.
 */
@Data
public class User {
   private int userId;
   private String userFirstName;
   private String userLastName = "";
   private String userName = "";

   /**
    * Constructs User, which didn't indicate their last name and userName
    */
   public User(int userId, String userFirstName) {
      this.userId = userId;
      this.userFirstName = userFirstName;
   }

   /**
    * Constructs User, with all information
    */
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

   @Override
   public boolean equals(Object us) {
      if (this == us) {
         return true;
      }
      if (us instanceof User) {
         return this.userId == ((User) us).userId && this.userFirstName.equals(((User) us).userFirstName)
                 && this.userLastName.equals(((User) us).userLastName) && this.userName.equals(((User) us).userName);
      }
      return false;
   }
}
