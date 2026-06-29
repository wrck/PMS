package com.dp.plat.prob.util;

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

import com.dp.plat.prob.version.NewSoftVersionStrategy;
import com.dp.plat.prob.version.SoftVersionParser;
import com.dp.plat.prob.version.SoftVersionStrategy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class SoftNewVersionUtil {
	
//	public static final String regex = "(?<Hvvv>[A-z0-9]{1,}[0-9]*(?:-.*)*-)?(?<Evvv>[S|B|A]\\d{3})?(?<Fxxx>[C|S]M?\\d{3})?(?<Dxxx>D\\d{3})?(?<Pxx>P\\d{2})?(?<PATCHxx>PATCH\\d{2})?(?<Lxx>L\\d{2})?(?<LPATCHxx>PATCH\\d{2})?(?<Txx>T\\d{2})?";
//	public static final String regex = "(?<Hvvv>[A-z0-9]{1,}[0-9]*(?:-.*)*-)?(?<Evvv>[S|B|A|L|M]\\d{3})?(?<Fxxx>[C|S]M?\\d{3})?(?<Dxxx>D\\d{3})?(?<Pxx>P\\d{2})?(?<PATCHxx>PATCH\\d{2})?(?<Txx>T\\d{2})?(?<Lxx>L\\d{2})?(?<LATCHxx>PATCH\\d{2})?";
//	public static final String regex = "(?<Hvvv>[A-z0-9]{1,}(?:[-|—][A-z0-9]*)*[-|—])?(?<Evvv>[S|B|A|L|M]\\d{3})?(?<Fxxx>[C|S]M?\\d{3})?(?<Dxxx>D\\d{3})?(?<Pxx>P\\d{2})?(?<PATCHxx>PATCH\\d{2})?(?<Txx>T\\d{2})?(?<Lxx>L\\d{2})?(?<LATCHxx>PATCH\\d{2})?";
	public static final String regex = "(?<Hvvv>[A-z0-9]{1,}(?:[-|—][A-z0-9]*)*[-|—])?(?<HSxxx>[H])(?<Hx>\\d{1,3})(?<Sxxx>[C|S])(?<VRxxx>\\d{1,3}\\.)(?<Bxxx>\\d{1,3}\\.)(?<Dxxx>\\d{1,3})(?<Rxxx>[R])?(?<Pxx>\\d{1,3}\\.?)?(?<PATCHxx>\\d{1,3})?(?<Txx>T\\d{1,3})?(?<Lxx>L\\d{1,3})?(?<LATCHxx>PATCH\\d{1,3})?(?<BinExt>\\.[A-z]{1,})?";

	// 匹配非单词部分和（）()包裹部分
	public static final String bracketsRegex = "(?:\\s|(?:[(|（][^(|（|）|)]*[）|)])?)";
	// 版本前标识
	public static final String beforeBreaketRegex = "(?:以?及?之?以?前)";
	// 版本后标识
	public static final String afterBreakRegex = "(?:~|-|以?及?之?以?后)";
	public static final String rangeRegex = "(" + regex.replaceAll("\\?<\\w+>", "?:") + ")?" + bracketsRegex + "?" + afterBreakRegex + "{1,}" + bracketsRegex + "?(" + regex.replaceAll("\\?<\\w+>", "?:") + ")?" + bracketsRegex + "?";
//	public static final String rangeRegex = "(" + regex.replaceAll("\\?<\\w+>", "") + ")?~(" + regex.replaceAll("\\?<\\w+>", "") + ")?";
	public static final String rangeRegexReverse = "(" + bracketsRegex + ")?" + afterBreakRegex + "?" + bracketsRegex + "?(" + regex.replaceAll("\\?<\\w+>", "?:") + ")?" + bracketsRegex + "?" + beforeBreaketRegex;
	
	public static final Pattern PATTERN = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE|Pattern.UNICODE_CASE);
	public static final Pattern RANGE_PATTERN = Pattern.compile(rangeRegex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE|Pattern.UNICODE_CASE);
	public static final Pattern RANGE_REVERSE_PATTERN = Pattern.compile(rangeRegexReverse, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE|Pattern.UNICODE_CASE);
	public static final Pattern[] RANGE_PATTERNS = new Pattern[] {RANGE_PATTERN, RANGE_REVERSE_PATTERN};
	
	public static final Map<String, Integer> partIndexMap = new LinkedHashMap<String, Integer>();
	public static final Map<Integer, String> indexMarkMap = new LinkedHashMap<Integer, String>();
	public static final Map<Integer, String> indexMarkMapStart = new LinkedHashMap<Integer, String>();
	public static final Map<Integer, String> indexMarkMapEnd = new LinkedHashMap<Integer, String>();
	public static final String[] rangeInheritParts = new String[] {"Hvvv", "Hxxx", "Hx", "Sxxx", "VRxxx", "Bxxx"};
	static {
		partIndexMap.put("Hvvv", 1);			//	产品类型。如：LSW3000、FW1000、UAG3000等
		partIndexMap.put("HSxxx", 2);			//  产品版本信息 HxSx.x.x
		partIndexMap.put("Hx", 3);              //	硬件平台版本号
		                                        //		H:Hardware，硬件架构版本号标识
                                                //		x:硬件架构数字编号
		partIndexMap.put("Sxxx", 4);            // 	软件平台基线版本号
		partIndexMap.put("VRxxx", 5);           //		S :Software，软件平台基线版本号标识
        partIndexMap.put("Bxxx", 6);            //		C-Conplat平台 S-Smart平台
		partIndexMap.put("Dxxx", 7);            //		x.x.x:利用小数点间隔的数字序号，对应软件平台的主线与稳定开局分支的VR、B、D版本号。
					                            //	
												//	
		partIndexMap.put("Rxxx", 8);			//	分支发布版本号
                                                //		R:分支发布版本号标识
                                                //		x.x:利用小数点间隔的数字序号，对应软件平台的受限局点分支集采测试分支、开发分支的P、PATCH版本号。
		partIndexMap.put("Pxx", 9);				// 	阶段信息
												//		P ：D版本之间的阶段性版本标识
												//		xx：数字序号
		partIndexMap.put("PATCHxx", 10); 		//	补丁信息
												//		PATCH ：补丁版本标识
												//		xx：数字序号
		partIndexMap.put("Txx", 11);				//	内部测试版本信息
												//		T ：内部测试版本标识
												//		xx：两位流水号
		partIndexMap.put("Lxx", 12);				// 	定制版本
												//		L ：定制版本之间的阶段性版本标识
												//		xx：两位流水号
		partIndexMap.put("LATCHxx", 13); 		//	定制版本补丁信息
												//		LATCH ：补丁版本标识
												//		xx：两位流水号
//		partIndexMap.put("BinExt", 14); 		//	版本文件扩展名
//												//		BinExt ：版本文件扩展名
		// 各部分掩码处理
		for (Entry<String, Integer> part : partIndexMap.entrySet()) {
			indexMarkMap.put(part.getValue(), part.getKey());
		}
	
		// 各部分缺省值
		indexMarkMapStart.put(1, "");
		indexMarkMapStart.put(2, "");
		indexMarkMapStart.put(3, "0000");
		indexMarkMapStart.put(4, "0");
		indexMarkMapStart.put(5, "000");
		indexMarkMapStart.put(6, "000");
		indexMarkMapStart.put(7, "000");
		indexMarkMapStart.put(8, "0");
		indexMarkMapStart.put(9, "000");
		indexMarkMapStart.put(10, "000");
		indexMarkMapStart.put(11, "");
		indexMarkMapStart.put(12, "");
		indexMarkMapStart.put(13, "");
		
		
		indexMarkMapEnd.put(1, "");
		indexMarkMapEnd.put(2, "");
		indexMarkMapEnd.put(3, "Z999");
		indexMarkMapEnd.put(4, "Z");
		indexMarkMapEnd.put(5, "999");
		indexMarkMapEnd.put(6, "999");
		indexMarkMapEnd.put(7, "999");
		indexMarkMapEnd.put(8, "Z");
        indexMarkMapEnd.put(9, "999");
        indexMarkMapEnd.put(10, "999");
        indexMarkMapEnd.put(11, "");
        indexMarkMapEnd.put(12, "");
        indexMarkMapEnd.put(13, "");
	}
	
	public static List<SoftVersionParser> createSoftVersionParser(String softVersion) {
		return createSoftVersionParser(softVersion, false);
	}
	
	public static List<SoftVersionParser> createSoftVersionParser(String softVersion, boolean defaultMarkAll) {
		return createSoftVersionParser(softVersion, defaultMarkAll, indexMarkMap);
	}
	
	public static List<SoftVersionParser> createSoftVersionParser(String softVersion, Map<Integer, String> markMap) {
		return createSoftVersionParser(softVersion, false, markMap);
	}
	
	public static List<SoftVersionParser> createSoftVersionParser(String softVersion, boolean defaultMarkAll, Map<Integer, String> markMap) {
		List<SoftVersionParser> softVersionParserResults = new ArrayList<SoftVersionParser>();
		Map<String, List<String>> softVersionMap = new LinkedHashMap<String, List<String>>();
		Matcher matcher = PATTERN.matcher(softVersion);
		while (matcher.find()) {
			int count = matcher.groupCount();
			String version = matcher.group().trim();
			if ("".equals(version) && matcher.start() > 0) {
				continue;
			}
			SoftVersionParser softVersionParserResult = new SoftVersionParser(new NewSoftVersionStrategy(), version);
			List<String> softVersions = new ArrayList<String>();
			softVersionMap.put(version, softVersions);
			StringBuilder marksAll = new StringBuilder();
			StringBuilder marksPrev = new StringBuilder();
			Map<String, String> prevMarks = new LinkedHashMap<String, String>(count);
			for (Entry<String, Integer> part : partIndexMap.entrySet()) {
				String key = part.getKey();
				Integer index = part.getValue();
				String group = matcher.group(index);
				String mark = group != null ? group : markMap.get(index);
				mark = processMark(key, mark);
				softVersionParserResult.putVersionParts(key, group);
				softVersionParserResult.putMarkParts(softVersionParserResult.markAllParts, key, mark);
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
	
	public static Map<String, Map<String, List<SoftVersionParser>>> createSoftVersionRangeParsers(String softVersion) {
        return createSoftVersionRangeParsers(softVersion, "", false);
    }
	
	public static Map<String, Map<String, List<SoftVersionParser>>> createSoftVersionRangeParsers(String softVersion, String fixedSoftVersionTypes) {
		return createSoftVersionRangeParsers(softVersion, fixedSoftVersionTypes, false);
	}
	
	public static Map<String, Map<String, List<SoftVersionParser>>> createSoftVersionRangeParsers(String softVersion, String fixedSoftVersionTypes, boolean defaultMarkAll) {
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
		
		Map<String, String> fixedInheritVersions = Collections.emptyMap();
		Map<String, String> fixedInheritMarks = Collections.emptyMap();
		if (StringUtils.isNotBlank(fixedSoftVersionTypes)) {
		    List<SoftVersionParser> fixedSoftVersionMap = createSoftVersionParser(fixedSoftVersionTypes, true);
		    if (!fixedSoftVersionMap.isEmpty()) {
		        fixedInheritVersions = fixedSoftVersionMap.get(0).getVersionParts();
		        fixedInheritMarks = fixedSoftVersionMap.get(0).getMarkAllParts();
		    }
		}
		
		Map<String, Map<String, List<SoftVersionParser>>> softVersionMap = new LinkedHashMap<String, Map<String, List<SoftVersionParser>>>();
		Map<String, List<SoftVersionParser>> rangesMap = new LinkedHashMap<String, List<SoftVersionParser>>();
		for (Pattern pattern : RANGE_PATTERNS) {
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
					
//					// 如果为空，则默认 
//					group = StringUtils.defaultIfBlank(group, softVersionTypes);
					
					boolean isStart = index % 2 == 1;
					boolean isMarkAll = "".equals(group) ? defaultMarkAll || (!isStart && !"".equals(prevGroup)) : defaultMarkAll;
//					List<SoftVersionParser> tempMap = createSoftVersionParser(group, "".equals(group) ? defaultMarkAll || !isStart : defaultMarkAll);
					List<SoftVersionParser> tempMap = createSoftVersionParser(group, isMarkAll);
					for (SoftVersionParser softVersionParserResult : tempMap) {
						for (String inheritPart : rangeInheritParts) {
						    String fixedInheritVersion = fixedInheritVersions.get(inheritPart);
                            String fixedInheritMark = fixedInheritMarks.get(inheritPart);
							String inheritVersion = prevInheritVersions.get(inheritPart);
							String inheritMark = prevInheritMarks.get(inheritPart);
							inheritVersion = StringUtils.defaultIfBlank(fixedInheritVersion, StringUtils.defaultIfBlank(softVersionParserResult.getVersionParts(inheritPart), inheritVersion));
							inheritMark = StringUtils.defaultIfBlank(StringUtils.defaultIfBlank(fixedInheritMark, StringUtils.defaultIfBlank(inheritVersion, softVersionParserResult.getMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart))), inheritMark);
							softVersionParserResult.putVersionParts(inheritPart, inheritVersion);
							if (StringUtils.isNotBlank(inheritMark)) {
								softVersionParserResult.putMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart, inheritMark);
								softVersionParserResult.putMarkParts(softVersionParserResult.getMarkAllParts(), inheritPart, inheritMark);
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
							for (String inheritPart : rangeInheritParts) {
							    String fixedInheritVersion = fixedInheritVersions.get(inheritPart);
	                            String fixedInheritMark = fixedInheritMarks.get(inheritPart);
	                            String inheritVersion = prevInheritVersions.get(inheritPart);
	                            String inheritMark = prevInheritMarks.get(inheritPart);
	                            inheritVersion = StringUtils.defaultIfBlank(fixedInheritVersion, StringUtils.defaultIfBlank(softVersionParserResult.getVersionParts(inheritPart), inheritVersion));
	                            inheritMark = StringUtils.defaultIfBlank(fixedInheritMark, StringUtils.defaultIfBlank(inheritVersion, softVersionParserResult.getMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart)));
	                            softVersionParserResult.putVersionParts(inheritPart, inheritVersion);
	                            if (StringUtils.isNotBlank(inheritMark)) {
	                                softVersionParserResult.putMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart, inheritMark);
	                                softVersionParserResult.putMarkParts(softVersionParserResult.getMarkAllParts(), inheritPart, inheritMark);
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
				for (String inheritPart : rangeInheritParts) {
				    String fixedInheritVersion = fixedInheritVersions.get(inheritPart);
                    String fixedInheritMark = fixedInheritMarks.get(inheritPart);
                    String inheritVersion = fixedInheritVersion;
                    String inheritMark = fixedInheritVersion;
                    inheritVersion = StringUtils.defaultIfBlank(fixedInheritVersion, StringUtils.defaultIfBlank(softVersionParserResult.getVersionParts(inheritPart), inheritVersion));
                    inheritMark = StringUtils.defaultIfBlank(StringUtils.defaultIfBlank(fixedInheritMark, StringUtils.defaultIfBlank(inheritVersion, softVersionParserResult.getMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart))), inheritMark);
                    softVersionParserResult.putVersionParts(inheritPart, inheritVersion);
                    if (StringUtils.isNotBlank(inheritMark)) {
                        softVersionParserResult.putMarkParts(softVersionParserResult.getMarkPrevParts(), inheritPart, inheritMark);
                        softVersionParserResult.putMarkParts(softVersionParserResult.getMarkAllParts(), inheritPart, inheritMark);
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
//			for (Entry<String, List<String>> tempList : tempMap.entrySet()) {
//				String source = tempList.getKey();
//				List<String> softVersions = tempList.getValue();
//				Map<String, List<String>> rangeMap = new LinkedHashMap<String, List<String>>();
//				rangeMap.put("source", Arrays.asList(source, source));
//				ArrayList<String> markList = new ArrayList<String>(softVersions);
//				markList.addAll(softVersions);
//				rangeMap.put("mark", markList);
//				rangesMap.put(source, rangeMap);
//			}
		}
		softVersionMap.put(softVersion, rangesMap);
		System.out.println(softVersionMap);
		System.out.println();
		return softVersionMap;
	}
	
	
	public static Map<String, List<String>> parseSoftVersion(String softVersion) {
		return parseSoftVersion(softVersion, false);
	}
	
	public static Map<String, List<String>> parseSoftVersion(String softVersion, boolean defaultMarkAll) {
		return parseSoftVersion(softVersion, defaultMarkAll, indexMarkMap);
	}
	
	public static Map<String, List<String>> parseSoftVersion(String softVersion, Map<Integer, String> markMap) {
		return parseSoftVersion(softVersion, false, markMap);
	}
	
	public static Map<String, List<String>> parseSoftVersion(String softVersion, boolean defaultMarkAll, Map<Integer, String> markMap) {
		Map<String, List<String>> softVersionMap = new LinkedHashMap<String, List<String>>();
		Matcher matcher = PATTERN.matcher(softVersion);
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
			for (Entry<String, Integer> part : partIndexMap.entrySet()) {
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
	
	public static Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRange(String softVersion) {
		return parseSoftVersionRange(softVersion, false);
	}
	
	public static Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRange(String softVersion, boolean defaultMarkAll) {
		Map<String, Map<String, Map<String, List<String>>>> softVersionMap = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();
		Map<String, Map<String, List<String>>> rangesMap = new LinkedHashMap<String, Map<String, List<String>>>();
		softVersionMap.put(softVersion, rangesMap);
		for (Pattern pattern : RANGE_PATTERNS) {
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
					Map<String, List<String>> tempMap = parseSoftVersion(group, "".equals(group) ? defaultMarkAll || !isStart : defaultMarkAll, isStart ? indexMarkMapStart : indexMarkMapEnd);
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
	
	private static Map<String, List<String>> parseSoftVersionTest(String softVersion) {
		return parseSoftVersionTest(softVersion, false);
	}
	
	private static Map<String, List<String>> parseSoftVersionTest(String softVersion, boolean defaultMarkAll) {
		return parseSoftVersionTest(softVersion, defaultMarkAll, indexMarkMap);
	}
	
	private static Map<String, List<String>> parseSoftVersionTest(String softVersion, Map<Integer, String> markMap) {
		return parseSoftVersionTest(softVersion, false, markMap);
	}
	
	private static Map<String, List<String>> parseSoftVersionTest(String softVersion, boolean defaultMarkAll, Map<Integer, String> markMap) {
		Map<String, List<String>> softVersionMap = new LinkedHashMap<String, List<String>>();
		Matcher matcher = PATTERN.matcher(softVersion);
		while (matcher.find()) {
			int count = matcher.groupCount();
			String full = matcher.group();
			if ("".equals(full) && matcher.start() > 0) {
				continue;
			}
			List<String> softVersions = new ArrayList<String>();
			softVersionMap.put(full, softVersions);
//			System.out.println(full);
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
//				System.out.println(group + "\t" + markMap.get(index));
			}
//			System.out.println(marksPrev);
//			System.out.println(marksAll);
//			System.out.println();
			
//			prevMarks.clear();
//			marksAll = new StringBuilder();
//			marksPrev = new StringBuilder();
//			for (int index = 2; index <= count; index++) {
//				String group = matcher.group(index);
//				String mark = group != null ? group : markMapStart.get(index);
//				prevMarks.add(mark);
//				if (group != null) {
//					marksPrev.append(String.join("", prevMarks));
//					prevMarks.clear();
//				}
//				marksAll.append(mark);
//				System.out.println(group + "\t" + markMapStart.get(index));
//			}
//			System.out.println(marksPrev);
//			System.out.println(marksAll);
//			System.out.println();
			softVersions.add(defaultMarkAll ? marksAll.toString() : marksPrev.toString());
		}
		return softVersionMap;
	}
	
	private static Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRangeTest(String softVersion) {
		return parseSoftVersionRangeTest(softVersion, false);
	}
	private static Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRangeTest(String softVersion, boolean defaultMarkAll) {
		Map<String, Map<String, Map<String, List<String>>>> softVersionMap = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();
		Map<String, Map<String, List<String>>> rangesMap = new LinkedHashMap<String, Map<String, List<String>>>();
		System.out.println(softVersion);
		softVersionMap.put(softVersion, rangesMap);
		for (Pattern pattern : RANGE_PATTERNS) {
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
	
	/**
	 * 处理缺省值
	 * @param part
	 * @param mark
	 * @return
	 */
	public static String processMark(String part, String mark) {
	    mark = StringUtils.trim(mark);
	    if (NumberUtils.isCreatable(mark)) {
	        Number version = NumberUtils.createNumber(mark);
	        String padded = String.format("%03d", version.intValue());
	        if (mark.endsWith(".")) {
	            padded += ".";
	        }
	        return padded;
	    }
	    return mark;
	}
	
//	@Test
	public void testParser() {
		String softVersion = "神七稳定开局H11C7.1.85（包含）之前的版本";
		SoftNewVersionUtil.createSoftVersionRangeParsers(softVersion);
		softVersion = "神七D035分支H11C7.1.35R45之前的版本";
        SoftNewVersionUtil.createSoftVersionRangeParsers(softVersion);
        softVersion = "神七D035分支H11C7.1.35R45~H11C7.2.35R145版本";
        SoftNewVersionUtil.createSoftVersionRangeParsers(softVersion);
        
        softVersion = "神七稳定开局H11C7.1.85（包含）之前的版本";
        SoftNewVersionUtil.parseSoftVersionRange(softVersion);
        softVersion = "神七D035分支H11C7.1.35R45之前的版本";
        SoftNewVersionUtil.parseSoftVersionRange(softVersion);
        softVersion = "神七D035分支H11C7.1.35R45~H11C7.2.35R145版本";
        SoftNewVersionUtil.parseSoftVersionRange(softVersion);
	}
	
	public static void main(String[] args) {
	    SoftNewVersionUtil versionUtil = new SoftNewVersionUtil();
	    versionUtil.testParser();
	}

}
