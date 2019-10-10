package com.example.materialdesignmusic;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.materialdesignmusic.Activity.SongPlayActivity;
import com.example.materialdesignmusic.CommonData.CommonData;
import com.example.materialdesignmusic.Notice.PlayNotification;
import com.example.materialdesignmusic.Service.MusicPlayService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private BottomNavigationView bottomNavigationView;
    private static Fragment[] fragments;
    WindowManager wm;
    WindowManager.LayoutParams layoutParams;
    private static int count = 0;
    public static MusicLyricReceiver musicLyricReceiver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.design_navigation_view1);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        initFragment();
        /*
         * 注册广播服务
         * */
        musicLyricReceiver = new MusicLyricReceiver();
        //注册广播服务
        IntentFilter intentFilter = new IntentFilter();
        //更新有UI定义广播过滤
        intentFilter.addAction("com.example.materialdesignmusic.musiclyric");
        //注册服务
        registerReceiver(musicLyricReceiver,intentFilter);
        CommonData.init(); //初始化桌面歌词

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
        switch(menuItem.getItemId()) {
            case R.id.my:
                break;
        }
        return true;
    }
    private void initFragment() {
        MyFragment myFragment = new MyFragment();
        fragments = new Fragment[]{myFragment};
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main_layout,myFragment)
                .show(myFragment).commit();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /*
     * 广播
     * */
    public class MusicLyricReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            switch(type) {
                case "musiclyric":
                    for (int i = 0;i < CommonData.NowMusicLyricData.size();i++) {
                        long dt = Float.valueOf(CommonData.mediaPlayer.getCurrentPosition() / 1000).longValue();
                        if(dt == CommonData.NowMusicLyricData.get(i).getTime()) {
                            System.out.println(CommonData.NowMusicLyricData.get(i).getTitle());
                            CommonData.musicTextView.setText(CommonData.NowMusicLyricData.get(i).getTitle() + "");
                            CommonData.wm.updateViewLayout(CommonData.musicTextView,CommonData.layoutParams);
                        }
                    }
                    break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("结束进程1111");
        Intent intent = new Intent(this, MusicPlayService.class);
        stopService(intent);
    }
}
