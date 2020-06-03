package com.dp.plat.pms.springmvc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * 类名称：DocUtil 类描述：导出word工具类
 */
public class DocUtil {
	public Configuration configure = null;

	public DocUtil() {
		configure = new Configuration(Configuration.getVersion());
		configure.setDefaultEncoding("utf-8");
	}

	/**
	 * 根据Doc模板生成word文件，从servlet所在资源获取模板
	 * 
	 * @param dataMap
	 *            需要填入模板的数据
	 * @param templatePath
	 *            模板文件所在路径
	 * @param templateName
	 *            模板文件名称
	 * @param savePath
	 *            保存路径
	 */
	public File createDoc(Map<String, Object> dataMap, String templatePath, String templateName, String fileName,
			HttpServletRequest request) {
		if (fileName == null) {
			fileName = "temp" + (int) (Math.random() * 100000) + ".docx";
		}
		File f = new File(fileName);
		// 加载需要装填的模板
		Template template = null;
		try {

			// 设置模板装置方法和路径，FreeMarker支持多种模板装载方法。可以重servlet，classpath,数据库装载。
			// 加载模板文件，放在/uploadFiles/file/demoDoc下
			configure.setServletContextForTemplateLoading(request.getSession().getServletContext(), templatePath);
//			configure.setDirectoryForTemplateLoading(new File(request.getSession().getServletContext().getRealPath("/") + templatePath));
			// 设置对象包装器
			// configure.setObjectWrapper(new DefaultObjectWrapper());
			// 设置异常处理器
			configure.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
			// 定义Template对象，注意模板类型名字与downloadType要一致
			template = configure.getTemplate(templateName);

			Writer out = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
			template.process(dataMap, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		return f;
	}

	/**
	 * 根据Doc模板生成word文件
	 * 
	 * @param map
	 *            需要填入模板的数据
	 * @param templatePath
	 *            模板文件所在路径
	 * @param templateName
	 *            模板文件名称
	 * @param fileName
	 *            导出的文件名
	 */
	public File createDoc(Map<?, ?> map, String templatePath, String templateName, String fileName) {
		if (fileName == null) {
			fileName = "temp" + (int) (Math.random() * 100000) + ".docx";
		}
		File f = new File(fileName);
		// 加载需要装填的模板
		Template template = null;
		try {

			// 设置模板装置方法和路径，FreeMarker支持多种模板装载方法。可以重servlet，classpath,数据库装载。
			configure.setClassLoaderForTemplateLoading(getClass().getClassLoader(), templatePath);
			// 设置对象包装器
			// configure.setObjectWrapper(new DefaultObjectWrapper());
			// 设置异常处理器
			configure.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
			// 定义Template对象，注意模板类型名字与downloadType要一致
			template = configure.getTemplate(templateName);

			Writer out = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
			template.process(map, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
		return f;
	}

	/**
	 * 根据Doc模板生成word文件
	 * 
	 * @param dataMap
	 *            需要填入模板的数据
	 * @param downloadType
	 *            文件名称
	 * @param savePath
	 *            保存路径
	 */
	public void createXls(Map<String, Object> dataMap, String downloadType, String webPath, String fileName,
			String savePath) {
		System.out.println(savePath.substring(savePath.length() - 1));
		if (savePath.substring(savePath.length() - 1).equals(File.separator)) {
			savePath = savePath + "uploadFiles" + File.separator + "file" + File.separator + "jdhDailySheet"
					+ File.separator;
		} else {
			savePath = savePath + File.separator + "uploadFiles" + File.separator + "file" + File.separator
					+ "jdhDailySheet" + File.separator;
		}
		File f = new File(savePath + fileName);

		// 加载需要装填的模板
		Template template = null;
		try {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			if (f.exists() && f.isFile()) {
				f.delete();
			} else {
				f.createNewFile();
			}

			// 设置模板装置方法和路径，FreeMarker支持多种模板装载方法。可以重servlet，classpath,数据库装载。
			// 加载模板文件，放在/uploadFiles/file/demoDoc下
			configure.setDirectoryForTemplateLoading(
					new File(webPath + "uploadFiles" + File.separator + "file" + File.separator));
			// 设置对象包装器
			// configure.setObjectWrapper(new DefaultObjectWrapper());
			// 设置异常处理器
			configure.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
			// 定义Template对象，注意模板类型名字与downloadType要一致
			template = configure.getTemplate(downloadType);

			Writer out = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
			template.process(dataMap, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}
}