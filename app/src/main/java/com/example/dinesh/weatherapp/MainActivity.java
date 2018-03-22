package com.example.dinesh.weatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btnSearch;
    private EditText etEnterCity;
    private TextView etCityName, etMinmax, etTemperature, etMain, etDescription, etHumidity, etClouds;
    private TextView etHumitText, etCloudText;
    private RelativeLayout relativeLayout;
    private String city, min_max;
    private Runnable runnable;
    private static final String TAG = "MyActivity";
    String cityEntered, weather_main, weather_description, icon;
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
        etHumitText = findViewById(R.id.humidText);
        etCloudText = findViewById(R.id.cloudText);
        relativeLayout = findViewById(R.id.relativeLayout);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the city name to get the weather
                city = etEnterCity.getText().toString();

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Invoke function to retrieve the weather information
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

        //build the request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, urlWithBase, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Toast.makeText(getApplicationContext(), "Success !",
                                Toast.LENGTH_SHORT).show();

                        try {

                            cityEntered= response.getString("name");

                            // Retrieve main to fetch the multiple temperature values hidden within it
                            main = response.getJSONObject("main");

                            temp = main.getDouble("temp");
                            temp = convertToCelcius(temp);

                            temp_min = main.getDouble("temp_min");
                            temp_min = convertToCelcius(temp_min);

                            temp_max = main.getDouble("temp_max");
                            temp_max = convertToCelcius(temp_max);


                            humidity = main.getInt("humidity");

                            // Retieve clouds object
                            clouds = response.getJSONObject("clouds");
                            clouds_all =clouds.getInt("all");

                            // Retrieve weather array
                            weather = response.getJSONArray("weather");
                            weather_detail = weather.getJSONObject(0);
                            weather_main = weather_detail.getString("main");
                            weather_description = weather_detail.getString("description");

                            // To display degree symbol
                            // Reference : https://stackoverflow.com/questions/3439517/android-set-degree-symbol-to-textview
                            min_max = "Min " + (int)temp_min + (char) 0x00B0 + "C     Max " + (int)temp_max + (char) 0x00B0 +"C";

                            // To display icon
                            // Reference : https://stackoverflow.com/questions/44177417/how-to-display-openweathermap-weather-icon
                            icon = weather_detail.getString("icon");

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

        // Set the city name entered
        etCityName.setText(cityEntered);

        // Set the temperature

        // To display degree, in all places below
        // Reference : https://stackoverflow.com/questions/3439517/android-set-degree-symbol-to-textview
        junk = String.valueOf((int)temp) + (char) 0x00B0 + "C";
        etTemperature.setText(junk);

        // Set the minimum and maximum temperatures received
        etMinmax.setText(min_max);

        junk = new String(String.valueOf(weather_main));
        if(junk.equals("Clouds")) {
            // Setting background color
            // Reference : https://stackoverflow.com/questions/15592850/setbackgroundcolorint-color-and-relativelayout
            relativeLayout.setBackgroundColor(Color.parseColor("#c8cccc"));
        } else if(junk.equals("Rain")) {
            relativeLayout.setBackgroundColor(Color.parseColor("#e1c7b5"));
        } else {
            relativeLayout.setBackgroundColor(Color.parseColor("#3ba4ea"));
        }

        etMain.setText(junk);
        junk = new String(String.valueOf(weather_description));
        etDescription.setText(junk);
        junk = new Integer(humidity).toString() + "%";
        etHumidity.setText(junk);
        junk = new Integer(clouds_all).toString() + "%";
        etClouds.setText(junk);
        etHumitText.setText("Humidity");
        etCloudText.setText("Clouds");

        /**
        // Display the weather icon
        icon_url = icon_url + icon + ".png";
        //String uri = icon_url + "/" + icon + ".png";
        Log.i(TAG, "URI = " + icon_url);
        try {
            URL url = new URL(icon_url);
            try {
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                etIconDetail.setImageBitmap(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
    }

    double convertToCelcius (double temp) {
        temp -= 273.15;
        return temp;
    }
}
