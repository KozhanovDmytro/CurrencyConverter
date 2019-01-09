package com.implemica.CurrencyConverter.model;


import lombok.Data;

import java.io.Serializable;

/**
 * Information about Telegram user
 *
 * @author Daria S.
 */
@Data
public class User implements Serializable {
   /**
    * Unique identifier for this user
    */
   private int userId;
   /**
    * User's first name
    */
   private String userFirstName;
   /**
    * User's last name
    */
   private String userLastName = "";
   /**
    * User's username
    */
   private String userName = "";

   /**
    * Constructs User, which didn't indicate their last name and username
    *
    * @param userId        user's unique identifier
    * @param userFirstName user's first name
    */
   public User(int userId, String userFirstName) {
      this.userId = userId;
      this.userFirstName = userFirstName;
   }

   /**
    * Constructs User, with all information
    *
    * @param userId        user's unique identifier
    * @param userFirstName user's first name
    * @param userLastName  user's last name
    * @param userName      user's username
    */
   public User(int userId, String userFirstName, String userLastName, String userName) {
      this.userId = userId;
      this.userName = userName;
      this.userFirstName = userFirstName;
      this.userLastName = userLastName;

   }

   /**
    * Constructs simple user without any information
    */
    private User() {
   }

   /**
    * Returns a string with information about user.
    *
    * @return string of user
    */
   @Override
   public String toString() {
      return userId + " " + userFirstName + " " + userLastName + " " + userName;
   }

   /**
    * Compares this object to the specified object.
    *
    * @return true if the objects are the same; false otherwise.
    */
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
