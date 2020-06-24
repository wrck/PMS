package com.dp.plat.ehr.service.impl;

import org.springframework.stereotype.Service;

import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.ehr.dao.HolidayMapper;
import com.dp.plat.ehr.entity.Holiday;
import com.dp.plat.ehr.service.IHolidayService;

/**
 *
 * Created by CodeGenerator
 */
@Service("holidayService")
public class HolidayService extends AbstractBaseService<HolidayMapper, Holiday> implements IHolidayService {
}