package com.dp.plat.subcontract.decorators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.TableDecorator;

import com.dp.plat.context.SystemContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.WorkflowCommonParam;
import com.dp.plat.param.FileParam;
import com.dp.plat.subcontract.constant.SubcontractConstant;
import com.dp.plat.subcontract.constant.SubcontractConstant.TaskKey;
import com.dp.plat.subcontract.entity.SubcontractDeliver;
import com.dp.plat.subcontract.entity.SubcontractPrice;
import com.dp.plat.subcontract.vo.SubcontractPaymentVO;
import com.dp.plat.subcontract.vo.SubcontractProjectVO;
import com.dp.plat.util.Base64Util;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.StringEscUtil;
import com.dp.plat.util.Util;
import com.dp.plat.warrantyCallback.vo.ProjectWarrantyCallbackVO;

/**
 * 项目转包装饰器
 */
public class SubcontractDecorator extends TableDecorator {
    
    public String getSmsTargetUrl() {
        Object object = getCurrentRowObject();
        Object objId = null;
        String procType = null;
        if (object instanceof Map) {
            objId = (Object) ((Map) object).get("objId");
            procType = (String) ((Map) object).get("procType");
        } else if (object instanceof SubcontractPrice){
            objId = ((SubcontractPrice) object).getObjId();
            procType = ((SubcontractPrice) object).getProcType();
        }
        
        if (objId == null || procType == null) {
            return "";
        }
        return "<a href=\"javascript:void(0)\" onclick=\"javascript:window.open('"
                + StringEscUtil.getText("pm.subcontract.sms.url") + "module/BusinessView43.action?param.objId=" + objId
                + "&dpActProcDesc.procType="+ procType +"');\"><span class=\"glyphicon glyphicon-link\"></span>外链</a>";
    }
    
    public String getSubcontractWsOperator() {
        Map<String, Object> object = (Map<String, Object>) getCurrentRowObject();
        if (object.get("taskId") != null) {
            String html = "<a href='module/subcontract_input.action?subcontract.id=" + object.get("subcontractId") + "'>办理</a>";
            if (!"-1".equals(object.get("taskId").toString())) {
                html += "&nbsp;&nbsp;&nbsp;"
                        + "<a target='_blank' href='work/sub/WorkFlowViewCurrentImage.action?param.taskId="
                        + object.get("taskId") + "'>查看当前流程</a>";
            }
            return html;
//          return "<a href='module/subcontract_input.action?subcontract.id=" + object.get("subcontractId") + "'>办理</a>"
//                  + "&nbsp;&nbsp;&nbsp;"
//                  + "<a target='_blank' href='work/sub/WorkFlowViewCurrentImage.action?param.taskId="
//                  + object.get("taskId") + "'>查看当前流程</a>";
        }
        return "";
    }
	
    public String getSubcontractFileName() {
        SubcontractDeliver object = (SubcontractDeliver) getCurrentRowObject();
//        return "<a href='module/subcontract_downloadFile.action?redirect=" + Base64Util.EncodeBase64(object.getId())
//                + "'>" + object.getFileName() + "</a>";
        return getViewableFileName(object);
    }
    
    public String getSubcontractSeeQuesnaireLink() {
        Map<String, Object> object = (Map<String, Object>) getCurrentRowObject();
        if (object.get("quesnaireId") != null) {
            return "<a href=\"javascript:popWindow('module/sub/callback_seeQuesnaire.action?quesnaireId=" + object.get("quesnaireId") + "',880, 600, '查看测评问卷', 'BudgetUpload', true)\">查看问卷</a>";
        }
        return "";
    }
    
