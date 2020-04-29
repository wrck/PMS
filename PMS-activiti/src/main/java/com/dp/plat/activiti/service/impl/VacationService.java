/**
 * 
 */
package com.dp.plat.activiti.service.impl;

import java.util.List;

import org.python.antlr.ast.alias;
import org.springframework.stereotype.Service;

import com.dp.plat.activiti.dao.VacationMapper;
import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.entity.Vacation;
import com.dp.plat.activiti.service.IVacationService;
import com.dp.plat.core.service.impl.AbstractBaseService;
import com.dp.plat.core.vo.PageParam;

/**
 * @author w02611
 *
 */
@Service("vacationService")
public class VacationService  extends AbstractBaseService<VacationMapper, Vacation> implements IVacationService {

}
