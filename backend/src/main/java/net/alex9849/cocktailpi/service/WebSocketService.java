package net.alex9849.cocktailpi.service;

import net.alex9849.cocktailpi.model.Glass;
import net.alex9849.cocktailpi.model.cocktail.CocktailProgress;
import net.alex9849.cocktailpi.model.eventaction.EventActionInformation;
import net.alex9849.cocktailpi.model.eventaction.RunningAction;
import net.alex9849.cocktailpi.model.pump.Pump;
import net.alex9849.cocktailpi.model.pump.PumpJobState;
import net.alex9849.cocktailpi.payload.dto.cocktail.CocktailProgressDto;
import net.alex9849.cocktailpi.payload.dto.eventaction.EventActionDto;
import net.alex9849.cocktailpi.payload.dto.glass.GlassDto;
import net.alex9849.cocktailpi.payload.dto.pump.PumpDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    public static final String WS_COCKTAIL_DESTINATION = "/topic/cocktailprogress";
    public static final String WS_PUMP_LAYOUT_DESTINATION = "/topic/pump/layout";
    public static final String WS_ACTIONS_STATUS_DESTINATION = "/topic/eventactionstatus";
    public static final String WS_ACTIONS_LOG_DESTINATION = "/topic/eventactionlog";
    public static final String WS_ACTIONS_DETECTED_GLASS = "/topic/placedglass";
    public static final String WS_PUMP_RUNNING_STATE_DESTINATION = "/topic/pump/runningstate";
    public static final String WS_UI_STATE_INFOS =  "/topic/uistateinfos";

    public synchronized void broadcastCurrentCocktailProgress(@Nullable CocktailProgress cocktailprogress) {
        Object cocktailprogressDto = "DELETE";
        if(cocktailprogress != null) {
            cocktailprogressDto = new CocktailProgressDto.Response.Detailed(cocktailprogress);
        }
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).toList();
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_COCKTAIL_DESTINATION, cocktailprogressDto);
        }
    }

    public synchronized void sendCurrentCocktailProgessToUser(@Nullable CocktailProgress cocktailProgress, String name) {
        Object cocktailProgressDto = "DELETE";
        if(cocktailProgress != null) {
            cocktailProgressDto = new CocktailProgressDto.Response.Detailed(cocktailProgress);
        }
        simpMessagingTemplate.convertAndSendToUser(name, WS_COCKTAIL_DESTINATION, cocktailProgressDto);
    }

    public synchronized void invalidateRecipeScrollCaches() {
        String message = "INVALIDATE_CACHED_RECIPES";
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).toList();
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_UI_STATE_INFOS, message);
        }
    }

    public synchronized void broadcastPumpLayout(List<Pump> pumps) {
        List<PumpDto.Response.Detailed> pumpDtos = pumps.stream().map(PumpDto.Response.Detailed::toDto).collect(Collectors.toList());
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).toList();
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_PUMP_LAYOUT_DESTINATION, pumpDtos);
        }
    }

    public synchronized void sendPumpLayoutToUser(List<Pump> pumps, String username) {
        simpMessagingTemplate.convertAndSendToUser(username, WS_PUMP_LAYOUT_DESTINATION,
                pumps.stream().map(PumpDto.Response.Detailed::toDto).collect(Collectors.toList()));
    }

    public synchronized void broadcastRunningEventActionsStatus(List<EventActionInformation> eai) {
        List<EventActionDto.Response.RunInformation> pumpDtos = eai.stream()
                .map(EventActionDto.Response.RunInformation::new).collect(Collectors.toList());
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).toList();
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_STATUS_DESTINATION, pumpDtos);
        }
    }

    public synchronized void sendRunningEventActionsStatusToUser(List<EventActionInformation> eai, String username) {
        simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_STATUS_DESTINATION,
                eai.stream().map(EventActionDto.Response.RunInformation::new).collect(Collectors.toList()));
    }

    public synchronized void broadcastClearEventActionLog(long runningActionId) {
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).toList();
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_LOG_DESTINATION + "/" + runningActionId, "DELETE");
        }
    }
    public synchronized void broadcastEventActionLog(long runningActionId, List<RunningAction.LogEntry> logEntries) {
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).toList();
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_LOG_DESTINATION + "/" + runningActionId, logEntries);
        }
    }

    public synchronized void sendEventActionLogToUser(long runningActionId, List<RunningAction.LogEntry> logEntries, String username) {
        simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_LOG_DESTINATION + "/" + runningActionId,
                logEntries);
    }

    public synchronized void sendPumpRunningStateToUser(long pumpId, PumpJobState runningState, String username) {
        simpMessagingTemplate.convertAndSendToUser(username, WS_PUMP_RUNNING_STATE_DESTINATION + "/" + pumpId,
                runningState);
    }


    public synchronized void broadcastPumpRunningState(long pumpId, PumpJobState runningState) {
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).toList();
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_PUMP_RUNNING_STATE_DESTINATION + "/" + pumpId, runningState);
        }
    }

    public synchronized void sendDetectedGlassToUser(Glass currentGlass, String username) {
        Object dto;
        if(currentGlass != null) {
            dto = new GlassDto.Duplex.Detailed(currentGlass);
        } else {
            dto = "DELETE";
        }
        simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_DETECTED_GLASS, dto);
    }

    public void broadcastDetectedGlass(Glass currentGlass) {
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).toList();
        for(String username : subscribers) {
            sendDetectedGlassToUser(currentGlass, username);
        }
    }
}