    public String getPaidAmountWrapper() {
        Object object = getCurrentRowObject();
        String paidAmount = "";
        if (object instanceof SubcontractProjectVO) {
            paidAmount = ((SubcontractProjectVO) object).getPaidAmount();
        } else if (object instanceof SubcontractPaymentVO) {
            SubcontractPaymentVO payment = ((SubcontractPaymentVO) object);
            if (payment.getPaymentTime() != null) {
                paidAmount = payment.getAmount();
            }
            paidAmount = String.valueOf(payment.getCustomInfoByKey("paidAmount", paidAmount));
        }
        
        return Util.formatDecimal(paidAmount);
    }
    public String getPaymentApprovedAmountWrapper() {
        Object object = getCurrentRowObject();
        StringBuilder html = new StringBuilder("");
        if (object instanceof SubcontractPaymentVO) {
            SubcontractPaymentVO payment = (SubcontractPaymentVO) object;
            String amount = Util.formatDecimal(payment.getAmount());
            String approvedAmount = Util.formatDecimal(payment.getCustomInfoByKey("approvedAmount"));
            String showDefaultAmount = StringUtils.defaultIfBlank(approvedAmount, amount);
            
            if (!(Boolean.valueOf(String.valueOf(payment.getCustomInfoByKey("approved"))) || payment.getConfirmTime() != null)) {
                WorkflowCommonParam workflowCommonParam = (WorkflowCommonParam) this.getPageContext().findAttribute("workflowCommonParam");
                if (workflowCommonParam != null && TaskKey.ACCEPTANCE_TASK.equals(workflowCommonParam.getOutcome())) {
                    // //直接调用paymentList传参
                    //html.append("<input type='hidden' name='subcontractPaymentList[0].id' value='").append(payment.getId()).append("' />");
                    //html.append("<input name='subcontractPaymentList[0].customStrInfo.approvedAmount' value='")
                    // //使用workflowParam的自定义字段传参
                    html.append("<input name='approvedAmount' value='")
                        .append(showDefaultAmount)
                        .append("' data-type='payment' data-id='").append(payment.getId())
                        .append("' data-key='approvedAmount' data-value='").append(showDefaultAmount)
                        .append("' class='form-control task-acceptanceTask task-customInfo' />");
                } else {
                    html.append(approvedAmount);
                }
            } else {
                html.append(showDefaultAmount);
            }
        }
        return html.toString();
    }
    
    public String getDeliverableName(){
        Object object = getCurrentRowObject();
        Map<String, Object> config = SystemContext.getConfig(SubcontractConstant.SUBCONTRACT_INSPECTION_DELIVERY_TYPES_CONFIG_KEY);
        if (config.isEmpty()) {
            config = new LinkedHashMap<String, Object>();
            config.put("验收材料", "验收材料");
            config.put("发票原件", "发票原件（务必仅传发票原件，类型错误请在附件列表删除）");
        }
        StringBuilder fileNames = new StringBuilder("<div>");
        Map<String, List<String>> fileTypeNamesMap = new HashMap<String, List<String>>(config.size() + 1);
        if (object instanceof SubcontractPaymentVO) {
            SubcontractPaymentVO payment = (SubcontractPaymentVO) object;
            List<SubcontractDeliver> delivers = payment.getDelivers();
            if (delivers != null && !delivers.isEmpty()) {
                //List<String> tags = new ArrayList<String>(delivers.size());
                for (SubcontractDeliver deliver : delivers) {
                    String deliverType = deliver.getType();
                    List<String> tags = fileTypeNamesMap.get(deliverType);
                    if (tags == null) {
                        tags = fileTypeNamesMap.getOrDefault(deliverType, new ArrayList<String>(delivers.size()));
                        fileTypeNamesMap.put(deliverType, tags);
                    }
                    String viewableFileName = getViewableFileName(deliver, true);
                    
                    if (StringUtils.isNotBlank(viewableFileName)) {
                        tags.add(viewableFileName);
                    }
                }
                if (config.size() > 1) {
                    for (Entry<String, Object> deliverEntry : config.entrySet()) {
                        String key = deliverEntry.getKey();
                        Object value = deliverEntry.getValue();
                        List<String> tags = fileTypeNamesMap.getOrDefault(key, Collections.emptyList());
                        if (!tags.isEmpty()) {
                            fileNames.append("<b>").append(value).append("：</b>").append(StringUtils.join(tags, " | ")).append("<br/>");
                        }
                    }
                } else {
                    Collection<List<String>> values = fileTypeNamesMap.values();
                    for (List<String> tags : values) {
                        fileNames.append(StringUtils.join(tags, " | "));
                    }
                }
            }
            // 如果不为只读，则增加上传附件按钮
            if (!Boolean.valueOf(String.valueOf(payment.getCustomInfoByKey("readonly")))) {
                WorkflowCommonParam workflowCommonParam = (WorkflowCommonParam) this.getPageContext().findAttribute("workflowCommonParam");
                if (workflowCommonParam != null && TaskKey.APPLY_PAYMENT.equals(workflowCommonParam.getOutcome())) {
                    StringBuilder uploadTag = new StringBuilder();
                    for (Entry<String, Object> deliverEntry : config.entrySet()) {
                        String key = deliverEntry.getKey();
                        Object value = deliverEntry.getValue();
                        uploadTag.append("<span class=\"uploadWrapper\">\r\n")
                        .append("   <span>").append(value).append("</span>\r\n")
                        .append("   <span class=\"inspection\">\r\n")
                        .append("       <input type=\"file\" label=\"File\" name=\"paymentDeliverList[0].uploads\" class=\"form-control multipleFileType\" multiple=\"true\"/>\r\n")
                        .append("       <input type=\"hidden\" class=\"ignore\" name=\"paymentDeliverList[0].type\" value=\"\" data-type=\"").append(key).append("\"/>\r\n")
                        .append("   </span>\r\n")
                        .append("</span>");
                    }
                    fileNames.append(uploadTag.toString());
                }
            }
        }
        fileNames.append("</div>");
        return fileNames.toString();
    }
    
