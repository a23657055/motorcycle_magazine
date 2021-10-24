package org.iii.SecBuzzer.template.controller.api;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.iii.SecBuzzer.template.controller.BaseController;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.domain.ResourceMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 網站設定控制器
 */
@Controller
@RequestMapping(value = "/sys/api", produces = "application/json; charset=utf-8")
public class s09_ResourceMessageController extends BaseController {
	private String targetControllerName = "sys";
	private String targetActionName = "s09";

	/**
	 * 取得網站設定資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            查詢條件
	 * @return 網站設定資料
	 */
	@PostMapping(value = "/s09/query")
	public String Query(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			List<ResourceMessage> resourceMessages = resourceMessageService.getList(json);
			listjson.put("total", resourceMessageService.getListSize(json));
			JSONArray sn_array = new JSONArray();
			if (resourceMessages != null) {
				for (ResourceMessage resourceMessage : resourceMessages) {
					JSONObject sn_json = new JSONObject();
					sn_json.put("Id", resourceMessage.getId());
					sn_json.put("Language", resourceMessage.getLanguage());
					sn_json.put("MessageKey", resourceMessage.getMessageKey());
					sn_json.put("MessageValue", resourceMessage.getMessageValue());
					sn_array.put(sn_json);
				}
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
	 * 取得網站設定資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @param json
	 *            網站設定資料Id
	 * @return 網站設定資料
	 */
	@PostMapping(value = "/s09/query/id")
	public String QueryById(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONArray sn_array = new JSONArray();
		if (menuService.getReadPermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			ResourceMessage resourceMessage = resourceMessageService.get(id);
			if(resourceMessage!=null) {
				JSONObject sn_json = new JSONObject();
				sn_json.put("Id", resourceMessage.getId());
				sn_json.put("Language", resourceMessage.getLanguage());
				sn_json.put("MessageKey", resourceMessage.getMessageKey());
				sn_json.put("MessageValue", resourceMessage.getMessageValue());
				
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
	 * 新增網站設定資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            網站設定資料代碼
	 * @return 是否新增成功
	 */
	@PostMapping(value = "/s09/create")
	public @ResponseBody String Create(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getCreatePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			String language = obj.isNull("Language") == true ? "" : obj.getString("Language");
			String messageKey = obj.isNull("MessageKey") == true ? "" : obj.getString("MessageKey");
			
//			if (resourceMessageService.isMessageKeyExist(messageKey)) {
			
			if (resourceMessageService.isMessageKeyExist(messageKey, language)) {
				responseJson.put("msg", messageSource.getMessage("globalDataExist", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Create, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				ResourceMessage resourceMessage = resourceMessageService.insert(json, baseMemberId);
				resourceMessageService.clearCache();
				if (resourceMessage != null) {
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
	 * 更新網站設定資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            網站設定資料代碼
	 * @return 是否更新成功
	 */
	@PostMapping(value = "/s09/update")
	public @ResponseBody String Update(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getUpdatePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");

			if (!resourceMessageService.isExist(id)) {
				responseJson.put("msg", messageSource.getMessage("globalDataNotExist", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Update, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				ResourceMessage resourceMessage = resourceMessageService.update(json, baseMemberId);
				resourceMessageService.clearCache();
				if (resourceMessage != null) {
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
	 * 刪除網站設定資料API
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param json
	 *            編號
	 * @return 是否刪除成功
	 */
	@PostMapping(value = "/s09/delete")
	public @ResponseBody String DeleteById(Locale locale, HttpServletRequest request, @RequestBody String json) {
		JSONObject responseJson = new JSONObject();
		CsrfToken token = new HttpSessionCsrfTokenRepository().loadToken(request);
		if (token == null || token.getToken().equals(""))
			return responseJson.toString();
		if (menuService.getDeletePermission(baseMemberId, targetControllerName, targetActionName)) {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			if (!resourceMessageService.isExist(id)) {
				responseJson.put("msg", messageSource.getMessage("globalDataNotExist", null, locale));
				responseJson.put("success", false);
				systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Delete, SystemLogVariable.Status.Fail, baseIpAddress, baseMemberAccount);
			} else {
				if (resourceMessageService.delete(id)) {
					resourceMessageService.clearCache();
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