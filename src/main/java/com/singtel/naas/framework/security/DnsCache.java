package com.singtel.naas.framework.security;

public class DnsCache {
	public static void disableNegativeCache() {
		setNegativeCache("0");
	}
	
	public static void cacheOneMinute() {
		setPositiveCache("60");
	}
	
	public static void setPositiveCache(String period) {
		java.security.Security.setProperty("networkaddress.cache.ttl", period);
	}
	
	public static void setNegativeCache(String period) {
		java.security.Security.setProperty("networkaddress.cache.negative.ttl", period);
	}
}
