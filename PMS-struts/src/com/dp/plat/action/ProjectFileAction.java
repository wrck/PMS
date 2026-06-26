package com.dp.plat.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.dp.plat.context.HttpContext;
import com.dp.plat.data.bean.ProjectDeliver;
import com.dp.plat.data.bean.WeeklyContent;
import com.dp.plat.service.SendMailService;
import com.dp.plat.util.Base64Util;
import com.dp.plat.util.UploadFileUtil;
import com.dp.plat.util.Util;

/**
 * 项目文件管理 Action
 * 处理文件上传、下载、删除等操作
 * 
 * @author PMS Team
 */
public class ProjectFileAction extends ProjectBaseAction {
    
    private static final long serialVersionUID = 1L;
    
    // Service 引用
    protected SendMailService sendMailService;
    
    // 文件上传参数
    private File[] upload;
    private String uploadFileName;
    private File[] uploaddelivery1;
    private File[] uploaddelivery2;
    private File[] uploaddelivery3;
    private File[] uploaddelivery4;
    private String uploaddelivery1FileName;
    private String uploaddelivery2FileName;
    private String uploaddelivery3FileName;
    private String uploaddelivery4FileName;
    
    // 文件下载参数
    private String downpath;
    private String downname;
    private int downFlileId;
    private int result;
    
    // 交付件参数
    private ProjectDeliver projectDeliver;
    private List<ProjectDeliver> projectDeliverList;
    private List<ProjectDeliver> deliverDetailList;
    private int deliverid;
    
    // 周报参数
    private List<WeeklyContent> filecontentList;
    private com.dp.plat.data.bean.ProjectWeekly projectWeekly;
    
    public void setSendMailService(SendMailService sendMailService) {
        this.sendMailService = sendMailService;
    }
    
    /**
     * 进入文件上传页面
     * @return
     */
    public String toUploadFile() {
        return SUCCESS;
    }
    
    /**
     * 进入交付件上传页面
     * @return
     */
    public String toUploadDeliverableFile() {
        String ek = null;
        try {
            ek = projectDeliver.getEventKey();
            String[] eksplit = ek.split("-");
            projectDeliver.setDataTypeCode(eksplit[0]);
            if (eksplit.length > 1) {
                projectDeliver.setBasicDataId(eksplit[1]);
            }
            projectDeliverList = projectService.queryProjectDeliverList(projectDeliver);
        } catch (Exception e) {
            setErrmsg(ExceptionUtils.getStackTrace(e));
            return ERROR;
        }
        return SUCCESS;
    }
    
