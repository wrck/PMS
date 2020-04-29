package com.dp.plat.pms.springmvc.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.core.service.IAbstractBaseService;
import com.dp.plat.pms.springmvc.vo.ProjectDeliver;

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
}
