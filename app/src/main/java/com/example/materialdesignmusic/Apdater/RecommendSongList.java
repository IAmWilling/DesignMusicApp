package com.example.materialdesignmusic.Apdater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.JSONDATA.RecommendSongListData;
import com.example.materialdesignmusic.R;

import java.util.List;

public class RecommendSongList extends RecyclerView.Adapter<RecommendSongList.Holder> {
    private List<RecommendSongListData> recommendSongListDataList;
    public RecommendSongList(List<RecommendSongListData> recommendSongListDataList){
        this.recommendSongListDataList = recommendSongListDataList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recmmond_song_list_item,viewGroup,false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder viewHolder, int i) {
        RecommendSongListData recommendSongListData = recommendSongListDataList.get(i);
        Glide.with(MyApplication.getContext())
                .load(recommendSongListData.getPicUrl())
                .into(viewHolder.img);
        viewHolder.title.setText(recommendSongListData.getName());
    }

    @Override
    public int getItemCount() {
        return recommendSongListDataList.size();
    }
    static class Holder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView title;
        public Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.recommend_item_image);
            title = itemView.findViewById(R.id.recommend_item_title);
        }
    }


}
