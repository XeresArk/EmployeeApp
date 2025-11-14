package com.employeeapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.employeeapp.entities.EmployeeEntity;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
	// You can add custom query methods here if needed

	@Query(value = "SELECT * FROM employee", nativeQuery = true)
	List<EmployeeEntity> findAllEmployees();
}
