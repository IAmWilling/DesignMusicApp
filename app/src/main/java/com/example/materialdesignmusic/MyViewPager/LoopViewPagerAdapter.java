package com.example.materialdesignmusic.MyViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.materialdesignmusic.CommonData.MyApplication;
import com.example.materialdesignmusic.JSONDATA.BannerData;
import com.example.materialdesignmusic.R;

import java.util.List;

public class LoopViewPagerAdapter extends PagerAdapter {
    private List<BannerData> bannerDataList;
    public LoopViewPagerAdapter(List<BannerData> bannerDataList) {
        this.bannerDataList = bannerDataList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.banner_image_view,container,false);
        int newPosition = position % bannerDataList.size();
        ImageView image = view.findViewById(R.id.banner_image_pic);
        Glide.with(container.getContext()).load(bannerDataList.get(newPosition).getPic()).into(image);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return bannerDataList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
