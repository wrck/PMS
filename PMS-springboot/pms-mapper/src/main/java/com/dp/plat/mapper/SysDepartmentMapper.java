package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.SysDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {

    /** 根据部门编码查询部门 */
    @Select("SELECT * FROM fnd_department WHERE department_num = #{deptCode} LIMIT 1")
    SysDepartment selectByDeptCode(@Param("deptCode") String deptCode);

    /** 根据父部门ID查询子部门 */
    @Select("SELECT * FROM fnd_department WHERE parent_id = #{parentId} ORDER BY sort")
    List<SysDepartment> selectByParentId(@Param("parentId") Long parentId);
}
