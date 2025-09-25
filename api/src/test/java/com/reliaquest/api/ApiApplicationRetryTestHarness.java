package com.reliaquest.api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

public class ApiApplicationRetryTestHarness {
    private final Process apiApplication;

    private final Process serverApplication;

    private final String baseUrl;

    private final ExecutorService executor = Executors.newFixedThreadPool(200);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final RestTemplate restTemplate = new RestTemplate();

    private final List<RequestResult> results = new CopyOnWriteArrayList<>();

    private static final AtomicBoolean saturated = new AtomicBoolean(false);

    private static final AtomicInteger loadLevel = new AtomicInteger(1);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        ApiApplicationRetryTestHarness testHarness = new ApiApplicationRetryTestHarness();
        try {
            testHarness.runTests();
            testHarness.stop();
        } finally {
            testHarness.printSummary();
        }
    }

    public ApiApplicationRetryTestHarness() throws IOException, ExecutionException, InterruptedException {
        final int apiServerPort = TestUtils.findRandomFreePort();
        final int serverPort = TestUtils.findRandomFreePort();

        CompletableFuture<Process> apiFuture = TestUtils.startServer(
                "api/build/libs/api-1.0.0.jar",
                apiServerPort,
                "Started ApiApplication",
                "--employee.api.port=" + serverPort);
        CompletableFuture<Process> serverFuture =
                TestUtils.startServer("server/build/libs/server-1.0.0.jar", serverPort, "Started ServerApplication");

        this.apiApplication = apiFuture.get();
        this.serverApplication = serverFuture.get();
        this.baseUrl = "http://localhost:" + apiServerPort + "/";
    }

    public void runTests() throws InterruptedException {
        AtomicReference<ScheduledFuture<?>> futureReference = new AtomicReference<>();
        final ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(
                () -> {
                    if (saturated.get()) {
                        System.out.println("!!! Stopping");
                        futureReference.get().cancel(true);
                        return;
                    }

                    int currentLevel = loadLevel.getAndIncrement();
                    System.out.println(">>> Starting load level " + currentLevel);

                    for (int i = 0; i < currentLevel * 10; i++) {
                        executor.submit(() -> {
                            long start = System.nanoTime();
                            Outcome outcome;

                            try {
                                ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
                                outcome = response.getStatusCode().is2xxSuccessful() ? Outcome.SUCCESS : Outcome.FAILED;
                            } catch (HttpStatusCodeException e) {
                                outcome = Outcome.FAILED;
                            } catch (ResourceAccessException e) {
                                outcome = Outcome.REJECTED;
                            }

                            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                            results.add(new RequestResult(outcome, elapsed));

                            if (failureRate() > 0.5) {
                                System.out.println("!!! Server saturated at level " + currentLevel);
                                saturated.set(true);
                            }
                        });
                    }
                },
                0,
                1,
                TimeUnit.MINUTES);

        futureReference.set(scheduledFuture);
        scheduler.awaitTermination(10, TimeUnit.MINUTES);
    }

    private double failureRate() {
        long total = results.size();
        if (total == 0) return 0.0;
        long failures =
                results.stream().filter(r -> r.outcome != Outcome.SUCCESS).count();
        return (double) failures / total;
    }

    private void printSummary() {
        long success =
                results.stream().filter(r -> r.outcome == Outcome.SUCCESS).count();
        long rejected =
                results.stream().filter(r -> r.outcome == Outcome.REJECTED).count();
        long failed = results.stream().filter(r -> r.outcome == Outcome.FAILED).count();
        double avgLatency =
                results.stream().mapToLong(r -> r.duration).average().orElse(0.0);

        System.out.println("\n=== Test Summary ===");
        System.out.println("Total requests : " + results.size());
        System.out.println("Success        : " + success);
        System.out.println("Rejected (HTTP): " + rejected);
        System.out.println("Failed (I/O)   : " + failed);
        System.out.println("Avg latency ms : " + avgLatency);
    }

    public void stop() throws InterruptedException {
        TestUtils.stopServer(apiApplication);
        TestUtils.stopServer(serverApplication);
        scheduler.shutdown();
        executor.shutdown();
    }

    private enum Outcome {
        SUCCESS,
        REJECTED,
        FAILED
    }

    private record RequestResult(Outcome outcome, long duration) {}
}
