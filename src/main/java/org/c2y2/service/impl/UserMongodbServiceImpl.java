package org.c2y2.service.impl;

import java.util.List;

import org.c2y2.base.PageModel;
import org.c2y2.constants.BukConstants;
import org.c2y2.dao.UserMongoDao;
import org.c2y2.domain.User;
import org.c2y2.service.UserMongodbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class UserMongodbServiceImpl implements UserMongodbService {
	@Autowired
	private UserMongoDao userMongoDao;

	@Override
	public PageModel<User> findUserOnPage(int pageNo,int pageCount) {
		Query query = new Query();
		PageModel<User> page = new PageModel<User>();
		page.setRowCount(pageCount);
		page.setPageNo(pageNo);
		page.setPageSize(BukConstants.bulkSize);//设置批量大小
		query.skip(page.getSkip());
		query.limit(page.getPageSize());
		List<User> users = userMongoDao.findMany(query);
		page.setDatas(users);
		return page;
	}
	
	/**
	 * 迁移数据总数
	 */
	@Override
	public Integer findUserOnPageCount() {
		Query query = new Query();
		Long count = userMongoDao.count(query);
		if(count!=null){
			return count.intValue();
		}else{
			return 0;
		}
	}
	
	

}
