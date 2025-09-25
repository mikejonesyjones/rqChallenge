package com.reliaquest.api.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.TestUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("SameParameterValue")
@SpringBootTest
public class RestTemplateEmployeeRepositoryTest {
    private RestTemplateEmployeeRepository employeeRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${employee.api.base-url}")
    private String baseUrl;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() {
        employeeRepository = new RestTemplateEmployeeRepository(restTemplate, baseUrl);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testGetAllEmployees() throws Exception {
        final String allEmployeesJson = TestUtils.loadJson("all-employees-response.json");
        mockServer
                .expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(allEmployeesJson, MediaType.APPLICATION_JSON));

        final List<Employee> result = employeeRepository.getAllEmployees();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(50);
        final Employee firstEmployee = result.get(0);
        assertThat(firstEmployee.getId().toString()).isEqualTo("d0177819-d83e-4694-80ad-4764bfd31571");
        assertThat(firstEmployee.getName()).isEqualTo("Myrle Mertz");
        assertThat(firstEmployee.getSalary()).isEqualTo(373058);
        assertThat(firstEmployee.getEmail()).isEqualTo("solarbreeze@company.com");
        assertThat(firstEmployee.getAge()).isEqualTo(39);
        assertThat(firstEmployee.getTitle()).isEqualTo("Sales Architect");

        final Employee lastEmployee = CollectionUtils.lastElement(result);
        assertThat(lastEmployee).isNotNull();
        assertThat(lastEmployee.getId().toString()).isEqualTo("c8ee0fba-3351-4d48-8a36-9116895fcd27");
        assertThat(lastEmployee.getName()).isEqualTo("Walton Stiedemann IV");
        assertThat(lastEmployee.getSalary()).isEqualTo(471439);
        assertThat(lastEmployee.getEmail()).isEqualTo("cardify@company.com");
        assertThat(lastEmployee.getAge()).isEqualTo(36);
        assertThat(lastEmployee.getTitle()).isEqualTo("Human Representative");
    }

    @Test
    public void testGetEmployee() throws Exception {
        final String id = "c8ee0fba-3351-4d48-8a36-9116895fcd27";
        mockGetEmployee(id);

        final Employee result = employeeRepository.getEmployee(id);
        assertThat(result.getId().toString()).isEqualTo("c8ee0fba-3351-4d48-8a36-9116895fcd27");
        assertThat(result.getName()).isEqualTo("Walton Stiedemann IV");
        assertThat(result.getSalary()).isEqualTo(471439);
        assertThat(result.getEmail()).isEqualTo("cardify@company.com");
        assertThat(result.getAge()).isEqualTo(36);
        assertThat(result.getTitle()).isEqualTo("Human Representative");
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        final String id = "c8ee0fba-3351-4d48-8a36-9116895fcd27";
        final RestTemplateEmployeeRepository.ServerResponse<Boolean> response =
                new RestTemplateEmployeeRepository.ServerResponse<>(
                        Boolean.TRUE, "Successfully processed request.", null);
        final String successJson = writeResponseJson(response);
        mockGetEmployee(id);
        mockServer
                .expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(
                        content()
                                .json(
                                        """
                                                    {
                                                        "name": "Walton Stiedemann IV"
                                                    }
                                                    """))
                .andRespond(withSuccess(successJson, MediaType.APPLICATION_JSON));

        final Employee result = employeeRepository.deleteEmployee(id);
        assertThat(result.getId().toString()).isEqualTo("c8ee0fba-3351-4d48-8a36-9116895fcd27");
        assertThat(result.getName()).isEqualTo("Walton Stiedemann IV");
    }

    @Test
    public void testCreateEmployee() throws Exception {
        final String name = "Michael Jones";
        final int salary = 1000000000;
        final int age = 42;
        final String title = "Software Developer";
        final UUID uuid = UUID.fromString("c8ee0fba-3351-4d48-8a36-9116895fcd27");
        final String email = "mike@gmail.com";
        final EmployeeDetails employeeDetails = new EmployeeDetails(name, salary, age, title);

        restTemplate.getInterceptors().add((request, body, execution) -> {
            System.out.println("Request URI: " + request.getURI());
            System.out.println("Request Method: " + request.getMethod());
            System.out.println("Request Body: " + new String(body, StandardCharsets.UTF_8));
            return execution.execute(request, body);
        });

        final String employee = createEmployee(uuid, employeeDetails, email);
        mockServer
                .expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(
                        content()
                                .json(
                                        """
                                                    {"name":"Michael Jones","salary":1000000000,"age":42,"title":"Software Developer"}
                                                    """))
                .andRespond(withSuccess(employee, MediaType.APPLICATION_JSON));

        final Employee result = employeeRepository.createEmployee(employeeDetails);
        assertThat(result.getId()).isEqualTo(uuid);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getSalary()).isEqualTo(salary);
        assertThat(result.getAge()).isEqualTo(age);
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getEmail()).isEqualTo(email);
    }

    private String createEmployee(UUID uuid, EmployeeDetails employeeDetails, String email)
            throws JsonProcessingException {
        final Employee employee = new Employee(uuid, employeeDetails, email);
        final RestTemplateEmployeeRepository.ServerResponse<Employee> response =
                new RestTemplateEmployeeRepository.ServerResponse<>(employee, "Successfully processed request.", null);
        return writeResponseJson(response);
    }

    private String writeResponseJson(RestTemplateEmployeeRepository.ServerResponse<?> response)
            throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(response);
    }

    private void mockGetEmployee(String id) throws Exception {
        final String employeeJson = TestUtils.loadJson("employee-" + id + ".json");
        mockServer
                .expect(requestTo(baseUrl + "/" + id))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(employeeJson, MediaType.APPLICATION_JSON));
    }
}
