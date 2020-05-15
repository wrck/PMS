package com.dp.plat.pms.springmvc.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.pms.springmvc.vo.ProjectDeliver;
import com.dp.plat.pms.springmvc.vo.TaskVO;

/**
 *
 * Created by CodeGenerator
 */
public interface IProjectTaskService extends IAbstractBaseService<ProjectTask> {

    /**
	 * 查询交付件
	 * @param projectDeliver
	 * @return
	 */
    List<ProjectDeliver> selectProjectDeliverBySelective(ProjectDeliver projectDeliver);

    /**
	 * 上传交付件
	 * @param deliver
	 * @param multipartFile
	 */
    void uploadFile(ProjectDeliver deliver, MultipartFile multipartFile);

	PermissionResult checkPermission(TaskVO v, String... permissions);

	Map<String, Object> checkPermissionMap(TaskVO task, String... permissions);

	boolean updateEventActualFinishDateByTask(ProjectDeliver pd);
}
