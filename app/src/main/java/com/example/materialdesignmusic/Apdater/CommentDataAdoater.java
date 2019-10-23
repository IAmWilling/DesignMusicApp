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
import com.example.materialdesignmusic.CommonMethods.MyDateUtils;
import com.example.materialdesignmusic.JSONDATA.CommentData;
import com.example.materialdesignmusic.JSONDATA.CommentDataWrapper;
import com.example.materialdesignmusic.R;

import java.util.ArrayList;
import java.util.List;

public class CommentDataAdoater extends RecyclerView.Adapter<CommentDataAdoater.Holder> {
    private List<CommentDataWrapper> commentDataWrapperList = new ArrayList<>();
    public CommentDataAdoater(List<CommentDataWrapper> commentDataWrapperList) {
        this.commentDataWrapperList = commentDataWrapperList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_comment_view,parent,false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CommentDataWrapper commentDataWrapper = commentDataWrapperList.get(position);
        CommentData commentData = commentDataWrapper.getCommentData();
        holder.nickname.setText(commentData.getUser().getNickname());
        Glide.with(MyApplication.getContext())
                .load(commentData.getUser().getAvatarUrl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .placeholder(R.drawable.avatar)
                .into(holder.avatar);
        holder.zanCount.setText(commentData.getLikedCount() + "");
        holder.content.setText(commentData.getContent());
        holder.date.setText(MyDateUtils.timeStampToYearDate(commentData.getTime()));
    }
    public static class Holder extends RecyclerView.ViewHolder{
        private TextView zanCount,nickname,date,content;
        private ImageView avatar,zan;
        public Holder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.song_comment_nickname);
            zanCount = itemView.findViewById(R.id.song_comment_zanCount);
            date = itemView.findViewById(R.id.song_comment_date);
            content = itemView.findViewById(R.id.song_comment_content);
            avatar = itemView.findViewById(R.id.song_comment_avatar);
            zan = itemView.findViewById(R.id.song_comment_zan);
        }
    }
    @Override
    public int getItemCount() {
        return commentDataWrapperList.size();
    }
}
