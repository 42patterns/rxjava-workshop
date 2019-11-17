package com.pttrn42.rx;

import io.reactivex.Single;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

@Ignore
public class R010_LetsMeetSingle {

	@Test
	public void helloSingleWithRxTest() throws Exception {
		//given
		final Single<String> reactor = Single.just("RxJava");

		//then
		reactor.test()
				.assertValue("RxJava");
	}

	/**
	 * Tip: Avoid block() in production code
	 */
	@Test
	public void helloSingle() throws Exception {
		//given
		final Single<String> reactor = Single.just("RxJava");

		//when
		final String value = reactor.blockingGet();

		//then
		assertThat(value, is("RxJava"));
	}

	@Test
	public void errorSingle() throws Exception {
		//given
		final Single<String> error = Single.error(new UnsupportedOperationException("Simulated"));

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
	public void SingleIsEager() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger();

		//when
		//TODO: use counter.incrementAndGet() eagerly
		Single.just(counter.incrementAndGet());

		//then
		assertThat(counter.get(), is(1));
	}

	@Test
	public void SingleIsLazy() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger();

		//when
		//TODO: use counter.incrementAndGet() lazily
		Single<Integer> single = Single.fromCallable(() -> counter.incrementAndGet());

		//then
		assertThat(counter.get(), is(0));
		assertThat(single.blockingGet(), is(1));
	}

	@Test
	public void lazyWithoutCaching() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger();
		final Single<Integer> lazy = Single.fromCallable(() -> counter.incrementAndGet());

		//when
		final Integer first = lazy.blockingGet();
		final Integer second = lazy.blockingGet();

		//then
		assertThat(first, is(1));
		assertThat(second, is(2));
	}

	/**
	 * TODO: use {@link Single#cache()} operator to call {@link AtomicInteger#incrementAndGet()} only once.
	 */
	@Test
	public void cachingSingleComputesOnlyOnce() throws Exception {
		//given
		AtomicInteger counter = new AtomicInteger();
		final Single<Integer> lazy = Single.fromCallable(counter::incrementAndGet)
				.cache();

		//when
		lazy.blockingGet();
		lazy.blockingGet();

		//then
		assertThat(counter.get(), is(1));
	}

}
