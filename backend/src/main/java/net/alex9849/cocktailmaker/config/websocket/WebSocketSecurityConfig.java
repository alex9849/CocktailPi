package net.alex9849.cocktailmaker.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    /*
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }*/

    @Bean
    AuthorizationManager<Message<?>> authorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        return messages.simpTypeMatchers(SimpMessageType.CONNECT).authenticated()
                .simpMessageDestMatchers("/topic/**", "/queue/**").denyAll().build();
    }
}
