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

import org.iii.SecBuzzer.template.domain.ResourceMessage;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ResourceMessageDAO {
	@PersistenceContext
    private EntityManager entityManager;
	
	public ResourceMessage get(Long id) {
		return entityManager.find(ResourceMessage.class, id);
	}
	public ResourceMessage insert(ResourceMessage entity) {
		Date now = new Date();
		entity.setCreateTime(now);
		entity.setModifyTime(now);
		entityManager.persist(entity);
		return entity;
	}
	public ResourceMessage update(ResourceMessage entity) {
		Date now = new Date();
		entity.setModifyTime(now);
		return entityManager.merge(entity);
	}
	public void delete(Long id) {
		ResourceMessage entity = this.get(id);
		entityManager.remove(entity);
	}
	
	public List<ResourceMessage> getAll() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<ResourceMessage> criteriaQuery = criteriaBuilder.createQuery(ResourceMessage.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ResourceMessage> root = criteriaQuery.from(ResourceMessage.class);

		TypedQuery<ResourceMessage> typedQuery = entityManager.createQuery(criteriaQuery);
		List<ResourceMessage> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}
	
	public List<ResourceMessage> getList(JSONObject obj) {
		int start = obj.isNull("start") == true ? 0 : obj.getInt("start");
		int maxRows = obj.isNull("maxRows") == true ? 0 : obj.getInt("maxRows");
		boolean dir = obj.isNull("dir") == true ? false : obj.getBoolean("dir");
		String sort = obj.isNull("sort") == true ? "id" : obj.getString("sort");

		long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
		String messageKey = obj.isNull("MessageKey") == true ? null : obj.getString("MessageKey");
		String messageValue = obj.isNull("MessageValue") == true ? null : obj.getString("MessageValue");
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<ResourceMessage> criteriaQuery = criteriaBuilder.createQuery(ResourceMessage.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ResourceMessage> root = criteriaQuery.from(ResourceMessage.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (id != 0) {
			conditions.add(criteriaBuilder.equal(root.get("id"), id));
		}
		if (messageKey != null && messageKey.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("messageKey"), "%"+messageKey+"%"));
		}
		if (messageValue != null && messageValue.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("messageValue"), "%"+messageValue+"%"));
		}

		if(conditions!=null && !conditions.isEmpty())
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		        
        if (dir == true)
        	criteriaQuery.orderBy(criteriaBuilder.desc(root.get(sort)));
  		else
  			criteriaQuery.orderBy(criteriaBuilder.asc(root.get(sort)));

		TypedQuery<ResourceMessage> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		if (maxRows != 0)
			typedQuery.setMaxResults(maxRows);

		List<ResourceMessage> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty())
			return result;
		else
			return null;
	}

	public long getListSize(JSONObject obj) {
		long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
		String messageKey = obj.isNull("MessageKey") == true ? null : obj.getString("MessageKey");
		String messageValue = obj.isNull("MessageValue") == true ? null : obj.getString("MessageValue");
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ResourceMessage> root = criteriaQuery.from(ResourceMessage.class);

		criteriaQuery = criteriaQuery.select(criteriaBuilder.count(root));

		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (id != 0) {
			conditions.add(criteriaBuilder.equal(root.get("id"), id));
		}
		if (messageKey != null && messageKey.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("messageKey"), "%"+messageKey+"%"));
		}
		if (messageValue != null && messageValue.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("messageValue"), "%"+messageValue+"%"));
		}

		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        long total = typedQuery.getSingleResult();
		return total;
	}
	
	public long countByMessageKeyAndLanguage(String messageKey, String language) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ResourceMessage> root = criteriaQuery.from(ResourceMessage.class);

		criteriaQuery = criteriaQuery.select(criteriaBuilder.count(root));

		List<Predicate> conditions = new ArrayList<>();
		if (messageKey != null && messageKey.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("messageKey"), messageKey));
		}
		if (language != null && language.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("language"), language));
		}

		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        long total = typedQuery.getSingleResult();
        return total;
	}
}
