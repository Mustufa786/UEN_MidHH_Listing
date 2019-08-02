package edu.aku.hassannaqvi.casi_hhlisting.Get;

/**
 * Created by hassan.naqvi on 11/5/2016.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.aku.hassannaqvi.casi_hhlisting.Core.DatabaseHelper;
import edu.aku.hassannaqvi.casi_hhlisting.Core.MainApp;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hassan.naqvi on 4/28/2016.
 */
public class GetUpdates extends AsyncTask<String, String, String> {

    private final String TAG = "GetUpdates()";
    HttpURLConnection urlConnection;
    private Context mContext;


    public GetUpdates(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();

        URL url = null;
        try {
            url = new URL(MainApp._UPDATE_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            Log.d(TAG, "doInBackground: " + urlConnection.getResponseCode());
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    Log.i(TAG, "Updates In: " + line);
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
                    JSONArray ja = new JSONArray(json);
                    JSONObject jObject = new JSONObject(ja.getString(0));


                    JSONObject properties = new JSONObject(jObject.getString("properties"));
                    JSONObject apkInfo = new JSONObject(jObject.getString("apkInfo"));

                    String path = jObject.getString("path");
                    String packageId = properties.getString("packageId");
                    String versionCode = apkInfo.getString("versionCode");
                    Toast.makeText(mContext, packageId + "-" + versionCode, Toast.LENGTH_LONG).show();

                    SharedPreferences.Editor editor = mContext.getSharedPreferences("info", MODE_PRIVATE).edit();
                    editor.putString("path", path);
                    editor.putString("packageId", packageId);
                    editor.putString("versionCode", versionCode);
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
        } else {

        }
    }

}