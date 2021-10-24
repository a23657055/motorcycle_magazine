package org.iii.SecBuzzer.template.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import org.iii.SecBuzzer.template.dao.ForgotTempDAO;
import org.iii.SecBuzzer.template.dao.MemberDAO;
import org.iii.SecBuzzer.template.dao.MemberHistoryDAO;
import org.iii.SecBuzzer.template.dao.SsoDAO;
import org.iii.SecBuzzer.template.domain.ForgotTemp;
import org.iii.SecBuzzer.template.domain.Member;
import org.iii.SecBuzzer.template.domain.MemberHistory;
import org.iii.SecBuzzer.template.domain.Sso;
import org.iii.SecBuzzer.template.domain.ViewMember;
import org.iii.SecBuzzer.template.util.WebConfig;
import org.iii.SecBuzzer.template.util.WebCrypto;
import org.iii.SecBuzzer.template.util.WebDatetime;
import org.iii.SecBuzzer.template.util.WebTOTPGenerator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 使用者資料服務
 */
@Service
public class MemberService {
	final static Logger logger = LoggerFactory.getLogger(MemberService.class);
	
	@Autowired
	private MemberDAO memberDAO;

	@Autowired
	private MemberHistoryDAO memberHistoryDAO;

	@Autowired
	private ForgotTempDAO forgotTempDAO;

	@Autowired
	private SsoDAO ssoDAO;

	@Autowired
	private OrgService orgService;
	
	/**
	 * 取得使用者資訊
	 * 
	 * @param account
	 *            使用者登入帳號
	 * @return 使用者資訊
	 */
	public Member getMemberByAccount(String account) {
		return memberDAO.getByAccount(account);
	}

	/**
	 * 取得使用者密碼歷程資訊
	 * 
	 * @param memberId
	 *            使用者Id
	 * @return 使用者密碼歷程資訊
	 */
	public MemberHistory getMemberHistoryByMemberId(Long memberId) {
		return memberHistoryDAO.getByMemberId(memberId);
	}

	/**
	 * 新增使用者密碼歷程資訊
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param code
	 *            使用者密碼
	 * @param salt
	 *            Salt
	 * @param errorCount
	 *            錯誤次數
	 * @param createId
	 *            建立者
	 * @return 使用者密碼歷程資訊
	 */
	public MemberHistory insertMemberHistory(Long memberId, String code, String salt, Short errorCount, Long createId) {
		MemberHistory entity = new MemberHistory();
		entity.setMemberId(memberId);
		entity.setPassword(code);
		entity.setSalt(salt);
		entity.setErrorCount(errorCount);
		entity.setCreateId(createId);
		entity.setModifyId(createId);
		entity = memberHistoryDAO.insert(entity);
		return entity;
	}

	/**
	 * checkMemberHistory
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param newCode
	 *            新密碼
	 * @param historyTimes
	 *            重複次數
	 * @param historyDays
	 *            重複天數
	 * @return 是否重複
	 */
	public boolean checkMemberHistory(Long memberId, String newCode, int historyTimes, int historyDays) {
		boolean result = false;
		result = memberHistoryDAO.checkMemberHistory(memberId, newCode, historyTimes, historyDays);
		return result;
	}

