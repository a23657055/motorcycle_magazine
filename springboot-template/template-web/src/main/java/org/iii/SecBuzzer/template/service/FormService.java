
package org.iii.SecBuzzer.template.service;

import java.util.Date;
import java.util.List;

import org.iii.SecBuzzer.template.dao.FormDAO;
import org.iii.SecBuzzer.template.domain.Form;
import org.iii.SecBuzzer.template.domain.ViewFormName;
import org.iii.SecBuzzer.template.domain.ViewFormSubsystem;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 表單資料維護服務
 */
@Service
public class FormService {
	final static Logger logger = LoggerFactory.getLogger(FormService.class);

	@Autowired
	FormDAO formDAO;

	/**
	 * 取得Form資訊
	 * 
	 * @param subsystemId
	 * 			subsystemId
	 * @return Form資訊
	 */
	public List<Form> getBySubsystemId(long subsystemId) {
		return formDAO.getBySubsystemId(subsystemId);
	}

	/**
	 * 取得ViewFormSubsystem資訊
	 * 
	 * @return ViewFormSubsystem資訊
	 */
	public List<ViewFormSubsystem> getFormAndSubsystem() {
		return formDAO.getFormAndSubsystem();
	}

	/**
	 * 取得所有Form資料
	 * 
	 * @return Form資料
	 */
	public List<Form> getAll() {
		return formDAO.getAll();
	}

	/**
	 * Form資料是否存在
	 * 
	 * @param id
	 *            Form Id
	 * @return 是否存在
	 */
	public boolean isExist(Long id) {
		return formDAO.get(id) != null;
	}

	/**
	 * 取得Form資料
	 * 
	 * @param json
	 *            查詢條件
	 * @return Form資料
	 */
	public List<ViewFormName> getList(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return formDAO.getList(obj);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 取得Form資料筆數
	 * 
	 * @param json
	 *            查詢條件
	 * @return Form資料筆數
	 */
	public long getListSize(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return formDAO.getListSize(obj);
		} catch (Exception e) {
			return 0;
		}
	}


	/**
	 * 新增Form資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param json
	 * 			Form資料
	 * @return Form資料
	 */
	public Form insert(Long memberId, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			long subsystemId = obj.isNull("SubsystemId") == true ? 0 : obj.getLong("SubsystemId");
			String code = obj.isNull("Code") == true ? null : obj.getString("Code");
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			String controllerName = obj.isNull("ControllerName") == true ? null : obj.getString("ControllerName");
			String actionName = obj.isNull("ActionName") == true ? null : obj.getString("ActionName");
			boolean isExternalLink = obj.isNull("IsExternalLink") == true ? false : obj.getBoolean("IsExternalLink");
			boolean isEnable = obj.isNull("IsEnable") == true ? false : obj.getBoolean("IsEnable");
			boolean isShow = obj.isNull("IsShow") == true ? false : obj.getBoolean("IsShow");
			long sort = obj.isNull("Sort") == true ? 0 : obj.getLong("Sort");
			Date now = new Date();
			Form entity = new Form();
			entity.setSubsystemId(subsystemId);
			entity.setCode(code);
			entity.setName(name);
			entity.setControllerName(controllerName);
			entity.setActionName(actionName);
			entity.setIsExternalLink(isExternalLink);
			entity.setIsEnable(isEnable);
			entity.setIsShow(isShow);
			entity.setSort(sort);
			entity.setCreateId(memberId);
			entity.setCreateTime(now);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);

			entity = formDAO.insert(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 更新Form資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param json
	 * 			FormId
	 * @return Form資料
	 */
	public Form update(long memberId, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			long subsystemId = obj.isNull("SubsystemId") == true ? 0 : obj.getLong("SubsystemId");
			String code = obj.isNull("Code") == true ? null : obj.getString("Code");
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			String controllerName = obj.isNull("ControllerName") == true ? null : obj.getString("ControllerName");
			String actionName = obj.isNull("ActionName") == true ? null : obj.getString("ActionName");
			boolean isExternalLink = obj.isNull("IsExternalLink") == true ? false : obj.getBoolean("IsExternalLink");
			boolean isEnable = obj.isNull("IsEnable") == true ? false : obj.getBoolean("IsEnable");
			boolean isShow = obj.isNull("IsShow") == true ? false : obj.getBoolean("IsShow");
			long sort = obj.isNull("Sort") == true ? 0 : obj.getLong("Sort");
			Date now = new Date();

			Form entity = formDAO.get(id);
			entity.setSubsystemId(subsystemId);
			entity.setCode(code);
			entity.setName(name);
			entity.setControllerName(controllerName);
			entity.setActionName(actionName);
			entity.setIsExternalLink(isExternalLink);
			entity.setIsEnable(isEnable);
			entity.setIsShow(isShow);
			entity.setSort(sort);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);
			entity = formDAO.update(entity);

			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 刪除Form資料
	 * 
	 * @param id
	 *            FormId
	 * @return 是否刪除成功
	 */
	public boolean delete(Long id) {
		try {
			formDAO.delete(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 取得Form資料
	 * 
	 * @param id
	 *            Form資料Id
	 * @return Form資料
	 */
	public Form get(Long id) {
		return formDAO.get(id);
	}
	/**
	 * 更新Form資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param json
	 * 			FormId
	 * @return Form資料
	 */
	public Form update2(long memberId,long id, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			boolean isShow = obj.isNull("IsShow") == true ? false : obj.getBoolean("IsShow");
			Form entity = formDAO.get(id);
			Date now = new Date();
			entity.setIsShow(isShow);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);
			entity = formDAO.update(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



}