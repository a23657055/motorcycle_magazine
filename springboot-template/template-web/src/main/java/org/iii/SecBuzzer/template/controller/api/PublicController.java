package org.iii.SecBuzzer.template.controller.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.iii.SecBuzzer.template.controller.BaseController;
import org.iii.SecBuzzer.template.dao.MemberDAO;
import org.iii.SecBuzzer.template.dao.OrgDAO;
import org.iii.SecBuzzer.template.dao.OrgVariable.AuthType;
import org.iii.SecBuzzer.template.dao.OrgVariable.OrgType;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.domain.ForgotTemp;
import org.iii.SecBuzzer.template.domain.Member;
import org.iii.SecBuzzer.template.domain.MemberHistory;
import org.iii.SecBuzzer.template.domain.Org;
import org.iii.SecBuzzer.template.service.MailAttachment;
import org.iii.SecBuzzer.template.service.MailService;
import org.iii.SecBuzzer.template.service.OrgService;
import org.iii.SecBuzzer.template.util.WebConfig;
import org.iii.SecBuzzer.template.util.WebCrypto;
import org.iii.SecBuzzer.template.util.WebDatetime;
import org.iii.SecBuzzer.template.util.WebNet;
import org.iii.SecBuzzer.template.util.WebTOTPGenerator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
/**
 * 公開資訊控制器
 */
@Controller
@RequestMapping(value = "/public/api", produces = "application/json; charset=utf-8")
public class PublicController extends BaseController {
	final static Logger logger = LoggerFactory.getLogger(PublicController.class);
	
	@Autowired
	private MailService mailService;

	@Autowired
	private OrgService orgService;

	@Autowired
	private MemberDAO memberDAO;

	@Autowired
	private OrgDAO orgDAO;

