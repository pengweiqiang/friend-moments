package com.anda.moments.constant.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserSettingType {

	ARROUND(11), //附近的人
	
	DOWNLOAD_TYPE(12),  //下载2G/3G提示
	
	GAME_NOTIFY(13),   //游戏更新通知栏提醒
	
	GAME_START_ALLOCATE(14),  //启动游戏后自动清理内存
	
	INSTALL_DEL_APK(15),   //游戏安装成功后安装包自动删除
	
	PAGE_SHOW(16);       //主页显示
	
	private int index;
	
	private static Map<Integer, UserSettingType> map;
	
	static {
        map = new HashMap<Integer, UserSettingType>();
        for (UserSettingType ust : UserSettingType.values()) {
            map.put(ust.getIndex(), ust);
        }
    }
	private UserSettingType(int index) {
        this.index = index;
    }
	public int getIndex() {
		return index;
	}
	public static Map<Integer, UserSettingType> getMap() {
        return map;
    }
}
