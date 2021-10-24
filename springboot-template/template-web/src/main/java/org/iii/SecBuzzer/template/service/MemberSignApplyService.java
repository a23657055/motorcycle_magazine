package org.iii.SecBuzzer.template.service;

import java.util.List;

import org.iii.SecBuzzer.template.dao.ViewMemberSignApplyDAO;
import org.iii.SecBuzzer.template.domain.ViewMemberSignApply;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 會員審查服務
 */
@Service
public class MemberSignApplyService {
	final static Logger logger = LoggerFactory.getLogger(MemberSignApplyService.class);
	@Autowired
	private ViewMemberSignApplyDAO viewMemberSignApplyDAO;

	/**
	 * 取得Member資料
	 * 
	 * @param json
	 *            查詢條件
	 * @return Member資料
	 */
	public List<ViewMemberSignApply> getList(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return viewMemberSignApplyDAO.getList(obj);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 取得Member資料筆數
	 * 
	 * @param json
	 *            查詢條件
	 * @return Member資料筆數
	 */
	public long getListSize(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			return viewMemberSignApplyDAO.getListSize(obj);
		} catch (Exception e) {
			return 0;
		}
	}
}
