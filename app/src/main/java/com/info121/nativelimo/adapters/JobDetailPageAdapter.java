package com.info121.nativelimo.adapters;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.info121.nativelimo.fragments.JobDetailFragment;
import com.info121.nativelimo.models.Job;

import java.util.ArrayList;
import java.util.List;

public class JobDetailPageAdapter extends FragmentStatePagerAdapter {

    List<Job> mJobList = new ArrayList<>();
    String mCurrentTab = "";
    int mIndex;


    public JobDetailPageAdapter(FragmentManager fm, List<Job> jobList , String currentTab, int index) {
        super(fm);
        mJobList = jobList;
        mCurrentTab = currentTab;
        mIndex = index;
    }

    @Override
    public Fragment getItem(int i) {

        return JobDetailFragment.newInstance(mJobList.get(i), mCurrentTab, mIndex);
    }

    @Override
    public int getCount() {
        return mJobList.size();
    }



}
