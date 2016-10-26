package com.anda.moments.api.constant;

public interface ReqUrls {

public static final String PROJECT_NAME = "";
	
	public static final boolean neuSecure = false;

	public static final int MEDIA_TYPE_PICTURE = 1;//图片
	public static final  int MEDIA_TYPE_AUDIO = 2;//音频
	public static final int MEDIA_TYPE_VIDEO = 3;//视频
	
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
	public static final String CONFIG_HOST_IP="www.weimkeji.com/friendscircle"; //dns ip
//	public static final String CONFIG_HOST_IP="123.57.163.138/friendscircletest";
//	public static final String CONFIG_HOST_IP="http://bldj.com" 
	
//	public static final String DEFAULT_REQ_HOST_IP = "http://bldj.com";
//    public static final String DEFAULT_REQ_HOST_IP = "172.16.102.145:8080/wechatshop/login.html"; //默认值异常的请求地址
	 public static final String DEFAULT_REQ_HOST_IP = Connection_Type_Common+CONFIG_HOST_IP+"/"; //默认值异常的请求地址

	//融云参数配置
	public static final String APPKEY_RONG = "cpj2xarljnnwn";

	public static final String APPSERCERT_RONG = "tA7Dxi0Sl1";

	public static final int LIMIT_DEFAULT_NUM=10;
	
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
	 * 更改评论消息状态
	 */
	public static final String REQUEST_FRIENDS_UPDATE_COMMENT_MSG = "friends/updateCommentMsg.do";

	/**
	 * 获取最新评论数量和获取最新评论消息列表接口
	 */
	public static final String REQUEST_FRIENDS_FIND_LATEST_COMMINFO = "friends/findLatestCommInfo.do";

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
	 * 判断是否已添加好友(判断是否收到对方添加好友请求)
	 */
	public static final String REQUEST_IS_EXISTS_FRIENDS= "friends/isExistsFriends.do";
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
	 * 萌化了
	 */
	public static final String REQUEST_FRIENDS_LOVE_STH = "friends/toLovelySth.do";
	/**
	 * 点赞
	 */
	public static final String REQUEST_FRIENDS_PRAISE = "friends/praise.do";

	/**
	 * 删除动态
	 */
	public static final String REQUEST_DELETE_MESSAGE = "friends/deleteNews.do";

	/**
	 * 取消点赞
	 */
	public static final String REQUEST_CANCEL_PRAISE = "friends/canclePraise.do";
	/**
	 * 意见反馈接口
	 */
	public static final String REQUEST_SUBMIT_IDEA = "friends/submitIdea.do";
	/**
	 * 删除评论
	 */
	public static final String REQUEST_DELETE_COMMENT = "friends/deleteComment.do";
	/**
	 * 获取融云token
	 */
	public static final String REQUEST_GET_RONGYUN_TOKEN = "friends/obtainRongCloudToken.do";
	/**
	 * 获取APP皮肤列表
	 */
	public static final String REQUEST_GET_APP_SKINS = "friends/getAPPSkins.jsp";
	/**
	 * 更换APPP皮肤
	 */
	public static final String REQUEST_UPDATE_SKIN = "friends/updateSkin.do";

	/**
	 * 刷新单个动态详情信息
	 */
	public static final String REQUEST_GET_NEW_INFOS_BYID = "friends/getNewsInfoById.do";
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
	 * 根据昵称、备注、ID、手机号进行模糊查询自己的好友
	 */
	public static final String REQUEST_FRIENDS_SEARCH = "friends/searchMyFriendsByConditions.do";

	//修改好友备注
	public static final String REQUEST_UPDATE_FRIENDS_TAGS ="friends/updateFriendsTags.do";

	//获取省市地区列表
	public static final String REQUEST_GET_DISTRICT_INFO = "friends/getDistrictInfo.jsp";

	/**
	 * 查询是否存在该userId
	 */
	public static final String REQUEST_IS_EXIST_USER_ID = "friends/isExistUser.do";

	/**
	 * 处理好友请求
	 */
	public static final String REQUEST_FRIENDS_DEAL_FRIENDS_REQUEST = "friends/dealFriendRequest.do";

	/**
	 * 删除好友请求
	 */
	public static final String REQUEST_DELETE_FRIENDS_REQUEST = "friends/deleteFriendMsg.do";

	/**
	 * 删除好友
	 */
	public static final String REQUEST_DELETE_FRIENDS = "friends/deleteFriend.do";

	/**
	 * 修改个人资料
	 */
	public static final String REQUEST_UPDATE_USER_INFO = "friends/updateMyInfos.do";

	/**
	 * 保存我是否看他的动态
	 */
	public static final String REQUEST_ADD_NOT_NOTICE_PERSON = "friends/addNotNoticePerson.do";
	/**
	 * 保存是否允许查看我的朋友圈接口
	 */
	public static final String REQUEST_ADD_NOT_PERM_LOOK_PERSON = "friends/addNotPermLookPerson.do";
	
	
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

	public static final String UPDATE_IS_BLACK= "friends/updateIsBlock.do";

	public static final String ADD_REPORT= "friends/addReport.do";

	public static final String GET_MY_BLACK_LIST = "friends/getBlockFriendsList.do";

	public static final String IS_CAN_CHAT_TOGETHER = "friends/isCanChatTogether.do";


    public static final String DOWNLOAD_REPO_INFO = "/app/downLoadApp";
    public static final String REQUEST_EXCEPTION_REPO = "";
}
