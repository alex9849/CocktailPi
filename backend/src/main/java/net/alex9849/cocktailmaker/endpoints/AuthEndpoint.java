package net.alex9849.cocktailmaker.endpoints;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthEndpoint {

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public void signUp() {

    }

    @RequestMapping(value = "signin", method = RequestMethod.POST)
    public void signIn() {

    }

}
