package com.dp.plat.pms.extend.fp.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.dp.plat.pms.extend.fp.model.ElectronicInvoiceModel;

public class InvoiceProviderInfo extends BaseEntity {

    private static final long serialVersionUID = 2896107435704390987L;

    // 关联tb_invoice表主键
    private Integer invoiceId;

    // 发票来源
    private String provider;

    // 用户唯一标识
    private String openId;

    // 电子发票Hash值
    private String electricHash;

    // 电子签名状态
    private String eSignature;

    // 电子发票修改状态
    private String eDocModified;

    // 电子签名时间
    @JsonSerialize(using = JsonSerializer.class)
    private Date eSignDate;

    // 电子发票大小
    private Integer fileSize;

    // 电子发票文件类型
    private String fileExt;

    // 电子发票本地下载保存地址
    private String downloadPath;

    // 电子发票本地上传保存地址
    private String uploadPath;

    // 电子发票来源地址
    private String sourceUrl;

    // 发票状态
    private String status;

    private String createBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date createTime;

    private String updateBy;

    @JsonSerialize(using = JsonSerializer.class)
    private Date updateTime;

    // 电子签名信息
    private String signatureInfo;

    // 发票来源查询参数
    @JSONField(name = "query", serialize = true, deserialize = false)
    private ElectronicInvoiceModel query;

    // 发票来源发票信息
    @JSONField(name = "info", serialize = true, deserialize = false)
    private Map<String, Object> info;
    
    public InvoiceProviderInfo() {
		super();
	}
    
	public InvoiceProviderInfo(Integer invoiceId, String provider, String openId) {
		super();
		this.invoiceId = invoiceId;
		this.provider = provider;
		this.openId = openId;
	}
	
	public InvoiceProviderInfo(Integer invoiceId, String provider, String openId, String status) {
		super();
		this.invoiceId = invoiceId;
		this.provider = provider;
		this.openId = openId;
		this.status = status;
	}

	public InvoiceProviderInfo(Integer invoiceId, String provider, String openId, String status, ElectronicInvoiceModel query, Map<String, Object> info) {
		super();
		this.invoiceId = invoiceId;
		this.provider = provider;
		this.openId = openId;
		this.status = status;
		this.query = query;
		this.info = info;
	}
	
	public InvoiceProviderInfo(String provider, String openId, String status, ElectronicInvoiceModel query, Map<String, Object> info) {
		super();
		this.provider = provider;
		this.openId = openId;
		this.status = status;
		this.query = query;
		this.info = info;
	}

    /**
     * 获取关联tb_invoice表主键
     *
     * @return invoiceId - 关联tb_invoice表主键
     */
    public Integer getInvoiceId() {
        return invoiceId;
    }

    /**
     * 设置关联tb_invoice表主键
     *
     * @param invoiceId 关联tb_invoice表主键
     */
    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    /**
     * 获取发票来源
     *
     * @return provider - 发票来源
     */
    public String getProvider() {
        return provider;
    }

    /**
     * 设置发票来源
     *
     * @param provider 发票来源
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * 获取用户唯一标识
     *
     * @return openId - 用户唯一标识
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 设置用户唯一标识
     *
     * @param openId 用户唯一标识
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 获取电子发票Hash值
     *
     * @return electricHash - 电子发票Hash值
     */
    public String getElectricHash() {
        return electricHash;
    }

    /**
     * 设置电子发票Hash值
     *
     * @param electricHash 电子发票Hash值
     */
    public void setElectricHash(String electricHash) {
        this.electricHash = electricHash;
    }

    /**
     * 获取电子签名状态
     *
     * @return eSignature - 电子签名状态
     */
    public String geteSignature() {
        return eSignature;
    }

    /**
     * 设置电子签名状态
     *
     * @param eSignature 电子签名状态
     */
    public void seteSignature(String eSignature) {
        this.eSignature = eSignature;
    }

