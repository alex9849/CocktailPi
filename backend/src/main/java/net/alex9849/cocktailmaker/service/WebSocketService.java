package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.model.eventaction.EventActionInformation;
import net.alex9849.cocktailmaker.payload.dto.cocktail.CocktailprogressDto;
import net.alex9849.cocktailmaker.payload.dto.eventaction.EventActionInformationDto;
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

    private static final String WS_COCKTAIL_DESTINATION = "/topic/cocktailprogress";
    private static final String WS_PUMP_LAYOUT_DESTINATION = "/topic/pumplayout";
    private static final String WS_RUNNING_ACTIONS_DESTINATION = "topic/eventactionstatus";

    public void broadcastCurrentCocktailProgress(@Nullable Cocktailprogress cocktailprogress) {
        Object cocktailprogressDto = "DELETE";
        if(cocktailprogress != null) {
            cocktailprogressDto = new CocktailprogressDto(cocktailprogress);
        }
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).collect(Collectors.toList());
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_COCKTAIL_DESTINATION, cocktailprogressDto);
        }
    }

    public void sendCurrentCocktailProgessToUser(@Nullable Cocktailprogress cocktailProgress, String name) {
        Object cocktailprogressDto = "DELETE";
        if(cocktailProgress != null) {
            cocktailprogressDto = new CocktailprogressDto(cocktailProgress);
        }
        simpMessagingTemplate.convertAndSendToUser(name, WS_COCKTAIL_DESTINATION, cocktailprogressDto);
    }

    public void broadcastPumpLayout(List<Pump> pumps) {
        List<PumpDto> pumpDtos = pumps.stream().map(PumpDto::new).collect(Collectors.toList());
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).collect(Collectors.toList());
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_PUMP_LAYOUT_DESTINATION, pumpDtos);
        }
    }

    public void sendPumpLayoutToUser(List<Pump> pumps, String username) {
        simpMessagingTemplate.convertAndSendToUser(username, WS_PUMP_LAYOUT_DESTINATION,
                pumps.stream().map(PumpDto::new).collect(Collectors.toList()));
    }

    public void broadcastRunningEventActionsStatus(List<EventActionInformation> eai) {
        List<EventActionInformationDto> pumpDtos = eai.stream()
                .map(EventActionInformationDto::new).collect(Collectors.toList());
        List<String> subscribers = simpUserRegistry.getUsers().stream()
                .map(SimpUser::getName).collect(Collectors.toList());
        for(String username : subscribers) {
            simpMessagingTemplate.convertAndSendToUser(username, WS_RUNNING_ACTIONS_DESTINATION, pumpDtos);
        }
    }

    public void sendRunningEventActionsStatusToUser(List<EventActionInformation> eai, String username) {
        simpMessagingTemplate.convertAndSendToUser(username, WS_RUNNING_ACTIONS_DESTINATION,
                eai.stream().map(EventActionInformationDto::new).collect(Collectors.toList()));
    }


}
