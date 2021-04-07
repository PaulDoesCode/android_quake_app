// Name:    Paul Harbison-Smith
// No:      S1712745

// Package
package org.me.gcu.equakestartercode;

// Imports
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

// MainActivity class
public class MainActivity extends AppCompatActivity
{
    // Declare variables
    // topDataDisplay is used for the LinearLayout which is used to display all elements of the application
    // result is used to store the string XML data
    // urlSource is used to retrieve earthquake data from the British Geological Survey XML feed
    // quakeLinkedList is where the array list of quake objects are stored
    // bottomNavigationView is used for the bottom navbar
    private LinearLayout topDataDisplay;
    private String result;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private LinkedList <Quake> quakeLinkedList;
    private BottomNavigationView bottomNavigationView;

    // onCreate holds code for MainActivity functionality and is triggered upon MainActivity creation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If MainActivity is getting created (and it's not the first time) a new quakeLinkedList is passed
        // using getIntent to retrieve it
        // Otherwise set to null just in case
        try {
            quakeLinkedList = (LinkedList<Quake>) getIntent().getExtras().getSerializable("data");
        } catch (Exception e) {
            quakeLinkedList = null;
        };

        // Attaches the XML layout to this activity
        setContentView(R.layout.activity_main);

        // Set up the raw links to the graphical components
        topDataDisplay = findViewById(R.id.topDataDisplay);
        bottomNavigationView = findViewById((R.id.bottom_navigation));

        // bottomNavigationView listener, used to check what button in the bottom navbar is clicked
        // This will then create a new activity based on whatever has been clicked (i.e., if it's "search" go to QuakeSearch")
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                if (bottomNavigationView.getMenu().getItem(0).isChecked()){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", quakeLinkedList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (bottomNavigationView.getMenu().getItem(1).isChecked()){
                    Intent intent = new Intent(getApplicationContext(), QuakeSearch.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", quakeLinkedList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (bottomNavigationView.getMenu().getItem(2).isChecked()){
                    Intent intent = new Intent(getApplicationContext(), QuakeMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", quakeLinkedList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return false;
            }
        });

        // Refreshes the XML data every 10 minutes or so
        final Handler quakeHandler = new Handler();
        Timer quakeTimer = new Timer();
        TimerTask doTimerTask = new TimerTask() {
                @Override
                public void run() {
                    quakeHandler.post(() -> {
                        try {
                            startProgress();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            };
            quakeTimer.schedule(doTimerTask, 0, 600000);
        };

    // Runs getQuakeXml which retrieves the XML string in the background and parses it into a LinkedList of earthquakes
    public void startProgress()
    {
        if (quakeLinkedList == null)    {
            GetQuakeXml getQuakeXml = new GetQuakeXml();
            getQuakeXml.execute();
        }
    }

    // GetQuakeXml gets the XML string in the background and parses it on the UI thread
    public class GetQuakeXml extends AsyncTask<String, Void, String> {
        @Override
        // Background thread gets the XML string and appends it to "result"
        protected String doInBackground(String[] strings) {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";

            Log.e("MyTag","in run");

            // Connects to the URL for the XML data and reads in the XML string using the BufferedReader
            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(urlSource);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                Log.e("MyTag","after ready");

                // While loop goes line by line in the BufferedReader until there is no text left
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);

                }
                in.close();
                return result;
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception in run");
            }
            return null;
        }

        // Un-nulling the XML data (because there's a "null" at the start and parses into a LinkedList of earthquakes
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("UI thread", "I am the UI thread");

            // Remove the null from the data using result.replace
            String noNull = result.replace("null", "");

            // Parse the string data
            quakeLinkedList = parseData(noNull);

            // Sort the earthquakes (done from largest magnitude at the top to smallest magnitude at the bottom)
            Collections.sort(quakeLinkedList);

            // FOR loop loops through the LinkedList and creates a new TextView and gets the location and magnitude data for each quake
            // Then it adds the data to the LinearLayout
            for(int i = 0; i < quakeLinkedList.size();i++)   {
                TextView textView = new TextView(getApplicationContext());
                textView.setText(quakeLinkedList.get(i).getLocation() + "\n" + quakeLinkedList.get(i).getMagnitude());
                textView.setGravity(Gravity.CENTER);

                // Code for checking earthquake magnitude
                String Magnitude = quakeLinkedList.get(i).getMagnitude().substring(11);

                // If the magnitude is less than 1, set background colour to green
                // If magnitude is equal to or greater than 1 and less than 1.5, set background colour to yellow
                // If magnitude is greater than or equal to 1.5, set background color to red
                if (Float.parseFloat(Magnitude) < 1) {
                    textView.setBackgroundColor(Color.GREEN);
                }
                else if (Float.parseFloat(Magnitude) >= 1 && Float.parseFloat(Magnitude) < 1.5)    {
                    textView.setBackgroundColor(Color.YELLOW);
                }
                else if (Float.parseFloat(Magnitude) >= 1.5)   {
                    textView.setBackgroundColor(Color.RED);
                }

                // Set q to quakeLinkedList.get(i) since quakeLinkedList.get(i) won't work on it's own with bundle.putSerializable
                Quake q = quakeLinkedList.get(i);

                // OnClickListener used to listen for when an earthquake entry is clicked on
                // Used to display the detailed earthquake activity information
                textView.setOnClickListener(v->{
                    Intent intent = new Intent(getApplicationContext(), DetailedEarthquakeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Quake", q);
                    intent.putExtras(bundle);
                    startActivity(intent);
                });
                topDataDisplay.addView(textView);
            }
        }
    };

