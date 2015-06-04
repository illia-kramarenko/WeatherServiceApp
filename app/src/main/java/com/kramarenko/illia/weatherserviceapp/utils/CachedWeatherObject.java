package com.kramarenko.illia.weatherserviceapp.utils;

import com.kramarenko.illia.weatherserviceapp.aidl.WeatherData;

/**
 * This class is used to store WeatherData object and
 * a timestamp for caching purposes
 */

public class CachedWeatherObject {

    private WeatherData weatherData;
    private long timeStamp;

    public CachedWeatherObject(WeatherData wData, long tStamp){
        weatherData = wData;
        timeStamp = tStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
    }
}
