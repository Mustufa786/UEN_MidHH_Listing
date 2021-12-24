package edu.aku.hassannaqvi.uenmd_hhlisting.activities.home;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.validatorcrawler.aliazaz.Validator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.EnumBlockContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.VersionAppContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Contracts.VerticesContract;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.AndroidDatabaseManager;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.DatabaseHelper;
import edu.aku.hassannaqvi.uenmd_hhlisting.Core.MainApp;
import edu.aku.hassannaqvi.uenmd_hhlisting.R;
import edu.aku.hassannaqvi.uenmd_hhlisting.activities.map.MapsActivity;
import edu.aku.hassannaqvi.uenmd_hhlisting.activities.menu.MenuActivity;
import edu.aku.hassannaqvi.uenmd_hhlisting.activities.ui.SetupActivity;
import edu.aku.hassannaqvi.uenmd_hhlisting.activities.ui.SyncActivity;

public class MainActivity extends MenuActivity {

    public static String TAG = "MainActivity";
    static File file;
    private static final String ipAddress = "192.168.1.10";
    private static final String port = "3000";
    public List<String> psuCode;
    String dtToday = new SimpleDateFormat("dd-MM-yy HH:mm").format(new Date().getTime());
    @BindView(R.id.districtN)
    TextView districtN;
    @BindView(R.id.ucN)
    TextView ucN;
    @BindView(R.id.psuN)
    TextView psuN;
    @BindView(R.id.msgUpdate)
    TextView msgUpdate;
    @BindView(R.id.msgText)
    TextView msgText;
    @BindView(R.id.txtPSU)
    EditText txtPSU;
    @BindView(R.id.btnCheckPSU)
    Button btnCheckPSU;
    @BindView(R.id.chkconfirm)
    CheckBox chkconfirm;
    @BindView(R.id.openForm)
    Button openForm;
    @BindView(R.id.na101a)
    TextView na101a;
    @BindView(R.id.na101b)
    TextView na101b;
    @BindView(R.id.na101c)
    TextView na101c;
    @BindView(R.id.lang)
    TextView lang;
    @BindView(R.id.na101d)
    Spinner na101d;
    @BindView(R.id.fldGrpna101)
    LinearLayout fldGrpna101;
    @BindView(R.id.adminBlock)
    LinearLayout adminBlock;
    @BindView(R.id.lllstwarning)
    LinearLayout lllstwarning;

    @BindView(R.id.fldGrpMain01)
    LinearLayout fldGrpMain01;

    @BindView(R.id.lstwarning)
    RadioGroup lstwarning;

    @BindView(R.id.lstwarninga)
    RadioButton lstwarninga;
    @BindView(R.id.lstwarningb)
    RadioButton lstwarningb;


    SharedPreferences sharedPref;
    SharedPreferences sharedPrefInfo;
    SharedPreferences.Editor editor;
    AlertDialog.Builder builder;
    String m_Text = "";
    Boolean exit = false;
    Boolean flag = false;
    DatabaseHelper db;
    ProgressDialog progressDoalog;
    @BindView(R.id.lblAppVersion)
    TextView lblAppVersion;
    VersionAppContract versionAppContract;
    String preVer = "", newVer = "";
    DownloadManager downloadManager;
    Long refID;
    SharedPreferences sharedPrefDownload;
    SharedPreferences.Editor editorDownload;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(sharedPrefDownload.getLong("refID", 0));

                downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = downloadManager.query(query);
                if (cursor.moveToFirst()) {
                    int colIndex = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(colIndex)) {

                        editorDownload.putBoolean("flag", true);
                        editorDownload.commit();

                        Toast.makeText(context, "UEN-Midline Linelisting APP downloaded!!", Toast.LENGTH_SHORT).show();
                        lblAppVersion.setText("UEN-Midline Linelisting APP New Version " + newVer + "  Downloaded.");

                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

                        if (taskInfo.get(0).topActivity.getClassName().equals(MainActivity.class.getName())) {
                            showDialog(newVer, preVer);
                        }
                    }
                }
            }
        }
    };

    private String clusterName;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem dbManager = menu.findItem(R.id.menu_openDB);
