package com.example.materialdesignmusic.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.materialdesignmusic.Activity.SongPlayActivity;
import com.example.materialdesignmusic.CommonData.CommonData;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.Notice.PlayNotification;

import java.io.IOException;
import java.util.Random;

public class MusicPlayService extends Service {
    private static Thread thread = null;
    private static volatile Boolean exitFlag = false;    //线程是否终止
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public class LocalBinder extends Binder {
        MusicPlayService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicPlayService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if(CommonData.playNotification == null) {
            CommonData.playNotification  = new PlayNotification();
            CommonData.playNotification.show();
//            CommonData.playNotification.start();
            startForeground(1,CommonData.playNotification.notification);
        }
        Toast.makeText(MyApplication.getContext(), "服务启动", Toast.LENGTH_LONG)
                .show();
        if (thread == null) {
            thread = new MusicPlayThread();
            thread.start();
            CommonData.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    System.out.println("结束onCreate");
                    Intent playandpauseIntent = new Intent();
                    playandpauseIntent.setAction("com.example.materialdesignmusic.musicui");
                    playandpauseIntent.putExtra("type", "playandpause");
                    MyApplication.getContext().sendBroadcast(playandpauseIntent);
                    if (CommonData.PlayType.equals(CommonData.LIST_LOOP)) {
                        CommonData.mediaPlayer.setLooping(false);
                        if(CommonData.musicIndex + 1 >= CommonData.commonSongSheetPlayListDetailList.size()) {
                            CommonData.musicIndex = 0;
                        }else {
                            CommonData.musicIndex += 1;
                        }
                        System.out.println("列表循环");
                    }else if(CommonData.PlayType.equals(CommonData.ONE_LOOP)) {
                        CommonData.mediaPlayer.setLooping(true);
                        System.out.println("单曲循环");
                    }
                    exitFlag = true;
                    thread.interrupt();
                    exitFlag = false;
                    thread = new MusicPlayThread();
                    thread.start();
                }
            });
            CommonData.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return true;
                }
            });


        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        int type = intent.getIntExtra("type", 0);
        switch (type) {
            case 1:
                exitFlag = false;
                thread.interrupt();
                thread = new MusicPlayThread();
                thread.start();
                CommonData.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        System.out.println("结束onStartCommand");
                        Intent playandpauseIntent = new Intent();
                        playandpauseIntent.setAction("com.example.materialdesignmusic.musicui");
                        playandpauseIntent.putExtra("type", "playandpause");
                        MyApplication.getContext().sendBroadcast(playandpauseIntent);
                        if (CommonData.PlayType.equals(CommonData.LIST_LOOP)) {
                            CommonData.mediaPlayer.setLooping(false);
                            if(CommonData.musicIndex + 1 >= CommonData.commonSongSheetPlayListDetailList.size()) {
                                CommonData.musicIndex = 0;
                            }else {
                                CommonData.musicIndex += 1;
                            }
                            System.out.println("列表循环");
                        }else if(CommonData.PlayType.equals(CommonData.ONE_LOOP)) {
                            CommonData.mediaPlayer.setLooping(true);
                            System.out.println("单曲循环");
                        }
                            exitFlag = true;
                            thread.interrupt();
                            exitFlag = false;
                            thread = new MusicPlayThread();
                            thread.start();

                    }
                });
                CommonData.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                        return true;
                    }
                });
                break;
            case 2:
                //上一首
                exitFlag = false;
                thread.interrupt();
               Log.d("CommonData.musicIndex ",CommonData.musicIndex + "");
                thread = new MusicPlayThread();
                thread.start();
                break;
            case 3:
                //下一首
                exitFlag = false;
                thread.interrupt();

                thread = new MusicPlayThread();
                thread.start();
                break;
        }
        return Service.START_STICKY;

    }

    //音乐播放线程
    public static class MusicPlayThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                System.out.println("CommonData.musicIndex " + CommonData.musicIndex + "   " + CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getId() + " size=" + CommonData.commonSongSheetPlayListDetailList.size());
                String url = "https://music.163.com/song/media/outer/url?id=" + CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getId() + ".mp3";

                CommonData.mediaPlayer.stop();
                CommonData.mediaPlayer.reset();
                CommonData.mediaPlayer.setDataSource(url);
                System.out.println("线程启动" + CommonData.mediaPlayer);
                CommonData.mediaPlayer.prepareAsync();
                CommonData.mediaPlayer.setOnPreparedListener(new ListenerSourceOnLoad());
                while ((CommonData.mediaPlayer.getDuration() >= CommonData.mediaPlayer.getCurrentPosition()) && !exitFlag) {
                    Thread.sleep(1000);
                    Intent seekbarIntent = new Intent();
                    seekbarIntent.setAction("com.example.materialdesignmusic.musicui");
                    seekbarIntent.putExtra("type", "seekbar");
                    MyApplication.getContext().sendBroadcast(seekbarIntent);
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /*
     * 监听播放资源已加载的监听
     * */
    private static class ListenerSourceOnLoad implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {

            //开始播放
            CommonData.mediaPlayer.start();
            Intent playandpauseIntent = new Intent();
            playandpauseIntent.setAction("com.example.materialdesignmusic.musicui");
            CommonData.nowMusicId = CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getId();
            CommonData.nowMusicName = CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getName();
            CommonData.nowPicUrl = CommonData.commonSongSheetPlayListDetailList.get(CommonData.musicIndex).getAl().getPicUrl();
            Intent noticIntent = new Intent("com.example.materialdesignmusic.playnnotification");

            noticIntent.putExtra("type",1001);   //1001更换封面
            MyApplication.getContext().sendBroadcast(noticIntent);
            noticIntent.putExtra("type",4);
            MyApplication.getContext().sendBroadcast(noticIntent);
            if (!CommonData.PlayType.equals(CommonData.ONE_LOOP)) {
                playandpauseIntent.putExtra("type", "change"); //切换封面
                MyApplication.getContext().sendBroadcast(playandpauseIntent);
            }

            playandpauseIntent.putExtra("type", "playandpause");
            MyApplication.getContext().sendBroadcast(playandpauseIntent);

        }
    }


    @Override
    public void onDestroy() {
        exitFlag = true;
        thread.interrupt();
        stopForeground(true);
        System.out.println("服务关闭");
        super.onDestroy();
    }
}
