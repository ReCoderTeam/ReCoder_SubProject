package com.ohgiraffers.refactorial.admin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    @Value("${weather.api-key}")
    private String weatherAPIKey;

    @GetMapping("/api/weather-key")
    public String getWeatherApiKey() {
        System.out.println("weatherAPIKey = " + weatherAPIKey);
        return weatherAPIKey;
    }
}
