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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText etEnterCity;
    private String city;
    private Runnable runnable;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEnterCity = findViewById(R.id.inputCityName);
        Button btnSearch = findViewById(R.id.btnSearch);

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
                            response = response.getJSONObject("name");
                            // getJSONString ??
                            //currencyList.clear();

                            //get and set currency data ArrayList
                            for (int i = 0; i < response.names().length(); i++) {
                                String key = response.names().getString(i);
                                String value = String.valueOf(response.get(response.names().getString(i).toString()));

                                Log.i(TAG, "DIN key = " + key + "DIN Value = " + value);

                                //double value = Double.parseDouble(response.get(response.names().getString(i)).toString());
                                //currencyList.add(new CurrencyItem(key, value));
                            }
                            //tells adapter that source data set has changed
                            //adapter.notifyDataSetChanged();

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

    }
}
