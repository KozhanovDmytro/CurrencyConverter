package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.UsersRequest;
import com.tunyk.currencyconverter.BankUaCom;
import com.tunyk.currencyconverter.api.Currency;
import com.tunyk.currencyconverter.api.CurrencyConverter;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import com.tunyk.currencyconverter.api.CurrencyNotSupportedException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The class for conversion currency.
 *
 * This class uses 5 APIs for currency conversion :
 *    1. bank-ua.com
 *    2. floatrates.com
 *    3. javamoney.org
 *    4. free.currencyconverterapi.com
 *    5. currencylayer.com
 *
 *
 * @see UsersRequest
 * @see Currency
 * @see CurrencyConverter
 * @see URL
 * @see JSONObject
 *
 * @author Dmytro K.
 * @version 10.01.2019 22:10
 */
@Service
public final class ConverterService {

   /** Logger. */
   private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

   /** The list stores links to functions which make conversion. */
   private List<ConverterAPI> options = new ArrayList<>();

   /*
    * Initialization scope for options. Note! In this
    * order will be convert currency.
    */
   {
      options.add(this::convertByBankUaCom);                      // unlimited
      options.add(this::convertByFloatRatesCom);                  // unlimited

      options.add(this::convertByFreeCurrencyConverterApiCom);    // has a limit - 100  requests per hour
      options.add(this::convertByCurrencyLayerCom);               // has a limit - 1000 requests per month

//      options.add(this::convertByJavaMoney);                      // unlimited, so slow
   }

   /**
    * An interface which supplies function of currency conversion.
    */
   private interface ConverterAPI {
      /**
       * Contains the function which can convert currency
       * by {@link UsersRequest}
       *
       * @param usersRequest contains currencies and value for conversion.
       * @return converted value.
       * @throws CurrencyConverterException if currency does not support.
       * @throws IOException if there is no internet connection.
       */
      Float convert(UsersRequest usersRequest) throws Exception;
   }

   /**
    * Function converts  currency from {@link UsersRequest#currencyFrom} to
    * {@link UsersRequest#currencyTo}.
    *
    * Firstly function  checks  internet connection if this false then it
    * throws  {@link UnknownHostException},  then  it  calls all the APIs
    * that are presented in the {@link #options}, if any API was able
    * to  convert the currency,  the function returns the result,  if all
    * APIs  could  not  convert  the  currency,  the  function  throws an
    * exception.
    *
    * @param usersRequest contains currencies and value for conversion.
    * @return converted value.
    * @throws CurrencyConverterException if currency does not support.
    * @throws UnknownHostException if there is no internet connection.
    */
   public Float convert(UsersRequest usersRequest) throws CurrencyConverterException, UnknownHostException {
      if(!checkConnection()) {
         logger.error(MESSAGE_PROBLEM_WITH_INTERNET_CONNECTION);
         throw new UnknownHostException(MESSAGE_PROBLEM_WITH_INTERNET_CONNECTION);
      }

      Float result = null;
      ArrayList<Exception> exceptions = new ArrayList<>();

      for (ConverterAPI option : options) {
         try {
            result = option.convert(usersRequest);
            break;
         } catch (Exception e) {
            exceptions.add(e);
         }
      }

      return analyzeResult(exceptions, result);
   }

   /**
    * Function checks connection.
    *
    * @return if site google.com is reachable or not.
    */
   private boolean checkConnection() {
      boolean result;

      try {
         InetAddress connection = InetAddress.getByName(URL_GOOGLE_COM);
         result = connection.isReachable(TIMEOUT_FOR_CONNECTION);
      } catch (IOException e) {
         result = false;
      }

      return result;
   }

