package org.iii.SecBuzzer.template.controller.api;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.iii.SecBuzzer.template.controller.BaseController;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.domain.Member;
import org.iii.SecBuzzer.template.domain.MemberHistory;
import org.iii.SecBuzzer.template.domain.Org;
import org.iii.SecBuzzer.template.domain.ViewMemberSignApply;
import org.iii.SecBuzzer.template.service.MailService;
import org.iii.SecBuzzer.template.service.MemberRoleService;
import org.iii.SecBuzzer.template.service.MemberSignApplyService;
import org.iii.SecBuzzer.template.service.OrgService;
import org.iii.SecBuzzer.template.util.WebConfig;
import org.iii.SecBuzzer.template.util.WebCrypto;
import org.iii.SecBuzzer.template.util.WebDatetime;
import org.json.JSONArray;
import org.json.JSONObject;
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
 * 會員審查控制器
 */
@Controller
@RequestMapping(value = "/sys/api", produces = "application/json; charset=utf-8")
public class s15_MemberSignApplyController extends BaseController {

	@Autowired
	private MemberSignApplyService memberSignApplyService;

	@Autowired
	private OrgService orgService;

	@Autowired
	private MailService mailService;

	@Autowired
	private MemberRoleService memberRoleService;

	private String targetControllerName = "sys";
	private String targetActionName = "s15";

	/**
	 * 取得Member資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            查詢條件
	 * @return Member資料
	 */
	@PostMapping(value = "/s15/query")
	public String Query(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		JSONArray sn_array = new JSONArray();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			List<ViewMemberSignApply> members = memberSignApplyService.getList(json);
			if (members != null) {
				for (ViewMemberSignApply member : members) {
					JSONObject sn_json = new JSONObject();
					sn_json.put("Id", member.getId());
					sn_json.put("OrgName", member.getOrgName());
					sn_json.put("Account", member.getAccount());
					sn_json.put("Name", member.getName());
					sn_json.put("Email", member.getEmail());
					sn_json.put("MobilePhone", member.getMobilePhone());
					sn_array.put(sn_json);
				}
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
			}
			listjson.put("total", memberSignApplyService.getListSize(json));
			listjson.put("datatable", sn_array);
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		model.addAttribute("json", listjson.toString());
		return "msg";
	}

	/**
	 * 取得Member資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            MemberId
	 * @return Member資料
	 */
	@PostMapping(value = "/s15/query/id")
	public String QueryById(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray sn_array = new JSONArray();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			Member member = memberService.get(id);
			if (member != null) {
				Org org = orgService.getDataById(member.getOrgId());
				if (org != null) {
					
//					ViewParentOrg orgLocal = orgService.getLocalParentOrg(member.getOrgId());
					String orgLocalName = "";
//					if (orgLocal != null)
//						orgLocalName = orgLocal.getName();
//					ViewParentOrg orgSupervise = orgService.getSuperviseParentOrg(member.getOrgId());
					String orgSuperviseName = "";
//					if (orgSupervise != null)
//						orgSuperviseName = orgSupervise.getName();
					
					JSONObject sn_json = new JSONObject();
					sn_json.put("Id", member.getId());
					sn_json.put("OrgId", member.getOrgId());
					sn_json.put("OrgName", org.getName());
					sn_json.put("OrgLocalName", orgLocalName);
					sn_json.put("OrgSuperviseName", orgSuperviseName);
					sn_json.put("Account", member.getAccount());
					sn_json.put("Name", member.getName());
					sn_json.put("Email", member.getEmail());
					sn_json.put("MobilePhone", member.getMobilePhone());
					sn_json.put("IsEnable", member.getIsEnable());
					sn_json.put("CreateTime", WebDatetime.toString(member.getCreateTime()));
					sn_array.put(sn_json);
					systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
				} else {
					systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
				}
			} else {
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
			}
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		model.addAttribute("json", sn_array.toString());
		return "msg";
	}

	/**
	 * 啟用會員API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            條件
	 * @return 是否啟用會員成功
	 */
	@PostMapping(value = "/s15/update")
	public @ResponseBody String Update(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getUpdatePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long memberId = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			Member member = memberService.get(memberId);
			MemberHistory memberHistory = memberService.getMemberHistoryByMemberId(memberId);
			if (member != null && memberHistory == null) {
				Org org = orgService.getDataById(member.getOrgId());
				if (org != null) {
					memberService.setMemberEnable(memberId, true, baseMemberId);
					String salt = WebCrypto.generateUUID();
					String newCode = WebCrypto.getHash(WebCrypto.HashType.SHA512, WebCrypto.getRandomPassword() + salt);
					memberService.insertMemberHistory(memberId, newCode, salt, (short) -1, baseMemberId);
					String code = WebCrypto.generateUUID() + WebCrypto.generateUUID() + WebCrypto.generateUUID() + WebCrypto.generateUUID();
					Date expireTime = WebDatetime.addDays(null, 30);
					memberService.updateForgotTemp(code, memberId, expireTime);
					memberRoleService.insert(baseMemberId, member.getId(), (long) 10); //自動新增會員聯絡人角色
					memberRoleService.insert(baseMemberId, member.getId(), (long) 11); //自動新增會員管理者角色
					String mailSubject = resourceMessageService.get(locale, "mailSignApplySetPasswordSubject");
					String mailBody = MessageFormat.format(resourceMessageService.get(locale, "mailSignApplySetPasswordBody"),  member.getName(), member.getAccount(), WebConfig.WEB_SITE_URL + "reset?" + code, WebConfig.WEB_SITE_URL);
					mailService.Send(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), member.getEmail(), null, null, mailSubject, mailBody, null);
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
		} else {
			responseJson.put("msg", messageSource.getMessage("globalPermissionDeny", null, locale));
			responseJson.put("success", false);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		return responseJson.toString();
	}
}