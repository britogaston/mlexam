package com.ml.exam.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Planet {

    @Id
    private String name;
    private Double distance;
    private Double speed;
    private Boolean clockwise;

    public Planet() {}

    public Planet(String name, Double distance, Double speed, Boolean clockwise) {
        this.name = name;
        this.distance = distance;
        this.speed = speed;
        this.clockwise = clockwise;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Boolean getClockwise() {
        return clockwise;
    }

    public void setClockwise(Boolean clockwise) {
        this.clockwise = clockwise;
    }
}
