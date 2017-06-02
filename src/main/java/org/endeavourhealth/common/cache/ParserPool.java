package org.endeavourhealth.common.cache;

import org.hl7.fhir.instance.formats.IParser;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.Type;

import java.util.Stack;

public class ParserPool implements ICache {
	private static ParserPool instance;

	public static synchronized ParserPool getInstance() {
		if (instance == null) {
			instance = new ParserPool();
			CacheManager.registerCache(instance);
		}
		return instance;
	}

	private final Stack<IParser> jsonPool = new Stack<>();
	private final Stack<IParser> xmlPool = new Stack<>();

	@Override
	public String getName() { return "ParserPool"; }

	@Override
	public long getSize() { return jsonPool.size() + xmlPool.size(); }

	@Override
	public synchronized void clearCache() {
		jsonPool.clear();
		xmlPool.clear();
	}

	public String composeString(Resource resource) throws Exception {
		return this.composeString(null, resource);
	}

	public String composeString(String contentType, Resource resource) throws Exception {
		IParser parser = pop(contentType);
		try {
			return parser.composeString(resource);
		} finally {
			push(parser);
		}
	}

	public String composeString(Type type, String contentType) throws Exception {
		IParser parser = pop(contentType);
		try {
			return parser.composeString(type, contentType);
		} finally {
			push(parser);
		}
	}

	public Resource parse(String data) throws Exception {
		return this.parse(null, data);
	}

	public Resource parse(String contentType, String data) throws Exception {
		IParser parser = pop(contentType);
		try {
			return parser.parse(data);
		} finally {
			push(parser);
		}
	}

	public Type parseType(String json, String contentType) throws Exception {
		IParser parser = pop(contentType);
		try {
			return parser.parseType(json, contentType);
		} finally {
			push(parser);
		}
	}

	private IParser pop(String contentType) {
		if (contentType == null || contentType.isEmpty())
			return jsonPop();

		if ("text/xml".equals(contentType) || "application/xml".equals(contentType))
			return xmlPop();

		return jsonPop();
	}

	private synchronized void push(IParser parser) {
		if (parser instanceof JsonParser)
			jsonPool.push(parser);
		else
			xmlPool.push(parser);
	}

	private synchronized IParser jsonPop() {
		if (jsonPool.size() > 0)
			return jsonPool.pop();
		else
			return new JsonParser();
	}

	private synchronized IParser xmlPop() {
		if (xmlPool.size() > 0)
			return xmlPool.pop();
		else
			return new XmlParser();
	}
}
