package com.example.materialdesignmusic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.materialdesignmusic.Activity.SongSheetListActivity;
import com.example.materialdesignmusic.Apdater.SongSheetInfoApdater;
import com.example.materialdesignmusic.CommonData.CommonData;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.JSONDATA.SongSheetInfo;
import com.example.materialdesignmusic.JSONDATA.UserInfo;
import com.example.materialdesignmusic.NetWorkUtil.NetworkUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyFragment extends Fragment implements View.OnClickListener{
    //旋转度数 判定动画标准
    private int imgCaleMyCreateSongImageType = 0,imgCaleollectionSongImageType = 0;
    /**
     * view 视图id
     * */
    private LinearLayout userInfoView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView userImage,collierMyCreateSongImag,collierMyCollectionSongImage;
    private TextView userNickNameText,userSignstrText,myCreateSongSheetCountText,myCollectionSongSheetCountText;
    private LinearLayout collierMyCreateSonglayout,collierMyCollectionSonglayout;
    private RecyclerView myCreateSongSheetRView,myCollectionSongSheetRView;
    public ImageView songSheetCover; //封面
    public TextView title,trackCount; //标题和几首歌
    /**
     * 数据对象结构映射
     * */
    private UserInfo userInfo; //个人信息对象
    private List<SongSheetInfo> songSheetInfoArray = new ArrayList<>(); //收藏和创建歌单的映射对象
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                //更新UI
                Glide.with(MyApplication.getContext())
                        .load(userInfo.getAvatarUrl())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(userImage);
                userNickNameText.setText("昵称：" + userInfo.getNickname());
                userSignstrText.setText("简介：" + userInfo.getSignature());
            }
            if(msg.what == 2) {
                changeSongSheetViewUI();



            }
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_fragment_item,container,false);
        viewLayoutInit(view);
        userInfoView.setOnClickListener(this);
        String url = "/login/cellphone?phone=18931893326&password=z123456789";
        NetworkUtil.requestUrlToData(url, new GetUserInfo());

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.user_info_view) {
            drawerLayout.openDrawer(navigationView);
        }
        switch(view.getId()) {
            case R.id.my_create_song_sheet:
                RotateAnimation rotateAnimation = null;
                ScaleAnimation scaleAnimation = null;
                if(imgCaleMyCreateSongImageType == 0) {
                    rotateAnimation = new RotateAnimation(0,90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    imgCaleMyCreateSongImageType = 90;
                }else if(imgCaleMyCreateSongImageType == 90){
                    rotateAnimation = new RotateAnimation(90,0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    imgCaleMyCreateSongImageType = 0;
                }
                rotateAnimation.setDuration(300);
                rotateAnimation.setFillAfter(true);
                collierMyCreateSongImag.startAnimation(rotateAnimation);
                break;
            case R.id.my_collection_song_sheet:
                RotateAnimation rotateAnimationcol = null;
                if(imgCaleollectionSongImageType == 0) {
                    rotateAnimationcol = new RotateAnimation(0,90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    imgCaleollectionSongImageType = 90;
                }else if(imgCaleollectionSongImageType == 90){
                    rotateAnimationcol = new RotateAnimation(90,0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    imgCaleollectionSongImageType = 0;
                }
                rotateAnimationcol.setDuration(300);
                rotateAnimationcol.setFillAfter(true);
                collierMyCollectionSongImage.startAnimation(rotateAnimationcol);
                break;


        }
    }
    /**
     * 获取个人信息
     * */
    private class GetUserInfo implements okhttp3.Callback {

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println(e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            final String strResponse = response.body().string();

            Gson gson = new Gson();
            try {
                JSONObject jsonObject = new JSONObject(strResponse);
                JSONObject profile = jsonObject.getJSONObject("profile");

                userInfo = gson.fromJson(profile.toString(), UserInfo.class);
                //获取Uid

                CommonData.userID = userInfo.getUserId();

                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
                NetworkUtil.requestUrlToData("/user/playlist?uid=" + CommonData.userID, new GetSongSheetInfo());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
    /**
     * 获取我的歌单列表信息
     * */

    private class GetSongSheetInfo implements okhttp3.Callback{

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("GetSongSheetInfo 发生异常" + e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            final String strResponse = response.body().string();
            System.out.println(strResponse);
            Gson gson = new Gson();
            try {
                JSONObject jsonObject = new JSONObject(strResponse);
                JSONArray profile = jsonObject.getJSONArray("playlist");
                for(int i = 0;i < profile.length();i++) {
                    JSONObject item = profile.getJSONObject(i);

                    SongSheetInfo songSheetInfo = gson.fromJson(item.toString(),SongSheetInfo.class);
                    songSheetInfoArray.add(songSheetInfo);
                }
                //获取Uid
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void viewLayoutInit(View view) {
        userInfoView = view.findViewById(R.id.user_info_view);
        navigationView = view.findViewById(R.id.design_navigation_view2);
        drawerLayout = view.findViewById(R.id.drawer);
        userImage = view.findViewById(R.id.user_image);
        userNickNameText = view.findViewById(R.id.user_nickname);
        userSignstrText = view.findViewById(R.id.user_signature);
        collierMyCreateSonglayout = view.findViewById(R.id.my_create_song_sheet);
        collierMyCreateSonglayout.setOnClickListener(this); //歌单展开的点击事件
        collierMyCollectionSonglayout = view.findViewById(R.id.my_collection_song_sheet);
        collierMyCollectionSonglayout.setOnClickListener(this);//歌单展开的点击事件
        collierMyCreateSongImag = view.findViewById(R.id.my_create_song_sheet_image);
        collierMyCollectionSongImage = view.findViewById(R.id.my_collection_song_sheet_image);
        myCreateSongSheetCountText = view.findViewById(R.id.my_create_song_sheet_count);
        myCollectionSongSheetCountText = view.findViewById(R.id.my_collection_song_sheet_count);
        myCollectionSongSheetRView = view.findViewById(R.id.my_collection_song_recyclerlist);
        myCreateSongSheetRView = view.findViewById(R.id.my_create_song_recyclerlist);

    }
    /**
     * 更新歌单UI view
     * */
    private void changeSongSheetViewUI() {
        int count = 0;
        List<SongSheetInfo> myCreateArraySongSheet = new ArrayList<>();
        List<SongSheetInfo> myCollectionArraySongSheet = new ArrayList<>();
        for(SongSheetInfo songSheetInfo : songSheetInfoArray) {
            if(songSheetInfo.getCreator().getUserId() == CommonData.userID) {
                count += 1;
                myCreateArraySongSheet.add(songSheetInfo);
            }else {
                myCollectionArraySongSheet.add(songSheetInfo);
            }
        }
        myCreateSongSheetCountText.setText("(" + count + ")");
        myCollectionSongSheetCountText.setText("(" + (songSheetInfoArray.size() - count) + ")");
        myCreateSongSheetRView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        myCollectionSongSheetRView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        myCollectionSongSheetRView.setNestedScrollingEnabled(false);
        myCreateSongSheetRView.setNestedScrollingEnabled(false);
        SongSheetInfoApdater myCreateSongSheetInfoApdater = new SongSheetInfoApdater(myCreateArraySongSheet,getActivity());
        SongSheetInfoApdater  myCollectionSongSheetInfoApdater = new SongSheetInfoApdater(myCollectionArraySongSheet,getActivity());
        myCreateSongSheetRView.setAdapter(myCreateSongSheetInfoApdater);
        myCollectionSongSheetRView.setAdapter(myCollectionSongSheetInfoApdater);

    }
}
