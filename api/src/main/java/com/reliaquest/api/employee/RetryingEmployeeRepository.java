package com.reliaquest.api.employee;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

public class RetryingEmployeeRepository implements EmployeeRepository {
    private final EmployeeRepository delegate;

    private final RetryTemplate retryTemplate;

    public RetryingEmployeeRepository(
            final EmployeeRepository delegate, @Qualifier("employeeRetryTemplate") final RetryTemplate retryTemplate) {
        this.delegate = delegate;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return executeWithRetry(delegate::getAllEmployees);
    }

    @Override
    public Employee getEmployee(final String id) {
        return executeWithRetry(() -> delegate.getEmployee(id));
    }

    @Override
    public Employee createEmployee(final EmployeeDetails employeeDetails) {
        return executeWithRetry(() -> delegate.createEmployee(employeeDetails));
    }

    @Override
    public Employee deleteEmployee(final String id) {
        return executeWithRetry(() -> delegate.deleteEmployee(id));
    }

    private <T> T executeWithRetry(final Request<T> request) {
        return retryTemplate.execute(request);
    }

    private interface Request<T> extends Supplier<T>, RetryCallback<T, RuntimeException> {
        @Override
        default T doWithRetry(final RetryContext context) throws RuntimeException {
            return get();
        }
    }
}
