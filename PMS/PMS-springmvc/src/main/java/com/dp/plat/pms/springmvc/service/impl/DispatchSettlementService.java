package com.dp.plat.pms.springmvc.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.dp.plat.context.SystemContext;
import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.exception.CustomRuntimeException;
import com.dp.plat.core.pojo.Company;
import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.pojo.NotifyTemplate;
import com.dp.plat.core.service.ICompanyService;
import com.dp.plat.core.service.IFileInfoService;
import com.dp.plat.core.service.INotifyTemplateService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.util.DateUtil;
import com.dp.plat.core.util.FileUtil;
import com.dp.plat.core.util.SystemLogUtil;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.aop.DispatchSettlementUpdateAspect;
import com.dp.plat.pms.extend.d365.entity.PurchaseReceipt;
import com.dp.plat.pms.extend.d365.model.PurchaseReceiptHeader;
import com.dp.plat.pms.extend.d365.model.PurchaseReceiptLine;
import com.dp.plat.pms.extend.d365.service.IPurchaseReceiptLineService;
import com.dp.plat.pms.extend.d365.service.IPurchaseReceiptService;
import com.dp.plat.pms.extend.d365.util.D365Api;
import com.dp.plat.pms.extend.fp.entity.InvoiceProviderInfo;
import com.dp.plat.pms.extend.fp.model.ElectronicInvoiceModel;
import com.dp.plat.pms.extend.fp.model.ElectronicInvoiceResponse;
import com.dp.plat.pms.extend.fp.model.Response;
import com.dp.plat.pms.extend.fp.util.FPApi;
import com.dp.plat.pms.extend.fp.util.InvoiceUtil;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.dao.DispatchSettlementMapper;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.pms.springmvc.vo.SettlementVO;

import cn.hutool.core.map.MapUtil;

/**
 *
 * Created by CodeGenerator
 */
@Service("dispatchSettlementService")
public class DispatchSettlementService extends AbstractBaseService<DispatchSettlementMapper, DispatchSettlement> implements IDispatchSettlementService {

    @Lazy
    @Autowired
    private IDispatchProjectService dispatchProjectService;
    
    @Autowired
    private INotifyTemplateService notifyTemplateService;
    
    @Autowired
    private ICompanyService companyService;
    
    @Autowired
    private IPurchaseReceiptService purchaseReceiptService;
    
    @Autowired
    private IPurchaseReceiptLineService purchaseReceiptLineService;
    
    @Autowired
    private IFileInfoService fileInfoService;
    
    
	@Override
    public void insertOrUpdateSelective(SettlementVO settlement) {
	    if (settlement.getId() != null) {
            this.updateByPrimaryKeySelective(settlement);
        } else {
            this.insertSelective(settlement);
        }
    //      dao.insertOrUpdateSelective(settlement);
    }

    @Override
    @Transactional
    public void settlementSubmit(Integer id, SettlementVO settlement) {
        DispatchSettlement dispatchSettlement = this.selectByPrimaryKey(id);
        if (dispatchSettlement == null || Boolean.TRUE.equals(dispatchSettlement.getDisabled())) {
            throw new CustomRuntimeException("未找到对应的转包结算记录！");
        }
        if (Boolean.TRUE.equals(dispatchSettlement.getSettled())) {
            throw new CustomRuntimeException("请勿重复发起结算！");
        }
        DispatchVO dispatch = new DispatchVO();
        dispatch.setId(dispatchSettlement.getDispatchId());
        dispatch.setDisabled(false);
        List<DispatchVO> dispatchVOs = dispatchProjectService.selectDispatchVOWithAmountBySelective(dispatch);
        if (dispatchVOs == null  || dispatchVOs.isEmpty()) {
            throw new CustomRuntimeException("未找到对应的项目转包记录！");
        }
        dispatch = dispatchVOs.get(0);
        
        BigDecimal settledRatio = BigDecimal.valueOf(dispatch.getSettledRatio());
        if (settledRatio.compareTo(BigDecimal.valueOf(100.00D)) > 0) {
            throw new CustomRuntimeException("已申请付款比例超过100");
        }
        
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int quarter = (int) Math.ceil(month / 3d);
        DispatchSettlement temp = new DispatchSettlement();
        temp.setId(id);
        temp.setYear(year);
        temp.setQuarter(quarter);
        temp.setMonth(month);
//        temp.setSettled(true);
        temp.setConfirmTime(new Date());
//        temp.setSettleSeq(generateSettleSeq(settlement));
        // 派单执行中
        temp.setState(50);
        this.updateByPrimaryKeySelective(temp);
        
        dispatchSettlement = this.selectByPrimaryKey(id);
        
        // 推D365的采购收货
        this.pushPurchaseReceipt(dispatchSettlement);
        BeanUtils.copyProperties(dispatchSettlement, settlement);
    }
    
