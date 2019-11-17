package com.pttrn42.rx;

import io.reactivex.Observable;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

@Ignore
public class R011_LetsMeetObservable {

    /**
     * Tip: Avoid block() in production code
     */
    @Test
    public void helloObservable() throws Exception {
        //given
        final Observable<String> rx = null;

        //when
        final List<String> value = rx.toList().blockingGet();

        //then
        assertThat(value, hasItems("RxJava"));
    }

    @Test
    public void emptyObservable() throws Exception {
        //given
        final Observable<String> rx = null;

        //when
        final List<String> value = rx.toList().blockingGet();

        //then
        assertThat(value, emptyIterable());
    }

    @Test
    public void manyValues() throws Exception {
        //given
        final Observable<String> rx = null;

        //when
        final List<String> value = rx.toList().blockingGet();

        //then
        assertThat(value, contains("RxJava", "library"));
    }

    @Test
    public void errorObservable() throws Exception {
        //given
        final Observable<String> error = null;

        //when
        try {
            error.toList().blockingGet();
            fail("No exception thrown: " + UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            //then
            assertThat(e, hasMessage(is("Simulated")));
        }
    }

    @Test
    public void concatTwoObservables() throws Exception {
        //given
        final Observable<String> many = null;

        //when
        final List<String> values = many.toList().blockingGet();

        //then
        assertThat(values, contains("Hello", "reactive", "world"));
    }

    @Test
    public void errorObservableAfterValues() throws Exception {
        //given
        final Observable<String> error = null;

        //when
        try {
            error.toList().blockingGet();
            fail("No exception thrown: " + UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            //then
            assertThat(e, hasMessage(is("Simulated")));
        }
    }

    @Test
    public void observableIsEager() throws Exception {
        //given
        AtomicInteger counter = new AtomicInteger();

        //when
        //TODO: call counter.incrementAndGet() multiple times

        //then
        assertThat(counter.get(), is(2));
    }

    @Test
    public void observableIsLazy() throws Exception {
        //given
        AtomicInteger c = new AtomicInteger();

        //when
        //TODO: call counter.incrementAndGet() multiple times
        Observable<Integer> observable = null;

        //then
        assertThat(c.get(), is(0));
        assertThat(observable.toList().blockingGet(), hasItems(1, 2));
    }

    @Test
    public void observableComputesManyTimes() throws Exception {
        //given
        AtomicInteger c = new AtomicInteger();
        final Observable<Integer> observable = null;

        //when
        final List<Integer> first = observable.toList().blockingGet();
        final List<Integer> second = observable.toList().blockingGet();

        //then
        assertThat(c.get(), is(4));
        assertThat(first, contains(1, 2));
        assertThat(second, contains(3, 4));
    }

    /**
     * TODO Make sure Observable is computed only once
     * Hint: {@link Observable#cache()}
     */
    @Test
    public void makeLazyComputeOnlyOnce() throws Exception {
        //given
        AtomicInteger c = new AtomicInteger();
        Observable<Integer> observable = null;

        //when
        final List<Integer> first = observable.toList().blockingGet();
        final List<Integer> second = observable.toList().blockingGet();

        //then
        assertThat(c.get(), is(2));
        assertThat(first, is(second));
    }

}
