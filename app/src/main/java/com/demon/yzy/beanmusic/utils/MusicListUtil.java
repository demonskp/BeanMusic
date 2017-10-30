package com.demon.yzy.beanmusic.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.demon.yzy.beanmusic.application.AppCache;
import com.demon.yzy.beanmusic.bean.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 易镇艺 on 2017/8/11.
 */

public class MusicListUtil {
    private static final String TAG = "MusicListUtil";

    /**
     * 扫描歌曲
     */
    public static void scanMusic(Context context) {

        Log.d(TAG, "scanMusic: ");
        List<Music> musicList=new ArrayList<Music>();
        musicList.clear();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            Log.d(TAG, "scanMusic: "+"isout And no data");
            return;
        }
        while (cursor.moveToNext()) {
            // 是否为音乐
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic == 0) {
                continue;
            }
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            // 标题
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            // 艺术家
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            // 专辑
            String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            // 持续时间
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            // 音乐uri
            String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            // 专辑封面id，根据该id可以获得专辑图片uri
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String coverUri = getCoverUri(context, albumId);
            // 音乐文件名
            String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
            // 音乐文件大小
            long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            // 发行时间
            String year = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)));
            Music music = new Music();
//            music.set...
            music.setId(id);
            music.setType(Music.Type.LOCAL);
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setDuration(duration);
            music.setPath(uri);
            music.setCoverPath(coverUri);
            music.setFileName(fileName);
            music.setFileSize(fileSize);

            Log.d(TAG, "scanMusic: "+music);
            musicList.add(music);
        }
        cursor.close();
        AppCache.getInstance().setMusicList(musicList);
        if (musicList.size()==0){
            AppCache.getInstance().setNoMusic(true);
        }else {
            AppCache.getInstance().setNoMusic(false);
        }
    }

    public static boolean isAudioControlPanelAvailable(Context context) {
        return isIntentAvailable(context, new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL));
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER) != null;
    }

    private static String getCoverPath(Context context, long albumId) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://media/external/audio/albums/" + albumId),
                new String[]{"album_art"}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext() && cursor.getColumnCount() > 0) {
                path = cursor.getString(0);
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 查询专辑封面图片uri
     */
    private static String getCoverUri(Context context, long albumId) {
        String uri = null;
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://media/external/audio/albums/" + albumId),
                new String[]{"album_art"}, null, null, null);
        if (cursor != null) {
            cursor.moveToNext();
            uri = cursor.getString(0);
            cursor.close();
        }
//        CoverLoader.getInstance().loadThumbnail(uri);
        return uri;
    }


    public static Music getMusicbyId(List<Music> musicList,long id){
        if (musicList.size()==0||id==-1) return null;
        Music music=new Music();
        for (int i=0;i<musicList.size();i++){
            music=musicList.get(i);
            if (music.getId()==id) return music;
        }
        return null;
    }




}
