package com.employeeapp.mapper;

import java.util.List;

import lombok.Data;

@Data
public class ParsedField {
    private String name;
    private String type;
    private List<String> annotations;
}