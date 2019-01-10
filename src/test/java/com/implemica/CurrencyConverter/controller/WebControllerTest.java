package com.implemica.CurrencyConverter.controller;


import com.implemica.CurrencyConverter.configuration.SpringConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = { WebController.class },
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SpringConfiguration.class})
@AutoConfigureMockMvc
@EnableAutoConfiguration
public class WebControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Test
   void mainPageTest() throws Exception {
      this.mockMvc.perform(get("/"))
              .andDo(print())
              .andExpect(status().isOk())
              .andExpect(content().string(containsString("WELCOME!")))
              .andExpect(content().string(containsString("to currency converter")))
              .andExpect(content().string(containsString("designed by Dasha S. and Dmytro K.")))
              .andExpect(content().string(containsString("show log")))
              .andExpect(content().string(containsString("monitor bot")));
   }
   @Test
   void logPageTest() throws Exception {
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
   void monitorPageTest() throws Exception {
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
