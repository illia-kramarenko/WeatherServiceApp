package com.kramarenko.illia.weatherserviceapp.activities;

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



    // TODO - make layout

    // TODO - FORMAT OUTPUT DATA

    // TODO - refactor Weather Operations

    // TODO - Test Services

    // TODO - config changes

    private EditText mInputCity;

    private ImageView mIconView;

    private TextView mCountry;
    private TextView mCity;
    private TextView mTemp;
    private TextView mWind;
    private TextView mHumidity;
    private TextView mWeather;

    final String DEGREE  = "\u00b0";
    final String DEGREE_CEL  = "\u2103";

    private final String iconsResPath = "com.kramarenko.illia.weatherserviceapp:drawable/icon";


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
    }

    public void downloadSync(View view){
        Utils.hideKeyboard(this, view.getWindowToken());

        String mCityStr = mInputCity.getText().toString();

        new AsyncTask<String, Void, WeatherData> () {

            private String mCityStr;
            /**
             * Retrieve the weather results via a
             * synchronous two-way method call, which runs in a
             * background thread to avoid blocking the UI thread.
             */
            protected WeatherData doInBackground(String... city) {
                mCityStr = city[0];
                return Utils.getWeatherData(mCityStr);
            }

            /**
             * Display the results in the UI Thread.
             */
            protected void onPostExecute(WeatherData resultWeatherData) {
                if (resultWeatherData != null) {
                    Log.d(TAG, "RECIEVED resultWeatherData:" + resultWeatherData.toString());
                    if (!resultWeatherData.isEmpty()) {
                        try {
                           setResults(resultWeatherData);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else {
                       locNotFound();
                    }
                } else {
                    Log.d(TAG, "resultWeatherData = NULL NULL NULL NULL NULL NULL NULL ");
                }
            }
            // Execute the AsyncTask to expand the acronym without
            // blocking the caller.
        }.execute(mCityStr);


    }

    public void downloadAsync(View view) {
        Utils.hideKeyboard(this, view.getWindowToken());
        Toast.makeText(this, "Wow. Keep your trousers on", Toast.LENGTH_LONG).show();
    }

    private void setResults(WeatherData resultWeatherData){
        mCountry.setText(resultWeatherData.getCountry());
        mCity.setText(resultWeatherData.getCity());
        mTemp.setText(String.valueOf(Math.round(resultWeatherData.getTemp())) + DEGREE_CEL);
        mWind.setText(String.valueOf(Math.round(resultWeatherData.getSpeed())) + " m/s " + calcWindDirection(resultWeatherData.getDeg()));
        mHumidity.setText(String.valueOf(resultWeatherData.getHumidity()) + "%");
        mWeather.setText(Character.toUpperCase(resultWeatherData.getWeather().charAt(0)) + resultWeatherData.getWeather().substring(1));
        int id = getResources().getIdentifier(iconsResPath + resultWeatherData.getIcon(), null, null);
        mIconView.setImageResource(id);
    }

    private void locNotFound(){
        mCountry.setText("");
        mCity.setText("");
        mTemp.setText("");
        mWind.setText("");
        mHumidity.setText("");
        mWeather.setText("Location not found");
        mIconView.setImageDrawable(null);
    }

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
