/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package zhwx.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间转换工具类
 * @author 容联•云通讯 Modify By Li.Xin @ 中电和讯
 * @date 2014-12-10
 * @version 4.0
 */
public class DateUtil {
	
	public static final TimeZone tz = TimeZone.getTimeZone("GMT+8:00");
	public static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final long ONEDAY = 86400000;
	public static final int SHOW_TYPE_SIMPLE = 0;
	public static final int SHOW_TYPE_COMPLEX = 1;
	public static final int SHOW_TYPE_ALL = 2;
	public static final int SHOW_TYPE_CALL_LOG = 3;
	public static final int SHOW_TYPE_CALL_DETAIL = 4;

	/**
	 * 获取当前当天日期的毫秒数 2012-03-21的毫秒数
	 *
	 * @return
	 */
	public static long getCurrentDayTime() {
		Date d = new Date(System.currentTimeMillis());
		String formatDate = yearFormat.format(d);
		try {
			return (yearFormat.parse(formatDate)).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

    public static String formatDate(int year, int month, int day) {
        Date d = new Date(year - 1900, month, day);
        return yearFormat.format(d);
    }

    public static long getDateMills(int year, int month, int day) {
        //Date d = new Date(year, month, day);
		// 1960 4 22
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(year, month, day);
		TimeZone tz = TimeZone.getDefault();
		calendar.setTimeZone(tz);
        return calendar.getTimeInMillis();
    }
	
    public static String getDateString(long time, int type) {
		Calendar c = Calendar.getInstance();
		c = Calendar.getInstance(tz);
		c.setTimeInMillis(time);
		long currentTime = System.currentTimeMillis();
		Calendar current_c = Calendar.getInstance();
		current_c = Calendar.getInstance(tz);
		current_c.setTimeInMillis(currentTime);

		int currentYear = current_c.get(Calendar.YEAR);
		int y = c.get(Calendar.YEAR);
		int m = c.get(Calendar.MONTH) + 1;
		int d = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		long t = currentTime - time;
		long t2 = currentTime - getCurrentDayTime();
		String dateStr = "";
		if (t < t2 && t > 0) {
			if (type == SHOW_TYPE_SIMPLE) {
				dateStr = (hour < 10 ? "0" + hour : hour) + ":"
						+ (minute < 10 ? "0" + minute : minute);
			} else if (type == SHOW_TYPE_COMPLEX) {
				dateStr = "今天  " + (hour < 10 ? "0" + hour : hour) + ":"
						+ (minute < 10 ? "0" + minute : minute);
			} else if (type == SHOW_TYPE_CALL_LOG) {
				dateStr = "今天  " + (hour < 10 ? "0" + hour : hour) + ":"
						+ (minute < 10 ? "0" + minute : minute);
			} else if (type == SHOW_TYPE_CALL_DETAIL) {
				dateStr = "今天  ";
			} else {
				dateStr = (hour < 10 ? "0" + hour : hour) + ":"
						+ (minute < 10 ? "0" + minute : minute) + ":"
						+ (second < 10 ? "0" + second : second);
			}
		} else if (t < (t2 + ONEDAY) && t > 0) {
			if (type == SHOW_TYPE_SIMPLE || type == SHOW_TYPE_CALL_DETAIL) {
				dateStr = "昨天  ";
			} else if (type == SHOW_TYPE_COMPLEX ) {
				dateStr = "昨天  " + (hour < 10 ? "0" + hour : hour) + ":"
						+ (minute < 10 ? "0" + minute : minute);
			} else if (type == SHOW_TYPE_CALL_LOG) {
				dateStr = "昨天  " + (hour < 10 ? "0" + hour : hour) + ":"
						+ (minute < 10 ? "0" + minute : minute);
			} else {
				dateStr = "昨天  " + (hour < 10 ? "0" + hour : hour) + ":"
						+ (minute < 10 ? "0" + minute : minute) + ":"
						+ (second < 10 ? "0" + second : second);
			}
		} else if (y == currentYear) {
			if (type == SHOW_TYPE_SIMPLE) {
				dateStr = (m < 10 ? "0" + m : m) + "/" + (d < 10 ? "0" + d : d);
			} else if (type == SHOW_TYPE_COMPLEX) {
				dateStr = (m < 10 ? "0" + m : m) + "月" + (d < 10 ? "0" + d : d)
						+ "日";
			} else if (type == SHOW_TYPE_CALL_LOG || type == SHOW_TYPE_COMPLEX) {
				dateStr = (m < 10 ? "0" + m : m) + /* 月 */"/"
						+ (d < 10 ? "0" + d : d) + /* 日 */" "
						+ (hour < 10 ? "0" + hour : hour) + ":"
						+ (minute < 10 ? "0" + minute : minute);
			} else if (type == SHOW_TYPE_CALL_DETAIL) {
				dateStr = y + "/" + (m < 10 ? "0" + m : m) + "/"
						+ (d < 10 ? "0" + d : d);
			} else {
				dateStr = (m < 10 ? "0" + m : m) + "月" + (d < 10 ? "0" + d : d)
						+ "日 " + (hour < 10 ? "0" + hour : hour) + ":"
						+ (minute < 10 ? "0" + minute : minute) + ":"
						+ (second < 10 ? "0" + second : second);
			}
		} else {
			if (type == SHOW_TYPE_SIMPLE) {
				dateStr = y + "/" + (m < 10 ? "0" + m : m) + "/"
						+ (d < 10 ? "0" + d : d);
			} else if (type == SHOW_TYPE_COMPLEX ) {
				dateStr = y + "年" + (m < 10 ? "0" + m : m) + "月"
						+ (d < 10 ? "0" + d : d) + "日";
			} else if (type == SHOW_TYPE_CALL_LOG || type == SHOW_TYPE_COMPLEX) {
				dateStr = y + /* 年 */"/" + (m < 10 ? "0" + m : m) + /* 月 */"/"
						+ (d < 10 ? "0" + d : d) + /* 日 */"  "/*
																 * + (hour < 10
																 * ? "0" + hour
																 * : hour) + ":"
																 * + (minute <
																 * 10 ? "0" +
																 * minute :
																 * minute)
																 */;
			} else if (type == SHOW_TYPE_CALL_DETAIL) {
				dateStr = y + "/" + (m < 10 ? "0" + m : m) + "/"
						+ (d < 10 ? "0" + d : d);
			} else {
				dateStr = y + "年" + (m < 10 ? "0" + m : m) + "月"
						+ (d < 10 ? "0" + d : d) + "日 "
						+ (hour < 10 ? "0" + hour : hour) + ":"
						+ (minute < 10 ? "0" + minute : minute) + ":"
						+ (second < 10 ? "0" + second : second);
			}
		}
		return dateStr;
	}
	/**
	 * 
	 * @return
	 */
	public static long getCurrentTime() {
		return System.currentTimeMillis() / 1000;
	}

    public static long getActiveTimelong(String result) {
        try {
            Date parse = yearFormat.parse(result);
            return parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }
    /**
	 * 根据自定义的格式获取日期字符串
	 * @param customFormat 自定义的格式
	 * @return
	 */
	public static String getCurrDateString(String customFormat) {
		return new SimpleDateFormat(customFormat).format(new Date());
	}

	/**
	 * 得到当前日期字符串，格式为 yyyy-MM-dd
	 * @return 当前日期的字符串
	 */
	public static String getCurrDateString() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
	/**
	 * 得到当前日期字符串，格式为 yyyy-MM-dd
	 * @return 当前日期的字符串
	 */
	public static String getCurrTimeString() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	/**
	 * 按日期得到字符串，格式为 yyyy-MM-dd
	 * @param date 日期
	 * @return
	 */
	public static String getDateString(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	/**
	 * 得到当前日期字符串，格式为 yyyy年MM月dd日
	 * @return 当前日期的字符串
	 */
	public static String getCurrDateStringChina() {
		return new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
	}

	/**
	 * 按日期得到字符串，格式为 yyyy年MM月dd日
	 * @param date 日期
	 * @return
	 */
	public static String getDateStringChina(Date date) {
		return new SimpleDateFormat("yyyy年MM月dd日").format(date);
	}

	/**
	 * 得到当前精确到分的日期字符串，格式为 yyyy-MM-dd HH:mm
	 * @return 当前日期的字符串
	 */
	public static String getCurrDateMinString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
	}

	/**
	 * 按日期得到精确到秒的字符串，格式为 yyyy-MM-dd HH:mm
	 * @param date
	 * @return
	 */
	public static String getDateMinString(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
	}

	/**
	 * 得到当前精确到秒的日期字符串，格式为 yyyy-MM-dd HH:mm:ss
	 * @return 当前日期的字符串
	 */
	public static String getCurrDateSecondString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/**
	 * 按日期得到精确到秒的日期字符串，格式为 yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String getDateSecondString(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	/**
	 * 得到当前精确到毫秒的日期字符串，格式为yyyy-MM-dd HH:mm:ss:SSS
	 * 
	 * @return 当前日期的字符串
	 */
	public static String getCurrDateMillisecondString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
	}

	/**
	 * 按字符串得到日期对象，格式为 yyyy-MM-dd
	 * @param dateStr 日期字符串
	 * @return
	 */
	public static Date getDate(String dateStr) {
		if (StringUtil.isBlank(dateStr)) {
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			
		}
		return date;
	}

	
	public static Date getDate(String dateStr, String customFormat) {
		if (StringUtil.isBlank(dateStr) || StringUtil.isBlank(customFormat)) {
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat(customFormat);
		Date date = null;
		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			
		}
		return date;
	}

	/**
	 * 按字符串得到日期对象，格式为 yyyy-MM-dd HH:mm
	 * @param str
	 * @return
	 */
	public static Date getDateMin(String str) {
		if (StringUtil.isBlank(str)) {
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = df.parse(str);
		} catch (ParseException e) {
			
		}
		return date;
	}

	/**
	 * 按字符串得到日期对象，格式为 yyyy-MM-dd HH:mm:ss
	 * @param str
	 * @return
	 */
	public static Date getDateSecond(String str) {
		if (StringUtil.isBlank(str)) {
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = df.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 得到下个月的第一天
	 * @param date
	 * @return
	 */
	public static Date getNextMonthFirstDay(Date date) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.add(Calendar.MONTH, 1);
		calender.set(Calendar.DAY_OF_MONTH, 1);
		Date d = calender.getTime();
		return d;
	}

	/**
	 * 是否过期
	 * @param dateString 开始日期字符串，格式 yyyy-MM-dd HH:mm
	 * @param limit 有效天数
	 * @return
	 */
	public static boolean isTimeOut(String dateString, int limit) {
		Date dueDate = getDateMin(dateString);
		Date nowDate = getDateMin(DateUtil.getCurrDateMinString());
		if ((nowDate.getTime() - dueDate.getTime()) < limit * 24 * 60 * 60 * 1000) {
			return false;
		}
		return true;
	}

	/**
	 * 比较时间大小
	 * @param d1 格式为 yyyy-MM-dd HH:mm:ss
	 * @param d2 格式为 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static boolean compareTime(String d1, String d2) {
		Date date1 = getDateSecond(d1);
		Date date2 = getDateSecond(d2);
		if (date1.getTime() - date2.getTime() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 比较日期大小
	 * @param d1 格式为 yyyy-MM-dd
	 * @param d2 格式为 yyyy-MM-dd
	 * @return
	 */
	public static boolean compareDay(String d1, String d2) {
		Date date1 = getDate(d1);
		Date date2 = getDate(d2);
		if (date1.getTime() - date2.getTime() >= 24 * 60 * 60 * 1000) {
			return true;
		}
		return false;
	}

	/**
	 * 根据指定的年、月、日返回当前是星期几。
	 * @param date(yyyy-mm-dd)
	 * @return 返回一个代表当期日期是星期几的数字。0表示星期天、1表示星期一、6表示星期六。
	 */
	public static int getDayOfWeek(String dateStr) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(getDate(dateStr));
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0) {
			w = 0;
		}
		return w;
	}

	/**
	 * 得到星期显示值
	 * @param dateStr 日期字符串，格式yyyy-MM-dd
	 * @return
	 */
	public static String getWeekStr(String dateStr) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		return weekDays[getDayOfWeek(dateStr)];
	}
	
