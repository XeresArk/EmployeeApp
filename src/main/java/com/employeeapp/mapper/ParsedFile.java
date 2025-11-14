package com.employeeapp.mapper;

import java.util.List;

import lombok.Data;

@Data
public class ParsedFile {
    private String path;
    private String packageName;
    private List<String> imports;
    private List<String> annotations;
    private List<ParsedClass> classes;
    private List<ParsedMethod> methods;
    private List<MethodCall> methodCalls;
    private List<InjectedBean> injects;
    private List<EntityAccess> entityAccess;
    private List<Endpoint> endpoints;
}
