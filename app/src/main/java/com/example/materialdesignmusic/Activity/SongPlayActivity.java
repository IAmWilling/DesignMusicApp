package com.example.materialdesignmusic.Activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.materialdesignmusic.Apdater.SongSheetPlayListInfoAdpater;
import com.example.materialdesignmusic.CommonData.CommonData;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.CommonMethods.BitmapMethods;
import com.example.materialdesignmusic.CommonMethods.MediaStorePlayer;
import com.example.materialdesignmusic.CommonMethods.Methods;
import com.example.materialdesignmusic.JSONDATA.MusicListInfo;
import com.example.materialdesignmusic.JSONDATA.SongSheetPlayListDetail;
import com.example.materialdesignmusic.R;
import com.example.materialdesignmusic.Service.MusicPlayService;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * 歌曲播放页面
 */
public class SongPlayActivity extends Activity implements View.OnClickListener{
    public static SongPlayActivity instance = null;
    private ImageView bgcover,centerImage;
    WindowManager wm;
    WindowManager.LayoutParams layoutParams;
    private Toolbar myToolbar;
    private ImageView playandpause,prev,next,playTypeImage;
    private TextView currentTimeView,durationView;
    private ProgressDialog dialog;
    private SeekBar seekBar;
    SongSheetPlayListDetail songSheetPlayListDetail;
    private ObjectAnimator objectAnimator = null;
    public static MusicPlayUIReceiver musicPlayUIReceiver = null;
    private long prelongTim = 0;    //定义上次单机的时间
    private Thread threadTime = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_playing_activity);
        /*
        * 注册广播服务
        * */
        musicPlayUIReceiver = new MusicPlayUIReceiver();
        //注册广播服务
        IntentFilter intentFilter = new IntentFilter();
        //更新有UI定义广播过滤
        intentFilter.addAction("com.example.materialdesignmusic.musicui");
        //注册服务
        registerReceiver(musicPlayUIReceiver,intentFilter);

           


        instance = this;
        dialog = ProgressDialog.show(this, "歌曲", "准备播放中...");
        init();
        setHalfTransparent();
        setFitSystemWindow(false);

        Intent intent = getIntent();

        songSheetPlayListDetail = (SongSheetPlayListDetail) intent.getSerializableExtra("object");
        CommonData.musicIndex = intent.getIntExtra("index",0); //拿到列表正确索引开始播放音乐
        if(CommonData.nowMusicId != songSheetPlayListDetail.getId()) {
            //说明不是当前的音乐id直接切换音乐
        }
        //判断状态
        if(CommonData.mediaPlayer.isPlaying()) {
            playandpause.setImageResource(R.drawable.pause_white);
        }else {
            playandpause.setImageResource(R.drawable.play_white);
        }

        myToolbar.setTitle(songSheetPlayListDetail.getName());
        Glide.with(MyApplication.getContext())
                .load(songSheetPlayListDetail.getAl().getPicUrl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(centerImage);
        Glide.with(this).asBitmap().load(songSheetPlayListDetail.getAl().getPicUrl())
                .into(new SetGlideBitmap(bgcover));
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener());

        objectAnimator = ObjectAnimator.ofFloat(centerImage,"rotation",0f,360f);
        //重复次数
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        //执行时间
        objectAnimator.setDuration(30000);
        //设置动画匀速运动
        LinearInterpolator lin = new LinearInterpolator();
        objectAnimator.setInterpolator(lin);
        //结束后的状态
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playing_activity_btn_playandpause:
                if(CommonData.mediaPlayer.isPlaying()) {
                    playandpause.setImageResource(R.drawable.play_white);
                    CommonData.mediaPlayer.pause();
                    objectAnimator.pause();
                }else {
                    playandpause.setImageResource(R.drawable.pause_white);
                    CommonData.mediaPlayer.start();
                    objectAnimator.start();
                }
                Intent noticIntent = new Intent("com.example.materialdesignmusic.playnnotification");
                noticIntent.putExtra("type",4);//暂停/开始
                sendBroadcast(noticIntent);
                break;
            case R.id.playing_activity_btn_previous:
                //上一首切换
                if(CommonData.musicIndex - 1 < 0) {
                    Toast.makeText(this,"已经是第一首了",Toast.LENGTH_SHORT).show();
                }else {
                    if(threadTime != null) {
                        threadTime.interrupt();
                        threadTime = null;

                    }
                    CommonData.musicIndex += 1;
                    threadTime = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(1500);
                                Intent intent = new Intent(SongPlayActivity.this, MusicPlayService.class);
                                //发送切歌服务 上一首
                                intent.putExtra("type",2);
                                startService(intent);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    threadTime.start();
                }
                break;
            case R.id.playing_activity_btn_next:
                //下一首切换
                if(CommonData.musicIndex + 1 >= CommonData.commonSongSheetPlayListDetailList.size()) {
                    Toast.makeText(this,"已经是最后一首了",Toast.LENGTH_SHORT).show();
                }else {
                    if(threadTime != null) {
                        threadTime.interrupt();
                        threadTime = null;

                    }
                    CommonData.musicIndex += 1;
                    threadTime = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(1500);
                                Intent intent = new Intent(SongPlayActivity.this, MusicPlayService.class);
                                //发送切歌服务 下一首
                                intent.putExtra("type",3);
                                startService(intent);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    threadTime.start();
                }
                break;
            case R.id.playing_type:
                if(CommonData.PlayType.equals(CommonData.LIST_LOOP)) {
                    CommonData.PlayType = CommonData.ONE_LOOP;
                    playTypeImage.setImageResource(R.drawable.oneloop_white);
                    Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
                }else if(CommonData.PlayType.equals(CommonData.ONE_LOOP)) {
                    CommonData.PlayType = CommonData.ROUND_PLAY;
                    Toast.makeText(this, "随机播放", Toast.LENGTH_SHORT).show();
                    playTypeImage.setImageResource(R.drawable.round_white);
                }else if(CommonData.PlayType.equals(CommonData.ROUND_PLAY)) {
                    CommonData.PlayType = CommonData.LIST_LOOP;
                    Toast.makeText(this, "列表循环", Toast.LENGTH_SHORT).show();
                    playTypeImage.setImageResource(R.drawable.listloop_white);
                }
                    break;
        }
    }

    /**
     * 进度条监听拖拽事件
     * */
    protected class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
    /**
     * 初始化控件
     * */
    private void init() {
        bgcover = findViewById(R.id.bg_cover_playing);
        centerImage = findViewById(R.id.image_transform_cover);
        myToolbar = findViewById(R.id.song_sheet_toolbar3);
        seekBar = findViewById(R.id.song_playing_seek_bar);
        currentTimeView = findViewById(R.id.song_playing_current_time);
        durationView = findViewById(R.id.song_playing_duration_time);
        playandpause = findViewById(R.id.playing_activity_btn_playandpause);
        prev = findViewById(R.id.playing_activity_btn_previous);
        next = findViewById(R.id.playing_activity_btn_next);
        playandpause.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        playTypeImage = findViewById(R.id.playing_type);
        playTypeImage.setOnClickListener(this);
    }
    /**
    * 设置图片高斯模糊加载
    * */
    private class SetGlideBitmap extends BitmapImageViewTarget {

        public SetGlideBitmap(ImageView view) {
            super(view);
        }
        /**
         * 重写方法得到得到bitmap
         * 对bitmap进行处理高斯模糊
         * */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected void setResource(Bitmap resource) {
            if(resource != null) {
                Bitmap bitmap = BitmapMethods.setBitmapGaussianBlur(getApplicationContext(),resource,25f);
                CommonData.nowMusicBitmap = resource;
                super.setResource(bitmap);
                /**
                 * 创建图片的同时启动服务进行播放
                 * */
                //启动音乐服务
                if((CommonData.nowMusicId != songSheetPlayListDetail.getId()) && CommonData.nowMusicId != 0) {
                    //说明不是当前的音乐id直接切换音乐
                    Intent intent = new Intent(SongPlayActivity.this, MusicPlayService.class);
                    //发送切歌服务
                    intent.putExtra("type",1);
                    startService(intent);
                }else {
                    //就是第一次播放
                    Intent intent = new Intent(SongPlayActivity.this, MusicPlayService.class);
                    startService(intent);
                }
                CommonData.nowMusicId = songSheetPlayListDetail.getId();
                dialog.dismiss();

            }else {
                super.setResource(resource);
            }

        }
    }




    /**
     * 半透明状态栏
     */
    protected void setHalfTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 如果需要内容紧贴着StatusBar
     * 应该在对应的xml布局文件中，设置根布局fitsSystemWindows=true。
     */
    private View contentViewGroup;

    protected void setFitSystemWindow(boolean fitSystemWindow) {
        if (contentViewGroup == null) {
            contentViewGroup = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        }
        contentViewGroup.setFitsSystemWindows(fitSystemWindow);
    }

    /*
    * 广播
    * */
    public class MusicPlayUIReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            switch(type) {
                case "seekbar":
                    String f = BitmapMethods.txfloat(CommonData.mediaPlayer.getCurrentPosition(),CommonData.mediaPlayer.getDuration());
                    double a = Double.valueOf(f);

                    currentTimeView.setText(Methods.formattime(CommonData.mediaPlayer.getCurrentPosition()));
                    durationView.setText(Methods.formattime(CommonData.mediaPlayer.getDuration()));
                    seekBar.setProgress((int)(a * 1000));
                    break;
                case "playandpause":
                    if(CommonData.mediaPlayer.isPlaying()) {
                        playandpause.setImageResource(R.drawable.pause_white);
                        objectAnimator.start();
                    }else {
                        playandpause.setImageResource(R.drawable.play_white);
                        objectAnimator.pause();
                    }
                    break;
                case "change":
                    //切换封面

                    myToolbar.setTitle(CommonData.nowMusicName);
                    Glide.with(MyApplication.getContext())
                            .load(CommonData.nowPicUrl)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(centerImage);
                    Glide.with(MyApplication.getContext()).asBitmap().load(CommonData.nowPicUrl)
                            .into(new BitmapImageViewTarget(bgcover){
                                @Override
                                protected void setResource(Bitmap resource) {
                                    if(resource != null) {
                                        Bitmap bitmap = BitmapMethods.setBitmapGaussianBlur(getApplicationContext(),resource,25f);
                                        CommonData.nowMusicBitmap = resource;
                                        super.setResource(bitmap);
                                    }else {
                                        super.setResource(resource);
                                    }

                                }
                            });
                    objectAnimator.start();
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        if(CommonData.nowMusicId != songSheetPlayListDetail.getId()) {

        }
        unregisterReceiver(musicPlayUIReceiver);

        super.onDestroy();
    }

}
