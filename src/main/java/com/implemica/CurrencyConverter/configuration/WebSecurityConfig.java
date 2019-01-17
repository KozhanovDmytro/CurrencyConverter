package com.implemica.CurrencyConverter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Class for configure WebSecurity
 *
 * @author Dmytro K.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
   /**
    * Configures the {@link HttpSecurity}
    */
   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http
              .authorizeRequests()
              .antMatchers("/").permitAll()
              .anyRequest().authenticated()
              .and()
              .formLogin()
              .loginPage("/login")
              .permitAll()
              .and()
              .logout()
              .permitAll();
   }

   /**
    * Loads user-specific data
    */
   @Bean
   @Override
   public UserDetailsService userDetailsService() {
      UserDetails user = User.withDefaultPasswordEncoder()
              .username("admin")
              .password("admin")
              .roles("ADMIN")
              .build();

      return new InMemoryUserDetailsManager(user);
   }
}
