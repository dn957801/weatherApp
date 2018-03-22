package com.example.dinesh.weatherapp;

/**
 * Created by dinesh on 21/03/18.
 */

public class weatherClass {

    private String cityEntered;
    private String weather_main;
    private String weather_description;
    private double temp=0;
    private double temp_min=0;
    private double temp_max = 0;
    private int humidity=0;
    private int clouds_all;


    public weatherClass( String cityEntered, String weather_main, String weather_description, double temp,
                         double temp_min, double temp_max, int humidity, int clouds_all) {
        this.cityEntered = cityEntered;
        this.weather_main = weather_main;
        this.weather_description = weather_description;
        this.temp = temp;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.humidity = humidity;
        this.clouds_all = clouds_all;
    }


}
