package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.Currency;
import com.implemica.CurrencyConverter.service.converters.*;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
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
 * @see URL
 * @see JSONObject
 *
 * @author Dmytro K.
 * @version 10.01.2019 22:10
 */
@Service
public class ConverterService {

   /** Logger. */
   private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

   /** The list stores links to functions which make conversion. */
   private static List<ConverterAPI> converters = new ArrayList<>();

   /* constants */
   /** Time for connection to server in ms. */
   private static final int TIMEOUT_FOR_CONNECTION = 3000;

   /** Official site Google comp. */
   private static final String URL_GOOGLE_COM = "www.google.com";

   /** Message for problem with internet connection. */
   private static final String MESSAGE_PROBLEM_WITH_INTERNET_CONNECTION = "Problem with internet connection.";

   /** Message for problem with server. */
   private static final String MESSAGE_PROBLEM_WITH_SERVER = "Server did not respond. Try again.";

   /** Message exception was thrown. */
   private static final String MESSAGE_EXCEPTION_WAS_THROWN = "exception was thrown: ";

   /** Message unsupported currency. */
   private static final String MESSAGE_UNSUPPORTED_CURRENCY = "One or two currencies not supported.";

   /** Message which was received from server. */
   private static final String API_MESSAGE_WITH_ONE_UNSUPPORTED_CURRENCY = "Currency not supported:";

   /*
    * Initialization scope for converters. Note! In this
    * order will be convert currency.
    */
   static {
      converters.add(new FloatRatesCom());                  // unlimited
      converters.add(new BankUaCom());                      // unlimited, converts through UAH

      converters.add(new FreeCurrencyConverterApiCom());    // has a limit - 100  requests per hour
      converters.add(new CurrencyLayerCom());               // has a limit - 1000 requests per month

      converters.add(new JavaMoney());                      // unlimited, so slow
   }

   /**
    * Function converts  currency from {@link UsersRequest#currencyFrom} to
    * {@link UsersRequest#currencyTo}.
    *
    * Firstly function  checks  internet connection if this false then it
    * throws  {@link UnknownHostException},  then  it  calls all the APIs
    * that are presented in  the {@link #converters}, if any API was able
    * to  convert the currency,  the function returns the result,  if all
    * APIs  could  not  convert  the  currency,  the  function  throws an
    * exception.
    *
    * @param from currency to convert from
    * @param to currency for conversion to
    * @param value value for conversion.
    * @return converted value.
    * @throws CurrencyConverterException if currency does not support.
    * @throws UnknownHostException if there is no internet connection.
    */
   public BigDecimal convert(Currency from, Currency to, BigDecimal value)
           throws CurrencyConverterException, UnknownHostException {
      if(from == to) {
         return value;
      }

      if(isValueZero(value)) {
         return BigDecimal.ZERO;
      }

      if(!isInternetConnectionExist()) {
         logger.error(MESSAGE_PROBLEM_WITH_INTERNET_CONNECTION);
         throw new UnknownHostException(MESSAGE_PROBLEM_WITH_INTERNET_CONNECTION);
      }

      BigDecimal result = null;
      ArrayList<Exception> exceptions = new ArrayList<>();

      for (ConverterAPI option : converters) {
         try {
            result = option.convert(from, to, value);
            break;
         } catch (Exception e) {
            exceptions.add(e);
         }
      }

      return analyzeResult(exceptions, result);
   }

   private boolean isValueZero(BigDecimal value) {
      return value.compareTo(BigDecimal.ZERO) == 0;
   }

   /**
    * Function checks Internet connection.
    *
    * @return if site google.com is reachable or not.
    */
   private boolean isInternetConnectionExist() {
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
    * or was not, if it false that it throws an exception.
    *
    * @param exceptions exceptions, which could be thrown
    * @param result result of conversion
    * @throws CurrencyConverterException if result wasn't returned from APIs
    * @return result of conversion.
    */
   private BigDecimal analyzeResult(ArrayList<Exception> exceptions, BigDecimal result) throws CurrencyConverterException {
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
}