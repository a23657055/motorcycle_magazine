package org.iii.SecBuzzer.template.service;

import java.util.List;

import org.iii.SecBuzzer.template.dao.FormDAO;
import org.iii.SecBuzzer.template.dao.SubsystemDAO;
import org.iii.SecBuzzer.template.dao.ViewMenuLimitDAO;
import org.iii.SecBuzzer.template.domain.Form;
import org.iii.SecBuzzer.template.domain.Subsystem;
import org.iii.SecBuzzer.template.domain.ViewMenuLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 選單服務
 */
@Service
public class MenuService {
	@Autowired
	private SubsystemDAO subsystemDAO;

	@Autowired
	private FormDAO formDAO;

	@Autowired
	private ViewMenuLimitDAO viewMenuLimitDAO;

	/**
	 * 取得使用者選單列表
	 * 
	 * @param memberId
	 *            使用者Id
	 * @return 使用者可見選單
	 */
	public List<ViewMenuLimit> getMenu(long memberId) {
		try {
			return viewMenuLimitDAO.getMenu(memberId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 取得使用者表單權限
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param controllerName
	 *            控制器名稱
	 * @param actionName
	 *            動作名稱
	 * @return 使用者選單
	 */
	public ViewMenuLimit getAction(long memberId, String controllerName, String actionName) {
		try {
			return viewMenuLimitDAO.getAction(memberId, controllerName, actionName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 取得使用者讀取權限
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param controllerName
	 *            控制器名稱
	 * @param actionName
	 *            動作名稱
	 * @return 是否有讀取權限
	 */
	public boolean getReadPermission(long memberId, String controllerName, String actionName) {
		boolean result = false;
		ViewMenuLimit viewMenuLimit = getAction(memberId, controllerName, actionName);
		if (viewMenuLimit != null) {
			result = viewMenuLimit.getActionRead();
		}
		return result;
	}

	/**
	 * 取得使用者簽核權限
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param controllerName
	 *            控制器名稱
	 * @param actionName
	 *            動作名稱
	 * @return 是否有簽核權限
	 */
	public boolean getSignPermission(long memberId, String controllerName, String actionName) {
		boolean result = false;
		ViewMenuLimit viewMenuLimit = getAction(memberId, controllerName, actionName);
		if (viewMenuLimit != null) {
			result = viewMenuLimit.getActionSign();
		}
		return result;
	}

	/**
	 * 取得使用者刪除權限
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param controllerName
	 *            控制器名稱
	 * @param actionName
	 *            動作名稱
	 * @return 是否有刪除權限
	 */
	public boolean getDeletePermission(long memberId, String controllerName, String actionName) {
		boolean result = false;
		ViewMenuLimit viewMenuLimit = getAction(memberId, controllerName, actionName);
		if (viewMenuLimit != null) {
			result = viewMenuLimit.getActionDelete();
		}
		return result;
	}

	/**
	 * 取得使用者更新權限
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param controllerName
	 *            控制器名稱
	 * @param actionName
	 *            動作名稱
	 * @return 是否有更新權限
	 */
	public boolean getUpdatePermission(long memberId, String controllerName, String actionName) {
		boolean result = false;
		ViewMenuLimit viewMenuLimit = getAction(memberId, controllerName, actionName);
		if (viewMenuLimit != null) {
			result = viewMenuLimit.getActionUpdate();
		}
		return result;
	}

	/**
	 * 取得使用者新增權限
	 * 
	 * @param memberId
	 *            使用者Id
	 * @param controllerName
	 *            控制器名稱
	 * @param actionName
	 *            動作名稱
	 * @return 是否有新增權限
	 */
	public boolean getCreatePermission(long memberId, String controllerName, String actionName) {
		boolean result = false;
		ViewMenuLimit viewMenuLimit = getAction(memberId, controllerName, actionName);
		if (viewMenuLimit != null) {
			result = viewMenuLimit.getActionCreate();
		}
		return result;
	}

	/**
	 * 取得選單名稱
	 * 
	 * @param controllerName
	 *            控制器名稱
	 * @param actionName
	 *            動作名稱
	 * @return 選單名稱
	 */
	public String getFormName(String controllerName, String actionName) {
		try {
			Form form = formDAO.getByCAName(controllerName, actionName);
			if (form != null) {
				return form.getName();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 取得子系統名稱
	 * 
	 * @param controllerName
	 *            控制器名稱
	 * @return 子系統名稱
	 */
	public String getSubsystemName(String controllerName) {
		try {
			Subsystem subsystem = subsystemDAO.getByCode(controllerName);
			if (subsystem != null) {
				return subsystem.getName();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
