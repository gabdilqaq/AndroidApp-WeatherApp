package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText userfield;
    private Button main_btn;
    private TextView gettext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userfield = findViewById(R.id.userfield);
        main_btn = findViewById(R.id.main_btn);
        gettext = findViewById(R.id.gettext);

        main_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(userfield.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this, R.string.empty_input,Toast.LENGTH_LONG);
                }
                else{
                    String city = userfield.getText().toString();
                    String key = "3d87d5072814f19c58eaac54a9f50ff8";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+key+"&lang=ru&units=metric";

                    new GetURLData().execute(url);
                }
            }
        });

    }
    private class GetURLData extends AsyncTask<String,String,String>{

        protected void onPreExecute(){
            super.onPreExecute();
            gettext.setText("Загрузка...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while((line = reader.readLine()) !=null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                if(connection!=null) connection.disconnect();
                if(reader!=null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jobj = new JSONObject(result);
                int temp = (int)Math.ceil((double)jobj.getJSONObject("main").getDouble("temp"));
                int feels_like = (int)Math.ceil(jobj.getJSONObject("main").getDouble("feels_like"));
//                String desc = jobj.get("weather").("description").toString();
                gettext.setText("Температура: "+temp+"°C\n" +
                                "Чувствуется как: "+feels_like+"°C\n"
//                                desc
                                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}