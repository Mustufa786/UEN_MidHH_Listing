package edu.aku.hassannaqvi.casi_hhlisting.Core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import edu.aku.hassannaqvi.casi_hhlisting.Contracts.BLRandomContract.singleRandomHH;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.EnumBlockContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.EnumBlockContract.EnumBlockTable;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.ListingContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.ListingContract.ListingEntry;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.SignupContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.SignupContract.SignUpTable;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.TeamsContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.TeamsContract.SingleTaluka;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.UsersContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.UsersContract.UsersTable;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.VersionAppContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.VersionAppContract.VersionAppTable;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.VerticesContract;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.VerticesContract.SingleVertices;


/**
 * Created by hassan.naqvi on 10/18/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    // The name of database.
    public static final String DATABASE_NAME = "casi-hhl.db";
    public static final String PROJECT_NAME = "casi-hhl-2019";
    public static final String DB_NAME = DATABASE_NAME.replace(".db", "-copy.db");
    public static final String FOLDER_NAME = "DMU-CASIPAKHHL";
    public static final String SQL_CREATE_BL_RANDOM = "CREATE TABLE " + singleRandomHH.TABLE_NAME + "("
            + singleRandomHH.COLUMN_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT,"
            + singleRandomHH.COLUMN_CLUSTER_BLOCK_CODE + " TEXT,"
            + singleRandomHH.COLUMN_LUID + " TEXT,"
            + singleRandomHH.COLUMN_STRUCTURE_NO + " TEXT,"
            + singleRandomHH.COLUMN_FAMILY_EXT_CODE + " TEXT,"
            + singleRandomHH.COLUMN_HH_HEAD + " TEXT,"
            + singleRandomHH.COLUMN_CONTACT + " TEXT,"
            + singleRandomHH.COLUMN_HH_SELECTED_STRUCT + " TEXT,"
            + singleRandomHH.COLUMN_RANDOMDT + " TEXT );";
    // Change this when you change the database schema.
    private static final int DATABASE_VERSION = 1;
    public static String TAG = "DataBaseHelper";
    public static String DB_FORM_ID;
    // Create a table to hold Listings.
    final String SQL_CREATE_LISTING_TABLE = "CREATE TABLE " + ListingEntry.TABLE_NAME + " (" +
            ListingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ListingEntry.COLUMN_NAME_UID + " TEXT, " +
            ListingEntry.COLUMN_NAME_HHDATETIME + " TEXT, " +
            ListingEntry.COLUMN_NAME_ENUMCODE + " TEXT, " +
            ListingEntry.COLUMN_NAME_CLUSTERCODE + " TEXT, " +
            ListingEntry.COLUMN_NAME_ENUMSTR + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH01 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH02 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH03 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH04 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH04OTHER + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH05 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH06 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH07 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH07n + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH08 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH08A1 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH09 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH09A1 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH10 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH11 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH12 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH13 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH14 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH15 + " TEXT, " +
            ListingEntry.COLUMN_NAME_HH16 + " TEXT, " +
            ListingEntry.COLUMN_ADDRESS + " TEXT, " +
            ListingEntry.COLUMN_ISNEWHH + " TEXT, " +
            ListingEntry.COLUMN_COUNTER + " TEXT, " +
            ListingEntry.COLUMN_USERNAME + " TEXT, " +
            ListingEntry.COLUMN_NAME_DEVICEID + " TEXT, " +
            ListingEntry.COLUMN_TAGID + " TEXT, " +
            ListingEntry.COLUMN_NAME_GPSLat + " TEXT, " +
            ListingEntry.COLUMN_NAME_GPSLng + " TEXT, " +
            ListingEntry.COLUMN_NAME_GPSTime + " TEXT, " +
            ListingEntry.COLUMN_APPVER + " TEXT, " +
            ListingEntry.COLUMN_NAME_GPSAccuracy + " TEXT, " +
            ListingEntry.COLUMN_NAME_GPSAltitude + " TEXT, " +
            ListingEntry.COLUMN_RANDOMIZED + " TEXT, " +
            ListingEntry.COLUMN_SYNCED + " TEXT, " +
            ListingEntry.COLUMN_SYNCED_DATE + " TEXT " +
            " );";
    final String SQL_CREATE_DISTRICT_TABLE = "CREATE TABLE " + SingleTaluka.TABLE_NAME + " (" +
            SingleTaluka._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            SingleTaluka.COLUMN_TEAM_NO + " TEXT " +
            ");";
    final String SQL_CREATE_PSU_TABLE = "CREATE TABLE " + EnumBlockTable.TABLE_NAME + " (" +
            EnumBlockTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EnumBlockTable.COLUMN_COUNTRY_ID + " TEXT, " +
            EnumBlockTable.COLUMN_GEO_AREA + " TEXT, " +
            EnumBlockTable.COLUMN_CLUSTER_AREA + " TEXT " +
            ");";
    public static final String SQL_CREATE_SIGNUP = "CREATE TABLE " + SignUpTable.TABLE_NAME + "("
            + SignUpTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SignUpTable.FULLNAME + " TEXT,"
            + SignUpTable.USERNAME + " TEXT,"
            + SignUpTable.DESIGNATION + " TEXT,"
            + SignUpTable.PASSWORD + " TEXT,"
            + SignUpTable.COUNTRY_ID + " TEXT, "
            + SignUpTable.COLUMN_DEVICEID + " TEXT, "
            + SignUpTable.COLUMN_FORMDATE + " TEXT, "
            + SignUpTable.COLUMN_SYNCED + " TEXT, "
            + SignUpTable.COLUMN_SYNCED_DATE + " TEXT " +
            ");";
    final String SQL_CREATE_VERTICES_TABLE = "CREATE TABLE " + SingleVertices.TABLE_NAME + " (" +
            SingleVertices._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            SingleVertices.COLUMN_CLUSTER_CODE + " TEXT," +
            SingleVertices.COLUMN_POLY_LAT + " TEXT, " +
            SingleVertices.COLUMN_POLY_LANG + " TEXT, " +
            SingleVertices.COLUMN_POLY_SEQ + " TEXT " +
            ");";
    final String SQL_COUNT_LISTINGS = "SELECT count(*) as ttlisting from " + ListingEntry.TABLE_NAME;
    final String SQL_CREATE_VERSIONAPP = "CREATE TABLE " + VersionAppTable.TABLE_NAME + " (" +
            VersionAppTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            VersionAppTable.COLUMN_VERSION_CODE + " TEXT, " +
            VersionAppTable.COLUMN_VERSION_NAME + " TEXT, " +
            VersionAppTable.COLUMN_PATH_NAME + " TEXT " +
            ");";
    final String SQL_CREATE_USERS = "CREATE TABLE " + UsersTable.TABLE_NAME + "("
            + UsersTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + UsersTable.ROW_USERNAME + " TEXT,"
            + UsersTable.ROW_PASSWORD + " TEXT,"
            + UsersTable.COUNTRY_ID + " TEXT );";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Do the creating of the databases.
        db.execSQL(SQL_CREATE_LISTING_TABLE);
        db.execSQL(SQL_CREATE_DISTRICT_TABLE);
        db.execSQL(SQL_CREATE_PSU_TABLE);
        db.execSQL(SQL_CREATE_USERS);
        db.execSQL(SQL_CREATE_BL_RANDOM);
        db.execSQL(SQL_CREATE_VERTICES_TABLE);
        db.execSQL(SQL_CREATE_SIGNUP);
        db.execSQL(SQL_CREATE_VERSIONAPP);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simply discard all old data and start over when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + ListingEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SingleTaluka.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EnumBlockTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UsersTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SingleVertices.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VersionAppTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SignUpTable.TABLE_NAME);
        onCreate(db);

    }

    public int getListingCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_COUNT_LISTINGS, null);
        int count = 0;

        while (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    public void syncEnumBlocks(JSONArray Enumlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EnumBlockTable.TABLE_NAME, null, null);
        try {
            JSONArray jsonArray = Enumlist;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectCC = jsonArray.getJSONObject(i);

                EnumBlockContract Vc = new EnumBlockContract();
                Vc.Sync(jsonObjectCC);

                ContentValues values = new ContentValues();

                values.put(EnumBlockTable.COLUMN_COUNTRY_ID, Vc.getEbcode());
                values.put(EnumBlockTable.COLUMN_GEO_AREA, Vc.getGeoarea());
                values.put(EnumBlockTable.COLUMN_CLUSTER_AREA, Vc.getCluster());

                db.insert(EnumBlockTable.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
        } finally {
            db.close();
        }
    }

    public boolean Login(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                UsersTable.ROW_USERNAME,
                UsersTable.ROW_PASSWORD,
        };

        String whereClause = UsersTable.ROW_USERNAME + "=? AND " + UsersTable.ROW_PASSWORD + "=?";
        String[] whereArgs = new String[]{username, password};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        try {
            c = db.query(
                    UsersTable.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            if (c.getCount() > 0) {
                return true;
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    public Long addSignUpForm(SignupContract fc) {

        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SignupContract.SignUpTable.FULLNAME, fc.getFullName());
        values.put(SignupContract.SignUpTable.DESIGNATION, fc.getDesignation());
        values.put(SignupContract.SignUpTable.USERNAME, fc.getUserName());
        values.put(SignupContract.SignUpTable.PASSWORD, fc.getPassword());
        values.put(SignupContract.SignUpTable.COUNTRY_ID, fc.getCountryId());
        values.put(SignUpTable.COLUMN_DEVICEID, fc.getDeviceID());
        values.put(SignUpTable.COLUMN_FORMDATE, fc.getFormDate());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                SignupContract.SignUpTable.TABLE_NAME,
                null,
                values);
        return newRowId;
    }

    public Long addForm(ListingContract lc) {

        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ListingEntry.COLUMN_NAME_UID, lc.getUID());
        values.put(ListingEntry.COLUMN_NAME_HHDATETIME, lc.getHhDT());

        values.put(ListingEntry.COLUMN_NAME_ENUMCODE, lc.getEnumCode());
        values.put(ListingEntry.COLUMN_NAME_CLUSTERCODE, lc.getClusterCode());
        values.put(ListingEntry.COLUMN_NAME_ENUMSTR, lc.getEnumStr());

        values.put(ListingEntry.COLUMN_NAME_HH01, lc.getHh01());
        values.put(ListingEntry.COLUMN_NAME_HH02, lc.getHh02());
        values.put(ListingEntry.COLUMN_NAME_HH03, lc.getHh03());

        MainApp.updatePSU(lc.getHh02(), lc.getHh03());
        Log.d(TAG, "PSUExist (Test): " + MainApp.sharedPref.getString(lc.getHh02(), "0"));

        values.put(ListingEntry.COLUMN_NAME_HH04, lc.getHh04());
        values.put(ListingEntry.COLUMN_NAME_HH04OTHER, lc.getHh04other());
        values.put(ListingEntry.COLUMN_NAME_HH05, lc.getHh05());
        values.put(ListingEntry.COLUMN_NAME_HH06, lc.getHh06());
        values.put(ListingEntry.COLUMN_NAME_HH07, lc.getHh07());
        values.put(ListingEntry.COLUMN_NAME_HH07n, lc.getHh07n());
        values.put(ListingEntry.COLUMN_NAME_HH08, lc.getHh08());
        values.put(ListingEntry.COLUMN_NAME_HH09, lc.getHh09());
        values.put(ListingEntry.COLUMN_NAME_HH08A1, lc.getHh08a1());
        values.put(ListingEntry.COLUMN_NAME_HH09A1, lc.getHh09a1());
        values.put(ListingEntry.COLUMN_NAME_HH10, lc.getHh10());
        values.put(ListingEntry.COLUMN_NAME_HH11, lc.getHh11());
        values.put(ListingEntry.COLUMN_NAME_HH12, lc.getHh12());
        values.put(ListingEntry.COLUMN_NAME_HH13, lc.getHh13());
        values.put(ListingEntry.COLUMN_NAME_HH14, lc.getHh14());
        values.put(ListingEntry.COLUMN_NAME_HH15, lc.getHh15());
        values.put(ListingEntry.COLUMN_NAME_HH16, lc.getHh16());
        values.put(ListingEntry.COLUMN_ISNEWHH, lc.getIsNewHH());
        values.put(ListingEntry.COLUMN_ADDRESS, lc.getHhadd());
        values.put(ListingEntry.COLUMN_COUNTER, lc.getCounter());
        values.put(ListingEntry.COLUMN_NAME_DEVICEID, lc.getDeviceID());
        values.put(ListingEntry.COLUMN_USERNAME, lc.getUsername());
        values.put(ListingEntry.COLUMN_NAME_GPSLat, lc.getGPSLat());
        values.put(ListingEntry.COLUMN_NAME_GPSLng, lc.getGPSLng());
        values.put(ListingEntry.COLUMN_NAME_GPSTime, lc.getGPSTime());
        values.put(ListingEntry.COLUMN_NAME_GPSAccuracy, lc.getGPSAcc());
        values.put(ListingEntry.COLUMN_NAME_GPSAltitude, lc.getGPSAlt());
        values.put(ListingEntry.COLUMN_APPVER, lc.getAppVer());
        values.put(ListingEntry.COLUMN_RANDOMIZED, lc.getIsRandom());
        values.put(ListingEntry.COLUMN_TAGID, lc.getTagId());

        long newRowId;
        newRowId = db.insert(
                ListingEntry.TABLE_NAME,
                ListingEntry.COLUMN_NAME_NULLABLE,
                values);
        DB_FORM_ID = String.valueOf(newRowId);

        return newRowId;
    }

    public Long addBLRandom(ListingContract lc) {

        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(singleRandomHH.COLUMN_LUID, lc.getUID());
        values.put(singleRandomHH.COLUMN_RANDOMDT, lc.getHhDT());
        values.put(singleRandomHH.COLUMN_CLUSTER_BLOCK_CODE, lc.getClusterCode());
        values.put(singleRandomHH.COLUMN_STRUCTURE_NO, lc.getHh03());
        values.put(singleRandomHH.COLUMN_FAMILY_EXT_CODE, lc.getHh07());
        values.put(singleRandomHH.COLUMN_HH_HEAD, lc.getHh08());
        values.put(singleRandomHH.COLUMN_CONTACT, lc.getHh09());

        values.put(singleRandomHH.COLUMN_HH_SELECTED_STRUCT, lc.getHh10().equals("1") ? "1" : "2");

        long newRowId;
        newRowId = db.insert(
                singleRandomHH.TABLE_NAME,
                null,
                values);

        return newRowId;
    }

    public void updateListingUID() {
        SQLiteDatabase db = this.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(ListingEntry.COLUMN_NAME_UID, MainApp.lc.getUID());

// Which row to update, based on the title
        String where = ListingEntry._ID + " = ?";
        String[] whereArgs = {MainApp.lc.getID()};

        int count = db.update(
                ListingEntry.TABLE_NAME,
                values,
                where,
                whereArgs);
    }

    public void updateListingRecord(String Clustercode) {
        SQLiteDatabase db = this.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(ListingEntry.COLUMN_RANDOMIZED, "1");

// Which row to update, based on the title
        String where = ListingEntry.COLUMN_NAME_CLUSTERCODE + " = ?";
        String[] whereArgs = {Clustercode};

        int count = db.update(
                ListingEntry.TABLE_NAME,
                values,
                where,
                whereArgs);
    }

    public JSONArray getAllListingsJSON() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                ListingEntry._ID,
                ListingEntry.COLUMN_NAME_UID,
                ListingEntry.COLUMN_NAME_HHDATETIME,
                ListingEntry.COLUMN_NAME_ENUMCODE,
                ListingEntry.COLUMN_NAME_CLUSTERCODE,
                ListingEntry.COLUMN_NAME_ENUMSTR,
                ListingEntry.COLUMN_NAME_HH01,
                ListingEntry.COLUMN_NAME_HH02,
                ListingEntry.COLUMN_NAME_HH03,
                ListingEntry.COLUMN_NAME_HH04,
                ListingEntry.COLUMN_NAME_HH04OTHER,
                ListingEntry.COLUMN_NAME_HH05,
                ListingEntry.COLUMN_NAME_HH06,
                ListingEntry.COLUMN_NAME_HH07,
                ListingEntry.COLUMN_NAME_HH07n,
                ListingEntry.COLUMN_NAME_HH08,
                ListingEntry.COLUMN_NAME_HH09,
                ListingEntry.COLUMN_NAME_HH08A1,
                ListingEntry.COLUMN_NAME_HH09A1,
                ListingEntry.COLUMN_NAME_HH10,
                ListingEntry.COLUMN_NAME_HH11,
                ListingEntry.COLUMN_NAME_HH12,
                ListingEntry.COLUMN_NAME_HH13,
                ListingEntry.COLUMN_NAME_HH14,
                ListingEntry.COLUMN_NAME_HH15,
                ListingEntry.COLUMN_NAME_HH16,
                ListingEntry.COLUMN_ADDRESS,
                ListingEntry.COLUMN_ISNEWHH,
                ListingEntry.COLUMN_COUNTER,
                ListingEntry.COLUMN_USERNAME,
                ListingEntry.COLUMN_NAME_DEVICEID,
                ListingEntry.COLUMN_TAGID,
                ListingEntry.COLUMN_NAME_GPSLat,
                ListingEntry.COLUMN_NAME_GPSLng,
                ListingEntry.COLUMN_NAME_GPSTime,
                ListingEntry.COLUMN_NAME_GPSAccuracy,
                ListingEntry.COLUMN_NAME_GPSAltitude,
                ListingEntry.COLUMN_APPVER,
                ListingEntry.COLUMN_RANDOMIZED
        };

        String whereClause = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;

        String orderBy =
                ListingEntry.COLUMN_NAME_CLUSTERCODE + " ASC";

        Collection<ListingContract> allLC = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        try {
            c = db.query(
                    ListingEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                ListingContract listing = new ListingContract();
                allLC.add(listing.hydrate(c, 0));
            }
            for (ListingContract fc : allLC) {
                jsonArray.put(fc.toJSONObject());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return jsonArray;
    }

    public Collection<ListingContract> getAllListings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                ListingEntry._ID,
                ListingEntry.COLUMN_NAME_UID,
                ListingEntry.COLUMN_NAME_HHDATETIME,
                ListingEntry.COLUMN_NAME_ENUMCODE,
                ListingEntry.COLUMN_NAME_CLUSTERCODE,
                ListingEntry.COLUMN_NAME_ENUMSTR,
                ListingEntry.COLUMN_NAME_HH01,
                ListingEntry.COLUMN_NAME_HH02,
                ListingEntry.COLUMN_NAME_HH03,
                ListingEntry.COLUMN_NAME_HH04,
                ListingEntry.COLUMN_NAME_HH04OTHER,
                ListingEntry.COLUMN_NAME_HH05,
                ListingEntry.COLUMN_NAME_HH06,
                ListingEntry.COLUMN_NAME_HH07,
                ListingEntry.COLUMN_NAME_HH07n,
                ListingEntry.COLUMN_NAME_HH08,
                ListingEntry.COLUMN_NAME_HH09,
                ListingEntry.COLUMN_NAME_HH08A1,
                ListingEntry.COLUMN_NAME_HH09A1,
                ListingEntry.COLUMN_NAME_HH10,
                ListingEntry.COLUMN_NAME_HH11,
                ListingEntry.COLUMN_NAME_HH12,
                ListingEntry.COLUMN_NAME_HH13,
                ListingEntry.COLUMN_NAME_HH14,
                ListingEntry.COLUMN_NAME_HH15,
                ListingEntry.COLUMN_NAME_HH16,
                ListingEntry.COLUMN_ADDRESS,
                ListingEntry.COLUMN_ISNEWHH,
                ListingEntry.COLUMN_COUNTER,
                ListingEntry.COLUMN_USERNAME,
                ListingEntry.COLUMN_NAME_DEVICEID,
                ListingEntry.COLUMN_TAGID,
                ListingEntry.COLUMN_NAME_GPSLat,
                ListingEntry.COLUMN_NAME_GPSLng,
                ListingEntry.COLUMN_NAME_GPSTime,
                ListingEntry.COLUMN_NAME_GPSAccuracy,
                ListingEntry.COLUMN_NAME_GPSAltitude,
                ListingEntry.COLUMN_APPVER,
                ListingEntry.COLUMN_RANDOMIZED
        };

        String whereClause = ListingEntry.COLUMN_SYNCED + " is null";
        String[] whereArgs = null;

        String groupBy = null;
        String having = null;

        String orderBy = ListingEntry.COLUMN_NAME_CLUSTERCODE + " ASC";

        Collection<ListingContract> allLC = new ArrayList<ListingContract>();
        try {
            c = db.query(
                    ListingEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                ListingContract listing = new ListingContract();
                allLC.add(listing.hydrate(c, 0));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allLC;
    }

    public void syncListingFromDevice(JSONArray fmlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            JSONArray jsonArray = fmlist;
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObjectUser = jsonArray.getJSONObject(i);

                ListingContract fmc = new ListingContract();
                fmc.Sync(jsonObjectUser);

                addForm(fmc);
            }


        } catch (Exception e) {
            Log.d(TAG, "syncListing(e): " + e);
        } finally {
            db.close();
        }
    }

    public Collection<SignupContract> getUnsyncedSignups() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                SignUpTable._ID,
                SignUpTable.FULLNAME,
                SignUpTable.DESIGNATION,
                SignUpTable.USERNAME,
                SignUpTable.PASSWORD,
                SignUpTable.COUNTRY_ID,
                SignUpTable.COLUMN_DEVICEID,
                SignUpTable.COLUMN_FORMDATE,
        };

        String whereClause = SignUpTable.COLUMN_SYNCED + " is null OR " + SignUpTable.COLUMN_SYNCED + " = '' ";
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Collection<SignupContract> allLC = new ArrayList<>();
        try {
            c = db.query(
                    SignUpTable.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                allLC.add(new SignupContract().Hydrate(c));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allLC;
    }

    public Collection<ListingContract> getAllListingsForRandom() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                ListingEntry._ID,
                ListingEntry.COLUMN_NAME_UID,
                ListingEntry.COLUMN_NAME_HHDATETIME,
                ListingEntry.COLUMN_NAME_ENUMCODE,
                ListingEntry.COLUMN_NAME_CLUSTERCODE,
                ListingEntry.COLUMN_NAME_ENUMSTR,
                ListingEntry.COLUMN_NAME_HH01,
                ListingEntry.COLUMN_NAME_HH02,
                ListingEntry.COLUMN_NAME_HH03,
                ListingEntry.COLUMN_NAME_HH04,
                ListingEntry.COLUMN_NAME_HH04OTHER,
                ListingEntry.COLUMN_NAME_HH05,
                ListingEntry.COLUMN_NAME_HH06,
                ListingEntry.COLUMN_NAME_HH07,
                ListingEntry.COLUMN_NAME_HH07n,
                ListingEntry.COLUMN_NAME_HH08,
                ListingEntry.COLUMN_NAME_HH09,
                ListingEntry.COLUMN_NAME_HH08A1,
                ListingEntry.COLUMN_NAME_HH09A1,
                ListingEntry.COLUMN_NAME_HH10,
                ListingEntry.COLUMN_NAME_HH11,
                ListingEntry.COLUMN_NAME_HH12,
                ListingEntry.COLUMN_NAME_HH13,
                ListingEntry.COLUMN_NAME_HH14,
                ListingEntry.COLUMN_NAME_HH15,
                ListingEntry.COLUMN_NAME_HH16,
                ListingEntry.COLUMN_ADDRESS,
                ListingEntry.COLUMN_ISNEWHH,
                ListingEntry.COLUMN_COUNTER,
                ListingEntry.COLUMN_USERNAME,
                ListingEntry.COLUMN_NAME_DEVICEID,
                ListingEntry.COLUMN_TAGID,
                ListingEntry.COLUMN_NAME_GPSLat,
                ListingEntry.COLUMN_NAME_GPSLng,
                ListingEntry.COLUMN_NAME_GPSTime,
                ListingEntry.COLUMN_NAME_GPSAccuracy,
                ListingEntry.COLUMN_NAME_GPSAltitude,
                ListingEntry.COLUMN_APPVER,
                ListingEntry.COLUMN_RANDOMIZED,
                "COUNT(*) as RESCOUNTER, " +
                        "COUNT(case " + ListingEntry.COLUMN_NAME_HH10 + " when '1' then 1 else null end) as CHILDCOUNTER," +
                        "COUNT(case " + ListingEntry.COLUMN_RANDOMIZED + " when '1' then 1 else null end) as RANDCOUNTER," +
                        "COUNT(*) as TOTALHH"
        };

        String whereClause = ListingEntry.COLUMN_NAME_HH08A1 + " =?";
        String[] whereArgs = {"1"};
        String groupBy = ListingEntry.COLUMN_NAME_CLUSTERCODE;
        String having = null;

        String orderBy = ListingEntry.COLUMN_NAME_CLUSTERCODE + " ASC";

        Collection<ListingContract> allLC = new ArrayList<>();
        try {
            c = db.query(
                    true,
                    ListingEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy,                    // The sort order
                    null

            );
            while (c.moveToNext()) {
                ListingContract listing = new ListingContract();
                allLC.add(listing.hydrate(c, 1));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allLC;
    }

    public JSONArray getListingsByCluster(String cluster) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                ListingEntry._ID,
                ListingEntry.COLUMN_NAME_UID,
                ListingEntry.COLUMN_NAME_HHDATETIME,
                ListingEntry.COLUMN_NAME_ENUMCODE,
                ListingEntry.COLUMN_NAME_CLUSTERCODE,
                ListingEntry.COLUMN_NAME_ENUMSTR,
                ListingEntry.COLUMN_NAME_HH01,
                ListingEntry.COLUMN_NAME_HH02,
                ListingEntry.COLUMN_NAME_HH03,
                ListingEntry.COLUMN_NAME_HH04,
                ListingEntry.COLUMN_NAME_HH04OTHER,
                ListingEntry.COLUMN_NAME_HH05,
                ListingEntry.COLUMN_NAME_HH06,
                ListingEntry.COLUMN_NAME_HH07,
                ListingEntry.COLUMN_NAME_HH07n,
                ListingEntry.COLUMN_NAME_HH08,
                ListingEntry.COLUMN_NAME_HH09,
                ListingEntry.COLUMN_NAME_HH08A1,
                ListingEntry.COLUMN_NAME_HH09A1,
                ListingEntry.COLUMN_NAME_HH10,
                ListingEntry.COLUMN_NAME_HH11,
                ListingEntry.COLUMN_NAME_HH12,
                ListingEntry.COLUMN_NAME_HH13,
                ListingEntry.COLUMN_NAME_HH14,
                ListingEntry.COLUMN_NAME_HH15,
                ListingEntry.COLUMN_NAME_HH16,
                ListingEntry.COLUMN_ADDRESS,
                ListingEntry.COLUMN_ISNEWHH,
                ListingEntry.COLUMN_COUNTER,
                ListingEntry.COLUMN_USERNAME,
                ListingEntry.COLUMN_NAME_DEVICEID,
                ListingEntry.COLUMN_TAGID,
                ListingEntry.COLUMN_NAME_GPSLat,
                ListingEntry.COLUMN_NAME_GPSLng,
                ListingEntry.COLUMN_NAME_GPSTime,
                ListingEntry.COLUMN_NAME_GPSAccuracy,
                ListingEntry.COLUMN_NAME_GPSAltitude,
                ListingEntry.COLUMN_APPVER,
                ListingEntry.COLUMN_RANDOMIZED
        };

        String whereClause = ListingEntry.COLUMN_NAME_CLUSTERCODE + " = ?";
        String[] whereArgs = {cluster};
        String groupBy = null;
        String having = null;

        String orderBy =
                ListingEntry.COLUMN_NAME_CLUSTERCODE + " ASC";
        JSONArray jsonArray = new JSONArray();

        Collection<ListingContract> allLC = new ArrayList<ListingContract>();
        try {
            c = db.query(
                    ListingEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                ListingContract listing = new ListingContract();
                allLC.add(listing.hydrate(c, 0));
            }

            for (ListingContract lc : allLC) {
                try {
                    jsonArray.put(lc.toJSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return jsonArray;
    }


    public ArrayList<ListingContract> randomLisiting(String clusterCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                ListingEntry._ID,
                ListingEntry.COLUMN_NAME_UID,
                ListingEntry.COLUMN_NAME_HHDATETIME,
                ListingEntry.COLUMN_NAME_ENUMCODE,
                ListingEntry.COLUMN_NAME_CLUSTERCODE,
                ListingEntry.COLUMN_NAME_ENUMSTR,
                ListingEntry.COLUMN_NAME_HH01,
                ListingEntry.COLUMN_NAME_HH02,
                ListingEntry.COLUMN_NAME_HH03,
                ListingEntry.COLUMN_NAME_HH04,
                ListingEntry.COLUMN_NAME_HH04OTHER,
                ListingEntry.COLUMN_NAME_HH05,
                ListingEntry.COLUMN_NAME_HH06,
                ListingEntry.COLUMN_NAME_HH07,
                ListingEntry.COLUMN_NAME_HH07n,
                ListingEntry.COLUMN_NAME_HH08,
                ListingEntry.COLUMN_NAME_HH09,
                ListingEntry.COLUMN_NAME_HH08A1,
                ListingEntry.COLUMN_NAME_HH09A1,
                ListingEntry.COLUMN_NAME_HH10,
                ListingEntry.COLUMN_NAME_HH11,
                ListingEntry.COLUMN_NAME_HH12,
                ListingEntry.COLUMN_NAME_HH13,
                ListingEntry.COLUMN_NAME_HH14,
                ListingEntry.COLUMN_NAME_HH15,
                ListingEntry.COLUMN_NAME_HH16,
                ListingEntry.COLUMN_ADDRESS,
                ListingEntry.COLUMN_ISNEWHH,
                ListingEntry.COLUMN_COUNTER,
                ListingEntry.COLUMN_USERNAME,
                ListingEntry.COLUMN_NAME_DEVICEID,
                ListingEntry.COLUMN_TAGID,
                ListingEntry.COLUMN_NAME_GPSLat,
                ListingEntry.COLUMN_NAME_GPSLng,
                ListingEntry.COLUMN_NAME_GPSTime,
                ListingEntry.COLUMN_NAME_GPSAccuracy,
                ListingEntry.COLUMN_NAME_GPSAltitude,
                ListingEntry.COLUMN_APPVER,
                ListingEntry.COLUMN_RANDOMIZED
        };

        String whereClause = ListingEntry.COLUMN_NAME_HH08A1 + " =? AND "
                + ListingEntry.COLUMN_NAME_CLUSTERCODE + " =? AND "
                + ListingEntry.COLUMN_RANDOMIZED + " =?";
        String[] whereArgs = {"1", clusterCode, "2"};
        String groupBy = null;
        String having = null;

        String orderBy = null;

        ArrayList<ListingContract> allLC = new ArrayList<>();
        try {
            c = db.query(
                    ListingEntry.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order

            );
            while (c.moveToNext()) {
                ListingContract listing = new ListingContract();
                allLC.add(listing.hydrate(c, 0));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allLC;
    }

    public Collection<TeamsContract> getAllTeams() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                SingleTaluka._ID,
                SingleTaluka.COLUMN_TEAM_NO
        };

        String whereClause = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;

        String orderBy =
                SingleTaluka.COLUMN_TEAM_NO + " ASC";

        Collection<TeamsContract> allDC = new ArrayList<TeamsContract>();
        try {
            c = db.query(
                    SingleTaluka.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                TeamsContract dc = new TeamsContract();
                allDC.add(dc.hydrate(c));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allDC;
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }


    }

    public void syncUsers(JSONArray userlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(UsersContract.UsersTable.TABLE_NAME, null, null);
        try {
            JSONArray jsonArray = userlist;
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObjectUser = jsonArray.getJSONObject(i);

                UsersContract user = new UsersContract();
                user.Sync(jsonObjectUser);
                ContentValues values = new ContentValues();

                values.put(UsersTable.ROW_USERNAME, user.getUserName());
                values.put(UsersTable.ROW_PASSWORD, user.getPassword());
                values.put(UsersTable.COUNTRY_ID, user.getCOUNTRY_ID());
                db.insert(UsersTable.TABLE_NAME, null, values);
            }


        } catch (Exception e) {
            Log.d(TAG, "syncUser(e): " + e);
        } finally {
            db.close();
        }
    }


    public Collection<VerticesContract> getVerticesByCluster(String cluster_code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                SingleVertices._ID,
                SingleVertices.COLUMN_CLUSTER_CODE,
                SingleVertices.COLUMN_POLY_LAT,
                SingleVertices.COLUMN_POLY_LANG,
                SingleVertices.COLUMN_POLY_SEQ
        };

        String whereClause = SingleVertices.COLUMN_CLUSTER_CODE + " = ?";
        String[] whereArgs = {cluster_code};
        String groupBy = null;
        String having = null;

        String orderBy =
                SingleVertices.COLUMN_POLY_SEQ + " ASC";

        Collection<VerticesContract> allVC = new ArrayList<>();
        try {
            c = db.query(
                    SingleVertices.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                VerticesContract vc = new VerticesContract();
                allVC.add(vc.hydrate(c));
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allVC;
    }

    public void updateSyncedForms(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(ListingEntry.COLUMN_SYNCED, true);
        values.put(ListingEntry.COLUMN_SYNCED_DATE, new Date().toString());

// Which row to update, based on the title
        String where = ListingEntry._ID + " = ?";
        String[] whereArgs = {id};

        int count = db.update(
                ListingEntry.TABLE_NAME,
                values,
                where,
                whereArgs);
    }

    public void updateSyncedSignup(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

// New value for one column
        ContentValues values = new ContentValues();
        values.put(SignUpTable.COLUMN_SYNCED, true);
        values.put(SignUpTable.COLUMN_SYNCED_DATE, new Date().toString());

// Which row to update, based on the title
        String where = SignUpTable._ID + " = ?";
        String[] whereArgs = {id};

        int count = db.update(
                SignUpTable.TABLE_NAME,
                values,
                where,
                whereArgs);
    }

    public void updateSyncedBLRandom(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

    }

    public void syncTeams(JSONArray dcList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SingleTaluka.TABLE_NAME, null, null);

        try {
            JSONArray jsonArray = dcList;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectDistrict = jsonArray.getJSONObject(i);

                TeamsContract dc = new TeamsContract();
                dc.sync(jsonObjectDistrict);

                ContentValues values = new ContentValues();

                values.put(SingleTaluka.COLUMN_TEAM_NO, dc.getTeamNo());

                db.insert(SingleTaluka.TABLE_NAME, null, values);
            }
            db.close();

        } catch (Exception e) {

        }
    }

    public EnumBlockContract getEnumBlock(String cluster) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                EnumBlockTable._ID,
                EnumBlockTable.COLUMN_COUNTRY_ID,
                EnumBlockTable.COLUMN_GEO_AREA,
                EnumBlockTable.COLUMN_CLUSTER_AREA
        };

        String whereClause = EnumBlockTable.COLUMN_CLUSTER_AREA + " =?";
        String[] whereArgs = new String[]{cluster};
        String groupBy = null;
        String having = null;

        String orderBy =
                EnumBlockTable._ID + " ASC";

        EnumBlockContract allEB = null;
        try {
            c = db.query(
                    EnumBlockTable.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                allEB = new EnumBlockContract().HydrateEnum(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allEB;
    }

    public void syncVertices(JSONArray vcList) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SingleVertices.TABLE_NAME, null, null);

        try {
            JSONArray jsonArray = vcList;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectVR = jsonArray.getJSONObject(i);

                VerticesContract vc = new VerticesContract();
                vc.sync(jsonObjectVR);

                ContentValues values = new ContentValues();

                values.put(SingleVertices.COLUMN_CLUSTER_CODE, vc.getCluster_code());
                values.put(SingleVertices.COLUMN_POLY_LAT, vc.getPoly_lat());
                values.put(SingleVertices.COLUMN_POLY_LANG, vc.getPoly_lng());
                values.put(SingleVertices.COLUMN_POLY_SEQ, vc.getPoly_seq());

                db.insert(SingleVertices.TABLE_NAME, null, values);
            }
            db.close();

        } catch (Exception e) {

        }
    }

    public void syncVersionApp(JSONArray Versionlist) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(VersionAppTable.TABLE_NAME, null, null);
        try {
            JSONArray jsonArray = Versionlist;
            JSONObject jsonObjectCC = jsonArray.getJSONObject(0);

            VersionAppContract Vc = new VersionAppContract();
            Vc.Sync(jsonObjectCC);

            ContentValues values = new ContentValues();

            values.put(VersionAppTable.COLUMN_PATH_NAME, Vc.getPathname());
            values.put(VersionAppTable.COLUMN_VERSION_CODE, Vc.getVersioncode());
            values.put(VersionAppTable.COLUMN_VERSION_NAME, Vc.getVersionname());

            db.insert(VersionAppTable.TABLE_NAME, null, values);
        } catch (Exception e) {
        } finally {
            db.close();
        }
    }

    public VersionAppContract getVersionApp() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        String[] columns = {
                VersionAppTable._ID,
                VersionAppTable.COLUMN_VERSION_CODE,
                VersionAppTable.COLUMN_VERSION_NAME,
                VersionAppTable.COLUMN_PATH_NAME
        };

        String whereClause = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;

        String orderBy = VersionAppTable._ID + " ASC";

        VersionAppContract allVC = new VersionAppContract();
        try {
            c = db.query(
                    VersionAppTable.TABLE_NAME,  // The table to query
                    columns,                   // The columns to return
                    whereClause,               // The columns for the WHERE clause
                    whereArgs,                 // The values for the WHERE clause
                    groupBy,                   // don't group the rows
                    having,                    // don't filter by row groups
                    orderBy                    // The sort order
            );
            while (c.moveToNext()) {
                allVC.hydrate(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return allVC;
    }
}