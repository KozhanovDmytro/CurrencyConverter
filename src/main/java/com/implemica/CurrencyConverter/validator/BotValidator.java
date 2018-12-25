package com.implemica.CurrencyConverter.validator;


/**
 * This class change user's currency value to upper case and skip spaces in it.
 *
 * @author daria solodkova
 */
public class BotValidator {

   /**
    * Check, that given string is float value
    *
    * @param value String, which is needed to check
    * @return true, if value is a float initialized to the value represented by the specified String;
    * false - if the string does not contain a parsable float
    */
   public static boolean isCorrectNumber(String value) {
      float number;
      try {
         number = Float.parseFloat(value);
      } catch (NumberFormatException e) {
         return false;
      }
      return number >= 0.0f;
   }

   /**
    * Converts all of the characters in given String to upper case
    */
   public static String toUpperCase(String string) {
      string = skipSpaces(string);
      return string.toUpperCase();
   }

   /**
    * Skips all spaces in given String
    */
   private static String skipSpaces(String string) {
      return string.replaceAll("\\s+", "");
   }
}
