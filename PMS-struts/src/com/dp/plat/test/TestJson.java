package com.dp.plat.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TestJson {
//	@Test
	public void test() throws ParseException {
		String[] arr = new String[] { "2015-11", "2015-12", "2015-10", "2015-09" };
		sortSettingTime(arr);
		System.out.println(arr[0]);
		System.out.println(arr[1]);
		System.out.println(arr[2]);
		System.out.println(arr[3]);
	}

	private void sortSettingTime(String[] settingArr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		int n = settingArr.length;
		if (n > 1) {
			for (int i = 0; i < n - 1; i++) {
				for (int j = 0; j < n - 1; j++) {
					if (sdf.parse(settingArr[j]).after(sdf.parse(settingArr[j + 1]))) {
						String tmp = settingArr[j];
						settingArr[j] = settingArr[j + 1];
						settingArr[j + 1] = tmp;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		String json = "{\"title\":{\"text\":\"闭环平均得分\",\"x\":\"center\",\"textStyle\":{\"fontSize\":12}},\"tooltip\":{\"trigger\":\"axis\",\"formatter\":\"{b}:<br/>{a0}:{c0}<br/>\"},\"legend\":{\"x\":\"left\",\"data\":[\"平均分\"]},\"calculable\":true,\"animation\":true,\"toolbox\":{\"show\":true,\"feature\":{\"magicType\":{\"show\":true,\"type\":[]},\"restore\":{\"show\":true,\"title\":\"刷新\"},\"saveAsImage\":{\"show\":true,\"title\":\"保存为图片\",\"type\":\"png\"}}},\"xAxis\":[{\"type\":\"category\",\"show\":true,\"data\":[\"全国\",\"武汉办事处\",\"沈阳办事处\",\"西安办事处\",\"企业网市场部\",\"专网营销部\",\"战略合作部\",\"北京办事处\",\"太原办事处\",\"天津办事处\",\"石家庄办事处\",\"呼和浩特办事处\",\"长春办事处\",\"哈尔滨办事处\",\"郑州办事处\",\"长沙办事处\",\"南昌办事处\",\"兰州办事处\",\"乌鲁木齐办事处\",\"成都办事处\",\"重庆办事处\",\"昆明办事处\",\"贵阳办事处\",\"上海办事处\",\"南京办事处\",\"杭州办事处\",\"合肥办事处\",\"济南办事处\",\"广州办事处\",\"福州办事处\",\"南宁办事处\",\"深圳办事处\",\"运营商市场部\",\"产品行销部\"],\"axisLabel\":{\"textStyle\":{\"fontSize\":12,\"align\":\"center\"},\"interval\":0,\"rotate\":-45,\"margin\":5}}],\"yAxis\":[{\"type\":\"value\",\"show\":true}],\"series\":[{\"name\":\"平均分\",\"type\":\"bar\",\"data\":[95.17,100.0,94.67,93.5,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0],\"itemStyle\":{\"normal\":{\"color\":\"#cccc33\"}}}]}";
		String seach = "\"yAxis\":[{\"type\":\"value\",";
		int index = json.lastIndexOf(seach);
		StringBuffer s = new StringBuffer(json);
		s.insert(index+seach.length(), "\"min\":40,\"max\":100,\"scale\":true,");
		System.out.println(s);
		System.out.println("\"min\":40,\"max\":100,\"scale\":true"+json.substring(index+seach.length()));
	}
}
