package net.alex9849.cocktailpi.config.websocket;

import lombok.SneakyThrows;
import net.alex9849.cocktailpi.service.EventService;
import net.alex9849.cocktailpi.service.GlassService;
import net.alex9849.cocktailpi.service.WebSocketService;
import net.alex9849.cocktailpi.service.pumps.CocktailOrderService;
import net.alex9849.cocktailpi.service.pumps.PumpDataService;
import net.alex9849.cocktailpi.service.pumps.PumpMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Objects;

@Component
public class WebSocketEventListener implements ApplicationListener<SessionSubscribeEvent> {

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private PumpDataService pumpService;

    @Autowired
    private PumpMaintenanceService maintenanceService;

    @Autowired
    private EventService eventService;

    @Autowired
    private CocktailOrderService cocktailOrderService;
    @Autowired
    private GlassService glassService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        final String simpDestination = (String) event.getMessage().getHeaders().get("simpDestination");
        if (simpDestination == null) {
            return;
        }

        /*
                Sleep for a few milliseconds. When I don't do this, the client sometimes looses messages
                that are sent on message subscribe. I don't know why this happens. Maybe the client isn't
                ready yet and misses the message? I hate this solution.
            */
        Thread.sleep(10);
        if (Objects.equals(simpDestination, "/user" + WebSocketService.WS_COCKTAIL_DESTINATION)) {
            webSocketService.sendCurrentCocktailProgessToUser(cocktailOrderService.getCurrentCocktailProgress(), event.getUser().getName());
        }
        if (Objects.equals(simpDestination, "/user" + WebSocketService.WS_PUMP_LAYOUT_DESTINATION)) {
            webSocketService.sendPumpLayoutToUser(pumpService.getAllPumps(), event.getUser().getName());
        }
        if (Objects.equals(simpDestination, "/user" + WebSocketService.WS_ACTIONS_STATUS_DESTINATION)) {
            webSocketService.sendRunningEventActionsStatusToUser(eventService.getRunningActionsInformation(), event.getUser().getName());
        }
        if (Objects.equals(simpDestination, "/user" + WebSocketService.WS_DISPENSING_AREA)) {
            webSocketService.sendDetectedGlassToUser(glassService.getDispensingAreaState(), event.getUser().getName());
        }

        final String userPumpRunningStateDestination = "/user" + WebSocketService.WS_PUMP_RUNNING_STATE_DESTINATION + "/";
        if (simpDestination.startsWith(userPumpRunningStateDestination)) {
            long pumpId;
            try {
                String stringActionId = simpDestination.substring(userPumpRunningStateDestination.length());
                pumpId = Long.parseLong(stringActionId);
            } catch (NumberFormatException e) {
                pumpId = -1L;
            }
            webSocketService.sendPumpRunningStateToUser(pumpId, maintenanceService.getJobStateByPumpId(pumpId), event.getUser().getName());
        }

        final String userDestinationActionsLogDestination = "/user" + WebSocketService.WS_ACTIONS_LOG_DESTINATION + "/";
        if (simpDestination.startsWith(userDestinationActionsLogDestination)) {
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
