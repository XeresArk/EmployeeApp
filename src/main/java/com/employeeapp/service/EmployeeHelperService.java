package com.employeeapp.service;

import com.employeeapp.dto.EmployeeDto;
import com.employeeapp.dto.EmployeeRequest;
import com.employeeapp.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmployeeHelperService {
    private final Map<Long, EmployeeDto> employeeStore = new HashMap<>();
    private long idCounter = 1;

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeDto addEmployee(EmployeeRequest req) {
        EmployeeDto emp = new EmployeeDto();
        emp.setId(idCounter++);
        emp.setName(req.getName());
        emp.setDepartment(req.getDepartment());
        emp.setRole(req.getRole());
        emp.setActive(true);
        employeeStore.put(emp.getId(), emp);
        return emp;
    }

    public EmployeeDto updateEmployee(Long id, EmployeeRequest req) {
        EmployeeDto emp = employeeStore.get(id);
        if (emp != null) {
            emp.setName(req.getName());
            emp.setDepartment(req.getDepartment());
            emp.setRole(req.getRole());
        }
        return emp;
    }

    public EmployeeDto getEmployee(Long id) {
        return employeeStore.get(id);
    }

    public List<EmployeeDto> searchEmployees(String department) {
        List<EmployeeDto> result = new ArrayList<>();
        for (EmployeeDto emp : employeeStore.values()) {
            if (emp.getDepartment().equalsIgnoreCase(department)) {
                result.add(emp);
            }
        }
        return result;
    }

    public boolean deactivateEmployee(Long id) {
        EmployeeDto emp = employeeStore.get(id);
        if (emp != null) {
            emp.setActive(false);
            return true;
        }
        return false;
    }

    public boolean promoteEmployee(Long id, String newRole) {
        EmployeeDto emp = employeeStore.get(id);
        if (emp != null) {
            emp.setRole(newRole);
            return true;
        }
        return false;
    }

    public boolean deleteEmployee(Long id) {
        return employeeStore.remove(id) != null;
    }

    public List<EmployeeDto> getAllEmployees() {
        return new ArrayList<>(employeeStore.values());
    }

    public int getActiveCount() {
        int count = 0;
        for (EmployeeDto emp : employeeStore.values()) {
            if (emp.isActive()) count++;
        }
        return count;
    }

    public List<EmployeeDto> auditEmployees() {
        // For demo, just return all employees
        return getAllEmployees();
    }

    public EmployeeDto findAllEmployees() {
        employeeRepository.findAllEmployees();
        throw new UnsupportedOperationException("Unimplemented method 'getEmployees'");
    }
}
