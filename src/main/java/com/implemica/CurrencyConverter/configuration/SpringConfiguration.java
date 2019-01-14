package com.implemica.CurrencyConverter.configuration;

import com.implemica.CurrencyConverter.dao.DialogDao;
import com.implemica.CurrencyConverter.dao.impl.DialogDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Dmytro K.
 * @version 02.01.2019 10:00
 */
@Configuration
public class SpringConfiguration {

   @Bean
   public DialogDao transactionDao() {
      return new DialogDaoImpl();
   }
}