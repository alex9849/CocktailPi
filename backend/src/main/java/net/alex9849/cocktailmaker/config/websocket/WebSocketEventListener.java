package net.alex9849.cocktailmaker.config.websocket;

import net.alex9849.cocktailmaker.service.PumpService;
import net.alex9849.cocktailmaker.service.WebSocketService;
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
    private PumpService pumpService;

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) throws IllegalAccessException {
        if(Objects.equals(event.getMessage().getHeaders().get("simpDestination"), "/user/topic/cocktailprogress")) {
            webSocketService.sendCurrentCocktailProgessToUser(pumpService.getCurrentCocktailProgress(), event.getUser().getName());
        }
        if(Objects.equals(event.getMessage().getHeaders().get("simpDestination"), "/user/topic/pumplayout")) {
            webSocketService.sendPumpLayoutToUser(pumpService.getAllPumps(), event.getUser().getName());
        }
        if(Objects.equals(event.getMessage().getHeaders().get("simpDestination"), "/user/topic/runningactions")) {
            //webSocketService.broadcastPumpLayout(pumpService.getAllPumps());
        }
    }
}
