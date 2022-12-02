package com.dp.plat.pms.springmvc.service.impl;

import static com.dp.plat.core.param.RoleConstant.ROLE_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_ADMIN;
import static com.dp.plat.pms.springmvc.constant.RoleConstant.ROLE_PM_SUB_ADMIN;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.CustomRuntimeException;
import com.dp.plat.core.pojo.Company;
import com.dp.plat.core.pojo.NotifyTemplate;
import com.dp.plat.core.service.ICompanyService;
import com.dp.plat.core.service.INotifyTemplateService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.util.DateUtil;
import com.dp.plat.core.util.SystemLogUtil;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.extend.d365.model.PurchaseHeader;
import com.dp.plat.pms.extend.d365.model.PurchaseLine;
import com.dp.plat.pms.extend.d365.service.IPurchaseLineService;
import com.dp.plat.pms.extend.d365.service.IPurchaseService;
import com.dp.plat.pms.extend.d365.util.D365Api;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.DispatchType;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.dao.DispatchProjectMapper;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.Facilitator;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IFacilitatorService;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.util.PermissionUtils;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.pms.springmvc.vo.ProjectVO;

/**
 *
 * Created by CodeGenerator
 */
@Service("dispatchProjectService")
public class DispatchProjectService extends AbstractBaseService<DispatchProjectMapper, DispatchProject> implements IDispatchProjectService {

    @Autowired
    private IProjectHeaderService projectHeaderService;
    
    @Autowired
    private IFacilitatorService facilitatorService;
    
    @Autowired
    private INotifyTemplateService notifyTemplateService;
    
    @Autowired
    private ICompanyService companyService;
    
    @Autowired
    private IPurchaseService purchaseService;
    
    @Autowired
    private IPurchaseLineService purchaseLineService;
    
    @Override
    public List<DispatchVO> selectDispatchProjectVOList(DispatchVO dispatch) {
        return dao.selectDispatchProjectVOList(dispatch);
    }

    @Override
    public Long countDispatchProjectVOList(DispatchVO dispatch) {
        return dao.countDispatchProjectVOList(dispatch);
    }

    @Override
    public DispatchVO selectVOByPrimaryKey(Integer id) {
        DispatchVO dispatch = new DispatchVO();
        dispatch.setId(id);
        List<DispatchVO> list = this.selectDispatchProjectVOList(dispatch);
        return list.stream().findFirst().orElse(null);
    }

    @Override
    public void insertOrUpdateSelective(DispatchProject dispatch) {
        if (dispatch.getId() != null) {
            this.updateByPrimaryKeySelective(dispatch);
        } else {
            this.insertSelective(dispatch);
        }
    //		dao.insertOrUpdateSelective(dispatch);
    }

    @Override
    @Transactional
    public void dispatchSubmit(Integer id, DispatchVO dispatch) {
        String dispatchType = dispatch.getType();
        if (StringUtils.isBlank(dispatchType)) {
            throw new CustomRuntimeException("请先选择转包类型！");
        }
        String dispatchSeq = dispatch.getDispatchSeq();
        String facilitatorCode = dispatch.getFacilitatorCode();
        String dispatchNo = dispatch.getDispatchNo();
        Date dispatchTime = new Date();
        if (StringUtils.isBlank(dispatchSeq)) {
            dispatchSeq = generateDispatchSeq(facilitatorCode);
        }
        if (DispatchType.FRAMEWORK_AGREEMENT.equals(dispatch.getType()) && StringUtils.isBlank(dispatchNo)) {
            dispatchNo = generateDispatchNo(dispatchTime, dispatchSeq);
        }
        if (StringUtils.isBlank(dispatchNo)) {
            throw new CustomRuntimeException("请输入转包合同号！");
        }

        DispatchProject temp = new DispatchProject();// dispatch;// this.selectByPrimaryKey(id);
        temp.setId(id);
        temp.setDispatchTime(dispatchTime);
        temp.setDispatchSeq(dispatchSeq);
        temp.setDispatchNo(dispatchNo);
        // 派单执行中
        temp.setState(50);
        temp.setDispatched(true);
        this.updateByPrimaryKeySelective(temp);
        
        DispatchVO dispatchVO = this.selectVOByPrimaryKey(id);
        // 推D365的采购订单
        this.pushPurchaseOrder(dispatchVO);
        BeanUtils.copyProperties(dispatchVO, dispatch);
    }
    
