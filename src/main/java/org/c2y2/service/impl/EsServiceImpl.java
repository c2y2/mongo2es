package org.c2y2.service.impl;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.c2y2.constants.ElasticConstants;
import org.c2y2.domain.User;
import org.c2y2.service.EsService;
import org.c2y2.util.EsClientFactory;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class EsServiceImpl implements EsService {
	private static final Logger logger = LoggerFactory.getLogger(EsServiceImpl.class);
	
	/**
	 * 新增
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@Override
	public Boolean saveIndex(User user) throws Exception {
		IndexResponse response = EsClientFactory.getElasticClient().prepareIndex(ElasticConstants.BaseInfo.INDEX,
				ElasticConstants.BaseInfo.TYPE,user.getId())
		.setSource(builderSource(user)).get();
		return response.isCreated();
	}
	
	private static XContentBuilder builderSource(User user) throws IOException {
		return jsonBuilder()
				.startObject()
					.field(ElasticConstants.Mapping.account, user.getAccount())
					.startArray(ElasticConstants.Mapping.home)
						.latlon(user.getLat(),user.getLon()).endArray()
				.endObject();
	}
	
	/**
	 * 删除索引
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@Override
	public Boolean delIndex(Long id) throws Exception {
		DeleteResponse response = EsClientFactory.getElasticClient()
				.prepareDelete(ElasticConstants.BaseInfo.INDEX,ElasticConstants.BaseInfo.TYPE,id+"").get();
		return response.isFound();
	}
	
	/**
	 * 更新或者新增索引
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@Override
	public Boolean updateOrSaveIndex(User user) throws Exception {
		String id =user.getId()+"";
		XContentBuilder builder = builderSource(user);
		IndexRequest indexRequest = new IndexRequest(ElasticConstants.BaseInfo.INDEX,
				ElasticConstants.BaseInfo.TYPE, id)
		        .source(builder);
		UpdateRequest updateRequest = new UpdateRequest(ElasticConstants.BaseInfo.INDEX,
				ElasticConstants.BaseInfo.TYPE, id)
		        .doc(builder)
		        .upsert(indexRequest); 
		UpdateResponse updateResponse = EsClientFactory.getElasticClient().update(updateRequest).get();
		return updateResponse.isCreated()||updateResponse.getGetResult().isExists();
	}
	/**
	 * 批量索引
	 * @return
	 * @throws Exception
	 */
	@Override
	public Boolean batchSaveIndex(List<User> users) throws Exception {
		if(users!=null && !users.isEmpty()){
			Client client = EsClientFactory.getElasticClient();
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for (User user : users) {
					bulkRequest.add(client
							.prepareIndex(ElasticConstants.BaseInfo.INDEX,
									ElasticConstants.BaseInfo.TYPE, 
									user.getId()+"")
							.setSource(builderSource(user)));
			}
			BulkResponse bulkResponse = bulkRequest.get();
			if (bulkResponse.hasFailures()) {
				logger.info("batch faile traceLog["+bulkResponse.buildFailureMessage()+"]");
			   return false;
			}
		}
		return true;
	}

	
	@Override
	public List<User> queryUser(String account) throws Exception {
		BoolQueryBuilder boleanQueryBuilder = QueryBuilders.boolQuery();
		boleanQueryBuilder.must(QueryBuilders.matchQuery(ElasticConstants.Mapping.account, account));
		SearchRequestBuilder builder =EsClientFactory.getElasticClient()
				.prepareSearch(ElasticConstants.BaseInfo.INDEX)
				.setTypes(ElasticConstants.BaseInfo.TYPE)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(boleanQueryBuilder) 
				.setFrom(0).setSize(1);
		logger.info("query body["+builder.toString()+"]");
		SearchResponse response =  builder.execute().actionGet();
		String resource;
		User user = null;
		List<User> users = new ArrayList<User>();
		for (SearchHit hit : response.getHits()) {
			resource = hit.getSourceAsString();
			JSONObject jsonObject = JSONObject.parseObject(resource);
			user = new User();
			logger.info("result["+jsonObject.toJSONString()+"]");
			users.add(user);
		}
		return users;
	}

}
