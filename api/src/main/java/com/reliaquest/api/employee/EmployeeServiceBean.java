package com.reliaquest.api.employee;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceBean implements EmployeeService
{
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceBean(final EmployeeRepository employeeRepository)
    {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees()
    {
        return employeeRepository.getAllEmployees();
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(final String searchString)
    {
        return employeeRepository.getAllEmployees()
                                 .stream()
                                 .filter(employee -> employee.getName()
                                                             .contains(searchString))
                                 .collect(Collectors.toList());
    }

    @Override
    public Employee getEmployee(final String id)
    {
        return employeeRepository.getEmployee(id);
    }

    @Override
    public Integer getHighestSalaryOfEmployees()
    {
        return employeeRepository.getAllEmployees()
                                 .stream()
                                 .max(Comparator.comparing(EmployeeDetails::getSalary))
                                 .map(EmployeeDetails::getSalary)
                                 .orElse(null);
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames()
    {
        return employeeRepository.getAllEmployees()
                                 .stream()
                                 .sorted(Comparator.comparing(EmployeeDetails::getSalary)
                                                   .reversed())
                                 .limit(10)
                                 .map(EmployeeDetails::getName)
                                 .collect(Collectors.toList());
    }

    @Override
    public Employee createEmployee(final EmployeeDetails employeeInput)
    {
        return employeeRepository.createEmployee(employeeInput);
    }

    @Override
    public Employee deleteEmployee(final String id)
    {
        return employeeRepository.deleteEmployee(id);
    }
}
