package com.implemica.CurrencyConverter.configuration;

import org.springframework.beans.factory.annotation.Value;
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
 * Class for configure Spring security
 *
 * @author Dmytro K.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

   @Value("${admin.login}")
   private String ADMIN_LOGIN;

   @Value("${admin.password}")
   private String ADMIN_PASSWORD;

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
    * Loads admin user.
    */
   @Bean
   @Override
   public UserDetailsService userDetailsService() {
      UserDetails user = User.withDefaultPasswordEncoder()
              .username(ADMIN_LOGIN)
              .password(ADMIN_PASSWORD)
              .roles("ADMIN")
              .build();

      return new InMemoryUserDetailsManager(user);
   }
}
