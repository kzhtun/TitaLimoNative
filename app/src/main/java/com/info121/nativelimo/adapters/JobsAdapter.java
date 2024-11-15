package com.info121.nativelimo.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.info121.nativelimo.R;
import com.info121.nativelimo.App;
import com.info121.nativelimo.activities.JobDetailActivity;
import com.info121.nativelimo.activities.LoginActivity;
import com.info121.nativelimo.activities.PatientHistoryActivity;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.Job;
import com.info121.nativelimo.models.JobRes;
import com.info121.nativelimo.models.RequestUpdateRemark;
import com.info121.nativelimo.models.SearchParams;
import com.info121.nativelimo.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class JobsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Calendar myCalendar;

    int adapterPosition;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mobileNoSapce = "";

    private String sort = "0";

    private int lastPosition = -1;
    public static List<String> updatesTextArray = new ArrayList<>(2000);

    private Context mContext;
    List<Job> mJobList = new ArrayList<>();
    List<Job> mJobListAsc = new ArrayList<>();
    List<Job> mJobListDesc = new ArrayList<>();
    private String mCurrentTab = "";

    public void updateJobList(List<Job> jobList, String currentTab) {
        mJobList = jobList;

        mCurrentTab = currentTab;

        if (mCurrentTab.equalsIgnoreCase("FUTURE")) {
            if (App.futureSearchParams.getSort() == "0") {
                mJobListAsc = mJobList;
                mJobListDesc = new ArrayList<>(mJobList);
                Collections.reverse(mJobListDesc);
            } else {
                mJobListDesc = mJobList;
                mJobListAsc = new ArrayList<>(mJobList);
                Collections.reverse(mJobListAsc);
            }
        }

        if (mCurrentTab.equalsIgnoreCase("HISTORY")) {
            if (App.historySearchParams.getSort() == "0") {
                mJobListAsc = mJobList;
                mJobListDesc = new ArrayList<>(mJobList);
                Collections.reverse(mJobListDesc);
            } else {
                mJobListDesc = mJobList;
                mJobListAsc = new ArrayList<>(mJobList);
                Collections.reverse(mJobListAsc);
            }
        }

        notifyDataSetChanged();
    }

    public JobsAdapter(Context mContext, List<Job> mJobList) {
        this.mContext = mContext;
        this.mJobList = mJobList;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_HEADER : TYPE_ITEM;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == TYPE_HEADER) {
            View searchView = inflater.inflate(R.layout.cell_search, parent, false);
            return new HeaderViewHolder(searchView);
        }

        if (viewType == TYPE_ITEM) {
            View itemView = inflater.inflate(R.layout.cell_job, parent, false);
            return new ItemViewHolder(itemView);
        }

        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder headerVH = (HeaderViewHolder) viewHolder;

            Log.e(" CURRENT TAB : ", mCurrentTab);

