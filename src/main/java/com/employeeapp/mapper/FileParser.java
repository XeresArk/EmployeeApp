package com.employeeapp.mapper;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

public class FileParser {

    public static ParsedFile parseFile(File file) throws Exception {
        String code = Files.readString(file.toPath());
        CompilationUnit cu = StaticJavaParser.parse(code);

        ParsedFile parsed = new ParsedFile();
        parsed.setPath(file.getPath());
        parsed.setPackageName(cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse(""));
        parsed.setImports(parseImports(cu));

        List<ParsedClass> classes = new ArrayList<>();
        List<ParsedMethod> methods = new ArrayList<>();
        List<MethodCall> calls = new ArrayList<>();
        List<Endpoint> endpoints = new ArrayList<>();
        List<InjectedBean> injections = new ArrayList<>();
        List<EntityAccess> entityAccesses = new ArrayList<>();

        // Parse classes
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> {
            ParsedClass pc = new ParsedClass();
            pc.setName(c.getNameAsString());
            pc.setQualifiedName(parsed.getPackageName() + "." + pc.getName());
            pc.setAnnotations(parseAnnotations(c));
            pc.setBean(isSpringBean(c));
            pc.setFields(parseFields(c));
            classes.add(pc);
        });

        // Parse methods + metadata
        cu.findAll(MethodDeclaration.class).forEach(m -> {
            ParsedMethod pm = new ParsedMethod();
            pm.setName(m.getNameAsString());
            pm.setSignature(m.getDeclarationAsString());
            pm.setReturnType(m.getTypeAsString());
            pm.setDeclaredIn(findDeclaringClass(cu, m));
            pm.setParameters(extractParams(m));
            pm.setAnnotations(parseAnnotations(m));
            pm.setStartLine(m.getBegin().map(p -> p.line).orElse(-1));
            pm.setEndLine(m.getEnd().map(p -> p.line).orElse(-1));
            methods.add(pm);
        });

        // Parse method calls
        cu.findAll(MethodCallExpr.class).forEach(call -> {
            MethodCall mc = new MethodCall();
            mc.setCaller(findCaller(methods, call));
            mc.setCallee(call.getNameAsString());
            calls.add(mc);
        });

        // Parse endpoints (Controller)
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> {
            if (c.isAnnotationPresent("RestController")) {
                endpoints.addAll(parseEndpoints(c));
            }
        });

        parsed.setClasses(classes);
        parsed.setMethods(methods);
        parsed.setMethodCalls(calls);
        parsed.setInjects(injections);
        parsed.setEndpoints(endpoints);
        parsed.setEntityAccess(entityAccesses);
        parsed.setAnnotations(findTopLevelAnnotations(cu));

        return parsed;
    }

    // ---- Helper Functions ----
    private static List<String> parseImports(CompilationUnit cu) {
        List<String> out = new ArrayList<>();
        cu.getImports().forEach(i -> out.add(i.getNameAsString()));
        return out;
    }

    private static List<String> parseAnnotations(NodeWithAnnotations<?> node) {
        List<String> out = new ArrayList<>();
        node.getAnnotations().forEach(a -> out.add(a.getNameAsString()));
        return out;
    }

    private static boolean isSpringBean(ClassOrInterfaceDeclaration c) {
        return c.isAnnotationPresent("Service")
            || c.isAnnotationPresent("Component")
            || c.isAnnotationPresent("RestController")
            || c.isAnnotationPresent("Controller");
    }

    private static String findDeclaringClass(CompilationUnit cu, MethodDeclaration m) {
        return cu.findFirst(ClassOrInterfaceDeclaration.class)
                .map(c -> cu.getPackageDeclaration().map(pd -> pd.getNameAsString() + ".").orElse("") + c.getNameAsString())
                .orElse("Unknown");
    }

    private static String findCaller(List<ParsedMethod> methods, MethodCallExpr expr) {
        Optional<MethodDeclaration> container = expr.findAncestor(MethodDeclaration.class);
        if (container.isPresent()) {
            MethodDeclaration md = container.get();
            return md.getNameAsString();
        }
        return "Unknown";
    }

    private static List<String> extractParams(MethodDeclaration m) {
        List<String> p = new ArrayList<>();
        m.getParameters().forEach(param -> p.add(param.getTypeAsString()));
        return p;
    }

    private static List<ParsedField> parseFields(ClassOrInterfaceDeclaration c) {
        List<ParsedField> fields = new ArrayList<>();
        c.getFields().forEach(f -> {
            f.getVariables().forEach(v -> {
                ParsedField field = new ParsedField();
                field.setName(v.getNameAsString());
                field.setType(f.getVariables().get(0).getTypeAsString());
                field.setAnnotations(parseAnnotations(f));
                fields.add(field);
            });
        });
        return fields;
    }

    private static List<String> findTopLevelAnnotations(CompilationUnit cu) {
        return cu.findFirst(ClassOrInterfaceDeclaration.class)
                .map(FileParser::parseAnnotations)
                .orElse(List.of());
    }

    private static List<Endpoint> parseEndpoints(ClassOrInterfaceDeclaration c) {
        List<Endpoint> endpoints = new ArrayList<>();
        c.findAll(MethodDeclaration.class).forEach(m -> {
            if (m.isAnnotationPresent("GetMapping") ||
                m.isAnnotationPresent("PostMapping") ||
                m.isAnnotationPresent("PutMapping") ||
                m.isAnnotationPresent("DeleteMapping")) {

                Endpoint ep = new Endpoint();
                ep.setController(c.getNameAsString());
                ep.setMethod(m.getNameAsString());

                String http = m.getAnnotations().get(0).getNameAsString();
                ep.setHttpMethod(http.replace("Mapping", "").toUpperCase());

                Expression value = m.getAnnotations().get(0).asSingleMemberAnnotationExpr().getMemberValue();
                ep.setPath(value.toString().replace("\"", ""));

                endpoints.add(ep);
            }
        });
        return endpoints;
    }
}
