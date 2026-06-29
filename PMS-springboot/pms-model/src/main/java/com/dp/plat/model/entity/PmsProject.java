package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目实体 - 对应 pm_project 表
 *
 * 注意：合同号(contractNo)存储在 pm_project_contract 表，非本表字段
 * column001~014 为泛化字段，语义见注释
 */
@Data
@TableName("pm_project")
public class PmsProject extends BaseEntity {

    @TableId(value = "projectId", type = IdType.AUTO)
    private Long id;

    /** 项目类型 (10=用服售后, afss=安服售后, afxx=安服先行) */
    @TableField("projectType")
    private String projectType;

    /** 项目编码 */
    @TableField("projectCode")
    private String projectCode;

    /** 项目名称 */
    @TableField("projectName")
    private String projectName;

    /** 项目状态编码 (关联fnd_basic_data dataTypeCode=02) */
    @TableField("projectState")
    private String projectState;

    /** 回退标志 (30=创建项目, 32=指定PM, 34=填写渠道, 40=不予跟踪) */
    @TableField("isback")
    private String isback;

    // ===== column 泛化字段 =====

    /** 办事处编码 (column001) */
    @TableField("column001")
    private String officeCode;

    /** 客户编码-ERP (column002) */
    @TableField("column002")
    private String customerCode;

    /** 客户名称-ERP (column003) */
    @TableField("column003")
    private String customerName;

    /** 市场部编码 (column004) */
    @TableField("column004")
    private String marketDeptCode;

    /** 系统部ID (column005) */
    @TableField("column005")
    private String systemDeptId;

    /** 拓展部ID (column006) */
    @TableField("column006")
    private String extendDeptId;

    /** 子行业ID (column007) */
    @TableField("column007")
    private String subIndustryId;

    /** 不予跟踪原因 (column008) */
    @TableField("column008")
    private String notGrantTailCause;

    /** 订单创建时间 (column009) */
    @TableField("column009")
    private LocalDateTime orderCreateTime;

    /** 项目类型/等级 (column010, 关联fnd_basic_data dataTypeCode=05) */
    @TableField("column010")
    private String projectCategory;

    /** 项目分类 (column011) */
    @TableField("column011")
    private String projectClassify;

    /** 项目实施方式 (column012, 关联fnd_basic_data dataTypeCode=15) */
    @TableField("column012")
    private String serviceType;

    /** 实施方式只读值 (-1=可修改) */
    @TableField("columno12_readonly")
    private Integer serviceTypeReadonly;

    /** 最终客户名称 (column013) */
    @TableField("column013")
    private String finalCustomerName;

    /** 回退说明 (column014) */
    @TableField("column014")
    private String backCause;

    // ===== 业务字段 =====

    /** 客户项目名称 */
    @TableField("customerProjectName")
    private String customerProjectName;

    /** 销售类型 (01=正常, 02=借转销, 14=销售类借货) */
    @TableField("salesType")
    private String salesType;

    /** 重大项目级别 */
    @TableField("majorProjectLevel")
    private String majorProjectLevel;

    /** 公司ID */
    @TableField("compId")
    private Long companyId;

    /** 项目开始实施时间 */
    @TableField("projectStartTime")
    private LocalDateTime projectStartTime;

    /** 项目创建时间 */
    @TableField("projectCreateTime")
    private LocalDateTime projectCreateTime;

    /** 项目最近刷新时间 */
    @TableField("projectRefreshTime")
    private LocalDateTime projectRefreshTime;

    /** 项目关闭时间 */
    @TableField("projectCloseTime")
    private LocalDateTime projectCloseTime;

    /** 自定义扩展信息(JSON) */
    @TableField("customInfo")
    private String customInfo;

    /** 自定义配置(JSON) */
    @TableField("customConfig")
    private String customConfig;

    /** 数据是否失效 (0=有效, 1=失效) */
    @TableField("disabled")
    private Integer disabled;

    // ===== 非数据库字段 (从关联表/视图获取) =====

    /** 合同号 (来自pm_project_contract表) */
    @TableField(exist = false)
    private String contractNo;

    /** 项目状态名称 */
    @TableField(exist = false)
    private String projectStateName;

    /** 实施状态 */
    @TableField(exist = false)
    private Integer executionState;

    /** 实施状态名称 */
    @TableField(exist = false)
    private String executionStateName;

    /** 服务经理编码 */
    @TableField(exist = false)
    private String smCode;

    /** 服务经理姓名 */
    @TableField(exist = false)
    private String smName;

    /** 项目经理编码 */
    @TableField(exist = false)
    private String pmCode;

    /** 项目经理姓名 */
    @TableField(exist = false)
    private String pmName;

    /** 项目经理B编码(备份) */
    @TableField(exist = false)
    private String pmCodeB;

    /** 项目经理B姓名 */
    @TableField(exist = false)
    private String pmNameB;

    /** 销售人员编码 */
    @TableField(exist = false)
    private String salesManCode;

    /** 销售人员姓名 */
    @TableField(exist = false)
    private String salesManName;

    /** 项目计划状态 */
    @TableField(exist = false)
    private String projectPlanState;

    /** 发货状态 */
    @TableField(exist = false)
    private Integer shipmentState;

    /** 发货状态名称 */
    @TableField(exist = false)
    private String shipmentStateName;

    /** 项目等级 */
    @TableField(exist = false)
    private String projectLevel;

    /** 合作渠道 */
    @TableField(exist = false)
    private String partnerChannel;

    /** 服务渠道 */
    @TableField(exist = false)
    private String serviceChannel;

    /** 代理商渠道 */
    @TableField(exist = false)
    private String agentChannel;

    /** 公司名称 */
    @TableField(exist = false)
    private String companyName;

    /** 办事处名称 */
    @TableField(exist = false)
    private String officeName;

    /** 团队成员编码 */
    @TableField(exist = false)
    private String teamMemberCodes;

    /** 团队成员姓名 */
    @TableField(exist = false)
    private String teamMemberNames;

    /** 计划状态名称 */
    @TableField(exist = false)
    private String planStateName;

    /** 质保状态 */
    @TableField(exist = false)
    private String warrantyStatus;

    /** 质保等级 */
    @TableField(exist = false)
    private String warrantyGrade;

    /** 关闭流程状态 */
    @TableField(exist = false)
    private String closeProcessState;
}
