package com.dp.plat.core.schedule;

public enum SyncType {
    FULL_SYNC((short) 1, "full_sync", "全量同步"), 
    INCREM_SYNC((short) 2, "increm_sync", "增量同步");

    private final short type;
    private final String code;
    private final String name;

    SyncType(short type, String code, String name) {
        this.type = type;
        this.code = code;
        this.name = name;
    }

    public short getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}