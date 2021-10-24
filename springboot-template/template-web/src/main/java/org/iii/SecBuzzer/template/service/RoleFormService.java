package org.iii.SecBuzzer.template.service;

import java.util.Date;
import java.util.List;

import org.iii.SecBuzzer.template.dao.RoleFormDAO;
import org.iii.SecBuzzer.template.domain.RoleForm;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;;
/**
 * 角色權限資料維護服務
 */
@Service
public class RoleFormService {
	@Autowired
	private RoleFormDAO roleFormDAO;

	/**
	 * 取得RoleForm資料
	 * @param roleId
	 *            查詢條件
	 * @return RoleForm資料
	 */
	public List<RoleForm> getList(Long roleId) {
		return roleFormDAO.getList(roleId);
	}
	
	/**
	 * 新增或修改RoleForm資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param json
	 * 			RoleForm資料
	 * @return Success
	 */
	public String insertOrUpdate(Long memberId, String json) {
		try {
			JSONObject obj = new JSONObject(json);
			long roleId = obj.isNull("RoleId") == true ? 0 : obj.getLong("RoleId");
			
			JSONArray array = obj.getJSONArray("RoleForm");
			for (int num = 0 ; num < array.length() ; num++) {
				 JSONObject result = array.getJSONObject(num);
				 long id = result.isNull("Id") == true ? 0 : result.getLong("Id");
				 long formId = result.isNull("FormId") == true ? 0 : result.getLong("FormId");
				 Boolean actionCreate = result.isNull("ActionCreate") == true ? false : result.getBoolean("ActionCreate");
				 Boolean actionUpdate = result.isNull("ActionUpdate") == true ? false : result.getBoolean("ActionUpdate");
				 Boolean actionDelete = result.isNull("ActionDelete") == true ? false : result.getBoolean("ActionDelete");
				 Boolean actionRead = result.isNull("ActionRead") == true ? false : result.getBoolean("ActionRead");
				 Boolean actionSign = result.isNull("ActionSign") == true ? false : result.getBoolean("ActionSign");
				 Boolean insertOrUpdate = result.isNull("InsertOrUpdate") == true ? false : result.getBoolean("InsertOrUpdate");
				 
				 Date now = new Date();
				 if (id ==0 && insertOrUpdate==true) {
					 	RoleForm entity = new RoleForm();
					 	entity.setRoleId(roleId);
						entity.setFormId(formId);
						entity.setActionCreate(actionCreate);
						entity.setActionUpdate(actionUpdate);
						entity.setActionDelete(actionDelete);
						entity.setActionRead(actionRead);
						entity.setActionSign(actionSign);
						entity.setCreateId(memberId);
						entity.setCreateTime(now);
						entity.setModifyId(memberId);
						entity.setModifyTime(now);
						
						entity = roleFormDAO.insert(entity);
				 }
				 if (id !=0 && insertOrUpdate==true) {
					 	RoleForm entity = roleFormDAO.get(id);					 	
						entity.setActionCreate(actionCreate);
						entity.setActionUpdate(actionUpdate);
						entity.setActionDelete(actionDelete);
						entity.setActionRead(actionRead);
						entity.setActionSign(actionSign);
						entity.setModifyId(memberId);
						entity.setModifyTime(now);
						
						entity = roleFormDAO.update(entity);
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "Success";
	}
}
