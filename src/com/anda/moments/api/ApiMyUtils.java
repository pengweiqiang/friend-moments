package com.anda.moments.api;

import android.content.Context;

import com.anda.moments.api.constant.MethodType;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.http.HttpClientAddHeaders;
import com.anda.moments.utils.HttpConnectionUtil.RequestCallback;

import java.util.Map;

/**
 * api 我的相关的接口
 * 
 * @author will
 */
public class ApiMyUtils {


	/**
	 * 验证是否存在该userId
	 * @param context
	 * @param userId
	 * @param requestCallback
     */
	public static void checkExistUserId(Context context,String userId,RequestCallback requestCallback){
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("userId", userId);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_IS_EXIST_USER_ID, false,
				requestCallback, MethodType.UPDATE, context);
	}
	/**
	 * 根据USERID查询添加好友
	 * @param context
	 * @param userId
	 * @param requestCallback
     */
	public static void searchFriendByUserID(Context context,String userId,String phoneNum,RequestCallback requestCallback){
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("userId", userId);
		params.put("phoneNum", phoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_SEARCH_BY_USERID, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 根据昵称、备注、ID、手机号进行模糊查询自己的好友
	 * @param context
	 * @param condition
	 * @param phoneNum
	 * @param requestCallback
     */
	public static void searchFriends(Context context,String condition,String phoneNum,RequestCallback requestCallback){
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("condition", condition);
		params.put("phoneNum", phoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_SEARCH, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 *  获得好友动态列表
	 * @param context
	 * @param phoneNum 手机号
	 * @param pageSize 每页显示记录条数
	 * @param pageNo 当前页数
	 * @param type 0全部动态，1好友动态，2我的动态
	 * @param requestCallback
	 */
	public static void getFriendsInfos(Context context, String phoneNum,String pageSize,String pageNo,String type,RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("phoneNum",phoneNum);
		params.put("pageSize", pageSize);
		params.put("pageNo", pageNo);
		params.put("type", type);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_GET_FRIENDS_INFOS, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 我的动态详情
	 * @param context
	 * @param phoneNum
	 * @param pageSize 每页请求记录条数
	 * @param pageNo 当前页数
	 * @param type   0全部动态，1好友动态，2我的动态
	 * @param requestCallBack
	 */
	public static void getInfoDetails(Context context, String phoneNum,String pageSize,String pageNo,String type,
			RequestCallback requestCallBack) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("phoneNum", phoneNum);
		params.put("pageSize", pageSize);
		params.put("pageNo", pageNo);
		params.put("type", type);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_GET_INFO_DETAILS, false,
				requestCallBack, MethodType.GET_INFO_DETAILS, context);
	}

	/**
	 * 我的(好友)资料详情
	 * @param context
	 * @param phoneNum 手机号码
	 * @param requestCallback
	 */
	public static void getMyInformations(Context context, String phoneNum,String queryPhoneNum,
			 RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("phoneNum", phoneNum);
		params.put("queryPhoneNum",queryPhoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_GET_MY_INFORMATIONS, false,
				requestCallback, MethodType.UPDATE, context);
	}

}