//            if (mCurrentTab.equalsIgnoreCase("TODAY") || mCurrentTab.equalsIgnoreCase("TOMORROW")) {
//                headerVH.itemView.setVisibility(GONE);
//                headerVH.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
//            }

            if (mCurrentTab.equalsIgnoreCase("FUTURE") || mCurrentTab.equalsIgnoreCase("HISTORY")) {
                headerVH.itemView.setVisibility(View.VISIBLE);

                int margin = (int) Util.convertDpToPixel(16, mContext);
                RecyclerView.LayoutParams llm = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                llm.setMargins(margin, 0, margin, margin);
                headerVH.itemView.setLayoutParams(llm);
            } else {
                headerVH.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }


            myCalendar = Calendar.getInstance();
            String myFormat = "dd MMM yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            // show/ hide sorting panel
            if (mCurrentTab.equalsIgnoreCase("FUTURE")) {
                headerVH.mPassenger.setText(App.futureSearchParams.getCustomer());
                headerVH.mUpdates.setText(App.futureSearchParams.getUpdates());

                if (App.futureSearchParams.getFromDate().trim().length() == 0)
                    headerVH.mFromDate.setText("");
                else
                    headerVH.mFromDate.setText(App.futureSearchParams.getFromDate());

                if (App.futureSearchParams.getToDate().trim().length() == 0)
                    headerVH.mToDate.setText("");
                else
                    headerVH.mToDate.setText(App.futureSearchParams.getToDate());

                if (App.futureSearchParams.getSort() == "0") {
                    headerVH.sortAsc.setChecked(true);
                } else {
                    headerVH.sortDesc.setChecked(true);
                }
            }

            if (mCurrentTab.equalsIgnoreCase("HISTORY")) {
//                headerVH.mSortLayout.setVisibility(View.INVISIBLE);
                headerVH.mSearchUpdateLayout.setVisibility(View.VISIBLE);

                headerVH.mPassenger.setText(App.historySearchParams.getCustomer());
                headerVH.mUpdates.setText(App.historySearchParams.getUpdates());

                if (App.historySearchParams.getFromDate().trim().length() == 0)
                    headerVH.mFromDate.setText("");
                else
                    headerVH.mFromDate.setText(App.historySearchParams.getFromDate());

                if (App.historySearchParams.getToDate().trim().length() == 0)
                    headerVH.mToDate.setText("");
                else
                    headerVH.mToDate.setText(App.historySearchParams.getToDate());

                if (App.historySearchParams.getSort() == "0") {
                    headerVH.sortAsc.setChecked(true);
                } else {
                    headerVH.sortDesc.setChecked(true);
                }

            } else {
//                headerVH.mSortLayout.setVisibility(View.VISIBLE);
                headerVH.mSearchUpdateLayout.setVisibility(View.GONE);
            }

            if (mJobList == null)
                headerVH.mJobCount.setText("0");
            else
                headerVH.mJobCount.setText(mJobList.size() + "");
        }


        if (viewHolder instanceof ItemViewHolder) {
            i--;

            ItemViewHolder itemVH = (ItemViewHolder) viewHolder;
            setAnimation(viewHolder.itemView, i);



//              show/ hide updates textbox and button depend on the which tabs
//            if (mCurrentTab.equalsIgnoreCase("HISTORY") && mJobList.get(i).getUaeType().equalsIgnoreCase("Translation Job"))
//                if (mCurrentTab.equalsIgnoreCase("HISTORY"))
//                    itemVH.updateLayout.setVisibility(View.VISIBLE);
//                else
//                    itemVH.updateLayout.setVisibility(GONE);

            itemVH.updateLayout.setVisibility(View.VISIBLE);

            if(mJobList.get(i).getUaeType().equalsIgnoreCase("Translation Job")){
                itemVH.mButtonLayout.setVisibility(View.VISIBLE);
            }else{
                itemVH.mButtonLayout.setVisibility(View.GONE);
            }



            if (Util.isNullOrEmpty(mJobList.get(i).getUpdates()))
                mJobList.get(i).setUpdates("");

            if (mJobList.get(i).getUpdates().length() == 0 || mJobList.get(i).getUpdates().equals("##-##") || mJobList.get(i).getUpdates().equals("\n")) {
                itemVH.mViewUpdates.setText("ADD");
                itemVH.mViewUpdates.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_button));
            } else {
                itemVH.mViewUpdates.setText("VIEW");
                itemVH.mViewUpdates.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_button_orange));
            }

            String jobStatus = (mJobList.get(i).getJobStatus().equalsIgnoreCase("JOB NEW") ? "JOB ASSIGNED" : mJobList.get(i).getJobStatus().toUpperCase());

          //  itemVH.jobType.setText(Util.capitalize(mJobList.get(i).getJobType()));
            itemVH.jobType.setText(mJobList.get(i).getJobType());
            itemVH.jobStatus.setText(jobStatus);
            itemVH.vehicleType.setText(mJobList.get(i).getVehicleType());
            itemVH.pickupTime.setText(mJobList.get(i).getPickUpTime());
            itemVH.pickup.setText(mJobList.get(i).getPickUp());
            itemVH.dropoff.setText(mJobList.get(i).getDestination());
            itemVH.passenger.setText(mJobList.get(i).getCustomer());

            mobileNoSapce = mJobList.get(i).getCustomerTel().toString().replaceAll(" ", "");
            itemVH.mobile.setText(mobileNoSapce.replaceAll("/", "\n"));
            itemVH.jobDate.setText(mJobList.get(i).getUsageDate());

            itemVH.staff.setText(mJobList.get(i).getStaff());
            itemVH.updateText.setText(mJobList.get(i).getUpdates().toString().replaceAll("##-##", "\n"));
            //viewHolder.passenger.setText(mJobList.get(i).getJobNo());

            Log.e("Job No:", mJobList.get(i).getJobNo());


            final String jobNo = mJobList.get(i).getJobNo();
            final int index = i;

            itemVH.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (mCurrentTab.equalsIgnoreCase("TODAY")) {
//                        getTodayJobs(jobNo, index);
//                    }
//
//                    if (mCurrentTab.equalsIgnoreCase("TOMORROW")) {
//                        getTomorrowJobs(jobNo, index);
//                    }

                   // if (mCurrentTab.equalsIgnoreCase("FUTURE") || mCurrentTab.equalsIgnoreCase("HISTORY")) {
                        App.jobList = mJobList;

                        Intent intent = new Intent(mContext, JobDetailActivity.class);
                        intent.putExtra("jobNo", jobNo);
                        intent.putExtra("index", index);
                        intent.putExtra("currentTab", mCurrentTab);
                        mContext.startActivity(intent);
                  //  }


//                    App.jobList = mJobList;
//                    Intent intent = new Intent(mContext, JobDetailActivity.class);
//                    intent.putExtra("jobNo", jobNo);
//                    intent.putExtra("index", index);
//                    mContext.startActivity(intent);

                }
            });

            if (mCurrentTab.equalsIgnoreCase("HISTORY") || mCurrentTab.equalsIgnoreCase("FUTURE")) {
                itemVH.jobDate.setVisibility(View.VISIBLE);
            }
        }

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
        int size = (mJobList == null) ? 1 : mJobList.size() + 1;

        return size;
    }


    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.from_date)
        EditText mFromDate;

        @BindView(R.id.to_date)
        EditText mToDate;

        @BindView(R.id.passenger)
        EditText mPassenger;

        @BindView(R.id.updates)
        EditText mUpdates;

        @BindView(R.id.search_update_layout)
        LinearLayout mSearchUpdateLayout;

        @BindView(R.id.sort_layout)
        LinearLayout mSortLayout;

        @BindView(R.id.radio_group)
        RadioGroup mRadioGroup;

        @BindView(R.id.sort_asc)
        RadioButton sortAsc;

        @BindView(R.id.sort_desc)
        RadioButton sortDesc;

        @BindView(R.id.job_count)
        TextView mJobCount;

        @BindView(R.id.search)
        Button mSearch;

        @BindView(R.id.clear)
        Button mClear;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            myCalendar = Calendar.getInstance();


            mFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDateDialog(mFromDate);
                }
            });


            mToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDateDialog(mToDate);
                }
            });


            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    switch (checkedId) {
                        case R.id.sort_asc:
                            sort = "0";
                            mJobList = mJobListAsc;
                            break;
                        case R.id.sort_desc:
                            sort = "1";
                            mJobList = mJobListDesc;
                            break;
                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCurrentTab.equalsIgnoreCase("HISTORY")) {
                                App.historySearchParams.setSort(sort);

                            }
                            if (mCurrentTab.equalsIgnoreCase("FUTURE")) {
                                App.futureSearchParams.setSort(sort);

                            }
                            notifyDataSetChanged();
                        }
                    });
                }
            });

