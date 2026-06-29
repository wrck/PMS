package com.dp.plat.erms.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;

import com.dp.plat.pms.extend.fp.model.ElectronicInvoiceModel;
import com.dp.plat.pms.extend.fp.model.ElectronicInvoiceResponse;
import com.dp.plat.pms.extend.fp.model.TokenResponse;
import com.dp.plat.pms.extend.fp.util.FPApi;

import cn.hutool.core.bean.BeanUtil;

public class FPApiTest {

    @InjectMocks
    private FPApi fpApi;

    private MockedStatic<FPApi> mockedStaticApi;

    private ConcurrentHashMap<String, Object> config;
    
    private ElectronicInvoiceModel model;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        config = new ConcurrentHashMap<>();
        config.putAll(JSON.parseObject("{\n" + 
                "  \"debug\": \"true\",\n" + 
                "  \"postByForm\": true,\n" + 
                "  \"authKey\": \"__RequestVerificationToken\",\n" + 
                "  \"authType\": \"header\",\n" + 
                "  \"enableCookie\": \"true\",\n" + 
                "  \"cookieKey\": \"dp.session.id\",\n" + 
                "  \"provider\": \"api\",\n" + 
                "  \"openId\": \"yfPurchase\",\n" + 
                "  \"serviceUrl\": \"http://fp.dptech.com\",\n" + 
                "  \"tokenUrl\": \"/m/oauth.json\",\n" + 
//                "  \"archiveUrl\": \"/m/e-Invoice/api/importInvoice.json\",\n" + 
                "  \"archiveUrl\": \"/invoices/api/CMBFPY/identifyAndVerify.json\",\n" + 
                "}", HashMap.class));
        initApi();
        System.out.println(JSON.toJSONString(FPApi.getConfig(), true));
        
        List<File> files = Arrays.asList(new File("C:\\Users\\user\\Desktop\\临时\\dzfp_25112000000134898848_杭州迪普科技股份有限公司_20250630102820.pdf"), new File("C:\\Users\\user\\Desktop\\临时\\dzfp_25312000000173029300_杭州迪普科技股份有限公司_20250605163943.pdf"));
        
        String dataType = "payment";
        String dataId = "1";
        model = ElectronicInvoiceModel.builder()
                .async(false)
                .files(files.toArray(new File[] {}))
                .dataType("payment")
                .dataId("1")
                .sourceList(Arrays.asList(
                        ElectronicInvoiceModel.builder()
                            .dataType("payment")
                            .dataId("1")
                            .invoiceCode("")
                            .invoiceNumber("25112000000134898848")
                            .build(),
                        ElectronicInvoiceModel.builder()
                            .dataType("payment")
                            .dataId("1")
                            .invoiceCode("")
                            .invoiceNumber("25312000000173029300")
                            .build()
                    )
                )
                .build();
        SerializeFilter filter = new PropertyPreFilter() {
            
            @Override
            public boolean apply(JSONSerializer serializer, Object object, String name) {
                return !"files".equalsIgnoreCase(name);
            }
        };
        model.setJsonData(JSON.toJSONString(model, filter));
//        mockedStaticCrmApi = mockStatic(CrmApi.class);
    }

    @After
    public void tearDown() {
        if (mockedStaticApi != null) {
            mockedStaticApi.close();
        }
    }
    
    @Test
    public void testGetToken() {
        TokenResponse token = FPApi.getToken();
        assertEquals(token.isSuccess(), true);
    }
    
    public static void main(String[] args) {
        String json = "{\r\n" + 
                "  \"name\": 1,\r\n" + 
                "  \"file\": {\r\n" + 
                "    \"@type\": \"com.dp.plat.erms.json.FileWrapper\",\r\n" + 
                "    \"path\": \"C:\\\\Users\\\\user\\\\Desktop\\\\临时\\\\flightTicket.png\"\r\n" + 
                "  },\r\n" + 
                "  \"files\": [\r\n" + 
                "    {\r\n" + 
                "      \"@type\": \"com.dp.plat.erms.json.FileWrapper\",\r\n" + 
                "      \"path\": \"C:\\\\Users\\\\user\\\\Desktop\\\\临时\\\\flightTicket.png\"\r\n" + 
                "    }\r\n" + 
                "  ]\r\n" + 
                "}";
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.setAutoTypeSupport(true);
//        parserConfig.putDeserializer(FileWrapper.class, new FileWrapperDeserializer());
        Map map = JSON.parseObject(json, Map.class, parserConfig);
        System.out.println(map);
    }
    
    @Test
    public void testPostArchive() {
        Map<String, Object> map = BeanUtil.beanToMap(model, false, true);
        ElectronicInvoiceResponse response = FPApi.postElectronicInvoice(model);
        String json = JSON.toJSONString(response);
        System.out.println(json);
        response = JSON.parseObject(json, ElectronicInvoiceResponse.class);
        json = JSON.toJSONString(response);
        System.out.println(json);
        assertEquals(response.isSuccess(), true);
    }

    @Test
    public void testInitConfigFunction_Success() {
        // Arrange
        Function<String, ConcurrentHashMap<String, Object>> mockFunction = new Function<String, ConcurrentHashMap<String, Object>>() {
            @Override
            public ConcurrentHashMap<String, Object> apply(String key) {
                return config;
            }
        };

        // Act
        Map<String, Object> result = FPApi.initConfig(mockFunction, "config");

        // Assert
        assertEquals(config, result);
//        mockedStaticCrmApi.verify(() -> CrmApi.initConfig(any(Function.class), any(String.class)), times(1));
    }

    @Test
    public void testInitConfigSupplier_Success() {
        // Arrange
        Supplier<ConcurrentHashMap<String, Object>> mockFunction = new Supplier<ConcurrentHashMap<String, Object>>() {
            @Override
            public ConcurrentHashMap<String, Object> get() {
                return config;
            }
        };

        // Act
        Map<String, Object> result = FPApi.initConfig(mockFunction);

        // Assert
        assertEquals(config, result);
//        mockedStaticCrmApi.verify(() -> CrmApi.initConfig(any(Supplier.class)), times(1));
    }

    private void initApi() {
        FPApi.initConfig(config);
    }
}
