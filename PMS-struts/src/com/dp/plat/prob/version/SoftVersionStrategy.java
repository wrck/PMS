package com.dp.plat.prob.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoftVersionStrategy extends AbstractSoftVersionStrategy {
    
//  public static final String regex = "(?<Hvvv>[A-z0-9]{1,}[0-9]*(?:-.*)*-)?(?<Evvv>[S|B|A]\\d{3})?(?<Fxxx>[C|S]M?\\d{3})?(?<Dxxx>D\\d{3})?(?<Pxx>P\\d{2})?(?<PATCHxx>PATCH\\d{2})?(?<Lxx>L\\d{2})?(?<LPATCHxx>PATCH\\d{2})?(?<Txx>T\\d{2})?";
//  public static final String regex = "(?<Hvvv>[A-z0-9]{1,}[0-9]*(?:-.*)*-)?(?<Evvv>[S|B|A|L|M]\\d{3})?(?<Fxxx>[C|S]M?\\d{3})?(?<Dxxx>D\\d{3})?(?<Pxx>P\\d{2})?(?<PATCHxx>PATCH\\d{2})?(?<Txx>T\\d{2})?(?<Lxx>L\\d{2})?(?<LATCHxx>PATCH\\d{2})?";
//  public static final String regex = "(?<Hvvv>[A-z0-9]{1,}(?:[-|—][A-z0-9]*)*[-|—])?(?<Evvv>[S|B|A|L|M]\\d{3})?(?<Fxxx>[C|S]M?\\d{3})?(?<Dxxx>D\\d{3})?(?<Pxx>P\\d{2})?(?<PATCHxx>PATCH\\d{2})?(?<Txx>T\\d{2})?(?<Lxx>L\\d{2})?(?<LATCHxx>PATCH\\d{2})?";
    public static final String regex = "(?<Hvvv>[A-z0-9]{1,}(?:[-|—][A-z0-9]*)*[-|—])?(?<Evvv>[S|B|A|L|M]\\d{3})?(?<Fxxx>[C|S]M?\\d{3})?(?<Dxxx>D\\d{3})?(?<Pxx>P\\d{2,3})?(?<PATCHxx>PATCH\\d{2,3})?(?<Txx>T\\d{2,3})?(?<Lxx>L\\d{2,3})?(?<LATCHxx>PATCH\\d{2,3})?(?<BinExt>\\.[A-z]{1,})?";

    // 匹配非单词部分和（）()包裹部分
    public static final String bracketsRegex = "(?:\\s|(?:[(|（][^(|（|）|)]*[）|)])?)";
    // 版本前标识
    public static final String beforeBreaketRegex = "(?:以?及?之?以?前)";
    // 版本后标识
    public static final String afterBreakRegex = "(?:~|以?及?之?以?后)";
    public static final String rangeRegex = "(" + regex.replaceAll("\\?<\\w+>", "?:") + ")?" + bracketsRegex + "?" + afterBreakRegex + "{1,}" + bracketsRegex + "?(" + regex.replaceAll("\\?<\\w+>", "?:") + ")?" + bracketsRegex + "?";
//  public static final String rangeRegex = "(" + regex.replaceAll("\\?<\\w+>", "") + ")?~(" + regex.replaceAll("\\?<\\w+>", "") + ")?";
    public static final String rangeRegexReverse = "(" + bracketsRegex + ")?" + afterBreakRegex + "?" + bracketsRegex + "?(" + regex.replaceAll("\\?<\\w+>", "?:") + ")?" + bracketsRegex + "?" + beforeBreaketRegex;
    
    public static final Pattern PATTERN = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE|Pattern.UNICODE_CASE);
    public static final Pattern RANGE_PATTERN = Pattern.compile(rangeRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE|Pattern.UNICODE_CASE);
    public static final Pattern RANGE_REVERSE_PATTERN = Pattern.compile(rangeRegexReverse, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE|Pattern.UNICODE_CASE);
    public static final Pattern[] RANGE_PATTERNS = new Pattern[] {RANGE_PATTERN, RANGE_REVERSE_PATTERN};
    
    public static final Map<String, Integer> partIndexMap = new LinkedHashMap<String, Integer>();
    public static final Map<Integer, String> indexMarkMap = new LinkedHashMap<Integer, String>();
    public static final Map<Integer, String> indexMarkMapStart = new LinkedHashMap<Integer, String>();
    public static final Map<Integer, String> indexMarkMapEnd = new LinkedHashMap<Integer, String>();
    public static final List<String> rangeInheritParts = new ArrayList<String>(Arrays.asList(new String[] {"Hvvv", "Evvv", "Fxxx"}));
    static {
        partIndexMap.put("Hvvv", 1);            //  产品类型。如：LSW3000、FW1000、UAG3000等
        partIndexMap.put("Evvv", 2);            //  产品版本信息
                                                //      E  ：产品版本发布类型（S、B、A）
                                                //      vvv：产品VRB版本号。如111表示V100R001B01
        partIndexMap.put("Fxxx", 3);            //  平台分支信息
                                                //      F  ：C- Conplat平台，S- Smartplat平台  
                                                //      xxx：平台分支编号 。如C011表示V100R004B01（神州三号分支）
        partIndexMap.put("Dxxx", 4);            //  官网版本阶段信息
                                                //      D  ：官网版本阶段标识
                                                //      xxx：三位流水号
        partIndexMap.put("Pxx", 5);             //  阶段信息
                                                //      P ：D版本之间的阶段性版本标识
                                                //      xx：两位流水号
        partIndexMap.put("PATCHxx", 6);         //  补丁信息
                                                //      PATCH ：补丁版本标识
                                                //      xx：两位流水号
//      partMap.put("Lxx", 7);                  //  定制版本
//                                              //      L ：定制版本之间的阶段性版本标识
//                                              //      xx：两位流水号
//      partMap.put("LPATCHxx", 8);             //  定制版本补丁信息
//                                              //      LPATCH ：补丁版本标识
//                                              //      xx：两位流水号
//      partMap.put("Txx", 9);                  //  内部测试版本信息
//                                              //      T ：内部测试版本标识
//                                              //      xx：两位流水号
        partIndexMap.put("Txx", 7);             //  内部测试版本信息
                                                //      T ：内部测试版本标识
                                                //      xx：两位流水号
        partIndexMap.put("Lxx", 8);             //  定制版本
                                                //      L ：定制版本之间的阶段性版本标识
                                                //      xx：两位流水号
        partIndexMap.put("LATCHxx", 9);         //  定制版本补丁信息
                                                //      LATCH ：补丁版本标识
                                                //      xx：两位流水号
//      partIndexMap.put("BinExt", 10);         //  版本文件扩展名
//                                              //      BinExt ：版本文件扩展名
        // 各部分掩码处理
        for (Entry<String, Integer> part : partIndexMap.entrySet()) {
            indexMarkMap.put(part.getValue(), part.getKey());
        }
    
        // 各部分缺省值
        indexMarkMapStart.put(1, "");
        indexMarkMapStart.put(2, "0000");
        indexMarkMapStart.put(3, "0000");
        indexMarkMapStart.put(4, "D000");
        indexMarkMapStart.put(5, "P00");
        indexMarkMapStart.put(6, "PATCH00");
//      indexMarkMapStart.put(7, "");
//      indexMarkMapStart.put(8, "");
//      indexMarkMapStart.put(9, "");
        indexMarkMapStart.put(7, "");
        indexMarkMapStart.put(8, "");
        indexMarkMapStart.put(9, "");
        
        indexMarkMapEnd.put(1, "");
        indexMarkMapEnd.put(2, "Z999");
        indexMarkMapEnd.put(3, "Z999");
        indexMarkMapEnd.put(4, "D999");
        indexMarkMapEnd.put(5, "P99");
        indexMarkMapEnd.put(6, "PATCH99");
//      indexMarkMapEnd.put(7, "");
//      indexMarkMapEnd.put(8, "");
//      indexMarkMapEnd.put(9, "");
        indexMarkMapEnd.put(7, "");
        indexMarkMapEnd.put(8, "");
        indexMarkMapEnd.put(9, "");
        
        
        rangeInheritParts.addAll(Arrays.asList("Hvvv", "Evvv", "Fxxx"));
    }
    
    @Override
    protected String getRegex() {
        return regex;
    }

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public Pattern[] getRangePatterns() {
        return RANGE_PATTERNS;
    }
    
    @Override
    public Map<String, Integer> getPartIndexMap() {
        return partIndexMap;
    }

    @Override
    public Map<Integer, String> getIndexMarkMap() {
        return indexMarkMap;
    }

    @Override
    public Map<Integer, String> getIndexMarkMapStart() {
        return indexMarkMapStart;
    }

    @Override
    public Map<Integer, String> getIndexMarkMapEnd() {
        return indexMarkMapEnd;
    }

    @Override
    public List<String> getRangeInheritParts() {
        return rangeInheritParts;
    }
    
    /**
     * 处理缺省值
     * @param part
     * @param mark
     * @return
     */
    public String processMark(String part, String mark) {
        mark = super.processMark(part, mark);
        int zeroPaddingLength = 3;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(mark);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String digitPart = matcher.group();
            String paddedDigitPart = String.format("%0" + zeroPaddingLength + "d", Integer.parseInt(digitPart));
            matcher.appendReplacement(result, paddedDigitPart);
        }
        matcher.appendTail(result);
        
        mark = result.toString();
        return mark;
    }
    
    public List<SoftVersionParser> createSoftVersionParser(String softVersion, boolean defaultMarkAll, Map<Integer, String> markMap) {
        return super.createSoftVersionParser(softVersion, defaultMarkAll, markMap);
        
//        List<SoftVersionParser> softVersionParsers = new ArrayList<SoftVersionParser>();
//        Map<String, List<String>> softVersionMap = new LinkedHashMap<String, List<String>>();
//        Matcher matcher = PATTERN.matcher(softVersion);
//        while (matcher.find()) {
//            int count = matcher.groupCount();
//            String version = matcher.group().trim();
//            if ("".equals(version) && matcher.start() > 0) {
//                continue;
//            }
//            SoftVersionParser softVersionParser = new SoftVersionParser(version);
//            List<String> softVersions = new ArrayList<String>();
//            softVersionMap.put(version, softVersions);
//            StringBuilder marksAll = new StringBuilder();
//            StringBuilder marksPrev = new StringBuilder();
//            Map<String, String> prevMarks = new LinkedHashMap<String, String>(count);
//            for (Entry<String, Integer> part : partIndexMap.entrySet()) {
//                String key = part.getKey();
//                Integer index = part.getValue();
//                String group = matcher.group(index);
//                String mark = group != null ? group : markMap.get(index);
//                mark = mark.trim();
//                softVersionParser.putVersionParts(key, group);
//                softVersionParser.putMarkParts(softVersionParser.markAllParts, key, mark);
//                prevMarks.put(key, mark);
//                if (group != null) {
//                    softVersionParser.putMarkPartsAll(softVersionParser.markPrevParts, prevMarks);
//                    marksPrev.append(String.join("", prevMarks.values()));
//                    prevMarks.clear();
//                }
//                marksAll.append(mark);
//            }
//            String markVersion = defaultMarkAll ? marksAll.toString() : marksPrev.toString();
//            softVersionParser.setVersion(StringUtils.join(softVersionParser.versionParts.values(), ""));
//            softVersionParser.setMark(markVersion);
//            softVersionParsers.add(softVersionParser);
//            softVersions.add(markVersion);
//        }
//        return softVersionParsers;
    }
    
    public Map<String, Map<String, List<SoftVersionParser>>> createSoftVersionRangeParsers(String softVersion, String fixedSoftVersionTypes, boolean defaultMarkAll) {
        return super.createSoftVersionRangeParsers(softVersion, fixedSoftVersionTypes, defaultMarkAll);
        
//        if (softVersion != null && (softVersion.contains("-") || softVersion.contains("—"))) {
//            String oldSoftVersion = new String(softVersion);
//            String newSoftVersion = new String(softVersion);
//            List<SoftVersionParser> versionParser = createSoftVersionParser(newSoftVersion);
//            for (SoftVersionParser softVersionParser : versionParser) {
//                String version = softVersionParser.getVersion();
//                String replacedVersion = version.replaceAll("[-|—]", "\\$\\$_\\$\\$");
//                newSoftVersion = newSoftVersion.replace(version, replacedVersion);
//            }
//            softVersion = newSoftVersion.replaceAll("[-|—]", "~").replace("$$_$$", "-");
//        }
//        
//        Map<String, String> fixedInheritVersions = Collections.emptyMap();
//        Map<String, String> fixedInheritMarks = Collections.emptyMap();
//        if (StringUtils.isNotBlank(fixedSoftVersionTypes)) {
//            List<SoftVersionParser> fixedSoftVersionMap = createSoftVersionParser(fixedSoftVersionTypes, true);
//            if (!fixedSoftVersionMap.isEmpty()) {
//                fixedInheritVersions = fixedSoftVersionMap.get(0).getVersionParts();
//                fixedInheritMarks = fixedSoftVersionMap.get(0).getMarkAllParts();
//            }
//        }
//        
//        Map<String, Map<String, List<SoftVersionParser>>> softVersionMap = new LinkedHashMap<String, Map<String, List<SoftVersionParser>>>();
//        Map<String, List<SoftVersionParser>> rangesMap = new LinkedHashMap<String, List<SoftVersionParser>>();
//        for (Pattern pattern : RANGE_PATTERNS) {
//            Matcher matcher = pattern.matcher(softVersion);
//            while (matcher.find()) {
//                int count = matcher.groupCount();
//                String full = matcher.group().trim();
//                if ("".equals(full)) {
//                    continue;
//                }
//                // 用来修正填入的数据，使输入的内容跟解析的结果保持一致
//                String oldFull = full;
//                List<SoftVersionParser> rangeMap = new ArrayList<SoftVersionParser>();
//                String prevGroup = null;
//                Map<String, String> prevInheritVersions = new HashMap<String, String>();
//                Map<String, String> prevInheritMarks = new HashMap<String, String>();
//                boolean isPrevMarkAll = false;
//                List<SoftVersionParser> prevMap = null;
//                for (int index = 1; index <= count; index++) {
//                    String group = matcher.group(index);
//                    group = (group != null ? group : "").trim();
//                    
////                  // 如果为空，则默认 
////                  group = StringUtils.defaultIfBlank(group, softVersionTypes);
//                    
//                    boolean isStart = index % 2 == 1;
//                    boolean isMarkAll = "".equals(group) ? defaultMarkAll || (!isStart && !"".equals(prevGroup)) : defaultMarkAll;
////                  List<SoftVersionParser> tempMap = createSoftVersionParser(group, "".equals(group) ? defaultMarkAll || !isStart : defaultMarkAll);
//                    List<SoftVersionParser> tempMap = createSoftVersionParser(group, isMarkAll);
//                    for (SoftVersionParser softVersionParser : tempMap) {
//                        for (String inheritPart : rangeInheritParts) {
//                            String fixedInheritVersion = fixedInheritVersions.get(inheritPart);
//                            String fixedInheritMark = fixedInheritMarks.get(inheritPart);
//                            String inheritVersion = prevInheritVersions.get(inheritPart);
//                            String inheritMark = prevInheritMarks.get(inheritPart);
//                            inheritVersion = StringUtils.defaultIfBlank(fixedInheritVersion, StringUtils.defaultIfBlank(softVersionParser.getVersionParts(inheritPart), inheritVersion));
//                            inheritMark = StringUtils.defaultIfBlank(StringUtils.defaultIfBlank(fixedInheritMark, StringUtils.defaultIfBlank(inheritVersion, softVersionParser.getMarkParts(softVersionParser.getMarkPrevParts(), inheritPart))), inheritMark);
//                            softVersionParser.putVersionParts(inheritPart, inheritVersion);
//                            if (StringUtils.isNotBlank(inheritMark)) {
//                                softVersionParser.putMarkParts(softVersionParser.getMarkPrevParts(), inheritPart, inheritMark);
//                                softVersionParser.putMarkParts(softVersionParser.getMarkAllParts(), inheritPart, inheritMark);
//                            }
//                            prevInheritVersions.put(inheritPart, inheritVersion);
//                            prevInheritMarks.put(inheritPart, inheritMark);
//                        }
//                        softVersionParser.setVersion(StringUtils.join(softVersionParser.getVersionParts().values(), ""));
//                        softVersionParser.setMark(StringUtils.join((isMarkAll ? softVersionParser.getMarkAllParts() : softVersionParser.getMarkPrevParts()).values(), ""));
//                    
//                        // 用来修正填入的数据，使输入的内容跟解析的结果保持一致
//                        if (StringUtils.isNotBlank(group)) {
//                            full = full.replaceFirst(group, softVersionParser.getVersion());
//                        }
//                    }
//                    if (!isStart && prevMap != null) {
//                        for (SoftVersionParser softVersionParser : prevMap) {
//                            for (String inheritPart : rangeInheritParts) {
//                                String fixedInheritVersion = fixedInheritVersions.get(inheritPart);
//                                String fixedInheritMark = fixedInheritMarks.get(inheritPart);
//                                String inheritVersion = prevInheritVersions.get(inheritPart);
//                                String inheritMark = prevInheritMarks.get(inheritPart);
//                                inheritVersion = StringUtils.defaultIfBlank(fixedInheritVersion, StringUtils.defaultIfBlank(softVersionParser.getVersionParts(inheritPart), inheritVersion));
//                                inheritMark = StringUtils.defaultIfBlank(fixedInheritMark, StringUtils.defaultIfBlank(inheritVersion, softVersionParser.getMarkParts(softVersionParser.getMarkPrevParts(), inheritPart)));
//                                softVersionParser.putVersionParts(inheritPart, inheritVersion);
//                                if (StringUtils.isNotBlank(inheritMark)) {
//                                    softVersionParser.putMarkParts(softVersionParser.getMarkPrevParts(), inheritPart, inheritMark);
//                                    softVersionParser.putMarkParts(softVersionParser.getMarkAllParts(), inheritPart, inheritMark);
//                                }
//                                prevInheritVersions.put(inheritPart, inheritVersion);
//                                prevInheritMarks.put(inheritPart, inheritMark);
//                            }
//                            softVersionParser.setVersion(StringUtils.join(softVersionParser.getVersionParts().values(), ""));
//                            softVersionParser.setMark(StringUtils.join((isPrevMarkAll ? softVersionParser.getMarkAllParts() : softVersionParser.getMarkPrevParts()).values(), ""));
//                        }
//                    } else if (prevInheritVersions.containsValue(null)) {
//                        prevMap = tempMap;
//                        isPrevMarkAll = isMarkAll;
//                    }
//                    rangeMap.addAll(tempMap);
//                    prevGroup = group;
//                }
//                rangesMap.put(full, rangeMap);
//                
//                // 用来修正填入的数据，使输入的内容跟解析的结果保持一致
//                softVersion = softVersion.replaceFirst(oldFull, full);
//            }
//        }
//        if (rangesMap.isEmpty()) {
//            List<SoftVersionParser> tempMap = createSoftVersionParser(softVersion);
//            for (SoftVersionParser softVersionParser : tempMap) {
//                List<SoftVersionParser> rangeMap = new ArrayList<SoftVersionParser>();
//                if ("".equals(softVersionParser.getVersion())) {
//                    continue;
//                }
//                // 如果有固定的版本前缀，则以固定的版本前缀为准，同时修正输入的版本中错误的版本
//                String oldVerison = softVersionParser.getVersion();
//                for (String inheritPart : rangeInheritParts) {
//                    String fixedInheritVersion = fixedInheritVersions.get(inheritPart);
//                    String fixedInheritMark = fixedInheritMarks.get(inheritPart);
//                    String inheritVersion = fixedInheritVersion;
//                    String inheritMark = fixedInheritVersion;
//                    inheritVersion = StringUtils.defaultIfBlank(fixedInheritVersion, StringUtils.defaultIfBlank(softVersionParser.getVersionParts(inheritPart), inheritVersion));
//                    inheritMark = StringUtils.defaultIfBlank(StringUtils.defaultIfBlank(fixedInheritMark, StringUtils.defaultIfBlank(inheritVersion, softVersionParser.getMarkParts(softVersionParser.getMarkPrevParts(), inheritPart))), inheritMark);
//                    softVersionParser.putVersionParts(inheritPart, inheritVersion);
//                    if (StringUtils.isNotBlank(inheritMark)) {
//                        softVersionParser.putMarkParts(softVersionParser.getMarkPrevParts(), inheritPart, inheritMark);
//                        softVersionParser.putMarkParts(softVersionParser.getMarkAllParts(), inheritPart, inheritMark);
//                    }
//                }
//                softVersionParser.setVersion(StringUtils.join(softVersionParser.getVersionParts().values(), ""));
//                softVersionParser.setMark(StringUtils.join((defaultMarkAll ? softVersionParser.getMarkAllParts() : softVersionParser.getMarkPrevParts()).values(), ""));
//            
//                
//                // 非范围这添加两次，确定开始和结束
//                rangeMap.add(softVersionParser);
//                rangeMap.add(softVersionParser);
//                rangesMap.put(softVersionParser.getVersion(), rangeMap);
//                
//                softVersion = softVersion.replaceFirst(oldVerison, softVersionParser.getVersion());
//            }
////          for (Entry<String, List<String>> tempList : tempMap.entrySet()) {
////              String source = tempList.getKey();
////              List<String> softVersions = tempList.getValue();
////              Map<String, List<String>> rangeMap = new LinkedHashMap<String, List<String>>();
////              rangeMap.put("source", Arrays.asList(source, source));
////              ArrayList<String> markList = new ArrayList<String>(softVersions);
////              markList.addAll(softVersions);
////              rangeMap.put("mark", markList);
////              rangesMap.put(source, rangeMap);
////          }
//        }
//        softVersionMap.put(softVersion, rangesMap);
//        System.out.println(softVersionMap);
//        System.out.println();
//        return softVersionMap;
    }
    
    public Map<String, List<String>> parseSoftVersion(String softVersion, boolean defaultMarkAll, Map<Integer, String> markMap) {
        return parseSoftVersion(softVersion, defaultMarkAll, markMap);
        
//        Map<String, List<String>> softVersionMap = new LinkedHashMap<String, List<String>>();
//        Matcher matcher = PATTERN.matcher(softVersion);
//        while (matcher.find()) {
//            int count = matcher.groupCount();
//            String full = matcher.group().trim();
//            if ("".equals(full) && matcher.start() > 0) {
//                continue;
//            }
//            List<String> softVersions = new ArrayList<String>();
//            softVersionMap.put(full, softVersions);
//            StringBuilder marksAll = new StringBuilder();
//            StringBuilder marksPrev = new StringBuilder();
//            List<String> prevMarks = new ArrayList<String>();
//            for (Entry<String, Integer> part : partIndexMap.entrySet()) {
//                String key = part.getKey();
//                Integer index = part.getValue();
//                String group = matcher.group(index);
//                String mark = group != null ? group : markMap.get(index);
//                mark = mark.trim();
//                prevMarks.add(mark);
//                if (group != null) {
//                    marksPrev.append(String.join("", prevMarks));
//                    prevMarks.clear();
//                }
//                marksAll.append(mark);
//            }
//            softVersions.add(defaultMarkAll ? marksAll.toString() : marksPrev.toString());
//        }
//        return softVersionMap;
    }
    
    public Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRange(String softVersion, boolean defaultMarkAll) {
        return parseSoftVersionRange(softVersion, defaultMarkAll);
        
//        Map<String, Map<String, Map<String, List<String>>>> softVersionMap = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();
//        Map<String, Map<String, List<String>>> rangesMap = new LinkedHashMap<String, Map<String, List<String>>>();
//        softVersionMap.put(softVersion, rangesMap);
//        for (Pattern pattern : RANGE_PATTERNS) {
//            Matcher matcher = pattern.matcher(softVersion);
//            while (matcher.find()) {
//                Map<String, List<String>> rangeMap = new LinkedHashMap<String, List<String>>();
//                List<String> softVersions = new ArrayList<String>();
//                List<String> softVersionMarks = new ArrayList<String>();
//                int count = matcher.groupCount();
//                String full = matcher.group().trim();
//                if ("".equals(full)) {
//                    continue;
//                }
//                for (int index = 1; index <= count; index++) {
//                    String group = matcher.group(index);
//                    group = (group != null ? group : "").trim();
//                    softVersions.add(group);
//                    boolean isStart = index % 2 == 1;
//                    Map<String, List<String>> tempMap = parseSoftVersion(group, "".equals(group) ? defaultMarkAll || !isStart : defaultMarkAll, isStart ? indexMarkMapStart : indexMarkMapEnd);
//                    for (List<String> tempList : tempMap.values()) {
//                        softVersionMarks.addAll(tempList);
//                    }
//                }
//                rangeMap.put("source", softVersions);
//                rangeMap.put("mark", softVersionMarks);
//                rangesMap.put(full, rangeMap);
//            }
//        }
//        if (rangesMap.isEmpty()) {
//            Map<String, List<String>> tempMap = parseSoftVersion(softVersion);
//            for (Entry<String, List<String>> tempList : tempMap.entrySet()) {
//                String source = tempList.getKey();
//                List<String> softVersions = tempList.getValue();
//                Map<String, List<String>> rangeMap = new LinkedHashMap<String, List<String>>();
//                rangeMap.put("source", Arrays.asList(source, source));
//                ArrayList<String> markList = new ArrayList<String>(softVersions);
//                markList.addAll(softVersions);
//                rangeMap.put("mark", markList);
//                rangesMap.put(source, rangeMap);
//            }
//        }
//        System.out.println(softVersionMap);
//        System.out.println();
//        return softVersionMap;
    }
    
    private Map<String, List<String>> parseSoftVersionTest(String softVersion) {
        return parseSoftVersionTest(softVersion, false);
    }
    
    private Map<String, List<String>> parseSoftVersionTest(String softVersion, boolean defaultMarkAll) {
        return parseSoftVersionTest(softVersion, defaultMarkAll, indexMarkMap);
    }
    
    private Map<String, List<String>> parseSoftVersionTest(String softVersion, Map<Integer, String> markMap) {
        return parseSoftVersionTest(softVersion, false, markMap);
    }
    
    private Map<String, List<String>> parseSoftVersionTest(String softVersion, boolean defaultMarkAll, Map<Integer, String> markMap) {
        Map<String, List<String>> softVersionMap = new LinkedHashMap<String, List<String>>();
        Matcher matcher = getPattern().matcher(softVersion);
        while (matcher.find()) {
            int count = matcher.groupCount();
            String full = matcher.group();
            if ("".equals(full) && matcher.start() > 0) {
                continue;
            }
            List<String> softVersions = new ArrayList<String>();
            softVersionMap.put(full, softVersions);
//          System.out.println(full);
            StringBuilder marksAll = new StringBuilder();
            StringBuilder marksPrev = new StringBuilder();
            List<String> prevMarks = new ArrayList<String>();
            for (Entry<String, Integer> part : partIndexMap.entrySet()) {
                String key = part.getKey();
                Integer index = part.getValue();
                String group = matcher.group(index);
                String mark = group != null ? group : markMap.get(index);
                prevMarks.add(mark);
                if (group != null) {
                    marksPrev.append(String.join("", prevMarks));
                    prevMarks.clear();
                }
                marksAll.append(mark);
//              System.out.println(group + "\t" + markMap.get(index));
            }
//          System.out.println(marksPrev);
//          System.out.println(marksAll);
//          System.out.println();
            
//          prevMarks.clear();
//          marksAll = new StringBuilder();
//          marksPrev = new StringBuilder();
//          for (int index = 2; index <= count; index++) {
//              String group = matcher.group(index);
//              String mark = group != null ? group : markMapStart.get(index);
//              prevMarks.add(mark);
//              if (group != null) {
//                  marksPrev.append(String.join("", prevMarks));
//                  prevMarks.clear();
//              }
//              marksAll.append(mark);
//              System.out.println(group + "\t" + markMapStart.get(index));
//          }
//          System.out.println(marksPrev);
//          System.out.println(marksAll);
//          System.out.println();
            softVersions.add(defaultMarkAll ? marksAll.toString() : marksPrev.toString());
        }
        return softVersionMap;
    }
    
    private Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRangeTest(String softVersion) {
        return parseSoftVersionRangeTest(softVersion, false);
    }
    private Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRangeTest(String softVersion, boolean defaultMarkAll) {
        Map<String, Map<String, Map<String, List<String>>>> softVersionMap = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();
        Map<String, Map<String, List<String>>> rangesMap = new LinkedHashMap<String, Map<String, List<String>>>();
        System.out.println(softVersion);
        softVersionMap.put(softVersion, rangesMap);
        for (Pattern pattern : getRangePatterns()) {
            Matcher matcher = pattern.matcher(softVersion);
            while (matcher.find()) {
                Map<String, List<String>> rangeMap = new LinkedHashMap<String, List<String>>();
                List<String> softVersions = new ArrayList<String>();
                List<String> softVersionMarks = new ArrayList<String>();
                int count = matcher.groupCount();
                String full = matcher.group().trim();
                System.out.println(full);
                if ("".equals(full)) {
                    continue;
                }
                for (int index = 1; index <= count; index++) {
                    String group = matcher.group(index);
                    System.out.println(index + ":" +group);
                    group = (group != null ? group : "").trim();
                    softVersions.add(group);
                    boolean isStart = index % 2 == 1;
                    Map<String, List<String>> tempMap = parseSoftVersionTest(group, "".equals(group) ? defaultMarkAll || !isStart : defaultMarkAll, isStart ? indexMarkMapStart : indexMarkMapEnd);
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
            Map<String, List<String>> tempMap = parseSoftVersionTest(softVersion);
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
    
//  @Test
    public void testParser() {
        SoftVersionStrategy SoftVersionStrategy = new SoftVersionStrategy();
        String softVersion = "C011D008~C011D008PATCH05";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion);
        softVersion = "~C011D008PATCH05,C012D001P04PATCH07T01~";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion);
        softVersion = "~S211C011D007P08PATCH03";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "ADX3000-TE-S112D001P21~";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "S211C012D005P01PATCH11L01PATCH10-S211C012D005P01PATCH11L01PATCH21、S211C012D005P08PATCH09-S211C012D005P08PATCH46、S211C012D005P09PATCH06-S211C012D006P03PATCH06";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "ADX盒式设备神六开局分支（CM005D011系列）D011P07PATCH07（包含）-D011P10PATCH07（不含）";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "ADX盒式产品神六开局分支D011P09PATCH09T01（包含）- CM005D011P10（不含）版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "VPN神六开局分支B311CM005D025P02PATCH31及之前的版本、海南农信神五分支S211C012D004P06PATCH07PATCH30及之前的版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "神州5号D006P01PATCH02及之后版本，ADX产品神六开局分支（CM005D011系列）D011P09PATCH01及之后版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "神州五号D003P02官网分支C012D003P08（含）之前版本、神州五号C012D004P09PATCH02T02（含）之前版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "S112D001P01前版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "ADX神六开局分支D011P08PATCH05T02（2020年11月17日）~D011P08PATCH12（不含）版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "神六B311CM005D025（不含）-B311CM005D027P01（不含）之间的版本、VPN神六开局B311CM005D025P02PATCH06（不含）之前的版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "神州五号S211C012D004P06PATCH04(含)-S211C012D005P09PATCH05(不含)，神州五号D005P01官网版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "C012D005P09PATCH07-C012D006P01PATCH09 C012D006P01PATCH08以前版本，CM005D019P02PATCH40以前版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "防火墙神州六号盒式开局分支B311CM005D022P01PATCH58及之前的版本、VPN神六开局分支B311CM005D025P02PATCH33及之前的版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "S112D001P01,S112D003P01";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "B5.0.74之前版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "他FW1000BLADE-17EI-S211C011D005P09PATCH02之前的版本、GUARD3000Blade-17EI-S211C012D006P02PATCH06之前的版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion );
        softVersion = "S221S005D007P08PATCH02之前版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion);
        softVersion = "LSW3620-S221S005D007P05PATCH07.app之前版本";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion);
        softVersion = "盒式：神三C011D009PATCH02（含）~~~C011D009PATCH04（不含）；神州五号C012D002PATCH03T03（含）~~~C012D002PATCH04（不含） 框式：神三C011D009PATCH02（含）~~~C011D010P01PATCH06（不含）；神州五号C012D002PATCH03T03（含）~~~C012D002P01PATCH02（不含）；神州五号D10开局分支C012D001P10PATCH04（含）~~~C012D001P10PATCHX(X代表大于04的数)";
        SoftVersionStrategy.createSoftVersionRangeParsers(softVersion);
    }
    
    public static void main(String[] args) {
        SoftVersionStrategy SoftVersionStrategy = new SoftVersionStrategy();
        String softVersion = "C011D008~C011D008PATCH05";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion);
        softVersion = "~C011D008PATCH05,C012D001P04PATCH07T01~";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion);
        softVersion = "~S211C011D007P08PATCH03";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "ADX3000-TE-S112D001P21~";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "S211C012D005P01PATCH11L01PATCH10-S211C012D005P01PATCH11L01PATCH21、S211C012D005P08PATCH09-S211C012D005P08PATCH46、S211C012D005P09PATCH06-S211C012D006P03PATCH06";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "ADX盒式设备神六开局分支（CM005D011系列）D011P07PATCH07（包含）-D011P10PATCH07（不含）";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "ADX盒式产品神六开局分支D011P09PATCH09T01（包含）- CM005D011P10（不含）版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "VPN神六开局分支B311CM005D025P02PATCH31及之前的版本、海南农信神五分支S211C012D004P06PATCH07PATCH30及之前的版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "神州5号D006P01PATCH02及之后版本，ADX产品神六开局分支（CM005D011系列）D011P09PATCH01及之后版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "神州五号D003P02官网分支C012D003P08（含）之前版本、神州五号C012D004P09PATCH02T02（含）之前版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "S112D001P01前版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "ADX神六开局分支D011P08PATCH05T02（2020年11月17日）~D011P08PATCH12（不含）版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "神六B311CM005D025（不含）-B311CM005D027P01（不含）之间的版本、VPN神六开局B311CM005D025P02PATCH06（不含）之前的版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "神州五号S211C012D004P06PATCH04(含)-S211C012D005P09PATCH05(不含)，神州五号D005P01官网版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "C012D005P09PATCH07-C012D006P01PATCH09 C012D006P01PATCH08以前版本，CM005D019P02PATCH40以前版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "防火墙神州六号盒式开局分支B311CM005D022P01PATCH58及之前的版本、VPN神六开局分支B311CM005D025P02PATCH33及之前的版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "S112D001P01,S112D003P01";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "FW1000-S111C001D001";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
        softVersion = "B5.0.74之前版本";
        SoftVersionStrategy.parseSoftVersionRangeTest(softVersion );
    }

}
