package org.iii.SecBuzzer.template.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.iii.SecBuzzer.template.domain.CommonSetting;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CommonSettingDAO {
	@PersistenceContext
    private EntityManager entityManager;
	
	public List<CommonSetting> getSetting(JSONObject obj) {
		String functionCode = obj.getString("functionCode");
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<CommonSetting> criteriaQuery = criteriaBuilder.createQuery(CommonSetting.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<CommonSetting> root = criteriaQuery.from(CommonSetting.class);
		
		List<Predicate> conditions = new ArrayList<>();
		//查詢條件
		conditions.add(criteriaBuilder.equal(root.get("functionCode"), functionCode));
		conditions.add(criteriaBuilder.like(root.get("useMark"), "Y"));
		
		criteriaQuery.where(conditions.toArray(new Predicate[1]));
        
		TypedQuery<CommonSetting> typedQuery = entityManager.createQuery(criteriaQuery);
		
		List<CommonSetting> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}
}
