package com.implemica.CurrencyConverter.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.implemica.CurrencyConverter.service.BotService;
import lombok.Getter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is part of user's conversation with bot. Stores date,
 * information about user, their request and bot's response
 *
 * @author Daria S.
 * @author Dmytro K.
 * @version 09.01.2019 14.48
 */
@Getter
public class Dialog implements Serializable {
   /**
    * User's message to bot
    */
   private String usersRequest;
   /**
    * Bot's reaction for user's message
    */
   private String botsResponse;
   /**
    * Represents a Telegram user
    */
   private User user;

   /**
    * Date and time of dialog
    */
   @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+2")
   private Date date;

   /**
    * Date format
    */
   @JsonIgnore
   private SimpleDateFormat df = BotService.SIMPLE_DATE_FORMAT;

   /**
    * Creates new simple Dialog
    */
   private Dialog() {
   }

   /**
    * Creates new Dialog, that represents information about user,
    * their request to bot, bot's response for it and date and time of
    * conversation.
    *
    * @param date         date and time of users request
    * @param user         represents User, which refers to bot
    * @param usersRequest message, which was sent from user to bot
    * @param botsResponse message, which was sent from bot to user
    */
   public Dialog(Date date, User user, String usersRequest, String botsResponse) {
      this.usersRequest = usersRequest;
      this.botsResponse = botsResponse;
      this.user = user;
      this.date = date;
   }

   /**
    * Line of all information of one request to bot from one user
    * @return array with information about date, User, user's request to bot and bot's response to user
    */
   public String[] toCsv() {
      return new String[]{df.format(date), Integer.toString(user.getUserId()), user.getUserFirstName(), user.getUserLastName()
              , user.getUserName(), usersRequest, botsResponse};
   }

   /**
    * Compares this object to the specified object.
    *
    * @return true if the objects are the same; false otherwise.
    */
   @Override
   public boolean equals(Object dialog) {
      if (this == dialog) {
         return true;
      }
      if (dialog instanceof Dialog) {
         return this.date.equals(((Dialog) dialog).date) && this.user.equals(((Dialog) dialog).user) &&
                 this.usersRequest.equals(((Dialog) dialog).usersRequest) &&
                 this.botsResponse.equals(((Dialog) dialog).botsResponse);
      }
      return false;
   }
}
