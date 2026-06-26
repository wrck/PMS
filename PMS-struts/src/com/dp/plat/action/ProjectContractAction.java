package com.dp.plat.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Contract;
import com.dp.plat.data.bean.Product;
import com.dp.plat.util.Base64Util;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.Util;

/**
 * 项目合同管理 Action
 * 处理合同合并、拆分、发货等操作
 * 
 * @author PMS Team
 */
public class ProjectContractAction extends ProjectBaseAction {
    
    private static final long serialVersionUID = 1L;
    
    // 合同参数
    private String mergeContractNo;
    private List<Contract> contractList;
    private List<Product> productList;
    private String paramId;
    private String mergeBranchMark;
    private String selected;
    
    // 发货参数
    private List<?> orderDataList;
    private List<?> realOrderDataList;
    private int realOrderDataSize;
    private List<?> shipmentInfoList;
    
    /**
     * 进入合同拆分合并页面
     * @return
     */
    public String toMergeOrBranch() {
        navTabList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_NAV_MERGE_TAB);
        orderDataList = projectService.queryOrderDataListByProjectId(project.getProjectId());
        return INPUT;
    }
    
    /**
     * 查询要合并的合同信息
     * @return
     */
    public String checkMergeContract() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("mergeContractNo", mergeContractNo);
        
        int size = projectService.queryProjectContractCountByContractNo(Util.appendChar(mergeContractNo, "'"));
        if (size == 1) {
            result = 404;
        } else {
            contractList = projectService.queryContractList(paramMap);
        }
        return SUCCESS;
    }
    
    /**
     * 合并操作
     * @return
     */
    public String mergeContract() {
        if (selected == null || "".equals(selected)) {
            setErrmsg("请至少选择一条合同数据，谢谢！");
            return ERROR;
        }
        projectService.insertMergeContract(selected, projectId);
        redirect = "module/ProjectModify.action?project.paramId=" + Base64Util.EncodeBase64(projectId) + "&result=302";
        return SUCCESS;
    }
    
    /**
     * 项目拆分
     * @return
     */
    public String branchContract() {
        int newProjectId = projectService.insertNewProject(projectId, project.getProjectCode(), productList, mergeBranchMark);
        redirect = "module/ProjectModify.action?project.paramId=" + Base64Util.EncodeBase64(projectId) + "&result=202&paramId=" + Base64Util.EncodeBase64(newProjectId);
        return SUCCESS;
    }
    
    /**
     * 检查订单数据
     * @return
     */
    public String checkOrderData() {
        orderDataList = projectService.queryOrderDataListByProjectId(projectId);
        return SUCCESS;
    }
    
    /**
     * 检查实际订单数据
     * @return
     */
    public String checkRealOrderData() {
        realOrderDataList = projectService.queryRealOrderDataListByProjectId(projectId);
        realOrderDataSize = realOrderDataList != null ? realOrderDataList.size() : 0;
        return SUCCESS;
    }
    
    // Getter/Setter 方法
    public String getMergeContractNo() {
        return mergeContractNo;
    }
    
    public void setMergeContractNo(String mergeContractNo) {
        this.mergeContractNo = mergeContractNo;
    }
    
    public List<Contract> getContractList() {
        return contractList;
    }
    
    public List<Product> getProductList() {
        return productList;
    }
    
    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
    
    public String getParamId() {
        return paramId;
    }
    
    public String getMergeBranchMark() {
        return mergeBranchMark;
    }
    
    public void setMergeBranchMark(String mergeBranchMark) {
        this.mergeBranchMark = mergeBranchMark;
    }
    
    public String getSelected() {
        return selected;
    }
    
    public void setSelected(String selected) {
        this.selected = selected;
    }
    
    public List<?> getOrderDataList() {
        return orderDataList;
    }
    
    public List<?> getRealOrderDataList() {
        return realOrderDataList;
    }
    
    public int getRealOrderDataSize() {
        return realOrderDataSize;
    }
    
    public List<?> getShipmentInfoList() {
        return shipmentInfoList;
    }
}
