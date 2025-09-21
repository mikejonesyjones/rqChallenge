package com.reliaquest.api.employee;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeDetails
{
    @JsonProperty("employee_name")
    private String name;

    @JsonProperty("employee_salary")
    private Integer salary;

    @JsonProperty("employee_age")
    private Integer age;

    @JsonProperty("employee_title")
    private String title;

    public String getName()
    {
        return name;
    }

    public Integer getSalary()
    {
        return salary;
    }

    public Integer getAge()
    {
        return age;
    }

    public String getTitle()
    {
        return title;
    }
}
