package com.implemica.CurrencyConverter.validator;


import java.math.BigDecimal;
import java.math.RoundingMode;
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
    *
    * @param string string, all letters of which is has to be in the upper case
    * @return the String, converted to uppercase.
    */
   public static String toUpperCase(String string) {
      string = string.trim();
      return string.toUpperCase();
   }

   /**
    * Parses given number to Float value
    *
    * @param value value, which has to be converted to Float value
    * @return Float number of given String
    * @throws ParseException if value is not a number
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

      setFormatter(separator, false);

      return (BigDecimal) DECIMAL_FORMATTER.parse(value);
   }

   /**
    * Formats number to String
    *
    * @param number float number, which has to be converted to String
    * @return the String of given number
    */
   public static String formatNumber(BigDecimal number) {
      setFormatter(POINT, true);

      return DECIMAL_FORMATTER.format(number);
   }

   /**
    * Sets parameters for decimal formatter
    *
    * @param separator    the character used for decimal sign
    * @param needRounding true, if rounding is needed
    */
   private static void setFormatter(char separator, boolean needRounding) {
      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setDecimalSeparator(separator);

      DECIMAL_FORMATTER.setDecimalFormatSymbols(symbols);

      if (needRounding) {
         DECIMAL_FORMATTER.setRoundingMode(RoundingMode.HALF_UP);

      } else {
         DECIMAL_FORMATTER.setParseBigDecimal(true);
      }
   }
}
