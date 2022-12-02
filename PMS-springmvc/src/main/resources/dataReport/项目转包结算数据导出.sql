SELECT '安服转包' AS dataType, h.id AS `dispatchId`, h.`dispatchName` AS `dispatchName`, h.`dispatchNo` AS `dispatchNo`
    , IFNULL(s.`customInfo` ->> '$.purchId', h.`customInfo` ->> '$.purchId') AS purchId
    , s.`customInfo` ->> '$.packingSlipId' AS packingSlipId, s.`customInfo` ->> '$.innerInvoiceId' AS innerInvoiceId
    , psf.account AS vendAccount, psf.name AS vendName
    , h.`dispatchSeq` AS `dispatchSeq`, h.`contractNos` AS `contractNos`, h.`projectIds` AS `projectIds`, h.`type` AS `dispatchType`, bd2.basicDataName AS dispatchTypeName
    , h.customInfo ->> '$.serviceType' AS serviceType, h.`state` AS `dispatchState`, bd1.basicDataName AS dispatchStateName
    , FORMAT(h.dispatchAmount, 2) AS `dispatchAmount`
        -- , SUM(sc.`collected_money_amount`) AS collectedAmount
--     , SUM(sc.`delivered_money_amount`) AS deliveredAmount
--     , SUM(sc.`contract_money_amount`) AS contractAmount
--     , ROUND(SUM(sc.`collected_money_amount`) / SUM(sc.`contract_money_amount`) * 100, 2) AS collectedRatio

    , s.settleSeq
    , ROUND(ds.settledAmount / h.dispatchAmount * 100, 2) AS settledRatio, FORMAT(ds.settledAmount, 2) AS settledAmount
    , s.`ratio`, FORMAT(s.`amount`, 2) AS amount, FORMAT(s.`customInfo` ->> '$.approvedAmount', 2) AS approvedAmount
    , FORMAT(s.`customInfo` ->> '$.paidAmount', 2) AS paidAmount, s.state AS settlementState, bd.basicDataName AS settlementStateName
    , CONCAT_WS('，', s.`customInfo` ->> '$.approveState', s.`customInfo` ->> '$.paystate') AS approveState
    , DATE_FORMAT(s.`confirmTime`, '%Y-%m-%d %H:%i:%s') AS confirmTime
    , DATE_FORMAT(s.`paymentTime`, '%Y-%m-%d %H:%i:%s') AS paymentTime
    , s.`customInfo` ->> '$.invoiceNumber' AS invoiceNumber, s.`memo`, s.`progressDesc`
    , s.`acceptanceDesc`, s.`remark`, s.customInfo, 1 AS orgId
FROM `pm_dispatch_project_settlement` s
    LEFT JOIN pm_dispatch_project_header h
    ON h.`id` = s.`dispatchId`
        AND s.`disabled` = FALSE
--     LEFT JOIN `sms_ofst_contract_head_sap` sc ON FIND_IN_SET(sc.`contract_num`, h.`contractNos`)
    LEFT JOIN (
        SELECT dispatchId
            , SUM(REPLACE(ds.`amount`, ',', '')) AS settledAmount
        FROM `pm_dispatch_project_settlement` ds
        WHERE ds.`disabled` = FALSE
        GROUP BY ds.`dispatchId`
    ) ds
    ON ds.`dispatchId` = h.`id`
    LEFT JOIN pm_facilitator psf ON psf.id = h.facilitatorId
    LEFT JOIN fnd_department d ON h.`officeCode` = d.`departmentNum`
    LEFT JOIN `fnd_basic_data` bd
    ON s.`state` = bd.`basicDataId`
        AND bd.`dataTypeCode` = 'settlementState'
    LEFT JOIN `fnd_basic_data` bd1
    ON h.`state` = bd1.`basicDataId`
        AND bd1.`dataTypeCode` = 'dispatchState'
    LEFT JOIN `fnd_basic_data` bd2
    ON h.`type` = bd2.`basicDataId`
        AND bd2.`dataTypeCode` = 'dispatchType'
