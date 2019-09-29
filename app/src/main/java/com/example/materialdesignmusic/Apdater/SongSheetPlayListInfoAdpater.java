package com.example.materialdesignmusic.Apdater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.materialdesignmusic.Activity.SongPlayActivity;
import com.example.materialdesignmusic.Activity.SongSheetListActivity;
import com.example.materialdesignmusic.CommonData.CommonData;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.CommonMethods.BitmapMethods;
import com.example.materialdesignmusic.JSONDATA.SongSheetInfo;
import com.example.materialdesignmusic.JSONDATA.SongSheetPlayListDetail;
import com.example.materialdesignmusic.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

public class SongSheetPlayListInfoAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADVIEW = 2;
    private static final int BODY = 1;
    private View ViewHead; //头布局
    private static int headViewSize = 0;
    private SongSheetInfo songSheetInfo;
    private List<SongSheetPlayListDetail> songSheetPlayListDetails;
    private Context context;
    public SongSheetPlayListInfoAdpater(List<SongSheetPlayListDetail> songSheetPlayListDetails, SongSheetInfo songSheetInfo, Context mContext) {
        this.context = mContext;
        this.songSheetInfo = songSheetInfo;
        this.songSheetPlayListDetails = songSheetPlayListDetails;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = null;
        switch(viewType) {
            case 1:
                holder = new Holder(layoutInflater.inflate(R.layout.song_sheet_playlist_view,parent,false));
                break;
            case 2:
                holder = new HeaderViewHolder(layoutInflater.inflate(R.layout.test_view_header,parent,false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof HeaderViewHolder) {
            //头布局holder.toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            ////                @Override
            ////                public void onClick(View view) {
            ////                }
            ////            });

            ((HeaderViewHolder)holder).title.setText(songSheetInfo.getName());
            ((HeaderViewHolder)holder).nickname.setText(songSheetInfo.getCreator().getNickname() + " >");
            ((HeaderViewHolder)holder).despation.setText((String)songSheetInfo.getDescription());
            long shareCount = songSheetPlayListDetails.get(0).getShareCount();
            long commentCount = songSheetPlayListDetails.get(0).getCommentCount();
            if(shareCount > 0) {
                ((HeaderViewHolder)holder).share.setText( shareCount+"");
            }else {
                ((HeaderViewHolder)holder).share.setText("分享");
            }
            if(commentCount > 0) {
                ((HeaderViewHolder)holder).comment.setText( commentCount+"");
            }else {
                ((HeaderViewHolder)holder).comment.setText("评论");
            }
        Glide.with(MyApplication.getContext()).load(songSheetInfo.getCreator().getAvatarUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(((HeaderViewHolder)holder).avatar);
        Glide.with(MyApplication.getContext()).load(songSheetInfo.getCoverImgUrl()).into(((HeaderViewHolder)holder).smallCover);
        Glide.with(MyApplication.getContext()).asBitmap().load(songSheetInfo.getCoverImgUrl())
        .into(new BitmapImageViewTarget(((HeaderViewHolder)holder).bgCover) {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            protected void setResource(Bitmap resource) {
                if(resource != null) {
                    Bitmap bitmap = BitmapMethods.setBitmapGaussianBlur(MyApplication.getContext(),resource,25);

                    super.setResource(bitmap);
                }
            }
        });
        }
        if(holder instanceof Holder) {
            final SongSheetPlayListDetail songSheetPlayListDetail = this.songSheetPlayListDetails.get(position - 1);

            if(songSheetPlayListDetail.getMv() > 0) {
                ((Holder)holder).ismv.setVisibility(View.VISIBLE);
            }else {
                ((Holder)holder).ismv.setVisibility(View.GONE);
            }
            //body布局
            ((Holder)holder).song_index.setText(position + "");
            String zj = "";
            List<SongSheetPlayListDetail.ArBean> arBeans = (List<SongSheetPlayListDetail.ArBean>) songSheetPlayListDetail.getArList();
            for(int i = 0;i < arBeans.size(); i++) {
                if(i != arBeans.size() - 1) {
                    zj += arBeans.get(i).getName() + "/";
                }else {
                    zj += arBeans.get(i).getName();
                }
            }
            ((Holder)holder).songPerson.setText(zj + " - " + ((SongSheetPlayListDetail.AlBean)songSheetPlayListDetail.getAl()).getName());
            ((Holder)holder).name.setText(songSheetPlayListDetail.getName());
            /**
             * 跳转播放音乐页面
             * */
            ((Holder)holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //拿到歌曲列表放入全局中
                    CommonData.commonSongSheetPlayListDetailList = songSheetPlayListDetails;
                    Intent intent = new Intent(MyApplication.getContext(), SongPlayActivity.class);
                    intent.putExtra("object",songSheetPlayListDetail);
                    intent.putExtra("index",position - 1); //拿到列表正确索引
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getContext().startActivity(intent);
                }
            });
        }

    }

    public  class Holder extends RecyclerView.ViewHolder{
        public TextView name,songPerson,song_index,ismv;
        public LinearLayout linearLayout;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.song_name);
            songPerson = itemView.findViewById(R.id.song__chusheng);
            song_index = itemView.findViewById(R.id.song_index);
            ismv = itemView.findViewById(R.id.id_song_mv);
            linearLayout = itemView.findViewById(R.id.song_sheet_playlistdetail_linearLayout);
        }
    }
    public  class HeaderViewHolder extends RecyclerView.ViewHolder {
        public AppBarLayout appBarLayout;
        public TextView title,nickname,despation,share,comment;
        public CollapsingToolbarLayout collapsingToolbarLayout;
        public ImageView bgCover,smallCover,avatar;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            collapsingToolbarLayout = itemView.findViewById(R.id.song_sheet_toolbarlayout1);
            appBarLayout = itemView.findViewById(R.id.appBar1);
            bgCover = itemView.findViewById(R.id.song_sheet_cover_img_big1);
            smallCover = itemView.findViewById(R.id.smallCover);
            title = itemView.findViewById(R.id.song_sheet_title_name1);
            avatar = itemView.findViewById(R.id.createor_avatar1);
            nickname = itemView.findViewById(R.id.createor_nickname1);
            despation = itemView.findViewById(R.id.despation1);
            comment = itemView.findViewById(R.id.track_content_count);
            share = itemView.findViewById(R.id.share_count);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return HEADVIEW;
        }else {
            return BODY;
        }
    }

    @Override
    public int getItemCount() {
        return songSheetPlayListDetails.size()+ 1;
    }
}
