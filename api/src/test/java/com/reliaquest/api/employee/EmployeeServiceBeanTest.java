package com.reliaquest.api.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.reliaquest.api.TestUtils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceBeanTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceBean employeeService;

    @Nested
    class GetEmployeesTests {
        @BeforeEach
        void setup() throws Exception {
            List<Employee> allEmployees =
                    TestUtils.loadFromClasspathResource("all-employees.json", new TypeReference<>() {});
            when(employeeRepository.getAllEmployees()).thenReturn(allEmployees);
        }

        @Test
        void testGetAllEmployees() {
            final List<Employee> results = employeeService.getAllEmployees();
            assertThat(results).hasSize(15);

            final Employee firstResult = CollectionUtils.firstElement(results);
            assertThat(firstResult).isNotNull();
            assertThat(firstResult.getId().toString()).isEqualTo("d0177819-d83e-4694-80ad-4764bfd31571");
            assertThat(firstResult.getName()).isEqualTo("Myrle Mertz");

            final Employee lastResult = CollectionUtils.lastElement(results);
            assertThat(lastResult).isNotNull();
            assertThat(lastResult.getId().toString()).isEqualTo("8e0455d9-f513-4621-ae98-814dcd23b9ec");
            assertThat(lastResult.getName()).isEqualTo("Mina Huels");
        }

        private static Stream<Arguments> searchByNameArgs() {
            return Stream.of(Arguments.of("Me", List.of("d0177819-d83e-4694-80ad-4764bfd31571")));
        }

        @ParameterizedTest
        @MethodSource("searchByNameArgs")
        void testGetEmployeesByNameSearch(String searchString, List<String> expectedIds) {
            final List<Employee> results = employeeService.getEmployeesByNameSearch(searchString);

            assertThat(results.stream()
                            .map(Employee::getId)
                            .map(Objects::toString)
                            .collect(Collectors.toList()))
                    .hasSameElementsAs(expectedIds);
        }

        @Test
        void testGetHighestSalaryOfEmployees() {
            final Integer result = employeeService.getHighestSalaryOfEmployees();

            assertThat(result).isEqualTo(484364);
        }

        @Test
        public void getTopTenHighestEarningEmployeeNames() {
            final List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

            assertThat(result)
                    .containsExactly(
                            "Miss Risa Johns",
                            "Nakia Collins",
                            "Demetrius Corkery",
                            "Darrel Sanford",
                            "Myrle Mertz",
                            "Rayford Raynor",
                            "Miss Jonell Wilderman",
                            "Curt Schultz",
                            "Dr. Lupe Kilback",
                            "Steven Kilback");
        }
    }

    @Test
    public void testGetEmployee() {
        final String id = "blah";
        final Employee employee = mock(Employee.class);
        when(employeeRepository.getEmployee(id)).thenReturn(employee);
        final Employee result = employeeService.getEmployee(id);
        assertThat(result).isEqualTo(employee);
    }

    @Test
    public void testCreateEmployee() {
        final EmployeeDetails employeeDetails = mock(EmployeeDetails.class);
        final Employee employee = mock(Employee.class);
        when(employeeRepository.createEmployee(employeeDetails)).thenReturn(employee);
        final Employee result = employeeService.createEmployee(employeeDetails);
        assertThat(result).isEqualTo(employee);
    }

    @Test
    public void testDeleteEmployee() {
        final String id = "blah";
        final Employee employee = mock(Employee.class);
        when(employeeRepository.deleteEmployee(id)).thenReturn(employee);
        final Employee result = employeeService.deleteEmployee(id);
        assertThat(result).isEqualTo(employee);
    }
}
