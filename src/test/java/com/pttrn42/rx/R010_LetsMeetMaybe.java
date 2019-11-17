package com.pttrn42.rx;

import com.pttrn42.rx.samples.RestClient;
import io.reactivex.Maybe;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

@Ignore
public class R010_LetsMeetMaybe {

	@Test
	public void helloMaybeWithRxTest() throws Exception {
		//given
		final Maybe<String> reactor = null;

		//then
		reactor.test()
				.assertValue("RxJava");
	}

	/**
	 * Tip: Avoid block() in production code
	 */
	@Test
	public void helloMaybe() throws Exception {
		//given
		final Maybe<String> reactor = null;

		//when
		final String value = reactor.blockingGet();

		//then
		assertThat(value, is("RxJava"));
	}

	@Test
	public void emptyMaybe() throws Exception {
		//given
		final Maybe<String> reactor = null;

		//when
		final String value = reactor.blockingGet();

		//then
		assertThat(value, nullValue());
	}

	@Test
	public void errorMaybe() throws Exception {
		//given
		final Maybe<String> error = null;

		//when
		try {
			error.blockingGet();
			fail("Expected exception of type " + UnsupportedOperationException.class);
		} catch (UnsupportedOperationException e) {
			//then
			assertThat(e, hasMessage(is("Simulated")));
		}
	}

	@Test
	public void maybeIsEager() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger();

		//when
		//TODO: use counter.incrementAndGet() eagerly

		//then
		assertThat(counter.get(), is(1));
	}

	@Test
	public void maybeIsLazy() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger();

		//when
		//TODO: use counter.incrementAndGet() lazily
		Maybe<Integer> maybe = null;

		//then
		assertThat(counter.get(), is(0));
		assertThat(maybe.blockingGet(), is(1));
	}

	@Test
	public void lazyWithoutCaching() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger();
		final Maybe<Integer> lazy = null; //TODO: call lazily

		//when
		final Integer first = lazy.blockingGet();
		final Integer second = lazy.blockingGet();

		//then
		assertThat(first, is(1));
		assertThat(second, is(2));
	}

	/**
	 * TODO: use {@link Maybe#cache()} operator to call {@link AtomicInteger#incrementAndGet()} only once.
	 */
	@Test
	public void cachingMaybeComputesOnlyOnce() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger();
		final Maybe<Integer> lazy = Maybe.fromCallable(counter::incrementAndGet).cache();

		//when
		lazy.blockingGet();
		lazy.blockingGet();

		//then
		assertThat(counter.get(), is(1));
	}

	/**
	 * RestClient fails if called multiple times with the same value
	 *
	 * TODO Use {@link Maybe#cache()} to avoid calling destructive method twice
	 */
	@Test
	public void nonIdempotentWebService() throws Exception {
		//given
		RestClient restClient = new RestClient();
		final Maybe<Object> result = Maybe.fromRunnable(() -> restClient.post(1));

		//when
		result.blockingGet();
		result.blockingGet();

		//then
		//no exceptions
	}

}
