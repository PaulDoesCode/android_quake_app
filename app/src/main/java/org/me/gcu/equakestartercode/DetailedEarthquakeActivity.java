// Name:    Paul Harbison-Smith
// No:      S1712745

// Package
package org.me.gcu.equakestartercode;

// Imports
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

// DetailedEarthquakeActivity class used for displaying detailed information for each earthquake
public class DetailedEarthquakeActivity extends AppCompatActivity {

    // Declare variables
    private Quake selected;
    private LinearLayout linearLayout;
    private TextView location;
    private TextView depth;
    private TextView magnitude;
    private TextView link;
    private TextView pubDate;
    private TextView geoLat;
    private TextView geoLong;

    // onCreate creates the activity
    // setContentView and findViewById used to connect to the activity_detailed_earthquake XML file
    // use .setText() to set each view to the appropriate values
    // "selected" variable is for the selected earthquake
    // .setPadding sets the padding for each line of data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_earthquake);
            selected = (Quake) getIntent().getExtras().getSerializable("Quake");

            linearLayout = findViewById(R.id.detailedQuakeLinearLayout);

            location = findViewById(R.id.LocationDisplay);
            location.setText(selected.getLocation());
            location.setPadding(50, 50, 50, 10);

            depth = findViewById(R.id.DepthDisplay);
            depth.setText(selected.getDepth());
            depth.setPadding(50, 10, 50, 10);

            // If the magnitude is less than 1, set rectangle colour to green
            // If magnitude is equal to or greater than 1 and less than 1.5, set rectangle colour to yellow
            // If magnitude is greater than or equal to 1.5, set rectangle colour to red
            if (Float.parseFloat(selected.getMagnitude().substring(11)) < 1) {
                linearLayout.setBackgroundResource(R.drawable.quake_border_green);
            }
            else if (Float.parseFloat(selected.getMagnitude().substring(11)) >= 1 && Float.parseFloat(selected.getMagnitude().substring(11)) < 1.5)    {
                linearLayout.setBackgroundResource(R.drawable.quake_border_yellow);
            }
            else if (Float.parseFloat(selected.getMagnitude().substring(11)) >= 1.5)   {
                linearLayout.setBackgroundResource(R.drawable.quake_border_red);
            }

            magnitude = findViewById(R.id.MagnitudeDisplay);
            magnitude.setText(selected.getMagnitude());
            magnitude.setPadding(50, 10, 50, 10);

            link = findViewById(R.id.LinkDisplay);
            link.setText("Link: " + selected.getLink());
            link.setPadding(50, 10, 50, 10);

            pubDate = findViewById(R.id.PubDateDisplay);
            pubDate.setText("PubDate: " + selected.getPubDate());
            pubDate.setPadding(50, 10, 50, 10);

            geoLat = findViewById(R.id.LatitudeDisplay);
            geoLat.setText("Latitude: " + selected.getGeoLat());
            geoLat.setPadding(50, 10, 50, 10);

            geoLong = findViewById(R.id.LongitudeDisplay);
            geoLong.setText("Longitude: " + selected.getGeoLong());
            geoLong.setPadding(50, 10, 50, 50);
    }
}