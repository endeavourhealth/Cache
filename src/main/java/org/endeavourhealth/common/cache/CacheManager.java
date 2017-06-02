package org.endeavourhealth.common.cache;

import java.util.ArrayList;
import java.util.List;

public class CacheManager {
	private static List<ICache> caches = new ArrayList<>();

	public static void registerCache(ICache cache) {
		caches.add(cache);
	}

	public static String listCaches() {
		StringBuilder sb = new StringBuilder();

		for(ICache cache : caches) {
			sb.append(
					String.format("Cache [%s] : [%d] items%n", cache.getName(), cache.getSize())
			);
		}

		return sb.toString();
	}

	public static void clearCaches() {
		caches.forEach(ICache::clearCache);
	}
}
