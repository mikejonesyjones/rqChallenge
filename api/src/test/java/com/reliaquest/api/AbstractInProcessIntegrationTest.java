package com.reliaquest.api;

import com.reliaquest.server.ServerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Runs the server in the same JVM as the test, letting it be started and stopped quickly (so doesn't trigger the rate
 * limiting while running the test cases)
 */
public class AbstractInProcessIntegrationTest
{
    private ConfigurableApplicationContext context;

    void startServer()
    {
        int port = 8112;
        context = SpringApplication.run(ServerApplication.class, "--server.port=" + port);
    }

    void stopServer()
    {
        if (context != null)
        {
            SpringApplication.exit(context, () -> 0);
            context.close();
        }
    }
}