    /**
     * 周报交付件上传
     * @return
     */
    public String UploadFile() {
        if (upload != null && !upload.equals("")) {
            filecontentList = new ArrayList<WeeklyContent>();
            WeeklyContent weeklyContent = null;
            
            String separator = java.io.File.separator;
            String path = separator + UploadFileUtil.UPLOAD_PATH + separator + "weekly" + separator + new Date().getTime();
            boolean bool = Util.mkdir(path);
            if (!bool) {
                addActionMessage(HttpContext.getMessage("sys.adderror"));
                return SUCCESS;
            }
            String uploadExtWhiteList = basicDataService.querySysArg("sys.upload.ext.whitelist");
            String targetDirectory = ServletActionContext.getServletContext().getRealPath(path);
            String[] uploadFileNames = uploadFileName.split(",");
            
            for (int i = 0; i < uploadFileNames.length; i++) {
                String ufn = uploadFileNames[i];
                String targetFileName = ufn.trim();
                if (!UploadFileUtil.checkFileExt(ufn, uploadExtWhiteList)) {
                    return ERROR;
                }
                String newName = projectService.getUploadFileRename(targetFileName);
                if (newName == null) {
                    newName = targetFileName;
                }
                File target = new File(targetDirectory, newName);
                try {
                    FileUtils.copyFile(upload[i], target);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                weeklyContent = new WeeklyContent();
                weeklyContent.setOptionDesc001(targetFileName);
                weeklyContent.setOptionDesc002(path + separator + newName);
                filecontentList.add(weeklyContent);
            }
            projectService.insertWeeklyFiles(filecontentList, projectWeekly.getWeeklyId());
        }
        
        redirect = "module/ProjectModify.action?project.paramId=" + Base64Util.EncodeBase64(projectWeekly.getProjectId()) + "&result=" + 200 + "&projectWeekly.weeklyId=" + projectWeekly.getWeeklyId();
        return SUCCESS;
    }
    
    /**
     * 下载文件
     * @return
     */
    public String downloadFile() {
        return SUCCESS;
    }
    
    /**
     * 获取下载文件名
     * @return
     */
    @org.apache.struts2.json.annotations.JSON(serialize = false)
    public String getDownloadFile() {
        ServletActionContext.getResponse().setHeader("charset", "ISO8859-1");
        try {
            if (downname != null) {
                if (result == 0) {
                    return new String(downname.getBytes(), "ISO8859-1");
                } else {
                    downname = URLEncoder.encode(downname, "ISO8859-1");
                    return downname;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "orderplan.xlsx";
    }
    
    /**
     * 获取文件流
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    @org.apache.struts2.json.annotations.JSON(serialize = false)
    public InputStream getFileStream() throws FileNotFoundException, UnsupportedEncodingException {
        InputStream in = findFileStream(downpath, true);
        if (null == in) {
            in = findFileStream(downpath, false);
        }
        if (null == in) {
            java.lang.System.out.println("Can not find a java.io.InputStream with the name [inputStream] in the invocation stack.");
        }
        return in;
    }
    
    private InputStream findFileStream(String downpath, boolean checkUpload) throws FileNotFoundException, UnsupportedEncodingException {
        if (checkUpload) {
            String uploadPrefix = StringUtils.split(UploadFileUtil.UPLOAD_PATH, "\\/")[0];
            String dowloadPrefix = StringUtils.split(downpath, "\\/")[0];
            if (downpath != null && !uploadPrefix.startsWith(dowloadPrefix)) {
                downpath = uploadPrefix + File.separator + downpath;
            }
        }
        InputStream in = ServletActionContext.getServletContext().getResourceAsStream(downpath);
        if (null == in) {
            String path = new String(downpath.getBytes(Charset.forName("ISO8859-1")), "UTF-8");
            if (!downpath.equals(path)) {
                in = ServletActionContext.getServletContext().getResourceAsStream(path);
            }
        }
        return in;
    }
    
    /**
     * 删除文件
     * @return
     */
    public String deleteFile() {
        try {
            projectService.deleteFileById(downFlileId);
            result = 0;
        } catch (Exception e) {
            e.printStackTrace();
            result = 1;
        }
        return SUCCESS;
    }
    
    /**
     * 删除交付件
     * @return
     */
    public String deleteDeliverById() {
        try {
            projectService.deleteDeliverById(deliverid);
            result = 0;
        } catch (Exception e) {
            e.printStackTrace();
            result = 1;
        }
        return SUCCESS;
    }
    
    /**
     * 上传交付件文件
     * @return
     */
    public String uploadDeliverableFile() {
        try {
            if (upload != null && upload.length > 0) {
                String separator = java.io.File.separator;
                String path = separator + UploadFileUtil.UPLOAD_PATH + separator + "deliver" + separator + new Date().getTime();
                boolean bool = Util.mkdir(path);
                if (!bool) {
                    addActionMessage(HttpContext.getMessage("sys.adderror"));
                    return SUCCESS;
                }
                String uploadExtWhiteList = basicDataService.querySysArg("sys.upload.ext.whitelist");
                String targetDirectory = ServletActionContext.getServletContext().getRealPath(path);
                String[] uploadFileNames = uploadFileName.split(",");
                
                for (int i = 0; i < uploadFileNames.length; i++) {
                    String ufn = uploadFileNames[i];
                    String targetFileName = ufn.trim();
                    if (!UploadFileUtil.checkFileExt(ufn, uploadExtWhiteList)) {
                        return ERROR;
                    }
                    String newName = projectService.getUploadFileRename(targetFileName);
                    if (newName == null) {
                        newName = targetFileName;
                    }
                    File target = new File(targetDirectory, newName);
                    FileUtils.copyFile(upload[i], target);
                    
                    List<ProjectDeliver> deliverList = new ArrayList<>();
                    ProjectDeliver newDeliver = new ProjectDeliver();
                    newDeliver.setProjectId(projectId);
                    newDeliver.setDeliverableName(targetFileName);
                    newDeliver.setDeliverablePath(path + separator + newName);
                    deliverList.add(newDeliver);
                    projectService.insertProjectDeliverFiles(projectDeliver, deliverList, com.dp.plat.context.UserContext.getUserContext().getUsername());
                }
            }
            result = 0;
        } catch (Exception e) {
            e.printStackTrace();
            result = 1;
        }
        return SUCCESS;
    }
    
    // Getter/Setter 方法
    public File[] getUpload() {
        return upload;
    }
    
    public void setUpload(File[] upload) {
        this.upload = upload;
    }
    
    public String getUploadFileName() {
        return uploadFileName;
    }
    
    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }
    
    public String getDownpath() {
        return downpath;
    }
    
    public void setDownpath(String downpath) {
        this.downpath = downpath;
    }
    
    public String getDownname() {
        return downname;
    }
    
    public void setDownname(String downname) {
        this.downname = downname;
    }
    
    public int getDownFlileId() {
        return downFlileId;
    }
    
    public void setDownFlileId(int downFlileId) {
        this.downFlileId = downFlileId;
    }
    
    public int getResult() {
        return result;
    }
    
    public ProjectDeliver getProjectDeliver() {
        return projectDeliver;
    }
    
    public void setProjectDeliver(ProjectDeliver projectDeliver) {
        this.projectDeliver = projectDeliver;
    }
    
    public List<ProjectDeliver> getProjectDeliverList() {
        return projectDeliverList;
    }
    
    public List<ProjectDeliver> getDeliverDetailList() {
        return deliverDetailList;
    }
    
    public int getDeliverid() {
        return deliverid;
    }
    
    public void setDeliverid(int deliverid) {
        this.deliverid = deliverid;
    }
    
    public com.dp.plat.data.bean.ProjectWeekly getProjectWeekly() {
        return projectWeekly;
    }
    
    public void setProjectWeekly(com.dp.plat.data.bean.ProjectWeekly projectWeekly) {
        this.projectWeekly = projectWeekly;
    }
}
