package com.kramarenko.illia.weatherserviceapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kramarenko.illia.weatherserviceapp.aidl.WeatherData;
import com.kramarenko.illia.weatherserviceapp.aidl.WeatherRequest;
import com.kramarenko.illia.weatherserviceapp.aidl.WeatherResults;
import com.kramarenko.illia.weatherserviceapp.utils.Utils;

/**
 * @class WeatherServiceSync
 *
 * @brief This class uses asynchronous AIDL interactions to get weather data
 *
 */
public class WeatherServiceAsync extends LifecycleLoggingService {
    /**
     * Factory method that makes an Intent used to start the
     * WeatherServiceSync when passed to bindService().
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                WeatherServiceAsync.class);
    }

    /**
     * Called when a client calls
     * bindService() with the proper Intent.  Returns the
     * implementation of WeatherRequest, which is implicitly cast as
     * an IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mWeatherRequestImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface
     * WeatherRequest, which extends the Stub class that implements
     * WeatherRequest, thereby allowing Android to handle calls across
     * process boundaries.  This method runs in a separate Thread as
     * part of the Android Binder framework.
     *
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    WeatherRequest.Stub mWeatherRequestImpl = new WeatherRequest.Stub() {
        /**
         * Implement the AIDL WeatherRequest getCurrentWeather()
         * method, which forwards to DownloadUtils getResults() to
         * obtain the results from the Acronym Web service and
         * then sends the results back to the Activity via a
         * callback.
         */
        @Override
        public void getCurrentWeather(String city, WeatherResults callback)
                throws RemoteException {

            // Call the Weather Web service to get weather results.
            WeatherData weatherResults = Utils.getWeatherData(city);

            // Invoke a one-way callback to send weather data back to the Activity.
            if (weatherResults != null) {
                Log.d(TAG, "Sending Weather results for " + city);
                callback.sendResults(weatherResults);
            } else {
                callback.sendError("Weather Results for "
                        + city
                        + " is NULL somehow");
            }
        }
    };
}
