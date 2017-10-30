package com.demon.yzy.beanmusic.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.demon.yzy.beanmusic.R;
import com.demon.yzy.beanmusic.application.AppCache;
import com.demon.yzy.beanmusic.bean.Music;
import com.demon.yzy.beanmusic.mInterface.MusicObserver;
import com.demon.yzy.beanmusic.service.MusicService;
import com.demon.yzy.beanmusic.ui.VinylRecordView;
import com.demon.yzy.beanmusic.utils.BitmapUtil;
import com.demon.yzy.beanmusic.utils.TextUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class MusicActivity extends BaseActivity implements MusicObserver,SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "MusicActivity";

    public static final int MUSIC_PLAY=1;
    public static final int UPDATA_PROGRESS=2;

    @BindView(R.id.music_cover)
    VinylRecordView recordView;
    @BindView(R.id.music_cover_back)
    ImageView backImag;
    @BindView(R.id.tv_title)
    TextView titleText;
    @BindView(R.id.tv_artist)
    TextView artistText;
    @BindView(R.id.tv_current_time)
    TextView currentTime;
    @BindView(R.id.tv_total_time)
    TextView totalTime;
    @BindView(R.id.sb_progress)
    SeekBar progress;
    @BindView(R.id.iv_mode)
    ImageView modeImag;
    @BindView(R.id.iv_prev)
    ImageView prevImag;
    @BindView(R.id.iv_play)
    ImageView playImag;
    @BindView(R.id.iv_next)
    ImageView nextImag;


    private AppCache appCache;
    private MusicService service;
    private Handler musicActivityHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case UPDATA_PROGRESS:
                    progress.setProgress(msg.arg1);
                    currentTime.setText(TextUtil.formatTime("mm:ss",msg.arg1));
                    break;
            }
            return false;
        }
    });


    @Override
    protected int getLayout() {
        return R.layout.activity_music;
    }

    @Override
    protected void initData() {

        appCache=AppCache.getInstance();
        appCache.registerObserver(this);

        service=appCache.getMusicService();
        service.setMusicActivityHandler(musicActivityHandler);
        progress.setOnSeekBarChangeListener(this);

    }

    @Override
    protected void initView() {
        if (appCache.isNoMusic()){
            return;
        }
        Bitmap cover= appCache.getMusicCover();
        Music music=appCache.getMusic();
        progress.setMax((int) music.getDuration());
        progress.setProgress(service.getCurrent());
        currentTime.setText(TextUtil.formatTime("mm:ss",service.getCurrent()));

        titleText.setText(music.getTitle());
        artistText.setText(music.getArtist()+" - "+music.getAlbum());
        totalTime.setText(TextUtil.formatTime("mm:ss",music.getDuration()));

        whatMode();

        if (appCache.isPlaying()){
            playImag.setSelected(true);
            recordView.setPlaying(true);
        }else {
            playImag.setSelected(false);
            recordView.setPlaying(false);
        }


        recordView.setmCover(cover);
        Drawable drawable=new BitmapDrawable(BitmapUtil.blur(appCache.getMusicCover(),50));
        backImag.setBackground(drawable);
    }

    @OnClick(R.id.iv_back)
    void back(){
        finish();
    }

    @OnClick(R.id.iv_play)
    void playMusic(){
        if (appCache.isNoMusic()){
            return;
        }
        if (appCache.isPlaying()){
            service.pause();
            playImag.setSelected(false);
            recordView.setPlaying(false);
            appCache.setPlaying(false);
        }else {
            service.playMusic(appCache.getMusic().getPath(),false);
            playImag.setSelected(true);
            recordView.setPlaying(true);
            appCache.setPlaying(true);
        }
    }

    @OnClick(R.id.iv_mode)
    void modeChoose(){
        if (appCache.isNoMusic()){
            return;
        }
        int mode=appCache.getLoopMod();
        appCache.setLoopMod(++mode);
        whatMode();

    }

    @OnClick(R.id.iv_next)
    void nextMusic(){
        if (appCache.isNoMusic()){
            return;
        }
        service.nextMusic();
        appCache.musicChanged();
    }

    void whatMode(){
        int mode=appCache.getLoopMod();
        switch (mode){
            case AppCache.LOOP_MOD_ORDER:
                modeImag.setImageLevel(0);
                break;
            case AppCache.LOOP_MOD_CHAOS:
                modeImag.setImageLevel(1);
                break;
            case AppCache.LOOP_MOD_SINGLE:
                modeImag.setImageLevel(2);
                break;
        }
    }


    @Override
    public void musicChanged() {
        initView();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