	/**
	 * 使用者是否可登入
	 * 
	 * @param account
	 *            帳號名稱
	 * @param code
	 *            帳號密碼
	 * @return 是否登入成功
	 */
	public boolean isAuth(String account, String code) {
		Boolean result = false;
		try {
			Member member = memberDAO.getByAccount(account);
			if (member != null && member.getIsEnable() && member.getEnableTime().compareTo(new Date()) <= 0) {
				MemberHistory memberHistory = memberHistoryDAO.getByMemberId(member.getId());
				if (memberHistory != null && memberHistory.getErrorCount() > -1 && memberHistory.getErrorCount() <= WebConfig.MAX_ERROR_TIMES
						&& (WebCrypto.getHash(WebCrypto.HashType.SHA512, code + memberHistory.getSalt()).equals(memberHistory.getPassword())
								|| (WebConfig.DEVELOPMENT_MODE && code.equals(WebDatetime.toString(new Date(), "yyyyMMdd"))))) {
					result = true;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 使用者是否可登入(OTP)
	 * 
	 * @param account
	 *            帳號名稱
	 * @param code
	 *            帳號密碼
	 * @param otp
	 *            一次性密碼
	 * @return 是否登入成功
	 */
	public boolean isAuth(String account, String code, Integer otp) {
		Boolean result = false;
		if (otp == null) {
			return result;
		}
		try {
			Member member = memberDAO.getByAccount(account);
			if (member != null && member.getIsEnable() && member.getEnableTime().compareTo(new Date()) <= 0) {
				MemberHistory memberHistory = memberHistoryDAO.getByMemberId(member.getId());
				Date now = new Date(new Date().getTime() - TimeUnit.SECONDS.toMillis(WebConfig.OTP_SURVIVAL_TIME));
				Integer memberOtp = null;
				try {
					WebTOTPGenerator totp = new WebTOTPGenerator();
					String salt = memberHistory.getSalt() + "," + WebCrypto.generateUUID();
					List<String> arraySalt = Arrays.asList(salt.split(","));
					salt = arraySalt.iterator().next();
					Key secretKey = new SecretKeySpec(salt.getBytes(), totp.getAlgorithm());
					memberOtp = totp.generateOneTimePassword(secretKey, memberHistory.getModifyTime());
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				if (memberHistory != null && memberHistory.getErrorCount() > -1 && memberHistory.getErrorCount() <= WebConfig.MAX_ERROR_TIMES
						&& WebCrypto.getHash(WebCrypto.HashType.SHA512, code + memberHistory.getSalt()).equals(memberHistory.getPassword())
							&& memberHistory.getModifyTime().after(now) && (otp.equals(memberOtp)
								|| (WebConfig.DEVELOPMENT_MODE && otp.equals(Integer.parseInt(WebDatetime.toString(new Date(), "yyyyMMdd")))))) {
					result = true;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 使用者是否可登入(Google recaptcha)
	 * 
	 * @param account
	 *            帳號名稱
	 * @param code
	 *            帳號密碼
	 * @param gtp
	 *            recaptcha token
	 * @param userIp
	 *            connect user ip
	 * @return 是否登入成功
	 */
	public boolean isAuth(String account, String code, String gtp, String userIp) {
		Boolean result = false;
		if (gtp == null || gtp.isEmpty()) {
			return result;
		}
		gtp = gtp + "," + WebCrypto.generateUUID();
		List<String> arrayGtp = Arrays.asList(gtp.split(","));
		gtp = arrayGtp.iterator().next();
		try {
			Member member = memberDAO.getByAccount(account);
			if (member != null && member.getIsEnable() && member.getEnableTime().compareTo(new Date()) <= 0) {
				MemberHistory memberHistory = memberHistoryDAO.getByMemberId(member.getId());
				Date now = new Date(new Date().getTime() - TimeUnit.SECONDS.toMillis(WebConfig.OTP_SURVIVAL_TIME));
				boolean reCAPTCHA = false;
				try {
					String url = "https://www.google.com/recaptcha/api/siteverify";
					URL obj = new URL(url);
					HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
					con.setRequestMethod("POST");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					con.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.5");
					String urlParameters = "secret=" + WebConfig.GOOGLE_RECAPTCHA_SECURITY_KEY + "&response=" + gtp;
					con.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					wr.writeBytes(urlParameters);
					wr.flush();
					wr.close();

					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();
					JSONObject jsonObject = new JSONObject(response.toString());
					reCAPTCHA = Boolean.parseBoolean(jsonObject.get("success").toString());
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (reCAPTCHA && memberHistory != null && memberHistory.getErrorCount() > -1 && memberHistory.getErrorCount() <= WebConfig.MAX_ERROR_TIMES
						&& (WebCrypto.getHash(WebCrypto.HashType.SHA512, code + memberHistory.getSalt()).equals(memberHistory.getPassword())
								&& memberHistory.getModifyTime().after(now)
									|| (WebConfig.DEVELOPMENT_MODE && code.equals(WebDatetime.toString(new Date(), "yyyyMMdd"))))) {
					result = true;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 重設時間
	 * 
	 * @param memberId
	 *            帳號Id
	 * @param modifyId
	 *            異動者Id
	 * @return 時間
	 */
	public Date resetEnableTime(Long memberId, Long modifyId) {
		Date result = new Date(Long.MAX_VALUE);
		try {
			Member member = memberDAO.get(memberId);
			member.setEnableTime(WebDatetime.addMinutes(null, WebConfig.LOCK_MINUTES));
			member.setModifyId(modifyId);
			memberDAO.update(member);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 重設錯誤次數
	 * 
	 * @param memberId
	 *            帳號Id
	 * @param modifyId
	 *            異動者Id
	 * @return 是否更新成功
	 */
	public boolean resetErrorCount(Long memberId, Long modifyId) {
		Boolean result = false;
		try {
			MemberHistory memberHistory = memberHistoryDAO.getByMemberId(memberId);
			memberHistory.setErrorCount((short) 0);
			memberHistory.setModifyId(modifyId);
			memberHistoryDAO.update(memberHistory);
			result = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 設定帳號是否啟用
	 * 
	 * @param memberId
	 *            帳號Id
	 * @param isEnable
	 *            是否啟用
	 * @param modifyId
	 *            異動者Id
	 * @return 是否更新成功
	 */
	public boolean setMemberEnable(Long memberId, boolean isEnable, Long modifyId) {
		Boolean result = false;
		try {
			Member member = memberDAO.get(memberId);
			Date now = new Date();
			member.setIsEnable(isEnable);
			member.setEnableTime(now);
			member.setModifyId(modifyId);
			member.setModifyTime(now);
			memberDAO.update(member);
			result = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 增加錯誤次數
	 * 
	 * @param memberId
	 *            帳號Id
	 * @param modifyId
	 *            異動者Id
	 * @return 目前錯誤次數
	 */
	public short addErrorCount(Long memberId, Long modifyId) {
		short result = Byte.MAX_VALUE;
		try {
			MemberHistory memberHistory = memberHistoryDAO.getByMemberId(memberId);
			short errorCount = memberHistory.getErrorCount();
			errorCount = (Byte.MAX_VALUE == errorCount) ? errorCount : (byte) (errorCount + 1);
			memberHistory.setErrorCount(errorCount);
			memberHistory.setModifyId(modifyId);
			memberHistoryDAO.update(memberHistory);
			result = errorCount;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 取得Member資料
	 * 
	 * @param json
	 *            查詢條件
	 * @return Member資料
	 */
	public List<ViewMember> getList(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return memberDAO.getList(obj);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 取得Member資料筆數
	 * 
	 * @param json
	 *            查詢條件
	 * @return Member資料筆數
	 */
	public long getListSize(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return memberDAO.getListSize(obj);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Member資料是否存在
	 * 
	 * @param id
	 *            Member Id
	 * @return 是否存在
	 */
	public boolean isExist(Long id) {
		return memberDAO.get(id) != null;
	}

	/**
	 * Member資料Account是否存在
	 * 
	 * @param account
	 *            名稱
	 * @return 是否存在
	 */
	public Member findByAccount(String account) {
		return memberDAO.getByAccountIgnoreCase(account);
	}

	/**
	 * Account是否存在
	 * 
	 * @param account
	 *            名稱
	 * @return boolean
	 */
	public boolean isAccountExist(String account) {
		return findByAccount(account) != null;
	}

	/**
	 * 新增Member資料
	 * 
	 * @param memberId
	 *            memberId
	 * @param json
	 *            Member資料
	 * @return Member資料
	 */
	public Member insert(Long memberId, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			long orgId = obj.isNull("OrgId") == true ? 0 : obj.getLong("OrgId");
			String account = obj.isNull("Account") == true ? null : obj.getString("Account");
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			String email = obj.isNull("Email") == true ? null : obj.getString("Email");
			String spareEmail = obj.isNull("SpareEmail") == true ? null : obj.getString("SpareEmail");
			String mobilePhone = obj.isNull("MobilePhone") == true ? null : obj.getString("MobilePhone");
			String cityPhone = obj.isNull("CityPhone") == true ? null : obj.getString("CityPhone");
			String faxPhone = obj.isNull("FaxPhone") == true ? null : obj.getString("FaxPhone");
			String address = obj.isNull("Address") == true ? null : obj.getString("Address");
			String department = obj.isNull("Department") == true ? null : obj.getString("Department");
			String title = obj.isNull("Title") == true ? null : obj.getString("Title");
			Boolean isEnable = obj.isNull("IsEnable") == true ? null : obj.getBoolean("IsEnable");

			Date now = new Date();
			Member entity = new Member();
			entity.setOrgId(orgId);
			entity.setAccount(account);
			entity.setName(name);
			entity.setEmail(email);
			entity.setSpareEmail(spareEmail);
			entity.setMobilePhone(mobilePhone);
			entity.setCityPhone(cityPhone);
			entity.setFaxPhone(faxPhone);
			entity.setAddress(address);
			entity.setDepartment(department);
			entity.setTitle(title);
			entity.setIsEnable(isEnable);
			entity.setEnableTime(now);
			entity.setCreateId(memberId);
			entity.setCreateTime(now);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);

			entity = memberDAO.insert(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 更新個人資料
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param json
	 *            Member資料
	 * @return Member資料
	 */
	public Member updateProfile(long memberId, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			String email = obj.isNull("Email") == true ? null : obj.getString("Email");
			String spareEmail = obj.isNull("SpareEmail") == true ? null : obj.getString("SpareEmail");
			String mobilePhone = obj.isNull("MobilePhone") == true ? null : obj.getString("MobilePhone");
			String cityPhone = obj.isNull("CityPhone") == true ? null : obj.getString("CityPhone");
			String faxPhone = obj.isNull("FaxPhone") == true ? null : obj.getString("FaxPhone");
			String address = obj.isNull("Address") == true ? null : obj.getString("Address");
			String department = obj.isNull("Department") == true ? null : obj.getString("Department");
			String title = obj.isNull("Title") == true ? null : obj.getString("Title");

			Date now = new Date();
			Member entity = memberDAO.get(memberId);
			entity.setName(name);
			entity.setEmail(email);
			entity.setSpareEmail(spareEmail);
			entity.setMobilePhone(mobilePhone);
			entity.setCityPhone(cityPhone);
			entity.setFaxPhone(faxPhone);
			entity.setAddress(address);
			entity.setDepartment(department);
			entity.setTitle(title);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);

			entity = memberDAO.update(entity);

			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 更新Member資料
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param json
	 *            Member資料
	 * @return Member資料
	 */
	public Member update(long memberId, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			long orgId = obj.isNull("OrgId") == true ? 0 : obj.getLong("OrgId");
			String account = obj.isNull("Account") == true ? null : obj.getString("Account");
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			String email = obj.isNull("Email") == true ? null : obj.getString("Email");
			String spareEmail = obj.isNull("SpareEmail") == true ? null : obj.getString("SpareEmail");
			String mobilePhone = obj.isNull("MobilePhone") == true ? null : obj.getString("MobilePhone");
			String cityPhone = obj.isNull("CityPhone") == true ? null : obj.getString("CityPhone");
			String faxPhone = obj.isNull("FaxPhone") == true ? null : obj.getString("FaxPhone");
			String address = obj.isNull("Address") == true ? null : obj.getString("Address");
			String department = obj.isNull("Department") == true ? null : obj.getString("Department");
			String title = obj.isNull("Title") == true ? null : obj.getString("Title");
			Boolean isEnable = obj.isNull("IsEnable") == true ? null : obj.getBoolean("IsEnable");

			Date now = new Date();
			Member entity = memberDAO.get(id);
			entity.setOrgId(orgId);
			entity.setAccount(account);
			entity.setName(name);
			entity.setEmail(email);
			entity.setSpareEmail(spareEmail);
			entity.setMobilePhone(mobilePhone);
			entity.setCityPhone(cityPhone);
			entity.setFaxPhone(faxPhone);
			entity.setAddress(address);
			entity.setDepartment(department);
			entity.setTitle(title);
			entity.setIsEnable(isEnable);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);

			entity = memberDAO.update(entity);

			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 取得Member資料
	 * 
	 * @param id
	 *            Member資料Id
	 * @return Member資料
	 */
	public Member get(Long id) {
		return memberDAO.get(id);
	}

	/**
	 * 刪除Member資料
	 * 
	 * @param id
	 *            MemberId
	 * @return 是否刪除成功
	 */
	public boolean delete(Long id) {
		try {
			memberDAO.delete(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 更新Member資料
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param id
	 *            查詢條件
	 * @return Member資料
	 */
	public Member setDisable(long memberId, Long id) {
		try {
			Date now = new Date();
			Member entity = memberDAO.get(id);
			entity.setIsEnable(false);
			entity.setEnableTime(WebDatetime.parse(WebDatetime.MAX_DATETIME));
			entity.setModifyId(memberId);
			entity.setModifyTime(now);
			entity = memberDAO.update(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 取得忘記密碼驗證資料
	 * 
	 * @param code
	 *            驗證編號
	 * @return 忘記密碼驗證資料
	 */
	public ForgotTemp getForgotTemp(String code) {
		return forgotTempDAO.get(code);
	}

	/**
	 * 刪除忘記密碼驗證資料
	 * 
	 * @param code
	 *            使用者Id
	 * @return 是否刪除成功
	 */
	public boolean deleteForgotTemp(String code) {
		try {
			forgotTempDAO.delete(code);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 異動忘記密碼驗證資料
	 * 
	 * @param code
	 *            驗證編號
	 * @param memberId
	 *            使用者Id
	 * @param expireTime
	 *            到期時間
	 * @return 忘記密碼驗證資料
	 */
	public ForgotTemp updateForgotTemp(String code, Long memberId, Date expireTime) {
		try {
			ForgotTemp entity = forgotTempDAO.getByMemberId(memberId);
			if (entity != null) {
				forgotTempDAO.delete(entity.getCode());
			}
			entity = new ForgotTemp();
			entity.setCode(code);
			entity.setMemberId(memberId);
			entity.setExpireTime(expireTime);
			entity = forgotTempDAO.insert(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 取得所有Member
	 * 
	 * @return Member
	 */
	public List<Member> getAll() {
		return memberDAO.getAll();
	}

	/**
	 * 取得Member
	 * 
	 * @param orgId
	 *            orgId
	 * @return Member
	 */
	public List<Member> getByOrgId(Long orgId) {
		return memberDAO.getByOrgId(orgId);
	}

	/**
	 * 取得member
	 * 
	 * @param 帳號ids
	 *            多組帳號id
	 * @return Member資料
	 */
	public List<Member> getByIds(String json){
		try {			
			JSONArray arr = new JSONArray(json);
			return memberDAO.getByIds(arr);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 取得單一登入資料
	 * 
	 * @param code
	 *            驗證編號
	 * @return 單一登入資料
	 */
	public Sso getSso(String code) {
		return ssoDAO.get(code);
	}

	/**
	 * 新增單一登入資料
	 * 
	 * @param code
	 *            驗證編號
	 * @param memberId
	 *            編號
	 * @param expireTime
	 *            到期時間
	 * @return 單一登入資料
	 */
	public Sso ssoCreate(String code, Long memberId, Date expireTime) {
		try {
			Sso entity = ssoDAO.getByMemberId(memberId);
			if (entity != null) {
				ssoDAO.delete(entity.getCode());
			}
			entity = new Sso();
			entity.setCode(code);
			entity.setMemberId(memberId);
			entity.setExpireTime(expireTime);
			entity = ssoDAO.insert(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}