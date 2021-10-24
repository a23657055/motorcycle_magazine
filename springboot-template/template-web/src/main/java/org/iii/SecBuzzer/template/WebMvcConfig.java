package org.iii.SecBuzzer.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iii.SecBuzzer.template.web.BaseInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.WebUtils;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
    private BaseInterceptor baseInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor)
        	.addPathPatterns("/public/api/login", "/*", "/pub/**", "/sys/**");
    }
	
	@Bean
	public LocaleResolver localeResolver() {
		return new NativeLocaleResolver();
	}

	protected static class NativeLocaleResolver implements LocaleResolver {
		@Value("${language.supports}")
		private String languageSupports;

		@Override
		public Locale resolveLocale(HttpServletRequest request) {
			Cookie language = WebUtils.getCookie(request, "language");
			Locale locale = new Locale("zh", "TW");
			if (!StringUtils.isEmpty(language) && !StringUtils.isEmpty(language.getValue())) {
				String[] splitLanguage = language.getValue().split("_");
				if (splitLanguage.length == 2) {
					List<String> arrLanguageSupports = new ArrayList<String>(
							Arrays.asList(languageSupports.split(",")));
					if (arrLanguageSupports.contains(language.getValue())) {
						locale = new Locale(splitLanguage[0], splitLanguage[1]);
					}
				} else {
					switch (language.getValue()) {
					case "zh":
						locale = new Locale("zh", "TW");
						break;
					case "en":
						locale = new Locale("en", "US");
						break;
					default:
					}
				}
			}
			return locale;
		}

		@Override
		public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

		}
	}
}