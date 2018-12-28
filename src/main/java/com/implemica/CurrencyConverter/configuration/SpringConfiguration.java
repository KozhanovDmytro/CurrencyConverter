package com.implemica.CurrencyConverter.configuration;

import com.implemica.CurrencyConverter.dao.DialogDao;
import com.implemica.CurrencyConverter.dao.impl.DialogDaoImpl;
import com.implemica.CurrencyConverter.service.ConverterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

   @Bean
   public ConverterService converterService() {
      return new ConverterService();
   }

   @Bean
   public DialogDao transactionDao() {
      return new DialogDaoImpl();
   }
}