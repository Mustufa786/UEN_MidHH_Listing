package edu.aku.hassannaqvi.casi_hhlisting.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.ListingContract;
import edu.aku.hassannaqvi.casi_hhlisting.Core.DatabaseHelper;
import edu.aku.hassannaqvi.casi_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.casi_hhlisting.R;

public class SetupActivity extends Activity {

    private static String deviceId;
    @BindView(R.id.activity_household_listing)
    ScrollView activityHouseholdListing;
    @BindView(R.id.hh02)
    EditText hh02;
    @BindView(R.id.hhadd)
    EditText hhadd;
    @BindView(R.id.hh03)
    TextView hh03;
    @BindView(R.id.hh04)
    RadioGroup hh04;
    @BindView(R.id.hh04a)
    RadioButton hh04a;
    @BindView(R.id.hh04b)
    RadioButton hh04b;
    @BindView(R.id.hh04c)
    RadioButton hh04c;
    @BindView(R.id.hh04d)
    RadioButton hh04d;
    @BindView(R.id.hh04e)
    RadioButton hh04e;
    @BindView(R.id.hh04f)
    RadioButton hh04f;
    @BindView(R.id.hh04fb)
    RadioButton hh04fb;
    @BindView(R.id.hh04g)
    RadioButton hh04g;
    @BindView(R.id.hh04h)
    RadioButton hh04h;
    @BindView(R.id.hh04i)
    RadioButton hh04i;
    @BindView(R.id.hh04j)
    RadioButton hh04j;
    @BindView(R.id.hh04jx)
    EditText hh04jx;
    @BindView(R.id.hh05)
    Switch hh05;
    @BindView(R.id.hh06)
    EditText hh06;
    @BindView(R.id.hh07)
    TextView hh07;
    @BindView(R.id.fldGrpHH04)
    LinearLayout fldGrpHH04;
    @BindView(R.id.btnAddHousehold)
    Button btnAddHousehold;
    @BindView(R.id.btnChangePSU)
    Button btnChangPSU;
    @BindView(R.id.hh09a1)
    CheckBox hh09a1;
    String dtToday = new SimpleDateFormat("dd-MM-yy HH:mm:ss").format(new Date().getTime());

    private String TAG = "Setup Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);

        this.setTitle("Structure Information");

        if (MainApp.userEmail == null) {
            Toast.makeText(this, "USER ERROR1[" + MainApp.userEmail + "]: Please Login Again!", Toast.LENGTH_LONG).show();
            Intent retreat = new Intent(this, LoginActivity.class);
            startActivity(retreat);
        }
        if (MainApp.userEmail.length() < 7) {
            Toast.makeText(this, "USER ERROR2" + MainApp.userEmail + ": Please Login Again!", Toast.LENGTH_LONG).show();
            Intent retreat = new Intent(this, LoginActivity.class);
            startActivity(retreat);
        }


        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        hh02.setText(MainApp.clusterCode);
        hh02.setEnabled(false);

        if (MainApp.hh02txt == null) {
            MainApp.hh03txt = 1;
        } else {
            MainApp.hh03txt++;
            //MainApp.lc.setHh03(String.valueOf(MainApp.hh03txt));
            //hh02.setText(MainApp.hh02txt.toString());
            hh02.setText(MainApp.clusterCode);
            hh02.setEnabled(false);
        }

        MainApp.hh07txt = "1";

        //String StructureNumber = "T-" + hh02.getText() + "-" + String.format("%03d", MainApp.hh03txt);
        String StructureNumber = "CASI-" + MainApp.clusterCode + "-S" + String.format("%04d", MainApp.hh03txt);

        // removed status for REFUSED and LOCKED
        hh04i.setVisibility(View.GONE);
        hh04fb.setVisibility(View.GONE);

        hh03.setTextColor(Color.RED);
        hh03.setText(StructureNumber);
        hh07.setText(getString(R.string.hh07) + ": " + MainApp.hh07txt);


        hh04.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (hh04a.isChecked()) {
                    //Moved to Add next Family button: MainApp.hh07txt = String.valueOf((char) MainApp.hh07txt.charAt(0) + 1);
                    MainApp.hh07txt = "1";


                } else {
                    MainApp.hh07txt = "";
                }

