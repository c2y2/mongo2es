package org.c2y2.thread;

import java.util.List;

import org.c2y2.base.PageModel;
import org.c2y2.domain.User;
import org.c2y2.service.UserMongodbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitDataFromMongodbThread implements Runnable {
	private final static Logger LOGGER = LoggerFactory.getLogger(InitDataFromMongodbThread.class);

	private UserMongodbService userMongodbService;

	public InitDataFromMongodbThread(UserMongodbService userMongodbService) {
		this.userMongodbService = userMongodbService;
	}

	@Override
	public void run() {//单线程处理
		List<User> users = null;
		LOGGER.info("row count["+InitDataFactory.rowCount+"] start getData");
		int pageNo =0;
		int getCount = 0;
		while ((pageNo=InitDataFactory.pageNo.incrementAndGet()) <= InitDataFactory.pageCount.get()) {
			try {
				PageModel<User> pages = userMongodbService.findUserOnPage(pageNo, InitDataFactory.rowCount.get());
				users = pages.getDatas();
				if (pages.getDatas() != null && !users.isEmpty()) {
					try {
						InitDataFactory.usersBulkQueue.put(users);
						LOGGER.info("迁移数据放入队列 count["+users.size()+"] start getData currentPage["+pageNo+"]");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					getCount = InitDataFactory.getCount.addAndGet(users.size());
					LOGGER.info("迁移数据放入队列 currentPage["+pageNo+"] 已入队列总数["+getCount+"]");
				}
			} catch (Exception e) {
				LOGGER.warn("error["+e.getMessage()+"]");
				LOGGER.warn("异常页面[处理数据已pageNo为准] getCount["+InitDataFactory.getCount.get()+"] rowCount["+InitDataFactory.rowCount+"] pageNo["+pageNo+"]");
			}
		}
		LOGGER.info("getdata count["+InitDataFactory.getCount.get()+"] end getData");
	}

}
