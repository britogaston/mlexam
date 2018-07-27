package com.ml.exam.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Weather {

    @Id
    private Integer day;
    private WeatherCondition condition;

    public  Weather() {}

    public Weather(Integer day, WeatherCondition weatherCondition) {
        this.day =  day;
        this.condition = weatherCondition;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public WeatherCondition getCondition() {
        return condition;
    }

    public void setCondition(WeatherCondition condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "day=" + day +
                ", condition=" + condition +
                '}';
    }
}