    /**
     * 生成结算编号
     * 
     * @param settlement
     * @return settleSeq
     */
    public String generateSettleSeq(DispatchSettlement settlement) {
        if (settlement == null || StringUtils.isBlank(settlement.getDispatchSeq())) {
            return null;
        }
        
        // 查询派单的结算次数
        DispatchSettlement temp = new DispatchSettlement();
        temp.setDispatchId(settlement.getDispatchId());
//        temp.setDisabled(false);
        long count = this.countBySelective(temp);
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
        String dispatchSeq = settlement.getDispatchSeq();
        String ratio = settlement.getRatio();
        String amount = settlement.getAmount();
        String smsProjectName = (String) settlement.getCustomInfoByKey("smsProjectName", "");
        Object[] seqs = new Object[] { dispatchSeq, String.format(seqFormat, count), smsProjectName, ratio, amount };
        String settleSeq = StringUtils.join(seqs, "-");
        return settleSeq;
    }
    
    /**
     * 推D365的采购订单
     * @param settlement
     */
    public void pushPurchaseReceipt(DispatchSettlement settlement) {
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
        
        // 查询项目转包的采购订单号和批次号
        DispatchProject dispatch = dispatchProjectService.selectByPrimaryKey(settlement.getDispatchId());
        String purchId = (String) dispatch.getCustomInfoByKey("purchId");
        String inventTransId = (String) dispatch.getCustomInfoByKey("inventTransId");
        settlement.setCustomInfoByKey("purchId", purchId);
        settlement.setCustomInfoByKey("inventTransId", inventTransId);
        settlement.setCustomInfoByKey("deliveryDate", DateUtil.getTodayDateTime());
        settlement.setCustomInfoByKey("documentDate", DateUtil.getTodayDateTime());
        config.put("dispatch", dispatch);
        
        // 设置账套
        Object orgId = settlement.getOrgId();
        orgId = settlement.getCustomInfoByKey("orgId", dispatch.getCustomInfoByKey("orgId", orgId));
        orgId = ObjectUtils.defaultIfNull(orgId, UserContext.getOrgId());
        Company company = companyService.selectByPrimaryKey(Integer.parseInt(String.valueOf(orgId)));
        String dataAreaId = company.getCompAccount();
        config.put("dataAreaId", dataAreaId);
        
        // 创建采购订单头
        PurchaseReceiptHeader receipt = this.createPurchashReceipt(settlement, config);
        List<PurchaseReceiptLine> receiptLines = this.createPurchaseReceiptLines(settlement, config);
        
        // 调用D365采购收货接口
        settlement = D365Api.pushPurchaseReceipt(settlement, dataAreaId, receipt, receiptLines, config);
        this.updateByPrimaryKeySelective(settlement);
    }
    
