package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.Currency;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

public class FreeCurrencyConverterApiCom implements ConverterAPI {

   private static final String API_NAME_FREE_CURRENCYAPI_COM = "free.currencyapi.com";
   private static final String URL_FREE_CURRENCY_CONVERTER_API_COM = "http://free.currencyconverterapi.com/api/v5/convert?q=%s_%s&compact=y";

   /**
    * Function connects to free.currencyapi.com, gets json and parse it.
    *
    * @param from currency to convert from
    * @param to currency for conversion to
    * @param value value for conversion.
    * @throws IOException if didn't parse a json
    * @return result of conversion.
    */
   @Override public BigDecimal convert(Currency from, Currency to, BigDecimal value) throws IOException {
      URL url = buildURL(URL_FREE_CURRENCY_CONVERTER_API_COM, from, to);

      JSONObject object = getJsonObjectByURL(url);

      double one = object.getJSONObject(from + "_" + to).getDouble("val");

      writeToLog(API_NAME_FREE_CURRENCYAPI_COM, from, to, value);
      return convertByOne(value, (float) one);
   }





}
