package com.kramarenko.illia.weatherserviceapp.jsonweather;


public class JsonWeatherTokens {

    public static final String SYS = "sys";
    public static final String COUNTRY = "country";
    public static final String SUNRISE = "sunrise";
    public static final String SUNSET = "sunset";

    public static final String WEATHER = "weather";
    public static final String DESCRIPTION = "description";

    public static final String MAIN = "main";
    public static final String TEMP = "temp";
    public static final String HUMIDITY = "humidity";

    public static final String WIND = "wind";
    public static final String SPEED = "speed";
    public static final String DEG = "deg";

    public static final String NAME = "name";


    /**
     * Ensure this class is only used as a support.
     */
    private JsonWeatherTokens() {
        throw new AssertionError();
    }
}
