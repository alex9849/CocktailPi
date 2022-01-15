package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.Pump;
import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.payload.dto.cocktail.CocktailprogressDto;
import net.alex9849.cocktailmaker.payload.dto.pump.PumpDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private static final String WS_COCKTAIL_DESTINATION = "/topic/cocktailprogress";
    private static final String WS_PUMP_LAYOUT_DESTINATION = "/topic/pumplayout";

    public void broadcastCurrentCocktailProgress(@Nullable Cocktailprogress cocktailprogress) {
        Object cocktailprogressDto = "DELETE";
        if(cocktailprogress != null) {
            cocktailprogressDto = new CocktailprogressDto(cocktailprogress);
        }
        simpMessagingTemplate.convertAndSend(WS_COCKTAIL_DESTINATION, cocktailprogressDto);
    }

    public void sendCurrentCocktailProgessToUser(@Nullable Cocktailprogress cocktailProgress, String name) {
        Object cocktailprogressDto = "DELETE";
        if(cocktailProgress != null) {
            cocktailprogressDto = new CocktailprogressDto(cocktailProgress);
        }
        simpMessagingTemplate.convertAndSendToUser(name, WS_COCKTAIL_DESTINATION, cocktailprogressDto);
    }

    public void broadcastPumpLayout(List<Pump> pumps) {
        simpMessagingTemplate.convertAndSend(WS_PUMP_LAYOUT_DESTINATION, pumps.stream().map(PumpDto::new).collect(Collectors.toList()));
    }

    public void sendPumpLayoutToUser(List<Pump> pumps, String username) {
        simpMessagingTemplate.convertAndSendToUser(username, WS_PUMP_LAYOUT_DESTINATION,
                pumps.stream().map(PumpDto::new).collect(Collectors.toList()));
    }
}
