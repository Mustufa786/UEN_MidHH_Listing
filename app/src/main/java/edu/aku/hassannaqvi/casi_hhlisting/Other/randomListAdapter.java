package edu.aku.hassannaqvi.casi_hhlisting.Other;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.aku.hassannaqvi.casi_hhlisting.activities.RandomizationActivity;
import edu.aku.hassannaqvi.casi_hhlisting.Contracts.ListingContract;
import edu.aku.hassannaqvi.casi_hhlisting.R;

/**
 * Created by ali.azaz on 13/04/2017.
 */

public class randomListAdapter extends RecyclerView.Adapter<randomListAdapter.ViewHolder> {
    Context mContext;
    ViewHolder holder;
    private ArrayList<ListingContract> list;
    private List<CheckBox> checksList;

    public randomListAdapter(Context context, ArrayList<ListingContract> list) {
        this.list = list;
        mContext = context;
        checksList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View statusContainer = LayoutInflater.from(parent.getContext()).inflate(R.layout.lstview_random1, parent, false);
        return new ViewHolder(statusContainer);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        this.holder = holder;
        this.holder.bindUser(list.get(position));

/*        this.holder.checkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                *//*if (b) {
                    MainActivity.checkPos.add(position);
                    Toast.makeText(mContext, list.get(position).getPackageName(), Toast.LENGTH_SHORT).show();
                } else {
                    for (byte i = 0; i < MainActivity.checkPos.size(); i++) {
                        if (MainActivity.checkPos.get(i) == position) {
                            MainActivity.checkPos.remove(i);
                            break;
                        }
                    }
                }*//*
            }
        });*/

        if (list.get(position).getTotalhh().equals(list.get(position).randCount)) {
            RandomizationActivity.hhRandomise.add(position);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        GestureDetector mGestureDetector;
        private OnItemClickListener mListener;
        private RecyclerView viewRecycle;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;

            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = viewRecycle.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onItemLongClick(child, viewRecycle.getChildAdapterPosition(child));
                    }

                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            viewRecycle = view;
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);

            void onItemLongClick(View view, int position);
        }


    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.checkSelected)
        ImageView checkSelected;
        @BindView(R.id.clusterCode)
        TextView clusterCode;
        @BindView(R.id.resCount)
        TextView resCount;
        @BindView(R.id.childCount)
        TextView childCount;
        @BindView(R.id.rndCount)
        TextView rndCount;
        @BindView(R.id.totalCount)
        TextView totalCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindUser(ListingContract contact) {
            clusterCode.setText(contact.getClusterCode());
            resCount.setText("Residential Count: " + contact.getResCount());
            childCount.setText("Child < 5 Count: " + contact.getChildCount());
            rndCount.setText("Randomized Count: " + contact.getRandCount());
            totalCount.setText("Total HH Count: " + contact.getTotalhh());

            if (contact.getTotalhh().equals(contact.randCount)) {
                checkSelected.setVisibility(View.VISIBLE);
            }

//            checkSelected.setChecked(contact.getIsRandom().equals("1") ? true : false);
        }
    }

}
