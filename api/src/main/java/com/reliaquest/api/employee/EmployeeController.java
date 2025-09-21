package com.reliaquest.api.employee;

import com.reliaquest.api.controller.IEmployeeController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class EmployeeController implements IEmployeeController<Employee, EmployeeDetails>
{
    private final EmployeeService employeeService;

    public EmployeeController(final EmployeeService employeeService)
    {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees()
    {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(final String searchString)
    {
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(searchString));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(final String id)
    {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees()
    {
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames()
    {
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Override
    public ResponseEntity<Employee> createEmployee(final EmployeeDetails employeeInput)
    {
        return ResponseEntity.ok(employeeService.createEmployee(employeeInput));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(final String id)
    {
        return ResponseEntity.ok(employeeService.deleteEmployee(id).getName());
    }
}
