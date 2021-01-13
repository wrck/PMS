package com.dp.plat.dao;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Presales;
import com.dp.plat.data.bean.PresalesComment;
import com.dp.plat.data.bean.PresalesProduct;
import com.dp.plat.data.bean.PresalesTask;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.vo.PresalesExportVO;
import com.dp.plat.param.DisplayParam;

/**
 * 售前项目DAO管理
 * @author admin
 *
 */
public interface PresalesDao {
	/**
	 * select * from pm_presales_header by presalesid
	 * @param presalesId
	 * @return
	 */
	Presales queryPresalesById(int presalesId);
	/**
	 * select * from pm_presales_product_line by presalesid
	 * @param presalesId
	 * @return
	 */
	List<PresalesProduct> queryPresalesProductByPresalesId(int presalesId);
	/**
	 * update pm_project_member set by objId and memberRole
	 * @param objId
	 * @param memberRole
	 */
	void invalidProjectMember(int objId, String memberRole);
	/**
	 * select from pm_presales_header
	 * @param presales
	 * @param displayParam
	 * @return
	 */
	List<Presales> queryPresalesList(Presales presales,
			DisplayParam displayParam) throws UnsupportedEncodingException;
	/**
	 * 查询审批意见
	 * @param presalesId
	 * @param simpleName
	 * @return
	 */
	List<PresalesComment> queryActComment(int presalesId, String procdefKey);
	/**
	 * update pm_presales_header by Id
	 * @param presales
	 */
	void updatePresaleHeader(Presales presales);
	/**
	 * select pm_presales_project_callback by presalesId taskId
	 * @param presales
	 * @return
	 */
	int queryCallBackQuesnaireId(Presales presales);
	/**
	 * update  pm_presales_project_callback by id
	 * @param callbackQuesnaireId
	 * @param pmClQuesnaireResultHeaderId
	 * @param status
	 */
	void updateCallBackQuesnaire(int callbackQuesnaireId,
			int pmClQuesnaireResultHeaderId, int status);
	/**
	 * select pm_presales_project_callback by presalesId
	 * @param presalesId
	 * @return
	 */
	int queryCallBackQuesnaireVersion(int presalesId);
	/**
	 * insert pm_presales_project_callback
	 * @param paramMap
	 */
	void insertCallBackQuesnaire(Map<String, Object> paramMap);
	/**
	 * query quesnaireId from pm_presales_project_callback
	 * @param presales
	 * @return
	 */
	int queryQuesnaireIdBycallbackId(Presales presales);
	/**
	 * update applyState projectState
	 * @param map
	 */
	void updatePresalesState(Map<String, Object> map);
	/**
	 * 
	 * @param presalesId
	 * @return
	 */
	int queryPresalesCodeNum(int presalesId);
	/**
	 * update presalescode by id num
	 * @param presalesId
	 * @param num
	 */
	void updatePresalesCode(int presalesId, int num);
	/**
	 * update pm_presales_project_product_line presalesId
	 * @param presalesId
	 */
	void updatePresalesProduct(int presalesId);
	/**
	 * select count(*) from pm_project_task  by projectid projectType 
	 * @param presalesId
	 * @param typeOfPresales
	 * @return
	 */
	boolean queryIsHasProjectTask(int presalesId, int typeOfPresales);
	/**
	 * insert into
	 * select
	 * @param presalesId
	 * @param typeOfPresales
	 * @param basicDataProjectType
	 */
	void insertPresaleTasks(int presalesId, int typeOfPresales,
			String basicDataProjectType);
	/**
	 * select 
	 * pm_project_task
	 * by projectId projecttype
	 * @param presalesId
	 * @param projectType
	 * @return
	 */
	List<PresalesTask> queryPresalesTaskList(int presalesId, int projectType);
	/**
	 * update pm_project_task deliverFileIds by taskId
	 * @param taskId
	 * @param fileIds
	 */
	void updatePresalesTaskDeliverFiles(int taskId, String fileIds);
	/**
	 * 
	 * @param taskFinshedTime
	 * @param presalesTaskId
	 */
	void updatePresalesTask(Date taskFinshedTime, int presalesTaskId);
	/**
	 * 
	 * @param presalesId
	 * @param fileIds
	 */
	void updatePresalesConfirmFileIds(int presalesId, String fileIds);
	void updatePrealesFileIds(int presalesId, int taskId, int fileId);
	/**
	 * 查询售前测试发货信息
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
     * @param presalesId
     */
    void updatePresalesDuration(int presalesId);
    /**
     * @param projectCode
     * @return
     */
    List<Map<String, Object>> queryPresaleLend2SaleInfo(String projectCode);
    /**
     * @param projectCode
     * @return
     */
    List<Map<String, Object>> queryPresaleLend2RmaInfo(String projectCode);

}
