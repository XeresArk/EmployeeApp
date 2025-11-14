package com.employeeapp.mapper;

import java.util.List;

import lombok.Data;

@Data
public class ParsedClass {
    private String name;
    private String qualifiedName;
    private List<String> annotations;
    private boolean isBean;
    private List<ParsedField> fields;
}
