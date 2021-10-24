package org.iii.SecBuzzer.template.controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/sys", produces = "application/json; charset=utf-8")
public class DirectSysController extends BaseController {
	/**
	 * 變更個人資料
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/edit_profile")
	public String edit_profile(Locale locale, Model model) {
		return "/sys/edit_profile";
	}
	
	/**
	 * 變更密碼
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/change_code")
	public String change_code(Locale locale, Model model) {
		return "/sys/change_code";
	}
	
	/**
	 * 清除資源檔快取
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/reload_resource")
	public String reload_resource(Locale locale, Model model) {
		long startTime = System.nanoTime();
		resourceMessageService.clearCache();
		long endTime = System.nanoTime();
		model.addAttribute("processTime", (endTime - startTime) / 1e6);		
		return "/sys/reload_resource";
	}

	/**
	 * 變更個人單位資料
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/edit_org_profile")
	public String edit_org_profile(Locale locale, Model model) {
		return "/sys/edit_org_profile";
	}

	/**
	 * 子系統管理轉址 Subsystem
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/s01")
	public String s01(Locale locale, Model model) {
		return "/sys/s01";
	}

	/**
	 * Role管理轉址
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/s02")
	public String s02(Locale locale, Model model) {
		return "/sys/s02";
	}
	
	/**
	 * System_log管理轉址
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/s03")
	public String s03(Locale locale, Model model) {
		return "/sys/s03";
	}
	
	/**
	 * Member管理轉址
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/s05")
	public String s05(Locale locale, Model model) {
		return "/sys/s05";
	}
	
	/**
	 * 組織管理轉址 Org
	 * 
	 * @param locale
	 * 				Locale
	 * @param model
	 * 				Model
	 * @return 轉址
	 */
	@GetMapping(value = "/s06")
	public String s06(Locale locale, Model model) {
		return "/sys/s06";
	}

	/**
	 * 網站設定
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/s09")
	public String s09(Locale locale, Model model) {
		return "/sys/s09";
	}

	/**
	 * 表單資料維護轉址 Form
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/s11")
	public String s11(Locale locale, Model model) {
		return "/sys/s11";
	}	
	
	/**
	 * 角色權限資料維護轉址 RoleForm
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/s12")
	public String s12(Locale locale, Model model) {
		return "/sys/s12";
	}

	/**
	 * MemberSignApply管理轉址
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return 轉址
	 */
	@GetMapping(value = "/s15")
	public String s15(Locale locale, Model model) {
		return "/sys/s15";
	}
	
	@GetMapping(value = "/home")
	public String home(Locale locale, Model model) {
		return "/sys/home";
	}
//	@RequestMapping(value = "/", method = RequestMethod.GET)
//	public String index(Locale locale, Model model) {
//		return "/sys/index";
//	}
//
//	@RequestMapping(value = "/index", method = RequestMethod.GET)
//	public String Index(Locale locale, Model model) {
//		return "/sys/index";
//	}
}
