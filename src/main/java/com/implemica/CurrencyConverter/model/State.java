package com.implemica.CurrencyConverter.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is represents state of dialog. This is necessary so that each user has his own dialogue with the bot,
 * regardless of other statesOfUsers
 *
 * @author Daria S.
 */
@Data
public class State {
   /**
    * Stores firstCurrency, which was entered by user
    */
   private String firstCurrency;
   /**
    * Stores secondCurrency, which was entered by user
    */
   private String secondCurrency;
   /**
    * Stores step of conversion
    */
   private int step;

   /**
    * Creates a state of dialog
    *
    * @param firstCurrency  state of {@link com.implemica.CurrencyConverter.service.BotService#firstCurrency}
    * @param secondCurrency state of {@link com.implemica.CurrencyConverter.service.BotService#secondCurrency}
    * @param step           state of {@link com.implemica.CurrencyConverter.service.BotService#convertStep}
    */
   public State(String firstCurrency, String secondCurrency, int step) {
      this.firstCurrency = firstCurrency;
      this.secondCurrency = secondCurrency;
      this.step = step;
   }


   /**
    * Stores all statesOfUsers, which use bot and their last command
    */
   public static Map<User, State> statesOfUsers = new HashMap<>();


}