package com.info121.nativelimo.models;

/**
 * Created by KZHTUN on 1/29/2018.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.Comparator;

public class Song {

    public static final Comparator<Song> songNameComparator
            = new Comparator<Song>() {

        public int compare(Song song1, Song song2) {

            String songName1 = song1.getSongName();
            String songName2 = song2.getSongName();

            //ascending order
            return songName1.compareToIgnoreCase(songName2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };
    public static final Comparator<String> songNameStrComparator
            = new Comparator<String>() {

        public int compare(String song1, String song2) {


            //ascending order
            return song1.compareToIgnoreCase(song2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };
    public static Comparator<Song> songIdComparator = new Comparator<Song>() {
        @Override
        public int compare(Song song1, Song song2) {
            return song1.getSongId().compareTo(song2.getSongId());
        }
    };

    private long songId;
    private long albumId;
    private String songName;
    private String artist;
    private String album;
    private String data;
    private String albumKey;
    private Uri songUri;
    private String albumName;
    private String artistName;
    private int duration;
    private int trackNumber;
    private long artistId;

    public Song() {

    }

    public Song(long songId) {
        this.songId = songId;
    }

    public Song(long songId, long albumId, String songName, String artist,
                String album, String data, String albumKey, Uri songUri,
                String albumName, String artistName, int duration, int trackNumber, long artistId) {
        this.songId = songId;
        this.albumId = albumId;
        this.songName = songName;
        this.artist = artist;
        this.album = album;
        this.data = data;
        this.albumKey = albumKey;
        this.songUri = songUri;
        this.albumName = albumName;
        this.artistName = artistName;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.artistId = artistId;
    }

    public Song(long songId, long albumId, long artistId, String songName, String artistName, String albumName, int duration, int trackNumber) {
        this.songId = songId;
        this.albumId = albumId;
        this.artistId = artistId;
        this.songName = songName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.duration = duration;
        this.trackNumber = trackNumber;
    }

    public Song(long songId, long albumId, long artistId, String songName, String artistName, String albumName, int duration, int trackNumber, String data, String albumKey, Uri songUri) {
        this.songId = songId;
        this.albumId = albumId;
        this.artistId = artistId;
        this.songName = songName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.duration = duration;
        this.trackNumber = trackNumber;
        this.data = data;
        this.albumKey = albumKey;
        this.songUri = songUri;
    }

    public Long getSongId() {
        return Long.valueOf(songId);
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlbumKey() {
        return albumKey;
    }

    public void setAlbumKey(String albumKey) {
        this.albumKey = albumKey;
    }

    public Uri getSongUri() {
        return songUri;
    }

    public void setSongUri(Uri songUri) {
        this.songUri = songUri;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public Bitmap getArtworkBitmap(Context ctx) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), songUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getSongId() == null) ? 0 : getSongId().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Song other = (Song) obj;
        if (getSongId() == null) {
            if (other.getSongId() != null)
                return false;
        } else if (!getSongId().equals(other.getSongId()))
            return false;
        return true;
    }
}