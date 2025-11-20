package com.employeeapp.service;

import com.employeeapp.dto.EmployeeRequest;
import com.employeeapp.entities.EmployeeEntity;
import com.employeeapp.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmployeeHelperServiceTest {

    private EmployeeHelperService helper;
    private EmployeeRepository repository;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(EmployeeRepository.class);
        helper = new EmployeeHelperService();
        helper.employeeRepository = repository;   // inject mock manually
    }

    private EmployeeRequest req(String name, String dept, String role) {
        EmployeeRequest r = new EmployeeRequest();
        r.setName(name);
        r.setDepartment(dept);
        r.setRole(role);
        return r;
    }

    // --------------------------------------------
    // TEST 1: addEmployee()
    // --------------------------------------------
    @Test
    void testAddEmployee() {
        EmployeeRequest req = req("John", "IT", "Developer");

        EmployeeEntity emp = helper.addEmployee(req);

        assertThat(emp.getId()).isEqualTo(1L);
        assertThat(emp.getName()).isEqualTo("John");
        assertThat(emp.getDepartment()).isEqualTo("IT");
        assertThat(emp.getRole()).isEqualTo("Developer");
        assertThat(emp.isActive()).isTrue();
    }

    // --------------------------------------------
    // TEST 2: updateEmployee()
    // --------------------------------------------
    @Test
    void testUpdateEmployee() {

        helper.addEmployee(req("John", "IT", "Dev")); // creates ID=1

        EmployeeRequest update = req("John Updated", "HR", "Lead");
        EmployeeEntity updated = helper.updateEmployee(1L, update);

        assertThat(updated.getName()).isEqualTo("John Updated");
        assertThat(updated.getDepartment()).isEqualTo("HR");
        assertThat(updated.getRole()).isEqualTo("Lead");
    }

    // --------------------------------------------
    // TEST 3: getEmployee()
    // --------------------------------------------
    @Test
    void testGetEmployee() {
        helper.addEmployee(req("A", "IT", "Dev"));
        EmployeeEntity emp = helper.getEmployee(1L);

        assertThat(emp).isNotNull();
        assertThat(emp.getName()).isEqualTo("A");
    }

    // --------------------------------------------
    // TEST 4: searchEmployees()
    // --------------------------------------------
    @Test
    void testSearchEmployees() {
        helper.addEmployee(req("A", "IT", "Dev"));
        helper.addEmployee(req("B", "HR", "Dev"));
        helper.addEmployee(req("C", "IT", "Lead"));

        List<EmployeeEntity> list = helper.searchEmployees("IT");

        assertThat(list).hasSize(2);
        assertThat(list).extracting("name").containsExactly("A", "C");
    }

    // --------------------------------------------
    // TEST 5: deactivateEmployee()
    // --------------------------------------------
    @Test
    void testDeactivateEmployee() {
        helper.addEmployee(req("A", "IT", "Dev"));

        boolean result = helper.deactivateEmployee(1L);

        assertThat(result).isTrue();
        assertThat(helper.getEmployee(1L).isActive()).isFalse();
    }

    @Test
    void testDeactivateEmployee_NotFound() {
        assertThat(helper.deactivateEmployee(999L)).isFalse();
    }

    // --------------------------------------------
    // TEST 6: promoteEmployee()
    // --------------------------------------------
    @Test
    void testPromoteEmployee() {
        helper.addEmployee(req("A", "IT", "Dev"));

        boolean result = helper.promoteEmployee(1L, "Lead");

        assertThat(result).isTrue();
        assertThat(helper.getEmployee(1L).getRole()).isEqualTo("Lead");
    }

    @Test
    void testPromoteEmployee_NotFound() {
        assertThat(helper.promoteEmployee(999L, "Lead")).isFalse();
    }

    // --------------------------------------------
    // TEST 7: deleteEmployee()
    // --------------------------------------------
    @Test
    void testDeleteEmployee() {
        helper.addEmployee(req("A", "IT", "Dev"));

        boolean removed = helper.deleteEmployee(1L);

        assertThat(removed).isTrue();
        assertThat(helper.getEmployee(1L)).isNull();
    }

    @Test
    void testDeleteEmployee_NotFound() {
        assertThat(helper.deleteEmployee(999L)).isFalse();
    }

    // --------------------------------------------
    // TEST 8: getAllEmployees()
    // --------------------------------------------
    @Test
    void testGetAllEmployees() {

        helper.addEmployee(req("A", "IT", "Dev"));
        helper.addEmployee(req("B", "HR", "Lead"));

        List<EmployeeEntity> list = helper.getAllEmployees();

        assertThat(list).hasSize(2);
    }

    // --------------------------------------------
    // TEST 9: getActiveCount()
    // --------------------------------------------
    @Test
    void testGetActiveCount() {

        helper.addEmployee(req("A", "IT", "Dev")); // active
        helper.addEmployee(req("B", "HR", "Lead")); // active

        helper.deactivateEmployee(2L); // deactivate B

        int count = helper.getActiveCount();
        assertThat(count).isEqualTo(1);
    }

    // --------------------------------------------
    // TEST 10: auditEmployees() (wrapper around getAllEmployees)
    // --------------------------------------------
    @Test
    void testAuditEmployees() {

        helper.addEmployee(req("A", "IT", "Dev"));
        helper.addEmployee(req("B", "HR", "Lead"));

        List<EmployeeEntity> list = helper.auditEmployees();

        assertThat(list).hasSize(2);
    }

    // --------------------------------------------
    // TEST 11: findAllEmployees() uses repository
    // --------------------------------------------
    @Test
    void testFindAllEmployees_RepositoryInteraction() {

        List<EmployeeEntity> mockList = Arrays.asList(
                new EmployeeEntity(), new EmployeeEntity()
        );

        when(repository.findAllEmployees()).thenReturn(mockList);

        List<EmployeeEntity> result = helper.findAllEmployees();

        assertThat(result).hasSize(2);
        verify(repository).findAllEmployees();
    }
}
