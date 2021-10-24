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

import org.iii.SecBuzzer.template.domain.ArticleOrPictureData;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ArticleOrPictureDAO {
	@PersistenceContext
    private EntityManager entityManager;
	
	public void insert(ArticleOrPictureData model) {
		entityManager.persist(model);
	}
	
	public List<ArticleOrPictureData> getImages(JSONObject obj) {
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		// createQuery()方法參數是criteriaQuery最後回傳值的型別
		CriteriaQuery<ArticleOrPictureData> criteriaQuery = criteriaBuilder.createQuery(ArticleOrPictureData.class);
		// from()方法設定criteriaQuery查詢的table，類似SQL的from xxxTable
		Root<ArticleOrPictureData> root = criteriaQuery.from(ArticleOrPictureData.class);
		
		List<Predicate> conditions = new ArrayList<>();
		//查詢條件
		conditions.add(criteriaBuilder.equal(root.get("dataType"), obj.getString("dataType")));
		conditions.add(criteriaBuilder.equal(root.get("enable"), obj.getString("enable")));
		conditions.add(criteriaBuilder.equal(root.get("title"), obj.getString("title")));
		
		criteriaQuery.where(conditions.toArray(new Predicate[1]));
        
		TypedQuery<ArticleOrPictureData> typedQuery = entityManager.createQuery(criteriaQuery);
		
		List<ArticleOrPictureData> result = typedQuery.getResultList();
		if (result != null && !result.isEmpty()) {
			return result;
		} else {
			return null;
		}
	}
}
