package com.dp.plat.pms.springmvc.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import com.dp.plat.pms.extend.d365.entity.PurchaseReceipt;
import com.dp.plat.pms.extend.d365.model.PurchaseReceiptHeader;
import com.dp.plat.pms.extend.d365.model.PurchaseReceiptLine;
import com.dp.plat.pms.extend.d365.service.IPurchaseReceiptLineService;
import com.dp.plat.pms.extend.d365.service.IPurchaseReceiptService;
import com.dp.plat.pms.extend.d365.util.D365Api;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.ProcessType.DataType;
import com.dp.plat.pms.springmvc.dao.DispatchSettlementMapper;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.entity.DispatchSettlement;
import com.dp.plat.pms.springmvc.service.IDispatchProjectService;
import com.dp.plat.pms.springmvc.service.IDispatchSettlementService;
import com.dp.plat.pms.springmvc.vo.DispatchVO;
import com.dp.plat.pms.springmvc.vo.SettlementVO;

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
	
}
