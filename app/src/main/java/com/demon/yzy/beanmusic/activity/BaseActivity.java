package com.demon.yzy.beanmusic.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.security.Permission;

import butterknife.ButterKnife;
import okhttp3.Request;

/**
 * Created by 易镇艺 on 2017/8/11.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    public static final int TAGPERMISSION=1024;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        RequestPermission();

        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void RequestPermission() {
        boolean isPermission=ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        Log.d(TAG, "RequestPermission: "+isPermission);
        if (!isPermission){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)){
                finish();
            }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},TAGPERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case TAGPERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "allow", Toast.LENGTH_SHORT).show();
                    doSomeThingNeedPermissons();
                } else {
//                    Toast.makeText(this, "deny", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }

    protected void doSomeThingNeedPermissons() {

    }

    //返回视图
    protected abstract int getLayout();

    //初始化数据
    protected abstract void initData();

    //初始化视图
    protected abstract void initView();
}
