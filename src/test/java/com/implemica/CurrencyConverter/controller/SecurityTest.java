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
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = { WebController.class, BotService.class, ConverterService.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({SpringConfiguration.class, WebSocketConfiguration.class, WebSecurityConfig.class})
@AutoConfigureMockMvc
@EnableAutoConfiguration
public class SecurityTest {

   @Autowired
   private MockMvc mockMvc;

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

   @Test
   @WithAnonymousUser
   void logPageTest() throws Exception {
      mockMvc.perform(get("/log"))
              .andDo(print())
              .andExpect(status().is3xxRedirection());
   }

   @Test
   @WithAnonymousUser
   void monitorPageTest() throws Exception {
      mockMvc.perform(get("/monitor"))
              .andDo(print())
              .andExpect(status().is3xxRedirection());
   }

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
}
