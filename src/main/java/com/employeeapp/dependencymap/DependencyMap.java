package com.employeeapp.dependencymap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class DependencyMap {
    private final List<Map<String, Object>> classes = new ArrayList<>();

    public void addClass(Class<?> clazz, Object bean) {
        Map<String, Object> classInfo = new LinkedHashMap<>();
        classInfo.put("name", clazz.getName());
        classInfo.put("package", clazz.getPackage().getName());
        classInfo.put("annotations", getAnnotations(clazz));
        classInfo.put("fields", getFields(clazz));
        classInfo.put("methods", getMethods(clazz));
        classInfo.put("beanInjections", getBeanInjections(clazz));
        classInfo.put("endpoints", getEndpoints(clazz));
        classInfo.put("dependencies", getDependencies(clazz));
        classes.add(classInfo);
    }

    public List<Map<String, Object>> toJson() {
        return classes;
    }

    private List<String> getAnnotations(Class<?> clazz) {
        List<String> result = new ArrayList<>();
        for (Annotation a : clazz.getAnnotations()) {
            result.add(a.annotationType().getSimpleName());
        }
        return result;
    }

    private List<Map<String, Object>> getFields(Class<?> clazz) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("name", f.getName());
            info.put("type", f.getType().getName());
            info.put("annotations", Arrays.toString(f.getAnnotations()));
            result.add(info);
        }
        return result;
    }

    private List<Map<String, Object>> getMethods(Class<?> clazz) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Method m : clazz.getDeclaredMethods()) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("name", m.getName());
            info.put("returnType", m.getReturnType().getName());
            info.put("parameters", Arrays.toString(m.getParameterTypes()));
            info.put("calls", getMethodCalls(m));
            info.put("annotations", Arrays.toString(m.getAnnotations()));
            result.add(info);
        }
        return result;
    }

    private List<String> getMethodCalls(Method m) {
        // Static analysis for method calls is limited in reflection; placeholder for integration with JavaParser
        return Collections.emptyList();
    }

    private List<String> getBeanInjections(Class<?> clazz) {
        List<String> result = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            for (Annotation a : f.getAnnotations()) {
                if (a.annotationType().getSimpleName().equals("Autowired") || a.annotationType().getSimpleName().equals("Inject")) {
                    result.add(f.getType().getName());
                }
            }
        }
        return result;
    }

    private List<Map<String, Object>> getEndpoints(Class<?> clazz) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (clazz.isAnnotationPresent(RestController.class)) {
            for (Method m : clazz.getDeclaredMethods()) {
                Map<String, Object> info = new LinkedHashMap<>();
                if (m.isAnnotationPresent(RequestMapping.class) || m.isAnnotationPresent(GetMapping.class) || m.isAnnotationPresent(PostMapping.class) || m.isAnnotationPresent(PutMapping.class) || m.isAnnotationPresent(DeleteMapping.class)) {
                    info.put("method", m.getName());
                    info.put("path", getEndpointPath(m));
                    info.put("httpMethod", getHttpMethod(m));
                    result.add(info);
                }
            }
        }
        return result;
    }

    private String getEndpointPath(Method m) {
        if (m.isAnnotationPresent(RequestMapping.class)) {
            return Arrays.toString(m.getAnnotation(RequestMapping.class).value());
        } else if (m.isAnnotationPresent(GetMapping.class)) {
            return Arrays.toString(m.getAnnotation(GetMapping.class).value());
        } else if (m.isAnnotationPresent(PostMapping.class)) {
            return Arrays.toString(m.getAnnotation(PostMapping.class).value());
        } else if (m.isAnnotationPresent(PutMapping.class)) {
            return Arrays.toString(m.getAnnotation(PutMapping.class).value());
        } else if (m.isAnnotationPresent(DeleteMapping.class)) {
            return Arrays.toString(m.getAnnotation(DeleteMapping.class).value());
        }
        return "";
    }

    private String getHttpMethod(Method m) {
        if (m.isAnnotationPresent(GetMapping.class)) return "GET";
        if (m.isAnnotationPresent(PostMapping.class)) return "POST";
        if (m.isAnnotationPresent(PutMapping.class)) return "PUT";
        if (m.isAnnotationPresent(DeleteMapping.class)) return "DELETE";
        if (m.isAnnotationPresent(RequestMapping.class)) return Arrays.toString(m.getAnnotation(RequestMapping.class).method());
        return "";
    }

    private List<String> getDependencies(Class<?> clazz) {
        List<String> result = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            result.add(f.getType().getName());
        }
        return result;
    }
}
