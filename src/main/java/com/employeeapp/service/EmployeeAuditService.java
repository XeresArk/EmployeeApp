package com.employeeapp.service;

import com.employeeapp.dto.EmployeeDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeAuditService {
    private final EmployeeHelperService helper;

    public EmployeeAuditService(EmployeeHelperService helper) {
        this.helper = helper;
    }

    public List<EmployeeDto> auditEmployees() {
        // Delegates to helper
        return helper.getAllEmployees();
    }
}
