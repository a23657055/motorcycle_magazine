package org.iii.SecBuzzer.template.service;

import java.util.Date;
import java.util.List;

import org.iii.SecBuzzer.template.dao.MemberRoleDAO;
import org.iii.SecBuzzer.template.dao.MemberRoleVariable;
import org.iii.SecBuzzer.template.domain.MemberRole;
import org.iii.SecBuzzer.template.domain.SpMemberRoleName;
import org.iii.SecBuzzer.template.domain.ViewMemberRoleMember;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 會員權限服務
 */
@Service
public class MemberRoleService {
	final static Logger logger = LoggerFactory.getLogger(MemberRoleService.class);

	@Autowired
	private MemberRoleDAO memberRoleDAO;

	/**
	 * 取得所有會員權限資料
	 * 
	 * @return 會員權限資料
	 */
	public List<MemberRole> getAll() {
		return memberRoleDAO.getAll();
	}

	/**
	 * 取得會員權限資料
	 * 
	 * @param json
	 *            查詢條件
	 * @return 會員權限資料join org
	 */

	public List<SpMemberRoleName> getMemberRoleList(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return memberRoleDAO.getMemberRoleList(obj);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * 新增/更新會員權限資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param member_Id
	 * 			member_Id
	 * @param roleId
	 * 			角色Id
	 * @return 會員權限資料
	 */
	public MemberRole insert(long memberId, Long member_Id, Long roleId) {
		try {
			MemberRole entity = new MemberRole();
			Date now = new Date();
			entity.setMemberId(member_Id);
			entity.setRoleId(roleId);
			entity.setIsEnable(true);
			entity.setCreateId(memberId);
			entity.setCreateTime(now);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);
			entity = memberRoleDAO.insert(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 新增/更新會員權限資料
	 * 
	 * @param memberId
	 * 			使用者Id
	 * @param id
	 * 			id
	 * @param member_Id
	 * 			使用者Id
	 * @param roleId
	 * 			角色Id
	 * @param flag
	 * 			IsEnable
	 */
	public void insertOrDelete(long memberId, Long id, Long member_Id, Long roleId, boolean flag) {
		Date now = new Date();

		if (flag == true && id == 0) {
			MemberRole entity = new MemberRole();
//			entity.setId(id);
			entity.setMemberId(member_Id);
			entity.setRoleId(roleId);
			entity.setIsEnable(flag);
			entity.setCreateId(memberId);
			entity.setCreateTime(now);
			entity.setModifyId(memberId);
			entity.setModifyTime(now);
			entity = memberRoleDAO.insert(entity);
		} else if (flag == false && id != 0) {
			this.delete(id);
		}
	}

	/**
	 * 刪除會員權限資料
	 * 
	 * @param id
	 *            會員權限Id
	 * @return 是否刪除成功
	 */
	public boolean delete(Long id) {
		try {
			memberRoleDAO.delete(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 取得會員權限資料
	 * 
	 * @param id
	 *            會員權限資料Id
	 * @return 會員權限資料
	 */
	public MemberRole getById(Long id) {
		return memberRoleDAO.get(id);
	}
	/**
	 * 會員權限資料是否存在
	 * 
	 * @param id
	 *            會員權限資料Id
	 * @return 是否存在
	 */
	public boolean isExist(Long id) {
		return memberRoleDAO.get(id) != null;
	}
	/**
	 * 會員權限資料名稱
	 * 
	 * @param roleId
	 *            會員權限名稱
	 * @return 會員權限名稱資料
	 */
	public List<ViewMemberRoleMember> findByRoleId(long roleId) {
		return memberRoleDAO.getByRoleId(roleId);
	}
	
	/**
	 * 會員權限資料名稱
	 * 
	 * @param memberId
	 *           使用者Id
	 * @return 會員權限名稱資料
	 */
	public List<MemberRole> getByMemberId(long memberId) {
		return memberRoleDAO.getByMemberId(memberId);
	}
	
	public MemberRoleVariable getMemberRoleVariable(Long memberId) {
		MemberRoleVariable memberRoleVariable = new MemberRoleVariable();
		List<MemberRole> memberRoles = memberRoleDAO.getByMemberId(memberId);
		for (MemberRole memberRole : memberRoles) {
			Long roleId = memberRole.getRoleId();
			if (roleId == (long) 1) {
				memberRoleVariable.IsAdmin = true;
			} else if (roleId == (long) 7) {
				memberRoleVariable.IsApplySign = true;
			} else if (roleId == (long) 8) {
				memberRoleVariable.IsApplyContact = true;
			} else if (roleId == (long) 9) {
				memberRoleVariable.IsApplyAdmin = true;
			} else if (roleId == (long) 10) {
				memberRoleVariable.IsMemberContact = true;
			} else if (roleId == (long) 11) {
				memberRoleVariable.IsMemberAdmin = true;
			} else if (roleId == (long) 15) {
				memberRoleVariable.IsApplySingAdmin = true;
			}
		}
		return memberRoleVariable;
	}
	
	/**
	 * 會員權限資料名稱(包含isEnable為false)
	 * 
	 * @param memberId
	 *            使用者Id
	 * @return 會員權限名稱資料
	 */
	public List<MemberRole> getAllByMemberId(long memberId) {
		return memberRoleDAO.getAllByMemberId(memberId);
	}
	
	/**
	 * 更新會員權限資料(第一筆isEnable=true,其他false)
	 * 
	 * @param memberId
	 *            使用者Id	
	 * @return 是否成功
	 */
	public boolean updateIsenable(long memberId) {
		try {
			List<MemberRole> memberRoles = memberRoleDAO.getAllByMemberId(memberId);				
			int num =0;
			if (memberRoles != null)
				for (MemberRole memberRole : memberRoles) {					
					if (num == 0)
						memberRole.setIsEnable(true);
					else
						memberRole.setIsEnable(false);
					num++;					
					memberRoleDAO.update(memberRole);					
				}	
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
