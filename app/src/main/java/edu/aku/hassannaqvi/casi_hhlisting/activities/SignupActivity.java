package edu.aku.hassannaqvi.casi_hhlisting.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONException;

import java.util.HashMap;

import edu.aku.hassannaqvi.casi_hhlisting.Contracts.SignupContract;
import edu.aku.hassannaqvi.casi_hhlisting.Core.DataBaseHelper;
import edu.aku.hassannaqvi.casi_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.casi_hhlisting.R;
import edu.aku.hassannaqvi.casi_hhlisting.databinding.ActivitySignupBinding;
import edu.aku.hassannaqvi.casi_hhlisting.validation.ValidatorClass;

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
    }

    public void BtnContinue() {

        if (formValidation()) {
            try {
                SaveDraft();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (UpdateDB()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();

            } else {
                Toast.makeText(this, "Failed to Update Database!", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private boolean formValidation() {
        if (!ValidatorClass.EmptyCheckingContainer(this, bi.signUpSection))
            return false;

        if (bi.password.getText().toString().length() < 8) {
            bi.password.setError("Password length requires 8 alphanumeric characters!");
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


    }

    private boolean UpdateDB() {

        DataBaseHelper db = new DataBaseHelper(this);

        long rowID = db.addSignUpForm(MainApp.signContract);

        if (rowID != 0) {
            return true;
        } else {
            Toast.makeText(this, "Error in updating DB", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
}
