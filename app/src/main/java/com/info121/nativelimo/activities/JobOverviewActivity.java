package com.info121.nativelimo.activities;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;
import com.info121.nativelimo.R;
import com.info121.nativelimo.AbstractActivity;
import com.info121.nativelimo.App;
import com.info121.nativelimo.adapters.OverviewPageAdapter;
import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.fragments.JobListFragment;
import com.info121.nativelimo.models.JobCount;
import com.info121.nativelimo.models.JobRes;
import com.info121.nativelimo.services.SmartLocationService;
import com.info121.nativelimo.utils.PrefDB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobOverviewActivity extends AbstractActivity {
    PrefDB prefDB;
    Context mContext = JobOverviewActivity.this;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.tablayout)
    TabLayout mTabLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onResume() {
        super.onResume();
        resetBadgeCounterOfPushMessages();
    }

    private void resetBadgeCounterOfPushMessages() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_overview);

        ButterKnife.bind(this);

        // set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Welcome " + App.userName);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefDB = new PrefDB(getApplicationContext());


//        TabLayout.Tab tab = mTabLayout.getTabAt(0);
//        TextView textView = new TextView(mContext);
//        textView.setText("AAAA");
//        tab.setCustomView(textView);


        // set view pager
        OverviewPageAdapter pageAdapter = new OverviewPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        callJobsCount(false);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                //App.test = i + "";
//                Toast.makeText(mContext, "Index : " + i, Toast.LENGTH_SHORT).show();
//                prefDB.putString(App.CONST_SELECTED_TAB_INDEX, i + "");

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

    }

    private void callJobsCount(final Boolean update) {
        Call<JobRes> call = RestClient.COACH().getApiService().GetJobsCount();

        call.enqueue(new Callback<JobRes>() {
            @Override
            public void onResponse(Call<JobRes> call, Response<JobRes> response) {

                JobCount jobCount = new JobCount();

               try {
                   jobCount = response.body().getJobcountlist().get(0);
               }catch (Exception e){
                   Log.e("call job count #getlist", e.getMessage());
                   return;
               }

                try {
                    App.BadgeCount = Integer.parseInt(jobCount.getTodayjobcount());
                }catch (Exception e){
                    Log.e("call job count #BadgeCount", e.getMessage());
                }

                if (update)
                    updateTabs(response.body().getJobcountlist());
                else
                    initializeTabs(response.body().getJobcountlist());

                //Toast.makeText(mContext, "Update Job List", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JobRes> call, Throwable t) {

            }
        });
    }


    // tab header with text
    private void initializeTabs(List<JobCount> jobCountList) {
        TabLayout.Tab tabitem;
        View v;
        TextView header, badge;

        try {

            JobCount jobCount = jobCountList.get(0);

            //  TabLayout.Tab tabitem = mTabLayout.newTab();

            float scale = getResources().getConfiguration().fontScale;


            tabitem = mTabLayout.getTabAt(0);
            v = View.inflate(mContext, R.layout.tab_header, null);
            header = v.findViewById(R.id.title);
            badge = v.findViewById(R.id.job_count);
            header.setText("TODAY");
            badge.setText(jobCount.getTodayjobcount());
            tabitem.setCustomView(v);

            tabitem = mTabLayout.getTabAt(1);
            v = View.inflate(mContext, R.layout.tab_header, null);
            header = v.findViewById(R.id.title);
            badge = v.findViewById(R.id.job_count);
            header.setText("TMR");
            badge.setText(jobCount.getTomorrowjobcount());
            tabitem.setCustomView(v);

            tabitem = mTabLayout.getTabAt(2);
            v = View.inflate(mContext, R.layout.tab_header, null);
            header = v.findViewById(R.id.title);
            badge = v.findViewById(R.id.job_count);
            header.setText("FUTURE");
            badge.setText(jobCount.getFuturejobcount());
            tabitem.setCustomView(v);


            tabitem = mTabLayout.getTabAt(3);
            v = View.inflate(mContext, R.layout.tab_header, null);
            header = v.findViewById(R.id.title);
            badge = v.findViewById(R.id.job_count);
            header.setText("HISTORY");
            badge.setVisibility(View.GONE);
            badge.setText("0");
            tabitem.setCustomView(v);
        }catch(Exception e){
            return;
        }
    }

    // tab header with graphic
