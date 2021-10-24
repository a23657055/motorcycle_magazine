package org.iii.SecBuzzer.template.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.iii.SecBuzzer.template.dao.MemberRoleVariable;
import org.iii.SecBuzzer.template.dao.SystemLogVariable.Action;
import org.iii.SecBuzzer.template.dao.SystemLogVariable.Status;
import org.iii.SecBuzzer.template.domain.MemberHistory;
import org.iii.SecBuzzer.template.domain.MemberRole;
import org.iii.SecBuzzer.template.domain.ViewMenuLimit;
import org.iii.SecBuzzer.template.service.MemberRoleService;
import org.iii.SecBuzzer.template.service.MemberService;
import org.iii.SecBuzzer.template.service.MenuService;
import org.iii.SecBuzzer.template.service.ResourceMessageService;
import org.iii.SecBuzzer.template.service.SystemCounterService;
import org.iii.SecBuzzer.template.service.SystemLogService;
import org.iii.SecBuzzer.template.util.MyFilter;
import org.iii.SecBuzzer.template.util.WebConfig;
import org.iii.SecBuzzer.template.util.WebCrypto;
import org.iii.SecBuzzer.template.util.WebMessage;
import org.iii.SecBuzzer.template.util.WebNet;
import org.iii.SecBuzzer.template.web.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {
	final static Logger logger = LoggerFactory.getLogger(BaseController.class);

	protected String baseMemberAccount = "";
	protected Long baseMemberId = null;
	protected Long baseOrgId = null;
	protected String baseIpAddress = "";
	protected MemberRoleVariable baseMemberRole;
	protected String baseControllerName = "";
	protected String baseActionName = "";
	
//	protected String baseMemberName = "";
//	protected String baseOrgType = "";
//	protected List<MemberRole> memberRoles;
//	protected String baseLanguageCountry = "";
//	protected String baseIp = "";
	
//	protected Boolean baseActionCreate = false;
//	protected Boolean baseActionUpdate = false;
//	protected Boolean baseActionDelete = false;
//	protected Boolean baseActionRead = false;
//	protected Boolean baseActionSign = false;
	
//	protected String baseAppName = "";
//	protected String baseSubsystemName = "";	
	
	@Autowired
	protected SystemLogService systemLogService;

	@Autowired
	protected MemberService memberService;

	@Autowired
	protected SystemCounterService systemCounterService;

	@Autowired
	protected ResourceMessageService resourceMessageService;

	@Autowired
	protected MenuService menuService;

	@Autowired
	protected MemberRoleService memberRoleService;

	@Autowired
	protected MessageSource messageSource;
	
	protected final MyFilter myFilter = new MyFilter();
	protected final Base64.Encoder encoder = Base64.getEncoder();
	protected final Base64.Decoder decoder = Base64.getDecoder();

	/**
	 * The Global Attributes for View
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param model
	 *            Model
	 */
	@ModelAttribute
	public void addAttributes(Locale locale, HttpServletRequest request, HttpServletResponse response, Model model) {
		HttpSession session = request.getSession();
		baseControllerName = (String) session.getAttribute("controllerName");
		baseActionName = (String) session.getAttribute("actionName");
		String baseLanguageCountry = WebMessage.getLanguageCountry(request.getLocale());

		String packageName = this.getClass().getPackage().getName();
		if (packageName.equals("org.iii.SecBuzzer.template.controller")) {
			model.addAttribute("enableGoogleRecaptcha", WebConfig.ENABLE_GOOGLE_RECAPTCHA);
			model.addAttribute("googleRecaptchaSiteKey", WebConfig.GOOGLE_RECAPTCHA_SITE_KEY);
			model.addAttribute("nowUser", SessionManager.getUserCount());
			model.addAttribute("totalUser", systemCounterService.getTotal());
			model.addAttribute("globalFooterDirectedBy", WebMessage.parseMessage(resourceMessageService.get(locale, "globalFooterDirectedBy"), locale));
			model.addAttribute("globalFooterAddressText", WebMessage.parseMessage(resourceMessageService.get(locale, "globalFooterAddressText"), locale));
			model.addAttribute("globalFooterAddressValue", WebMessage.parseMessage(resourceMessageService.get(locale, "globalFooterAddressValue"), locale));
			model.addAttribute("globalFooterTelText", WebMessage.parseMessage(resourceMessageService.get(locale, "globalFooterTelText"), locale));
			model.addAttribute("globalFooterTelValue", WebMessage.parseMessage(resourceMessageService.get(locale, "globalFooterTelValue"), locale));
			model.addAttribute("globalFooterEmail", WebMessage.parseMessage(resourceMessageService.get(locale, "globalFooterEmail"), locale));
			model.addAttribute("developmentMode", WebConfig.DEVELOPMENT_MODE);
			model.addAttribute("debugMode", WebConfig.DEBUG_MODE);
			model.addAttribute("languageCountry", baseLanguageCountry);
		}

		if (baseControllerName!=null && baseControllerName.equals("Index")) {
			return;
		}
		model.addAttribute("controllerName", baseControllerName);
		model.addAttribute("actionName", baseActionName);

		String baseMemberName = "";
		String baseOrgType = "";
		List<MemberRole> memberRoles = null;
		baseIpAddress = WebNet.getIpAddr(request);		
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!(auth instanceof AnonymousAuthenticationToken)) {
				UserDetails userDetail = (UserDetails) auth.getPrincipal();
				Object[] authorities = userDetail.getAuthorities().toArray();
				JSONObject memberInformation = new JSONObject(authorities[0].toString());
				
				baseMemberAccount = userDetail.getUsername();
				baseMemberName = memberInformation.getString("Name");
				baseMemberId = memberInformation.getLong("Id");
				baseOrgType = memberInformation.getString("OrgType");
				baseOrgId = memberInformation.getLong("OrgId");
				String baseIp = memberInformation.getString("Ip");
				if (!(baseIp.trim().equals(baseIpAddress.trim()))) {
					try {
						Cookie cookie = new Cookie("JSESSIONID", "");
						cookie.setPath(request.getContextPath());
						cookie.setMaxAge(-1);
						response.addCookie(cookie);
						response.sendRedirect(request.getContextPath() + "/?g=" + WebCrypto.getRandomCode(8) + WebCrypto.generateUUID());
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				baseMemberRole = memberRoleService.getMemberRoleVariable(baseMemberId);
				memberRoles = memberRoleService.getAllByMemberId(baseMemberId);
			}
			
			model.addAttribute("memberAccount", baseMemberAccount);
			model.addAttribute("memberName", baseMemberName);
			model.addAttribute("memberId", baseMemberId);
			model.addAttribute("orgType", baseOrgType);
			model.addAttribute("orgId", baseOrgId);
			model.addAttribute("beforeSessionTimeoutMinutes", WebConfig.BEFORE_SESSION_TIMEOUT_MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e.getMessage());
		}
		
		Boolean baseActionCreate = false;
		Boolean baseActionUpdate = false;
		Boolean baseActionDelete = false;
		Boolean baseActionRead = false;
		Boolean baseActionSign = false;
		
		if (packageName.equals("org.iii.SecBuzzer.template.controller") && baseControllerName != null && baseActionName != null) {
			// For Controller Router
			ViewMenuLimit action = menuService.getAction(baseMemberId, baseControllerName, baseActionName);
			if (action != null) {
				baseActionCreate = action.getActionCreate();
				baseActionUpdate = action.getActionUpdate();
				baseActionDelete = action.getActionDelete();
				baseActionRead = action.getActionRead();
				baseActionSign = action.getActionSign();
			}
			if (!baseActionRead) {
				try {
					response.sendRedirect(request.getContextPath() + "/forbidden?g=" + WebCrypto.getRandomCode(8) + WebCrypto.generateUUID());
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			systemLogService.insert(baseControllerName, baseActionName, "", Action.PageLoad, Status.Success, baseIpAddress, baseMemberAccount);
			model.addAttribute("actionCreate", baseActionCreate);
			model.addAttribute("actionUpdate", baseActionUpdate);
			model.addAttribute("actionDelete", baseActionDelete);
			model.addAttribute("actionRead", baseActionRead);
			model.addAttribute("actionSign", baseActionSign);

			model.addAttribute("memberRoles", memberRoles);	
			/*
			 * 1-SuperAdmin
			 * 7-權責單位警訊審核者
			 * 8-權責單位聯絡人
			 * 9-權責單位管理者
			 * 10-會員單位聯絡人
			 * 11-會員單位管理者
			 * 15-權責通報審核者
			 */
			model.addAttribute("isAdmin", baseMemberRole.IsAdmin);					//1-SuperAdmin
//			model.addAttribute("isApplySign", baseMemberRole.IsApplySign);			//7-權責單位警訊審核者
//			model.addAttribute("isApplyContact", baseMemberRole.IsApplyContact);	//8-權責單位聯絡人
//			model.addAttribute("isApplyAdmin", baseMemberRole.IsApplyAdmin);		//9-權責單位管理者
//			model.addAttribute("isMemberContact", baseMemberRole.IsMemberContact);	//10-會員單位連絡人
//			model.addAttribute("isMemberAdmin", baseMemberRole.IsMemberAdmin);		//11-會員單位管理者
//			model.addAttribute("isApplySingAdmin", baseMemberRole.IsApplySingAdmin); //15-權責通報審核者

			String baseAppName = menuService.getFormName(baseControllerName, baseActionName);
			String baseSubsystemName = menuService.getSubsystemName(baseControllerName);
			model.addAttribute("appName", WebMessage.parseMessage(baseAppName, locale));
			model.addAttribute("subsystemName", WebMessage.parseMessage(baseSubsystemName, locale));
			
			//menuJson begin
			if (session.getAttribute("menuJson") == null) {
				List<ViewMenuLimit> viewMenuLimits = menuService.getMenu(baseMemberId);
				List<ViewMenuLimit> subsystems = viewMenuLimits.stream().filter(distinctByKey(p -> p.getSubsytemId())).collect(Collectors.toList());
				JSONObject menuJson = new JSONObject();
				JSONArray arrMenuJson = new JSONArray();
				if (subsystems != null) {
					for (ViewMenuLimit subsystem : subsystems) {
						List<ViewMenuLimit> separator = viewMenuLimits.stream().filter(p -> p.getSubsytemId() == subsystem.getSubsytemId() &&!p.getFormCode().equals("separator") && !p.getFormName().equals("separator")).collect(Collectors.toList());
						if (separator.size() == 0) {
							continue;
						}
						JSONObject menuJsonParent = new JSONObject();
						menuJsonParent.put("id", subsystem.getSubsytemId());
						menuJsonParent.put("iconStyle", subsystem.getIconStyle());
						menuJsonParent.put("link", WebMessage.parseMessage(subsystem.getSubsystemName(), locale));
						menuJsonParent.put("code", "subsystem_" + subsystem.getSubsystemCode());
						List<ViewMenuLimit> forms = viewMenuLimits.stream().filter(p -> p.getSubsytemId().equals(subsystem.getSubsytemId())).collect(Collectors.toList());
						if (forms != null) {
							int formSize = forms.size();
							boolean separatorCheck = false;
							int menuIndex = 0;
							for (ViewMenuLimit form : forms) {
								menuIndex++;
								if (formSize == menuIndex && form.getFormCode().equals("separator") && form.getFormName().equals("separator")) {
									continue;
								}
								if (separatorCheck == false && form.getFormCode().equals("separator") && form.getFormName().equals("separator")) {
									continue;
								} else {
									JSONObject menuJsonChildren = new JSONObject();
									if (form.getFormCode().equals("separator") && form.getFormName().equals("separator")) {
										separatorCheck = false;
										menuJsonChildren.put("id", form.getFormId());
										menuJsonChildren.put("value", "separator");
										menuJsonChildren.put("separator", true);
									} else {
										separatorCheck = true;
										menuJsonChildren.put("id", form.getFormId());
										if (form.getControllerName().isEmpty()) {
											menuJsonChildren.put("value", "/" + form.getActionName().toLowerCase());
										} else {
											menuJsonChildren.put("value", "/" + form.getControllerName().toLowerCase() + "/" + form.getActionName().toLowerCase());
										}
										if (form.getIsExternalLink()) {
											menuJsonChildren.put("target", form.getFormCode());
										}
										if (WebConfig.DEVELOPMENT_MODE) {
											menuJsonChildren.put("link", form.getActionName().toLowerCase() + "_" + WebMessage.parseMessage(form.getFormName(), locale) + "_" + form.getFormCode());
										} else {
											menuJsonChildren.put("link", WebMessage.parseMessage(form.getFormName(), locale));
										}
									}
									menuJsonChildren.put("code", "form_" + form.getFormCode());
									menuJsonParent.append("children", menuJsonChildren);
								}
							}
						}
						arrMenuJson.put(menuJsonParent);
					}
					menuJson.put("menu", arrMenuJson);
				}
				String strMenuJson = "";
				try {
					byte[] _byteMenuJson = menuJson.toString().getBytes(StandardCharsets.UTF_8.toString());
					strMenuJson = encoder.encodeToString(_byteMenuJson);
				} catch (Exception e) {
					e.printStackTrace();
				}
				session.setAttribute("menuJson", myFilter.stripXSS(strMenuJson));
				model.addAttribute("menuJson", myFilter.stripXSS(strMenuJson));
			} else {
				model.addAttribute("menuJson", myFilter.stripXSS(session.getAttribute("menuJson").toString()));
			}

			//menuJson end
		} else {
			// TODO: For Controller RESTFul API
		}
	}
	
	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	/**
	 * The Global Catch Error Code
	 * 
	 * @param httpRequest
	 *            HttpServletRequest
	 * @return Error Code
	 */
	public int getErrorCode(HttpServletRequest httpRequest) {
		return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
	}
	
	/**
	 * Associates the specified value with the specified key in the map with which
	 * is exist and key is not null.
	 * 
	 * @param map
	 *            is not null and will to be put the key with the value into the map
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            to be associated with the specified key
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void saveMap(Map map, Object key, Object value) {
		if (null == map || null == key) {
			return;
		}
		map.put(key, value);
	}
	
	/**
	 * 是否需修改密碼
	 * 
	 * @return 是否
	 */
	public boolean isChangePassword(Locale locale) {
		//密碼是否需修改修改
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!(auth instanceof AnonymousAuthenticationToken)) {
				UserDetails userDetail = (UserDetails) auth.getPrincipal();
				Object[] authorities = userDetail.getAuthorities().toArray();
				JSONObject memberInformation = new JSONObject(authorities[0].toString());				
				baseMemberId = memberInformation.getLong("Id");
			}
		}
		catch (Exception e) {
			logger.warn(e.getMessage());
		}
		MemberHistory memberHistory = memberService.getMemberHistoryByMemberId(baseMemberId);
		Calendar now = Calendar.getInstance();
		Calendar cpd = Calendar.getInstance();
		now.setTime(new Date());		
		cpd.setTime(memberHistory.getCreateTime());
		cpd.add(Calendar.DATE, Integer.parseInt(resourceMessageService.get(locale, "maxChangePasswordDays")));												
		if (now.compareTo(cpd) == -1)
			return false;
		else
			return true;
	}	
}
