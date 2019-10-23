package com.example.materialdesignmusic.Service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.materialdesignmusic.JSONDATA.BannerData;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class TuiJianFragment extends Fragment {
    private List<BannerData>bannerDataList = new ArrayList<>();
    private ViewPager loopViewPager;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 4001) {
                loopViewPager.setAdapter(new LoopViewPagerAdapter(bannerDataList));
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tuijian_fragment,container,false);
        loopViewPager = view.findViewById(R.id.loopviewpager);
        NetworkUtil.requestUrlToData("/banner?type=1",new GetMusicBanner());
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
