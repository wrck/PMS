package com.dp.plat.ehr.entity;

import java.util.Date;

import com.dp.plat.core.entity.BaseEntity;
import com.dp.plat.core.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Holiday extends BaseEntity {

    // 日期
    @JsonSerialize(using = JsonSerializer.class)
    private Date date;

    // 是否为节假日
    private Boolean isHoliday;

    /**
     * 获取日期
     *
     * @return date - 日期
     */
    public Date getDate() {
        return date;
    }

    /**
     * 设置日期
     *
     * @param date 日期
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * 获取是否为节假日
     *
     * @return isHoliday - 是否为节假日
     */
    public Boolean getIsHoliday() {
        return isHoliday;
    }

    /**
     * 设置是否为节假日
     *
     * @param isHoliday 是否为节假日
     */
    public void setIsHoliday(Boolean isHoliday) {
        this.isHoliday = isHoliday;
    }
}
