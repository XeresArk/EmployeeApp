package com.employeeapp.controller;

import com.employeeapp.entities.EmployeeEntity;
import com.employeeapp.service.EmployeeAuditService;
import com.employeeapp.service.EmployeeHelperService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeInfoController.class)
class EmployeeInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeHelperService helper;

    @MockBean
    private EmployeeAuditService auditService;

    private EmployeeEntity mockEmployee(Long id, String dept, boolean active) {
        EmployeeEntity e = new EmployeeEntity();
        e.setId(id);
        e.setName("Emp" + id);
        e.setActive(active);
        e.setDepartment(dept);
        return e;
    }

    // -------------------------------------------
    // TEST 1: Get employee by ID
    // -------------------------------------------
    @Test
    void testGetEmployee() throws Exception {

        EmployeeEntity emp = mockEmployee(1L, "IT", true);

        when(helper.getEmployee(1L)).thenReturn(emp);

        mockMvc.perform(get("/api/employee/info/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.department").value("IT"))
                .andExpect(jsonPath("$.name").value("Emp1"));

        Mockito.verify(helper).getEmployee(1L);
    }

    // -------------------------------------------
    // TEST 2: Search employees by department
    // -------------------------------------------
    @Test
    void testSearchEmployees() throws Exception {

        List<EmployeeEntity> audited = Arrays.asList(
                mockEmployee(1L, "HR", true),
                mockEmployee(2L, "IT", true),
                mockEmployee(3L, "IT", false)
        );

        when(auditService.auditEmployees()).thenReturn(audited);

        mockMvc.perform(get("/api/employee/info/search")
                        .param("department", "IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));

        Mockito.verify(auditService).auditEmployees();
    }

    // -------------------------------------------
    // TEST 3: Summary count (active + all audited employees)
    // -------------------------------------------
    @Test
    void testGetActiveCount() throws Exception {

        when(helper.getActiveCount()).thenReturn(3);
        when(auditService.auditEmployees()).thenReturn(
                Arrays.asList(
                        mockEmployee(1L, "HR", true),
                        mockEmployee(2L, "IT", false)
                )
        );

        mockMvc.perform(get("/api/employee/info/summary"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));  // 3 active + 2 audited

        Mockito.verify(helper).getActiveCount();
        Mockito.verify(auditService).auditEmployees();
    }

    // -------------------------------------------
    // TEST 4: Find all employees
    // -------------------------------------------
    @Test
    void testFindAllEmployees() throws Exception {

        List<EmployeeEntity> all = Arrays.asList(
                mockEmployee(1L, "HR", true),
                mockEmployee(2L, "Finance", false)
        );

        when(helper.findAllEmployees()).thenReturn(all);

        mockMvc.perform(get("/api/employee/info/findAllEmployees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].department").value("Finance"));

        Mockito.verify(helper).findAllEmployees();
    }
}
