package com.implemica.CurrencyConverter.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.implemica.CurrencyConverter.model.Converter;
import com.tunyk.currencyconverter.BankUaCom;
import com.tunyk.currencyconverter.api.Currency;
import com.tunyk.currencyconverter.api.CurrencyConverter;
import com.tunyk.currencyconverter.api.CurrencyConverterException;
import com.tunyk.currencyconverter.api.CurrencyNotSupportedException;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Dmytro K.
 * @version 02.01.2019 10:00
 */
public class ConverterService {

   private final Logger log = Logger.getLogger(this.getClass().getName());

   private Float convertedValue = 0.0f;

   private List<Exception> exceptions = new ArrayList<>();

   private List<OptionSupplier> options = new ArrayList<>();

   {
      options.add(this::convertByBankUaCom);
      options.add(this::convertByURL);
      options.add(this::convertByJavaMoney);
   }

   private interface OptionSupplier {
      void execute(Converter converter) throws Exception;
   }

   public Float convert(Converter converter) throws CurrencyConverterException {
      for (OptionSupplier option : options) {
         if(handleException(option, converter)) {
            return convertedValue;
         }
      }
      log.log(Level.SEVERE, "exception was thrown: " + Arrays.toString(exceptions.toArray()));
      throw new CurrencyConverterException(analyzeException());
   }

   private String analyzeException() {
      String result = "Fatal error!";
      for (Exception e : exceptions) {
         if (e instanceof CurrencyNotSupportedException) {
            result = e.getMessage();
         }
      }
      return result;
   }

   private void convertByBankUaCom(Converter converter) throws CurrencyConverterException {
      Currency usersCurrency = Currency.fromString(converter.getUsersCurrency().getCurrencyCode());
      Currency desiredCurrency = Currency.fromString(converter.getDesiredCurrency().getCurrencyCode());

      CurrencyConverter currencyConverter = new BankUaCom(usersCurrency, desiredCurrency);
      convertedValue = currencyConverter.convertCurrency(converter.getValue());
   }

   private void convertByJavaMoney(Converter converter) {
      MonetaryAmount userMoney = Monetary.getDefaultAmountFactory()
              .setCurrency(converter.getUsersCurrency().getCurrencyCode())
              .setNumber(converter.getValue()).create();

      CurrencyConversion conversion = MonetaryConversions.getConversion(converter.getDesiredCurrency().getCurrencyCode());
      MonetaryAmount convertedAmountUSDtoEUR = userMoney.with(conversion);

      convertedValue = convertedAmountUSDtoEUR.getNumber().floatValue();
   }

   private void convertByURL(Converter converter) throws IOException {
      String url = String.format("http://free.currencyconverterapi.com/api/v5/convert?q=%s_%s&compact=y",
              converter.getUsersCurrency(), converter.getDesiredCurrency());

      JsonParser jsonParser = new JsonFactory().createParser(new URL(url));

      jsonParser.nextToken();
      jsonParser.nextFieldName();
      jsonParser.nextToken();
      jsonParser.nextFieldName();
      jsonParser.nextToken();
      float value = jsonParser.getFloatValue();

      convertedValue = converter.getValue() * value;
   }

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
}