   /**
    * Function makes a decision - result was returned from APIs
    * or was not returned, if it false that it throws an exception.
    *
    * @param exceptions exceptions, which could be thrown
    * @param result result of conversion
    * @throws CurrencyConverterException if result wasn't returned from APIs
    * @return result of conversion.
    */
   private Float analyzeResult(ArrayList<Exception> exceptions, Float result) throws CurrencyConverterException {
      if(result == null) {
         logger.error(MESSAGE_EXCEPTION_WAS_THROWN + Arrays.toString(exceptions.toArray()));
         throw new CurrencyConverterException(getExceptionMessage(exceptions));
      } else {
         return result;
      }
   }

   /**
    * The function analyzes the exceptions that were thrown in several APIs
    * and decides which currency is not supported. If it is not possible to
    * decide (which currency is not supported) the message will be returned
    * by default.
    *
    * @param exceptions exceptions which was thrown in APIs
    * @return exception message
    */
   private String getExceptionMessage(ArrayList<Exception> exceptions) {
      String message = MESSAGE_UNSUPPORTED_CURRENCY;
      for (Exception e : exceptions) {
         if(e instanceof ConnectException) {
            message = MESSAGE_PROBLEM_WITH_SERVER;
            break;
         } else if(isValidMessage(e)) {
            message = e.getMessage();
         }
      }

      return message;
   }

   /**
    * Function define if exception has valid message, which
    * possible to return to user, or not.
    *
    * @param e exception
    * @return possibility to return to user exception message.
    */
   private boolean isValidMessage(Exception e) {
      return e instanceof CurrencyConverterException && e.getMessage().contains(API_MESSAGE_WITH_ONE_UNSUPPORTED_CURRENCY);
   }

   /**
    * Converts by bank-ua.com API.
    *
    * @param usersRequest contains currencies and value for conversion.
    * @throws CurrencyConverterException if currency does not support.
    * @return result of conversion.
    */
   Float convertByBankUaCom(UsersRequest usersRequest) throws CurrencyConverterException {
      Currency usersCurrency = getCurrencyByUtilCurrency(usersRequest.getCurrencyFrom());
      Currency desiredCurrency = getCurrencyByUtilCurrency(usersRequest.getCurrencyTo());

      CurrencyConverter currencyConverter = new BankUaCom(usersCurrency, desiredCurrency);

      Float result = currencyConverter.convertCurrency(usersRequest.getValue());

      writeToLog(API_NAME_BANK_UA_COM, usersRequest);
      return result;
   }

   /**
    * Converts by java money api.
    *
    * @return result of conversion.
    * @param usersRequest contains currencies and value for conversion.
    */
   Float convertByJavaMoney(UsersRequest usersRequest) {
      MonetaryAmount userMoney = Monetary.getDefaultAmountFactory()
              .setCurrency(usersRequest.getCurrencyFrom().getCurrencyCode())
              .setNumber(usersRequest.getValue()).create();

      CurrencyConversion conversion = MonetaryConversions.getConversion(usersRequest.getCurrencyTo().getCurrencyCode());
      MonetaryAmount converted = userMoney.with(conversion);

      Float result = converted.getNumber().floatValue();

      writeToLog(API_NAME_JAVA_MONEY, usersRequest);
      return result;
   }

   /**
    * Function connects to free.currencyapi.com, gets json and parse it.
    *
    * @param usersRequest contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    * @return result of conversion.
    */
   Float convertByFreeCurrencyConverterApiCom(UsersRequest usersRequest) throws IOException {
      URL url = buildURL(URL_FREE_CURRENCY_CONVERTER_API_COM, usersRequest);

      JSONObject object = getJsonObjectByURL(url);

      double value = object.getJSONObject(usersRequest.getCurrencyFrom() + "_" + usersRequest.getCurrencyTo())
              .getDouble("val");

      Float result = usersRequest.getValue() * (float) value;

      writeToLog(API_NAME_FREE_CURRENCYAPI_COM, usersRequest);
      return result;
   }

