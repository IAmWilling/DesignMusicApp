package com.example.materialdesignmusic.Notice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.materialdesignmusic.Activity.SongPlayActivity;
import com.example.materialdesignmusic.Activity.SongSheetListActivity;
import com.example.materialdesignmusic.CommonData.CommonData;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.CommonMethods.BitmapMethods;
import com.example.materialdesignmusic.JSONDATA.SongSheetPlayListDetail;
import com.example.materialdesignmusic.R;
import com.example.materialdesignmusic.Service.MusicPlayService;

import java.util.List;

public class PlayNotification {
    public Notification notification;
    private static NotificationManager manager =(NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    public static RemoteViews notificationView = new RemoteViews(MyApplication.getContext().getPackageName(),R.layout.play_notice_bar_view);
    public static RemoteViews notificationViewSmall = new RemoteViews(MyApplication.getContext().getPackageName(),R.layout.play_notice_bar_small);
    private Thread threadTime = null;
    public PlayNotification(){
        //判断SDK
        //判断当前设备版本
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //设置通知渠道
            this.setNotificationChannel();
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("playnotice","音乐播放器", NotificationManager.IMPORTANCE_HIGH);
        channel.setBypassDnd(true); //设置绕过免打扰模式
        channel.canBypassDnd(); //检测是否绕过免打扰模式
        channel.setSound(null,null);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);//设置在锁屏界面上显示这条通知
        manager.createNotificationChannel(channel);
    }
    public void initReceiver() {
        //注册更新通知栏广播
        MyChangeNoticReceiver myChangeNoticReceiver = new MyChangeNoticReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.materialdesignmusic.playnnotification");
        MyApplication.getContext().registerReceiver(myChangeNoticReceiver,intentFilter);
    }
    public PendingIntent getDefaultIntent(int flags) {
        Intent intent = new Intent(MyApplication.getContext(),SongPlayActivity.class);
        intent.putExtra("object",CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex));
        intent.putExtra("index",CommonData.musicIndex);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.getContext(),
                0,intent,flags);
        return pendingIntent;
    }
    public void start(){
        manager.notify(1,notification);
    }
    public void show(){
        initReceiver();
        Intent startIntent = new Intent(MyApplication.getContext(),SongPlayActivity.class);
//        startIntent.putExtra("object",CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex));
//        startIntent.putExtra("index",CommonData.musicIndex);
        PendingIntent pendingIntentActivity = PendingIntent.getActivity(MyApplication.getContext(),100,startIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new NotificationCompat.Builder(MyApplication.getContext(),"playnotice")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.ic_launcher))
