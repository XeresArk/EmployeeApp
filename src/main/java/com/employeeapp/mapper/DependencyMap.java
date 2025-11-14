package com.employeeapp.mapper;

import java.util.List;

import lombok.Data;

@Data
public class DependencyMap {
    private String project;
    private String version = "1.0";
    private String generatedAt;
    private List<ParsedFile> files;
}