//            sortAsc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//                    if (checked) {
//                        switch (compoundButton.getId()) {
//                            case R.id.sort_asc:
//                                sort = "0";
//                                mJobList = mJobListAsc;
//                                break;
//
//                            case R.id.sort_desc:
//                                sort = "1";
//                                mJobList = mJobListDesc;
//                                break;
//                        }
//
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                notifyDataSetChanged();
//                            }
//                        });
//                    }
//
//                    if (mCurrentTab.equalsIgnoreCase("HISTORY")) {
//                        App.historySearchParams.setSort(sort);
//                      //  EventBus.getDefault().post(App.historySearchParams);
//                    }
//                    if (mCurrentTab.equalsIgnoreCase("FUTURE")) {
//                        App.futureSearchParams.setSort(sort);
//                     //   EventBus.getDefault().post(App.futureSearchParams);
//                    }
//
//                }
//            });
//
//            sortDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//                    if (checked) {
//                        switch (compoundButton.getId()) {
//                            case R.id.sort_asc:
//                                sort = "0";
//                                mJobList = mJobListAsc;
//                                break;
//
//                            case R.id.sort_desc:
//                                sort = "1";
//                                mJobList = mJobListDesc;
//                                break;
//                        }
//
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                notifyDataSetChanged();
//                            }
//                        });
//                    }
//
//
//                    if (mCurrentTab.equalsIgnoreCase("HISTORY")) {
//                        App.historySearchParams.setSort(sort);
//                      //  EventBus.getDefault().post(App.historySearchParams);
//                    }
//                    if (mCurrentTab.equalsIgnoreCase("FUTURE")) {
//                        App.futureSearchParams.setSort(sort);
//                      //  EventBus.getDefault().post(App.futureSearchParams);
//                    }
//
//
//                }
//            });


            mSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performSearch();
                }
            });

            mClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCurrentTab.equalsIgnoreCase("HISTORY")) {
                        App.historySearchParams = new SearchParams("", "", "", "0", "");
                        EventBus.getDefault().post(App.historySearchParams);
                    }


                    if (mCurrentTab.equalsIgnoreCase("FUTURE")) {
                        App.futureSearchParams = new SearchParams("", "", "", "0", "");
                        EventBus.getDefault().post(App.futureSearchParams);
                    }

                   notifyDataSetChanged();
