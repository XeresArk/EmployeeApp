package com.employeeapp.service;

import org.springframework.stereotype.Service;

import com.employeeapp.entities.EmployeeEntity;

import java.util.List;

@Service
public class EmployeeAuditService {
    private final EmployeeHelperService helper;

    public EmployeeAuditService(EmployeeHelperService helper) {
        this.helper = helper;
    }

    public List<EmployeeEntity> auditEmployees() {
        // Delegates to helper
        return helper.getAllEmployees();
    }
}
