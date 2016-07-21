package org.c2y2.thread;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.c2y2.base.PageModel;
import org.c2y2.domain.User;
import org.c2y2.service.EsService;
import org.c2y2.service.UserMongodbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitDataFactory {
	@Autowired
	public UserMongodbService userMongodbService;
	@Autowired
	public EsService esService;
	public static final BlockingQueue<List<User>> usersBulkQueue = new LinkedBlockingQueue<List<User>>(10);
	public static final ExecutorService executorService= Executors.newFixedThreadPool(20);
	
	public static final AtomicInteger countThreads = new AtomicInteger(0);
	
	public static final AtomicBoolean initflag= new AtomicBoolean(true);
	public static final AtomicInteger insertCount= new AtomicInteger(0);
	
	
	public static final AtomicInteger pageNo= new AtomicInteger(0);//线程执行时默认+1
	public static final AtomicInteger pageCount= new AtomicInteger(0);//总页数
	public static final AtomicInteger getCount= new AtomicInteger(0);//已插入总记录数

	public static final AtomicInteger rowCount= new AtomicInteger(0);//总行数
	
	public static boolean isMigrate= false;
	
	public static int productThreads=3;
	public static int customerThreads=5;
	
	
	public InitDataFactory() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				//是否迁移初始化数据
					rowCount.getAndAdd(userMongodbService.findUserOnPageCount());
					 PageModel<User> pageModel = new PageModel<User>();
					 pageModel.setRowCount(rowCount.get());
					 pageCount.set(pageModel.getTotalPages());
					Runnable initData2EsThread = new InitData2EsThread(esService);
					Runnable initDataGetDataThread = new InitDataFromMongodbThread(userMongodbService);
					if(initflag.getAndSet(false)){
						for(int i=0;i<productThreads;i++){
							executorService.execute(initDataGetDataThread);//数据生产线程开启3个
						}
						for(int i=0;i<customerThreads;i++){//默认	//数据消费线程开启3个
							executorService.execute(initData2EsThread);
							countThreads.incrementAndGet();
						}
					}else{
						if(countThreads.get()<=customerThreads && usersBulkQueue.size()>0){
							for(int i=0;i<customerThreads/2;i++){
								executorService.execute(initData2EsThread);
								countThreads.incrementAndGet();
							}
						}
					}
			}
		}, 30*1000l,5000);//30秒后开启初始化线程,然后每5秒维护一下消费线程
	}
	
	
	
}
