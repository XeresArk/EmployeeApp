package com.employeeapp.mapper;

import java.util.List;

import lombok.Data;

@Data
public class ParsedMethod {
    private String name;
    private String signature;
    private String returnType;
    private String declaredIn;
    private List<String> parameters;
    private List<String> annotations;
    private int startLine;
    private int endLine;
}
