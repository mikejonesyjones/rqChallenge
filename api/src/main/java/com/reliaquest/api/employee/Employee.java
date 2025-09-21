package com.reliaquest.api.employee;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Employee extends EmployeeDetails
{
    private UUID id;

    @JsonProperty("employee_email")
    private String email;

    Employee(UUID uuid, EmployeeDetails details, String email)
    {
        super(details.getName(), details.getSalary(), details.getAge(), details.getTitle());
        this.id = uuid;
        this.email = email;
    }

    protected Employee()
    {

    }

    public UUID getId()
    {
        return id;
    }

    public String getEmail()
    {
        return email;
    }

    @Override
    @JsonProperty("employee_name")
    public String getName()
    {
        return super.getName();
    }

    @Override
    @JsonProperty("employee_salary")
    public Integer getSalary()
    {
        return super.getSalary();
    }

    @Override
    @JsonProperty("employee_age")
    public Integer getAge()
    {
        return super.getAge();
    }

    @Override
    @JsonProperty("employee_title")
    public String getTitle()
    {
        return super.getTitle();
    }
}