    /**
     * 推D365的采购订单
     * @param dispatch
     */
    public void pushPurchaseOrder(DispatchProject dispatch) {
    	String dispatchType = dispatch.getType();
    	// 获取项目转包推采购订单的配置项
    	String configStr = SystemConfig.systemVariables.get("pm.project.dispatch.pushPurchaseOrder.config");
		if (StringUtils.isBlank(configStr)) {
			NotifyTemplate template = notifyTemplateService.selectByTemplateCode("pm.project.dispatch.pushPurchaseOrder.config");
			if (template != null) {
				configStr = template.getContent();
			}
		}
		configStr = StringUtils.defaultIfBlank(configStr, "{}");
		Map<String, Object> config = JSON.parseObject(configStr, new TypeReference<HashMap<String, Object>>() {});
        boolean enablePushPurchaseOrder = Boolean.TRUE.equals(Boolean.parseBoolean(String.valueOf(config.get("enablePushPurchaseOrder"))));
        if (!enablePushPurchaseOrder) {
        	return;
        }
               
        // 设置账套
        Company company = companyService.selectByPrimaryKey(UserContext.getOrgId());
        String dataAreaId = company.getCompAccount();
        config.put("dataAreaId", dataAreaId);
        
        // 创建采购订单头
		PurchaseHeader purchTable = this.createPurchashHeader(dispatch, config);
		List<PurchaseLine> purchLines = this.createPurchaseLines(dispatch, config);
		
		// 调用D365推送采购订单接口
		dispatch = D365Api.pushPurchaseOrder(dispatch, dataAreaId, purchTable, purchLines, config);
		this.updateByPrimaryKeySelective(dispatch);
    }
    
    /**
     * 基于项目转包创建采购订单头
     * @param dispatch
     * @param config
     * @return
     */
    public PurchaseHeader createPurchashHeader(DispatchProject dispatch, Map<String, Object> config) {
        // 检查服务商编码是不是存在
        String facilitatorCode = dispatch.getFacilitatorCode();
        Facilitator facilitator = new Facilitator();
        facilitator.setCode(facilitatorCode);
        facilitator.setType(dispatch.getType());
        List<Facilitator> facilitators = facilitatorService.selectBySelective(facilitator);
        if (facilitators.isEmpty()) {
            throw new CustomRuntimeException("该服务商不存在或转包类型与合作类型不匹配！");
        }
        facilitator = facilitators.get(0);
        
        DispatchVO dispatchVO = null;
        if (dispatch instanceof DispatchVO) {
            dispatchVO = (DispatchVO) dispatch;
        } else {
            dispatchVO = this.selectVOByPrimaryKey(dispatch.getId());
        }
        
        // 判断采购订单池是否指定
        String purchPoolId = (String) config.get("purchPoolId");
        if (StringUtils.isBlank(purchPoolId)) {
            throw new CustomRuntimeException("采购订单池未指定！");
        }
        
        // 获取指定的仓库
        String inventLocationId = (String) config.getOrDefault("inventLocationId", "");
        
        // 获取工号
        String workNo = UserContext.getCurrentPrincipal().getUserInfo().getWorkNo();
        // 处理备注信息
        String remark = SystemLogUtil.format((String) config.getOrDefault("remarkFormat", dispatch.getRemark()), dispatch);
        
        // 获取项目进度
        ProjectVO minProgressProject = this.queryMinProgressProject(dispatch);
        
        // 填充采购订单的基准单位
        D365Api.fillPurchaseUnitBase(dispatch, config);
        
        // 创建采购订单头
        PurchaseHeader purchTable = new PurchaseHeader();
        purchTable.setSourceType(DataType.PROJECT_DISPATCH);
        purchTable.setSourceId(dispatch.getId());
        purchTable.setDataAreaId((String) config.get("dataAreaId")); // 账套
        purchTable.vendAccount(facilitator.getAccount()) // D365供应商编号
//                .purchName(dispatch.getDispatchName())// 采购事项（供应商名称）
                .purchName(facilitator.getName())// 采购事项（供应商名称）
                .purchPoolId(purchPoolId.toString()) // 采购订单池
                .purContract(dispatch.getDispatchNo()) // 采购合同号
                .salesContract(dispatch.getContractNos()) // 销售合同号
                .contractAmount(RegExUtils.replaceAll(dispatch.getDispatchAmount(), ",", "")) // 合同金额
                .inventLocationId(inventLocationId) // 仓库
                .deliveryDate((String) dispatch.getCustomInfoByKey("deliveryDate", DateUtil.getTodayDateTime())) // 交货日期
                .dlvMode((String) dispatch.getCustomInfoByKey("dlvMode")) // 交货模式
                .dlvTerm((String) dispatch.getCustomInfoByKey("dlvTerm")) // 交货条款
                .payment(dispatch.getPrepaidRule()) // 付款条款
                .paymMode((String) dispatch.getCustomInfoByKey("paymMode")) // 付款方式
                .remark(remark) // 备注，解析remarkFormat
                .otherSysNum(String.valueOf(config.getOrDefault("sysTag", "PMS2#")) + dispatch.getId()) // 外部系统编号
                .projectName((String) dispatch.getDispatchName()) // 项目名称
                .projectProgress((String) minProgressProject.getCustomInfoByKey("projectProgress", "0")) // 项目进度
                .subcontractType((String) config.getOrDefault("typeTag", "安服") + dispatchVO.getCustomInfoByKey("serviceType", dispatchVO.getCustomInfoByKey("typeName", dispatchVO.getTypeName()))) // 转包类型
                .subcontStartDate(StringUtils.trimToNull((String) dispatch.getCustomInfoByKey("subcontStartDate"))) // 转包周期开始
                .subcontEndDate(StringUtils.trimToNull((String) dispatch.getCustomInfoByKey("subcontEndDate"))) // 转包周期结束
                .applicant(workNo) // 申请人
                .workerPurchPlacer(workNo) // 订货人
        ;
        return purchTable;
    }
    