   /**
    * Function connects to currencylayer.com, gets json and parse it.
    *
    * @param usersRequest contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    * @return result of conversion.
    */
   Float convertByCurrencyLayerCom(UsersRequest usersRequest) throws IOException {
      URL url = buildURL(URL_CURRENCY_LAYER_COM, usersRequest);

      JSONObject object = getJsonObjectByURL(url);

      double value = object.getJSONObject("quotes")
              .getDouble(usersRequest.getCurrencyFrom() + "" + usersRequest.getCurrencyTo());

      Float result = usersRequest.getValue() * (float) value;

      writeToLog(API_NAME_CURRENCYLAYER_COM, usersRequest);
      return result;
   }

   /**
    * Function connects to floatrates.com, gets json and parse it.
    *
    * @param usersRequest contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    * @return result of conversion.
    */
   Float convertByFloatRatesCom(UsersRequest usersRequest) throws IOException {
      String url = String.format(URL_FLOAT_RATES_COM, usersRequest.getCurrencyFrom());

      JSONObject object = getJsonObjectByURL(new URL(url));

      String desiredCurrency = usersRequest.getCurrencyTo().getCurrencyCode();

      double rate = object.getJSONObject(desiredCurrency.toLowerCase())
              .getDouble("rate");

      Float result = usersRequest.getValue() * (float) rate;

      writeToLog(API_NAME_FLOATRATES_COM, usersRequest);
      return result;
   }

   /**
    * Function converts {@link java.util.Currency} to {@link Currency}
    *
    * @param currency contains currencies and value for conversion.
    * @return instance of {@link Currency}
    * @throws CurrencyNotSupportedException if currency does not support.
    */
   private Currency getCurrencyByUtilCurrency(java.util.Currency currency) throws CurrencyNotSupportedException {
      return Currency.fromString(currency.getCurrencyCode());
   }

   /**
    * Gets a json object by url.
    *
    * @param url address
    * @return an instance of {@link JSONObject}
    * @throws IOException if an I/O exception occurs.
    */
   private JSONObject getJsonObjectByURL(URL url) throws IOException {
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
   private URL buildURL(String path, UsersRequest usersRequest) throws MalformedURLException {
      String url = String.format(path, usersRequest.getCurrencyFrom(), usersRequest.getCurrencyTo());
      return new URL(url);
   }
   
   private void writeToLog(String api, UsersRequest usersRequest) {
      logger.info("converted by " + api + ": " + usersRequest);
   }

   /* constants */

   private static final int TIMEOUT_FOR_CONNECTION = 3000;

   private static final String URL_FREE_CURRENCY_CONVERTER_API_COM = "http://free.currencyconverterapi.com/api/v5/convert?q=%s_%s&compact=y";
   private static final String URL_CURRENCY_LAYER_COM = "http://apilayer.net/api/live?access_key=f91895130d9f009b167cd5299cdd923c&source=%s&currencies=%s&format=1";
   private static final String URL_FLOAT_RATES_COM = "http://www.floatrates.com/daily/%s.json";
   private static final String URL_GOOGLE_COM = "www.google.com";

   private static final String API_NAME_BANK_UA_COM = "bank-ua.com";
   private static final String API_NAME_JAVA_MONEY = "Java money api";
   private static final String API_NAME_FREE_CURRENCYAPI_COM = "free.currencyapi.com";
   private static final String API_NAME_CURRENCYLAYER_COM = "currencylayer.com";
   private static final String API_NAME_FLOATRATES_COM = "floatrates.com";

   private static final String MESSAGE_PROBLEM_WITH_INTERNET_CONNECTION = "Problem with internet connection.";
   private static final String MESSAGE_PROBLEM_WITH_SERVER = "Server did not respond. Try again.";
   private static final String MESSAGE_EXCEPTION_WAS_THROWN = "exception was thrown: ";
   private static final String MESSAGE_UNSUPPORTED_CURRENCY = "One or two currencies not supported.";

   private static final String API_MESSAGE_WITH_ONE_UNSUPPORTED_CURRENCY = "Currency not supported:";
}