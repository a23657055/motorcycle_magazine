package org.iii.SecBuzzer.template.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.iii.SecBuzzer.template.dao.ArticleOrPictureDAO;
import org.iii.SecBuzzer.template.domain.ArticleOrPictureData;
import org.iii.SecBuzzer.template.vo.ArticleOrPictureDataVO;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ArticleOrPictureDataService {
	
	@Autowired
	private ArticleOrPictureDAO articleOrPictureDAO;
	
	public void insert(String json, Long createId) {
		Date sysdate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			JSONObject obj = new JSONObject(json);
			
			ArticleOrPictureDataVO dataVO = this.getData(json);//產生ID

			ArticleOrPictureData model = new ArticleOrPictureData();
			model.setId(dataVO.getId());
			model.setLocationCode(obj.getString("locationCode"));
			model.setDataType(obj.getString("dataType"));
			model.setEnable(obj.getString("enable"));
			if("A".equals(obj.getString("dataType"))) {
				model.setTitle(obj.getString("title"));
			}else if("I".equals(obj.getString("dataType"))){
				model.setTitle("Images");
			}
			model.setContent(obj.getString("content"));
			model.setReleaseTime(sdf.parse(obj.getString("releaseTime")));
			model.setCreateTime(sysdate);
			model.setUpdateTime(sysdate);
			model.setCreateId(String.valueOf(createId));
			model.setUpdateId(String.valueOf(createId));
			
			articleOrPictureDAO.insert(model);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	public List<ArticleOrPictureData> getImages(String json) {
		JSONObject obj = new JSONObject(json);
		return articleOrPictureDAO.getImages(obj);
	}
	
	public ArticleOrPictureDataVO getData(String json) {
		ArticleOrPictureDataVO dataVO = new ArticleOrPictureDataVO();
		
		JSONObject obj = new JSONObject(json);
			
		Date sysDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		String time = sdf.format(sysDate);
		dataVO.setId(obj.getString("dataType") + time);
		
		return dataVO;
	}
}
