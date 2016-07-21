package org.c2y2.service;

import org.c2y2.base.PageModel;
import org.c2y2.domain.User;

public interface UserMongodbService {
	/**
	 * 分页获取数据
	 * @param pageNo
	 * @param pageCount
	 * @return
	 */
	public PageModel<User> findUserOnPage(int pageNo,int pageCount);
	/**
	 * 需要迁移的总数
	 * @return
	 */
	public Integer findUserOnPageCount();
}
