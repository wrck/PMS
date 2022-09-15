package code;

import com.alibaba.fastjson.JSON;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

public class TestFastJson {

	public static void main(String[] args) {
		long[][][] diffs = new long[2][5000][3];

		Class<?> type = ProjectVO.class;
		String json = "{\"boolean\":true,\"Boolean\":false,\"Character\":\"c\",\"Byte\":1,\"Short\":3,\"Integer\":5,\"Long\":7,\"Float\":9.1,\"Double\":11.3,\"BigInteger\":1234567890123456800,\"BigDecimal\":1234567890.0123458,\"String\":\"string\",\"Currency\":\"1,234,567,890.0123456789\",\"Date\":\"2021-12-06\",\"DateTime\":\"2021-12-06 13:38:46\",\"Timestamp\":1652094864718,\"Array\":[{\"boolean\":true,\"Boolean\":false,\"Character\":\"c\",\"Byte\":1,\"Short\":3,\"Integer\":5,\"Long\":7,\"Float\":9.1,\"Double\":11.3,\"BigInteger\":1234567890123456800,\"BigDecimal\":1234567890.0123458,\"String\":\"string\",\"Currency\":\"1,234,567,890.0123456789\",\"Date\":\"2021-12-06\",\"DateTime\":\"2021-12-06 13:38:46\",\"Timestamp\":1652094864718,\"Array\":[]},{\"boolean\":true,\"Boolean\":false,\"Character\":\"c\",\"Byte\":1,\"Short\":3,\"Integer\":5,\"Long\":7,\"Float\":9.1,\"Double\":11.3,\"BigInteger\":1234567890123456800,\"BigDecimal\":1234567890.0123458,\"String\":\"string\",\"Currency\":\"1,234,567,890.0123456789\",\"Date\":\"2021-12-06\",\"DateTime\":\"2021-12-06 13:38:46\",\"Array\":[]},{\"boolean\":true,\"Boolean\":false,\"Character\":\"c\",\"Byte\":1,\"Short\":3,\"Integer\":5,\"Long\":7,\"Float\":9.1,\"Double\":11.3,\"BigInteger\":1234567890123456800,\"BigDecimal\":1234567890.0123458,\"String\":\"string\",\"Currency\":\"1,234,567,890.0123456789\",\"Date\":\"2021-12-06\",\"DateTime\":\"2021-12-06 13:38:46\",\"Array\":[]}],\"Object\":{\"boolean\":true,\"Boolean\":false,\"Character\":\"c\",\"char\":\"c\",\"Byte\":1,\"byte\":2,\"Short\":3,\"short\":4,\"Integer\":5,\"int\":6,\"Long\":7,\"long\":8,\"Float\":9.1,\"float\":10.2,\"Double\":11.3,\"double\":12.4,\"BigInteger\":1234567890123456800,\"BigDecimal\":1234567890.0123458,\"String\":\"string\",\"Currency\":\"1,234,567,890.0123456789\",\"Date\":\"2021-12-06\",\"DateTime\":\"2021-12-06 13:38:46\",\"Array\":[{\"boolean\":true,\"Boolean\":false,\"Character\":\"c\",\"Byte\":1,\"Short\":3,\"Integer\":5,\"Long\":7,\"Float\":9.1,\"Double\":11.3,\"BigInteger\":1234567890123456800,\"BigDecimal\":1234567890.0123458,\"String\":\"string\",\"Currency\":\"1,234,567,890.0123456789\",\"Date\":\"2021-12-06\",\"DateTime\":\"2021-12-06 13:38:46\",\"Array\":[{\"boolean\":true,\"Boolean\":false,\"Character\":\"c\",\"Byte\":1,\"Short\":3,\"Integer\":5,\"Long\":7,\"Float\":9.1,\"Double\":11.3,\"BigInteger\":1234567890123456800,\"BigDecimal\":1234567890.0123458,\"String\":\"string\",\"Currency\":\"1,234,567,890.0123456789\",\"Date\":\"2021-12-06\",\"DateTime\":\"2021-12-06 13:38:46\",\"Array\":[]}]}]}}";
		System.out.println(json);

		long t1 = 0;
		Object parseObject = null;

		// 消除fastjson初始化可能带来的影响；
		JSON.isValid(json);
		parseObject = JSON.parseObject(json, type);
		JSON.toJSONString(parseObject);
		// 循环
		for (int i = 0; i < diffs[0].length; i++) {
			t1 = System.nanoTime();
			JSON.isValid(json);
			diffs[0][i][0] = System.nanoTime() - t1;

			t1 = System.nanoTime();
			parseObject = JSON.parseObject(json, type);
			diffs[0][i][1] = System.nanoTime() - t1;

			t1 = System.nanoTime();
			JSON.toJSONString(parseObject);
			diffs[0][i][2] = System.nanoTime() - t1;

		}

		// 消除fastjson2初始化可能带来的影响；
		com.alibaba.fastjson2.JSON.isValid(json);
		parseObject = com.alibaba.fastjson2.JSON.parseObject(json, type);
		com.alibaba.fastjson2.JSON.toJSONString(parseObject);

		for (int i = 0; i < diffs[1].length; i++) {
			t1 = System.nanoTime();
			com.alibaba.fastjson2.JSON.isValid(json);
			diffs[1][i][0] = System.nanoTime() - t1;

			t1 = System.nanoTime();
			parseObject = com.alibaba.fastjson2.JSON.parseObject(json, type);
			diffs[1][i][1] = System.nanoTime() - t1;

			t1 = System.nanoTime();
			com.alibaba.fastjson2.JSON.toJSONString(parseObject);
			diffs[1][i][2] = System.nanoTime() - t1;
		}

		double[][] avgs = { { 0d, 0d, 0d }, { 0d, 0d, 0d } };
		for (int i = 0; i < diffs.length; i++) {
			for (long[] arr : diffs[i]) {
				avgs[i][0] += 1d * arr[0] / diffs[i].length;
				avgs[i][1] += 1d * arr[1] / diffs[i].length;
				avgs[i][2] += 1d * arr[2] / diffs[i].length;
			}
		}
		for (int i = 0; i < avgs[0].length; i++) {
			System.out.printf("%s, %s, %s\n", avgs[0][i], avgs[1][i], (avgs[0][i] - avgs[1][i]) / avgs[0][i]);
		}
	}
}
