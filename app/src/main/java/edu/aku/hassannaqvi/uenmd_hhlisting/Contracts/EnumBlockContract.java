package edu.aku.hassannaqvi.uenmd_hhlisting.Contracts;


import android.database.Cursor;
import android.provider.BaseColumns;

import org.json.JSONException;
import org.json.JSONObject;

public class EnumBlockContract {

    private static final String TAG = "EnumBlock_CONTRACT";
    String dist_id;
    String geoarea;
    String cluster;

    public EnumBlockContract() {
        // Default Constructor
    }

    public EnumBlockContract Sync(JSONObject jsonObject) throws JSONException {
        this.dist_id = jsonObject.getString(EnumBlockTable.COLUMN_DIST_ID);
        this.geoarea = jsonObject.getString(EnumBlockTable.COLUMN_GEO_AREA);
        this.cluster = jsonObject.getString(EnumBlockTable.COLUMN_CLUSTER_AREA);
        return this;
    }

    public EnumBlockContract HydrateEnum(Cursor cursor) {
        this.dist_id = cursor.getString(cursor.getColumnIndexOrThrow(EnumBlockTable.COLUMN_DIST_ID));
        this.geoarea = cursor.getString(cursor.getColumnIndexOrThrow(EnumBlockTable.COLUMN_GEO_AREA));
        this.cluster = cursor.getString(cursor.getColumnIndexOrThrow(EnumBlockTable.COLUMN_CLUSTER_AREA));
        return this;
    }

    public String getDist_id() {
        return dist_id;
    }

    public void setDist_id(String dist_id) {
        this.dist_id = dist_id;
    }

    public String getGeoarea() {
        return geoarea;
    }

    public void setGeoarea(String geoarea) {
        this.geoarea = geoarea;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public JSONObject toJSONObject() throws JSONException {

        JSONObject json = new JSONObject();
        json.put(EnumBlockTable.COLUMN_DIST_ID, this.dist_id == null ? JSONObject.NULL : this.dist_id);
        json.put(EnumBlockTable.COLUMN_GEO_AREA, this.geoarea == null ? JSONObject.NULL : this.geoarea);
        json.put(EnumBlockTable.COLUMN_CLUSTER_AREA, this.cluster == null ? JSONObject.NULL : this.cluster);
        return json;
    }


    public static abstract class EnumBlockTable implements BaseColumns {

        public static final String TABLE_NAME = "clusters";
        public static final String COLUMN_DIST_ID = "dist_id";
        public static final String COLUMN_GEO_AREA = "geoarea";
        public static final String COLUMN_CLUSTER_AREA = "cluster_no";
        //        public static final String _URI = "enumblock.php";
        public static final String _URI = "clusters.php";
    }
}