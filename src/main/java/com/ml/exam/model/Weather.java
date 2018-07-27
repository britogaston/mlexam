package com.ml.exam.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weather weather = (Weather) o;
        return Objects.equals(day, weather.day);
    }

    @Override
    public int hashCode() {

        return Objects.hash(day);
    }
}
