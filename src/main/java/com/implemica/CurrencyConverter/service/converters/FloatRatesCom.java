package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.UsersRequest;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FloatRatesCom implements ConverterAPI {

   private static final String URL_FLOAT_RATES_COM = "http://www.floatrates.com/daily/%s.json";
   private static final String API_NAME_FLOATRATES_COM = "floatrates.com";
   private static final String DATE_FORMAT_FOR_FLOAT_RATES_API = "E, d MMM yyyy HH:mm:ss Z";
   private static final double TWO_WEEKS = 1.21e9;


   /**
    * Function connects to floatrates.com, gets json and parse it.
    *
    * @param usersRequest contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    * @return result of conversion.
    */
   @Override public BigDecimal convert(UsersRequest usersRequest) throws Exception {
      String url = String.format(URL_FLOAT_RATES_COM, usersRequest.getCurrencyFrom());

      JSONObject object = getJsonObjectByURL(new URL(url));

      String currencyTo = usersRequest.getCurrencyTo().getCurrencyCode();

      JSONObject desiredCurrency = object.getJSONObject(currencyTo.toLowerCase());

      try {
         checkLatestInfoForFloatRatesAPI(desiredCurrency);
      } catch (ParseException e) {
         logger.error(e.getMessage());
      }

      double one = object.getJSONObject(currencyTo.toLowerCase())
              .getDouble("rate");

      writeToLog(API_NAME_FLOATRATES_COM, usersRequest);
      return convertByOne(usersRequest, (float) one);
   }


   /**
    * The function checks the data received from the API, which contain
    * the date when the currencies were updated. If the data is old or
    * SimpleDateFormat is outdated, the function throws an exception.
    *
    * @param object received data with date.
    * @throws CurrencyConverterException if data is old.
    */
   private void checkLatestInfoForFloatRatesAPI(JSONObject object) throws CurrencyConverterException, ParseException {
      SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_FOR_FLOAT_RATES_API, Locale.ENGLISH);
      Date update = sdf.parse(object.getString("date"));
      Date today = new Date();

      if (today.getTime() - update.getTime() > TWO_WEEKS) {
         throw new CurrencyConverterException("This info is old.");
      }
   }
}
