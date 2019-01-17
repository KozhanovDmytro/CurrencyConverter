package com.implemica.CurrencyConverter.controller;


import com.implemica.CurrencyConverter.configuration.SpringConfiguration;
import com.implemica.CurrencyConverter.configuration.WebSecurityConfig;
import com.implemica.CurrencyConverter.configuration.WebSocketConfiguration;
import com.implemica.CurrencyConverter.service.BotService;
import com.implemica.CurrencyConverter.service.ConverterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests webApplication with and without anonymous user.
 *
 * @author Dmytro K.
 * @author Daria S.
 * @see MockMvc
 */
@SpringBootTest(classes = {WebController.class, BotService.class, ConverterService.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@ContextConfiguration(classes = {SpringConfiguration.class, WebSocketConfiguration.class, WebSecurityConfig.class})
public class WebControllerTest {

   /** Main entry point for server-side Spring MVC test support. */
   @Autowired private MockMvc mockMvc;

   /**
    * Test content on main page with anonymous user.
    *
    * @throws Exception if an error occurs
    */
   @Test
   @WithAnonymousUser
   void mainPageTest() throws Exception {
      mockMvc.perform(get("/"))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("WELCOME!")))
              .andExpect(content().string(containsString("to currency converter")))
              .andExpect(content().string(containsString("designed by Dasha S. and Dmytro K.")))
              .andExpect(content().string(containsString("show log")))
              .andExpect(content().string(containsString("monitor bot")));
   }

   /**
    * Test content on log page with anonymous user.
    *
    * @throws Exception if an error occurs
    */
   @Test
   @WithAnonymousUser
   void logPageTest() throws Exception {
      mockMvc.perform(get("/log"))
              .andDo(print())
              .andExpect(status().is3xxRedirection());
   }

   /**
    * Test content on monitor page with anonymous user.
    *
    * @throws Exception if an error occurs
    */
   @Test
   @WithAnonymousUser
   void monitorPageTest() throws Exception {
      mockMvc.perform(get("/monitor"))
              .andDo(print())
              .andExpect(status().is3xxRedirection());
   }

   /**
    * Test content on main page with some user.
    *
    * @throws Exception if an error occurs
    */
   @Test
   @WithMockUser
   void mainPageTestWithUser() throws Exception {
      mockMvc.perform(get("/"))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("WELCOME!")))
              .andExpect(content().string(containsString("to currency converter")))
              .andExpect(content().string(containsString("designed by Dasha S. and Dmytro K.")))
              .andExpect(content().string(containsString("show log")))
              .andExpect(content().string(containsString("monitor bot")));
   }

   /**
    * Test content on log page with some user.
    *
    * @throws Exception if an error occurs
    */
   @Test
   @WithMockUser
   void logPageTestWithUser() throws Exception {
      this.mockMvc.perform(get("/log"))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("Log")))
              .andExpect(content().string(containsString("<th>Date</th>")))
              .andExpect(content().string(containsString("<th>ID</th>")))
              .andExpect(content().string(containsString("<th>First name</th>")))
              .andExpect(content().string(containsString("<th>Last name</th>")))
              .andExpect(content().string(containsString("<th>User name</th>")))
              .andExpect(content().string(containsString("<th>Response</th>")))
              .andExpect(content().string(containsString("<th>Request</th>")));
   }

   /**
    * Test content on monitor page with some user.
    *
    * @throws Exception if an error occurs
    */
   @Test
   @WithMockUser
   void monitorPageTestWithUser() throws Exception {
      this.mockMvc.perform(get("/monitor"))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("monitor")))
              .andExpect(content().string(containsString("<th>Date</th>")))
              .andExpect(content().string(containsString("<th>ID</th>")))
              .andExpect(content().string(containsString("<th>First name</th>")))
              .andExpect(content().string(containsString("<th>Last name</th>")))
              .andExpect(content().string(containsString("<th>User name</th>")))
              .andExpect(content().string(containsString("<th>Response</th>")))
              .andExpect(content().string(containsString("<th>Request</th>")));
   }

   /**
    * Test content on monitor page with anonymous user.
    *
    * @throws Exception if an error occurs
    */
   @Test
   @WithAnonymousUser
   void loginPageWithUser() throws Exception {
      this.mockMvc.perform(get("/login"))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("Login")))
              .andExpect(content().string(containsString("Password")));

   }

   /**
    * Test content on monitor page with some user.
    *
    * @throws Exception if an error occurs
    */
   @Test
   @WithMockUser
   void loginPageWithMockUser() throws Exception {
      this.mockMvc.perform(get("/login"))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("Login")))
              .andExpect(content().string(containsString("Password")));

   }
}
