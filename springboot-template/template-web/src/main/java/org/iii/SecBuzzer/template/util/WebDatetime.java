package org.iii.SecBuzzer.template.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期時間格式化
 */
public class WebDatetime {
	final static Logger logger = LoggerFactory.getLogger(WebDatetime.class);

	public final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat simpleDateFormatMS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	public final static SimpleDateFormat simpleDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final static long ONE_SECOND_IN_MILLIS = 1000;
	private final static long ONE_MINUTE_IN_MILLIS = 60000;
	private final static long ONE_HOUR_IN_MILLIS = 3600000;
	public final static String MAX_DATETIME = "9999-12-31 23:59:59";
	public final static String MIN_DATETIME = "1000-01-01 00:00:00";

	/**
	 * 取得日期時間字串 若是null則回傳空字串
	 * 
	 * @param datetime
	 *            日期時間
	 * @return 日期時間字串(yyyy-MM-dd HH:mm:ss)
	 */
	public static String toString(Date datetime) {
		String result = "";
		try {
			if (datetime != null) {
				result = simpleDateFormat.format(datetime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 取得日期時間字串 若是null則回傳空字串
	 * 
	 * @param datetime
	 *            日期時間
	 * @param format
	 *            格式
	 * @return 日期時間字串
	 */
	public static String toString(Date datetime, String format) {
		String result = "";
		try {
			if (datetime != null) {
				result = new SimpleDateFormat(format).format(datetime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 轉換日期
	 * 
	 * @param datetime
	 *            日期時間字串
	 * @return 日期時間(yyyy-MM-dd HH:mm:ss)
	 */
	public static Date parseOnlyDate(String datetime) {
		Date result = new Date();
		try {
			result = simpleDateOnlyFormat.parse(datetime);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 轉換日期時間
	 * 
	 * @param datetime
	 *            日期時間字串
	 * @return 日期時間(yyyy-MM-dd HH:mm:ss)
	 */
	public static Date parse(String datetime) {
		Date result = new Date();
		try {
			result = simpleDateFormat.parse(datetime);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 轉換日期時間
	 * 
	 * @param datetime
	 *            日期時間字串
	 * @return 日期時間(yyyy-MM-ddTHH:mm:ss.SSS)
	 */
	public static Date parseForMS(String datetime) {
		Date result = new Date();
		try {
			result = simpleDateFormatMS.parse(datetime);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 轉換開始日期時間
	 * 
	 * @param datetime
	 *            日期時間字串
	 * @return 日期時間(yyyy-MM-dd HH:mm:ss)
	 */
	public static Date parseSdate(String datetime) {
		Date result = new Date();
		try {
			result = simpleDateFormat.parse(datetime + " 00:00:00.0000");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 轉換結束日期時間
	 * 
	 * @param datetime
	 *            日期時間字串
	 * @return 日期時間(yyyy-MM-dd HH:mm:ss)
	 */
	public static Date parseEdate(String datetime) {
		Date result = new Date();
		try {
			result = simpleDateFormat.parse(datetime + " 23:59:59.9999");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 轉換日期時間
	 * 
	 * @param datetime
	 *            日期時間字串
	 * @param format
	 *            格式
	 * @return 日期時間
	 */
	public static Date parse(String datetime, String format) {
		Date result = new Date();
		try {
			result = new SimpleDateFormat(format).parse(datetime);
		} catch (Exception e) {
			result = new Date();
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 對傳入時間增加秒數
	 * 
	 * @param datetime
	 *            傳入時間
	 * @param seconds
	 *            增加秒數
	 * @return 已增加後的時間
	 */
	public static Date addSeconds(Date datetime, long seconds) {
		Date result = new Date();
		try {
			if (datetime == null) {
				datetime = result;
			}
			long nowTime = datetime.getTime();
			result = new Date(nowTime + (seconds * ONE_SECOND_IN_MILLIS));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 對傳入時間減少時數
	 * 
	 * @param datetime
	 *            傳入時間
	 * @param hours
	 *            減少時數
	 * @return 已增加後的時間
	 */
	public static Date substrationHours(Date datetime, long hours) {
		Date result = new Date();
		try {
			if (datetime == null) {
				datetime = result;
			}
			long nowTime = datetime.getTime();
			result = new Date(nowTime - (hours * ONE_HOUR_IN_MILLIS));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 對傳入時間增加時數
	 * 
	 * @param datetime
	 *            傳入時間
	 * @param hours
	 *            增加時數
	 * @return 已增加後的時間
	 */
	public static Date addHours(Date datetime, long hours) {
		Date result = new Date();
		try {
			if (datetime == null) {
				datetime = result;
			}
			long nowTime = datetime.getTime();
			result = new Date(nowTime + (hours * ONE_HOUR_IN_MILLIS));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 對傳入時間增加分鐘
	 * 
	 * @param datetime
	 *            傳入時間
	 * @param minutes
	 *            增加分鐘
	 * @return 已增加後的時間
	 */
	public static Date addMinutes(Date datetime, long minutes) {
		Date result = new Date();
		try {
			if (datetime == null) {
				datetime = result;
			}
			long nowTime = datetime.getTime();
			result = new Date(nowTime + (minutes * ONE_MINUTE_IN_MILLIS));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 對傳入時間增加天數
	 * 
	 * @param datetime
	 *            傳入時間
	 * @param days
	 *            增加天數
	 * @return 已增加後的時間
	 */
	public static Date addDays(Date datetime, long days) {
		Date result = new Date();
		try {
			if (datetime == null) {
				datetime = result;
			}
			long nowTime = datetime.getTime();
			result = new Date(nowTime + (days * 24 * 60 * ONE_MINUTE_IN_MILLIS));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 對傳入時間增加月數
	 * 
	 * @param date
	 *            傳入時間，如果傳入<b>null</b>則表示從現在時刻增加指定月數
	 * @param months
	 *            增加月數
	 * @return 已增加後的時間
	 */
	public static Date addMonths(Date date, int months) {
		Calendar c = Calendar.getInstance();
		c.setTime(null == date ? new Date() : date);
		c.add(Calendar.MONTH, months);
		return c.getTime();
	}

	/**
	 * 對傳入時間增加年數
	 * 
	 * @param date
	 *            傳入時間，如果傳入<b>null</b>則表示從現在時刻增加指定年數
	 * @param years
	 *            增加年數
	 * @return 已增加後的時間
	 */
	public static Date addYears(Date date, int years) {
		Calendar c = Calendar.getInstance();
		c.setTime(null == date ? new Date() : date);
		c.add(Calendar.YEAR, years);
		return c.getTime();
	}

	/**
	 * 
	 * @param year
	 *            int 年份
	 * @param month
	 *            int 月份
	 * 
	 * @return int 某年某月的最后一天 需要注意的是：月份是從0開始的，比如說如果輸入5的話，實際上顯示的是4月份的最後一天，千萬不要搞錯了哦
	 */
	public static int getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, (month - 1));
		// 某年某月的最后一天
		return cal.getActualMaximum(Calendar.DATE);
	}

	
	/**
	 * 以目前系統的時區為準將日期時間轉換為當日開始日期時間(23:59:59)
	 * 
	 * @param date
	 *            日期時間
	 * @return 當日開始日期時間(23:59:59)
	 */
	public static Date getEndOfDay(Date date) {
		String datetime = simpleDateOnlyFormat.format(date);
		return WebDatetime.parseEdate(datetime);
	}
	
	/**
	 * 以目前系統的時區為準將日期時間轉換為當日結束日期時間(00:00:00)
	 * 
	 * @param date
	 *            日期時間
	 * @return 當日結束日期時間(00:00:00)
	 */
	public static Date getStartOfDay(Date date) {
		String datetime = simpleDateOnlyFormat.format(date);
		return WebDatetime.parseSdate(datetime);
	}
}
