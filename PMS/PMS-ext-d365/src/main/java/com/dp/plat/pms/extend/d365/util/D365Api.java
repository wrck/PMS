package com.dp.plat.pms.extend.d365.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dp.plat.pms.extend.d365.entity.BaseEntity;
import com.dp.plat.pms.extend.d365.exception.CustomRuntimeException;
import com.dp.plat.pms.extend.d365.model.PurchaseHeader;
import com.dp.plat.pms.extend.d365.model.PurchaseLine;
import com.dp.plat.pms.extend.d365.model.PurchaseReceiptHeader;
import com.dp.plat.pms.extend.d365.model.PurchaseReceiptLine;
import com.dp.plat.pms.extend.d365.model.PurchaseRequestBody;
import com.dp.plat.pms.extend.d365.model.Request;
import com.dp.plat.pms.extend.d365.model.Response;
import com.dp.plat.pms.extend.d365.model.TokenRequest;
import com.dp.plat.pms.extend.d365.model.TokenResponse;
import com.dp.plat.pms.extend.d365.service.IPurchaseLineService;
import com.dp.plat.pms.extend.d365.service.IPurchaseReceiptLineService;
import com.dp.plat.pms.extend.d365.service.IPurchaseReceiptService;
import com.dp.plat.pms.extend.d365.service.IPurchaseService;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;

@Component("d365Api")
public class D365Api {

	// token获取相关参数
	private static String appId;
	private static String clientSecret;
	private static String clientId;
	private static String resource;
	private static String grantType;
	private static String tokenUrl;
	
	// 服务器地址
	private static String serviceUrl;
	private static String createPOUrl;
	private static String receiptPOUrl;

	// 缓存的token请求
	private static volatile TokenResponse cachedToken;
	
	@Autowired
    private IPurchaseService purchaseService;
	
	@Autowired
    private IPurchaseLineService purchaseLineService;
	
	@Autowired
    private IPurchaseReceiptService purchaseReceiptService;
	
	@Autowired
    private IPurchaseReceiptLineService purchaseReceiptLineService;
	
	private static D365Api d365Api;
	
	@PostConstruct
    public void init() {
	    d365Api = this;
	    d365Api.purchaseService = this.purchaseService;
	    d365Api.purchaseLineService = this.purchaseLineService;
	    d365Api.purchaseReceiptService = this.purchaseReceiptService;
	    d365Api.purchaseReceiptLineService = this.purchaseReceiptLineService;
    }

	public D365Api() {
		super();
	}
	
	public D365Api(Map<String, Object> config) {
		super();
		initConfig(config);
	}

