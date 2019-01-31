package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.Currency;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

/**
 * Class for conversion currencies by free.currencyapi.com
 *
 * @author Dmytro K.
 */
public class FreeCurrencyConverterApiCom implements ConverterAPI {

   /** API name. */
   private static final String API_NAME_FREE_CURRENCYAPI_COM = "free.currencyapi.com";

   /** URL for connection to API. */
   private static final String URL_FREE_CURRENCY_CONVERTER_API_COM = "http://free.currencyconverterapi.com/api/v5/convert?q=%s_%s&compact=y";

   @Override
   public BigDecimal convert(Currency from, Currency to, BigDecimal value) throws IOException {
      URL url = buildURL(URL_FREE_CURRENCY_CONVERTER_API_COM, from, to);

      JSONObject object = getJsonObjectByURL(url);

      double one = object.getJSONObject(from + "_" + to).getDouble("val");

      writeToLog(API_NAME_FREE_CURRENCYAPI_COM, from, to, value);
      return convertByOne(value, (float) one);
   }
}
