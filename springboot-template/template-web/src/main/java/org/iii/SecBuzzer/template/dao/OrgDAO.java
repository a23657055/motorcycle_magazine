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

import org.iii.SecBuzzer.template.domain.Org;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class OrgDAO {
	@PersistenceContext
    private EntityManager entityManager;

	public Org insert(Org entity) {
		Date now = new Date();
		entity.setCreateTime(now);
		entity.setModifyTime(now);
		entityManager.persist(entity);
		return entity;
	}

	public Org update(Org entity) {
		Date now = new Date();
		entity.setModifyTime(now);
		return entityManager.merge(entity);
	}

	public void delete(Long id) {
		Org entity = this.get(id);
		entityManager.remove(entity);
	}

	public Org get(Long id) {
		return entityManager.find(Org.class, id);
	}

	public List<Org> getAll() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Org> criteriaQuery = criteriaBuilder.createQuery(Org.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Org> root = criteriaQuery.from(Org.class);
		
		TypedQuery<Org> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Org> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public List<Org> getAllMember() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Org> criteriaQuery = criteriaBuilder.createQuery(Org.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Org> root = criteriaQuery.from(Org.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		conditions.add(criteriaBuilder.equal(root.get("orgType"), "1"));
		conditions.add(criteriaBuilder.equal(root.get("isEnable"), true));
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		TypedQuery<Org> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Org> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public List<Org> getByOrgType(String orgType) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Org> criteriaQuery = criteriaBuilder.createQuery(Org.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Org> root = criteriaQuery.from(Org.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (orgType != null && orgType.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("orgType"), orgType));
		}
		conditions.add(criteriaBuilder.equal(root.get("isEnable"), true));
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		TypedQuery<Org> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Org> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public List<Org> getList(Boolean isEnable, String orgType) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Org> criteriaQuery = criteriaBuilder.createQuery(Org.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Org> root = criteriaQuery.from(Org.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (orgType != null && orgType.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("orgType"), orgType));
		}
		if (isEnable != null) {
			conditions.add(criteriaBuilder.equal(root.get("isEnable"), isEnable.booleanValue()));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		TypedQuery<Org> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Org> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public List<Org> getList(Boolean isEnable, String orgType, String authType) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Org> criteriaQuery = criteriaBuilder.createQuery(Org.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Org> root = criteriaQuery.from(Org.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (orgType != null && orgType.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("orgType"), orgType));
		}
		if (isEnable != null) {
			conditions.add(criteriaBuilder.equal(root.get("isEnable"), isEnable.booleanValue()));
		}
		if (authType != null && authType.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("authType"), authType));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		TypedQuery<Org> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Org> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public List<Org> getList(Boolean isEnable, String orgType, String queryString, int perPage) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Org> criteriaQuery = criteriaBuilder.createQuery(Org.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Org> root = criteriaQuery.from(Org.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (orgType != null && orgType.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("orgType"), orgType));
		}
		if (isEnable != null) {
			conditions.add(criteriaBuilder.equal(root.get("isEnable"), isEnable.booleanValue()));
		}
		if (queryString != null && queryString.length()!=0) {
			conditions.add(
					criteriaBuilder.or(
							criteriaBuilder.like(root.get("code"), "%"+queryString+"%"),
							criteriaBuilder.like(root.get("name"), "%"+queryString+"%")
					)
			);
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		TypedQuery<Org> typedQuery = entityManager.createQuery(criteriaQuery);
		if (perPage > 0) {
			typedQuery.setFirstResult(0);
			typedQuery.setMaxResults(perPage);
		}
		
		List<Org> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}		
	}

	public List<Org> getList(JSONObject obj) {
		int start = obj.isNull("start") == true ? 0 : obj.getInt("start");
		int maxRows = obj.isNull("maxRows") == true ? 0 : obj.getInt("maxRows");
		boolean dir = obj.isNull("dir") == true ? false : obj.getBoolean("dir");
		String sort = obj.isNull("sort") == true ? "id" : obj.getString("sort");
		
		long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
		String name = obj.isNull("Name") == true ? null : obj.getString("Name");
		String code = obj.isNull("Code") == true ? null : obj.getString("Code");
		String orgType = obj.isNull("OrgType") == true ? null : obj.getString("OrgType");
		String authType = obj.isNull("AuthType") == true ? null : obj.getString("AuthType");
		String tel = obj.isNull("Tel") == true ? null : obj.getString("Tel");
		String fax = obj.isNull("Fax") == true ? null : obj.getString("Fax");
		String city = obj.isNull("City") == true ? null : obj.getString("City");
		String town = obj.isNull("Town") == true ? null : obj.getString("Town");
		String address = obj.isNull("Address") == true ? null : obj.getString("Address");
		Boolean isEnable = obj.isNull("IsEnable") == true ? null : obj.getBoolean("IsEnable");

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Org> criteriaQuery = criteriaBuilder.createQuery(Org.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Org> root = criteriaQuery.from(Org.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (id != 0) {
			conditions.add(criteriaBuilder.equal(root.get("id"), id));
		}
		if (name != null && name.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
		}
		if (code != null && code.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("code"), "%"+code+"%"));
		}
		if (orgType != null && orgType.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("orgType"), orgType));
		}
		if (authType != null && authType.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("authType"), authType));
		}
		if (tel != null && tel.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("tel"), "%"+tel+"%"));
		}
		if (fax != null && fax.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("fax"), "%"+fax+"%"));
		}
		if (city != null && city.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("city"), "%"+city+"%"));
		}
		if (town != null && town.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("town"), "%"+town+"%"));
		}
		if (address != null && address.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("address"), "%"+address+"%"));
		}
		if (isEnable != null) {
			conditions.add(criteriaBuilder.equal(root.get("isEnable"), isEnable.booleanValue()));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
        if (dir == true) {
        	criteriaQuery.orderBy(criteriaBuilder.desc(root.get(sort)));
        } else {
  			criteriaQuery.orderBy(criteriaBuilder.asc(root.get(sort)));
        }
        
		TypedQuery<Org> typedQuery = entityManager.createQuery(criteriaQuery);
		
		typedQuery.setFirstResult(start);
		if (maxRows != 0) {
			typedQuery.setMaxResults(maxRows);
		}
		
		List<Org> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public long getListSize(JSONObject obj) {
		long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
		// String name = obj.isNull("Name") == true ? null :
		// obj.getString("Name");
		// Boolean isEnable = obj.isNull("IsEnable") == true ? null :
		// obj.getBoolean("IsEnable");
		String name = obj.isNull("Name") == true ? null : obj.getString("Name");
		String code = obj.isNull("Code") == true ? null : obj.getString("Code");
		String orgType = obj.isNull("OrgType") == true ? null : obj.getString("OrgType");
		String authType = obj.isNull("AuthType") == true ? null : obj.getString("AuthType");
		String tel = obj.isNull("Tel") == true ? null : obj.getString("Tel");
		String fax = obj.isNull("Fax") == true ? null : obj.getString("Fax");
		String city = obj.isNull("City") == true ? null : obj.getString("City");
		String town = obj.isNull("Town") == true ? null : obj.getString("Town");
		String address = obj.isNull("Address") == true ? null : obj.getString("Address");

		Boolean isEnable = obj.isNull("IsEnable") == true ? null : obj.getBoolean("IsEnable");

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Org> root = criteriaQuery.from(Org.class);
		
		criteriaQuery = criteriaQuery.select(criteriaBuilder.count(root));
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (id != 0) {
			conditions.add(criteriaBuilder.equal(root.get("id"), id));
		}
		if (name != null && name.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
		}
		if (code != null && code.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("code"), "%"+code+"%"));
		}
		if (orgType != null && orgType.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("orgType"), orgType));
		}
		if (authType != null && authType.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("authType"), authType));
		}
		if (tel != null && tel.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("tel"), "%"+tel+"%"));
		}
		if (fax != null && fax.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("fax"), "%"+fax+"%"));
		}
		if (city != null && city.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("city"), "%"+city+"%"));
		}
		if (town != null && town.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("town"), "%"+town+"%"));
		}
		if (address != null && address.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("address"), "%"+address+"%"));
		}
		if (isEnable != null) {
			conditions.add(criteriaBuilder.equal(root.get("isEnable"), isEnable.booleanValue()));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        long total = typedQuery.getSingleResult();
		return total;
	}
}
