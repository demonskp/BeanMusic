package com.demon.yzy.beanmusic.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.demon.yzy.beanmusic.R;
import com.demon.yzy.beanmusic.activity.BaseActivity;
import com.demon.yzy.beanmusic.bean.Music;
import com.demon.yzy.beanmusic.mInterface.MusicObserver;
import com.demon.yzy.beanmusic.service.MusicService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 易镇艺 on 2017/8/11.
 */

public class AppCache {

    public static final String APP_LAST_MUSIC="applastmusic";
    public static final String NOTIFICATION_CONTROL="com.yzy.control";
    public static final int LOOP_MOD_SINGLE=2;
    public static final int LOOP_MOD_ORDER=0;
    public static final int LOOP_MOD_CHAOS=1;


    private boolean isPlaying=false;
    private boolean isNoMusic=false;
    private Music music=null;
    private List<Music> musicList;
    private Bitmap musicCover;
    private Music nextMusic=null;
    private Music lastMusic=null;
    private int loopMod=LOOP_MOD_ORDER;
    private MusicService musicService;
    private List<MusicObserver> observerList=new ArrayList<MusicObserver>();

    private static AppCache instance;

    private AppCache(){

    }

    public static AppCache getInstance(){
        if (instance==null){
            synchronized (AppCache.class){
                if (instance==null){
                    instance=new AppCache();
                }
            }
        }
        return instance;
    }


    public int getLoopMod() {
        return loopMod;
    }

    public void setLoopMod(int loopMod) {
        if (loopMod>2) loopMod=0;
        this.loopMod = loopMod;
        calculateNext(music);
    }

    public void registerObserver(MusicObserver observer){
        observerList.add(observer);
    }

    public void musicChanged(){
        for (MusicObserver m:observerList){
            m.musicChanged();
        }
    }

    public boolean isNoMusic() {
        return isNoMusic;
    }

    public void setNoMusic(boolean noMusic) {
        isNoMusic = noMusic;
    }

    public MusicService getMusicService() {
        return musicService;
    }

    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }

    public void getNextMusic() {
        setMusic(nextMusic);
    }

    public void setNextMusic(Music nextMusic) {
        this.nextMusic = nextMusic;
    }

    public Music getLastMusic() {
        return lastMusic;
    }

    public void setLastMusic(Music lastMusic) {
        this.lastMusic = lastMusic;
    }

    public Bitmap getMusicCover() {
        return musicCover;
    }

    public void setMusicCover(Bitmap musicCover) {
        this.musicCover = musicCover;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
        if (music.getCoverPath()!=null){
            File file=new File(music.getCoverPath());
            Picasso.with(BaseApplication.getContext()).load(file).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    setMusicCover(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Bitmap bitmap= BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.drawable.default_cover);
                    setMusicCover(bitmap);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
        calculateNext(music);
    }

    private void calculateNext(Music music) {
        Music nowMusic;
        int listPosition=0;
        for (int i=0;i<musicList.size();i++){
            nowMusic=musicList.get(i);
            if (nowMusic.equals(music)){
                listPosition=i;
                 break;
            }
        }
        lastMusic=music;
        switch (loopMod){
            case LOOP_MOD_ORDER:
                if (listPosition==musicList.size()-1) listPosition=-1;
                nextMusic=musicList.get(listPosition+1);
                break;
            case LOOP_MOD_SINGLE:
                nextMusic=music;
                break;
            case LOOP_MOD_CHAOS:
                int i= (int) (Math.random()*musicList.size());
                nextMusic=musicList.get(i);
                break;
        }


    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}

