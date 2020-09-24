package net.alex9849.cocktailmaker.config.websocket;

import net.alex9849.cocktailmaker.service.CocktailFactoryService;
import net.alex9849.cocktailmaker.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private CocktailFactoryService cocktailFactoryService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        webSocketService.sendCocktailProgress(cocktailFactoryService.getCurrentProgress(), event.getUser().getName());
    }
}
