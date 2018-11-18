package com.practice.venues;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient client;
    static String stringLatitude = "28.459497"; // initialization
    static String stringLongitude = "77.026634"; // initialization
    String request_url;

    private static final String KEY_LOCATION = "location";

    private Location mLastKnownLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    stringLatitude = String.valueOf(location.getLatitude());
                    stringLongitude = "" + location.getLongitude();
                    Log.i("INFO", "location: location" + location + stringLatitude + stringLongitude);
//                    final String request_url = "https://api.foursquare.com/v2/venues/explore?client_id=LVLTBDJNW0MOJYHODY05KKICBZIBITPPCS2JKCNME1HDE0OW&client_secret=VILS0XSIRC2D22OWTDKZKWTHPLRL5HEV1K1Q5XAXTXROO3BA&v=20180323&ll=" + stringLatitude + "," + stringLongitude + "&query=coffee&limit=10";
                    request_url = "https://api.foursquare.com/v2/venues/explore?client_id=IXBWNFAW3TTSJV4VGAXOCYT0413STW4C5D05ANUPPXRFNOFZ&client_secret=EUTWE0FF51XINYPX14311URNYGIRANWSLC4OJT0W2YCPOO0E&v=20180323&ll=" + stringLatitude + "," + stringLongitude + "&query=restaurant";
                    Log.i("ashhutosh", request_url);

                    RetrieveVenueTask task = new RetrieveVenueTask(request_url);
                    task.execute();
                }
            }
        });
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }


        setContentView(R.layout.activity_main);
        // 1. get a reference to recyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // this is data fro recycler view
        ArrayList<ItemData> itemsData = new ArrayList<ItemData>();
        itemsData.add(new ItemData("Venue 1", "i", "Address"));
        itemsData.add(new ItemData("Venue 2", "i", "Address"));
        itemsData.add(new ItemData("Venue 3", "i", "Address"));
        itemsData.add(new ItemData("Venue 4", "i", "Address"));
        itemsData.add(new ItemData("Venue 5", "i", "Address"));

        // 2. set layoutManger
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 3. create an adapter
        MyAdapter mAdapter = new MyAdapter(recyclerItemClickListener, itemsData);
        // 4. set adapter
        recyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

    }


    public interface RecyclerViewItemClickListener {
        void onClickListenerForItem(int position, ArrayList<ItemData> itemsData);

    }

    RecyclerViewItemClickListener recyclerItemClickListener = new RecyclerViewItemClickListener() {
        @Override
        public void onClickListenerForItem(int position, ArrayList<ItemData> itemsData) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            //intent.putExtra("model", model);
            intent.putExtra("title", itemsData.get(position).getTitle());
            intent.putExtra("imageUrl", itemsData.get(position).getImageId());
            intent.putExtra("address", itemsData.get(position).getAddress());
            startActivity(intent);
        }
    };

    private void updateUI(ArrayList<ItemData> fetchedData) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // this is data fro recycler view
        // 2. set layoutManger
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 3. create an adapter
        MyAdapter mAdapter = new MyAdapter(recyclerItemClickListener, fetchedData);
        // 4. set adapter
        recyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private class RetrieveVenueTask extends AsyncTask<Void, Void, ArrayList<ItemData>> {
        private Exception exception;
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        private String requestUrl;

        public RetrieveVenueTask(String requestUrl) {
            this.requestUrl = requestUrl;

        }

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected ArrayList<ItemData> doInBackground(Void... urls) {
            // Do some validation here

            try {
                Log.i("Nandit", requestUrl);
                URL url = new URL(requestUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String response = stringBuilder.toString();
                    JSONObject baseJsonResponse = new JSONObject(response);
                    JSONObject responseObject = baseJsonResponse.getJSONObject("response");
                    JSONArray groupsArray = responseObject.getJSONArray("groups");
                    JSONObject firstGroup = groupsArray.getJSONObject(0);
                    JSONArray itemsArray = firstGroup.getJSONArray("items");


                    ArrayList<ItemData> fetchedData = new ArrayList<ItemData>();
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject item = itemsArray.getJSONObject(i);
                        JSONObject venue = item.getJSONObject("venue");
                        String title = venue.getString("name");
                        JSONObject location = venue.getJSONObject("location");
                        String address = "Address not available";
                        try {
                            address = location.getString("address");
                        }
                        catch(Exception e) {
                            Log.i("INFO", "Address not found");
                        }
//                        location.getString("address");
                        Log.i("INFO", "hey: " + title + address);
                        // now fetch photo Id

                        String VENUE_ID = venue.getString("id"); // will be used to fetch photo
                        String photo_request_url = "https://api.foursquare.com/v2/venues/" + VENUE_ID + "/photos?client_id=IXBWNFAW3TTSJV4VGAXOCYT0413STW4C5D05ANUPPXRFNOFZ&client_secret=EUTWE0FF51XINYPX14311URNYGIRANWSLC4OJT0W2YCPOO0E&v=20180323";
//                        https://api.foursquare.com/v2/venues/45e98bacf964a52080431fe3/photos?client_id=DHKYGCOKZFQAFIRXEQGD0KRBRGNQV0M1OLZDCV3WDX4B5U1E&client_secret=CLL5ZIYIEDB5TYZQUPFJ25TCMGO23TDNP1KUZTS5XLOKZ1S4&v=20180323
                        URL url2 = new URL(photo_request_url);
                        String imageURL;
                        HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                        try {
                            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(urlConnection2.getInputStream()));
                            StringBuilder stringBuilder2 = new StringBuilder();
                            String line2;
                            while ((line2 = bufferedReader2.readLine()) != null) {
                                stringBuilder2.append(line2).append("\n");
                            }
                            bufferedReader2.close();
                            String response2 = stringBuilder2.toString();
                            JSONObject baseJsonResponse2 = new JSONObject(response2);
                            JSONObject responseObject2 = baseJsonResponse2.getJSONObject("response");
                            JSONObject photos = responseObject2.getJSONObject("photos");
                            JSONArray items2 = photos.getJSONArray("items");
                            if(items2.length() != 0) {
                                JSONObject firstPhoto = items2.getJSONObject(0);
//                            Log.i("ASSERT", "source is :" + source);
                                String prefix = firstPhoto.getString("prefix");
                                String suffix = firstPhoto.getString("suffix");

                                imageURL = prefix + "700x700" + suffix;
                            }
                            else {
                                imageURL = "";
                            }
                            Log.i("ASSERT", "imageURL is :" + imageURL);
                            fetchedData.add(new ItemData(title, imageURL, address));
                        } catch (MalformedURLException e) {
                            Log.e("Error", "Problem with url 2");
                        }
//                        Log.i("INFO", "Hey again");
//                        titleArray[i] =  title;

                        Log.i("INFO", "venue is: " + title);
                    }
                    return fetchedData;
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(ArrayList<ItemData> fetchedData) {
            String response;
            if (fetchedData == null) {
                response = "THERE WAS AN ERROR";

            }
            updateUI(fetchedData);
            progressBar.setVisibility(View.GONE);

        }
    }
}
