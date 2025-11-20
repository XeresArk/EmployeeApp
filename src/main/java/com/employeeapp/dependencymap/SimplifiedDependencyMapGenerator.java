package com.employeeapp.dependencymap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SimplifiedDependencyMapGenerator implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        generateDependencyMapToFile();
    }

    public List<Map<String, Object>> generateDependencyMap() throws Exception {
        String srcDir = System.getProperty("user.dir") + "/src/main/java";
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        ParserConfiguration parserConfig = new ParserConfiguration();
        parserConfig.setSymbolResolver(symbolSolver);

        List<Map<String, Object>> classList = new ArrayList<>();
        Files.walk(Paths.get(srcDir))
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        JavaParser parser = new JavaParser(parserConfig);
                        ParseResult<CompilationUnit> result = parser.parse(path.toFile());
                        if (result.isSuccessful() && result.getResult().isPresent()) {
                            CompilationUnit cu = result.getResult().get();
                            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                                Map<String, Object> classInfo = new LinkedHashMap<>();
                                classInfo.put("name", clazz.getNameAsString());
                                classInfo.put("package", cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse(""));
                                classInfo.put("annotations", getAnnotations(clazz.getAnnotations()));
                                classInfo.put("fields", getFields(clazz));
                                classInfo.put("methods", getMethods(clazz));
                                classInfo.put("dependencies", getDependencies(clazz));
                                classList.add(classInfo);
                            });
                        } else {
                            System.err.println("Parse failed for file: " + path);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing file: " + path + ", " + e.getMessage());
                    }
                });

        // ClassGraph for runtime beans, endpoints, repository/entity usage
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
            // Optionally process scanResult if needed
        } catch (Exception e) {
            System.err.println("ClassGraph scan error: " + e.getMessage());
        }

        return classList;
    }

    public void generateDependencyMapToFile() throws Exception {
        List<Map<String, Object>> classList = generateDependencyMap();
        ObjectMapper mapper = new ObjectMapper();
        String outPath = "./simplifiedDependencyMap.json";
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outPath), classList);
        System.out.println("Simplified dependency map generated: " + outPath);
    }

    private List<String> getAnnotations(List<AnnotationExpr> annotations) {
        List<String> result = new ArrayList<>();
        for (AnnotationExpr a : annotations) {
            result.add(a.getNameAsString());
        }
        return result;
    }

    private List<Map<String, Object>> getFields(ClassOrInterfaceDeclaration clazz) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (FieldDeclaration f : clazz.getFields()) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("name", f.getVariable(0).getNameAsString());
            info.put("type", f.getVariable(0).getType().asString());
            info.put("annotations", getAnnotations(f.getAnnotations()));
            result.add(info);
        }
        return result;
    }

    private List<Map<String, Object>> getMethods(ClassOrInterfaceDeclaration clazz) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (MethodDeclaration m : clazz.getMethods()) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("name", m.getNameAsString());
            info.put("returnType", m.getType().asString());
            info.put("parameters", m.getParameters().toString());
            info.put("annotations", getAnnotations(m.getAnnotations()));
            info.put("calls", getMethodCalls(m));
            result.add(info);
        }
        return result;
    }

    private List<String> getMethodCalls(MethodDeclaration m) {
        // Use JavaParser SymbolSolver to resolve method calls (can be extended for more detail)
        List<String> calls = new ArrayList<>();
        m.findAll(com.github.javaparser.ast.expr.MethodCallExpr.class).forEach(call -> {
            try {
                calls.add(call.resolve().getQualifiedSignature());
            } catch (Exception e) {
                calls.add(call.getNameAsString());
            }
        });
        return calls;
    }

    private List<String> getDependencies(ClassOrInterfaceDeclaration clazz) {
        List<String> deps = new ArrayList<>();
        for (FieldDeclaration f : clazz.getFields()) {
            deps.add(f.getVariable(0).getType().asString());
        }
        return deps;
    }
}