//                    performSearch();


                    // clear recycler view
//                    mJobList = new ArrayList<>();
//                    notifyDataSetChanged();

                }
            });

        }



        private void performSearch() {
            SearchParams searchParams = new SearchParams(
                    mPassenger.getText().toString().trim(),
                    mFromDate.getText().toString().trim(),
                    mToDate.getText().toString().trim(),
                    sort,
                    mUpdates.getText().toString().trim()
            );

            //  App.mHistorySearch = searchParams;

            if (mCurrentTab.equalsIgnoreCase("HISTORY"))
                App.historySearchParams = searchParams;

            if (mCurrentTab.equalsIgnoreCase("FUTURE"))
                App.futureSearchParams = searchParams;

            EventBus.getDefault().post(searchParams);
        }

        private void showDateDialog(final EditText target) {
            DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {

                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "dd MMM yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    target.setText(sdf.format(myCalendar.getTime()));
                }
            };


            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

            if (mCurrentTab.equalsIgnoreCase("FUTURE"))
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 172800000);

            if (mCurrentTab.equalsIgnoreCase("HISTORY")) {
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                if (target.getTag().toString().equalsIgnoreCase("TO_DATE")) {
                    datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                }
            }

            datePickerDialog.show();

        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
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

        @BindView(R.id.job_date)
        TextView jobDate;

        @BindView(R.id.main_layout)
        LinearLayout parent;

        @BindView(R.id.buttons_layout)
        LinearLayout mButtonLayout;


        @BindView(R.id.view)
        Button mViewUpdates;

        @BindView(R.id.patient_history)
        Button mPatientHistory;


        @BindView(R.id.updateText)
        TextView updateText;

        @BindView(R.id.update_layout)
        LinearLayout updateLayout;

        @BindView(R.id.staff)
        TextView staff;


        public ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


