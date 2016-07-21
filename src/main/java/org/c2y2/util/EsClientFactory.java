package org.c2y2.util;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.net.InetAddress;

import org.c2y2.constants.ConfigConstants;
import org.c2y2.constants.ElasticConstants;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsClientFactory {
	private final static Logger LOGGER = LoggerFactory.getLogger(EsClientFactory.class);
	
	private static Client client = null;
	public static String clusterName;
	public static String hosts;
	
	public EsClientFactory() {
		LOGGER.info("client init start");
		try {
			if (client == null) {
				clusterName = ConfigConstants.es_clusterName;
				hosts = ConfigConstants.es_hosts;
				String[] hostsArray=hosts.split(",");
				int hostLenth = hostsArray.length;
				TransportAddress[] addresses =new InetSocketTransportAddress[hostLenth];
				String host="";//ip:port
				String[] hostArray;//ip,port
				for (int i=0;i<hostLenth; i++) {
					host=hostsArray[i];
					if(host!=null&&!host.isEmpty()){
						hostArray = host.split(":");
						if(hostArray.length==2){
							String addressStr = host.split(":")[0];
							int port = Integer.parseInt(host.split(":")[1]);
							addresses[i]=new InetSocketTransportAddress(InetAddress.getByName(addressStr), port);
						}
					}
				}
				if (clusterName != null && !clusterName.equals("")) {
					Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).build();
					client = TransportClient.builder().settings(settings).build().addTransportAddresses(addresses);
				} else {
					client = TransportClient.builder().build().addTransportAddresses(addresses);
				}
				if(client!=null){//初始化索引
					XContentBuilder mapping = mapping();  
			        // 创建一个空索引  
			        client.admin().indices().prepareCreate(ElasticConstants.BaseInfo.INDEX).execute().actionGet();  
			        PutMappingRequest putMapping = Requests.putMappingRequest(ElasticConstants.BaseInfo.INDEX).type(ElasticConstants.BaseInfo.TYPE).source(mapping);  
			        PutMappingResponse response = client.admin().indices().putMapping(putMapping).actionGet();  
			        if (!response.isAcknowledged()) {  
			        	LOGGER.info("Could not define mapping for type");  
			        } else {  
			        	LOGGER.info("Mapping definition for succesfully created.");  
			        }  
				}
			}
		} catch (Exception e) {
			LOGGER.error("client init end "+e.getMessage());
			e.printStackTrace();// 初始化异常失败
		}
		LOGGER.info("client init end");
	}
	
	private static XContentBuilder mapping()throws IOException{
		 XContentBuilder mapping = null;  
	        try {  
	            mapping = jsonBuilder().startObject()  
	                    .startObject(ElasticConstants.BaseInfo.TYPE).startObject("properties")  
	                    .startObject(ElasticConstants.Mapping.account).field("type", "string").endObject()  
	                    .startObject(ElasticConstants.Mapping.home).field("type", "geo_point").endObject() 
	                    .endObject().endObject().endObject();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        return mapping;  
	}
	
	/**
	 * 获取连接
	 * 
	 * @return
	 */
	public static Client getElasticClient() {
		return client;
	}

	public static void closeClient() {
				client.close();
	}

}
