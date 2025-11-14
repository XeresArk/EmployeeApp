package com.employeeapp.controller;

import com.employeeapp.service.EmployeeHelperService;
import com.employeeapp.entities.EmployeeEntity;
import com.employeeapp.service.EmployeeAdminService;
import com.employeeapp.service.EmployeeAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee/admin")
public class EmployeeAdminController {
    @Autowired
    private EmployeeHelperService helper;
    @Autowired
    private EmployeeAdminService adminService;
    @Autowired
    private EmployeeAuditService auditService;
    @Autowired
    private EmployeeActionController actionController;

    // API 1: Promote employee (uses adminService, helper, DTO)
    @PostMapping("/promote/{id}")
    public boolean promoteEmployee(@PathVariable Long id, @RequestParam String newRole) {
        return adminService.promoteEmployee(id, newRole);
    }

    // API 2: Deactivate employee (uses adminService, calls action API for delete)
    @PostMapping("/deactivate/{id}")
    public boolean deactivateEmployee(@PathVariable Long id) {
        boolean deactivated = adminService.deactivateEmployee(id);
        if (deactivated) {
            actionController.deleteEmployee(id); // cross-controller invocation
        }
        return deactivated;
    }

    // API 3: Audit employees (uses auditService, helper, DTO)
    @GetMapping("/audit")
    public List<EmployeeEntity> auditEmployees() {
        return auditService.auditEmployees();
    }
}