    /**
     * 基于项目转包创建采购订单行
     * @param dispatch
     * @param config
     * @return
     */
    public List<PurchaseLine> createPurchaseLines(DispatchProject dispatch, Map<String, Object> config) {
        // 判断采购订单物料是否指定
        String itemId = (String) config.get("itemId");
        if (StringUtils.isBlank(itemId)) {
            throw new CustomRuntimeException("采购订单物料编码未指定！");
        }
        // 获取指定的仓库
        String inventLocationId = (String) config.getOrDefault("inventLocationId", "");
        
        // 获取采购订单的基准单位
        String purchUnitBase = (String) dispatch.getCustomInfoByKey("purchUnitBase", config.getOrDefault("purchUnitBase", "price"));
        // 获取采购订单的基准单价，默认为1
        BigDecimal purchPriceBase = new BigDecimal(String.valueOf(dispatch.getCustomInfoByKey("purchPriceBase", config.getOrDefault("purchPriceBase", "1.00")))).setScale(2, RoundingMode.HALF_UP);
        // 获取采购订单的基准数量，默认为1,
        BigDecimal purchQtyBase = new BigDecimal(String.valueOf(dispatch.getCustomInfoByKey("purchQtyBase", config.getOrDefault("purchQtyBase", "1.00")))).setScale(2, RoundingMode.HALF_UP);

        BigDecimal dispatchAmount = new BigDecimal(dispatch.getDispatchAmount()).setScale(2, RoundingMode.HALF_UP);
        // 默认指定基准采购价，数量由转包价和基准采购价确定
        BigDecimal purchPrice = purchPriceBase;
        BigDecimal purchQty = purchQtyBase;
        if ("price".equalsIgnoreCase(purchUnitBase)) {
            // 根据转包价和基准采购价计算采购订单的采购数量
            purchQty = dispatchAmount.divide(purchPriceBase, 2, RoundingMode.HALF_UP);
        } else {
            // 根据转包价和基准数量计算采购订单的采购单价
            purchPrice = dispatchAmount.divide(purchQtyBase, 2, RoundingMode.HALF_UP);
        }

        // 处理备注信息
        String remark = StringUtils.defaultIfBlank(SystemLogUtil.format((String) config.getOrDefault("lineRemarkFormat", config.getOrDefault("remarkFormat", "")), dispatch), dispatch.getRemark());
        
        
        // 税组，默认为税组，如果没有则税率加碎组前缀组成税组
        String taxRate = (String) dispatch.getCustomInfoByKey("taxRate");
        String taxItemGroup = (String) dispatch.getCustomInfoByKey("taxItemGroup");
        if (StringUtils.isBlank(taxItemGroup) && StringUtils.isNotBlank(taxRate)) {
            taxItemGroup = config.getOrDefault("taxGroupPrefix", "J") + taxRate;
        }
        
        // 创建采购订单行
        List<PurchaseLine> purchLines = new ArrayList<PurchaseLine>();
        PurchaseLine purchaseLine = new PurchaseLine();
        purchaseLine.setDataAreaId((String) config.get("dataAreaId")); // 账套
        purchaseLine.lineNum(dispatch.getId().toString()) // 行号（用系统ID代替）
                .itemId(itemId) // 物料编码
                .purchQty(purchQty) // 采购数量
                .purchPrice(purchPrice)// 采购价
                .inventLocationId(inventLocationId)// 仓库
                .taxItemGroup(taxItemGroup)// 税收组
                .inventSerialId((String) config.get("inventSerialId"))// 厂商型号
                .officeCode(dispatch.getProfitDepCode())// 办事处
                .deliveryDate((String) dispatch.getCustomInfoByKey("deliveryDate"))// 交货日期
                .remark(remark)// 行备注
                .multiDimID((String) dispatch.getCustomInfoByKey("multiDimID"))// 行多维度ID
                .investmentProject((String) dispatch.getCustomInfoByKey("investmentProject"))// 募投项目
                .dimBankAccount((String) dispatch.getCustomInfoByKey("dimBankAccount"))// 维度-银行账户
                .dimCustomer((String) dispatch.getCustomInfoByKey("dimCustomer"))// 维度-客户
                .dimVendor((String) dispatch.getCustomInfoByKey("dimVendor"))// 维度-供应商
                .dimEmployee((String) dispatch.getCustomInfoByKey("dimEmployee"))// 维度-员工
                .dimContract((String) dispatch.getCustomInfoByKey("dimContract"))// 维度-合同号
                .dimDepartment(dispatch.getProfitDepCode())// 维度-部门
                .dimBU((String) dispatch.getCustomInfoByKey("dimBU"))// 维度-BU
                .dimProductLine((String) dispatch.getCustomInfoByKey("dimProductLine"))// 维度-产品线
                .dimTerritory((String) dispatch.getCustomInfoByKey("dimTerritory"))// 维度-区域
                .dimIndustry((String) dispatch.getCustomInfoByKey("dimIndustry"))// 维度-行业
                .dimMultiDimID((String) dispatch.getCustomInfoByKey("dimMultiDimID"))// 维度-多维度ID
        ;
        purchLines.add(purchaseLine);
        return purchLines;
    }
    
