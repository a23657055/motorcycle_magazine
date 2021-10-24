package org.iii.SecBuzzer.template.controller.api;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.iii.SecBuzzer.template.controller.BaseController;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.domain.Member;
import org.iii.SecBuzzer.template.domain.Org;
import org.iii.SecBuzzer.template.domain.SpMemberRoleName;
import org.iii.SecBuzzer.template.domain.ViewMember;
import org.iii.SecBuzzer.template.service.MailService;
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
 * 人員基本資料管理控制器
 */
@Controller
@RequestMapping(value = "/sys/api", produces = "application/json; charset=utf-8")
public class s05_MemberController extends BaseController {

	@Autowired
	private OrgService orgService;

	@Autowired
	private MailService mailService;
	
//	@Autowired
//	private OrgSignService orgSignService;	

	private String targetControllerName = "sys";
	private String targetActionName = "s05";

	/**
	 * 取得單位機關名稱資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            orgService資料
	 * @return 單位名稱機關資料
	 */
	@PostMapping(value = "/s05/getorg")
	public @ResponseBody String Getorg(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray sn_array = new JSONArray();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			List<Org> orgs = null;

//			if (baseMemberRole.IsAdmin || baseMemberRole.IsHisac) {
			if (baseMemberRole.IsAdmin) {
				orgs = orgService.getAll();
			} else {
				JSONObject obj = new JSONObject(json);
				obj.put("Id", baseOrgId);
				json = obj.toString();
				orgs = orgService.getList(json);
			}
			if (orgs != null) {
				for (Org org : orgs) {
					if (baseMemberRole.IsAdmin) {
						JSONObject sn_json = new JSONObject();
						sn_json.put("Id", org.getId());
						sn_json.put("Code", org.getCode());
						sn_json.put("Name", org.getName());
						sn_array.put(sn_json);
					} else {
						JSONObject sn_json = new JSONObject();
						sn_json.put("Id", org.getId());
						sn_json.put("Code", org.getCode());
						sn_json.put("Name", org.getName());
						sn_array.put(sn_json);
					}
				}
			}
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		return sn_array.toString();
	}
	
