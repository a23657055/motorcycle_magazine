package org.iii.SecBuzzer.template;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
//<security:global-method-security secured-annotations="enabled" />
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers(
					"/css/**",
					"/images/**",
					"/img/**",
					"/js/**",
					"/plugins/**")
			.permitAll()
			.and().authorizeRequests()
			.antMatchers(
					"/",
					"/*",
					"/public/api/**")
			.anonymous()
			.and().authorizeRequests()
			.anyRequest()
			.authenticated();
		
//		<security:session-management invalid-session-url="/" />		
//
//		http.sessionManagement().invalidSessionUrl("/");
		
//		<security:headers>
//			<security:frame-options policy="SAMEORIGIN"></security:frame-options>
//		</security:headers>
//
//		http.headers().frameOptions().sameOrigin();
		
		http.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
			.logoutSuccessUrl("/")
			.invalidateHttpSession(true)
			.deleteCookies("JSESSIONID", "SESSION");

		http.csrf().ignoringAntMatchers(
				"/public/api/login",
				"/logout"
		);
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
//		<security:http pattern="/open/api/**" security="none"></security:http>
//		
//		web.ignoring().antMatchers("/open/api/**");
//		web.ignoring().antMatchers("/sys/api/**");
//		web.ignoring().antMatchers("**");
	}
}
