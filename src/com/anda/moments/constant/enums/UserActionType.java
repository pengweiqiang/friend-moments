package com.anda.moments.constant.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户登陆+注册等行为
 * @author handong
 * @email dong.han1@renren-inc.com
 */
public enum UserActionType {

	LOGIN(1),	//登陆
	
	REGISTER(2), //注册
	
	USER_NOTICE(3),  //关注
	
	USER_ARROUND(4); //身边
	
	private int userActionType;
	
	private static Map<Integer, UserActionType> allTypeMap;
    
	static {
        allTypeMap = new HashMap<Integer, UserActionType>();
        for (UserActionType uat : UserActionType.values()) {
            allTypeMap.put(uat.getUserActionType(), uat);
        }
    }
	
	private UserActionType(int type) {
        this.userActionType =type;
    }

    public int getUserActionType() {
        return userActionType;
    }
}
