package com.demon.yzy.beanmusic.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.demon.yzy.beanmusic.R;
import com.demon.yzy.beanmusic.application.AppCache;
import com.demon.yzy.beanmusic.application.BaseApplication;
import com.demon.yzy.beanmusic.bean.Music;
import com.demon.yzy.beanmusic.utils.MusicListUtil;
import com.demon.yzy.beanmusic.utils.PreferencesUtil;

public class WelcomeActivity extends BaseActivity {

    private static final int START_MAIN=1024;


    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what){
                case START_MAIN:
                    Intent intent=new Intent(BaseApplication.getContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }


            return false;
        }
    });


    @Override
    protected int getLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initData() {
        MusicListUtil.scanMusic(BaseApplication.getContext());
        long lastMusicId=PreferencesUtil.getLong(BaseApplication.getContext(),AppCache.APP_LAST_MUSIC,-1);
        Music lastMusic=MusicListUtil.getMusicbyId(AppCache.getInstance().getMusicList(),lastMusicId);
        if(lastMusic==null&&!AppCache.getInstance().isNoMusic()){
           lastMusic=AppCache.getInstance().getMusicList().get(0);
        }

        if (AppCache.getInstance().isNoMusic()){
            Toast.makeText(this,"暂时没有发现音乐文件",Toast.LENGTH_SHORT).show();
        }else {
            AppCache.getInstance().setMusic(lastMusic);
        }

    }

    @Override
    protected void initView() {

        handler.sendEmptyMessageDelayed(START_MAIN,5000);
    }
}
