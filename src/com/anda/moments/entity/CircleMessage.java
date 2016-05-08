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
    private LovelyInfo lovelyInfo;//萌化了列表
    private CommentInfo commentInfo;//评论列表



    private List<Images> images;
    private List<Media> audios;
    private List<Media> videos;


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

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public List<Media> getAudios() {
        return audios;
    }

    public void setAudios(List<Media> audios) {
        this.audios = audios;
    }

    public List<Media> getVideos() {
        return videos;
    }

    public void setVideos(List<Media> videos) {
        this.videos = videos;
    }

    public LovelyInfo getLovelyInfo() {
        return lovelyInfo;
    }

    public void setLovelyInfo(LovelyInfo lovelyInfo) {
        this.lovelyInfo = lovelyInfo;
    }
}
