package edu.aku.hassannaqvi.uenmd_hhlisting.activities.ui;

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
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.DatabaseHelper;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.uenmd_hhlisting.R;
import edu.aku.hassannaqvi.uenmd_hhlisting.validation.ClearClass;
import edu.aku.hassannaqvi.uenmd_hhlisting.validation.ValidatorClass;

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
    @BindView(R.id.hh16)
    EditText hh16;
    @BindView(R.id.hh17)
    Switch hh17;
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
    @BindViews({R.id.hh10})
    List<RadioGroup> hh10_12;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_listing);
        ButterKnife.bind(this);

        this.setTitle("Family Information");
        txtTeamNoWithFam.setText(MainApp.tabCheck + "-" + String.format("%04d", MainApp.hh03txt) + "-" + String.format("%03d", Integer.valueOf(MainApp.hh07txt)));

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

                txtTeamNoWithFam.setText(MainApp.tabCheck + "-S" + String.format("%04d", MainApp.hh03txt) + "-H" + String.format("%03d", Integer.valueOf(MainApp.hh07txt)));
            }
        });

        deleteHH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ClearClass.ClearAllFields(fldGrpSecB01, !b);
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
            MainApp.lc.setTagId(MainApp.lc.getTagId() + "3");

            if (UpdateDB()) {
                MainApp.hh07txt = String.valueOf(Integer.valueOf(MainApp.hh07txt) + 1);
                if (!familyFlag) {
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

        /*if (hh09.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter contact number", Toast.LENGTH_SHORT).show();
            hh09.setError("Please enter contact number");
            Log.i(TAG, "Please enter contact number");
            return false;
        } else {
            hh09.setError(null);
        }*/

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
        if (hh10a.isChecked()) {
            if (!ValidatorClass.EmptyTextBox(this, hh11, getString(R.string.hh10))) {
                return false;
            }
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
            MainApp.lc.setTagId(MainApp.lc.getTagId() + "4");

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
        btnAddHousehold.setEnabled(false);
        if (formValidation()) {

            try {
                SaveDraft();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MainApp.lc.setTagId(MainApp.lc.getTagId() + "5");

            if (UpdateDB()) {
                MainApp.fCount = 0;
                MainApp.fTotal = 0;
                MainApp.cCount = 0;
                MainApp.cTotal = 0;
                familyFlag = false;
                Intent fA = new Intent(this, SetupActivity.class);
                startActivity(fA);
                finish();

            }
        } else {
            btnAddHousehold.setEnabled(true);

        }

    }

    private boolean UpdateDB() {
        db = MainApp.db;

        long updcount = db.addForm(MainApp.lc);

        MainApp.lc.setID(String.valueOf(updcount));

        if (updcount > 0) {

            MainApp.lc.setUID(
                    (MainApp.lc.getDeviceID() + MainApp.lc.getID()));

            db.updateListingUID();
            MainApp.lc.setUID("");
        } else {
            Toast.makeText(this, "Updating Database... ERROR!", Toast.LENGTH_SHORT).show();
        }
        //   MainApp.lc = null;
        return true;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Back Button NOT Allowed!", Toast.LENGTH_SHORT).show();

    }
}
