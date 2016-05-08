package com.anda.moments.entity;

import java.io.Serializable;
import java.util.List;

public class Infos implements Serializable {

    private long infoId;
    private String infoText;
    private String isPublic;
    private String phoneNum;
    private long createTime;
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

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
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
}
