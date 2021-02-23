package com.yesido.im.restful.utils.date;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * 
 * @ClassName: DateUtils
 * @Description: 日期工具类
 * @author: yesido
 * @date: Oct 15, 2014
 * 
 */
public class DateUtils {
    
    /**
     * 获取日期中的某数值。如获取月份
     * 
     * @param date
     *            日期
     * @param dateType
     *            日期格式
     * @return 数值
     */
    public static Integer getInteger(Date date, int dateType) {
        Integer integer = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            integer = calendar.get(dateType);
            if (dateType == Calendar.MONTH) {//如果是月份，Calendar是从0开始的，需要+1
                integer++;
            }
        } catch (Exception e) {
        }
        return integer;
    }
    
    /**
     * 获取日期的年份。失败返回null。
     * 
     * @param date
     *            日期
     * @return 年份
     */
    public static Integer getYear(Date date) {
        return getInteger(date, Calendar.YEAR);
    }
    
    /**
     * 获取日期的月份。失败返回null。
     * 
     * @param date
     *            日期
     * @return 月份
     */
    public static Integer getMonth(Date date) {
        return getInteger(date, Calendar.MONTH);
    }
    
    /**
     * 获取日期的天数。失败返回null。
     * 
     * @param date
     *            日期
     * @return 天
     */
    public static Integer getDay(Date date) {
        return getInteger(date, Calendar.DATE);
    }
    
    /**
     * 获取日期的小时。失败返回null。
     * 
     * @param date
     *            日期
     * @return 小时
     */
    public static Integer getHour(Date date) {
        return getInteger(date, Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 获取日期的分钟。失败返回null。
     * 
     * @param date
     *            日期
     * @return 分钟
     */
    public static Integer getMinute(Date date) {
        return getInteger(date, Calendar.MINUTE);
    }
    
    /**
     * 获取日期的秒钟。失败返回null。
     * 
     * @param date
     *            日期
     * @return 秒钟
     */
    public static Integer getSecond(Date date) {
        return getInteger(date, Calendar.SECOND);
    }
    
    /**
     * 创建日期
     * 
     * @param year
     * @param month
     * @param date
     * @return
     */
    public static Date createDate(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        month--;
        calendar.set(year, month, date);
        return calendar.getTime();
    }
    
    /**
     * 创建日期
     * 
     * @param year
     * @param month
     * @param date
     * @param hourOfDay
     * @param minute
     * @return
     */
    public static Date createDate(int year, int month, int date, int hourOfDay,
            int minute) {
        Calendar calendar = Calendar.getInstance();
        month--;
        calendar.set(year, month, date, hourOfDay, minute);
        return calendar.getTime();
    }
    
    /**
     * 创建日期
     * 
     * @param year
     * @param month
     * @param date
     * @param hourOfDay
     * @param minute
     * @param second
     * @return
     */
    public static Date createDate(int year, int month, int date, int hourOfDay,
            int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        month--;
        calendar.set(year, month, date, hourOfDay, minute, second);
        return calendar.getTime();
    }
    
    /**
     * 日期转成字符串(yyyy-MM-dd HH:mm:ss)
     * 
     * @param date
     * @return
     */
    public static String format(Date date) {
        String result = null;
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat(DateStyle.YYYY_MM_DD_HH_MM_SS.getValue());
            result = format.format(date);
        }
        return result;
    }
    
    /**
     * 日期转成字符串
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        String result = null;
        if (pattern == null) {
            pattern = DateStyle.YYYY_MM_DD.getValue();
        }
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            result = format.format(date);
        }
        return result;
    }
    
    /**
     * 字符串转成日期
     * 
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date toDate(String dateStr, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     * 增加日期的某一个值（如增加一年/一月/一天）
     * @param date
     * @param dateType
     * @param addNum
     * @return
     */
    public static Date add(Date date, int dateType, int addNum) {
        Date result = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(dateType, addNum);
            result = calendar.getTime();
        }
        return result;
    }
    /**
     * 将日期往后推addNum分钟
     * 
     * @param date
     * @param addNum
     * @return
     */
    public static Date addMin(Date date, int addNum) {
        return add(date, Calendar.MINUTE, addNum);
    }
    /**
     * 将日期往后推addNum小时
     * 
     * @param date
     * @param addNum
     * @return
     */
    public static Date addHours(Date date, int addNum) {
        return add(date, Calendar.HOUR, addNum);
    }
    /**
     * 将日期往后推addNum天
     * 
     * @param date
     * @param addNum
     * @return
     */
    public static Date addDay(Date date, int addNum) {
        return add(date, Calendar.DATE, addNum);
    }
    
    /**
     * 将日期往后推addNum月
     * 
     * @param date
     * @param addNum
     * @return
     */
    public static Date addMonth(Date date, int addNum) {
        return add(date, Calendar.MONTH, addNum);
    }
    
    /**
     * 将日期往后推addNum年
     * 
     * @param date
     * @param addNum
     * @return
     */
    public static Date addYear(Date date, int addNum) {
        return add(date, Calendar.YEAR, addNum);
    }
    
    /**
     * 判断两个日期前后,只判断到天
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean before(Date date1, Date date2) {
        return compare(date1, date2) < 0 ? true : false;
    }
    /**
     * 判断两个日期前后,只判断到天
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean after_dd(Date date1, Date date2) {
        return compare(date1, date2) > 0 ? true : false;
    }
    
    /**
     * 判断两个日期是为同一天
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean equals(Date date1, Date date2) {
        return compare(date1, date2) == 0 ? true : false;
    }
    
    /**
     * 比较两个日期,date1大的返回1
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static int compare(Date date1, Date date2) {
        String dateStr1 = format(date1);
        String dateStr2 = format(date2);
        return dateStr1.compareTo(dateStr2);
    }
    
   
    
    /**
     * 计算两个日期间隔天数
     * 
     * @param littleDate
     * @param bigDate
     * @return
     */
    public static double differDay(Date littleDate, Date bigDate) {
        return differ(littleDate, bigDate, 1000 * 60 * 60 *24);
    }
    
    /**
     * 计算两个日期间隔小时数
     * 
     * @param littleDate
     * @param bigDate
     * @return
     */
    public static double differHours(Date littleDate, Date bigDate) {
        return differ(littleDate, bigDate, 1000 * 60 * 60);
    }
    
    /**
     * 计算两个日期间隔分钟数
     * 
     * @param littleDate
     * @param bigDate
     * @return
     */
    public static double differMinute(Date littleDate, Date bigDate) {
        return differ(littleDate, bigDate, 1000 * 60);
    }
    
    /**
     * 计算两个日期间隔秒数
     * 
     * @param littleDate
     * @param bigDate
     * @return
     */
    public static double differSecond(Date littleDate, Date bigDate) {
        return differ(littleDate, bigDate, 1000);
    }
    /**
     * 根据计量单位计算计算两个日期间隔秒数(1000)/分钟数(1000*60)/小时数(1000*60*60)/天数(1000*60*60*24)
     * @param littleDate
     * @param bigDate
     * @param divider 计量单位
     * @return
     */
    private static double differ(Date littleDate, Date bigDate, int divider) {
        if (littleDate.after(bigDate)) {
            Date tem = littleDate;
            littleDate = bigDate;
            bigDate = tem;
        }
        BigDecimal bigDecimal = new BigDecimal(Integer.toString(divider));
        long result = bigDate.getTime() - littleDate.getTime();
        BigDecimal bigDecimal2 = new BigDecimal(Long.toString(result));
        BigDecimal decimal = bigDecimal2.divide(bigDecimal, 5, BigDecimal.ROUND_HALF_UP);
        return decimal.doubleValue();
    }
    /**
     * 获取日期的星期。失败返回null。
     * 
     * @param date
     *            日期
     * @return 星期
     */
    private static Week getWeek(Date date) {
        Week week = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            week = Week.getWeek(weekNumber);
        } catch (Exception e) {
        }
        return week;
    }
    /**
     * 获取中文的星期
     * @param date
     * @return
     */
    public static String getWeekCN(Date date) {
        if(date == null) {
            return null;
        }
        Week week = getWeek(date);
        return week.getChineseName();
    }
    /**
     * 获取英文的星期
     * @param date
     * @return
     */
    public static String getWeekEn(Date date) {
        return getWeekEn(date, false);
    }
    /**
     * 获取英文的星期
     * @param date
     * @param isShort 是否简写
     * @return
     */
    public static String getWeekEn(Date date, boolean isShort) {
        if(date == null) {
            return null;
        }
        Week week = getWeek(date);
        if(isShort) {
            return week.getEnShortName();
        }
        return week.getEnName();
    }
    /**
     * 获取星期的序号。星期日 为7
     * @param date
     * @return
     */
    public static int getWeekNum(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            return Week.getWeek(weekNumber).getNumber();
        } catch (Exception e) {
        }
        return -1;
    }
    public static void main(String[] args) {
	}
}
