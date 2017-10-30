package com.demon.yzy.beanmusic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.demon.yzy.beanmusic.R;
import com.demon.yzy.beanmusic.application.AppCache;
import com.demon.yzy.beanmusic.service.MusicService;

public class MusicBarReceiver extends BroadcastReceiver {
    private static final String TAG = "MusicBarReceiver";
    private MusicService service;
    private AppCache appCache;



    @Override
    public void onReceive(Context context, Intent intent) {
        appCache=AppCache.getInstance();
        service=appCache.getMusicService();

        switch (intent.getStringExtra(AppCache.NOTIFICATION_CONTROL)){
            case "play":
                notifiPlayClick();
                break;
            case "next":
                notifiNextClick();
                break;
        }
    }

    private void notifiPlayClick(){

        if (appCache.isPlaying()){
            service.pause();
        }else {
            service.playMusic(AppCache.getInstance().getMusic().getPath(),false);
        }
        appCache.musicChanged();

    }

    private void notifiNextClick(){
        appCache.setPlaying(true);
        service.nextMusic();
        appCache.musicChanged();
    }



}
