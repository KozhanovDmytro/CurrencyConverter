package com.implemica.CurrencyConverter.configuration;

import com.implemica.CurrencyConverter.model.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

   @Bean
   public Converter convert() {
      return new Converter();
   }
}