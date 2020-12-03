package com.miraclesoft.datalake.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.miraclesoft.datalake.model.Extractor;

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {	    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
//        stompEndpointRegistry.addEndpoint("/ws").setAllowedOrigins("http://172.17.0.154:4200").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); //This is where our simple text oriented messaging will be pushed
        registry.setApplicationDestinationPrefixes("/app"); //whatever websockets path you provide.
    }   
   

}
