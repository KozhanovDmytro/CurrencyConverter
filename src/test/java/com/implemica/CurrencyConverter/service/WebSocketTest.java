package com.implemica.CurrencyConverter.service;

import com.implemica.CurrencyConverter.configuration.SpringConfiguration;
import com.implemica.CurrencyConverter.configuration.WebSocketConfiguration;
import com.implemica.CurrencyConverter.model.Dialog;
import com.implemica.CurrencyConverter.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for testing websocket.
 *
 * In this test created client endpoint {@link Handler}.
 *
 * First of all, a message send to the {@link BotService}, bot sends to the web
 * socket channel, then client endpoint checks that he received this message.
 *
 * @see BotService
 * @see Dialog
 * @see StompSessionHandlerAdapter
 *
 * @author Dmytro K.
 */
@SpringBootTest(classes = {BotService.class, ConverterService.class},
webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({SpringConfiguration.class, WebSocketConfiguration.class})
@AutoConfigureMockMvc
@EnableAutoConfiguration
public class WebSocketTest {

   private Logger logger = Logger.getLogger(WebSocketTest.class.getName());

   @Autowired
   private BotService botService;

   private final String URL = "ws://localhost:8080/monitor-bot";

   private final String URL_SUBSCRIBE = "/listen/bot";

   private User user = new User(1234234,
           "testFirstName",
           "testLastName",
           "testUserName");

   /** Instance which was received from websocket for testing soon. */
   private Dialog receivedDialog;


   @BeforeEach void setUp() throws InterruptedException, ExecutionException, TimeoutException {
      List<Transport> transports = new ArrayList<>(1);
      transports.add(new WebSocketTransport( new StandardWebSocketClient()) );
      WebSocketClient transport = new SockJsClient(transports);
      WebSocketStompClient stompClient = new WebSocketStompClient(transport);

      stompClient.setMessageConverter(new MappingJackson2MessageConverter());

      Handler handler = new Handler();

      StompSession session = stompClient.connect(URL, handler)
              .get(1, SECONDS);

      session.subscribe(URL_SUBSCRIBE, handler);

      logger.log(Level.INFO, "connected: " + session.isConnected());
   }

   @Test
   void test() {
      sendAndCheck("/convert");
      sendAndCheck("some message");
      sendAndCheck("UAH");
      sendAndCheck("");
      sendAndCheck("UAH");
      sendAndCheck("USD");
      sendAndCheck("BYR");
      sendAndCheck("AND");
      sendAndCheck("/stop");
   }

   /**
    * Sends message to bot and check that {@link Handler} received this message.
    *
    * @param expectedMessage expected message
    */
   private void sendAndCheck(String expectedMessage) {
      botService.onUpdateReceived(expectedMessage, user);

      try {
         Thread.sleep(500);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      assertNotNull(receivedDialog);
      assertEquals(expectedMessage, receivedDialog.getUsersRequest());
      assertEquals(user, receivedDialog.getUser());
   }

   /**
    * Client end point which received message from bot.
    */
   private class Handler extends StompSessionHandlerAdapter {

      @Override
      public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
         exception.printStackTrace();
      }

      @Override
      public Type getPayloadType(StompHeaders headers) {
         return Dialog.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
         receivedDialog = (Dialog) payload;
         logger.log(Level.INFO, "receive from web socket server. ");
      }
   }
}
