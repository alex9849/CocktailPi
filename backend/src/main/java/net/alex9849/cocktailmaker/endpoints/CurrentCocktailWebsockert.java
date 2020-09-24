package net.alex9849.cocktailmaker.endpoints;

import net.alex9849.cocktailmaker.service.CocktailFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CurrentCocktailWebsockert {

    @Autowired
    private CocktailFactoryService cocktailFactoryService;

    /*@MessageMapping("/cocktailprogress")
    @SendToUser("/topic/cocktailprogress")
    public Object sendWelcomeMessage(Principal user) {
        return cocktailFactoryService.getCurrentProgress();
    }*/
}
