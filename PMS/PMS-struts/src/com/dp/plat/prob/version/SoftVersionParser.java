package com.dp.plat.prob.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SoftVersionParser {
    @JsonIgnore
    @org.codehaus.jackson.annotate.JsonIgnore
    @JSONField(serialize = false, deserialize = false)
    public AbstractSoftVersionStrategy strategy;
    public String type;
    public String series;
	public String version;
	public String mark;
	public Map<String, String> seriesParts = new LinkedHashMap<String, String>();
	public Map<String, String> versionParts = new LinkedHashMap<String, String>();
	public Map<String, String> markPrevParts = new LinkedHashMap<String, String>();
	public Map<String, String> markAllParts = new LinkedHashMap<String, String>();


	public SoftVersionParser() {
		super();
	}

	public SoftVersionParser(AbstractSoftVersionStrategy strategy, String version) {
		this();
		this.strategy = strategy;
		if (strategy != null) {
		    this.type = strategy.getClass().getSimpleName();
		}
		this.version = version;
	}
	
	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
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
	
	public Map<String, String> getSeriesParts() {
        return seriesParts;
    }

    public void setSeriesParts(Map<String, String> seriesParts) {
        this.seriesParts = seriesParts;
    }

    public Collection<Entry<String, String>> getSeriesPartsSet() {
        return new ArrayList<Entry<String, String>>(seriesParts.entrySet());
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
	
	public void putSeriesParts(String part, String series) {
        this.seriesParts.put(part, series);
    }

    public void putSeriesPartsAll(Map<String, String> map) {
        this.seriesParts.putAll(map);
    }

    public String getSeriesParts(String part) {
        return this.seriesParts.get(part);
    }

    public String getSeriesParts(String part, String defaultSeries) {
        return this.seriesParts.getOrDefault(part, defaultSeries);
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
	
	/**
     * 填充系列
     * @param part
     * @param mark
     * @return
     */
    public String fillSeries() {
        if (strategy == null) {
            return null;
        }
        List<String> rangeInheritParts = strategy.getRangeInheritParts();
        Map<String, String> versionParts = this.getVersionParts();
        List<String> series = new ArrayList<String>(rangeInheritParts.size());
        for (String part : rangeInheritParts) {
            String partValue = versionParts.getOrDefault(part, part);
            series.add(partValue);
            this.putSeriesParts(part, partValue);
        }
        this.setSeries(StringUtils.join(series, ""));
        return this.getSeries();
    }

	@Override
	public String toString() {
		return "{" 
		        + (type != null ? "type=" + type + ", " : "")
		        + (series != null ? "series=" + series + ", " : "")
		        + (version != null ? "version=" + version + ", " : "")
				+ (mark != null ? "mark=" + mark + ", " : "")
				+ (seriesParts != null ? "seriesParts=" + seriesParts + ", " : "")
				+ (versionParts != null ? "versionParts=" + versionParts + ", " : "")
				+ (markPrevParts != null ? "markPrevParts=" + markPrevParts + ", " : "")
				+ (markAllParts != null ? "markAllParts=" + markAllParts : "") 
		+ "}";
	}

}