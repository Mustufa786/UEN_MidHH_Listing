package edu.aku.hassannaqvi.uenmd_hhlisting.Core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.ListingContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.SignupContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.UsersContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Other.TypefaceUtil;

/**
 * Created by hassan.naqvi on 10/15/2016.
 */

public class MainApp extends Application {

    // GPS Related Field Variables

    public static final Integer _PORT = 443; // Port - with colon (:)
    public static final String _SERVER_URL = "syncenc.php";
    public static final String _SERVER_GET_URL = "getDataenc.php";
    public static final String _EMPTY_ = "";
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    //public static final String _IP = "http://f38158";// .TEST server
    public static final String _IP = "https://vcoe1.aku.edu";// .LIVE server
    public static final String _HOST_URL = MainApp._IP + "/uen_rs/api/";
    //public static String _UPDATE_URL = MainApp._IP + "/uen_ml/app/listings/";
    public static String _UPDATE_URL = MainApp._IP + "/uen_rs/app/listings/";
    public static String DeviceURL = "devices.php";

    public static String TAG = "MainApp";
    public static String IBAHC = "";


    public static Boolean admin = false;
    public static String IMEI;
    public static String DIST_ID;
    public static ListingContract lc;
    public static String hh01txt;
    public static String hh02txt;
    public static int hh03txt = 0;
    public static String hh07txt;
    public static int fCount = 0;
    public static int fTotal = 0;
    public static int cCount = 0;

    public static String enumCode = "";
    public static String clusterCode = "";
    public static String enumStr = "";
    public static UsersContract user = new UsersContract();

    public static SignupContract signContract;

    public static int cTotal = 0;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences sharedPref;
    public static String userEmail;
    public static int versionCode;
    public static String versionName;
    public static boolean validateFlag;
    public static int selectedLanguage = 0;
    public static boolean langRTL = false;
    public static String selectedVillage = "";
    public static String[] downloadData;
    public static List<JSONArray> uploadData;
    protected static LocationManager locationManager;
    Location location;

    public static String tabCheck = "";
    public static DatabaseHelper db;

    public static void updatePSU(String psuCode, String structureNo) {

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(psuCode, structureNo);

        editor.apply();
        Log.d(TAG, "updatePSU: " + psuCode + " " + structureNo);

    }

    public static void updateTabNo(String psuCode, String structureNo) {

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("T" + psuCode, structureNo);

        editor.apply();
        Log.d(TAG, "updatePSU: " + psuCode + " " + structureNo);

    }

    public static Boolean PSUExist(String psuCode) {
        Log.d(TAG, "PSUExist: " + psuCode);
        MainApp.hh03txt = Integer.valueOf(sharedPref.getString(psuCode, "0"));
        Log.d(TAG, "PSUExist (Test): " + sharedPref.getString(psuCode, "0"));

        MainApp.tabCheck = sharedPref.getString("T" + psuCode, "");

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

    public static Boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
/*        RootBeer rootBeer = new RootBeer(this);
        if (rootBeer.isRooted()) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }*/
        initSecure();
        Log.d("App", "Creating...");
        //TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/JameelNooriNastaleeq.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/MBLateefi.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf

        // Declare and Initialize GPS collection module
/*        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // requestPermission();
            } else {
                requestLocationUpdate();
            }
        } else {
            requestLocationUpdate();
        }*/


        sharedPref = getSharedPreferences("PSUCodes", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        db = new DatabaseHelper(this);
    }

/*    public void requestLocationUpdate() {
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
    }*/


   /* protected void showCurrentLocation() {

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

    */

    /**
     * Checks whether two providers are the same
     *//*
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
*/
/*
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
*/

    private void initSecure() {
        // Initialize SQLCipher library
        SQLiteDatabase.loadLibs(this);

        // Prepare encryption KEY
        ApplicationInfo ai = null;
        try {
            ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            int TRATS = bundle.getInt("YEK_TRATS");
            IBAHC = bundle.getString("YEK_REVRES").substring(TRATS, TRATS + 16);
            Log.d(TAG, "onCreate: YEK_REVRES = " + IBAHC);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
