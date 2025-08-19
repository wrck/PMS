package com.dp.plat.prob.dao;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ReportLineData;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.prob.bean.Prob;
import com.dp.plat.prob.bean.ProbFile;
import com.dp.plat.prob.bean.ProbProduct;
import com.dp.plat.prob.bean.ProbReadLog;
import com.dp.plat.prob.bean.ProbRestore;
import com.dp.plat.prob.bean.ProbRestoreWeekly;
import com.dp.plat.prob.bean.ProbStatistic;
import com.dp.plat.prob.bean.ProductComponent;
import com.dp.plat.prob.bean.SoftVersion;
import com.dp.plat.prob.param.ProbParam;
import com.dp.plat.prob.util.ProductItemExample;
import com.dp.plat.prob.vo.ProbProductPageParam;
import com.dp.plat.prob.vo.ProbProductVO;
import com.dp.plat.prob.vo.ProductComponentPageParam;
import com.dp.plat.prob.vo.ProductComponentVO;

/**
 * 技术公告操作数据库接口
 * @author j01441
 *
 */
public interface ProbManageDao {

	int saveProb(Prob prob);
	
	List<Prob> queryProbList(Prob prob, DisplayParam displayParam);

	Prob queryOneProb(Prob prob);

	void updateProb(Prob prob);

	List<SoftVersion> checkSoftVersionList(SoftVersion softVersion);

	void updateInvalidSoftVersion(int probId);

	void saveSoftVersion(List<SoftVersion> softVersionList, int probId);

	List<SoftVersion> querySoftVersionList(int probId);
	
	List<SoftVersion> querySoftVersionList(SoftVersion softVersion);

	Map<Integer, String> queryProbFileMap(int probId);

	List<ProbRestore> queryProbRestoreList(ProbRestore probRestore, DisplayParam restoreDisplayParam);

	void insertBatchProbRestoreTask(ProbRestore probRestore, List<ProbRestore> probRestoreTaskList);

	List<ProbRestore> queryProbRestoreTaskList(ProbRestore probRestore, DisplayParam restoreDisplayParam) throws UnsupportedEncodingException;

	int insertProbRestoreProcess(ProbRestore probRestore);

	void updateProbRestore(int processId, String restoreIds, String assignee);

	void updateProbRestoreAssignee(ProbRestore probRestore, String restoreIds);

	int queryProbRestoreProcessSize(int probId);

	void deleteProbInfo(int probId);

	int queryNextVal();

	List<ProbFile> queryProbFileList(String attachments);

	void insertProbTaskWeekly(int fileId, int probId);

	List<ProbRestoreWeekly> queryProbWeekly(int probId, String username);

	String queryProbAssigneeEmails(String restoreIds);

	/**
	 * 批量删除子任务
	 * @param probRestoreIds
	 */
	void bacthDeleteProbRestores(String probRestoreIds);

	/**
	 * 更新Prob state 主要用于技术公告原审批
	 * @param prob
	 */
	void updateProbStatus(Prob prob);

	/**
	 * 查询处理的子任务所在的项目projectId
	 * @param restoreIds
	 * @return 
	 */
	String queryProjectIdsByProbRestoreIds(String restoreIds);

	/**
	 * 查询子任务涉及到的序列号的软件变更列表
	 * @param restoreIds
	 * @return
	 */
	List<ShipmentInfo> queryHistSoftVersionListByProbRestoreIds(String restoreIds);

	/**
	 * 查询子任务涉及到的办事处的用服邮箱
	 * @param restoreIds
	 * @return
	 */
	String queryOfficeMailsByProbRestoreIds(String restoreIds);

	/**
	 * @param probRestore
	 * @param restoreDisplayParam
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	List<ProbRestore> queryProbRestoreTaskProjectList(ProbRestore probRestore, DisplayParam restoreDisplayParam) throws UnsupportedEncodingException;

	/**
	 * 查询技术公告导出数据
	 * @param params
	 * @return
	 */
	List<ProbParam> queryExportProbList(Map<Object, Object> params);

	/**
	 * 批量导入软件版本
	 * @param softVersions
	 */
	void batchAddSoftVersion(List<SoftVersion> softVersions);

