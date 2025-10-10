package com.dp.plat.prob.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.data.bean.CustomInfoEntity;
import com.dp.plat.prob.version.SoftVersionParser;

/**
 * 技术公告信息
 * 
 * @author j01441
 *
 */
public class Prob extends CustomInfoEntity {
	private static final long serialVersionUID = 4265353476481783579L;
    private int probId;// 主键
	private String probNum;//
	private String watch;// 跟踪
	private String watchName;// 名称
	private String theme;// 主题
	private String desc;// 技术公告描述
	private String solution;// 解决方案
	private String status;// 状态
	private String statusName;// 状态名称
	private Date startdate;// 开始日期
	private Date duedate;// 计划完成日期
	private String attachmentNames;// 文件名称
	private String attachments;// 文件
	private String priority;// 严重级别
	private String priorityName;// 级别名称
	private String affectedVersion;// 影响的版本
	private String productType;// 产品类型
	private String relatedSceneTypes;// 关联场景类型
	private Long relatedSceneTypesMark;// 关联场景类型的bitMark
	private String relatedSceneTypesName;
	private String trackingUser;// 跟踪用户
	private String trackingUsername;// 跟踪用户名称
	private String createBy;
	private Date createTime;
	private String updateBy;
	private Date updateTime;
	private String effectiveFrom;
	private String effectiveTo;
	private String remark;
	private int visibleRange;// 可见范围
	private String reader;
	private int readStatus;
	
	// 影响版本类型，1：盒式系列，2：框式系列
	private Integer affectedType;
	// 影响版本解析结果
	private List<SoftVersionParser> softVersionParserList;
	
	// 状态查询
	private List<? extends Object> statusList;
	
	public int getProbId() {
		return probId;
	}

	public void setProbId(int probId) {
		this.probId = probId;
	}

	public String getProbNum() {
		return probNum;
	}

	public void setProbNum(String probNum) {
		this.probNum = probNum;
	}

	public String getWatch() {
		return watch;
	}

	public void setWatch(String watch) {
		this.watch = watch;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getDuedate() {
		return duedate;
	}

	public void setDuedate(Date duedate) {
		this.duedate = duedate;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public String getAffectedVersion() {
		return affectedVersion;
	}

	public void setAffectedVersion(String affectedVersion) {
		this.affectedVersion = affectedVersion;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(String effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public String getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(String effectiveTo) {
		this.effectiveTo = effectiveTo;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getAttachmentNames() {
		return attachmentNames;
	}

	public void setAttachmentNames(String attachmentNames) {
		this.attachmentNames = attachmentNames;
	}

	public String getWatchName() {
		return watchName;
	}

	public void setWatchName(String watchName) {
		this.watchName = watchName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getPriorityName() {
		return priorityName;
	}

	public void setPriorityName(String priorityName) {
		this.priorityName = priorityName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	public String getRelatedSceneTypes() {
        return relatedSceneTypes;
    }

    public void setRelatedSceneTypes(String relatedSceneTypes) {
        this.relatedSceneTypes = relatedSceneTypes;
        if (StringUtils.isNotBlank(relatedSceneTypes)) {
            List<String> relatedSceneTypeList = Arrays.asList(StringUtils.split(StringUtils.stripToEmpty(relatedSceneTypes), ", "));
            if (!relatedSceneTypeList.isEmpty()) {
                this.setCustomInfoByKey("relatedSceneTypes", relatedSceneTypeList);
                long bitMark = 0;
                for (String sceneType : relatedSceneTypeList) {
                    bitMark |= Integer.parseInt(sceneType);
                }
                this.relatedSceneTypesMark = bitMark;
                this.relatedSceneTypes = StringUtils.join(relatedSceneTypeList, ",");
            }
        }
    }
    
    public Long getRelatedSceneTypesMark() {
        return relatedSceneTypesMark;
    }

    public void setRelatedSceneTypesMark(Long relatedSceneTypesMark) {
        this.relatedSceneTypesMark = relatedSceneTypesMark;
        if (relatedSceneTypesMark != null) {
            List<Long> relatedSceneTypes = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                long bit = 1L << i;
                // 匹配对应的bit值
                if ((relatedSceneTypesMark & bit) != 0) {
                    relatedSceneTypes.add(bit);
                }
            }
            this.relatedSceneTypes = StringUtils.join(relatedSceneTypes, ",");
            this.setCustomInfoByKey("relatedSceneTypes", relatedSceneTypes);
        }
    }

    public String getRelatedSceneTypesName() {
        return relatedSceneTypesName;
    }

    public void setRelatedSceneTypesName(String relatedSceneTypesName) {
        this.relatedSceneTypesName = relatedSceneTypesName;
    }

    public String getTrackingUser() {
		return trackingUser;
	}

	public void setTrackingUser(String trackingUser) {
		this.trackingUser = trackingUser;
	}

	public String getTrackingUsername() {
		return trackingUsername;
	}

	public void setTrackingUsername(String trackingUsername) {
		this.trackingUsername = trackingUsername;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getVisibleRange() {
		return visibleRange;
	}

	public void setVisibleRange(int visibleRange) {
		this.visibleRange = visibleRange;
	}

	public String getReader() {
		return reader;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public int getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}
	
	public Integer getAffectedType() {
		return affectedType;
	}

	public void setAffectedType(Integer affectedType) {
		this.affectedType = affectedType;
	}

	public List<SoftVersionParser> getSoftVersionParserList() {
		return softVersionParserList;
	}

	public void setSoftVersionParserList(List<SoftVersionParser> softVersionParserList) {
		this.softVersionParserList = softVersionParserList;
	}

	public Boolean getCheckSoft() {
		return affectedType != null || (affectedVersion != null && affectedVersion.trim().length() > 0);
	}

	public List<? extends Object> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<? extends Object> statusList) {
		this.statusList = statusList;
	}

}
