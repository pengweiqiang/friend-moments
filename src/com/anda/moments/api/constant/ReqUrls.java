package com.anda.moments.api.constant;

public interface ReqUrls {

public static final String PROJECT_NAME = "";
	
	public static final boolean neuSecure = false;
	
	public static final String Connection_Type_Seccurity = "https://";
	
	public static final String Connection_Type_Common = "http://";
	
	public static String ONLINE_CONFIG_FILE_TYPE=".json";
	
	public static String USERNAME="username"; //用户账号
	
	public static String PASSWORD="password";
	
	public static String OPASS = "opass";
	
	public static String STREAM = "stream";
	
	public static String CURPAGE = "curpage";
	
	public static String ID = "id";
	
	public static String START = "start";
	
	public static String LIMIT = "limit" ;
	
	public static String USER_ID = "userId" ;
	
	public static String USER_LON = "lon" ;
	
	public static String USER_LAT = "lat" ;
	
	public static String UIDS = "uids";
	
	public static String TYPE = "type";
	
	public static String ALL_PRICE = "allPrice";
	
	public static String ITEM_TYPE = "itemType";
	
	public static String OPER_TYPE = "operType";
	
	public static String KIND_TYPE = "kindType";
	
	public static String ITEM_ID = "itemId";
	
	public static String INFO_STR = "infoStr";
	
	public static String CATEGORY_ID = "industryId";
	
	public static String APP_ID = "appId";

    public static String INDUSTRY_ID = "industryId";
	
	public static String PRODUCT_MODEL = "productModel";


//	http://123.57.163.138/friendscircle
	public static final String CONFIG_HOST_IP="123.57.163.138/friendscircle"; //dns ip
//	public static final String CONFIG_HOST_IP="http://bldj.com" 
	
//	public static final String DEFAULT_REQ_HOST_IP = "http://bldj.com";
//    public static final String DEFAULT_REQ_HOST_IP = "172.16.102.145:8080/wechatshop/login.html"; //默认值异常的请求地址
	 public static final String DEFAULT_REQ_HOST_IP = Connection_Type_Common+"123.57.163.138/friendscircle/"; //默认值异常的请求地址

	//融云参数配置
	public static final String APPKEY_RONG = "lmxuhwagxvpkd";

	public static final String APPSERCERT_RONG = "DK6yuQWNLGQ";

	public static final String LIMIT_DEFAULT_NUM="10";
	
	public static final String SEARCH_KEY="keyWord";
	
	public static final String ORDER_ID = "orderId";
	public static final String ORDER_TYPE = "orderType";
	
	public static final String NICK_NAME = "nickname";
	public static final String NAME = "name";
	public static final String MOBILE = "mobile";
	public static final String ADDRESS = "address";

	
	public static final String OPEN_TYPE = "operType";
	
	public static final String IMG_FILE = "formFile";
	
	public static final String CUR_VERSION = "curVersion";
	
	public static final String FILE_STATE = "fileState";
	
	public static final String DEVICE_VERSION = "deviceVersion";
	
	public static final String CLIENT_IP = "clientIp";
	
	public static final String DEVICE = "device";
	
	public static final String EXCEPTION_MSG="exceptionMsg";
	
	public static final String USER_REQUEST_INFO="userRequestInfo";
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 获取验证码
	 */
	public static final String REQUEST_FRIENDS_GETVALIDATECODE = "friends/getValidateCode.do";
	
	/**
	 * 用户登录
	 */
	public static final String REQUEST_USER_LOGIN = "friends/login.do";
	/**
	 * 添加好友
	 */
	public static final String REQUEST_FRIENDS_ADDFRIEND = "friends/addFriend.do";
	/**
	 * 获取好友列表
	 */
	public static final String REQUEST_FRIENDS_GET_MY_FRIENDS_LIST = "friends/getMyFriendsList.do";
	/**
	 * 发布动态
	 */
	public static final String REQUEST_FRIENDS_PUBLISH_INFORMATION = "friends/publishInformation.do";
	/**
	 * 评论
	 */
	public static final String REQUEST_FRIENDS_ADD_COMMENT = "friends/addComment.do";
	/**
	 * 点赞
	 */
	public static final String REQUEST_FRIENDS_PRAISE = "friends/praise.do";
	/**
	 * 保存好友之间的聊天信息
	 */
	public static final String REQUEST_FRIENDS_ADD_MESSAGE = "friends/addMessage.do";
	/**
	 * 获取朋友圈消息
	 */
	public static final String REQUEST_FRIENDS_GET_FRIEND_MESSAGES = "friends/getFriendMessages.do";
	/**
	 * 获得好友动态列表
	 */
	public static final String REQUEST_FRIENDS_GET_FRIENDS_INFOS = "friends/getFriendsInfos.do";
	/**
	 * 我的动态详情
	 */
	public static final String REQUEST_FRIENDS_GET_INFO_DETAILS = "friends/getInfoDetails.do";
	/**
	 * 我的(好友)资料详情  2.3.3
	 */
	public static final String REQUEST_FRIENDS_GET_MY_INFORMATIONS = "friends/getMyInformations.do";


	/**
	 * 根据USERID查询添加好友–已完成
	 */
	public static final String REQUEST_FRIENDS_SEARCH_BY_USERID = "friends/searchFriendsByUserId.do";

	/**
	 * 处理好友请求
	 */
	public static final String REQUEST_FRIENDS_DEAL_FRIENDS_REQUEST = "friends/dealFriendRequest.do";
	
	
	/**
	 * 修改密码
	 */
	public static final String UPDATE_PWD = "/user/mpass/";
	
	/**
	 * 修改用户昵称
	 */
	public static final String UPDATE_NICKNAME = "/user/updateNickName";
	/**
	 * 修改用户头像
	 */
	public static final String UPDATE_HEADER_URL=Connection_Type_Common+DEFAULT_REQ_HOST_IP+ "/user/updateHeadPic";
	/**
	 * 信息收集接口(意见、招聘、打分)
	 */
	public static final String UNIFOR = "/user/uinfor";
	/**
	 * 忘记密码
	 */
	public static final String FORGET_PWD = "/user/rpass";
	/**
	 * 用户注册
	 */
	public static final String REGISTER_USER = "/user/regist.inc";
	/**
	 * 用户地址(0:添加 1删除 2修改 3查询)
	 */
	public static final String ADDRESS_MANAGER = "/user/addressManage";
	/**
	 * 短信验证
	 */
	public static final String CHECK_CODE = "/user/checkCode";


    public static final String DOWNLOAD_REPO_INFO = "/app/downLoadApp";
    public static final String REQUEST_EXCEPTION_REPO = "";
}
