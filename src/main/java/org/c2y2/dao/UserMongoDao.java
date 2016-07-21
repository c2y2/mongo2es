package org.c2y2.dao;

import org.c2y2.core.BaseMongoDao;
import org.c2y2.domain.User;
import org.springframework.data.mongodb.core.query.Query;

public class UserMongoDao extends BaseMongoDao<User> {

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}

	@Override
	protected String getCollectionName() {
		return "user";
	}

	@Override
	public Long count(Query query) {
		return super.mongoTemplate.count(query, getCollectionName());
	}

}
