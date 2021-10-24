package org.iii.SecBuzzer.template.service;

import java.util.Date;
import java.util.List;

import org.iii.SecBuzzer.template.dao.SystemLogDAO;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.domain.SystemLog;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 操作記錄服務
 */
@Service
public class SystemLogService {
	final static Logger logger = LoggerFactory.getLogger(SystemLogService.class);

	@Autowired
	private SystemLogDAO systemLogDAO;
	
	/**
	 * 新增SystemLog資料
	 * 
	 * @param controllerName
	 * 			控制器名稱
	 * @param actionName
	 * 			方法名稱
	 * @param inputValue
	 * 			inputValue
	 * @param action
	 * 			SystemLogVariable.Action
	 * @param status
	 * 			SystemLogVariable.Status
	 * @param ipAddress
	 * 			ipAddress
	 * @param memberAccount
	 * 			memberAccount
	 */
	public void insert(String controllerName, String actionName, String inputValue, SystemLogVariable.Action action, SystemLogVariable.Status status, String ipAddress, String memberAccount) {
		try {
			Date now = new Date();
			SystemLog systemLog = new SystemLog();
			systemLog.setAppName(controllerName);
			systemLog.setFuncName(actionName);
			systemLog.setInputValue(inputValue);
			systemLog.setActionName(action.toString());
			systemLog.setStatus(status.toString());
			systemLog.setIp(ipAddress);
			systemLog.setCreateAccount(memberAccount);
			systemLog.setCreateTime(now);
			systemLog = systemLogDAO.insert(systemLog);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	/**
	 * 取得SystemLog資料
	 * 
	 * @param json
	 *            查詢條件
	 * @return SystemLog資料
	 */
	public List<SystemLog> getList(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return systemLogDAO.getList(obj);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 取得SystemLog資料筆數
	 * 
	 * @param json
	 *            查詢條件
	 * @return SystemLog資料筆數
	 */
	public long getListSize(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return systemLogDAO.getListSize(obj);
		} catch (Exception e) {
			return 0;
		}
	}
}