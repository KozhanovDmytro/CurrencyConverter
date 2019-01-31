package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.Currency;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class for conversion currencies by floatrates.com
 *
 * @author Dmytro K.
 */
public class FloatRatesCom implements ConverterAPI {

   /** Date format needed for getting data from JSON object. */
   private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_FOR_FLOAT_RATES_API, Locale.ENGLISH);

   /** API name. */
   private static final String API_NAME_FLOATRATES_COM = "floatrates.com";

   /** URL for connection to API. */
   private static final String URL_FLOAT_RATES_COM = "http://www.floatrates.com/daily/%s.json";

   /** Date format in JSON object. */
   private static final String DATE_FORMAT_FOR_FLOAT_RATES_API = "E, d MMM yyyy HH:mm:ss Z";

   /** Two weeks in milliseconds. */
   private static final long TWO_WEEKS = DateUtils.MILLIS_PER_DAY * 14;

   @Override
   public BigDecimal convert(Currency from, Currency to, BigDecimal value) throws CurrencyConverterException, IOException {
      String url = String.format(URL_FLOAT_RATES_COM, from);

      JSONObject object = getJsonObjectByURL(new URL(url));

      String currencyTo = to.getCurrencyCode();

      JSONObject desiredCurrency = object.getJSONObject(currencyTo.toLowerCase());

      try {
         checkLatestInfoForFloatRatesAPI(desiredCurrency);
      } catch (ParseException e) {
         logger.error(e.getMessage());
      }

      double one = object.getJSONObject(currencyTo.toLowerCase())
              .getDouble("rate");

      writeToLog(API_NAME_FLOATRATES_COM, from, to, value);
      return convertByOne(value, (float) one);
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
      Date update = sdf.parse(object.getString("date"));
      Date today = new Date();

      if (today.getTime() - update.getTime() > TWO_WEEKS) {
         throw new CurrencyConverterException("This info is old.");
      }
   }
}
