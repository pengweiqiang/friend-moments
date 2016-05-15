package com.anda.moments.entity;

import java.io.Serializable;

/**
 * Created by pengweiqiang on 16/5/8.
 */
public class Skins implements Serializable {

    private long id;
    private String name;//皮肤名称
    private String isDefault;//是否为默认
    private String skinPath;//皮肤图片地址

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

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getSkinPath() {
        return skinPath;
    }

    public void setSkinPath(String skinPath) {
        this.skinPath = skinPath;
    }
}
