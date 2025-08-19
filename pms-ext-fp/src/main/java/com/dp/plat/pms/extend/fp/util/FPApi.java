package com.dp.plat.pms.extend.fp.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ws.rs.HttpMethod;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import com.dp.plat.pms.extend.fp.model.ElectronicInvoiceModel;
import com.dp.plat.pms.extend.fp.model.ElectronicInvoiceResponse;
import com.dp.plat.pms.extend.fp.model.Request;
import com.dp.plat.pms.extend.fp.model.Response;
import com.dp.plat.pms.extend.fp.model.TokenRequest;
import com.dp.plat.pms.extend.fp.model.TokenResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * @author w02611
 */
@Component("fpApi")
public class FPApi implements DisposableBean {
    public static final String SYS_FP_API = "sys.fp.api";
    public static final String MINUTE = "MINUTE";
    public static final String SINGLE = "SINGLE";
    public static final TypeReference<Map<String, Object>> DEFAULT_TYPE = new TypeReference<Map<String, Object>>() {};
    static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new CustomizableThreadFactory("FPApi-ScheduledPool-Thread-"));
    private static final Logger logger = LoggerFactory.getLogger(FPApi.class);//日志
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    // token获取相关参数
    private static String authType;
    private static String authKey;
    private static String authValue;
    private static String enableCookie;
    private static String cookieKey;
    private static String cookieValue;
    private static String appId;
    private static String clientSecret;
    private static String clientId;
    private static String resource;
    private static String grantType;

    // 服务器地址
    private static String serviceUrl;
    private static String tokenUrl;
    private static String archiveUrl;
    private static String ssoUrl;

    // 缓存的token请求
    private static volatile TokenResponse cachedToken;

    private static FPApi FPApi;
    private static volatile ConcurrentHashMap<String, Object> config = new ConcurrentHashMap<>();
    private static volatile Supplier<ConcurrentHashMap<String, Object>> configSupplier;
    private static volatile Function<String, ConcurrentHashMap<String, Object>> configFunction;
    private static volatile String configKey;

    public FPApi() {
        super();
    }

    public FPApi(Map<String, Object> config) {
        this();
        initConfig(config);
    }

    /**
     * 获取在途接口配置
     *
     * @return
     */
    public static Map<String, Object> getConfig() {
        if (configSupplier != null) {
            config = configSupplier.get();
        } else if (configFunction != null) {
            config = configFunction.apply(configKey);
        }
        return config;
    }

    /**
     * 定义 initConfig 方法
     */
    private static Map<String, Object> initConfig() {
        return initConfig(config);
    }

    /**
     * 定义 initConfig 方法，接收一个 Function 作为参数
     */
    public static Map<String, Object> initConfig(Supplier<ConcurrentHashMap<String, Object>> configSupplier) {
        FPApi.configSupplier = configSupplier;
        FPApi.configFunction = null;
        FPApi.configKey = null;
        FPApi.config.clear();
        return initConfig();
    }

    /**
     * 定义 initConfig 方法，接收一个 Function 作为参数
     */
    public static Map<String, Object> initConfig(Function<String, ConcurrentHashMap<String, Object>> configFunction, String key) {
        FPApi.configFunction = configFunction;
        FPApi.configKey = key;
        FPApi.configSupplier = null;
        FPApi.config.clear();
        return initConfig();
    }

    /**
     * 初始化配置
     *
     * @param config
     *
     * @return
     */
    public static Map<String, Object> initConfig(Map<String, Object> config) {
        if (config == null || config.isEmpty()) {
            config = getConfig();
        } else if (config.equals(FPApi.config)) {
            // 相同对象不重复初始化
            return config;
        } else {
            FPApi.config.putAll(config);
        }
        Field[] fields = FPApi.class.getDeclaredFields();

        FPApi api = FPApi.FPApi;
        if (api == null) {
            api = new FPApi();
        }
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!field.getType().equals(String.class) || Modifier.isFinal(modifiers) || Modifier.isVolatile(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }
            String name = field.getName();
            Object value = config.get(name);
            try {
                field.setAccessible(true);
                field.set(api, value);
            } catch (Exception e) {
            }
        }
        try {
            FPApi.tokenUrl = String.format(FPApi.tokenUrl, appId);
        } catch (Exception e) {
        }
        clearToken();
        return config;
    }

    /**
     * 构造一个集合包裹元素，如果本身是Collection则addAll
     * @param target
     * @return
     */
    public static Collection<Object> wrapCollection(Object target) {
        // 处理字段关系
        Collection<Object> list = new ArrayList<Object>();
        if (target instanceof Collection) {
            list.addAll((Collection<?>) target);
        } else if (target != null) {
            list.add(target);
        }
        return list;
    }

    /**
     * 获取Token接口
     *
     * @return
     */
    public static TokenResponse getToken() {
        try {
            lock.readLock().lock();
        
            if (cachedToken != null) {
                try {
                    String expiresOn = cachedToken.getExpiresOn();
                    if (StringUtils.isBlank(expiresOn)) {
                        if (cachedToken.getExpiresIn() != null) {
                            long timeInMillis = cachedToken.getTimestamp() != null ? Long.parseLong(cachedToken.getTimestamp()) : Calendar.getInstance().getTimeInMillis();
                            long expiresIn = Long.parseLong(cachedToken.getExpiresIn());
                            expiresOn = String.valueOf(timeInMillis / 1000 + expiresIn);
                            cachedToken.setExpiresOn(expiresOn);
                        } else if (cachedToken.getExpireTime() != null) {
                            Date expireTime = cachedToken.getExpireTime() != null ? DateUtil.parseDateTime(cachedToken.getExpireTime()).toJdkDate() : Calendar.getInstance().getTime();
                            expiresOn = String.valueOf(expireTime.getTime() / 1000);
                            cachedToken.setExpiresOn(expiresOn);
                        } else {
                            long timeInMillis = cachedToken.getTimestamp() != null ? Long.parseLong(cachedToken.getTimestamp()) : Calendar.getInstance().getTimeInMillis();
                            long expiresIn = 1800;
                            expiresOn = String.valueOf(timeInMillis / 1000 + expiresIn);
                            cachedToken.setExpiresOn(expiresOn);
                        }
                    }
                    long expiresOnTimeInMillis = Long.parseLong(expiresOn) * 1000;
                    if (expiresOnTimeInMillis >= Calendar.getInstance().getTimeInMillis()) {
                        return cachedToken;
                    }
                } catch (Exception e) {
                    clearToken();
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        boolean locked = lock.writeLock().tryLock();
        if (!locked) {
            return getToken();
        }
        try {
            clearToken();
            
            TokenRequest request = new TokenRequest();
            request.oauthType(MapUtil.getStr(config, "provider"));
            request.code(MapUtil.getStr(config, "openId"));
            request.nickName(MapUtil.getStr(config, "nickName"));
    
            TokenResponse tokenResponse = get(tokenUrl, request, false);
            if (tokenResponse != null && tokenResponse.getError() == null && tokenResponse.getAccessToken() != null) {
                cachedToken = tokenResponse;
                cachedToken.setTimestamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                if (Boolean.parseBoolean(enableCookie) && StringUtils.isNotBlank(cookieKey)) {
                    cookieValue = getCookieValue(tokenResponse, cookieKey);
                }
            } else {
                clearToken();
            }
            return tokenResponse;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 清除
     */
    private static void clearToken() {
        boolean locked = lock.writeLock().tryLock();
        if (!locked) {
            return;
        }
        try {
            cachedToken = null;
            cookieValue = null;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 批量推送电子档案
     *
     * @return
     */
    public static List<Response<ElectronicInvoiceModel>> postElectronicInvoice(String dataType, String dataId, List<File> files, List<Object> sourceList, Map<String, Object> config) {
       if (files == null || files.isEmpty()) {
           return Collections.emptyList();
       }
        List<ElectronicInvoiceModel> list = new ArrayList<>(files.size());
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            if (file == null || !file.isFile() || !file.exists()) {
                continue;
            }
            Object sourceMap = (sourceList.isEmpty() ? Collections.emptyMap() : sourceList.get(Math.min(i, sourceList.size() - 1)));
            
            ElectronicInvoiceModel source = JSON.parseObject(toJSONString(sourceMap), ElectronicInvoiceModel.class);
//            source.setDataType(dataType);
//            source.setDataId(dataId);
            
            ElectronicInvoiceModel model = ElectronicInvoiceModel
                    .builder()
                    .async(false)
                    .files(new File[] { file })
                    .dataType(dataType)
                    .dataId(dataId)
                    .sourceList(Arrays.asList(source))
                    .build();
            model.setJsonData(toJSONString(model));
            list.add(model);
        }
        return postElectronicInvoice(list, config);
    }

    /**
     * 批量推送电子档案
     *
     * @return
     */
    public static <T> ElectronicInvoiceResponse postElectronicInvoice(T data) {
        return postElectronicInvoice(data, null);
    }

    /**
     * 批量推送电子档案
     * @return
     */
    public static <T> ElectronicInvoiceResponse postElectronicInvoice(T data, Map<String, Object> config) {
        config = FPApi.initConfig(config);
        Map<String, Object> options = new HashMap<>();
        options.put("responseType", ElectronicInvoiceResponse.class);
        options.put("headers", Collections.singletonMap("Content-Type", "multipart/form-data"));
        return (ElectronicInvoiceResponse) pushSingleData(data, archiveUrl, config, options);
    }

    /**
     * 批量推送电子档案
     *
     * @return
     */
    public static <T> List<Response<T>> postElectronicInvoice(List<T> list) {
        return postElectronicInvoice(list, null);
    }

    /**
     * 批量推送电子档案
     *
     * @return
     */
    public static <T> List<Response<T>> postElectronicInvoice(List<T> list, Map<String, Object> config) {
        config = FPApi.initConfig(config);
        Map<String, Object> options = new HashMap<>();
        options.put("responseType", ElectronicInvoiceResponse.class);
        options.put("headers", Collections.singletonMap("Content-Type", "multipart/form-data"));
        return pushSingleData(list, archiveUrl, 30, config, MINUTE, options);
    }

    /**
     * 推送在途列表数据，请求参数为List
     *
     * @return
     */
    public static <T> List<Response<T>> pushListData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, Map<String, Object> options) {
        return pushListData(list, syncUrl, rateLimit, config, SINGLE, options);
    }

    /**
     * 推送在途列表数据，请求参数为List
     *
     * @return
     */
    public static <T> List<Response<T>> pushListData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, String limitType, Map<String, Object> options) {
        return pushData(list, syncUrl, rateLimit, config, limitType, true, options);
    }

    /**
     * 推送在途数据，请求参数为单个Data
     *
     * @return
     */
    public static <T> List<Response<T>> pushSingleData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, String limitType, Map<String, Object> options) {
        return pushData(list, syncUrl, rateLimit, config, limitType, false, options);
    }

    /**
     * 推送在途数据，请求参数为单个Data
     *
     * @return
     */
    public static <T> List<Response<T>> pushData(List<T> list, String syncUrl, Integer rateLimit, Map<String, Object> config, String limitType, boolean splitToList, Map<String, Object> options) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        // 判断是否需要拆分成多个数组
        List<Object> dataList = new ArrayList<Object>();
        if (splitToList) {
            // 根据接口同步次数限制拆分成若干个List
            //            List<List<T>> lists = Stream.iterate(0, i -> i + rateLimit)
            //                    .limit((list.size() + rateLimit - 1) / rateLimit)
            //                    .map(i -> list.subList(i, Math.min(i + rateLimit, list.size())))
            //                    .collect(Collectors.toList());
            List<List<T>> lists = new ArrayList<>();
            int listSize = list.size();
            for (int i = 0; i < listSize; i += rateLimit) {
                int endIndex = Math.min(i + rateLimit, listSize);
                List<T> subList = list.subList(i, endIndex);
                lists.add(subList);
            }
            dataList.addAll(lists);
        } else {
            dataList.addAll(list);
        }

        List<Response<T>> responseList = new ArrayList<>(dataList.size());

        // 单次请求限制
        switch (limitType) {
        case MINUTE: {
            // 如果是List提交，这默认按1分钟进行处理
            int delay = 1;
            TimeUnit timeUnit = TimeUnit.MINUTES;
            // 如果不是list，需要单个提交，则按秒计算时间
            if (!splitToList) {
                delay = 60 / rateLimit;
                timeUnit = TimeUnit.SECONDS;
            }
            // 通过单位时间请求限制的方式，通过调度发送请求
            responseList.addAll(schedulePushData(dataList, syncUrl, config, delay, timeUnit, options));
            break;
        }
        case SINGLE:
        default: {
            // 按单个请求数量限制的方式发送请求
            for (Object data : dataList) {
                responseList.add(pushData(data, syncUrl, config, options));
            }
        }
        }
        return responseList;
    }

    /**
     * 推送列表数据，请求参数为List
     *
     * @return
     */
    private static <T> Response<T> pushListData(List<T> list, String syncUrl, Map<String, Object> config) {
        return pushListData(list, syncUrl, config, null);
    }
    
    /**
     * 推送列表数据，请求参数为List
     *
     * @return
     */
    private static <T> Response<T> pushListData(List<T> list, String syncUrl, Map<String, Object> config, Map<String, Object> options) {
        return pushData(list, syncUrl, config, options);
    }

    /**
     * 推送单个数据
     *
     * @param data
     * @param syncUrl
     * @param config
     * @param <T>
     *
     * @return
     */
    private static <T> Response<T> pushSingleData(T data, String syncUrl, Map<String, Object> config) {
        return pushSingleData(data, syncUrl, config, null);
    }
    
    /**
     * 推送单个数据
     *
     * @param data
     * @param syncUrl
     * @param config
     * @param <T>
     *
     * @return
     */
    private static <T> Response<T> pushSingleData(T data, String syncUrl, Map<String, Object> config, Map<String, Object> options) {
        return pushData(data, syncUrl, config, options);
    }
    
    /**
     * 推送单个数据
     *
     * @param data
     * @param syncUrl
     * @param config
     * @param <T>
     *
     * @return
     */
    private static <T> Response<T> pushData(Object data, String syncUrl, Map<String, Object> config) {
        return pushData(data, syncUrl, config, null);
    }

    /**
     * 推送单个数据
     *
     * @param data
     * @param syncUrl
     * @param config
     * @param <T>
     *
     * @return
     */
    private static <T> Response<T> pushData(Object data, String syncUrl, Map<String, Object> config, Map<String, Object> options) {
        if (data == null) {
            return new Response<T>();
        }
        
        if (options == null) {
            options = Collections.emptyMap();
        }
        
        Type responseType = MapUtil.get(options, "responseType", Type.class);
        Map<String, String> headers = MapUtil.get(options, "headers", Map.class);
        Request<Response<T>> request = new Request<Response<T>>();
        request.setResponseType(responseType);
        request.setHeaders(headers);
        request.setRequest(data);
        
        Boolean isForm = MapUtil.getBool(options, "postByForm", MapUtil.getBool(config, "postByForm", false));
        Response<T> response = null;
        if (isForm) {
            response = postForm(syncUrl, request, true, options);
        } else {
            response = postBody(syncUrl, request, true, options);
        }
        response.setRequest(request);
        return response;
    }

    /**
     * 周期推送数据
     *
     * @param list
     * @param syncUrl
     * @param config
     * @param delay
     * @param timeUnit
     * @param <T>
     *
     * @return
     */
    private static <T> List<Response<T>> schedulePushData(List<Object> list, String syncUrl, Map<String, Object> config, int delay, TimeUnit timeUnit, Map<String, Object> options) {
        delay = delay == 0 ? 1 : Math.abs(delay);
        timeUnit = timeUnit == null ? TimeUnit.MINUTES : timeUnit;

        ConcurrentLinkedQueue<Object> linkedQueue = new ConcurrentLinkedQueue<Object>(list);
        CountDownLatch countDownLatch = new CountDownLatch(linkedQueue.size());
        List<Response<T>> responseList = new ArrayList<>(list.size());
        // 每分钟发送一次请求
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                // 每个List发送请求
                try {
                    Object data = linkedQueue.poll();
                    if (data != null) {
                        responseList.add(pushData(data, syncUrl, config, options));
                    }
                } catch (Exception e) {
                    // 处理异常，例如记录日志或抛出自定义异常
                    throw new RuntimeException("Error pushing data", e);
                } finally {
                    countDownLatch.countDown();
                }
            }
        }, 0, delay, timeUnit);
        try {
            // 延迟时长*请求次数，等于总延迟时长，*2放大倍数得出一个超时时长
            long timeout = delay * list.size() * 20;
            // 转化为秒
            long seconds = timeUnit.toSeconds(timeout);
            // 如果超时时间至少延迟30秒
            timeout = timeUnit.convert(Math.max(seconds, 30), timeUnit);
            // 等待所有请求任务都发送完成
            countDownLatch.await(timeout, timeUnit);
        } catch (Exception e) {
            // 处理异常，例如记录日志或抛出自定义异常
            throw new RuntimeException("Error pushing data", e);
        } finally {
            linkedQueue.clear();
            scheduledFuture.cancel(true);
        }
        return responseList;
    }

    /**
     * @param request
     *
     * @deprecated 直接在post中进行
     */
    @Deprecated
    public static void initAuthorization(Request<?> request) {
        TokenResponse token = getToken();
        if (token.getAccessToken() != null) {
            Map<String, String> headers = new HashMap<String, String>(1);
            StringBuilder authorization = new StringBuilder(token.getTokenType());
            authorization.append(" ").append(token.getAccessToken());
            headers.put("Authorization", authorization.toString());
            request.setHeaders(headers);
        }
    }

    /**
     * 推送采购订单
     *
     * @param dataAreaId
     * @param travelPlan
     * @param travelItems
     * @param config
     *
     * @return map
     */
    //    public static Map<String, Object> pushTravelPlan(String dataAreaId, RequestBody travelPlan, List<RequestBody> travelItems, Map<String, Object> config) {
    //        // 初始化ZTrip接口配置
    //        config = ErmsApi.initConfig(config);
    //        Request<Response> request = new Request<Response>();
    //        RequestBody requestBody = new RequestBody();
    //        request.setRequest(requestBody);
    //        List<Traveller> travellers;
    //        requestBody.put("travelItems", travelItems)
    //                .put("travellers", travellers);
    //        Response response = ErmsApi.postBody((String) config.get("createPOUrl"), request);
    //        System.out.println(response);
    //        if (!response.isSuccess()) {
    //            throw new CustomRuntimeException(StringUtils.defaultIfBlank(response.getMessage(), "接口调用异常！"));
    //        }
    //
    //        List<Object> purchIds = new ArrayList<Object>();
    //        List<Object> inventTransIds = new ArrayList<Object>();
    //        Map<String, Object> customInfo = new HashMap<String, Object>();
    //
    //        // 处理推送结果
    //        List<PurchaseRequestBody> dataList = JSON.parseArray(JSON.toJSONString(response.getData()), PurchaseRequestBody.class);
    //        for (PurchaseRequestBody responseBody : dataList) {
    //            PurchaseHeader header = responseBody.getPurchTable();
    //            String purchId = header.getPurchId();
    //            purchTable.setPurchId(purchId);
    //            ErmsApi.purchaseService.insertSelective(purchTable);
    //            purchIds.add(purchId);
    //
    //            Integer headerId = purchTable.getId();
    //            List<PurchaseLine> lines = responseBody.getPurchLine();
    //            // 回填采购订单号和头ID
    //            String inventTransId = null;
    //            for (PurchaseLine poLine : purchLines) {
    //                // 回填采购订单号和头ID
    //                poLine.setHeaderId(headerId);
    //                poLine.setPurchId(purchId);
    //
    //                // 根据lineNum回填inventTransId
    //                for (PurchaseLine line : lines) {
    //                    String lineNum = line.getLineNum();
    //                    inventTransId = line.getInventTransId();
    //                    // lineNum相同，则回填inventTransId退出循环
    //                    if (poLine.getLineNum().equals(lineNum)) {
    //                        poLine.setInventTransId(inventTransId);
    //                        break;
    //                    }
    //                }
    //
    //                ErmsApi.purchaseLineService.insertSelective(poLine);
    //                inventTransIds.add(poLine.getInventTransId());
    //            }
    //
    //            customInfo.put("purchId", purchId);
    //            customInfo.put("purchIds", purchIds);
    //            customInfo.put("inventTransId", inventTransId);
    //            customInfo.put("inventTransIds", inventTransIds);
    //        }
    //        return customInfo;
    //    }

    /**
     * 设置认证参数
     *
     * @param httpRequest
     */
    public static void initAuthorization(HttpRequest httpRequest) {
        TokenResponse token = getToken();
        if (Boolean.parseBoolean(enableCookie)) {
            httpRequest.disableCookie();
        }
        if (token != null) {
            String accessToken = token.getAccessToken();
            if (StringUtils.isNotBlank(accessToken)) {
                if ("bearer".equalsIgnoreCase(authType)) {
                    httpRequest.form(authKey, accessToken);
                } else if ("header".equalsIgnoreCase(authType)) {
                    httpRequest.header(authKey, accessToken);
                } else if ("query".equalsIgnoreCase(authType)) {
                    httpRequest.header(authKey, accessToken);
                } else if ("cookie".equalsIgnoreCase(authType)) {
                    httpRequest.cookie(new HttpCookie(authKey, StringUtils.defaultIfBlank(authValue, accessToken)));
                }
                if (Boolean.parseBoolean(enableCookie) && !StringUtils.equalsIgnoreCase(cookieKey, authKey)) {
                    httpRequest.cookie(new HttpCookie(cookieKey, StringUtils.defaultIfBlank(cookieValue, accessToken)));
                }
            }
        }
    }

    /**
     * 设置认证参数
     *
     * @param url
     * @param uri
     *
     * @return
     */
    public static String initAuthorization(String url, URI uri) {
        TokenResponse token = getToken();
        if ("query".equalsIgnoreCase(authType)) {
            String accessToken = token.getAccessToken();
            if (StringUtils.isNotBlank(accessToken)) {
                if (uri.getQuery() == null || uri.getQuery().isEmpty()) {
                    url = url + "?" + authKey + "=" + accessToken;
                } else {
                    url = url + "&" + authKey + "=" + accessToken;
                }
            }
        }
        return url;
    }
    
    /**
     * 设置认证参数
     *
     * @param httpRequest
     */
    public static void initAuthorization(HttpRequestBase httpRequest) {
        TokenResponse token = getToken();
        if (token.getTokenType() != null) {
            httpRequest.addHeader(authKey, token.getAccessToken());
        }
        if (token != null) {
            try {
                String accessToken = token.getAccessToken();
                if (StringUtils.isNotBlank(accessToken)) {
                    if ("bearer".equalsIgnoreCase(authType)) {
                        httpRequest.setHeader(authKey, accessToken);
                    } else if ("header".equalsIgnoreCase(authType)) {
                        httpRequest.setHeader(authKey, accessToken);
                    } else if ("query".equalsIgnoreCase(authType)) {
                        URI originalUri = httpRequest.getURI();
                        URIBuilder uriBuilder = new URIBuilder(originalUri);
                        uriBuilder.addParameter(authKey, accessToken);
                        httpRequest.setURI(uriBuilder.build());
                    } else if ("cookie".equalsIgnoreCase(authType)) {
                        String existing = httpRequest.getFirstHeader("Cookie") != null ? httpRequest.getFirstHeader("Cookie").getValue() : null;
                        String newCookie = initCookie(existing, authKey, accessToken);
                        httpRequest.setHeader("Cookie", newCookie);
                    }
                    if (Boolean.parseBoolean(enableCookie) && !StringUtils.equalsIgnoreCase(cookieKey, authKey)) {
                        String existing = httpRequest.getFirstHeader("Cookie") != null ? httpRequest.getFirstHeader("Cookie").getValue() : null;
                        String newCookie = initCookie(existing, cookieKey, StringUtils.defaultIfBlank(cookieValue, accessToken));
                        httpRequest.setHeader("Cookie", newCookie);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void initAuthorization(okhttp3.Request.Builder requestBuilder) {
        TokenResponse token = getToken();
        if (token.getTokenType() != null) {
            requestBuilder.addHeader(authKey, token.getAccessToken());
        }
        if (token != null) {
            try {
                String accessToken = token.getAccessToken();
                if (StringUtils.isNotBlank(accessToken)) {
                    if ("bearer".equalsIgnoreCase(authType)) {
                        requestBuilder.header(authKey, accessToken);
                    } else if ("header".equalsIgnoreCase(authType)) {
                        requestBuilder.header(authKey, accessToken);
                    } else if ("query".equalsIgnoreCase(authType)) {
                        HttpUrl originalUri = requestBuilder.getUrl$okhttp();
                        URI uri = originalUri.uri();
                        HttpUrl.Builder uriBuilder = HttpUrl.parse(uri.toString()).newBuilder();
                        uriBuilder.addQueryParameter(authKey, accessToken);
                        requestBuilder.url(uriBuilder.build());
                    } else if ("cookie".equalsIgnoreCase(authType)) {
                        String existing = requestBuilder.build().header("Cookie");
                        String newCookie = initCookie(existing, authKey, accessToken);
                        requestBuilder.header("Cookie", newCookie);
                    }
                    if (Boolean.parseBoolean(enableCookie) && !StringUtils.equalsIgnoreCase(cookieKey, authKey)) {
                        String existing = requestBuilder.build().header("Cookie");
                        String newCookie = initCookie(existing, cookieKey, StringUtils.defaultIfBlank(cookieValue, accessToken));
                        requestBuilder.header("Cookie", newCookie);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加认证信息
     *
     * @param httpRequest
     */
    public static void initApikey(HttpRequest httpRequest) {
        if (StringUtils.isNoneBlank(authKey, appId)) {
            httpRequest.header(authKey, appId);
        }
    }
    
    public static String initCookie(String existingCookie, String key, String value) {
        String newCookie = key + "=" + value;
        if (existingCookie != null && !existingCookie.contains(key + "=")) {
            newCookie = existingCookie + "; " + newCookie;
        }
        return newCookie;
    }
    
    public static String getCookieValue(Response response, String name) {
        if (response == null || response.getHeaders() == null) {
            return null;
        }
        // 获取所有 Set-Cookie 头
        Map<String, List<String>> headers = response.getHeaders();
        List<String> setCookies = headers.getOrDefault("Set-Cookie", Collections.emptyList());

        // 转换为 Map<String, List<String>>
        Map<String, String> cookiesMap = new HashMap<>();
        for (String cookie : setCookies) {
            String[] parts = cookie.split(";", 2);
            if (parts.length > 0) {
                String[] kv = parts[0].split("=", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim();
                    String value = kv[1].trim();
                    cookiesMap.putIfAbsent(key, value);
                }
            }
        }
        return cookiesMap.get(name);
    }

    /**
     * 以表单的形式post发送请求，默认需要Auth认证
     *
     * @param <T>
     * @param url
     * @param params
     *
     * @return
     */
    public static <T extends Response<E>, E> T postForm(String url, Request<T> params) {
        return postForm(url, params, true);
    }

    /**
     * 以form的形式post发送请求，指定是否需要Auth认证
     *
     * @param <T>
     * @param url
     * @param params
     *
     * @return
     */
    public static <T extends Response<E>, E> T postForm(String url, Request<T> params, boolean needAuth) {
        return postForm(url, params, needAuth, null);
    }
    
    /**
     * 以form的形式post发送请求，指定是否需要Auth认证
     *
     * @param <T>
     * @param url
     * @param params
     *
     * @return
     */
    public static <T extends Response<E>, E> T postForm(String url, Request<T> params, boolean needAuth, Map<String, Object> options) {
        return post(url, params, true, needAuth, options);
    }

    /**
     * 以body的形式post发送json字符串请求，默认需要Auth认证
     *
     * @param <T>
     * @param url
     * @param params
     *
     * @return
     */
    public static <T extends Response<E>, E> T postBody(String url, Request<T> params) {
        return postBody(url, params, true, null);
    }
    
    /**
     * 以body的形式post发送json字符串请求，默认需要Auth认证
     *
     * @param <T>
     * @param url
     * @param params
     *
     * @return
     */
    public static <T extends Response<E>, E> T postBody(String url, Request<T> params, Map<String, Object> options) {
        return postBody(url, params, true, options);
    }
    
    /**
     * 以body的形式post发送json字符串请求，指定是否需要Auth认证
     *
     * @param <T>
     * @param url
     * @param params
     *
     * @return
     */
    public static <T extends Response<E>, E> T postBody(String url, Request<T> params, boolean needAuth) {
        return postBody(url, params, needAuth, null);
    }

    /**
     * 以body的形式post发送json字符串请求，指定是否需要Auth认证
     *
     * @param <T>
     * @param url
     * @param params
     *
     * @return
     */
    public static <T extends Response<E>, E> T postBody(String url, Request<T> params, boolean needAuth, Map<String, Object> options) {
        return post(url, params, false, needAuth, options);
    }

    /**
     * 以body或者form的形式post发送请求，指定是否需要Auth认证
     *
     * @param <T>
     * @param url
     * @param request
     * @param isForm
     * @param needAuth
     *
     * @return
     */
    public static <T extends Response<E>, E> T post(String url, Request<T> request, boolean isForm, boolean needAuth) {
        return request(HttpMethod.POST, url, request, isForm, needAuth);
    }
    
    /**
     * 以body或者form的形式post发送请求，指定是否需要Auth认证
     *
     * @param <T>
     * @param url
     * @param request
     * @param isForm
     * @param needAuth
     *
     * @return
     */
    public static <T extends Response<E>, E> T post(String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options) {
        return request(HttpMethod.POST, url, request, isForm, needAuth, options);
    }

    /**
     * 发送GET请求，默认需要Auth认证
     *
     * @param <T>
     * @param url
     * @param request
     *
     * @return
     */
    public static <T extends Response<E>, E> T get(String url, Request<T> request) {
        return get(url, request, true);
    }

    /**
     * 以body或者form的形式post发送请求，指定是否需要Auth认证
     *
     * @param <T>
     * @param url
     * @param request
     * @param needAuth
     *
     * @return
     */
    public static <T extends Response<E>, E> T get(String url, Request<T> request, boolean needAuth) {
        return request(HttpMethod.GET, url, request, true, needAuth);
    }
    
    /**
     * 以body或者form的形式发送请求，指定是否需要Auth认证
     *
     * @param method
     * @param url
     * @param request
     * @param isForm
     * @param needAuth
     * @param <T>
     *
     * @return
     */
    public static <T extends Response<E>, E> T request(String method, String url, Request<T> request, boolean isForm, boolean needAuth) {
        return request(method, url, request, isForm, needAuth, null);
    }
    
    /**
     * 以body或者form的形式发送请求，指定是否需要Auth认证
     *
     * @param method      HTTP 方法（GET/POST等）
     * @param url         请求URL（可为相对路径）
     * @param request     请求对象（包含 headers、body 等）
     * @param isForm      是否以表单形式提交
     * @param needAuth    是否需要认证（会调用 initAuthorization）
     * @param <T>         响应类型，继承 Response<E>
     * @param <E>         响应数据泛型
     * @return            解析后的响应对象
     */
    public static <T extends Response<E>, E> T request(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options) {
//        return requestWithHutool(method, url, request, isForm, needAuth, options);
//        return requestWithPool(method, url, request, isForm, needAuth, options);
        return requestWithOkHttp(method, url, request, isForm, needAuth, options);
    }

    /**
     * 以body或者form的形式发送请求，指定是否需要Auth认证
     *
     * @param method      HTTP 方法（GET/POST等）
     * @param url         请求URL（可为相对路径）
     * @param request     请求对象（包含 headers、body 等）
     * @param isForm      是否以表单形式提交
     * @param needAuth    是否需要认证（会调用 initAuthorization）
     * @param <T>         响应类型，继承 Response<E>
     * @param <E>         响应数据泛型
     * @return            解析后的响应对象
     */
    public static <T extends Response<E>, E> T requestWithHutool(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options) {
        if (request == null) {
            request = new Request<T>();
        }
        Type responseType = request.getResponseType();
        T response;

        // 处理空 URL
        if (url == null || url.length() == 0) {
            response = JSON.parseObject("{}", responseType);
            response.setMessage("没有指定URL！");
            return response;
        }

        // 初始化参数，防止部分方法忘记初始化
        Map<String, Object> config = initConfig();

        // 构建完整 URL
        URI uri = URLUtil.toURI(url);
        if (uri.getHost() == null) {
            url = serviceUrl + url;
        }

        // 认证：URL 参数方式
        if (needAuth) {
            url = initAuthorization(url, uri);
        }

        logInfo("请求URL: {}", url);

        HttpRequest httpRequest = null;
        HttpResponse httpResponse = null;
        String body = null;

        try {
            // 创建请求
            if (HttpMethod.GET.equals(method)) {
                httpRequest = HttpUtil.createGet(url);
            } else {
                httpRequest = HttpUtil.createPost(url);
            }

            // 设置超时（避免无限等待）
            httpRequest.setConnectionTimeout(10_000); // 10秒连接超时

            // 添加 headers
            Map<String, String> headers = MapUtil.get(options, "headers", Map.class, request.getHeaders());
            if (headers != null && !headers.isEmpty()) {
                httpRequest.headerMap(headers, true);
            }

            // 认证：Header 方式
            if (needAuth) {
                initAuthorization(httpRequest);
            }

            log("请求：{}", request);

            // 构建请求体
            Object requestData = request.getRequest() != null ? request.getRequest() : request;
            if (isForm) {
                Map<String, Object> requestForm = beanToMap(requestData);
                log("表单数据: {}", requestForm);
                httpRequest.form(requestForm);
            } else {
                String requestJson = toJSONString(requestData);
                log("请求体: {}", requestJson);
                httpRequest.body(requestJson);
            }

            // 执行请求（自动使用 HttpURLConnection）
            httpResponse = httpRequest.execute();
            body = httpResponse.body();

            log("响应体: {}", body);

            // 解析响应
            response = JSON.parseObject(body, responseType);
            if (response == null) {
                response = retryRequest(method, url, request, isForm, needAuth, options);
            }
            response.setHeaders(httpResponse.headers());
        } catch (Exception e) {
            log("请求异常: {}", e.getMessage());
            response = retryRequest(method, url, request, isForm, needAuth, options);
            if (response == null) {
                response = JSON.parseObject("{}", responseType);
                if (!JSON.isValid(body)) {
                    response.setMessage(String.format("响应内容不是Json格式！%s", StringUtils.substring(body, 0, 255)));
                } else {
                    response.setMessage(String.format("反序列化发生异常！错误信息：%s", e.getMessage()));
                }
            }
        } finally {
            // 确保连接释放（尤其是 Keep-Alive 连接）
            if (httpResponse != null) {
                httpResponse.close();
            }
        }

        return response;
    }
    
    /**
     * 使用连接池发送请求（高性能版本)
     *
     * @param method      HTTP 方法（GET/POST等）
     * @param url         请求URL（可为相对路径）
     * @param request     请求对象（包含 headers、body 等）
     * @param isForm      是否以表单形式提交
     * @param needAuth    是否需要认证（会调用 initAuthorization）
     * @param <T>         响应类型，继承 Response<E>
     * @param <E>         响应数据泛型
     * @return            解析后的响应对象
     */
    public static <T extends Response<E>, E> T requestWithPool(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options) {
        if (request == null) {
            request = new Request<>();
        }
        Type responseType = request.getResponseType();
        T response;

        // 处理空 URL
        if (url == null || url.length() == 0) {
            response = JSON.parseObject("{}", responseType);
            response.setMessage("没有指定URL！");
            return response;
        }
        
        // 初始化参数，防止部分方法忘记初始化
        Map<String, Object> config = initConfig();

        // 构建完整 URL
        URI uri = URLUtil.toURI(url);
        if (uri.getHost() == null) {
            url = serviceUrl + url;
        }

        // 认证：URL 参数方式
        if (needAuth) {
            url = initAuthorization(url, uri);
        }

        logInfo("【连接池】请求URL: {}", url);

        CloseableHttpClient httpClient = HttpClientPool.getHttpClient();
        HttpRequestBase httpRequest = null;
        String body = null;

        Object requestData = request.getRequest() != null ? request.getRequest() : request;
        try {
            // 构建请求
            switch (method) {
                case HttpMethod.GET:
                    URIBuilder uriBuilder = new URIBuilder(url);
                    Map<String, Object> params = beanToMap(requestData);
                    log("【连接池】请求参数: {}", params);
                    if (params != null) {
                        for (Map.Entry<String, Object> entry : params.entrySet()) {
                            if (entry.getValue() != null) {
                                uriBuilder.addParameter(entry.getKey(), entry.getValue().toString());
                            }
                        }
                    }

                    try {
                        URI finalUri = uriBuilder.build();
                        httpRequest = new HttpGet(finalUri);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("URL 构建失败", e);
                    }
                    break;
                case HttpMethod.POST:
                    httpRequest = new HttpPost(url);
                    break;
                case HttpMethod.PUT:
                    httpRequest = new HttpPut(url);
                    break;
                case HttpMethod.DELETE:
                    httpRequest = new HttpDelete(url);
                    break;
                default:
                    throw new IllegalArgumentException("不支持的HTTP方法: " + method);
            }

            // 设置超时
            // 注意：HttpClient 超时需通过 RequestConfig 设置（可扩展）
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(10_000)
//                    .setConnectionRequestTimeout(5_000)
//                    .setSocketTimeout(20_000)
                    .build();
            httpRequest.setConfig(requestConfig);

            // 添加 headers
            Map<String, String> headers = request.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    httpRequest.setHeader(entry.getKey(), entry.getValue());
                }
            }

            // 认证：Header 方式
            if (needAuth) {
                initAuthorization(httpRequest); // 新增方法，见下方
            }
            
            log("【连接池】请求：{}", request);

            // 设置请求体（POST/PUT）
            if (httpRequest instanceof HttpEntityEnclosingRequest) {
                HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) httpRequest;
                if (isForm) {
                    Map<String, Object> form = beanToMap(requestData);
                    log("【连接池】表单数据: {}", form);
                    HttpEntity formEntity = new MultipartBodyBuilder().form(form).buildHttp().build();
                    entityRequest.setEntity(formEntity);
                    httpRequest.setHeader(formEntity.getContentType().getName(), formEntity.getContentType().getValue());
                } else {
                    String json = toJSONString(requestData);
                    log("【连接池】请求体: {}", json);
                    StringEntity entity = new StringEntity(json, "UTF-8");
                    entity.setContentType("application/json");
                    entityRequest.setEntity(entity);
                }
            }

            // 执行请求
            CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            HttpEntity entity = httpResponse.getEntity();
            body = EntityUtils.toString(entity, "UTF-8");

            log("【连接池】响应体: {}", body);

            // 解析响应
            response = JSON.parseObject(body, responseType);
            if (response == null) {
                response = retryRequest(method, url, request, isForm, needAuth, options);
            }
            
            Map<String, List<String>> headerMap = new HashMap<>();
            for (org.apache.http.Header header : httpResponse.getAllHeaders()) {
                String key = header.getName();
                String value = header.getValue();
                headerMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
            response.setHeaders(headerMap);
        } catch (Exception e) {
            log("【连接池】请求异常: {}", e.getMessage());
            response = retryRequest(method, url, request, isForm, needAuth, options);
            if (response == null) {
                response = JSON.parseObject("{}", responseType);
                if (!JSON.isValid(body)) {
                    response.setMessage(String.format("响应内容不是Json格式！%s", StringUtils.substring(body, 0, 255)));
                } else {
                    response.setMessage(String.format("反序列化发生异常！错误信息：%s", e.getMessage()));
                }
            }
        } finally {
            // 连接由连接池自动管理，无需手动 close httpClient
            if (httpRequest != null) {
                httpRequest.releaseConnection(); // 释放连接回池
            }
        }

        return response;
    }

    /**
     * 使用 OkHttp 发送请求（高性能、简洁、支持 HTTP/2）
     */
    public static <T extends Response<E>, E> T requestWithOkHttp(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options) {
        if (request == null) {
            request = new Request<>();
        }
        Type responseType = request.getResponseType();
        T response;

        // 处理空 URL
        if (url == null || url.length() == 0) {
            response = JSON.parseObject("{}", responseType);
            response.setMessage("没有指定URL！");
            return response;
        }
        
        // 初始化参数，防止部分方法忘记初始化
        Map<String, Object> config = initConfig();

        // 构建完整 URL
        URI uri = URLUtil.toURI(url);
        if (uri.getHost() == null) {
            url = serviceUrl + url;
        }

        // 认证：URL 参数方式
        if (needAuth) {
            url = initAuthorization(url, uri);
        }

        logInfo("【OkHttp】请求URL: {}", url);

        OkHttpClient client = OkHttpPool.get();
        okhttp3.Request okRequest = null;
        String body = null;

        try {
            okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(url);

            // 添加 headers
            Map<String, String> headers = request.getHeaders();
            if (headers != null && !headers.isEmpty()) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.header(entry.getKey(), entry.getValue());
                }
            }

            // 认证：Header
            if (needAuth) {
                initAuthorization(requestBuilder);
            }
            
            log("【OkHttp】请求：{}", request);

            // 构建请求体
            Object requestData = request.getRequest() != null ? request.getRequest() : request;
            if (HttpMethod.GET.equals(method)) {
                // GET 无请求体把参数拼接到 URL 上
                Map<String, Object> params = beanToMap(requestData);
                log("【OkHttp】请求参数: {}", params);
                HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
                if (params != null) {
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        Object value = entry.getValue();
                        if (value != null) {
                            urlBuilder.addQueryParameter(entry.getKey(), value.toString());
                        }
                    }
                }
                requestBuilder.url(urlBuilder.build());
            } else if (isForm) {
                Map<String, Object> form = beanToMap(requestData);
                log("【OkHttp】表单数据: {}", form);
                MultipartBody.Builder multipartBuilder = new MultipartBodyBuilder().form(form).buildOkHttp();
                RequestBody formBody = multipartBuilder.build();
                requestBuilder.method(method, formBody);
            } else {
                String json = toJSONString(requestData);
                log("【OkHttp】请求体: {}", json);
                RequestBody jsonBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
                requestBuilder.method(method, jsonBody);
            }

            // 构建最终请求
            okRequest = requestBuilder.build();

            // 执行请求
            try (okhttp3.Response okResponse = client.newCall(okRequest).execute()) {
//                if (!okResponse.isSuccessful()) {
//                    throw new IOException("请求失败: " + okResponse.code());
//                }

                body = okResponse.body().string();
                log("【OkHttp】响应体: {}", body);

                // 解析响应
                response = JSON.parseObject(body, responseType);
                if (response == null) {
                    response = retryRequest(method, url, request, isForm, needAuth, options);
                }
                response.setHeaders(okResponse.headers().toMultimap());
            }
        } catch (Exception e) {
            log("【OkHttp】请求异常: {}", e.getMessage());
            response = retryRequest(method, url, request, isForm, needAuth, options);
            if (response == null) {
                response = JSON.parseObject("{}", responseType);
                if (!JSON.isValid(body)) {
                    response.setMessage(String.format("响应内容不是Json格式！%s", StringUtils.substring(body, 0, 255)));
                } else {
                    response.setMessage(String.format("反序列化发生异常！错误信息：%s", e.getMessage()));
                }
            }
        }

        return response;
    }
    
    /**
     * 尝试重新请求
     * @param <T>
     * @param method
     * @param url
     * @param request
     * @param isForm
     * @param needAuth
     * @param options
     * @param responseType
     * @param config
     * @return
     */
    public static <T extends Response<E>, E> T retryRequest(String method, String url, Request<T> request, boolean isForm, boolean needAuth, Map<String, Object> options) {
        log("尝试重新请求: {}", url);
        Type responseType = request.getResponseType();
        T response;
        
        // 初始化参数，防止部分方法忘记初始化
        Map<String, Object> config = initConfig();
        
        // 清除缓存
        clearToken();
        
        Boolean enableRetry = MapUtil.getBool(config, "enableRetry", false);
        Boolean retried = MapUtil.getBool(options, "retried", false);
        if (enableRetry && !retried) {
            options = new HashMap<String, Object>(options != null ? options : Collections.emptyMap());
            options.put("retried", true);
            return request(method, url, request, isForm, needAuth, options);
        }
        response = JSON.parseObject("{}", responseType, Feature.SupportAutoType);
        response.setMessage("响应内容为空！");
        return response;
    }

    /**
     * 转化按属性顺序的json map，默认fastjson会按字母进行排序
     *
     * @param object
     *
     * @return
     */
    public static Map<String, Object> beanToMap(Object object) {
        return BeanUtil.beanToMap(object, false, true);
    }

    /**
     * 转化按属性顺序的json map，默认fastjson会按字母进行排序
     *
     * @param object
     *
     * @return
     */
    public static Map toJSONMap(Object object) {
        if (object == null) {
            return null;
        }

        String json = toJSONString(object);
        Map map = JSON.parseObject(json, DEFAULT_TYPE, Feature.OrderedField);
        return map;
    }

    /**
     * 转化按属性顺序的json字符串，默认fastjson会按字母进行排序
     *
     * @param object
     * @param serializerFeatures
     *
     * @return
     */
    public static String toJSONString(Object object, SerializerFeature... serializerFeatures) {
        if (object == null) {
            return "null";
        }

        Class<? extends Object> clazz = object.getClass();
        int features = JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.SortField.getMask() | SerializerFeature.IgnoreNonFieldGetter.getMask();

        SerializeConfig serializeConfig = new SerializeConfig(true);
        serializeConfig.config(clazz, SerializerFeature.SortField, false);
        serializeConfig.config(clazz, SerializerFeature.MapSortField, false);
        serializeConfig.config(clazz, SerializerFeature.WriteDateUseDateFormat, true);
        serializeConfig.config(clazz, SerializerFeature.IgnoreNonFieldGetter, true);
        serializeConfig.config(clazz, SerializerFeature.WriteMapNullValue, false);

        for (SerializerFeature feature : serializerFeatures) {
            features |= feature.getMask();
        }

        String json = JSON.toJSONString(object, serializeConfig, null, JSON.DEFFAULT_DATE_FORMAT, features);
        return json;
    }

    /**
     * 清理和验证输入值
     * 如果输入值为null，返回空字符串
     * 目前该方法尚未实现任何清理逻辑
     * TODO: 实现适当的清理和验证逻辑，例如转义特殊字符
     *
     * @param value 待清理和验证的字符串
     *
     * @return 清理和验证后的字符串，或空字符串（如果输入为null）
     */
    private static String sanitizeValue(Object value) {
        // 检查输入值是否为null
        if (value == null) {
            // 如果输入值为null，返回空字符串
            return "";
        }
        if (value instanceof String) {
            return value.toString();
        }
        // TODO: 进行适当的清理和验证，例如转义特殊字符
        // 注意：此处的代码已被注释掉，需要根据实际需求实现适当的清理逻辑
        // return value.replaceAll("[\\'\";]", "");
        // 直接返回输入值的字符串表示，目前未进行任何清理
        return JSON.toJSONString(value);
    }


    /**
     * 检查字符串是否包含中文字符
     *
     * @param value 需要检查的字符串
     *
     * @return 如果字符串包含中文字符返回 true，否则返回 false
     */
    private static boolean containsChinese(String value) {
        return value.matches(".*[\u4e00-\u9fa5].*");
        //      for (char c : value.toCharArray()) {
        //          if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
        //              return true;
        //          }
        //      }
        //      return false;
    }

    /**
     * 过滤集合，去除null或者空字符串
     * @param <T>
     * @param collection
     * @return
     */
    public static <T> Collection<T> filterCollection(Collection<T> collection) {
        if (collection == null) {
            return Collections.emptyList();
        }

        return collection.stream()
                .filter(new Predicate<T>() {
                    @Override
                    public boolean test(T o) {
                        return o != null && !(o instanceof String && StringUtils.isBlank((String) o));
                    }
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 
     * @param format
     * @param arguments
     */
    public static void logInfo(String format, Object... arguments) {
        log(format, false, arguments);
    }
    
    /**
     * 
     * @param format
     * @param arguments
     */
    public static void logDebug(String format, Object... arguments) {
        log(format, true, arguments);
    }

    /**
     *
     * @param format
     * @param arguments
     */
    public static void log(String format, Object... arguments) {
        Map<String, Object> config = getConfig();
        boolean debug = Boolean.parseBoolean(String.valueOf(config.getOrDefault("debug", false)));
        if (debug) {
            logger.debug(format, arguments);
        }
    }
    
    /**
     * 
     * @param format
     * @param arguments
     */
    public static void log(String format, boolean isDebug, Object... arguments) {
        Map<String, Object> config = getConfig();
        boolean debug = Boolean.parseBoolean(String.valueOf(config.getOrDefault("debug", true)));
        if (debug && isDebug) {
            logger.debug(format, arguments);
        } else if (!isDebug){
            logger.info(format, arguments);
        }
    }

    @PostConstruct
    public void init() {
        FPApi = this;
    }

    @Override
    public void destroy() throws Exception {
        try {
            FPApi = null;
            scheduler.shutdownNow();
            HttpClientPool.close();
            OkHttpPool.close();
        } catch (Exception e) {
            logger.error("回收线程池异常", e);
        }
    }
    
    /**
     * HTTP 连接池管理类（单例）
     */
    public static class HttpClientPool {
        private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        private static volatile CloseableHttpClient httpClient;

        private HttpClientPool() {}

        public static CloseableHttpClient getHttpClient() {
            if (httpClient == null) {
                synchronized (HttpClientPool.class) {
                    if (httpClient == null) {
                        Map<String, Object> config = getConfig();
                        // 创建连接池
                        connManager.setMaxTotal(MapUtil.getInt(config, "httpMaxTotal", 100));           // 最大连接数
                        connManager.setDefaultMaxPerRoute(MapUtil.getInt(config, "httpDefaultMaxPerRoute", 20));  // 每个域名最大连接数

                        // 构建 HttpClient
                        httpClient = HttpClients.custom()
                                .setConnectionManager(connManager)
                                .setConnectionManagerShared(true) // 允许多线程共享
                                .build();
                    }
                }
            }
            return httpClient;
        }

        /**
         * 关闭连接池（应用关闭时调用）
         */
        public static void close() {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * OkHttp 连接池管理类（单例）
     */
    public static class OkHttpPool {
        private static volatile OkHttpClient httpClient;

        public static OkHttpClient get() {
            if (httpClient == null) {
                synchronized (HttpClientPool.class) {
                    if (httpClient == null) {
                        Map<String, Object> config = getConfig();
                        // 构建 HttpClient
                        httpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .connectionPool(new ConnectionPool(MapUtil.getInt(config, "httpMaxTotal", 100), 5L, TimeUnit.MINUTES)) // 核心：连接池
                        .build();
                    }
                }
            }
            return httpClient;
        }

        // 应用关闭时调用（可选，JVM 会自动清理）
        public static void close() {
            if (httpClient != null) {
                httpClient.dispatcher().executorService().shutdown();
                httpClient.connectionPool().evictAll();
            }
        }
    }
}
