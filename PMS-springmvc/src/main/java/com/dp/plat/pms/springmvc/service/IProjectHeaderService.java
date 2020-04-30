package com.dp.plat.pms.springmvc.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.core.vo.PageParam;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.vo.ProjectVO;
import com.dp.plat.service.ProjectService;

/**
 *
 * Created by CodeGenerator
 */
public interface IProjectHeaderService extends ProjectService/*IAbstractBaseService<ProjectHeader>*/  {

	int deleteByPrimaryKey(Object pk);

	int insert(ProjectHeader t);

	int insertSelective(ProjectHeader t);

	ProjectHeader selectByPrimaryKey(Object pk);

	int updateByPrimaryKeySelective(ProjectHeader t);

	int updateByPrimaryKey(ProjectHeader t);

	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param pageParam
	 * @return
	 */
	long countBySelectivePageable(PageParam<?> pageParam);
	
	/**
	 * 查询满足条件的记录条数记录
	 * 
	 * @param t
	 * @return
	 */
	long countBySelective(ProjectHeader t);

	/**
	 * 分页查询满足条件的记录
	 * 
	 * @param pageParam
	 * @return
	 */
	List<Object> selectBySelectivePageable(PageParam<?> pageParam);
	
	/**
	 * 查询满足条件的所有记录
	 * 
	 * @param t
	 * @return
	 */
	List<ProjectHeader> selectBySelective(ProjectHeader t);
	
	/**
	 * 未创建项目数
	 * @param tempParam
	 * @return
	 */
	long countUncreateProjectList(PageParam<Object> tempParam);

	/**
	 * 未创建项目列表
	 * @param pageParam
	 * @return
	 */
	List<Object> selectUncreateProjectList(PageParam<Object> pageParam);

	/**
	 * 检查是否具有该项目的权限
	 * @param project
	 * @return
	 */
	Map<String, Boolean> checkPermission(ProjectVO project);

	PermissionResult checkPermission(ProjectVO project, String... permissions);

}
