package com.reliaquest.api.employee;

public class EmployeeDetails
{
    private String name;

    private Integer salary;

    private Integer age;

    private String title;

    EmployeeDetails(final String name, final Integer salary, final Integer age, final String title)
    {
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.title = title;
    }

    protected EmployeeDetails()
    {

    }

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
