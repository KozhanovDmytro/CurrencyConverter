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
import java.util.List;

public class ConverterService {

   private Float convertedValue;

   private List<Exception> exceptions = new ArrayList<>();

   private List<OptionSupplier> options = new ArrayList<>();

   {
      options.add(this::convertByBankUaCom);
      options.add(this::convertByJavaMoney);
      options.add(this::convertByURL);
   }

   private interface OptionSupplier {
      boolean execute(Converter converter);
   }

   public Float convert(Converter converter) throws CurrencyConverterException {
      for (OptionSupplier option : options) {
         if(option.execute(converter)) {
            return convertedValue;
         }
      }
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

   private boolean convertByBankUaCom(Converter converter) {
      boolean result = true;
      try {
         Currency usersCurrency = Currency.fromString(converter.getUsersCurrency().getCurrencyCode());
         Currency desiredCurrency = Currency.fromString(converter.getDesiredCurrency().getCurrencyCode());

         CurrencyConverter currencyConverter = new BankUaCom(usersCurrency, desiredCurrency);
         convertedValue = currencyConverter.convertCurrency(converter.getValue());
      } catch (CurrencyConverterException e) {
         exceptions.add(e);
         result = false;
      }

      return result;
   }

   private boolean convertByJavaMoney(Converter converter) {
      boolean result = true;
      try {
         MonetaryAmount userMoney = Monetary.getDefaultAmountFactory()
                 .setCurrency(converter.getUsersCurrency().getCurrencyCode())
                 .setNumber(converter.getValue()).create();

         CurrencyConversion conversion = MonetaryConversions.getConversion(converter.getDesiredCurrency().getCurrencyCode());
         MonetaryAmount convertedAmountUSDtoEUR = userMoney.with(conversion);

         convertedValue = convertedAmountUSDtoEUR.getNumber().floatValue();
      } catch (Exception e) {
         exceptions.add(e);
         result = false;
      }

      return result;
   }

   private boolean convertByURL(Converter converter) {
      boolean result = true;
      try {
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
      } catch (IOException e) {
         exceptions.add(e);
         result = false;
      }
      return result;
   }
}