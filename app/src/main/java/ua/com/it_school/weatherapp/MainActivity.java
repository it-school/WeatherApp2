package ua.com.it_school.weatherapp;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    
    Button button;
    TextView textView;
    WebView webView;
    Resources res;
    Document page = null;
    private String FLAG;
    String message;
    WebPageGetter wg = null;
    String url;
    WeatherInfo [] weatherInfo;
    ImageView weatherImage;
    final int NUMBER_OF_FORECSTS = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.buttonLoadData);
        textView = findViewById(R.id.textView);
        weatherImage = findViewById(R.id.weatherImage);
        weatherImage.setBackgroundResource(R.drawable.sun);
        weatherImage.setImageDrawable(null);
        message = "";
        url = "https://www.gismeteo.ua/weather-odessa-4982/legacy/";
        weatherInfo = new WeatherInfo[NUMBER_OF_FORECSTS];


        wg = new WebPageGetter();
        wg.execute();
    }

    public void RefreshWeather() {

    }

    public void btnLoadData(View view) {
        wg = new WebPageGetter();
        wg.execute();
    }



    class WebPageGetter extends AsyncTask<Void, Void, Void>
    {
        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        public void ConnectAndGetData(String url)
        {
            InputStream is = null;

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if(netInfo.isConnected())

            {
                try {
                    is = new URL(url).openStream();
                    try {
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                        try {
                            message = readAll(rd);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                page = Jsoup.connect(url).get();// Connect to the web site
                message = page.text();           // Get the html document title

         //       page = Jsoup.parse(new URL(url), 10000);
         //       message = "| "+page.text()+ " |";

//                textView.setText(message);
            } catch (IOException e) {
            e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
           //ParseWeather();

            Element tableWth = page.select("table").first();
//            textView.setText(tableWth.text());
            Elements dates = tableWth.select("th[colspan=4]"); // даты дней недели для прогноза (их 3)
         //   textView.setText(dates.html() + "\n\n" + dates.outerHtml() + "\n\n" + dates.text());
            int i= 1;
            for (Element date: dates)
            {
                 textView.setText(textView.getText() + "\n" + i++ + ") " + date.text());
            }

            textView.setText(textView.getText() + "\n\nTemperatures");
            i = 1;
            Elements temperatures = tableWth.select("span[class=value m_temp c]");
            for (Element t : temperatures) {
             //   textView.setText(/*textView.getText() + "\n" + i + ") " + */t.text().replace("+",""));
                String tempT = t.text().replace("+","");
                int temp = Integer.parseInt(tempT);
                weatherInfo[i-1] = new WeatherInfo();

                weatherInfo[i-1].setTemperature(temp);

                i++;
                if (i == 13)
                    break;
            }
            textView.setText(textView.getText() + "\n" + weatherInfo[0].getTemperature()+"\n\n------------\n\n");

            // cloudness
            i = 1;
            Elements cloudness = tableWth.select("tr[class=persp]");
            Elements clouds = cloudness.select("img");

            String tempStr="";
            for (Element c : clouds) {
                tempStr = c.attr("src");
            //    textView.setText(textView.getText() + "\n" + i + ") " + tempStr.substring(tempStr.indexOf("old/")+4, tempStr.indexOf("old/")+8));
                weatherInfo[i-1].setDay(tempStr.charAt(0) == 'd' ? true : false);
                weatherInfo[i-1].setCloudness(Integer.parseInt(""+tempStr.charAt(3)));

           /*     String tempT = t.text().replace("+","");
                int temp = Integer.parseInt(tempT);
                weatherInfo[i-1] = new WeatherInfo();

                weatherInfo[i-1].setTemperature(temp);

*/
                i++;
                if (i == 13)
                    break;
            }

            textView.setText(textView.getText() + "\n" + weatherInfo[0].getisDay() + " " + weatherInfo[0].getCloudness());

            if (weatherInfo[0].getisDayBoolean())
                weatherImage.setBackgroundResource(R.drawable.sun);
            else
                weatherImage.setBackgroundResource(R.drawable.moon);


            switch (weatherInfo[0].getCloudness()) {
                case 4:
                    weatherImage.setImageResource(R.drawable.cloud4);
                    break;
                case 3:
                    weatherImage.setImageResource(R.drawable.cloud3);
                    break;
                case 2:
                    weatherImage.setImageResource(R.drawable.cloud2);
                    break;
                case 1:
                    weatherImage.setImageResource(R.drawable.cloud1);
                    break;
                default:
                    weatherImage.setImageDrawable(null);
                    break;


            }



            //            Elements rows = tableWth.select("tr");
/*
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

        @Override
        protected void onCancelled()
        {
            super.onCancelled();
            Log.d("", "Process canceling");
        }
    }
}
