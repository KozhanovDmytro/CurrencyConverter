package com.implemica.CurrencyConverter.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @version 31.01.2019 16.44
 */
@Getter
public class Dialog implements Serializable {

   /**
    * User's message to bot.
    */
   private String usersRequest;

   /**
    * Bot's reaction for user's message.
    */
   private String botsResponse;

   /**
    * Represents a Telegram user.
    */
   private User user;

   /**
    * Date and time of dialog.
    */
   @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+2")
   private Date date;

   /**
    * Date format.
    */
   private static SimpleDateFormat df = BotService.SIMPLE_DATE_FORMAT;

   /**
    * String format for writing to line
    */
   public static final String PATTERN_FOR_LINE = "%s;%s;%s;%s;%s;%s;%s;\n";

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
    * @param user         represents User, who refers to bot
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
    * Creates line of all information of one request to bot from one user
    *
    * @return array with information about date, User, user's request to bot and bot's response to user
    */
   public String toLine() {
      botsResponse = botsResponse.replaceAll("\n", "");
      return String.format(PATTERN_FOR_LINE, df.format(date), user.getUserId(), user.getUserFirstName(),
              user.getUserLastName(), user.getUserName(), usersRequest, botsResponse);
   }

}
