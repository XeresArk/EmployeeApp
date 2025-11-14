package com.employeeapp.controller;

import com.employeeapp.dto.EmployeeDto;
import com.employeeapp.dto.EmployeeRequest;
import com.employeeapp.service.EmployeeHelperService;
import com.employeeapp.service.EmployeeAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee/action")
public class EmployeeActionController {
    @Autowired
    private EmployeeHelperService helper;
    @Autowired
    private EmployeeAdminService adminService;
    @Autowired
    private EmployeeInfoController infoController;

    // API 1: Add employee (uses helper, DTO, adminService)
    @PostMapping("/add")
    public EmployeeDto addEmployee(@RequestBody EmployeeRequest req) {
        EmployeeDto emp = helper.addEmployee(req);
        adminService.promoteEmployee(emp.getId(), "Staff"); // cross-service dependency
        return emp;
    }

    // API 2: Update employee (uses helper, DTO)
    @PutMapping("/update/{id}")
    public EmployeeDto updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequest req) {
        return helper.updateEmployee(id, req);
    }

    // API 3: Delete employee (calls info API for validation, uses adminService)
    @DeleteMapping("/delete/{id}")
    public boolean deleteEmployee(@PathVariable Long id) {
        EmployeeDto emp = infoController.getEmployee(id); // cross-controller dependency
        if (emp != null && emp.isActive()) {
            adminService.deactivateEmployee(id); // cross-service dependency
            return helper.deleteEmployee(id);
        }
        return false;
    }
}
