package com.anda.moments.entity;

import java.util.List;

/**
 * 点赞列表
 */
public class CommentInfo extends ParseModel{

   private int commentNum;//评论总数
   private List<CommentUser> commentUsers;


   public int getCommentNum() {
      return commentNum;
   }

   public void setCommentNum(int commentNum) {
      this.commentNum = commentNum;
   }

   public List<CommentUser> getCommentUsers() {
      return commentUsers;
   }

   public void setCommentUsers(List<CommentUser> commentUsers) {
      this.commentUsers = commentUsers;
   }

   class CommentUser {
      private String userName;
      private String userId;
      private String phoneNum;
      private String icon;
      private String text;
      private long publishTime;

      public String getUserName() {
         return userName;
      }

      public void setUserName(String userName) {
         this.userName = userName;
      }

      public String getUserId() {
         return userId;
      }

      public void setUserId(String userId) {
         this.userId = userId;
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

      public String getText() {
         return text;
      }

      public void setText(String text) {
         this.text = text;
      }

      public long getPublishTime() {
         return publishTime;
      }

      public void setPublishTime(long publishTime) {
         this.publishTime = publishTime;
      }
   }

}
