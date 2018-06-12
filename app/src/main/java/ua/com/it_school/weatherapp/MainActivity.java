package ua.com.it_school.weatherapp;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    String jsonIn, text;
    TextView textView;
    WebView webView;
    Resources res;

    Document page = null;
    private String FLAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        jsonIn = "";//"{\"coord\":{\"lon\":30.73,\"lat\":46.48},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"ясно\",\"icon\":\"01d\"}],\"base\":\"stations\",\"main\":{\"temp\":296.15,\"pressure\":1020,\"humidity\":33,\"temp_min\":296.15,\"temp_max\":296.15},\"visibility\":10000,\"wind\":{\"speed\":3,\"deg\":150},\"clouds\":{\"all\":0},\"dt\":1528381800,\"sys\":{\"type\":1,\"id\":7366,\"message\":0.0021,\"country\":\"UA\",\"sunrise\":1528337103,\"sunset\":1528393643},\"id\":698740,\"name\":\"Odessa\",\"cod\":200}";
        text = "";

        WeatherGetter wg = new WeatherGetter();
        wg.execute();
    }

    public void RefreshWeather()
    {

    }

    public void ParseWeather()
    {
        JSONObject json = null;
        try {
            json = new JSONObject(jsonIn);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        try {
            String temp1 = "";
            JSONObject jsontemp = (JSONObject) json.get("main") ;

            temp1 = "" + (jsontemp.getDouble("temp")-273.15) ;



            SimpleDateFormat sm = new SimpleDateFormat("d.M.Y H:m");  // https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
            sm.setTimeZone(TimeZone.getTimeZone("GMT+3"));
            Date date = new Date(json.getLong("dt")*1000);


            ((TextView)findViewById(R.id.textView)).setText(json.getString ("name")+" ("+sm.format(date) + ": " + temp1+")");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        /*
  String str=null; String input = "данные ";
  JsonParser parser = new JsonParser();
  JsonObject mainObject = parser.parse(input).getAsJsonObject();
  JsonArray pItem = mainObject.getAsJsonArray("p_item");
  for (JsonElement user : pItem)
  {
  JsonObject userObject = user.getAsJsonObject();
  userObject.get("p_id");
  str = userObject.get("p_id").toString();
  }
         */

    }

    public void btnClick(View view) {
        imageView.setImageResource(R.drawable.sun);
        ParseWeather();
    }


    class WeatherGetter extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url="http://api.openweathermap.org/data/2.5/weather?id=698740&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";
            String urlForecast="api.openweathermap.org/data/2.5/forecast?id=698740&appid=dac392b2d2745b3adf08ca26054d78c4&lang=ru";

            InputStream is = null;
            try {
                is = new URL(url).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                try {
                    jsonIn = readAll(rd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*
            url = "http://study.cc.ua";
            try {
                page = Jsoup.parse(new URL(url), 10000);
                jsonIn = "| "+page.text()+ " |";
            } catch (IOException e) {
                e.printStackTrace();
            }

           //textView.setText(textView.getText()+"In2\n");
            try {
                  page = Jsoup.connect(url).get();// Connect to the web site
                  jsonIn = page.text() ;           // Get the html document title

                    //textView.setText(text);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
//            textView.setText(textView.getText()+"In3\n");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //textView.setText("\n------------------\n" + jsonIn+"\n--------------------\n");
           ParseWeather();
/*
            Element tableWth = page.select("table").first();
            Elements dates = tableWth.select("th[colspan=4]"); // даты дней недели для прогноза (их 3)
            Elements rows = tableWth.select("tr");

            // извлекаем даты
            date = "";
            for (Element d : dates)
                date += "\t\t\t" + d.text();

            // извлекаем температуру и темп. по ощущениям
            int i = 0;
            int r = 2;
            Elements temperatures = tableWth.select("span[class=value m_temp c]");
            for (Element t : temperatures) {
                wt[r][i++] = t.text();
                if (i > 12) {
                    r = 6;
                    i = 0;
                }
            }
            */

        }
    }
}
