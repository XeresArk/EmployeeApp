package com.employeeapp.mapper;

import lombok.Data;

@Data
public class InjectedBean {
    private String className;
    private String beanName;
    private String beanType;
    private String injectionType;
}
