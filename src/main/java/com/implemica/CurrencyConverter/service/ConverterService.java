package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.model.Converter;
import com.tunyk.currencyconverter.BankUaCom;
import com.tunyk.currencyconverter.api.Currency;
import com.tunyk.currencyconverter.api.CurrencyConverter;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import com.tunyk.currencyconverter.api.CurrencyNotSupportedException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dmytro K.
 * @version 02.01.2019 20:00
 */
public class ConverterService {

   private final Logger log = Logger.getLogger(this.getClass().getName());

   private float convertedValue = 0.0f;

   private List<Throwable> exceptions = new ArrayList<>();

   private List<OptionSupplier> options = new ArrayList<>();

   /* constants */
   private static final String URL_FREE_CURRENCY_CONVERTER_API_COM = "http://free.currencyconverterapi.com/api/v5/convert?q=%s_%s&compact=y";
   private static final String URL_CURRENCY_LAYER_COM = "http://apilayer.net/api/live?access_key=f91895130d9f009b167cd5299cdd923c&source=%s&currencies=%s&format=1";
   private static final String URL_FLOAT_RATES_COM = "http://www.floatrates.com/daily/%s.json";

   {
      options.add(this::convertByBankUaCom);                      // unlimited
      options.add(this::convertByFloatRatesCom);                  // unlimited
      options.add(this::convertByJavaMoney);                      // unlimited, but so slow

      options.add(this::convertByFreeCurrencyConverterApiCom);    // has a limit - 100  requests per hour
      options.add(this::convertByCurrencyLayerCom);               // has a limit - 1000 requests per month
   }

   private interface OptionSupplier {
      void execute(Converter converter) throws Exception;
   }

   public float convert(Converter converter) throws CurrencyConverterException {
      for (OptionSupplier option : options) {
         if(handleException(option, converter)) {
            return convertedValue;
         }
      }
      log.log(Level.SEVERE, "exception was thrown: " + Arrays.toString(exceptions.toArray()));
      throw new CurrencyConverterException(analyzeException());
   }

   private String analyzeException() {
      String result = "Unknown error!";
      for (Throwable e : exceptions) {
         if (e instanceof IOException) {
            log.log(Level.SEVERE, e.getMessage());
            result = "Server not responding";
         } else if (e instanceof CurrencyNotSupportedException) {
            log.log(Level.SEVERE, e.getMessage());
            result = e.getMessage();
         }
      }

      return result;
   }

   private void convertByBankUaCom(Converter converter) throws CurrencyConverterException {
      Currency usersCurrency = getCurrencyByUtilCurrency(converter.getUsersCurrency());
      Currency desiredCurrency = getCurrencyByUtilCurrency(converter.getDesiredCurrency());

      CurrencyConverter currencyConverter = new BankUaCom(usersCurrency, desiredCurrency);
      convertedValue = currencyConverter.convertCurrency(converter.getValue());
      log.log(Level.INFO, "converted by bank.ua : " + converter.toString());
   }

   private Currency getCurrencyByUtilCurrency(java.util.Currency currency) throws CurrencyNotSupportedException {
      return Currency.fromString(currency.getCurrencyCode());
   }

   private void convertByJavaMoney(Converter converter) {
      MonetaryAmount userMoney = Monetary.getDefaultAmountFactory()
              .setCurrency(converter.getUsersCurrency().getCurrencyCode())
              .setNumber(converter.getValue()).create();

      CurrencyConversion conversion = MonetaryConversions.getConversion(converter.getDesiredCurrency().getCurrencyCode());
      MonetaryAmount converted = userMoney.with(conversion);

      convertedValue = converted.getNumber().floatValue();
      log.log(Level.INFO, "converted by java money : " + converter.toString());
   }

   private void convertByFreeCurrencyConverterApiCom(Converter converter) throws IOException {
      URL url = buildURL(URL_FREE_CURRENCY_CONVERTER_API_COM, converter);

      JSONObject object = getJsonObjectByURL(url);

      double value = object.getJSONObject(converter.getUsersCurrency() + "_" + converter.getDesiredCurrency())
                     .getDouble("val");

      convertedValue = converter.getValue() * (float) value;
      log.log(Level.INFO, "converted by free.currencyapi.com : " + converter.toString());
   }

   private void convertByCurrencyLayerCom(Converter converter) throws IOException {
      URL url = buildURL(URL_CURRENCY_LAYER_COM, converter);

      JSONObject object = getJsonObjectByURL(url);

      double value = object.getJSONObject("quotes")
                           .getDouble(converter.getUsersCurrency() + "" + converter.getDesiredCurrency());

      convertedValue = converter.getValue() * (float) value;
      log.log(Level.INFO, "converted by currencylayer.com : " + converter.toString());
   }

   private void convertByFloatRatesCom(Converter converter) throws IOException {
      URL url = buildURL(URL_FLOAT_RATES_COM, converter.getUsersCurrency());

      JSONObject object = getJsonObjectByURL(url);

      String desiredCurrency = converter.getDesiredCurrency().getCurrencyCode();

      double rate = object.getJSONObject(desiredCurrency.toLowerCase()).getDouble("rate");

      convertedValue = (float) rate * converter.getValue();
      log.log(Level.INFO, "converted by floatrates.com : " + converter.toString());
   }

   private JSONObject getJsonObjectByURL(URL url) throws IOException {
      JSONTokener tokener = new JSONTokener(url.openStream());

      return new JSONObject(tokener);
   }

   private boolean handleException(OptionSupplier optionSupplier, Converter converter) {
      boolean result = true;
      try {
         optionSupplier.execute(converter);
      } catch(Throwable e) {
         exceptions.add(e);
         result = false;
      }

      return result;
   }

   private static URL buildURL(String path, Converter converter) throws MalformedURLException {
      String url = String.format(path, converter.getUsersCurrency(), converter.getDesiredCurrency());
      return new URL(url);
   }

   private static URL buildURL(String path, java.util.Currency currency) throws MalformedURLException {
      String url = String.format(path, currency);
      return new URL(url);
   }
}