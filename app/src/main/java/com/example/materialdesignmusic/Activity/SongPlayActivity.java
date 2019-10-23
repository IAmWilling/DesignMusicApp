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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.materialdesignmusic.CommonData.CommonData;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.CommonMethods.BitmapMethods;
import com.example.materialdesignmusic.CommonMethods.Methods;
import com.example.materialdesignmusic.JSONDATA.MusicLyricData;
import com.example.materialdesignmusic.JSONDATA.SongSheetPlayListDetail;
import com.example.materialdesignmusic.NetWorkUtil.NetworkUtil;
import com.example.materialdesignmusic.R;
import com.example.materialdesignmusic.Service.MusicPlayService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

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
    private TextView currentTimeView,durationView,totalView;
    private ProgressDialog dialog;
    private LinearLayout totalViewLayout;
    private SeekBar seekBar;
    SongSheetPlayListDetail songSheetPlayListDetail = null;
    private ObjectAnimator objectAnimator = null;
    public static MusicPlayUIReceiver musicPlayUIReceiver = null;
    private long prelongTim = 0;    //定义上次单机的时间
    private Thread threadTime = null;
    static long total = 0;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 2002) {
                totalView.setText(total + "");

            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_playing_activity);
        init();
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

        setHalfTransparent();
        setFitSystemWindow(false);

        Intent intent = getIntent();
        long id = 0;
        if((SongSheetPlayListDetail) intent.getSerializableExtra("object") != null) {
            songSheetPlayListDetail = (SongSheetPlayListDetail) intent.getSerializableExtra("object");
        }else {
            songSheetPlayListDetail = CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex);
        }


        System.out.println(id);
        if(intent.getIntExtra("index",-1) != -1){
            CommonData.musicIndex =intent.getIntExtra("index",-1);
        }else {

        }



        if(CommonData.nowMusicId != songSheetPlayListDetail.getId()) {
            //说明不是当前的音乐id直接切换音乐
        }
        //判断状态
        if(CommonData.mediaPlayer != null){
            if(CommonData.mediaPlayer.isPlaying()) {
                playandpause.setImageResource(R.drawable.pause_white);
            }else {
                playandpause.setImageResource(R.drawable.play_white);
            }
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

//                try {
//            Class clazz = Settings.class;
//            Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
//            Intent intent1 = new Intent(field.get(null).toString());
//            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent1.setData(Uri.parse("package:" + getPackageName()));
//            startActivity(intent1);
//        } catch (Exception e) {
//            Log.e("45", Log.getStackTraceString(e));
//        }

    }

    //获取歌曲详情
    public class GetMusicDetail implements okhttp3.Callback {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            final String strResponse = response.body().string();
            try {
                JSONObject data = new JSONObject(strResponse);
                final long total2 = data.getLong("total");
                System.out.println("歌曲评论 " + total);
                total = total2;
                Message message = new Message();
                message.what = 2002;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //获取歌词
    public class GetMusicLyric implements okhttp3.Callback {

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            final String strResponse = response.body().string();
            CommonData.NowMusicLyricData.clear(); //清空歌词列表
            try {
                JSONObject data = new JSONObject(strResponse);
                JSONObject lrc = data.getJSONObject("lrc");
                String str = lrc.getString("lyric");
                String [] split = str.split("\n");
                for(String s:split) {
//                    System.out.println(s);
                    String[] strK = s.split("]");
                    List<String> bufferK = new ArrayList<>();
                    for(String s2:strK) {
                        bufferK.add(s2.replace("[",""));
                    }
                    for (int i = 0;i < bufferK.size() - 1;i++) {
                        String title = bufferK.get(bufferK.size() - 1);
                        long time = Methods.minToSecond(bufferK.get(i));
                        CommonData.NowMusicLyricData.add(new MusicLyricData(title,time));
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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
                    CommonData.musicIndex -= 1;
                    threadTime = new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(1500);
                                Message msg = new Message();
                                msg.what = 1002;
                                SongSheetListActivity.insthis.handler.sendMessage(msg);
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
                                Message msg = new Message();
                                msg.what = 1002;
                                SongSheetListActivity.insthis.handler.sendMessage(msg);
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
            case R.id.song_comment_layout:
                Intent intent = new Intent(this,CommentDetail.class);
                startActivity(intent);
                System.out.println("6666666666666");
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
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        totalView = findViewById(R.id.song_comment_total);
        totalViewLayout = findViewById(R.id.song_comment_layout);
        totalViewLayout.setOnClickListener(this);



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
                    System.out.println("切歌 哦哦哦");
                    startService(intent);
                }else {
                    //就是第一次播放
                    Intent intent = new Intent(SongPlayActivity.this, MusicPlayService.class);
                    System.out.println("切歌 ddddd");
                    startService(intent);
                }
                //获取歌词
                NetworkUtil.requestUrlToData("/lyric?id=" + CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getId(),new CommonData.GetMusicLyric());

                Message msg = new Message();
                msg.what = 1002;
                SongSheetListActivity.insthis.handler.sendMessage(msg);
                CommonData.nowMusicId = songSheetPlayListDetail.getId();
                dialog.dismiss();
                NetworkUtil.requestUrlToData("/comment/music?id="+CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getId()+"&limit=1",new GetMusicDetail());

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
                    NetworkUtil.requestUrlToData("/comment/music?id="+CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getId()+"&limit=1",new GetMusicDetail());
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
        try{
            unregisterReceiver(musicPlayUIReceiver);
        }catch(IllegalArgumentException e) {

        }


        super.onDestroy();
    }

}
