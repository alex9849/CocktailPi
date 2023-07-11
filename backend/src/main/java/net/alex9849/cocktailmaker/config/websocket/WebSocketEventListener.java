package net.alex9849.cocktailmaker.config.websocket;

import net.alex9849.cocktailmaker.service.EventService;
import net.alex9849.cocktailmaker.service.WebSocketService;
import net.alex9849.cocktailmaker.service.pumps.CocktailOrderService;
import net.alex9849.cocktailmaker.service.pumps.PumpDataService;
import net.alex9849.cocktailmaker.service.pumps.PumpUpService;
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
    private PumpDataService pumpService;

    @Autowired
    private PumpUpService maintenanceService;

    @Autowired
    private EventService eventService;

    @Autowired
    private CocktailOrderService cocktailOrderService;

    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        final String simpDestination = (String) event.getMessage().getHeaders().get("simpDestination");
        if(simpDestination == null) {
            return;
        }
        if(Objects.equals(simpDestination, "/user" + WebSocketService.WS_COCKTAIL_DESTINATION)) {
            webSocketService.sendCurrentCocktailProgessToUser(cocktailOrderService.getCurrentCocktailProgress(), event.getUser().getName());
        }
        if(Objects.equals(simpDestination, "/user" + WebSocketService.WS_PUMP_LAYOUT_DESTINATION)) {
            webSocketService.sendPumpLayoutToUser(pumpService.getAllPumps(), event.getUser().getName());
        }
        if(Objects.equals(simpDestination, "/user" + WebSocketService.WS_ACTIONS_STATUS_DESTINATION)) {
            webSocketService.sendRunningEventActionsStatusToUser(eventService.getRunningActionsInformation(), event.getUser().getName());
        }

        final String userPumpRunningStateDestination = "/user" + WebSocketService.WS_PUMP_RUNNING_STATE_DESTINATION + "/";
        if(simpDestination.startsWith(userPumpRunningStateDestination)) {
            long pumpId;
            try {
                String stringActionId = simpDestination.substring(userPumpRunningStateDestination.length());
                pumpId = Long.parseLong(stringActionId);
            } catch (NumberFormatException e) {
                pumpId = -1L;
            }

            webSocketService.sendPumpRunningStateToUser(pumpId, maintenanceService.getState(), event.getUser().getName());
        }

        final String userDestinationActionsLogDestination = "/user" + WebSocketService.WS_ACTIONS_LOG_DESTINATION + "/";
        if(simpDestination.startsWith(userDestinationActionsLogDestination)) {
            long actionId;
            try {
                String stringActionId = simpDestination.substring(userDestinationActionsLogDestination.length());
                actionId = Long.parseLong(stringActionId);
            } catch (NumberFormatException e) {
                actionId = -1L;
            }

            webSocketService.sendEventActionLogToUser(actionId, eventService.getEventActionLog(actionId), event.getUser().getName());
        }
    }
}