    /**
     * 获取电子发票修改状态
     *
     * @return eDocModified - 电子发票修改状态
     */
    public String geteDocModified() {
        return eDocModified;
    }

    /**
     * 设置电子发票修改状态
     *
     * @param eDocModified 电子发票修改状态
     */
    public void seteDocModified(String eDocModified) {
        this.eDocModified = eDocModified;
    }

    /**
     * 获取电子签名时间
     *
     * @return eSignDate - 电子签名时间
     */
    public Date geteSignDate() {
        return eSignDate;
    }

    /**
     * 设置电子签名时间
     *
     * @param eSignDate 电子签名时间
     */
    public void seteSignDate(Date eSignDate) {
        this.eSignDate = eSignDate;
    }

    /**
     * 获取电子发票大小
     *
     * @return fileSize - 电子发票大小
     */
    public Integer getFileSize() {
        return fileSize;
    }

    /**
     * 设置电子发票大小
     *
     * @param fileSize 电子发票大小
     */
    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 获取电子发票文件类型
     *
     * @return fileExt - 电子发票文件类型
     */
    public String getFileExt() {
        return fileExt;
    }

    /**
     * 设置电子发票文件类型
     *
     * @param fileExt 电子发票文件类型
     */
    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    /**
     * 获取电子发票本地下载保存地址
     *
     * @return downloadPath - 电子发票本地下载保存地址
     */
    public String getDownloadPath() {
        return downloadPath;
    }

    /**
     * 设置电子发票本地下载保存地址
     *
     * @param downloadPath 电子发票本地下载保存地址
     */
    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    /**
     * 获取电子发票本地上传保存地址
     *
     * @return uploadPath - 电子发票本地上传保存地址
     */
    public String getUploadPath() {
        return uploadPath;
    }

    /**
     * 设置电子发票本地上传保存地址
     *
     * @param uploadPath 电子发票本地上传保存地址
     */
    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    /**
     * 获取电子发票来源地址
     *
     * @return sourceUrl - 电子发票来源地址
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * 设置电子发票来源地址
     *
     * @param sourceUrl 电子发票来源地址
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    /**
     * 获取发票状态
     *
     * @return status - 发票状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置发票状态
     *
     * @param status 发票状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return createBy
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * @param createBy
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * @return createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return updateBy
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * @param updateBy
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * @return updateTime
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取电子签名信息
     *
     * @return signatureInfo - 电子签名信息
     */
    public String getSignatureInfo() {
        return signatureInfo;
    }

    /**
     * 设置电子签名信息
     *
     * @param signatureInfo 电子签名信息
     */
    public void setSignatureInfo(String signatureInfo) {
        this.signatureInfo = signatureInfo;
    }

    /**
     * 获取发票来源查询参数
     *
     * @return query - 发票来源查询参数
     */
    public ElectronicInvoiceModel getQuery() {
        return query;
    }

    /**
     * 设置发票来源查询参数
     *
     * @param query 发票来源查询参数
     */
    public void setQuery(ElectronicInvoiceModel query) {
        this.query = query;
    }
    
    /**
     * 设置发票来源查询参数
     *
     * @param query 发票来源查询参数
     */
    @JSONField(name = "query", deserialize = true)
    public void setQuery(String query) {
        if (query != null) {
            try {
                this.query = JSON.parseObject(query, ElectronicInvoiceModel.class);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 获取发票来源发票信息
     *
     * @return info - 发票来源发票信息
     */
    public Map<String, Object> getInfo() {
        return info;
    }

    /**
     * 设置发票来源发票信息
     *
     * @param info 发票来源发票信息
     */
    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }
    
    /**
     * 设置发票来源发票信息
     *
     * @param info 发票来源发票信息
     */
    @JSONField(name = "info", deserialize = true)
    public void setInfo(String info) {
        if (info != null) {
            try {
                this.info = JSON.parseObject(info, HashMap.class);
            } catch (Exception e) {
            }
        }
    }
}
