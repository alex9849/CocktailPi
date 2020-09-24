package net.alex9849.cocktailmaker.service;

import net.alex9849.cocktailmaker.model.cocktail.Cocktailprogress;
import net.alex9849.cocktailmaker.payload.dto.cocktail.CocktailprogressDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Controller
@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private static final String WS_COCKTAIL_DESTINATION = "/topic/cocktailprogress";

    public void broadcastCurrentCocktail(@Nullable Cocktailprogress cocktailprogress) {
        Object cocktailprogressDto = "DELETE";
        if(cocktailprogress != null) {
            cocktailprogressDto = new CocktailprogressDto(cocktailprogress);
        }
        simpMessagingTemplate.convertAndSend(WS_COCKTAIL_DESTINATION, cocktailprogressDto);
    }
}
