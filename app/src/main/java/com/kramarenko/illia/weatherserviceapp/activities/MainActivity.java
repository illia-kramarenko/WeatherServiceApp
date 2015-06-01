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
    }

    public void downloadSync(View view){
        Utils.hideKeyboard(this, view.getWindowToken());

        String mCityStr = mInputCity.getText().toString();

        new AsyncTask<String, Void, WeatherData> () {
            /**
             * Acronym we're trying to expand.
             */
            private String mCityStr;

            /**
             * Retrieve the expanded acronym results via a
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
                            mCountry.setText(resultWeatherData.getCountry());
                            mCity.setText(resultWeatherData.getCity());
                            mTemp.setText(String.valueOf(resultWeatherData.getTemp()));
                            mWind.setText(String.valueOf(resultWeatherData.getSpeed()) + " " + String.valueOf(resultWeatherData.getDeg()) );
                            mHumidity.setText(String.valueOf(resultWeatherData.getHumidity()));

                            int id = getResources().getIdentifier("com.kramarenko.illia.weatherserviceapp:drawable/clouds", null, null);
                            mIconView.setImageResource(id);


                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mCountry.setText("Err 404");
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
