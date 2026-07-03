package com.dp.plat.integration.service.impl;

import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.integration.config.FpProperties;
import com.dp.plat.integration.constant.IntegrationConstants;
import com.dp.plat.integration.entity.IntegrationLog;
import com.dp.plat.integration.model.fp.FpResponse;
import com.dp.plat.integration.model.fp.FpTokenResponse;
import com.dp.plat.integration.model.fp.SettlementPushRequest;
import com.dp.plat.integration.service.FpIntegrationService;
import com.dp.plat.integration.service.IIntegrationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link FpIntegrationService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FpIntegrationServiceImpl implements FpIntegrationService {

    /** Refresh the token this many seconds before it actually expires. */
    private static final long TOKEN_SKEW_SECONDS = 60L;

    private final FpProperties fpProperties;
    private final RestTemplate integrationRestTemplate;
    private final IIntegrationLogService integrationLogService;
    private final ObjectMapper objectMapper;

    /** Simple in-memory token cache (ConcurrentHashMap) with expiry timestamp. */
    private final java.util.concurrent.ConcurrentHashMap<String, TokenCache> tokenCache =
            new java.util.concurrent.ConcurrentHashMap<>();

    private static final String TOKEN_CACHE_KEY = "fp";

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
            FpTokenResponse response = requestToken();
            if (response == null || response.getAccessToken() == null) {
                throw new BusinessException("FP token response is empty");
            }
            int expiresIn = response.getExpiresIn() == null ? 3600 : response.getExpiresIn();
            tokenCache.put(TOKEN_CACHE_KEY, new TokenCache(response.getAccessToken(), now + expiresIn * 1000L));
            return response.getAccessToken();
        }
    }

    @Override
    public FpResponse<String> pushSettlement(SettlementPushRequest request) {
        String url = fpProperties.getBaseUrl() + "/settlements";
        String body = writeJson(request);
        IntegrationLog log = IntegrationLog.builder()
                .logType(IntegrationConstants.LOG_TYPE_FP)
                .businessType(IntegrationConstants.BIZ_SETTLEMENT)
                .businessId(request.getSettlementNo())
                .requestUrl(url)
                .requestBody(body)
                .build();
        log = integrationLogService.log(log);
        return executePost(log, url, body);
    }

    @Override
    public IntegrationLog retry(Long logId) {
        IntegrationLog logRecord = integrationLogService.getById(logId);
        if (logRecord == null) {
            throw new BusinessException("集成日志不存在");
        }
        try {
            executePost(logRecord, logRecord.getRequestUrl(), logRecord.getRequestBody());
        } catch (Exception e) {
            // The log has already been marked failed inside executePost.
            log.warn("FP retry failed for log {}: {}", logId, e.getMessage());
        }
        return integrationLogService.getById(logId);
    }

    /**
     * Perform the actual POST against FP, then mark the log success/failed.
     *
     * @throws BusinessException when the HTTP call fails (the log is already
     *                           marked failed before rethrowing).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private FpResponse<String> executePost(IntegrationLog log, String url, String body) {
        try {
            String token = getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<FpResponse> response = integrationRestTemplate.postForEntity(
                    url, entity, FpResponse.class);
            FpResponse<String> fpResponse = (FpResponse<String>) response.getBody();
            integrationLogService.markSuccess(log.getId(), writeJson(fpResponse));
            return fpResponse;
        } catch (Exception e) {
            integrationLogService.markFailed(log.getId(), e.getMessage());
            throw new BusinessException("FP integration call failed: " + e.getMessage());
        }
    }

    private FpTokenResponse requestToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", fpProperties.getClientId());
        form.add("client_secret", fpProperties.getClientSecret());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        try {
            return integrationRestTemplate.postForObject(
                    fpProperties.getTokenUrl(), entity, FpTokenResponse.class);
        } catch (RestClientException e) {
            throw new BusinessException("FP token request failed: " + e.getMessage());
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new BusinessException("Failed to serialize FP request body: " + e.getMessage());
        }
    }
}
