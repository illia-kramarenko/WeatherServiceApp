package com.kramarenko.illia.weatherserviceapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kramarenko.illia.weatherserviceapp.aidl.WeatherCall;
import com.kramarenko.illia.weatherserviceapp.aidl.WeatherData;
import com.kramarenko.illia.weatherserviceapp.utils.Utils;

/**
 * @class WeatherServiceSync
 *
 * @brief This class uses synchronous AIDL interactions to get weather data
 *
 */
public class WeatherServiceSync extends LifecycleLoggingService {
    /**
     * Factory method that makes an Intent used to start the
     * WeatherServiceSync when passed to bindService().
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                WeatherServiceSync.class);
    }

    /**
     * Called when a client calls
     * bindService() with the proper Intent.  Returns the
     * implementation of WeatherCall, which is implicitly cast as
     * an IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mWeatherCallImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface
     * WeatherCall, which extends the Stub class that implements
     * WeatherCall, thereby allowing Android to handle calls across
     * process boundaries.  This method runs in a separate Thread as
     * part of the Android Binder framework.
     *
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    WeatherCall.Stub mWeatherCallImpl = new WeatherCall.Stub() {
        /**
         * Implement the AIDL WeatherCall getCurrentWeather()
         * method, which forwards to DownloadUtils getResults() to
         * obtain the results from the Acronym Web service and
         * then sends the results back to the Activity via a
         * callback.
         */
        @Override
        public WeatherData getCurrentWeather(String city)
                throws RemoteException {

            // Call the Weather Web service to get weather results.
            WeatherData weatherResults = Utils.getWeatherData(city);

            // Invoke a one-way callback to send weather data back to the Activity.
            if (weatherResults != null) {
                Log.d(TAG, "Returning Weather results for " + city);
                return weatherResults;
            } else {
                Log.d(TAG, "Weather results for " + city
                        + " is NULL. Returning empty WeatherData" );
                Utils.showToast(WeatherServiceSync.this, "Weather results for " + city
                        + " is NULL. Returning empty WeatherData");
                weatherResults = new WeatherData();
                return weatherResults;
            }

        }
    };
}
