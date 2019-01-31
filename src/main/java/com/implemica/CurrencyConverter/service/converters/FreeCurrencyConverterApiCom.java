package com.implemica.CurrencyConverter.service.converters;

import com.implemica.CurrencyConverter.model.UsersRequest;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

public class FreeCurrencyConverterApiCom implements ConverterAPI {

   private static final String API_NAME_FREE_CURRENCYAPI_COM = "free.currencyapi.com";
   private static final String URL_FREE_CURRENCY_CONVERTER_API_COM = "http://free.currencyconverterapi.com/api/v5/convert?q=%s_%s&compact=y";

   /**
    * Function connects to free.currencyapi.com, gets json and parse it.
    *
    * @param usersRequest contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    * @return result of conversion.
    */
   @Override public BigDecimal convert(UsersRequest usersRequest) throws Exception {
      URL url = buildURL(URL_FREE_CURRENCY_CONVERTER_API_COM, usersRequest);

      JSONObject object = getJsonObjectByURL(url);

      double one = object.getJSONObject(usersRequest.getCurrencyFrom() + "_" + usersRequest.getCurrencyTo())
              .getDouble("val");

      writeToLog(API_NAME_FREE_CURRENCYAPI_COM, usersRequest);
      return convertByOne(usersRequest, (float) one);
   }





}
