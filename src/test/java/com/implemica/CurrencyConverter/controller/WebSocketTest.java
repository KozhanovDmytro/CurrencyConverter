package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.configuration.SpringConfiguration;
import com.implemica.CurrencyConverter.configuration.WebSocketConfiguration;
import com.implemica.CurrencyConverter.controller.util.Client;
import com.implemica.CurrencyConverter.model.Dialog;
import com.implemica.CurrencyConverter.model.User;
import com.implemica.CurrencyConverter.service.BotService;
import com.implemica.CurrencyConverter.service.ConverterService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest(classes = {BotService.class, ConverterService.class},
webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import({SpringConfiguration.class, WebSocketConfiguration.class})
@AutoConfigureMockMvc
@EnableAutoConfiguration
public class WebSocketTest {

   @Autowired
   private BotService botService;

   private final String url = "ws://localhost:8080/monitor-bot";

   private final String URL_SUBSCRIBE = "/listen/bot";

   private Client client;

   private User user = new User(1234234,
           "testFirstName",
           "testLastName",
           "testUserName");


   @BeforeEach
   void setUp() throws InterruptedException, ExecutionException, TimeoutException {
      List<Transport> transports = new ArrayList<>(1);
      transports.add(new WebSocketTransport( new StandardWebSocketClient()) );
      WebSocketClient transport = new SockJsClient(transports);
      WebSocketStompClient stompClient = new WebSocketStompClient(transport);

      stompClient.setMessageConverter(new MappingJackson2MessageConverter());

      StompSession session = stompClient.connect(url, new Handler())
              .get(1, SECONDS);

      session.subscribe(URL_SUBSCRIBE, new Handler());

      System.out.println(session.isConnected());
   }

   @Test
   void test() {
      send("/convert");
//      send("/convert");
//      send("/convert");
//      send("/convert");
   }

   private void send(String message) {
      botService.onUpdateReceived(message, user);
      System.out.println("send");

   }

   /* extra */


   private class Handler extends StompSessionHandlerAdapter {

      @Override
      public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
         System.out.println("after connection");
      }

      @Override
      public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
         throw new RuntimeException("Failure in WebSocket handling", exception);
      }

      @Override
      public void handleTransportError(StompSession session, Throwable exception) {
         System.out.println("error");
      }

      @Override
      public Type getPayloadType(StompHeaders headers) {
         System.out.println("getPayloadType");
         return Dialog.class;
      }

      @Override
      public void handleFrame(StompHeaders headers, Object payload) {
         System.out.println("handleFrame");
      }
   }
}
