package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;

import com.alibaba.fastjson.JSON;

public class ColumnRenderJSTest {

    public void render(String renderFunction, Object data, String type, Object row, Map<String, Object> options) throws ScriptException, NoSuchMethodException {
        // 创建并初始化 ScriptEngineManager 和 ScriptEngine
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine jsEngine = engineManager.getEngineByName("nashorn");

        // 创建自定义 console 对象
        Bindings bindings = jsEngine.createBindings();
        bindings.put("console", new Object() {
            public void log(Object... messages) {
                for (Object message : messages) {
                    System.out.println(message);
                }
            }
        });

        // 设置全局绑定
        jsEngine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);

        // 将函数定义添加到引擎中
        jsEngine.eval("var render = " + renderFunction);

        // 添加其他需要的变量到引擎
        jsEngine.put("options", options);
        jsEngine.put("row", row); // 确保 row 是一个有效的 JSON 字符串或 Map
        jsEngine.put("data", data);

        // 如果引擎是 Invocable 类型，则可以调用 JavaScript 函数
        if (jsEngine instanceof Invocable) {
            Invocable invocable = (Invocable) jsEngine;
            Object jsOptions = ((ScriptEngine) invocable).get("options");
            Object renderValue = null;
            try {
                renderValue = invocable.invokeFunction("render", null, type, row, jsOptions);
                if (renderValue == null) {
                    renderValue = invocable.invokeFunction("render", data, type, row, jsOptions);
                }
            } catch (Exception e) {
                renderValue = invocable.invokeFunction("render", data, type, row, jsOptions);
            }
            renderValue = ObjectUtils.defaultIfNull(renderValue, data);
            System.out.println(renderValue);
        }
    }
    
    public static void main(String[] args) {
        try {
            // 定义 JavaScript 函数
            String render = "(function(data, type, row, options) {\r\n" +
                    "  print(data, type, row, options); \r\n" +
                    "  if (!data) {\r\n" +
                    "        var col = options.col;\r\n" +
                    "        var column = options.settings.aoColumns[col];\r\n" +
                    "        var alias = column.data;\r\n" +
                    "  print(col, column, alias); \r\n" +
                    "        if (alias) {\r\n" +
                    "            var keys = alias.split(' ');\r\n" +
                    "  print(col, column, alias, keys); \r\n" +
                    "            var values = [];\r\n" +
                    "            for (var i in keys) {\r\n" +
                    "                var key = keys[i];\r\n" +
                    "  print(key); \r\n" +
                    "                try {\r\n" +
                    "                    var value = eval('row.' + key);\r\n" +
                    "  print('row.' + key, value); \r\n" +
                    "                    if (value) {\r\n" +
                    "                        values.push(value);\r\n" +
                    "                    }\r\n" +
                    "                } catch (e) {" +
                    "  print(e); \r\n" +
                    "                }\r\n" +
                    "            }\r\n" +
                    "            data = values.join('，') || data;\r\n" +
                    "        }\r\n" +
                    "    }\r\n" +
                    "    return data;\r\n" +
                    "})";
            Object row = "{\"id\":321,\"createBy\":\"w02332\",\"createTime\":\"2023-11-01 14:23:21\",\"updateBy\":\"x04354\",\"updateTime\":\"2023-11-07 20:02:09\",\"orgId\":1,\"customInfo\":{\"documentDate\":\"2023-11-07 20:02:08\",\"smsProjectAmount\":\"465609.64\",\"packingSlipId\":\"PA073117_01\",\"collectedAmount\":465609.64,\"relateSource\":\"D365\",\"invoiceAmount\":\"133100.000000\",\"orgId\":1,\"purchIds\":[\"PA073117\"],\"contractNos\":\"61020230919A92\",\"inventTransIds\":[\"11354861\"],\"fileIds\":\"468\",\"invoiceNumber\":\"23312000000111175356\",\"smsProjectCode\":\"16202523091203N\",\"approvedAmount\":\"133100\",\"deliveryDate\":\"2023-11-07 20:02:08\",\"approveState\":\"审批通过\",\"deliveredAmount\":\"465,609.64\",\"collectedRatio\":100,\"settledRatio\":100,\"paystate\":\"已付款\",\"invoiceDate\":\"2023-11-08 00:00:00\",\"inventTransId\":\"11354861\",\"updateName\":\"谢菁璐\",\"contractRatio\":\"100.00\",\"settledAmount\":133100,\"smsProjectName\":\"广东农信信息科技外包框架协议安全保障类-2023年一二季度人月结算\",\"frameworkAgreementMemo\":\"1.安全咨询类服务项目金额大于等于10万，按照3:6:1进行付款，项目金额小于10万无预付款。\\n2.安全运维服务项目周期1年，按照合理合情的原则，背靠背支付。\\n3.特殊项目，特殊申请。\",\"paid\":1,\"purchId\":\"PA073117\",\"paidState\":\"已付款\",\"contractAmount\":\"465,609.64\",\"deliveredRatio\":\"100.00\",\"invoiceId\":\"23312000000111175356\",\"smsOrderExecNumber\":\"1620252309191X303\",\"paidAmount\":\"133100.00\",\"smsSubmitTime\":\"2023-09-12 00:00:00\",\"createName\":\"吴迪锋\",\"innerInvoiceId\":\"PI004662\"},\"settleSeq\":\"2023-01-04-广东农信信息科技外包框架协议安全保障类-2023年一二季度人月结算-100-133100.00\",\"dispatchId\":503,\"dispatchSeq\":\"2023-01-04\",\"progressDesc\":\"100%\",\"progressRatio\":null,\"acceptanceDesc\":\"已完成\",\"acceptanceRatio\":null,\"ratio\":\"100\",\"amount\":\"133100.00\",\"memo\":\"本次付100%全款\",\"confirmTime\":\"2023-11-07 20:02:08\",\"paymentTime\":\"2023-11-10 14:13:08\",\"remark\":null,\"state\":50,\"sseId\":5637218450,\"year\":2023,\"quarter\":4,\"month\":11,\"disabled\":false,\"settled\":true,\"projectId\":null,\"officeCodes\":null,\"projectTypes\":null,\"memberCode\":null,\"smsProjectCode\":null,\"smsSubmitTime\":null,\"smsProjectAmount\":null,\"smsProjectName\":null,\"smsOrderExecNumber\":\"1620252309191X303\",\"contractNos\":null,\"collectedAmount\":\"465,609.64\",\"deliveredAmount\":\"465,609.64\",\"contractAmount\":\"465,609.64\",\"settledAmount\":\"133,100.00\",\"collectedRatio\":\"100.00%\",\"settledRatio\":\"100.00%\",\"dispatch\":{\"id\":503,\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"orgId\":1,\"customInfo\":{\"serviceType\":\"运维类\",\"officeName\":\"广州办事处\",\"dimIndustry\":\"\",\"orgId\":1,\"purchIds\":[\"PA073117\"],\"inventTransIds\":[\"11354861\"],\"profitOfficeName\":\"安全咨询服务部（用服）\",\"dimTerritoryName\":\"\",\"taxItemGroup\":\"J6\",\"dimBUName\":\"安全检测与服务产品BU\",\"dimProductLine\":\"1022\",\"dimIndustryName\":\"\",\"dimDepartmentName\":\"安全咨询服务部（用服）\",\"purchQtyBase\":1,\"purchUnitBase\":\"price\",\"dimBU\":\"14\",\"purchPriceBase\":1,\"dimProductLineName\":\"安全服务\",\"inventTransId\":\"11354861\",\"updateName\":\"吴迪锋\",\"subcontStartDate\":\"2022-12-01\",\"dimDepartment\":\"319000\",\"multiDimInfos\":{\"dimIndustry\":\"\",\"dimDepartment\":\"319000\",\"dimTerritoryName\":\"\",\"dimBUName\":\"安全检测与服务产品BU\",\"dimBU\":\"14\",\"dimProductLine\":\"1022\",\"dimIndustryName\":\"\",\"dimProductLineName\":\"安全服务\",\"dimDepartmentName\":\"安全咨询服务部（用服）\",\"dimTerritory\":\"\"},\"subcontEndDate\":\"2023-06-30\",\"frameworkAgreementMemo\":\"1.安全咨询类服务项目金额大于等于10万，按照3:6:1进行付款，项目金额小于10万无预付款。\\n2.安全运维服务项目周期1年，按照合理合情的原则，背靠背支付。\\n3.特殊项目，特殊申请。\",\"purchId\":\"PA073117\",\"smsOrderExecNumber\":\"1620252309191X303\",\"dimTerritory\":\"\",\"createName\":\"吴迪锋\"},\"dispatchName\":\"广东农信信息科技外包框架协议安全保障类-2023年一二季度人月结算\",\"dispatchNo\":\"SS2023101620230104\",\"dispatchSeq\":\"2023-01-04\",\"contractNos\":\"61020230919A92\",\"projectIds\":\"56027\",\"type\":\"frameworkAgreement\",\"state\":50,\"peopleNum\":null,\"callbackState\":null,\"facilitatorId\":6,\"facilitatorCode\":\"01\",\"facilitatorName\":\"上海安洵信息技术有限公司\",\"bankInfo\":null,\"bankAccount\":null,\"officeCode\":null,\"profitDepCode\":null,\"dutyPerson\":null,\"officeDutyPerson\":null,\"isAccrued\":null,\"isInvoiced\":null,\"dispatchAmount\":\"133100.00\",\"prepaidInfo\":null,\"prepaidRule\":null,\"acceptanceInfo\":null,\"reason\":null,\"remark\":null,\"dispatchTime\":null,\"smsProjectCode\":\"16202523091203N\",\"smsSubmitTime\":null,\"smsProjectAmount\":null,\"smsAfProjectAmount\":null,\"effectiveFrom\":null,\"effectiveTo\":null,\"disabled\":null,\"dispatched\":null,\"settled\":null},\"dispatched\":null,\"checkCollectAndSettle\":null}";
            row = JSON.parse(row.toString());
            Object data = "XXXX";
            // 准备参数
            Map<String, Object> options = new HashMap<>();
            Map<String, Object> settings = new HashMap<>();
            settings.put("aoColumns", Collections.singletonList(MapUtils.putAll(new HashMap(), new Object[] {
                    "data", "createBy customInfo.createName",
                    "alias", "createBy"
            })));
            options.put("settings", settings);
            options.put("col", 0);

            ColumnRenderJSTest jsTest = new ColumnRenderJSTest();
            jsTest.render(render, data, null, row, options);
            
            
            
            render = "(function(data, type, row, options) {\r\n" + 
                    "    if (!data) {\r\n" + 
                    "        var col = options.col;\r\n" + 
                    "        var column = options.settings.aoColumns[col];\r\n" + 
                    "        var alias = column.data;\r\n" + 
                    "        if (alias) {\r\n" + 
                    "            var keys = alias.split(\" \");\r\n" + 
                    "            var values = [];\r\n" + 
                    "            for (var i in keys) {\r\n" + 
                    "                var key = keys[i];\r\n" + 
                    "                try {\r\n" + 
                    "                    var value = eval(\"row.\" + key);\r\n" + 
                    "                    if (value) {\r\n" + 
                    "                        values.push(value);\r\n" + 
                    "                    }\r\n" + 
                    "                } catch (e) {}\r\n" + 
                    "            }\r\n" + 
                    "            data = values.join(\"，\") || data;\r\n" + 
                    "        }\r\n" + 
                    "    }\r\n" + 
                    "    return data;\r\n" + 
                    "})";
            
            settings.put("aoColumns", Collections.singletonList(MapUtils.putAll(new HashMap(), new Object[] {
                    "data", "customInfo.approveState customInfo.paystate",
                    "alias", "json_extract(s.customInfo, '$.approveState'), json_extract(s.customInfo, '$.paystate')"
            })));
            jsTest.render(render, data, null, row, options);
        } catch (NoSuchMethodException | ScriptException e) {
            e.printStackTrace();
        }
    }
}
