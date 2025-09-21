package com.reliaquest.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;

public final class TestUtils
{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T loadFromClasspathResource(String classpathResource, TypeReference<T> typeReference)
            throws Exception
    {
        final String allEmployeesJson = loadJson(classpathResource);
        return objectMapper.readValue(allEmployeesJson, typeReference);
    }

    public static String loadJson(String filename) throws Exception
    {
        ClassPathResource resource = new ClassPathResource(filename);
        return Files.readString(resource.getFile()
                                        .toPath());
    }
}
