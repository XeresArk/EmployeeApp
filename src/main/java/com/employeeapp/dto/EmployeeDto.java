package com.employeeapp.dto;

import lombok.Data;

@Data
public class EmployeeDto {
    private Long id;
    private String name;
    private String department;
    private boolean active;
    private String role;
}
