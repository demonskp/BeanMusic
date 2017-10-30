package com.demon.yzy.beanmusic.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.renderscript.RenderScript;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.demon.yzy.beanmusic.R;
import com.demon.yzy.beanmusic.activity.MusicActivity;
import com.demon.yzy.beanmusic.activity.WelcomeActivity;
import com.demon.yzy.beanmusic.application.AppCache;
import com.demon.yzy.beanmusic.application.BaseApplication;
import com.demon.yzy.beanmusic.bean.Music;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by 易镇艺 on 2017/8/14.
 */

public class NotificationUtil {
    private NotificationManager manager;



    public  static void showDefuatNotification(Context context,int icon, String contentTitle,String contentText,Intent intent){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
        builder.setAutoCancel(true)
                .setSmallIcon(icon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(contentTitle)
                .setContentText(contentText);

        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification=builder.build();

        NotificationManager manager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,notification);

    }

    public static Notification getMusicDefuatNotification(Context context){
        Intent intent=new Intent(context, WelcomeActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification=new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.default_cover)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(false)
                .setCustomContentView(getMusicDefuatRemoteView(context))
                .build();


        return notification;
    }

    private static RemoteViews getMusicDefuatRemoteView(Context context) {

        Music music=AppCache.getInstance().getMusic();

        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.item_notification);
        if (AppCache.getInstance().isPlaying()){
            remoteViews.setImageViewResource(R.id.notification_play_bar_play,R.drawable.ic_play_bar_btn_pause);
        }else {
            remoteViews.setImageViewResource(R.id.notification_play_bar_play,R.drawable.ic_play_bar_btn_play);
        }

        remoteViews.setTextViewText(R.id.notification_play_bar_title,music.getTitle());
        remoteViews.setTextViewText(R.id.notification_play_bar_artist,music.getArtist()+" - "+music.getAlbum());
        remoteViews.setImageViewBitmap(R.id.notification_play_bar_cover,AppCache.getInstance().getMusicCover());

        //next的点击事件
        Intent intent=new Intent();
        intent.setAction(AppCache.NOTIFICATION_CONTROL);
        intent.putExtra(AppCache.NOTIFICATION_CONTROL,"next");
        PendingIntent nextIntent=PendingIntent.getBroadcast(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_play_bar_next,nextIntent);

        //play的点击事件  需要receiver
        Intent intent1=new Intent();
        intent1.setAction(AppCache.NOTIFICATION_CONTROL);
        intent1.putExtra(AppCache.NOTIFICATION_CONTROL,"play");
        PendingIntent playIntent=PendingIntent.getBroadcast(context,2,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_play_bar_play,playIntent);

        //all的点击事件  跳转到歌曲播放界面
        Intent intent2=new Intent(context, MusicActivity.class);
//        intent2.putExtra(AppCache.NOTIFICATION_ALL,"all");
        PendingIntent allIntent=PendingIntent.getActivity(context,3,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_play_bar_all,allIntent);

        return remoteViews;
    }


}
