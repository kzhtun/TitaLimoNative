package com.info121.nativelimo.adapters;


import android.content.Context;

import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;

import android.net.Uri;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.info121.nativelimo.R;
import com.info121.nativelimo.App;

import com.info121.nativelimo.api.RestClient;
import com.info121.nativelimo.models.ObjectRes;
import com.info121.nativelimo.models.RequestUpdateChannelID;

import com.info121.nativelimo.models.Song;
import com.info121.nativelimo.utils.PrefDB;



import java.util.Date;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KZHTUN on 1/30/2018.
 */


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    String OLD_CH = "";
    String NEW_CH = "";

    List<Song> mSongs;

    Context mContext;
    int lastIndex = -1;
    PrefDB prefDB = null;

    String MODE = "";

    MediaPlayer mediaPlayer;

    public SongAdapter(List<Song> songs, String mode) {
        this.mSongs = songs;
        this.MODE = mode;

        mediaPlayer  = new MediaPlayer();
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View v = layoutInflater.inflate(R.layout.card_song, parent, false);

        prefDB = new PrefDB(mContext);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.songName.setText(mSongs.get(i).getSongName());
        holder.path.setText(mSongs.get(i).getSongUri().toString());

        holder.select.setChecked(lastIndex == i);
    }


    public void dismiss(){
        mediaPlayer.stop();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        Song song;

        Ringtone r;


        TextView songName, path;
        RadioButton select;

        LinearLayout mainLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            songName = (TextView) itemView.findViewById(R.id.song_title);
            path = (TextView) itemView.findViewById(R.id.song_path);
            select = (RadioButton) itemView.findViewById(R.id.select);
            mainLayout = itemView.findViewById(R.id.select_layout);

            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastIndex = getAdapterPosition();
                    notifyDataSetChanged();

//                    if(MODE.equalsIgnoreCase("PROMINENT")){
//                        prefDB.putString(App.CONST_PROMINENT_TONE, mSongs.get(lastIndex).getData());
//                    }
//
//                    if(MODE.equalsIgnoreCase("NOTIFICATION")){
//                        prefDB.putString(App.CONST_NOTIFICATION_TONE, mSongs.get(lastIndex).getData());
//                    }
               //     Toast.makeText(mContext, mSongs.get(lastIndex).getSongName(), Toast.LENGTH_SHORT).show();

                   // song = mSongs.get(getAdapterPosition());

                    Date now = new Date();
                    App.N_CHANNEL =  Long.toString(now.getTime());
                    Log.e("Channel ID", App.N_CHANNEL);
                    Toast.makeText(mContext, "Channel ID : " +   App.N_CHANNEL , Toast.LENGTH_SHORT).show();




//                    if(MODE.equalsIgnoreCase("PROMINENT")){
//                        prefDB.putString(App.CONST_PROMINENT_TONE,  mSongs.get(lastIndex).getSongUri().toString());
//                        App.setupNotificationChannels(mContext,
//                                App.P_CHANNEL,
//                                mSongs.get(lastIndex).getSongUri()
//                                );
//                    }

                    if(MODE.equalsIgnoreCase("NOTIFICATION")){
                        prefDB.putString(App.CONST_NOTIFICATION_TONE, mSongs.get(lastIndex).getSongUri().toString());
                        callUpdateChannelID();
                    }

//                    Date now = new Date();
//                    long CHANNEL_ID = now.getTime();
//
//                    prefDB.putString("OLD_CH_ID", prefDB.getString("NEW_CH_ID"));
//                    prefDB.putString("NEW_CH_ID", CHANNEL_ID + "");
//
//                    prefDB.putString("OLD_CH_ID_P", prefDB.getString("NEW_CH_ID_P"));
//                    prefDB.putString("NEW_CH_ID_P", CHANNEL_ID + "_P");



                }
            });

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    song = mSongs.get(getAdapterPosition());
                    playSong(song.getSongUri());
                }
            });
        }
    }


    private void callUpdateChannelID(){
        Call<ObjectRes> call = RestClient.COACH().getApiService().UpdateChannelID(
                new RequestUpdateChannelID(App.N_CHANNEL));

        call.enqueue(new Callback<ObjectRes>() {
            @Override
            public void onResponse(Call<ObjectRes> call, Response<ObjectRes> response) {
                App.setupNotificationChannels(mContext,
                        App.N_CHANNEL,
                        mSongs.get(lastIndex).getSongUri()
                );

                prefDB.putString(App.CONST_NOTIFICATION_CHANNEL_ID, App.N_CHANNEL);
                Toast.makeText(mContext, "Set notification tone (" + mSongs.get(lastIndex).getSongName() + ")" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ObjectRes> call, Throwable t) {
                Toast.makeText(mContext, "Set notification tone error!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void playSong(Uri ringtone) {
        long count = 1;
       //Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone notification = RingtoneManager.getRingtone(mContext, ringtone);
        //Ringtone notification = RingtoneManager.getRingtone(mContext.getApplicationContext(), songUri);

        // If phone is not set to silent mode
        if (notification != null) {
            for (long i = 0; i < count; ++i) {
                notification.play();
                long timeout = 5000;
                while (notification.isPlaying() && (timeout > 0)) {
                    timeout = timeout - 100;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }


    public void playAudio(String path){
        //set up MediaPlayer
        mediaPlayer.stop();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path );
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
