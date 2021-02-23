package com.yesido.im.restful.utils.date;

import java.util.ArrayList;
import java.util.List;

/**
 * 周一到周日的枚举类
 * @author yesido
 * @date 2016年2月2日 上午11:31:50
 */
public enum Week {

	/** 星期一. */
	MONDAY("星期一", "Monday", "Mon", 1), 
	/** 星期二. */
	TUESDAY("星期二", "Tuesday", "Tues", 2), 
	/** 星期三. */
	WEDNESDAY("星期三", "Wednesday", "Wed", 3), 
	/** 星期四. */
	THURSDAY("星期四", "Thursday", "Thur", 4),
	/** 星期五. */
	FRIDAY("星期五", "Friday", "Fri", 5),
	/** 星期六. */
	SATURDAY("星期六", "Saturday", "Sat", 6),
	/** 星期日. */
	SUNDAY("星期日", "Sunday", "Sun", 7);

	/** 中文星期. */
	private String name_cn;
	/** 英文星期. */
	private String name_en;
	/** 英文星期缩写. */
	private String name_enShort;
	/** 星期序号. */
	private int number;

	private Week(String name_cn, String name_en, String name_enShort, int number) {
		this.name_cn = name_cn;
		this.name_en = name_en;
		this.name_enShort = name_enShort;
		this.number = number;
	}

	/**
	 * 通过序号获取星期
	 * @param index
	 * @return
	 */
	public static Week getWeek(int index) {
		while (index > 7) {
			index -= 7;
		}
		while (index < 1) {
			index += 7;
		}
		Week week = null;
		switch (index) {
		case 1:
			week = Week.MONDAY;
			break;
		case 2:
			week = Week.TUESDAY;
			break;
		case 3:
			week = Week.WEDNESDAY;
			break;
		case 4:
			week = Week.THURSDAY;
			break;
		case 5:
			week = Week.FRIDAY;
			break;
		case 6:
			week = Week.SATURDAY;
			break;
		case 7:
			week = Week.SUNDAY;
			break;
		}

		return week;
	}

	/**
	 * 获取所有的日期
	 * @return
	 */
	public List<Week> getWeeks() {
		List<Week> weeks = new ArrayList<Week>();
		Week[] arrs = values();
		for (Week week : arrs) {
			weeks.add(week);
		}
		return weeks;
	}
	
	public String getChineseName() {
		return name_cn;
	}

	public String getEnName() {
		return name_en;
	}

	public String getEnShortName() {
		return name_enShort;
	}

	public int getNumber() {
		return number;
	}
}
