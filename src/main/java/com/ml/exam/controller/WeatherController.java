package com.ml.exam.controller;

import com.ml.exam.model.Weather;
import com.ml.exam.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/weather")
@RestController
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "{day}")
    public ResponseEntity<?> getWeatherByDay(@PathVariable(value = "day") Integer day) {
        Weather weather = weatherService.getWeather(day);

        if(weather != null) {
            return ResponseEntity.ok(weather);
        }

        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getWeather() {
        return ResponseEntity.ok(weatherService.getAll());
    }
}
