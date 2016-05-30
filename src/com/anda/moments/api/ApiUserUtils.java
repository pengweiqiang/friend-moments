package com.anda.moments.api;

import android.content.Context;

import com.anda.moments.api.constant.MethodType;
import com.anda.moments.api.constant.ReqUrls;
import com.anda.moments.http.HttpClientAddHeaders;
import com.anda.moments.utils.HttpConnectionUtil.HttpMethod;
import com.anda.moments.utils.HttpConnectionUtil.RequestCallback;

import java.io.File;
import java.util.Map;

/**
 * api user相关的接口
 * 
 * @author will
 */
public class ApiUserUtils {

	/**
	 * 获取省市地区列表
	 * @param context
	 * @param requestCallback
     */
	public static void getDistrictInfo(Context context,RequestCallback requestCallback){
		Map<String,Object> params = HttpClientAddHeaders.getHeaders(context);

		ApiUtils.getParseModel(params, ReqUrls.REQUEST_GET_DISTRICT_INFO, false,
				requestCallback, MethodType.UPDATE, context,HttpMethod.POST);
	}

	/**
	 * 修改好友备注
	 * @param context
	 * @param phoneNum
	 * @param id 好友关系主键
	 * @param descTag
	 * @param requestCallback
     */
	public static void updateFriendTags(Context context,String phoneNum,String id,String descTag,RequestCallback requestCallback){
		Map<String,Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("phoneNum",phoneNum);
		params.put("relationId",id);
		params.put("descTag",descTag);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_UPDATE_FRIENDS_TAGS, false,
				requestCallback, MethodType.UPDATE, context,HttpMethod.POST);
	}

	/**
	 * 修改个人资料
	 * @param context
	 * @param phoneNum
	 * @param userName
	 * @param userId
	 * @param gender
	 * @param address
	 * @param district
	 * @param summary
     * @param descTag
     * @param requestCallback
     */
	public static void updateUserInfo(Context context,String phoneNum,String userName,String userId,String gender,
									  String address,String district,String summary,String descTag,RequestCallback requestCallback){
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("phoneNum",phoneNum);
//		if(!StringUtils.isEmpty(userName)) {
			params.put("userName", userName);
//		}
//		if(!StringUtils.isEmpty(userId)) {
			params.put("userId", userId);
//		}
//		if(!StringUtils.isEmpty(gender)) {
			params.put("gender", gender);
//		}
//		if(!StringUtils.isEmpty(address)) {
			params.put("address", address);
//		}
//		if(!StringUtils.isEmpty(district)) {
			params.put("district", district);
//		}
//		if(!StringUtils.isEmpty(summary)) {
			params.put("summary",summary);
//		}
//		if(!StringUtils.isEmpty(descTag)) {
			params.put("descTag", descTag);
//		}
		params.put("icon","");
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_UPDATE_USER_INFO, false,
				requestCallback, MethodType.UPDATE, context,HttpMethod.POST);
	}

	/**
	 * 处理好友请求
	 * @param context
	 * @param phoneNum
	 * @param relationId
	 * @param  flag 1-接受添加好友请求，2-拒绝添加好友请求
	 * @param requestCallback
     */
	public static void dealFriendsRequest(Context context,String phoneNum,String relationId,int flag,RequestCallback requestCallback){
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("myPhoneNum", phoneNum);
		params.put("relationId",relationId);
		params.put("flag",String.valueOf(flag));
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_DEAL_FRIENDS_REQUEST, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 判断是否已添加好友(判断是否收到对方添加好友请求)
	 * @param context
	 * @param phoneNum
	 * @param friendPhoneNum
	 * @param requestCallback
     */
	public static void isExistsFriends(Context context,String phoneNum,String friendPhoneNum,RequestCallback requestCallback){
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("myPhoneNum", phoneNum);
		params.put("friendPhoneNum",friendPhoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_IS_EXISTS_FRIENDS, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 删除好友请求
	 * @param context
	 * @param relationId
	 * @param phoneNum
	 * @param requestCallback
     */
	public static void deleteFriendRequest(Context context,String relationId,String phoneNum,RequestCallback requestCallback){
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("relationId", relationId);
		params.put("phoneNum",phoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_DELETE_FRIENDS_REQUEST, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 删除好友
	 * @param context
	 * @param relationId
	 * @param phoneNum
	 * @param requestCallback
     */
	public static void deleteFriend(Context context,String relationId,String phoneNum,RequestCallback requestCallback){
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("relationId", relationId);
		params.put("phoneNum",phoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_DELETE_FRIENDS, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 邀请添加好友接口
	 * @param context
	 * @param phoneNum
	 * @param friendPhoneNum
	 * @param requestCallback
     */
	public static void addFriends(Context context,String phoneNum,String friendPhoneNum,RequestCallback requestCallback){
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("myPhoneNum", phoneNum);
		params.put("friendPhoneNum",friendPhoneNum);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_ADDFRIEND, false,
				requestCallback, MethodType.UPDATE, context,HttpMethod.GET);
	}
	/**
	 * 获取验证码
	 * 
	 * @param context
	 * @param mobile
	 * @param requestCallback
	 */
	public static void getValidateCode(Context context, String mobile,RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("phoneNum", mobile);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_FRIENDS_GETVALIDATECODE, false,
				requestCallback, MethodType.UPDATE, context,HttpMethod.GET);
	}

	/**
	 * 登录
	 * 
	 * @param context
	 * @param phoneNum 手机号码
	 * @param validateCode 验证码
	 * @param requestCallBack
	 */
	public static void login(Context context, String phoneNum,String validateCode,
			RequestCallback requestCallBack) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("phoneNum", phoneNum);
		params.put("validateCode", validateCode);
		ApiUtils.getParseModel(params, ReqUrls.REQUEST_USER_LOGIN, false,
				requestCallBack, MethodType.LOGIN2, context);
	}

	/**
	 * 修改密码
	 * 
	 * @param context
	 * @param username
	 * @param opass
	 * @param password
	 * @param requestCallback
	 */
	public static void updatePwd(Context context, String username,
			String opass, String password, RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put(ReqUrls.USERNAME, username);
		params.put(ReqUrls.OPASS, opass);
		params.put(ReqUrls.PASSWORD, password);
		ApiUtils.getParseModel(params, ReqUrls.UPDATE_PWD, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 修改昵称
	 * 
	 * @param context
	 * @param username
	 * @param nickname
	 * @param requestCallback
	 */
	public static void updateNickName(Context context, String username,
			String nickname, RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put(ReqUrls.USERNAME, username);
		params.put(ReqUrls.NICK_NAME, nickname);
		ApiUtils.getParseModel(params, ReqUrls.UPDATE_NICKNAME, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 修改头像
	 * 
	 * @param context
	 * @param username
	 * @param stream
	 *            经过Base64转码过的字符串
	 * @param requestCallback
	 */
	public static void updateHeader(Context context, String username,
			String stream, File formFile, RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put(ReqUrls.USERNAME, username);
		params.put(ReqUrls.IMG_FILE, stream);


//		 Map<String,HttpBodyData> paramsImage = new HashMap<String,
//                  HttpBodyData>();
//		 paramsImage.put("source", new HttpBodyData(HttpBodyData.TYPE_STRING,
//		 "Android"));
//		 paramsImage.put("version", new HttpBodyData(HttpBodyData.TYPE_STRING,
//		 (String)params.get("version")));
//		 paramsImage.put("appname", new HttpBodyData(HttpBodyData.TYPE_STRING,
//		 (String)params.get("appname")));
//		 paramsImage.put(ReqUrls.USERNAME, new HttpBodyData(HttpBodyData.TYPE_STRING, username));
//		 paramsImage.put(ReqUrls.IMG_FILE, new
//		 HttpBodyData(HttpBodyData.TYPE_IMAGE,formFile));

		// params.put(ReqUrls.STREAM, stream);
		ApiUtils.getParseModel(params, ReqUrls.UPDATE_HEADER_URL, false,
				requestCallback, MethodType.UPDATE, context);
	}

	

	/**
	 * 忘记密码
	 * 
	 * @param context
	 * @param username
	 * @param password
	 * @param requestCallback
	 */
	public static void forgetPwd(Context context, String username,
			String password, RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put(ReqUrls.USERNAME, username);
		params.put(ReqUrls.PASSWORD, password);
		ApiUtils.getParseModel(params, ReqUrls.FORGET_PWD, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 注册
	 * 
	 * @param context
	 * @param mobile
	 * @param password
	 * @param lon
	 * @param lat
	 * @param requestCallback
	 */
	public static void register(Context context, String mobile,
			String password, double lon, double lat,
			RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put(ReqUrls.PASSWORD, password);
		params.put(ReqUrls.MOBILE, mobile);
		ApiUtils.getParseModel(params, ReqUrls.REGISTER_USER, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 地址管理
	 * 
	 * @param context
	 * @param type
	 *            0:添加 1删除 2修改 3查询
	 * @param userId
	 *            用户id
	 * @param curLocation
	 *            当前位置
	 * @param detailAddress
	 *            详细地址
	 * @param id
	 *            地址id
	 * @param requestCallback
	 */
	public static void addressManager(Context context, int type, long userId,
			String curLocation, String detailAddress, String id,String name,
			RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put("type", String.valueOf(type));
		switch (type) {
		case 0:// 增加地址
			params.put(ReqUrls.USER_ID, userId);
			params.put("curLocation", curLocation);
			params.put("detailAddress", detailAddress);
			params.put("contactor", name);
			break;
		case 1:// 删除地址
			params.put(ReqUrls.ID, id);
			break;
		case 2:// 修改地址
			params.put(ReqUrls.USER_ID, userId);
			params.put("curLocation", curLocation);
			params.put("detailAddress", detailAddress);
			params.put("contactor", name);
			params.put(ReqUrls.ID, id);
			break;
		case 3:// 查询地址
			params.put(ReqUrls.USER_ID, userId);
			break;

		default:
			break;
		}

		ApiUtils.getParseModel(params, ReqUrls.ADDRESS_MANAGER, false,
				requestCallback, MethodType.UPDATE, context);
	}

	/**
	 * 短信验证
	 * 
	 * @param context
	 * @param mobile
	 * @param code
	 * @param type
	 * @param name
	 * @param requestCallback
	 */
	public static void checkCode(Context context, String mobile, String code,
			String type, String name, RequestCallback requestCallback) {
		Map<String, Object> params = HttpClientAddHeaders.getHeaders(context);
		params.put(ReqUrls.MOBILE, mobile);
		params.put("code", code);
		params.put("type", type);
		params.put(ReqUrls.NAME, name);
		ApiUtils.getParseModel(params, ReqUrls.CHECK_CODE, false,
				requestCallback, MethodType.UPDATE, context);
	}

//	/**
//	 * 获取验证码
//	 * @param context
//	 * @param mobile
//	 * @param requestCallBack
//	 */
//	public static void getCode(Context context,String mobile,RequestCallback requestCallBack){
//		Map<String,Object> params = HttpClientAddHeaders.getHeaders(context);
//		params.put("mobile", mobile);
//		ApiUtils.getParseModel(params, ReqUrls.GET_CODE,false, requestCallBack, MethodType.UPDATE, context);
//	}
//
//	/**
//	 * 获取关于我们的地址、分享的各类文案等等常用信息
//	 * @param context
//	 * @param requestCallBack
//	 */
//	public static void getConfParams(Context context,RequestCallback requestCallBack){
//		Map<String,Object> params = HttpClientAddHeaders.getHeaders(context);
//		ApiUtils.getParseModel(params, ReqUrls.REQUEST_CONFPARAMS,false, requestCallBack, MethodType.UPDATE, context);
//	}
	// /**
	// * 获取用户个人中心界面
	// *
	// * @param userId
	// * @return
	// */
	// public static void getUserInfo(Context context,
	// RequestCallback requestCallBack) {
	// Map<String,Object> params = HttpClientAddHeaders.getHeaders(context);
	// // ApiUtils.getParseModel(params, ReqUrls.REQUEST_USER_INFO, false,
	// // requestCallBack,MethodType.USER_PAGE_INFO,null);
	// }
	//
	// /**
	// * 修改用户信息
	// *
	// * @param enterId
	// * @return
	// */
	// public static void modifyUserInfo(Context context, String name,
	// String address, String mobile, RequestCallback requestCallBack) {
	// Map<String,Object> params = HttpClientAddHeaders.getHeaders(context);
	// params.put(ReqUrls.NAME, name);
	// params.put(ReqUrls.MOBILE, mobile);
	// params.put(ReqUrls.ADDRESS, address);
	// ApiUtils.getParseModel(params, ReqUrls.UPDATE_USER_INFO, false,
	// requestCallBack, MethodType.GET_MAINPAGE_AD, null);
	// }
	//
	// /**
	// * 修改用户信息
	// *
	// * @param
	// * @return
	// */
	// public static void downloadRepoInfo(Context context, long appId) {
	// Map<String,Object> params = HttpClientAddHeaders.getHeaders(context);
	// params.put(ReqUrls.APP_ID, String.valueOf(appId));
	// ApiUtils.repo(params, ReqUrls.DOWNLOAD_REPO_INFO, HttpMethod.GET);
	// // ApiUtils.getParseModel(params, ReqUrls.DOWNLOAD_REPO_INFO, false,
	// // null,MethodType.DOWNLOAD_REPO_INFO,null);
	// }
	//
	// //
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// public static String parseLong2Str(long userId) {
	// return String.valueOf(userId);
	// }
}
