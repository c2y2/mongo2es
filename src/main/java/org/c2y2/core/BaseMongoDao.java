package org.c2y2.core;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @param <T>
 */
public abstract class BaseMongoDao<T> {
	@Autowired
	protected MongoTemplate mongoTemplate;

	/**
	 * 获取Class<T>
	 * @return
	 */
	protected abstract Class<T> getEntityClass();
	/**
	 * 获取集合名称
	 * @return
	 */
	protected abstract String getCollectionName();
	
	public abstract Long count(Query query);

	public void insert(T item) {
		mongoTemplate.insert(item, getCollectionName());
	}
	

	public void save(T item) {
		mongoTemplate.save(item, getCollectionName());
	}

	public void updateFirst(Query query, Update update) {
		mongoTemplate.updateFirst(query, update, getCollectionName());
	}

	public void updateMulti(Query query, Update update) {
		mongoTemplate.updateMulti(query, update, getCollectionName());
	}

	public T find(String id) {
		return mongoTemplate.findOne(new Query(Criteria.where("id").is(id)), getEntityClass(), getCollectionName());
	}

	public T find(Query query) {
		return mongoTemplate.findOne(query, getEntityClass(), getCollectionName());
	}

	public List<T> findMany(Query query) {
		return mongoTemplate.find(query, getEntityClass(), getCollectionName());
	}

	public List<T> findAll() {
		return mongoTemplate.find(new Query(), getEntityClass(), getCollectionName());
	}

	public void remove(String id) {
		mongoTemplate.remove(new Query(Criteria.where("id").is(id)), getCollectionName());
	}

	public void removeByCarIdTime(String carId, Date beginDate, Date endDate) {
		mongoTemplate.remove(new Query(Criteria.where("carId").is(carId).
				andOperator(Criteria.where("time").gte(beginDate),
						Criteria.where("time").lte(endDate))),
				getCollectionName());
	}

	public void removeAll() {
		mongoTemplate.remove(new Query(), getCollectionName());
	}

	public void insertBatch(List<T> item) {
		mongoTemplate.insert(item, getCollectionName());
	}
}
