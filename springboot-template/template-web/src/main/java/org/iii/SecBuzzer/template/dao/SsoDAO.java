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

import org.iii.SecBuzzer.template.domain.Sso;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SsoDAO {
	@PersistenceContext
    private EntityManager entityManager;

	public Sso insert(Sso entity) {
		entityManager.persist(entity);
		return entity;
	}

	public void delete(String code) {
		Sso entity = this.get(code);
		entityManager.remove(entity);
	}

	public Sso get(String code) {
		return entityManager.find(Sso.class, code);
	}

	public Sso getByMemberId(Long memberId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		//createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Sso> criteriaQuery = criteriaBuilder.createQuery(Sso.class);
		//from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Sso> root = criteriaQuery.from(Sso.class);

		List<Predicate> conditions = new ArrayList<>();
		if (memberId != null && memberId.longValue()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("memberId"), memberId.longValue()));
		}
		if(conditions!=null && !conditions.isEmpty())
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		
    	criteriaQuery.orderBy(criteriaBuilder.desc(root.get("expireTime")));
    	
		TypedQuery<Sso> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);

		List<Sso> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}
	}
}
