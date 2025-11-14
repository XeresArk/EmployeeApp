package com.employeeapp.mapper;

import lombok.Data;

@Data
public class EntityAccess {
    private String method;
    private String entity;
    private String accessType;
}
