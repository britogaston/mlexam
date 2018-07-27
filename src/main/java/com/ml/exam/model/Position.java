package com.ml.exam.model;

import java.text.DecimalFormat;

public class Position {

    //saco los decimales para aumentar dias con lluvia, sequia, optimos, etc
    private static final DecimalFormat df = new DecimalFormat("#");

    private Double x;
    private Double y;

    public Position(Double x, Double y) {
        this.x = Double.valueOf(df.format(x));
        this.y = Double.valueOf(df.format(y));
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
