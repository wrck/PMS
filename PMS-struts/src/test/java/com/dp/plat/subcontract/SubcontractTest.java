package com.dp.plat.subcontract;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.activiti.engine.delegate.VariableScope;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSON;
import com.dp.plat.context.SpringContext;
import com.dp.plat.subcontract.entity.SubcontractPayment;
import com.dp.plat.subcontract.entity.SubcontractProject;
import com.dp.plat.subcontract.listener.SubcontractInspectionListener;
import com.dp.plat.subcontract.service.SubcontractService;
import com.dp.plat.subcontract.utils.SubcontractUtil;
import com.dp.plat.util.AviatorUtils;

import cn.hutool.core.map.MapUtil;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.JavaMethodReflectionFunctionMissing;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SubcontractTest {
    
    @Mock
    private ApplicationContext applicationContext;
    
    @Mock
    private SubcontractService subcontractService;
    
    @Mock
    private SubcontractInspectionListener context;
    
    @Mock
    private VariableScope variableScope;
    

    @Test
    public void assigneExpr() throws Exception {
        SubcontractProject subcontract = new SubcontractProject();
        subcontract.setCustomInfoByKey("parentOfficeCode", "16");
        HashMap<String, Object> taskDefinedVariables = new HashMap<String, Object>();        
        String condition = "use com.alibaba.fastjson.JSON;"
                + "use java.util.Map;"
                + "let officeParentRelations = getSysArg(context, 'pm.subcontract.office2parent.relations');"
                + "p(officeParentRelations);"
                + "let definedVariables = JSON.parseObject(officeParentRelations);"
                + "p(definedVariables);"
                + "let parentOfficeCode = get(definedVariables, entity.customInfo.parentOfficeCode);"
                + "p(parentOfficeCode);"
                + "if (parentOfficeCode != nil) {"
                + "    put(config, 'areaPower', parentOfficeCode);"
                + "    return true;"
                + "}"
                + "return false;"
        ;
        System.out.println(condition);
        taskDefinedVariables.put("condition", condition);
        
        when(context.getSysArg("pm.subcontract.office2parent.relations")).thenReturn("{\"16\": \"30\"}");
        boolean enable = checkAssignee(subcontract, taskDefinedVariables);
        assertEquals(true, enable);
        
        when(context.getSysArg("pm.subcontract.office2parent.relations")).thenReturn("{\"17\": \"31\"}");
        enable = checkAssignee(subcontract, taskDefinedVariables);
        assertEquals(false, enable);
        
        // 使用自定义函数
        AviatorEvaluator.addFunction(new checkConditionFunction());
        taskDefinedVariables.put("condition", "checkCondition()");
        
        when(context.getSysArg("pm.subcontract.office2parent.relations")).thenReturn("{\"16\": \"30\"}");
        enable = checkAssignee2(subcontract, taskDefinedVariables);
        assertEquals(true, enable);
        
        when(context.getSysArg("pm.subcontract.office2parent.relations")).thenReturn("{\"17\": \"31\"}");
        enable = checkAssignee2(subcontract, taskDefinedVariables);
        assertEquals(false, enable);
    }
    
    class checkConditionFunction extends AbstractVariadicFunction {
        private static final long serialVersionUID = -2570774445928953487L;
        @Override
        public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
            String officeParentRelations = context.getSysArg("pm.subcontract.office2parent.relations");
            Map<?, ?> definedVariables = JSON.parseObject(officeParentRelations, java.util.Map.class);
            SubcontractProject entity = (SubcontractProject) env.getOrDefault("entity", new SubcontractProject());
            HashMap<String, Object> config = (HashMap<String, Object>) env.getOrDefault("config", new HashMap<Object, Object>());
            Object parentOfficeCode = definedVariables.get(entity.getCustomInfoByKey("parentOfficeCode"));
            System.out.println(parentOfficeCode);
            boolean enable = false;
            if (parentOfficeCode != null) {
                config.put("areaPower", parentOfficeCode);
                enable = true;
            }
            return AviatorRuntimeJavaType.valueOf(enable);
        }
        public String getName() {
            return "checkCondition";
        }
    }
    
    private boolean checkAssignee(SubcontractProject subcontract, Map<String, Object> taskDefinedVariables) throws Exception {
//        return checkAssignee2(subcontract, taskDefinedVariables);
        String condition = (String) taskDefinedVariables.get("condition");
        
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("AviatorScript");
        
        Compilable compilable = (Compilable) engine;
        CompiledScript script = compilable.compile(condition);
        
        Bindings bindings = engine.createBindings();
        bindings.put("entity", subcontract);
        bindings.put("config", taskDefinedVariables);
        bindings.put("context", context);
        
        Object enable = script.eval(bindings);
        
        return Boolean.TRUE.equals(enable);
    }
    
    private boolean checkAssignee2(SubcontractProject subcontract, Map<String, Object> taskDefinedVariables) throws Exception {
        String condition = (String) taskDefinedVariables.get("condition");
        
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("entity", subcontract);
        env.put("config", taskDefinedVariables);
        env.put("context", context);
        // 启用基于反射的方法查找和调用
        AviatorEvaluator.setFunctionMissing(JavaMethodReflectionFunctionMissing.getInstance());
        AviatorEvaluatorInstance evaluatorInstance = AviatorEvaluator.getInstance();
        evaluatorInstance.setCachedExpressionByDefault(true);
        evaluatorInstance.useLRUExpressionCache(100);
        Expression expression = evaluatorInstance.compile("checkAssginee" + condition.hashCode(), condition, true);
        System.out.println(expression.hashCode() + expression.toString());
        Object enable = expression.execute(env);
        
        return Boolean.TRUE.equals(enable);
    }
    
    /**
     * 检查办理人是否存在启用条件，如果存在启用条件，进行校验
     * 
     * @param variableScope
     * @param approverConfig
     * @param taskDefinedVariables
     * @return
     */
    public boolean checkAssignee(
            VariableScope variableScope, Map<String, Object> approverConfig, Map<String, Object> taskDefinedVariables
    ) {
        // 判断是否存在条件，无条件则默认启用
        boolean enable = !approverConfig.containsKey("condition");
        if (enable) {
            return enable;
        }

        // 存在启用条件，进行校验
        String condition = (String) approverConfig.get("condition");
        Object entity = variableScope.getVariable("entity");
        if (entity == null) {
            entity = taskDefinedVariables.get("entity");
        }
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("entity", entity);
        env.put("config", approverConfig);
        env.put("context", context);

        Object result = false;
        try {
            result = AviatorUtils.exceute(condition, env);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Boolean.TRUE.equals(result);
    }
    
    @Test
    public void areaLeaderAudit() throws Exception {
        HashMap<String, Object> taskDefinedVariables = new HashMap<String, Object>();        
        String condition = "use com.alibaba.fastjson.JSON;"
                + "    use java.util.Map;"
                + "    use java.util.List;"
                + "    use com.dp.plat.context.SpringContext;"
                + "    let payments = entity.entity;\n    p(payments);"
                + "    if (payments == nil || isEmpty(payments)) {"
                + "      return false;"
                + "    }"
                + "    let payment = get(payments, 0);"
                + "    let subcontractService = SpringContext.getBean('subcontractService');"
                + "    let subcontract = selectSubcontractProjectById(subcontractService, payment.subcontractId);"
                + "    if (subcontract == nil) {"
                + "      return false;"
                + "    }"
                + "    let auditEngineeFeeOffices = getSysArg(context, 'subcontract.areaLeader.auditEngineeFee.offices');"
                + "    p(subcontract.profitDepCode);"
                + "    p(auditEngineeFeeOffices);"
                + "    if(auditEngineeFeeOffices == nil) {"
                + "      return true;"
                + "    }"
                + "    return !contains(auditEngineeFeeOffices, subcontract.profitDepCode);";

        SubcontractProject subcontract = new SubcontractProject();
        subcontract.setProfitDepCode("161010");
        List<SubcontractPayment> payments = Collections.singletonList(new SubcontractPayment());
        HashMap entity = new HashMap();
        entity.put("entity", payments);
        
        System.out.println(condition);
        taskDefinedVariables.put("condition", condition);
        taskDefinedVariables.put("entity", entity);
        new SpringContext().setApplicationContext(applicationContext);
        when(SpringContext.getBean("subcontractService")).thenReturn(subcontractService);
        when(variableScope.getVariable("entity")).thenReturn(entity);
        when(subcontractService.selectSubcontractProjectById(null)).thenReturn(subcontract);
        when(context.getSysArg("subcontract.areaLeader.auditEngineeFee.offices")).thenReturn("161000,311000");
        boolean enable = checkAssignee(variableScope, taskDefinedVariables, taskDefinedVariables);
        assertEquals(true, enable);
        
        subcontract.setProfitDepCode("161000");
        enable = checkAssignee(variableScope, taskDefinedVariables, taskDefinedVariables);
        assertEquals(false, enable);
    }
    
    @Test
    public void needVerify() {
        String condition = "use com.alibaba.fastjson.JSON;"
                + "    use java.util.Map;"
                + "    use java.util.List;"
                + "    use com.dp.plat.context.SpringContext;"
                + "    use cn.hutool.core.map.MapUtil;"
                + "    use org.apache.commons.lang3.StringUtils;"
                + "    let invoice = entity.entity;\n    p(invoice);"
                + "    if (invoice == nil || isEmpty(invoice)) {"
                + "      return false;"
                + "    }"
                + "    let needVerify = MapUtil.getBool(invoice, 'needVerify', false);"
                + "    p(needVerify);"
                + "    let unNeedVerify = !needVerify && StringUtils.isBlank(MapUtil.getStr(invoice, 'invoice_number'));"
                + "    p(unNeedVerify);"
                + "    return !unNeedVerify;";
        
        System.out.println(condition);
        HashMap env = new HashMap();
        env.put("condition", condition);
        
        Map<String, Object> invoice = Collections.singletonMap("needVerify", true);
        env.put("entity", Collections.singletonMap("entity", invoice));
        
        Object needVerify = AviatorUtils.exceute(condition, env);
        assertEquals(true, Boolean.TRUE.equals(needVerify));
        assertEquals(true, SubcontractUtil.checkDeliveryInvoiceType(MapUtils.putAll(new HashMap<>(), new Object[] { "condition", condition, "needVerify", true})));
        
        invoice = Collections.singletonMap("needVerify", false);
        env.put("entity", Collections.singletonMap("entity", invoice));
        needVerify = AviatorUtils.exceute(condition, env);
        assertEquals(false, Boolean.TRUE.equals(needVerify));
        assertEquals(false, SubcontractUtil.checkDeliveryInvoiceType(MapUtils.putAll(new HashMap<>(), new Object[] { "condition", condition, "needVerify", false})));
        
        invoice = Collections.singletonMap("invoice_number", "");
        env.put("entity", Collections.singletonMap("entity", invoice));
        needVerify = AviatorUtils.exceute(condition, env);
        assertEquals(false, Boolean.TRUE.equals(needVerify));
        assertEquals(false, SubcontractUtil.checkDeliveryInvoiceType(MapUtils.putAll(new HashMap<>(), new Object[] { "condition", condition, "invoice_number", ""})));
        
    }
}
