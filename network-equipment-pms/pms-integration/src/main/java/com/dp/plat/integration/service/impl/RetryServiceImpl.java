package com.dp.plat.integration.service.impl;

import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.integration.constant.IntegrationConstants;
import com.dp.plat.integration.entity.IntegrationLog;
import com.dp.plat.integration.service.D365IntegrationService;
import com.dp.plat.integration.service.FpIntegrationService;
import com.dp.plat.integration.service.IIntegrationLogService;
import com.dp.plat.integration.service.RetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link RetryService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetryServiceImpl implements RetryService {

    private final IIntegrationLogService integrationLogService;
    private final D365IntegrationService d365IntegrationService;
    private final FpIntegrationService fpIntegrationService;

    /**
     * Run every {@code integration.retry.interval} milliseconds (default 5 min).
     */
    @Override
    @Scheduled(fixedDelayString = "${integration.retry.interval:300000}")
    public void scheduledRetry() {
        List<IntegrationLog> pendingLogs = integrationLogService.getPendingRetryLogs();
        if (pendingLogs.isEmpty()) {
            return;
        }
        log.info("Integration retry job picked up {} pending log(s)", pendingLogs.size());
        for (IntegrationLog logRecord : pendingLogs) {
            try {
                integrationLogService.incrementRetryCount(logRecord.getId());
                dispatch(logRecord);
            } catch (Exception e) {
                log.warn("Retry attempt for log {} failed: {}", logRecord.getId(), e.getMessage());
            }
        }
    }

    @Override
    public IntegrationLog retryLog(Long logId) {
        IntegrationLog logRecord = integrationLogService.getById(logId);
        if (logRecord == null) {
            throw new BusinessException("集成日志不存在");
        }
        // Manual retry always attempts once, regardless of the retry count.
        return dispatch(logRecord);
    }

    /**
     * Dispatch a log to the matching integration adapter based on its log type.
     * The adapter re-posts the stored request body and updates the log status.
     */
    private IntegrationLog dispatch(IntegrationLog logRecord) {
        String logType = logRecord.getLogType();
        if (IntegrationConstants.LOG_TYPE_D365.equals(logType)) {
            return d365IntegrationService.retry(logRecord.getId());
        }
        if (IntegrationConstants.LOG_TYPE_FP.equals(logType)) {
            return fpIntegrationService.retry(logRecord.getId());
        }
        log.warn("No retry adapter registered for log type: {} (logId={})", logType, logRecord.getId());
        return integrationLogService.getById(logRecord.getId());
    }
}
