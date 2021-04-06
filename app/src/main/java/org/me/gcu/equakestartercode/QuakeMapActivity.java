// Name:    Paul Harbison-Smith
// No:      S1712745

package org.me.gcu.equakestartercode;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class QuakeMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Quake> quakeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quakeArrayList = (ArrayList<Quake>) getIntent().getExtras().getSerializable("Quake");
        setContentView(R.layout.activity_quake_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add markers in the UK and move the camera on load to focus on the UK
        LatLng unitedKingdom = new LatLng(54, 2);
        for(int i = 0; i < quakeArrayList.size();i++)   {
                String Magnitude = quakeArrayList.get(i).getMagnitude().substring(11);
                BitmapDescriptor quakeMapMarkerIcon = null;
                if (Float.parseFloat(Magnitude) < 1) {
                    quakeMapMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                }
                else if (Float.parseFloat(Magnitude) >= 1 && Float.parseFloat(Magnitude) < 3)    {
                    quakeMapMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                }
                else if (Float.parseFloat(Magnitude) >= 3)   {
                    quakeMapMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                }
            mMap.addMarker(new MarkerOptions().position(
                    new LatLng(Double.parseDouble(quakeArrayList.get(i).getGeoLat()), Double.parseDouble((quakeArrayList.get(i).getGeoLong())))).title(quakeArrayList.get(i).getLocation() + "," + quakeArrayList.get(i).getMagnitude()).icon(quakeMapMarkerIcon));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(unitedKingdom));
    }
}