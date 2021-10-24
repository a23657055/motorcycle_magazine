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

import org.iii.SecBuzzer.template.dao.OrgVariable.OrgType;
import org.iii.SecBuzzer.template.domain.ViewMemberSignApply;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class ViewMemberSignApplyDAO {
	@PersistenceContext
    private EntityManager entityManager;
	
	public List<ViewMemberSignApply> getList(JSONObject obj) {
		int start = obj.isNull("start") == true ? 0 : obj.getInt("start");
		int maxRows = obj.isNull("maxRows") == true ? 0 : obj.getInt("maxRows");
		boolean dir = obj.isNull("dir") == true ? false : obj.getBoolean("dir");
		String sort = obj.isNull("sort") == true ? "id" : obj.getString("sort");
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<ViewMemberSignApply> criteriaQuery = criteriaBuilder.createQuery(ViewMemberSignApply.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ViewMemberSignApply> root = criteriaQuery.from(ViewMemberSignApply.class);

		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		conditions.add(criteriaBuilder.equal(root.get("orgType"), OrgType.Member.getValue()));
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
        if (dir == true) {
        	criteriaQuery.orderBy(criteriaBuilder.desc(root.get(sort)));
        } else {
  			criteriaQuery.orderBy(criteriaBuilder.asc(root.get(sort)));
        }	        
	        
		TypedQuery<ViewMemberSignApply> typedQuery = entityManager.createQuery(criteriaQuery);
		
		typedQuery.setFirstResult(start);
		if (maxRows != 0) {
			typedQuery.setMaxResults(maxRows);
		}
		
		List<ViewMemberSignApply> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}		
	}

	public long getListSize(JSONObject obj) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ViewMemberSignApply> root = criteriaQuery.from(ViewMemberSignApply.class);
		
		criteriaQuery = criteriaQuery.select(criteriaBuilder.count(root));
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		conditions.add(criteriaBuilder.equal(root.get("orgType"), OrgType.Member.getValue()));
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        long total = typedQuery.getSingleResult();
		return total;
	}
}