//            updateText.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    //updatesTextArray.set(getAdapterPosition(), "");
//                   // mJobList.get(getAdapterPosition()).setRemark(charSequence.toString());
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//
//                }
//            });

            mViewUpdates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterPosition = getAdapterPosition() - 1;
                    showUpdatesDialog(mJobList.get(adapterPosition).getJobNo(), mJobList.get(adapterPosition).getUpdates());
                }
            });

            mPatientHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterPosition = getAdapterPosition() - 1;
                    Intent intent = new Intent(mContext, PatientHistoryActivity.class);
                    intent.putExtra("CUSTOMER_NAME", mJobList.get(adapterPosition).getCustomerCode().trim());
                    mContext.startActivity(intent);
                }
            });
        }
    }


    private void UpdateLongRemarks(RequestUpdateRemark request){
        Call<JobRes> call = RestClient.COACH().getApiService().UpdateLongRemarks(request);

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    Toast.makeText(mContext, "Successfully Updated", Toast.LENGTH_SHORT).show();

                    // refresh the adapter
                    mJobList.get(adapterPosition).setUpdates(request.getRemarks());
                    notifyDataSetChanged();
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                    RestClient.refreshToken("GET_TODAY_JOBS");
                }
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {

            }
        });
    }



    private void getTodayJobs(final String jobNo, final int index) {
        Call<JobRes> call = RestClient.COACH().getApiService().GetTodayJobs();

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
               assert response.body() != null;
                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    mJobList = (List<Job>) response.body().getJobs();
                    if (mJobList != null)
                        if (mJobList.size() > 0) {
                            App.jobList = mJobList;

                            updateJobList(mJobList, mCurrentTab);
                            notifyDataSetChanged();

                            Intent intent = new Intent(mContext, JobDetailActivity.class);
                            intent.putExtra("jobNo", jobNo);
                            intent.putExtra("index", index);
                            intent.putExtra("currentTab", mCurrentTab);
                            mContext.startActivity(intent);
                        }
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                    RestClient.refreshToken("GET_TODAY_JOBS");
                }
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {

            }
        });


    }

    private void getTomorrowJobs(final String jobNo, final int index) {

        Call<JobRes> call = RestClient.COACH().getApiService().GetTomorrowJobs();

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                assert response.body() != null;
                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {

                    mJobList = (List<Job>) response.body().getJobs();

                    if (mJobList != null)
                        if (mJobList.size() > 0) {
                            App.jobList = mJobList;
                            updateJobList(mJobList, mCurrentTab);
                            notifyDataSetChanged();

                            Intent intent = new Intent(mContext, JobDetailActivity.class);
                            intent.putExtra("jobNo", jobNo);
                            intent.putExtra("index", index);
                            intent.putExtra("currentTab", mCurrentTab);
                            mContext.startActivity(intent);
                        }
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                    RestClient.refreshToken("GET_TOMORROW_JOBS");
                }

            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {

            }
        });


    }

    private void showUpdatesDialog(final String jobno, final String message) {
        final Dialog dialog = new Dialog((Activity) mContext);

        dialog.setContentView(R.layout.dialog_updates);
        dialog.setTitle("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText updates = dialog.findViewById(R.id.updates);

        String msg = (message.equals("##-##")) ? "" : message;
        msg = msg.replaceAll("##-##", "\n");

        updates.setText(msg);

        Button save = dialog.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newRemark = updates.getText().toString().trim();
                newRemark = (newRemark.length() == 0) ? "\n" : newRemark;
                newRemark = newRemark.replaceAll("\n", "##-##");
                newRemark = newRemark.replaceAll("/", "|");
                newRemark = newRemark.replaceAll("\\\\", "|");


                //updateJobRemark(jobno, newRemark);

                RequestUpdateRemark request = new RequestUpdateRemark(jobno, newRemark);
                UpdateLongRemarks(request);

                dialog.hide();
                dialog.dismiss();
                notifyDataSetChanged();
            }
        });

//        complete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callCompletedJob();
//            }
//        });

        // resize dialog
        Rect displayRectangle = new Rect();
        Window window = ((Activity) mContext).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (displayRectangle.width() * 0.85f);

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        // updateJobRemark( mJobList.get(getAdapterPosition()).getJobNo(),  mJobList.get(getAdapterPosition()).getRemark());
    }
}
