package com.pttrn42.java8;

import com.pttrn42.java8.util.BaseFutureTest;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Ignore
public class J26_AllAnyTest extends BaseFutureTest {

    private static final Logger log = LoggerFactory.getLogger(J26_AllAnyTest.class);

    @Test
    public void allOf() throws Exception {
        final CompletableFuture<String> java = questions("java");
        final CompletableFuture<String> scala = questions("scala");
        final CompletableFuture<String> clojure = questions("clojure");
        final CompletableFuture<String> groovy = questions("groovy");

        final CompletableFuture<Void> allCompleted = null;

        allCompleted.thenRun(() -> {
            assertThat(java.join(), equalTo("Why don't common Map implementations cache the result of Map.containsKey() for Map.get()"));
            assertThat(scala.join(), equalTo("Update a timestamp SettingKey in an sbt 0.12 task"));
            assertThat(clojure.join(), equalTo("Secure web sockets with Clojure [on hold]"));
            assertThat(groovy.join(), equalTo("Trim domain field by default"));
        }).join();
    }

    @Test
    public void anyOf() throws Exception {
        final CompletableFuture<String> java = questions("java");
        final CompletableFuture<String> scala = questions("scala");
        final CompletableFuture<String> clojure = questions("clojure");
        final CompletableFuture<String> groovy = questions("groovy");

        final CompletableFuture<Object> firstCompleted = null;

        firstCompleted.thenAccept((Object result) -> {
            log.debug("Finished java: {}", java.isDone());
            log.debug("Finished scala: {}", scala.isDone());
            log.debug("Finished groovy: {}", groovy.isDone());
            log.debug("Finished clojure: {}", clojure.isDone());
            assertThat(Arrays.asList(java.isDone(), scala.isDone(), groovy.isDone(), clojure.isDone()),
                    Matchers.containsInAnyOrder(true, false, false, false));
            assertThat(result.toString(), either(is("Why don't common Map implementations cache the result of Map.containsKey() for Map.get()"))
                    .or(is("Update a timestamp SettingKey in an sbt 0.12 task"))
                    .or(is("Secure web sockets with Clojure [on hold]"))
                    .or(is("Trim domain field by default")));
        }).join();
    }

}

