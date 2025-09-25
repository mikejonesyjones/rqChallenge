package com.reliaquest.api.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {
    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Test
    void testGetEmployeeById() {
        String id = "id";
        Employee employee = mock(Employee.class);
        when(employeeService.getEmployee(id)).thenReturn(employee);
        assertThat(employeeController.getEmployeeById(id).getBody()).isEqualTo(employee);
    }

    @Test
    void testGetAllEmployees() {
        List<Employee> allEmployees = List.of(mock(Employee.class), mock(Employee.class));
        when(employeeService.getAllEmployees()).thenReturn(allEmployees);
        assertThat(employeeController.getAllEmployees().getBody()).isEqualTo(allEmployees);
    }

    @Test
    void testGetEmployeesByNameSearch() {
        String search = "search";
        List<Employee> allEmployees = List.of(mock(Employee.class), mock(Employee.class));
        when(employeeService.getEmployeesByNameSearch(search)).thenReturn(allEmployees);
        assertThat(employeeController.getEmployeesByNameSearch(search).getBody())
                .isEqualTo(allEmployees);
    }

    @Test
    void testGetHighestSalaryOfEmployees() {
        Integer highestSalary = 10000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(highestSalary);
        assertThat(employeeController.getHighestSalaryOfEmployees().getBody()).isEqualTo(highestSalary);
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        List<String> names = List.of("Michael Jones", "Mr Jones");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(names);
        assertThat(employeeController.getTopTenHighestEarningEmployeeNames().getBody())
                .isEqualTo(names);
    }

    @Test
    void testCreateEmployee() {
        EmployeeDetails employeeDetails = mock(EmployeeDetails.class);
        Employee employee = mock(Employee.class);
        when(employeeService.createEmployee(employeeDetails)).thenReturn(employee);
        assertThat(employeeController.createEmployee(employeeDetails).getBody()).isEqualTo(employee);
    }

    @Test
    void testDeleteEmployeeById() {
        String id = "id";
        Employee employee = mock(Employee.class);
        String name = "name";
        when(employee.getName()).thenReturn(name);
        when(employeeService.deleteEmployee(id)).thenReturn(employee);
        assertThat(employeeController.deleteEmployeeById(id).getBody()).isEqualTo(name);
    }
}