    // Parses the XML string into a LinkedList of earthquakes
    private LinkedList<Quake> parseData(String dataToParse)
    {
        Quake quake = new Quake();
        LinkedList <Quake> quakeList = new LinkedList <Quake>();
        try
        {
            // Create XML pull parser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(dataToParse));
            int eventType = xpp.getEventType();

            // WHILE loop loops through each tag until the end of the XML document
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                // Found a start tag
                if(eventType == XmlPullParser.START_TAG)
                {
                    // Check which tag we have
                    // Set each <item> entry to a new earthquake object
                    // Go through each tag in <item> and add them to the object using setters
                    // temp is used to store the text inside each tag
                    // .nextText() moves to the next tag in each item
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        quake = new Quake();
                    }
                    else if (xpp.getName().equalsIgnoreCase("title"))
                    {
                        String temp = xpp.nextText();
                        quake.setTitle(temp);
                    }
                    else if (xpp.getName().equalsIgnoreCase("description"))
                    {
                        String temp = xpp.nextText();
                        quake.setDescription(temp);
                    }
                    else if (xpp.getName().equalsIgnoreCase("link"))
                    {
                        String temp = xpp.nextText();
                        quake.setLink(temp);
                    }
                    else if (xpp.getName().equalsIgnoreCase("pubDate"))
                    {
                        String temp = xpp.nextText();
                        quake.setPubDate(temp);
                    }
                    else if (xpp.getName().equalsIgnoreCase("category"))
                    {
                        String temp = xpp.nextText();
                        quake.setCategory(temp);
                    }
                    else if (xpp.getName().equalsIgnoreCase("lat"))
                    {
                        String temp = xpp.nextText();
                        quake.setGeoLat(temp);
                    }
                    else if (xpp.getName().equalsIgnoreCase("long"))
                    {
                        String temp = xpp.nextText();
                        quake.setGeoLong(temp);
                    }
                }
                // When you get to the end of the tag you add the quake item to the list
                else if(eventType == XmlPullParser.END_TAG)
                {
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        quakeList.add(quake);
                    }
                }
                // Move to the next <item> tag
                eventType = xpp.next();
            }
        }
        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }
        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        }

        Log.e("MyTag","End document");

        // Return the list
        return quakeList;
    }
}