package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.service.SubcontractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubcontractServiceImpl implements SubcontractService {

    @Autowired
    private PmsSubcontractMapper subcontractMapper;
    @Autowired
    private PmsSubcontractLineMapper lineMapper;
    @Autowired
    private PmsSubcontractDeliverMapper deliverMapper;
    @Autowired
    private PmsSubcontractPaymentMapper paymentMapper;
    @Autowired
    private PmsSubcontractFacilitatorMapper facilitatorMapper;
    @Autowired
    private PmsProjectMapper projectMapper;
    @Autowired
    private PmsShipmentInfoMapper shipmentInfoMapper;

    // ===== 基础CRUD =====

    @Override
    public IPage<PmsSubcontract> queryPage(Integer pageNum, Integer pageSize,
                                            String subcontractName, String officeCode, Integer state) {
        Page<PmsSubcontract> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsSubcontract> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(subcontractName), PmsSubcontract::getSubcontractName, subcontractName)
               .eq(StringUtils.hasText(officeCode), PmsSubcontract::getOfficeCode, officeCode)
               .eq(state != null, PmsSubcontract::getState, state)
               .orderByDesc(PmsSubcontract::getCreateTime);
        return subcontractMapper.selectPage(page, wrapper);
    }

    @Override
    public PmsSubcontract getDetail(Long id) {
        PmsSubcontract sc = subcontractMapper.selectById(id);
        if (sc == null) {
            throw new BusinessException("分包项目不存在");
        }
        return sc;
    }

    @Override
    @Transactional
    public void create(PmsSubcontract subcontract) {
        subcontract.setCreateBy(SecurityUtil.getCurrentUsername());
        subcontract.setCreateTime(LocalDateTime.now());
        if (subcontract.getState() == null) {
            subcontract.setState(0); // 草稿
        }
        subcontractMapper.insert(subcontract);
    }

    @Override
    @Transactional
    public void update(PmsSubcontract subcontract) {
        PmsSubcontract existing = subcontractMapper.selectById(subcontract.getId());
        if (existing == null) {
            throw new BusinessException("分包项目不存在");
        }
        subcontract.setUpdateBy(SecurityUtil.getCurrentUsername());
        subcontract.setUpdateTime(LocalDateTime.now());
        subcontractMapper.updateById(subcontract);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PmsSubcontract existing = subcontractMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("分包项目不存在");
        }
        // 删除关联数据
        lineMapper.delete(new LambdaQueryWrapper<PmsSubcontractLine>().eq(PmsSubcontractLine::getSubcontractId, id));
        deliverMapper.delete(new LambdaQueryWrapper<PmsSubcontractDeliver>().eq(PmsSubcontractDeliver::getSubcontractId, id));
        paymentMapper.delete(new LambdaQueryWrapper<PmsSubcontractPayment>().eq(PmsSubcontractPayment::getSubcontractId, id));
        subcontractMapper.deleteById(id);
    }

    // ===== 设备行 =====

    @Override
    public List<PmsSubcontractLine> queryLines(Long subcontractId) {
        return lineMapper.selectBySubcontractId(subcontractId);
    }

    @Override
    @Transactional
    public void saveLine(PmsSubcontractLine line) {
        if (line.getId() != null) {
            line.setUpdateBy(SecurityUtil.getCurrentUsername());
            line.setUpdateTime(LocalDateTime.now());
            lineMapper.updateById(line);
        } else {
            line.setCreateBy(SecurityUtil.getCurrentUsername());
            line.setCreateTime(LocalDateTime.now());
            lineMapper.insert(line);
        }
    }

    @Override
    @Transactional
    public void deleteLine(Long id) {
        lineMapper.deleteById(id);
    }

    // ===== 交付件 =====

    @Override
    public List<PmsSubcontractDeliver> queryDelivers(Long subcontractId) {
        return deliverMapper.selectBySubcontractId(subcontractId);
    }

    @Override
    @Transactional
    public void saveDeliver(PmsSubcontractDeliver deliver) {
        if (deliver.getId() != null) {
            deliverMapper.updateById(deliver);
        } else {
            deliver.setUploadBy(SecurityUtil.getCurrentUsername());
            deliver.setUploadTime(LocalDateTime.now());
            deliverMapper.insert(deliver);
        }
    }

    @Override
    @Transactional
    public void deleteDeliver(Long id) {
        deliverMapper.deleteById(id);
    }

    // ===== 付款 =====

    @Override
    public List<PmsSubcontractPayment> queryPayments(Long subcontractId) {
        return paymentMapper.selectBySubcontractId(subcontractId);
    }

    @Override
    @Transactional
    public void savePayment(PmsSubcontractPayment payment) {
        if (payment.getId() != null) {
            payment.setUpdateBy(SecurityUtil.getCurrentUsername());
            payment.setUpdateTime(LocalDateTime.now());
            paymentMapper.updateById(payment);
        } else {
            payment.setCreateBy(SecurityUtil.getCurrentUsername());
            payment.setCreateTime(LocalDateTime.now());
            paymentMapper.insert(payment);
        }
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        paymentMapper.deleteById(id);
    }

    // ===== 服务商 =====

    @Override
    public List<PmsSubcontractFacilitator> queryFacilitators() {
        return facilitatorMapper.selectAllActive();
    }

    @Override
    public PmsSubcontractFacilitator getFacilitator(Long id) {
        return facilitatorMapper.selectById(id);
    }

    @Override
    @Transactional
    public void saveFacilitator(PmsSubcontractFacilitator facilitator) {
        if (facilitator.getId() != null) {
            facilitator.setUpdateBy(SecurityUtil.getCurrentUsername());
            facilitator.setUpdateTime(LocalDateTime.now());
            facilitatorMapper.updateById(facilitator);
        } else {
            facilitator.setCreateBy(SecurityUtil.getCurrentUsername());
            facilitator.setCreateTime(LocalDateTime.now());
            facilitatorMapper.insert(facilitator);
        }
    }

    // ===== 流程 =====

    @Override
    @Transactional
    public void startFlow(Long id) {
        PmsSubcontract sc = subcontractMapper.selectById(id);
        if (sc == null) {
            throw new BusinessException("分包项目不存在");
        }
        sc.setState(1); // 审批中
        sc.setUpdateBy(SecurityUtil.getCurrentUsername());
        sc.setUpdateTime(LocalDateTime.now());
        subcontractMapper.updateById(sc);
        // 工作流引擎集成后可启用
        // workflowService.startProcess("subcontract", sc.getId().toString(), vars);
    }

    @Override
    @Transactional
    public void approve(Long id, String comment, boolean approved) {
        PmsSubcontract sc = subcontractMapper.selectById(id);
        if (sc == null) {
            throw new BusinessException("分包项目不存在");
        }
        if (approved) {
            sc.setState(2); // 已通过
            sc.setZrApproveTime(LocalDateTime.now());
        } else {
            sc.setState(3); // 已驳回
        }
        sc.setUpdateBy(SecurityUtil.getCurrentUsername());
        sc.setUpdateTime(LocalDateTime.now());
        subcontractMapper.updateById(sc);
        // 工作流引擎集成后可启用
        // workflowService.completeTask(taskId, approved, comment);
    }

    @Override
    @Transactional
    public void close(Long id, String comment) {
        PmsSubcontract sc = subcontractMapper.selectById(id);
        if (sc == null) {
            throw new BusinessException("分包项目不存在");
        }
        sc.setState(4); // 已关闭
        sc.setUpdateBy(SecurityUtil.getCurrentUsername());
        sc.setUpdateTime(LocalDateTime.now());
        subcontractMapper.updateById(sc);
        // 工作流引擎集成后可启用
        // workflowService.completeTask(taskId, true, "闭环");
    }

    // ===== 回访 =====

    @Override
    @Transactional
    public void startCallBackFlow(Long id) {
        PmsSubcontract sc = subcontractMapper.selectById(id);
        if (sc == null) {
            throw new BusinessException("分包项目不存在");
        }
        sc.setCallbackState(1); // 回访中
        sc.setUpdateBy(SecurityUtil.getCurrentUsername());
        sc.setUpdateTime(LocalDateTime.now());
        subcontractMapper.updateById(sc);
        // 工作流引擎集成后可启用
        // workflowService.startProcess("subcontract_callback", sc.getId().toString(), vars);
    }

    // ===== 辅助查询 =====

    @Override
    public List<Map<String, Object>> queryProjectList(String contractNos) {
        if (!StringUtils.hasText(contractNos)) {
            return new ArrayList<>();
        }
        List<PmsProject> projects = projectMapper.selectList(
                new LambdaQueryWrapper<PmsProject>()
                        .in(PmsProject::getOfficeCode, contractNos.split(",")));
        return projects.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("projectId", p.getId());
            map.put("projectCode", p.getProjectCode());
            map.put("projectName", p.getProjectName());
            map.put("officeCode", p.getOfficeCode());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> queryShipmentInfo(String contractNos, String projectIds) {
        if (!StringUtils.hasText(contractNos)) {
            return new ArrayList<>();
        }
        List<PmsShipmentInfo> shipments = shipmentInfoMapper.selectList(
                new LambdaQueryWrapper<PmsShipmentInfo>()
                        .in(PmsShipmentInfo::getContractNo, contractNos.split(",")));
        return shipments.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("barCode", s.getBarCode());
            map.put("itemCode", s.getItemCode());
            map.put("itemModel", s.getItemModel());
            map.put("itemName", s.getItemName());
            map.put("contractNo", s.getContractNo());
            return map;
        }).collect(Collectors.toList());
    }
}
