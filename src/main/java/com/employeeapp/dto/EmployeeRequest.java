package com.employeeapp.dto;

import lombok.Data;

@Data
public class EmployeeRequest {
    private String name;
    private String department;
    private String role;
}
