package com.dp.plat.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.Presales;
import com.dp.plat.data.bean.PresalesComment;
import com.dp.plat.data.bean.PresalesProduct;
import com.dp.plat.data.bean.PresalesTask;
import com.dp.plat.data.bean.ProjectDeliver;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.vo.PresalesExportVO;
import com.dp.plat.param.DisplayParam;

/**
 * 售前项目业务管理
 * @author admin
 *
 */
public interface PresalesService {
	/**
	 * 根据ID查询 preslaes
	 * @param presalesId
	 * @return
	 */
	Presales queryPresalesById(int presalesId);
	/**
	 * 根据ID查询 presalesProduct
	 * @param presalesId
	 * @return
	 */
	List<PresalesProduct> queryPresalesProductByPresalesId(int presalesId);
	/**
	 * 启动流程
	 * @param presales
	 * @param param
	 */
	void startPresalesFlow(Presales presales, PresalesComment param);
	/**
	 * 查询售前项目集合
	 * @param presales
	 * @param displayParam
	 * @return
	 */
	List<Presales> queryPresalesList(Presales presales,
			DisplayParam displayParam) throws UnsupportedEncodingException;
	/**
	 * 查询售前流程意见
	 * @param presalesId
	 * @return
	 */
	List<PresalesComment> queryPresalesCommentList(int presalesId);
	/**
	 * 服务经理审批
	 * @param presales
	 * @param param
	 */
	void submitSmAduit(Presales presales, PresalesComment param);

	/**
	 * 项目经理审批
	 * @param presales
	 * @param param
	 */
	void submitpmAduit(Presales presales, PresalesComment param);
	/**
	 * 保存问卷
	 * @param presales
	 * @param pmClQuesnaireResultHeader
	 * @param pmClQuesnaireResultLineList
	 */
	void insertPresalesQuesnaire(Presales presales,
			PmClQuesnaireResultHeader pmClQuesnaireResultHeader,
			List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList);
	/**
	 * 查询当前任务保存的问卷
	 * @param presales
	 * @return
	 */
	int queryPresalesQuesnaireId(Presales presales);
	/**
	 * 结束售前项目
	 * @param presalesId
	 */
	void updateEndingPresalesProject(int presalesId);
	/**
	 * 直接闭环售前项目
	 * @param presalesId
	 */
	void updateEnding20PresalesProject(int presalesId);
	/**
	 * 工程管理部审批
	 * @param presales
	 * @param param
	 */
	void submitEmAduit(Presales presales, PresalesComment param);
	/**
	 * 重新提交申请
	 * @param presales
	 * @param param
	 */
	void submitReApply(Presales presales, PresalesComment param);
	/**
	 * 查询项目任务
	 * @param presalesId
	 * @param projectType
	 * @return
	 */
	List<PresalesTask> queryPresalesTaskList(int presalesId, int projectType);
	/**
	 * 更新项目计划的交付件
	 * @param taskId
	 * @param fileIds
	 */
	void updatePresalesTaskDeliverFiles(int taskId, String fileIds);
	/**
	 * 更新计划完成时间
	 * @param taskFinshedTime
	 * @param presalesTaskId
	 */
	void updatePresalesTask(Date taskFinshedTime, int presalesTaskId);
	/**
	 * 更新交付件信息到项目主表
	 * @param presalesId
	 * @param fileIds
	 */
	void updatePresalesConfirmFileIds(int presalesId, String fileIds);
	
	
	void updatePrealesFileIds(int presalesId, int taskId, int fileId);

	/**
	 * 查询售前测试发货信息, 不包含退货设备
	 * @param projectCode
	 * @return
	 */
	List<ShipmentInfo> queryPresaleShipmentInfo(String projectCode);
	/**
	 * 查询售前测试发货信息, 是否包含退货设备
     * @param projectCode
     * @param containRma
     * @return
     */
    List<ShipmentInfo> queryPresaleShipmentInfo(String projectCode, boolean containRma);
    
	/**
	 * 终止流程，直接关闭
	 * @param presalesIds
	 * @param comment
	 */
	void terminate2Close(String presalesIds, String comment);
	/**
	 * 更新计划完成时间,和进展情况
	 * @param taskFinshedTime
	 * @param remark
	 * @param presalesTaskId
	 */
	void updatePresalesTask(Date taskFinshedTime, String remark, int presalesTaskId);
	/**
	 * @param presales
	 * @return
	 */
	List<PresalesExportVO> queryPresalesExportData(Presales presales);
    /**
     * @param projectDeliver
     * @param string
     * @param uploaddelivery
     * @param uploaddeliveryFileName
     * @return
     */
    boolean uploadFile(ProjectDeliver projectDeliver, String string, File[] uploaddelivery, String uploaddeliveryFileName);
    /**
     * @param projectDeliver
     * @return
     */
    List<ProjectDeliver> queryProjectDeliverList(ProjectDeliver projectDeliver);
    /**
     * @param fileId
     * @return
     */
    int deleteDeliverById(int fileId);
    /**
     * @param projectDeliver
     * @return
     */
    void updateProjectDeliverById(ProjectDeliver projectDeliver);
    /**
     * @param presalesCode
     * @return
     */
    List<Map<String, Object>> queryPresaleLend2SaleInfo(String presalesCode);
    /**
     * @param presalesCode
     * @return
     */
    List<Map<String, Object>> queryPresaleLend2RmaInfo(String presalesCode);
    /**
     * 查询临时授权数据
     * @param params
     * @return
     */
    List<Map<String, Object>> selectPresalesTempAuthInfo(Map<String, Object> params);
}