	public static void initConfig(Map<String, Object> config) {
		Field[] fields = D365Api.class.getDeclaredFields();
		D365Api api = new D365Api();
		for (Field field : fields) {
		    if (!field.getType().equals(String.class)) {
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
		D365Api.tokenUrl = String.format(D365Api.tokenUrl, appId);
	}

	/**
	 * 获取Token接口
	 * @return
	 */
	public static TokenResponse getToken() {
		if (cachedToken != null) {
		    try {
		        String expiresOn = cachedToken.getExpiresOn();
		        if (StringUtils.isBlank(expiresOn) && cachedToken.getExpiresIn() != null) {
		            long timeInMillis = cachedToken.getTimestamp() != null ? Long.parseLong(cachedToken.getTimestamp()) : Calendar.getInstance().getTimeInMillis();
		            long expiresIn = Long.parseLong(cachedToken.getExpiresIn());
		            expiresOn = String.valueOf(timeInMillis / 1000 + expiresIn);
		            cachedToken.setExpiresOn(expiresOn);
		        }
    			long expiresOnTimeInMillis = Long.parseLong(expiresOn) * 1000;
    			if (expiresOnTimeInMillis >= Calendar.getInstance().getTimeInMillis()) {
    				return cachedToken;
    			}
		    } catch (Exception e) {
		        cachedToken = null;
            }
		}
		TokenRequest request = new TokenRequest();
		request.setResource(resource != null ? resource : serviceUrl);
		request.setClientSecret(clientSecret);
		request.setClientId(clientId);
		request.setGrantType(grantType);

		TokenResponse tokenResponse = postForm(tokenUrl, request, false);
		if (tokenResponse != null  && tokenResponse.getError() == null && tokenResponse.getAccessToken() != null) {
			cachedToken = tokenResponse;
			cachedToken.setTimestamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
		} else {
			cachedToken = null;
		}
		return tokenResponse;
	}

	/**
	 * 创建采购订单接口
	 * @param request
	 * @return
	 */
	public static Response createPurchaseOrder(Request<Response> request) {
		Response response = postBody(serviceUrl + createPOUrl, request);
		return response;
	}
	
	/**
	 * 推送采购订单
	 * @param <T>
	 * @param subcontract
	 * @param dataAreaId
	 * @param purchTable
	 * @param purchLines
	 * @param config
	 * @return
	 */
	public static <T> T pushPurchaseOrder(T subcontract, String dataAreaId, PurchaseHeader purchTable, List<PurchaseLine> purchLines, Map<String, Object> config) {
	    Map<String, Object> result = pushPurchaseOrder(dataAreaId, purchTable, purchLines, config);
        BaseEntity baseEntity = new BaseEntity();
        BeanUtils.copyProperties(subcontract, baseEntity);
        List<Object> purchIds = (List<Object>) baseEntity.getCustomInfoByKey("purchIds", new ArrayList<Object>());
        List<Object> inventTransIds = (List<Object>) baseEntity.getCustomInfoByKey("inventTransIds", new ArrayList<Object>());
        purchIds.addAll((Collection<? extends Object>) result.get("purchIds"));
        inventTransIds.addAll((Collection<? extends Object>) result.get("inventTransIds"));
        baseEntity.setCustomInfoByKey("purchId", result.get("purchId"));
        baseEntity.setCustomInfoByKey("purchIds", purchIds);
        baseEntity.setCustomInfoByKey("inventTransId", result.get("inventTransId"));
        baseEntity.setCustomInfoByKey("inventTransIds", inventTransIds);
        BeanUtils.copyProperties(baseEntity, subcontract);
        return subcontract;
	}
	
	/**
     * 推送采购订单
	 * @param dataAreaId
	 * @param purchTable
	 * @param purchLines
	 * @param config
	 * @return map
	 */
	public static Map<String, Object> pushPurchaseOrder(String dataAreaId, PurchaseHeader purchTable, List<PurchaseLine> purchLines, Map<String, Object> config) {
        // 初始化D365接口配置
        D365Api.initConfig(config);
        Request<Response> request = new Request<Response>();
        PurchaseRequestBody requestBody = new PurchaseRequestBody();
        requestBody.setDataAreaId(dataAreaId);
        request.setRequest(requestBody);
        requestBody.purchTable(purchTable).purchLine(purchLines);
        Response response = D365Api.postBody((String) config.get("createPOUrl"), request);
        System.out.println(response);
        if (!response.isSuccess()) {    
            throw new CustomRuntimeException(StringUtils.defaultIfBlank(response.getMessage(), "接口调用异常！"));
        }

        List<Object> purchIds = new ArrayList<Object>();
        List<Object> inventTransIds = new ArrayList<Object>();
        Map<String, Object> customInfo = new HashMap<String, Object>();

        // 处理推送结果
        List<PurchaseRequestBody> dataList = JSON.parseArray(JSON.toJSONString(response.getData()), PurchaseRequestBody.class);
        for (PurchaseRequestBody responseBody : dataList) {
            PurchaseHeader header = responseBody.getPurchTable();
            String purchId = header.getPurchId();
            purchTable.setPurchId(purchId);
            d365Api.purchaseService.insertSelective(purchTable);
            purchIds.add(purchId);

            Integer headerId = purchTable.getId();
            List<PurchaseLine> lines = responseBody.getPurchLine();
            // 回填采购订单号和头ID
            String inventTransId = null;
            for (PurchaseLine poLine : purchLines) {
                // 回填采购订单号和头ID
                poLine.setHeaderId(headerId);
                poLine.setPurchId(purchId);
                
                // 根据lineNum回填inventTransId
                for (PurchaseLine line : lines) {
                    String lineNum = line.getLineNum();
                    inventTransId = line.getInventTransId();
                    // lineNum相同，则回填inventTransId退出循环
                    if (poLine.getLineNum().equals(lineNum)) {
                        poLine.setInventTransId(inventTransId);
                        break;
                    }
                }
                
                d365Api.purchaseLineService.insertSelective(poLine);
                inventTransIds.add(poLine.getInventTransId());
            }
            
            customInfo.put("purchId", purchId);
            customInfo.put("purchIds", purchIds);
            customInfo.put("inventTransId", inventTransId);
            customInfo.put("inventTransIds", inventTransIds);
        }
        return customInfo;
    }
	
	/**
	 * 创建采购订单收货接口
	 * @param request
	 * @return
	 */
	public static Response receiptPurchaseOrder(Request<Response> request) {
		Response response = postBody(serviceUrl + receiptPOUrl, request);
		return response;
	}
	
	/**
	 * 推送采购订单收货
	 * @param <T>
	 * @param subcontract
	 * @param dataAreaId
	 * @param receipt
	 * @param receiptLines
	 * @param config
	 * @return subcontract
	 */
	public static <T> T pushPurchaseReceipt(T subcontract, String dataAreaId, PurchaseReceiptHeader receipt, List<PurchaseReceiptLine> receiptLines, Map<String, Object> config) {
	    Map<String, Object> result = pushPurchaseReceipt(dataAreaId, receipt, receiptLines, config);
        BaseEntity baseEntity = new BaseEntity();
        BeanUtils.copyProperties(subcontract, baseEntity);
        List<Object> purchIds = (List<Object>) baseEntity.getCustomInfoByKey("purchIds", new ArrayList<Object>());
        List<Object> inventTransIds = (List<Object>) baseEntity.getCustomInfoByKey("inventTransIds", new ArrayList<Object>());
        purchIds.addAll((Collection<? extends Object>) result.get("purchIds"));
        inventTransIds.addAll((Collection<? extends Object>) result.get("inventTransIds"));
        baseEntity.setCustomInfoByKey("packingSlipId", receipt.getPackingSlipId());
        baseEntity.setCustomInfoByKey("purchId", result.get("purchId"));
        baseEntity.setCustomInfoByKey("purchIds", purchIds);
        baseEntity.setCustomInfoByKey("inventTransId", result.get("inventTransId"));
        baseEntity.setCustomInfoByKey("inventTransIds", inventTransIds);
        BeanUtils.copyProperties(baseEntity, subcontract);
        return subcontract;
	}
	
	/**
     * 推送采购订单收货
     * @param subcontract
     * @param dataAreaId
     * @param receipt
     * @param receiptLines
     * @param config
     * @return map
     */
	public static Map<String, Object> pushPurchaseReceipt(String dataAreaId, PurchaseReceiptHeader receipt, List<PurchaseReceiptLine> receiptLines, Map<String, Object> config) {
        D365Api.initConfig(config);
        Request<Response> request = new Request<Response>();
        PurchaseReceiptHeader requestBody = receipt;
        requestBody.setDataAreaId(dataAreaId);
        request.setRequest(requestBody);
        requestBody.setLines(receiptLines);
        Response response = D365Api.postBody((String) config.get("receiptPOUrl"), request);
        if (!response.isSuccess()) {
            throw new CustomRuntimeException(StringUtils.defaultIfBlank(response.getMessage(), "接口调用异常！"));
        }
        
        String purchId = receipt.getPurchId();
        String inventTransId = null;
        
        List<Object> purchIds = new ArrayList<Object>();
        List<Object> inventTransIds = new ArrayList<Object>();
        Map<String, Object> customInfo = new HashMap<String, Object>();
        // 处理推送结果
        List<PurchaseReceiptHeader> dataList = JSON.parseArray(JSON.toJSONString(response.getData()), PurchaseReceiptHeader.class);
        for (PurchaseReceiptHeader header : dataList) {
            d365Api.purchaseReceiptService.insertSelective(receipt);
            purchIds.add(purchId);

            Integer headerId = receipt.getId();
            List<PurchaseReceiptLine> lines = header.getLines();
            // 回填采购订单号和头ID
            for (PurchaseReceiptLine poLine : receiptLines) {
                // 回填采购订单号和头ID
                poLine.setReceiptId(headerId);
                poLine.setPurchId(purchId);
                
                inventTransId = poLine.getInventTransId();
                // 根据lineNum回填inventTransId
                for (PurchaseReceiptLine line : lines) {
                    String inventTransIdTemp = line.getInventTransId();
                    // lineNum相同，则回填需要回填数据的部分退出循环
                    if (poLine.getInventTransId().equals(inventTransIdTemp)) {
                        // 需要回填的数据，预留
                        break;
                    }
                }
                
                d365Api.purchaseReceiptLineService.insertSelective(poLine);
                inventTransIds.add(poLine.getInventTransId());
            }
            
            customInfo.put("purchId", purchId);
            customInfo.put("purchIds", purchIds);
            customInfo.put("inventTransId", inventTransId);
            customInfo.put("inventTransIds", inventTransIds);
        }
        return customInfo;
    }
	
	/**
	 * 推送合同收款计划的验收交付节点信息
	 * @param dataAreaId
	 * @param contractNo
	 * @param lines
	 * @param config
	 * @return
	 */
	public static Response pushContractAcceptanceDeliveryInfo(String dataAreaId, String contractNo, List<Map<String, Object>> lines, Map<String, Object> config) {
	    D365Api.initConfig(config);
        Request<Response> request = new Request<Response>();
        HashMap<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("dataAreaId", dataAreaId);
        requestBody.put("contract", contractNo);
        requestBody.put("line", lines);
        request.setRequest(requestBody);
        Response response = D365Api.postBody((String) config.get("paymentSchedUrl"), request);
        if (!response.isSuccess()) {
            throw new CustomRuntimeException(StringUtils.defaultIfBlank(response.getMessage(), "接口调用异常！"));
        }
        return response;
	}
	
	/**
     * 填充采购订单的基准单位
	 * @param <T>
     * @param baseEntity
     * @param config
     */
    public static <T> T fillPurchaseUnitBase(T subcontract, Map<String, Object> config) {
        if (subcontract == null) {
            return subcontract;
        }
        BaseEntity baseEntity = new BaseEntity();
        BeanUtils.copyProperties(subcontract, baseEntity);
      
        Integer qtyScale = Integer.valueOf(String.valueOf(config.getOrDefault("qtyScale", "2")));
        Integer priceScale = Integer.valueOf(String.valueOf(config.getOrDefault("priceScale", "2")));
        String purchUnitBase = (String) baseEntity.getCustomInfoByKey("purchUnitBase", config.getOrDefault("purchUnitBase", "price"));
        BigDecimal purchPriceBase = new BigDecimal(String.valueOf(baseEntity.getCustomInfoByKey("purchPriceBase", config.getOrDefault("purchPriceBase", "1.00")))).setScale(priceScale, RoundingMode.HALF_UP);
        BigDecimal purchQtyBase = new BigDecimal(String.valueOf(baseEntity.getCustomInfoByKey("purchQtyBase", config.getOrDefault("purchQtyBase", "1.00")))).setScale(qtyScale, RoundingMode.HALF_UP);
        baseEntity.setCustomInfoByKey("purchUnitBase", purchUnitBase);
        baseEntity.setCustomInfoByKey("purchPriceBase", purchPriceBase);
        baseEntity.setCustomInfoByKey("purchQtyBase", purchQtyBase);
        
        BeanUtils.copyProperties(baseEntity, subcontract);
        return subcontract;
    }
	
	/**
	 * @deprecated 直接在post中进行
	 * @param request
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
	 * 以表单的形式post发送请求，默认需要Auth认证
	 * @param <T>
	 * @param url
	 * @param params
	 * @return
	 */
	public static <T> T postForm(String url, Request<T> params) {
		return postForm(url, params, true);
	}
	
	/**
	 * 以form的形式post发送请求，指定是否需要Auth认证
	 * @param <T>
	 * @param url
	 * @param params
	 * @return
	 */
	public static <T> T postForm(String url, Request<T> params, boolean needAuth) {
		return post(url, params, true, needAuth);
	}
	
	/**
	 * 以body的形式post发送json字符串请求，默认需要Auth认证
	 * @param <T>
	 * @param url
	 * @param params
	 * @return
	 */
	public static <T> T postBody(String url, Request<T> params) {
		return postBody(url, params, true);
	}

	/**
	 * 以body的形式post发送json字符串请求，指定是否需要Auth认证
	 * @param <T>
	 * @param url
	 * @param params
	 * @return
	 */
	public static <T> T postBody(String url, Request<T> params, boolean needAuth) {
		return post(url, params, false, needAuth);
	}
	
	/**
	 * 以body或者form的形式post发送请求，指定是否需要Auth认证
	 * @param <T>
	 * @param url
	 * @param request
	 * @param isForm
	 * @param needAuth
	 * @return
	 */
	public static <T> T post(String url, Request<T> request, boolean isForm, boolean needAuth) {
		if (request == null) {
			request = new Request<T>();
		}
		Type responseType = request.getResponseType();
		if (url == null || url.length() == 0) {
			return JSON.parseObject("{}", responseType);
		}
	    URI uri = URLUtil.toURI(url);
	    if (uri.getHost() == null) {
	    	url = serviceUrl + url;
	    }
	    System.out.println(url);
		HttpRequest httpRequest = HttpUtil.createPost(url);
		Map<String, String> headers = request.getHeaders();
		if (headers != null && !headers.isEmpty()) {
			httpRequest.headerMap(headers, true);
		}
		// 是否需要认证，如果需要正则，则请求token，在请求头中进行设置
		if (needAuth) {
			TokenResponse token = getToken();
			if (token.getTokenType() != null) {
				StringBuilder authorization = new StringBuilder(token.getTokenType());
				authorization.append(" ").append(token.getAccessToken());
				httpRequest.header("Authorization", authorization.toString());
			}
		}
		System.out.println(request);
		// 表单提交
		if (isForm) {
		    System.out.println(toJSONMap(request));
			httpRequest.form(toJSONMap(request));
		} else {
		    System.out.println(toJSONString(request));
			httpRequest.body(toJSONString(request));
		}
		String body = httpRequest.execute().body();
		System.out.println(body);
		T response = JSON.parseObject(body, responseType);
		if (response == null) {
		    return JSON.parseObject("{}", responseType);
		}
		return response;
	}
	
	/**
	 * 转化按属性顺序的json map，默认fastjson会按字母进行排序
	 * @param object
	 * @return
	 */
	public static Map<String, Object> toJSONMap(Object object) {
		if (object == null) {
			return null;
		}
		
		String json = toJSONString(object);
		LinkedHashMap<String, Object> map = JSON.parseObject(json , new TypeReference<LinkedHashMap<String, Object>>() {}, Feature.OrderedField);
		return map;
	}
	
	/**
	 * 转化按属性顺序的json字符串，默认fastjson会按字母进行排序
	 * @param object
	 * @return
	 */
	public static String toJSONString(Object object) {
		if (object == null) {
			return "null";
		}
		Class<? extends Object> clazz = object.getClass();
		int features = JSON.DEFAULT_GENERATE_FEATURE & ~SerializerFeature.SortField.getMask();
		SerializeConfig serializeConfig = new SerializeConfig(true);
		serializeConfig.config(clazz, SerializerFeature.SortField, false);
		serializeConfig.config(clazz, SerializerFeature.MapSortField, false);
		String json = JSON.toJSONString(object, serializeConfig, null, null, features);
		return json;
	}
	
	public static void main(String[] args) {
		Map<String, Object> config = new HashMap<String, Object>();
		config.put("serviceUrl", "https://usnconeboxax1aos.cloud.onebox.dynamics.com");
		config.put("tokenUrl", "https://login.microsoftonline.com/%s/oauth2/token");
		config.put("appId", "1402f304-d45a-48fa-8ad7-920a9acd8800");
		config.put("clientSecret", "F-58Q~ZZ.qmLzJC-cL_4ziMYPa40TboDdluRZaH-");
		config.put("clientId", "69d7585c-1665-4013-a8fe-08c9eff4f287");
		config.put("grantType", "client_credentials");
		System.out.println(JSON.toJSONString(config));
		String configStr = "{\r\n" + 
        "  \"enablePushPurchaseOrder\": true,\r\n" + 
        "  \"appId\": \"1402f304-d45a-48fa-8ad7-920a9acd8800\",\r\n" + 
        "  \"clientId\": \"69d7585c-1665-4013-a8fe-08c9eff4f287\",\r\n" + 
        "  \"clientSecret\": \"F-58Q~ZZ.qmLzJC-cL_4ziMYPa40TboDdluRZaH-\",\r\n" + 
        "  \"grantType\": \"client_credentials\",\r\n" + 
        "  \"tokenUrl\": \"https://login.microsoftonline.com/%s/oauth2/token\",\r\n" + 
        "  \"serviceUrl\": \"https://usnconeboxax1aos.cloud.onebox.dynamics.com\",\r\n" + 
        "  \"createPOUrl\": \"/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchTable/create\",\r\n" + 
        "  \"receiptPOUrl\": \"/api/services/IWS_InterfaceInboundServiceGroup/CreatePurchPackingSlip/create\",\r\n" + 
        "  \"purchPoolId\": \"安服转包\",\r\n" + 
        "  \"itemId\": \"S0000001\",\r\n" + 
        "  \"sysTag\": \"PMS2#\",\r\n" + 
        "  \"inventSiteId\": \"S1\",\r\n" + 
        "  \"inventLocationId\": \"088\"\r\n" + 
        "}";
		config = JSON.parseObject(configStr, HashMap.class);
		D365Api.initConfig(config);
		TokenResponse token = D365Api.getToken();
		System.out.println(token);
		token = D365Api.getToken();
		System.out.println(token.getAccessToken());
		
		Request<Response> request = new Request<Response>();
		PurchaseRequestBody body = new PurchaseRequestBody();
		PurchaseHeader purchTable = JSON.parseObject("{\r\n" + 
				"            \"vendAccount\": \"V00003\",\r\n" + 
				"            \"inventLocationId\": \"088\",\r\n" + 
				"            \"purchName\": \"带称干经\",\r\n" + 
				"            \"deliveryDate\": \"2022-06-23\",\r\n" + 
				"            \"projectName\": \"济支下\",\r\n" + 
				"            \"otherSysNum\": \"58\",\r\n" + 
				"            \"purContract\": \"ea veniam dolore officia\",\r\n" + 
				"            \"salesContract\": \"consectetur\",\r\n" + 
				"            \"contractAmount\": 11,\r\n" + 
				"            \"subcontractType\": \"adipisicing\",\r\n" + 
				"            \"subcontStartDate\": \"2011-09-23\",\r\n" + 
				"            \"subcontEndDate\": \"1981-06-27\",\r\n" + 
				"            \"projectProgress\": \"occaecat consectetur\",\r\n" + 
				"            \"purchPoolId\": \"安服转包\",\r\n" + 
				"            \"workerPurchPlacer\": \"commodo Excepteur aute\",\r\n" + 
				"            \"applicant\": \"tempor veniam\",\r\n" + 
				"            \"remark\": \"dolore esse\",\r\n" + 
				"            \"payment\": \"laboris do magna\",\r\n" + 
				"            \"paymMode\": \"sunt minim ea\",\r\n" + 
				"            \"dlvMode\": \"ad minim magna sunt est\",\r\n" + 
				"            \"dlvTerm\": \"sed Excepteur deserunt magna cupidatat\"\r\n" + 
				"        }", PurchaseHeader.class);
		List<PurchaseLine> purchLines = JSON.parseArray("[\r\n" + 
				"            {\r\n" + 
				"                \"lineNum\": \"78\",\r\n" + 
				"                \"inventLocationId\": \"088\",\r\n" + 
				"                \"itemId\": \"T0000001\",\r\n" + 
				"                \"purchQty\": 100,\r\n" + 
				"                \"purchPrice\": 5235.83,\r\n" + 
				"                \"taxItemGroup\": \"cillum\",\r\n" + 
				"                \"inventSerialId\": \"63\",\r\n" + 
				"                \"officeCode\": \"59\",\r\n" + 
				"                \"multiDimID\": \"94\",\r\n" + 
				"                \"deliveryDate\": \"2022-06-23\",\r\n" + 
				"                \"remark\": \"enim\",\r\n" + 
				"                \"investmentProject\": \"esse fugiat irure Lorem\",\r\n" + 
				"                \"dimDepartment\": \"\",\r\n" + 
				"                \"dimBankAccount\": \"\",\r\n" + 
				"                \"dimCustomer\": \"\",\r\n" + 
				"                \"dimVendor\": \"\",\r\n" + 
				"                \"dimEmployee\": \"\",\r\n" + 
				"                \"dimContract\": \"\",\r\n" + 
				"                \"dimBU\": \"\",\r\n" + 
				"                \"dimProductLine\": \"\",\r\n" + 
				"                \"dimTerritory\": \"\",\r\n" + 
				"                \"dimIndustry\": \"\",\r\n" + 
				"                \"dimMultiDimID\": \"16\"\r\n" + 
				"            }\r\n" + 
				"        ]", PurchaseLine.class);
		body.dataAreaId("DPGF").purchTable(purchTable).purchLine(purchLines);
		request.setRequest(body);
		Response response = D365Api.createPurchaseOrder(request);
		System.out.println(response);
		List<Map<String, Object>> data = response.getData();
		if (data != null) {
			Object purchId = data.get(0).get("PurchId");
			purchTable.setPurchId(String.valueOf(purchId));
		}
		
		PurchaseReceiptHeader receiptHeader = JSON.parseObject("{\r\n" + 
				"        \"dataAreaId\": \"DPGF\",\r\n" + 
				"        \"deliveryDate\": \"2022-06-23\",\r\n" + 
				"        \"documentDate\": \"2005-04-09 21:50:12\",\r\n" + 
				"        \"packingSlipId\": \"PO000330_1\",\r\n" + 
				"        \"packingSlipRemark\": \"aliqua qui ex\",\r\n" + 
				"        \"projectProgress\": \"et exercitation\",\r\n" + 
				"        \"lines\": [\r\n" + 
				"            {\r\n" + 
				"                \"inventSiteId\": \"S1\",\r\n" + 
				"                \"inventLocationId\": \"088\",\r\n" + 
				"                \"inventTransId\": \"\",\r\n" + 
				"                \"lineNum\": 1,\r\n" + 
				"                \"purchId\": \"PO000329\",\r\n" + 
				"                \"qty\": 16.57,\r\n" + 
				"                \"wmsLocationId\": \"\"\r\n" + 
				"            }\r\n" + 
				"        ]\r\n" + 
				"    }", PurchaseReceiptHeader.class);
		request.setRequest(receiptHeader);
		String purchId = purchTable.getPurchId();
		receiptHeader.packingSlipId(purchId + "_" + new Date().getTime());
		List<PurchaseReceiptLine> lines = receiptHeader.getLines();
		for (PurchaseReceiptLine purchaseReceiptLine : lines) {
			purchaseReceiptLine.setPurchId(purchId);
		}
		System.out.println(request);
		response = D365Api.receiptPurchaseOrder(request);
		System.out.println(response);
	}
	
	public static void main2(String[] args) {
        String json = "[{\r\n" + 
                "        \"purchTable\": {\r\n" + 
                "            \"PurchId\": \"PO000433\"\r\n" + 
                "        },\r\n" + 
                "        \"purchLine\": [\r\n" + 
                "            {\r\n" + 
                "                \"lineNum\": \"78\",\r\n" + 
                "                \"inventTransId\": \"DPGF-003674\"\r\n" + 
                "            }\r\n" + 
                "        ]\r\n" + 
                "    }]";
        List<PurchaseRequestBody> list = JSON.parseArray(json, PurchaseRequestBody.class);
        System.out.println(list);
    }
}