	/**
	 * 登入驗證
	 * 
	 * @param locale
	 *            Locale
	 * @param authentication
	 *            Authentication
	 * @param session
	 *            HttpSession
	 * @param request
	 *            HttpServletRequest
	 * @param account
	 *            帳號
	 * @param code
	 *            密碼
	 * @param otp
	 *            一次性密碼
	 * @param gtp
	 *            recaptcha token
	 * @return JSON Format String
	 */
	@PostMapping(value = "/login")
	public @ResponseBody String Login(Locale locale, Authentication authentication, HttpSession session, HttpServletRequest request,
			@RequestParam String account, @RequestParam String code, @RequestParam String otp, @RequestParam String gtp) {	

		JSONObject responseJson = new JSONObject();
		Integer otpCode = null;
		Base64.Decoder decoder = Base64.getDecoder();
		try {
			code = new String(decoder.decode(code), StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			code = "";
		}
		try {
			otpCode = Integer.parseInt(otp);
		} catch (Exception e) {
			otpCode = null;
		}
		
		if (!WebConfig.ENABLE_GOOGLE_RECAPTCHA && memberService.isAuth(account, code)) {
			session.invalidate();
			HttpSession newSession = request.getSession();
			if (newSession != null) {
				Member member = memberService.getMemberByAccount(account);
				Org org = orgService.getDataById(member.getOrgId());
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				List<GrantedAuthority> updatedAuthorities = new ArrayList<GrantedAuthority>();
				JSONObject memberInformation = new JSONObject();
				memberInformation.put("Id", member.getId());
				memberInformation.put("Name", member.getName());
				memberInformation.put("Ip", baseIpAddress);
				memberInformation.put("OrgType", org.getOrgType());
				
//				memberInformation.put("AuthType", org.getAuthType());
//				memberInformation.put("CILevel", org.getCiLevel() == null ? "0" : org.getCiLevel());
				
				memberInformation.put("OrgId", org.getId());
				updatedAuthorities.add(new SimpleGrantedAuthority(memberInformation.toString()));
				User user = new User(account, WebCrypto.getHash(WebCrypto.HashType.SHA512, code),
						true, true, true, true, updatedAuthorities);
				Authentication newAuth = new UsernamePasswordAuthenticationToken(
						user, auth.getCredentials(), updatedAuthorities);
				SecurityContext securityContext = SecurityContextHolder.getContext();
				securityContext.setAuthentication(newAuth);
				memberService.resetErrorCount(member.getId(), member.getId());
				responseJson.put("success", true);
				responseJson.put("msg", messageSource.getMessage("loginSuccess", null, locale));
				responseJson.put("url", "./pub/");
				systemCounterService.insert(baseIpAddress);
				systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login,
						SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
			} else {
				responseJson.put("success", false);
				responseJson.put("msg", messageSource.getMessage("loginFail", null, locale));
				responseJson.put("url", "./");
				systemLogService.insert(baseControllerName, baseActionName, account + " Session Exist", SystemLogVariable.Action.Login,
						SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			}
		} else if (gtp.isEmpty() && memberService.isAuth(account, code)) {
			Member member = memberService.getMemberByAccount(account);
			memberService.resetErrorCount(member.getId(), member.getId());
			responseJson.put("success", true);
			responseJson.put("msg", messageSource.getMessage("loginSuccess", null, locale));
			responseJson.put("url", "");
		} else if (!gtp.isEmpty() && memberService.isAuth(account, code, gtp, baseIpAddress)) {
			session.invalidate();
			HttpSession newSession = request.getSession();
			if (newSession != null) {
				Member member = memberService.getMemberByAccount(account);
				Org org = orgService.getDataById(member.getOrgId());
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				List<GrantedAuthority> updatedAuthorities = new ArrayList<GrantedAuthority>();
				JSONObject memberInformation = new JSONObject();
				memberInformation.put("Id", member.getId());
				memberInformation.put("Name", member.getName());
				memberInformation.put("Ip", baseIpAddress);
				memberInformation.put("OrgType", org.getOrgType());
				
//				memberInformation.put("AuthType", org.getAuthType());
//				memberInformation.put("CILevel", org.getCiLevel() == null ? "0" : org.getCiLevel());
				
				memberInformation.put("OrgId", org.getId());
				updatedAuthorities.add(new SimpleGrantedAuthority(memberInformation.toString()));
				User user = new User(account, WebCrypto.getHash(WebCrypto.HashType.SHA512, code),
						true, true, true, true, updatedAuthorities);
				Authentication newAuth = new UsernamePasswordAuthenticationToken(
						user, auth.getCredentials(), updatedAuthorities);
				SecurityContext securityContext = SecurityContextHolder.getContext();
				securityContext.setAuthentication(newAuth);
				memberService.resetErrorCount(member.getId(), member.getId());
				responseJson.put("success", true);
				responseJson.put("msg", messageSource.getMessage("loginSuccess", null, locale));
				responseJson.put("url", "./pub/");
				systemCounterService.insert(baseIpAddress);
				systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login,
						SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
			} else {
				responseJson.put("success", false);
				responseJson.put("msg", messageSource.getMessage("loginFail", null, locale));
				responseJson.put("url", "./");
				systemLogService.insert(baseControllerName, baseActionName, account + " Session Exist", SystemLogVariable.Action.Login,
						SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			}
		} else if (otp.isEmpty() && memberService.isAuth(account, code)) {
			Member member = memberService.getMemberByAccount(account);
			memberService.resetErrorCount(member.getId(), member.getId());
			MemberHistory memberHistory = memberService.getMemberHistoryByMemberId(member.getId());
			Integer newOtp = null;
			try {
				WebTOTPGenerator totp = new WebTOTPGenerator();
				String salt = memberHistory.getSalt() + "," + WebCrypto.generateUUID();
				List<String> arraySalt = Arrays.asList(salt.split(","));
				salt = arraySalt.iterator().next();
				Key secretKey = new SecretKeySpec(salt.getBytes(), totp.getAlgorithm());
				newOtp = totp.generateOneTimePassword(secretKey, memberHistory.getModifyTime());
				String mailSubject = resourceMessageService.get(locale, "mailOTPSubject");
				String mailBody = MessageFormat.format(
						resourceMessageService.get(locale, "mailOTPBody"), newOtp.toString());
				mailService.Send(this.getClass().getSimpleName() + " - " + 
						Thread.currentThread().getStackTrace()[1].getMethodName(),
						member.getEmail(), member.getSpareEmail(), null, mailSubject, mailBody, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			responseJson.put("success", true);
			responseJson.put("msg", messageSource.getMessage("loginSuccess", null, locale));
			responseJson.put("url", "");
		} else if (!otp.isEmpty() && memberService.isAuth(account, code, otpCode)) {
			session.invalidate();
			HttpSession newSession = request.getSession();
			if (newSession != null) {
				Member member = memberService.getMemberByAccount(account);
				Org org = orgService.getDataById(member.getOrgId());
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				List<GrantedAuthority> updatedAuthorities = new ArrayList<GrantedAuthority>();
				JSONObject memberInformation = new JSONObject();
				memberInformation.put("Id", member.getId());
				memberInformation.put("Name", member.getName());
				memberInformation.put("Ip", baseIpAddress);
				memberInformation.put("OrgType", org.getOrgType());
				
//				memberInformation.put("AuthType", org.getAuthType());
//				memberInformation.put("CILevel", org.getCiLevel() == null ? "0" : org.getCiLevel());
				
				memberInformation.put("OrgId", org.getId());
				updatedAuthorities.add(new SimpleGrantedAuthority(memberInformation.toString()));
				User user = new User(account, WebCrypto.getHash(WebCrypto.HashType.SHA512, code),
						true, true, true, true, updatedAuthorities);
				Authentication newAuth = new UsernamePasswordAuthenticationToken(
						user, auth.getCredentials(), updatedAuthorities);
				SecurityContext securityContext = SecurityContextHolder.getContext();
				securityContext.setAuthentication(newAuth);
				memberService.resetErrorCount(member.getId(), member.getId());
				responseJson.put("success", true);
				responseJson.put("msg", messageSource.getMessage("loginSuccess", null, locale));
				responseJson.put("url", "./pub/");
				systemCounterService.insert(baseIpAddress);
				systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login,
						SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
			} else {
				responseJson.put("success", false);
				responseJson.put("msg", messageSource.getMessage("loginFail", null, locale));
				responseJson.put("url", "./");
				systemLogService.insert(baseControllerName, baseActionName, account + " Session Exist", SystemLogVariable.Action.Login,
						SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			}
		} else {
			Member member = memberService.getMemberByAccount(account);
			if (member == null) {
				// 帳號不存在
				responseJson.put("success", false);
				responseJson.put("url", "./");
				responseJson.put("msg", messageSource.getMessage("loginFailAccountOrCode", null, locale));
				systemLogService.insert(baseControllerName, baseActionName, account + " is not exist",
						SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				MemberHistory memberHistory = memberService.getMemberHistoryByMemberId(member.getId());
				if (memberHistory == null) {
					// 審核中
					responseJson.put("success", false);
					responseJson.put("url", "");
					responseJson.put("msg", messageSource.getMessage("loginFailAccountOrCode", null, locale));
					systemLogService.insert(baseControllerName, baseActionName, account + " is wait to apply",
							SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);					
				} else {
					if (memberHistory.getErrorCount() == -1) {
						// 已審核 等待啟用 設定密碼
						responseJson.put("success", false);
						responseJson.put("url", "");
						responseJson.put("msg", messageSource.getMessage("loginFailAccountOrCode", null, locale));
						systemLogService.insert(baseControllerName, baseActionName,
								account + " is wait to change password", SystemLogVariable.Action.Login,
								SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
					} else if (member.getIsEnable() == false) {
						// 已停用
						responseJson.put("success", false);
						responseJson.put("url", "");
						responseJson.put("msg", messageSource.getMessage("loginFailAccountOrCode", null, locale));
						systemLogService.insert(baseControllerName, baseActionName, account + " was disabled to login",
								SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);						
					} else if (member.getEnableTime().compareTo(new Date()) > 0) {
						// 密碼超過錯誤次數後無法登入的時間
						responseJson.put("success", false);
						responseJson.put("url", "");
						Object[] messageArgs = new Object[] {WebConfig.LOCK_MINUTES};
						responseJson.put("msg", messageSource.getMessage("loginFailOverMaxErrorTimes", null, locale) + messageSource.getMessage("loginTryAgainAfterMaxErrorTimes", messageArgs, locale));
						systemLogService.insert(baseControllerName, baseActionName, account + " was locked to login",
								SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
					} else {
						// 帳號密碼錯誤
						if (memberHistory.getErrorCount() < WebConfig.MAX_ERROR_TIMES) {
							memberService.addErrorCount(member.getId(), member.getId());
							responseJson.put("success", false);
							responseJson.put("url", "");
							responseJson.put("msg", messageSource.getMessage("loginFailAccountOrCode", null, locale));
							systemLogService.insert(baseControllerName, baseActionName, account + " password error",
									SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
						} else {
							memberService.resetEnableTime(member.getId(), member.getId());
							responseJson.put("success", false);
							responseJson.put("url", "");
							responseJson.put("msg", messageSource.getMessage("loginFailAccountOrCode", null, locale));
							systemLogService.insert(baseControllerName, baseActionName, account + " locking to login",
									SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
						}
					}
				}
			}
		}
		return responseJson.toString();
	}
	
	/**
	 * 發驗證信
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param account
	 *            帳號
	 * @param email
	 *            註冊時的電子郵件信箱
	 * @return JSON Format String
	 */
	@PostMapping(value = "/resend")
	public @ResponseBody String Resend(Locale locale, HttpServletRequest request, @RequestParam String account, @RequestParam String email) {
		JSONObject responseJson = new JSONObject();
		Member member = memberService.getMemberByAccount(account);

		if (member == null) {
			// 帳號不存在
			responseJson.put("success", false);
			responseJson.put("msg", messageSource.getMessage("memeberAccountNotExist", null, locale));
			systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
		} else {
			MemberHistory memberHistory = memberService.getMemberHistoryByMemberId(member.getId());
			if (memberHistory == null) {
				// 審核中
				responseJson.put("success", false);
				responseJson.put("msg", messageSource.getMessage("memeberAccountNotExist", null, locale));
				systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				if (member.getIsEnable() == false) {
					// 已停用
					responseJson.put("success", false);
					responseJson.put("msg", messageSource.getMessage("memeberAccountNotExist", null, locale));
					systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
				} else {
					// 帳號與電子郵件符合
					if (member.getEmail().equals(email)) {
						String code = WebCrypto.generateUUID() + WebCrypto.generateUUID() + WebCrypto.generateUUID() + WebCrypto.generateUUID();
						Long memberId = member.getId();
						Date now = new Date();
						Date expireTime = WebDatetime.addMinutes(now, WebConfig.FORGOT_EXPIRE_MINUTES);
						memberService.updateForgotTemp(code, memberId, expireTime);
						if (WebConfig.DEVELOPMENT_MODE) {
							// member.getEmail());
						}
						
						String mailSubject = resourceMessageService.get(locale, "mailResetPasswordSubject");
						String mailBody = MessageFormat.format(resourceMessageService.get(locale, "mailResetPasswordBody"),
								member.getName(), WebDatetime.toString(now), WebNet.getIpAddr(request), WebConfig.WEB_SITE_URL + "reset?" + code, WebConfig.WEB_SITE_URL);
						mailService.Send(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), member.getEmail(), member.getSpareEmail(), null, mailSubject, mailBody, null);
						responseJson.put("success", true);
						responseJson.put("msg", messageSource.getMessage("loginResendSuccess", null, locale));
						systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);

					} else {
						responseJson.put("success", false);
						responseJson.put("msg", messageSource.getMessage("memeberAccountNotExist", null, locale));
						systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
					}
				}
			}
		}
		return responseJson.toString();
	}
	
	/**
	 * 取得密碼長度API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model	
	 * @return 密碼長度
	 */
	@PostMapping(value = "/getPasswordLength")
	public @ResponseBody String GetPasswordLength(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray arrjson = new JSONArray();
		arrjson.put(resourceMessageService.get(locale, "passwordLength"));
		return arrjson.toString();
	}

	/**
	 * 重設密碼
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param account
	 *            帳號
	 * @param id
	 *            驗證碼
	 * @param code
	 *            密碼
	 * @return JSON Format String
	 */
	@PostMapping(value = "/reset")
	public @ResponseBody String Reset(Locale locale, HttpServletRequest request, @RequestParam String account, @RequestParam String id, @RequestParam String code) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		Member member = memberService.getMemberByAccount(account);
		if (member == null) {
			// 帳號不存在
			responseJson.put("success", false);
			responseJson.put("msg", messageSource.getMessage("memberResetCodeFail", null, locale));
			systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);

		} else {
			if (member.getIsEnable() == false) {
				// 已停用
				responseJson.put("success", false);
				responseJson.put("msg", messageSource.getMessage("memberResetCodeFail", null, locale));
				systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);

			} else {
				// 帳號與驗證碼符合
				ForgotTemp forgotTemp = memberService.getForgotTemp(id);
				if (forgotTemp == null) {
					responseJson.put("success", false);
					responseJson.put("msg", messageSource.getMessage("memberResetCodeFail", null, locale));
					systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);

				} else {
					if (forgotTemp.getMemberId().equals(member.getId()) && forgotTemp.getExpireTime().compareTo(new Date()) > 0) {
						try {
							Base64.Decoder decoder = Base64.getDecoder();
							code = new String(decoder.decode(code), StandardCharsets.UTF_8.toString());
							if (code == null || code.isEmpty() || code.length() < 8 || !Pattern.compile("^(?=.*\\d)(?=.*[a-zA-Z])(?=.*\\W).{8,}$").matcher(code).matches()) {
								// 密碼強度不符合
								responseJson.put("success", false);
								responseJson.put("msg", messageSource.getMessage("memberResetCodeFail", null, locale));
								systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);

							} else {
								boolean isMemberHistoryeEffective = memberService.checkMemberHistory(member.getId(), code, WebConfig.HISTORY_TIMES, WebConfig.HISTORY_DAYS);
								if (isMemberHistoryeEffective) {
									Object[] messageArgs = new Object[2];
									messageArgs[0] = WebConfig.HISTORY_TIMES;
									messageArgs[1] = WebConfig.HISTORY_DAYS;
									responseJson.put("success", false);
									responseJson.put("msg", messageSource.getMessage("memberHistoryeEffective", messageArgs, locale));
									systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);

								} else {
									String salt = WebCrypto.generateUUID();
									String newCode = WebCrypto.getHash(WebCrypto.HashType.SHA512, code + salt);
									memberService.insertMemberHistory(member.getId(), newCode, salt, (short) 0, member.getId());
									memberService.deleteForgotTemp(id);
									responseJson.put("success", true);
									responseJson.put("msg", messageSource.getMessage("memberResetCodeSuccess", null, locale));
									systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Update, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
								}
							}
						} catch (UnsupportedEncodingException e) {
							responseJson.put("success", false);
							responseJson.put("msg", messageSource.getMessage("memberResetCodeFail", null, locale));
							systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
						}
					} else {
						responseJson.put("success", false);
						responseJson.put("msg", messageSource.getMessage("memberResetCodeFail", null, locale));
						systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
					}
				}
			}
		}
		return responseJson.toString();
	}
	
	/**
	 * 重新設定Session Timeout
	 * 
	 * @return JSON Format String
	 */
	@PostMapping(value = "/resetTimeout")
	public @ResponseBody String resetTimeout() {
		JSONObject json = new JSONObject();
		json.put("success", true);
		return json.toString();
	}
	
	/**
	 * 取得隸屬主管機關資料
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            RequestBody
	 * @return JSON Format String
	 */
	@PostMapping(value = "/getAuthorOrgs")
	public @ResponseBody String getAuthorOrgs(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			List<Org> orgs = orgService.getList(true, OrgType.Authority, AuthType.Supervise);
			if (orgs != null) {
				for (Org org : orgs) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("value", org.getId());
					jsonObject.put("name", org.getName());
					jsonArray.put(jsonObject);
				}
			}
			responseJson.put("success", true);
			responseJson.put("msg", messageSource.getMessage("globalReadDataSuccess", null, locale));
			responseJson.put("datatable", jsonArray);
		} catch (Exception e) {
			responseJson.put("success", false);
			responseJson.put("msg", messageSource.getMessage("globalReadDataFail", null, locale));
			responseJson.put("datatable", jsonArray);
		}
		return responseJson.toString();
	}

	/**
	 * 檢查帳號是否存在
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            RequestBody
	 * @return JSON Format String
	 */
	@PostMapping(value = "/checkAccount")
	public @ResponseBody String checkAccount(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		try {
			JSONObject obj = new JSONObject(URLDecoder.decode(json, StandardCharsets.UTF_8.toString()));
			String memberAccount = obj.isNull("memberAccount") ? "" : obj.getString("memberAccount");
			Member member = memberService.getMemberByAccount(memberAccount);
			if (member == null) {
				responseJson.put("success", true);
			} else {
				responseJson.put("success", false);
			}
		} catch (Exception e) {
			responseJson.put("success", false);
		}
		return responseJson.toString();
	}
	
	/**
	 * 新增會員資料
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            RequestBody
	 * @return JSON Format String
	 */
	@PostMapping(value = "/sign_up")
	public @ResponseBody String signUp(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals("")) {
			return responseJson.toString();
		}
		
		try {
			JSONObject obj = new JSONObject(URLDecoder.decode(json, StandardCharsets.UTF_8.toString()));
			String orgCode = obj.isNull("orgCode") ? "" : obj.getString("orgCode");
			String orgName = obj.isNull("orgName") ? "" : obj.getString("orgName");
			String orgCity = obj.isNull("orgCity") ? "" : obj.getString("orgCity");
			String orgTown = obj.isNull("orgTown") ? "" : obj.getString("orgTown");
			String orgAddress = obj.isNull("orgAddress") ? "" : obj.getString("orgAddress");
			Long parentOrgId = obj.isNull("parentOrgId") ? 0 : obj.getLong("parentOrgId");
			Org parentOrg = orgService.getDataById(parentOrgId);
			String parentOrgName = "";
			if (parentOrg != null) {
				parentOrgName = parentOrg.getName();
			}
			String memberAccount = obj.isNull("memberAccount") ? "" : obj.getString("memberAccount");
			String memberName = obj.isNull("memberName") ? "" : obj.getString("memberName");
			String memberEmail = obj.isNull("memberEmail") ? "" : obj.getString("memberEmail");
			String memberMobilePhone = obj.isNull("memberMobilePhone") ? "" : obj.getString("memberMobilePhone");
			Date now = new Date();

			Member existMember = memberService.getMemberByAccount(memberAccount);
			if (existMember == null) {
				Org org = orgService.findByCode(orgCode);
				if (org == null) {
					// Create Org Info
					org = new Org();
					org.setCode(orgCode);
					org.setOrgType(OrgType.Member.getValue());
					org.setAuthType(AuthType.None.getValue());
					org.setName(orgName);
					org.setCity(orgCity);
					org.setTown(orgTown);
					org.setAddress(orgAddress);
					org.setIsEnable(true);
					org.setCreateId((long) 1);
					org.setCreateTime(now);
					org.setModifyId((long) 1);
					org.setModifyTime(now);
					orgDAO.insert(org);
					
				}

				// Create Member Info
				Member member = new Member();
				member.setAccount(memberAccount);
				member.setOrgId(org.getId());
				member.setName(memberName);
				member.setEmail(memberEmail);
				member.setMobilePhone(memberMobilePhone);
				member.setIsEnable(false);
				member.setEnableTime(WebDatetime.parse(WebDatetime.MAX_DATETIME));
				member.setCreateId((long) 1);
				member.setCreateTime(now);
				member.setModifyId((long) 1);
				member.setModifyTime(now);
				memberDAO.insert(member);

				// Create pdf & Send Mail
				List<MailAttachment> attachements = new ArrayList<MailAttachment>();
				MailAttachment attachement = new MailAttachment();
				Document document = new Document(PageSize.A4);

				BaseFont baseFont;
				try {
					baseFont = BaseFont.createFont(WebConfig.PDF_FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				} catch (Exception e) {
					baseFont = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257, BaseFont.EMBEDDED);
				}
				Font fontTitle = new Font(baseFont, 13, Font.NORMAL);
				Font fontContent = new Font(baseFont, 11, Font.NORMAL);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				PdfWriter writer = PdfWriter.getInstance(document, stream);
				document.open();
				PdfContentByte cb = writer.getDirectContent();

				{
					Paragraph p = new Paragraph("", fontContent);
					p.add(new Chunk(new Chunk(new VerticalPositionMark())));
					p.add("機密等級:                           ");
					document.add(p);
				}
				{

					PdfPTable table = new PdfPTable(4);
					table.setWidthPercentage(100);
					table.setWidths(new int[]{1, 1, 1, 1});
					table.addCell(new PdfPCell(new Paragraph("制定單位", fontContent)));
					table.addCell(new PdfPCell(new Paragraph("文件名稱", fontContent)));
					table.addCell(new PdfPCell(new Paragraph("表單編號", fontContent)));
					table.addCell(new PdfPCell(new Paragraph("版次", fontContent)));

					table.addCell(new PdfPCell(new Paragraph("H-ISAC", fontContent)));
					table.addCell(new PdfPCell(new Paragraph(messageSource.getMessage("orgSingUpData", null, Locale.TRADITIONAL_CHINESE), fontContent)));
					table.addCell(new PdfPCell(new Paragraph(" ", fontContent)));
					table.addCell(new PdfPCell(new Paragraph("V 0.0", fontContent)));
					document.add(table);
				}
				{
					Paragraph p = new Paragraph("", fontContent);
					p.add(new Chunk(new Chunk(new VerticalPositionMark())));
					p.add("流水號:                           ");
					document.add(p);
				}

				Paragraph p = new Paragraph();
				p = new Paragraph(messageSource.getMessage("orgSingUpData", null, Locale.TRADITIONAL_CHINESE), fontTitle);
				document.add(p);
				{
					PdfPTable table = new PdfPTable(3);
					table.setWidthPercentage(100);
					table.setWidths(new int[]{1, 1, 3});
					PdfPCell cell;

					cell = new PdfPCell(new Phrase(new Paragraph("醫事機構資訊", fontTitle)));
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell.setColspan(3);
					table.addCell(cell);
					
					cell = new PdfPCell(new Phrase(new Paragraph(messageSource.getMessage("orgType4Code", null, Locale.TRADITIONAL_CHINESE), fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph(orgCode + "                        為醫事系統核准之醫事機構", fontContent)));
					cell.setColspan(2);
					table.addCell(cell);
					
					cell = new PdfPCell(new Phrase(new Paragraph(messageSource.getMessage("orgType4Name", null, Locale.TRADITIONAL_CHINESE), fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph(orgName, fontContent)));
					cell.setColspan(2);
					table.addCell(cell);

					cell = new PdfPCell(new Phrase(new Paragraph("機構地址", fontContent)));
					cell.setRowspan(3);
					table.addCell(cell);

					cell = new PdfPCell(new Phrase(new Paragraph(messageSource.getMessage("orgCity", null, Locale.TRADITIONAL_CHINESE), fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph(orgCity, fontContent)));
					table.addCell(cell);
					
					cell = new PdfPCell(new Phrase(new Paragraph(messageSource.getMessage("orgTown", null, Locale.TRADITIONAL_CHINESE), fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph(orgTown, fontContent)));
					table.addCell(cell);
					
					cell = new PdfPCell(new Phrase(new Paragraph(messageSource.getMessage("orgType4Address", null, Locale.TRADITIONAL_CHINESE), fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph(orgAddress, fontContent)));
					table.addCell(cell);

					cell = new PdfPCell(new Phrase(new Paragraph("衛生主管機關", fontTitle)));
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell.setColspan(3);
					table.addCell(cell);
					
					cell = new PdfPCell(new Phrase(new Paragraph(messageSource.getMessage("orgType3SuperviseName", null, Locale.TRADITIONAL_CHINESE), fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph(parentOrgName, fontContent)));
					cell.setColspan(2);
					table.addCell(cell);
					
					cell = new PdfPCell(new Phrase(new Paragraph(messageSource.getMessage("orgType3LocalName", null, Locale.TRADITIONAL_CHINESE), fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph("(所在地縣、市)衛生局", fontContent)));
					cell.setColspan(2);
					table.addCell(cell);

					cell = new PdfPCell(new Phrase(new Paragraph("系統管理者資訊", fontTitle)));
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell.setColspan(3);
					table.addCell(cell);
					
					cell = new PdfPCell(new Phrase(messageSource.getMessage("memberAdminAccount", null, Locale.TRADITIONAL_CHINESE), fontContent));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph(memberAccount, fontContent)));
					cell.setColspan(2);
					table.addCell(cell);
					
					cell = new PdfPCell(new Phrase(messageSource.getMessage("memberAdminName", null, Locale.TRADITIONAL_CHINESE), fontContent));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph(memberName, fontContent)));
					cell.setColspan(2);
					table.addCell(cell);
					
					cell = new PdfPCell(new Phrase(new Paragraph(messageSource.getMessage("memberAdminEmail", null, Locale.TRADITIONAL_CHINESE), fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph(memberEmail, fontContent)));
					cell.setColspan(2);
					table.addCell(cell);
					
					cell = new PdfPCell(new Phrase(new Paragraph(messageSource.getMessage("memberAdminMobilePhone", null, Locale.TRADITIONAL_CHINESE), fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph(memberMobilePhone, fontContent)));
					cell.setColspan(2);
					table.addCell(cell);

					cell = new PdfPCell(new Phrase(new Paragraph("(本欄由H-ISAC填寫)", fontTitle)));
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell.setColspan(3);
					table.addCell(cell);

					cell = new PdfPCell(new Phrase(new Paragraph("審核人員", fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph("", fontContent)));
					cell.setColspan(2);
					table.addCell(cell);

					cell = new PdfPCell(new Phrase(new Paragraph("審核日期", fontContent)));
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(new Paragraph("", fontContent)));
					cell.setColspan(2);
					table.addCell(cell);

					Phrase pr = new Phrase();
					pr.add(new Paragraph("請於此處加蓋機構關防", fontContent));
					pr.add(new Paragraph("\n\n\n\n\n\n\n\n\n\n", fontContent));
					pr.add(new Paragraph("申請日期:" + WebDatetime.toString(now, "yyyy年MM月dd日"), fontContent));
					cell = new PdfPCell(pr);
					cell.setColspan(3);
					table.addCell(cell);

					document.add(table);
				}

				document.add(new Paragraph("備註: ", fontTitle));
				document.add(new Paragraph("1. 新會員單位於H-ISAC網站申請會員註冊，會員單位於申請頁面完成相關資訊填寫並列印「H-ISAC會員單位申請表」。經機構用印後以掛號寄至PMO專案辦公室統一受理轉發H-ISAC審核。", fontContent));

				Phrase footer = new Phrase("□永久保存  ■定期保存    年", fontContent);
				ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, document.right() - 120 + document.leftMargin(), document.bottom() - 10, 0);

				document.close();
				attachement.setAttachmentName("HisacSignUp.pdf");
				attachement.setAttachmentBody(stream.toByteArray());
				attachements.add(attachement);
				responseJson.put("success", true);
				responseJson.put("msg", messageSource.getMessage("memberSignUpSuccess", null, locale));
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
				String mailSubject = resourceMessageService.get(locale, "mailMemberSignUpSubject");
				String mailBody = MessageFormat.format(resourceMessageService.get(locale, "mailMemberSignUpBody"), memberName);
				mailService.Send(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), myFilter.filterEmail(memberEmail), null, null, mailSubject, mailBody, attachements);
				responseJson.put("success", true);
				responseJson.put("msg", messageSource.getMessage("memberSignUpSuccess", null, locale));
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				responseJson.put("success", false);
				responseJson.put("msg", messageSource.getMessage("memberSignUpFail", null, locale));
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseJson.put("success", false);
			responseJson.put("msg", messageSource.getMessage("memberSignUpFail", null, locale));
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
		}
		return responseJson.toString();
	}

//
//	/**
//	 * ChangeUser
//	 * 
//	 * @param locale
//	 *            Locale
//	 * @param session
//	 *            HttpSession
//	 * @param request
//	 *            HttpServletRequest
//	 * @param response
//	 *            HttpServletResponse
//	 * @param account
//	 *            account
//	 * @return JSON Format String
//	 */
//	@RequestMapping(value = "/change_user", method = RequestMethod.POST)
//	public @ResponseBody String ChangeUser(Locale locale, HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam String account) {
//		JSONObject responseJson = new JSONObject();
//		Authentication nowAuth = SecurityContextHolder.getContext().getAuthentication();
//		if (!(nowAuth instanceof AnonymousAuthenticationToken)) {
//			if (!account.equals("") && memberService.isAccountExist(account) && WebConfig.DEVELOPMENT_MODE) {
//				session.invalidate();
//				HttpSession newSession = request.getSession();
//				if (newSession != null) {
//					Member member = memberService.getMemberByAccount(account);
//					Org org = orgService.getDataById(member.getOrgId());
//					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//					List<GrantedAuthority> updatedAuthorities = new ArrayList<GrantedAuthority>();
//					JSONObject memberInformation = new JSONObject();
//					memberInformation.put("Id", member.getId());
//					memberInformation.put("Name", member.getName());
//					memberInformation.put("Ip", baseIpAddress);
//					memberInformation.put("OrgType", org.getOrgType());
//					memberInformation.put("OrgId", org.getId());
//					updatedAuthorities.add(new SimpleGrantedAuthority(memberInformation.toString()));
//					User user = new User(account, WebCrypto.getHash(WebCrypto.HashType.SHA512, WebCrypto.getRandomCode(12)), true, true, true, true, updatedAuthorities);
//					Authentication newAuth = new UsernamePasswordAuthenticationToken(user, auth.getCredentials(), updatedAuthorities);
//					SecurityContext securityContext = SecurityContextHolder.getContext();
//					securityContext.setAuthentication(newAuth);
//					responseJson.put("success", true);
//					responseJson.put("msg", WebMessage.getMessage("loginSuccess", null, locale));
//					responseJson.put("url", "./pub/");
//					systemCounterService.insert(baseIpAddress);
//					systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
//					return responseJson.toString();
//				} else {
//					responseJson.put("success", false);
//					responseJson.put("msg", WebMessage.getMessage("loginFail", null, locale));
//					responseJson.put("url", "./");
//					systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
//					return responseJson.toString();
//				}
//			} else {
//				responseJson.put("success", false);
//				responseJson.put("msg", WebMessage.getMessage("loginFail", null, locale));
//				responseJson.put("url", "./");
//				systemLogService.insert(baseControllerName, baseActionName, account, SystemLogVariable.Action.Login, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
//				return responseJson.toString();
//			}
//		} else {
//			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//		}
//		return responseJson.toString();
//	}
}