//                .setAutoCancel(true)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setCustomBigContentView(notificationView)
                .setCustomContentView(notificationViewSmall)
                .setPriority(NotificationCompat.PRIORITY_MAX)//设置最大优先级
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntentActivity)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;


        Intent intent = new Intent("com.example.materialdesignmusic.playnnotification");
        intent.putExtra("type",1);//上一首
        PendingIntent pendingIntentPrev = PendingIntent.getBroadcast(MyApplication.getContext(),1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.notic_btn_previous,pendingIntentPrev);


        intent.putExtra("type",2);//暂停或播放
        PendingIntent pendingIntentpauseplay = PendingIntent.getBroadcast(MyApplication.getContext(),2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.notic_btn_playandpause,pendingIntentpauseplay);
        notificationViewSmall.setOnClickPendingIntent(R.id.notic_btn_playandpause_small,pendingIntentpauseplay);

        intent.putExtra("type",3);//下一首
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(MyApplication.getContext(),3,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationView.setOnClickPendingIntent(R.id.notic_btn_next,pendingIntentNext);
        notificationViewSmall.setOnClickPendingIntent(R.id.notic_btn_next_small,pendingIntentNext);

//        intent.putExtra("type",50);//下一首
//        PendingIntent pendingIntentActivity = PendingIntent.getBroadcast(MyApplication.getContext(),50,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        notificationView.setOnClickPendingIntent(R.id.notic_actiity,pendingIntentActivity);


    }
    /**
     * 更新通知栏UI
     * */
    public class MyChangeNoticReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type",0);
            switch (type) {
                case 1:
                    //上一首切换
                    if(CommonData.musicIndex - 1 < 0) {

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
                                    Thread.sleep(1000);
                                    Message msg = new Message();
                                    msg.what = 1002;
                                    SongSheetListActivity.insthis.handler.sendMessage(msg);
                                    Intent intent1 = new Intent(MyApplication.getContext(), MusicPlayService.class);
                                    //发送切歌服务 上一首
                                    intent1.putExtra("type",2);
                                    MyApplication.getContext().startService(intent1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        threadTime.start();

                    }
                    break;
                case 2:
                    //暂停或播放
                    if(CommonData.mediaPlayer.isPlaying()) {
                        CommonData.mediaPlayer.pause();
                        notificationView.setImageViewResource(R.id.notic_btn_playandpause,R.drawable.play_gray);
                        notificationViewSmall.setImageViewResource(R.id.notic_btn_playandpause_small,R.drawable.play_gray);
                    }else {

                        CommonData.mediaPlayer.start();
                        notificationView.setImageViewResource(R.id.notic_btn_playandpause,R.drawable.pause_gray);
                        notificationViewSmall.setImageViewResource(R.id.notic_btn_playandpause_small,R.drawable.pause_gray);
                    }
                    //发送给SongPlayActivity 广播接收变化
                    Intent playandpauseIntent = new Intent();
                    playandpauseIntent.setAction("com.example.materialdesignmusic.musicui");
                    playandpauseIntent.putExtra("type", "playandpause");
                    MyApplication.getContext().sendBroadcast(playandpauseIntent);
                    manager.notify(1,notification);
                    break;
                case 3:
                    //下一首切换
                    if(CommonData.musicIndex + 1 > CommonData.commonSongSheetPlayListDetailList.size()) {
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
                                    Thread.sleep(1000);
                                    Message msg = new Message();
                                    msg.what = 1002;
                                    SongSheetListActivity.insthis.handler.sendMessage(msg);
                                    Intent intent1 = new Intent(MyApplication.getContext(), MusicPlayService.class);
                                    //发送切歌服务
                                    intent1.putExtra("type",3);
                                    MyApplication.getContext().startService(intent1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        threadTime.start();

                    }
                    break;
                case 4://表示切换 暂停或播放状态
                    if(CommonData.mediaPlayer.isPlaying()) {
                        notificationView.setImageViewResource(R.id.notic_btn_playandpause,R.drawable.pause_gray);
                        notificationViewSmall.setImageViewResource(R.id.notic_btn_playandpause_small,R.drawable.pause_gray);

                    }else {
                        notificationView.setImageViewResource(R.id.notic_btn_playandpause,R.drawable.play_gray);
                        notificationViewSmall.setImageViewResource(R.id.notic_btn_playandpause_small,R.drawable.play_gray);
                    }
                    manager.notify(1,notification);
                    break;
                case 1001:
                    notificationView.setTextViewText(R.id.notic_music_name,CommonData.nowMusicName);
                    notificationViewSmall.setTextViewText(R.id.notic_music_name_small,CommonData.nowMusicName);
                    String zj = "";
                    List<SongSheetPlayListDetail.ArBean> arBeans = (List<SongSheetPlayListDetail.ArBean>) CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getArList();
                    for(int i = 0;i < arBeans.size(); i++) {
                        if(i != arBeans.size() - 1) {
                            zj += arBeans.get(i).getName() + "/";
                        }else {
                            zj += arBeans.get(i).getName();
                        }
                    }
                    notificationView.setTextViewText(R.id.notic_music_detail_name,zj+ " - " + ((SongSheetPlayListDetail.AlBean)CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getAl()).getName());
                    notificationViewSmall.setTextViewText(R.id.notic_music_detail_name_small,zj+ " - " + ((SongSheetPlayListDetail.AlBean)CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getAl()).getName());
                    Glide.with(context).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).load(CommonData.nowPicUrl)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    notificationView.setImageViewBitmap(R.id.notic_img,resource);
                                    notificationViewSmall.setImageViewBitmap(R.id.notic_img_small,resource);
                                    manager.notify(1,notification);
                                }
                            });
                    manager.notify(1,notification);
                    break;
                case 50:
//                    Intent intent4 = new Intent(MyApplication.getContext(),SongPlayActivity.class);
//                    final SongSheetPlayListDetail songSheetPlayListDetail = CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex);
//                    System.out.println(songSheetPlayListDetail.getId());
//                    intent.putExtra("object",songSheetPlayListDetail);
//                    intent.putExtra("index",CommonData.musicIndex);
//                    MyApplication.getContext().startActivity(intent4);
                    break;
            }
        }
    }
}