//    private void initializeTabs(List<JobCount> jobCountList) {
//        TabLayout.Tab tabitem;
//        View v;
//        ImageView header;
//        TextView badge;
//
//        try {
//
//            JobCount jobCount = jobCountList.get(0);
//
//            //  TabLayout.Tab tabitem = mTabLayout.newTab();
//
//            float scale = getResources().getConfiguration().fontScale;
//
//            tabitem = mTabLayout.getTabAt(0);
//            v = View.inflate(mContext, R.layout.tab_header_graphic, null);
//             header = v.findViewById(R.id.title);
//            badge = v.findViewById(R.id.job_count);
//            header.setImageResource(R.mipmap.tab_today);
//            badge.setText(jobCount.getTodayjobcount());
//            tabitem.setCustomView(v);
//
//            tabitem = mTabLayout.getTabAt(1);
//            v = View.inflate(mContext, R.layout.tab_header_graphic, null);
//            header = v.findViewById(R.id.title);
//            badge = v.findViewById(R.id.job_count);
//            header.setImageResource(R.mipmap.tab_tomorrow);
//            badge.setText(jobCount.getTomorrowjobcount());
//            tabitem.setCustomView(v);
//
//            tabitem = mTabLayout.getTabAt(2);
//            v = View.inflate(mContext, R.layout.tab_header_graphic, null);
//            header = v.findViewById(R.id.title);
//            badge = v.findViewById(R.id.job_count);
//            header.setImageResource(R.mipmap.tab_future);
//            badge.setText(jobCount.getFuturejobcount());
//            tabitem.setCustomView(v);
//
//
//            tabitem = mTabLayout.getTabAt(3);
//            v = View.inflate(mContext, R.layout.tab_header_graphic, null);
//            header = v.findViewById(R.id.title);
//            badge = v.findViewById(R.id.job_count);
//            header.setImageResource(R.mipmap.tab_history);
//            badge.setVisibility(View.GONE);
//            badge.setText("0");
//            tabitem.setCustomView(v);
//
//        }catch(Exception e){
//            return;
//        }
//    }

    private void updateTabs(List<JobCount> jobCountList) {
        TabLayout.Tab tabitem;
        View v;
        TextView header, badge;

        if (jobCountList != null) {

            try {
                JobCount jobCount = jobCountList.get(0);

                tabitem = mTabLayout.getTabAt(0);
                badge = tabitem.getCustomView().findViewById(R.id.job_count);
                badge.setText(jobCount.getTodayjobcount());

                tabitem = mTabLayout.getTabAt(1);
                badge = tabitem.getCustomView().findViewById(R.id.job_count);
                badge.setText(jobCount.getTomorrowjobcount());

                tabitem = mTabLayout.getTabAt(2);
                badge = tabitem.getCustomView().findViewById(R.id.job_count);
                badge.setText(jobCount.getFuturejobcount());

                tabitem = mTabLayout.getTabAt(3);
                badge = tabitem.getCustomView().findViewById(R.id.job_count);
                badge.setText("0");

            }catch (NullPointerException e){
                Log.e("NullPointerException : ", e.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//
//
//        menu.add(Menu.NONE, 0, Menu.NONE, "Prominent Tone")
//                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Intent intent = new Intent(mContext, ToneSelection.class);
//                        intent.putExtra(ToneSelection.TONE_TYPE, "PROMINENT");
//                        startActivity(intent);
//                        return true;
//                    }
//                })
//                .setIcon(ContextCompat.getDrawable(mContext, R.mipmap.ic_settings_white_24dp))
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(Menu.NONE, 1, Menu.NONE, "Notification Tone")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(mContext, ToneSelection.class);
                        intent.putExtra(ToneSelection.TONE_TYPE, "NOTIFICATION");
                        startActivity(intent);
                        return true;
                    }
                })
                .setIcon(ContextCompat.getDrawable(mContext, R.mipmap.ic_settings_white_24dp))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(Menu.NONE, 2, Menu.NONE, "Change Password")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        startActivity(new Intent(JobOverviewActivity.this, ChangePasswordActivity.class));
                        return true;
                    }
                })
                .setIcon(ContextCompat.getDrawable(mContext, R.mipmap.ic_settings_white_24dp))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(Menu.NONE, 3, Menu.NONE, "Logout")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        finish();
                        //prefDB.remove(App.CONST_ALREADY_LOGIN);
                        stopLocationService();
                        return true;
                    }
                })
                .setIcon(ContextCompat.getDrawable(mContext, R.mipmap.ic_exit_to_app_white_24dp))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    private void showSettingSelectDialog() {
        Button mNoti, mProminent;

        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat);
        dialog.setContentView(R.layout.dialog_setting_selection);
        dialog.setTitle("App Selection");

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);


        //adding dialog animation sliding up and down
        // dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        // to cancel when outside touch
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_Fade;

        // window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        mNoti = (Button) dialog.findViewById(R.id.btn_notification);
        mProminent = (Button) dialog.findViewById(R.id.btn_prominent);

        mProminent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(mContext, ToneSelection.class);
                intent.putExtra(ToneSelection.TONE_TYPE, "PROMINENT");
                startActivity(intent);
            }
        });

        mNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(mContext, ToneSelection.class);
                intent.putExtra(ToneSelection.TONE_TYPE, "NOTIFICATION");
                startActivity(intent);
            }
        });


        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle Action bar item clicks here. The Action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            if(!LoginActivity.active){
                LoginActivity.autoLogin = false;
                startActivity(new Intent(mContext, LoginActivity.class));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Subscribe(sticky = true)
    public void onEvent(String event) {
        if(event.equalsIgnoreCase("UPDATE_JOB_COUNT")){
            callJobsCount(true);
            EventBus.getDefault().removeStickyEvent("UPDATE_JOB_COUNT");
        }
    }


    private void stopLocationService(){
        Intent intent = new Intent(mContext, SmartLocationService.class);
        mContext.stopService(intent);
    }
}
