package org.iii.SecBuzzer.template.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.iii.SecBuzzer.template.domain.ViewMenuLimit;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ViewMenuLimitDAO {
	@PersistenceContext
    private EntityManager entityManager;

	public List<ViewMenuLimit> getMenu(Long memberId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		//createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<ViewMenuLimit> criteriaQuery = criteriaBuilder.createQuery(ViewMenuLimit.class);
		//from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ViewMenuLimit> root = criteriaQuery.from(ViewMenuLimit.class);

		Predicate conjunction1 = criteriaBuilder.and(
				criteriaBuilder.isNull(root.get("memberId")),
				criteriaBuilder.equal(root.get("formCode"), "separator"),
				criteriaBuilder.equal(root.get("formName"), "separator")
		);

		Predicate memberIdEqOrIsNull2 = criteriaBuilder.or(
				criteriaBuilder.equal(root.get("memberId"), memberId.longValue()),
				criteriaBuilder.isNull(root.get("memberId"))
		);
		Predicate conjunction2 = criteriaBuilder.and(memberIdEqOrIsNull2,
				criteriaBuilder.equal(root.get("actionRead"), true)				
		);
		
		Predicate disjunction = criteriaBuilder.or(conjunction1, conjunction2);

		List<Predicate> conditions = new ArrayList<>();
		conditions.add(disjunction);
		conditions.add(criteriaBuilder.equal(root.get("formIsShow"), true));
		conditions.add(criteriaBuilder.equal(root.get("subsystemIsShow"), true));
		criteriaQuery.where(conditions.toArray(new Predicate[1]));

		List<Order> orderList = new ArrayList<>();
		orderList.add(criteriaBuilder.asc(root.get("subsystemSort")));
		orderList.add(criteriaBuilder.asc(root.get("formSort")));
		criteriaQuery.orderBy(orderList);
		
		TypedQuery<ViewMenuLimit> typedQuery = entityManager.createQuery(criteriaQuery);
		List<ViewMenuLimit> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public ViewMenuLimit getAction(Long memberId, String controllerName, String actionName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		//createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<ViewMenuLimit> criteriaQuery = criteriaBuilder.createQuery(ViewMenuLimit.class);
		//from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ViewMenuLimit> root = criteriaQuery.from(ViewMenuLimit.class);

		List<Predicate> conditions = new ArrayList<>();
		if (memberId != null && memberId.longValue()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("memberId"), memberId.longValue()));
		}
		if (controllerName != null && controllerName.length()!=0) {
//			conditions.add(criteriaBuilder.equal(root.get("controllerName"), controllerName));
			conditions.add(
					criteriaBuilder.equal(
							criteriaBuilder.lower(root.get("controllerName")), controllerName.toLowerCase()
					)
			);
		}
		if (actionName != null && actionName.length()!=0) {
//			conditions.add(criteriaBuilder.equal(root.get("actionName"), actionName));
			conditions.add(
					criteriaBuilder.equal(
							criteriaBuilder.lower(root.get("actionName")), actionName.toLowerCase()
					)
			);
		}		

		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
    	criteriaQuery.orderBy(criteriaBuilder.desc(root.get("memberId")));
    	
		TypedQuery<ViewMenuLimit> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);

		List<ViewMenuLimit> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}
	}
}