    /**
     * 获取项目进度最慢的项目
     * @param dispatch
     * @return
     */
    public ProjectVO queryMinProgressProject(DispatchProject dispatch) {
        // 获取项目进展最慢的项目
        List<String> projectIds = Arrays.asList(StringUtils.split(dispatch.getProjectIds(), ","));
        ProjectVO minProgressProject = projectIds.parallelStream().map(projectId -> {
            return projectHeaderService.selectByPrimaryKey(Integer.valueOf(projectId));
        }).min((p, n) -> {
            Integer progressPrev = Integer.valueOf((String) p.getCustomInfoByKey("projectProgress", "0"));
            Integer progressNext = Integer.valueOf((String) n.getCustomInfoByKey("projectProgress", "0"));
            return progressPrev.compareTo(progressNext);
        }).map(project -> {
            ProjectVO projectVO = new ProjectVO();
            BeanUtils.copyProperties(project, projectVO);
            return projectVO;
        }).get();
        return minProgressProject;
    }

    /**
	 * 生成派单编号
	 * 
	 * @param facilitatorCode
	 * @return dispatchSeq
	 */
    @Override
    public String generateDispatchSeq(String facilitatorCode) {
        if (StringUtils.isBlank(facilitatorCode)) {
            return null;
        }
        // 查询服务商的项目派单顺序
        DispatchVO temp = new DispatchVO();
        temp.setDispatched(true);
        temp.setFacilitatorCode(facilitatorCode);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        temp.setDispatchYear(year);
        long count = this.countBySelective(temp) + 1;
        String seqFormat = SystemConfig.systemVariables.getOrDefault("pm.project.disptachSeq.format", "%02d");
        // String seqFormat =
        // SystemConfig.systemVariables.getOrDefault("pm.project.disptachSeq.format",
        // "{100:'%02d', 256:'%02x'}");
        // Map<Integer, String> seqFormats = (Map<Integer, String>)
        // JSON.parse(seqFormat);
        // Integer minInt = Integer.MAX_VALUE;
        // for (Entry<Integer, String> format : seqFormats.entrySet()) {
        // Integer key = format.getKey();
        // if (Long.valueOf(count).intValue() < key && key < minInt) {
        // minInt = key;
        // seqFormat = format.getValue();
        // }
        // }
        Object[] seqs = new Object[] { year, facilitatorCode, String.format(seqFormat, count) };
        String dispatchSeq = StringUtils.join(seqs, "-");
        return dispatchSeq;
    }

