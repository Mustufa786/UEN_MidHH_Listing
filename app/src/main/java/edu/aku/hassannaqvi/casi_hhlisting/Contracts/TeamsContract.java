package edu.aku.hassannaqvi.casi_hhlisting.Contracts;

import android.database.Cursor;
import android.provider.BaseColumns;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hassan.naqvi on 10/31/2016.
 */

public class TeamsContract {

    private String teamNo;

    public TeamsContract() {
    }

    public TeamsContract sync(JSONObject jsonObject) throws JSONException {
        this.teamNo = jsonObject.getString(SingleTaluka.COLUMN_TEAM_NO);

        return this;
    }

    public TeamsContract hydrate(Cursor cursor) {
        this.teamNo = cursor.getString(cursor.getColumnIndex(SingleTaluka.COLUMN_TEAM_NO));

        return this;
    }

    public String getTeamNo() {
        return teamNo;
    }

    public void setTeamNo(String teamNo) {
        this.teamNo = teamNo;
    }

    public static abstract class SingleTaluka implements BaseColumns {

        public static final String TABLE_NAME = "Districts";
        public static final String COLUMN_NAME_NULLABLE = "nullColumnHack";
        public static final String _ID = "_ID";
        public static final String COLUMN_TEAM_NO = "teamno";

        public static final String _URI = "teams.php";
    }

}
