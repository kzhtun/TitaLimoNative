package com.info121.nativelimo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.info121.nativelimo.R;
import com.info121.nativelimo.adapters.SongAdapter;
import com.info121.nativelimo.models.Song;
import com.info121.nativelimo.utils.PrefDB;
import com.info121.nativelimo.utils.SongLoader;

import java.util.List;

public class ToneSelection extends AppCompatActivity {

    WebView mWebView;
    Toolbar mToolbar;

    PrefDB prefDB = null;
    ProgressBar mProgressBar;
    RecyclerView mRecyclerView;

    Context mContext;
    List<Song> mSongList;
    String MODE;

    public static final String TONE_TYPE = "TONE_TYPE";

    SongAdapter songAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tone_selection);

        initializeControls();

        SongLoader.getSongListFromLocal(ToneSelection.this);
        mSongList = SongLoader.getSongList();
        songAdapter = new SongAdapter(mSongList, MODE);

        populateSongs();

    }

    @Override
    protected void onPause() {
        super.onPause();
        songAdapter.dismiss();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        songAdapter.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle Action bar item clicks here. The Action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            songAdapter.dismiss();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void initializeControls() {

        prefDB = new PrefDB(getApplicationContext());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        MODE = intent.getStringExtra(TONE_TYPE);

        if(MODE.equalsIgnoreCase("PROMINENT")){
            mToolbar.setTitle("Select Prominent Job Tone");
        }

        if(MODE.equalsIgnoreCase("NOTIFICATION")){
            mToolbar.setTitle("Select Notification Tone");
        }


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.song_list);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);


    }


    private void populateSongs() {

        // Initialize Recycler View
        mRecyclerView = (RecyclerView) findViewById(R.id.song_list);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ToneSelection.this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(songAdapter);

        mProgressBar.setVisibility(View.GONE);
    }

}
