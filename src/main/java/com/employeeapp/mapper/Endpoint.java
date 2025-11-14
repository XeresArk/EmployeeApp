package com.employeeapp.mapper;

import lombok.Data;

@Data
public class Endpoint {
    private String controller;
    private String method;
    private String httpMethod;
    private String path;
}
