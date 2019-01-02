package com.implemica.CurrencyConverter.validator;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class change user's currency value to upper case and skip spaces in it.
 *
 * @author Daria S.
 */
public class BotValidator {

   /**
    * These values are needed for parsing Float number
    */
   private static final DecimalFormat DF = new DecimalFormat("#.##");
   private static final char COMMA = ',';
   private static final char POINT = '.';
   private static final Pattern hasComma = Pattern.compile(".*,.*");


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

   /**
    * Parses given number to Float value
    *
    * @param value value, which has to be converted to Float value
    * @throws ParseException if value is not a number
    */
   public static Float parseNumber(String value) throws ParseException {
      Matcher matcher = hasComma.matcher(value);
      char separator;
      if (matcher.matches()) {
         separator = COMMA;
      } else {
         separator = POINT;
      }
      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setDecimalSeparator(separator);
      DF.setDecimalFormatSymbols(symbols);
      return DF.parse(value).floatValue();
   }


   public static String formatNumber(Float number) {
      DecimalFormatSymbols s = new DecimalFormatSymbols();
      s.setDecimalSeparator(POINT);
      DF.setDecimalFormatSymbols(s);
      return DF.format(number);
   }
}
