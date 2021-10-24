package org.iii.SecBuzzer.template.controller.api;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.iii.SecBuzzer.template.controller.BaseController;
import org.iii.SecBuzzer.template.service.MemberSignApplyService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 公開資訊index控制器
 */
@Controller
@RequestMapping(value = "/pub/api", produces = "application/json; charset=utf-8")
public class p00_IndexController extends BaseController {
	@Autowired
	private MemberSignApplyService memberSignApplyService;
	
	@PostMapping(value = "/count/member_sign")
	public @ResponseBody String CountMemberSign(Locale locale,
			HttpServletRequest request, Model model, @RequestBody String json) {
		JSONObject countJson = new JSONObject();
		long resultValue = memberSignApplyService
				.getListSize(new JSONObject().toString());
		countJson.put("count", resultValue);
		return countJson.toString();
	}
}