    public String getViewableFileName() {
        return getViewableFileName(null);
    }
    
    public String getViewableFileName(SubcontractDeliver deliver) {
        return getViewableFileName(deliver, false);
    }
    
    public String getViewableFileName(SubcontractDeliver deliver, boolean overflow) {
        deliver = deliver != null ? deliver : (SubcontractDeliver) getCurrentRowObject();
        String downloadTag = "<a href='module/subcontract_downloadFile.action?redirect=%s'>下载</a>";
        String viewTag = "<a href='%s%s' target='_blank'>预览</a>";
        Pattern pattern = Pattern.compile(".*\\.(png|jpg|jpeg|gif|pdf)$", Pattern.CASE_INSENSITIVE);
        String contextPath = getPageContext().getRequest().getServletContext().getContextPath();
        String fileName = deliver.getFileName();
        String filePath = deliver.getFilePath();
        //        String encodeFileName = fileName;
        //        String encodeFilePath = filePath;
        String encodeId = Base64Util.EncodeBase64(deliver.getId());
        //        try {
        //            encodeFilePath = URLEncoder.encode(filePath, "UTF-8");
        //            encodeFileName = URLEncoder.encode(fileName, "UTF-8");
        //        } catch (UnsupportedEncodingException e1) {
        //            e1.printStackTrace();
        //        }
        String overflowCss = " class='text-ellipsis' style='display:inline-block;max-width:10vw;' ";
        String atag2 = "<a %s href='module/subcontract_downloadFile.action?redirect=%s' title='%s'>%s</a>";
        String alink = String.format(atag2, overflow ? overflowCss: "", encodeId, fileName, fileName);
        StringBuilder str = new StringBuilder();
        if (pattern.matcher(fileName).matches()) {
            str.append("<span class='hover-wrapper'>")
                .append(alink)
                .append(String.format("<label class='hover-label' title='%s'>", fileName))
                .append(String.format(viewTag, contextPath, filePath))
                .append(String.format(downloadTag, encodeId))
                .append("</label>").append("</span>");
        } else {
            str.append(alink);
        }
        return str.toString();
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
        int supervisionId = 0;
        String taskName = "";
        Boolean state = true;
        String createUser = "";
        if (obj instanceof ProjectWarrantyCallbackVO) {
            supervisionId = ((ProjectWarrantyCallbackVO) obj).getId();
//            state = ((ProjectWarrantyCallbackVO) obj).getState();
            createUser = ((ProjectWarrantyCallbackVO) obj).getCreateBy();
        } else if (obj instanceof Map) {
            supervisionId = (int) ((Map) obj).get("id");
//            state = (Boolean) ((Map) obj).get("state");
            createUser = (String) ((Map) obj).get("createBy");
        }
        if (Boolean.TRUE.equals(state)) {
            return "";
        }
        User user = UserContext.getUserContext().getUser();
        StringBuilder url = new StringBuilder();
        if (Boolean.FALSE.equals(state) && (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
                || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_CALLBACKPER)
                || user.isHasRole(MessageUtil.ROLE_WARRANTY_CALLBACKER))) {
            taskName = "办理";
            url.append("<a href='javascript:void(0)' onclick='").append("openQuesTask(\"").append(supervisionId).append("\")'>").append(taskName).append("</a>");
        }
        if (Boolean.FALSE.equals(state) && (createUser.equals(user.getUsername()) || (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)))) {
            if (StringUtils.isNotBlank(taskName)) {
                url.append(" | ");
            }
            taskName = "删除";
            url.append("<a href='javascript:void(0)' onclick='").append("deleteWarrantyCallback(\"").append(supervisionId).append("\")'>").append(taskName).append("</a>");
        }
        return url.toString();
    }
    
}