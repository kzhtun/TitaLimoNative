package com.info121.nativelimo.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.info121.nativelimo.AbstractFragment;
import com.info121.nativelimo.App;
import com.info121.nativelimo.R;
import com.info121.nativelimo.adapters.JobsAdapter;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.Job;
import com.info121.nativelimo.models.JobRes;
import com.info121.nativelimo.models.SearchParams;
import com.info121.nativelimo.utils.Util;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@linkFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FutureHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FutureHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FutureHistoryFragment extends AbstractFragment {

    List<Job> mJobList;

    @BindView(R.id.no_data)
    TextView mNoData;

    @BindView(R.id.rv_jobs)
    RecyclerView mRecyclerView;

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout mSwipeLayout;

    @BindView(R.id.from_date)
    EditText mFromDate;

    @BindView(R.id.to_date)
    EditText mToDate;

    @BindView(R.id.passenger)
    EditText mPassenger;


    @BindView(R.id.sort_layout)
    LinearLayout mSortLayout;


    @BindView(R.id.sort_asc)
    RadioButton sortAsc;

    @BindView(R.id.job_count)
    TextView mJobCount;

    Context mContext = getActivity();

    JobsAdapter jobsAdapter;

    SearchParams mSearchParams;

    String mCurrentTab = "";
    Calendar myCalendar;
    String sort = "0";


    public static FutureHistoryFragment newInstance(String param1) {
        FutureHistoryFragment fragment = new FutureHistoryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);

        fragment.mCurrentTab = param1;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @OnCheckedChanged({R.id.sort_asc, R.id.sort_desc})
    public void onRadioButtonCheckChanged(CompoundButton button, boolean checked) {
        if (checked) {
            switch (button.getId()) {
                case R.id.sort_asc:
                    sort = "0";
                    break;
                case R.id.sort_desc:
                    sort = "1";
                    break;
            }
        }
    }

    @OnFocusChange(R.id.from_date)
    public void fromDateOnFocusChange(boolean focused) {
        if (focused) showDateDialog(mFromDate);
    }

    @OnFocusChange(R.id.to_date)
    public void toDateOnFocusChange(boolean focused) {
        if (focused) showDateDialog(mToDate);
    }

    @OnClick(R.id.from_date)
    public void fromDateOnClick() {
        showDateDialog(mFromDate);
    }

    @OnClick(R.id.to_date)
    public void toDateOnClick() {
        showDateDialog(mToDate);
    }

//    @OnClick(R.id.date)
//    public void dateOnClick() {
//        showDateDialog();
//    }

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


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        if (mCurrentTab.equalsIgnoreCase("FUTURE"))
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 172800000);

        if (mCurrentTab.equalsIgnoreCase("HISTORY")) {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

           if(target.getTag().toString().equalsIgnoreCase("TO_DATE")){
               datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
           }
        }

        datePickerDialog.show();

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        mPassenger.setText("");
//        mDate.setText("");
//
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

      //  searchOnClick();

