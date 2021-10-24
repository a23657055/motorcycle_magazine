package org.iii.SecBuzzer.template.controller.api;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.iii.SecBuzzer.template.controller.BaseController;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.domain.Member;
import org.iii.SecBuzzer.template.domain.Org;
import org.iii.SecBuzzer.template.service.OrgService;
import org.iii.SecBuzzer.template.util.WebConfig;
import org.iii.SecBuzzer.template.util.WebCrypto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 設置會員個人資料控制器
 */
@Controller
@RequestMapping(value = "/sys/api", produces = "application/json; charset=utf-8")
public class s00_MemberProfileController extends BaseController {
	final static Logger logger = LoggerFactory.getLogger(s00_MemberProfileController.class);

	@Autowired
	private OrgService orgService;

	/**
	 * 重設個人資料
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            查詢條件
	 * @return JSON Format String
	 */
	@PostMapping(value = "/profile/query")
	public @ResponseBody String QueryMember(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray sn_array = new JSONArray();
		Member member = memberService.get(baseMemberId);
		JSONObject sn_json = new JSONObject();
		sn_json.put("Name", member.getName());
		sn_json.put("Email", member.getEmail());
		sn_json.put("SpareEmail", member.getSpareEmail());
		sn_json.put("MobilePhone", member.getMobilePhone());
		sn_json.put("CityPhone", member.getCityPhone());
		sn_json.put("FaxPhone", member.getFaxPhone());
		sn_json.put("Address", member.getAddress());
		sn_json.put("Department", member.getDepartment());
		sn_json.put("Title", member.getTitle());
		sn_array.put(sn_json);
		systemLogService.insert(baseControllerName, baseActionName, baseMemberId.toString(), SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
		return sn_array.toString();
	}

	/**
	 * 重設個人資料
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            個人資料
	 * @return JSON Format String
	 */
	@PostMapping(value = "/profile/update")
	public @ResponseBody String UpdateMember(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();

		try {
			if (memberService.isExist(baseMemberId)) {
				if (memberService.updateProfile(baseMemberId, json) != null) {
					responseJson.put("msg", messageSource.getMessage("globalUpdateDataSuccess", null, locale));
					responseJson.put("success", true);
					systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
				} else {
					responseJson.put("msg", messageSource.getMessage("globalUpdateDataFail", null, locale));
					responseJson.put("success", false);
					systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
				}
			} else {
				responseJson.put("msg", messageSource.getMessage("globalUpdateDataFail", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			}
		} catch (Exception e) {
			responseJson.put("msg", messageSource.getMessage("globalUpdateDataFail", null, locale));
			responseJson.put("success", false);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
		}
		return responseJson.toString();
	}

	/**
	 * 重設密碼
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            RequestBody
	 * @return JSON Format String
	 */
	@PostMapping(value = "/code/update")
	public @ResponseBody String ChangeCode(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();

		JSONObject obj = new JSONObject(json);
		Base64.Decoder decoder = Base64.getDecoder();
		String oldCode = obj.isNull("OldCode") ? null : obj.getString("OldCode");
		String newCode = obj.isNull("NewCode") ? null : obj.getString("NewCode");
		try {
			oldCode = new String(decoder.decode(oldCode), StandardCharsets.UTF_8.toString());
			newCode = new String(decoder.decode(newCode), StandardCharsets.UTF_8.toString());
			if (memberService.isAuth(baseMemberAccount, oldCode)) {
				if (newCode == null || newCode.isEmpty() || newCode.length() < 10 || !Pattern.compile("^(?=.*\\d)(?=.*[a-zA-Z])(?=.*\\W).{10,}$").matcher(newCode).matches()) {
					// 密碼強度不符合
					responseJson.put("msg", messageSource.getMessage("memberResetCodeFail", null, locale));
					responseJson.put("success", false);
				} else {
					boolean isMemberHistoryeEffective = memberService.checkMemberHistory(baseMemberId, newCode, WebConfig.HISTORY_TIMES, WebConfig.HISTORY_DAYS);
					if (isMemberHistoryeEffective) {
						Object[] messageArgs = new Object[2];
						messageArgs[0] = WebConfig.HISTORY_TIMES;
						messageArgs[1] = WebConfig.HISTORY_DAYS;
						responseJson.put("msg", messageSource.getMessage("memberHistoryeEffective", messageArgs, locale));
						responseJson.put("success", false);
					} else {
						String salt = WebCrypto.generateUUID();
						newCode = WebCrypto.getHash(WebCrypto.HashType.SHA512, newCode + salt);
						memberService.insertMemberHistory(baseMemberId, newCode, salt, (short) 0, baseMemberId);
						responseJson.put("msg", messageSource.getMessage("globalResetCodeSuccess", null, locale));
						responseJson.put("success", true);
						systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
					}
				}
			} else {
				responseJson.put("msg", messageSource.getMessage("globalResetCodeFail", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			responseJson.put("msg", messageSource.getMessage("globalResetCodeFail", null, locale));
			responseJson.put("success", false);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
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
	@PostMapping(value = "/code/getPasswordLength")
	public @ResponseBody String GetPasswordLength(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray arrjson = new JSONArray();
		arrjson.put(resourceMessageService.get(locale, "passwordLength"));
		return arrjson.toString();
	}
	 
	/**
	 * 取得組織資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            組織Id
	 * @return 組織資料
	 */
	@PostMapping(value = "/profile/org/query")
	public @ResponseBody String QueryOrg(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray sn_array = new JSONArray();
		Org org = orgService.getDataById(baseOrgId);
		JSONObject sn_json = new JSONObject();
		sn_json.put("Id", org.getId());
		sn_json.put("Name", org.getName());
		sn_json.put("Code", org.getCode());
		sn_json.put("OrgType", org.getOrgType());
		sn_json.put("AuthType", org.getAuthType());
		sn_json.put("Tel", org.getTel());
		sn_json.put("Fax", org.getFax());
		sn_json.put("City", org.getCity());
		sn_json.put("Town", org.getTown());
		sn_json.put("Address", org.getAddress());
		sn_json.put("IsEnable", org.getIsEnable());

		sn_array.put(sn_json);
		systemLogService.insert(baseControllerName, baseActionName, baseOrgId.toString(), SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
		return sn_array.toString();
	}

	/**
	 * 更新組織資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            組織
	 * @return 是否更新成功
	 */
	@PostMapping(value = "/profile/org/update")
	public @ResponseBody String UpdateOrg(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();

		if (!orgService.isExist(baseOrgId)) {
			responseJson.put("msg", messageSource.getMessage("globalDataNotExist", null, locale));
			responseJson.put("success", false);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
		} else {
			Org org = orgService.updateProfile(baseMemberId, json, baseOrgId);
			if (org != null) {
				responseJson.put("msg", messageSource.getMessage("globalUpdateDataSuccess", null, locale));
				responseJson.put("success", true);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
			} else {
				responseJson.put("msg", messageSource.getMessage("globalUpdateDataFail", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			}
		}
		return responseJson.toString();
	}
}
