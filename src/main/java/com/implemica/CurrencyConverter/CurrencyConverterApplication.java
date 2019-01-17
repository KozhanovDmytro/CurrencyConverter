package com.implemica.CurrencyConverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.telegram.telegrambots.ApiContextInitializer;

/**Main class of application*/
@SpringBootApplication
public class CurrencyConverterApplication extends SpringBootServletInitializer {

   static {
      ApiContextInitializer.init();
   }

   /**Runs application*/
   public static void main(String[] args) {
      SpringApplication.run(CurrencyConverterApplication.class, args);
   }

   @Override
   protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
      return builder.sources(CurrencyConverterApplication.class);
   }
}