    /**
     * 基于项目转包结算创建采购收货头
     * @param settlement
     * @param config
     * @return
     */
    public PurchaseReceiptHeader createPurchashReceipt(DispatchSettlement settlement, Map<String, Object> config) {
        // 获取结算编号
        String settleSeq = settlement.getSettleSeq();
        // 此次付款说明
        String memo = settlement.getMemo();
        // 实施进度
        String progressDesc = settlement.getProgressDesc();
//        // 验收进度
//        String acceptanceDesc = settlement.getAcceptanceDesc();
        
        String dataAreaId = (String) config.get("dataAreaId");
        String purchId = (String) settlement.getCustomInfoByKey("purchId");
        PurchaseReceipt t = new PurchaseReceipt();
        t.setPurchId(purchId);
        t.setDataAreaId(dataAreaId);
        long count = purchaseReceiptService.countBySelective(t) + 1;
        String packingSlipId = purchId + "_" + String.format("%02d", count);
        
        // 处理备注信息
        String remark = SystemLogUtil.format((String) config.getOrDefault("remarkFormat", memo), settlement);
        
        // 创建采购收货头
        PurchaseReceiptHeader receipt = new PurchaseReceiptHeader();
        receipt.setSourceOrderType(DataType.PROJECT_DISPATCH);
        receipt.setSourceOrderId(settlement.getDispatchId());
        receipt.setSourceReceiptType(DataType.DISPATCH_SETTLEMENT);
        receipt.setSourceReceiptId(settlement.getId());
        receipt.setPurchId(purchId);
        receipt.packingSlipId(packingSlipId) // 采购收货单号（物料收货）
                .packingSlipRemark(remark)// 采购收货备注（物料收货描述）
                .projectProgress(progressDesc) // 项目进度
                .deliveryDate((String) settlement.getCustomInfoByKey("deliveryDate")) // 交货日期
                .documentDate((String) settlement.getCustomInfoByKey("documentDate")) // 下单日期
                .dataAreaId(dataAreaId) // 账套
        ;
        return receipt;
    }
    
    /**
     * 基于项目转包结算创建采购收货行
     * @param settlement
     * @param config
     * @return
     */
    public List<PurchaseReceiptLine> createPurchaseReceiptLines(DispatchSettlement settlement, Map<String, Object> config) {
        // 获取指定的站点
        String inventSiteId = (String) config.getOrDefault("inventSiteId", "");
        // 获取指定的仓库
        String inventLocationId = (String) config.getOrDefault("inventLocationId", "");
        // 获取指定的库位
        String wmsLocationId = (String) config.getOrDefault("wmsLocationId", "");
        
        DispatchProject dispatch = (DispatchProject) config.getOrDefault("dispatch", new DispatchProject());
        
        // 获取采购订单的基准单位
        String purchUnitBase = (String) dispatch.getCustomInfoByKey("purchUnitBase", config.getOrDefault("purchUnitBase", "price"));
        // 获取采购订单的基准单价，默认为1
        BigDecimal purchPriceBase = new BigDecimal(String.valueOf(dispatch.getCustomInfoByKey("purchPriceBase", config.getOrDefault("purchPriceBase", "1.00")))).setScale(2, RoundingMode.HALF_UP);
        // 获取采购订单的基准数量，默认为1,
        BigDecimal purchQtyBase = new BigDecimal(String.valueOf(dispatch.getCustomInfoByKey("purchQtyBase", config.getOrDefault("purchQtyBase", "1.00")))).setScale(2, RoundingMode.HALF_UP);

        String amountStr = StringUtils.defaultIfBlank(StringUtils.trimToEmpty(String.valueOf(settlement.getCustomInfoByKey("approvedAmount", settlement.getAmount()))), "0");
        BigDecimal amount = new BigDecimal(amountStr.replaceAll(",", ""));
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        // 默认指定基准采购价，数量由转包价和基准采购价确定
        BigDecimal qty = purchQtyBase;
        if ("price".equalsIgnoreCase(purchUnitBase)) {
            // 根据转包价和基准采购价计算采购订单的采购数量
            qty = amount.divide(purchPriceBase, 2, RoundingMode.HALF_UP);
        } else {
            // 根据转包价和基准数量计算采购订单的采购单价
            qty = purchQtyBase.multiply(new BigDecimal(settlement.getRatio()).setScale(2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100d))).setScale(2, RoundingMode.HALF_UP);
        }
        
        // 根据转包价和基准数量计算采购订单的采购单价
        BigDecimal price = amount.divide(qty, 2, RoundingMode.HALF_UP);
        

