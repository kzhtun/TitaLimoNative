package com.info121.nativelimo.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.info121.nativelimo.R;
import com.info121.nativelimo.AbstractFragment;
import com.info121.nativelimo.adapters.JobsAdapter;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.Action;
import com.info121.nativelimo.models.Job;
import com.info121.nativelimo.models.JobRes;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link JobListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link JobListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobListFragment extends AbstractFragment {

    List<Job> mJobList = new ArrayList<>();

    @BindView(R.id.list_date)
    TextView mListDate;

    @BindView(R.id.no_data)
    TextView mNoData;

    @BindView(R.id.rv_jobs)
    RecyclerView mRecyclerView;

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout mSwipeLayout;


    JobsAdapter jobsAdapter;

    Context mContext = getActivity();

    String mCurrentTab = "";

    String todayDate, tomorrowDate;


    public static JobListFragment newInstance(String param1) {
        JobListFragment fragment = new JobListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);

        fragment.mCurrentTab = param1;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onResume() {
        super.onResume();

        getRelatedTabData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //getRelatedTabData();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            //getRelatedTabData();
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            getRelatedTabData();
        }
    }

    private void getRelatedTabData() {
        switch (mCurrentTab) {
            case "TODAY":
                getTodayJobs();
                break;

            case "TOMORROW":
                getTomorrowJobs();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_job_list, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        jobsAdapter = new JobsAdapter(mContext, mJobList);
        mRecyclerView.setAdapter(jobsAdapter);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRelatedTabData();
            }
        });


        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd  MMM  yyyy");
        todayDate = df.format(c.getTime());

        c.add(Calendar.DATE, 1);
        tomorrowDate = df.format(c.getTime());


        // Inflate the layout for this fragment
        return view;
    }


    private void getTodayJobs() {


        Call<JobRes> call = RestClient.COACH().getApiService().GetTodayJobs();

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {

                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    mSwipeLayout.setRefreshing(false);
                    mJobList = (List<Job>) response.body().getJobs();

                    mListDate.setText(todayDate);

                    if (mJobList != null)
                        if (mJobList.size() > 0)
                            mNoData.setVisibility(View.GONE);
                        else
                            mNoData.setVisibility(View.VISIBLE);

                    // data refresh
                    jobsAdapter.updateJobList(mJobList, mCurrentTab);
                    mRecyclerView.getAdapter().notifyDataSetChanged();
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

    private void getTomorrowJobs() {

        Call<JobRes> call = RestClient.COACH().getApiService().GetTomorrowJobs();

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {
                if (response.body().getResponsemessage().equalsIgnoreCase("Success")) {
                    mSwipeLayout.setRefreshing(false);
                    mJobList = (List<Job>) response.body().getJobs();

                    mListDate.setText(tomorrowDate);

                    if (mJobList != null)
                        if (mJobList.size() > 0)
                            mNoData.setVisibility(View.GONE);
                        else
                            mNoData.setVisibility(View.VISIBLE);

                    // data refresh
                    jobsAdapter.updateJobList(mJobList, mCurrentTab);
                    mRecyclerView.getAdapter().notifyDataSetChanged();
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
    public void onEvent(Action action) {
        //  Toast.makeText(getContext(), "Action Done", Toast.LENGTH_SHORT).show();
        getRelatedTabData();
    }

    @Subscribe(sticky = true)
    public void onEvent(String action) {
        if (action.equalsIgnoreCase("GET_TODAY_JOBS")) {
            getTodayJobs();
        }

        if (action.equalsIgnoreCase("GET_TOMORROW_JOBS")) {
            getTomorrowJobs();
        }
    }

}
