package com.yesido.im.restful.utils.date;

import java.util.ArrayList;
import java.util.List;

/**
 * 日期格式枚举类
 * @author yesido
 * @date 2016年2月2日 上午11:31:29
 */
public enum DateStyle {
	
	MM_DD("MM-dd"),
	YYYY_MM("yyyy-MM"),
	YYYY_MM_DD("yyyy-MM-dd"),
	MM_DD_HH_MM("MM-dd HH:mm"),
	MM_DD_HH_MM_SS("MM-dd HH:mm:ss"),
	YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
	YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
	
	EN_MM_DD("MM/dd"),
	EN_YYYY_MM("yyyy/MM"),
	EN_YYYY_MM_DD("yyyy/MM/dd"),
	EN_MM_DD_HH_MM("MM/dd HH:mm"),
	EN_MM_DD_HH_MM_SS("MM/dd HH:mm:ss"),
	EN_YYYY_MM_DD_HH_MM("yyyy/MM/dd HH:mm"),
	EN_YYYY_MM_DD_HH_MM_SS("yyyy/MM/dd HH:mm:ss"),
	
	CN_MM_DD("MM月dd日"),
	CN_YYYY_MM("yyyy年MM月"),
	CN_YYYY_MM_DD("yyyy年MM月dd日"),
	CN_MM_DD_HH_MM("MM月dd日 HH:mm"),
	CN_MM_DD_HH_MM_SS("MM月dd日 HH:mm:ss"),
	CN_YYYY_MM_DD_HH_MM("yyyy年MM月dd日 HH:mm"),
	CN_YYYY_MM_DD_HH_MM_SS("yyyy年MM月dd日 HH:mm:ss"),
	
	HH_MM("HH:mm"),
	HH_MM_SS("HH:mm:ss");
	
	/** 日期格式. */
	private String value;
	
	private DateStyle(String value) {
	    this.value = value;
	}
	
	public String getValue() {
	    return value;
	}
	
	/**
	 * 获取所有的日期格式
	 * @return
	 */
	public List<DateStyle> getDateStyles() {
		List<DateStyle> dateStyles = new ArrayList<DateStyle>();
		DateStyle[] arrs = values();
		for (DateStyle week : arrs) {
			dateStyles.add(week);
		}
		return dateStyles;
	}
}
