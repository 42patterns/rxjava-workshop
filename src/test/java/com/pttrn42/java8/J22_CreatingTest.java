package com.pttrn42.java8;

import com.pttrn42.java8.util.BaseFutureTest;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Ignore
public class J22_CreatingTest extends BaseFutureTest {

    private static final Logger log = LoggerFactory.getLogger(J22_CreatingTest.class);

    @Test
    public void should_perform_an_asynchronous_operation() {
        final CompletableFuture<String> java = CompletableFuture.supplyAsync(() -> {
            throw new UnsupportedOperationException("Not implemented");
        });

        String result = java.join();
        log.debug("Found: '{}'", result);
        assertThat(result, equalTo("Why don't common Map implementations cache the result of Map.containsKey() for Map.get()"));
    }

    @Test
    public void should_perform_an_asynchronous_operation_with_custom_executor() {
        final CompletableFuture<String> java = CompletableFuture.supplyAsync(() -> {
            throw new UnsupportedOperationException("Not implemented");
        }, executorService);

        String result = java.join();
        log.debug("Found: '{}'", result);
        assertThat(result, equalTo("Why don't common Map implementations cache the result of Map.containsKey() for Map.get()"));
    }

}

