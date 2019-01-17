package com.implemica.CurrencyConverter.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Class for configure webSocket server.
 *
 * @author Dmytro K.
 * @version 02.01.2019 10:00
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
   /**
    * Configure message broker options.
    */
   @Override
   public void configureMessageBroker(MessageBrokerRegistry registry) {
      registry.enableSimpleBroker("/listen");
      registry.setApplicationDestinationPrefixes("/");
   }

   /**
    * Register STOMP endpoints mapping each to a specific URL and (optionally)
    * enabling and configuring SockJS fallback options.
    */
   @Override
   public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
      stompEndpointRegistry.addEndpoint("/monitor-bot").withSockJS();
   }
}
