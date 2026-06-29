package com.dp.plat.prob.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.dp.plat.prob.bean.DeviceVersionInfo;

public class DeviceLogParserTest {

    public static void main(String[] args) {
        String testDir = "d:\\EclipseWorkspace\\Parctice\\PMS\\PMS-struts\\WebContent\\test\\";
        
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
            System.out.println("========================================");
            System.out.println("测试文件: " + fileName);
            System.out.println("========================================");
            
            String logContent = readFile(testDir + fileName);
            if (logContent != null) {
                boolean matches = DeviceLogParserFacade.matches(logContent);
                System.out.println("是否匹配: " + matches);
                
                if (matches) {
                    DeviceVersionInfo info = DeviceLogParserFacade.parse(logContent);
                    System.out.println("解析结果:");
                    System.out.println("  conp: " + info.getConp());
                    System.out.println("  pcb: " + info.getPcb());
                    System.out.println("  cpld: " + info.getCpld());
                    System.out.println("  serial: " + info.getSerial());
                    System.out.println("  boot: " + info.getBoot());
                    
                    if (!info.getSlotDevices().isEmpty()) {
                        System.out.println("  插槽设备信息:");
                        for (DeviceVersionInfo slot : info.getSlotDevices()) {
                            System.out.println("    机框: " + slot.getChassis() + 
                                             ", 插槽: " + slot.getSlot() + 
                                             ", 设备: " + slot.getDeviceName());
                            System.out.println("      PCB: " + slot.getPcb() + 
                                             ", CPLD: " + slot.getCpld() + 
                                             ", FPGA: " + slot.getFpga() + 
                                             ", CONBOOT: " + slot.getBoot() +
                                             ", SERIAL: " + slot.getSerial());
                        }
                    }
                }
                System.out.println();
            }
        }
    }
    
    private static String readFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("文件不存在: " + filePath);
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
            System.out.println("读取文件失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
