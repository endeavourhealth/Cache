package org.endeavourhealth.common.cache;

public interface ICache {
	String getName();
	long getSize();
	void clearCache();
}
