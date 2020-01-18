package edu.aku.hassannaqvi.uenmd_hhlisting.Core;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.Map;

import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.ListingContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.SignupContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Other.TypefaceUtil;

/**
 * Created by hassan.naqvi on 10/15/2016.
 */

public class MainApp extends Application {

    // GPS Related Field Variables

    public static final Integer _PORT = 443; // Port - with colon (:)
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    //public static String _IP = "10.1.42.30"; // Testing Server
    public static String _IP = "vcoe1.aku.edu"; // Test PHP server
    public static String _IP2 = "58.65.211.13"; // Test PHP server
    public static String _HOST_URL = "https://" + MainApp._IP + ":" + MainApp._PORT + "/casi/api/";
    public static String _UPDATE_URL = "https://" + MainApp._IP + ":" + MainApp._PORT + "/casi/app/linelisting/";
    public static String DeviceURL = "devices.php";

    public static String TAG = "MainApp";
    public static Boolean admin = false;
    public static String IMEI;
    public static ListingContract lc;
    public static int hh01txt;
    public static String hh02txt;
    public static int hh03txt = 0;
    public static String hh07txt;
    public static int fCount = 0;
    public static int fTotal = 0;
    public static int cCount = 0;

    public static String enumCode = "";
    public static String clusterCode = "";
    public static String enumStr = "";

    public static SignupContract signContract;

    public static int hh07 = 0;
    public static int cTotal = 0;
    public static SharedPreferences sharedPref;
    public static String userEmail;
    public static int versionCode;
    public static String versionName;
    public static boolean validateFlag;
    protected static LocationManager locationManager;
    Location location;

    public static void updatePSU(String psuCode, String structureNo) {

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(psuCode, structureNo);

        editor.apply();
        Log.d(TAG, "updatePSU: " + psuCode + " " + structureNo);

    }

    public static Boolean PSUExist(String psuCode) {
        Log.d(TAG, "PSUExist: " + psuCode);
        MainApp.hh03txt = Integer.valueOf(sharedPref.getString(psuCode, "0"));
        Log.d(TAG, "PSUExist (Test): " + sharedPref.getString(psuCode, "0"));

        if (MainApp.hh03txt == 0) {
            Log.d(TAG, "PSUExist (False): " + MainApp.hh03txt);

            return false;
        } else {
            Log.d(TAG, "PSUExist (True): " + MainApp.hh03txt);

            return true;
        }
    }

    public static String getTagName(Context mContext) {
        SharedPreferences sharedPref = mContext.getSharedPreferences("tagName", MODE_PRIVATE);
        return sharedPref.getString("tagName", null);
    }


    public static HashMap<String, String> getTagValues(Context mContext) {
        SharedPreferences sharedPref = mContext.getSharedPreferences("tagName", MODE_PRIVATE);

        HashMap<String, String> map = new HashMap<>();
        map.put("tag", sharedPref.getString("tagName", null));
        map.put("org", sharedPref.getString("countryID", null));
        map.put("listing", sharedPref.getString("listing", null));
        map.put("date", sharedPref.getString("date", null));

        return map;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("App", "Creating...");
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/JameelNooriNastaleeq.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf

        // Declare and Initialize GPS collection module
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // requestPermission();
            } else {
                requestLocationUpdate();
            }
        } else {
            requestLocationUpdate();
        }


        sharedPref = getSharedPreferences("PSUCodes", Context.MODE_PRIVATE);

    }

    public void requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener()
        );
    }


    protected void showCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
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

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {

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
                editor.putString("Altitude", String.valueOf(location.getAltitude()));
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

        public void onStatusChanged(String s, int i, Bundle b) {
            showCurrentLocation();
        }

        public void onProviderDisabled(String s) {

        }

        public void onProviderEnabled(String s) {

        }

    }

}
