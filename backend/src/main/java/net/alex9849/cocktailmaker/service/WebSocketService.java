package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.pump.Pump;
import net.alex9849.cocktailmaker.model.cocktail.CocktailProgress;
import net.alex9849.cocktailmaker.model.eventaction.EventActionInformation;
import net.alex9849.cocktailmaker.model.eventaction.RunningAction;
import net.alex9849.cocktailmaker.payload.dto.cocktail.CocktailProgressDto;
import net.alex9849.cocktailmaker.payload.dto.eventaction.EventActionDto;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
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
    public static final String WS_PUMP_LAYOUT_DESTINATION = "/topic/pumplayout";
    public static final String WS_ACTIONS_STATUS_DESTINATION = "/topic/eventactionstatus";
    public static final String WS_ACTIONS_LOG_DESTINATION = "/topic/eventactionlog";

    public void broadcastCurrentCocktailProgress(@Nullable CocktailProgress cocktailprogress) {
        Object cocktailprogressDto = "DELETE";
        if(cocktailprogress != null) {
            cocktailprogressDto = new CocktailProgressDto.Response.Detailed(cocktailprogress);
        }
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).collect(Collectors.toList());
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_COCKTAIL_DESTINATION, cocktailprogressDto);
        }
    }

    public void sendCurrentCocktailProgessToUser(@Nullable CocktailProgress cocktailProgress, String name) {
        Object cocktailProgressDto = "DELETE";
        if(cocktailProgress != null) {
            cocktailProgressDto = new CocktailProgressDto.Response.Detailed(cocktailProgress);
        }
        simpMessagingTemplate.convertAndSendToUser(name, WS_COCKTAIL_DESTINATION, cocktailProgressDto);
    }

    public void broadcastPumpLayout(List<Pump> pumps) {
        List<PumpDto.Response.Detailed> pumpDtos = pumps.stream().map(PumpDto.Response.Detailed::new).collect(Collectors.toList());
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).collect(Collectors.toList());
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_PUMP_LAYOUT_DESTINATION, pumpDtos);
        }
    }

    public void sendPumpLayoutToUser(List<Pump> pumps, String username) {
        simpMessagingTemplate.convertAndSendToUser(username, WS_PUMP_LAYOUT_DESTINATION,
                pumps.stream().map(PumpDto.Response.Detailed::new).collect(Collectors.toList()));
    }

    public void broadcastRunningEventActionsStatus(List<EventActionInformation> eai) {
        List<EventActionDto.Response.RunInformation> pumpDtos = eai.stream()
                .map(EventActionDto.Response.RunInformation::new).collect(Collectors.toList());
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).collect(Collectors.toList());
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_STATUS_DESTINATION, pumpDtos);
        }
    }

    public void sendRunningEventActionsStatusToUser(List<EventActionInformation> eai, String username) {
        simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_STATUS_DESTINATION,
                eai.stream().map(EventActionDto.Response.RunInformation::new).collect(Collectors.toList()));
    }

    public void broadcastClearEventActionLog(long runningActionId) {
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).collect(Collectors.toList());
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_LOG_DESTINATION + "/" + runningActionId, "DELETE");
        }
    }
    public void broadcastEventActionLog(long runningActionId, List<RunningAction.LogEntry> logEntries) {
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).collect(Collectors.toList());
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_LOG_DESTINATION + "/" + runningActionId, logEntries);
        }
    }

    public void sendEventActionLogToUser(long runningActionId, List<RunningAction.LogEntry> logEntries, String username) {
        simpMessagingTemplate.convertAndSendToUser(username, WS_ACTIONS_LOG_DESTINATION + "/" + runningActionId,
                logEntries);
    }


}
