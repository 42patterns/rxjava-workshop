package com.pttrn42.rx;

import com.pttrn42.rx.samples.CacheServer;
import com.pttrn42.rx.user.User;
import io.reactivex.Maybe;
import io.reactivex.functions.Function;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class R045_ErrorHandling {

	private static final Logger log = LoggerFactory.getLogger(R045_ErrorHandling.class);

	@Test
	public void onErrorReturn() throws Exception {
		//given
		final Maybe<String> err = Maybe.error(new RuntimeException("Opps"));

		//when
		final Maybe<String> withFallback = null;

		//then
		err.test()
				.assertErrorMessage("Opps");
		withFallback.test()
				.assertValue("Fallback")
				.assertComplete();
	}

	/**
	 * TODO Where's the 'Failed' exception? Add some logging
	 */
	@Test
	public void onErrorResume() throws Exception {
		//given
		AtomicBoolean cheapFlag = new AtomicBoolean();
		AtomicBoolean expensiveFlag = new AtomicBoolean();

		Maybe<String> cheapButDangerous = Maybe.fromCallable(() -> {
			cheapFlag.set(true);
			throw new RuntimeException("Failed");
		});

		Maybe<String> expensive = Maybe.fromCallable(() -> {
			expensiveFlag.set(true);
			return "Expensive";
		});

		//when
		final Maybe<String> withError = null;

		//then
		withError.test()
				.assertValue("Expensive")
				.assertComplete();
		assertThat(cheapFlag.get(), is(true));
		assertThat(expensiveFlag.get(), is(true));
	}

	/**
	 * TODO Return different value for {@link IllegalStateException} and different for {@link IllegalArgumentException}
	 * @throws Exception
	 */
	@Test
	public void handleExceptionsDifferently() throws Exception {
		handle(danger(1)).test()
				.assertValue(-1)
				.assertComplete();

		handle(danger(2)).test()
				.assertValue(-2)
				.assertComplete();

		handle(danger(3)).test()
				.assertErrorMessage("Other: 3");
	}

	/**
	 * TODO Add error handling
	 * @see Maybe#onErrorResumeNext(Function)
	 */
	private Maybe<Integer> handle(Maybe<Integer> careful) {
		throw new IllegalStateException("Not implemented!");
	}

	private Maybe<Integer> danger(int id) {
		return Maybe.fromCallable(() -> {
			switch(id) {
				case 1:
					throw new IllegalArgumentException("One");
				case 2:
					throw new IllegalStateException("Two");
				default:
					throw new RuntimeException("Other: " + id);
			}
		});
	}

	/**
	 * TODO: tune in failure probability for the test eventually succeeded (complete successfully)
	 *
	 * @throws Exception
	 */
	@Test
	public void simpleRetry() throws Exception {
		//given
		CacheServer cacheServer = new CacheServer("foo.com", Duration.ofMillis(500), 1);

		//when
		final Maybe<String> retried = cacheServer
				.findBy(1)
				.retry(4);

		//then
		retried.test()
				.await()
//				.assertComplete();
				.assertErrorMessage("Simulated fault");
	}

	/**
	 * TODO Why this test never finishes? Add some logging and fix {@link #broken()} method.
 	 */
	@Test
	public void fixEagerMaybe() throws Exception {
		//given
		final Maybe<User> maybe = broken();

		//when
		final Maybe<User> retried = maybe.retry();

		//then
		retried.test()
				.assertValue(new User(1))
				.assertComplete();
	}

	Maybe<User> broken() {
		if (ThreadLocalRandom.current().nextDouble() > 0.1) {
			return Maybe.error(new RuntimeException("Opps"));
		}
		return Maybe.just(new User(1));
	}

	@Test
	public void retryFiveTimes() throws Exception {
		Maybe
				.error(new RuntimeException("Opps"))
				.doOnError(x -> log.warn("Exception: {}", x.toString()))
				.retry(5)
//				.retryBackoff(20, Duration.ofMillis(100), Duration.ofSeconds(2), 1)
				.subscribe();
		TimeUnit.SECONDS.sleep(5);
	}

}
