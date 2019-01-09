package com.implemica.CurrencyConverter.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is part of user's conversation with bot. Stores date, information about user, their request and bot's response
 */
@Data
public class Dialog implements Serializable {

   private String usersRequest;

   private String botsResponse;

   private User user;

   @JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy hh:mm:ss")
   private Date date;

   /**
    * Date format
    */
   @JsonIgnore
   private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

   public Dialog(Date date, User user, String usersRequest, String botsResponse) {
      this.usersRequest = usersRequest;
      this.botsResponse = botsResponse;
      this.user = user;
      this.date = date;
   }

   public Dialog() {
   }

   /**Needed to write to .csv file*/
   public String[] toCsv() {
      return new String[]{df.format(date), Integer.toString(user.getUserId()), user.getUserFirstName(), user.getUserLastName()
              , user.getUserName(), usersRequest, botsResponse};
   }


   @Override
   public boolean equals(Object tr) {
      if (this == tr) {
         return true;
      }
      if (tr instanceof Dialog) {
         return this.date.equals(((Dialog) tr).date) && this.user.equals(((Dialog) tr).user) &&
                 this.usersRequest.equals(((Dialog) tr).usersRequest) &&
                 this.botsResponse.equals(((Dialog) tr).botsResponse);
      }
      return false;

   }
}
