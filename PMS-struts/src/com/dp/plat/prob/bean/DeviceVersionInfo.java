package com.dp.plat.prob.bean;

import java.util.ArrayList;
import java.util.List;

public class DeviceVersionInfo {
    
    private String serial;
    private String conp;
    private String pcb;
    private String cpld;
    private String boot;
    private String fpga;
    private String chassis;
    private String slot;
    private String deviceName;
    
    private List<DeviceVersionInfo> slotDevices = new ArrayList<>();
    
    public DeviceVersionInfo() {
    }
    
    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getConp() {
        return conp;
    }
    
    public void setConp(String conp) {
        this.conp = conp;
    }
    
    public String getPcb() {
        return pcb;
    }
    
    public void setPcb(String pcb) {
        this.pcb = pcb;
    }
    
    public String getCpld() {
        return cpld;
    }
    
    public void setCpld(String cpld) {
        this.cpld = cpld;
    }
    
    public String getBoot() {
        return boot;
    }
    
    public void setBoot(String boot) {
        this.boot = boot;
    }

    public String getFpga() {
        return fpga;
    }
    
    public void setFpga(String fpga) {
        this.fpga = fpga;
    }

    public String getChassis() {
        return chassis;
    }

    public void setChassis(String chassis) {
        this.chassis = chassis;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public List<DeviceVersionInfo> getSlotDevices() {
        return slotDevices;
    }
    
    public void setSlotDevices(List<DeviceVersionInfo> slotDevices) {
        this.slotDevices = slotDevices;
    }
    
    public void addSlotDevice(DeviceVersionInfo slotDevice) {
        this.slotDevices.add(slotDevice);
    }
    
    @Override
    public String toString() {
        return "DeviceVersionInfo{" +
                "serial='" + serial + '\'' +
                ", conp='" + conp + '\'' +
                ", pcb='" + pcb + '\'' +
                ", cpld='" + cpld + '\'' +
                ", boot='" + boot + '\'' +
                ", fpga='" + fpga + '\'' +
                ", chassis='" + chassis + '\'' +
                ", slot='" + slot + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", slotDevices=" + slotDevices +
                '}';
    } 
}
