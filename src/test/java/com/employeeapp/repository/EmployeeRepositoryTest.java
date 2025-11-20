package com.employeeapp.repository;

import com.employeeapp.entities.EmployeeEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testFindAllEmployees() {

        // GIVEN: Insert employees into H2 DB
        EmployeeEntity emp1 = new EmployeeEntity();
        emp1.setName("John");
        emp1.setDepartment("IT");

        EmployeeEntity emp2 = new EmployeeEntity();
        emp2.setName("Alice");
        emp2.setDepartment("HR");

        entityManager.persist(emp1);
        entityManager.persist(emp2);
        entityManager.flush();

        // WHEN: calling custom repository method
        List<EmployeeEntity> employees = repository.findAllEmployees();

        // THEN: validating result
        assertThat(employees).hasSize(2);
        assertThat(employees.get(0).getName()).isIn("John", "Alice");
        assertThat(employees.get(1).getName()).isIn("John", "Alice");
    }

    @Test
    void testFindAllEmployees_Empty() {

        List<EmployeeEntity> employees = repository.findAllEmployees();

        assertThat(employees).isEmpty();
    }
}
