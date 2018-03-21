package com.example.dinesh.weatherapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button btnSearch;
    private EditText etEnterCity;
    private TextView etCityName, etMinmax, etTemperature, etMain, etDescription, etHumidity, etClouds;
    private String city;
    private Runnable runnable;
    private static final String TAG = "MyActivity";
    String cityEntered, weather_main, weather_description;
    double temp=0, temp_min=0, temp_max = 0;
    int humidity=0, clouds_all;
    JSONObject main, clouds, weather_detail;
    JSONArray weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEnterCity = findViewById(R.id.inputCityName);
        btnSearch = findViewById(R.id.btnSearch);
        etCityName = findViewById(R.id.CityName);
        etMinmax = findViewById(R.id.minmax);
        etTemperature = findViewById(R.id.temperature);
        etMain = (TextView) findViewById(R.id.main);
        etDescription = findViewById(R.id.description);
        etHumidity = findViewById(R.id.humidity) ;
        etClouds = findViewById(R.id.clouds);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the city name to get the weather
                city = etEnterCity.getText().toString();
                Log.i(TAG, "DIN onClick = " + city);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "DIN Invoking getWeather");
                        getWeather();
                    }
                };

                //retrieve data on separate thread
                Thread thread = new Thread( null, runnable, "background");
                thread.start();

                //close the soft keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

    }

    public void getWeather() {

        final String url = "https://api.openweathermap.org/data/2.5/weather?q=";
        String urlWithBase = url.concat(TextUtils.isEmpty(city) ? "Halifax" : city);
        urlWithBase = urlWithBase.concat("&APPID=0ad3765829f9e966c939cb84722b598b");

        Log.i(TAG, "DIN Inside getWeather, will build a JSon Obj now");
        Log.i(TAG, "DIN printing urlWithBase = " + urlWithBase);
        //build the request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, urlWithBase, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG, "DIN Respones received ");

                        Toast.makeText(getApplicationContext(), "Success !",
                                Toast.LENGTH_SHORT).show();

                        try {
                            //response1 = response.getJSONArray("name");

                            cityEntered= response.getString("name");
                            Log.i(TAG, "DIN inside try, String a = " + city);

                            main = response.getJSONObject("main");
                            temp = main.getDouble("temp");
                            temp -= 273.15;
                            temp_min = main.getDouble("temp_min");
                            temp_min -= 273.15;
                            temp_max = main.getDouble("temp_max");
                            temp_max -= 273.15;
                            Log.i(TAG, "DIN inside try, Temp = " + temp + " min_temp = " + temp_min + " max_temp = " + temp_max);
                            humidity = main.getInt("humidity");
                            Log.i(TAG, "Humidity = " + humidity + "%");

                            clouds = response.getJSONObject("clouds");
                            clouds_all =clouds.getInt("all");
                            Log.i(TAG, "Cloud all = " + clouds_all + "%");

                            weather = response.getJSONArray("weather");
                            weather_detail = weather.getJSONObject(0);
                            weather_main = weather_detail.getString("main");
                            weather_description = weather_detail.getString("description");

                            Log.i(TAG, "main = " + weather_main + " description = " + weather_description );

                            set_result();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Toast.makeText(getApplicationContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }
        }
        );

        //add the request to the queue
        RequestQueueSingleton.getmInstance(getApplicationContext()).addToRequestQueue(request);
    }

    void set_result() {

        String junk;

        etCityName.setText(cityEntered);
        //etMinmax.setText();
        junk = new Double(temp).toString();
        etTemperature.setText(junk);
        junk = new String(String.valueOf(weather_main));
        etMain.setText(junk);
        junk = new String(String.valueOf(weather_description));
        etDescription.setText(junk);
        junk = new Integer(humidity).toString();
        etHumidity.setText(junk);
        junk = new Integer(clouds_all).toString();
        etClouds.setText(junk);

    }

}
