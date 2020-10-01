package net.alex9849.cocktailmaker.config.websocket;

import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.WebSocketService;
import net.alex9849.cocktailmaker.service.cocktailfactory.CocktailFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Objects;

@Component
public class WebSocketEventListener {

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private CocktailFactoryService cocktailFactoryService;

    @Autowired
    private PumpService pumpService;

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        if(Objects.equals(event.getMessage().getHeaders().get("simpDestination"), "/topic/cocktailprogress")) {
            webSocketService.broadcastCurrentCocktail(cocktailFactoryService.getCurrentProgress());
        }
        if(Objects.equals(event.getMessage().getHeaders().get("simpDestination"), "/topic/pumplayout")) {
            webSocketService.broadcastPumpLayout(pumpService.getAllPumps());
        }
    }
}
