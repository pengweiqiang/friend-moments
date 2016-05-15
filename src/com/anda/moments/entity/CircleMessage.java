package com.anda.moments.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 朋友圈动态消息
 */
public class CircleMessage implements Serializable {

    private long infoId;
    private String infoText;
    private String isPublic;
    private long createTime;
    private User publishUser;
    private PraisedInfo praisedInfo;//点赞列表
    private LovelyInfo lovelyInfo;//萌化了列表
    private CommentInfo commentInfo;//评论列表

    private boolean isPlay;



    private List<Images> images;
    private List<Audio> audios;
    private List<Video> videos;


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

    public User getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(User publishUser) {
        this.publishUser = publishUser;
    }

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

    public List<Audio> getAudios() {
        return audios;
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public LovelyInfo getLovelyInfo() {
        return lovelyInfo;
    }

    public void setLovelyInfo(LovelyInfo lovelyInfo) {
        this.lovelyInfo = lovelyInfo;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }
}
