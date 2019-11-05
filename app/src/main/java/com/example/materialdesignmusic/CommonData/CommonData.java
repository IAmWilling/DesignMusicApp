package com.example.materialdesignmusic.CommonData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.materialdesignmusic.BroadCastService.MusicUIBroadCastReceiver;
import com.example.materialdesignmusic.CommonMethods.Methods;
import com.example.materialdesignmusic.JSONDATA.MusicListInfo;
import com.example.materialdesignmusic.JSONDATA.MusicLyricData;
import com.example.materialdesignmusic.JSONDATA.SongSheetPlayListDetail;
import com.example.materialdesignmusic.Notice.PlayNotification;
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

public class CommonData {
    public static String COOKIE = "";
    public static String ONE_LOOP = "one_loop"; //单曲循环
    public static String LIST_LOOP = "list_loop"; //列表循环
    public static String ORDER_PLAY = "order_play";//顺序播放
    public static String ROUND_PLAY = "round_play"; //随机播放
    public static String LOCALHOST= "http://10.2.4.19:3000";
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
    public static  List<MusicLyricData> NowMusicLyricData = new ArrayList<>(); //当前歌词对象list
    public static Bitmap nowMusicBitmap;
    public static WindowManager wm = (WindowManager) MyApplication.getContext().getSystemService(Context.WINDOW_SERVICE);;
    public static WindowManager.LayoutParams layoutParams;
    public static TextView musicTextView = null; //桌面歌词view
    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void init() {
        musicTextView = new TextView(MyApplication.getContext());
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        layoutParams.alpha = 1;
        layoutParams.y = 500;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;;
        //这个Gravity也不能少，不然的话，下面"移动歌词"的时候就会出问题了～ 可以试试[官网文档有说明]
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.format = PixelFormat.TRANSLUCENT; //设置透明背景色 默认是黑色
        musicTextView.setTextColor(Color.rgb(198,47,47));
        musicTextView.setGravity(Gravity.CENTER);
        musicTextView.setShadowLayer(10.0f,5.0f,5.0f,Color.argb(190,198,47,47));
        musicTextView.setTextSize(18);
        musicTextView.setElevation(5.0f);
        musicTextView.setText("等待音乐呦~");
        musicTextView.setBackgroundColor(Color.argb(1,255,255,255));
        wm.addView(musicTextView,layoutParams);
    }
    public static class GetMusicLyric implements okhttp3.Callback {


        @Override
        public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
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

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

        }
    }
}
