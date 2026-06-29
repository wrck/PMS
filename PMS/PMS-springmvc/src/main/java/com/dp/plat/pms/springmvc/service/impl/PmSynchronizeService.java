package com.dp.plat.pms.springmvc.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.SynchronizeService;
import com.dp.plat.pms.springmvc.dao.PmSynchronizeMapper;
import com.dp.plat.pms.springmvc.entity.Facilitator;
import com.dp.plat.pms.springmvc.entity.OfstContractHead;
import com.dp.plat.pms.springmvc.entity.OfstContractHeadSAP;
import com.dp.plat.pms.springmvc.service.IPmSynchronizeService;
import com.dp.plat.pms.springmvc.vo.AfPrjProperty;
import com.dp.plat.pms.springmvc.vo.ProjectProduct;
import com.dp.plat.pms.springmvc.vo.PurchaseReceiptSettlement;

@Service("pmSynchronizeService")
public class PmSynchronizeService extends SynchronizeService implements IPmSynchronizeService {

	@Resource
	private PmSynchronizeMapper perfSynchronizeMapper;

	@Override
	public List<AfPrjProperty> selectAllAfPrjProperty() {
		return perfSynchronizeMapper.selectAllAfPrjProperty();
	}

	@Override
	public void clearAllAfPrjProperty() {
		perfSynchronizeMapper.clearAllAfPrjProperty();
	}

	@Override
	public void insertAfPrjProperty(List<AfPrjProperty> list) {
		perfSynchronizeMapper.insertAfPrjProperty(list);
	}

	@Override
	public List<ProjectProduct> selectAllProjectProduct() {
		return perfSynchronizeMapper.selectAllProjectProduct();
	}

	@Override
	public void clearAllProjectProduct() {
		perfSynchronizeMapper.clearAllProjectProduct();
	}

	@Override
	public void insertProjectProduct(List<ProjectProduct> list) {
		perfSynchronizeMapper.insertProjectProduct(list);
	}
	
	@Override
    public void splitAfProjectByProductCode(Map<String, Object> params) {
        perfSynchronizeMapper.splitAfProjectByProductCode(params);
    }

	@Override
	public void splitAfProjectByProductCode(String productfirstCodes) {
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put("productfirstCodes", productfirstCodes);
	    splitAfProjectByProductCode(params);
	}
	
	@Override
	public int insertOfstContractHeadSAP(List<OfstContractHeadSAP> record) {
		return perfSynchronizeMapper.insertOfstContractHeadSAP(record);
	}

	@Override
	public List<OfstContractHead> selectAllOfstContractHeadSAP() {
		return perfSynchronizeMapper.selectAllOfstContractHeadSAP();
	}

	@Override
	public void clearAllOfstContractHeadSAP() {
		perfSynchronizeMapper.clearAllOfstContractHeadSAP();
	}

    @Override
    public List<Facilitator> selectAllFacilitator() {
        return perfSynchronizeMapper.selectAllFacilitator();
    }

    @Override
    public void clearAllFacilitator() {
        perfSynchronizeMapper.clearAllFacilitator();
    }

    @Override
    public void insertFacilitator(List<Facilitator> list) {
        perfSynchronizeMapper.insertFacilitator(list);
    }

    @Override
    public void insertOrUpdateFacilitatorFromD365() {
        perfSynchronizeMapper.insertOrUpdateFacilitatorFromD365();
    }

    @Override
    public List<PurchaseReceiptSettlement> selectAllPurchaseReceiptSettlement(Map<String, Object> params) {
        return perfSynchronizeMapper.selectAllPurchaseReceiptSettlement(params);
    }

    @Override
    public void clearAllPurchaseReceiptSettlement(Map<String, Object> params) {
        perfSynchronizeMapper.clearAllPurchaseReceiptSettlement(params);
    }

    @Override
    public void insertPurchaseReceiptSettlement(List<PurchaseReceiptSettlement> list) {
        perfSynchronizeMapper.insertPurchaseReceiptSettlement(list);
    }

    @Override
    public void updateDispatchAndSubcontractPaymentFromD365(Map<String, Object> params) {
        perfSynchronizeMapper.updateDispatchAndSubcontractPaymentFromD365(params);
    }
    
}