WHERE s.`disabled` = FALSE
    AND s.state > 0
    AND h.customInfo ->> '$.purchId' IS NOT NULL
GROUP BY s.`id`
UNION ALL
SELECT '用服转包' AS dataType, h.id AS `dispatchId`, h.`subcontractName` AS `dispatchName`, h.`subcontractNo` AS `dispatchNo`
    , IFNULL(s.`customInfo` ->> '$.purchId', h.`customInfo` ->> '$.purchId') AS purchId
    , s.`customInfo` ->> '$.packingSlipId' AS packingSlipId, s.`customInfo` ->> '$.innerInvoiceId' AS innerInvoiceId
    , psf.account AS vendAccount, psf.name AS vendName
    , NULL AS `dispatchSeq`, h.`contractNos` AS `contractNos`, h.`projectIds` AS `projectIds`, h.`type` AS `dispatchType`, bd2.basicDataName AS dispatchTypeName
    , h.customInfo ->> '$.serviceType' AS serviceType, h.`state` AS `dispatchState`, bd1.basicDataName AS dispatchStateName
    , h.subcontractAmount AS `dispatchAmount`
        -- , SUM(sc.`collected_money_amount`) AS collectedAmount
--     , SUM(sc.`delivered_money_amount`) AS deliveredAmount
--     , SUM(sc.`contract_money_amount`) AS contractAmount
--     , ROUND(SUM(sc.`collected_money_amount`) / SUM(sc.`contract_money_amount`) * 100, 2) AS collectedRatio

    , NULL AS settleSeq
    , ROUND(ds.settledAmount / REPLACE(h.subcontractAmount, ",", "") * 100, 2) AS settledRatio, FORMAT(ds.settledAmount, 2) AS settledAmount
    , s.`ratio`, s.`amount` AS amount, s.`customInfo` ->> '$.approvedAmount' AS approvedAmount
    , FORMAT(s.`customInfo` ->> '$.paidAmount', 2) AS paidAmount, NULL AS settlementState, NULL AS settlementStateName
    , CONCAT_WS('，', s.`customInfo` ->> '$.status', s.`customInfo` ->> '$.paystate') AS approveState
    , DATE_FORMAT(s.`confirmTime`, '%Y-%m-%d %H:%i:%s') AS confirmTime
    , DATE_FORMAT(s.`paymentTime`, '%Y-%m-%d %H:%i:%s') AS paymentTime
    , s.`customInfo` ->> '$.invoiceNumber' AS invoiceNumber, s.remark AS `memo`, NULL AS `progressDesc`
    , NULL AS `acceptanceDesc`, s.`remark`, s.customInfo, h.orgId
FROM `pm_subcontract_project_payment` s
    LEFT JOIN `pm_subcontract_project_header` h
    ON h.`id` = s.`subcontractId`
--     LEFT JOIN `sms_ofst_contract_head_sap` sc ON FIND_IN_SET(sc.`contract_num`, h.`contractNos`)
    LEFT JOIN (
        SELECT subcontractId AS dispatchId
            , SUM(REPLACE(ds.`amount`, ',', '')) AS settledAmount
        FROM `pm_subcontract_project_payment` ds
        GROUP BY ds.`subcontractId`
    ) ds
    ON ds.`dispatchId` = h.`id`
    LEFT JOIN pm_subcontract_facilitator psf on psf.id = h.facilitatorId
    LEFT JOIN fnd_department d ON h.`officeCode` = d.`departmentNum`
    LEFT JOIN `fnd_basic_data` bd1
    ON h.`state` = bd1.`basicDataId`
        AND bd1.`dataTypeCode` = 'subcontractState'
    LEFT JOIN `fnd_basic_data` bd2
    ON h.`type` = bd2.`basicDataId`
        AND bd2.`dataTypeCode` = 'subcontractType'
WHERE h.customInfo ->> '$.purchId' IS NOT NULL AND s.`customInfo` ->> '$.status' IS NOT NULL
GROUP BY s.`id`
ORDER BY NULL;