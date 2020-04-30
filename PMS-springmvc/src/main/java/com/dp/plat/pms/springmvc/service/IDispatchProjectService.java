package com.dp.plat.pms.springmvc.service;

import java.util.Date;
import java.util.List;

import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.entity.DispatchProject;
import com.dp.plat.pms.springmvc.vo.DispatchVO;

/**
 *
 * Created by CodeGenerator
 */
public interface IDispatchProjectService extends IAbstractBaseService<DispatchProject> {

    void dispatchSubmit(Integer id, DispatchVO dispatch);

    void insertOrUpdateSelective(DispatchProject dispatch);

	/**
	 * 生成派单编号
	 * @param facilitatorCode 
	 * @return dispatchSeq
	 */
	String generateDispatchSeq(String facilitatorCode);

	/**
	 * 生成框架协议派单合同
	 * @param dispatchTime 
	 * @param dispatchSeq 
	 * @return dispatchNo
	 */
	String generateDispatchNo(Date dispatchTime, String dispatchSeq);

	/**
	 * 查询派单信息，带合同回款情况，以及结算情况
	 * @param dispatchProject
	 * @return
	 */
	List<DispatchVO> selectDispatchVOWithAmountBySelective(DispatchVO dispatchProject);
	
	List<DispatchVO> selectDispatchVOWithAmountBySelectivePageable(PageParam<Object> pageParam);

	PermissionResult checkPermission(DispatchVO dispatchVO, String... permissions);
}
