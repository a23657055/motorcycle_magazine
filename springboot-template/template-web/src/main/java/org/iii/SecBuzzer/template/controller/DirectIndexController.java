package org.iii.SecBuzzer.template.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/", produces = "application/json; charset=utf-8")
public class DirectIndexController extends BaseController {

	@GetMapping(value = "/test")
	public String test(Model model) {
		return "include/footer";
	}
	/**
	 * 首頁登入
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return Url
	 */
	@GetMapping(value = "/")
	public String Index(Model model) {
		return "index";
	}
	/**
	 * 首頁登入
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return Url
	 */
	@GetMapping(value = "/index")
	public String Index(Locale locale, Model model) {
		return "index";
	}
	
	/**
	 * Login
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return Url
	 */
	@GetMapping(value = "/login")
	public String Login(Locale locale, Model model, HttpServletRequest httpServletRequest, HttpSession httpSession) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			model.asMap().clear();
			return "redirect:/";
		} else {
			return "login";
		}
	}
	
	/**
	 * 忘記密碼
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return Url
	 */
	@GetMapping(value = "/forgot")
	public String ForgotCode(Locale locale, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return "redirect:/";
		}
		return "forgot";
	}
	
	/**
	 * 忘記密碼
	 * 
	 * @param locale
	 *            Locale
	 * @param request
	 *            HttpServletRequest
	 * @param model
	 *            Model
	 * @return Url
	 */
	@GetMapping(value = "/reset")
	public String ResetCode(Locale locale, HttpServletRequest request, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			return "redirect:/pub/";
		}
		return "reset";
	}

	/**
	 * 會員申請
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return Url
	 */
	@GetMapping(value = "/sign_up")
	public String SignUp(Locale locale, Model model) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if (!(auth instanceof AnonymousAuthenticationToken)) {
//			return "redirect:/pub/";
//		}
//		return "sign_up";
		
		return "redirect:/pub/";
	}

	/**
	 * 聯絡我們
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return Url
	 */
	@GetMapping(value = "/contact_us")
	public String ContactUs(Locale locale, Model model) {
		return "contact_us";
	}
	
	/**
	 * 權限不足
	 * 
	 * @param locale
	 *            Locale
	 * @param model
	 *            Model
	 * @return Url
	 */
	@GetMapping(value = "/forbidden")
	public String Forbidden(Locale locale, Model model) {
		throw new ForbiddenException();
	}

	@SuppressWarnings("serial")
	@ResponseStatus(HttpStatus.FORBIDDEN)
	private class ForbiddenException extends RuntimeException {
		
	}
}
