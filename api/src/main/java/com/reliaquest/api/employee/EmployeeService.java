package com.reliaquest.api.employee;

import java.util.List;

public interface EmployeeService
{
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployee(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(EmployeeDetails employeeInput);

    Employee deleteEmployee(String id);
}
