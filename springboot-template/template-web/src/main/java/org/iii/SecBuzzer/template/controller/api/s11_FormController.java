package org.iii.SecBuzzer.template.controller.api;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.iii.SecBuzzer.template.controller.BaseController;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.domain.Form;
import org.iii.SecBuzzer.template.domain.Subsystem;
import org.iii.SecBuzzer.template.domain.ViewFormName;
import org.iii.SecBuzzer.template.service.FormService;
import org.iii.SecBuzzer.template.service.SubsystemService;
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
 * 表單資料維護控制器
 */
@Controller
@RequestMapping(value = "/sys/api", produces = "application/json; charset=utf-8")
public class s11_FormController extends BaseController {
	@Autowired
	private FormService formService;
	
	@Autowired
	private SubsystemService subsystemService;

	private String targetControllerName = "sys";
	private String targetActionName = "s11";

	/**
	 * 取得Form資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            查詢條件
	 * @return Form資料
	 */
	@PostMapping(value = "/s11/query")
	public String Query(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			List<ViewFormName> forms = formService.getList(json);
			listjson.put("total", formService.getListSize(json));

			JSONArray sn_array = new JSONArray();
			if (forms != null)
				for (ViewFormName form : forms) {
					JSONObject sn_json = new JSONObject();
					sn_json.put("Id", form.getId());
					sn_json.put("SubsystemName", form.getSubsystemName());
					sn_json.put("Name", form.getName());
					sn_json.put("IsExternalLink", form.getIsExternalLink());
					sn_json.put("IsEnable", form.getIsEnable());
					sn_json.put("IsShow", form.getIsShow());
					sn_json.put("SubsystemId", form.getSubsystemId());
					sn_json.put("Code", form.getCode());
					sn_json.put("ControllerName", form.getControllerName());
					sn_json.put("ActionName", form.getActionName());
					sn_json.put("Sort", form.getSort());

					sn_array.put(sn_json);
				}
			listjson.put("datatable", sn_array);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		model.addAttribute("json", listjson.toString());
		return "msg";
	}

	/**
	 * 取得Form資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            Id
	 * @return Form資料
	 */
	@PostMapping(value = "/s11/query/id")
	public String QueryById(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray sn_array = new JSONArray();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);

			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");

			Form form = formService.get(id);
			JSONObject sn_json = new JSONObject();
			sn_json.put("Id", form.getId());
			sn_json.put("SubsystemId", form.getSubsystemId());
			sn_json.put("Code", form.getCode());
			sn_json.put("Name", form.getName());
			sn_json.put("ControllerName", form.getControllerName());
			sn_json.put("ActionName", form.getActionName());
			sn_json.put("IsExternalLink", form.getIsExternalLink());
			sn_json.put("IsEnable", form.getIsEnable());
			sn_json.put("IsShow", form.getIsShow());
			sn_json.put("Sort", form.getSort());

			sn_array.put(sn_json);

			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		model.addAttribute("json", sn_array.toString());
		return "msg";
	}

	/**
	 * 取得subsystem資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            subsystem資料
	 * @return subsystem資料
	 */
	@PostMapping(value = "/s11/getsubsystem")
	public String Getsubsystem(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray sn_array = new JSONArray();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			List<Subsystem> subsystems = subsystemService.getAll();
			if (subsystems != null)
				for (Subsystem subsystem : subsystems) {
					JSONObject sn_json = new JSONObject();
					sn_json.put("Id", subsystem.getId());
					sn_json.put("Name", subsystem.getName());
					sn_array.put(sn_json);
				}
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
		} else {
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		model.addAttribute("json", sn_array.toString());
		return "msg";
	}

	/**
	 * 新增Form資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            Form
	 * @return 是否新增成功
	 */
	@PostMapping(value = "/s11/create")
	public @ResponseBody String Create(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getCreatePermission(baseMemberId, targetControllerName, targetActionName)) {
			Form form = formService.insert(baseMemberId, json);
			if (form != null) {
				responseJson.put("msg", messageSource.getMessage("globalInsertDataSuccess", null, locale));
				responseJson.put("success", true);

				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);
			} else {
				responseJson.put("msg", messageSource.getMessage("globalInsertDataFail", null, locale));
				responseJson.put("success", false);

				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			}
		} else {
			responseJson.put("msg", messageSource.getMessage("globalPermissionDeny", null, locale));
			responseJson.put("success", false);
			systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Deny, baseIpAddress, baseMemberAccount);
		}
		return responseJson.toString();
	}

	/**
	 * 更新Form資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            Form
	 * @return 是否更新成功
	 */
	@PostMapping(value = "/s11/update")
	public @ResponseBody String Update(Locale locale, HttpServletRequest request, @RequestBody String json) {

		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getUpdatePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.getLong("Id");

			if (!formService.isExist(id)) {
				responseJson.put("msg", messageSource.getMessage("globalDataNotExist", null, locale));
				responseJson.put("success", false);

				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				Form form = formService.update(baseMemberId, json);
				if (form != null) {
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
	 * 刪除Form資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            編號
	 * @return 是否刪除成功
	 */
	@PostMapping(value = "/s11/delete")
	public @ResponseBody String DeleteById(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getDeletePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");

			if (!formService.isExist(id)) {
				responseJson.put("msg", messageSource.getMessage("globalDataNotExist", null, locale));
				responseJson.put("success", false);
				
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Delete, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				if (formService.delete(id)) {
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