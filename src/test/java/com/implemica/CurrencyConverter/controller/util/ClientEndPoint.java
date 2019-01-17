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

/**
 * Class simulate client end point. Which listens a webSocket
 * channel and saves the response. Which  possible to get and
 * check.
 *
 * @author Dmytro K.
 * @see Dialog
 */
public class ClientEndPoint extends StompSessionHandlerAdapter {

   private Logger logger = Logger.getLogger(ClientEndPoint.class.getName());

   /**
    * Received instance of Dialog from webSocket chanel.
    */
   @Getter
   @Setter
   private Dialog receivedDialog;

   /**
    * Handle any exception arising while processing a STOMP frame such as a failure to convert the payload or an
    * unhandled exception in the application StompFrameHandler.
    *
    * @param session   the client STOMP session
    * @param command   the STOMP command of the frame
    * @param headers   the headers
    * @param payload   the raw payload
    * @param exception the exception
    */
   @Override
   public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
      exception.printStackTrace();
   }

   /**
    * Invoked before handleFrame(StompHeaders, Object) to determine the type of Object the payload should be converted to.
    *
    * @param headers the headers of a message
    */
   @Override
   public Type getPayloadType(StompHeaders headers) {
      return Dialog.class;
   }

   /**
    * Function for getting instance of Dialog.
    *
    * @param headers information about response
    * @param o       object which was received from webSocket
    */
   @Override
   public void handleFrame(StompHeaders headers, Object o) {
      receivedDialog = (Dialog) o;
      logger.log(Level.INFO, "receive from web socket server. ");
   }


}