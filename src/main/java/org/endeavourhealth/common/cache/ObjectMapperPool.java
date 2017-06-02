package org.endeavourhealth.common.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

public class ObjectMapperPool implements ICache {
	private static ObjectMapperPool instance;

	public static synchronized ObjectMapperPool getInstance() {
		if (instance == null) {
			instance = new ObjectMapperPool();
			CacheManager.registerCache(instance);
		}
		return instance;
	}

	private final Stack<ObjectMapper> pool = new Stack<>();

	@Override
	public String getName() { return "ObjectMapperPool"; }

	@Override
	public long getSize() { return pool.size(); }

	@Override
	public synchronized void clearCache() {
		pool.clear();
	}

	public <T> T readValue(String content, Class<T> valueType) throws IOException {
		ObjectMapper objectMapper = pop();
		T result = objectMapper.readValue(content, valueType);
		push(objectMapper);
		return result;
	}

	public <T> T readValue(String content, TypeReference valueTypeRef) throws IOException {
		ObjectMapper objectMapper = pop();
		T result = objectMapper.readValue(content, valueTypeRef);
		push(objectMapper);
		return result;
	}

	public <T> T readValue(InputStream stream, TypeReference valueTypeRef) throws IOException {
		ObjectMapper objectMapper = pop();
		T result = objectMapper.readValue(stream, valueTypeRef);
		push(objectMapper);
		return result;
	}

	public JsonNode readTree(String content) throws IOException {
		ObjectMapper objectMapper = pop();
		JsonNode result = objectMapper.readTree(content);
		push(objectMapper);
		return result;
	}

	public String writeValueAsString(Object value) throws JsonProcessingException {
		ObjectMapper objectMapper = pop();
		String result = objectMapper.writeValueAsString(value);
		push(objectMapper);
		return result;
	}

	private synchronized void push(ObjectMapper objectMapper) {
		pool.push(objectMapper);
	}

	private synchronized ObjectMapper pop() {
		if (pool.size() > 0)
			return pool.pop();
		else
			return new ObjectMapper();
	}
}
