package com.demon.yzy.beanmusic.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demon.yzy.beanmusic.mInterface.LocalMusicItemListener;
import com.demon.yzy.beanmusic.R;
import com.demon.yzy.beanmusic.activity.MainActivity;
import com.demon.yzy.beanmusic.adapter.LocalMusicListAdapter;
import com.demon.yzy.beanmusic.application.AppCache;
import com.demon.yzy.beanmusic.bean.Music;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 易镇艺 on 2017/8/12.
 */

public class LocalMusicFragment extends Fragment {

    @BindView(R.id.local_music_list)
    RecyclerView listView;

    private List<Music> musicList;
    private Handler musicHandler;


    LocalMusicItemListener listListener=new LocalMusicItemListener() {
        @Override
        public void OnItmeClick(Music music) {
            Message message=new Message();
            message.what= MainActivity.MUSIC_PLAY;
            message.obj=music;
            musicHandler.sendMessage(message);
        }

        @Override
        public void OnMoreClick(Music music) {

        }
    };



    public LocalMusicFragment(){}

    @SuppressLint("ValidFragment")
    public LocalMusicFragment(Handler handler) {
        this();
        musicHandler=handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicList= AppCache.getInstance().getMusicList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_local_music,container,false);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    private void initView() {
        LocalMusicListAdapter adapter=new LocalMusicListAdapter(getActivity(),musicList);
        adapter.setListener(listListener);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
