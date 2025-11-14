package com.employeeapp.mapper;

public class DependencyMapGenerator {
    public static void main(String[] args) throws Exception {
        String projectPath = args.length > 0 ? args[0] : "D:\\Work\\EmployeeApp";
        String outputFile = "./dependency-map-new.json";

        System.out.println("Scanning: " + projectPath);

        ProjectScanner scanner = new ProjectScanner(projectPath);
        DependencyMap map = scanner.scan();

        JsonWriter.write(outputFile, map);

        System.out.println("Dependency map generated at: " + outputFile);
    }
}
