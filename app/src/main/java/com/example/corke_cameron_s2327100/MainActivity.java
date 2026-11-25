//
// Name                 Cameron Corke
// Student ID           S2327100
// Programme of Study   Computing
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
package com.example.corke_cameron_s2327100;

import static android.app.ProgressDialog.show;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Intent;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.Criteria;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewTreeLifecycleOwner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Base64;


public class MainActivity extends AppCompatActivity implements OnClickListener {
    // private TextView rawDataDisplay;
    ListView situationList;
    private ArrayList<Situation> allSituations;
    private Button startButton;
    private Button nearbyButton;
    private TextView updateTime;
    private Button mapButton;
    private EditText roadSearch;

    //DOWNLOADING FROM TRAFFIC SCOTLAND
    final String clientId = BuildConfig.DATEX_CLIENT_ID;
    final String clientKey = BuildConfig.DATEX_CLIENT_KEY;
    final String unplannedEventsURL = "https://datex2.trafficscotland.org/rest/2.3/publications/UnplannedEvents/Content.xml";
    final String currentRoadworksURL = "https://datex2.trafficscotland.org/rest/2.3/publications/CurrentRoadworks/Content.xml";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        updateTime = findViewById(R.id.updateTime);
        PeriodicWorkRequest refreshRequest = new PeriodicWorkRequest.Builder(SituationWorker.class, 1, TimeUnit.HOURS).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("TrafficFeedRefresh", ExistingPeriodicWorkPolicy.KEEP, refreshRequest);
        roadSearch = findViewById(R.id.roadSearch);
        roadSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            } @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            } @Override
            public void afterTextChanged (Editable s) { // ROAD FILTER
              String searchText = s.toString().toLowerCase().trim();
              ArrayList<Situation> filteredSituations = new ArrayList<>();
              for (Situation situation : allSituations) {
                  String road = situation.getDescriptor();
                  if (searchText.isEmpty() || (road != null && road.toLowerCase().contains(searchText))) {
                      filteredSituations.add(situation);
                  }
              }
              SituationAdapter adapter = new SituationAdapter(MainActivity.this, filteredSituations);
              situationList.setAdapter(adapter); }
          });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Set up the raw links to the graphical components
        // rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        situationList = (ListView)findViewById(R.id.situationList);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
        nearbyButton = (Button)findViewById(R.id.nearbyButton);
        nearbyButton.setOnClickListener(v -> checkLocationPermission());
        mapButton = (Button)findViewById(R.id.mapButton);
        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);

            ArrayList<Situation> mapSituations = new ArrayList<>(allSituations);

            intent.putExtra("situationCount", mapSituations.size());

            for (int i = 0; i < mapSituations.size(); i++) {
                Situation s = mapSituations.get(i);

                intent.putExtra("latitude_" + i, s.getFromLatitude());
                intent.putExtra("longitude_" + i, s.getFromLongitude());
                intent.putExtra("type_" + i, s.getSituationRecord());
                intent.putExtra("description_" + i, s.getGeneralPublicComment());
            }

            startActivity(intent);
        });

        // More Code goes here
        allSituations = new ArrayList<>();
        loadFeeds();
        String result = "";
        for (Situation s : allSituations) { result += s.toString() + "\n"; }

    }
    public void loadFeeds() { allSituations. clear(); // Loads Automatic Feed from the Cache'd Feed
    String unplanned = Feed.getFeed(this, "unplannedEvents");
    String roadworks = Feed.getFeed(this,"currentRoadworks");
    if (unplanned != null && !unplanned.isEmpty()) { new Task(unplanned).parseData(unplanned); }
    if (roadworks != null && !roadworks.isEmpty()) { new Task(roadworks).parseData(roadworks); }
    SituationAdapter adapter = new SituationAdapter(MainActivity.this, allSituations);
    situationList.setAdapter(adapter);
    updateRefreshTime();
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener feedListener = (SharedPreferences, key) -> {
        if (key.equals("unplannedEvents") || key.equals("currentRoadworks")) { runOnUiThread(this::loadFeeds); }
    };
    @Override
    protected void onResume() { super.onResume(); getSharedPreferences("TrafficFeeds", MODE_PRIVATE).registerOnSharedPreferenceChangeListener(feedListener);
    loadFeeds(); }
    @Override
    protected void onPause() { super.onPause(); getSharedPreferences("TrafficFeeds", MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(feedListener);}

    public void updateRefreshTime() {SharedPreferences preferences = getSharedPreferences("TrafficFeeds", MODE_PRIVATE);
    long lastUpdated = preferences.getLong("lastUpdated", 0);
    if (lastUpdated == 0) { updateTime.setText( "Last Updated: --:--  |  Next Update: --:--"); return; }
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.UK);
    String lastTime = timeFormat.format(new Date(lastUpdated));
    long nextUpdate = lastUpdated + (60 * 60 * 1000);
    String nextTime = timeFormat.format(new Date(nextUpdate));
    updateTime.setText( "Last Updated: " + lastTime + "  |   Next Update:  " + nextTime);
    }
    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else { getNearbySituations(); }
    }
    private void getNearbySituations() { LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation == null) {
                Toast.makeText(this, "Location unavailable", Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<Situation> nearbySituations = new ArrayList<>();
            float[] distance = new float[1];
            for (Situation situation : allSituations) {
                Location.distanceBetween(
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude(),
                        situation.getFromLatitude(),
                        situation.getFromLongitude(),
                        distance
                );
                if (distance[0] <= 16093) {
                    nearbySituations.add(situation);
                }}
            SituationAdapter adapter = new SituationAdapter(MainActivity.this, nearbySituations);
            situationList.setAdapter(adapter);
            }catch(SecurityException e){
                Toast.makeText(this, "Location Permissions Required", Toast.LENGTH_LONG).show();
            }
        }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) { if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getNearbySituations();
        } else { Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
    } } }
    public void onClick(View aview)
    {
        startProgress();
    }

    public void startProgress()
    {
        new Thread (new Runnable() {
            @Override
            public void run() {

                // Clear Data Before Refresh
                allSituations.clear();

                // Run network access on a separate thread;
                new Task(unplannedEventsURL).run();
                Log.d("Feed Check","Unplanned Events finished. Situations: " + allSituations.size());
                new Task(currentRoadworksURL).run();
                Log.d("Feed Check","Current Roadworks finished. Situations: " + allSituations.size());

                for (Situation s : allSituations) {
                Log.d("Situation Types", s.getSituationRecord()); }

                // ArrayList<String> displayItems = new ArrayList<>();

                // for (Situation s : allSituations) {
                //    String type = s.getSituationRecord();
                //    String description = s.getGeneralPublicComment();
                //    displayItems.add(type + "\n" + description);
                // }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SituationAdapter adapter = new SituationAdapter(MainActivity.this, allSituations);
                        situationList.setAdapter(adapter);
                    }
                });
            }
            }).start();
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String result="";
        private String myurl;
        BufferedReader in = null;
        String inputLine = "";
        public Task(String aurl){
            myurl = aurl;
        }
        @Override
        public void run() {
            Log.d("MyTask", "in run");
            try {
                URL url = new URL(myurl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //ADD AUTHORISATION (string with clientIID:clientKey)
                urlConnection.setRequestProperty("Authorization", Base64.getEncoder().encodeToString((clientId + ":" + clientKey).getBytes()));
                try {
                    in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((inputLine = in.readLine()) != null) {
                        result = result + inputLine;
                    }
                    in.close();
                } catch (IOException e) {
                    String error = urlConnection.getErrorStream().toString();
                    Log.e("urlConnection", "ioexception");
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("URL", "ioexception");
            }

            //Clean up any leading garbage characters
            int i = result.indexOf("<?"); //initial tag
            // result = result.substring(i);
            if (i >= 0) { result = result.substring(i); }
            if (myurl.equals(unplannedEventsURL)) { Feed.saveFeed(MainActivity.this, "unplannedEvents", result); }
            else if (myurl.equals(currentRoadworksURL)) { Feed.saveFeed(MainActivity.this, "currentRoadworks", result); }

            // Now that you have the xml data into result, you can parse it
            parseData(result);

        }
                //PARSING

    private void parseData(String dataToParse) {
        Situation aSituation = null;
        try {
            boolean insideASituation = false;
            boolean insideAGPC = false;
            boolean insideFrom = false;
            boolean insideTo = false;
            boolean insideDescriptor = false;
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(dataToParse));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("situation")) {
                        insideASituation = true;
                        aSituation = new Situation();
                        Log.d("myTag", "New Situation Found");
                    } else if (xpp.getName().equalsIgnoreCase("situationRecord")) {
                        String temp = xpp.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (insideASituation) {
                            aSituation.setSituationRecord(temp);
                            Log.d("Parsing", "Situation Record: " + temp);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("overallStartTime")) {
                        String temp = xpp.nextText(); // Gets Start Time
                        if (insideASituation) {
                            aSituation.setOverallStartTime(temp);
                            Log.d("Parsing", "Start Time: " + temp);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("tpegDirection")) {
                        String temp = xpp.nextText();
                        if (insideASituation) {
                            aSituation.setTpegDirection(temp);
                            Log.d("Parsing", "Tpeg Direction: " + temp);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("numberOfOperationalLanes")) {
                        String temp = xpp.nextText();
                        if (insideASituation) {
                            aSituation.setNumberOfOperationalLanes(temp);
                            Log.d("Parsing", "Number of Operational Lanes: " + temp);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("overallEndTime")) {
                        String temp = xpp.nextText();
                        if (insideASituation) {
                            aSituation.setOverallEndTime(temp);
                            Log.d("Parsing", "End Time: " + temp);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("delaysType")) {
                        String temp = xpp.nextText();
                        if (insideASituation) {
                            aSituation.setDelaysType(temp);
                            Log.d("Parsing","Delays Type: " + temp);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("ilc")) {
                        insideDescriptor = true;
                    } else if (xpp.getName().equalsIgnoreCase("generalPublicComment")) {
                        insideAGPC = true;
                    } else if (xpp.getName().equalsIgnoreCase("value") && insideAGPC) {
                        String temp = xpp.nextText();
                        if (insideASituation) {
                            aSituation.setGeneralPublicComment(temp);
                            Log.d("Parsing", "General Public Comment: " + temp);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("value") && insideDescriptor) {
                        String temp = xpp.nextText();
                        if (insideASituation) {
                            aSituation.setDescriptor(temp);
                            Log.d("Parsing", "Road Descriptor: " + temp);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("from")) {
                            insideFrom = true;
                        } else if (xpp.getName().equalsIgnoreCase("latitude") && insideFrom) {
                            double temp = Double.parseDouble(xpp.nextText());
                            aSituation.setFromLatitude(temp);
                            Log.d("Parsing", "From Latitude: " + temp);
                        } else if (xpp.getName().equalsIgnoreCase("longitude") && insideFrom) {
                            double temp = Double.parseDouble(xpp.nextText());
                            aSituation.setFromLongitude(temp);
                            Log.d("Parsing","From Longitude: " + temp);
                        } else if (xpp.getName().equalsIgnoreCase("to")) {
                            insideTo = true;
                        } else if (xpp.getName().equalsIgnoreCase("latitude") && insideTo) {
                            double temp = Double.parseDouble(xpp.nextText());
                            aSituation.setToLatitude(temp);
                            Log.d("Parsing","To Latitude: " + temp);
                        } else if (xpp.getName().equalsIgnoreCase("longitude") && insideTo) {
                            double temp = Double.parseDouble(xpp.nextText());
                            aSituation.setToLongitude(temp);
                            Log.d("Parsing","To Longitude: " + temp);
                    }
                    } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("situation")) {
                        allSituations.add(aSituation);
                        Log.d("Parsing","Situation Finished");
                        insideASituation = false;
                        aSituation = null;
                    } else if (xpp.getName().equalsIgnoreCase("generalPublicComment")) {
                        insideAGPC = false;
                    } else if (xpp.getName().equalsIgnoreCase("from")) {
                        insideFrom = false;
                    } else if (xpp.getName().equalsIgnoreCase(("to"))) {
                        insideTo = false;
                    } else if (xpp.getName().equalsIgnoreCase("ilc")) {
                        insideDescriptor = false;
                    }
                }
                eventType = xpp.next();
            }
            } catch (XmlPullParserException e) {
                Log.e("Parsing","EXCEPTION" + e);
                //throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            //MainActivity.this.runOnUiThread(new Runnable()
            //{
            //    public void run() {
            //        Log.d("UI thread", "I am the UI thread");
            //        rawDataDisplay.setText(dataToParse);
            //    }
            //});
        }

    }

}