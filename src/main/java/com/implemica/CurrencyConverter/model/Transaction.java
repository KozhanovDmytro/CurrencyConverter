package com.implemica.CurrencyConverter.model;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is part of user's conversation with bot. Stores date, information about user, their request and bot's response
 */
@Data
public class Transaction {

   private String usersRequest;

   private String botsResponse;

   private User user;

   private Date date;

   /**
    * Date format
    */
   SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss:SS");

   public Transaction(Date date, User user, String usersRequest, String botsResponse) {
      this.usersRequest = usersRequest;
      this.botsResponse = botsResponse;
      this.user = user;
      this.date = date;
   }

   /**Needed to write to .csv file*/
   public String[] toCsv() {
      return new String[]{df.format(date), Integer.toString(user.getUserId()), user.getUserFirstName(), user.getUserLastName()
              , user.getUserName(), usersRequest, botsResponse};
   }
}
