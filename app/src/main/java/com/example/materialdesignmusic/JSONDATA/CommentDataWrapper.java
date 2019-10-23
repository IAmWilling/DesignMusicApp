package com.example.materialdesignmusic.JSONDATA;

public class CommentDataWrapper {
    public int type;   //1 - 热门评论 | 2 - 普通评论
    private CommentData commentData;

    public CommentDataWrapper(int type, CommentData commentData) {
        this.type = type;
        this.commentData = commentData;
    }

    public CommentData getCommentData() {
        return commentData;
    }

    public void setCommentData(CommentData commentData) {
        this.commentData = commentData;
    }
}
