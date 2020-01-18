package edu.aku.hassannaqvi.uenmd_hhlisting.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.aku.hassannaqvi.uenmd_hhlisting.Core.AndroidDatabaseManager;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.DatabaseHelper;
import edu.aku.hassannaqvi.uenmd_hhlisting.R;
import edu.aku.hassannaqvi.uenmd_hhlisting.WifiDirect.WiFiDirectActivity;

public class MenuActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String dtToday = new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date().getTime());
    String DirectoryName;
    DatabaseHelper db;
    boolean flagSync = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHelper(this);
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
            startActivity(new Intent(MenuActivity.this, SyncActivity.class));
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

            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + DatabaseHelper.FOLDER_NAME);
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
                        File dbFile = new File(this.getDatabasePath(DatabaseHelper.DATABASE_NAME).getPath());
                        FileInputStream fis = new FileInputStream(dbFile);

                        String outFileName = DirectoryName + File.separator +
                                DatabaseHelper.DB_NAME;

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

            startActivity(new Intent(MenuActivity.this, SyncActivity.class));

        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }

    }
}
