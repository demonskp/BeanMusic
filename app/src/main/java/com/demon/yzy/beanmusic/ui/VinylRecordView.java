package com.demon.yzy.beanmusic.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.demon.yzy.beanmusic.R;
import com.demon.yzy.beanmusic.utils.BitmapUtil;

/**
 * Created by 易镇艺 on 2017/8/15.
 */

public class VinylRecordView extends View implements ValueAnimator.AnimatorUpdateListener{
    private static final String TAG = "VinylRecordView";
    private static final int DES=100;
    private static final float ROTATE_PLAY=0.5f;
    private static final float ROTATE_PUASE=0.0f;
    private static final long TIME_UPDATE = 50L;
    private static final float POINT_ROTATION_PLAY = 0.0f;
    private static final float POINT_ROTATION_PAUSE = -25.0f;


    private Bitmap mPointer;
    private Bitmap mCover;
    private float mCoverRotation=0.0f;
    private int mCoverRad=0;
    private Paint backPaint;
    private BitmapDrawable src;
    private float discRotate=ROTATE_PUASE;
    private float pointRotate=POINT_ROTATION_PAUSE;
    private boolean isPlaying=false;
    private Point mPoint=new Point();

    private Matrix discMatrix=new Matrix();
    private Matrix pointMatrix=new Matrix();

    private ValueAnimator mPlayAnimator;
    private ValueAnimator mPauseAnimator;

    private  Shader backShader;
    private int size;

    private Handler discHandle=new Handler();
    private Runnable discRunnable=new Runnable() {
        @Override
        public void run() {
            if (isPlaying){
                discRotate+=ROTATE_PLAY;
                if (discRotate >= 360) {
                    discRotate = 0;
                }
                invalidate();
            }
            discHandle.postDelayed(this,TIME_UPDATE);
        }
    };


    public VinylRecordView(Context context) {
        this(context,null);
    }

    public VinylRecordView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public VinylRecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.VinylRecordView);
        init(ta);
        ta.recycle();
    }

    private void init(TypedArray ta) {
        backPaint=new Paint();
        src= (BitmapDrawable) ta.getDrawable(R.styleable.VinylRecordView_coverRes);

        if (src!=null){
            setmCover(src.getBitmap());
        }else {
            setmCover(BitmapFactory.decodeResource(getResources(),R.drawable.default_cover));
        }

        mPointer=BitmapFactory.decodeResource(getResources(),R.drawable.play_page_needle);
        mPoint.x = getWidth() / 2 - mPointer.getWidth() / 6;
        mPoint.y = -mPointer.getWidth() / 6;

        mPlayAnimator=ValueAnimator.ofFloat(POINT_ROTATION_PAUSE,POINT_ROTATION_PLAY);
        mPauseAnimator=ValueAnimator.ofFloat(POINT_ROTATION_PLAY,POINT_ROTATION_PAUSE);
        mPlayAnimator.setDuration(400);
        mPauseAnimator.setDuration(400);
        mPauseAnimator.addUpdateListener(this);
        mPlayAnimator.addUpdateListener(this);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initData();
    }

    private void initData() {
        Log.d(TAG, "initData: --------");
        mCoverRad=Math.min(getMeasuredHeight(),getMeasuredWidth())/2-3*DES;

        mPointer=BitmapFactory.decodeResource(getResources(),R.drawable.play_page_needle);
        mPoint.x = getWidth() / 2 - mPointer.getWidth() / 6;
        mPoint.y = -mPointer.getWidth() / 6;

        backShader=new RadialGradient(getWidth()/2,getHeight()/2,5, Color.BLACK,Color.parseColor("#2E2E2E"),Shader.TileMode.MIRROR);
        size=Math.min(getMeasuredHeight(),getMeasuredWidth());

        Bitmap scaleBitmap=Bitmap.createScaledBitmap(mCover,2*mCoverRad,2*mCoverRad,false);
        mCover= BitmapUtil.createCircleImage(scaleBitmap);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        backPaint.reset();
        backPaint.setAlpha(50);
        backPaint.setColor(Color.parseColor("#809E9E9E"));
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setAntiAlias(true);
        canvas.drawCircle(getWidth()/2,getHeight()/2,size/2-1.3f*DES,backPaint);


        //画黑胶部分
        backPaint.reset();
        backPaint.setColor(getResources().getColor(R.color.black));
        backPaint.setAntiAlias(true);
        backPaint.setShader(backShader);
        backPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth()/2,getHeight()/2,size/2-1.5f*DES,backPaint);

        //画封面图片
        backPaint.reset();
        backPaint.setAntiAlias(true);
        discMatrix.setRotate(discRotate,getWidth()/2,getHeight()/2);
        discMatrix.preTranslate(getWidth()/2-mCoverRad,getHeight()/2-mCoverRad);
        canvas.drawBitmap(mCover,discMatrix,backPaint);

        backPaint.reset();
        backPaint.setAntiAlias(true);
        pointMatrix.setRotate(pointRotate,getWidth()/2,0);
        pointMatrix.preTranslate(mPoint.x,mPoint.y);
        canvas.drawBitmap(mPointer,pointMatrix,backPaint);

    }


    public void setPlaying(boolean isPlaying){

        if (isPlaying==this.isPlaying) return;

        this.isPlaying=isPlaying;
        if (isPlaying){
            discHandle.post(discRunnable);
            mPlayAnimator.start();
        }else {
            discHandle.removeCallbacks(discRunnable);
            mPauseAnimator.start();
        }
        invalidate();
    }

    public Bitmap getmCover() {
        return mCover;
    }

    public void setmCover(Bitmap mCover) {
        if (mCoverRad!=0){
            Bitmap scaleBitmap=Bitmap.createScaledBitmap(mCover,2*mCoverRad,2*mCoverRad,false);
            Bitmap ss= BitmapUtil.createCircleImage(scaleBitmap);
            this.mCover=ss;
        }else {
            this.mCover=mCover;
        }
        invalidate();
    }

    public void setmCover(@IntegerRes int res){
        Bitmap baseBitmap=BitmapFactory.decodeResource(getResources(),res);
        setmCover(baseBitmap);
    }

    public float getmCoverRotation() {
        return mCoverRotation;
    }

    public void setmCoverRotation(float mCoverRotation) {
        this.mCoverRotation = mCoverRotation;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        pointRotate= (float) animation.getAnimatedValue();
        invalidate();
    }
}
