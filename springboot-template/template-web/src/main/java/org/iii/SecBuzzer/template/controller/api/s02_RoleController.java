package org.iii.SecBuzzer.template.controller.api;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.iii.SecBuzzer.template.controller.BaseController;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.domain.Role;
import org.iii.SecBuzzer.template.service.RoleService;
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
 * 角色資料維護控制器
 */
@Controller
@RequestMapping(value = "/sys/api", produces = "application/json; charset=utf-8")
public class s02_RoleController extends BaseController {
	@Autowired
	private RoleService roleService;

	private String targetControllerName = "sys";
	private String targetActionName = "s02";

	/**
	 * 取得角色資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            查詢條件
	 * @return 依照條件查詢角色資料
	 */
	@PostMapping(value = "/s02/query")
	public String Query(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONArray sn_array = new JSONArray();
			listjson.put("total", roleService.getRoleListSize(json));
			List<Role> roles = roleService.getList(json);
			if (roles != null) {
				for (Role role : roles) {
					JSONObject sn_json = new JSONObject();
					sn_json.put("Id", role.getId());
					sn_json.put("Name", role.getName());
					sn_json.put("IsEnable", role.getIsEnable());
					sn_json.put("Sort", role.getSort());
					sn_json.put("CreateId", role.getCreateId());
					sn_json.put("CreateTime", role.getCreateTime());
					sn_json.put("ModifyId", role.getModifyId());
					sn_json.put("ModifyTime", role.getModifyTime());
					sn_array.put(sn_json);
				}
				listjson.put("datatable", sn_array);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
			}
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		model.addAttribute("json", listjson.toString());
		return "msg";
	}

	/**
	 * 依照 id 取得角色資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            Id
	 * @return 取得角色資料
	 */
	@PostMapping(value = "/s02/query/id")
	public String QueryById(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray sn_array = new JSONArray();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			Role role = roleService.getDataById(id);
			JSONObject sn_json = new JSONObject();
			sn_json.put("Id", role.getId());
			sn_json.put("Name", role.getName());
			sn_json.put("IsEnable", role.getIsEnable());
			sn_json.put("Sort", role.getSort());
			sn_json.put("CreateId", role.getCreateId());
			sn_json.put("CreateTime", role.getCreateTime());
			sn_json.put("ModifyId", role.getModifyId());
			sn_json.put("ModifyTime", role.getModifyTime());
			sn_array.put(sn_json);
			systemLogService.insert(baseControllerName, baseActionName, String.valueOf(id), SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		model.addAttribute("json", sn_array.toString());
		return "msg";
	}

	/**
	 * 新增角色資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            角色資料
	 * @return 是否新增成功
	 */
	@PostMapping(value = "/s02/create")
	public @ResponseBody String Create(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getCreatePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			if (roleService.isNameExist(name)) {
				responseJson.put("msg", messageSource.getMessage("globalDataExist", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				Role role = roleService.insertData(baseMemberId, json);
				if (role != null) {
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
	 * 更新角色資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            角色資料
	 * @return 是否更新成功
	 */
	@PostMapping(value = "/s02/update")
	public @ResponseBody String Update(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getUpdatePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.getLong("Id");
			if (!roleService.isExist(id)) {
				responseJson.put("msg", messageSource.getMessage("globalDataNotExist", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				Role role = roleService.updateData(baseMemberId, json);
				if (role != null) {
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
	 * 刪除角色資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            編號
	 * @return 是否刪除成功
	 */
	@PostMapping(value = "/s02/delete")
	public @ResponseBody String DeleteById(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getDeletePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			if (!roleService.isExist(id)) {
				responseJson.put("msg", messageSource.getMessage("globalDataNotExist", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Delete, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				if (roleService.deleteDataById(id)) {
					responseJson.put("msg", messageSource.getMessage("globalDeleteDataSuccess", null, locale));
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

}