//        if (mCurrentTab.equalsIgnoreCase("HISTORY")) {
//
//            myCalendar = Calendar.getInstance();
//
//            String myFormat = "dd MMM yyyy"; //In which you need put here
//            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//
//            mFromDate.setText(sdf.format(myCalendar.getTime()));
//            mToDate.setText(sdf.format(myCalendar.getTime()));
//        }


        mSwipeLayout.setEnabled(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_future_history, container, false);

        ButterKnife.bind(this, view);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchOnClick();
            }
        });

        myCalendar = Calendar.getInstance();

        if (mCurrentTab.equalsIgnoreCase("HISTORY"))
            mSortLayout.setVisibility(View.INVISIBLE);
        else
            mSortLayout.setVisibility(View.VISIBLE);


        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        jobsAdapter = new JobsAdapter(mContext, mJobList);
        mRecyclerView.setAdapter(jobsAdapter);

        mRecyclerView.setNestedScrollingEnabled(true);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchOnClick();
            }
        });

        sortAsc.setChecked(true);

        // Inflate the layout for this fragment
        return view;
    }


    @OnClick(R.id.search)
    public void searchOnClick() {

    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        //mSearchParams = new SearchParams("","", "", "0", "");

        if (menuVisible) {
            switch (mCurrentTab) {
                case "FUTURE": {
                    getFutureJobs(App.futureSearchParams);
                }
                break;
                case "HISTORY": {
                    getHistoryJobs(App.historySearchParams);
                }
                break;
            }
        }
    }

    private void getFutureJobs(SearchParams params) {

//        if (mPassenger.getText().length() == 0)
//            customer = " ";
//        else
//            customer = mPassenger.getText().toString();
//
//        if (mFromDate.getText().length() == 0)
//            mFromDate.setText(" ");
//
//        if (mToDate.getText().length() == 0)
//            mToDate.setText(" ");



        Call<JobRes> call = RestClient.COACH().getApiService().GetFutureJobs(
                params.getFromDate(),
                params.getToDate(),
                Util.replaceEscapeChr(params.getCustomer()),
                params.getSort()
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    mSwipeLayout.setRefreshing(false);
                    mJobList = (List<Job>) response.body().getJobs();

                    mJobCount.setText(String.valueOf(mJobList.size()));

                    if (mJobList.size() > 0)
                        mNoData.setVisibility(View.GONE);
                    else
                        mNoData.setVisibility(View.VISIBLE);

                    // data refresh
                    jobsAdapter.updateJobList(mJobList, mCurrentTab);
                    mRecyclerView.getAdapter().notifyDataSetChanged();

                }

                if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                    RestClient.refreshToken("GET_FUTURE_JOBS");
                }

            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {

            }
        });


    }

    private void getHistoryJobs(SearchParams params) {
//        String customer = " ";
//
//        if (mPassenger.getText().length() == 0)
//            customer = " ";
//        else
//            customer = mPassenger.getText().toString();
//
//        if (mFromDate.getText().length() == 0)
//            mFromDate.setText(" ");
//
//        if (mToDate.getText().length() == 0)
//            mToDate.setText(" ");

        Log.e("Search Params : ", params.getSort());

        Call<JobRes> call = RestClient.COACH().getApiService().GetHistoryJobs(
                params.getFromDate(),
                params.getToDate(),
                Util.replaceEscapeChr(params.getCustomer()),
                params.getUpdates(),
                params.getSort()
        );

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {

                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    mSwipeLayout.setRefreshing(false);
                    mJobList = (List<Job>) response.body().getJobs();

                    mJobCount.setText(String.valueOf(mJobList.size()));

                    if (mJobList.size() > 0)
                        mNoData.setVisibility(View.GONE);
                    else
                        mNoData.setVisibility(View.VISIBLE);

                    // data refresh
                    jobsAdapter.updateJobList(mJobList, mCurrentTab);
                    //mRecyclerView.getAdapter().notifyDataSetChanged();
                }

                if (response.body().getResponsemessage().equalsIgnoreCase("BAD TOKEN")) {
                    RestClient.refreshToken("GET_HISTORY_JOBS");
                }

            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {

            }
        });


    }

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Subscribe(sticky = false)
    public void onEvent(SearchParams params) {
        //mSearchParams = params;
        mSwipeLayout.setRefreshing(true);

        switch (mCurrentTab) {
            case "FUTURE": {
                getFutureJobs(params);
            }
            break;
            case "HISTORY": {
                getHistoryJobs(params);
            }
            break;
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(String action) {
        if (action.equalsIgnoreCase("GET_FUTURE_JOBS")) {
            getFutureJobs(App.futureSearchParams);
        }

        if (action.equalsIgnoreCase("GET_HISTORY_JOBS")) {
            getHistoryJobs(App.historySearchParams);
        }
    }


}
