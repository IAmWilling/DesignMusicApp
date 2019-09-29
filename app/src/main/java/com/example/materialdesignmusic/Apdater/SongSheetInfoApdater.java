package com.example.materialdesignmusic.Apdater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.materialdesignmusic.Activity.SongSheetListActivity;
import com.example.materialdesignmusic.JSONDATA.SongSheetInfo;
import com.example.materialdesignmusic.R;

import java.util.List;

public class SongSheetInfoApdater extends RecyclerView.Adapter<SongSheetInfoApdater.Holder> {
    private List<SongSheetInfo> songSheetInfos;
    private Context mContext;
    public  SongSheetInfoApdater(List<SongSheetInfo> songSheetInfos, Context context){
        this.songSheetInfos = songSheetInfos;
        this.mContext = context;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_sheet_list_view,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final SongSheetInfo songSheetInfo = songSheetInfos.get(position);

        holder.trackCount.setText(songSheetInfo.getTrackCount() + "首 by " + songSheetInfo.getCreator().getNickname());
        Glide.with(mContext).load(songSheetInfo.getCoverImgUrl()).apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).into(holder.songSheetCover);
        holder.title.setText(songSheetInfo.getName());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SongSheetListActivity.class);
                intent.putExtra("object", songSheetInfo);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songSheetInfos.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{
        public ImageView songSheetCover; //封面
        public TextView title,trackCount; //标题和几首歌
        public LinearLayout linearLayout;
        public Holder(@NonNull View itemView) {
            super(itemView);
            songSheetCover = itemView.findViewById(R.id.song_sheet_cover_image);
            title = itemView.findViewById(R.id.song_sheet_title);
            trackCount = itemView.findViewById(R.id.song_sheet_tarkcount);
            linearLayout = itemView.findViewById(R.id.song_sheet_linearLayout_startActity);
        }
    }
}
