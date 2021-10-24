package org.iii.SecBuzzer.template.service;

import java.util.List;

import javax.transaction.Transactional;

import org.iii.SecBuzzer.template.dao.CommonSettingDAO;
import org.iii.SecBuzzer.template.domain.CommonSetting;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CommonSettingService {
	
	@Autowired
	private CommonSettingDAO commonSettingDAO;
	
	public List<CommonSetting> getSetting(String json){
		JSONObject obj = new JSONObject(json);
		return commonSettingDAO.getSetting(obj);
	}
}
