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

import org.iii.SecBuzzer.template.domain.ForgotTemp;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ForgotTempDAO {

	@PersistenceContext
    private EntityManager entityManager;
	
	public ForgotTemp insert(ForgotTemp entity) {
		entityManager.persist(entity);
		return entity;
	}
	public void delete(String code) {
		ForgotTemp entity = this.get(code);
		entityManager.remove(entity);
	}
	public ForgotTemp get(String code) {
		return entityManager.find(ForgotTemp.class, code);
	}
	
	public ForgotTemp getByMemberId(Long memberId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		//createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<ForgotTemp> criteriaQuery = criteriaBuilder.createQuery(ForgotTemp.class);
		//from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ForgotTemp> root = criteriaQuery.from(ForgotTemp.class);

		List<Predicate> conditions = new ArrayList<>();
		if (memberId != null && memberId.longValue()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("memberId"), memberId.longValue()));
		}
		if(conditions!=null && !conditions.isEmpty())
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		
    	criteriaQuery.orderBy(criteriaBuilder.desc(root.get("memberId")));
    	
		TypedQuery<ForgotTemp> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);

		List<ForgotTemp> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}
	}
}
