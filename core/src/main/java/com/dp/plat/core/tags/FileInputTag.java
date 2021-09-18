package com.dp.plat.core.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.core.context.SpringContext;
import com.dp.plat.core.dao.FileInfoMapper;
import com.dp.plat.core.pojo.FileType;
/**
 * 	文件上传标签
 * @author j01441
 *
 */
public class FileInputTag extends BodyTagSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String cssClass;
	
	private String cssStyle;
	
	private String name;
	
	private String fileType;
	
	private String multiple;
	
	@Override
	public int doStartTag() throws JspException {
		try {
			
			FileInfoMapper fileInfoMapper =SpringContext.getBean("fileInfoMapper", FileInfoMapper.class);
			// 输出value值
			StringBuilder text = new StringBuilder();
			FileType fileType2 = fileInfoMapper.selectFileTypeByCode(fileType);
			if(fileType2 != null) {
				text.append("<input type='file' ");
				if(StringUtils.isNotEmpty(cssClass)) {
					text.append(" class='"+cssClass+"' ");
				}
				if(StringUtils.isNotEmpty(cssStyle)) {
					text.append(" style='"+cssStyle+"' ");
				}
				if(StringUtils.isNotEmpty(id)) {
					text.append(" id='"+id+"' ");
				}
				if(StringUtils.isNotEmpty(multiple)) {
					text.append(" multiple='"+multiple+"' ");
				}
				text.append(" allowType='"+fileType2.getAllowType()+"' ");
				
				text.append(" uploadUrl='"+pageContext.getRequest().getServletContext().getContextPath()+fileType2.getUploadUrl()+"' ");
				text.append("/>");
				//隐藏域 文件ID
				text.append("<input type='hidden' ");
				text.append(" name='"+name+"'");
				if(StringUtils.isNotEmpty(id)) {
					text.append(" id='"+id+"_hidden' ");
				}
				text.append("/>");
			}
			pageContext.getOut().println(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_BODY_INCLUDE;
	}
 
	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFileType() {
		return fileType;
	}
	
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}
}
