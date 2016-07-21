package org.c2y2.thread;

import java.util.List;

import org.c2y2.domain.User;
import org.c2y2.service.EsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitData2EsThread implements Runnable{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(InitData2EsThread.class);
		
	private EsService esService;
	
	
	public InitData2EsThread(EsService esService) {
		this.esService = esService;
	}


	@Override
	public void run() {
		List<User> users = null;
		//单批次插入大小由后去时数据页大小控制
		LOGGER.info("执行批次入es线程 start");
		boolean flag = false;
		while((users=InitDataFactory.usersBulkQueue.poll())!=null){
			try {
				flag = esService.batchSaveIndex(users);
				LOGGER.info("batinsert count["+users.size()+"] ["+flag+"] total["+InitDataFactory.insertCount.incrementAndGet()+"]");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int countThreads = InitDataFactory.countThreads.decrementAndGet();
		LOGGER.info("当前剩余消费线程数["+countThreads+"]");
		LOGGER.info("执行批次入es线程 end");
	}

}
