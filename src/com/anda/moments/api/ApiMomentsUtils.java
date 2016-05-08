package com.anda.moments.api;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.anda.moments.api.constant.MethodType;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.http.HttpClientAddHeaders;
import com.anda.moments.utils.HttpConnectionUtil.RequestCallback;

/**
 * api 朋友圈相关的接口
 * 
 * @author will
 */
public class ApiMomentsUtils {
	
	/**
	 * 添加好友
	 * @param context
	 * @param myPhoneNum  我的手机号
	 * @param friendPhoneNum  好友手机号
	 * @param flag  1接受添加好友请求  2 拒绝添加好友请求
	 * @param requestCallback
	 */
	public static void addFriend(Context context, String myPhoneNum,String friendPhoneNum,String flag,RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("myPhoneNum", myPhoneNum);
		params.put("friendPhoneNum", friendPhoneNum);
		params.put("flag", flag);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_GETVALIDATECODE, false,
				requestCallback, MethodType.UPDATE, context);
	}
	/**
	 * 获取好友列表
	 * 
	 * @param context
	 * @param phoneNum  手机号
	 * @param flag    请求接口标识：1-表示查询已经添加完成好友列表
	 * @param requestCallBack
	 */
	public static void getMyFriendsList(Context context,String phoneNum,int flag,RequestCallback requestCallBack){
		Map<String,Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("phoneNum", phoneNum);
		if(flag != -1) {
			params.put("flag", String.valueOf(flag));
		}
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_GET_MY_FRIENDS_LIST, false, 
				requestCallBack, MethodType.FRIENDS, context);
	}

	/**
	 * 发布动态
	 * 提交方式（Content-Type:multipart/form-data）
	 * 
	 * @param context
	 * @param infoText 动态内容 必填
	 * @param isPublic 是否公开 0：私有 1：公开（必填）
	 * @param type 附件类型：1-图片，2-音频，3-视频
	 * @param file 附件数组
	 * @param requestCallBack
	 */
	public static void publishInformation(Context context, String infoText,String isPublic,String type,List file,
			RequestCallback requestCallBack) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("infoText", infoText);
		params.put("isPublic", isPublic);
		params.put("type",type);
		
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_PUBLISH_INFORMATION, false,
				requestCallBack, MethodType.LOGIN, context);
	}

	/**
	 * 评论
	 * @param context
	 * @param infoId 动态信息id
	 * @param commentText  评论内容
	 * @param requestCallback
	 */
	public static void addComment(Context context, String infoId,
			String commentText,String phoneNum ,RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("infoId", infoId);
		params.put("commentText", commentText);
		params.put("phoneNum", phoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_ADD_COMMENT, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 萌化了
	 * @param context
	 * @param infoId 动态信息id
	 * @param type  1
	 * @param requestCallback
	 */
	public static void addLoveSth(Context context, String infoId,
								  String type,String phoneNum ,RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("infoId", infoId);
		params.put("type", type);
		params.put("phoneNum", phoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_LOVE_STH, false,
				requestCallback, MethodType.UPDATE, context);
	}
	

	/**
	 * 点赞
	 * @param context
	 * @param infoId 动态信息id
	 * @param requestCallback
	 */
	public static void praise(Context context, String infoId,String phoneNum,
			 RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("infoId", infoId);
		params.put("phoneNum", phoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_PRAISE, false,
				requestCallback, MethodType.UPDATE, context);
	}
	
	/**
	 * 保存好友之间的聊天信息
	 * @param context
	 * @param phoneNum 我的手机号
	 * @param otherPhoneNum 聊天好友手机号
	 * @param msgText 聊天信息内容
	 * @param requestCallback
	 */
	public static void addMessage(Context context, String phoneNum,String otherPhoneNum,String msgText,
			 RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("phoneNum", phoneNum);
		params.put("otherPhoneNum", otherPhoneNum);
		params.put("msgText", msgText);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_ADD_MESSAGE, false,
				requestCallback, MethodType.UPDATE, context);
	}
	/**
	 * 获得朋友圈消息
	 * @param context
	 * @param phoneNum
	 * @param requestCallback
	 */
	public static void getFriendMessages(Context context, String phoneNum,
			 RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("phoneNum", phoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_GET_FRIEND_MESSAGES, false,
				requestCallback, MethodType.UPDATE, context);
	}
	

}
