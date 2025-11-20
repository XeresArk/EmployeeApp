package com.employeeapp.service;

import com.employeeapp.dto.EmployeeRequest;
import com.employeeapp.entities.EmployeeEntity;
import com.employeeapp.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EmployeeHelperService {
    private final Map<Long, EmployeeEntity> employeeStore = new HashMap<>();
    private long idCounter = 1;

    @Autowired
    EmployeeRepository employeeRepository;

    public EmployeeEntity addEmployee(EmployeeRequest req) {
        EmployeeEntity emp = new EmployeeEntity();
        emp.setId(idCounter++);
        emp.setName(req.getName());
        emp.setDepartment(req.getDepartment());
        emp.setRole(req.getRole());
        emp.setActive(true);
        employeeStore.put(emp.getId(), emp);
        return emp;
    }

    public EmployeeEntity updateEmployee(Long id, EmployeeRequest req) {
        EmployeeEntity emp = employeeStore.get(id);
        if (emp != null) {
            emp.setName(req.getName());
            emp.setDepartment(req.getDepartment());
            emp.setRole(req.getRole());
        }
        return emp;
    }

    public EmployeeEntity getEmployee(Long id) {
        return employeeStore.get(id);
    }

    public List<EmployeeEntity> searchEmployees(String department) {
        List<EmployeeEntity> result = new ArrayList<>();
        for (EmployeeEntity emp : employeeStore.values()) {
            if (emp.getDepartment().equalsIgnoreCase(department)) {
                result.add(emp);
            }
        }
        return result;
    }

    public boolean deactivateEmployee(Long id) {
        EmployeeEntity emp = employeeStore.get(id);
        if (emp != null) {
            emp.setActive(false);
            return true;
        }
        return false;
    }

    public boolean promoteEmployee(Long id, String newRole) {
        EmployeeEntity emp = employeeStore.get(id);
        if (emp != null) {
            emp.setRole(newRole);
            return true;
        }
        return false;
    }

    public boolean deleteEmployee(Long id) {
        return employeeStore.remove(id) != null;
    }

    public List<EmployeeEntity> getAllEmployees() {
        return new ArrayList<>(employeeStore.values());
    }

    public int getActiveCount() {
        int count = 0;
        for (EmployeeEntity emp : employeeStore.values()) {
            if (emp.isActive()) count++;
        }
        return count;
    }

    public List<EmployeeEntity> auditEmployees() {
        // For demo, just return all employees
        return getAllEmployees();
    }

    public List<EmployeeEntity> findAllEmployees() {
        return employeeRepository.findAllEmployees();
    }
}
