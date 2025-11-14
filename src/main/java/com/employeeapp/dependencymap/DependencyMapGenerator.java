package com.employeeapp.dependencymap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

@Component
public class DependencyMapGenerator implements CommandLineRunner {
    @Autowired
    private ApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        DependencyMap map = new DependencyMap();
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);
            Class<?> clazz = bean.getClass();
            if (clazz.getName().contains("$$")) {
                clazz = clazz.getSuperclass(); // handle Spring proxies
            }
            map.addClass(clazz, bean);
        }
        // Output JSON
        ObjectMapper mapper = new ObjectMapper();
        String outPath = "D:/Work/DependencyMap/dependency-map.json";
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outPath), map.toJson());
        System.out.println("Dependency map generated: " + outPath);
    }
}
