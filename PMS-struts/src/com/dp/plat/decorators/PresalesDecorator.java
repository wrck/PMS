package com.dp.plat.decorators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.displaytag.decorator.TableDecorator;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.Presales;
import com.dp.plat.data.vo.PresalesExportVO;
import com.dp.plat.param.FileParam;
import com.dp.plat.util.ActivityMessage;
import com.dp.plat.util.DateUtil;
import com.dp.plat.util.MessageUtil;

/**
 * 售前测试displayTable 装饰器
 */
public class PresalesDecorator extends TableDecorator {
    
    public String getPresalesWrapper(){
        Presales presales = (Presales) getCurrentRowObject();
        UserContext context = UserContext.getUserContext();
        String username = UserContext.getUserContext().getUsername();
        String taskDefKey = presales.getTaskDefKey();
        if( presales.getApplyState() == ActivityMessage.FLOW_RUNING || presales.getApplyState() == ActivityMessage.FLOW_UNSTART
                ){//任务ID不为空，且为当前办理人
            boolean canCreate = context.isHasAnyRole(MessageUtil.ROLE_ENGINEEMANAGER, MessageUtil.ROLE_PRESALES_STAFF);
            if(("usertask2".equals(taskDefKey) || "serviceApprove".equals(taskDefKey)) && username.equals(presales.getTaskAssignee())){
                return "<a href='module/presales_smaduit.action?presales.presalesId="+presales.getPresalesId()+"'>办理</a>";
            } else if("usertask3".equals(taskDefKey) && username.equals(presales.getTaskAssignee())){
                return "<a href='module/presales_pmaduit.action?presales.presalesId="+presales.getPresalesId()+"'>办理</a>";
            } else if("usertask4".equals(taskDefKey) && "emRole".equals(presales.getTaskAssignee()) && (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || context.isHasRole(MessageUtil.ROLE_PRESALES_STAFF))){
                return "<a href='module/presales_emaduit.action?presales.presalesId="+presales.getPresalesId()+"'>回访</a>";
            } else if("usertask1".equals(taskDefKey) && "emRole".equals(presales.getTaskAssignee()) && (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || context.isHasRole(MessageUtil.ROLE_PRESALES_STAFF))){
                return "<a href='module/presales_input.action?presales.presalesId="+presales.getPresalesId()+"' >办理</a>";
            } else if(presales.getApplyState() == ActivityMessage.FLOW_UNSTART && canCreate){
                return "<a href='module/presales_input.action?presales.presalesId="+presales.getPresalesId()+"' >创建</a>";
            }
        }
        if (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) && username.equals(presales.getServiceManager())) {
            return "<a href='module/presales_smaduit.action?presales.presalesId="+presales.getPresalesId()+"' >变更</a>";
        }
        return "<a href='module/presales_read.action?presales.presalesId="+presales.getPresalesId()+"' >查看</a>";
    }

    public String getExpendPresalesQuesResult() {
        Object obj = getCurrentRowObject();
        StringBuilder html = new StringBuilder();
        if (obj instanceof PresalesExportVO) {
            Integer maxQuesResultSize = (Integer) getPageContext().getAttribute("MaxQuesResultSize");
            PresalesExportVO presales = (PresalesExportVO) obj;
            String questionResults = StringUtils.trimToEmpty(presales.getQuestionResults());
            List<String> resultList = Arrays.asList(StringUtils.split(questionResults, ","));
            for (Iterator<String> iterator = resultList.iterator(); iterator.hasNext();) {
                String resultStr = StringUtils.trimToEmpty(iterator.next());
                String[] result = StringUtils.split(resultStr, "=");
                if (result.length > 1) {
                    html.append(result[1]);
                }
                if (iterator.hasNext()) {
                    html.append("</td><td>");
                }
            }
            if (maxQuesResultSize == null || maxQuesResultSize < resultList.size()) {
                maxQuesResultSize = resultList.size();
                getPageContext().setAttribute("MaxQuesResultSize", maxQuesResultSize);
            }
        } else if (obj instanceof Map) {
            Map<String, Object> presales = (Map<String, Object>) obj;
            for (int i = 1;; i++) {
                String key = "questionResult" + i;
                String nextKey = "questionResult" + (i + 1);
                if (!presales.containsKey(key)) {
                    break;
                } else {
                    html.append(StringUtils.trimToEmpty((String) presales.get(key)));
                    if (presales.containsKey(nextKey)) {
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
        if (obj instanceof PresalesExportVO) {
            PresalesExportVO presales = (PresalesExportVO) obj;
            List<FileParam> resultList = presales.getFileParams();
            for (Iterator<FileParam> iterator = resultList.iterator(); iterator.hasNext();) {
                FileParam fileParam = iterator.next();
                StringBuilder str = new StringBuilder(atag);
                str.append(fileParam.getId()).append("'>").append(fileParam.getFileName()).append("</a>");
                html.add(str.toString());
            }
        } else if (obj instanceof Map) {
            Map<String, Object> presales = (Map<String, Object>) obj;
            String deliverFiles = StringUtils.trimToEmpty((String) presales.get("deliverFiles"));
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
        if (obj instanceof PresalesExportVO) {
            PresalesExportVO presales = (PresalesExportVO) obj;
            List<FileParam> resultList = presales.getFileParams();
            for (Iterator<FileParam> iterator = resultList.iterator(); iterator.hasNext();) {
                FileParam fileParam = iterator.next();
                html.add(fileParam.getFileName());
            }
        } else if (obj instanceof Map) {
            Map<String, Object> presales = (Map<String, Object>) obj;
            String deliverFiles = StringUtils.trimToEmpty((String) presales.get("deliverFiles"));
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
    
    public String getLendPeriod() {
        Object obj = getCurrentRowObject();
        Date startTime = null, endTime = null;
        if (obj instanceof PresalesExportVO) {
            PresalesExportVO presales = (PresalesExportVO) obj;
            startTime = presales.getDeliveryDate();
            endTime = presales.getRmaDate();
        } else if (obj instanceof Map) {
            startTime = (Date) ((Map) obj).get("deliveryDate");
            endTime = (Date) ((Map) obj).get("rmaDate");
        }
        if (endTime == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            endTime = calendar.getTime();
        }
        if (endTime != null) {
            endTime = DateUtils.addDays(endTime, 1);
        }
        return DateUtil.getFormatedDateIntervalByType(startTime, endTime, Calendar.DATE);
    }
    
}