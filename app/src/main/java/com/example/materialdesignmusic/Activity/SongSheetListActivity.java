package com.example.materialdesignmusic.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.materialdesignmusic.Apdater.SongSheetPlayListInfoAdpater;
import com.example.materialdesignmusic.CommonData.CommonData;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.CommonMethods.BitmapMethods;
import com.example.materialdesignmusic.JSONDATA.SongSheetInfo;
import com.example.materialdesignmusic.JSONDATA.SongSheetPlayListDetail;
import com.example.materialdesignmusic.NetWorkUtil.NetworkUtil;
import com.example.materialdesignmusic.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class SongSheetListActivity extends Activity {
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private List<SongSheetPlayListDetail> songSheetPlayListDetailList = new ArrayList<>();
    ProgressDialog dialog;
    SongSheetInfo songSheetInfo;
    public static SongSheetListActivity insthis = null;
    private static SongSheetPlayListInfoAdpater songSheetPlayListInfoAdpater;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1001:
                    recyclerView.setItemViewCacheSize(50);
                    recyclerView.setAdapter(songSheetPlayListInfoAdpater);
                    dialog.dismiss();
                    break;
                case 1002:

                    songSheetPlayListInfoAdpater.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.song_sheet_list_activity);
        insthis = this;
        setHalfTransparent();
        setFitSystemWindow(false);
        toolbar = findViewById(R.id.song_sheet_toolbar4);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.song_sheet_list_recyclerview_detail);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (firstItemPosition >= 1 && toolbar.getVisibility() == View.GONE) {
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitle(songSheetInfo.getName());
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                    alphaAnimation.setFillAfter(true);
                    alphaAnimation.setDuration(300);
                    toolbar.startAnimation(alphaAnimation);

                } else if (firstItemPosition == 0 && toolbar.getVisibility() == View.VISIBLE) {
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
                    alphaAnimation.setFillAfter(true);
                    alphaAnimation.setDuration(300);
                    toolbar.startAnimation(alphaAnimation);
                    toolbar.setVisibility(View.GONE);
                }
            }
        });


        Intent intent = getIntent();
        songSheetInfo = (SongSheetInfo) intent.getSerializableExtra("object");
        dialog = ProgressDialog.show(this, "歌曲", "歌曲正在加载中...");
        NetworkUtil.requestUrlToData("/playlist/detail?id=" + songSheetInfo.getId(), new GetSongPlaylistDeatil());

    }

    /**
     * 请求歌单列表详情数据
     */
    public class GetSongPlaylistDeatil implements okhttp3.Callback {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println(e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            final String strResponse = response.body().string();
            Gson gson = new Gson();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(strResponse);
                JSONObject profile = jsonObject.getJSONObject("playlist");
                long shareCount = profile.getLong("shareCount");
                long commentCount = profile.getLong("commentCount");
                JSONArray tracks = profile.getJSONArray("tracks");
                for (int i = 0; i < tracks.length(); i++) {
                    JSONObject item = tracks.getJSONObject(i);
                    SongSheetPlayListDetail songSheetPlayListDetail = new SongSheetPlayListDetail();
                    songSheetPlayListDetail.setShareCount(shareCount);
                    songSheetPlayListDetail.setCommentCount(commentCount);
                    songSheetPlayListDetail.setId(item.getLong("id"));
                    songSheetPlayListDetail.setName(item.getString("name"));
                    songSheetPlayListDetail.setMv(item.getLong("mv"));
                    SongSheetPlayListDetail.AlBean alBean = new SongSheetPlayListDetail.AlBean();
                    JSONObject al = item.getJSONObject("al");
                    alBean.setId(al.getLong("id"));
                    alBean.setName(al.getString("name"));
                    alBean.setPicUrl(al.getString("picUrl"));
                    songSheetPlayListDetail.setAl(alBean);
                    JSONArray ar = item.getJSONArray("ar");
                    List<SongSheetPlayListDetail.ArBean> arBean = new ArrayList<>();
                    for (int j = 0; j < ar.length(); j++) {
                        JSONObject arItem = ar.getJSONObject(j);
                        SongSheetPlayListDetail.ArBean ari = new SongSheetPlayListDetail.ArBean();
                        ari.setId(arItem.getLong("id"));
                        ari.setName(arItem.getString("name"));
                        arBean.add(ari);
                    }
                    songSheetPlayListDetail.setArList(arBean);
                    songSheetPlayListDetailList.add(songSheetPlayListDetail);
                }
                songSheetPlayListInfoAdpater = new SongSheetPlayListInfoAdpater(songSheetPlayListDetailList, songSheetInfo, getApplicationContext());
                Message message = new Message();
                message.what = 1001;
                handler.sendMessage(message);
            } catch (JSONException e) {
                System.out.println(e);
                e.printStackTrace();
            }

        }
    }

    /**
     * 全透状态栏
     */
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
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


}
