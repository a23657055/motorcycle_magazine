package org.iii.SecBuzzer.template.service;

import java.util.Date;
import java.util.List;

import org.iii.SecBuzzer.template.dao.OrgDAO;
import org.iii.SecBuzzer.template.dao.OrgVariable.AuthType;
import org.iii.SecBuzzer.template.dao.OrgVariable.OrgType;
import org.iii.SecBuzzer.template.domain.Org;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 單位基本資料管理服務
 */
@Service
public class OrgService {
	@Autowired
	private OrgDAO orgDAO;

	/**
	 * 取得所有組織資料
	 * 
	 * @return 組織資料
	 */
	public List<Org> getAll() {
		return orgDAO.getAll();
	}

	/**
	 * 取得組織資料
	 * 
	 * @param orgType
	 * 			orgType
	 * @return 組織資料
	 */
	public List<Org> getByOrgType(String orgType) {
		return orgDAO.getByOrgType(orgType);
	}

	/**
	 * 取得組織資料
	 * 
	 * @param json
	 *            查詢條件
	 * @return 組織資料
	 */
	public List<Org> getList(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return orgDAO.getList(obj);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 取得組織資料
	 * 
	 * @param isEnable
	 *            是否有效
	 * @param orgType
	 *            組織類別
	 * @return 組織資料
	 */
	public List<Org> getList(Boolean isEnable, OrgType orgType) {
		try {
			return orgDAO.getList(isEnable, orgType.getValue());
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 取得組織資料
	 * 
	 * @param isEnable
	 * 			是否有效
	 * @param orgType
	 * 			組織類別
	 * @param authType
	 * 			AuthType
	 * @return 組織資料
	 */
	public List<Org> getList(Boolean isEnable, OrgType orgType, AuthType authType) {
		try {
			return orgDAO.getList(isEnable, orgType.getValue(), authType.getValue());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 取得組織資料
	 * 
	 * @param isEnable
	 *            是否有效
	 * @param orgType
	 *            組織類別
	 * @param queryString
	 *            查詢字串
	 * @param perPage
	 *            每頁幾筆
	 * @return 組織資料
	 */
	public List<Org> getList(Boolean isEnable, OrgType orgType, String queryString, int perPage) {
		try {
			return orgDAO.getList(isEnable, orgType.getValue(), queryString, perPage);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 取得組織資料筆數
	 * 
	 * @param json
	 *            查詢條件
	 * @return 組織資料筆數
	 */
	public long getListSize(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return orgDAO.getListSize(obj);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 新增組織資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param json
	 * 			組織資料
	 * @return 組織資料
	 */
	public Org insert(Long memberId, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			String code = obj.isNull("Code") == true ? null : obj.getString("Code");
			String orgType = obj.isNull("OrgType") == true ? null : obj.getString("OrgType");
			String authType = obj.isNull("AuthType") == true ? null : obj.getString("AuthType");
			String tel = obj.isNull("Tel") == true ? null : obj.getString("Tel");
			String fax = obj.isNull("Fax") == true ? null : obj.getString("Fax");
			String city = obj.isNull("City") == true ? null : obj.getString("City");
			String town = obj.isNull("Town") == true ? null : obj.getString("Town");
			String address = obj.isNull("Address") == true ? null : obj.getString("Address");
			boolean isEnable = obj.isNull("IsEnable") == true ? false : obj.getBoolean("IsEnable");

			Date now = new Date();
			Org entity = new Org();
			entity.setName(name);
			entity.setCode(code);
			entity.setOrgType(orgType);
			entity.setAuthType(authType);
			entity.setTel(tel);
			entity.setFax(fax);
			entity.setCity(city);
			entity.setTown(town);
			entity.setAddress(address);
			entity.setIsEnable(isEnable);
			entity.setCreateId(memberId);
			entity.setCreateTime(now);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);

			entity = orgDAO.insert(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 更新組織資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param json
	 * 			組織資料
	 * @return 組織資料
	 */
	public Org update(long memberId, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			String code = obj.isNull("Code") == true ? null : obj.getString("Code");
			String orgType = obj.isNull("OrgType") == true ? null : obj.getString("OrgType");
			String authType = obj.isNull("AuthType") == true ? null : obj.getString("AuthType");
			String tel = obj.isNull("Tel") == true ? null : obj.getString("Tel");
			String fax = obj.isNull("Fax") == true ? null : obj.getString("Fax");
			String city = obj.isNull("City") == true ? null : obj.getString("City");
			String town = obj.isNull("Town") == true ? null : obj.getString("Town");
			String address = obj.isNull("Address") == true ? null : obj.getString("Address");
			boolean isEnable = obj.isNull("IsEnable") == true ? false : obj.getBoolean("IsEnable");

			Date now = new Date();
			Org entity = orgDAO.get(id);
			entity.setName(name);
			entity.setCode(code);
			entity.setOrgType(orgType);
			entity.setAuthType(authType);
			entity.setTel(tel);
			entity.setFax(fax);
			entity.setCity(city);
			entity.setTown(town);
			entity.setAddress(address);
			entity.setIsEnable(isEnable);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);

			entity = orgDAO.update(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 更新組織資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param json
	 * 			組織資料
	 * @param baseOrgId
	 * 			組織Id
	 * @return 組織資料
	 */
	public Org updateProfile(long memberId, String json,long baseOrgId) {
		try {
			JSONObject obj = new JSONObject(json);
			//long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
			String name = obj.isNull("Name") == true ? null : obj.getString("Name");
			String code = obj.isNull("Code") == true ? null : obj.getString("Code");
			String orgType = obj.isNull("OrgType") == true ? null : obj.getString("OrgType");
			String authType = obj.isNull("AuthType") == true ? null : obj.getString("AuthType");
			String tel = obj.isNull("Tel") == true ? null : obj.getString("Tel");
			String fax = obj.isNull("Fax") == true ? null : obj.getString("Fax");
			String city = obj.isNull("City") == true ? null : obj.getString("City");
			String town = obj.isNull("Town") == true ? null : obj.getString("Town");
			String address = obj.isNull("Address") == true ? null : obj.getString("Address");
			
			Date now = new Date();
			Org entity = orgDAO.get(baseOrgId);
			entity.setName(name);
			entity.setCode(code);
			entity.setOrgType(orgType);
			entity.setAuthType(authType);
			entity.setTel(tel);
			entity.setFax(fax);
			entity.setCity(city);
			entity.setTown(town);
			entity.setAddress(address);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);

			entity = orgDAO.update(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 刪除組織資料
	 * 
	 * @param id
	 *            組織Id
	 * @return 是否刪除成功
	 */
	public boolean delete(Long id) {
		try {
			orgDAO.delete(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 取得組織資料
	 * 
	 * @param id
	 *            組織資料Id
	 * @return 組織資料
	 */
	public Org getDataById(Long id) {
		return orgDAO.get(id);
	}

	/**
	 * 組織資料是否存在
	 * 
	 * @param id
	 *            組織資料Id
	 * @return 是否存在
	 */
	public boolean isExist(Long id) {
		return orgDAO.get(id) != null;
	}

	/**
	 * 組織資料名稱是否存在
	 * 
	 * @param name
	 *            名稱
	 * @return 是否存在
	 */
	public Org findByName(String name) {
		List<Org> entitys = orgDAO.getAll();
		for (Org entity : entitys) {
			if (entity.getName().equalsIgnoreCase(name)) {
				return entity;
			}
		}
		return null;
	}

	/**
	 * isNameExist
	 * 
	 * @param name
	 * 			name
	 * @return isNameExist
	 */
	public boolean isNameExist(String name) {
		return findByName(name) != null;
	}

	/**
	 * 組織資料代碼是否存在
	 * 
	 * @param code
	 *            代碼
	 * @return 是否存在
	 */
	public Org findByCode(String code) {
		List<Org> entitys = orgDAO.getAll();
		for (Org entity : entitys) {
			if (entity.getCode().equalsIgnoreCase(code)) {
				return entity;
			}
		}
		return null;
	}

	/**
	 * 組織資料城市是否存在
	 * 
	 * @param city
	 * 			城市
	 * @param orgType
	 * 			組織類別
	 * @param authType
	 * 			AuthType
	 * @return 是否存在
	 */
	public Org findByCity(String city, OrgType orgType, AuthType authType) {
		List<Org> entitys = orgDAO.getAll();
		for (Org entity : entitys) {
			if (entity.getCity().equals(city) && entity.getAuthType().equals(authType.getValue()) && entity.getOrgType().equals(orgType.getValue())) {
				return entity;
			}
		}
		return null;
	}
}
