package edu.aku.hassannaqvi.uenmd_hhlisting.Get;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.EnumBlockContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.UsersContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.VersionAppContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.DatabaseHelper;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.uenmd_hhlisting.Other.SyncModel;
import edu.aku.hassannaqvi.uenmd_hhlisting.adapters.SyncListAdapter;

/**
 * Created by ali.azaz on 7/14/2017.
 */

public class GetAllData extends AsyncTask<String, String, String> {

    HttpURLConnection urlConnection;
    SyncListAdapter adapter;
    List<SyncModel> list;
    int position;
    private String TAG = "";
    private Context mContext;
    private ProgressDialog pd;
    private String syncClass;


    public GetAllData(Context context, String syncClass) {
        mContext = context;
        this.syncClass = syncClass;
        TAG = "Get" + syncClass;
    }

    public GetAllData(Context context, String syncClass, SyncListAdapter adapter, List<SyncModel> list) {
        mContext = context;
        this.syncClass = syncClass;
        this.adapter = adapter;
        this.list = list;
        TAG = "Get" + syncClass;
        switch (syncClass) {
            case "EnumBlock":
                position = 0;
                break;
            case "User":
                position = 1;
                break;
            case "VersionApp":
                position = 2;
                break;
        }
        list.get(position).settableName(syncClass);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setTitle("Syncing " + syncClass);
        pd.setMessage("Getting connected to server...");
//        pd.show();
        list.get(position).setstatus("Getting connected to server...");
        list.get(position).setstatusID(2);
        list.get(position).setmessage("");
        adapter.updatesyncList(list);

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        switch (values[0]) {
            case "EnumBlock":
                position = 0;
                break;
            case "User":
                position = 1;
                break;
            case "VersionApp":
                position = 2;
                break;
        }
        list.get(position).setstatus("Syncing");
        list.get(position).setstatusID(2);
        list.get(position).setmessage("");
        adapter.updatesyncList(list);
    }

    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();

        URL url = null;
        try {
            switch (syncClass) {
                case "EnumBlock":
                    url = new URL(MainApp._HOST_URL + EnumBlockContract.EnumBlockTable._URI);
                    position = 0;
                    break;
                case "User":
                    url = new URL(MainApp._HOST_URL + UsersContract.UsersTable._URI);
                    position = 1;
                    break;
                case "VersionApp":
                    url = new URL(MainApp._UPDATE_URL + VersionAppContract.VersionAppTable._URI);
                    position = 2;
                    break;
            }

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(100000 /* milliseconds */);
            urlConnection.setConnectTimeout(150000 /* milliseconds */);

            switch (syncClass) {
                case "EnumBlock":
                case "User":

                    if (args[0] != null && !args[0].equals("")) {
                        if (Integer.valueOf(args[0]) > 0) {
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setDoOutput(true);
                            urlConnection.setDoInput(true);
                            urlConnection.setRequestProperty("Content-Type", "application/json");
                            urlConnection.setRequestProperty("charset", "utf-8");
                            urlConnection.setUseCaches(false);

                            // Starts the query
                            urlConnection.connect();
                            JSONArray jsonSync = new JSONArray();
                            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                            JSONObject json = new JSONObject();
                            try {
                                json.put("country_id", args[0]);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            Log.d(TAG, "downloadUrl: " + json.toString());
                            wr.writeBytes(json.toString());
                            wr.flush();
                            wr.close();
                        }
                    }
                    break;
            }


            Log.d(TAG, "doInBackground: " + urlConnection.getResponseCode());
            publishProgress(syncClass);
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                publishProgress("In Progress");

                String line;
                while ((line = reader.readLine()) != null) {
                    Log.i(TAG, syncClass + " In: " + line);
                    result.append(line);
                }
            }
        } catch (java.net.SocketTimeoutException e) {
            return null;
        } catch (java.io.IOException e) {
            return null;
        } finally {
            urlConnection.disconnect();
        }


        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {

        //Do something with the JSON string
        if (result != null) {
            String json = result;
            if (json.length() > 0) {
                DatabaseHelper db = new DatabaseHelper(mContext);
                try {
                    JSONArray jsonArray = new JSONArray(json);

                    switch (syncClass) {
                        case "EnumBlock":
                            db.syncEnumBlocks(jsonArray);
                            position = 0;
                            break;
                        case "User":
                            db.syncUsers(jsonArray);
                            position = 1;
                            break;
                        case "VersionApp":
                            db.syncVersionApp(jsonArray);
                            position = 2;
                            break;
                    }

                    pd.setMessage("Received: " + jsonArray.length());
                    list.get(position).setmessage("Received: " + jsonArray.length());
                    list.get(position).setstatus("Successfull");
                    list.get(position).setstatusID(3);
                    adapter.updatesyncList(list);
//                    pd.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                pd.setMessage("Received: " + json.length() + "");
                list.get(position).setmessage("Received: " + json.length() + "");
                list.get(position).setstatus("Successfull");
                list.get(position).setstatusID(3);
                adapter.updatesyncList(list);
//                pd.show();
            }
        } else {
            pd.setTitle("Connection Error");
            pd.setMessage("Server not found!");
            list.get(position).setstatus("Failed");
            list.get(position).setstatusID(1);
            list.get(position).setmessage("Server not found!");
            adapter.updatesyncList(list);
//            pd.show();
        }
    }

}
