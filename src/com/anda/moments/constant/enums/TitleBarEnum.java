package com.anda.moments.constant.enums;
/** 
 * @author handong E-mail: handong@si-tech.com.cn
 * @version 创建时间：2014-11-5 上午11:50:51 
 * 类说明 
 */
public enum TitleBarEnum {

	ABOUT(0,"关于我们"),	
	FEEDBACK(1,"意见反馈"),	
	SHARE(2,"分享好友"),	
	ZHAOPIN(3,"推拿师招聘"), 
	COMPANY(4,"企业专区"), 
	SELLER(5,"推拿师入口"),
	
	ORDER_SALE(4,"销量排序",2),
	ORDER_TIME(5,"时间排序",0),
	ORDER_PRICE(6,"价格排序",1),
	
	PRICE_NONE(7,"价格区间"),
	PRICE_ORDER0(29,"100以下",0,100),
	PRICE_ORDER1(8,"100-300",100,300),
	PRICE_ORDER2(9,"300-500",300,500),
	PRICE_ORDER3(30,"500以上",500,-1),
	
	
	TYPE_ORDER_ALL(10,"全部",2),
	TYPE_ORDER_DOUBLE(11,"双人",1),
	TYPE_ORDER_MANY(12,"多人",1),
	TYPE_ORDER_SINGLE(13,"个人",0),
	//0时间 3均价 4累计成交量 5距离
	ORDER_DEFAULT(14,"默认排序",0),
//	ORDER_PRICE_SELLER(25,"价格排序",3),
	ORDER_DISTANCE(15,"距离排序",5),
	ORDER_COUNT(16,"接单次数",4),
	
	WORK_3_SMALL(17,"三年以下",0,3),
	WORK_3_5(18,"3-5年",3,5),
	WORK_5_10(19,"5-10年",5,10),
	WORK_10_BIG(28,"10年以上",10,-1),
	
	SHARE_SINA(20,"新浪微博"),
	SHARE_WEIXIN(21,"朋友圈"),
	SHARE_WEIXIN_FRIEND(31,"微信好友"),
	SHARE_TENCENT(22,"腾讯微博"),
	
	CITY_BEIJING(22,"北京"),
	CITY_GUANGZHOU(23,"广州"),
	CITY_SHENZHEN(24,"深圳"),
	
	SELLER_FAV(26,"收藏"),
	SELLER_SHARE(27,"分享");
	
	private int index;
	
	private String msg;
	
	private int value;
	
	private int start;
	private int end;
	
    private TitleBarEnum(int index,String msg) {
        this.index = index;
        this.msg = msg;
    }
    private TitleBarEnum(int index,String msg,int value) {
        this.index = index;
        this.msg = msg;
        this.value = value;
    }
    private TitleBarEnum(int index,String msg,int start ,int end){
    	this.index = index;
    	this.msg = msg;
    	this.start = start;
    	this.end = end;
    }

    public int getValue(){
    	return value;
    }
    
    public int getIndex() {
        return index;
    }
    
    public String getMsg() {
		return msg;
	}
    
	public int getStart() {
		return start;
	}
	public int getEnd() {
		return end;
	}
	public static TitleBarEnum getTitleBarResult(int index) {
        for (TitleBarEnum v : TitleBarEnum.values()) {
            if (index == v.getIndex()) {
                return v;
            }
        }
        return ABOUT;
    }
}
