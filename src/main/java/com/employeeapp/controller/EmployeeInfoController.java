package com.employeeapp.controller;

import com.employeeapp.service.EmployeeHelperService;
import com.employeeapp.entities.EmployeeEntity;
import com.employeeapp.service.EmployeeAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee/info")
public class EmployeeInfoController {
    @Autowired
    private EmployeeHelperService helper;
    @Autowired
    private EmployeeAuditService auditService;

    // API 1: Get employee info (uses helper, DTO)
    @GetMapping("/{id}")
    public EmployeeEntity getEmployee(@PathVariable Long id) {
        return helper.getEmployee(id);
    }

    // API 2: Search employees by department (uses helper, DTO, auditService)
    @GetMapping("/search")
    public List<EmployeeEntity> searchEmployees(@RequestParam String department) {
        List<EmployeeEntity> audited = auditService.auditEmployees();
        // filter audited employees by department
        return audited.stream().filter(e -> e.getDepartment().equalsIgnoreCase(department)).toList();
    }

    // API 3: Get summary (uses helper, auditService)
    @GetMapping("/summary")
    public int getActiveCount() {
        return helper.getActiveCount() + auditService.auditEmployees().size();
    }

    @GetMapping("/findAllEmployees")
    public List<EmployeeEntity> findAllEmployees() {
        return helper.findAllEmployees();
    }
}
