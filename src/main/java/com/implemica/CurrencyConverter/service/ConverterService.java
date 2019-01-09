package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.Converter;
import com.tunyk.currencyconverter.BankUaCom;
import com.tunyk.currencyconverter.api.Currency;
import com.tunyk.currencyconverter.api.CurrencyConverter;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import com.tunyk.currencyconverter.api.CurrencyNotSupportedException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * @see Converter
 * @see Currency
 * @see CurrencyConverter
 * @see URL
 * @see JSONObject
 *
 * @author Dmytro K.
 * @version 08.01.2019 05:01
 */
@Service
public final class ConverterService {

   /** Logger. */
   private final Logger logger = Logger.getLogger(this.getClass().getName());

   /** The value which will be returned to user (result). */
   private Float convertedValue;

   /** The list stores exceptions which was thrown by some API. */
   private List<Exception> exceptions = new ArrayList<>();

   /** The list stores links to functions which make conversion. */
   private List<OptionSupplier> options = new ArrayList<>();

   /*
    * Initialization scope for options. Note! In this
    * order will be convert currency.
    */
   {
      options.add(this::convertByBankUaCom);                      // unlimited
      options.add(this::convertByFloatRatesCom);                  // unlimited
      options.add(this::convertByJavaMoney);                      // unlimited, but so slow

      options.add(this::convertByFreeCurrencyConverterApiCom);    // has a limit - 100  requests per hour
      options.add(this::convertByCurrencyLayerCom);               // has a limit - 1000 requests per month
   }

   /**
    * An interface where stores function for currency conversion.
    */
   private interface OptionSupplier {
      void execute(Converter converter) throws Exception;
   }

   /**
    * The function which will be converted to some currency.
    *
    * @param converter contains currencies and value for conversion.
    * @return converted value.
    * @throws CurrencyConverterException if currency does not support.
    * @throws UnknownHostException if there is no internet connection.
    */
   public Float convert(Converter converter) throws CurrencyConverterException, UnknownHostException {
      if(!checkConnection()) {
         logger.log(Level.SEVERE, MESSAGE_PROBLEM_WITH_INTERNET_CONNECTION);
         throw new UnknownHostException(MESSAGE_PROBLEM_WITH_INTERNET_CONNECTION);
      }

      for (OptionSupplier option : options) {
         if(handleException(option, converter)) {
            return convertedValue;
         }
      }

      logger.log(Level.SEVERE, MESSAGE_EXCEPTION_WAS_THROWN + Arrays.toString(exceptions.toArray()));
      throw new CurrencyConverterException(analyzeException());
   }

   /**
    * Function check connection.
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
    * Function handles exception.
    *
    * @param optionSupplier contains function
    * @param converter contains currencies and value for conversion.
    * @return if exception was not thrown or not.
    */
   private boolean handleException(OptionSupplier optionSupplier, Converter converter) {
      boolean result = true;

      try {
         optionSupplier.execute(converter);
      } catch(Exception e) {
         exceptions.add(e);
         result = false;
      }

      return result;
   }

   /**
    * Function read {@link this#exceptions} list and makes a decision which type of
    * exception was happen.
    *
    * @return analyzed exception.
    */
   private String analyzeException() {
      String result = MESSAGE_UNKNOWN_ERROR;
      for (Throwable e : exceptions) {
         if (e instanceof IOException) {
            logger.log(Level.SEVERE, e.getMessage());
            result = MESSAGE_UNSUPPORTED_CURRENCY;
         } else if (e instanceof CurrencyNotSupportedException) {
            logger.log(Level.SEVERE, e.getMessage());
            result = e.getMessage();
         }
      }

      return result;
   }

   /**
    * Convert by bank-ua.com API.
    *
    * @param converter contains currencies and value for conversion.
    * @throws CurrencyConverterException if currency does not support.
    */
   private void convertByBankUaCom(Converter converter) throws CurrencyConverterException {
      Currency usersCurrency = getCurrencyByUtilCurrency(converter.getUsersCurrency());
      Currency desiredCurrency = getCurrencyByUtilCurrency(converter.getDesiredCurrency());

      CurrencyConverter currencyConverter = new BankUaCom(usersCurrency, desiredCurrency);
      convertedValue = currencyConverter.convertCurrency(converter.getValue());

      writeToLog(API_NAME_BANK_UA_COM, converter);
   }

