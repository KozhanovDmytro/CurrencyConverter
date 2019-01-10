package com.implemica.CurrencyConverter.controller;


import com.implemica.CurrencyConverter.configuration.SpringConfiguration;
import com.implemica.CurrencyConverter.configuration.WebSocketConfiguration;
import com.implemica.CurrencyConverter.controller.util.ClientEndPoint;
import com.implemica.CurrencyConverter.model.Dialog;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import com.implemica.CurrencyConverter.service.ConverterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author Dmytro K.
 */
@SpringBootTest(classes = { WebController.class, BotService.class, ConverterService.class},
               webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({SpringConfiguration.class, WebSocketConfiguration.class})
@AutoConfigureMockMvc
@EnableAutoConfiguration
public class WebControllerTest {

   private Logger logger = Logger.getLogger(WebControllerTest.class.getName());

   private final String URL = "ws://localhost:8080/monitor-bot";

   private final String URL_SUBSCRIBE = "/listen/bot";

   private User user = new User(1234234,
           "testFirstName",
           "testLastName",
           "testUserName");

   private ClientEndPoint clientEndPoint = new ClientEndPoint();

   private DateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private BotService botService;

   @BeforeEach
   void setUp() throws InterruptedException, ExecutionException, TimeoutException {
      List<Transport> transports = new ArrayList<>(1);
      transports.add(new WebSocketTransport( new StandardWebSocketClient()) );
      WebSocketClient transport = new SockJsClient(transports);
      WebSocketStompClient stompClient = new WebSocketStompClient(transport);

      stompClient.setMessageConverter(new MappingJackson2MessageConverter());

      StompSession session = stompClient.connect(URL, clientEndPoint)
              .get(1, SECONDS);

      session.subscribe(URL_SUBSCRIBE, clientEndPoint);

      logger.log(Level.INFO, "connected: " + session.isConnected());
   }

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

   @Test
   void webSocketTest() {
      sendAndCheckWS("/convert");
      sendAndCheckWS("some message");
      sendAndCheckWS("UAH");
      sendAndCheckWS("");
      sendAndCheckWS("UAH");
      sendAndCheckWS("USD");
      sendAndCheckWS("BYR");
      sendAndCheckWS("AND");
      sendAndCheckWS("/stop");
   }

   @Test
   @Disabled
   void pageTest() throws Exception {
      sendAndCheckPage("/convert");
   }

   private void sendAndCheckWS(String expectedMessage) {
      botService.onUpdateReceived(expectedMessage, user);

      try {
         Thread.sleep(500);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      assertNotNull(clientEndPoint.getReceivedDialog());

      Dialog receivedDialog = clientEndPoint.getReceivedDialog();
      clientEndPoint.setReceivedDialog(null);

      assertEquals(expectedMessage, receivedDialog.getUsersRequest());
      assertEquals(user, receivedDialog.getUser());
   }

   private void sendAndCheckPage(String expectedMessage) throws Exception {
//      TODO use Thyme leaf test!
      botService.onUpdateReceived(expectedMessage, user);

      try {
         Thread.sleep(500);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      assertNotNull(clientEndPoint.getReceivedDialog());

      Dialog receivedDialog = clientEndPoint.getReceivedDialog();
      clientEndPoint.setReceivedDialog(null);

      String expectedInHTMLPage = new StringBuilder().append("<tr>")
              .append("<td>")
              .append(df.format(receivedDialog.getDate()))
              .append("</td>")

              .append("<td>")
              .append(receivedDialog.getUser().getUserId())
              .append("</td>")

              .append("<td>")
              .append(receivedDialog.getUser().getUserFirstName())
              .append("</td>")

              .append("<td>")
              .append(receivedDialog.getUser().getUserLastName())
              .append("</td>")

              .append("<td>")
              .append(receivedDialog.getUser().getUserName())
              .append("</td>")

              .append("<td>")
              .append(receivedDialog.getUsersRequest())
              .append("</td>")

              .append("<td>")
              .append(receivedDialog.getBotsResponse())
              .append("</td>")

              .append("</tr>").toString();

      mockMvc.perform(get("/log"))
              .andExpect(status().isOk())
              .andExpect(content().string(containsString(expectedInHTMLPage)));


   }
}
