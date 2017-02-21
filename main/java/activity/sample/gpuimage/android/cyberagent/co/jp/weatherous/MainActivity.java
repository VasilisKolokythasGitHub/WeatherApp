package activity.sample.gpuimage.android.cyberagent.co.jp.weatherous;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import static activity.sample.gpuimage.android.cyberagent.co.jp.weatherous.R.id.nameTextView;



public class MainActivity extends AppCompatActivity implements LocationListener{


    static TextView placeTextView;
    static ImageView temperatureTextView;
    static TextView city;
    static TextView descr;


    private GoogleApiClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);






        placeTextView = (TextView) findViewById(nameTextView);
        city = (TextView) findViewById(R.id.city);
        descr = (TextView) findViewById(R.id.description);
        temperatureTextView = (ImageView) findViewById(R.id.temperatureTextView);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "nexa_light.otf");
        Typeface bold = Typeface.createFromAsset(getAssets(),
                "nexa_bold.otf");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);







        city.setTypeface(face);
        descr.setTypeface(bold);
        placeTextView.setTypeface(face);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, new Listener());

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new Listener());

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Double lat = 0.0, lon = 0.0;


        if (location == null) {

        }
        if (location != null) {
            handleLatLng(location.getLatitude(), location.getLongitude());
            lat = location.getLatitude();
            lon = location.getLongitude();
        }

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        final Double finalLat = lat;
        final Double finalLon = lon;
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                getData task = new getData();

                task.execute("http://api.openweathermap.org/data/2.5/weather?lat=" + finalLat + "&lon=" + finalLon + "&APPID=YOUR-APP-ID-HERE");
                swipeView.setRefreshing(false);

            }
        });


        getData task = new getData();
        task.execute("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&APPID=YOUR-APP-ID-HERE");



        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }



    private void handleLatLng(double latitude, double longitude) {
        Log.v("TAG", "(" + latitude + "," + longitude + ")");
    }





    private class Listener implements LocationListener {
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            handleLatLng(latitude, longitude);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

class getData extends AsyncTask<String, String, String> {

    HttpURLConnection urlConnection;

    @Override
    protected String doInBackground(String... urls) {

        StringBuilder result = new StringBuilder();

        try {

            URL url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);

            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }


        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);





        try {
            JSONObject jsonObject = new JSONObject(result);
           JSONArray jsonMainArr = jsonObject.getJSONArray("weather");
           JSONObject childJSONObject = jsonMainArr.getJSONObject(0);
            String il_descr = childJSONObject.getString("description");
            String main = childJSONObject.getString("main");
            switch (main) {
               case "Rain":
                    MainActivity.temperatureTextView.setImageResource(R.drawable.rain);
                    break;
                case "Thunderstorm":
                    MainActivity.temperatureTextView.setImageResource(R.drawable.storm);
                    break;
                case "Snow":
                    MainActivity.temperatureTextView.setImageResource(R.drawable.snow);
                    break;
                case "Mist":
                    MainActivity.temperatureTextView.setImageResource(R.drawable.fog);
                    break;
                case "Few Clouds":
                    MainActivity.temperatureTextView.setImageResource(R.drawable.most_cloud_sun);
                    break;
                case "Clear":
                    MainActivity.temperatureTextView.setImageResource(R.drawable.clear);
                    break;
                default: main = "Clouds";
                    MainActivity.temperatureTextView.setImageResource(R.drawable.clouds);
                    break;

            }

            JSONObject il_temp = jsonObject.getJSONObject("main");
            Double temp = il_temp.getDouble("temp");
            temp = temp-272.15;
            int la_temp = (int)Math.round(temp);  //converts temperature to integer
            MainActivity.descr.setText(il_descr);
            MainActivity.placeTextView.setText("Temperature: "+ la_temp + "°C");

            String polh = jsonObject.getString("name");
            MainActivity.city.setText(polh);

        }catch (Exception e)

        {

            e.printStackTrace();
        }


    }



}
