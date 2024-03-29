package com.example.materialdesignmusic;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import com.example.materialdesignmusic.Service.TuiJianFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private BottomNavigationView bottomNavigationView;
    private static Fragment[] fragments;
    WindowManager wm;
    WindowManager.LayoutParams layoutParams;
    private static int count = 0;
    public static MusicLyricReceiver musicLyricReceiver = null;
    private FragmentManager fManager; //管理Fragment界面
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fManager = getSupportFragmentManager();
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
        checkPermission();
//        try {
//
//            CommonData.mediaPlayer.setDataSource("https://music.163.com/song/media/outer/url?id=156630.mp3");
//            CommonData.mediaPlayer.prepare();
//            CommonData.mediaPlayer.start();
//        } catch (IOException e) {
//            System.out.println(e);
//            e.printStackTrace();
//        }
        CommonData.init(); //初始化桌面歌词

    }
    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e("45 ", "checkPermission: 已经授权！");
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
        switch(menuItem.getItemId()) {
            case R.id.my:
                fManager.beginTransaction().hide(fragments[1]).commit();
                fManager.beginTransaction().show(fragments[0]).commit();

                break;
            case R.id.music:
                fManager.beginTransaction().hide(fragments[0]).commit();
                fManager.beginTransaction().show(fragments[1]).commit();

                break;

        }
        return true;
    }
    private void initFragment() {
        MyFragment myFragment = new MyFragment();
        TuiJianFragment tuiJianFragment = new TuiJianFragment();

        fragments = new Fragment[]{myFragment,tuiJianFragment};
        fManager.beginTransaction().replace(R.id.fragment_main_layout,myFragment)
                .show(myFragment).commit();
        fManager.beginTransaction().add(R.id.fragment_main_layout,tuiJianFragment).commit();

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

        Intent intent = new Intent(this, MusicPlayService.class);
        stopService(intent);
        CommonData.wm.removeView(CommonData.musicTextView);
    }
}
