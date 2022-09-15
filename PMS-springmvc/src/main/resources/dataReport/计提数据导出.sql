-- 数据类型 数据ID    采购订单号   转包服务合同编号    销售合同号   部门名称    
-- 外包项目名称   服务外包商   合同金额    项目类别    服务外包期间  派单时间    
-- 发票类型 发票税率    项目进度    备注  计提编号    计提会计科目  计提金额    计提会计期
SELECT 
    'dispatch' AS dataType, h.id AS dataId, h.customInfo ->> '$.purchId' AS purchId, h.dispatchNo, contractNos
--     , h.customInfo ->> '$.officeName' AS officeName, h.customInfo ->> '$.profitDepName' AS profitDepName
    , d.`departmentName` AS officeName, d1.`departmentName` AS profitDepName
    , h.dispatchName, h.facilitatorId, h.facilitatorName, h.dispatchAmount, bd3.`basicDataName` AS serviceName
    , h.customInfo ->> '$.subcontStartDate' AS subcontStartDate, h.customInfo ->> '$.subcontEndDate' AS subcontEndDate
    , h.dispatchTime, h.customInfo ->> '$.taxItemGroup' taxRate
--     , bd2.basicDataName as typeName
    
    , ph.customInfo ->> '$.projectProgress' AS `projectProgress`
    , phsd.`basicDataName` AS `projectStateName`
    , h.remark
        
    , sc.collectedAmount, sc.deliveredAmount, sc.contractAmount, sc.collectContractNos
--  , sc.collectedRatio
    , ROUND(sc.`collectedAmount` / sc.`contractAmount` * 100, 2) AS collectedRatio
    , ds.settledAmount
    , ROUND(
        ds.settledAmount / REPLACE(h.dispatchAmount, ',', '') * 100, 2
    ) AS settledRatio 

    , h.customInfo ->> '$.createName' AS createName
    
    , acd.field6 AS accrualNo, acd.field7 AS accrualAccount, acd.field8 AS accrualAmount
    , acd.field9 AS accrualPeriod, acd.field10 AS accrualRemark
FROM
    pm_dispatch_project_header h 
    LEFT JOIN (
    SELECT h.id, SUM(sc.`collected_money_amount`) AS collectedAmount
        , SUM(sc.`delivered_money_amount`) AS deliveredAmount
        , SUM(sc.`contract_money_amount`) AS contractAmount
        , GROUP_CONCAT(sc.`contract_num`) AS collectContractNos
--      , ROUND(SUM(sc.`collected_money_amount`) / SUM(sc.`contract_money_amount`), 4) AS collectedRatio
    FROM pm_dispatch_project_header h 
    LEFT JOIN `sms_ofst_contract_head_sap` sc 
        ON FIND_IN_SET(
            sc.`contract_num`, h.`contractNos`
        ) 
        WHERE h.disabled = FALSE AND h.customInfo ->> '$.purchId' IS NOT NULL
    GROUP BY h.`id`
    ) sc
    ON sc.id = h.id
    LEFT JOIN `pm_project` ph 
        ON ph.projectId = h.projectIds 
    LEFT JOIN 
        (SELECT 
            dispatchId, ROUND(SUM(REPLACE(ds.`amount`, ',', '')), 2) AS settledAmount 
        FROM
            `pm_dispatch_project_settlement` ds 
        WHERE ds.`disabled` = FALSE 
        GROUP BY ds.`dispatchId`) ds 
        ON ds.`dispatchId` = h.`id` 
    LEFT JOIN fnd_department d 
        ON h.`officeCode` = d.`departmentNum` 
    LEFT JOIN fnd_department d1 
        ON h.`profitDepCode` = d1.`departmentNum` 
--     left join `fnd_basic_data` bd 
--         on h.`state` = bd.`basicDataId` 
--         and bd.`dataTypeCode` = 'dispatchState' 
--     left join `fnd_basic_data` bd2 
--         on h.`type` = bd2.`basicDataId` 
--         and bd2.`dataTypeCode` = 'dispatchType' 
    LEFT JOIN `fnd_basic_data` bd3
        ON bd3.`basicDataId` = h.customInfo ->> '$.serviceType'
        AND bd3.`dataTypeCode` = 'dispatchServiceType'
    LEFT JOIN fnd_basic_data phsd 
        ON phsd.`dataTypeCode` = CONCAT(
            ph.`projectType`, '_projectState'
        ) 
        AND phsd.`basicDataId` = ph.`projectState` 
    LEFT JOIN `pm_common_related_data` acd
    ON acd.type = 'accrualData'
    AND acd.objType = 'dispatch'
    AND acd.field3 = h.id
