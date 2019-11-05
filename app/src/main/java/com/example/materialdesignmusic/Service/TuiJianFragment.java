package com.example.materialdesignmusic.Service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.materialdesignmusic.Apdater.RecommendSongList;
import com.example.materialdesignmusic.CommonData.CommonData;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.JSONDATA.BannerData;
import com.example.materialdesignmusic.JSONDATA.RecommendSongListData;
import com.example.materialdesignmusic.JSONDATA.UserInfo;
import com.example.materialdesignmusic.MyViewPager.LoopViewPagerAdapter;
import com.example.materialdesignmusic.NetWorkUtil.NetworkUtil;
import com.example.materialdesignmusic.R;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

public class TuiJianFragment extends Fragment {
    private List<BannerData>bannerDataList = new ArrayList<>();
    private List<RecommendSongListData> recommendSongListDataList = new ArrayList<>();
    private ViewPager loopViewPager;
    private RecyclerView recommendRecoundView;
    private static RecommendSongList recommendSongListAdapter;
    private int bannerIndex = 0;
    private static Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 4001) {
                loopViewPager.setAdapter(new LoopViewPagerAdapter(bannerDataList));

            }
            if(msg.what == 4004) {
                List<RecommendSongListData> list = new ArrayList<>();
                for(int i = 0;i < 6;i++) {
                    list.add(recommendSongListDataList.get(i));
                }
                recommendRecoundView.setNestedScrollingEnabled(false);
                recommendSongListAdapter = new RecommendSongList(list);
                recommendRecoundView.setLayoutManager( new GridLayoutManager(MyApplication.getContext(), 3 ));
                recommendRecoundView.setAdapter(recommendSongListAdapter);
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tuijian_fragment,container,false);
        loopViewPager = view.findViewById(R.id.loopviewpager);
        recommendRecoundView = view.findViewById(R.id.tuijian_music_recyclerview);
        loopViewPager.setPageMargin(30);
        loopViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        NetworkUtil.requestUrlToData("/banner?type=1",new GetMusicBanner());
        NetworkUtil.requestUrlToData("/login/cellphone?phone=18931893326&password=z123456789",new GetUserInfo2());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * 获取个人信息
     * */
    private class GetUserInfo2 implements okhttp3.Callback {

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println(e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            Headers headers = response.headers();
            List<String> strings = headers.values("Set-Cookie");
            String cookie = "";
            for(String i : strings) {
                String str[] = i.split(";");
                cookie += str[0] + ";";
            }
            CommonData.COOKIE = cookie;
            NetworkUtil.requestUrlToData("/recommend/resource?timestamp=" + new Date().getTime(),new GetRecommendSongList());


        }
    }



    /*
    * 获取 推荐歌单
    * */
    private class GetRecommendSongList implements okhttp3.Callback {

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("推荐歌单 出现错误 " + e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String responsestring = response.body().string();
            System.out.println(responsestring);
            Gson gson = new Gson();
            try {
                JSONObject data = new JSONObject(responsestring);
                JSONArray recommend = data.getJSONArray("recommend");
                for(int i = 0;i < recommend.length();i++) {
                    JSONObject item = recommend.getJSONObject(i);
                    System.out.println(item);
                    RecommendSongListData recommendSongListData = gson.fromJson(item.toString(), RecommendSongListData.class);
                    recommendSongListDataList.add(recommendSongListData);
                }
                Message message = new Message();
                message.what = 4004;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class GetMusicBanner implements okhttp3.Callback{
        public GetMusicBanner(){

        }
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("获取轮播图数据失败 " + e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String responsestring = response.body().string();
            Gson gson = new Gson();
            try {
                JSONObject data = new JSONObject(responsestring);
                JSONArray banners = data.getJSONArray("banners");
                for(int i = 0;i < banners.length();i++) {
                    JSONObject item = banners.getJSONObject(i);
                    BannerData bannerData = gson.fromJson(item.toString(), BannerData.class);
                    bannerDataList.add(bannerData);
                    Message message = new Message();
                    message.what = 4001;
                    handler.sendMessage(message);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
