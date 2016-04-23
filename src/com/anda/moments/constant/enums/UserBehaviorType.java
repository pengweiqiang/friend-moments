package com.anda.moments.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserBehaviorType {

	SHARE_GAME(1),      //分享游戏
    INSTALL_GAME(2),    //安装游戏
    FAVORITE_GAME(3),   //收藏游戏
    
    FAVORITE_ALBUM(4),  //收藏专辑
    SHARE_ALBUM(5),     //分享专辑
    ;
	private int behaviorType;
	
	private static Map<Integer, UserBehaviorType> allTypeMap;
    
	static {
        allTypeMap = new HashMap<Integer, UserBehaviorType>();
        for (UserBehaviorType userBehaviorType : UserBehaviorType.values()) {
            allTypeMap.put(userBehaviorType.getBehaviorType(), userBehaviorType);
        }
    }
	private UserBehaviorType(int behaviorType) {
        this.behaviorType = behaviorType;
    }

    public int getBehaviorType() {
        return behaviorType;
    }

    public static Map<Integer, UserBehaviorType> getAllTypeMap() {
        return allTypeMap;
    }
}
