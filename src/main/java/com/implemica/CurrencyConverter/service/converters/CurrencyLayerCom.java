package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.Currency;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

public class CurrencyLayerCom implements ConverterAPI {

   private static final String URL_CURRENCY_LAYER_COM = "http://apilayer.net/api/live?access_key=f91895130d9f009b167cd5299cdd923c&source=%s&currencies=%s&format=1";
   private static final String API_NAME_CURRENCYLAYER_COM = "currencylayer.com";


   /**
    * Function connects to currencylayer.com, gets json and parse it.
    *
    * @param from currency to convert from
    * @param to currency for conversion to
    * @param value value for conversion.
    * @throws IOException if didn't parse a json
    * @return result of conversion.
    */
   @Override public BigDecimal convert(Currency from, Currency to, BigDecimal value) throws IOException {
      URL url = buildURL(URL_CURRENCY_LAYER_COM, from, to);

      JSONObject object = getJsonObjectByURL(url);

      double one = object.getJSONObject("quotes").getDouble(from + "" + to);

      writeToLog(API_NAME_CURRENCYLAYER_COM, from, to, value);
      return convertByOne(value, (float) one);
   }
}
