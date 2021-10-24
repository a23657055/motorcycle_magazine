package org.iii.SecBuzzer.template.util;

import java.util.Iterator;
import java.util.Locale;

import org.json.JSONObject;

/**
 * 文字訊息共用
 */
public class WebMessage {
	/**
	 * 取得使用者前端語系資料
	 * 
	 * @param locale
	 *            Locale
	 * @return Language_Country String
	 */
	public static String getLanguageCountry(Locale locale) {
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String lc = "";
		if (country.isEmpty()) {
			lc = language;
		} else {
			lc = language + "_" + country;
		}
		return lc;
	}

	/**
	 * 取得使用者前端語系資料
	 * 
	 * @param locale
	 *            Locale
	 * @return Language_Country String
	 */
	public static String getLanguage(Locale locale) {
		return locale.getLanguage();
	}

	/**
	 * 取得多國語言字串
	 * 
	 * @param message
	 *            訊息
	 * @param locale
	 *            Locale
	 * @return 單一語言字串
	 */
	public static String parseMessage(String message, Locale locale) {
		try {
			String lc = getLanguageCountry(locale);
			String language = getLanguage(locale);
			JSONObject jsonObj = new JSONObject(message);
			if (jsonObj.length() == 0) {
				message = "";
			} else if (jsonObj.length() == 1) {
				Iterator<String> keys = jsonObj.keys();
				message = jsonObj.optString(keys.next());
			} else {
				message = jsonObj.optString(lc);
				if (message.equals("")) {
					message = jsonObj.optString(language);
				}
				if (message.equals("")) {
					Iterator<String> keys = jsonObj.keys();
					message = jsonObj.optString(keys.next());
				}
			}
			return message;
		} catch (Exception e) {
			return message;
		}
	}
}
