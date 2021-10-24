package org.iii.SecBuzzer.template.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebConfig {
	public static int MAX_ERROR_TIMES;
	@Value("${webconfig.max_error_times}")
	public void setMAX_ERROR_TIMES(int _MAX_ERROR_TIMES) {
		MAX_ERROR_TIMES = _MAX_ERROR_TIMES;
	}

	public static int LOCK_MINUTES;
	@Value("${webconfig.lock_minutes}")
	public void setLOCK_MINUTES(int _LOCK_MINUTES) {
		LOCK_MINUTES = _LOCK_MINUTES;
	}	
	
	public static int FORGOT_EXPIRE_MINUTES;
	@Value("${webconfig.forgot_expire_minutes}")
	public void setFORGOT_EXPIRE_MINUTES(int _FORGOT_EXPIRE_MINUTES) {
		FORGOT_EXPIRE_MINUTES = _FORGOT_EXPIRE_MINUTES;
	}	
	
	public static int OTP_SURVIVAL_TIME;
	@Value("${webconfig.otp_survival_time}")
	public void setOTP_SURVIVAL_TIME(int _OTP_SURVIVAL_TIME) {
		OTP_SURVIVAL_TIME = _OTP_SURVIVAL_TIME;
	}	
	
	public static boolean DEVELOPMENT_MODE;
	@Value("${webconfig.development_mode}")
	public void setDEVELOPMENT_MODE(boolean _DEVELOPMENT_MODE) {
		DEVELOPMENT_MODE = _DEVELOPMENT_MODE;
	}
	
	public static boolean DEBUG_MODE;
	@Value("${webconfig.debug_mode}")
	public void setDEBUG_MODE(boolean _DEBUG_MODE) {
		DEBUG_MODE = _DEBUG_MODE;
	}

	public static boolean ENABLE_GOOGLE_RECAPTCHA;
	@Value("${webconfig.enable_google_recaptcha}")
	public void setENABLE_GOOGLE_RECAPTCHA(boolean _ENABLE_GOOGLE_RECAPTCHA) {
		ENABLE_GOOGLE_RECAPTCHA = _ENABLE_GOOGLE_RECAPTCHA;
	}

	public static String GOOGLE_RECAPTCHA_SECURITY_KEY;
	@Value("${webconfig.google_recaptcha_security_key}")
	public void setGOOGLE_RECAPTCHA_SECURITY_KEY(String _GOOGLE_RECAPTCHA_SECURITY_KEY) {
		GOOGLE_RECAPTCHA_SECURITY_KEY = _GOOGLE_RECAPTCHA_SECURITY_KEY;
	}
	
	public static String GOOGLE_RECAPTCHA_SITE_KEY;
	@Value("${webconfig.google_recaptcha_site_key}")
	public void setGOOGLE_RECAPTCHA_SITE_KEY(String _GOOGLE_RECAPTCHA_SITE_KEY) {
		GOOGLE_RECAPTCHA_SITE_KEY = _GOOGLE_RECAPTCHA_SITE_KEY;
	}
	
	public static String BEFORE_SESSION_TIMEOUT_MINUTES;
	@Value("${webconfig.before_session_timeout_minutes}")
	public void setBEFORE_SESSION_TIMEOUT_MINUTES(String _BEFORE_SESSION_TIMEOUT_MINUTES) {
		BEFORE_SESSION_TIMEOUT_MINUTES = _BEFORE_SESSION_TIMEOUT_MINUTES;
	}

	public static String WEB_SITE_URL;
	@Value("${webconfig.web_site_url}")
	public void setWEB_SITE_URL(String _WEB_SITE_URL) {
		WEB_SITE_URL = _WEB_SITE_URL;
	}
	
	public static int HISTORY_TIMES;
	@Value("${webconfig.history_times}")
	public void setHISTORY_TIMES(int _HISTORY_TIMES) {
		HISTORY_TIMES = _HISTORY_TIMES;
	}

	public static int HISTORY_DAYS;
	@Value("${webconfig.history_days}")
	public void setHISTORY_DAYS(int _HISTORY_DAYS) {
		HISTORY_DAYS = _HISTORY_DAYS;
	}	
	
	public static String PDF_FONT;
	@Value("${webconfig.pdf_font}")
	public void setPDF_FONT(String _PDF_FONT) {
		PDF_FONT = _PDF_FONT;
	}
	
}
