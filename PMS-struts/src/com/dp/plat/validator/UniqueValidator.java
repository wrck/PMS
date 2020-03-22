package com.dp.plat.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 列表字段唯一验证器
 * 
 * @author 王树太
 * 
 */
public class UniqueValidator
{

	public static List<Object> uniqueValidator(List<Object> list,
			String uniquePropName)
	{
		return uniqueValidator(list.toArray(), uniquePropName);
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private static Object getValueOfObject(Object owner, String propName)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException
	{
		Object tester = owner;
		String parentPropName = propName;
		String subPropName = propName;
		int index = propName.indexOf('.');
		if (index > 0)
		{
			subPropName = propName.substring(propName.indexOf('.') + 1);
			parentPropName = propName.substring(0, index);
			tester = getValueOfObject(owner, parentPropName);
		}
		Class ownerClass = tester.getClass();
		Method method = ownerClass.getMethod("get"
				+ subPropName.substring(0, 1).toUpperCase()
				+ subPropName.substring(1));
		Object[] args = null;
		return method.invoke(tester, args);
	}

	/**
	 * @param array
	 * @param uniquePropName
	 * @return
	 */
	public static List<Object> uniqueValidator(Object[] array,
			String uniquePropName)
	{
		List<Object> result = new ArrayList<Object>();
		HashMap<Object, Integer> dict = new HashMap<Object, Integer>();
		for (Object agent : array)
		{
			Object value = null;
			try
			{
				value = getValueOfObject(agent, uniquePropName);
			}
			catch (Exception e)
			{
				System.out.println("Get value of an object failed."
						+ e.getLocalizedMessage());
			}
			if (value != null)
			{
				if (dict.containsKey(value))
				{
					result.add(value);
				}
				else
				{
					dict.put(value, 0);
				}
			}
		}
		return result;
	}
}
