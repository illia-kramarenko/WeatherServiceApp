package com.kramarenko.illia.weatherserviceapp.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kramarenko.illia.weatherserviceapp.R;
import com.kramarenko.illia.weatherserviceapp.aidl.WeatherData;
import com.kramarenko.illia.weatherserviceapp.jsonweather.WeatherJSONParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class MainActivity extends LifecycleLoggingActivity {

    private EditText mInputCity;

    private Button mWeatherSyncButton;

    private Button mWeatherAsyncButton;

    private TextView mResultTextView;

    private final static String sWeather_Web_Service_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputCity = (EditText) findViewById(R.id.editText);
        mWeatherSyncButton = (Button) findViewById(R.id.buttonSync);
        mWeatherAsyncButton = (Button) findViewById(R.id.buttonAsync);
        mResultTextView = (TextView) findViewById(R.id.resultText);
    }

    public void downloadSync(View v){
        hideKeyboard();

        String mCity = mInputCity.getText().toString();

        new AsyncTask<String, Void, WeatherData> () {
            /**
             * Acronym we're trying to expand.
             */
            private String mCity;

            /**
             * Retrieve the expanded acronym results via a
             * synchronous two-way method call, which runs in a
             * background thread to avoid blocking the UI thread.
             */
            protected WeatherData doInBackground(String... city) {
                mCity = city[0];
                return getWeather(mCity);
            }

            /**
             * Display the results in the UI Thread.
             */
            protected void onPostExecute(WeatherData resultWeatherList) {
                if (resultWeatherList != null) {
                    if (!resultWeatherList.isEmpty()) {
                        try {
                            mResultTextView.setText("Country:" + resultWeatherList.getCountry()
                                            + ", sunrise:" + resultWeatherList.getSunrise()
                                            + ", sunset:" + resultWeatherList.getSunset()
                                            + ", weather:" + resultWeatherList.getWeather()
                                            + ", temp:" + resultWeatherList.getTemp()
                                            + ", humidity:" + resultWeatherList.getHumidity()
                                            + ", speed:" + resultWeatherList.getSpeed()
                                            + ", deg:" + resultWeatherList.getDeg()
                                            + ", city:" + resultWeatherList.getCity()

                            );
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mResultTextView.setText("Err 404");
                    }
                } else {
                    Log.d(TAG, "resultWeatherList = NULL NULL NULL NULL NULL NULL NULL ");
                }
            }
            // Execute the AsyncTask to expand the acronym without
            // blocking the caller.
        }.execute(mCity);


    }

    public void downloadAsync(View view) {
        hideKeyboard();
        Toast.makeText(this, "Wow. Keep your trousers on", Toast.LENGTH_LONG).show();
    }


    private WeatherData getWeather(String city){

        WeatherData weatherData = null;

        try {
            // Append the location to create the full URL.
            final URL url = new URL(sWeather_Web_Service_URL + city);

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


    protected void hideKeyboard() {
        InputMethodManager mgr =
                (InputMethodManager) getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mInputCity.getWindowToken(),
                0);
    }


    /**====================MENU(idk if i need it)====================================*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}