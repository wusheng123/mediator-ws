package com.mytijian.mediator.service.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mytijian.mediator.api.model.AgentInfo;
import com.mytijian.mediator.api.service.AgentInfoService;

@Service("agentInfoService")
public class AgentInfoServiceImpl implements AgentInfoService {

	private Logger logger = LoggerFactory.getLogger(AgentInfoServiceImpl.class);

	@Resource(name = "mongoTemplate")
	private MongoTemplate mongoTemplate;

	private final String COLLECTION_NAME = "agentInfo";

	@Override
	public void addAgentInfo(Map<String, Object> map) {
		mongoTemplate.insert(getValue(map), COLLECTION_NAME);
	}

	@Override
	public void updateAgentInfo(Integer hospitalId, Map<String, Object> map) {
		DBCollection coll = mongoTemplate.getCollection(COLLECTION_NAME);
		DBObject doc = new BasicDBObject("$set", getValue(map));
		coll.update(getCondition(hospitalId), doc, false, true);
	}

	@Override
	public DBObject getByHospital(Integer hospitalId) {
		DBCollection coll = mongoTemplate.getCollection(COLLECTION_NAME);
		return coll.findOne(getCondition(hospitalId));
	}

	private DBObject getCondition(Integer hospitalId) {
		DBObject cond = new BasicDBObject();
		cond.put("hospitalId", new BasicDBObject("$eq", hospitalId));
		return cond;
	}

	private BasicDBObject getValue(Map<String, Object> map) {
		BasicDBObject dbo = new BasicDBObject();
		for (String key : map.keySet()) {
			dbo.put(key, map.get(key));
		}

		return dbo;
	}

	@Override
	public void addAgentInfo(AgentInfo info) {
		try {
			this.addAgentInfo(introspect(info));
		} catch (Exception e) {
			logger.error("add agent info", e);
		}

	}

	private Map<String, Object> introspect(Object obj) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		BeanInfo info = Introspector.getBeanInfo(obj.getClass());
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			Method reader = pd.getReadMethod();
			if (reader != null && !pd.getName().equals("class"))
				result.put(pd.getName(), reader.invoke(obj));
		}
		return result;
	}

	@Override
	public void updateAgentInfo(Integer hospitalId, AgentInfo info) {
		try {

			this.updateAgentInfo(hospitalId, introspect(info));
		} catch (Exception e) {
			logger.error("add agent info", e);
		}

	}

	@Override
	public List<DBObject> getAll() {
		try {
			DBCollection coll = mongoTemplate.getCollection(COLLECTION_NAME);
			List<DBObject> list = coll.find().toArray();
			return list;
		} catch (MongoException e) {
			logger.error("mongo connect error", e);
		} catch (Exception e) {
			logger.error("mongo connect error", e);
		}

		return Collections.emptyList();

	}

}
