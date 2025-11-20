package com.employeeapp.controller;

import com.employeeapp.entities.EmployeeEntity;
import com.employeeapp.service.EmployeeAdminService;
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
import static org.mockito.Mockito.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeAdminController.class)
class EmployeeAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeHelperService helper;

    @MockBean
    private EmployeeAdminService adminService;

    @MockBean
    private EmployeeAuditService auditService;

    @MockBean
    private EmployeeActionController actionController;

    private EmployeeEntity mockEmployee(Long id) {
        EmployeeEntity e = new EmployeeEntity();
        e.setId(id);
        e.setName("John");
        e.setActive(true);
        return e;
    }

    // ----------------------------------------------------
    // TEST 1: Promote Employee
    // ----------------------------------------------------
    @Test
    void testPromoteEmployee() throws Exception {

        when(adminService.promoteEmployee(10L, "Manager")).thenReturn(true);

        mockMvc.perform(post("/api/employee/admin/promote/10")
                        .param("newRole", "Manager")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(adminService).promoteEmployee(10L, "Manager");
    }

    // ----------------------------------------------------
    // TEST 2a: Deactivate Employee (SUCCESS)
    // ----------------------------------------------------
    @Test
    void testDeactivateEmployee_Success() throws Exception {

        when(adminService.deactivateEmployee(5L)).thenReturn(true);
        when(actionController.deleteEmployee(5L)).thenReturn(true);

        mockMvc.perform(post("/api/employee/admin/deactivate/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(adminService).deactivateEmployee(5L);
        verify(actionController).deleteEmployee(5L);
    }

    // ----------------------------------------------------
    // TEST 2b: Deactivate Employee (FAIL)
    // ----------------------------------------------------
    @Test
    void testDeactivateEmployee_Failure() throws Exception {

        when(adminService.deactivateEmployee(5L)).thenReturn(false);

        mockMvc.perform(post("/api/employee/admin/deactivate/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        // ❌ Should NOT call delete (because deactivated=false)
        verify(actionController, never()).deleteEmployee(anyLong());
    }

    // ----------------------------------------------------
    // TEST 3: Audit Employees
    // ----------------------------------------------------
    @Test
    void testAuditEmployees() throws Exception {

        List<EmployeeEntity> auditList =
                Arrays.asList(mockEmployee(1L), mockEmployee(2L));

        when(auditService.auditEmployees()).thenReturn(auditList);

        mockMvc.perform(get("/api/employee/admin/audit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(auditService).auditEmployees();
    }
}
