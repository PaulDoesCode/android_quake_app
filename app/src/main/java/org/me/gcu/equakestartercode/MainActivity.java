// Name:    Paul Harbison-Smith
// No:      S1712745

package org.me.gcu.equakestartercode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.me.gcu.equakestartercode.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

// MainActivity class
public class MainActivity extends AppCompatActivity implements OnClickListener
{
    // Declare variables
    // topDataDisplay is used for the LinearLayout which is used to display all elements of the application
    // startButton is used to start the process and retrieve the XML earthquake data from urlSource
    // result is used to store the string XML data
    // urlSource is used to retrieve earthquake data from the British Geological Survey XML feed
    // searchButton is used to go to the search page which allows a user to search for quakes between two given dates
    // quakeMapButton is used to go the map page which allows a user to view a map of recent earthquakes as well as their strength
    // quakeLinkedList is where the array list of quake objects are stored
    private LinearLayout topDataDisplay;
    private Button startButton;
    private String result;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private Button searchButton;
    private Button quakeMapButton;
    private LinkedList <Quake> quakeLinkedList;

    // onCreate holds code for MainActivity functionality and is triggered upon MainActivity creation
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Attaches the XML layout to this activity
        setContentView(R.layout.activity_main);

        // Set up the raw links to the graphical components
        topDataDisplay = findViewById(R.id.topDataDisplay);
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        // Run startProgress function
        startProgress();

        // When you click the search button, it takes you to search page
        // Creates the search activity and passes quake data
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), QuakeSearch.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", quakeLinkedList);
            intent.putExtras(bundle);
            startActivity(intent);
        });


        quakeMapButton = findViewById(R.id.quakeMapButton);
        quakeMapButton.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), QuakeMapActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Quake", quakeLinkedList);
            intent.putExtras(bundle);
            startActivity(intent);

            //
            //
            final Handler quakeHandler = new Handler();
            Timer quakeTimer = new Timer();
            TimerTask doTimerTask = new TimerTask() {
                @Override
                public void run() {
                    quakeHandler.post(() -> {
                        try {
                            startProgress();
                        } catch (
                                Exception e
                        ) {
                            e.printStackTrace();
                        }
                    });
                }
            };
            quakeTimer.schedule(doTimerTask, 0, 600000);
        });
    }

    public void onClick(View aview)
    {
        Log.e("MyTag","in onClick");
        Log.e("MyTag","after startProgress");
    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    } //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                Log.e("MyTag","after ready");
                //
                // Throw away the first 2 header lines before parsing
                //
                //
                //
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception in run");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    String noNull = result.replace("null", "");
                    quakeLinkedList = parseData(noNull);
                    String string = "";
                    Collections.sort(quakeLinkedList);
                    for(int i = 0; i < quakeLinkedList.size();i++)   {
                        TextView textView = new TextView(getApplicationContext());
                        textView.setText(quakeLinkedList.get(i).getLocation() + "\n" + quakeLinkedList.get(i).getMagnitude());
                        textView.setGravity(Gravity.CENTER);
                        String Magnitude = quakeLinkedList.get(i).getMagnitude().substring(11);
                        if (Float.parseFloat(Magnitude) < 1) {
                            textView.setBackgroundColor(Color.GREEN);
                        }
                        else if (Float.parseFloat(Magnitude) >= 1 && Float.parseFloat(Magnitude) < 3)    {
                            textView.setBackgroundColor(Color.YELLOW);
                        }
                        else if (Float.parseFloat(Magnitude) >= 3)   {
                            textView.setBackgroundColor(Color.RED);
                        }
                        Quake q = quakeLinkedList.get(i);
                        textView.setOnClickListener(v->{
                            Intent intent = new Intent(getApplicationContext(), DetailedEarthquakeActivity.class);
                            Bundle bundle = new Bundle();
                                    bundle.putSerializable("Quake", q);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                        });
                            topDataDisplay.addView(textView);
                        Log.e("test", String.valueOf(i));
                    }
                }
            });
        }
    }

    private LinkedList<Quake> parseData(String dataToParse)
    {
        Quake quake = new Quake();
        LinkedList <Quake> alist = new LinkedList <Quake>();
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( dataToParse ) );
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                // Found a start tag
                if(eventType == XmlPullParser.START_TAG)
                {
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        quake = new Quake();
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("title"))
                    {
                        String temp = xpp.nextText();
                        quake.setTitle(temp);
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("description"))
                    {
                        String temp = xpp.nextText();
                        quake.setDescription(temp);
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("link"))
                    {
                        // Now just get the associated text
                        // Do something with text
                        String temp = xpp.nextText();
                        quake.setLink(temp);
                    }
                    else
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("pubDate"))
                        {
                            // Now just get the associated text
                            // Do something with text
                            String temp = xpp.nextText();
                            quake.setPubDate(temp);
                        }
                        else
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("category"))
                            {
                                // Now just get the associated text
                                // Do something with text
                                String temp = xpp.nextText();
                                quake.setCategory(temp);
                            }
                            else
                                // Check which Tag we have
                                if (xpp.getName().equalsIgnoreCase("lat"))
                                {
                                    // Now just get the associated text
                                    // Do something with text
                                    String temp = xpp.nextText();
                                    quake.setGeoLat(temp);
                                }
                                else
                                    // Check which Tag we have
                                    if (xpp.getName().equalsIgnoreCase("long"))
                                    {
                                        // Now just get the associated text
                                        // Do something with text
                                        String temp = xpp.nextText();
                                        quake.setGeoLong(temp);
                                    }
                }
                else
                if(eventType == XmlPullParser.END_TAG)
                {
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        alist.add(quake);
                    }
                }


                // Get the next event
                eventType = xpp.next();

            } // End of while

            //return alist;
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

        return alist;
    }
}