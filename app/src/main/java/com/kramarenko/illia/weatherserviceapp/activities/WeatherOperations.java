package com.kramarenko.illia.weatherserviceapp.activities;


import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.kramarenko.illia.weatherserviceapp.aidl.WeatherCall;
import com.kramarenko.illia.weatherserviceapp.aidl.WeatherData;
import com.kramarenko.illia.weatherserviceapp.aidl.WeatherRequest;
import com.kramarenko.illia.weatherserviceapp.aidl.WeatherResults;
import com.kramarenko.illia.weatherserviceapp.services.WeatherServiceAsync;
import com.kramarenko.illia.weatherserviceapp.services.WeatherServiceSync;
import com.kramarenko.illia.weatherserviceapp.utils.GenericServiceConnection;

import java.lang.ref.WeakReference;

public class WeatherOperations {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Used to enable garbage collection.
     */
    protected WeakReference<MainActivity> mActivity;

    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the WeatherServiceSync Service using bindService().
     */
    private GenericServiceConnection<WeatherCall> mServiceConnectionSync;

    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the WeatherServiceAsync Service using bindService().
     */
    private GenericServiceConnection<WeatherRequest> mServiceConnectionAsync;

    /**
     * List of results to display (if any).
     */
    protected WeatherData mResults;

    /**
     * This Handler is used to post Runnables to the UI from the
     * mWeatherResults callback methods to avoid a dependency on the
     * Activity, which may be destroyed in the UI Thread during a
     * runtime configuration change.
     */
    private final Handler mDisplayHandler = new Handler();

    /**
     * The implementation of the WeatherResults AIDL Interface, which
     * will be passed to the Weather Web service using the
     * WeatherRequest.getCurrentWeather() method.
     *
     * This implementation of WeatherResults.Stub plays the role of
     * Invoker in the Broker Pattern since it dispatches the upcall to
     * sendResults().
     */
    private final WeatherResults.Stub mWeatherResults =
            new WeatherResults.Stub() {
                /**
                 * This method is invoked by the AcronymServiceAsync to
                 * return the results back to the AcronymActivity.
                 */
                @Override
                public void sendResults(final WeatherData weatherDataResults)
                        throws RemoteException {
                    // Since the Android Binder framework dispatches this
                    // method in a background Thread we need to explicitly
                    // post a runnable containing the results to the UI
                    // Thread, where it's displayed.
                    mDisplayHandler.post(new Runnable() {
                        public void run() {
                            mResults = weatherDataResults;
                            if(mResults.isEmpty()) {
                                mActivity.get().locNotFound("Location was not found");
                            }
                            else {
                                mActivity.get().setResults(mResults);
                            }
                        }
                    });
                }

                /**
                 * This method is invoked by the AcronymServiceAsync to
                 * return error results back to the AcronymActivity.
                 */
                @Override
                public void sendError(final String reason)
                        throws RemoteException {
                    // Since the Android Binder framework dispatches this
                    // method in a background Thread we need to explicitly
                    // post a runnable containing the results to the UI
                    // Thread, where it's displayed.
                    mDisplayHandler.post(new Runnable() {
                        public void run() {
                            mActivity.get().locNotFound(reason);
                        }
                    });
                }
            };

    /**
     * Constructor initializes the fields.
     */
    public WeatherOperations(MainActivity activity) {
        // Initialize the WeakReference.
        mActivity = new WeakReference<>(activity);

        // Initialize the GenericServiceConnection objects.
        mServiceConnectionSync =
                new GenericServiceConnection<WeatherCall>(WeatherCall.class);

        mServiceConnectionAsync =
                new GenericServiceConnection<WeatherRequest>(WeatherRequest.class);
    }

    /**
     * Display results if any (due to runtime configuration change).
     */
    private void updateResultsDisplay() {
        if (mResults != null)
            if(mResults.isEmpty()) {
                mActivity.get().locNotFound("Location was not found");
            }
            else {
                mActivity.get().setResults(mResults);
            }
    }

    /**
     * Called after a runtime configuration change occurs.
     */
    public void onConfigurationChanged(Configuration newConfig) {
        // Checks the orientation of the screen.
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            Log.d(TAG,
                    "Now running in landscape mode");
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            Log.d(TAG,
                    "Now running in portrait mode");
        updateResultsDisplay();
    }

    /**
     * Initiate the service binding protocol.
     */
    public void bindService() {
        Log.d(TAG,
                "calling bindService()");

        // Launch the Acronym Bound Services if they aren't already
        // running via a call to bindService(), which binds this
        // activity to the AcronymService* if they aren't already
        // bound.
        if (mServiceConnectionSync.getInterface() == null)
            mActivity.get().bindService
                    (WeatherServiceSync.makeIntent(mActivity.get()),
                            mServiceConnectionSync,
                            Context.BIND_AUTO_CREATE);

        if (mServiceConnectionAsync.getInterface() == null)
            mActivity.get().bindService
                    (WeatherServiceAsync.makeIntent(mActivity.get()),
                            mServiceConnectionAsync,
                            Context.BIND_AUTO_CREATE);
    }

    /**
     * Initiate the service unbinding protocol.
     */
    public void unbindService() {
        Log.d(TAG,
                "calling unbindService()");

        // Unbind the Async Service if it is connected.
        if (mServiceConnectionAsync.getInterface() != null)
            mActivity.get().unbindService
                    (mServiceConnectionAsync);

        // Unbind the Sync Service if it is connected.
        if (mServiceConnectionSync.getInterface() != null)
            mActivity.get().unbindService
                    (mServiceConnectionSync);
    }


    /**
     * Initiate the asynchronous acronym lookup when the user presses
     * the "Look Up Async" button.
     */
    public void getWeatherAsync(String location) {
        final WeatherRequest weatherRequest =
                mServiceConnectionAsync.getInterface();

        if (weatherRequest != null) {
            try {
                // Invoke a one-way AIDL call, which does not block
                // the client.  The results are returned via the
                // sendResults() method of the mWeatherResults
                // callback object, which runs in a Thread from the
                // Thread pool managed by the Binder framework.
                weatherRequest.getCurrentWeather(location,
                        mWeatherResults);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException:" + e.getMessage());
            }
        } else {
            Log.d(TAG, "weatherRequest was null.");
        }
    }

    /**
     * Initiate the synchronous weather data lookup
     */
    public void getWeatherSync(String location) {
        final WeatherCall weatherCall =
                mServiceConnectionSync.getInterface();

        if (weatherCall != null) {
            // Use an anonymous AsyncTask to download the Acronym data
            // in a separate thread and then display any results in
            // the UI thread.
            new AsyncTask<String, Void, WeatherData>() {
                /**
                 * Location fro weather data.
                 */
                private String mCity;

                /**
                 * Retrieve the location results via a
                 * synchronous two-way method call, which runs in a
                 * background thread to avoid blocking the UI thread.
                 */
                protected WeatherData doInBackground(String... city) {
                    try {
                        mCity = city[0];
                        return weatherCall.getCurrentWeather(mCity);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                /**
                 * Display the results in the UI Thread.
                 */
                protected void onPostExecute(WeatherData weatherDataResult) {
                    mResults = weatherDataResult;
                    if(weatherDataResult.isEmpty())
                        mActivity.get().locNotFound("No location for " + mCity + " was found");
                    else
                        mActivity.get().setResults(mResults);
                }
                // Execute the AsyncTask to get weather without
                // blocking the caller.
            }.execute(location);
        } else {
            Log.d(TAG, "weatherCall was null.");
        }
    }
}
