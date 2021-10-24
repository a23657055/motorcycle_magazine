package org.iii.SecBuzzer.template.controller.api;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.iii.SecBuzzer.template.controller.BaseController;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.domain.ArticleOrPictureData;
import org.iii.SecBuzzer.template.domain.Subsystem;
import org.iii.SecBuzzer.template.service.ArticleOrPictureDataService;
import org.iii.SecBuzzer.template.service.SubsystemService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/sys/api", produces = "application/json; charset=utf-8")
public class reloadResourceController extends BaseController{
	
	@Autowired
	private SubsystemService subsystemService;
	
	@Autowired
	private ArticleOrPictureDataService articleOrPictureDataService;
	
	@PostMapping(value = "/r00/query")
	public String Query(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		
		List<Subsystem> subsystems = subsystemService.getList(json);
		listjson.put("total", subsystemService.getListSize(json));
		JSONArray sn_array = new JSONArray();
		if (subsystems != null)
			for (Subsystem subsystem : subsystems) {
				JSONObject sn_json = new JSONObject();
				sn_json.put("Id", subsystem.getId());
				sn_json.put("Code", subsystem.getCode());
				sn_json.put("Name", subsystem.getName());
				sn_json.put("IconStyle", subsystem.getIconStyle());
				sn_json.put("IsEnable", subsystem.getIsEnable());
				sn_json.put("IsShow", subsystem.getIsShow());
				sn_json.put("Sort", subsystem.getSort());
				sn_array.put(sn_json);
			}
		listjson.put("datatable", sn_array);
		systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);

		model.addAttribute("json", listjson.toString());
		return "msg";
	}
	
	@PostMapping(value = "/r00/queryImage")
	public String queryImage(Locale locale, HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject listjson = new JSONObject();
		
		List<ArticleOrPictureData> imageList = articleOrPictureDataService.getImages(json);
		listjson.put("total", subsystemService.getListSize(json));
		JSONArray sn_array = new JSONArray();
		if (imageList != null)
			for (ArticleOrPictureData articleOrPictureData : imageList) {
				JSONObject sn_json = new JSONObject();
				sn_json.put("location", articleOrPictureData.getLocationCode());
				sn_json.put("content", articleOrPictureData.getContent());
				sn_array.put(sn_json);
			}
		listjson.put("datatable", sn_array);
		systemLogService.insert(baseControllerName, baseActionName, json, SystemLogVariable.Action.Read, SystemLogVariable.Status.Success, baseIpAddress, baseMemberAccount);

		model.addAttribute("json", listjson.toString());
		return "msg";
	}
}
