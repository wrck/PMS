package com.dp.plat.subcontract.vo;

import java.util.List;

import com.dp.plat.subcontract.entity.SubcontractDeliver;
import com.dp.plat.subcontract.entity.SubcontractPayment;

public class SubcontractPaymentVO extends SubcontractPayment {

    private List<SubcontractDeliver> delivers;
    
    private List<SubcontractDeliver> invoiceDelivers;

    public List<SubcontractDeliver> getDelivers() {
        return delivers;
    }

    public void setDelivers(List<SubcontractDeliver> delivers) {
        this.delivers = delivers;
    }

    public List<SubcontractDeliver> getInvoiceDelivers() {
        return invoiceDelivers;
    }

    public void setInvoiceDelivers(List<SubcontractDeliver> invoiceDelivers) {
        this.invoiceDelivers = invoiceDelivers;
    }
    
}
