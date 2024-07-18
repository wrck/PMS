package com.dp.plat.job;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.TypeReference;
import com.dp.plat.data.bean.Presales;
import com.dp.plat.util.AviatorUtils;

/**
 * 自动开始售前测试项目
 * 
 * @author w02611
 */
@SuppressWarnings({ "unused", "unchecked" })
public class AutoStartPresalesProjectJobTest {
    private final static TypeReference<HashMap<String, Object>> TYPE_MAP = new TypeReference<HashMap<String, Object>>() {};
    
    public static String sciprt(Presales presales, Map<String, Object> config) {
        String projectType = presales.getProjectType();
        if (projectType != null && projectType != "") {
            return projectType;
        }
        if ("专网营销部" == presales.getOfficeName()) {
            projectType = "专网项目";
        } else if ("战略合作部" == presales.getOfficeName()) {
            projectType = "战略合作部项目";
        } else if (presales.getProjectName().contains("集采")) {
            projectType = "集采项目";
        } else if (presales.getProjectName().contains("展会")) {
            projectType = "展会";
        } else {
            projectType = "销售测试";
        }
        return projectType;
    }

    @Test
    public void testStartScript() {
        String script = 
                "      let presales = entity.presales;\r\n" + 
                "      let projectType = presales.projectType;\r\n" + 
                "      let officeName = presales.officeName;\r\n" + 
                "      let officeCode = presales.officeCode;\r\n" + 
                "      let projectName = presales.projectName;\r\n" + 
                "      p('#############################');p(officeName);p(officeCode);p(projectName);p(projectType);p('#############################');\r\n" + 
                "      if (projectType != nil && projectType != '') {\n" +
                "          return;\n" +
                "      }\r\n" + 
                "      if ('161000' == officeCode || contains(officeName, '专网营销部')) {\r\n" + 
                "          projectType = '专网项目';\r\n" + 
                "      } elsif ('161100' == officeCode || contains(officeName, '战略合作部')) {\r\n" + 
                "          projectType = '战略合作部项目';\r\n" + 
                "      } elsif (contains(projectName, '集采')) {\r\n" + 
                "          projectType = '集采项目';\r\n" + 
                "      } elsif (contains(projectName, '展会')) {\r\n" + 
                "          projectType = '展会';\r\n" + 
                "      } else {\r\n" + 
                "          projectType = '销售测试';\r\n" + 
                "      }\r\n" +
                "      p('匹配结果:' + projectType);" + 
                "      setProjectType(presales, projectType);";

        Map<String, Object> env = new HashMap<String, Object>();
        Map<String, Object> entity = new HashMap<String, Object>();
        String exceptProjectType = null;
        Presales presales = new Presales();
        presales.setProjectType("");
        presales.setOfficeName("");
        presales.setProjectName("");
        entity.put("presales", presales);
        env.put("entity", entity);
        env.put("config", null);

        presales.setProjectType("");
        presales.setOfficeName("战略合作部");
        exceptProjectType = sciprt(presales, env);
        AviatorUtils.exceute(script, env);
        assertEquals(exceptProjectType, presales.getProjectType());

        presales.setProjectType("");
        presales.setOfficeName("专网营销部");
        exceptProjectType = sciprt(presales, env);
        AviatorUtils.exceute(script, env);
        assertEquals(exceptProjectType, presales.getProjectType());

        presales.setProjectType("");
        presales.setOfficeName("运营商市场部");
        presales.setProjectName("集采");
        exceptProjectType = sciprt(presales, env);
        AviatorUtils.exceute(script, env);
        assertEquals(exceptProjectType, presales.getProjectType());

        presales.setProjectType("");
        presales.setProjectName("展会");
        exceptProjectType = sciprt(presales, env);
        AviatorUtils.exceute(script, env);
        assertEquals(exceptProjectType, presales.getProjectType());

        presales.setProjectType("");
        presales.setProjectName("颠三倒四");
        exceptProjectType = sciprt(presales, env);
        AviatorUtils.exceute(script, env);
        assertEquals(exceptProjectType, presales.getProjectType());
        
        presales.setProjectType("OA临时授权");
        presales.setProjectName("OA临时授权");
        exceptProjectType = sciprt(presales, env);
        AviatorUtils.exceute(script, env);
        assertEquals(exceptProjectType, presales.getProjectType());
    }

}