        // 创建采购订单行
        List<PurchaseReceiptLine> receiptLines = new ArrayList<PurchaseReceiptLine>();
        PurchaseReceiptLine receiptLine = new PurchaseReceiptLine();
        receiptLine.setDataAreaId((String) config.get("dataAreaId")); // 采购价
        receiptLine.setAmount(amount); // 采购价
        receiptLine.setPrice(price); // 采购单价
        receiptLine.purchId((String) settlement.getCustomInfoByKey("purchId")) // 采购单号
//                .lineNum(settlement.getDispatchId().toString()) // 行号（用系统ID代替）
                .inventTransId((String) settlement.getCustomInfoByKey("inventTransId")) // 批次号
                .qty(qty) // 采购数量
                .inventSiteId(inventSiteId) // 站点
                .inventLocationId(inventLocationId) // 仓库
                .wmsLocationId(wmsLocationId) // 库位
        ;
        receiptLines.add(receiptLine);
        return receiptLines;
    }

    @Override
	public long countSettlementWidthDispatchPageable(PageParam<Object> pageParam) {
		return dao.countSettlementWidthDispatchPageable(pageParam);
	}

	@Override
	public List<Object> selectSettlementWidthDispatchPageable(PageParam<Object> pageParam) {
		return dao.selectSettlementWidthDispatchPageable(pageParam);
	}

	@Override
	public List<SettlementVO> querySSEDispatchSettlementPaymentList() {
		return dao.querySSEDispatchSettlementPaymentList();
	}

	@Override
	public void saveSettlementPayment(List<SettlementVO> settlementPaymentList) {
		saveSettlementPayment(settlementPaymentList, null);
	}

	@Override
	@Transactional
	public void saveSettlementPayment(List<SettlementVO> settlementPaymentList, Integer[] delIds) {
		// 删除原来的付款信息
		if (delIds != null && delIds.length > 0) {
			for (Integer id : delIds) {
				DispatchSettlement temp = new DispatchSettlement();
				temp.setId(id);
				temp.setDisabled(true);
				this.updateByPrimaryKeySelective(temp);
			}
		}
		if (settlementPaymentList != null && !settlementPaymentList.isEmpty()) {
			for (SettlementVO settlementVO : settlementPaymentList) {
				if (settlementVO.getId() != null) {
					this.updateByPrimaryKeySelective(settlementVO);
				} else {
					this.insertSelective(settlementVO);
				}
			}
		}
	}
	
	/**
     * 付款附件查验
     * @param dispatchId
     * @param settlementId
     * @param invoiceFileList
     * @param deliverInfoList
     * @return
     */
    public Result verifySettlementInvoice(DispatchSettlement settlement) {
        if (settlement == null || settlement.getId() == null || settlement.getId() == 0) {
            return Result.fail("未找到对应的转包结算记录！");
        }
        
        Integer invoiceType = InvoiceUtil.getFileInvoiceType(9);
//        Integer inspectionType = InvoiceUtil.getFileInspectionType();
//        
//        Integer dispatchId = settlement.getDispatchId();
//        Integer settlementId = settlement.getId();
        
        List<String> invoiceFileIds = Arrays.asList(StringUtils.split(MapUtils.getString(settlement.getCustomInfo(), "invoiceFileIds", ""), ","));
//        List<String> fileIds = Arrays.asList(StringUtils.split(MapUtils.getString(settlement.getCustomInfo(), "fileIds", ""), ","));
        
        List<FileInfo> invoiceFileInfos = Collections.emptyList();
        if (!invoiceFileIds.isEmpty()) {
            invoiceFileInfos = fileInfoService.selectFileInfoByIdsAndType(invoiceFileIds, invoiceType);
        }
        String webroot = FileUtil.getWebRoot() + File.separator;
        
        List<File> invoiceFileList = new ArrayList<>(invoiceFileInfos.size());
        List<Object> deliverInfoList = new ArrayList<>(invoiceFileInfos.size());
        Map<String, List<Integer>> uniqueFilePathDeliverMap = new HashMap<>(invoiceFileInfos.size());
        // 按path分组，相同文件只查询一次，查询后更新相同内容
        for (FileInfo fileInfo : invoiceFileInfos) {
            // 文件是否重复，发票状态是否已验真
            Boolean isVerified = InvoiceUtil.checkFileInvoiceStatus(fileInfo.getCustomInfo());
            List<Integer> deliverIds = uniqueFilePathDeliverMap.getOrDefault(fileInfo.getPath(), new ArrayList<Integer>());
            if (uniqueFilePathDeliverMap.containsKey(fileInfo.getPath()) || isVerified) {
                if (!isVerified) {
                    deliverIds.add(fileInfo.getId());
                }
                continue;
            }
            deliverIds.add(fileInfo.getId());
            uniqueFilePathDeliverMap.put(fileInfo.getPath(), deliverIds);
        }
        // 相同文件只查询一次，查询后更新相同内容
        for (Entry<String, List<Integer>> entry : uniqueFilePathDeliverMap.entrySet()) {
            String filePath = entry.getKey();
            List<Integer> deliverIds = entry.getValue();
            invoiceFileList.add(new File(webroot + filePath));
            deliverInfoList.add(ElectronicInvoiceModel.builder().dataType("fileInfo").dataId(StringUtils.join(deliverIds, ",")).build());
        }
        
        return verifySettlementInvoice(settlement, invoiceFileList, deliverInfoList);
    }
    
    
    /**
     * 付款附件查验
     * @param dispatchId
     * @param settlementId
     * @param invoiceFileList
     * @param deliverInfoList
     * @return
     */
    public Result verifySettlementInvoice(DispatchSettlement settlement, List<File> invoiceFileList, List<Object> deliverInfoList) {
        if (settlement == null || settlement.getId() == null || settlement.getId() == 0) {
            return Result.fail("未找到对应的转包结算记录！");
        }
        Integer invoiceType = InvoiceUtil.getFileInvoiceType(9);
        Integer inspectionType = InvoiceUtil.getFileInspectionType(5);
        
        // 按path分组，相同文件只查询一次，查询后更新相同内容
        Map<String, ElectronicInvoiceModel> fileInfoMap = new LinkedHashMap<String, ElectronicInvoiceModel>(invoiceFileList.size());
        for (int i = 0; i < invoiceFileList.size(); i++) {
            File file = invoiceFileList.get(i);
            if (file == null || !file.isFile() || !file.exists()) {
                continue;
            }
            String filePath = file.getAbsolutePath();
            ElectronicInvoiceModel sourceMap = (deliverInfoList.isEmpty() ? ElectronicInvoiceModel.builder().build() : (ElectronicInvoiceModel) deliverInfoList.get(Math.min(i, deliverInfoList.size() - 1)));
            ElectronicInvoiceModel deliverInfo = fileInfoMap.getOrDefault(filePath, sourceMap);
            if (fileInfoMap.containsKey(filePath)) {
                Set<String> dataIds = new LinkedHashSet<String>();
                dataIds.addAll(Arrays.asList(StringUtils.split(deliverInfo.getDataId(), ",")));
                dataIds.addAll(Arrays.asList(StringUtils.split(sourceMap.getDataId(), ",")));
                deliverInfo.setDataId(StringUtils.join(dataIds, ","));
            }
            
            fileInfoMap.put(filePath, deliverInfo);
        }
        
        List<File> uniqueFileList = new ArrayList<>();
        List<Object> uniqueInfoList = new ArrayList<>();
        for (Entry<String, ElectronicInvoiceModel> entry : fileInfoMap.entrySet()) {
            uniqueFileList.add(new File(entry.getKey()));
            uniqueInfoList.add(entry.getValue());
        }
        
        // 发票识别
        Integer settlementId = settlement.getId() != null ? settlement.getId() : 0;
        Set<String> invoiceFileIds = new LinkedHashSet<>(Arrays.asList(StringUtils.split(MapUtils.getString(settlement.getCustomInfo(), "invoiceFileIds", ""), ",")));
        Set<String> fileIds = new LinkedHashSet<>(Arrays.asList(StringUtils.split(MapUtils.getString(settlement.getCustomInfo(), "fileIds", ""), ",")));
        
        Map<String, Object> config = JSON.parseObject(SystemConfig.systemVariables.getOrDefault("sys.fp.api.config", "{}"), HashMap.class);
        List<Response<ElectronicInvoiceModel>> responses = FPApi.postElectronicInvoice("settlement", settlementId.toString(), uniqueFileList, uniqueInfoList, config);
        Integer successCount = 0;
        for (Iterator iterator = responses.iterator(); iterator.hasNext();) {
            ElectronicInvoiceResponse response = (ElectronicInvoiceResponse) iterator.next();
            List<InvoiceProviderInfo> dataList = response.getData() != null ? response.getData() : Collections.emptyList();
            for (InvoiceProviderInfo data : dataList) {
                Map<String, Object> invoice = data.getInfo();
                String dataIds = data.getQuery().getDataId();
                // 相同文件只查询一次，查询后更新相同内容
                List<String> dataIdList = Arrays.asList(StringUtils.split(dataIds, ","));
                for (String dataId : dataIdList) {
                    if (NumberUtils.isCreatable(dataId)) {
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setId(Integer.parseInt(dataId));
                        fileInfo.setDataType("settlement");
                        fileInfo.setDataId(settlementId);
                        fileInfo.setCustomInfo(invoice);
                        fileInfo.setCustomInfoByKey("identify", true);
                        fileInfo.setCustomInfoByKey("uniqueInvoiceNumber", InvoiceUtil.getUniqueInvoiceNumber(invoice));
                        
                        // 如果不是发票识别不需要验真，说明不是发票，将类型改为验收材料
                        if (!InvoiceUtil.checkFileInvoiceType(invoice)) {
                            invoiceFileIds.remove(dataId);
                            fileIds.add(dataId);
                            fileInfo.setTypeId(inspectionType);
                        }
                        fileInfoService.updateByPrimaryKeySelective(fileInfo);
                        successCount++;
                    }
                }
            }
        }
        
        // 更新发票信息
        settlement.setCustomInfoByKey("fileIds", StringUtils.join(fileIds, ","));
        settlement.setCustomInfoByKey("invoiceFileIds", StringUtils.join(invoiceFileIds, ","));
        this.updateSubcontractPaymentInvoiceNumber(settlement);
//        fillSettlementInvoiceNumber(settlement, fileIds, invoiceFileIds);
        return Result.success(successCount).message(String.format("发票待查验%s张，已查验%s张！", uniqueFileList.size(), successCount));
    }
    
    /**
     * 更新付款申请对应的发票编号
     * @param settlementIds
     */
    public void fillSettlementInvoiceNumber(DispatchSettlement settlement, Collection<String> fileIds, Collection<String> invoiceFileIds) {
        if (settlement == null) {
            return;
        }
        settlement.setCustomInfoByKey("fileIds", StringUtils.join(fileIds, ","));
        settlement.setCustomInfoByKey("invoiceFileIds", StringUtils.join(invoiceFileIds, ","));
        
        Integer invoiceType = InvoiceUtil.getFileInvoiceType(9);

        List<FileInfo> invoiceFileInfos = Collections.emptyList();
        if (!invoiceFileIds.isEmpty()) {
            invoiceFileInfos = fileInfoService.selectFileInfoByIdsAndType(invoiceFileIds, invoiceType);
        }
        Set<String> invoiceNumberList = new LinkedHashSet<String>();
        BigDecimal invoiceAmountSum = new BigDecimal("0.00");
        AtomicBoolean invoiceVerified = new AtomicBoolean(true);
        for (FileInfo fileInfo : invoiceFileInfos) {
            Map<String, Object> invoiceInfo = fileInfo.getCustomInfo();
            String uniqueInvoiceNumber = InvoiceUtil.getUniqueInvoiceNumber(invoiceInfo);
            BigDecimal invoiceAmount = MapUtil.get(invoiceInfo, "total_amount", BigDecimal.class, BigDecimal.ZERO);
            if (StringUtils.isNotBlank(uniqueInvoiceNumber) && !invoiceNumberList.contains(uniqueInvoiceNumber)) {
                invoiceNumberList.add(uniqueInvoiceNumber);
                invoiceAmountSum = invoiceAmountSum.add(invoiceAmount);
            }
            invoiceVerified.compareAndSet(true, InvoiceUtil.checkFileInvoiceStatus(invoiceInfo));
        }
        
        settlement.setCustomInfoByKey("invoiceNumber", StringUtils.join(invoiceNumberList, ","));
        settlement.setCustomInfoByKey("invoiceAmount", invoiceAmountSum);
        settlement.setCustomInfoByKey("invoiceVerified", invoiceVerified.get());
    }
    
    @Override
    public List<FileInfo> selectDispatchSettlementInvoiceDetails(DispatchSettlement settlement) {
        if (settlement == null) {
            return Collections.emptyList();
        }
        
        List<String> invoiceFileIds = Arrays.asList(StringUtils.split(MapUtils.getString(settlement.getCustomInfo(), "invoiceFileIds", "0"), ","));

        Integer invoiceType = InvoiceUtil.getFileInvoiceType(9);
        List<FileInfo> invoiceFileInfos = Collections.emptyList();
        if (!invoiceFileIds.isEmpty()) {
            invoiceFileInfos = fileInfoService.selectFileInfoByIdsAndType(invoiceFileIds, invoiceType);
        }
        BigDecimal d100 = new BigDecimal("100.00");
        BigDecimal invoiceAmountSum = new BigDecimal("0.00");
        AtomicBoolean invoiceVerified = new AtomicBoolean(true);
        AtomicInteger atomicSumAmount = new AtomicInteger(0);
        Map<String, FileInfo> uniqueInvoiceMap = invoiceFileInfos.parallelStream().filter(d-> {
            // 过滤识别后的发票附件
            boolean isInvoice = invoiceType.equals(d.getTypeId());
            Map<String, Object> invoiceInfo = d.getCustomInfo();
            Boolean identify = MapUtil.getBool(invoiceInfo, "identify", false);
            if (isInvoice) {
                // 是否所有的发票都已经完成发票识别
                invoiceVerified.compareAndSet(true, DispatchSettlementUpdateAspect.checkFileInvoiceStatus(invoiceInfo));
            }
            return isInvoice && identify;
        }).collect(Collectors.toMap(
            // 根据发票号去重
            d -> InvoiceUtil.getUniqueInvoiceNumber(d.getCustomInfo()),  // Key: 发票号
            d -> d,                    // Value: 对象本身
            (existing, replacement) -> replacement,  // 当重复时，保留最新的（去重）
            LinkedHashMap::new      
        ));
        
        invoiceFileInfos = new ArrayList<>(uniqueInvoiceMap.values());
        invoiceFileInfos.parallelStream().map(fileInfo -> {
            Map<String, Object> invoiceInfo = fileInfo.getCustomInfo();
            BigDecimal invoiceAmount = MapUtil.get(invoiceInfo, "total_amount", BigDecimal.class, BigDecimal.ZERO);
            
            // 计算去重后的发票总金额
            atomicSumAmount.addAndGet(invoiceAmount.multiply(d100).intValue());
            return fileInfo;
        })
        .collect(Collectors.toList());
        
        invoiceAmountSum = BigDecimal.valueOf(atomicSumAmount.get()).divide(d100);
        settlement.setCustomInfoByKey("identifyInvoiceCount", uniqueInvoiceMap.size());
        settlement.setCustomInfoByKey("invoiceNumber", StringUtils.join(uniqueInvoiceMap.keySet(), ","));
        settlement.setCustomInfoByKey("invoiceAmount", invoiceAmountSum);
        settlement.setCustomInfoByKey("invoiceVerified", invoiceVerified.get());
        
        return invoiceFileInfos;
    }
    
    /**
     * 更新付款申请对应的发票编号
     * @param settlementIds
     */
    public Result updateSubcontractPaymentInvoiceNumber(Integer settlementId, Integer dispatchId, List<String> fileIds, List<String> invoiceFileIds) {
        DispatchSettlement settlement = new DispatchSettlement();
        settlement.setId(settlementId);
        settlement.setDispatchId(dispatchId);
        settlement.setCustomInfoByKey("fileIds", StringUtils.join(fileIds, ","));
        settlement.setCustomInfoByKey("invoiceFileIds", StringUtils.join(invoiceFileIds, ","));
        return updateSubcontractPaymentInvoiceNumber(settlement);
    }
    
    /**
     * 更新付款申请对应的发票编号
     * @param settlementIds
     */
    public Result updateSubcontractPaymentInvoiceNumber(DispatchSettlement settlement) {
        if (settlement == null || settlement.getId() == null || settlement.getId() == 0) {
            return Result.fail("未找到对应的转包结算记录！");
        }
        List<String> fileIds = Arrays.asList(StringUtils.split(MapUtils.getString(settlement.getCustomInfo(), "fileIds", ""), ","));
        List<String> invoiceFileIds = Arrays.asList(StringUtils.split(MapUtils.getString(settlement.getCustomInfo(), "invoiceFileIds", ""), ","));
        
        fillSettlementInvoiceNumber(settlement, fileIds, invoiceFileIds);
        
        this.updateByPrimaryKeySelective(settlement);
        return Result.success();
    }
	
}
