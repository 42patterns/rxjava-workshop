package com.pttrn42.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

@Ignore
public class R021_ObservableSubscribing {

    private static final Logger log = LoggerFactory.getLogger(R021_ObservableSubscribing.class);

    @Test
    public void noWorkHappensWithoutSubscription() throws Exception {
        //given
        AtomicBoolean flag = new AtomicBoolean();

        //when
        log.info("About to create Observable");
        Observable.fromIterable(() -> {
            log.info("Doing hard work");
            flag.set(true);
            return Stream.of(1, 2, 3).iterator();
        });

        //then
        assertThat(flag.get(), is(false));
    }

    /**
     * Notice on which thread everything runs
     */
    @Test
    public void blockTriggersWork() throws Exception {
        //given
        AtomicBoolean flag = new AtomicBoolean();

        //when
        log.info("About to create Observable");
        final Observable<Integer> work = Observable.fromIterable(() -> {
            log.info("Doing hard work");
            flag.set(true);
            return Stream.of(1, 2, 3).iterator();
        });
        log.info("Observable was created");

        final Integer result = null; //TODO: get last value
        log.info("Work is done");

        //then
        assertThat(flag.get(), is(true));
        assertThat(result, is(3));
    }

    @Test
    public void subscriptionTriggersWork() throws Exception {
        //given
        AtomicBoolean flag = new AtomicBoolean();
        log.info("About to create Observable");

        //when
        final Observable<Integer> work = Observable.fromIterable(() -> {
            log.info("Doing hard work");
            flag.set(true);
            return Stream.of(1, 2, 3).iterator();
        });

        //then
        log.info("Observable was created");

        Disposable d = null; //subscription tiggers work

        log.info("Work is done");
        assertThat(d.isDisposed(), is(true));
    }

    @Test
    public void subscriptionOfManyNotifications() throws Exception {
        //given
        AtomicBoolean flag = new AtomicBoolean();
        log.info("About to create Observable");

        //when
        final Observable<Integer> work = Observable.fromIterable(() -> {
            log.info("Doing hard work");
            flag.set(true);
            return Stream.of(1, 2, 3).iterator();
        });

        //then
        log.info("Observable was created");

        work.subscribe(
                i -> log.info("Received {}", i),
                ex -> log.error("Opps!", ex),
                () -> log.info("Observable completed")
        );

        log.info("Work is done");
    }

    private final List<Integer> onNext = new CopyOnWriteArrayList<>();
    private final AtomicReference<Throwable> error = new AtomicReference<>();
    private final AtomicBoolean completed = new AtomicBoolean();

    /**
     * TODO create a {@link Observable} that completes with an error immediately
     */
    @Test
    public void observableCompletingWithError() {
        //given

        //when
        final Observable<Integer> work = Observable.error(new IOException("Simulated"));

        //then
        work.subscribe(
                i -> { /*TODO: do something here */ },
                ex -> { /*TODO: do something here */ },
                () -> { /*TODO: do something here */ }
        );

        //Hint: you don't normally test streams like that! Don't get used to it
        assertThat(onNext, empty());
        assertThat(error.get(), instanceOf(IOException.class));
        assertThat(error.get(), hasMessage(equalTo("Simulated")));
        assertThat(completed.get(), is(false));
    }

    /**
     * TODO create a {@link Observable} that completes normally without emitting any value
     */
    @Test
    public void observableCompletingWithoutAnyValue() throws Exception {
        //given

        //when
        final Observable<Integer> work = Observable.empty();

        //then
        work.subscribe(
                i -> { /*TODO: do something here */ },
                ex -> { /*TODO: do something here */ },
                () -> { /*TODO: do something here */ }
        );

        //Hint: you don't normally test streams like that! Don't get used to it
        assertThat(onNext, empty());
        assertThat(error.get(), nullValue());
        assertThat(completed.get(), is(true));
    }

    /**
     * TODO create a {@link Observable} that <b>fails</b> after emitting few values
     * Hint: {@link Observable#concat(Iterable)} with failing {@link Observable} as second argument
     */
    @Test
    public void observableThatFailsAfterEmitting() throws Exception {
        //given

        //when
        final Observable<Integer> work = Observable.concat(
                Observable.just(1, 2, 3),
                Observable.error(new IOException("Simulated"))
        );

        //then
        work.subscribe(
                i -> { /*TODO: do something here */ },
                ex -> { /*TODO: do something here */ },
                () -> { /*TODO: do something here */ }
        );

        assertThat(onNext, contains(1, 2, 3));
        assertThat(error.get(), instanceOf(IOException.class));
        assertThat(error.get(), hasMessage(equalTo("Simulated")));
        assertThat(completed.get(), is(false));
    }

    /**
     * TODO create a {@link Observable} that never completes <b>after</b> emitting few values.
     * What happens if you {@link Observable#blockingLast()} ()} on such {@link Observable}?
     * Hint: {@link Observable#concat(ObservableSource, ObservableSource)}
     */
    @Test
    public void observableThatNeverCompletesAtAll() throws Exception {
        //given

        //when
        final Observable<Integer> work = Observable.concat(
                Observable.just(1, 2, 3),
                Observable.never()
        );

        //then
        work.subscribe(
                i -> { /*TODO: do something here */ },
                ex -> { /*TODO: do something here */ },
                () -> { /*TODO: do something here */ }
        );

        assertThat(onNext, contains(1, 2, 3));
        assertThat(error.get(), nullValue());
        assertThat(completed.get(), is(false));
    }

}
