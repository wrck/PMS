package com.dp.plat.prob.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractSoftVersionStrategy {
    
    // 判断该解析器是否适用于当前输入
    public boolean matches(String softVersion) {
        Matcher matcher = getPattern().matcher(softVersion);
        boolean isFind = false;
        while (matcher.find()) {
            String full = matcher.group().trim();
            if ("".equals(full)) {
                continue;
            }
            isFind = true;
            break;
        }
        return isFind;
    }
    
    protected abstract String getRegex();

    // 获取当前解析器使用的正则表达式 Pattern
    public abstract Pattern getPattern();
    
    public abstract Pattern[] getRangePatterns();
    
    public abstract Map<String, Integer> getPartIndexMap();

    public abstract Map<Integer, String> getIndexMarkMap();

    public abstract Map<Integer, String> getIndexMarkMapStart();

    public abstract Map<Integer, String> getIndexMarkMapEnd();

    public abstract List<String> getRangeInheritParts();

    // 对输入进行预处理（如替换特殊符号）
    protected String preprocess(String softVersion) {
        if (softVersion != null && (softVersion.contains("-") || softVersion.contains("—"))) {
            String oldSoftVersion = new String(softVersion);
            String newSoftVersion = new String(softVersion);
            List<SoftVersionParser> versionParser = createSoftVersionParser(newSoftVersion);
            for (SoftVersionParser softVersionParserResult : versionParser) {
                String version = softVersionParserResult.getVersion();
                String replacedVersion = version.replaceAll("[-|—]", "\\$\\$_\\$\\$");
                newSoftVersion = newSoftVersion.replace(version, replacedVersion);
            }
            softVersion = newSoftVersion.replaceAll("[-|—]", "~").replace("$$_$$", "-");
        }
        return softVersion;
    }

    public final List<SoftVersionParser> createSoftVersionParser(String softVersion) {
        return createSoftVersionParser(softVersion, false);
    }
    
    public final List<SoftVersionParser> createSoftVersionParser(String softVersion, boolean defaultMarkAll) {
        return createSoftVersionParser(softVersion, defaultMarkAll, getIndexMarkMap());
    }
    
    public final List<SoftVersionParser> createSoftVersionParser(String softVersion, Map<Integer, String> markMap) {
        return createSoftVersionParser(softVersion, false, markMap);
    }
    
    public List<SoftVersionParser> createSoftVersionParser(String softVersion, boolean defaultMarkAll, Map<Integer, String> markMap) {
        List<SoftVersionParser> softVersionParserResults = new ArrayList<SoftVersionParser>();
        Map<String, List<String>> softVersionMap = new LinkedHashMap<String, List<String>>();
        Matcher matcher = getPattern().matcher(softVersion);
        while (matcher.find()) {
            int count = matcher.groupCount();
            String version = matcher.group().trim();
            if ("".equals(version) && matcher.start() > 0) {
                continue;
            }
            SoftVersionParser softVersionParserResult = new SoftVersionParser(this, version);
            List<String> softVersions = new ArrayList<String>();
            softVersionMap.put(version, softVersions);
            StringBuilder marksAll = new StringBuilder();
            StringBuilder marksPrev = new StringBuilder();
            Map<String, String> prevMarks = new LinkedHashMap<String, String>(count);
            for (Entry<String, Integer> part : getPartIndexMap().entrySet()) {
                String key = part.getKey();
                Integer index = part.getValue();
                String group = matcher.group(index);
                String mark = group != null ? group : markMap.get(index);
                mark = this.processMark(key, mark);
                softVersionParserResult.putVersionParts(key, group);
                this.putMarkParts(softVersionParserResult.markAllParts, key, mark);
                prevMarks.put(key, mark);
                if (group != null) {
                    softVersionParserResult.putMarkPartsAll(softVersionParserResult.markPrevParts, prevMarks);
                    marksPrev.append(String.join("", prevMarks.values()));
                    prevMarks.clear();
                }
                marksAll.append(mark);
            }
            String markVersion = defaultMarkAll ? marksAll.toString() : marksPrev.toString();
            softVersionParserResult.setVersion(StringUtils.join(softVersionParserResult.versionParts.values(), ""));
            softVersionParserResult.setMark(markVersion);
            softVersionParserResult.fillSeries();
            softVersionParserResults.add(softVersionParserResult);
            softVersions.add(markVersion);
        }
        return softVersionParserResults;
    }
    
    public final Map<String, Map<String, List<SoftVersionParser>>> createSoftVersionRangeParsers(String softVersion) {
        return createSoftVersionRangeParsers(softVersion, "", false);
    }
    
    public final Map<String, Map<String, List<SoftVersionParser>>> createSoftVersionRangeParsers(String softVersion, String fixedSoftVersionTypes) {
        return createSoftVersionRangeParsers(softVersion, fixedSoftVersionTypes, false);
    }
    
    public Map<String, Map<String, List<SoftVersionParser>>> createSoftVersionRangeParsers(String softVersion, String fixedSoftVersionTypes, boolean defaultMarkAll) {
        // 预处理
        softVersion = preprocess(softVersion);
        
        Map<String, String> fixedInheritVersions = Collections.emptyMap();
        Map<String, String> fixedInheritMarks = Collections.emptyMap();
        if (StringUtils.isNotBlank(fixedSoftVersionTypes)) {
            List<SoftVersionParser> fixedSoftVersionMap = createSoftVersionParser(fixedSoftVersionTypes, true);
            if (!fixedSoftVersionMap.isEmpty()) {
                fixedInheritVersions = fixedSoftVersionMap.get(0).getVersionParts();
                fixedInheritMarks = fixedSoftVersionMap.get(0).getMarkPrevParts();
            }
        }
        
        Map<String, Map<String, List<SoftVersionParser>>> softVersionMap = new LinkedHashMap<String, Map<String, List<SoftVersionParser>>>();
        Map<String, List<SoftVersionParser>> rangesMap = new LinkedHashMap<String, List<SoftVersionParser>>();
        for (Pattern pattern : getRangePatterns()) {
            Matcher matcher = pattern.matcher(softVersion);
            while (matcher.find()) {
                int count = matcher.groupCount();
                String full = matcher.group().trim();
                if ("".equals(full)) {
                    continue;
                }
                // 用来修正填入的数据，使输入的内容跟解析的结果保持一致
                String oldFull = full;
                List<SoftVersionParser> rangeMap = new ArrayList<SoftVersionParser>();
                String prevGroup = null;
                Map<String, String> prevInheritVersions = new HashMap<String, String>();
                Map<String, String> prevInheritMarks = new HashMap<String, String>();
                boolean isPrevMarkAll = false;
                List<SoftVersionParser> prevMap = null;
                for (int index = 1; index <= count; index++) {
                    String group = matcher.group(index);
                    group = (group != null ? group : "").trim();
                    
//                  // 如果为空，则默认 
//                  group = StringUtils.defaultIfBlank(group, softVersionTypes);
                    
                    boolean isStart = index % 2 == 1;
                    boolean isMarkAll = "".equals(group) ? defaultMarkAll || (!isStart && !"".equals(prevGroup)) : defaultMarkAll;
//                  List<SoftVersionParser> tempMap = createSoftVersionParser(group, "".equals(group) ? defaultMarkAll || !isStart : defaultMarkAll);
//                    Map<Integer, String> markMap = isStart ? getIndexMarkMapStart() : getIndexMarkMapEnd();
//                    List<SoftVersionParser> tempMap = createSoftVersionParser(group, isMarkAll, markMap);
                    List<SoftVersionParser> tempMap = createSoftVersionParser(group, isMarkAll);
                    for (SoftVersionParser softVersionParserResult : tempMap) {
                        for (String inheritPart : getRangeInheritParts()) {
                            String fixedInheritVersion = fixedInheritVersions.get(inheritPart);
                            String fixedInheritMark = fixedInheritMarks.get(inheritPart);
                            String inheritVersion = prevInheritVersions.get(inheritPart);
                            String inheritMark = prevInheritMarks.get(inheritPart);
                            inheritVersion = StringUtils.defaultIfBlank(fixedInheritVersion, StringUtils.defaultIfBlank(softVersionParserResult.getVersionParts(inheritPart), inheritVersion));
                            inheritMark = StringUtils.defaultIfBlank(StringUtils.defaultIfBlank(fixedInheritMark, StringUtils.defaultIfBlank(inheritVersion, softVersionParserResult.getMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart))), inheritMark);
                            softVersionParserResult.putVersionParts(inheritPart, inheritVersion);
                            if (StringUtils.isNotBlank(inheritMark)) {
                                this.putMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart, inheritMark);
                                this.putMarkParts(softVersionParserResult.getMarkAllParts(), inheritPart, inheritMark);
                            }
                            prevInheritVersions.put(inheritPart, inheritVersion);
                            prevInheritMarks.put(inheritPart, inheritMark);
                        }
                        softVersionParserResult.setVersion(StringUtils.join(softVersionParserResult.getVersionParts().values(), ""));
                        softVersionParserResult.setMark(StringUtils.join((isMarkAll ? softVersionParserResult.getMarkAllParts() : softVersionParserResult.getMarkPrevParts()).values(), ""));
                        softVersionParserResult.fillSeries();
                        
                        // 用来修正填入的数据，使输入的内容跟解析的结果保持一致
                        if (StringUtils.isNotBlank(group)) {
                            full = full.replaceFirst(group, softVersionParserResult.getVersion());
                        }
                    }
                    if (!isStart && prevMap != null) {
                        for (SoftVersionParser softVersionParserResult : prevMap) {
                            for (String inheritPart : getRangeInheritParts()) {
                                String fixedInheritVersion = fixedInheritVersions.get(inheritPart);
                                String fixedInheritMark = fixedInheritMarks.get(inheritPart);
                                String inheritVersion = prevInheritVersions.get(inheritPart);
                                String inheritMark = prevInheritMarks.get(inheritPart);
                                inheritVersion = StringUtils.defaultIfBlank(fixedInheritVersion, StringUtils.defaultIfBlank(softVersionParserResult.getVersionParts(inheritPart), inheritVersion));
                                inheritMark = StringUtils.defaultIfBlank(fixedInheritMark, StringUtils.defaultIfBlank(inheritVersion, softVersionParserResult.getMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart)));
                                softVersionParserResult.putVersionParts(inheritPart, inheritVersion);
                                if (StringUtils.isNotBlank(inheritMark)) {
                                    this.putMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart, inheritMark);
                                    this.putMarkParts(softVersionParserResult.getMarkAllParts(), inheritPart, inheritMark);
                                }
                                prevInheritVersions.put(inheritPart, inheritVersion);
                                prevInheritMarks.put(inheritPart, inheritMark);
                            }
                            softVersionParserResult.setVersion(StringUtils.join(softVersionParserResult.getVersionParts().values(), ""));
                            softVersionParserResult.setMark(StringUtils.join((isPrevMarkAll ? softVersionParserResult.getMarkAllParts() : softVersionParserResult.getMarkPrevParts()).values(), ""));
                        }
                    } else if (prevInheritVersions.containsValue(null)) {
                        prevMap = tempMap;
                        isPrevMarkAll = isMarkAll;
                    }
                    rangeMap.addAll(tempMap);
                    prevGroup = group;
                }
                rangesMap.put(full, rangeMap);
                
                // 用来修正填入的数据，使输入的内容跟解析的结果保持一致
                softVersion = softVersion.replaceFirst(oldFull, full);
            }
        }
        if (rangesMap.isEmpty()) {
            List<SoftVersionParser> tempMap = createSoftVersionParser(softVersion);
            for (SoftVersionParser softVersionParserResult : tempMap) {
                List<SoftVersionParser> rangeMap = new ArrayList<SoftVersionParser>();
                if ("".equals(softVersionParserResult.getVersion())) {
                    continue;
                }
                // 如果有固定的版本前缀，则以固定的版本前缀为准，同时修正输入的版本中错误的版本
                String oldVerison = softVersionParserResult.getVersion();
                for (String inheritPart : getRangeInheritParts()) {
                    String fixedInheritVersion = fixedInheritVersions.get(inheritPart);
                    String fixedInheritMark = fixedInheritMarks.get(inheritPart);
                    String inheritVersion = fixedInheritVersion;
                    String inheritMark = fixedInheritVersion;
                    inheritVersion = StringUtils.defaultIfBlank(fixedInheritVersion, StringUtils.defaultIfBlank(softVersionParserResult.getVersionParts(inheritPart), inheritVersion));
                    inheritMark = StringUtils.defaultIfBlank(StringUtils.defaultIfBlank(fixedInheritMark, StringUtils.defaultIfBlank(inheritVersion, softVersionParserResult.getMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart))), inheritMark);
                    softVersionParserResult.putVersionParts(inheritPart, inheritVersion);
                    if (StringUtils.isNotBlank(inheritMark)) {
                        this.putMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart, inheritMark);
                        this.putMarkParts(softVersionParserResult.getMarkAllParts(), inheritPart, inheritMark);
                    }
                }
                softVersionParserResult.setVersion(StringUtils.join(softVersionParserResult.getVersionParts().values(), ""));
                softVersionParserResult.setMark(StringUtils.join((defaultMarkAll ? softVersionParserResult.getMarkAllParts() : softVersionParserResult.getMarkPrevParts()).values(), ""));
            
                
                // 非范围这添加两次，确定开始和结束
                rangeMap.add(softVersionParserResult);
                rangeMap.add(softVersionParserResult);
                rangesMap.put(softVersionParserResult.getVersion(), rangeMap);
                
                softVersion = softVersion.replaceFirst(oldVerison, softVersionParserResult.getVersion());
            }
