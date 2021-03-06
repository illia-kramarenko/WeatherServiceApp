package com.kramarenko.illia.weatherserviceapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kramarenko.illia.weatherserviceapp.aidl.WeatherCall;
import com.kramarenko.illia.weatherserviceapp.aidl.WeatherData;
import com.kramarenko.illia.weatherserviceapp.utils.CachedWeatherObject;
import com.kramarenko.illia.weatherserviceapp.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * Used to store cached weather data
     */
    private HashMap<String, CachedWeatherObject> weatherCache = new HashMap<String, CachedWeatherObject>();

    /**
     * Used to clearCache() in a separate thread
     */
    private ExecutorService mExecutorService;

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

    @Override
    public void onCreate(){
        super.onCreate();
        // Create an ExecutorService that manages a pool of threads.
        mExecutorService = Executors.newCachedThreadPool();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Immediately shutdown the ExecutorService.
        mExecutorService.shutdown();
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
            WeatherData weatherResults = checkCachedValues(city);

            //  Clear cache in a separate thread
            final Runnable clearCache =
                    new Runnable() {
                        /**
                         * This method runs in a background Thread.
                         */
                        @Override
                        public void run() {
                            clearCache();
                        }
                    };
            try{
                Log.d(TAG, "TRYING mExecutorService.EXECUTE");
                mExecutorService.execute(clearCache);
            } catch (Exception  e){
                e.printStackTrace();
                Log.d(TAG, "FAILED mExecutorService.EXECUTE");
            }

            // Invoke a one-way callback to send weather data back to the Activity.
            if (weatherResults != null) {
                Log.d(TAG, "Returning Weather results for " + city);
                return weatherResults;
            } else {
                Log.d(TAG, "Weather results for " + city
                        + " is NULL. Returning empty WeatherData" );
                weatherResults = new WeatherData();
                return weatherResults;
            }

        }
    };

    /**
     *
     *  This method checks the cached weather data. If it has
     *  value for specified location and it's not too old,
     *  then returns cached value. If there is no entry for
     *  specified location or it's too old, then get fresh one
     *  and store it into cache.
     *
     *  It is also synchronized to make cache to be thread-safe
     */
    private synchronized WeatherData checkCachedValues(String city){
        if(!weatherCache.isEmpty()){
            //  Access here if Weather Cache HashMap is not empty
            Log.d(TAG, "weatherCache is not empty");
            if(weatherCache.get(city) != null){
                //  Access here if weather Cache has value for specified location
                Log.d(TAG, "Entry for " + city + " is found");
                //  Get stored value
                CachedWeatherObject cachedWeatherObject = weatherCache.get(city);
                //  Check if the entry is too old for display
                if ((System.currentTimeMillis() - cachedWeatherObject.getTimeStamp()) <= 10000L ){
                    //  If NOT too old, then load from cache and return
                    Log.d(TAG, "Weather Results for " + city + " are LOADED FROM Cache");
                    return cachedWeatherObject.getWeatherData();
                } else if ((System.currentTimeMillis() - cachedWeatherObject.getTimeStamp()) > 10000L ){
                    //  If too old then remove old, get fresh and store to cache
                    Log.d(TAG, "Weather Results for " + city + " are too old and removed from Cache. Returning fresh data.");
                    //  Remove old entry
                    weatherCache.remove(city);
                    return getWeatherAndStore(city);
                }
            } else{
                // If Weather Cache HashMap is not empty, but
                // has no entry for specified location.
                Log.d(TAG,
                        "Weather Cache HashMap is not empty, but has no entry for specified location. Returning fresh data.");
                return getWeatherAndStore(city);
            }
        } else {
            //  If Weather Cache HashMap is Empty
            //  Use helper method
            Log.d(TAG,
                    "Cache HashMap is Empty. Returning fresh data.");
            return getWeatherAndStore(city);
        }
        Log.d(TAG, "BAD THINGS APPEAR. Returning fresh data WITHOUT checking");
        return getWeatherAndStore(city);
    }

    /**
     *  Helper method to get fresh weather data and store it into Cache
     */
    private WeatherData getWeatherAndStore(String city){
        Log.d(TAG, "entered getWeatherAndStore()");
        //  Call the Weather Web service to get weather results.
        WeatherData weatherData = Utils.getWeatherData(city);
        //  Put weather result, Location key and a timestamp into cache HashMap
        //  if its not empty and not null
        if (weatherData != null) {
            if (!weatherData.isEmpty())
                weatherCache.put(city, new CachedWeatherObject(weatherData, System.currentTimeMillis()));
            else
                Log.d(TAG, "Weather data for " + city + " is empty. No caching was made.");
        } else {
            Log.d(TAG, "Weather data for " + city + " is NULL. No caching was made.");
        }
        //  Return fresh weather data
        return weatherData;
    }

    /**
     * Check cache for old entries and remove them
     * It is also synchronized to make cache to be thread-safe
     */
     private synchronized void clearCache(){
        Log.d(TAG, "Entered clearCache()");
        if (!weatherCache.isEmpty()) {
            for (Iterator<Map.Entry<String, CachedWeatherObject>> it = weatherCache.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, CachedWeatherObject> entry = it.next();
                String key = entry.getKey();
                long timeStamp = entry.getValue().getTimeStamp();
                if((System.currentTimeMillis() - timeStamp) > 10000L ){
                    it.remove();
                    Log.d(TAG, "Cache Entry for " + key + " was removed for being too old.");
                }
            }
        }
    }

}
