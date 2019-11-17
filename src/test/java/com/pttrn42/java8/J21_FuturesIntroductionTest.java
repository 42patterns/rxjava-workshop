package com.pttrn42.java8;

import com.pttrn42.java8.stackoverflow.LoadFromStackOverflowTask;
import com.pttrn42.java8.util.BaseFutureTest;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Let's start with basic.
 */
@Ignore
public class J21_FuturesIntroductionTest extends BaseFutureTest {

    private static final Logger log = LoggerFactory.getLogger(J21_FuturesIntroductionTest.class);

    @Test
    public void should_perform_a_blocking_call() throws Exception {
        final String title = client.mostRecentQuestionAbout("java");
        log.debug("Most recent Java question: '{}'", title);

        assertThat(title, equalTo("Why don't common Map implementations cache the result of Map.containsKey() for Map.get()"));
    }

    @Test
    public void should_perform_asynchronous_operation() throws Exception {
        final Callable<String> task = () -> client.mostRecentQuestionAbout("java");
        final Future<String> javaQuestionFuture = executorService.submit(task);
        final String javaQuestion = javaQuestionFuture.get();
        log.debug("Found: '{}'", javaQuestion);

        assertThat(javaQuestion, equalTo("Why don't common Map implementations cache the result of Map.containsKey() for Map.get()"));
    }

    @Test
    public void should_wait_for_either_to_finish() throws Exception {
        final Future<String> java = findQuestionsAbout("java");
        final Future<String> scala = findQuestionsAbout("scala");

        // * A {@link CompletionService} that uses a supplied {@link Executor}
        // * to execute tasks.  This class arranges that submitted tasks are,
        // * upon completion, placed on a queue accessible using {@code take}.
        CompletionService<String> completionService = new ExecutorCompletionService<>(executorService);
        completionService.submit(() -> java.get());
        completionService.submit(() -> scala.get());

        String result = completionService.take().get();

        log.debug("Found: {} ", result);
        assertThat(result, either(is("Why don't common Map implementations cache the result of Map.containsKey() for Map.get()"))
                .or(is("Update a timestamp SettingKey in an sbt 0.12 task"))
        );
    }

    @Test
    public void should_wait_for_both_to_finish() {
        final Future<String> java = findQuestionsAbout("java");
        final Future<String> scala = findQuestionsAbout("scala");

        CompletionService<String> completionService = new ExecutorCompletionService<>(executorService);
        completionService.submit(() -> java.get());
        completionService.submit(() -> scala.get());

        List<String> results = new ArrayList<>();

        boolean error = false;
        while (results.size() < 2 && !error) {
            try {
                results.add(completionService.take().get());
            } catch (InterruptedException | ExecutionException e) {
                error = true;
            }
        }


        log.debug("Found: {} ", results);
        assertThat(results, containsInAnyOrder(
                "Why don't common Map implementations cache the result of Map.containsKey() for Map.get()",
                "Update a timestamp SettingKey in an sbt 0.12 task"));
    }

    private Future<String> findQuestionsAbout(String tag) {
        final Callable<String> task = new LoadFromStackOverflowTask(client, tag);
        return executorService.submit(task);
    }

}

