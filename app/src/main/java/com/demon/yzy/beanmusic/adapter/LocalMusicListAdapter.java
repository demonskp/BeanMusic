package com.demon.yzy.beanmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demon.yzy.beanmusic.mInterface.LocalMusicItemListener;
import com.demon.yzy.beanmusic.R;
import com.demon.yzy.beanmusic.bean.Music;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 易镇艺 on 2017/8/12.
 */

public class LocalMusicListAdapter extends RecyclerView.Adapter<LocalMusicListAdapter.LocalViewHolder> {
    private static final String TAG = "LocalMusicListAdapter";
    private List<Music> musicList;
    private Context mContext;
    private LocalMusicItemListener listener=new LocalMusicItemListener() {
        @Override
        public void OnItmeClick(Music music) {

        }

        @Override
        public void OnMoreClick(Music music) {

        }
    };


    public LocalMusicListAdapter(Context context,List<Music> musicList) {
        super();
        this.mContext=context;
        this.musicList=musicList;
    }

    @Override
    public LocalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_local_music,parent,false);
        LocalViewHolder viewHolder=new LocalViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LocalViewHolder holder, int position) {
        final Music music=musicList.get(position);
        Log.d(TAG, "onBindViewHolder: "+music.getCoverPath());

        if (music.getCoverPath()!=null){
            File file=new File(music.getCoverPath());
            Picasso.with(mContext).load(file).placeholder(R.drawable.default_cover).into(holder.caverImg);
        }else {
            Picasso.with(mContext).load(music.getCoverPath()).placeholder(R.drawable.default_cover).into(holder.caverImg);
        }


        holder.titleText.setText(music.getTitle());

        holder.artistText.setText(music.getArtist()+" - "+music.getAlbum());
        //点击事件
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnItmeClick(music);
            }
        });

        holder.moreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnMoreClick(music);
            }
        });

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class LocalViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_cover)
        ImageView caverImg;
        @BindView(R.id.tv_title)
        TextView titleText;
        @BindView(R.id.tv_artist)
        TextView artistText;
        @BindView(R.id.iv_more)
        ImageView moreImg;
        @BindView(R.id.v_divider)
        View dividerView;
        @BindView(R.id.v_playing)
        View playingView;
        @BindView(R.id.ly_item_local)
        LinearLayout layout;



        public LocalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public LocalMusicItemListener getListener() {
        return listener;
    }

    public void setListener(LocalMusicItemListener listener) {
        this.listener = listener;
    }
}
