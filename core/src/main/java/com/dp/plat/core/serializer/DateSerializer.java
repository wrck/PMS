package com.dp.plat.core.serializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.joda.time.DateTime;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.DateCodec;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import com.alibaba.fastjson.util.TypeUtils;

public class DateSerializer extends DateCodec {

	protected TimeZone timeZone = JSON.defaultTimeZone;
	protected Locale locale = JSON.defaultLocale;
	
	private final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd", this.timeZone, this.locale);
	private final FastDateFormat dateTimeFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", this.timeZone, this.locale);

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
			throws IOException {
		SerializeWriter out = serializer.out;

		if (object == null) {
			out.writeNull();
			return;
		}

		Class<?> clazz = object.getClass();
		if (clazz == java.sql.Date.class) {
			long millis = ((java.sql.Date) object).getTime();
			TimeZone timeZone = this.timeZone;
			int offset = timeZone.getOffset(millis);
			if (millis % offset == 0) {
				out.writeString(object.toString());
				return;
			}
		}

		if (clazz == java.sql.Time.class) {
			long millis = ((java.sql.Time) object).getTime();
			if (millis < 24L * 60L * 60L * 1000L) {
				out.writeString(object.toString());
				return;
			}
		}

		Date date;
		if (object instanceof Date) {
			date = (Date) object;
		} else {
			date = TypeUtils.castToDate(object);
		}

		if (out.isEnabled(SerializerFeature.WriteDateUseDateFormat)) {
			DateFormat format = serializer.getDateFormat();
			if (format == null) {
//				XXX 修改为自定义转化
//				format = new SimpleDateFormat(JSON.DEFFAULT_DATE_FORMAT, this.locale);
//				format.setTimeZone(this.timeZone);
				String text = serialize(date);
				out.writeString(text);
				return;
			}
			String text = format.format(date);
			out.writeString(text);
			return;
		}

		if (out.isEnabled(SerializerFeature.WriteClassName)) {
			if (clazz != fieldType) {
				if (clazz == java.util.Date.class) {
					out.write("new Date(");
					out.writeLong(((Date) object).getTime());
					out.write(')');
				} else {
					out.write('{');
					out.writeFieldName(JSON.DEFAULT_TYPE_KEY);
					serializer.write(clazz.getName());
					out.writeFieldValue(',', "val", ((Date) object).getTime());
					out.write('}');
				}
				return;
			}
		}

		long time = date.getTime();
		if (out.isEnabled(SerializerFeature.UseISO8601DateFormat)) {
			char quote = out.isEnabled(SerializerFeature.UseSingleQuotes) ? '\'' : '\"';
			out.write(quote);

			Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
			calendar.setTimeInMillis(time);

			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			int second = calendar.get(Calendar.SECOND);
			int millis = calendar.get(Calendar.MILLISECOND);

			char[] buf;
			if (millis != 0) {
				buf = "0000-00-00T00:00:00.000".toCharArray();
				IOUtils.getChars(millis, 23, buf);
				IOUtils.getChars(second, 19, buf);
				IOUtils.getChars(minute, 16, buf);
				IOUtils.getChars(hour, 13, buf);
				IOUtils.getChars(day, 10, buf);
				IOUtils.getChars(month, 7, buf);
				IOUtils.getChars(year, 4, buf);

			} else {
				if (second == 0 && minute == 0 && hour == 0) {
					buf = "0000-00-00".toCharArray();
					IOUtils.getChars(day, 10, buf);
					IOUtils.getChars(month, 7, buf);
					IOUtils.getChars(year, 4, buf);
				} else {
					buf = "0000-00-00T00:00:00".toCharArray();
					IOUtils.getChars(second, 19, buf);
					IOUtils.getChars(minute, 16, buf);
					IOUtils.getChars(hour, 13, buf);
					IOUtils.getChars(day, 10, buf);
					IOUtils.getChars(month, 7, buf);
					IOUtils.getChars(year, 4, buf);
				}
			}

			out.write(buf);

			float timeZoneF = calendar.getTimeZone().getOffset(calendar.getTimeInMillis()) / (3600.0f * 1000);
			int timeZone = (int) timeZoneF;
			if (timeZone == 0.0) {
				out.write('Z');
			} else {
				if (timeZone > 9) {
					out.write('+');
					out.writeInt(timeZone);
				} else if (timeZone > 0) {
					out.write('+');
					out.write('0');
					out.writeInt(timeZone);
				} else if (timeZone < -9) {
					out.write('-');
					out.writeInt(timeZone);
				} else if (timeZone < 0) {
					out.write('-');
					out.write('0');
					out.writeInt(-timeZone);
				}
				out.write(':');
				// handles uneven timeZones 30 mins, 45 mins
				// this would always be less than 60
				int offSet = (int) ((timeZoneF - timeZone) * 60);
				out.append(String.format("%02d", offSet));
			}

			out.write(quote);
		} else {
//			XXX 修改为自定义
//			out.writeLong(time);
			out.writeString(serialize(date));
		}
	}
	
	public String serialize(Date value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Date) {
			try {
				String tempValue = dateFormat.format(value);
				if(StringUtils.isNotBlank(tempValue) && value.equals(DateTime.parse(tempValue).toDate())){
					return tempValue;
				} else if (StringUtils.isBlank(tempValue)){
					return null;
				}
			} catch (Exception e) {
			}
			return dateTimeFormat.format(value);
		}
		return null;
	}
}
