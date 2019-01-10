package com.implemica.CurrencyConverter.controller.util;

import com.implemica.CurrencyConverter.model.Dialog;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientEndPoint extends StompSessionHandlerAdapter {

   private Logger logger = Logger.getLogger(ClientEndPoint.class.getName());

   @Getter
   @Setter
   private Dialog receivedDialog;

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