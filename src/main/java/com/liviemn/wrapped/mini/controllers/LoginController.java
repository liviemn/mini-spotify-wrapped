package com.liviemn.wrapped.mini.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/")
    @ResponseBody
    public String homePage() {
        return "<html><body><h2>Welcome to Spotify Wrapped</h2><a href='/loginSpotify'>Login with Spotify</a></body></html>";
    }

    @GetMapping("/loginSpotify")
    public String loginSpotify() {
        return "redirect:/oauth2/authorization/spotify";
    }

    @GetMapping("/success")
    @ResponseBody
    public String loginSuccess() {
        return "<html><body><h2>Login Successful 🎉</h2><p><a href='/top-tracks-html'>View Top Tracks</a></p></body></html>";
    }
}
