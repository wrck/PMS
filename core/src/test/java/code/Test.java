/**
 * 
 */
package code;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.plat.core.aop.SystemLogAspect;
import com.dp.plat.core.entity.DataOperation;
import com.dp.plat.core.exception.ExcelImportException;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.vo.PageParam;

/**
 * @author w02611
 *
 */
public class Test {

	public static void main(String[] args) {
		Map<String, Object> params = new HashMap<>();
		PageParam<Object> pages = new PageParam<>();
		PageParam<User> pageParam = new PageParam<>();
		PageParam<Object> page = new PageParam<>();
		User user = new User(111);
		user.setUserName("2222");
		pageParam.setModel(user);
		
		int i = 10;
		page.setModel(pageParam);
		StringBuilder str = new StringBuilder("model.model.");
		while (i > 0) {
			PageParam<Object> temp = new PageParam<>();
			temp.setModel(page);
			str.insert(0, "model.");
			page = temp;
			i--;
		}
		pages.setModel(page);
		str.insert(0, "model.");
		params.put("page", pages);
		params.put("user", user);
		
		
		String jsonStr = JSON.toJSONString(params);
		System.out.println(jsonStr);
		Map<String, Object> newParams = JSON.parseObject(jsonStr, HashMap.class);
		System.out.println(newParams);
		System.out.println(params);
		System.out.println(((Map)newParams.getOrDefault("page", "")).get("model"));
		
		SystemLogAspect logAspect = new SystemLogAspect();
		long t = System.currentTimeMillis();
//		System.out.println(logAspect.parseObjectValue("page.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.userId", params));
//		System.out.println(System.currentTimeMillis() - t);
//		t = System.currentTimeMillis();
//		System.out.println(logAspect.parseObjectValue("page.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.userId", params));
//		System.out.println(System.currentTimeMillis() - t);
//		t = System.currentTimeMillis();
//		System.out.println(logAspect.parseObjectValue("page.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.username", params));
//		System.out.println(System.currentTimeMillis() - t);
//		t = System.currentTimeMillis();
//		System.out.println(logAspect.parseMapValue("page.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.userId", newParams));
//		System.out.println(System.currentTimeMillis() - t);
//		t = System.currentTimeMillis();
//		System.out.println(logAspect.parseMapValue("page.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.userId", newParams));
//		System.out.println(System.currentTimeMillis() - t);
//		t = System.currentTimeMillis();
//		System.out.println(logAspect.parseMapValue("page.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.model.username", newParams));
//		System.out.println(System.currentTimeMillis() - t);
		
		System.out.println(str);
		System.out.println("---------------------");
		i = 4;
		while (i > 0) {
			t = System.currentTimeMillis();
			System.out.println(logAspect.parseMapValue("page." + str + "userName", newParams));
			System.out.println(System.currentTimeMillis() - t);
			t = System.currentTimeMillis();
			System.out.println(logAspect.parseMapValue("page." + str + "userId", newParams));
			System.out.println(System.currentTimeMillis() - t);
			i--;
		}
		i = 4;
		System.out.println("---------------------");
		while (i > 0) {
			t = System.currentTimeMillis();
			System.out.println(logAspect.parseObjectValue("page."+ str +"userName", params));
			System.out.println(System.currentTimeMillis() - t);
			t = System.currentTimeMillis();
			System.out.println(logAspect.parseMapValue("page." + str + "userId", newParams));
			System.out.println(System.currentTimeMillis() - t);
			i--;
		}
//		List<ExcelImportException> list = new ArrayList<>();
//		list.add(new ExcelImportException("message1", "1"));
//		list.add(new ExcelImportException("message2", "2"));
//		list.add(new ExcelImportException("message3", "3"));
//		JSONArray jsonArray =  (JSONArray) JSON.toJSON(list);
//		for (Object jsonObject : jsonArray) {
//			((JSONObject)jsonObject).remove("stackTrace");
//		}
//		System.out.println(JSON.toJSONString(list));
//		Date startTime = new Date();
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(startTime);
//		calendar.add(Calendar.MONTH, -6);
//		System.out.println(String.valueOf(calendar.get(Calendar.YEAR)));
//		int month = calendar.get(Calendar.MONTH) + 1;
//		System.out.println(month);
//		String quarter = String.valueOf((int)Math.ceil(month / 3d));
//		System.out.println(quarter);
	}
	
	@org.junit.Test
	public void testGenerator() {
		generatorUpdateDuplicateStr(DataOperation.class);
	}

	public void generatorUpdateDuplicateStr(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ON DUPLICATE KEY UPDATE ");
		int i = 0;
		for (Field field : fields) {
			if (!field.isAnnotationPresent(Id.class)) {
				String name = field.getName();
				stringBuilder.append(name).append(" = ").append("VALUES(").append(name).append("), ");
				i++;
				if (i % 3 == 0) {
					stringBuilder.append("\n");
				}
			}
		}
		stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
		System.out.println(stringBuilder.toString());
	}
}
