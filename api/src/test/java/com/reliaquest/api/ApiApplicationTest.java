package com.reliaquest.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.reliaquest.api.employee.Employee;
import com.reliaquest.api.employee.EmployeeDetails;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ApiApplicationTest extends AbstractInProcessIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        startServer();
        this.restTemplate =
                restTemplateBuilder.rootUri("http://localhost:" + port).build();
    }

    @AfterEach
    void tearDown() {
        stopServer();
    }

    @Test
    void testGetAllEmployees() {
        List<Employee> allEmployees = getAllEmployees();
        assertThat(allEmployees).isNotEmpty();
    }

    @Test
    void testGetEmployeeById() {
        List<Employee> allEmployees = getAllEmployees();
        final Employee firstEmployee = CollectionUtils.firstElement(allEmployees);
        final String id = firstEmployee.getId().toString();
        final Employee actual =
                restTemplate.getForEntity("/{id}", Employee.class, id).getBody();
        assertThat(actual).usingRecursiveComparison().isEqualTo(firstEmployee);
    }

    @Test
    void getEmployeesByNameSearch() {
        final String name = "dzxcvmcz qwjdbsacnb";
        final Employee employee = createEmployee(name, 1000);
        final Employee[] result = restTemplate
                .getForEntity("/search/{searchString}", Employee[].class, name)
                .getBody();
        assertThat(result).usingRecursiveFieldByFieldElementComparator().contains(employee);
    }

    @Test
    void getHighestSalaryOfEmployees() {
        final Integer result =
                restTemplate.getForEntity("/highestSalary", Integer.class).getBody();
        assertThat(getAllEmployees().stream()).noneMatch(e -> e.getSalary() > result);
    }

    @Test
    void deleteEmployee() {
        final String name = "Some probably unique name";
        final Employee employee = createEmployee(name, 1000);
        final String employeeIdentifier = employee.getId().toString();
        final ResponseEntity<String> response = restTemplate.exchange(
                "/{id}", HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {}, employeeIdentifier);

        assertThat(response.getBody()).isEqualTo(name);
    }

    private Employee createEmployee(String name, Integer salary) {
        final EmployeeDetails employeeDetails = new EmployeeDetails(name, salary, 42, "Who cares?");
        final ResponseEntity<Employee> employeeResponse =
                restTemplate.postForEntity("/", employeeDetails, Employee.class);
        assertThat(employeeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        return employeeResponse.getBody();
    }

    private List<Employee> getAllEmployees() {
        final ResponseEntity<Employee[]> allEmployeesResponse = restTemplate.getForEntity("/", Employee[].class);
        assertThat(allEmployeesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Employee> allEmployees = List.of(allEmployeesResponse.getBody());
        return allEmployees;
    }
}
