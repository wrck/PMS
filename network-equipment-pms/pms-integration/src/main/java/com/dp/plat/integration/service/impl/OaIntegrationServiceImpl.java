package com.dp.plat.integration.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.integration.config.OaProperties;
import com.dp.plat.integration.constant.IntegrationConstants;
import com.dp.plat.integration.dto.OaHealthDto;
import com.dp.plat.integration.entity.IntegrationLog;
import com.dp.plat.integration.model.oa.OaTodoRequest;
import com.dp.plat.integration.model.oa.OaTokenResponse;
import com.dp.plat.integration.service.IIntegrationLogService;
import com.dp.plat.integration.service.OaIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link OaIntegrationService}.
 *
 * <p>Reuses the same OAuth2 token caching pattern (ConcurrentHashMap +
 * ahead-of-expiry refresh + double-checked locking) as the D365 and FP
 * adapters, but with a 5-minute skew so the token is auto-renewed whenever
 * less than 5 minutes remain before expiry. All outbound calls are recorded
 * in {@link IntegrationLog} with {@code logType="OA"} for retry.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OaIntegrationServiceImpl implements OaIntegrationService {

    /** Refresh the token when less than this many seconds remain. */
    private static final long TOKEN_SKEW_SECONDS = 300L;

    private static final String TOKEN_CACHE_KEY = "oa";

    private final OaProperties oaProperties;
    private final RestTemplate integrationRestTemplate;
    private final IIntegrationLogService integrationLogService;
    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, TokenCache> tokenCache = new ConcurrentHashMap<>();

    private record TokenCache(String accessToken, long expiresAtMillis) {
        boolean isValid(long now) {
            return accessToken != null && expiresAtMillis > now + TOKEN_SKEW_SECONDS * 1000L;
        }
    }

    @Override
    public String getAccessToken() {
        long now = System.currentTimeMillis();
        TokenCache cache = tokenCache.get(TOKEN_CACHE_KEY);
        if (cache != null && cache.isValid(now)) {
            return cache.accessToken;
        }
        synchronized (this) {
            cache = tokenCache.get(TOKEN_CACHE_KEY);
            if (cache != null && cache.isValid(now)) {
                return cache.accessToken;
            }
            OaTokenResponse response = requestToken();
            if (response == null || response.getAccessToken() == null) {
                throw new BusinessException("OA token response is empty");
            }
            int expiresIn = response.getExpiresIn() == null ? 3600 : response.getExpiresIn();
            tokenCache.put(TOKEN_CACHE_KEY, new TokenCache(response.getAccessToken(), now + expiresIn * 1000L));
            return response.getAccessToken();
        }
    }

    @Override
    public boolean pushTodo(OaTodoRequest request) {
        String url = oaProperties.getBaseUrl() + "/todo/push";
        String body = writeJson(request);
        IntegrationLog logRecord = IntegrationLog.builder()
                .logType(IntegrationConstants.LOG_TYPE_OA)
                .businessType(IntegrationConstants.BIZ_OA_TODO_PUSH)
                .businessId(request.getBusinessKey() != null
                        ? request.getBusinessKey()
                        : request.getProcessInstanceId())
                .requestUrl(url)
                .requestBody(body)
                .build();
        logRecord = integrationLogService.log(logRecord);
        return execute(HttpMethod.POST, logRecord, url, body);
    }

    @Override
    public boolean completeTodo(String businessKey) {
        String url = oaProperties.getBaseUrl() + "/todo/complete";
        String body = writeJson(java.util.Map.of("businessKey", businessKey == null ? "" : businessKey));
        IntegrationLog logRecord = IntegrationLog.builder()
                .logType(IntegrationConstants.LOG_TYPE_OA)
                .businessType(IntegrationConstants.BIZ_OA_TODO_COMPLETE)
                .businessId(businessKey)
                .requestUrl(url)
                .requestBody(body)
                .build();
        logRecord = integrationLogService.log(logRecord);
        return execute(HttpMethod.PUT, logRecord, url, body);
    }

    @Override
    public boolean transferTask(String businessKey, String newHandlerUserId) {
        String url = oaProperties.getBaseUrl() + "/todo/transfer";
        String body = writeJson(java.util.Map.of(
                "businessKey", businessKey == null ? "" : businessKey,
                "newHandlerUserId", newHandlerUserId == null ? "" : newHandlerUserId));
        IntegrationLog logRecord = IntegrationLog.builder()
                .logType(IntegrationConstants.LOG_TYPE_OA)
                .businessType(IntegrationConstants.BIZ_OA_TODO_TRANSFER)
                .businessId(businessKey)
                .requestUrl(url)
                .requestBody(body)
                .build();
        logRecord = integrationLogService.log(logRecord);
        return execute(HttpMethod.PUT, logRecord, url, body);
    }

    @Override
    public OaHealthDto healthCheck() {
        boolean tokenValid = false;
        boolean connected = false;
        try {
            getAccessToken();
            tokenValid = true;
            // A trivial GET against the base URL confirms reachability.
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            integrationRestTemplate.exchange(oaProperties.getBaseUrl(),
                    HttpMethod.GET, new HttpEntity<>(headers), String.class);
            connected = true;
        } catch (Exception e) {
            log.debug("OA health check failed: {}", e.getMessage());
        }
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        long pushCount = countLogs(IntegrationConstants.STATUS_SUCCESS, since);
        long failCount = countLogs(IntegrationConstants.STATUS_FAILED, since);
        List<IntegrationLog> recent = recentLogs(10);
        return OaHealthDto.builder()
                .connected(connected)
                .tokenValid(tokenValid)
                .recentPushCount((int) pushCount)
                .recentFailCount((int) failCount)
                .recentLogs(recent)
                .build();
    }

    // ---- internals ----

    private boolean execute(HttpMethod method, IntegrationLog logRecord, String url, String body) {
        try {
            String token = getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = integrationRestTemplate.exchange(
                    url, method, entity, String.class);
            integrationLogService.markSuccess(logRecord.getId(), response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            integrationLogService.markFailed(logRecord.getId(), e.getMessage());
            log.warn("OA integration call failed for log {}: {}", logRecord.getId(), e.getMessage());
            return false;
        }
    }

    private OaTokenResponse requestToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", oaProperties.getClientId());
        form.add("client_secret", oaProperties.getClientSecret());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        try {
            return integrationRestTemplate.postForObject(
                    oaProperties.getTokenUrl(), entity, OaTokenResponse.class);
        } catch (RestClientException e) {
            throw new BusinessException("OA token request failed: " + e.getMessage());
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new BusinessException("Failed to serialize OA request body: " + e.getMessage());
        }
    }

    private long countLogs(String status, LocalDateTime since) {
        LambdaQueryWrapper<IntegrationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IntegrationLog::getLogType, IntegrationConstants.LOG_TYPE_OA)
                .eq(IntegrationLog::getResponseStatus, status)
                .ge(IntegrationLog::getCreateTime, since);
        return integrationLogService.count(wrapper);
    }

    private List<IntegrationLog> recentLogs(int limit) {
        LambdaQueryWrapper<IntegrationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IntegrationLog::getLogType, IntegrationConstants.LOG_TYPE_OA)
                .orderByDesc(IntegrationLog::getCreateTime)
                .last("LIMIT " + Math.max(limit, 1));
        List<IntegrationLog> logs = integrationLogService.list(wrapper);
        return logs == null ? new ArrayList<>() : logs;
    }
}
