package com.info121.nativelimo.adapters;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.info121.nativelimo.fragments.FutureHistoryFragment;
import com.info121.nativelimo.fragments.JobListFragment;

public class OverviewPageAdapter extends FragmentStatePagerAdapter {

    public OverviewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: return JobListFragment.newInstance("TODAY");
            case 1: return JobListFragment.newInstance("TOMORROW");
            case 2: return FutureHistoryFragment.newInstance("FUTURE");
            case 3: return FutureHistoryFragment.newInstance("HISTORY");
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Log.e("PageTitle", position + "");

        switch (position){
            case 0: return "TODAY";
            case 1: return "TOMORROW";
            case 2: return "FUTURE";
            case 3: return "HISTORY";
            default: return null;
        }


    }
}
