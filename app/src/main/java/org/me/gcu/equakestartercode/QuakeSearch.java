// Name:    Paul Harbison-Smith
// No:      S1712745

// Package used
package org.me.gcu.equakestartercode;

// Imports used
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// QuakeSearch class used for searching for earthquakes between a selected start and end date and then determining
// the northernmost, easternmost, southernmost, and westernmost earthquakes as well as the quake with the largest magnitude,
// the deepest quake, and the most shallow quake
public class QuakeSearch extends AppCompatActivity implements View.OnClickListener {

    // Declare variables
    private EditText startDate;
    private EditText endDate;
    private Button dateSearch;
    private LinearLayout dateLinearLayout;
    private ArrayList <Quake> quakeArrayList;
    private BottomNavigationView bottomNavigationView;

    // When the activity runs, carry out onCreate function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_search);
        startDate = findViewById(R.id.StartDate);
        endDate = findViewById(R.id.EndDate);
        dateSearch = findViewById(R.id.DateSearch);
        dateSearch.setOnClickListener(this);
        dateLinearLayout = (LinearLayout)findViewById(R.id.DateSearchResults);
        quakeArrayList = (ArrayList <Quake>) getIntent().getExtras().getSerializable("data");

        // bottomNavigationView listener, used to check what button in the bottom navbar is clicked
        // This will then create a new activity based on whatever has been clicked (i.e., if it's "search" go to QuakeSearch")
        bottomNavigationView = findViewById((R.id.bottom_navigation));
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                if (bottomNavigationView.getMenu().getItem(0).isChecked()){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", quakeArrayList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (bottomNavigationView.getMenu().getItem(1).isChecked()){
                    Intent intent = new Intent(getApplicationContext(), QuakeSearch.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", quakeArrayList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (bottomNavigationView.getMenu().getItem(2).isChecked()){
                    Intent intent = new Intent(getApplicationContext(), QuakeMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", quakeArrayList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    // When the search button is pressed, carry out onClick function
    @Override
    public void onClick(View v) {
        // Instantiate variables
        String startDateText = startDate.getText().toString();
        String endDateText = endDate.getText().toString();
        Date startDateDate = parseDate(startDateText);
        Date endDateDate = parseDate(endDateText);
        ArrayList <Quake> quakeDates = new ArrayList<>();
        Quake quakeNorth = null;
        Quake quakeEast = null;
        Quake quakeSouth = null;
        Quake quakeWest = null;
        Quake largestMagnitude = null;
        Quake deepQuake = null;
        Quake shallowQuake = null;
        // FOR loop loops through the array list and gathers the items which fall between the selected start and end dates,
        // as seen in the IF statement
        for (int i = 0; i < quakeArrayList.size(); i++)    {
            Date date = parseDate(quakeArrayList.get(i).getPubDate());
            if (date.after(startDateDate) && date.before(endDateDate))  {
                quakeDates.add(quakeArrayList.get(i));
            }
        }
        // IF array list exists, loop through it and assign values to variables for each item in the array list
        if (quakeArrayList.size() >= 0) {
        for (int i = 0; i < quakeArrayList.size(); i++)    {
            if (i == 0) {
                quakeNorth = quakeArrayList.get(i);
                quakeEast = quakeArrayList.get(i);
                quakeSouth = quakeArrayList.get(i);
                quakeWest = quakeArrayList.get(i);
                largestMagnitude = quakeArrayList.get(i);
                deepQuake = quakeArrayList.get(i);
                shallowQuake = quakeArrayList.get(i);
            }
            // IF statements here compare the current index of the array with the forthcoming index and decide which one should take the place
            // of "northernmost quake" or "quake with largest magnitude", for example
            if (Float.parseFloat(quakeNorth.getGeoLat()) < Float.parseFloat(quakeArrayList.get(i).getGeoLat()))    {
                quakeNorth = quakeArrayList.get(i);
            }
            if (Float.parseFloat(quakeEast.getGeoLong()) < Float.parseFloat(quakeArrayList.get(i).getGeoLong()))   {
                quakeEast = quakeArrayList.get(i);
            }
            if (Float.parseFloat(quakeSouth.getGeoLat()) > Float.parseFloat(quakeArrayList.get(i).getGeoLat()))   {
                quakeSouth = quakeArrayList.get(i);
            }
            if (Float.parseFloat(quakeWest.getGeoLong()) > Float.parseFloat(quakeArrayList.get(i).getGeoLong()))   {
                quakeWest = quakeArrayList.get(i);
            }
            // Magnitude is saved as a string "Magnitude: [X VALUE]" so this code simply extracts the required value alone
            // using substring rather than the value + the associated text
            String currentLargestMagnitude = quakeArrayList.get(i).getMagnitude().substring(11);
            String largestMagnitudeSoFar = largestMagnitude.getMagnitude().substring(11);
            if (Float.parseFloat(largestMagnitudeSoFar) < Float.parseFloat(currentLargestMagnitude))   {
                largestMagnitude = quakeArrayList.get(i);
            }
            // Depth is saved as a string "Depth: [X VALUE]" so this code simply extracts the required value alone
            // using substring rather than the value + the associated text
            String currentDeepestQuake = quakeArrayList.get(i).getDepth().substring(7);
            currentDeepestQuake = currentDeepestQuake.substring(0, currentDeepestQuake.length() - 3);
            String deepestQuakeSoFar = deepQuake.getDepth().substring(7);
            deepestQuakeSoFar = deepestQuakeSoFar.substring(0, deepestQuakeSoFar.length() - 3);
            if (Float.parseFloat(deepestQuakeSoFar) < Float.parseFloat(currentDeepestQuake))   {
                deepQuake = quakeArrayList.get(i);
            }
            if (Float.parseFloat(deepestQuakeSoFar) > Float.parseFloat(currentDeepestQuake))   {
                shallowQuake = quakeArrayList.get(i);
            }
        }
        // Turn variables into text UI elements
        TextView quakeNorthText = new TextView(getApplicationContext());
        quakeNorthText.setText("Northernmost: " + quakeNorth.getLocation().substring(10));

        TextView quakeEastText = new TextView(getApplicationContext());
        quakeEastText.setText("Easternmost: " + quakeEast.getLocation().substring(10));

        TextView quakeSouthText = new TextView(getApplicationContext());
        quakeSouthText.setText("Southernmost: " + quakeSouth.getLocation().substring(10));

        TextView quakeWestText = new TextView(getApplicationContext());
        quakeWestText.setText("Westernmost: " + quakeWest.getLocation().substring(10));

        TextView largestMagnitudeText = new TextView(getApplicationContext());
        largestMagnitudeText.setText("Largest magnitude: " + largestMagnitude.getLocation().substring(10));

        TextView deepQuakeText = new TextView(getApplicationContext());
        deepQuakeText.setText("Deepest quake: " + deepQuake.getLocation().substring(10));

        TextView shallowQuakeText = new TextView(getApplicationContext());
        shallowQuakeText.setText("Shallowest quake: " + shallowQuake.getLocation().substring(10));

        // Add to linear layout
        dateLinearLayout.addView(quakeNorthText);
        dateLinearLayout.addView(quakeEastText);
        dateLinearLayout.addView(quakeSouthText);
        dateLinearLayout.addView(quakeWestText);
        dateLinearLayout.addView(largestMagnitudeText);
        dateLinearLayout.addView(deepQuakeText);
        dateLinearLayout.addView(shallowQuakeText);
        }
    }

    // Parse date as String
    public Date parseDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.mm.yyyy");
        try {
            return simpleDateFormat.parse(date);
        }
        catch(Exception e)   {
            e.printStackTrace();
            return new Date();
        }
    }
}