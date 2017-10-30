package com.demon.yzy.beanmusic.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 易镇艺 on 2017/8/12.
 */

public class MusicPagerAdapter<T extends Fragment> extends FragmentPagerAdapter {

    List<T> list=new ArrayList<T>();

    public MusicPagerAdapter(FragmentManager fm, List<T> list) {
        super(fm);
        this.list=list;
    }


    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
