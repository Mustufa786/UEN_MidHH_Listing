package edu.aku.hassannaqvi.casi_hhlisting.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.aku.hassannaqvi.casi_hhlisting.Contracts.ListingContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.SignupContract;
import edu.aku.hassannaqvi.casi_hhlisting.Core.DatabaseHelper;
import edu.aku.hassannaqvi.casi_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.casi_hhlisting.Get.GetAllData;
import edu.aku.hassannaqvi.casi_hhlisting.Other.SyncModel;
import edu.aku.hassannaqvi.casi_hhlisting.R;
import edu.aku.hassannaqvi.casi_hhlisting.Sync.SyncAllData;
import edu.aku.hassannaqvi.casi_hhlisting.Sync.SyncDevice;
import edu.aku.hassannaqvi.casi_hhlisting.adapters.SyncListAdapter;
import edu.aku.hassannaqvi.casi_hhlisting.adapters.Upload_list_adapter;
import edu.aku.hassannaqvi.casi_hhlisting.databinding.ActivitySyncBinding;

public class SyncActivity extends AppCompatActivity implements SyncDevice.SyncDevicInterface {
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    String DirectoryName;
    DatabaseHelper db;
    SyncListAdapter syncListAdapter;
    Upload_list_adapter uploadListAdapter;
    ActivitySyncBinding bi;
    SyncModel model;
    SyncModel uploadmodel;
    List<SyncModel> list;
    List<SyncModel> uploadlist;
    Boolean listActivityCreated;
    Boolean uploadlistActivityCreated;
    String dtToday = new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date().getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_sync);
        bi.setCallback(this);
        list = new ArrayList<>();
        uploadlist = new ArrayList<>();
        bi.noItem.setVisibility(View.VISIBLE);
        bi.noDataItem.setVisibility(View.VISIBLE);
        listActivityCreated = true;
        uploadlistActivityCreated = true;
        db = new DatabaseHelper(this);
        dbBackup();
        bi.btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SyncActivity.this, "Start Downloading Data", Toast.LENGTH_SHORT).show();
                onSyncDataClick();
            }
        });
        bi.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SyncActivity.this, "Start Uploading Data", Toast.LENGTH_SHORT).show();
                syncServer();
            }
        });
        setAdapter();
        setUploadAdapter();
    }

    public void onSyncDataClick() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new SyncDevice(SyncActivity.this, true).execute();
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    void setAdapter() {
        syncListAdapter = new SyncListAdapter(list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        bi.rvSyncList.setLayoutManager(mLayoutManager);
        bi.rvSyncList.setItemAnimator(new DefaultItemAnimator());
        bi.rvSyncList.setAdapter(syncListAdapter);
        syncListAdapter.notifyDataSetChanged();
        if (syncListAdapter.getItemCount() > 0) {
            bi.noItem.setVisibility(View.GONE);
        } else {
            bi.noItem.setVisibility(View.VISIBLE);
        }
    }

    void setUploadAdapter() {
        uploadListAdapter = new Upload_list_adapter(uploadlist);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        bi.rvUploadList.setLayoutManager(mLayoutManager2);
        bi.rvUploadList.setItemAnimator(new DefaultItemAnimator());
        bi.rvUploadList.setAdapter(uploadListAdapter);
        uploadListAdapter.notifyDataSetChanged();
        if (uploadListAdapter.getItemCount() > 0) {
            bi.noDataItem.setVisibility(View.GONE);
        } else {
            bi.noDataItem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void processFinish(boolean flag) {
        if (flag) {
            HashMap<String, String> tagVal = MainApp.getTagValues(this);
            new SyncData(SyncActivity.this, tagVal.get("org") != null ? tagVal.get("org").equals("null") ? null : tagVal.get("org") : null).execute();
        }
    }

    public void syncServer() {

        // Require permissions INTERNET & ACCESS_NETWORK_STATE
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            DatabaseHelper db = new DatabaseHelper(this);

            new SyncDevice(this, false).execute();
//            Toast.makeText(getApplicationContext(), "Syncing Forms", Toast.LENGTH_SHORT).show();
            if (uploadlistActivityCreated) {
                uploadmodel = new SyncModel();
                uploadmodel.setstatusID(0);
                uploadlist.add(uploadmodel);
            }
            new SyncAllData(
                    this,
                    "Listing Form",
                    "updateSyncedForms",
                    ListingContract.class,
                    MainApp._HOST_URL + ListingContract.ListingEntry._URL,
                    db.getUnsyncedListings(), 0, uploadListAdapter, uploadlist
            ).execute();

//            Toast.makeText(getApplicationContext(), "Syncing Family Members", Toast.LENGTH_SHORT).show();
            if (uploadlistActivityCreated) {
                uploadmodel = new SyncModel();
                uploadmodel.setstatusID(0);
                uploadlist.add(uploadmodel);
            }
            new SyncAllData(
                    this,
                    "New Users",
                    "updateSyncedSignup",
                    SignupContract.class,
                    MainApp._HOST_URL + SignupContract.SignUpTable._URL,
                    db.getUnsyncedSignups(), 1, uploadListAdapter, uploadlist
            ).execute();
            bi.noDataItem.setVisibility(View.GONE);

            uploadlistActivityCreated = false;

            SharedPreferences syncPref = getSharedPreferences("SyncInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = syncPref.edit();

            editor.putString("LastUpSyncServer", dtToday);

            editor.apply();

        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }

    }

    public void dbBackup() {

        sharedPref = getSharedPreferences("src", MODE_PRIVATE);
        editor = sharedPref.edit();

        if (sharedPref.getBoolean("flag", false)) {

            String dt = sharedPref.getString("dt", new SimpleDateFormat("dd-MM-yy").format(new Date()));

            if (dt != new SimpleDateFormat("dd-MM-yy").format(new Date())) {
                editor.putString("dt", new SimpleDateFormat("dd-MM-yy").format(new Date()));

                editor.commit();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + DatabaseHelper.PROJECT_NAME);
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

    public class SyncData extends AsyncTask<String, String, String> {

        String countryID;
        private Context mContext;

        public SyncData(Context mContext, String countryID) {
            this.mContext = mContext;
            this.countryID = countryID;
        }

        @Override
        protected String doInBackground(String... strings) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

//                  getting Enum Blocks
                    if (listActivityCreated) {
                        model = new SyncModel();
                        model.setstatusID(0);
                        list.add(model);
                    }
                    new GetAllData(mContext, "EnumBlock", syncListAdapter, list).execute(countryID);
                    bi.noItem.setVisibility(View.GONE);

//                  getting Users!!
//                    Toast.makeText(SyncActivity.this, "Sync Users", Toast.LENGTH_SHORT).show();
                    if (listActivityCreated) {
                        model = new SyncModel();
                        model.setstatusID(0);
                        list.add(model);
                    }
                    new GetAllData(mContext, "User", syncListAdapter, list).execute(countryID);

//                    Getting App Version
//                    Toast.makeText(SyncActivity.this, "Sync App Version", Toast.LENGTH_SHORT).show();
                    if (listActivityCreated) {
                        model = new SyncModel();
                        model.setstatusID(0);
                        list.add(model);
                    }
                    new GetAllData(mContext, "VersionApp", syncListAdapter, list).execute();

                    listActivityCreated = false;
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

//                    populateSpinner(mContext);

                    editor.putBoolean("flag", true);
                    editor.commit();

                    dbBackup();

                }
            }, 1200);
        }
    }

}
