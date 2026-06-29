package com.dp.plat.pms.springmvc.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.core.service.ISynchronizeService;
import com.dp.plat.pms.springmvc.entity.Facilitator;
import com.dp.plat.pms.springmvc.entity.OfstContractHead;
import com.dp.plat.pms.springmvc.entity.OfstContractHeadSAP;
import com.dp.plat.pms.springmvc.vo.AfPrjProperty;
import com.dp.plat.pms.springmvc.vo.ProjectProduct;
import com.dp.plat.pms.springmvc.vo.PurchaseReceiptSettlement;

public interface IPmSynchronizeService extends ISynchronizeService {

	List<AfPrjProperty> selectAllAfPrjProperty();

	void clearAllAfPrjProperty();

	void insertAfPrjProperty(List<AfPrjProperty> list);

	List<ProjectProduct> selectAllProjectProduct();

	void clearAllProjectProduct();

	void insertProjectProduct(List<ProjectProduct> list);

	/**
	 * 拆分工程实施项目以及安服项目
	 * 
	 * @param productfirstCodes
	 */
	void splitAfProjectByProductCode(String productfirstCodes);
	/**
     * 拆分工程实施项目以及安服项目
     * 
     * @param params
     */
	void splitAfProjectByProductCode(Map<String, Object> params);

	int insertOfstContractHeadSAP(List<OfstContractHeadSAP> record);
	List<OfstContractHead> selectAllOfstContractHeadSAP();
	void clearAllOfstContractHeadSAP();
	
    List<Facilitator> selectAllFacilitator();
    void clearAllFacilitator();
    void insertFacilitator(List<Facilitator> list);
    void insertOrUpdateFacilitatorFromD365();

    /**
     * 同步D365的采购订单物料收货结算数据
     * @return
     */
    List<PurchaseReceiptSettlement> selectAllPurchaseReceiptSettlement(Map<String, Object> params);
    void clearAllPurchaseReceiptSettlement(Map<String, Object> params);
    void insertPurchaseReceiptSettlement(List<PurchaseReceiptSettlement> list);
    void updateDispatchAndSubcontractPaymentFromD365(Map<String, Object> params);
}