	/**
	 * 得到星期显示值(英文缩写)
	 * @param dateStr 日期字符串，格式yyyy-MM-dd
	 * @return
	 */
	public static String getWeekStrEn(String dateStr) {
		String[] weekDays = {"Sun" ,"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		return weekDays[getDayOfWeek(dateStr)];
	}

	/**
	 * 求两个日期相差天数
	 * @param startDateStr 开始日期字符串，格式yyyy-MM-dd
	 * @param endDateStr 结束日期字符串，格式yyyy-MM-dd
	 * @return 天数，整数
	 */
	public static long getIntervalDays(String startDateStr, String endDateStr) {
		Date d1 = getDate(startDateStr);
		Date d2 = getDate(endDateStr);
		return (d2.getTime() - d1.getTime()) / (3600 * 24 * 1000);
	}

	/*****************************************wbf补充   start************************************************/

	/**
	 * 
	 * 取得给定日期加上一定天数后的日期对象.
	 * 
	 * @param date
	 * 
	 * 给定的日期对象
	 * 
	 * @param amount
	 * 
	 * 需要添加的天数，如果是向前的天数，使用负数就可以.
	 * 
	 * @param format
	 * 
	 * 输出格式.
	 * 
	 * @return Date 加上一定天数以后的Date对象.
	 * 
	 */
	public static String getFormatDateAdd(Date date, int amount) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(GregorianCalendar.DATE, amount);
		return getDateString(cal.getTime());
	}

	/**
	 * 
	 * 返回指定日期的前一天。<br>
	 * @param sourceDate
	 * 
	 */
	public static String getYestoday(String sourceDate) {
		return getFormatDateAdd(getDate(sourceDate), -1);
	}

	/**
	 * 返回指定日期的后一天。<br>
	 * @param sourceDate
	 * 
	 */
	public static String getFormatDateTommorrow(String sourceDate) {
		return getFormatDateAdd(getDate(sourceDate), 1);
	}
	
	
	 /**
     * 两个时间之间相差距离多少天
     * @param one 时间参数 1：
     * @param two 时间参数 2：
     * @return 相差天数
     */
    public static long getDistanceDays(String str1, String str2) throws Exception{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date one;
        Date two;
        long days=0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }
    
    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};
        return times;
    }
    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }
}
