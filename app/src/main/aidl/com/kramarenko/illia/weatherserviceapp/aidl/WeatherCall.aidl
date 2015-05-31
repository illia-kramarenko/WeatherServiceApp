// WeatherCall.aidl
package com.kramarenko.illia.weatherserviceapp.aidl;

import com.kramarenko.illia.weatherserviceapp.aidl.WeatherData;
// Declare any non-default types here with import statements

/**
 * Interface defining the method implemented within WeatherServiceSync
 * that provides synchronous access to the Weather Service web
 * service.
 */
interface WeatherCall {
   /**
    * A two-way (blocking) call that retrieves information about the
    * current weather from the Weather Service web service and returns
    * a WeatherData object containing the results from the
    * Weather Service web service back to the WeatherActivity.
    */
    WeatherData getCurrentWeather(in String Weather);
}