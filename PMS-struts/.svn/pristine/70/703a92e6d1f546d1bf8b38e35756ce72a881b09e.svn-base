package com.dp.plat.job;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dp.plat.context.SpringContext;
import com.dp.plat.param.OrderBean;
import com.dp.plat.param.OrderLineBean;
import com.dp.plat.service.ProjectService;
import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;


public class GainOrderBySAP implements Job{
    private static final Logger LOGGER = Logger.getLogger(GainOrderBySAP.class);
    
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
		    LOGGER.debug("#SAP数据同步开始");
			this.work();
			LOGGER.debug("#SAP数据同步结束");
			
			LOGGER.debug("#更新项目发货状态开始");
			UpdateShipmentState.work();//更新项目发货状态
			LOGGER.debug("#更新项目发货状态结束");
//			CloseNotTrackProject.work();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void work() throws IOException, SQLException {
	    ApplicationContext ctx = SpringContext.getApplicationContext();
        if (ctx == null) {
            ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        }
		Reader readerSap = Resources.getResourceAsReader("sqlMapConfigSAP.xml");
		SqlMapClient sqlMapSap=SqlMapClientBuilder.buildSqlMapClient(readerSap);
		
		Reader reader = Resources.getResourceAsReader("sqlMapConfig.xml");
		SqlMapClient sqlMap=SqlMapClientBuilder.buildSqlMapClient(reader);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//开始同步，增加日志
		paramMap.put("refreshTaskName", GainOrderBySAP.class.toString());
		paramMap.put("handleUser", "system");
		paramMap.put("dataFrom", "ERP");
		paramMap.put("dataTo", null);
		paramMap.put("refreshFrom", new Date());
		Object obj = sqlMap.insert("insert_fnd_data_refresh_log", paramMap);
		try {
			paramMap.put("orderType", 0);
			//刷新同步正常销售订单
			List<OrderBean> orderBeans = sqlMapSap.queryForList("query_DP_V_SO_ORDER_4_PMS");
			sqlMap.startTransaction();
			sqlMap.delete("delete_pm_order_data");
			List<OrderBean> list = new ArrayList<OrderBean>();
			int i = 0;
			for(OrderBean orderBean : orderBeans){
				if(orderBean.getOrderExecNumber()!= null){
					orderBean.setOrderExecNumber(orderBean.getOrderExecNumber().replace("J", "X"));
				}
				if(i < 2000){
					i ++;
					list.add(orderBean);
				} else {
					paramMap.put("list", list);
					sqlMap.insert("insert_pm_order_data", paramMap);
					i = 0;
					list = new ArrayList<OrderBean>();
					list.add(orderBean);
				}
			}
			
			paramMap.put("list", list);
			sqlMap.insert("insert_pm_order_data", paramMap);
			sqlMap.commitTransaction();
            sqlMap.endTransaction();
            
			paramMap.put("lineType", 0);
			List<OrderLineBean> lineBeans  = sqlMapSap.queryForList("query_DP_V_SO_LINE_4_PMS");
			sqlMap.startTransaction();
			sqlMap.delete("delete_pm_order_line");
			List<OrderLineBean> list2 = new ArrayList<OrderLineBean>();
			int k = 0;
			for(OrderLineBean lineBean : lineBeans){
				if(k< 2000){
					k ++;
					list2.add(lineBean);
				}else{
					paramMap.put("lineList", list2);
					sqlMap.insert("insert_pm_order_line" ,paramMap);
					k = 0;
					list2 = new ArrayList<OrderLineBean>();
					list2.add(lineBean);
				}
			}
			paramMap.put("lineList", list2);
			sqlMap.insert("insert_pm_order_line" ,paramMap);
			sqlMap.commitTransaction();
            sqlMap.endTransaction();
            
			// 刷新同步退货数据
			paramMap.put("orderType", 1);
			List<OrderBean> rmaOrderBeans = sqlMapSap.queryForList("query_DP_V_RMA_ORDER_4_PMS");
			List<OrderBean> rmaList = new ArrayList<OrderBean>();
			int l = 0;
			sqlMap.startTransaction();
			for(OrderBean bean : rmaOrderBeans){
				if(l< 2000){
					rmaList.add(bean);
					l++;
				}else{
					paramMap.put("list", rmaList);
					sqlMap.insert("insert_pm_order_data", paramMap);
					l = 0;
					rmaList = new ArrayList<OrderBean>();
					rmaList.add(bean);
				}
			}
			paramMap.put("list", rmaList);
			sqlMap.insert("insert_pm_order_data", paramMap);
			sqlMap.commitTransaction();
            sqlMap.endTransaction();
            
			paramMap.put("lineType", 1);
			List<OrderLineBean> rmaLineBeans  = sqlMapSap.queryForList("query_DP_V_RMA_LINE_4_PMS");
			List<OrderLineBean> rmaList2 = new ArrayList<OrderLineBean>();
			int m = 0;
			sqlMap.startTransaction();
			for(OrderLineBean lBean : rmaLineBeans){
				if(m < 2000){
					m++;
					rmaList2.add(lBean);
				}else{
					paramMap.put("lineList", rmaList2);
					sqlMap.insert("insert_pm_order_line" ,paramMap);
					rmaList2 = new ArrayList<OrderLineBean>();
					rmaList2.add(lBean);
					m = 0;
				}
			}
			paramMap.put("lineList", rmaList2);
			sqlMap.insert("insert_pm_order_line" ,paramMap);
			sqlMap.commitTransaction();
            sqlMap.endTransaction();
            
			//刷新已创建项目设备信息
			//没做拆分的合同做更新
//		    // 原来的逐条查询更新太影响效率，更新时间太长，废弃
//			List<Product> productList = sqlMap.queryForList("query_pm_project_product_line");
//			List<OrderLineBean> lbs = null;
//			Map<String, Object> productMap = new HashMap<String, Object>();
//			sqlMap.startTransaction();
//			List<Product> newList = new ArrayList<>(productList.size());
//			for(Product product : productList){
//				  lbs = sqlMapSap.queryForList("query_2_DP_V_SO_LINE_4_PMS", product.getContractNo());
//				  if(lbs!=null && lbs.size() >0){
//					  //删除原有数据
//					  sqlMap.delete("delete_2_pm_project_product_line" ,product );
//					  //插入新数据
//					  productMap.put("projectId", product.getProjectId());
//					  productMap.put("contractNo", product.getContractNo());
//					  productMap.put("productlist", lbs);
//					  sqlMap.insert("insert_2_pm_project_product_line", productMap );
//				  }
//			}
		    
            LOGGER.debug("#SAP订单信息同步完成，开始拆分总代借货项目订单信息");
		    // 拆分总代借货项目订单信息
		    sqlMap.update("callSplitSoleAgentLendOrderInfo");
		    ProjectService projectService = ctx.getBean("projectService", ProjectService.class);
            projectService.updateSoleAgentLendProject();
		    
            LOGGER.debug("#拆分总代借货项目订单信息完成，开始更新项目设备清单");
            sqlMap.startTransaction();
		    // 创建需要更新的项目合同号、projectId临时表
		    sqlMap.insert("createTempNeedUpdateProject");
		    // 删除需要更新的项目的产品信息
		    sqlMap.delete("deleteOldProductLines");
		    // 重新更新产品信息的自增序列，避免id不连续
		    sqlMap.update("updateProductLineId");
		    sqlMap.insert("resetProductLineAutoId");
		    // 插入需要更新的项目的产品信息
		    sqlMap.insert("insertNewProductLines");
		    // 删除临时表
		    sqlMap.delete("dropTempNeedUpdateProject");
		    
			sqlMap.commitTransaction();
			sqlMap.endTransaction();
			
			LOGGER.debug("#数据同步完成，记录日志");
			//更新成功日志
			paramMap.put("id", Integer.parseInt(obj.toString()));
			paramMap.put("refreshTo", new Date());
			paramMap.put("refreshState", 1);
			sqlMap.update("update_fnd_data_refresh_log_success", paramMap);
		} catch (Exception e) {
		    LOGGER.error("#SAP数据同步发生异常", e);
            if (sqlMap != null) {
                try {
                    sqlMap.getCurrentConnection().rollback();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
			//更新失败日志
			paramMap.put("refreshException", ExceptionUtils.getStackTrace(e));
			paramMap.put("id", Integer.parseInt(obj.toString()));
			sqlMap.update("update_fnd_data_refresh_log_fail", paramMap);
		} finally {
			sqlMap.endTransaction();
			LOGGER.debug("#数据同步结束");
		}
	}
	/**
	 * test
	 * @param arg0
	 * @throws IOException
	 * @throws SQLException
	 */
	public  static void main(String[] arg0) throws IOException, SQLException{
	    new GainOrderBySAP().work();
	}
}
