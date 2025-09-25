package com.reliaquest.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import org.springframework.core.io.ClassPathResource;

public final class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T loadFromClasspathResource(String classpathResource, TypeReference<T> typeReference)
            throws Exception {
        final String allEmployeesJson = loadJson(classpathResource);
        return objectMapper.readValue(allEmployeesJson, typeReference);
    }

    public static String loadJson(String filename) throws Exception {
        ClassPathResource resource = new ClassPathResource(filename);
        return Files.readString(resource.getFile().toPath());
    }

    public static int findRandomFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        }
    }

    /**
     * My personal laptop is too old to run Docker, so couldn't use TestContainers :(
     */
    public static CompletableFuture<Process> startServer(
            String jarPath, int port, String readyLogLine, String... extraArgs) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String[] allArgs = Stream.concat(
                                Stream.of("java", "-jar", jarPath, "--server.port=" + port), Stream.of(extraArgs))
                        .toArray(String[]::new);
                final ProcessBuilder pb = new ProcessBuilder(allArgs);
                pb.redirectErrorStream(true);
                final Process process = pb.start();

                final CompletableFuture<Void> readyFuture = new CompletableFuture<>();
                new Thread(() -> {
                            try (BufferedReader reader =
                                    new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    System.out.println(line);
                                    if (line.contains(readyLogLine)) {
                                        readyFuture.complete(null);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                        .start();
                readyFuture.get(60, TimeUnit.SECONDS);

                return process;
            } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void stopServer(Process process) throws InterruptedException {
        process.destroy();
        final boolean exited = process.waitFor(5, TimeUnit.SECONDS);

        if (!exited) {
            process.destroyForcibly();
            process.waitFor();
        }
    }
}
