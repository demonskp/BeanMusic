package com.demon.yzy.beanmusic.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demon.yzy.beanmusic.R;

import butterknife.ButterKnife;

/**
 * Created by 易镇艺 on 2017/8/12.
 */

public class OnlineMusicFragment extends Fragment {

    private Handler musicHandler;

    public OnlineMusicFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public OnlineMusicFragment(Handler handler){
        this();
        musicHandler=handler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_online_music,container,false);
        ButterKnife.bind(this,view);

        return view;
    }
}
