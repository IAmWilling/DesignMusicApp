package com.example.materialdesignmusic.JSONDATA;

import java.util.List;

public class CommentData {

    /**
     * user : {"locationInfo":null,"liveInfo":null,"expertTags":null,"remarkName":null,"userId":249657534,"userType":0,"nickname":"幻语丶","vipRights":null,"vipType":0,"experts":null,"authStatus":0,"avatarUrl":"https://p3.music.126.net/x0ma3Z6BTqUuzm9ezJgMwA==/109951163687554276.jpg"}
     * beReplied : [{"user":{"locationInfo":null,"liveInfo":null,"expertTags":null,"remarkName":null,"userId":271835506,"userType":0,"nickname":"绝不醒着","vipRights":null,"vipType":0,"experts":null,"authStatus":0,"avatarUrl":"https://p4.music.126.net/mdLApVxL_HqYS4d1GQsaGg==/109951164409290326.jpg"},"beRepliedCommentId":1655369975,"content":"听说你住的城市下雨了，无论你带没带伞，我都希望下的是刀子。","status":0,"expressionUrl":null}]
     * pendantData : null
     * showFloorComment : null
     * status : 0
     * commentId : 1655379612
     * content : 突然反转
     * time : 1571106526854
     * likedCount : 0
     * expressionUrl : null
     * commentLocationType : 0
     * parentCommentId : 1655369975
     * decoration : {}
     * repliedMark : null
     * liked : false
     */

    private UserBean user;
    private Object pendantData;
    private Object showFloorComment;
    private int status;
    private long commentId;
    private String content;
    private long time;
    private int likedCount;
    private Object expressionUrl;
    private int commentLocationType;
    private long parentCommentId;
    private DecorationBean decoration;
    private Object repliedMark;
    private boolean liked;
    private List<BeRepliedBean> beReplied;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public Object getPendantData() {
        return pendantData;
    }

    public void setPendantData(Object pendantData) {
        this.pendantData = pendantData;
    }

    public Object getShowFloorComment() {
        return showFloorComment;
    }

