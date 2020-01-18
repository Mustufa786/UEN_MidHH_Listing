package edu.aku.hassannaqvi.uenmd_hhlisting.Contracts;


import android.database.Cursor;
import android.provider.BaseColumns;

import org.json.JSONException;
import org.json.JSONObject;

public class BLRandomContract {

    private static final String TAG = "BLRandom_CONTRACT";

    private String _ID;
    private String LUID;
    private String subVillageCode; // hh02
    private String structure;  // Structure
    private String extension; // Extension
    private String hh;
    private String hhhead;
    private String randomDT;
    private String contact;
    private String selStructure;

    public BLRandomContract() {
    }

    public BLRandomContract(BLRandomContract rnd) {
        this._ID = rnd.get_ID();
        this.LUID = rnd.getLUID();
        this.subVillageCode = rnd.getSubVillageCode();
        this.structure = rnd.getStructure();
        this.extension = rnd.getExtension();
        this.hh = rnd.getHh();
        this.hhhead = rnd.getHhhead();
        this.randomDT = rnd.getRandomDT();
        this.contact = rnd.getContact();
        this.selStructure = rnd.getSelStructure();
    }

    public BLRandomContract Sync(JSONObject jsonObject) throws JSONException {
        this._ID = jsonObject.getString(singleRandomHH.COLUMN_ID);
        this.LUID = jsonObject.getString(singleRandomHH.COLUMN_LUID);
        this.subVillageCode = jsonObject.getString(singleRandomHH.COLUMN_CLUSTER_BLOCK_CODE);
        this.structure = jsonObject.getString(singleRandomHH.COLUMN_STRUCTURE_NO);

        this.structure = String.format("%04d", Integer.valueOf(this.structure));

        this.extension = jsonObject.getString(singleRandomHH.COLUMN_FAMILY_EXT_CODE);
        this.hh = jsonObject.getString(singleRandomHH.COLUMN_STRUCTURE_NO)
                + "-" + jsonObject.getString(singleRandomHH.COLUMN_FAMILY_EXT_CODE);
        this.randomDT = jsonObject.getString(singleRandomHH.COLUMN_RANDOMDT);
        this.hhhead = jsonObject.getString(singleRandomHH.COLUMN_HH_HEAD);
        this.contact = jsonObject.getString(singleRandomHH.COLUMN_CONTACT);
        this.selStructure = jsonObject.getString(singleRandomHH.COLUMN_HH_SELECTED_STRUCT);

        return this;
    }

    public BLRandomContract Hydrate(Cursor cursor) {
        this._ID = cursor.getString(cursor.getColumnIndex(singleRandomHH.COLUMN_ID));
        this.LUID = cursor.getString(cursor.getColumnIndex(singleRandomHH.COLUMN_LUID));
        this.subVillageCode = cursor.getString(cursor.getColumnIndex(singleRandomHH.COLUMN_CLUSTER_BLOCK_CODE));
        this.structure = cursor.getString(cursor.getColumnIndex(singleRandomHH.COLUMN_STRUCTURE_NO));
        this.extension = cursor.getString(cursor.getColumnIndex(singleRandomHH.COLUMN_FAMILY_EXT_CODE));
        this.hh = cursor.getString(cursor.getColumnIndex(singleRandomHH.COLUMN_HH));
        this.randomDT = cursor.getString(cursor.getColumnIndex(singleRandomHH.COLUMN_RANDOMDT));
        this.hhhead = cursor.getString(cursor.getColumnIndex(singleRandomHH.COLUMN_HH_HEAD));
        this.contact = cursor.getString(cursor.getColumnIndex(singleRandomHH.COLUMN_CONTACT));
        this.selStructure = cursor.getString(cursor.getColumnIndex(singleRandomHH.COLUMN_HH_SELECTED_STRUCT));

        return this;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getLUID() {
        return LUID;
    }

    public void setLUID(String LUID) {
        this.LUID = LUID;
    }

    public String getSubVillageCode() {
        return subVillageCode;
    }

    public void setSubVillageCode(String subVillageCode) {
        this.subVillageCode = subVillageCode;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getHh() {
        return structure + "-" + "" + extension;
    }

    public void setHh(String hh) {
        this.hh = hh;
    }

    public String getRandomDT() {
        return randomDT;
    }

    public void setRandomDT(String randomDT) {
        this.randomDT = randomDT;
    }

    public String getHhhead() {
        return hhhead;
    }

    public void setHhhead(String hhhead) {
        this.hhhead = hhhead;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSelStructure() {
        return selStructure;
    }

    public void setSelStructure(String selStructure) {
        this.selStructure = selStructure;
    }

    public static abstract class singleRandomHH implements BaseColumns {

        public static final String TABLE_NAME = "BLRandom";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_RANDOMDT = "randDT";
        public static final String COLUMN_LUID = "UID";
        public static final String COLUMN_CLUSTER_BLOCK_CODE = "hh02";
        public static final String COLUMN_STRUCTURE_NO = "hh03";
        public static final String COLUMN_FAMILY_EXT_CODE = "hh07";
        public static final String COLUMN_HH = "hh";
        public static final String COLUMN_HH_HEAD = "hh08";
        public static final String COLUMN_CONTACT = "hh09";
        public static final String COLUMN_HH_SELECTED_STRUCT = "hhss";
        public static String _URL = "bl_random.php";
    }

}