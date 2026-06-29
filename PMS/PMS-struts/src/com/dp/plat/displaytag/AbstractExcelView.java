package com.dp.plat.displaytag;

import java.lang.reflect.Method;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.displaytag.export.BinaryExportView;
import org.displaytag.model.TableModel;
import org.displaytag.properties.MediaTypeEnum;
import org.displaytag.properties.TableProperties;

public abstract class AbstractExcelView implements BinaryExportView {

	/**
	 * TableModel to render.
	 */
	private TableModel model;

	/**
	 * @see org.displaytag.export.ExportView#setParameters(org.displaytag.model.TableModel,
	 *      boolean, boolean, boolean)
	 */
	public void setParameters(TableModel model, boolean exportFullList, boolean includeHeader, boolean decorateValues) {
		this.model = model;
		
		initExportReponseHeader();
	}

	/**
	 * 初始化导出响应头，补充导出文件，头信息等
	 */
	public void initExportReponseHeader() {
		// 获取文件名，并添加到文件头中，避免导出时没有文件名
		TableProperties properties = this.model.getProperties();
		MediaTypeEnum exportType = this.model.getMedia();
		String exportFileName = properties.getExportFileName(this.model.getMedia());
		try {
			Method method = TableModel.class.getDeclaredMethod("getPageContext");
			method.setAccessible(true);
			PageContext pageContext = (PageContext) method.invoke(this.model);
			HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
//					if (StringUtils.isBlank(exportFileName)) {
//						exportFileName = System.currentTimeMillis() + ".xlsx";
//					}
			if (StringUtils.isNotBlank(exportFileName)) {
				String disposition = response.getHeader("Content-Disposition");
				// displaytag1.1 setPropertyTag 中对value 做了GBK,ISO-8859-1的编码转换，不需要再进行转换，否则会乱码，其他版本则需要进行转换
				exportFileName = URLEncoder.encode(exportFileName, "UTF-8");
				// 去除原有后缀，根据mimeType自动补充，避免文件类型与导出文件格式不符，部分软件打开提示文件损坏的问题
				exportFileName = exportFileName.replaceAll("\\.xlsx$", "").replaceAll("\\.xls$", "");
//				// 判断是否有后缀，如果没有后缀进行补充
//				if (!exportFileName.contains(".")) {
					String mimeType = getMimeType();
					if ("application/vnd.ms-excel".equals(mimeType)) {
						exportFileName += ".xls";
					} else {
						exportFileName += ".xlsx";
					}
//				}
				properties.setProperty(TableProperties.PROPERTY_EXPORT_PREFIX + '.' + exportType.getName() + '.' + TableProperties.EXPORTPROPERTY_STRING_FILENAME, exportFileName);
				String attachment = "attachment;filename=" + exportFileName;
				if (disposition == null) {
					disposition = attachment;
				} else if (!(disposition.contains("attachment;filename=")
						|| disposition.contains("attachment; filename="))) {
					disposition += ";" + attachment;
				} else {
					disposition = attachment;
				}
				response.setCharacterEncoding("utf-8");
				response.setHeader("Content-Disposition", disposition);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