//        MenuItem randomization = menu.findItem(R.id.menu_randomization);

        //            randomization.setVisible(true);
        //            randomization.setVisible(false);
        dbManager.setVisible(MainApp.admin);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.setTitle("Household Linelisting");

        Locale current = getResources().getConfiguration().locale;
        lang.setText(current.getDisplayLanguage() + " | " + current.getISO3Language() + " | " + current.getLanguage());

        if (MainApp.admin) {
            adminBlock.setVisibility(View.VISIBLE);
        } else {
            adminBlock.setVisibility(View.GONE);
        }
        MainApp.lc = null;

        /*Tag Info Start*/
        sharedPref = getSharedPreferences("tagName", MODE_PRIVATE);
        sharedPrefInfo = getSharedPreferences("info", MODE_PRIVATE);
        editor = sharedPref.edit();

        /*Download File*/
        sharedPrefDownload = getSharedPreferences("appDownload", MODE_PRIVATE);
        editorDownload = sharedPrefDownload.edit();

        String versionCode = sharedPrefInfo.getString("versionCode", "");
        if (versionCode.equals("")) {


        } else {
            if (Integer.valueOf(versionCode) > Integer.valueOf(MainApp.versionCode)) {
                msgUpdate.setVisibility(View.VISIBLE);
            }
        }
        builder = new AlertDialog.Builder(MainActivity.this);
        ImageView img = new ImageView(getApplicationContext());
        img.setImageResource(R.drawable.tagimg);
        img.setPadding(0, 15, 0, 15);
        builder.setCustomTitle(img);

        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if (!m_Text.equals("")) {
                    editor.putString("tagName", m_Text);
                    editor.commit();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // database handler
        db = new DatabaseHelper(getApplicationContext());

        msgText.setText(db.getListingCount() + " records found in Listings table.");
        // spinnersFill();

//        Version Checking
        versionAppContract = db.getVersionApp();
        if (versionAppContract.getVersioncode() != null) {

            preVer = MainApp.versionName + "." + MainApp.versionCode;
            newVer = versionAppContract.getVersionname() + "." + versionAppContract.getVersioncode();

            if (MainApp.versionCode < Integer.valueOf(versionAppContract.getVersioncode())) {
                lblAppVersion.setVisibility(View.VISIBLE);

                String fileName = DatabaseHelper.DATABASE_NAME.replace(".db", "-New-Apps");
                file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName, versionAppContract.getPathname());

                if (file.exists()) {
                    lblAppVersion.setText("UEN-Midline Linelisting APP New Version " + newVer + "  Downloaded.");
                    showDialog(newVer, preVer);
                } else {
                    NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {

                        lblAppVersion.setText("UEN-Midline Linelisting APP New Version " + newVer + " Downloading..");
                        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(MainApp._UPDATE_URL + versionAppContract.getPathname());
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setDestinationInExternalPublicDir(fileName, versionAppContract.getPathname())
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setTitle("Downloading UEN-Midline new App ver." + newVer);
                        refID = downloadManager.enqueue(request);

                        editorDownload.putLong("refID", refID);
                        editorDownload.putBoolean("flag", false);
                        editorDownload.commit();

                    } else {
                        lblAppVersion.setText("UEN-Midline Linelisting APP New Version " + newVer + "  Available..\n(Can't download.. Internet connectivity issue!!)");
                    }
                }

            } else {
                lblAppVersion.setVisibility(View.GONE);
                lblAppVersion.setText(null);
            }
        }

        registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void populateSpinner(String villages) {

        MainApp.selectedVillage = villages;
        // Spinner Drop down elements
        //List<String> teamNos = new ArrayList<String>();
        //Collection<TeamsContract> dc = db.getAllTeams();

        //teamNos.add("....");

        //Log.d(TAG, "onCreate: " + dc.size());
        ArrayList villageList = new ArrayList<>();
        villageList.add("...");
        String[] villiageSplit = villages.split(",");

        for (String v : villiageSplit) {

            villageList.add(v);

        }


        // Creating adapter for spinner
        na101d.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, villageList));

        na101d.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chkconfirm.setChecked(false);
                lstwarning.clearCheck();
                if (na101d.getSelectedItemPosition() != 0) {

                    MainApp.hh01txt = na101d.getSelectedItem().toString();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtPSU.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                districtN.setText(null);
                psuN.setText(null);
                ucN.setText(null);
                fldGrpna101.setVisibility(View.GONE);
                lllstwarning.setVisibility(View.GONE);
                chkconfirm.setChecked(false);
                lstwarning.clearCheck();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

/*
        mN02.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainApp.hh02txt = psuCode.get(position);
                Collection<PSUsContract> pc = db.getAllPSUsByTaluka(MainApp.hh01txt);
                for (PSUsContract p : pc) {
                    Log.d(TAG, "onItemSelected: " + p.getPSUCode() + " -" + MainApp.hh02txt);

                    if (p.getPSUCode().equals(MainApp.hh02txt)) {
                        Log.d(TAG, "onItemSelected: " + p.getPSUName());
                        String[] psuNameS = p.getPSUName().toString().split("\\|");
                        districtN.setText(psuNameS[0]);
                        Log.d(TAG, "onItemSelected: " + psuNameS[0]);
                        ucN.setText(psuNameS[1]);
                        Log.d(TAG, "onItemSelected: " + psuNameS[1]);
                        psuN.setText(psuNameS[2]);
                        Log.d(TAG, "onItemSelected: " + psuNameS[2]);

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

    }

    @OnClick(R.id.btnCheckPSU)
    void onCheckPSUClick() {
        //TODO implement
        OpenFormFun();
    }

    public void OpenFormFun() {

        if (txtPSU.getText().toString().length() != 6) {

            txtPSU.setError(null);
            boolean loginFlag;
            int clus = Integer.valueOf(txtPSU.getText().toString().substring(3, 6));
            if (clus < 500) {
                loginFlag = !(MainApp.userEmail.equals("test1234") || MainApp.userEmail.equals("dmu@aku") || MainApp.userEmail.startsWith("user"));
            } else {
                loginFlag = MainApp.userEmail.equals("test1234") || MainApp.userEmail.equals("dmu@aku") || MainApp.userEmail.startsWith("user");
            }
            if (loginFlag) {
                db = MainApp.db;
                EnumBlockContract enumBlockContract = db.getEnumBlock(txtPSU.getText().toString());
                if (enumBlockContract != null) {
                    String selected = enumBlockContract.getGeoarea();

                    if (!selected.equals("")) {

                        String[] selSplit = selected.split("\\|");

                        na101a.setText(selSplit[0]);
                        na101b.setText(selSplit[1].equals("") ? "----" : selSplit[1]);
                        na101c.setText(selSplit[2].equals("") ? "----" : selSplit[2]);
                        populateSpinner(selSplit[3]);
                        clusterName = selSplit[1];

                        fldGrpna101.setVisibility(View.VISIBLE);

                        flag = true;
                        chkconfirm.setOnCheckedChangeListener((compoundButton, b) -> {


                            if (chkconfirm.isChecked()) {
                                if (na101d.getSelectedItemPosition() != 0) {
                                    openForm.setBackgroundColor(getResources().getColor(R.color.green));
                                    lllstwarning.setVisibility(View.VISIBLE);
                                    // MainApp.hh01txt = "1";

                                    // Cluster question
                                    if (MainApp.PSUExist(MainApp.hh02txt))
                                        fldGrpMain01.setVisibility(View.GONE);
                                    else {
                                        fldGrpMain01.setVisibility(View.VISIBLE);
                                        lstwarning.clearCheck();
                                    }
                                } else {
                                    chkconfirm.setChecked(false);
                                    Toast.makeText(this, "Please select a village.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                lllstwarning.setVisibility(View.GONE);
                            }


                        });

                        MainApp.hh02txt = txtPSU.getText().toString();
                        MainApp.enumCode = enumBlockContract.getDist_id();
                        MainApp.enumStr = enumBlockContract.getGeoarea();
                        MainApp.clusterCode = txtPSU.getText().toString();
                    }
                } else {
                    Toast.makeText(this, "Sorry not found any block", Toast.LENGTH_SHORT).show();
                    flag = false;
                    lllstwarning.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "Can't proceed test cluster for current user!!", Toast.LENGTH_SHORT).show();
                flag = false;
                lllstwarning.setVisibility(View.GONE);
            }
        } else {
            txtPSU.setError("Incorrect Cluster Number");
            txtPSU.setFocusable(true);
        }
    }

    public void alertPSU() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent oF = new Intent(MainActivity.this, SetupActivity.class);
                        startActivity(oF);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to continue?");
        builder.setMessage("PSU data already exist.").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    public void openClusterMap(View view) {

        db = MainApp.db;
        Collection<VerticesContract> v = db.getVerticesByCluster(txtPSU.getText().toString());
        if (v.size() > 3) {
            startActivity(new Intent(this, MapsActivity.class));
        } else {
            Toast.makeText(this, "Cluster map do not exist for " + clusterName, Toast.LENGTH_SHORT).show();
        }
    }

    public void openForm(View view) {
        NextSetupActivity();
    }

    public void NextSetupActivity() {

        if (flag) {

            if (!Validator.emptyCheckingContainer(this, fldGrpMain01)) return;

            if (MainApp.tabCheck.equals(""))
                MainApp.tabCheck = lstwarninga.isChecked() ? "A" : lstwarningb.isChecked() ? "B" : "";

            if (MainApp.PSUExist(MainApp.hh02txt)) {
                Toast.makeText(MainActivity.this, "PSU data exist!", Toast.LENGTH_LONG).show();
                alertPSU();
            } else {
                if (MainApp.tabCheck.equals(""))
                    MainApp.tabCheck = lstwarninga.isChecked() ? "A" : lstwarningb.isChecked() ? "B" : "";
                startActivity(new Intent(this, SetupActivity.class));
            }
        } else {
            Toast.makeText(this, "Please Click on CHECK button!", Toast.LENGTH_SHORT).show();
        }

    }

    public void openDB(View view) {
        Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void syncFunction(View view) {
        if (isNetworkAvailable()) {

            /*new SyncDevice(this).execute();

            Toast.makeText(getApplicationContext(), "Syncing Listing", Toast.LENGTH_SHORT).show();
            new SyncAllData(
                    this,
                    "Listing",
                    "updateSyncedForms",
                    ListingContract.class,
                    MainApp._HOST_URL + ListingContract.ListingEntry._URL,
                    db.getUnsyncedListings()
            ).execute();
            Toast.makeText(getApplicationContext(), "Syncing New Users Data", Toast.LENGTH_SHORT).show();
            new SyncAllData(
                    this,
                    "New Users",
                    "updateSyncedSignup",
                    SignupContract.class,
                    MainApp._HOST_URL + SignupContract.SignUpTable._URL,
                    db.getUnsyncedSignups()
            ).execute();*/

            startActivity(new Intent(MainActivity.this, SyncActivity.class));

            SharedPreferences syncPref = getSharedPreferences("SyncInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = syncPref.edit();

            editor.putString("LastSyncDB", dtToday);

            editor.apply();
        } else {
            Toast.makeText(getApplicationContext(), "Network Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity

            Intent ii = new Intent(this, LoginActivity.class);
            ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(ii);
            finish();

        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    void showDialog(String newVer, String preVer) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment newFragment = MyDialogFragment.newInstance(newVer, preVer);
        newFragment.show(ft, "dialog");

    }

    public static class MyDialogFragment extends DialogFragment {

        String newVer, preVer;

        static MyDialogFragment newInstance(String newVer, String preVer) {
            MyDialogFragment f = new MyDialogFragment();

            Bundle args = new Bundle();
            args.putString("newVer", newVer);
            args.putString("preVer", preVer);
            f.setArguments(args);

            return f;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            newVer = getArguments().getString("newVer");
            preVer = getArguments().getString("preVer");

            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.exclamation)
                    .setTitle("UEN-Midline-2018 Line Listing APP is available!")
                    .setMessage("UEN-Midline Line Listing App " + newVer + " is now available. Your are currently using older version " + preVer + ".\nInstall new version to use this app.")
                    .setPositiveButton("INSTALL!!",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                    )
                    .create();
        }

    }

  /*  public class CopyTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog Asycdialog;
        Context mContext;

        public CopyTask(Context mContext) {
            this.mContext = mContext;
            Asycdialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Asycdialog.setTitle("COPYING DATA");
            Asycdialog.setMessage("Loading...");
            Asycdialog.setCancelable(false);
            Asycdialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // do the task you want to do. This will be executed in background.
            try {

                File root = new File(Environment.getExternalStorageDirectory(), "Notes");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)
                        + "_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                        + "_" + ListingContract.ListingEntry.TABLE_NAME);
                FileWriter writer = new FileWriter(gpxfile);

                Collection<ListingContract> listing = db.getUnsyncedListings();
                if (listing.size() > 0) {
                    JSONArray jsonSync = new JSONArray();
                    for (ListingContract fc : listing) {
                        jsonSync.put(fc.toJSONObject());
                    }

                    writer.append(String.valueOf(jsonSync));
                    writer.flush();
                    writer.close();

                    if (listing.size() < 100) {
                        Thread.sleep(3000);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            Asycdialog.dismiss();
            Toast.makeText(mContext, "Copying done!!", Toast.LENGTH_SHORT).show();
        }
    }*/

}
