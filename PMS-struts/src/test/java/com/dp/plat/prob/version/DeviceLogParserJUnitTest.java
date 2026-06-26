package com.dp.plat.prob.version;

import com.dp.plat.prob.bean.DeviceVersionInfo;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DeviceLogParserJUnitTest {

    private static final String TEST_DIR = "d:\\EclipseWorkspace\\Parctice\\PMS\\PMS-struts\\WebContent\\test\\";

    private static class ExpectedResult {
        String conp;
        String pcb;
        String cpld;
        String barCode;
        String boot;
        int slotCount;

        ExpectedResult(String conp, String pcb, String cpld, String barCode, String boot, int slotCount) {
            this.conp = conp;
            this.pcb = pcb;
            this.cpld = cpld;
            this.barCode = barCode;
            this.boot = boot;
            this.slotCount = slotCount;
        }
    }

    private static final Map<String, ExpectedResult> EXPECTED_RESULTS = new HashMap<>();

    static {
        EXPECTED_RESULTS.put("version神六盒式.txt", 
            new ExpectedResult("S311C013D111", "B", "128.00", "02051245P23C000021", "17.08.10", 1));
        
        EXPECTED_RESULTS.put("version神六业务板.txt", 
            new ExpectedResult("S311C013D091P02", "B", "3.00", null, "21.03.02", 0));
        
        EXPECTED_RESULTS.put("version神六主控.txt", 
            new ExpectedResult("S211C013D091P02", "B", "8.00", "02050386d16c000133", "4.10.20", 12));
        
        EXPECTED_RESULTS.put("version神六NFV.txt", 
            new ExpectedResult("S311C013D008T01", null, null, "00000000D2303181A9", null, 0));
        
        EXPECTED_RESULTS.put("version神七盒式.txt", 
            new ExpectedResult("H10C7.1.125R22", "B", "4.00", "02052300N24C000021", "27.07.01", 1));
        
        EXPECTED_RESULTS.put("version神七业务板.txt", 
            new ExpectedResult("H6C7.1.156", "D", "5.00", null, "17.08.10", 0));
        
        EXPECTED_RESULTS.put("version神七主控.txt", 
            new ExpectedResult("H6C7.1.156", "B", "2.00", "02051223N216000011", "17.08.11", 8));
        
        EXPECTED_RESULTS.put("version神七NFV.txt", 
            new ExpectedResult("H1C7.1.156", null, null, "00000000D2303181A9", null, 0));
        
        EXPECTED_RESULTS.put("version神五盒式.txt", 
            new ExpectedResult("S211C012D105", "D", "2.02", "02050513D178000005", "4.10.20", 1));
        
        EXPECTED_RESULTS.put("version神五业务板.txt", 
            new ExpectedResult("S211C012D106P02", "B", "2.00", null, "9.02.17", 0));
        
        EXPECTED_RESULTS.put("version神五主控.txt", 
            new ExpectedResult("S211C012D106P02", "C", "4.02", null, "4.10.02", 4));
    }

    @Test
    public void testAllFiles() {
        String[] testFiles = {
            "version神六盒式.txt",
            "version神六业务板.txt",
            "version神六主控.txt",
            "version神六NFV.txt",
            "version神七盒式.txt",
            "version神七业务板.txt",
            "version神七主控.txt",
            "version神七NFV.txt",
            "version神五盒式.txt",
            "version神五业务板.txt",
            "version神五主控.txt"
        };

        for (String fileName : testFiles) {
            System.out.println("测试文件: " + fileName);
            testSingleFile(fileName);
            System.out.println();
        }
    }

    private void testSingleFile(String fileName) {
        String logContent = readFile(TEST_DIR + fileName);
        assertNotNull("文件读取失败: " + fileName, logContent);

        boolean matches = DeviceLogParserFacade.matches(logContent);
        assertTrue("文件应匹配解析器: " + fileName, matches);

        DeviceVersionInfo result = DeviceLogParserFacade.parse(logContent);
        assertNotNull("解析结果不应为空: " + fileName, result);

        ExpectedResult expected = EXPECTED_RESULTS.get(fileName);
        assertNotNull("未找到预期结果: " + fileName, expected);

        assertEquals("conp 不匹配", expected.conp, result.getConp());
        assertEquals("pcb 不匹配", expected.pcb, result.getPcb());
        assertEquals("cpld 不匹配", expected.cpld, result.getCpld());
        assertEquals("barCode 不匹配", expected.barCode, result.getSerial());
        assertEquals("boot 不匹配", expected.boot, result.getBoot());
        assertEquals("slot数量不匹配", expected.slotCount, result.getSlotDevices().size());

        System.out.println("✓ " + fileName + " 测试通过");
    }

    private String readFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }

            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        } catch (Exception e) {
            System.err.println("读取文件失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
