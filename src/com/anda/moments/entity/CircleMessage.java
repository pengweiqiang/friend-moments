package com.anda.moments.entity;

import java.util.List;

/**
 * 朋友圈动态消息
 */
public class CircleMessage extends ParseModel {

    private long infoId;
    private String infoText;
    private String isPublic;
    private long createTime;
//    private User publishUser;
    private PraisedInfo praisedInfo;//点赞列表
    private CommentInfo commentInfo;//评论列表



    private List<String> images;
    private List<String> audios;
    private List<String> videos;


    public long getInfoId() {
        return infoId;
    }

    public void setInfoId(long infoId) {
        this.infoId = infoId;
    }

    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

//    @Override
//    public User getPublishUser() {
//        return publishUser;
//    }
//
//    @Override
//    public void setPublishUser(User publishUser) {
//        this.publishUser = publishUser;
//    }

    public PraisedInfo getPraisedInfo() {
        return praisedInfo;
    }

    public void setPraisedInfo(PraisedInfo praisedInfo) {
        this.praisedInfo = praisedInfo;
    }

    public CommentInfo getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(CommentInfo commentInfo) {
        this.commentInfo = commentInfo;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getAudios() {
        return audios;
    }

    public void setAudios(List<String> audios) {
        this.audios = audios;
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }
}
