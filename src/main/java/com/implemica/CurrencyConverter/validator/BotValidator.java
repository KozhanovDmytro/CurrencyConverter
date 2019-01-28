package com.implemica.CurrencyConverter.validator;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class change user's currency value to upper case and skip spaces in it. Also it validates amount of currency
 *
 * @author Daria S.
 */
public class BotValidator {

   /**
    * Format for amount of currency
    */
   private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("#.##");
   /**
    * Separator of amount value
    */
   private static final char COMMA = ',';
   /**
    * Separator of amount value
    */
   private static final char POINT = '.';
   /**
    * Regular expression, which matches, that string contains the comma
    */
   private static final String COMMA_REGEX = ".*,.*";
   /**
    * Regular expression, which matches, that string contains only the positive number
    */
   private static final String POSITIVE_NUMBER_REGEX = "^\\d+([.,])?\\d*$";
   /**
    * Defines the regular expression, which is needed to check that string has a comma
    */
   private static final Pattern hasComma = Pattern.compile(COMMA_REGEX);
   /**
    * Defines the regular expression, which is needed to check that string is positive number
    */
   private static final Pattern isPositiveNumber = Pattern.compile(POSITIVE_NUMBER_REGEX);


   /**
    * Converts all of the characters in given String to upper case.
    * Calls {@link #skipSpaces(String)} to skip spaces
    *
    * @param string string, all letters of which is has to be in the upper case
    * @return the String, converted to uppercase.
    */
   public static String toUpperCase(String string) {
      string = skipSpaces(string);
      return string.toUpperCase();
   }

   /**
    * Skips all spaces in given String
    *
    * @param string string, which can contain some spaces
    * @return the String without spaces
    */
   private static String skipSpaces(String string) {
      return string.trim();
   }

   /**
    * Parses given number to Float value
    *
    * @param value value, which has to be converted to Float value
    * @throws ParseException if value is not a number
    * @return Float number of given String
    */
   public static BigDecimal parseNumber(String value) throws ParseException {
      Matcher matcher = isPositiveNumber.matcher(value);
      if (!matcher.matches()) {
         throw new ParseException("Invalid number: " + value, 0);
      }
      matcher = hasComma.matcher(value);
      char separator;
      if (matcher.matches()) {
         separator = COMMA;
      } else {
         separator = POINT;
      }
      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setDecimalSeparator(separator);
      DECIMAL_FORMATTER.setDecimalFormatSymbols(symbols);
      DECIMAL_FORMATTER.setParseBigDecimal(true);
      return (BigDecimal) DECIMAL_FORMATTER.parse(value);
   }

   /**
    * Formats number to String
    *
    * @param number float number, which has to be converted to String
    * @return the String of given number
    */
   public static String formatNumber(BigDecimal number) {
      DecimalFormatSymbols s = new DecimalFormatSymbols();
      s.setDecimalSeparator(POINT);
      DECIMAL_FORMATTER.setDecimalFormatSymbols(s);
      return DECIMAL_FORMATTER.format(number);
   }
}
