package com.kramarenko.illia.weatherserviceapp.utils;


import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.kramarenko.illia.weatherserviceapp.aidl.WeatherData;
import com.kramarenko.illia.weatherserviceapp.jsonweather.WeatherJSONParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
    /**
     * Logging tag used by the debugger.
     */
    private final static String TAG = Utils.class.getCanonicalName();

    /**
     * URL to the Weather web service.
     */
    private final static String sWeather_Web_Service_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=";

    private final static String sWeather_Web_Service_URL_Param =
            "&units=metric";

    /**
     * Obtain the Weather information.
     *
     */
    public static WeatherData getWeatherData(final String city) {
        WeatherData weatherData = null;
        Log.d(TAG, "Entering getWeatherData");
        try {
            // Append the location to create the full URL.
            final URL url = new URL(sWeather_Web_Service_URL + city + sWeather_Web_Service_URL_Param);

            // Opens a connection to the Acronym Service.
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Sends the GET request and reads the Json results.
            try (InputStream in = new BufferedInputStream(urlConnection.getInputStream())) {

                final WeatherJSONParser parser = new WeatherJSONParser();

                weatherData = parser.parseJsonStream(in);
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weatherData;
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing.
     */
    public static void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                0);
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    }


}
