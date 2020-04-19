package com.dp.plat.pms.springmvc.service.impl;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileOutputStream;
import com.dp.plat.core.service.impl.AbstractBaseService;
import java.util.Map;
import com.dp.plat.core.util.FileUtil;
import java.io.File;
import com.dp.plat.core.context.HttpContext;
import org.springframework.stereotype.Service;
import java.io.BufferedInputStream;
import org.apache.commons.fileupload.util.Streams;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.pms.springmvc.dao.ProjectTaskMapper;
import com.dp.plat.pms.springmvc.service.IProjectTaskService;
import java.io.BufferedOutputStream;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import java.util.ArrayList;
import com.dp.plat.core.config.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import com.dp.plat.core.util.UploadUtils;
import com.dp.plat.dao.ProjectDao;
import java.io.IOException;
import java.util.HashMap;
import com.dp.plat.pms.springmvc.vo.ProjectDeliver;

/**
 *
 * Created by CodeGenerator
 */
@Service("projectTaskService")
public class ProjectTaskService extends AbstractBaseService<ProjectTaskMapper, ProjectTask> implements IProjectTaskService {

    @Autowired
    protected ProjectDao projectDao;

    @Override
    public void uploadFile(ProjectDeliver deliver, MultipartFile multipartFile) {
        // 文件名
        String originalFilename = multipartFile.getOriginalFilename();
        // 文件重命名
        String fileName = FileUtil.getFileNameByMD5(multipartFile);
        String webDir = UploadUtils.getWebDir(HttpContext.getCurrentRequest());
        // 获取文件保存目录
        String saveDir = UploadUtils.getSaveDir(SystemConfig.systemVariables.getOrDefault("pm.project.deliver.dir", "/upload/delivery/"));
        // 构造完整的文件保存路径
        String fullPath = webDir + saveDir + fileName;
        // 处理操作系统的差异
        fullPath = fullPath.replaceAll("//", File.separator);
        String shortPath = saveDir + fileName;
        // 处理操作系统的差异
        shortPath = shortPath.replaceAll("//", File.separator);
        // 判断上传目录是否存在
        UploadUtils.mkdir(webDir, saveDir.replaceAll("//", File.separator));
        File file = new File(fullPath);
        if (file.isFile() && file.exists()) {
        //
        // 重命名的情况下，文件已经存在的不用再重复上传
        } else {
            try {
                BufferedInputStream inputStream = new BufferedInputStream(multipartFile.getInputStream());
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fullPath));
                Streams.copy(inputStream, outputStream, true);
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ProjectDeliver pdeliver = new ProjectDeliver();
        pdeliver.setProjectId(deliver.getProjectId());
        pdeliver.setContractNo(deliver.getContractNo());
        pdeliver.setDeliverId(deliver.getDeliverId());
        pdeliver.setDeliverableType(deliver.getDeliverableType());
        pdeliver.setDeliverableName(originalFilename);
        pdeliver.setDeliverablePath(shortPath);
        List<ProjectDeliver> pdlist = new ArrayList<ProjectDeliver>(1);
        pdlist.add(pdeliver);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (pdlist != null && pdlist.size() > 0) {
            paramMap.put("uploadUser", UserContext.getCurrentUser().getUserName());
            paramMap.put("list", pdlist);
            if (deliver != null) {
                paramMap.put("taskId", deliver.getTaskId());
                paramMap.put("projectType", deliver.getProjectType());
            }
            projectDao.batchInsertDeliverFiles(paramMap);
        }
    }

    @Override
    public List<ProjectDeliver> selectProjectDeliverBySelective(ProjectDeliver projectDeliver) {
        return dao.selectProjectDeliverBySelective(projectDeliver);
    }
}
