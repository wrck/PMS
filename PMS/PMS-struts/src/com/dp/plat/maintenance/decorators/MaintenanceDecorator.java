package com.dp.plat.maintenance.decorators;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.TableDecorator;

import com.dp.plat.maintenance.vo.ProjectMaintenanceVO;
import com.dp.plat.param.FileParam;
import com.dp.plat.util.Base64Util;

/**
 * 自动根据表格标题长度计算表格的宽度<br/>
 */
@SuppressWarnings("unchecked")
public class MaintenanceDecorator extends TableDecorator {

    public String getExpendMaintenanceQuesResult() {
        Object obj = getCurrentRowObject();
        StringBuilder html = new StringBuilder();
        if (obj instanceof ProjectMaintenanceVO) {
            Integer maxQuesResultSize = (Integer) getPageContext().getAttribute("MaxQuesResultSize");
            ProjectMaintenanceVO projectMaintenance = (ProjectMaintenanceVO) obj;
            List<Map<String, String>> resultList = projectMaintenance.getQuesnaireResultList();

            for (Iterator<Map<String, String>> iterator = resultList.iterator(); iterator.hasNext();) {
                Map<String, String> map = (Map<String, String>) iterator.next();
                html.append(map.get("result"));
                if (iterator.hasNext()) {
                    html.append("</td><td>");
                }
            }
            if (maxQuesResultSize == null || maxQuesResultSize < resultList.size()) {
                maxQuesResultSize = resultList.size();
                getPageContext().setAttribute("MaxQuesResultSize", maxQuesResultSize);
            }
        } else if (obj instanceof Map) {
            Map<String, Object> projectMaintenance = (Map<String, Object>) obj;
            for (int i = 1;; i++) {
                String key = "questionResult" + i;
                String nextKey = "questionResult" + (i + 1);
                if (!projectMaintenance.containsKey(key)) {
                    break;
                } else {
                    html.append(StringUtils.trimToEmpty((String) projectMaintenance.get(key)));
                    if (projectMaintenance.containsKey(nextKey)) {
                        html.append("</td><td>");
                    }
                }
            }
        }
        return html.toString();
    }

    public String getExpendDeliverFilesURL() {
        Object obj = getCurrentRowObject();
        String atag = "<a title='点击下载' href='module/download.action?fileId=";
        // '<a href="module/download.action?fileId=' + fileId + '" title="点击下载">' +
        // fileName + '</a>'
        String atag2 = "<a href='module/DownloadFile.action?downname=%s&downpath=%s'>%s</a>";
        String downloadTag = "<a href='module/DownloadFile.action?downname=%s&downpath=%s'>下载</a>";
        String viewTag = "<a href='%s%s' target='_blank'>预览</a>";
        Pattern pattern = Pattern.compile(".*\\.(png|jpg|jpeg|gif)$", Pattern.CASE_INSENSITIVE);
        String contextPath = getPageContext().getRequest().getServletContext().getContextPath();
        List<String> html = new ArrayList<String>();
        if (obj instanceof ProjectMaintenanceVO) {
            ProjectMaintenanceVO projectMaintenance = (ProjectMaintenanceVO) obj;
            List<FileParam> resultList = projectMaintenance.getDeliverFileList();
            for (Iterator<FileParam> iterator = resultList.iterator(); iterator.hasNext();) {
                FileParam fileParam = iterator.next();
                StringBuilder str = new StringBuilder(atag);
                str.append(fileParam.getId()).append("'>").append(fileParam.getFileName()).append("</a>");
                html.add(str.toString());
            }
        } else if (obj instanceof Map) {
            Map<String, Object> projectMaintenance = (Map<String, Object>) obj;
            String deliverFiles = StringUtils.trimToEmpty((String) projectMaintenance.get("deliverFiles"));
            if (StringUtils.isNotBlank(deliverFiles)) {
                String[] files = StringUtils.split(deliverFiles, "||");
                for (String file : files) {
                	try {
	                	String[] kv = StringUtils.split(file, "$");
	                	String fileName = kv[1];
	                	StringBuilder str = new StringBuilder();
						try {
		                	Integer fileId = Integer.valueOf(kv[0]);
		                	str.append(atag);
		                    str.append(fileId).append("'>").append(fileName).append("</a>");
	                	} catch (Exception e) {
	                		String filePath = kv[0];
	                		String encodeFilePath = filePath;
	                		String encodeFileName = fileName;
	                		try {
	                			encodeFilePath = URLEncoder.encode(filePath, "UTF-8");
	                			encodeFileName = URLEncoder.encode(fileName, "UTF-8");
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							}
	                		String alink = String.format(atag2, encodeFileName, encodeFilePath, fileName);
	                		if (pattern.matcher(fileName).matches()) {
	                			str.append("<span class='hover-wrapper'>")
	                				.append(alink)
	                				.append("<label class='hover-label'>")
	                				.append(String.format(viewTag, contextPath, filePath))
	                				.append("|")
	                				.append(String.format(downloadTag, encodeFileName, encodeFilePath))
	                				.append("</label>")
	                				.append("</span>");
	                		} else {
	                			str.append(alink);
	                		}
						}
						html.add(str.toString());
                	} catch (Exception e) {
						e.printStackTrace();
					}
                }
            }
        }
        return StringUtils.join(html, " | ");
    }
    
