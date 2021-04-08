// Name:    Paul Harbison-Smith
// No:      S1712745

// Package
package org.me.gcu.equakestartercode;

// Imports
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

// QuakeMapActivity class used for map functionality
public class QuakeMapActivity extends FragmentActivity implements OnMapReadyCallback {

    // Declare variables
    private GoogleMap mMap;
    private ArrayList<Quake> quakeArrayList;
    private BottomNavigationView bottomNavigationView;

    // onCreate creates everything for when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quakeArrayList = (ArrayList<Quake>) getIntent().getExtras().getSerializable("data");

        setContentView(R.layout.activity_quake_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // bottomNavigationView listener, used to check what button in the bottom navbar is clicked
        // This will then create a new activity based on whatever has been clicked (i.e., if it's "search" go to QuakeSearch")
        bottomNavigationView = findViewById((R.id.bottom_navigation));
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                if (bottomNavigationView.getMenu().getItem(0).isChecked()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", quakeArrayList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (bottomNavigationView.getMenu().getItem(1).isChecked()) {
                    Intent intent = new Intent(getApplicationContext(), QuakeSearch.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", quakeArrayList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (bottomNavigationView.getMenu().getItem(2).isChecked()) {
                    Intent intent = new Intent(getApplicationContext(), QuakeMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", quakeArrayList);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return false;
            }
        });}

    // Code used to manipulate the map once available.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Declare map variable
        mMap = googleMap;

        // Add markers in the UK and move the camera on load to focus on the UK
        LatLng unitedKingdom = new LatLng(54, 2);

        // FOR loop loops through earthquake array list to check each quakes location and magnitude value
        // Same as main activity basically, IF statements used to determine map marker colour depending on magnitude value
        // If magnitude is less than 1, set marker colour to green
        // If magnitude is greater than or equal to 1 and less than 1.5, set marker colour to yellow
        // If magnitude is greater than or equal to 1.5, set marker colour to red
        for(int i = 0; i < quakeArrayList.size();i++)   {
                String Magnitude = quakeArrayList.get(i).getMagnitude().substring(11);
                BitmapDescriptor quakeMapMarkerIcon = null;
                if (Float.parseFloat(Magnitude) < 1) {
                    quakeMapMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                }
                else if (Float.parseFloat(Magnitude) >= 1 && Float.parseFloat(Magnitude) < 1.5)    {
                    quakeMapMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                }
                else if (Float.parseFloat(Magnitude) >= 1.5)   {
                    quakeMapMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                }
                // .addMarker() used to add marker to map based on latitude and longitude
                // .title() used to set on-screen display text for each marker when hovered over
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(quakeArrayList.get(i).getGeoLat()),
                    Double.parseDouble((quakeArrayList.get(i).getGeoLong())))).title(quakeArrayList.get(i).getLocation() +
                    "," + quakeArrayList.get(i).getMagnitude()).icon(quakeMapMarkerIcon));
        }
        // Adjust the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(unitedKingdom));
    }
}