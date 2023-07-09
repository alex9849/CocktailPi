package net.alex9849.cocktailmaker.endpoints;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class CsrfEndpoint {

    @RequestMapping(path = "/api/csrf", method = RequestMethod.GET)
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

}
