package org.iii.SecBuzzer.template.controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/pub", produces = "application/json; charset=utf-8")
public class DirectPubController extends BaseController {
	/**
	 * Home Index
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return Url
	 */
	@GetMapping(value = "/")
	public String index(Locale locale, Model model) {
		return "/pub/index";
	}

	/**
	 * Home Index
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return Url
	 */
	@GetMapping(value = "/index")
	public String Index(Locale locale, Model model) {
		return "/pub/index";
	}

}