	/**
	 * 查询已维护的项目软件版本统计表
	 * @param probStatistic
	 * @param displayParam 
	 * @return
	 */
	List<ProbStatistic> queryProbStatisticList(ProbStatistic probStatistic, DisplayParam displayParam);

	/**
	 * 查询已维护的项目软件版本统计表,附带报表数据
	 * @param probStatistic
	 * @param displayParam
	 * @param reportLineDatas
	 * @return
	 */
	List<ProbStatistic> queryProbStatisticListWithReport(ProbStatistic probStatistic, DisplayParam displayParam,
			List<ReportLineData> reportLineDatas);

	/**
	 * 查询原厂直服的项目列表
	 * @param probStatistic
	 * @param displayParam
	 * @return
	 */
	List<Project> queryProbStatisticProjectList(ProbStatistic probStatistic, DisplayParam displayParam);
	
	/**
     * 查询合同发货软件版本
     * @param probStatistic
     * @param displayParam
     * @return
     */
    List<?> queryContractShipmentSoftList(ProbStatistic probStatistic, DisplayParam displayParam);

	/**
	 * @param probReadLog
	 */
	void insertProbReadLog(ProbReadLog probReadLog);

	/**
	 * @param probReadLog
	 * @param displayParam
	 * @return
	 */
	List<ProbReadLog> queryProbReadLogList(ProbReadLog probReadLog, DisplayParam displayParam);

	/**
     * @param id
     * @return
     */
    ProductComponent selectProductComponentById(Integer id);
    
    /**
     * @param id
     * @return
     */
    ProductComponentVO selectProductComponentVOById(Integer id);
    
    /**
     * @param component
     * @return
     */
    List<ProductComponent> selectProductComponentList(ProductComponent component);
    
    /**
     * @param pageParam
     * @return
     */
    List<ProductComponentVO> selectProductComponentListPageable(ProductComponentPageParam pageParam);
    /**
     * 
     * @param pageParam
     * @return
     */
    Integer countProductComponentListPageable(ProductComponentPageParam pageParam);
    
    /**
     * @param component
     */
    Integer insertProductComponent(ProductComponent component);
    /**
     * 
     * @param component
     * @return
     */
    Integer insertProductComponentSelective(ProductComponent component);
    /**
     * 
     * @param component
     * @return
     */
    Integer insertOrUpdateProductComponentSelective(ProductComponent component);
    
    /**
     * @param id
     * @return
     */
    void deleteProductComponentById(Integer id);

    /**
     * @param component
     */
    void updateProductComponentById(ProductComponent component);
    /**
     * @param component
     */
    void updateProductComponentByIdSelective(ProductComponent component);

    /**
     * @param id
     * @return
     */
    ProbProduct selectProbProductById(Integer id);
    
    /**
     * @param id
     * @return
     */
    ProbProductVO selectProbProductVOById(Integer id);
    
    /**
     * @param probProduct
     * @return
     */
    List<ProbProduct> selectProbProductList(ProbProduct probProduct);
    
    /**
     * @param pageParam
     * @return
     */
    List<ProbProductVO> selectProbProductListPageable(ProbProductPageParam pageParam);
    /**
     * 
     * @param pageParam
     * @return
     */
    Integer countProbProductListPageable(ProbProductPageParam pageParam);
    
    /**
     * @param probProduct
     */
    Integer insertProbProduct(ProbProduct probProduct);
    /**
     * 
     * @param probProduct
     * @return
     */
    Integer insertProbProductSelective(ProbProduct probProduct);
    
    /**
     * @param id
     * @return
     */
    void deleteProbProductById(Integer id);

    /**
     * @param probProduct
     */
    void updateProbProductById(ProbProduct probProduct);
    /**
     * @param probProduct
     */
    void updateProbProductByIdSelective(ProbProduct probProduct);
    
    void updateProbProductByProbIdSelective(ProbProduct probProduct);

    Integer insertOrUpdateProbProductSelective(ProbProduct probProduct);

    void deleteProbProductByProbId(Integer probId);

    List<? extends Object> selectProductItemListByParams(Map<String, Object> commonMap);

    List<? extends Object> selectProductItemListByExample(ProductItemExample example);

    Integer bastchInsertProbProduct(List<ProbProduct> probProductList, int probId);

}
