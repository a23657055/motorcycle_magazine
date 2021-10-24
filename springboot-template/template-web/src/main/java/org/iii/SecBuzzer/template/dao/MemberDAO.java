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

import org.iii.SecBuzzer.template.domain.Member;
import org.iii.SecBuzzer.template.domain.ViewMember;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class MemberDAO {
	@PersistenceContext
    private EntityManager entityManager;
	
	public Member insert(Member entity) {
		Date now = new Date();
		entity.setCreateTime(now);
		entity.setModifyTime(now);
		entityManager.persist(entity);
		return entity;
	}
	public Member update(Member entity) {
		Date now = new Date();
		entity.setModifyTime(now);
		return entityManager.merge(entity);
	}
	public void delete(Long id) {
		Member entity = this.get(id);
		entityManager.remove(entity);
	}
	public Member get(Long id) {
		return entityManager.find(Member.class, id);
	}

	public List<Member> getAll() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Member> root = criteriaQuery.from(Member.class);
		
		TypedQuery<Member> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Member> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public Member getByAccountIgnoreCase(String account) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Member> root = criteriaQuery.from(Member.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (account != null && account.length()!=0) {
			conditions.add(
					criteriaBuilder.equal(
							criteriaBuilder.lower(root.get("account")), account.toLowerCase()
					)
			);
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
		TypedQuery<Member> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);

		List<Member> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}
	}
	
	public Member getByAccount(String account) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Member> root = criteriaQuery.from(Member.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (account != null && account.length()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("account"), account));
		}
		
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
		TypedQuery<Member> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setMaxResults(1);

		List<Member> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}
	}

	public List<Member> getByOrgId(Long orgId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Member> root = criteriaQuery.from(Member.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 一般查詢
		if (orgId != null && orgId.longValue()!=0) {
			conditions.add(criteriaBuilder.equal(root.get("orgId"), orgId.longValue()));
		}
		if(conditions!=null && !conditions.isEmpty()) {
			criteriaQuery.where(conditions.toArray(new Predicate[1]));
		}
		
		TypedQuery<Member> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Member> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}
	
	public List<ViewMember> getList(JSONObject obj) {
		int start = obj.isNull("start") == true ? 0 : obj.getInt("start");
		int maxRows = obj.isNull("maxRows") == true ? 0 : obj.getInt("maxRows");
		boolean dir = obj.isNull("dir") == true ? false : obj.getBoolean("dir");
		String sort = obj.isNull("sort") == true ? "id" : obj.getString("sort");

		long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
		long orgId = obj.isNull("OrgId") == true ? 0 : obj.getLong("OrgId");
		String account = obj.isNull("Account") == true ? null : obj.getString("Account");
		String name = obj.isNull("Name") == true ? null : obj.getString("Name");
		String email = obj.isNull("Email") == true ? null : obj.getString("Email");
		String spareEmail = obj.isNull("SpareEmail") == true ? null : obj.getString("SpareEmail");
		String mobilePhone = obj.isNull("MobilePhone") == true ? null : obj.getString("MobilePhone");
		String cityPhone = obj.isNull("CityPhone") == true ? null : obj.getString("CityPhone");
		String faxPhone = obj.isNull("FaxPhone") == true ? null : obj.getString("FaxPhone");
		String address = obj.isNull("Address") == true ? null : obj.getString("Address");
		String department = obj.isNull("Department") == true ? null : obj.getString("Department");
		String title = obj.isNull("Title") == true ? null : obj.getString("Title");
		Boolean isEnable = obj.isNull("IsEnable") == true ? null : obj.getBoolean("IsEnable");

		long roleId = obj.isNull("RoleId") == true ? 0 : obj.getLong("RoleId");
		long baseOrgId = obj.isNull("baseOrgId") == true ? 0 : obj.getLong("baseOrgId");

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<ViewMember> criteriaQuery = criteriaBuilder.createQuery(ViewMember.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ViewMember> root = criteriaQuery.from(ViewMember.class);
		
		List<Predicate> conditions = new ArrayList<>();
		// 權限控制
		if (roleId == 2) {
			conditions.add(criteriaBuilder.equal(root.get("id"), 1L));
		} else if (roleId == 9 || roleId == 11) {
			conditions.add(criteriaBuilder.equal(root.get("orgId"), baseOrgId));
		}
		
		// 一般查詢
		if (id != 0) {
			conditions.add(criteriaBuilder.equal(root.get("id"), id));
		}
		if (orgId != 0) {
			conditions.add(criteriaBuilder.equal(root.get("orgId"), orgId));
		}
		if (account != null && account.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("account"), "%"+account+"%"));
		}
		if (name != null && name.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
		}
		if (email != null && email.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("email"), "%"+email+"%"));
		}
		if (spareEmail != null && spareEmail.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("spareEmail"), "%"+spareEmail+"%"));
		}
		if (mobilePhone != null && mobilePhone.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("mobilePhone"), "%"+mobilePhone+"%"));
		}
		if (cityPhone != null && cityPhone.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("cityPhone"), "%"+cityPhone+"%"));
		}
		if (faxPhone != null && faxPhone.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("faxPhone"), "%"+faxPhone+"%"));
		}
		if (address != null && address.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("address"), "%"+address+"%"));
		}
		if (department != null && department.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("department"), "%"+department+"%"));
		}
		if (title != null && title.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("title"), "%"+title+"%"));
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
        
		TypedQuery<ViewMember> typedQuery = entityManager.createQuery(criteriaQuery);
		
		typedQuery.setFirstResult(start);
		if (maxRows != 0) {
			typedQuery.setMaxResults(maxRows);
		}
		
		List<ViewMember> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}

	public long getListSize(JSONObject obj) {

		long id = obj.isNull("Id") == true ? 0 : obj.getLong("Id");
		long orgId = obj.isNull("OrgId") == true ? 0 : obj.getLong("OrgId");
		String account = obj.isNull("Account") == true ? null : obj.getString("Account");
		String name = obj.isNull("Name") == true ? null : obj.getString("Name");
		String email = obj.isNull("Email") == true ? null : obj.getString("Email");
		String spareEmail = obj.isNull("SpareEmail") == true ? null : obj.getString("SpareEmail");
		String mobilePhone = obj.isNull("MobilePhone") == true ? null : obj.getString("MobilePhone");
		String cityPhone = obj.isNull("CityPhone") == true ? null : obj.getString("CityPhone");
		String faxPhone = obj.isNull("FaxPhone") == true ? null : obj.getString("FaxPhone");
		String address = obj.isNull("Address") == true ? null : obj.getString("Address");
		String department = obj.isNull("Department") == true ? null : obj.getString("Department");
		String title = obj.isNull("Title") == true ? null : obj.getString("Title");
		Boolean isEnable = obj.isNull("IsEnable") == true ? null : obj.getBoolean("IsEnable");

		long roleId = obj.isNull("RoleId") == true ? 0 : obj.getLong("RoleId");
		long baseOrgId = obj.isNull("baseOrgId") == true ? 0 : obj.getLong("baseOrgId");

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ViewMember> root = criteriaQuery.from(ViewMember.class);
		
		criteriaQuery = criteriaQuery.select(criteriaBuilder.count(root));
		
		List<Predicate> conditions = new ArrayList<>();
		// 權限控制
		if (roleId == 2) {
			conditions.add(criteriaBuilder.equal(root.get("id"), 1L));
		} else if (roleId == 9 || roleId == 11) {
			conditions.add(criteriaBuilder.equal(root.get("orgId"), baseOrgId));
		}
		// 一般查詢
		if (id != 0) {
			conditions.add(criteriaBuilder.equal(root.get("id"), id));
		}
		if (orgId != 0) {
			conditions.add(criteriaBuilder.equal(root.get("orgId"), orgId));
		}
		if (account != null && account.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("account"), "%"+account+"%"));
		}
		if (name != null && name.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
		}
		if (email != null && email.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("email"), "%"+email+"%"));
		}
		if (spareEmail != null && spareEmail.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("spareEmail"), "%"+spareEmail+"%"));
		}
		if (mobilePhone != null && mobilePhone.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("mobilePhone"), "%"+mobilePhone+"%"));
		}
		if (cityPhone != null && cityPhone.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("cityPhone"), "%"+cityPhone+"%"));
		}
		if (faxPhone != null && faxPhone.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("faxPhone"), "%"+faxPhone+"%"));
		}
		if (address != null && address.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("address"), "%"+address+"%"));
		}
		if (department != null && department.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("department"), "%"+department+"%"));
		}
		if (title != null && title.length()!=0) {
			conditions.add(criteriaBuilder.like(root.get("title"), "%"+title+"%"));
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
	
	public List<Member> getByIds(JSONArray arr) {
		List<Long> ids = new ArrayList<Long>();		
		for (int i=0; i < arr.length(); i++) {
			ids.add(arr.getLong(i));
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<Member> root = criteriaQuery.from(Member.class);
		
		Predicate predicate = root.get("id").in(ids);
		criteriaQuery.where(predicate);
	
		TypedQuery<Member> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Member> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}
}