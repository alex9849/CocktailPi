package net.alex9849.cocktailmaker.config.websocket;

import net.alex9849.cocktailmaker.service.EventService;
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

    @Autowired
    private EventService eventService;

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        final String simpDestination = (String) event.getMessage().getHeaders().get("simpDestination");
        if(simpDestination == null) {
            return;
        }
        if(Objects.equals(simpDestination, "/user" + WebSocketService.WS_COCKTAIL_DESTINATION)) {
            webSocketService.sendCurrentCocktailProgessToUser(pumpService.getCurrentCocktailProgress(), event.getUser().getName());
        }
        if(Objects.equals(simpDestination, "/user" + WebSocketService.WS_PUMP_LAYOUT_DESTINATION)) {
            webSocketService.sendPumpLayoutToUser(pumpService.getAllPumps(), event.getUser().getName());
        }
        if(Objects.equals(simpDestination, "/user" + WebSocketService.WS_ACTIONS_STATUS_DESTINATION)) {
            webSocketService.sendRunningEventActionsStatusToUser(eventService.getRunningActionsInformation(), event.getUser().getName());
        }

        if(simpDestination.startsWith("/user" + WebSocketService.WS_ACTIONS_LOG_DESTINATION + "/")) {
            long actionId;
            try {
                String stringActionId = simpDestination.substring(WebSocketService.WS_ACTIONS_LOG_DESTINATION.length() + 1);
                actionId = Long.parseLong(stringActionId);
            } catch (NumberFormatException e) {
                actionId = -1L;
            }

            webSocketService.sendEventActionLogToUser(actionId, eventService.getEventActionLog(actionId), event.getUser().getName());
        }
    }
}
