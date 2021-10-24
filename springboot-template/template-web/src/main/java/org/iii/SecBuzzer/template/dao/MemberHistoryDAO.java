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

import org.iii.SecBuzzer.template.domain.MemberHistory;
import org.iii.SecBuzzer.template.util.WebCrypto;
import org.iii.SecBuzzer.template.util.WebDatetime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class MemberHistoryDAO {
	@PersistenceContext
    private EntityManager entityManager;
	
	public MemberHistory insert(MemberHistory entity) {
		Date now = new Date();
		entity.setCreateTime(now);
		entity.setModifyTime(now);
		entityManager.persist(entity);
		return entity;
	}

	public MemberHistory update(MemberHistory entity) {
		Date now = new Date();
		entity.setModifyTime(now);
		return entityManager.merge(entity);
	}

	public void delete(Long id) {
		MemberHistory entity = this.get(id);
		entityManager.remove(entity);
	}

	public MemberHistory get(Long id) {
		return entityManager.find(MemberHistory.class, id);
	}

	public List<MemberHistory> getAll() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<MemberHistory> criteriaQuery = criteriaBuilder.createQuery(MemberHistory.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<MemberHistory> root = criteriaQuery.from(MemberHistory.class);
		
		TypedQuery<MemberHistory> typedQuery = entityManager.createQuery(criteriaQuery);
		List<MemberHistory> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public MemberHistory getByMemberId(Long memberId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<MemberHistory> criteriaQuery = criteriaBuilder.createQuery(MemberHistory.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<MemberHistory> root = criteriaQuery.from(MemberHistory.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (memberId != null && memberId.longValue()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("memberId"), memberId.longValue()));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
		TypedQuery<MemberHistory> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);

		List<MemberHistory> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}
	}
	
	public boolean checkMemberHistory(Long memberId, String newCode, int historyTimes, int historyDays) {
		boolean resultTimes = false;
		boolean resultDays = false;
		{
			// History Times
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			// createQuery()方法參數是criteriaQuery最後回傳值的型別
			CriteriaQuery<MemberHistory> criteriaQuery = criteriaBuilder.createQuery(MemberHistory.class);
			// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
			Root<MemberHistory> root = criteriaQuery.from(MemberHistory.class);
			
			List<Predicate> conditions = new ArrayList<>();
			// 一般查詢
			if (memberId != null && memberId.longValue()!=0) {
				conditions.add(criteriaBuilder.equal(root.get("memberId"), memberId.longValue()));
			}
			
			if(conditions!=null && !conditions.isEmpty()) {
				criteriaQuery.where(conditions.toArray(new Predicate[1]));
			}
			
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
			TypedQuery<MemberHistory> typedQuery = entityManager.createQuery(criteriaQuery);
			typedQuery.setMaxResults(historyTimes);

			List<MemberHistory> memberHistory = typedQuery.getResultList();
			if (memberHistory != null && !memberHistory.isEmpty()) {
				for (MemberHistory history : memberHistory) {
					if (history.getPassword().equals(WebCrypto.getHash(WebCrypto.HashType.SHA512, newCode + history.getSalt()))) {
						resultTimes = true;
					}
				}
			}
		}
		{
			// History Days
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			// createQuery()方法參數是criteriaQuery最後回傳值的型別
			CriteriaQuery<MemberHistory> criteriaQuery = criteriaBuilder.createQuery(MemberHistory.class);
			// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
			Root<MemberHistory> root = criteriaQuery.from(MemberHistory.class);
			
			List<Predicate> conditions = new ArrayList<>();
			// 一般查詢
			if (memberId != null && memberId.longValue()!=0) {
				conditions.add(criteriaBuilder.equal(root.get("memberId"), memberId.longValue()));
			}
			conditions.add(criteriaBuilder.greaterThan(
					root.get("createTime"), WebDatetime.addDays(new Date(), -1*historyDays)
			));
			
			if(conditions!=null && !conditions.isEmpty()) {
				criteriaQuery.where(conditions.toArray(new Predicate[1]));
			}
			
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
			TypedQuery<MemberHistory> typedQuery = entityManager.createQuery(criteriaQuery);

			List<MemberHistory> memberHistory = typedQuery.getResultList();
			if (memberHistory != null && !memberHistory.isEmpty()) {
				for (MemberHistory history : memberHistory) {
					if (history.getPassword().equals(WebCrypto.getHash(WebCrypto.HashType.SHA512, newCode + history.getSalt()))) {
						resultDays = true;
					}
				}
			}			
		}
		return resultTimes || resultDays;
	}
}