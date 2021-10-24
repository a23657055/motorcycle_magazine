package org.iii.SecBuzzer.template.service;

import java.util.Date;
import java.util.List;

import org.iii.SecBuzzer.template.dao.SubsystemDAO;
import org.iii.SecBuzzer.template.domain.Subsystem;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 子系統資料維護服務
 */
@Service
public class SubsystemService {
	@Autowired
	SubsystemDAO subsystemDAO;

	/**
	 * 取得所有子系統資料
	 * 
	 * @return 子系統資料
	 */
	public List<Subsystem> getAll() {
		return subsystemDAO.getAll();
	}

	/**
	 * 取得子系統資料
	 * 
	 * @param json
	 *            查詢條件
	 * @return 子系統資料
	 */
	public List<Subsystem> getList(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return subsystemDAO.getList(obj);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 取得子系統資料筆數
	 * 
	 * @param json
	 *            查詢條件
	 * @return 子系統資料筆數
	 */
	public long getListSize(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return subsystemDAO.getListSize(obj);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 新增子系統資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param json
	 * 			子系統資料
	 * @return 子系統資料
	 */
	public Subsystem insert(Long memberId, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			String code = obj.isNull("Code") == true ? null : obj.getString("Code");
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			String iconStyle = obj.isNull("IconStyle") == true ? null : obj.getString("IconStyle");
			boolean isEnable = obj.isNull("IsEnable") == true ? false : obj.getBoolean("IsEnable");
			boolean isShow = obj.isNull("IsShow") == true ? false : obj.getBoolean("IsShow");
			long sort = obj.isNull("Sort") == true ? 0 : obj.getLong("Sort");
			Date now = new Date();
			Subsystem entity = new Subsystem();
			entity.setCode(code);
			entity.setName(name);
			entity.setIconStyle(iconStyle);
			entity.setIsEnable(isEnable);
			entity.setIsShow(isShow);
			entity.setSort(sort);
			entity.setCreateId(memberId);
			entity.setCreateTime(now);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);

			entity = subsystemDAO.insert(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 更新子系統資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param json
	 * 			子系統資料
	 * @return 子系統資料
	 */
	public Subsystem update(long memberId, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			String code = obj.isNull("Code") == true ? null : obj.getString("Code");
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			String iconStyle = obj.isNull("IconStyle") == true ? null : obj.getString("IconStyle");
			boolean isEnable = obj.isNull("IsEnable") == true ? false : obj.getBoolean("IsEnable");
			boolean isShow = obj.isNull("IsShow") == true ? false : obj.getBoolean("IsShow");
			long sort = obj.isNull("Sort") == true ? 0 : obj.getLong("Sort");
			Date now = new Date();
			Subsystem entity = subsystemDAO.get(id);
			entity.setCode(code);
			entity.setName(name);
			entity.setIconStyle(iconStyle);
			entity.setIsEnable(isEnable);
			entity.setIsShow(isShow);
			entity.setSort(sort);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);

			entity = subsystemDAO.update(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 刪除子系統資料
	 * 
	 * @param id
	 *            子系統Id
	 * @return 是否刪除成功
	 */
	public boolean delete(Long id) {
		try {
			subsystemDAO.delete(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 取得子系統資料
	 * 
	 * @param id
	 *            子系統資料Id
	 * @return 子系統資料
	 */
	public Subsystem getById(Long id) {
		return subsystemDAO.get(id);
	}
	
	/**
	 * 子系統資料是否存在
	 * 
	 * @param id
	 *            子系統資料Id
	 * @return 是否存在
	 */
	public boolean isExist(Long id) {
		return subsystemDAO.get(id) != null;
	}
	
	/**
	 * 子系統資料名稱
	 * 
	 * @param name
	 *            子系統名稱
	 * @return 子系統名稱資料
	 */
	public Subsystem findByName(String name) {
		return subsystemDAO.getByName(name);
	}
	
	/**
	 * 子系統資料名稱是否存在
	 * 
	 * @param name
	 *            名稱
	 * @return 是否存在
	 */
	public boolean isNameExist(String name) {
		return findByName(name) != null;
	}
	
	/**
	 * 子系統資料名稱
	 * 
	 * @param code
	 *            子系統編號
	 * @return 子系統名稱資料
	 */
	public Subsystem findByCode(String code) {
		return subsystemDAO.getByCode(code);
	}
	
	/**
	 * 子系統資料編號是否存在
	 * 
	 * @param code
	 *            編號
	 * @return 是否存在
	 */
	public boolean isCodeExist(String code) {
		return findByCode(code) != null;
	}

}
