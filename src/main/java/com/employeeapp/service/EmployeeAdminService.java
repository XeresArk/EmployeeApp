package com.employeeapp.service;

import org.springframework.stereotype.Service;

@Service
public class EmployeeAdminService {
    private final EmployeeHelperService helper;

    public EmployeeAdminService(EmployeeHelperService helper) {
        this.helper = helper;
    }

    public boolean promoteEmployee(Long id, String newRole) {
        return helper.promoteEmployee(id, newRole);
    }

    public boolean deactivateEmployee(Long id) {
        return helper.deactivateEmployee(id);
    }
}
