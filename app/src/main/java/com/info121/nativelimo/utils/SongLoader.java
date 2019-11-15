package com.info121.nativelimo.utils;

/**
 * Created by KZHTUN on 1/29/2018.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.info121.nativelimo.App;
import com.info121.nativelimo.models.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SongLoader {

    private static final String TAG = SongLoader.class.getSimpleName();

    private static List<Song> songList = new ArrayList<>();

    public static List<Song> getSongList() {

        Collections.sort(songList, Song.songNameComparator);

        return songList;
    }

    public static void setSongList(List<Song> songList) {
        Collections.sort(songList, Song.songNameComparator);
        SongLoader.songList = songList;
    }

    public static void getSongListFromLocal(Activity activity) {
     //   if (PermissionHelper.checkPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            SongLoader songLoader = new SongLoader();
            List<Song> getAllSongs = songLoader.getAllSongs(activity.getApplicationContext());
            Collections.sort(getAllSongs, Song.songNameComparator);
            setSongList(getAllSongs);
     //   }
    }

    public static void getSongListFromLocal(Context context) {
     //  if (PermissionHelper.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            SongLoader songLoader = new SongLoader();
            List<Song> getAllSongs = songLoader.getAllSongs(context);
            Collections.sort(getAllSongs, Song.songNameComparator);
            setSongList(getAllSongs);
     //  }
    }

    public static Song getSongForID(Context context, long id) {
        SongLoader songLoader = new SongLoader();
        String selectionStatement = "is_music=1 AND title != ''";
        Cursor cursor = songLoader.makeSongCursor(context, selectionStatement, MediaStore.Audio.Media._ID + " = " + String.valueOf(id), null);
        Song songForCursor = songLoader.getSongForCursor(cursor);
        return songForCursor;
    }

    public static boolean deleteSong(ContentResolver resolver, Song song) {
        resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + "=" + song.getSongId(), null);
        File file = new File(song.getSongUri().toString());
        boolean deleted = file.delete();
        return deleted;
    }

    private List<Song> getSongsForCursor(Cursor cursor) {

        List<Song> songList = new ArrayList<Song>();
        //iterate over results if valid
        if (cursor != null && cursor.moveToFirst()) {

            //add songs to list
            do {
                long id = cursor.getLong(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                int duration = cursor.getInt(4);
                int trackNumber = cursor.getInt(5);
                long artistId = cursor.getInt(6);
                long albumId = cursor.getLong(7);
                String data = cursor.getString(8);
                String albumKey = cursor.getString(9);

                Uri albumArtUri = getAlbumArtUri(albumId);

                songList.add(new Song(id, albumId, title, artist,
                        album, data, albumKey, albumArtUri,
                        album, artist, duration, trackNumber, artistId));
            }
            while (cursor.moveToNext());
            cursor.close();
        }

        return songList;
    }

//    private List<AlbumModel> getAlbumsForCursor(Cursor cursor) {
//        List<AlbumModel> albumList = new ArrayList<>();
//        Map<Long, AlbumModel> albumModelMap = new HashMap<>();
//        try {
//            if (null != cursor && cursor.moveToFirst()) {
//                do {
//                    long albumRowId = cursor.getLong(0);
//                    long albumId = cursor.getLong(1);
//                    String albumName = cursor.getString(2);
//                    String artistName = cursor.getString(3);
//
//                    Uri albumArtUri = getAlbumArtUri(albumId);
//
//                    if (null == albumModelMap.get(albumId)) {
//                        albumModelMap.put(albumId, new AlbumModel(albumRowId, albumId, albumName, artistName, albumArtUri));
//                    }
//
//                } while (cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
//        } finally {
//            cursor.close();
//        }
//
//        albumList.addAll(albumModelMap.values());
//
//        return albumList;
//    }

    private Uri getAlbumArtUri(long albumId) {
        Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, albumId);
        return albumArtUri;
    }

    private List<Song> getAllSongs(Context context) {
        SongLoader songLoader = new SongLoader();
        String selectionStatement = "is_music=1 AND title != ''";
        Cursor cursor = songLoader.makeSongCursor(context, selectionStatement, null, null);
        List<Song> songsForCursor = songLoader.getSongsForCursor(cursor);
        return songsForCursor;
    }

    private Cursor makeSongCursor(Context context, String selectionStatement, String selection, String[] paramArrayOfString) {
        final String songSortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        return makeSongCursor(context, selectionStatement, selection, paramArrayOfString, songSortOrder);
    }

    private Cursor makeSongCursor(Context context, String selectionStatement, String selection, String[] paramArrayOfString, String sortOrder) {

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = selectionStatement + " AND " + selection;
        }
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                App.SONG_PROJECTION,
                selectionStatement, null, sortOrder);
    }

    private Cursor getSongCursor(Context context, String selection, String[] selectionArgs, String sortOrder) {
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , App.SONG_PROJECTION, selection, selectionArgs, sortOrder);
    }

//    private Cursor getAlbumCursor(Context context, String selection, String[] selectionArgs, String sortOrder) {
//        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//                , App.ALBUM_PROJECTION, selection, selectionArgs, sortOrder);
//    }

    private Song getSongForCursor(Cursor cursor) {
        Song song = new Song();
        if ((cursor != null) && (cursor.moveToFirst())) {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            String artist = cursor.getString(2);
            String album = cursor.getString(3);
            int duration = cursor.getInt(4);
            int trackNumber = cursor.getInt(5);
            long artistId = cursor.getInt(6);
            long albumId = cursor.getLong(7);
            String data = cursor.getString(8);
            String albumKey = cursor.getString(9);

            Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, albumId);


            song = new Song(id, albumId, title, artist,
                    album, data, albumKey, albumArtUri,
                    album, artist, duration, trackNumber, artistId);
        }

        if (cursor != null)
            cursor.close();
        return song;
    }

    /**
     * Searching song using inputted search song.
     *
     * @param context      Context
     * @param searchString searchString
     * @return List<Song>
     */
    public List<Song> searchAllSongs(Context context, String searchString) {

        String selection = "(is_music=1 AND title != '') AND (title LIKE ? OR artist LIKE ? OR  album LIKE ?)";
        String[] selectionArgs = new String[]{"%" + searchString + "%", "%" + searchString + "%", "%" + searchString + "%"};

        List<Song> result = getSongsForCursor(getSongCursor(context, selection, selectionArgs, null));
        Collections.sort(result, Song.songNameComparator);

        return result;
    }

    /**
     * Get song by using id.
     *
     * @param context Context
     * @param songId  songId
     * @return Song
     */
    public Song getSongById(Context context, long songId) {
        String selection = MediaStore.Audio.Media._ID + "= ? ";
        String[] selectionArgs = new String[]{String.valueOf(songId)};

        List<Song> result = getSongsForCursor(getSongCursor(context, selection, selectionArgs, null));
        if (null != result && result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    /**
     * Get number of track according to album.
     *
     * @param context Song
     * @param albumId albumId
     * @return number of track related with albumid.
     */
    public int getNoTrackByAlbumId(Context context, long albumId) {
        String selection = MediaStore.Audio.Media.ALBUM_ID + "= ? ";
        String[] selectionArgs = new String[]{String.valueOf(albumId)};

        List<Song> result = getSongsForCursor(getSongCursor(context, selection, selectionArgs, null));

        return result.size();
    }

    /**
     * Getting song list by using albumId.
     *
     * @param context   Context
     * @param albumId   albumId
     * @param sortOrder sortOrder
     * @return List<Song>
     */
    public List<Song> getSongByAlbumId(Context context, long albumId, String sortOrder) {
        String selection = "is_music=1 AND " + MediaStore.Audio.Media.ALBUM_ID + "= ? ";
        String[] selectionArgs = new String[]{String.valueOf(albumId)};

        List<Song> result = getSongsForCursor(getSongCursor(context, selection, selectionArgs, sortOrder));

        return result;
    }

    /**
     * Getting all music album.
     *
     * @param context Context
     * @return List<AlbumModel>
     */
//    public List<AlbumModel> getAllAlbums(Context context) {
//        String selectionStatement = "is_music=1";
//        List<AlbumModel> result = getAlbumsForCursor(getAlbumCursor(context, selectionStatement, null, null));
//        return result;
//    }

    /**
     * Checking song is existed or not.
     *
     * @param context Context
     * @param song    song
     * @return true if song is exist / false if song is not exist.
     */
    public boolean isSongValid(Context context, Song song) {
        boolean flag = false;

        if (null == song) {
            flag = false;
        } else {
            try {
                Song checkSong = getSongById(context.getApplicationContext(), song.getSongId());
                if (checkSong == null) {
                    flag = false;
                } else {
                    flag = !(checkSong.getSongId() == 0 && TextUtils.isEmpty(song.getData()) && TextUtils.isEmpty(song.getAlbum()));
                }
            } catch (Exception exception) {
                flag = false;
                Log.e(TAG, exception.getMessage(), exception);
            }
        }

        return flag;
    }

}