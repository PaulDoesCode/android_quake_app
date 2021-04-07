// Name:    Paul Harbison-Smith
// No:      S1712745

// Package
package org.me.gcu.equakestartercode;

// Imports
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

// DetailedEarthquakeActivity class used for displaying detailed information for each earthquake
public class DetailedEarthquakeActivity extends AppCompatActivity {

    // Declare variables
    private Quake selected;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_earthquake);
            selected = (Quake) getIntent().getExtras().getSerializable("Quake");
            location = findViewById(R.id.LocationDisplay);
            location.setText(selected.getLocation());
            depth = findViewById(R.id.DepthDisplay);
            depth.setText(selected.getDepth());
            magnitude = findViewById(R.id.MagnitudeDisplay);
            magnitude.setText(selected.getMagnitude());
            link = findViewById(R.id.LinkDisplay);
            link.setText(selected.getLink());
            pubDate = findViewById(R.id.PubDateDisplay);
            pubDate.setText(selected.getPubDate());
            geoLat = findViewById(R.id.LatitudeDisplay);
            geoLat.setText("Latitude: " + selected.getGeoLat());
            geoLong = findViewById(R.id.LongitudeDisplay);
            geoLong.setText("Longitude: " + selected.getGeoLong());
    }
}