package com.demon.yzy.beanmusic.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;

import com.demon.yzy.beanmusic.activity.MainActivity;
import com.demon.yzy.beanmusic.activity.MusicActivity;
import com.demon.yzy.beanmusic.application.AppCache;
import com.demon.yzy.beanmusic.application.BaseApplication;
import com.demon.yzy.beanmusic.utils.NotificationUtil;

import java.io.IOException;

public class MusicService extends Service {

    private static final String TAG = "MusicService";
    public static final int MUSIC_NOTIFICATION_ID=1;
    private Handler musicHandler;
    private Handler musicActivityHandler;
    private  Message msg=new Message();
    private Message msg1=new Message();;


    Thread updataThread=new Thread(new Runnable() {
        @Override
        public void run() {
            if (mPlayer.isPlaying()){

                int progress=mPlayer.getCurrentPosition();

                if (musicHandler!=null){
                    msg=Message.obtain();
                    msg.what= MainActivity.UPDATA_PROGRESS;
                    msg.arg1=progress;
                    musicHandler.sendMessage(msg);
                }

                if (musicActivityHandler!=null){
                    msg1=Message.obtain();
                    msg1.what= MusicActivity.UPDATA_PROGRESS;
                    msg1.arg1=progress;
                    musicActivityHandler.sendMessage(msg1);
                }
                try {
                    updataThread.sleep(200l);
                    run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    
    private Context mContext;
    MediaPlayer  mPlayer=new MediaPlayer();

    private MediaPlayer.OnPreparedListener mPreparedListener=new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            start();
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            overPlay();
        }
    };


    public MusicService(){}

    public MusicService(Context context) {
        mContext=context;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        mContext= BaseApplication.getContext();
        return new MusicBinder();
    }


    public void playMusic(String path, boolean isChange){

        //播放器记录的位置  判断是不是暂停重播的。

        if (!isChange&&mPlayer.getCurrentPosition()>0){
            start();
            Log.d(TAG, "playMusic: ");
        }else {
            try {
                mPlayer.reset();
                mPlayer.setDataSource(path);
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(mPreparedListener);
                mPlayer.setOnCompletionListener(mCompletionListener);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void pause(){
        if (AppCache.getInstance().isPlaying()){
            mPlayer.pause();
            AppCache.getInstance().setPlaying(false);
            startForeground(MUSIC_NOTIFICATION_ID, NotificationUtil.getMusicDefuatNotification(mContext));
        }
    }

    public void setMusicHandler(Handler musicHandler) {
        this.musicHandler = musicHandler;
    }

    public void start(){
        mPlayer.start();
        updataThread.start();
        AppCache.getInstance().setPlaying(true);
        startForeground(MUSIC_NOTIFICATION_ID, NotificationUtil.getMusicDefuatNotification(mContext));
    }

    public void overPlay(){
        AppCache.getInstance().setPlaying(false);
        nextMusic();
    }

    public void nextMusic(){
        AppCache.getInstance().getNextMusic();
        playMusic(AppCache.getInstance().getMusic().getPath(),true);
    }
    public void seekTo(int progress){
        mPlayer.seekTo(progress);
    }

    public int getCurrent() {
        return mPlayer.getCurrentPosition();
    }

    public Handler getMusicActivityHandler() {
        return musicActivityHandler;
    }

    public void setMusicActivityHandler(Handler musicActivityHandler) {
        this.musicActivityHandler = musicActivityHandler;
    }

    public class MusicBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }
}
