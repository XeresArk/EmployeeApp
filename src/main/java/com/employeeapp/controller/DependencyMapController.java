package com.employeeapp.controller;

import com.employeeapp.dependencymap.SimplifiedDependencyMapGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dependency-map")
public class DependencyMapController {
    @Autowired
    private SimplifiedDependencyMapGenerator generator;

    @GetMapping("/generate")
    public List<Map<String, Object>> generateDependencyMap() throws Exception {
        return generator.generateDependencyMap();
    }

    @GetMapping("/generateJson")
    public String generateDependencyMapJson() throws Exception {
        generator.generateDependencyMapToFile();
        return "Simplified dependency map generated: advanced-dependency-map.json";
    }
}
