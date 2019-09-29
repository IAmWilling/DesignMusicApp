package com.example.materialdesignmusic.JSONDATA;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class SongSheetPlayListDetail implements Serializable {
    private String name;
    private long id;
    private AlBean al;
    private ArBean ar;
    private List<?> arList;
    private Long mv;

    public long getShareCount() {
        return shareCount;
    }

    public void setShareCount(long shareCount) {
        this.shareCount = shareCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    private long shareCount; //分享数量
    private long commentCount;//评论数量
    public Long getMv() {
        return mv;
    }

    public void setMv(Long mv) {
        this.mv = mv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AlBean getAl() {
        return al;
    }

    public void setAl(AlBean al) {
        this.al = al;
    }

    public ArBean getAr() {
        return ar;
    }

    public void setAr(ArBean ar) {
        this.ar = ar;
    }

    public List<?> getArList() {
        return arList;
    }

    public void setArList(List<?> arList) {
        this.arList = arList;
    }

    public static class ArBean implements Serializable{
        private long id;
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    public static class AlBean implements Serializable{
        private long id;
        private String name;
        private String picUrl;
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }


    }
}
