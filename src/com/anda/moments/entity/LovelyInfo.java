package com.anda.moments.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 点赞列表
 */
public class LovelyInfo implements Serializable {

    private int lovelyNum;//萌化了总数
    private List<LovelyUsers> lovelyUsers;//点赞用户列表

    public int getLovelyNum() {
        return lovelyNum;
    }

    public void setLovelyNum(int lovelyNum) {
        this.lovelyNum = lovelyNum;
    }

    public List<LovelyUsers> getLovelyUsers() {
        return lovelyUsers;
    }

    public void setLovelyUsers(List<LovelyUsers> lovelyUsers) {
        this.lovelyUsers = lovelyUsers;
    }

    class LovelyUsers implements Serializable{
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
