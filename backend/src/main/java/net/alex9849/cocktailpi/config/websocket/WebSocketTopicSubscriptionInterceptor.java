package net.alex9849.cocktailpi.config.websocket;

import net.alex9849.cocktailpi.model.user.ERole;
import net.alex9849.cocktailpi.model.user.User;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class WebSocketTopicSubscriptionInterceptor implements ChannelInterceptor {
    private final String destination;
    private final ERole minRole;

    public WebSocketTopicSubscriptionInterceptor(String destination, ERole minRole) {
        this.destination = destination;
        this.minRole = minRole;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor= StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            UsernamePasswordAuthenticationToken userPrincipal = (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
            User user = (User) userPrincipal.getPrincipal();
            if(!validateSubscription(user, headerAccessor.getDestination()))
            {
                throw new IllegalArgumentException("No permission for this topic");
            }
        }
        return message;
    }

    private boolean validateSubscription(User user, String topicDestination) {
        if(topicDestination == null) {
            return true;
        }
        if(!topicDestination.startsWith(destination)) {
            return true;
        }
        if (user == null) {
            return false;
        }
        if (user.getAuthority().getLevel() < minRole.getLevel()) {
            return false;
        }
        return true;
    }

}
