package com.anda.moments.entity;

import java.util.List;

/**
 * 点赞列表
 */
public class PraisedInfo extends ParseModel{

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

    class PraiseUser {
        private String userName;
        private String userId;
        private String icon;
        private String phoneNum;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
