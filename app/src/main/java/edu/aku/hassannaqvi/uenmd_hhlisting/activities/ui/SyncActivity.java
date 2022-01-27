package edu.aku.hassannaqvi.uenmd_hhlisting.activities.ui;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.EnumBlockContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.ListingContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.UsersContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.DatabaseHelper;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.uenmd_hhlisting.Other.SyncModel;
import edu.aku.hassannaqvi.uenmd_hhlisting.R;
import edu.aku.hassannaqvi.uenmd_hhlisting.adapters.SyncListAdapter;
import edu.aku.hassannaqvi.uenmd_hhlisting.databinding.ActivitySyncBinding;
import edu.aku.hassannaqvi.uenmd_hhlisting.workers.DataDownWorkerALL;
import edu.aku.hassannaqvi.uenmd_hhlisting.workers.DataUpWorkerALL;


public class SyncActivity extends AppCompatActivity {
    private static final String TAG = "SyncActivity";
    final Handler handler = new Handler();
    DatabaseHelper db;
    SyncListAdapter syncListAdapter;
    ActivitySyncBinding bi;
    List<SyncModel> uploadTables;
    List<SyncModel> downloadTables;
    Boolean listActivityCreated;
    Boolean uploadlistActivityCreated;
    String campCode;
    private int totalFiles;
    private long tStart;
    private String progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_sync);
        bi.setCallback(this);
        setSupportActionBar(bi.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = MainApp.db;
        uploadTables = new ArrayList<>();
        downloadTables = new ArrayList<>();
        MainApp.uploadData = new ArrayList<>();

        //bi.noItem.setVisibility(View.VISIBLE);
        bi.noDataItem.setVisibility(View.VISIBLE);
        listActivityCreated = true;
        uploadlistActivityCreated = true;

        db = MainApp.db;
        //dbBackup(this);
/*        OneTimeWorkRequest JSONWorker =
                new OneTimeWorkRequest.Builder(ReadJSONWorker.class)
                        .build();
        WorkManager.getInstance(this).enqueue(JSONWorker);*/

    }

    void setAdapter(List<SyncModel> tables) {
        syncListAdapter = new SyncListAdapter(tables);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        bi.rvUploadList.setLayoutManager(mLayoutManager2);
        bi.rvUploadList.setItemAnimator(new DefaultItemAnimator());
        bi.rvUploadList.setAdapter(syncListAdapter);
        syncListAdapter.notifyDataSetChanged();
        if (syncListAdapter.getItemCount() > 0) {
            bi.noDataItem.setVisibility(View.GONE);
        } else {
            bi.noDataItem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    public void ProcessStart(View view) {

        if (!MainApp.isNetworkAvailable(this))
            return;

        switch (view.getId()) {

            case R.id.btnUpload:

                bi.activityTitle.setText("Upload Data");

                bi.dataLayout.setVisibility(View.VISIBLE);
                bi.photoLayout.setVisibility(View.GONE);
                bi.mTextViewS.setVisibility(View.GONE);
                bi.pBar.setVisibility(View.GONE);
                uploadTables.clear();
                MainApp.uploadData.clear();

                // Forms
                uploadTables.add(new SyncModel(ListingContract.ListingEntry.TABLE_NAME));
                try {
                    MainApp.uploadData.add(db.getUnsyncedListings());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "ProcessStart: JSONException(Forms): " + e.getMessage());
                    Toast.makeText(this, "JSONException(Forms): " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                MainApp.downloadData = new String[MainApp.uploadData.size()];

                setAdapter(uploadTables);
                BeginUpload();
                break;
            case R.id.btnSync:
                String select;
                String filter;


                bi.activityTitle.setText("Download Data");

                MainApp.downloadData = new String[0];
                bi.dataLayout.setVisibility(View.VISIBLE);
                bi.photoLayout.setVisibility(View.GONE);
                bi.mTextViewS.setVisibility(View.GONE);
                bi.pBar.setVisibility(View.GONE);
                downloadTables.clear();


                boolean sync_flag = getIntent().getBooleanExtra("sync_flag", false);
                if (sync_flag) {
                    select = " * ";
                    filter = " enabled = '1' ";
                    downloadTables.add(new SyncModel(UsersContract.UsersTable.TABLE_NAME, select, filter));
                } else {
                    select = " * ";
                    filter = " col_flag is null AND dist_id = '" + MainApp.user.getDIST_ID() + "' ";
                    downloadTables.add(new SyncModel(EnumBlockContract.EnumBlockTable.TABLE_NAME, select, filter));
                }
                MainApp.downloadData = new String[downloadTables.size()];
                setAdapter(downloadTables);
                BeginDownload();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void BeginDownload() {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        List<OneTimeWorkRequest> workRequests = new ArrayList<>();

        for (int i = 0; i < downloadTables.size(); i++) {
            Data.Builder data = new Data.Builder()
                    .putString("table", downloadTables.get(i).gettableName())
                    .putInt("position", i)
                    .putString("select", downloadTables.get(i).getSelect() != null ? downloadTables.get(i).getSelect() : " * ")
                    .putString("filter", downloadTables.get(i).getFilter() != null ? downloadTables.get(i).getFilter() : " 1=1 ");

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DataDownWorkerALL.class)
                    .addTag(String.valueOf(i))
                    .setInputData(data.build()).build();
            workRequests.add(workRequest);

        }

        // FOR SIMULTANEOUS WORKREQUESTS (ALL TABLES DOWNLOAD AT THE SAME TIME)
        WorkManager wm = WorkManager.getInstance(this);
        WorkContinuation wc = wm.beginWith(workRequests);
        wc.enqueue();

        wc.getWorkInfosLiveData().observe(this, workInfos -> {
            Log.d(TAG, "workInfos: " + workInfos.size());
            for (WorkInfo workInfo : workInfos) {
                Log.d(TAG, "workInfo: getState " + workInfo.getState());
                Log.d(TAG, "workInfo: data " + workInfo.getOutputData().getString("data"));
                Log.d(TAG, "workInfo: error " + workInfo.getOutputData().getString("error"));
                Log.d(TAG, "workInfo: position " + workInfo.getOutputData().getInt("position", 0));
            }
            for (WorkInfo workInfo : workInfos) {
                int position = workInfo.getOutputData().getInt("position", 0);
                String tableName = downloadTables.get(position).gettableName();

                        /*String progress = workInfo.getProgress().getString("progress");
                        bi.wmError.setText("Progress: " + progress);*/

                if (workInfo.getState() != null &&
                        workInfo.getState() == WorkInfo.State.SUCCEEDED) {

                    //String result = workInfo.getOutputData().getString("data");
                    String result = MainApp.downloadData[position];
//Do something with the JSON string
                    if (result != null) {
                        if (result.length() > 0) {
                            Log.d(TAG, "onChanged: result " + result);
                            System.out.println("SYSTEM onChanged: result" + result);
                            DatabaseHelper db = new DatabaseHelper(SyncActivity.this);
                            JSONArray jsonArray = new JSONArray();
                            int insertCount = 0;
                            switch (tableName) {
                                case UsersContract.UsersTable.TABLE_NAME:
                                    try {
                                        jsonArray = new JSONArray(result);

                                        insertCount = db.syncUsers(jsonArray);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        downloadTables.get(position).setstatus("Process Failed");
                                        downloadTables.get(position).setstatusID(1);
                                        downloadTables.get(position).setmessage(e.getMessage());
                                        syncListAdapter.updatesyncList(downloadTables);
                                    }
                                    break;
/*                                case VersionTable.TABLE_NAME:
                                    try {
                                        insertCount = db.syncVersionApp(new JSONObject(result));

                                        if (insertCount == 1) jsonArray.put("1");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        downloadTables.get(position).setstatus("Process Failed");
                                        downloadTables.get(position).setstatusID(1);
                                        downloadTables.get(position).setmessage(e.getMessage());
                                        syncListAdapter.updatesyncList(downloadTables);
                                    }
                                    break;*/

                                case EnumBlockContract.EnumBlockTable.TABLE_NAME:
                                    try {
                                        jsonArray = new JSONArray(result);

                                        insertCount = db.syncEnumBlocks(jsonArray);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        downloadTables.get(position).setstatus("Process Failed");
                                        downloadTables.get(position).setstatusID(1);
                                        downloadTables.get(position).setmessage(e.getMessage());
                                        syncListAdapter.updatesyncList(downloadTables);
                                    }
                                    break;
                            }

                            downloadTables.get(position).setmessage("Received: " + jsonArray.length() + "  •  Saved: " + insertCount);
                            downloadTables.get(position).setstatus(insertCount == 0 ? "Unsuccessful" : "Successful");
                            downloadTables.get(position).setstatusID(insertCount == 0 ? 1 : 3);
                            syncListAdapter.updatesyncList(downloadTables);

//                    pd.show();

                        } else {
                            downloadTables.get(position).setmessage("Received: " + result.length() + "");
                            downloadTables.get(position).setstatus("Successful");
                            downloadTables.get(position).setstatusID(3);
                            syncListAdapter.updatesyncList(downloadTables);
//                pd.show();
                        }
                    } else {
                        downloadTables.get(position).setstatus("Process Failed");
                        downloadTables.get(position).setstatusID(1);
                        downloadTables.get(position).setmessage("Server not found!");
                        syncListAdapter.updatesyncList(downloadTables);
//            pd.show();
                    }
                }
                //mTextView1.append("\n" + workInfo.getState().name());
                if (workInfo.getState() != null &&
                        workInfo.getState() == WorkInfo.State.FAILED) {
                    String message = workInfo.getOutputData().getString("error");
                    downloadTables.get(position).setstatus("Process Failed");
                    downloadTables.get(position).setstatusID(1);
                    downloadTables.get(position).setmessage(message);
                    syncListAdapter.updatesyncList(downloadTables);

                }
            }
        });
    }

    private void BeginUpload() {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        List<OneTimeWorkRequest> workRequests = new ArrayList<>();

        for (int i = 0; i < uploadTables.size(); i++) {
            Data data = new Data.Builder()
                    .putString("table", uploadTables.get(i).gettableName())
                    .putInt("position", i)
                    //    .putString("data", uploadData.get(i).toString())

                    //.putString("columns", "_id, sysdate")
                    // .putString("where", where)
                    .build();
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DataUpWorkerALL.class)
                    .addTag(String.valueOf(i))
                    .setInputData(data).build();
            workRequests.add(workRequest);

        }

        // FOR SIMULTANEOUS WORKREQUESTS (ALL TABLES DOWNLOAD AT THE SAME TIME)
        WorkManager wm = WorkManager.getInstance(this);
        WorkContinuation wc = wm.beginWith(workRequests);
        wc.enqueue();

        // FOR WORKREQUESTS CHAIN (ONE TABLE DOWNLOADS AT A TIME)
/*        WorkManager wm = WorkManager.getInstance();
        WorkContinuation wc = wm.beginWith(workRequests.get(0));
        for (int i=1; i < workRequests.size(); i++ ) {
            wc = wc.then(workRequests.get(i));
        }
        wc.enqueue();*/


        wc.getWorkInfosLiveData().observe(this, workInfos -> {
            Log.d(TAG, "workInfos: " + workInfos.size());
            for (WorkInfo workInfo : workInfos) {
                Log.d(TAG, "workInfo: getState " + workInfo.getState());
                Log.d(TAG, "workInfo: data " + workInfo.getTags());
                Log.d(TAG, "workInfo: data " + workInfo.getOutputData().getString("message"));
                Log.d(TAG, "workInfo: error " + workInfo.getOutputData().getString("error"));
                Log.d(TAG, "workInfo: position " + workInfo.getOutputData().getInt("position", 0));
            }
            for (WorkInfo workInfo : workInfos) {
                int position = workInfo.getOutputData().getInt("position", 0);
                String tableName = uploadTables.get(position).gettableName();
                String result = MainApp.downloadData[position];

                        /*String progress = workInfo.getProgress().getString("progress");
                        bi.wmError.setText("Progress: " + progress);*/

                if (workInfo.getState() != null &&
                        workInfo.getState() == WorkInfo.State.SUCCEEDED) {

                    //String result = workInfo.getOutputData().getString("message");

                    int sSynced = 0;
                    int sDuplicate = 0;
                    StringBuilder sSyncedError = new StringBuilder();
                    JSONArray json;

                    if (result != null) {
                        if (result.length() > 0) {
                            try {
                                Log.d(TAG, "onPostExecute: " + result);
                                json = new JSONArray(result);

                                // DatabaseHelper db = new DatabaseHelper(SyncActivity.this); // Database Helper

                                Method method = null;
                                for (Method method1 : db.getClass().getDeclaredMethods()) {

                                    Log.d(TAG, "onChanged Methods: " + method1.getName());
                                    /**
                                     * MAKE SURE TABLE_NAME = <table> IS SAME AS updateSynced<table> :
                                     *
                                     *      -   public static final String TABLE_NAME = "<table>";  // in Contract
                                     *      -   public JSONArray updateSynced<table>() {              // in DatabaseHelper
                                     *
                                     *      e.g: Forms and updateSyncedForms
                                     *
                                     */
                                    Log.d(TAG, "onChanged Names: updateSynced" + tableName);
                                    Log.d(TAG, "onChanged Compare: " + method1.getName().equals("updateSynced" + tableName));
                                    if (method1.getName().equals("updateSynced" + tableName)) {
                                        method = method1;
                                        //Toast.makeText(SyncActivity.this, "updateSynced not found: updateSynced" + tableName, Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                                if (method != null) {
                                    for (int i = 0; i < json.length(); i++) {
                                        JSONObject jsonObject = new JSONObject(json.getString(i));
                                        Log.d(TAG, "onChanged: " + json.getString(i));
                                        if (jsonObject.getString("status").equals("1") && jsonObject.getString("error").equals("0")) {
                                            method.invoke(db, jsonObject.getString("id"));
                                            sSynced++;
                                        } else if (jsonObject.getString("status").equals("2") && jsonObject.getString("error").equals("0")) {
                                            method.invoke(db, jsonObject.getString("id"));
                                            sDuplicate++;
                                        } else {
                                            sSyncedError.append("\nError: ").append(jsonObject.getString("message"));
                                        }
                                    }
                                    Toast.makeText(SyncActivity.this, tableName + " synced: " + sSynced + "\r\nErrors: " + sSyncedError, Toast.LENGTH_SHORT).show();

                                    if (sSyncedError.toString().equals("")) {
                                        uploadTables.get(position).setmessage(" Synced: " + sSynced + "  •  Duplicates: " + sDuplicate + "  •  Errors: " + sSyncedError);
                                        uploadTables.get(position).setstatus("Completed");
                                        uploadTables.get(position).setstatusID(3);
                                        syncListAdapter.updatesyncList(uploadTables);
                                    } else {
                                        uploadTables.get(position).setmessage(" Synced: " + sSynced + "  •  Duplicates: " + sDuplicate + "  •  Errors: " + sSyncedError);
                                        uploadTables.get(position).setstatus("Process Failed");
                                        uploadTables.get(position).setstatusID(1);
                                        syncListAdapter.updatesyncList(uploadTables);
                                    }
                                } else {
                                    uploadTables.get(position).setmessage("Method not found: updateSynced" + tableName);
                                    uploadTables.get(position).setstatus("Process Failed");
                                    uploadTables.get(position).setstatusID(1);
                                    syncListAdapter.updatesyncList(uploadTables);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(SyncActivity.this, "Sync Result:  " + result, Toast.LENGTH_SHORT).show();

                                uploadTables.get(position).setmessage(result);
                                uploadTables.get(position).setstatus("Process Failed");
                                uploadTables.get(position).setstatusID(1);
                                syncListAdapter.updatesyncList(uploadTables);

                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                                uploadTables.get(position).setstatus("Process Failed");
                                uploadTables.get(position).setstatusID(1);
                                uploadTables.get(position).setmessage(e.getMessage());
                                syncListAdapter.updatesyncList(uploadTables);
                            }
                        } else {
                            uploadTables.get(position).setmessage("Received: " + result.length() + "");
                            uploadTables.get(position).setstatus("Successful");
                            uploadTables.get(position).setstatusID(3);
                            syncListAdapter.updatesyncList(uploadTables);
//                pd.show();
                        }
                    } else {
                        uploadTables.get(position).setstatus("Process Failed");
                        uploadTables.get(position).setstatusID(1);
                        uploadTables.get(position).setmessage("Server not found!");
                        syncListAdapter.updatesyncList(uploadTables);
//            pd.show();
                    }
                }
                //mTextView1.append("\n" + workInfo.getState().name());
                if (workInfo.getState() != null &&
                        workInfo.getState() == WorkInfo.State.FAILED) {
                    String message = workInfo.getOutputData().getString("error");
                    if (message.equals("No new records to upload")) {
                        uploadTables.get(position).setmessage(message);
                        uploadTables.get(position).setstatus("Not processed");
                        uploadTables.get(position).setstatusID(4);
                        syncListAdapter.updatesyncList(uploadTables);
                    } else {
                        uploadTables.get(position).setstatus("Process Failed");
                        uploadTables.get(position).setstatusID(1);
                        uploadTables.get(position).setmessage(message);
                        syncListAdapter.updatesyncList(uploadTables);
                    }
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                //onBackPressed();
                finish();
                //   downloadApp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

/*    private void downloadApp() throws MalformedURLException {

        URL url = new URL(MainApp._HOST_URL + _UPDATE_URL);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addDataScheme("package");
        registerReceiver(br, intentFilter);

    }*/


}