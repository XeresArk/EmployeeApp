package com.employeeapp.controller;

import com.employeeapp.dependencymap.SimplifiedDependencyMapGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DependencyMapController.class)
public class DependencyMapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SimplifiedDependencyMapGenerator generator;

    private List<Map<String, Object>> mockResponse;

    @BeforeEach
    void setUp() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("table", "EMPLOYEE");
        entry.put("dependsOn", Arrays.asList("DEPARTMENT", "ROLE"));
        mockResponse = Arrays.asList(entry);
    }

    @Test
    void testGenerateDependencyMap() throws Exception {
        when(generator.generateDependencyMap()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/dependency-map/generate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].table").value("EMPLOYEE"))
                .andExpect(jsonPath("$[0].dependsOn[0]").value("DEPARTMENT"))
                .andExpect(jsonPath("$[0].dependsOn[1]").value("ROLE"));
    }

    @Test
    void testGenerateDependencyMapJson() throws Exception {
        Mockito.doNothing().when(generator).generateDependencyMapToFile();

        mockMvc.perform(get("/api/dependency-map/generateJson"))
                .andExpect(status().isOk())
                .andExpect(content().string("Simplified dependency map generated: advanced-dependency-map.json"));
    }
}
