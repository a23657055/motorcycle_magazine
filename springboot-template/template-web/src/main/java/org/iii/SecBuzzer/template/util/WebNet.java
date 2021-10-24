package org.iii.SecBuzzer.template.util;


import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.net.InternetDomainName;

/**
 * 網路工具
 */
public class WebNet {
	private static final String[] IP_HEADER_CANDIDATES = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED",
			"HTTP_VIA", "REMOTE_ADDR"};

	public static String getIpAddr(HttpServletRequest request) {
		for (String header : IP_HEADER_CANDIDATES) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}

	public static boolean isAllowIp(String ips, String ip) {
		boolean result = false;
		if (ips == null || ips.isEmpty() || ip == null || ip.isEmpty()) {
			result = false;
		} else if (ips.equals("*")) {
			result = true;
		} else {
			List<String> allowIps = Arrays.asList(ips.split(","));
			if (allowIps.contains(ip)) {
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, String> getHeadersInfo(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		return map;
	}
	
	public static String getBaseUrl(HttpServletRequest request) {
	    String scheme = request.getScheme();
	    String host = request.getServerName();
	    int port = request.getServerPort();
	    String contextPath = request.getContextPath();

	    String baseUrl = scheme + "://" + host + ((("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443)) ? "" : ":" + port) + contextPath;
	    return baseUrl;
	}
	
	public static Boolean securitySSRFUrlCheck(String url, String[] urlwhitelist) {
	    try {
	        URL u = new URL(url);
	        
	        if (!u.getProtocol().startsWith("http") && !u.getProtocol().startsWith("https")) {
	            return  false;
	        }
	        String host = u.getHost().toLowerCase();
	        String rootDomain = InternetDomainName.from(host).topPrivateDomain().toString();
	        
	        for (String whiteurl: urlwhitelist){
	            if (rootDomain.equals(whiteurl)) {
	                return true;
	            }
	        }
	        return false;

	    } catch (Exception e) {
	        return false;
	    }
	}
}
