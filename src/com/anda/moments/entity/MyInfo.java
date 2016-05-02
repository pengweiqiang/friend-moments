package com.anda.moments.entity;

import java.io.Serializable;
import java.util.List;

public class MyInfo extends ParseModel {

    private User publishUser;

    private List<Infos> infos;


    public User getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(User publishUser) {
        this.publishUser = publishUser;
    }

    public List<Infos> getInfos() {
        return infos;
    }

    public void setInfos(List<Infos> infos) {
        this.infos = infos;
    }
}
