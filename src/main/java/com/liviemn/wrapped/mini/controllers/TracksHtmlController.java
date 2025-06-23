package com.liviemn.wrapped.mini.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Controller
public class TracksHtmlController {

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @GetMapping("/top-tracks-html")
    public String topTracksHtml(OAuth2AuthenticationToken authentication, Model model) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
            authentication.getAuthorizedClientRegistrationId(),
            authentication.getName()
        );

        if (client == null) {
            return "redirect:/loginSpotify";
        }

        String token = client.getAccessToken().getTokenValue();

        WebClient webClient = WebClient.builder()
            .baseUrl("https://api.spotify.com/v1")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();

        @SuppressWarnings("unchecked")
        Map<String, Object> response = webClient.get()
            .uri("/me/top/tracks?limit=10")
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        List<Map<String, String>> tracks = new ArrayList<>();
        for (Map<String, Object> item : items) {
            String name = (String) item.get("name");

            @SuppressWarnings("unchecked")
            Map<String, Object> album = (Map<String, Object>) item.get("album");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> images = (List<Map<String, Object>>) album.get("images");
            String imageUrl = images.isEmpty() ? "" : (String) images.get(0).get("url");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> artists = (List<Map<String, Object>>) item.get("artists");
            String artistNames = artists.stream()
                .map(artist -> (String) artist.get("name"))
                .reduce((a, b) -> a + ", " + b)
                .orElse("Unknown");

            @SuppressWarnings("unchecked")
            Map<String, Object> externalUrls = (Map<String, Object>) item.get("external_urls");
            String url = (String) externalUrls.get("spotify");

            Map<String, String> trackInfo = new HashMap<>();
            trackInfo.put("name", name);
            trackInfo.put("artist", artistNames);
            trackInfo.put("image", imageUrl);
            trackInfo.put("url", url);
            tracks.add(trackInfo);
        }

        model.addAttribute("tracks", tracks);
        return "top-tracks"; // Thymeleaf template: templates/top-tracks.html
    }
}
