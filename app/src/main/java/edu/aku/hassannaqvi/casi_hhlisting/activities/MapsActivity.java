package edu.aku.hassannaqvi.casi_hhlisting.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import edu.aku.hassannaqvi.casi_hhlisting.Contracts.VerticesContract;
import edu.aku.hassannaqvi.casi_hhlisting.Core.DataBaseHelper;
import edu.aku.hassannaqvi.casi_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.casi_hhlisting.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 17;
    private static final String TAG = "MAY";
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    protected LocationManager locationManager;
    //    Login Members Array
    Location location;
    Location mLastKnownLocation;
    DataBaseHelper db;
    private GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLocation;
    private ArrayList<LatLng> clusterPoints;
    private ArrayList<LatLng> newClusterPoints;
    private ArrayList<LatLng> ucPoints;
    private PolygonOptions polygon102;
    private LatLng clusterStart;
    private String clusterName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = new DataBaseHelper(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener()
        );


        clusterPoints = new ArrayList<LatLng>();
        newClusterPoints = new ArrayList<LatLng>();
        ucPoints = new ArrayList<LatLng>();

        Collection<VerticesContract> vc = db.getVerticesByCluster(MainApp.hh02txt);

        for (VerticesContract v : vc) {


            clusterName = v.getCluster_code();

            clusterPoints.add(new LatLng(v.getPoly_lat(), v.getPoly_lng()));
        }
        clusterStart = (clusterPoints.get(0));
       /* Collection<VerticesUCContract> vcuc = db.getVerticesByUC(MainApp.hh01txt);
        for (VerticesUCContract v : vcuc) {
            ucPoints.add(new LatLng(v.getPoly_lat(), v.getPoly_lng()));
        }*/
    }

    protected void showCurrentLocation() {

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            //Toast.makeText(getApplicationContext(), message,
            //Toast.LENGTH_SHORT).show();
        }

    }

    public void showGPSCoordinates(View v) {
        showCurrentLocation();


    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
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

        // Get the current location of the device and set the position of the map.
        // Add a marker in Sydney and move the camera
        Marker clusterMarker = mMap.addMarker(new MarkerOptions()
                .position(clusterStart)
                .title(clusterName)
                .anchor(0.5f, 1)
        );

        clusterMarker.showInfoWindow();
        // Instantiates a new Polyline object and adds clusterPoints to define a rectangle
        PolygonOptions rectCluster = new PolygonOptions()
                .fillColor(getResources().getColor(R.color.colorAccentAlpha))
                .strokeColor(Color.RED)
                .zIndex(2.0f);
        rectCluster.addAll(clusterPoints);
/*
        PolygonOptions rectUC = new PolygonOptions()
                .fillColor(getResources().getColor(R.color.dullBlueOverlay))
                .strokeColor(R.color.dullBlack)
                .zIndex(1.0f);

        rectUC.addAll(ucPoints);*/


// Get back the mutable Polyline
        // Cluster Poly
        Polygon polyCluster = mMap.addPolygon(rectCluster);
        polyCluster.setGeodesic(true);
        // UC Poly
       /* Polygon polyUC = mMap.addPolygon(rectUC);
        polyUC.setGeodesic(true);*/


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clusterPoints.get(0), DEFAULT_ZOOM));

        //DRAW CLUSTER
       /* mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                newClusterPoints.add(new LatLng(point.latitude, point.latitude));

                Toast.makeText(getApplicationContext(), "(" + newClusterPoints.size() + ") " +
                                point.latitude + ", " + point.longitude,
                        Toast.LENGTH_SHORT).show();

                if (newClusterPoints.size() > 3) {
                    PolygonOptions rectCluster = new PolygonOptions()
                            .fillColor(getResources().getColor(R.color.colorAccentAlpha))
                            .strokeColor(Color.RED);
                    rectCluster.addAll(newClusterPoints);
                    mMap.addPolygon(rectCluster);

                }
            }
        });*/
    }

    private void updateCameraBearing(GoogleMap googleMap, float bearing) {
        if (googleMap == null) return;
        CameraPosition camPos = CameraPosition
                .builder(
                        googleMap.getCameraPosition() // current Camera
                )
                .bearing(bearing)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    private class MyLocationListener implements LocationListener {

        Polygon polySCluster = null;


        public void onLocationChanged(Location location) {
            mDefaultLocation = new LatLng(location.getLatitude(), location.getLongitude());
            updateCameraBearing(mMap, location.getBearing());

            if (polySCluster == null && PolyUtil.containsLocation(mDefaultLocation, clusterPoints, false)) {
                PolygonOptions rectSCluster = new PolygonOptions()
                        .fillColor(getResources().getColor(R.color.colorAccentGAlpha))
                        .strokeColor(Color.GREEN)
                        .zIndex(2.0f);
                rectSCluster.addAll(clusterPoints);


// Get back the mutable Polyline
                // Cluster Poly
                polySCluster = mMap.addPolygon(rectSCluster);


                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 20));


            } else if (polySCluster != null && !(PolyUtil.containsLocation(mDefaultLocation, clusterPoints, false))) {

                polySCluster.remove();
                polySCluster = null;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clusterPoints.get(0), DEFAULT_ZOOM));

            }


            SharedPreferences sharedPref = getSharedPreferences("GPSCoordinates", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            String dt = DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(sharedPref.getString("Time", "0"))).toString();

            Location bestLocation = new Location("storedProvider");
            bestLocation.setAccuracy(Float.parseFloat(sharedPref.getString("Accuracy", "0")));
            bestLocation.setTime(Long.parseLong(sharedPref.getString(dt, "0")));
//                bestLocation.setTime(Long.parseLong(dt));
            bestLocation.setLatitude(Float.parseFloat(sharedPref.getString("Latitude", "0")));
            bestLocation.setLongitude(Float.parseFloat(sharedPref.getString("Longitude", "0")));

            if (isBetterLocation(location, bestLocation)) {
                editor.putString("Longitude", String.valueOf(location.getLongitude()));
                editor.putString("Latitude", String.valueOf(location.getLatitude()));
                editor.putString("Accuracy", String.valueOf(location.getAccuracy()));
                editor.putString("Time", String.valueOf(location.getTime()));
//                    editor.putString("Time", DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(String.valueOf(location.getTime()))).toString());

//                String date = DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(String.valueOf(location.getTime()))).toString();
//                Toast.makeText(getApplicationContext(),
//                        "GPS Commit! LAT: " + String.valueOf(location.getLongitude()) +
//                                " LNG: " + String.valueOf(location.getLatitude()) +
//                                " Accuracy: " + String.valueOf(location.getAccuracy()) +
//                                " Time: " + date,
//                        Toast.LENGTH_SHORT).show();

                editor.apply();
            }


            Map<String, ?> allEntries = sharedPref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("Map", entry.getKey() + ": " + entry.getValue().toString());
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
