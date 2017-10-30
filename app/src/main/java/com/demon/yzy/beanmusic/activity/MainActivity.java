package com.demon.yzy.beanmusic.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.demon.yzy.beanmusic.adapter.MusicPagerAdapter;
import com.demon.yzy.beanmusic.application.AppCache;
import com.demon.yzy.beanmusic.fragment.LocalMusicFragment;
import com.demon.yzy.beanmusic.fragment.OnlineMusicFragment;
import com.demon.yzy.beanmusic.mInterface.MusicObserver;
import com.demon.yzy.beanmusic.service.MusicService;
import com.demon.yzy.beanmusic.R;
import com.demon.yzy.beanmusic.bean.Music;
import com.demon.yzy.beanmusic.utils.MusicListUtil;
import com.demon.yzy.beanmusic.utils.ToastUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,MusicObserver{
    private static final String TAG = "MainActivity";

    public static final int MUSIC_PLAY=1;
    public static final int UPDATA_PROGRESS=2;


    @BindView(R.id.iv_play_bar_play)
    ImageView playBnt;
    @BindView(R.id.iv_play_bar_cover)
    ImageView coverImg;
    @BindView(R.id.tv_play_bar_title)
    TextView titleText;
    @BindView(R.id.tv_play_bar_artist)
    TextView artistText;
    @BindView(R.id.pb_play_bar)
    ProgressBar progressBar;


    @BindView(R.id.tv_local_music)
    TextView localMusicText;
    @BindView(R.id.tv_online_music)
    TextView onLineMusicText;

    @BindView(R.id.main_viewpager)
    ViewPager viewPager;

    @BindView(R.id.main_drawer)
    DrawerLayout drawerLayout;

    private List<Music> musicList=new ArrayList<Music>();
    private List<Fragment> fragmentList=new ArrayList<Fragment>();
    private FragmentManager manager;
    private ViewPager.OnPageChangeListener changeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected: ----------");
            if (position==0){
                localMusicText.setSelected(true);
                onLineMusicText.setSelected(false);
            }else {
                localMusicText.setSelected(false);
                onLineMusicText.setSelected(true);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    Handler musicHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what){
                case MUSIC_PLAY:
                    AppCache.getInstance().setMusic((Music) msg.obj);
                    AppCache.getInstance().setPlaying(false);
                    initMusicPlayView();
                    onRealPlay(true);
                    break;
                case UPDATA_PROGRESS:
                    int progress=msg.arg1;
                    onUpdataProgress(progress);
                    break;
            }


            return false;
        }
    });


    private MusicService musicService;

    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService= ((MusicService.MusicBinder) service).getService();
            musicService.setMusicHandler(musicHandler);
            AppCache.getInstance().setMusicService(musicService);
            Log.d(TAG, "onServiceConnected: "+musicService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService=null;
        }
    };


    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        musicList=AppCache.getInstance().getMusicList();
        Intent intent=new Intent(this,MusicService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);

        manager=getSupportFragmentManager();
        fragmentList.add(new LocalMusicFragment(musicHandler));
        fragmentList.add(new OnlineMusicFragment(musicHandler));

        //注册对于music的观察  在APPCache中
        AppCache.getInstance().registerObserver(this);
    }


    @Override
    protected void initView() {
        localMusicText.setSelected(true);
        viewPager.setAdapter(new MusicPagerAdapter<>(manager,fragmentList));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(changeListener);

        initMusicPlayView();
    }

    //初始化 播放碎片的View
    private void initMusicPlayView() {

        coverImg.setImageResource(R.drawable.default_cover);

        if (musicList==null)  return;

        if (AppCache.getInstance().isPlaying()){
            playBnt.setBackgroundResource(R.drawable.ic_play_bar_btn_pause);
        }else {
            playBnt.setBackgroundResource(R.drawable.ic_play_bar_btn_play);
        }

        Music music=AppCache.getInstance().getMusic();
        if (music!=null&&music.getCoverPath()!=null){
            File file=new File(music.getCoverPath());
            Picasso.with(this).load(file).placeholder(R.drawable.default_cover).into(coverImg);
            titleText.setText(music.getTitle());
            artistText.setText(music.getArtist()+" - "+music.getAlbum());
            progressBar.setMax((int) music.getDuration());
        }

    }

    private void onUpdataProgress(int progress){

        progressBar.setProgress(progress);
    }




    @OnClick(R.id.iv_play_bar_play)
    void onPlay(){
        if (!AppCache.getInstance().isNoMusic()){
            onRealPlay(false);
        }else {
            ToastUtil.ShowText(this,"请先下载音乐后再播放！");
        }

    }

    @OnClick(R.id.iv_play_bar_next)
    void onNext(){
        if(!AppCache.getInstance().isNoMusic()){
            AppCache.getInstance().setPlaying(true);
            musicService.nextMusic();
            initMusicPlayView();
        }else {
            //什么都不做
        }

    }

    void onRealPlay(boolean isChange)
    {
        if (AppCache.getInstance().isPlaying()){
            playBnt.setBackgroundResource(R.drawable.ic_play_bar_btn_play);
            musicService.pause();
        }else {
            playBnt.setBackgroundResource(R.drawable.ic_play_bar_btn_pause);
            musicService.playMusic(AppCache.getInstance().getMusic().getPath(),isChange);
        }
    }

    /**
      *Titile bar 上面的一些点击事件
     **/

    @OnClick(R.id.iv_menu)
    void menuOpen(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.tv_online_music)
    void clickOnlineText(){
        viewPager.setCurrentItem(1);
    }
    @OnClick(R.id.tv_local_music)
    void clickLocalText(){
        viewPager.setCurrentItem(0);
    }



    @OnClick(R.id.ll_music_play)
    void clickMusicPlay(){
        Intent intent=new Intent(MainActivity.this,MusicActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: ");
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void musicChanged() {
        initMusicPlayView();
    }
}
