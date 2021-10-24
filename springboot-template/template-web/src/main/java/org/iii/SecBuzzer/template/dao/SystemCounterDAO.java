package org.iii.SecBuzzer.template.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.iii.SecBuzzer.template.domain.SystemCounter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SystemCounterDAO {
	@PersistenceContext
    private EntityManager entityManager;

	public SystemCounter insert(SystemCounter entity) {
		Date now = new Date();
		entity.setCreateTime(now);
		entityManager.persist(entity);
		return entity;
	}

	public long getTotal() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<SystemCounter> root = criteriaQuery.from(SystemCounter.class);
		criteriaQuery = criteriaQuery.select(criteriaBuilder.count(root));
		
        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        long total = typedQuery.getSingleResult();
		return total;
	}
}
