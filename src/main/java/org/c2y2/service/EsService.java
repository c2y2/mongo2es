package org.c2y2.service;

import java.util.List;

import org.c2y2.domain.User;

public interface EsService {
	/**
	 * 新增
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Boolean saveIndex(User user)throws Exception;
	/**
	 * 删除索引
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Boolean delIndex(Long id)throws Exception;
	/**
	 * 更新或者新增索引
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Boolean updateOrSaveIndex(User user)throws Exception;
	/**
	 * 批量索引
	 * @return
	 * @throws Exception
	 */
	public Boolean batchSaveIndex(List<User> users)throws Exception;
	/**
	 * 查询user
	 * @param account
	 * @return
	 * @throws Exception
	 */
	public List<User> queryUser(String account)throws Exception;
}
