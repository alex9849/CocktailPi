package net.alex9849.cocktailpi.config.websocket;

import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.service.WebSocketService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    @Bean
    AuthorizationManager<Message<?>> authorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        return messages.simpTypeMatchers(SimpMessageType.CONNECT).authenticated()
                .simpMessageDestMatchers("/topic/**", "/queue/**").denyAll()
                .simpDestMatchers(WebSocketService.WS_ACTIONS_STATUS_DESTINATION).hasAuthority(ERole.ROLE_ADMIN.name())
                .simpDestMatchers(WebSocketService.WS_ACTIONS_LOG_DESTINATION).hasAuthority(ERole.ROLE_ADMIN.name())
                .simpDestMatchers("/**").authenticated()
                .simpTypeMatchers(SimpMessageType.values()).authenticated()
                //.anyMessage().authenticated()
                .build();
    }

    @Bean(name = "csrfChannelInterceptor")
    ChannelInterceptor csrfChannelInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                return message;
            }
        };
    }
}
