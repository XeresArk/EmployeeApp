package com.employeeapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class EmployeeAdminServiceTest {

    private EmployeeHelperService helper;
    private EmployeeAdminService adminService;

    @BeforeEach
    void setUp() {
        helper = Mockito.mock(EmployeeHelperService.class);
        adminService = new EmployeeAdminService(helper);
    }

    // ---------------------------------------------------------
    // TEST 1: promoteEmployee()
    // ---------------------------------------------------------
    @Test
    void testPromoteEmployee() {

        when(helper.promoteEmployee(10L, "Manager"))
                .thenReturn(true);

        boolean result = adminService.promoteEmployee(10L, "Manager");

        assertThat(result).isTrue();
        verify(helper).promoteEmployee(10L, "Manager");
    }

    // ---------------------------------------------------------
    // TEST 2: deactivateEmployee()
    // ---------------------------------------------------------
    @Test
    void testDeactivateEmployee() {

        when(helper.deactivateEmployee(5L)).thenReturn(true);

        boolean result = adminService.deactivateEmployee(5L);

        assertThat(result).isTrue();
        verify(helper).deactivateEmployee(5L);
    }

    // ---------------------------------------------------------
    // TEST 3: promoteEmployee() failure
    // ---------------------------------------------------------
    @Test
    void testPromoteEmployee_Failure() {

        when(helper.promoteEmployee(10L, "Lead"))
                .thenReturn(false);

        boolean result = adminService.promoteEmployee(10L, "Lead");

        assertThat(result).isFalse();
        verify(helper).promoteEmployee(10L, "Lead");
    }

    // ---------------------------------------------------------
    // TEST 4: deactivateEmployee() failure
    // ---------------------------------------------------------
    @Test
    void testDeactivateEmployee_Failure() {

        when(helper.deactivateEmployee(22L)).thenReturn(false);

        boolean result = adminService.deactivateEmployee(22L);

        assertThat(result).isFalse();
        verify(helper).deactivateEmployee(22L);
    }
}
