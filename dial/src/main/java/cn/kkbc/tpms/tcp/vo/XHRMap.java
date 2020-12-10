package cn.kkbc.tpms.tcp.vo;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * 改造的用于链式构造JSON数据的工具类
 * 
 * @author hylexus
 *
 */
public class XHRMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public static final int UNAUTHORIZED = HttpServletResponse.SC_UNAUTHORIZED;

	public XHRMap() {
	}

	public XHRMap(boolean empty) {
		this();
		if (!empty)
			this.clear();
	}

	public XHRMap success() {
		this.put("success", true);
		return this;
	}

	public XHRMap failure() {
		this.put("success", false);
		return this;
	}

	public XHRMap msg(String msg) {
		this.put("msg", msg);
		return this;
	}

	public XHRMap code(Integer code) {
		this.put("code", code);
		return this;
	}

	public XHRMap kv(String k, Object v) {
		this.put(k, v);
		return this;
	}

	public XHRMap addAll(Map<String, Object> map) {
		this.putAll(map);
		return this;
	}
}
