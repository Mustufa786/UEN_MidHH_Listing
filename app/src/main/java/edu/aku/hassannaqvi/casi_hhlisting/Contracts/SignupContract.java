package edu.aku.hassannaqvi.casi_hhlisting.Contracts;

import android.database.Cursor;
import android.provider.BaseColumns;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hassan.naqvi on 11/30/2016.
 */

public class SignupContract {

    private static final String TAG = "Signup_Contract";
    String _ID;
    String fullName;
    String designation;
    String userName;
    String password;
    String cPassword;
    String countryId;

    public SignupContract() {
        // Default Constructor
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getcPassword() {
        return cPassword;
    }

    public void setcPassword(String cPassword) {
        this.cPassword = cPassword;
    }

    public SignupContract Sync(JSONObject jsonObject) throws JSONException {
        this._ID = jsonObject.getString(SignUpTable._ID);
        this.fullName = jsonObject.getString(SignUpTable.FULLNAME);
        this.designation = jsonObject.getString(SignUpTable.DESIGNATION);
        this.userName = jsonObject.getString(SignUpTable.USERNAME);
        this.password = jsonObject.getString(SignUpTable.PASSWORD);
        this.countryId = jsonObject.getString(SignUpTable.COUNTRY_ID);

        return this;

    }

    public SignupContract Hydrate(Cursor cursor) {
        this._ID = cursor.getString(cursor.getColumnIndex(SignUpTable._ID));
        this.fullName = cursor.getString(cursor.getColumnIndex(SignUpTable.FULLNAME));
        this.userName = cursor.getString(cursor.getColumnIndex(SignUpTable.USERNAME));
        this.designation = cursor.getString(cursor.getColumnIndex(SignUpTable.DESIGNATION));
        this.password = cursor.getString(cursor.getColumnIndex(SignUpTable.PASSWORD));
        this.countryId = cursor.getString(cursor.getColumnIndex(SignUpTable.COUNTRY_ID));

        return this;

    }


    public JSONObject toJSONObject() throws JSONException {

        JSONObject json = new JSONObject();
        json.put(SignUpTable._ID, this._ID == null ? JSONObject.NULL : this._ID);
        json.put(SignUpTable.FULLNAME, this.fullName == null ? JSONObject.NULL : this.fullName);
        json.put(SignUpTable.DESIGNATION, this.designation == null ? JSONObject.NULL : this.designation);
        json.put(SignUpTable.USERNAME, this.userName == null ? JSONObject.NULL : this.userName);
        json.put(SignUpTable.PASSWORD, this.password == null ? JSONObject.NULL : this.password);
        json.put(SignUpTable.COUNTRY_ID, this.countryId == null ? JSONObject.NULL : this.countryId);

        return json;
    }

    public static abstract class SignUpTable implements BaseColumns {

        public static final String TABLE_NAME = "signup";
        public static final String _ID = "id";
        public static final String FULLNAME = "fullname";
        public static final String USERNAME = "username";
        public static final String DESIGNATION = "designation";
        public static final String PASSWORD = "password";
        public static final String COUNTRY_ID = "country_id";
        public static final String COLUMN_SYNCED = "synced";
        public static final String COLUMN_SYNCED_DATE = "synced_date";

        public static final String _URL = "signup.php";

    }
}