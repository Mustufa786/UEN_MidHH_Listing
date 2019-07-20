package edu.aku.hassannaqvi.casi_hhlisting.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.aku.hassannaqvi.casi_hhlisting.Core.DataBaseHelper;
import edu.aku.hassannaqvi.casi_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.casi_hhlisting.R;
import edu.aku.hassannaqvi.casi_hhlisting.validation.ClearClass;
import edu.aku.hassannaqvi.casi_hhlisting.validation.ValidatorClass;

public class FamilyListingActivity extends Activity {

    public static String TAG = "FamilyListingActivity";
    static Boolean familyFlag = false;
    @BindView(R.id.txtTeamNoWithFam)
    TextView txtTeamNoWithFam;
    @BindView(R.id.hh08)
    EditText hh08;
    @BindView(R.id.hh09)
    EditText hh09;
    @BindView(R.id.hh10)
    RadioGroup hh10;
    @BindView(R.id.hh10a)
    RadioButton hh10a;
    @BindView(R.id.hh10b)
    RadioButton hh10b;
    @BindView(R.id.hh11)
    EditText hh11;
    @BindView(R.id.hh14)
    RadioGroup hh14;
    /*    @BindView(R.id.hh14a)
        RadioButton hh14a;
        @BindView(R.id.hh14b)
        RadioButton hh14b;
        @BindView(R.id.hh15)
        EditText hh15;*/
    @BindView(R.id.hh16)
    EditText hh16;
    @BindView(R.id.hh17)
    Switch hh17;
    @BindView(R.id.hh08a1)
    RadioGroup hh08a1;
    @BindView(R.id.hh08a1a)
    RadioButton hh08a1a;
    @BindView(R.id.hh08a1b)
    RadioButton hh08a1b;
    @BindView(R.id.hh08a1c)
    RadioButton hh08a1c;
    @BindView(R.id.deleteHH)
    CheckBox deleteHH;
    @BindView(R.id.fldGrpSecB01)
    LinearLayout fldGrpSecB01;
    @BindView(R.id.btnAddNewHousehold)
    Button btnAddNewHousehold;
    @BindView(R.id.btnAddFamily)
    Button btnAddFamilty;
    @BindView(R.id.btnAddHousehold)
    Button btnAddHousehold;
    @BindViews({R.id.hh10, R.id.hh14})
    List<RadioGroup> hh10_12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_listing);
        ButterKnife.bind(this);

        this.setTitle("Family Information");
        txtTeamNoWithFam.setText("CASI-S" + String.format("%04d", MainApp.hh03txt) + "-H" + String.format("%03d", Integer.valueOf(MainApp.hh07txt)));

        setupButtons();

        // =================== Q 10 12 Skip Pattern ================
        for (RadioGroup sw : hh10_12) {
            sw.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (hh10a.isChecked()) {
                        hh11.setVisibility(View.VISIBLE);
                        hh11.requestFocus();
                    } else {
                        hh11.setVisibility(View.GONE);
                        hh11.setText(null);
                    }

                    /*if (hh14a.isChecked()) {
                        hh15.setVisibility(View.VISIBLE);
                        hh15.requestFocus();
                    } else {
                        hh15.setVisibility(View.GONE);
                        hh15.setText(null);
                    }*/
                }
            });
        }

        hh17.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    btnAddNewHousehold.setVisibility(View.VISIBLE);
                    btnAddHousehold.setVisibility(View.GONE);

                    if (MainApp.hh07txt.equals("1")) {
                        MainApp.hh07txt = "1";
                    }

                } else {
                    btnAddNewHousehold.setVisibility(View.GONE);
                    setupButtons();

                    if (MainApp.fTotal == 0) {
                        if (MainApp.hh07txt.equals("1")) {
                            MainApp.hh07txt = "1";
                        }
                    }

                }

                txtTeamNoWithFam.setText("CASI-S" + String.format("%04d", MainApp.hh03txt) + "-H" + String.format("%03d", Integer.valueOf(MainApp.hh07txt)));
            }
        });

        deleteHH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    ClearClass.ClearAllFields(fldGrpSecB01, false);
                else
                    ClearClass.ClearAllFields(fldGrpSecB01, true);
            }
        });

    }

    public void setupButtons() {
        if (MainApp.fCount < MainApp.fTotal) {
            btnAddFamilty.setVisibility(View.VISIBLE);
            btnAddHousehold.setVisibility(View.GONE);

            hh17.setVisibility(View.GONE);
        } else {
            btnAddFamilty.setVisibility(View.GONE);
            btnAddHousehold.setVisibility(View.VISIBLE);

            hh17.setVisibility(View.VISIBLE);

            deleteHH.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.btnAddNewHousehold)
    void onBtnAddNewHouseHoldClick() {

        if (formValidation()) {

            try {
                SaveDraft();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (UpdateDB()) {
                if (familyFlag) {
                    MainApp.hh07txt = String.valueOf(Integer.valueOf(MainApp.hh07txt) + 1);
                } else {

                    MainApp.hh07txt = String.valueOf(Integer.valueOf(MainApp.hh07txt) + 1);

                    familyFlag = true;
                }
                MainApp.lc.setHh07(MainApp.hh07txt);
                MainApp.fCount++;

                finish();
                Intent fA = new Intent(this, FamilyListingActivity.class);
                startActivity(fA);
            }
        }

    }

    private void SaveDraft() throws JSONException {

        MainApp.lc.setHh07(MainApp.hh07txt);
        MainApp.lc.setHh08a1("1");
        MainApp.lc.setHh08(hh08.getText().toString());
        MainApp.lc.setHh09(hh09.getText().toString());
        MainApp.lc.setHh10(hh10a.isChecked() ? "1" : hh10b.isChecked() ? "2" : "0");
        MainApp.lc.setHh11(hh11.getText().toString().isEmpty() ? "0" : hh11.getText().toString());
        MainApp.lc.setHh14(hh16.getText().toString());
        MainApp.lc.setHh15(deleteHH.isChecked() ? "1" : "0");
        MainApp.lc.setIsNewHH(hh17.isChecked() ? "1" : "2");

        Log.d(TAG, "SaveDraft: Structure " + MainApp.lc.getHh03());

    }

    private boolean formValidation() {

        if (deleteHH.isChecked()) return true;

        if (hh08.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            hh08.setError("Please enter name");
            Log.i(TAG, "Please enter name");
            return false;
        } else {
            hh08.setError(null);
        }

        if (hh09.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter contact number", Toast.LENGTH_SHORT).show();
            hh09.setError("Please enter contact number");
            Log.i(TAG, "Please enter contact number");
            return false;
        } else {
            hh09.setError(null);
        }

        if (hh16.getText().toString().isEmpty()) {
            Toast.makeText(this, "ERROR(empty): " + getString(R.string.hh16), Toast.LENGTH_SHORT).show();
            hh16.setError("ERROR(empty): " + getString(R.string.hh16));
            Log.i(TAG, "ERROR(empty): " + getString(R.string.hh16));
            return false;
        } else {
            hh16.setError(null);
        }


        if (!ValidatorClass.EmptyRadioButton(this, hh10, hh10a, getString(R.string.hh10))) {
            return false;
        }
        /*if (!ValidatorClass.EmptyRadioButton(this, hh10, hh10a, hh11, "Adolescents count")) {
            return false;
        }
        if (hh10a.isChecked()) {
            if (!ValidatorClass.RangeTextBox(this, hh11, 1, 99, getString(R.string.hh10), "for Adolescents")) {
                return false;
            }
        }

        if (!ValidatorClass.EmptyRadioButton(this, hh14, hh14a, getString(R.string.hh14))) {
            return false;
        }
        if (!ValidatorClass.EmptyRadioButton(this, hh14, hh14a, hh15, "Married Woman count")) {
            return false;
        }
        if (hh14a.isChecked()) {
            if (!ValidatorClass.RangeTextBox(this, hh15, 1, 99, getString(R.string.hh14), "for Married Woman")) {
                return false;
            }
        }*/

        if (Integer.valueOf(hh16.getText().toString()) < 1) {
            Toast.makeText(this, "Invalid Value!", Toast.LENGTH_SHORT).show();
            hh16.setError("Invalid Value!");
            Log.i(TAG, "Invalid Value!");
            return false;
        } else {
            hh16.setError(null);
        }


        if (Integer.valueOf(hh16.getText().toString()) <= (Integer.valueOf(hh11.getText().toString().isEmpty() ? "0" : hh11.getText().toString()))) {
            Toast.makeText(this, "Invalid Count!", Toast.LENGTH_SHORT).show();
            hh16.setError("Invalid Count!");
            Log.i(TAG, "(hh16): Invalid Count! ");
            return false;
        } else {
            hh16.setError(null);
        }

        return true;
    }


    @OnClick(R.id.btnAddFamily)
    void onBtnAddFamilyClick() {
        if (formValidation()) {

            try {
                SaveDraft();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (UpdateDB()) {
                MainApp.hh07txt = String.valueOf(Integer.valueOf(MainApp.hh07txt) + 1);
                MainApp.lc.setHh07(MainApp.hh07txt);
                MainApp.fCount++;

                finish();
                Intent fA = new Intent(this, FamilyListingActivity.class);
                startActivity(fA);
            }

        }

    }

    @OnClick(R.id.btnAddHousehold)
    void onBtnAddHouseholdClick() {
        if (formValidation()) {

            try {
                SaveDraft();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (UpdateDB()) {
                MainApp.fCount = 0;
                MainApp.fTotal = 0;
                MainApp.cCount = 0;
                MainApp.cTotal = 0;
                familyFlag = false;
                finish();
                Intent fA = new Intent(this, SetupActivity.class);
                startActivity(fA);

            }
        }

    }

    private boolean UpdateDB() {
        DataBaseHelper db = new DataBaseHelper(this);

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
