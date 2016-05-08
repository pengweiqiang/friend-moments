package com.anda.moments.entity;

import java.io.Serializable;

/**
 * Created by pengweiqiang on 16/5/8.
 */
public class Images implements Serializable{
    private String imgPath;
    private String fileOrder;//文件类型  1 图片 2 视频 3音频

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getFileOrder() {
        return fileOrder;
    }

    public void setFileOrder(String fileOrder) {
        this.fileOrder = fileOrder;
    }
}
