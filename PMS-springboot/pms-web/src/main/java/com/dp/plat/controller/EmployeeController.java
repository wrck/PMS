package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.Employee;
import com.dp.plat.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工 Controller - migrated from Struts
 */
@RestController
@RequestMapping("/api/ehr/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list")
    public R<IPage<Employee>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(employeeService.queryPage(pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public R<Employee> detail(@PathVariable Long id) {
        return R.ok(employeeService.getById(id));
    }

    @PostMapping
    public R<Void> add(@RequestBody Employee entity) {
        employeeService.add(entity);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody Employee entity) {
        employeeService.update(entity);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return R.ok();
    }

    @GetMapping("/all")
    public R<List<Employee>> listAll() {
        return R.ok(employeeService.listAll());
    }
}
