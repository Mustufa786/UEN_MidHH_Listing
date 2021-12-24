package edu.aku.hassannaqvi.uenmd_hhlisting.activities.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.SignupContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.DatabaseHelper;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.uenmd_hhlisting.R;
import edu.aku.hassannaqvi.uenmd_hhlisting.activities.home.LoginActivity;
import edu.aku.hassannaqvi.uenmd_hhlisting.databinding.ActivitySignupBinding;
import edu.aku.hassannaqvi.uenmd_hhlisting.validation.ValidatorClass;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding bi;

    String[] countries = new String[]{

            "- Select Country - ", "Afghanistan", "Pakistan", "Tajikistan"
    };

    HashMap<String, String> coutryMap = new HashMap<String, String>() {{

        put("Afghanistan", "1");
        put("Pakistan", "2");
        put("Tajikistan", "3");

    }};

    String countryId = "";
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bi = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        bi.setCallback(this);

        setUIContent();
    }

    private void setUIContent() {

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, countries);
        bi.countries.setAdapter(arrayAdapter);
        bi.countries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    countryId = coutryMap.get(bi.countries.getSelectedItem().toString());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        HashMap<String, String> tagVal = MainApp.getTagValues(this);
        String countryID = tagVal.get("org") != null ? tagVal.get("org").equals("null") ? "" : tagVal.get("org") : "";
        if (countryID.equals("") || countryID.equals("0")) {
            bi.countries.setEnabled(true);
            bi.countries.setSelection(0);
        } else {
            bi.countries.setSelection(Integer.valueOf(countryID));
            bi.countries.setEnabled(false);
        }

    }

    public void BtnContinue() {

        if (formValidation()) {
            try {
                SaveDraft();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (UpdateDB()) {
                Toast.makeText(this, "New Account Registered!!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                Toast.makeText(this, "Failed to Update Database!", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private boolean formValidation() {
        if (!ValidatorClass.EmptyCheckingContainer(this, bi.signUpSection))
            return false;

        if (bi.userName.getText().toString().length() < 8) {
            bi.userName.setError("Username length requires min 8 characters!");
            return false;
        }

        if (bi.password.getText().toString().length() < 8) {
            bi.password.setError("Password length requires min 8 alphanumeric characters!");
            return false;
        }

        if (!bi.password.getText().toString().equals(bi.cPassword.getText().toString())) {
            bi.password.setError("Password not match!");
            return false;
        }

        return true;
    }

    private void SaveDraft() throws JSONException {

        MainApp.signContract = new SignupContract();
        MainApp.signContract.setFullName(bi.fullName.getText().toString());
        MainApp.signContract.setUserName(bi.userName.getText().toString());
        MainApp.signContract.setDesignation(bi.designation.getText().toString());
        MainApp.signContract.setPassword(bi.password.getText().toString());
        MainApp.signContract.setCountryId(countryId);
        MainApp.signContract.setDeviceID(Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
        MainApp.signContract.setFormDate(new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date().getTime()));

    }

    private boolean UpdateDB() {

        db = MainApp.db;

        long rowID = db.addSignUpForm(MainApp.signContract);

        if (rowID != 0) {
            return true;
        } else {
            Toast.makeText(this, "Error in updating DB", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
}
