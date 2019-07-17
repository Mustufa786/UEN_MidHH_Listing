package edu.aku.hassannaqvi.casi_hhlisting.Get;

/**
 * Created by javed.khan on 2/21/2018.
 */

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
import java.util.HashMap;

import edu.aku.hassannaqvi.casi_hhlisting.Contracts.EnumBlockContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.TeamsContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.UsersContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.VersionAppContract;
import edu.aku.hassannaqvi.casi_hhlisting.Core.DataBaseHelper;
import edu.aku.hassannaqvi.casi_hhlisting.Core.MainApp;


/**
 * Created by ali.azaz on 7/14/2017.
 */

public class GetAllData extends AsyncTask<String, String, String> {

    HttpURLConnection urlConnection;
    private String TAG = "";
    private Context mContext;
    private ProgressDialog pd;

    private String syncClass;


    public GetAllData(Context context, String syncClass) {
        mContext = context;
        this.syncClass = syncClass;

        TAG = "Get" + syncClass;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setTitle("Syncing " + syncClass);
        pd.setMessage("Getting connected to server...");
        pd.show();

    }

    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();

        URL url = null;
        try {
            switch (syncClass) {
                case "EnumBlock":
                    url = new URL(MainApp._HOST_URL + EnumBlockContract.EnumBlockTable._URI);
                    break;
                case "User":
                    url = new URL(MainApp._HOST_URL + UsersContract.SingleUser._URI);
                    break;
                case "Team":
                    url = new URL(MainApp._HOST_URL + TeamsContract.SingleTaluka._URI);
                    break;
                case "VersionApp":
                    url = new URL(MainApp._UPDATE_URL + VersionAppContract.VersionAppTable._URI);
                    break;
            }

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);

            switch (syncClass) {
                case "EnumBlock":
                case "User":

                    HashMap<String, String> tagVal = MainApp.getTagValues(mContext);

                    if (tagVal.get("org") != null && (!tagVal.get("org").equals("null") && tagVal.get("org") != null && !tagVal.get("org").equals(""))) {
                        if (Integer.valueOf(tagVal.get("org")) > 0) {
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
                                json.put("id_org", tagVal.get("org"));
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
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

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
                DataBaseHelper db = new DataBaseHelper(mContext);
                try {
                    JSONArray jsonArray = new JSONArray(json);

                    switch (syncClass) {
                        case "EnumBlock":
                            db.syncEnumBlocks(jsonArray);
                            break;
                        case "User":
                            db.syncUsers(jsonArray);
                            break;
                        case "Team":
                            db.syncTeams(jsonArray);
                            break;
                        case "VersionApp":
                            db.syncVersionApp(jsonArray);
                            break;
                    }

                    pd.setMessage("Received: " + jsonArray.length());
                    pd.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                pd.setMessage("Received: " + json.length() + "");
                pd.show();
            }
        } else {
            pd.setTitle("Connection Error");
            pd.setMessage("Server not found!");
            pd.show();
        }
    }
}