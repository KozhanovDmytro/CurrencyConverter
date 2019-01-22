package com.implemica.CurrencyConverter.controller;


import com.implemica.CurrencyConverter.configuration.SpringConfiguration;
import com.implemica.CurrencyConverter.configuration.WebSocketConfiguration;
import com.implemica.CurrencyConverter.controller.util.ClientEndPoint;
import com.implemica.CurrencyConverter.controller.util.TestSecurityConfiguration;
import com.implemica.CurrencyConverter.dao.DialogDao;
import com.implemica.CurrencyConverter.model.Dialog;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import com.implemica.CurrencyConverter.service.ConverterService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The goal of this test is create webSocket follower - {@link ClientEndPoint},
 * call {@link BotService#onUpdateReceived(String, User)} method, it have to send
 * instance of {@link Dialog} to webSocket followers and catch it in {@link ClientEndPoint}
 *
 * @author Dmytro K.
 * @author Daria S.
 * @see ClientEndPoint
 * @see Dialog
 * @see BotService
 */
@SpringBootTest(classes = {WebController.class, BotService.class, ConverterService.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@ContextConfiguration(classes = {SpringConfiguration.class, WebSocketConfiguration.class, TestSecurityConfiguration.class})
public class WebSocketTest {

   /** Representational of row in HTML table. */
   private static final String ROW_FORMAT = "<tr>" +
           "            <td>%s</td>" +
           "            <td>%d</td>" +
           "            <td>%s</td>" +
           "            <td>%s</td>" +
           "            <td>%s</td>" +
           "            <td>%s</td>" +
           "            <td>%s</td>" +
           "        </tr>";

   private static final String SELENIUM_FORMAT = "%s %d %s %s %s %s %s";

   private Logger logger = LoggerFactory.getLogger(WebSocketTest.class.getName());

   /** WebSocket URL. */
   private static final String URL = "ws://localhost:8080/monitor-bot";

   /** Path for listening webSocket. */
   private static final String URL_SUBSCRIBE = "/listen/bot";

   /** Test user. */
   private User user = new User(1234234,
           "testFirstName",
           "testLastName",
           "testUserName");

   /** Client end point which catch message from webSocket. */
   private ClientEndPoint clientEndPoint = new ClientEndPoint();

   /** Date formatter for check date format. */
   private DateFormat df = BotService.SIMPLE_DATE_FORMAT;

   /** Main entry point for server-side Spring MVC test support. */
   @Autowired private MockMvc mockMvc;

   /** BotService which send message to webSocket followers. */
   @Autowired private BotService botService;

   /**
    * Uses for get last {@link com.implemica.CurrencyConverter.model.Dialog} which
    * was sent to webSocket followers.
    */
   @Autowired private DialogDao dialogDao;

   private static ChromeDriver chromeDriver;

   @Value("${admin.login}")
   private String ADMIN_LOGIN;

   @Value("${admin.password}")
   private String ADMIN_PASSWORD;

   @BeforeAll
   static void beforeAll() {
      System.setProperty("webdriver.chrome.driver", "C:/Selenium/chromedriver.exe");

      chromeDriver = new ChromeDriver();
   }

   /**
    * Settings for client end point.
    *
    * @throws ExecutionException   if the computation threw an exception
    * @throws InterruptedException if the current thread was interrupted while waiting
    * @throws TimeoutException     if the wait timed out
    */
   @BeforeEach
   void setUp() throws InterruptedException, ExecutionException, TimeoutException {
      List<Transport> transports = new ArrayList<>(1);
      transports.add(new WebSocketTransport(new StandardWebSocketClient()));
      WebSocketClient transport = new SockJsClient(transports);
      WebSocketStompClient stompClient = new WebSocketStompClient(transport);

      stompClient.setMessageConverter(new MappingJackson2MessageConverter());

      StompSession session = stompClient.connect(URL, clientEndPoint)
              .get(1, SECONDS);

      session.subscribe(URL_SUBSCRIBE, clientEndPoint);

      chromeDriver.get("localhost:8080/monitor");
      chromeDriver.findElementById("connect").click();

      logger.info("connected: " + session.isConnected());
   }

   /**
    * Tests, that content, which was sent to page is present on the page
    */
   @Test
   void webSocketTest() throws Exception {

      //non-valid data
      sendAndCheckWSAndPageContent("pksajhyufd");
      sendAndCheckWSAndPageContent("hfds43kj23 kj");
      sendAndCheckWSAndPageContent("\"\"klhgf\"n.jhj\"\"lkljgt");
      sendAndCheckWSAndPageContent("pksajhyufd");
      sendAndCheckWSAndPageContent("dsakjdsa hasdvhj335j kgwa k34");
      sendAndCheckWSAndPageContent("lkjfewqjf'''dsflkjj");
      sendAndCheckWSAndPageContent("aslfhkkfakjsf,vslk");
      sendAndCheckWSAndPageContent("Lorem kjfsdlksdfj");
      sendAndCheckWSAndPageContent("dsflkalkdsfj");
      sendAndCheckWSAndPageContent("wglkj45hkg");

      //valid data
      sendAndCheckWSAndPageContent("/start");
      sendAndCheckWSAndPageContent("/convert");
      sendAndCheckWSAndPageContent("USD");
      sendAndCheckWSAndPageContent("UAH");
      sendAndCheckWSAndPageContent("1");
      sendAndCheckWSAndPageContent("/convert");
      sendAndCheckWSAndPageContent("RUB");
      sendAndCheckWSAndPageContent("UAH");
      sendAndCheckWSAndPageContent("1");
      sendAndCheckWSAndPageContent("/stop");
      sendAndCheckWSAndPageContent("18 usd to uah");
      sendAndCheckWSAndPageContent("1000 eur in rub");

      //valid data, but incorrect requests from user
      sendAndCheckWSAndPageContent("hello");
      sendAndCheckWSAndPageContent("start");
      sendAndCheckWSAndPageContent("stop");
      sendAndCheckWSAndPageContent("convert");
      sendAndCheckWSAndPageContent("/help");
      sendAndCheckWSAndPageContent("/clear");
      sendAndCheckWSAndPageContent("/cancel");
      sendAndCheckWSAndPageContent("14$ to eur");
      sendAndCheckWSAndPageContent("sell 16 usd to eur");
      sendAndCheckWSAndPageContent("107 dollars to yen");
      sendAndCheckWSAndPageContent("77,4 pln from byn");
      sendAndCheckWSAndPageContent("10 USD is PLN");
      sendAndCheckWSAndPageContent("Please, convert 7 uah to rub");
      sendAndCheckWSAndPageContent("How are you?");
      sendAndCheckWSAndPageContent("-7,5 xxx to rur");
      sendAndCheckWSAndPageContent("repeat last");

      //shows, that user sent non-text message
      sendAndCheckWSAndPageContent(BotService.UNIQUE);
   }

   /**
    * Call {@link BotService#onUpdateReceived(String, User)} which sends to
    * webSocket followers and check it on the log page and in client end point.
    *
    * @param expectedMessage message received from user
    * @throws Exception if an error occurs
    */
   private void sendAndCheckWSAndPageContent(String expectedMessage) throws Exception {
      botService.onUpdateReceived(expectedMessage, user);

      List<Dialog> dialogs = dialogDao.getAll();
      Dialog dialogFromFile = dialogs.get(dialogs.size() - 1);

      try {
         Thread.sleep(500);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      assertNotNull(clientEndPoint.getReceivedDialog());

      Dialog receivedDialogFromWS = clientEndPoint.getReceivedDialog();

      // sets null here is important for next tests
      // it needed for check in next tests if data
      // was received from webSocket or not.
      clientEndPoint.setReceivedDialog(null);

      // check WS
      assertEquals(dialogFromFile, receivedDialogFromWS);

      String expectedRow = getExpectedRow(dialogFromFile);
      String expectedRowForSelenium = getExpectedDataForSelenium(dialogFromFile);

      // check log page
      checkLogPage(expectedRow);
      checkMonitorPage(expectedRowForSelenium);
   }

   /**
    * Gets content by given path
    *
    * @param mapping path to page, content from which  has to be gotten
    * @return String of content
    */
   private String getContentByMapping(String mapping) throws Exception {
      return mockMvc.perform(get(mapping))
              .andDo(print())
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString()
              .replaceAll("\n", "")
              .replaceAll("\r", "")
              .replaceAll("&quot;", "\"")
              .replaceAll("&#39;", "'");
   }

   /**
    * Formats given dialog to String, which will be ay table row on web page
    *
    * @param dialog given dialog
    * @return formatted String
    */
   private String getExpectedRow(Dialog dialog) {
      return String.format(ROW_FORMAT,
              df.format(dialog.getDate()),
              dialog.getUser().getUserId(),
              dialog.getUser().getUserFirstName(),
              dialog.getUser().getUserLastName(),
              dialog.getUser().getUserName(),
              dialog.getUsersRequest(),
              dialog.getBotsResponse());
   }

   private String getExpectedDataForSelenium(Dialog  dialog) {
      return String.format(SELENIUM_FORMAT,
              df.format(dialog.getDate()),
              dialog.getUser().getUserId(),
              dialog.getUser().getUserFirstName(),
              dialog.getUser().getUserLastName(),
              dialog.getUser().getUserName(),
              dialog.getUsersRequest(),
              dialog.getBotsResponse());
   }

   private void checkMonitorPage(String expected) {
      String content = chromeDriver.findElementById("response").getText();
      assertTrue(content.contains(expected));
   }

   private void checkLogPage(String expected) throws Exception {
      String actualContentOnPageLog = getContentByMapping("/log");
      assertTrue(actualContentOnPageLog.contains(expected));
   }

   @AfterAll
   static void afterAll() {
      chromeDriver.quit();
   }
}