WHERE h.disabled = FALSE AND h.customInfo ->> '$.purchId' IS NOT NULL
UNION ALL
SELECT 
    'subcontract' AS dataType, h.id AS dataId, h.customInfo ->> '$.purchId' AS purchId, h.subcontractNo, contractNos
--     , h.customInfo ->> '$.officeName' AS officeName, h.customInfo ->> '$.profitDepName' AS profitDepName
    , d.`departmentName` AS officeName, d1.`departmentName` AS profitDepName
    , h.subcontractName, h.facilitatorId, h.facilitatorName, h.subcontractAmount, bd3.`basicDataName` AS serviceName
    , h.customInfo ->> '$.subcontStartDate' AS subcontStartDate, h.customInfo ->> '$.subcontEndDate' AS subcontEndDate
    , h.customInfo ->> '$.subcontractTime' AS subcontractTime, h.customInfo ->> '$.taxItemGroup' taxRate
--     , bd2.basicDataName as typeName

    , ph.customInfo ->> '$.projectProgress' AS `projectProgress`
    , phsd.`basicDataName` AS `projectStateName`
    , h.remark
    
    , sc.collectedAmount, sc.deliveredAmount, sc.contractAmount, sc.collectContractNos
--  , sc.collectedRatio
    , ROUND(sc.`collectedAmount` / sc.`contractAmount` * 100, 2) AS collectedRatio
    , ds.settledAmount
    , ROUND(
        ds.settledAmount / REPLACE(h.subcontractAmount, ',', '') * 100, 2
    ) AS settledRatio 

    , h.customInfo ->> '$.createName' AS createName
    
    , acd.field6 AS accrualNo, acd.field7 AS accrualAccount, acd.field8 AS accrualAmount
    , acd.field9 AS accrualPeriod, acd.field10 AS accrualRemark
FROM
    `pm_subcontract_project_header` h 
    LEFT JOIN (
    SELECT h.id, SUM(sc.`collected_money_amount`) AS collectedAmount
        , SUM(sc.`delivered_money_amount`) AS deliveredAmount
        , SUM(sc.`contract_money_amount`) AS contractAmount
        , GROUP_CONCAT(sc.`contract_num`) AS collectContractNos
    FROM pm_subcontract_project_header h 
    LEFT JOIN `sms_ofst_contract_head_sap` sc 
        ON FIND_IN_SET(
            sc.`contract_num`, h.`contractNos`
        ) 
        WHERE h.effectiveTo IS NULL AND h.customInfo ->> '$.purchId' IS NOT NULL
    GROUP BY h.`id`
    ) sc
    ON sc.id = h.id
    LEFT JOIN `pm_project` ph 
        ON ph.projectId = h.projectIds 
    LEFT JOIN 
        (SELECT 
            subcontractId, ROUND(SUM(REPLACE(ds.`amount`, ',', '')), 2) AS settledAmount 
        FROM
            `pm_subcontract_project_payment` ds 
        GROUP BY ds.`subcontractId`) ds 
        ON ds.`subcontractId` = h.`id` 
    LEFT JOIN fnd_department d 
        ON h.`officeCode` = d.`departmentNum` 
    LEFT JOIN fnd_department d1 
        ON h.`profitDepCode` = d1.`departmentNum` 
--     left join `fnd_basic_data` bd 
--         on h.`state` = bd.`basicDataId` 
--         and bd.`dataTypeCode` = 'subcontractState' 
--     left join `fnd_basic_data` bd2 
--         on h.`type` = bd2.`basicDataId` 
--         and bd2.`dataTypeCode` = 'subcontractType' 
    LEFT JOIN `fnd_basic_data` bd3
        ON h.`type` = bd3.`basicDataId` 
        AND bd3.`dataTypeCode` = 'subcontractType' 
    LEFT JOIN fnd_basic_data phsd 
        ON phsd.`dataTypeCode` = '02'
        AND phsd.`basicDataId` = ph.`projectState` 
    LEFT JOIN `pm_common_related_data` acd
    ON acd.type = 'accrualData'
    AND acd.objType = 'subcontract'
    AND acd.field3 = h.id
WHERE h.effectiveTo IS NULL AND h.customInfo ->> '$.purchId' IS NOT NULL
;


