package com.dp.plat.prob.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dp.plat.prob.version.AbstractSoftVersionStrategy;
import com.dp.plat.prob.version.SoftVersionParser;
import com.dp.plat.prob.version.SoftVersionParserFactory;

public class SoftVersionUtil {
    private static final SoftVersionParserFactory factory = new SoftVersionParserFactory();
    
    public static AbstractSoftVersionStrategy getParser(String input) {
        return factory.getParserFor(input);
    }
    
    public static List<SoftVersionParser> createSoftVersionParser(String softVersion) {
		return createSoftVersionParser(softVersion, false);
	}
	
	public static List<SoftVersionParser> createSoftVersionParser(String softVersion, boolean defaultMarkAll) {
		return createSoftVersionParser(softVersion, defaultMarkAll, null);
	}
	
	public static List<SoftVersionParser> createSoftVersionParser(String softVersion, Map<Integer, String> markMap) {
		return createSoftVersionParser(softVersion, false, markMap);
	}
	
	public static List<SoftVersionParser> createSoftVersionParser(String softVersion, boolean defaultMarkAll, Map<Integer, String> markMap) {
	    if (StringUtils.isBlank(softVersion)) {
	        return Collections.emptyList();
	    }
	    if (markMap == null || markMap.isEmpty()) {
	        markMap = getParser(softVersion).getIndexMarkMap();
	    }
		return getParser(softVersion).createSoftVersionParser(softVersion, defaultMarkAll, markMap);
	}
	
	public static Map<String, Map<String, List<SoftVersionParser>>> createSoftVersionRangeParsers(String softVersion) {
        return createSoftVersionRangeParsers(softVersion, "", false);
    }
	
	public static Map<String, Map<String, List<SoftVersionParser>>> createSoftVersionRangeParsers(String softVersion, String fixedSoftVersionTypes) {
		return createSoftVersionRangeParsers(softVersion, fixedSoftVersionTypes, false);
	}
	
	public static Map<String, Map<String, List<SoftVersionParser>>> createSoftVersionRangeParsers(String softVersion, String fixedSoftVersionTypes, boolean defaultMarkAll) {
	    if (StringUtils.isBlank(softVersion)) {
            return Collections.emptyMap();
        }
        return getParser(softVersion).createSoftVersionRangeParsers(softVersion, fixedSoftVersionTypes, defaultMarkAll);
	}
	
	
	public static Map<String, List<String>> parseSoftVersion(String softVersion) {
		return parseSoftVersion(softVersion, false);
	}
	
	public static Map<String, List<String>> parseSoftVersion(String softVersion, boolean defaultMarkAll) {
		return parseSoftVersion(softVersion, defaultMarkAll, null);
	}
	
	public static Map<String, List<String>> parseSoftVersion(String softVersion, Map<Integer, String> markMap) {
		return parseSoftVersion(softVersion, false, markMap);
	}
	
	public static Map<String, List<String>> parseSoftVersion(String softVersion, boolean defaultMarkAll, Map<Integer, String> markMap) {
	    if (StringUtils.isBlank(softVersion)) {
            return Collections.emptyMap();
        }
        if (markMap == null || markMap.isEmpty()) {
            markMap = getParser(softVersion).getIndexMarkMap();
        }
        return getParser(softVersion).parseSoftVersion(softVersion, defaultMarkAll, markMap);
	}
	
	public static Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRange(String softVersion) {
		return parseSoftVersionRange(softVersion, false);
	}
	
	public static Map<String, Map<String, Map<String, List<String>>>> parseSoftVersionRange(String softVersion, boolean defaultMarkAll) {
	    if (StringUtils.isBlank(softVersion)) {
            return Collections.emptyMap();
        }
        return getParser(softVersion).parseSoftVersionRange(softVersion, defaultMarkAll);
	}
	
	public static SoftVersionParser newSoftVersionParser(String version) {
        return newSoftVersionParser(version, version);
    }
	
	public static SoftVersionParser newSoftVersionParser(String version, String mark) {
        SoftVersionParser parserStart = new SoftVersionParser(version);
        parserStart.setMark(mark);
        return parserStart;
    }
	
//	@Test
	public void testParser() {
		String softVersion = "C011D008~C011D008PATCH05";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion);
		softVersion = "~C011D008PATCH05,C012D001P04PATCH07T01~";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion);
		softVersion = "~S211C011D007P08PATCH03";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion);
		softVersion = "ADX3000-TE-S112D001P21~";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion);
		softVersion = "S211C012D005P01PATCH11L01PATCH10-S211C012D005P01PATCH11L01PATCH21、S211C012D005P08PATCH09-S211C012D005P08PATCH46、S211C012D005P09PATCH06-S211C012D006P03PATCH06";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "ADX盒式设备神六开局分支（CM005D011系列）D011P07PATCH07（包含）-D011P10PATCH07（不含）";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "ADX盒式产品神六开局分支D011P09PATCH09T01（包含）- CM005D011P10（不含）版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "VPN神六开局分支B311CM005D025P02PATCH31及之前的版本、海南农信神五分支S211C012D004P06PATCH07PATCH30及之前的版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "神州5号D006P01PATCH02及之后版本，ADX产品神六开局分支（CM005D011系列）D011P09PATCH01及之后版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "神州五号D003P02官网分支C012D003P08（含）之前版本、神州五号C012D004P09PATCH02T02（含）之前版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "S112D001P01前版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "ADX神六开局分支D011P08PATCH05T02（2020年11月17日）~D011P08PATCH12（不含）版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "神六B311CM005D025（不含）-B311CM005D027P01（不含）之间的版本、VPN神六开局B311CM005D025P02PATCH06（不含）之前的版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "神州五号S211C012D004P06PATCH04(含)-S211C012D005P09PATCH05(不含)，神州五号D005P01官网版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "C012D005P09PATCH07-C012D006P01PATCH09 C012D006P01PATCH08以前版本，CM005D019P02PATCH40以前版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "防火墙神州六号盒式开局分支B311CM005D022P01PATCH58及之前的版本、VPN神六开局分支B311CM005D025P02PATCH33及之前的版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "S112D001P01,S112D003P01";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "FW1000-S111C001D001";
        SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "B5.0.74之前版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "他FW1000BLADE-17EI-S211C011D005P09PATCH02之前的版本、GUARD3000Blade-17EI-S211C012D006P02PATCH06之前的版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion );
		softVersion = "S221S005D007P08PATCH02之前版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion);
		softVersion = "LSW3620-S221S005D007P05PATCH07.app之前版本";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion);
		softVersion = "盒式：神三C011D009PATCH02（含）~~~C011D009PATCH04（不含）；神州五号C012D002PATCH03T03（含）~~~C012D002PATCH04（不含） 框式：神三C011D009PATCH02（含）~~~C011D010P01PATCH06（不含）；神州五号C012D002PATCH03T03（含）~~~C012D002P01PATCH02（不含）；神州五号D10开局分支C012D001P10PATCH04（含）~~~C012D001P10PATCHX(X代表大于04的数)";
		SoftVersionUtil.createSoftVersionRangeParsers(softVersion);
	}
	
	public static void main(String[] args) {
	    SoftVersionUtil SoftVersionUtil = new SoftVersionUtil();
	    SoftVersionUtil.testParser();
	}

}
