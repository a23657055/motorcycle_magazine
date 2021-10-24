package org.iii.SecBuzzer.template.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.iii.SecBuzzer.template.domain.MemberRole;
import org.iii.SecBuzzer.template.domain.SpMemberRoleName;
import org.iii.SecBuzzer.template.domain.ViewMemberRoleMember;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 會員權限
 */
@Repository
@Transactional
public class MemberRoleDAO {
	@PersistenceContext
    private EntityManager entityManager;

	public MemberRole insert(MemberRole entity) {
		Date now = new Date();
		entity.setCreateTime(now);
		entity.setModifyTime(now);
		entityManager.persist(entity);
		return entity;
	}

	public MemberRole update(MemberRole entity) {
		Date now = new Date();
		entity.setModifyTime(now);
		return entityManager.merge(entity);
	}

	public void delete(Long id) {
		MemberRole entity = this.get(id);
		entityManager.remove(entity);
	}

	public MemberRole get(Long id) {
		return entityManager.find(MemberRole.class, id);
	}
	
	public List<MemberRole> getAll() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<MemberRole> criteriaQuery = criteriaBuilder.createQuery(MemberRole.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<MemberRole> root = criteriaQuery.from(MemberRole.class);
		
		TypedQuery<MemberRole> typedQuery = entityManager.createQuery(criteriaQuery);
		List<MemberRole> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}
	
	public List<MemberRole> getAllByMemberId(long memberId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<MemberRole> criteriaQuery = criteriaBuilder.createQuery(MemberRole.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<MemberRole> root = criteriaQuery.from(MemberRole.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (memberId != 0) {
			conditions.add(criteriaBuilder.equal(root.get("memberId"), memberId));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		TypedQuery<MemberRole> typedQuery = entityManager.createQuery(criteriaQuery);
		List<MemberRole> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}
	
	public List<MemberRole> getByMemberId(long memberId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<MemberRole> criteriaQuery = criteriaBuilder.createQuery(MemberRole.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<MemberRole> root = criteriaQuery.from(MemberRole.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (memberId != 0) {
			conditions.add(criteriaBuilder.equal(root.get("memberId"), memberId));
		}
		conditions.add(criteriaBuilder.equal(root.get("isEnable"), true));
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		TypedQuery<MemberRole> typedQuery = entityManager.createQuery(criteriaQuery);
		List<MemberRole> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public List<ViewMemberRoleMember> getByRoleId(long roleId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<ViewMemberRoleMember> criteriaQuery = criteriaBuilder.createQuery(ViewMemberRoleMember.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ViewMemberRoleMember> root = criteriaQuery.from(ViewMemberRoleMember.class);

		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (roleId != 0) {
			conditions.add(criteriaBuilder.equal(root.get("roleId"), roleId));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		TypedQuery<ViewMemberRoleMember> typedQuery = entityManager.createQuery(criteriaQuery);
		List<ViewMemberRoleMember> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public List<SpMemberRoleName> getMemberRoleList(JSONObject obj) {
		long memberId = obj.isNull("Id") == true ? 0 : obj.getInt("Id");
		
		StoredProcedureQuery storedProcedure = entityManager
				.createStoredProcedureQuery("xp_member_role_name")
				.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
				.setParameter(1, memberId);
		
		List<Object[]> procedureResult = storedProcedure.getResultList();
		if (procedureResult != null && !procedureResult.isEmpty()) {
			List<SpMemberRoleName> result = new ArrayList<>();
			for (Object[] array : procedureResult) {
				long id = Long.parseLong((String) array[0]);
				Long roleId = ((BigInteger) array[1]).longValue();
				String name = (String) array[2];
				Long flag = Long.parseLong((String) array[3]);
			
				SpMemberRoleName row = new SpMemberRoleName();
				row.setId(id);
				row.setRoleId(roleId);
				row.setName(name);
				row.setFlag(flag);
				
				result.add(row);
			}
			return result;
		} else {
			return null;
		}
	}
}
