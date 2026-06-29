package com.dp.plat.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientCallback;

import com.dp.plat.data.bean.Company;
import com.dp.plat.data.bean.Department;
import com.dp.plat.param.DepartmentParam;
import com.dp.plat.param.DisplayParam;
import com.ibatis.sqlmap.client.SqlMapExecutor;

public class DepartmentManageDaoImpl extends BaseDao implements DepartmentManageDao {
	@SuppressWarnings("unchecked")
	public List<Department> queryDepartmentList(DisplayParam displayParam,Department department) {
		displayParam.setPagesize(50);
		displayParam.setOffset((displayParam.getCurrentpage() - 1)
				* displayParam.getPagesize());
		Integer totalcount = (Integer) getSqlMapClientTemplate().
				queryForObject("query-department-count",department);
		displayParam.setTotalcount(totalcount);
		DepartmentParam departmentParam  = new DepartmentParam();
		departmentParam.setDisplayParam(displayParam);
		departmentParam.setDepartment(department);
		return (List<Department>) getSqlMapClientTemplate().queryForList(
				"query-departmentlist", departmentParam);
	}
	
	public int addDepartmentSubmit(Department department){
		return (Integer)getSqlMapClientTemplate().insert("insert-departmentObject",department);
	}
	
	@SuppressWarnings("unchecked")
	public void refreshDepartment(){
		try {
			getSqlMapClientTemplate().getSqlMapClient().startTransaction();
			//获得部门信息
			final List<Department>departmentList=(List<Department>)getSqlMapClientTemplateSAP().queryForList("query-sap-departmentList");
			
			//清空部门表
			getSqlMapClientTemplate().delete("truncate_department");
			
			//批量插入
			getSqlMapClientTemplate().execute(new SqlMapClientCallback<Object>() {
				
				@Override
				public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
					executor.startBatch();
					int batch=0;
					for(Department department:departmentList){
						executor.insert("insert-departmentObject",department);
						if(batch==500){
							executor.executeBatch();
							batch=0;
						}
						batch++;
					}
					executor.executeBatch();
					return null;
				}
			});

			getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			try {
				getSqlMapClientTemplate().getSqlMapClient().endTransaction();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Department> queryAllDepartments(Department department) {
		return getSqlMapClientTemplate().queryForList("query_all_department", department);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> queryDepartmentMap() {
		return getSqlMapClientTemplate().queryForMap("query_department_map", null, "officeCode", "officeName");
	}

	@Override
	public Department queryDepartmentByDepartmentNum(String officeCode) {
		return (Department) getSqlMapClientTemplate().queryForObject("queryDepartmentByDepartmentNum", officeCode);
	}

    @SuppressWarnings("unchecked")
    @Override
    public List<Company> queryCompanyList(Company company) {
        return (List<Company>) getSqlMapClientTemplate().queryForList("queryCompanyList", company);
    }

    @Override
    public Company queryCompanyOne(Company company) {
        return (Company) getSqlMapClientTemplate().queryForObject("queryCompanyOne", company);
    }
}
