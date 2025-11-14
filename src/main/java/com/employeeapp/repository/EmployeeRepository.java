package com.employeeapp.repository;

import com.employeeapp.dto.EmployeeDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeDto, Long> {
	// You can add custom query methods here if needed

	@Query(value = "SELECT * FROM employee", nativeQuery = true)
	java.util.List<EmployeeDto> findAllEmployees();
}
