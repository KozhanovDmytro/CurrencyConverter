package com.implemica.CurrencyConverter.controller;

import com.implemica.CurrencyConverter.model.Dialog;
import org.junit.Before;
import org.junit.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitPlatform.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

   @Value("${local.server.port}")
   private int port;

   private static CompletableFuture<Dialog> dialogs;

   @Before
   static void setUp() {
      dialogs = new CompletableFuture<>();
   }

   @Test
   void test() {
      assertEquals(8080, port);
   }


   private class StompFrameHandlerImpl implements StompFrameHandler {
      @Override
      public Type getPayloadType(StompHeaders stompHeaders) {
         return Dialog.class;
      }

      @Override
      public void handleFrame(StompHeaders stompHeaders, Object o) {
         dialogs.complete((Dialog) o);
      }
   }
}
