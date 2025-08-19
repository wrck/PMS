package com.dp.plat.prob.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SoftVersionParser {
	public String version;
	public String mark;
	public Map<String, String> versionParts = new LinkedHashMap<String, String>();
	public Map<String, String> markPrevParts = new LinkedHashMap<String, String>();
	public Map<String, String> markAllParts = new LinkedHashMap<String, String>();


	public SoftVersionParser() {
		super();
	}

	public SoftVersionParser(String version) {
		super();
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public Map<String, String> getVersionParts() {
		return versionParts;
	}

	public void setVersionParts(Map<String, String> versionParts) {
		this.versionParts = versionParts;
	}

	public Collection<Entry<String, String>> getVersionPartsSet() {
		return new ArrayList<Entry<String, String>>(versionParts.entrySet());
	}

	public Map<String, String> getMarkPrevParts() {
		return markPrevParts;
	}

	public void setMarkPrevParts(Map<String, String> markPrevParts) {
		this.markPrevParts = markPrevParts;
	}

	public Collection<Entry<String, String>> getMarkPrevPartsSet() {
		return new ArrayList<Entry<String, String>>(markPrevParts.entrySet());
	}

	public Map<String, String> getMarkAllParts() {
		return markAllParts;
	}

	public void setMarkAllParts(Map<String, String> markAllParts) {
		this.markAllParts = markAllParts;
	}

	public Collection<Entry<String, String>> getMarkAllPartsSet() {
		return new ArrayList<Entry<String, String>>(markAllParts.entrySet());
	}

	public void putVersionParts(String part, String version) {
		this.versionParts.put(part, version);
	}

	public void putVersionPartsAll(Map<String, String> map) {
		this.versionParts.putAll(map);
	}

	public String getVersionParts(String part) {
		return this.versionParts.get(part);
	}

	public String getVersionParts(String part, String defaultVersion) {
		return this.versionParts.getOrDefault(part, defaultVersion);
	}

	public void putMarkParts(Map<String, String> markParts, String part, String mark) {
		markParts.put(part, mark);
	}

	public void putMarkPartsAll(Map<String, String> markParts, Map<String, String> map) {
		markParts.putAll(map);
	}

	public String getMarkParts(Map<String, String> markParts, String part) {
		return markParts.get(part);
	}

	public String getMarkParts(Map<String, String> markParts, String part, String defaultMark) {
		return markParts.getOrDefault(part, defaultMark);
	}

	@Override
	public String toString() {
		return "{" + (version != null ? "version=" + version + ", " : "")
				+ (mark != null ? "mark=" + mark + ", " : "")
				+ (versionParts != null ? "versionParts=" + versionParts + ", " : "")
				+ (markPrevParts != null ? "markPrevParts=" + markPrevParts + ", " : "")
				+ (markAllParts != null ? "markAllParts=" + markAllParts : "") + "}";
	}

}