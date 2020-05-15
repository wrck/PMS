package com.dp.plat.support;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringUtils;

import com.dp.plat.context.SpringContext;
import com.dp.plat.context.VContext;
import com.dp.plat.service.BasicDataService;

import net.sf.json.JSONArray;

public class LeftMenu {
	private static Map<String, String> externalGroupMap = null;
	private LeftMenuGroupInterface[] groups = null;
	private String imgurl = "images/left_line.gif";

	public void drow(PageContext pageContext) {
		if (null == groups) {
			return;
		}
		for (LeftMenuGroupInterface mg : groups) {
			if (mg.gainPermission(mg.getTitlesrc()) == 1) {
				mg.drow(pageContext);
				drowLine(pageContext);
			}
		}
		LeftMenuGroupInterface[] external = parseExternalGroup(getExternalGroup());
		if (external != null) {
			for (LeftMenuGroupInterface mg : external) {
				mg.drow(pageContext);
				drowLine(pageContext);
			}
		}
	}

	public void drowLine(PageContext pageContext) {
		VContext.getVM(pageContext.getOut(), "com/dp/plat/vmpage/LeftMenuGroupLine.vm", "imgurl", imgurl);
	}

	public LeftMenuGroupInterface[] getGroups() {
		return groups;
	}

	public void setGroups(LeftMenuGroupInterface[] groups) {
		this.groups = groups;
	}

	private LeftMenuGroupInterface[] parseExternalGroup(String jsonStrBody) {
		if (StringUtils.isBlank(jsonStrBody)) {
			return null;
		}
		// JSON转换
		JSONArray jsonObj = JSONArray.fromObject(jsonStrBody);

		Map<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("children", LeftMenuExternalLi.class);
		LeftMenuGroupInterface[] group = (LeftMenuGroupInterface[]) JSONArray.toArray(jsonObj,
				LeftMenuExternalGroup.class, classMap);
		return group;
	}

	private String getExternalGroup() {
		if (externalGroupMap == null) {
			externalGroupMap = queryExternalGroupMap();
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			String expirationTime = (String) externalGroupMap.get("expirationTime");
			long oldTime = Long.valueOf(expirationTime);
			if (oldTime < calendar.getTimeInMillis()) {
				externalGroupMap = queryExternalGroupMap();
			}
		}
		return externalGroupMap.get("externalGroupJSON");
	}

	private HashMap<String, String> queryExternalGroupMap() {
		BasicDataService basicDataService = SpringContext.getApplicationContext().getBean("basicDataService",
				BasicDataService.class);
		String externalGroupStr = basicDataService.querySysArg("sys.external.menu.group");
		HashMap<String, String> map = new HashMap<>();
		// 切割变为k-v
		if (StringUtils.isNotBlank(externalGroupStr)) {
			map.put("externalGroupJSON", externalGroupStr);
		} else {
			return map;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, 1);
		String expirationTime = String.valueOf(calendar.getTimeInMillis());
		map.put("expirationTime", expirationTime);
		return map;
	}

	public static void main(String[] args) {
		LeftMenuExternalGroup[] external = new LeftMenuExternalGroup[1];
		LeftMenuExternalGroup menuGroup = new LeftMenuExternalGroup();
		menuGroup.setTitlesrc("站点导航");

		LeftMenuExternalLi[] lis = new LeftMenuExternalLi[3];
		LeftMenuExternalLi leftMenuLi = new LeftMenuExternalLi();
		leftMenuLi.setText("备件管理系统");
		leftMenuLi.setTitle("备件管理系统");
		leftMenuLi.setUrl("http://spms.dptech.com");

		LeftMenuExternalLi leftMenuLi1 = new LeftMenuExternalLi();
		leftMenuLi1.setText("技术支援部办公平台");
		leftMenuLi1.setTitle("技术支援部办公平台");
		leftMenuLi1.setUrl("http://172.17.0.209");

		LeftMenuExternalLi leftMenuLi2 = new LeftMenuExternalLi();
		leftMenuLi2.setText("在线学习考试系统");
		leftMenuLi2.setTitle("在线学习考试系统");
		leftMenuLi2.setUrl("http://learning.dptech.com");

		lis[0] = leftMenuLi;
		lis[1] = leftMenuLi1;
		lis[2] = leftMenuLi2;

		menuGroup.setChildren(lis);
		external[0] = menuGroup;
		JSONArray jsonObject = JSONArray.fromObject(external);
		System.out.println(jsonObject.toString());

		LeftMenuGroupInterface[] externalGroup = new LeftMenu().parseExternalGroup(jsonObject.toString());
		jsonObject = JSONArray.fromObject(externalGroup);
		System.out.println(jsonObject.toString());
		// Map<String, Class> classMap = new HashMap<String, Class>();
		// classMap.put("lis", LeftMenuLi.class);
		// external = (LeftExternalMenuGroup[]) JSONArray.toArray(jsonObject,
		// LeftExternalMenuGroup.class, classMap);
	}

}
