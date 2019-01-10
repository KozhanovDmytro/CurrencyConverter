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
    * An interface which supplies function of currency conversion.
    */
   private interface OptionSupplier {
      Float execute(Converter converter) throws CurrencyConverterException, IOException;
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

      Float result = null;
      ArrayList<Exception> exceptions = new ArrayList<>();

      for (OptionSupplier option : options) {
         try {
            result = option.execute(converter);
            break;
         } catch (Exception e) {
            logger.log(Level.SEVERE, MESSAGE_EXCEPTION_WAS_THROWN + e.getMessage());
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
    * Function makes a decision result was returned from APIs
    * or not. If it false that it throws an exception.
    *
    * @throws CurrencyConverterException if result wasn't returned from APIs
    * @return result of conversion.
    */
   private Float analyzeResult(ArrayList<Exception> exceptions, Float result) throws CurrencyConverterException {
      if(result == null) {
         throw new CurrencyConverterException(getExceptionMessage(exceptions));
      } else {
         return result;
      }
   }

   /**
    * Function analyze exceptions which was thrown in several APIs and makes decision
    * which currency does not support.
    *
    * @param exceptions exceptions which was thrown in APIs
    * @return exception message
    */
   private String getExceptionMessage(ArrayList<Exception> exceptions) {
      String message = MESSAGE_UNSUPPORTED_CURRENCY;
      for (Exception e : exceptions) {
         if(e instanceof CurrencyConverterException) {
            message = e.getMessage();
         }
      }

      return message;
   }

   /**
    * Converts by bank-ua.com API.
    *
    * @param converter contains currencies and value for conversion.
    * @throws CurrencyConverterException if currency does not support.
    * @return result of conversion.
    */
   private Float convertByBankUaCom(Converter converter) throws CurrencyConverterException {
      Currency usersCurrency = getCurrencyByUtilCurrency(converter.getUsersCurrency());
      Currency desiredCurrency = getCurrencyByUtilCurrency(converter.getDesiredCurrency());

      CurrencyConverter currencyConverter = new BankUaCom(usersCurrency, desiredCurrency);
      writeToLog(API_NAME_BANK_UA_COM, converter);

      return currencyConverter.convertCurrency(converter.getValue());
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
    * Converts by java money api.
    *
    * @return result of conversion.
    * @param converter contains currencies and value for conversion.
    */
   private Float convertByJavaMoney(Converter converter) {
      MonetaryAmount userMoney = Monetary.getDefaultAmountFactory()
              .setCurrency(converter.getUsersCurrency().getCurrencyCode())
              .setNumber(converter.getValue()).create();

      CurrencyConversion conversion = MonetaryConversions.getConversion(converter.getDesiredCurrency().getCurrencyCode());
      MonetaryAmount converted = userMoney.with(conversion);

      writeToLog(API_NAME_JAVA_MONEY, converter);

      return converted.getNumber().floatValue();
   }

   /**
    * Function connects to free.currencyapi.com, gets json and parse it.
    * 
    * @param converter contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    * @return result of conversion.
    */
   private Float convertByFreeCurrencyConverterApiCom(Converter converter) throws IOException {
      URL url = buildURL(URL_FREE_CURRENCY_CONVERTER_API_COM, converter);

      JSONObject object = getJsonObjectByURL(url);

      double value = object.getJSONObject(converter.getUsersCurrency() + "_" + converter.getDesiredCurrency())
                     .getDouble("val");

      writeToLog(API_NAME_FREE_CURRENCYAPI_COM, converter);

      return converter.getValue() * (float) value;
   }

   /**
    * Function connects to currencylayer.com, gets json and parse it.
    * 
    * @param converter contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    * @return result of conversion.
    */
   private Float convertByCurrencyLayerCom(Converter converter) throws IOException {
      URL url = buildURL(URL_CURRENCY_LAYER_COM, converter);

      JSONObject object = getJsonObjectByURL(url);

      double value = object.getJSONObject("quotes")
                           .getDouble(converter.getUsersCurrency() + "" + converter.getDesiredCurrency());

      writeToLog(API_NAME_CURRENCYLAYER_COM, converter);

      return converter.getValue() * (float) value;
   }

   /**
    * Function connects to floatrates.com, gets json and parse it.
    * 
    * @param converter contains currencies and value for conversion.
    * @throws IOException if didn't parse a json
    * @return result of conversion.
    */
   private Float convertByFloatRatesCom(Converter converter) throws IOException {
      String url = String.format(URL_FLOAT_RATES_COM, converter.getUsersCurrency());

      JSONObject object = getJsonObjectByURL(new URL(url));

      String desiredCurrency = converter.getDesiredCurrency().getCurrencyCode();

      double rate = object.getJSONObject(desiredCurrency.toLowerCase()).getDouble("rate");

      writeToLog(API_NAME_FLOATRATES_COM, converter);

      return converter.getValue() * (float) rate;
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
      logger.log(Level.INFO, "converted by " + api + ": " + converter);
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
   private static final String MESSAGE_UNSUPPORTED_CURRENCY = "One or two currencies not supported.";
}