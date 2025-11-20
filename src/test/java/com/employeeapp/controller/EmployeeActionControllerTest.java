package com.employeeapp.controller;

import com.employeeapp.dto.EmployeeRequest;
import com.employeeapp.entities.EmployeeEntity;
import com.employeeapp.service.EmployeeHelperService;
import com.employeeapp.service.EmployeeAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeActionController.class)
class EmployeeActionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeHelperService helper;

    @MockBean
    private EmployeeAdminService adminService;

    @MockBean
    private EmployeeInfoController infoController;

    private EmployeeEntity mockEmployee(Long id) {
        EmployeeEntity e = new EmployeeEntity();
        e.setId(id);
        e.setName("John");
        e.setActive(true);
        return e;
    }

    // ----------------------------------------------------
    // TEST 1: Add employee
    // ----------------------------------------------------
    @Test
    void testAddEmployee() throws Exception {

        EmployeeEntity saved = mockEmployee(1L);

        when(helper.addEmployee(any(EmployeeRequest.class)))
                .thenReturn(saved);

        // FIXED: doNothing() removed → use thenReturn()
        when(adminService.promoteEmployee(1L, "Staff"))
                .thenReturn(true);

        mockMvc.perform(post("/api/employee/action/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\", \"departmentId\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"));

        verify(helper).addEmployee(any(EmployeeRequest.class));
        verify(adminService).promoteEmployee(1L, "Staff");
    }

    // ----------------------------------------------------
    // TEST 2: Update employee
    // ----------------------------------------------------
    @Test
    void testUpdateEmployee() throws Exception {

        EmployeeEntity updated = mockEmployee(5L);
        updated.setName("Updated Name");

        when(helper.updateEmployee(eq(5L), any(EmployeeRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/employee/action/update/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\", \"departmentId\":20}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(helper).updateEmployee(eq(5L), any(EmployeeRequest.class));
    }

    // ----------------------------------------------------
    // TEST 3a: Delete employee (success)
    // ----------------------------------------------------
    @Test
    void testDeleteEmployee_Success() throws Exception {

        EmployeeEntity activeEmp = mockEmployee(3L);

        when(infoController.getEmployee(3L)).thenReturn(activeEmp);

        // FIXED: doNothing() removed → use thenReturn()
        when(adminService.deactivateEmployee(3L)).thenReturn(true);

        when(helper.deleteEmployee(3L)).thenReturn(true);

        mockMvc.perform(delete("/api/employee/action/delete/3"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(adminService).deactivateEmployee(3L);
        verify(helper).deleteEmployee(3L);
    }

    // ----------------------------------------------------
    // TEST 3b: Delete employee (inactive or null)
    // ----------------------------------------------------
    @Test
    void testDeleteEmployee_Failure() throws Exception {

        when(infoController.getEmployee(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/employee/action/delete/99"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(adminService, never()).deactivateEmployee(anyLong());
        verify(helper, never()).deleteEmployee(anyLong());
    }
}
