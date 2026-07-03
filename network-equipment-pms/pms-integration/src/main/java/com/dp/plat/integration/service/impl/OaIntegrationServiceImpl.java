package com.dp.plat.integration.service.impl;

import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.integration.config.OaProperties;
import com.dp.plat.integration.constant.IntegrationConstants;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link OaIntegrationService}.
 *
 * <p>Reuses the same OAuth2 token caching pattern (ConcurrentHashMap +
 * ahead-of-expiry refresh + double-checked locking) as
 * {@link D365IntegrationServiceImpl} and {@link FpIntegrationServiceImpl}.
 * All outbound calls are recorded in {@link IntegrationLog} for retry.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OaIntegrationServiceImpl implements OaIntegrationService {

    /** Refresh the token this many seconds before it actually expires. */
    private static final long TOKEN_SKEW_SECONDS = 60L;

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
        String url = oaProperties.getBaseUrl() + "/todos";
        String body = writeJson(request);
        IntegrationLog logRecord = IntegrationLog.builder()
                .logType(IntegrationConstants.LOG_TYPE_OA)
                .businessType(IntegrationConstants.BIZ_OA_TODO_PUSH)
                .businessId(request.getProcessInstanceId())
                .requestUrl(url)
                .requestBody(body)
                .build();
        logRecord = integrationLogService.log(logRecord);
        return executePost(logRecord, url, body);
    }

    @Override
    public boolean completeTodo(String taskId) {
        String url = oaProperties.getBaseUrl() + "/todos/" + taskId + "/complete";
        IntegrationLog logRecord = IntegrationLog.builder()
                .logType(IntegrationConstants.LOG_TYPE_OA)
                .businessType(IntegrationConstants.BIZ_OA_TODO_COMPLETE)
                .businessId(taskId)
                .requestUrl(url)
                .requestBody("{}")
                .build();
        logRecord = integrationLogService.log(logRecord);
        return executePost(logRecord, url, "{}");
    }

    private boolean executePost(IntegrationLog logRecord, String url, String body) {
        try {
            String token = getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = integrationRestTemplate.postForEntity(url, entity, String.class);
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
}
