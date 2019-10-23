package com.example.materialdesignmusic.CommonMethods;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaStorePlayer {
    public static List<String> musicUrl= new ArrayList<>();
    public static int musicIndex = 0;
    public static MediaPlayer mediaPlayer = new MediaPlayer(); //实例化
    MediaStorePlayer(){}
    public static void startMusic() throws IOException {
        //默认第一个
        mediaPlayer.setDataSource(musicUrl.get(musicIndex));
        mediaPlayer.prepare(); // 让MediaPlayer进入到准备状态
        mediaPlayer.start();
    }
    public static void startMusic(int i) throws IOException {
        //默认第一个
        if(0<=i && musicIndex >= i) {
            //播放指定
            play(i);
        }
    }
    public static void lastMusic() throws  IOException{
        if(musicIndex - 1 >= 0) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            musicIndex -= 1;
            play(musicIndex);
        }
    }
    public static void nextMusic() throws IOException {
        if(musicIndex + 1 < musicUrl.size()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            musicIndex += 1;
            play(musicIndex);
        }
    }
    public static void pause() {
        mediaPlayer.pause();
    }
    public static void play(int i) throws IOException {
        mediaPlayer.setDataSource(musicUrl.get(i));
        mediaPlayer.prepare();
        mediaPlayer.start();
    }
    public static void start() {
        mediaPlayer.start();

    }
    public static int getDurection() throws IOException {

        mediaPlayer.stop();
        play(musicIndex);
        return mediaPlayer.getDuration();
    }
    public static boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


}