    public String getExpendDeliverFiles() {
        Object obj = getCurrentRowObject();
        List<String> html = new ArrayList<String>();
        if (obj instanceof ProjectMaintenanceVO) {
            ProjectMaintenanceVO projectMaintenance = (ProjectMaintenanceVO) obj;
            List<FileParam> resultList = projectMaintenance.getDeliverFileList();
            for (Iterator<FileParam> iterator = resultList.iterator(); iterator.hasNext();) {
                FileParam fileParam = iterator.next();
                html.add(fileParam.getFileName());
            }
        } else if (obj instanceof Map) {
            Map<String, Object> projectMaintenance = (Map<String, Object>) obj;
            String deliverFiles = StringUtils.trimToEmpty((String) projectMaintenance.get("deliverFiles"));
            if (StringUtils.isNotBlank(deliverFiles)) {
                String[] files = StringUtils.split(deliverFiles, "||");
                for (String file : files) {
                    String[] kv = StringUtils.split(file, "$");
                    html.add(kv[1]);
                }
            }
        }
        return StringUtils.join(html, " | ");
    }
    
    public String getProjectNameWithURL() {
        Object obj = getCurrentRowObject();
        Integer projectId = 0;
        String projectName = "";
        String projectCode = "";
        String projectType = null;
        if (obj instanceof ProjectMaintenanceVO) {
            projectId = ((ProjectMaintenanceVO) obj).getProjectId();
            projectCode = ((ProjectMaintenanceVO) obj).getProjectCode();
            projectName = ((ProjectMaintenanceVO) obj).getProjectName();
            projectType = String.valueOf(((ProjectMaintenanceVO) obj).getProjectType());
        } else if (obj instanceof Map) {
            projectId = (Integer) ((Map<?, ?>) obj).get("projectId");
            projectCode = (String) ((Map<?, ?>) obj).get("projectCode");
            projectName = (String) ((Map<?, ?>) obj).get("projectName");
            projectType = String.valueOf(((Map<?, ?>) obj).get("projectType"));
        }
        if(StringUtils.isNotBlank(projectName)){
        	projectName = projectName.replaceAll("&", "＆");
        	projectName = projectName.replaceAll("<", "&lt;");
        	projectName = projectName.replaceAll(">", "&gt;");
        }
        Object objId = null;
        if(String.valueOf(10).equals(projectType)) {
            objId = Base64Util.EncodeBase64(projectId);
        } else {
            objId = projectId;
        }
        return "<a href='javascript:void(0)' class='updateMark' onclick='updateProject( \""+ objId.toString() +"\", " + projectType + ")'>"+ projectCode + "<br/>" + projectName+"</a>";
    }
    
    public Date getOperateTime() {
        Object obj = getCurrentRowObject();
        Date createTime = null;
        Date updateTime = null;
        if (obj instanceof ProjectMaintenanceVO) {
            createTime = ((ProjectMaintenanceVO) obj).getCreateTime();
            updateTime = ((ProjectMaintenanceVO) obj).getUpdateTime();
        } else if (obj instanceof Map) {
            createTime = (Date) ((Map<?, ?>) obj).get("createTime");
            updateTime = (Date) ((Map<?, ?>) obj).get("updateTime");
        }
        if (updateTime != null) {
            return updateTime;
        }
        return createTime;
    }
}
