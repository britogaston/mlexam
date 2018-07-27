package com.ml.exam.service;

import com.ml.exam.model.Weather;
import com.ml.exam.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    public Weather getWeather(Integer day) {
        return weatherRepository.findById(day)
                .orElse(null);
    }

    public List<Weather> getAll() {
        return (List<Weather>) weatherRepository.findAll();
    }
}