   /**
    * Function convert {@link java.util.Currency} to {@link Currency}
    * 
    * @param currency contains currencies and value for conversion.
    * @return instance of {@link Currency}
    * @throws CurrencyNotSupportedException if currency does not support. 
    */
   private Currency getCurrencyByUtilCurrency(java.util.Currency currency) throws CurrencyNotSupportedException {
      return Currency.fromString(currency.getCurrencyCode());
   }

   /**
    * Convert by java money api. 
    * 
    * @param converter contains currencies and value for conversion.
    */
   private void convertByJavaMoney(Converter converter) {
      MonetaryAmount userMoney = Monetary.getDefaultAmountFactory()
              .setCurrency(converter.getUsersCurrency().getCurrencyCode())
              .setNumber(converter.getValue()).create();

      CurrencyConversion conversion = MonetaryConversions.getConversion(converter.getDesiredCurrency().getCurrencyCode());
      MonetaryAmount converted = userMoney.with(conversion);

      convertedValue = converted.getNumber().floatValue();

      writeToLog(API_NAME_JAVA_MONEY, converter);
   }

   /**
    * Function connects to free.currencyapi.com, gets json and parse it.
    * 
    * @param converter contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    */
   private void convertByFreeCurrencyConverterApiCom(Converter converter) throws IOException {
      URL url = buildURL(URL_FREE_CURRENCY_CONVERTER_API_COM, converter);

      JSONObject object = getJsonObjectByURL(url);

      double value = object.getJSONObject(converter.getUsersCurrency() + "_" + converter.getDesiredCurrency())
                     .getDouble("val");

      convertedValue = converter.getValue() * (float) value;

      writeToLog(API_NAME_FREE_CURRENCYAPI_COM, converter);
   }

   /**
    * Function connects to currencylayer.com, gets json and parse it.
    * 
    * @param converter contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    */
   private void convertByCurrencyLayerCom(Converter converter) throws IOException {
      URL url = buildURL(URL_CURRENCY_LAYER_COM, converter);

      JSONObject object = getJsonObjectByURL(url);

      double value = object.getJSONObject("quotes")
                           .getDouble(converter.getUsersCurrency() + "" + converter.getDesiredCurrency());

      convertedValue = converter.getValue() * (float) value;

      writeToLog(API_NAME_CURRENCYLAYER_COM, converter);
   }

   /**
    * Function connects to floatrates.com, gets json and parse it.
    * 
    * @param converter contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    */
   private void convertByFloatRatesCom(Converter converter) throws IOException {
      String url = String.format(URL_FLOAT_RATES_COM, converter.getUsersCurrency());

      JSONObject object = getJsonObjectByURL(new URL(url));

      String desiredCurrency = converter.getDesiredCurrency().getCurrencyCode();

      double rate = object.getJSONObject(desiredCurrency.toLowerCase()).getDouble("rate");

      convertedValue = (float) rate * converter.getValue();

      writeToLog(API_NAME_FLOATRATES_COM, converter);
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
    * @param converter contains currencies and value for conversion.
    * @return An instance of URL. 
    * @throws MalformedURLException if no protocol is specified, or an
    *               unknown protocol is found.
    */
   private static URL buildURL(String path, Converter converter) throws MalformedURLException {
      String url = String.format(path, converter.getUsersCurrency(), converter.getDesiredCurrency());
      return new URL(url);
   }
   
   private void writeToLog(String api, Converter converter) {
      logger.log(Level.INFO, "converted by " + api + ": " + converter + " result: " + convertedValue);
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
   private static final String MESSAGE_EXCEPTION_WAS_THROWN = "exception was thrown: ";
   private static final String MESSAGE_UNKNOWN_ERROR = "Unknown error!";
   private static final String MESSAGE_UNSUPPORTED_CURRENCY = "One or two currencies not supported.";
}