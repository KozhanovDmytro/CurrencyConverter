package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.UsersRequest;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An interface which supplies function of currency conversion.
 */
public interface ConverterAPI {

   Logger logger = LoggerFactory.getLogger("ConverterAPI");

   /**
    * Contains the function which can convert currency
    * by {@link UsersRequest}
    *
    * @param usersRequest contains currencies and value for conversion.
    * @return converted value.
    * @throws CurrencyConverterException if currency does not support.
    * @throws IOException if there is no internet connection.
    */
   BigDecimal convert(UsersRequest usersRequest) throws Exception;

   default void writeToLog(String api, UsersRequest usersRequest) {
      logger.info("converted by " + api + ": " + usersRequest);
   }

   default BigDecimal convertByOne(UsersRequest usersRequest, Float one) {
      return usersRequest.getValue().multiply(new BigDecimal(one));
   }

   /**
    * Gets a json object by url.
    *
    * @param url address
    * @return an instance of {@link JSONObject}
    * @throws IOException if an I/O exception occurs.
    */
   default JSONObject getJsonObjectByURL(URL url) throws IOException {
      JSONTokener tokener = new JSONTokener(url.openStream());

      return new JSONObject(tokener);
   }

   /**
    * Function for build URL.
    *
    * @param path url as string
    * @param usersRequest contains currencies and value for conversion.
    * @return An instance of URL.
    * @throws MalformedURLException if no protocol is specified, or an
    *               unknown protocol is found.
    */
   default URL buildURL(String path, UsersRequest usersRequest) throws MalformedURLException {
      String url = String.format(path, usersRequest.getCurrencyFrom(), usersRequest.getCurrencyTo());
      return new URL(url);
   }
}
