package com.reliaquest.api.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
public class RetryingEmployeeRepositoryTest {
    @Mock
    private EmployeeRepository delegateRepository;

    private RetryingEmployeeRepository retryingEmployeeRepository;

    @BeforeEach
    void setup() {
        final RetryTemplate retryTemplate = new RetryTemplate();
        retryingEmployeeRepository = new RetryingEmployeeRepository(delegateRepository, retryTemplate);
    }

    @Test
    void testGetAllEmployees() {
        final List<Employee> employees = List.of(mock(Employee.class), mock(Employee.class));
        when(delegateRepository.getAllEmployees())
                .thenThrow(RestClientException.class)
                .thenThrow(RestClientException.class)
                .thenReturn(employees);
        final List<Employee> result = retryingEmployeeRepository.getAllEmployees();
        assertThat(result).containsExactlyElementsOf(employees);

        verify(delegateRepository, times(3)).getAllEmployees();
    }

    @Test
    public void testGetEmployee() {
        final String id = "id";
        final Employee employee = mock(Employee.class);
        when(delegateRepository.getEmployee(id))
                .thenThrow(RestClientException.class)
                .thenThrow(RestClientException.class)
                .thenReturn(employee);
        final Employee result = retryingEmployeeRepository.getEmployee(id);
        assertThat(result).isEqualTo(employee);

        verify(delegateRepository, times(3)).getEmployee(id);
    }

    @Test
    public void testDeleteEmployee() {
        final String id = "id";
        final Employee employee = mock(Employee.class);
        when(delegateRepository.deleteEmployee(id))
                .thenThrow(RestClientException.class)
                .thenThrow(RestClientException.class)
                .thenReturn(employee);
        final Employee result = retryingEmployeeRepository.deleteEmployee(id);
        assertThat(result).isEqualTo(employee);

        verify(delegateRepository, times(3)).deleteEmployee(id);
    }

    @Test
    public void testCreateEmployee() {
        final EmployeeDetails details = mock(EmployeeDetails.class);
        final Employee employee = mock(Employee.class);
        when(delegateRepository.createEmployee(details))
                .thenThrow(RestClientException.class)
                .thenThrow(RestClientException.class)
                .thenReturn(employee);
        final Employee result = retryingEmployeeRepository.createEmployee(details);
        assertThat(result).isEqualTo(employee);

        verify(delegateRepository, times(3)).createEmployee(details);
    }
}