    /**
	 * 生成框架协议派单合同
	 * 
	 * @param dispatchTime
	 * @param dispatchSeq
	 * @return dispatchNo
	 */
    @Override
    public String generateDispatchNo(Date dispatchTime, String dispatchSeq) {
        String dispatchTimeStr = DateFormatUtils.format(dispatchTime, "yyyyMMdd");
        String dispatchNo = ProjectConstant.DispatchNOPrefix.AF + dispatchTimeStr + dispatchSeq.replaceAll("-", "");
        return dispatchNo;
    }

    @Override
    public List<DispatchVO> selectDispatchVOWithAmountBySelective(DispatchVO dispatchProject) {
        return dao.selectDispatchVOWithAmountBySelective(dispatchProject);
    }

    @Override
    public List<DispatchVO> selectDispatchVOWithAmountBySelectivePageable(PageParam<Object> pageParam) {
        return dao.selectDispatchVOWithAmountBySelectivePageable(pageParam);
    }
    
    @Override
    public DispatchVO selectDispatchVOWithAmount(Integer dispatchId) {
        DispatchVO dispatchProject = new DispatchVO();
        dispatchProject.setId(dispatchId);
        List<DispatchVO> list = this.selectDispatchVOWithAmountBySelective(dispatchProject);
        if (null != list && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public PermissionResult checkPermission(DispatchVO v, String... permissions) {
        if (!UserContext.checkPermission(permissions)) {
            return new PermissionResult(Boolean.FALSE, "没有权限进行该操作！");
        }
        Boolean isPermit = false;
        String permissionType = "";
        PermissionResult result = null;
        if (!UserContext.checkPermission("project:*") && v != null) {
            ProjectVO project = new ProjectVO();
            project.setProjectId(v.getProjectId());
            project.setProjectIds(v.getProjectIds());
//            Map<String, Object> permission = projectHeaderService.checkPermissionMap(project, permissions);
//            PermissionResult checkPermit = new PermissionUtils("dispatch:", new String[] { ROLE_ADMIN, ROLE_PM_ADMIN, ROLE_PM_SUB_ADMIN }).checkPermit(permission, permissions);
            PermissionResult projectPermit = projectHeaderService.checkPermission(project, permissions);
			String[] allPermitRoles = PermissionUtils.getRetainAllRoles(new String[] { ROLE_ADMIN, ROLE_PM_ADMIN, ROLE_PM_SUB_ADMIN }, projectPermit.getRoles());
			PermissionResult checkPermit = new PermissionUtils("dispatch:" , allPermitRoles)
					.checkPermit(projectPermit.getPermissionMap(), permissions);
            isPermit = checkPermit.isPermit();
            permissionType = checkPermit.getPermissionType();
            result = checkPermit;
        } else {
            isPermit = true;
            permissionType = "all";
        }
        return result != null ? result : new PermissionResult(isPermit, null, permissionType);
    }
}