                if (hh04a.isChecked() || hh04g.isChecked() || hh04h.isChecked() || hh04fb.isChecked() || hh04i.isChecked()) {
                    hh09a1.setVisibility(View.GONE);
                    hh09a1.setChecked(false);

                } else {
                    hh09a1.setVisibility(View.VISIBLE);
                    hh09a1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (hh09a1.isChecked()) {
                                fldGrpHH04.setVisibility(View.VISIBLE);
                                btnAddHousehold.setVisibility(View.GONE);
                                MainApp.hh07txt = "1";
                                hh07.setText(getString(R.string.hh07) + ": " + MainApp.hh07txt);

                            } else {
                                fldGrpHH04.setVisibility(View.GONE);
                                hh05.setChecked(false);
                                hh06.setText(null);
                                MainApp.hh07txt = "";
                                btnAddHousehold.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }


                hh07.setText(getString(R.string.hh07) + ": " + MainApp.hh07txt);
                if (hh04a.isChecked()) {
                    fldGrpHH04.setVisibility(View.VISIBLE);
                    btnAddHousehold.setVisibility(View.GONE);
                } else {
                    fldGrpHH04.setVisibility(View.GONE);
                    hh05.setChecked(false);
                    hh06.setText(null);
                    btnAddHousehold.setVisibility(View.VISIBLE);
                }
                if (hh04g.isChecked() || hh04h.isChecked()) {
                    btnAddHousehold.setVisibility(View.GONE);
                    btnChangPSU.setVisibility(View.VISIBLE);
                    if (hh04g.isChecked()) {
                        btnChangPSU.setText("Logout");
                    } else {
                        btnChangPSU.setText("Change Enumeration Block");
                    }
                } else {
                    btnChangPSU.setVisibility(View.GONE);

                }

                if (hh04j.isChecked()) {
                    hh04jx.setVisibility(View.VISIBLE);
                } else {
                    hh04jx.setVisibility(View.GONE);
                    hh04jx.setText(null);
                }
            }
        });
        hh05.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainApp.hh07txt = "1";
                    hh07.setText(getString(R.string.hh07) + ": " + MainApp.hh07txt);
                    hh06.setVisibility(View.VISIBLE);
                    hh06.requestFocus();

                } else {
                    MainApp.hh07txt = "1";
                    hh07.setText(getString(R.string.hh07) + ": " + MainApp.hh07txt);
                    hh06.setVisibility(View.GONE);
                    hh06.setText(null);
                }
            }
        });


    }

    @OnClick(R.id.btnAddChild)
    void onBtnAddChildClick() {

        if (MainApp.hh02txt == null) {
            MainApp.hh02txt = hh02.getText().toString();
        }
        if (formValidation()) {
            SaveDraft();
            MainApp.fCount++;
            finish();
            Intent fA = new Intent(this, FamilyListingActivity.class);
            startActivity(fA);
        }

    }

    @OnClick(R.id.btnChangePSU)
    void onBtnChangePSUClick() {

        finish();

        Intent fA;
        if (hh04g.isChecked()) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            SaveDraft();

            if (UpdateDB()) {
                MainApp.hh02txt = null;

                fA = new Intent(this, MainActivity.class);
                startActivity(fA);
            }
        }

    }

    private void SaveDraft() {

        MainApp.lc = new ListingContract();
        SharedPreferences sharedPref = getSharedPreferences("tagName", MODE_PRIVATE);
        MainApp.lc.setTagId(sharedPref.getString("tagName", null));
        MainApp.lc.setAppVer(MainApp.versionName + "." + MainApp.versionCode);
        MainApp.lc.setHhDT(dtToday);

        MainApp.lc.setEnumCode(MainApp.enumCode);
        MainApp.lc.setClusterCode(MainApp.clusterCode);
        MainApp.lc.setEnumStr(MainApp.enumStr);

        MainApp.lc.setHh01(String.valueOf(MainApp.hh01txt));
        MainApp.lc.setHh02(MainApp.hh02txt);
        MainApp.lc.setHh03(String.valueOf(MainApp.hh03txt));

        switch (hh04.getCheckedRadioButtonId()) {
            case R.id.hh04a:
                MainApp.lc.setHh04("1");
                break;
            case R.id.hh04b:
                MainApp.lc.setHh04("2");
                break;
            case R.id.hh04c:
                MainApp.lc.setHh04("3");
                break;
            case R.id.hh04d:
                MainApp.lc.setHh04("4");
                break;
            case R.id.hh04e:
                MainApp.lc.setHh04("5");
                break;
            case R.id.hh04f:
                MainApp.lc.setHh04("6");
                break;
            case R.id.hh04fb:
                MainApp.lc.setHh04("7");
                break;
            case R.id.hh04g:
                MainApp.lc.setHh04("8");
                break;
            case R.id.hh04h:
                MainApp.lc.setHh04("9");
                break;
            case R.id.hh04i:
                MainApp.lc.setHh04("10");
                break;
            case R.id.hh04j:
                MainApp.lc.setHh04("88");
                break;
            default:
                break;
        }
        MainApp.lc.setHh04other(hh04jx.getText().toString());
        MainApp.lc.setUsername(MainApp.userEmail);
        MainApp.lc.setHh05(hh05.isChecked() ? "1" : "2");
        MainApp.lc.setHh06(hh06.getText().toString());
        MainApp.lc.setHh07(MainApp.hh07txt);
        MainApp.lc.setHh09a1(hh04a.isChecked() || hh09a1.isChecked() ? "1" : "2");

        MainApp.lc.setDeviceID(deviceId);

        MainApp.lc.setIsRandom("2");

        setGPS();

        MainApp.fTotal = hh06.getText().toString().isEmpty() ? 0 : Integer.parseInt(hh06.getText().toString());


        Log.d(TAG, "SaveDraft: " + MainApp.lc.getHh03());

    }

    public void setGPS() {
        SharedPreferences GPSPref = getSharedPreferences("GPSCoordinates", Context.MODE_PRIVATE);

//        String date = DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(GPSPref.getString("Time", "0"))).toString();

        try {
            String lat = GPSPref.getString("Latitude", "0");
            String lang = GPSPref.getString("Longitude", "0");
            String acc = GPSPref.getString("Accuracy", "0");
            String dt = GPSPref.getString("Time", "0");

            if (lat == "0" && lang == "0") {
                Toast.makeText(this, "Could not obtained GPS points", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS set", Toast.LENGTH_SHORT).show();
            }

            String date = DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(GPSPref.getString("Time", "0"))).toString();

            MainApp.lc.setGPSLat(GPSPref.getString("Latitude", "0"));
            MainApp.lc.setGPSLng(GPSPref.getString("Longitude", "0"));
            MainApp.lc.setGPSAcc(GPSPref.getString("Accuracy", "0"));
            MainApp.lc.setGPSAlt(GPSPref.getString("Altitude", "0"));
//            MainApp.fc.setGpsTime(GPSPref.getString(date, "0")); // Timestamp is converted to date above
            MainApp.lc.setGPSTime(date); // Timestamp is converted to date above

            Toast.makeText(this, "GPS set", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "setGPS: " + e.getMessage());
        }

    }

    private boolean formValidation() {
        if (MainApp.hh02txt == null) {
            Toast.makeText(this, "Please enter PSU", Toast.LENGTH_LONG).show();
            hh02.setError("Please enter PSU");
            Log.i(TAG, "PSU not given");
            return false;
        } else {
            hh02.setError(null);
        }
        if (hh04.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please one option", Toast.LENGTH_LONG).show();
            hh04j.setError("Please one option");
            Log.i(TAG, "Please one option");
            return false;
        } else {
            hh04j.setError(null);
        }

        if (hh04a.isChecked()) {

            if (hh05.isChecked() && hh06.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter number", Toast.LENGTH_LONG).show();
                hh06.setError("Please enter number");
                Log.i(TAG, "Please enter number");
                return false;
            } else {
                hh06.setError(null);
            }

            if (!hh06.getText().toString().isEmpty() && Integer.valueOf(hh06.getText().toString()) <= 1) {
                Toast.makeText(this, "Greater then 1!", Toast.LENGTH_LONG).show();
                hh06.setError("Greater then 1!");
                Log.i(TAG, "hh06:Greater then 1!");
                return false;
            } else {
                hh06.setError(null);
            }
        }

        if (hh04j.isChecked() && hh04jx.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "ERROR(empty): Data required", Toast.LENGTH_LONG).show();
            hh04jx.setError("ERROR(empty): Data required");
            Log.i(TAG, "ERROR(empty): Data required");
            return false;
        } else {
            hh04jx.setError(null);
        }


        return true;
    }

    @OnClick(R.id.btnAddHousehold)
    void onBtnAddHouseholdClick() {
        if (MainApp.hh02txt == null) {
            MainApp.hh02txt = hh02.getText().toString();
        }
        if (formValidation()) {

            SaveDraft();
            if (UpdateDB()) {
                MainApp.fCount = 0;
                MainApp.fTotal = 0;
                MainApp.cCount = 0;
                MainApp.cTotal = 0;
                finish();
                Intent fA = new Intent(this, SetupActivity.class);
                startActivity(fA);

            }
        }
    }

    private boolean UpdateDB() {
        DatabaseHelper db = new DatabaseHelper(this);
        Log.d(TAG, "UpdateDB: Structure" + MainApp.lc.getHh03());

        long updcount = db.addForm(MainApp.lc);

        MainApp.lc.setID(String.valueOf(updcount));

        if (updcount != 0) {


            MainApp.lc.setUID(
                    (MainApp.lc.getDeviceID() + MainApp.lc.getID()));

            db.updateListingUID();

        } else {
            Toast.makeText(this, "Updating Database... ERROR!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Back Button NOT Allowed!", Toast.LENGTH_SHORT).show();

    }
}