	/**
	 * 取得人員基本資料資料API
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
	@PostMapping(value = "/s05/query")
	public @ResponseBody String Query(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			if (baseMemberRole.IsAdmin) {
				obj.put("RoleId", 1);
				json = obj.toString();
			} else if (baseMemberRole.IsApplyAdmin) {
				obj.put("RoleId", 9);
				obj.put("baseOrgId", baseOrgId);
				json = obj.toString();
			} else if (baseMemberRole.IsMemberAdmin) {
				obj.put("RoleId", 11);
				obj.put("baseOrgId", baseOrgId);
				json = obj.toString();
			} else {

			}

			obj = new JSONObject(json);
			List<ViewMember> members = memberService.getList(obj.toString());
			long size = memberService.getListSize(obj.toString());
			listjson.put("total", size);
			JSONArray sn_array = new JSONArray();
			if (members != null) {
				for (ViewMember member : members) {
					JSONObject sn_json = new JSONObject();
					sn_json.put("Id", member.getId());
					sn_json.put("Account", member.getAccount());
					sn_json.put("Name", member.getName());
					sn_json.put("Email", member.getEmail());
					sn_json.put("SpareEmail", member.getSpareEmail());
					sn_json.put("MobilePhone", member.getMobilePhone());
					sn_json.put("CityPhone", member.getCityPhone());
					sn_json.put("FaxPhone", member.getFaxPhone());
					sn_json.put("Address", member.getAddress());
					sn_json.put("Department", member.getDepartment());
					sn_json.put("Title", member.getTitle());
					sn_json.put("IsEnable", member.getIsEnable());
					sn_json.put("EnableTime", member.getEnableTime());
					sn_json.put("ErrorCount", member.getErrorCount());
					sn_json.put("OrgName", member.getOrgName());
					sn_json.put("Code", member.getCode());
					sn_json.put("OrgType", member.getOrgType());
					sn_json.put("AuthType", member.getAuthType());
					if (member.getErrorCount() == null) {
						sn_json.put("Status", 0); // 審核中(查看,無動作)
					} else if (member.getErrorCount() == -1) {
						sn_json.put("Status", 1); // 等待啟用 設定密碼(查看,無動作)
					} else if (member.getIsEnable() == false) {
						sn_json.put("Status", 2); // 已停用(查看,啟用)
					} else {
						sn_json.put("Status", 3); // 正常狀態(查看,停用,編輯)
					}
					JSONArray sn_role_array = new JSONArray();
					JSONObject memberObj = new JSONObject();
					memberObj.put("Id", member.getId());
					List<SpMemberRoleName> roles = memberRoleService.getMemberRoleList(memberObj.toString());
					if (roles != null) {
						for (SpMemberRoleName role : roles) {
							if (role.getFlag() != 0)
								sn_role_array.put(role.getName());
						}
					}
					sn_json.put("Roles", sn_role_array);
					sn_array.put(sn_json);
				}
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
			}
			listjson.put("datatable", sn_array);
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		return listjson.toString();
	}

	/**
	 * 取得人員基本資料資料API
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
	@PostMapping(value = "/s05/query/id")
	public @ResponseBody String QueryById(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray sn_array = new JSONArray();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			Member member = memberService.get(id);
			JSONObject sn_json = new JSONObject();
			sn_json.put("Id", member.getId());
			sn_json.put("OrgId", member.getOrgId());
			sn_json.put("Account", member.getAccount());
			sn_json.put("Name", member.getName());
			sn_json.put("Email", member.getEmail());
			sn_json.put("SpareEmail", member.getSpareEmail());
			sn_json.put("MobilePhone", member.getMobilePhone());
			sn_json.put("CityPhone", member.getCityPhone());
			sn_json.put("FaxPhone", member.getFaxPhone());
			sn_json.put("Address", member.getAddress());
			sn_json.put("Department", member.getDepartment());
			sn_json.put("Title", member.getTitle());
			sn_json.put("IsEnable", member.getIsEnable());
			sn_json.put("EnableTime", member.getEnableTime());
			sn_json.put("CreateId", member.getCreateId());
			sn_json.put("CreateTime", member.getCreateTime());
			sn_json.put("ModifyId", member.getModifyId());
			sn_json.put("ModifyTime", member.getModifyTime());
			sn_array.put(sn_json);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		return sn_array.toString();
	}

	/**
	 * 新增人員基本資料資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            Member
	 * @return 是否新增成功
	 */
	@PostMapping(value = "/s05/create")
	public @ResponseBody String Create(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getCreatePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			String account = obj.isNull("Account") == true ? null : obj.getString("Account");
			if (memberService.isAccountExist(account)) {
				responseJson.put("msg", messageSource.getMessage("globalDataExist", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				Member member = memberService.insert(baseMemberId, json);
				if (member != null) {
					JSONArray json_array = obj.getJSONArray("MemberRoleData");
					if (baseMemberRole.IsAdmin || baseMemberRole.IsApplyAdmin) {
						boolean isApplyContact = false; // roleid=8
						boolean isMemberContact = false; // roleid=10
						boolean isWriteApplyContact = false;
						boolean isWriteMemberContact = false;
						for (int i = 0; i < json_array.length(); i++) {							
							JSONObject obj_member_role = json_array.getJSONObject(i);
							long member_role_id = obj_member_role.isNull("Id") == true ? 0 : obj_member_role.getLong("Id");
							long roleId = obj_member_role.isNull("RoleId") == true ? 0 : obj_member_role.getLong("RoleId");
							boolean flag = obj_member_role.isNull("Flag") == true ? false : obj_member_role.getBoolean("Flag");
							if (roleId == 8 && !isApplyContact && flag)
								isApplyContact = flag;
							if (roleId == 9 && !isApplyContact && flag)
								isApplyContact = true;
							if (roleId == 10 && !isMemberContact && flag)
								isMemberContact = flag;
							if (roleId == 11 && !isMemberContact && flag)
								isMemberContact = true;
							if (roleId != 8 && roleId != 10)
								memberRoleService.insertOrDelete(baseMemberId, member_role_id, member.getId(), roleId, flag);
							if (isApplyContact && !isWriteApplyContact) {
								memberRoleService.insertOrDelete(baseMemberId, (long) 0, member.getId(), (long) 8, true);
								isWriteApplyContact = true;
							}
							if (isMemberContact && !isWriteMemberContact) {
								memberRoleService.insertOrDelete(baseMemberId, (long) 0, member.getId(), (long) 10, true);
								isWriteMemberContact = true;
							}
						}
					} else if (baseMemberRole.IsMemberAdmin) {
						memberRoleService.insertOrDelete(baseMemberId, (long) 0, member.getId(), (long) 10, true);
					}
					memberRoleService.updateIsenable(member.getId());
					memberService.setMemberEnable(member.getId(), true, baseMemberId);
					String salt = WebCrypto.generateUUID();
					String newCode = WebCrypto.getHash(WebCrypto.HashType.SHA512, WebCrypto.getRandomPassword() + salt);
					memberService.insertMemberHistory(member.getId(), newCode, salt, (short) -1, baseMemberId);
					String code = WebCrypto.generateUUID() + WebCrypto.generateUUID() + WebCrypto.generateUUID() + WebCrypto.generateUUID();
					Date expireTime = WebDatetime.addDays(null, 30);
					memberService.updateForgotTemp(code, member.getId(), expireTime);
					String mailSubject = resourceMessageService.get(locale, "mailSignApplySetPasswordSubject");
					String mailBody = MessageFormat.format(
							resourceMessageService.get(locale, "mailSignApplySetPasswordBody"),
							member.getName(), member.getAccount(), WebConfig.WEB_SITE_URL + "reset?" + code,
							WebConfig.WEB_SITE_URL);
					mailService.Send(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), member.getEmail(), null, null, mailSubject, mailBody, null);
					responseJson.put("msg", messageSource.getMessage("globalInsertDataSuccess", null, locale));
					responseJson.put("success", true);
					systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
				} else {
					responseJson.put("msg", messageSource.getMessage("globalInsertDataFail", null, locale));
					responseJson.put("success", false);
					systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
				}
			}
		} else {
			responseJson.put("msg", messageSource.getMessage("globalPermissionDeny", null, locale));
			responseJson.put("success", false);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		return responseJson.toString();
	}

	/**
	 * 更新人員基本資料資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            Member
	 * @return 是否更新成功
	 */
	@PostMapping(value = "/s05/update")
	public @ResponseBody String Update(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getUpdatePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.getLong("Id");
			if (!memberService.isExist(id)) {
				responseJson.put("msg", messageSource.getMessage("globalDataNotExist", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				Member member = memberService.update(baseMemberId, json);
				if (member != null) {
					JSONArray json_array = obj.getJSONArray("MemberRoleData");
					for (int i = 0; i < json_array.length(); i++) {
						JSONObject obj_member_role = json_array.getJSONObject(i);
						long member_role_id = obj_member_role.isNull("Id") == true ? 0 : obj_member_role.getLong("Id");
						long roleId = obj_member_role.isNull("RoleId") == true ? 0 : obj_member_role.getLong("RoleId");
						boolean flag = obj_member_role.isNull("Flag") == true ? false : obj_member_role.getBoolean("Flag");
						memberRoleService.insertOrDelete(baseMemberId, member_role_id, id, roleId, flag);
					}
					memberRoleService.updateIsenable(id);
					responseJson.put("msg", messageSource.getMessage("globalUpdateDataSuccess", null, locale));
					responseJson.put("success", true);
					systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
				} else {
					responseJson.put("msg", messageSource.getMessage("globalUpdateDataFail", null, locale));
					responseJson.put("success", false);
					systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
				}
			}
		} else {
			responseJson.put("msg", messageSource.getMessage("globalPermissionDeny", null, locale));
			responseJson.put("success", false);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		return responseJson.toString();
	}

	/**
	 * 刪除人員基本資料資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            編號
	 * @return 是否刪除成功
	 */
	@PostMapping(value = "/s05/delete")
	public @ResponseBody String DeleteById(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getDeletePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			if (!memberService.isExist(id)) {
				responseJson.put("msg", messageSource.getMessage("globalDataNotExist", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Delete, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				Member member = memberService.setDisable(baseMemberId, id);
				if (member != null) {
					responseJson.put("msg", "刪除帳號成功");
					responseJson.put("success", true);
					systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Delete, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);

				} else {
					responseJson.put("msg", messageSource.getMessage("globalDeleteDataFail", null, locale));
					responseJson.put("success", false);
					systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Delete, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
				}
			}
		} else {
			responseJson.put("msg", messageSource.getMessage("globalPermissionDeny", null, locale));
			responseJson.put("success", false);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Delete, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		return responseJson.toString();
	}

	/**
	 * 取得會員權限資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            查詢條件
	 * @return 會員權限資料
	 */
	@PostMapping(value = "/s05/member/role/query")
	public @ResponseBody String QueryMemberRole(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONArray sn_array = new JSONArray();
			if (baseMemberRole.IsAdmin || baseMemberRole.IsApplyAdmin) {				
				List<SpMemberRoleName> spMemberRoleNames = memberRoleService.getMemberRoleList(json);
				for (SpMemberRoleName spMemberRoleName : spMemberRoleNames) {
					if (baseMemberRole.IsAdmin || (baseMemberRole.IsApplyAdmin 
							&& (spMemberRoleName.getRoleId()==7 || spMemberRoleName.getRoleId()==8
								|| spMemberRoleName.getRoleId()==9 || spMemberRoleName.getRoleId()==10
								|| spMemberRoleName.getRoleId()==11))) {						
						JSONObject sn_json = new JSONObject();
						sn_json.put("Id", spMemberRoleName.getId());
						sn_json.put("RoleId", spMemberRoleName.getRoleId());
						sn_json.put("Name", spMemberRoleName.getName());
						sn_json.put("Flag", spMemberRoleName.getFlag() == 0 ? false : true);
						sn_array.put(sn_json);
					}
				}
			}			
			listjson.put("datatable", sn_array);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		return listjson.toString();
	}
	
	/**
	 * 寄發會員帳號申請完成通知信件API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            寄送帳號id
	 * @return 是否成功
	 */
	@PostMapping(value = "/s05/member/sent")
	public @ResponseBody String Sent(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();	
		try {
			List<Member> members = memberService.getByIds(json);
			if (members!=null)
				for (Member member: members) {
					String mailSubject = resourceMessageService.get(locale, "mailSignUpPassSubject");
					String mailBody = MessageFormat.format(resourceMessageService.get(locale, "mailSignUpPassBody"), member.getName(), member.getAccount(), member.getEmail());
					mailService.Send(this.getClass().getSimpleName() + " - " + Thread.currentThread().getStackTrace()[1].getMethodName(), member.getEmail(), member.getSpareEmail(), null, mailSubject, mailBody, null);		
				}	
			responseJson.put("msg", "寄發會員帳號申請完成通知信件成功");
			responseJson.put("success", true);	
		} catch (Exception e) {
			responseJson.put("msg", "寄發會員帳號申請完成通知信件失敗");
			responseJson.put("success", false);
			e.printStackTrace();
		}
		return responseJson.toString();
	}
}