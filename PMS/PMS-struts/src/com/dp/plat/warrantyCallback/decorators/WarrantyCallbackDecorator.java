package com.dp.plat.warrantyCallback.decorators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.TableDecorator;
import org.displaytag.model.HeaderCell;
import org.displaytag.model.TableModel;
import org.displaytag.util.HtmlAttributeMap;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.dp.plat.param.FileParam;
import com.dp.plat.util.Base64Util;
import com.dp.plat.warrantyCallback.vo.ProjectWarrantyCallbackVO;

/**
 * 自动根据表格标题长度计算表格的宽度<br/>
 */
public class WarrantyCallbackDecorator extends TableDecorator {
	
	@Override
	public void init(PageContext pageContext, Object decorated, TableModel tableModel) {
		List<HeaderCell> headerCellList = tableModel.getHeaderCellList();
		int offset = 0;
		for (int i = 0; i < headerCellList.size(); i++) {
			HeaderCell headerCell = headerCellList.get(i);
			headerCell.setColumnNumber(headerCell.getColumnNumber() + offset);
			
			String propertyName = headerCell.getBeanPropertyName();
			Object splitCell = headerCell.getHeaderAttributes().get("scope");
			if (splitCell != null && splitCell.toString().contains("splitCell=true")) {
				String title = headerCell.getTitle();
				String[] splitTitle = title.split("</th><th>");
				for (int j = 1; j < splitTitle.length; j++) {
					String subTitle = splitTitle[j];
					HeaderCell cell = new HeaderCell();
					BeanUtils.copyProperties(headerCell, cell);
					cell.setTitle(subTitle);
					cell.setColumnNumber(cell.getColumnNumber() + j);
					cell.setBeanPropertyName("");
					HtmlAttributeMap htmlAttributes = new HtmlAttributeMap();
					htmlAttributes.putAll(cell.getHtmlAttributes());
					Object style = htmlAttributes.getOrDefault("style", "");
					style += ";width:0;padding:0;margin:0;";
					htmlAttributes.put("style", style);
					cell.setHtmlAttributes(htmlAttributes);
					headerCellList.add(i + j, cell);
				}
				offset = splitTitle.length - 1;
				headerCell.setTitle(splitTitle[0]);
			}
		}
		super.init(pageContext, decorated, tableModel);
	}

