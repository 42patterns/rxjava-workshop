package com.pttrn42.java8.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pttrn42.java8.db.DB;
import com.pttrn42.java8.db.Query;
import com.pttrn42.java8.stackoverflow.ArtificialSleepWrapper;
import com.pttrn42.java8.stackoverflow.HttpStackOverflowClient;
import com.pttrn42.java8.stackoverflow.InjectErrorsWrapper;
import com.pttrn42.java8.stackoverflow.LoadFromDiskHttpStackOverflowClient;
import com.pttrn42.java8.stackoverflow.LoggingWrapper;
import com.pttrn42.java8.stackoverflow.StackOverflowClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getProperty;

public class BaseFutureTest {

    private static final Logger log = LoggerFactory.getLogger(BaseFutureTest.class);

    protected final ExecutorService executorService = Executors.newFixedThreadPool(10, threadFactory());
    protected final DB db = new DB();
    protected final Query query = new Query("1");

    @Rule
    public TestName testName = new TestName();

    private ThreadFactory threadFactory() {
        return new ThreadFactoryBuilder().setNameFormat("test-pool-%d").build();
    }

    protected final StackOverflowClient client =
            new InjectErrorsWrapper(
                    new LoggingWrapper(
                            new ArtificialSleepWrapper(
                                    parseBoolean(getProperty("isOnline", "false")) ?
                                            new HttpStackOverflowClient() : new LoadFromDiskHttpStackOverflowClient()
                            )
                    ), "php"
            );


    @Before
    public void logTestStart() {
        log.debug("Starting: {}", testName.getMethodName());
    }

    @After
    public void stopPool() {
        executorService.shutdownNow();
    }

    protected CompletableFuture<String> getFutureQueryResult(final String queryId) {
        return CompletableFuture.supplyAsync(
                () -> db.apply(new Query(queryId))

        );
    }

    protected CompletableFuture<String> questions(String tag) {
        return CompletableFuture.supplyAsync(() ->
                        client.mostRecentQuestionAbout(tag),
                executorService
        );
    }
}
