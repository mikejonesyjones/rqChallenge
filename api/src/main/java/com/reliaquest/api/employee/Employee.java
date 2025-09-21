package com.reliaquest.api.employee;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Employee extends EmployeeDetails
{
    private UUID id;

    @JsonProperty("employee_email")
    private String email;

    public UUID getId()
    {
        return id;
    }

    public String getEmail()
    {
        return email;
    }
}
