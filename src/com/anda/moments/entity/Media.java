package com.anda.moments.entity;

import java.io.Serializable;

/**
 * Created by pengweiqiang on 16/5/8.
 */
public class Media implements Serializable {
    private String path;
    private String fileOrder;//预留的排序字段

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileOrder() {
        return fileOrder;
    }

    public void setFileOrder(String fileOrder) {
        this.fileOrder = fileOrder;
    }
}
