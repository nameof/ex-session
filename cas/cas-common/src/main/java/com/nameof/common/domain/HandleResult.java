package com.nameof.common.domain;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author ChengPan
 * 2018年1月15日
 */
public class HandleResult implements Serializable {
	
	private static final long serialVersionUID = 3960913710864539549L;
	/**
	 * 返回结果状态
	 */
	private boolean state;
	/**
	 * 返回消息
	 */
	private String info;

	/**
	 * 返回额外数据
	 */
	private HashMap<String, Object> content;
	
	public HandleResult() {
		this.state = true;
		this.info = "";
	}

	public static HandleResult success(String info){
		HandleResult hr = new HandleResult();
		hr.setState(true);
		hr.setInfo(info);
		return hr;
	}
	
	public static HandleResult success(){
		HandleResult hr = new HandleResult();
		hr.setState(true);
		return hr;
	}
	
	public static HandleResult error(){
		HandleResult hr = new HandleResult();
		hr.setState(false);
		return hr;
	}

	public static HandleResult error(String info){
		HandleResult hr = new HandleResult();
		hr.setState(false);
		hr.setInfo(info);
		return hr;
	}

	public void put(String key, Object value) {
		if (content == null) {
			content = new HashMap<String, Object>();
		}
		content.put(key, value);
	}
	
	public Object get(String key) {
		if (content != null) {
			return content.get(key);
		}
		else {
			return null;
		}
	}
	
	public String getString(String key) {
		if (content != null) {
			return (String) content.get(key);
		}
		else {
			return null;
		}
	}
	
	public Boolean getBoolean(String key) {
		if (content != null) {
			return (boolean) content.get(key);
		}
		else {
			return null;
		}
	}
	
	public Integer getInteger(String key) {
		if (content != null) {
			return (Integer) content.get(key);
		}
		else {
			return null;
		}
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public HashMap<String, Object> getContent() {
		return content;
	}

	public void setContent(HashMap<String, Object> content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "HandleResult [state=" + state + ", info=" + info + ", content=" + content + "]";
	}
	
}