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

import org.iii.SecBuzzer.template.domain.SystemLog;
import org.iii.SecBuzzer.template.util.WebCrypto;
import org.iii.SecBuzzer.template.util.WebDatetime;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SystemLogDAO {
	@PersistenceContext
    private EntityManager entityManager;
	
	public SystemLog insert(SystemLog entity) {
		entity.setCreateTime(new Date());
		String hashCode = WebCrypto.getHash(WebCrypto.HashType.SHA512,
				entity.getAppName() + entity.getFuncName() + entity.getInputValue() + entity.getActionName() +
				entity.getStatus() + entity.getIp() + entity.getCreateAccount() + entity.getCreateTime().getTime());
		entity.setHashCode(hashCode);
		
		entityManager.persist(entity);
		return entity;
	}

//	public void delete(Long id) {
//		SystemLog entity = this.get(id);
//		entityManager.remove(entity);
//	}

	public SystemLog get(Long id) {
		return entityManager.find(SystemLog.class, id);
	}

//	public List<SystemLog> getAll() {
//		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//		// createQuery()方法參數是criteriaQuery最後回傳值的型別
//		CriteriaQuery<SystemLog> criteriaQuery = criteriaBuilder.createQuery(SystemLog.class);
//		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
//		Root<SystemLog> root = criteriaQuery.from(SystemLog.class);
//		
//		TypedQuery<SystemLog> typedQuery = entityManager.createQuery(criteriaQuery);
//		List<SystemLog> result = typedQuery.getResultList();
//		if (result != null && !result.isEmpty()) {
//			return result;
//		} else {
//			return null;
//		}
//	}

	public List<SystemLog> getList(JSONObject obj) {
		int start = obj.isNull("start") == true ? 0 : obj.getInt("start");
		int maxRows = obj.isNull("maxRows") == true ? 0 : obj.getInt("maxRows");
		boolean dir = obj.isNull("dir") == true ? false : obj.getBoolean("dir");
		String sort = obj.isNull("sort") == true ? "id" : obj.getString("sort");

		long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
		String appName = obj.isNull("AppName") == true ? null : obj.getString("AppName");
		String funcName = obj.isNull("FuncName") == true ? null : obj.getString("FuncName");
		String inputValue = obj.isNull("InputValue") == true ? null : obj.getString("InputValue");
		String actionName = obj.isNull("ActionName") == true ? null : obj.getString("ActionName");
		String status = obj.isNull("Status") == true ? null : obj.getString("Status");
		String ip = obj.isNull("Ip") == true ? null : obj.getString("Ip");
		String hashCode = obj.isNull("HashCode") == true ? null : obj.getString("HashCode");
		String createAccount = obj.isNull("CreateAccount") == true ? null : obj.getString("CreateAccount");
		String sdate = obj.isNull("Sdate") == true ? null : obj.getString("Sdate");
		String edate = obj.isNull("Edate") == true ? null : obj.getString("Edate");

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<SystemLog> criteriaQuery = criteriaBuilder.createQuery(SystemLog.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<SystemLog> root = criteriaQuery.from(SystemLog.class);

		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (id != 0) {
			conditions.add(criteriaBuilder.equal(root.get("id"), id));
		}
		if(appName!=null && appName.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("appName"), "%"+appName+"%"));
		}
		if(funcName!=null && funcName.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("funcName"), "%"+funcName+"%"));
		}
		if(inputValue!=null && inputValue.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("inputValue"), "%"+inputValue+"%"));
		}
		if(actionName!=null && actionName.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("actionName"), "%"+actionName+"%"));
		}
		if(status!=null && status.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("status"), "%"+status+"%"));
		}
		if(ip!=null && ip.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("ip"), "%"+ip+"%"));
		}
		if(hashCode!=null && hashCode.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("hashCode"), "%"+hashCode+"%"));
		}
		if(createAccount!=null && createAccount.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("createAccount"), "%"+createAccount+"%"));
		}
		if(sdate!=null && sdate.length() != 0) {
			conditions.add(
					criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"),
							WebDatetime.getStartOfDay(WebDatetime.parseOnlyDate(sdate)))
			);
		}
		if(edate!=null && edate.length() != 0) {
			conditions.add(
					criteriaBuilder.lessThanOrEqualTo(root.get("createTime"),
							WebDatetime.getStartOfDay(WebDatetime.parseOnlyDate(edate))));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
        if (dir == true) {
        	criteriaQuery.orderBy(criteriaBuilder.desc(root.get(sort)));
        } else {
  			criteriaQuery.orderBy(criteriaBuilder.asc(root.get(sort)));
        }	        
	        
		TypedQuery<SystemLog> typedQuery = entityManager.createQuery(criteriaQuery);
		
		typedQuery.setFirstResult(start);
		if (maxRows != 0) {
			typedQuery.setMaxResults(maxRows);
		}
		
		List<SystemLog> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}		
	}

	public long getListSize(JSONObject obj) {
		long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
		String appName = obj.isNull("AppName") == true ? null : obj.getString("AppName");
		String funcName = obj.isNull("FuncName") == true ? null : obj.getString("FuncName");
		String inputValue = obj.isNull("InputValue") == true ? null : obj.getString("InputValue");
		String actionName = obj.isNull("ActionName") == true ? null : obj.getString("ActionName");
		String status = obj.isNull("Status") == true ? null : obj.getString("Status");
		String ip = obj.isNull("Ip") == true ? null : obj.getString("Ip");
		String hashCode = obj.isNull("HashCode") == true ? null : obj.getString("HashCode");
		String createAccount = obj.isNull("CreateAccount") == true ? null : obj.getString("CreateAccount");
		String sdate = obj.isNull("Sdate") == true ? null : obj.getString("Sdate");
		String edate = obj.isNull("Edate") == true ? null : obj.getString("Edate");
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<SystemLog> root = criteriaQuery.from(SystemLog.class);
		
		criteriaQuery = criteriaQuery.select(criteriaBuilder.count(root));
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (id != 0) {
			conditions.add(criteriaBuilder.equal(root.get("id"), id));
		}
		if(appName!=null && appName.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("appName"), "%"+appName+"%"));
		}
		if(funcName!=null && funcName.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("funcName"), "%"+funcName+"%"));
		}
		if(inputValue!=null && inputValue.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("inputValue"), "%"+inputValue+"%"));
		}
		if(actionName!=null && actionName.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("actionName"), "%"+actionName+"%"));
		}
		if(status!=null && status.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("status"), "%"+status+"%"));
		}
		if(ip!=null && ip.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("ip"), "%"+ip+"%"));
		}
		if(hashCode!=null && hashCode.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("hashCode"), "%"+hashCode+"%"));
		}
		if(createAccount!=null && createAccount.length() != 0) {
			conditions.add(criteriaBuilder.like(root.get("createAccount"), "%"+createAccount+"%"));
		}
		if(sdate!=null && sdate.length() != 0) {
			conditions.add(
					criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"),
							WebDatetime.getStartOfDay(WebDatetime.parseOnlyDate(sdate)))
			);
		}
		if(edate!=null && edate.length() != 0) {
			conditions.add(
					criteriaBuilder.lessThanOrEqualTo(root.get("createTime"),
							WebDatetime.getStartOfDay(WebDatetime.parseOnlyDate(edate))));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        long total = typedQuery.getSingleResult();
		return total;		
	}
}