    public void setShowFloorComment(Object showFloorComment) {
        this.showFloorComment = showFloorComment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(int likedCount) {
        this.likedCount = likedCount;
    }

    public Object getExpressionUrl() {
        return expressionUrl;
    }

    public void setExpressionUrl(Object expressionUrl) {
        this.expressionUrl = expressionUrl;
    }

    public int getCommentLocationType() {
        return commentLocationType;
    }

    public void setCommentLocationType(int commentLocationType) {
        this.commentLocationType = commentLocationType;
    }

    public long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public DecorationBean getDecoration() {
        return decoration;
    }

    public void setDecoration(DecorationBean decoration) {
        this.decoration = decoration;
    }

    public Object getRepliedMark() {
        return repliedMark;
    }

    public void setRepliedMark(Object repliedMark) {
        this.repliedMark = repliedMark;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public List<BeRepliedBean> getBeReplied() {
        return beReplied;
    }

    public void setBeReplied(List<BeRepliedBean> beReplied) {
        this.beReplied = beReplied;
    }

    public static class UserBean {
        /**
         * locationInfo : null
         * liveInfo : null
         * expertTags : null
         * remarkName : null
         * userId : 249657534
         * userType : 0
         * nickname : 幻语丶
         * vipRights : null
         * vipType : 0
         * experts : null
         * authStatus : 0
         * avatarUrl : https://p3.music.126.net/x0ma3Z6BTqUuzm9ezJgMwA==/109951163687554276.jpg
         */

        private Object locationInfo;
        private Object liveInfo;
        private Object expertTags;
        private Object remarkName;
        private long userId;
        private int userType;
        private String nickname;
        private Object vipRights;
        private int vipType;
        private Object experts;
        private int authStatus;
        private String avatarUrl;

        public Object getLocationInfo() {
            return locationInfo;
        }

        public void setLocationInfo(Object locationInfo) {
            this.locationInfo = locationInfo;
        }

        public Object getLiveInfo() {
            return liveInfo;
        }

        public void setLiveInfo(Object liveInfo) {
            this.liveInfo = liveInfo;
        }

        public Object getExpertTags() {
            return expertTags;
        }

        public void setExpertTags(Object expertTags) {
            this.expertTags = expertTags;
        }

        public Object getRemarkName() {
            return remarkName;
        }

        public void setRemarkName(Object remarkName) {
            this.remarkName = remarkName;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Object getVipRights() {
            return vipRights;
        }

        public void setVipRights(Object vipRights) {
            this.vipRights = vipRights;
        }

        public int getVipType() {
            return vipType;
        }

        public void setVipType(int vipType) {
            this.vipType = vipType;
        }

        public Object getExperts() {
            return experts;
        }

        public void setExperts(Object experts) {
            this.experts = experts;
        }

        public int getAuthStatus() {
            return authStatus;
        }

        public void setAuthStatus(int authStatus) {
            this.authStatus = authStatus;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }

    public static class DecorationBean {
    }

    public static class BeRepliedBean {
        /**
         * user : {"locationInfo":null,"liveInfo":null,"expertTags":null,"remarkName":null,"userId":271835506,"userType":0,"nickname":"绝不醒着","vipRights":null,"vipType":0,"experts":null,"authStatus":0,"avatarUrl":"https://p4.music.126.net/mdLApVxL_HqYS4d1GQsaGg==/109951164409290326.jpg"}
         * beRepliedCommentId : 1655369975
         * content : 听说你住的城市下雨了，无论你带没带伞，我都希望下的是刀子。
         * status : 0
         * expressionUrl : null
         */

        private UserBeanX user;
        private long beRepliedCommentId;
        private String content;
        private int status;
        private Object expressionUrl;

        public UserBeanX getUser() {
            return user;
        }

        public void setUser(UserBeanX user) {
            this.user = user;
        }

        public long getBeRepliedCommentId() {
            return beRepliedCommentId;
        }

        public void setBeRepliedCommentId(long beRepliedCommentId) {
            this.beRepliedCommentId = beRepliedCommentId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Object getExpressionUrl() {
            return expressionUrl;
        }

        public void setExpressionUrl(Object expressionUrl) {
            this.expressionUrl = expressionUrl;
        }

        public static class UserBeanX {
            /**
             * locationInfo : null
             * liveInfo : null
             * expertTags : null
             * remarkName : null
             * userId : 271835506
             * userType : 0
             * nickname : 绝不醒着
             * vipRights : null
             * vipType : 0
             * experts : null
             * authStatus : 0
             * avatarUrl : https://p4.music.126.net/mdLApVxL_HqYS4d1GQsaGg==/109951164409290326.jpg
             */

            private Object locationInfo;
            private Object liveInfo;
            private Object expertTags;
            private Object remarkName;
            private long userId;
            private int userType;
            private String nickname;
            private Object vipRights;
            private int vipType;
            private Object experts;
            private int authStatus;
            private String avatarUrl;

            public Object getLocationInfo() {
                return locationInfo;
            }

            public void setLocationInfo(Object locationInfo) {
                this.locationInfo = locationInfo;
            }

            public Object getLiveInfo() {
                return liveInfo;
            }

            public void setLiveInfo(Object liveInfo) {
                this.liveInfo = liveInfo;
            }

            public Object getExpertTags() {
                return expertTags;
            }

            public void setExpertTags(Object expertTags) {
                this.expertTags = expertTags;
            }

            public Object getRemarkName() {
                return remarkName;
            }

            public void setRemarkName(Object remarkName) {
                this.remarkName = remarkName;
            }

            public long getUserId() {
                return userId;
            }

            public void setUserId(long userId) {
                this.userId = userId;
            }

            public int getUserType() {
                return userType;
            }

            public void setUserType(int userType) {
                this.userType = userType;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public Object getVipRights() {
                return vipRights;
            }

            public void setVipRights(Object vipRights) {
                this.vipRights = vipRights;
            }

            public int getVipType() {
                return vipType;
            }

            public void setVipType(int vipType) {
                this.vipType = vipType;
            }

            public Object getExperts() {
                return experts;
            }

            public void setExperts(Object experts) {
                this.experts = experts;
            }

            public int getAuthStatus() {
                return authStatus;
            }

            public void setAuthStatus(int authStatus) {
                this.authStatus = authStatus;
            }

            public String getAvatarUrl() {
                return avatarUrl;
            }

            public void setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
            }
        }
    }
}