    public String getExpendWarrantyCallbackQuesResult() {
        Object obj = getCurrentRowObject();
//        Object hasSplitExpendHeader = getPageContext().findAttribute("hasSplitExpendHeader");
//        if (hasSplitExpendHeader == null) {
//        	List<HeaderCell> headerCellList = this.tableModel.getHeaderCellList();
//        	for (int i = 0; i < headerCellList.size(); i++) {
//        		HeaderCell headerCell = headerCellList.get(i);
//        		String propertyName = headerCell.getBeanPropertyName();
//        		if ("expendWarrantyCallbackQuesResult".equals(propertyName)) {
//        			String title = headerCell.getTitle();
//        			String[] splitTitle = title.split("</th><th>");
//        			for (int j = 1; j < splitTitle.length; j++) {
//        				String subTitle = splitTitle[j];
//        				HeaderCell cell = new HeaderCell();
//        				BeanUtils.copyProperties(headerCell, cell);
//        				cell.setTitle(subTitle);
//        				headerCellList.add(i + j, cell);
//        			}
//        			headerCell.setTitle(splitTitle[0]);
//        			break;
//        		}
//        	}
//        	getPageContext().setAttribute("hasSplitExpendHeader", true);
//        }
        StringBuilder html = new StringBuilder();
        if (obj instanceof ProjectWarrantyCallbackVO) {
            Integer maxQuesResultSize = (Integer) getPageContext().getAttribute("MaxQuesResultSize");
            ProjectWarrantyCallbackVO projectWarrantyCallback = (ProjectWarrantyCallbackVO) obj;
            List<Map<String, String>> resultList = projectWarrantyCallback.getQuesnaireResultList();

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
            Map<String, Object> projectWarrantyCallback = (Map<String, Object>) obj;
            for (int i = 1;; i++) {
                String key = "questionResult" + i;
                String nextKey = "questionResult" + (i + 1);
                if (!projectWarrantyCallback.containsKey(key)) {
                    break;
                } else {
                    html.append(StringUtils.trimToEmpty((String) projectWarrantyCallback.get(key)));
                    if (projectWarrantyCallback.containsKey(nextKey)) {
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
        List<String> html = new ArrayList<String>();
        if (obj instanceof ProjectWarrantyCallbackVO) {
            ProjectWarrantyCallbackVO projectWarrantyCallback = (ProjectWarrantyCallbackVO) obj;
            List<FileParam> resultList = projectWarrantyCallback.getDeliverFileList();
            for (Iterator<FileParam> iterator = resultList.iterator(); iterator.hasNext();) {
                FileParam fileParam = iterator.next();
                StringBuilder str = new StringBuilder(atag);
                str.append(fileParam.getId()).append("'>").append(fileParam.getFileName()).append("</a>");
                html.add(str.toString());
            }
        } else if (obj instanceof Map) {
            Map<String, Object> projectWarrantyCallback = (Map<String, Object>) obj;
            String deliverFiles = StringUtils.trimToEmpty((String) projectWarrantyCallback.get("deliverFiles"));
            if (StringUtils.isNotBlank(deliverFiles)) {
                String[] files = StringUtils.split(deliverFiles, "||");
                for (String file : files) {
                    StringBuilder str = new StringBuilder(atag);
                    String[] kv = StringUtils.split(file, "$");
                    str.append(kv[0]).append("'>").append(kv[1]).append("</a>");
                    html.add(str.toString());
                }
            }
        }
        return StringUtils.join(html, " | ");
    }

    public String getExpendDeliverFiles() {
        Object obj = getCurrentRowObject();
        List<String> html = new ArrayList<String>();
        if (obj instanceof ProjectWarrantyCallbackVO) {
            ProjectWarrantyCallbackVO projectWarrantyCallback = (ProjectWarrantyCallbackVO) obj;
            List<FileParam> resultList = projectWarrantyCallback.getDeliverFileList();
            for (Iterator<FileParam> iterator = resultList.iterator(); iterator.hasNext();) {
                FileParam fileParam = iterator.next();
                html.add(fileParam.getFileName());
            }
        } else if (obj instanceof Map) {
            Map<String, Object> projectWarrantyCallback = (Map<String, Object>) obj;
            String deliverFiles = StringUtils.trimToEmpty((String) projectWarrantyCallback.get("deliverFiles"));
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
        int projectId = 0;
        String projectName = "";
        if (obj instanceof ProjectWarrantyCallbackVO) {
            projectId = ((ProjectWarrantyCallbackVO) obj).getProjectId();
            projectName = ((ProjectWarrantyCallbackVO) obj).getProjectName();
        } else if (obj instanceof Map) {
            projectId = (int) ((Map) obj).get("projectId");
            projectName = (String) ((Map) obj).get("projectName");
        }
        if (StringUtils.isNotBlank(projectName)) {
            if (projectName.startsWith("<")) {
                projectName = projectName.substring(1);
            }
            if (projectName.endsWith(">")) {
                projectName = projectName.substring(0, projectName.length() - 1);
            }
        }
        return "<a href='javascript:void(0)' class='updateMark' onclick='updateProject( \"" + Base64Util.EncodeBase64(projectId) + "\")'>" + projectName + "</a>";
    }

    public String getOperateUrl() {
        Object obj = getCurrentRowObject();
        Object projectId = null;
        Object id = null;
        Object latestId = null;
        Object latestRenewalIntention = null;
        Object latestPhoneAnswerState = null;
        if (obj instanceof ProjectWarrantyCallbackVO) {
            ProjectWarrantyCallbackVO vo = ((ProjectWarrantyCallbackVO) obj);
            projectId = vo.getProjectId();
            latestId = vo.getId();
            id = vo.getId();
            latestRenewalIntention = vo.getRenewalIntention();
        } else if (obj instanceof Map) {
            Map<String, Object> vo = ((Map) obj);
            projectId = vo.get("projectId");
            id = vo.get("id");
            latestId = vo.get("latestId");
            latestRenewalIntention = vo.get("latestRenewalIntention");
            latestPhoneAnswerState = vo.get("latestPhoneAnswerState");
        }
        boolean canEdit = ProjectWarrantyCallbackVO.canEdit(latestRenewalIntention, latestPhoneAnswerState);
        id = canEdit ? latestId : id;
        StringBuilder html = new StringBuilder();
        html.append("<a class=\"btn btn-xs btn-info\" href=\"javascript:popWindow('module/sub/warrantyCallback_createProjectWarrantyCallback.action?project.projectId=${projectId}&projectWarrantyCallback.id=${id}', '95vw', 600, '维保回访', 'createProjectWarrantyCallback', true)\">回访</a>");
        if (latestRenewalIntention != null) {
            html.append(" <a class=\"btn btn-xs btn-success\" href=\"javascript:popWindow('module/sub/warrantyCallback_projectWarrantyCallback.action?projectWarrantyCallback.projectId=${projectId}&projectWarrantyCallback.id=${id}', '95vw', 600, '维保回访记录', 'ProjectWarrantyCallback', true)\">查看</a>");
        }
        
        return html.toString().replace("${id}", ObjectUtils.defaultIfNull(id, "").toString())
                .replace("${projectId}", ObjectUtils.defaultIfNull(projectId, "").toString());
    }
    
    /**
     * 获取续保意向描述
     * @return
     */
    public String getRenewalIntentionName() {
        Object obj = getCurrentRowObject();
        Object renewalIntention = null;
        if (obj instanceof ProjectWarrantyCallbackVO) {
            ProjectWarrantyCallbackVO vo = ((ProjectWarrantyCallbackVO) obj);
            renewalIntention = vo.getRenewalIntention();
        } else if (obj instanceof Map) {
            Map vo = ((Map) obj);
            renewalIntention = vo.get("renewalIntention");
        }
        return (String) RenewalIntentionDecorator.getRenewalIntentionName(renewalIntention);
    }
    
    /**
     * 获取续保意向描述
     * @return
     */
    public String getPhoneAnswerStateName() {
        Object obj = getCurrentRowObject();
        Object phoneAnswerState = null;
        Object phoneAnswerStateName = null;
        if (obj instanceof ProjectWarrantyCallbackVO) {
            ProjectWarrantyCallbackVO vo = ((ProjectWarrantyCallbackVO) obj);
            phoneAnswerState = vo.getCustomInfoByKey("phoneAnswerState");
            phoneAnswerStateName = vo.getCustomInfoByKey("phoneAnswerStateName");
        } else if (obj instanceof Map) {
            Map vo = ((Map) obj);
            Map customIfno = JSON.parseObject(ObjectUtils.defaultIfNull(vo.get("customInfo"), "{}").toString(), HashMap.class);
            phoneAnswerState = customIfno.getOrDefault("phoneAnswerState", vo.get("phoneAnswerState"));
            phoneAnswerStateName = customIfno.getOrDefault("phoneAnswerStateName", vo.get("phoneAnswerStateName"));
        }
        if (phoneAnswerStateName != null) {
            return phoneAnswerStateName.toString();
        }
        return phoneAnswerState != null ? phoneAnswerState.toString() : "";
    }
}