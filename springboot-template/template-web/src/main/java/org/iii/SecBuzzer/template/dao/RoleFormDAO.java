package org.iii.SecBuzzer.template.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.iii.SecBuzzer.template.domain.RoleForm;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * RoleForm服務
 */
@Repository
@Transactional
public class RoleFormDAO {
	@PersistenceContext
    private EntityManager entityManager;

	public RoleForm insert(RoleForm entity) {
		Date now = new Date();
		entity.setCreateTime(now);
		entity.setModifyTime(now);
		entityManager.persist(entity);
		return entity;
	}

	public RoleForm update(RoleForm entity) {
		Date now = new Date();
		entity.setModifyTime(now);
		return entityManager.merge(entity);
	}

	public void delete(Long id) {
		RoleForm entity = this.get(id);
		entityManager.remove(entity);
	}

	public RoleForm get(Long id) {
		return entityManager.find(RoleForm.class, id);
	}

	public List<RoleForm> getAll() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<RoleForm> criteriaQuery = criteriaBuilder.createQuery(RoleForm.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<RoleForm> root = criteriaQuery.from(RoleForm.class);
		
		TypedQuery<RoleForm> typedQuery = entityManager.createQuery(criteriaQuery);
		List<RoleForm> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	@Transactional
	public RoleForm insertOrUpdate(RoleForm entity) {
		return this.update(entity);
	}
	
	public List<RoleForm> getList(Long roleId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<RoleForm> criteriaQuery = criteriaBuilder.createQuery(RoleForm.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<RoleForm> root = criteriaQuery.from(RoleForm.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (roleId!=null) {
			conditions.add(criteriaBuilder.equal(root.get("roleId"), roleId.longValue()));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		TypedQuery<RoleForm> typedQuery = entityManager.createQuery(criteriaQuery);
		List<RoleForm> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}
}
