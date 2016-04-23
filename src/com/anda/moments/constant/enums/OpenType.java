package com.anda.moments.constant.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 打开状态设置
 * @author handong
 * @email dong.han1@renren-inc.com
 */
public enum OpenType {

	CLOSE(0),   //关闭
	
	OPEN(1);  //打开
	
	private int index;
	
	private static Map<Integer, OpenType> map;
	
	static {
        map = new HashMap<Integer, OpenType>();
        for (OpenType ust : OpenType.values()) {
            map.put(ust.getIndex(), ust);
        }
    }
	private OpenType(int index) {
        this.index = index;
    }
	public int getIndex() {
		return index;
	}
	public static Map<Integer, OpenType> getMap() {
        return map;
    }
}
