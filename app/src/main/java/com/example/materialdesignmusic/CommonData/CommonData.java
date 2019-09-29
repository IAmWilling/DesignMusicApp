package com.example.materialdesignmusic.CommonData;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

import com.example.materialdesignmusic.BroadCastService.MusicUIBroadCastReceiver;
import com.example.materialdesignmusic.JSONDATA.MusicListInfo;
import com.example.materialdesignmusic.JSONDATA.SongSheetPlayListDetail;
import com.example.materialdesignmusic.Notice.PlayNotification;
import com.example.materialdesignmusic.Service.MusicPlayService;

import java.util.ArrayList;
import java.util.List;

public class CommonData {
    public static String ONE_LOOP = "one_loop"; //单曲循环
    public static String LIST_LOOP = "list_loop"; //列表循环
    public static String ORDER_PLAY = "order_play";//顺序播放
    public static String ROUND_PLAY = "round_play"; //随机播放
    public static String LOCALHOST= "http://10.2.4.21:3000";
    public static int userID = 0;
    public static final MusicPlayService musicPlayService = new MusicPlayService(); //后台服务
    public static final MusicUIBroadCastReceiver musicUIReceiver = new MusicUIBroadCastReceiver(); //广播实例
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static List<MusicListInfo> musicUrlArray = new ArrayList<>(); //播放音乐列表
    public static int musicIndex = 0; //播放音乐索引
    public static long nowMusicId = 0; //当前歌曲名称
    public static String nowMusicName = ""; //当前歌曲名字
    public static String nowPicUrl = "";    //当前歌曲封面
    public static List<SongSheetPlayListDetail> commonSongSheetPlayListDetailList = new ArrayList<>();
    public static String PlayType = LIST_LOOP; //歌曲播放类型
    public static PlayNotification playNotification = null;

    public static Bitmap nowMusicBitmap;
}
