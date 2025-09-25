package com.reliaquest.api.employee;

import java.util.List;

public interface EmployeeRepository {
    List<Employee> getAllEmployees();

    Employee getEmployee(String id);

    Employee createEmployee(EmployeeDetails employeeDetails);

    Employee deleteEmployee(String id);
}
