package edu.aku.hassannaqvi.casi_hhlisting.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.ListingContract;
import edu.aku.hassannaqvi.casi_hhlisting.Core.DataBaseHelper;
import edu.aku.hassannaqvi.casi_hhlisting.Other.randomListAdapter;
import edu.aku.hassannaqvi.casi_hhlisting.R;

public class RandomizationActivity extends MenuActivity {

    public static ArrayList<Integer> hhRandomise;
    ArrayList<ListingContract> lstList;
    Collection<ListingContract> listingCollection;
    @BindView(R.id.lstClusters)
    RecyclerView lstClusters;
    randomListAdapter randomListAdapter;
    DataBaseHelper db;
    ArrayList<ListingContract> listingDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_randomization);
        ButterKnife.bind(this);

        this.setTitle("Randomization Clusters");

        db = new DataBaseHelper(this);

        lstList = new ArrayList<>();
        hhRandomise = new ArrayList<>();

        new ApplicationsTask(this).execute();

        lstClusters.addOnItemTouchListener(
                new randomListAdapter.RecyclerItemClickListener(getApplicationContext(), new randomListAdapter.RecyclerItemClickListener.OnItemClickListener() {
                    Boolean delFlag = true;

                    @Override
                    public void onItemClick(View view, final int position) {
                        // TODO Handle item click

                        if (position != -1) {

                        }
                    }

                    @Override
                    public void onItemLongClick(final View view, final int position) {

                        if (position != -1) {
                            Boolean flag = true;
                            for (int item : hhRandomise) {
                                if (item == position) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        RandomizationActivity.this);
                                alertDialogBuilder
                                        .setMessage("Are you sure to randomize this cluster?")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        new RandomizationTask(RandomizationActivity.this, lstList.get(position).getClusterCode()).execute();

                                                    }
                                                });
                                alertDialogBuilder.setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = alertDialogBuilder.create();
                                alert.show();
                            }
                        }
                    }
                })
        );
    }

    public class ApplicationsTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;
        private Context context;

        public ApplicationsTask(Context mContext) {
            context = mContext;
            dialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Analyzing Data");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            lstClusters.setAdapter(randomListAdapter);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (success) {
                Toast.makeText(context, "Done!!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Error!!", Toast.LENGTH_LONG).show();
            }
        }

        protected Boolean doInBackground(final String... args) {
            try {

                for (ListingContract lst : db.getAllListingsForRandom()) {
                    lstList.add(lst);
                }

                randomListAdapter = new randomListAdapter(context, lstList);

                return true;
            } catch (Exception e) {
                Log.e("tag", "error", e);
                return false;
            }
        }
    }

    public class RandomizationTask extends AsyncTask<String, Void, Boolean> {
        String clustercode;
        private ProgressDialog dialog;
        private Context context;

        public RandomizationTask(Context mContext, String clustercode) {
            context = mContext;
            dialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            this.clustercode = clustercode;

            listingDataList = new ArrayList<>();
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Analyzing Listing");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    if (success) {

                        new ApplicationsTask(context).execute();

                        Toast.makeText(context, "Randomized!!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Error in Randomization!!", Toast.LENGTH_LONG).show();
                    }

                }
            }, 800);
        }

        protected Boolean doInBackground(final String... args) {
            try {

                ArrayList<ListingContract> listingContracts = db.randomLisiting(clustercode);

                if (listingContracts.size() > 20) {

                    double sum = listingContracts.size() / 20d;
                    double random = (new Random().nextDouble() * sum) + 1d;

                    listingDataList.add(listingContracts.get((int) random));

                    double lstSize = sum + random + 0d;

                    while ((int) lstSize <= listingContracts.size()) {

                        listingDataList.add(listingContracts.get((int) lstSize));

                        lstSize += sum + 0d;

                    }

                } else {
                    for (ListingContract lst : listingContracts) {
                        listingDataList.add(lst);
                    }
                }

                dialog.setTitle("Saving Randomization");

                for (ListingContract listingData : listingDataList) {
                    db.addBLRandom(listingData);
                }

                /*Update in db*/
                dialog.setTitle("Updating Listing");

                db.updateListingRecord(clustercode);

                return true;
            } catch (Exception e) {
                Log.e("tag", "error", e);
                return false;
            }
        }
    }

}
