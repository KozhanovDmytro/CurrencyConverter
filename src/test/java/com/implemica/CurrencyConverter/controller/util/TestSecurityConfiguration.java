package com.implemica.CurrencyConverter.controller.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Configuration needed for {@link com.implemica.CurrencyConverter.controller.WebSocketTest} and
 * test webSocket without spring security.
 *
 * @author Dmytro K.
 */
@Configuration
@EnableWebSecurity
public class TestSecurityConfiguration extends WebSecurityConfigurerAdapter {

   /**
    * Gets all permissions.
    * @param http the {@link HttpSecurity} to modify
    * @throws Exception if an error occurs
    */
   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable().authorizeRequests().anyRequest().permitAll();
   }

   /**
    * Configures WebSecurity. Enables debug support with Spring Security
    */
   @Override
   public void configure(WebSecurity web) {
      web.debug(true);
   }
}