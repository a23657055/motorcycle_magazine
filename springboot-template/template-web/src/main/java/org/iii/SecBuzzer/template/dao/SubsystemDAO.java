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

import org.iii.SecBuzzer.template.domain.Subsystem;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 子系統服務
 */
@Repository
@Transactional
public class SubsystemDAO {
	@PersistenceContext
    private EntityManager entityManager;

	public Subsystem insert(Subsystem entity) {
		Date now = new Date();
		entity.setCreateTime(now);
		entity.setModifyTime(now);
		entityManager.persist(entity);
		return entity;
	}

	public Subsystem update(Subsystem entity) {
		Date now = new Date();
		entity.setModifyTime(now);
		return entityManager.merge(entity);
	}

	public void delete(Long id) {
		Subsystem entity = this.get(id);
		entityManager.remove(entity);
	}

	public Subsystem get(Long id) {
		return entityManager.find(Subsystem.class, id);
	}

	public List<Subsystem> getAll() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Subsystem> criteriaQuery = criteriaBuilder.createQuery(Subsystem.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Subsystem> root = criteriaQuery.from(Subsystem.class);
		
		TypedQuery<Subsystem> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Subsystem> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}		
	}

	public Subsystem getByCode(String code) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		//createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Subsystem> criteriaQuery = criteriaBuilder.createQuery(Subsystem.class);
		//from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Subsystem> root = criteriaQuery.from(Subsystem.class);

		List<Predicate> conditions = new ArrayList<>();
		
		if (code != null && code.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("code"), code));
		}

		if(conditions!=null && !conditions.isEmpty())
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		
    	criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
    	
		TypedQuery<Subsystem> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);

		List<Subsystem> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}
	}

	public Subsystem getByName(String name) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		//createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Subsystem> criteriaQuery = criteriaBuilder.createQuery(Subsystem.class);
		//from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Subsystem> root = criteriaQuery.from(Subsystem.class);

		List<Predicate> conditions = new ArrayList<>();
		
		if (name != null && name.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("name"), name));
		}

		if(conditions!=null && !conditions.isEmpty())
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		
    	criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
    	
		TypedQuery<Subsystem> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);

		List<Subsystem> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}
	}

	public List<Subsystem> getList(JSONObject obj) {
		int start = obj.isNull("start") == true ? 0 : obj.getInt("start");
		int maxRows = obj.isNull("maxRows") == true ? 0 : obj.getInt("maxRows");
		boolean dir = obj.isNull("dir") == true ? false : obj.getBoolean("dir");
		String sort = obj.isNull("sort") == true ? "id" : obj.getString("sort");

		long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
		String code = obj.isNull("Code") == true ? null : obj.getString("Code");
		String name = obj.isNull("Name") == true ? null : obj.getString("Name");
		Boolean isEnable = obj.isNull("IsEnable") == true ? null : obj.getBoolean("IsEnable");
		Boolean isShow = obj.isNull("IsShow") == true ? null : obj.getBoolean("IsShow");
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Subsystem> criteriaQuery = criteriaBuilder.createQuery(Subsystem.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Subsystem> root = criteriaQuery.from(Subsystem.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (id != 0) {
			conditions.add(criteriaBuilder.equal(root.get("id"), id));
		}
		if (code != null && code.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("code"), "%"+code+"%"));
		}
		if (name != null && name.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
		}
		if (isEnable != null) {
			conditions.add(criteriaBuilder.equal(root.get("isEnable"), isEnable.booleanValue()));
		}
		if (isShow != null) {
			conditions.add(criteriaBuilder.equal(root.get("isShow"), isShow.booleanValue()));
		}

		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
        if (dir == true) {
        	criteriaQuery.orderBy(criteriaBuilder.desc(root.get(sort)));
        } else {
  			criteriaQuery.orderBy(criteriaBuilder.asc(root.get(sort)));
        }
        
		TypedQuery<Subsystem> typedQuery = entityManager.createQuery(criteriaQuery);
		
		typedQuery.setFirstResult(start);
		if (maxRows != 0) {
			typedQuery.setMaxResults(maxRows);
		}
		
		List<Subsystem> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public long getListSize(JSONObject obj) {
		long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
		String code = obj.isNull("Code") == true ? null : obj.getString("Code");
		String name = obj.isNull("Name") == true ? null : obj.getString("Name");
		Boolean isEnable = obj.isNull("IsEnable") == true ? null : obj.getBoolean("IsEnable");
		Boolean isShow = obj.isNull("IsShow") == true ? null : obj.getBoolean("IsShow");
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Subsystem> root = criteriaQuery.from(Subsystem.class);
		
		criteriaQuery = criteriaQuery.select(criteriaBuilder.count(root));
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (id != 0) {
			conditions.add(criteriaBuilder.equal(root.get("id"), id));
		}
		if (code != null && code.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("code"), "%"+code+"%"));
		}
		if (name != null && name.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
		}
		if (isEnable != null) {
			conditions.add(criteriaBuilder.equal(root.get("isEnable"), isEnable.booleanValue()));
		}
		if (isShow != null) {
			conditions.add(criteriaBuilder.equal(root.get("isShow"), isShow.booleanValue()));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        long total = typedQuery.getSingleResult();
		return total;
	}
}
