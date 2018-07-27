package com.ml.exam.repository;

import com.ml.exam.model.Weather;
import org.springframework.data.repository.CrudRepository;

public interface WeatherRepository extends CrudRepository<Weather, Integer> {

}
