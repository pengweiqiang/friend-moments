package com.anda.moments.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 点赞列表
 */
public class CommentInfo implements Serializable{

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



}
