package org.c2y2.constants;

public interface ElasticConstants {
	
	
	public interface Mapping{
		
		
		public static final String account = "account";
		
		public static final String home = "home";
		/**
		 * 经度
		 */
		public static final String lat_common = "lat";
		/**
		 * 维度
		 */
		public static final String lon_common = "lon";

	}
	
	public interface BaseInfo{
		/**
		 * 索引名称
		 */
		public static final String INDEX = "appserver";
		/**
		 * type名
		 */
		public static final String TYPE = "user";
	}
	
}
