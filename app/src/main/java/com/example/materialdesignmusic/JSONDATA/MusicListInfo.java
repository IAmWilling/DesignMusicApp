package com.example.materialdesignmusic.JSONDATA;

public class MusicListInfo {
    private long id;
    private String name;
    private String url;

    public String getUrl() {
        return url;
    }

    public MusicListInfo(long id, String name) {
        this.id = id;
        this.name = name;
        this.url = "https://music.163.com/song/media/outer/url?id=" + id + ".mp3";
    }

    public void setUrl(String url) {
        this.url = url;
    }

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
