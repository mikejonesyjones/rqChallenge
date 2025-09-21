package com.reliaquest.api.employee;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class RestTemplateEmployeeRepository implements EmployeeRepository
{
    private final RestTemplate restTemplate;

    private final String baseUrl;

    public RestTemplateEmployeeRepository(final RestTemplate restTemplate,
                                          @Value("${employee.api.base-url}") final String baseUrl)
    {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<Employee> getAllEmployees()
    {
        ResponseEntity<ServerResponse<List<Employee>>> response =
                restTemplate.exchange(baseUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return Optional.ofNullable(response.getBody()).map(ServerResponse::data).orElse(Collections.emptyList());
    }

    @Override
    public Employee getEmployee(final String id)
    {
        ResponseEntity<ServerResponse<Employee>> response = restTemplate.exchange(baseUrl + "/{id}",
                                                                                  HttpMethod.GET,
                                                                                  null,
                                                                                  new ParameterizedTypeReference<>() {},
                                                                                  id);
        return Optional.ofNullable(response.getBody()).map(ServerResponse::data).orElse(null);
    }

    @Override
    public Employee createEmployee(final EmployeeDetails employeeDetails)
    {
        ResponseEntity<ServerResponse<Employee>> response = restTemplate.exchange(baseUrl,
                                                                                  HttpMethod.POST,
                                                                                  new HttpEntity<>(employeeDetails),
                                                                                  new ParameterizedTypeReference<>() {});
        return Optional.ofNullable(response.getBody()).map(ServerResponse::data).orElse(null);
    }

    @Override
    public Employee deleteEmployee(final String id)
    {
        //Hmmmm...the mock server wants their name, but the API accepts their id...
        final Employee employee = getEmployee(id);
        final EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier(employee.getName());
        ResponseEntity<ServerResponse<Boolean>> response = restTemplate.exchange(baseUrl,
                                                                                  HttpMethod.DELETE,
                                                                                  new HttpEntity<>(employeeIdentifier),
                                                                                  new ParameterizedTypeReference<>() {});
        //Dodgy, even if it successfully deleted, nothing to say this was still their representation...
        return Optional.ofNullable(response.getBody())
                       .filter(ServerResponse::data)
                       .map(deleted -> employee)
                       .orElse(null);
    }

    private record EmployeeIdentifier(String name)
    {

    }

    record ServerResponse<T>(T data, String status, String error)
    {
    }
}
