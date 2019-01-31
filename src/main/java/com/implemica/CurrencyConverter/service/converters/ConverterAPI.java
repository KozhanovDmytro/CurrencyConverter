package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.Currency;
import com.implemica.CurrencyConverter.model.UsersRequest;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An interface which supplies function of currency conversion.
 */
public interface ConverterAPI {

   /** Logger. */
   Logger logger = LoggerFactory.getLogger("ConverterAPI");

   /** Format line in logs. */
   String STRING_FORMAT_FOR_LOGS = "converted by %s: %s -> %s value: %s";

   /**
    * Function for convert currencies.
    *
    * @param from currency to convert from
    * @param to currency for conversion to
    * @param value value for conversion.
    * @return converted value.
    * @throws CurrencyConverterException if currency does not support.
    * @throws IOException if there is no internet connection.
    */
   BigDecimal convert(Currency from, Currency to, BigDecimal value) throws Exception;

   /**
    * Function to white to log.
    */
   default void writeToLog(String api, Currency from, Currency to, BigDecimal value) {
      logger.info(String.format(STRING_FORMAT_FOR_LOGS, api, from, to, value));
   }

   /**
    * The function multiplies the cost for 1 unit of the desired
    * currency by the amount that the user wants to receive.
    *
    * @param value user's value
    * @param one the cost for 1 unit of the desired currency
    * @return result
    */
   default BigDecimal convertByOne(BigDecimal value, Float one) {
      return value.multiply(new BigDecimal(one));
   }

   /**
    * Gets a json object by url.
    *
    * @param url address
    * @return an instance of {@link JSONObject}
    * @throws IOException if an I/O exception occurs.
    */
   default JSONObject getJsonObjectByURL(URL url) throws IOException {
      InputStream inputStream = url.openStream();

      JSONTokener tokener = new JSONTokener(inputStream);

      JSONObject result = new JSONObject(tokener);

      inputStream.close();
      return result;
   }

   /**
    * Function for build URL.
    *
    * @param path url as string
    * @param from currency to convert from
    * @param to currency for conversion to
    * @return An instance of URL.
    * @throws MalformedURLException if no protocol is specified, or an
    *               unknown protocol is found.
    */
   default URL buildURL(String path, Currency from, Currency to) throws MalformedURLException {
      String url = String.format(path, from, to);
      return new URL(url);
   }
}
