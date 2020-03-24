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
import com.dp.plat.pms.springmvc.vo.AfPrjProperty;
import com.dp.plat.pms.springmvc.vo.ProjectProduct;

/**
 * @author w02611
 *
 */
public class Test {

	public static void main(String[] args) {
		generatorUpdateDuplicateStr(ProjectProduct.class);
	}
	
//	@org.junit.Test
	public void testGenerator() {
		generatorUpdateDuplicateStr(DataOperation.class);
	}

	public static void generatorUpdateDuplicateStr(Class<?> clazz) {
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
