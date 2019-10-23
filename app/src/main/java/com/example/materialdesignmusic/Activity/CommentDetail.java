package com.example.materialdesignmusic.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.materialdesignmusic.Apdater.CommentDataAdoater;
import com.example.materialdesignmusic.CommonData.CommonData;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.JSONDATA.CommentData;
import com.example.materialdesignmusic.JSONDATA.CommentDataWrapper;
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

public class CommentDetail extends Activity {
    private RecyclerView recyclerView;
    private static CommentDataAdoater commentDataAdoater;
    private Toolbar toolbar;
    private static long total = 0;
    private static long commentOffset = 1;
    private List<CommentDataWrapper> commentDataWrapperList = new ArrayList<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 3002) {
                toolbar.setTitle( "评论(" + total +")");
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setAdapter(commentDataAdoater);
            }
            if(msg.what == 3003) {
                System.out.println("更新数据 list size = " + commentDataWrapperList.size());
                commentDataAdoater.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        init();
//        NetworkUtil.requestUrlToData("/comment/music?id="+CommonData.nowMusicId+"&limit=20&offset=" + commentOffset,new GetMusicDetail(1));
        NetworkUtil.requestUrlToData("/comment/music?id="+CommonData.nowMusicId+"&limit=20",new GetMusicDetail(1));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(isSlideToBottom(recyclerView)) {
                    if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                        commentOffset+=1;
                        System.out.println(commentOffset);
                        NetworkUtil.requestUrlToData("/comment/music?id="+CommonData.nowMusicId+"&limit=20&offset=" + commentOffset,new GetMusicDetail(3));
                    }
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
    private void init() {
        recyclerView = findViewById(R.id.song_comment_detail_recyclerview);
        toolbar = findViewById(R.id.song_comment_detail_toolbar);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);


    }
    public static boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }
    //获取歌曲详情
    public class GetMusicDetail implements okhttp3.Callback {
        private int type = 0;
        GetMusicDetail(int i){
            type = i;
        }
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("评论请求超时了 " + e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            final String strResponse = response.body().string();

            Gson gson = new Gson();
            try {
                JSONObject data = new JSONObject(strResponse);
                if(type == 1) {
                    //增加热门评论
                    JSONArray hotComments = data.getJSONArray("hotComments");
                    for(int i = 0;i < hotComments.length();i++) {
                        JSONObject item = hotComments.getJSONObject(i);
                        CommentData commentData = gson.fromJson(item.toString(),CommentData.class);

                        commentDataWrapperList.add(new CommentDataWrapper(1,commentData));
                    }
                }
                JSONArray comments = data.getJSONArray("comments");
                for(int i = 0;i < comments.length();i++) {
                    JSONObject item = comments.getJSONObject(i);
                    CommentData commentData = gson.fromJson(item.toString(),CommentData.class);
                    commentDataWrapperList.add(new CommentDataWrapper(2,commentData));
                }
                if(type == 1) {
                    commentDataAdoater = new CommentDataAdoater(commentDataWrapperList);
                }


                final long total2 = data.getLong("total");

                total = total2;
                Message message = new Message();
                if(type != 3) {
                    message.what = 3002;
                    handler.sendMessage(message);
                }

                if(type == 3) {
                    message.what = 3003;
                    handler.sendMessage(message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        commentOffset = 0;
    }
}
