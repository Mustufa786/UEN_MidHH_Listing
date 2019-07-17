package edu.aku.hassannaqvi.casi_hhlisting.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.aku.hassannaqvi.casi_hhlisting.Contracts.ListingContract;
import edu.aku.hassannaqvi.casi_hhlisting.Core.AndroidDatabaseManager;
import edu.aku.hassannaqvi.casi_hhlisting.Core.DataBaseHelper;
import edu.aku.hassannaqvi.casi_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.casi_hhlisting.Get.GetAllData;
import edu.aku.hassannaqvi.casi_hhlisting.Get.GetUpdates;
import edu.aku.hassannaqvi.casi_hhlisting.R;
import edu.aku.hassannaqvi.casi_hhlisting.Sync.SyncAllData;
import edu.aku.hassannaqvi.casi_hhlisting.Sync.SyncDevice;
import edu.aku.hassannaqvi.casi_hhlisting.WifiDirect.WiFiDirectActivity;

public class MenuActivity extends AppCompatActivity implements SyncDevice.SyncDevicInterface {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String dtToday = new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date().getTime());
    String DirectoryName;
    DataBaseHelper db;
    boolean flagSync = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DataBaseHelper(this);
        dbBackup();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                onSyncDataClick();
                return true;

            case R.id.menu_upload:
                syncServer();
                return true;

            /*case R.id.menu_randomization:
                Intent iA = new Intent(this, RandomizationActivity.class);
                startActivity(iA);
                return true;*/

            case R.id.menu_openDB:
                Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
                startActivity(dbmanager);
                return true;

            case R.id.menu_wifiDirect:
                Intent wifidirect = new Intent(getApplicationContext(), WiFiDirectActivity.class);
                startActivity(wifidirect);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onSyncDataClick() {
        //TODO implement

        // Require permissions INTERNET & ACCESS_NETWORK_STATE
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new syncData(this, 1).execute();

            flagSync = true;

        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void dbBackup() {

        sharedPref = getSharedPreferences("linelisting", MODE_PRIVATE);
        editor = sharedPref.edit();

        if (sharedPref.getBoolean("checkingFlag", false)) {

            String dt = sharedPref.getString("dt", new SimpleDateFormat("dd-MM-yy").format(new Date()));

            if (dt != new SimpleDateFormat("dd-MM-yy").format(new Date())) {
                editor.putString("dt", new SimpleDateFormat("dd-MM-yy").format(new Date()));

                editor.commit();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + DataBaseHelper.FOLDER_NAME);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {

                DirectoryName = folder.getPath() + File.separator + sharedPref.getString("dt", "");
                folder = new File(DirectoryName);
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {

                    try {
                        File dbFile = new File(this.getDatabasePath(DataBaseHelper.DATABASE_NAME).getPath());
                        FileInputStream fis = new FileInputStream(dbFile);

                        String outFileName = DirectoryName + File.separator +
                                DataBaseHelper.DB_NAME;

                        // Open the empty db as the output stream
                        OutputStream output = new FileOutputStream(outFileName);

                        // Transfer bytes from the inputfile to the outputfile
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }
                        // Close the streams
                        output.flush();
                        output.close();
                        fis.close();
                    } catch (IOException e) {
                        Log.e("dbBackup:", e.getMessage());
                    }

                }

            } else {
                Toast.makeText(this, "Not create folder", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void syncServer() {

        // Require permissions INTERNET & ACCESS_NETWORK_STATE
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new syncData(this, 2).execute();

            flagSync = false;

        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void processFinish(boolean flag) {
        if (flag && flagSync) {
            Toast.makeText(MenuActivity.this, "Sync Enum Blocks", Toast.LENGTH_LONG).show();
            new GetAllData(this, "EnumBlock").execute();
            Toast.makeText(MenuActivity.this, "Sync User", Toast.LENGTH_LONG).show();
            new GetAllData(this, "User").execute();
            Toast.makeText(MenuActivity.this, "Sync Teams", Toast.LENGTH_SHORT).show();
            new GetAllData(this, "Team").execute();
            Toast.makeText(MenuActivity.this, "Get Updates", Toast.LENGTH_SHORT).show();
            new GetUpdates(this).execute();
            /*Toast.makeText(MenuActivity.this, "Get Vertices", Toast.LENGTH_SHORT).show();
            new GetVertices(this).execute();*/
        }
    }

    public class syncData extends AsyncTask<String, String, String> {

        int type;
        private Context mContext;

        public syncData(Context mContext, int type) {
            this.mContext = mContext;
            this.type = type;
        }

        @Override
        protected String doInBackground(String... strings) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    new SyncDevice(MenuActivity.this).execute();

                    if (type == 1) {

                    } else {
                        Toast.makeText(getApplicationContext(), "Syncing Listing", Toast.LENGTH_SHORT).show();
                        new SyncAllData(
                                mContext,
                                "Listing",
                                "updateSyncedForms",
                                ListingContract.class,
                                MainApp._HOST_URL + ListingContract.ListingEntry._URL,
                                db.getAllListings()
                        ).execute();

                    }
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (type == 1) {
                        editor.putBoolean("flag", true);
                        editor.commit();

                        dbBackup();
                    }

                }
            }, 1200);
        }
    }
}
