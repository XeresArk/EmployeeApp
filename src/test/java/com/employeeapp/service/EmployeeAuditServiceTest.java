package com.employeeapp.service;

import com.employeeapp.entities.EmployeeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class EmployeeAuditServiceTest {

    private EmployeeHelperService helper;
    private EmployeeAuditService auditService;

    @BeforeEach
    void setUp() {
        helper = Mockito.mock(EmployeeHelperService.class);
        auditService = new EmployeeAuditService(helper);
    }

    private EmployeeEntity emp(long id, String name) {
        EmployeeEntity e = new EmployeeEntity();
        e.setId(id);
        e.setName(name);
        return e;
    }

    // -----------------------------------------------------
    // TEST 1: auditEmployees returns full list from helper
    // -----------------------------------------------------
    @Test
    void testAuditEmployees() {

        List<EmployeeEntity> list = Arrays.asList(
                emp(1L, "John"),
                emp(2L, "Alice")
        );

        when(helper.getAllEmployees()).thenReturn(list);

        List<EmployeeEntity> result = auditService.auditEmployees();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John");
        assertThat(result.get(1).getName()).isEqualTo("Alice");

        verify(helper).getAllEmployees();
    }

    // -----------------------------------------------------
    // TEST 2: auditEmployees returns empty list
    // -----------------------------------------------------
    @Test
    void testAuditEmployees_Empty() {

        when(helper.getAllEmployees()).thenReturn(Collections.emptyList());

        List<EmployeeEntity> result = auditService.auditEmployees();

        assertThat(result).isEmpty();
        verify(helper).getAllEmployees();
    }
}
