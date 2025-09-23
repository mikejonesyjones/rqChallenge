package com.reliaquest.api.employee;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EmployeeConfiguration
{
    @Value("${employee.api.max-attempts}")
    private int maxAttempts;

    @Value("${employee.api.min-backoff-period}")
    private int minBackOffPeriod;

    @Value("${employee.api.base-url}")
    private String baseUrl;

    @Bean
    public RetryPolicy employeeRetryPolicy()
    {
        return new SimpleRetryPolicy(maxAttempts);
    }

    @Bean
    public BackOffPolicy employeeBackOffPolicy()
    {
        final FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(minBackOffPeriod);
        return fixedBackOffPolicy;
    }

    @Bean
    public RetryTemplate employeeRetryTemplate(RetryPolicy retryPolicy, BackOffPolicy backOffPolicy)
    {
        final RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }

    @Bean
    public EmployeeRepository employeeRepository(final RetryTemplate retryTemplate, final RestTemplate restTemplate)
    {
        final EmployeeRepository delegate = new RestTemplateEmployeeRepository(restTemplate, baseUrl);
        return new RetryingEmployeeRepository(delegate, retryTemplate);
    }
}
