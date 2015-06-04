package com.kramarenko.illia.weatherserviceapp.activities;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kramarenko.illia.weatherserviceapp.R;
import com.kramarenko.illia.weatherserviceapp.aidl.WeatherData;
import com.kramarenko.illia.weatherserviceapp.utils.Utils;


public class MainActivity extends LifecycleLoggingActivity {

    // TODO - CACHE

    /**
     *  Used for input location
     */
    private EditText mInputCity;

    /**
     *  Displays icon according to weather data
     */
    private ImageView mIconView;

    /**
     *  Displays received weather data:
     */
    // Country
    private TextView mCountry;
    // City
    private TextView mCity;
    // Temperature
    private TextView mTemp;
    // Wind
    private TextView mWind;
    // Humidity
    private TextView mHumidity;
    // Weather description
    private TextView mWeather;

    // Celsius unicode symbol
    final String DEGREE_CEL  = "\u2103";

    // Weather operations
    private WeatherOperations mWeatherOps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputCity = (EditText) findViewById(R.id.editText);
        mIconView = (ImageView) findViewById(R.id.iconView);

        mCountry = (TextView) findViewById(R.id.country);
        mCity = (TextView) findViewById(R.id.city);
        mTemp = (TextView) findViewById(R.id.temp);
        mWind = (TextView) findViewById(R.id.wind);
        mHumidity = (TextView) findViewById(R.id.humidity);
        mWeather = (TextView) findViewById(R.id.weather);

        // Create the WeatherOperations object one time.
        mWeatherOps = new WeatherOperations(this);
        // Initiate the service binding protocol.
        mWeatherOps.bindService();
    }

    /**
     * Hook method invoked when the screen orientation changes.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "Entering onConfigurationChanged");
        mWeatherOps.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause(){
        if(isFinishing()){
            // Unbind from the Service.
            mWeatherOps.unbindService();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // MOVED

        // Always call super class for necessary operations when an
        // Activity is destroyed.
        super.onDestroy();
    }

    public void downloadSync(View view){
        // Get input
        final String mLocation = mInputCity.getText().toString();
        // Hide keyboard
        Utils.hideKeyboard(this, view.getWindowToken());
        // Get weather sync using AsyncTask
        mWeatherOps.getWeatherSync(mLocation);
    }

    public void downloadAsync(View view) {
        // Get input
        final String mLocation = mInputCity.getText().toString();
        // Hide keyboard
        Utils.hideKeyboard(this, view.getWindowToken());
        // Send async request to service
        mWeatherOps.getWeatherAsync(mLocation);
    }

    /**
     *  Display results
     */
    public void setResults(WeatherData resultWeatherData){
        //Set country
        mCountry.setText(resultWeatherData.getCountry());
        mCity.setText(resultWeatherData.getCity());
        mTemp.setText(String.valueOf(Math.round(resultWeatherData.getTemp())) + DEGREE_CEL);
        mWind.setText(String.valueOf(Math.round(resultWeatherData.getSpeed())) + " m/s " + calcWindDirection(resultWeatherData.getDeg()));
        mHumidity.setText(String.valueOf(resultWeatherData.getHumidity()) + "%");
        mWeather.setText(Character.toUpperCase(resultWeatherData.getWeather().charAt(0)) + resultWeatherData.getWeather().substring(1));
        String iconsResPath = "com.kramarenko.illia.weatherserviceapp:drawable/icon";
        int id = getResources().getIdentifier(iconsResPath + resultWeatherData.getIcon(), null, null);
        mIconView.setImageResource(id);
    }

    /**
     *  Display error if location not found
     */
    public void locNotFound(String errMsg){
        mCountry.setText("");
        mCity.setText("");
        mTemp.setText("");
        mWind.setText("");
        mHumidity.setText("");
        mWeather.setText(errMsg);
        mIconView.setImageDrawable(null);
    }

    /**
     * Format wind degree data to get understandable direction
     */
    private String calcWindDirection(double deg){
        int mDeg = (int) deg;
        if(mDeg > 337 && mDeg < 23) // 338 - 22
            return "N";

        if(mDeg > 22 && mDeg < 68) // 23 - 67
            return "NE";

        if(mDeg > 67 && mDeg < 114) // 68 - 113
            return "E";

        if(mDeg > 113 && mDeg < 158) // 114 - 157
            return "SE";

        if(mDeg > 157 && mDeg < 203) // 158 - 202
            return "S";

        if(mDeg > 202 && mDeg < 248) // 203 - 247
            return "SW";

        if(mDeg > 247 && mDeg < 293) // 248 - 292
            return "W";

        if(mDeg > 292 && mDeg < 338) // 293 - 337
            return "E";

        else return "";
    }
}
