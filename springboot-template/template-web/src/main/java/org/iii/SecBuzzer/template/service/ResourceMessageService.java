package org.iii.SecBuzzer.template.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.iii.SecBuzzer.template.dao.ResourceMessageDAO;
import org.iii.SecBuzzer.template.domain.ResourceMessage;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ResourceMessageService {
	final static Logger logger = LoggerFactory.getLogger(ResourceMessageService.class);

	@Value("${language.supports}")
	private String languageSupports;

	@Autowired
	private ResourceMessageDAO resourceMessageDAO;
	
	public List<ResourceMessage> getAll() {
		return resourceMessageDAO.getAll();
	}

	/**
	 * 取得子系統資料
	 * 
	 * @param id
	 *            子系統資料Id
	 * @return 子系統資料
	 */
	//相當於 ResourceMessage getById(Long id)
	public ResourceMessage get(Long id) {
		return resourceMessageDAO.get(id);
	}
	
	//相當於 String getMessageValue(String messageKey)
	@Cacheable(value = "resourceMessage", key = "#locale+#messageKey")
	public String get(Locale locale, String messageKey) {
		String result = "";
		String language = locale.getLanguage() + "_" + locale.getCountry();
		List<ResourceMessage> resourceMessages = this.getAll();
		List<String> arrLanguageSupports = new ArrayList<String>(Arrays.asList(languageSupports.split(",")));
		if (!arrLanguageSupports.contains(language)) {
			ResourceMessage resourceMessage = resourceMessages.stream()
					.filter(p -> "zh_TW".equals(p.getLanguage()) && messageKey.equals(p.getMessageKey())).findFirst()
					.orElse(null);
			result = resourceMessage == null ? "" : StringUtils.defaultString(resourceMessage.getMessageValue());
		} else {
			ResourceMessage resourceMessage = resourceMessages.stream()
					.filter(p -> language.equals(p.getLanguage()) && messageKey.equals(p.getMessageKey())).findFirst()
					.orElse(null);
			result = resourceMessage == null ? "" : StringUtils.defaultString(resourceMessage.getMessageValue());
		}
		if (result == "") {
			ResourceMessage resourceMessage = resourceMessages.stream()
					.filter(p -> "default".equals(p.getLanguage()) && messageKey.equals(p.getMessageKey())).findFirst()
					.orElse(null);
			result = resourceMessage == null ? "" : StringUtils.defaultString(resourceMessage.getMessageValue());
		}
		return result;
	}
	
	/**
	 * 取得ResourceMessage資料
	 * 
	 * @param json
	 *            查詢條件
	 * @return ResourceMessage資料
	 */
	public List<ResourceMessage> getList(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return resourceMessageDAO.getList(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 取得ResourceMessage資料筆數
	 * 
	 * @param json
	 *            查詢條件
	 * @return ResourceMessage資料筆數
	 */
	public long getListSize(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return resourceMessageDAO.getListSize(obj);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 新增ResourceMessage資料
	 * 
	 * @param json
	 * 			user資料
	 * @return ResourceMessage子系統資料
	 */
	public ResourceMessage insert(String json, Long createId) {
		try {
			JSONObject obj = new JSONObject(json);
			String category = obj.isNull("category") == true ? null : obj.getString("category");
			String title = obj.isNull("title") == true ? null : obj.getString("title");
			String contentHtml = obj.isNull("contentHtml") == true ? null : obj.getString("contentHtml");

			ResourceMessage entity = new ResourceMessage();
			entity.setLanguage(category);
			entity.setMessageKey(title);
			entity.setMessageValue(contentHtml);
			entity.setCreateId(createId);
			entity.setModifyId(createId);
			entity = resourceMessageDAO.insert(entity);
			
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * 更新ResourceMessage資料
	 * 
	 * @param json
	 * 			ResourceMessage資料
	 * @return ResourceMessage子系統資料
	 */
	public ResourceMessage update(String json, Long modifyId) {
		try {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			String language = obj.isNull("Language") == true ? null : obj.getString("Language");
			String messageKey = obj.isNull("MessageKey") == true ? null : obj.getString("MessageKey");
			String messageValue = obj.isNull("MessageValue") == true ? null : obj.getString("MessageValue");
			
			ResourceMessage entity = this.get(id);
			entity.setLanguage(language);
			entity.setMessageKey(messageKey);
			entity.setMessageValue(messageValue);
			entity.setModifyId(modifyId);
			
			entity = resourceMessageDAO.update(entity);
			
			Locale locale = this.getLocale(language);
			this.clearCacheForKey(locale, messageKey);
			
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 刪除ResourceMessage資料
	 * 
	 * @param id
	 *           
	 * @return 是否刪除成功
	 */
	public boolean delete(Long id) {
		try {
			ResourceMessage entity = resourceMessageDAO.get(id);
			Locale locale = this.getLocale(entity.getLanguage());
			this.clearCacheForKey(locale, entity.getMessageKey());
			
			resourceMessageDAO.delete(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * ResourceMessage資料是否存在
	 * 
	 * @param id
	 *            user資料Id
	 * @return 是否存在
	 */
	public boolean isExist(Long id) {
		return resourceMessageDAO.get(id) != null;
	}

	/**
	 * 網站設定資料編號是否存在
	 * 
	 * @param messageKey
	 *            網站設定key
	 * @return 是否存在
	 */
	public boolean isMessageKeyExist(String messageKey, String language) {
		long count = resourceMessageDAO.countByMessageKeyAndLanguage(messageKey, language);
		return count != 0;	
	}
	
	//CacheManager Code begin	
	@Autowired
	private CacheManager cacheManager;
	
	/**
	 * 清除所有的ResourceMessage的Cache資料
	 */
	public void clearCache() {
		for (String cacheName : cacheManager.getCacheNames()) {
			cacheManager.getCache(cacheName).clear();
		}
	}

	/**
	 * 清除特定messageKey的ResourceMessage的Cache資料
	 * 
	 * @param locale
	 *            Locale資料
	 * @param messageKey
	 *            messageKey資料
	 */
	@CacheEvict(value = "resourceMessage", key = "#locale+#messageKey")
	public void clearCacheForKey(Locale locale, String messageKey) {
		return;
	}
	
	private Locale getLocale(String language) {
		if(language!=null && language.equals("en_US")) {
			return new Locale("en", "US");
		} else {
			return new Locale("zh", "TW");
		}
	}
	//CacheManager Code End
}