//          for (Entry<String, List<String>> tempList : tempMap.entrySet()) {
//              String source = tempList.getKey();
//              List<String> softVersions = tempList.getValue();
//              Map<String, List<String>> rangeMap = new LinkedHashMap<String, List<String>>();
//              rangeMap.put("source", Arrays.asList(source, source));
//              ArrayList<String> markList = new ArrayList<String>(softVersions);
//              markList.addAll(softVersions);
//              rangeMap.put("mark", markList);
//              rangesMap.put(source, rangeMap);
//          }
        }
        softVersionMap.put(softVersion, rangesMap);
        System.out.println(softVersionMap);
        System.out.println();
        return softVersionMap;
    }
    
    
    public final Map<String, List<String>> parseSoftVersion(String softVersion) {
        return parseSoftVersion(softVersion, false);
    }
    
    public final Map<String, List<String>> parseSoftVersion(String softVersion, boolean defaultMarkAll) {
        return parseSoftVersion(softVersion, defaultMarkAll, getIndexMarkMap());
    }
    
    public final Map<String, List<String>> parseSoftVersion(String softVersion, Map<Integer, String> markMap) {
        return parseSoftVersion(softVersion, false, markMap);
    }
    
    public Map<String, List<String>> parseSoftVersion(String softVersion, boolean defaultMarkAll, Map<Integer, String> markMap) {
        Map<String, List<String>> softVersionMap = new LinkedHashMap<String, List<String>>();
        Matcher matcher = getPattern().matcher(softVersion);
        while (matcher.find()) {
            int count = matcher.groupCount();
            String full = matcher.group().trim();
            if ("".equals(full) && matcher.start() > 0) {
                continue;
            }
            List<String> softVersions = new ArrayList<String>();
            softVersionMap.put(full, softVersions);
            StringBuilder marksAll = new StringBuilder();
            StringBuilder marksPrev = new StringBuilder();
            List<String> prevMarks = new ArrayList<String>();
            for (Entry<String, Integer> part : getPartIndexMap().entrySet()) {
                String key = part.getKey();
                Integer index = part.getValue();
                String group = matcher.group(index);
                String mark = group != null ? group : markMap.get(index);
                mark = processMark(key, mark);
                prevMarks.add(mark);
                if (group != null) {
                    marksPrev.append(String.join("", prevMarks));
                    prevMarks.clear();
                }
                marksAll.append(mark);
            }
            softVersions.add(defaultMarkAll ? marksAll.toString() : marksPrev.toString());
        }
        return softVersionMap;
    }
    
    public Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRange(String softVersion) {
        return parseSoftVersionRange(softVersion, false);
    }
    
    public Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRange(String softVersion, boolean defaultMarkAll) {
        Map<String, Map<String, Map<String, List<String>>>> softVersionMap = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();
        Map<String, Map<String, List<String>>> rangesMap = new LinkedHashMap<String, Map<String, List<String>>>();
        softVersionMap.put(softVersion, rangesMap);
        for (Pattern pattern : getRangePatterns()) {
            Matcher matcher = pattern.matcher(softVersion);
            while (matcher.find()) {
                Map<String, List<String>> rangeMap = new LinkedHashMap<String, List<String>>();
                List<String> softVersions = new ArrayList<String>();
                List<String> softVersionMarks = new ArrayList<String>();
                int count = matcher.groupCount();
                String full = matcher.group().trim();
                if ("".equals(full)) {
                    continue;
                }
                for (int index = 1; index <= count; index++) {
                    String group = matcher.group(index);
                    group = (group != null ? group : "").trim();
                    softVersions.add(group);
                    boolean isStart = index % 2 == 1;
                    Map<String, List<String>> tempMap = parseSoftVersion(group, "".equals(group) ? defaultMarkAll || !isStart : defaultMarkAll, isStart ? getIndexMarkMapStart() : getIndexMarkMapEnd());
                    for (List<String> tempList : tempMap.values()) {
                        softVersionMarks.addAll(tempList);
                    }
                }
                rangeMap.put("source", softVersions);
                rangeMap.put("mark", softVersionMarks);
                rangesMap.put(full, rangeMap);
            }
        }
        if (rangesMap.isEmpty()) {
            Map<String, List<String>> tempMap = parseSoftVersion(softVersion);
            for (Entry<String, List<String>> tempList : tempMap.entrySet()) {
                String source = tempList.getKey();
                List<String> softVersions = tempList.getValue();
                Map<String, List<String>> rangeMap = new LinkedHashMap<String, List<String>>();
                rangeMap.put("source", Arrays.asList(source, source));
                ArrayList<String> markList = new ArrayList<String>(softVersions);
                markList.addAll(softVersions);
                rangeMap.put("mark", markList);
                rangesMap.put(source, rangeMap);
            }
        }
        System.out.println(softVersionMap);
        System.out.println();
        return softVersionMap;
    }
    
    /**
     * 处理缺省值
     * @param part
     * @param mark
     * @return
     */
    public String processMark(String part, String mark) {
        mark = StringUtils.trim(mark);
        return mark;
    }
    
    public void putMarkParts(Map<String, String> markParts, String part, String mark) {
        markParts.put(part, processMark(part, mark));
    }
    
    /**
     * 填充系列
     * @param part
     * @param mark
     * @return
     */
    public String fillSeries(SoftVersionParser softVersionParser) {
        List<String> rangeInheritParts = getRangeInheritParts();
        Map<String, String> versionParts = softVersionParser.getVersionParts();
        List<String> series = new ArrayList<String>(rangeInheritParts.size());
        for (String part : rangeInheritParts) {
            String partValue = versionParts.getOrDefault(part, part);
            series.add(partValue);
            softVersionParser.putSeriesParts(part, partValue);
        }
        softVersionParser.setSeries(StringUtils.join(series, ""));
        return softVersionParser.getSeries();
    }
}
