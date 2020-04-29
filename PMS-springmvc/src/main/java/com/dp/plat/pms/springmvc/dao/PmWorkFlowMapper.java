package com.dp.plat.pms.springmvc.dao;

import com.dp.plat.core.dao.AbstractBaseMapper;
import com.dp.plat.pms.springmvc.entity.PmWorkFlow;
import java.util.List;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;

public interface PmWorkFlowMapper extends AbstractBaseMapper<PmWorkFlow> {

    /**
	 * 查询流程实例Id
	 * @param workFlow
	 * @return
	 */
    List<String> selectProcInstIdsBySelective(PmWorkFlowVO workFlow);

    void deleteByProcInstId(String processInstanceId);

    void deleteByProcInstIds(String join);
}
