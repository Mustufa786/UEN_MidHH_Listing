package edu.aku.hassannaqvi.casi_hhlisting.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.aku.hassannaqvi.casi_hhlisting.Core.DataBaseHelper;
import edu.aku.hassannaqvi.casi_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.casi_hhlisting.R;

public class AddChildActivity extends Activity {

    public static String TAG = "ChildListingActivity";


    @BindView(R.id.activity_add_child)
    LinearLayout activityAddChild;
    @BindView(R.id.txtChildListing)
    TextView txtChildListing;
    @BindView(R.id.icName)
    EditText icName;
    @BindView(R.id.icAgeM)
    EditText icAgeM;
    @BindView(R.id.icAgeD)
    EditText icAgeD;
    @BindView(R.id.btnAddChild)
    Button btnAddChild;
    @BindView(R.id.btnAddFamily)
    Button btnAddFamilty;
    @BindView(R.id.btnAddHousehold)
    Button btnAddHousehold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);
        ButterKnife.bind(this);
        txtChildListing.setText("Child Listing: " + MainApp.hh03txt + "-" + MainApp.hh07txt + " (" + MainApp.cCount + " of " + MainApp.cTotal + ")");
        if (MainApp.cCount < MainApp.cTotal) {
            btnAddChild.setVisibility(View.VISIBLE);
            btnAddHousehold.setVisibility(View.GONE);
            btnAddFamilty.setVisibility(View.GONE);
        } else if (MainApp.cCount >= MainApp.cTotal && MainApp.fCount < MainApp.fTotal) {
            btnAddFamilty.setVisibility(View.VISIBLE);
            btnAddHousehold.setVisibility(View.GONE);
            btnAddChild.setVisibility(View.GONE);
        } else {
            btnAddFamilty.setVisibility(View.GONE);
            btnAddHousehold.setVisibility(View.VISIBLE);
            btnAddChild.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.btnAddChild)
    void onBtnAddChildClick() {
        if (formValidation()) {

            SaveDraft();
            if (UpdateDB()) {
                MainApp.cCount++;
                Intent fA = new Intent(this, AddChildActivity.class);
                startActivity(fA);
            }

        }
    }

    private boolean UpdateDB() {
        DataBaseHelper db = new DataBaseHelper(this);


        long updcount = db.addForm(MainApp.lc);

        MainApp.lc.setID(String.valueOf(updcount));

        if (updcount != 0) {

            MainApp.lc.setHhadd(null);
            MainApp.lc.setHh13(null);
            MainApp.lc.setHh12(null);

            MainApp.lc.setUID(
                    (MainApp.lc.getDeviceID() + MainApp.lc.getID()));

            db.updateListingUID();
        } else {
            Toast.makeText(this, "Updating Database... ERROR!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void SaveDraft() {
        MainApp.lc.setHhadd(icName.getText().toString());
        MainApp.lc.setHh13(icAgeD.getText().toString());
        MainApp.lc.setHh12(icAgeM.getText().toString());

        Log.d(TAG, "SaveDraft: Structure " + MainApp.lc.getHh03());
    }

    private boolean formValidation() {

        if (icName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show();
            icName.setError("Please enter name");
            Log.i(TAG, "Please enter name");
            return false;
        } else {
            icName.setError(null);
        }

        if (icAgeM.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Age Months", Toast.LENGTH_LONG).show();
            icAgeM.setError("Please enter age");
            Log.i(TAG, "Please enter age");
            return false;
        } else if (Integer.valueOf(icAgeM.getText().toString()) > 11) {
            Toast.makeText(this, "Invalid Age Months", Toast.LENGTH_LONG).show();
            icAgeM.setError("Invalid enter age");
            Log.i(TAG, "Please enter age");
            return false;
        } else {
            icAgeM.setError(null);
        }
        if (icAgeD.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter Age Days", Toast.LENGTH_LONG).show();
            icAgeD.setError("Please enter age");
            Log.i(TAG, "Please enter age");
            return false;
        } else if (Integer.valueOf(icAgeD.getText().toString()) > 29) {
            Toast.makeText(this, "Invalid Age Days", Toast.LENGTH_LONG).show();
            icAgeD.setError("Invalid enter age");
            Log.i(TAG, "Please enter age");
            return false;
        } else {
            icAgeD.setError(null);
        }
        if (Integer.valueOf(icAgeD.getText().toString()) == 0 && Integer.valueOf(icAgeM.getText().toString()) == 0) {
            Toast.makeText(this, "Invalid Age ", Toast.LENGTH_LONG).show();
            icAgeD.setError("Invalid age");
            icAgeM.setError("Invalid age");
            Log.i(TAG, "Please enter age");
            return false;
        } else {
            icAgeD.setError(null);
            icAgeM.setError(null);
        }
        return true;
    }


    @OnClick(R.id.btnAddFamily)
    void onBtnAddFamilyClick() {
        if (formValidation()) {

            SaveDraft();
            if (UpdateDB()) {
                MainApp.cCount = 0;
                MainApp.cTotal = 0;
                MainApp.hh07txt = String.valueOf((char) (MainApp.hh07txt.charAt(0) + 1));
                MainApp.lc.setHh07(MainApp.hh07txt);
                MainApp.fCount++;

                Intent fA = new Intent(this, FamilyListingActivity.class);
                startActivity(fA);
                try {
                    Log.d(TAG, "onBtnAddFamilyClick: " + MainApp.lc.toJSONObject().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @OnClick(R.id.btnAddHousehold)
    void onBtnAddHouseholdClick() {
        if (formValidation()) {

            SaveDraft();
            if (UpdateDB()) {
                MainApp.fCount = 0;
                MainApp.fTotal = 0;
                MainApp.cCount = 0;
                MainApp.cTotal = 0;
                Intent fA = new Intent(this, SetupActivity.class);
                startActivity(fA);

            }
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Back Button NOT Allowed!", Toast.LENGTH_SHORT).show();

    }
}
