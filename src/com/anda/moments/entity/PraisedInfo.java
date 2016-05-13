package com.anda.moments.entity;

import java.util.List;

/**
 * 点赞列表
 */
public class PraisedInfo {

    private int praiseNum;//点赞总数
    private List<PraiseUser> praiseUsers;//点赞用户列表

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public List<PraiseUser> getPraiseUsers() {
        return praiseUsers;
    }

    public void setPraiseUsers(List<PraiseUser> praiseUsers) {
        this.praiseUsers = praiseUsers;
    }


}
