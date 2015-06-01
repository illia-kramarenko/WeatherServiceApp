package com.kramarenko.illia.weatherserviceapp.jsonweather;


import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.kramarenko.illia.weatherserviceapp.aidl.WeatherData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WeatherJSONParser {

    private final String TAG =
            this.getClass().getCanonicalName();

    private WeatherData weatherData = null;


    public WeatherJSONParser(){
        weatherData = new WeatherData();
    }

    public WeatherData parseJsonStream(InputStream inputStream)
            throws IOException {
        Log.d(TAG, "Entering parseJsonStream");
        // Create a JsonReader for the inputStream.
        try (JsonReader reader =
                     new JsonReader(new InputStreamReader(inputStream,
                             "UTF-8"))) {

            // Handle the array returned from the Weather Service.
            parseWeatherServiceResults(reader);
            Log.d(TAG, "RETURNING weatherData:" + weatherData.toString());
            return weatherData ;
        }
    }


    private void parseWeatherServiceResults(JsonReader reader)
            throws IOException {

        Log.d(TAG, "Entering parse Weather Service Results");
        reader.beginObject();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case JsonWeatherTokens.SYS:
                        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                            parseWeatherFormSys(reader);
                            break;
                        }
                    case JsonWeatherTokens.WEATHER:
                        parseWeatherFormWeather(reader);
                        break;
                    case JsonWeatherTokens.MAIN:
                        parseWeatherFormMain(reader);
                        break;
                    case JsonWeatherTokens.WIND:
                        parseWeatherFormWind(reader);
                        break;
                    case JsonWeatherTokens.NAME:
                        weatherData.setCity(reader.nextString());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        } finally {
            reader.endObject(); //Caused by: java.lang.IllegalStateException: Expected END_OBJECT but was NUMBER
        }
    }


    private void parseWeatherFormSys(JsonReader reader)
            throws IOException {
        Log.d(TAG, "Entering parseWeatherFormSys");
        reader.beginObject();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case JsonWeatherTokens.COUNTRY:
                        weatherData.setCountry(reader.nextString());
                        break;
                    case JsonWeatherTokens.SUNRISE:
                        weatherData.setSunrise(reader.nextLong());
                        break;
                    case JsonWeatherTokens.SUNSET:
                        weatherData.setSunset(reader.nextLong());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        } finally {
            reader.endObject();
        }
    }

    private void parseWeatherFormWeather(JsonReader reader)
        throws IOException{
        Log.d(TAG, "Entering parseWeatherFormWeather");

        reader.beginArray();
        reader.beginObject();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case JsonWeatherTokens.DESCRIPTION:
                        weatherData.setWeather(reader.nextString());
                        break;
                    case JsonWeatherTokens.ICON:
                        weatherData.setIcon(reader.nextString());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        } finally {
            reader.endObject();
            reader.endArray();
        }

    }

    private void parseWeatherFormMain(JsonReader reader)
        throws IOException{
        Log.d(TAG, "Entering parseWeatherFormMain");

        reader.beginObject();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case JsonWeatherTokens.TEMP:
                        weatherData.setTemp(reader.nextDouble());
                        break;
                    case JsonWeatherTokens.HUMIDITY:
                        weatherData.setHumidity(reader.nextLong());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        } finally {
            reader.endObject();
        }
    }

    private void parseWeatherFormWind(JsonReader reader)
        throws IOException{
        Log.d(TAG, "Entering parseWeatherFormWind");
        reader.beginObject();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case JsonWeatherTokens.SPEED:
                        weatherData.setSpeed(reader.nextDouble());
                        break;
                    case JsonWeatherTokens.DEG:
                        weatherData.setDeg(reader.nextDouble());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        } finally {
            reader.endObject();
        }
    }
}
