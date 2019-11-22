package com.info121.nativelimo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info121.nativelimo.R;
import com.info121.nativelimo.App;
import com.info121.nativelimo.activities.JobDetailActivity;
import com.info121.nativelimo.models.Job;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.ViewHolder> {
    private int lastPosition = -1;

    private Context mContext;
    List<Job> mJobList = new ArrayList<>();
    private String mCurrentTab = "";

    public void updateJobList(List<Job> jobList, String currentTab) {
        mJobList = jobList;
        mCurrentTab = currentTab;
    }

    public JobsAdapter(Context mContext, List<Job> mJobList) {
        this.mContext = mContext;
        this.mJobList = mJobList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View promotionView = inflater.inflate(R.layout.cell_job, parent, false);

        // Return a new holder instance
        return new ViewHolder(promotionView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        setAnimation(viewHolder.itemView, i);

        viewHolder.jobType.setText(mJobList.get(i).getJobType());
        viewHolder.jobStatus.setText(mJobList.get(i).getJobStatus());
        viewHolder.vehicleType.setText(mJobList.get(i).getVehicleType());

        viewHolder.pickupTime.setText(mJobList.get(i).getPickUpTime());
        viewHolder.pickup.setText(mJobList.get(i).getPickUp());
        viewHolder.dropoff.setText(mJobList.get(i).getDestination());

        viewHolder.passenger.setText(mJobList.get(i).getCustomer());
        viewHolder.mobile.setText(mJobList.get(i).getCustomerTel());


        //viewHolder.passenger.setText(mJobList.get(i).getJobNo());

        final String jobNo = mJobList.get(i).getJobNo();
        final int index = i;


        viewHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.jobList = mJobList;

                Intent intent = new Intent(mContext, JobDetailActivity.class);
                intent.putExtra("jobNo", jobNo);
                intent.putExtra("index", index);
                intent.putExtra("currentTab", mCurrentTab);
                mContext.startActivity(intent);

            }
        });

//        if (!mCurrentTab.equalsIgnoreCase("HISTORY"))
//            viewHolder.parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    App.jobList = mJobList;
//
//                    Intent intent = new Intent(mContext, JobDetailActivity.class);
//                    intent.putExtra("jobNo", jobNo);
//                    intent.putExtra("index", index);
//                    mContext.startActivity(intent);
//
//                }
//            });

    }

    @Override
    public int getItemCount() {
        return (mJobList == null) ? 0 : mJobList.size();
    }


    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.job_type)
        TextView jobType;

        @BindView(R.id.job_status)
        TextView jobStatus;

        @BindView(R.id.vehicle_type)
        TextView vehicleType;

        @BindView(R.id.pickup_time)
        TextView pickupTime;

        @BindView(R.id.pickup)
        TextView pickup;

        @BindView(R.id.dropoff)
        TextView dropoff;

        @BindView(R.id.passenger)
        TextView passenger;

        @BindView(R.id.mobile)
        TextView mobile;

        @BindView(R.id.main_layout)
        LinearLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
