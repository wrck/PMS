package com.dp.plat.util;

import java.sql.SQLException;

import com.dp.plat.type.DateTime;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class DateTimeTypeHandler implements TypeHandlerCallback {
	/**
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#getResult(com.ibatis.sqlmap.client.extensions.ResultGetter)
	 */
	public Object getResult(ResultGetter getter) throws SQLException {
		if (getter.getObject() == null) {
			return null;
		}
		return convertDbToValue(getter.getInt());
	}

	/**
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#setParameter(com.ibatis.sqlmap.client.extensions.ParameterSetter,
	 *      java.lang.Object)
	 */
	public void setParameter(ParameterSetter setter, Object value)
			throws SQLException {
		setter.setInt(saveValueToDb((DateTime) value));
	}

	/**
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#valueOf(java.lang.String)
	 */
	public Object valueOf(String value) {
		return convertDbToValue(Integer.parseInt(value));
	}

	/**
	 * 将POJO中的值转换数据库值存储
	 * 
	 * @param value
	 * @return
	 */
	private int saveValueToDb(DateTime value) {
		return (int) (value.getTime() / 1000);
	}

	/**
	 * 将数据库的值转换为POJO所需要的值
	 * 
	 * @param value
	 * @return
	 */
	private DateTime convertDbToValue(int value) {
		return new DateTime((long) value);
	}
}
