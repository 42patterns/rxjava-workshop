package com.pttrn42.rx;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@Ignore
public class R022_HowToCreateMaybeAndObservable {

	private static final Logger log = LoggerFactory.getLogger(R022_HowToCreateMaybeAndObservable.class);

	@Test
	public void eagerlyEvaluated() throws Exception {
		//when
		Maybe.just(destroyEarth());

		//then
		assertThat(destroyed.get(), is(true));
	}

	@Test
	public void lazilyEvaluateMono() throws Exception {
		//when
		Maybe.fromCallable(this::destroyEarth);

		//then
		assertThat(destroyed.get(), is(false));
	}

	private AtomicBoolean killed = new AtomicBoolean();
	private AtomicBoolean destroyed = new AtomicBoolean();

	@Test
	public void creatingEagerFluxFromStreamIncorrectly() throws Exception {
		//given
		List<Boolean> tasks = Arrays.asList(killHumanity(), destroyEarth());

		//when
		Observable.fromIterable(tasks::iterator);

		//then
		assertThat(killed.get(), is(true));
		assertThat(destroyed.get(), is(true));
	}

	@Test
	public void creatingLazyFluxFromStreamCorrectly() throws Exception {
		//when
		Observable.fromIterable(() -> Stream.of(killHumanity(), destroyEarth()).iterator());

		//then
		assertThat(killed.get(), is(false));
		assertThat(destroyed.get(), is(false));
	}

	/**
	 * TODO Make sure operations are run only once, despite two subscriptions
	 * @throws Exception
	 */
	@Test
	public void createLazyFluxStreamThatDestroysEarthOnlyOnce() throws Exception {
		//given
		final Observable<Boolean> operations = Observable.fromIterable(() -> Stream.of(killHumanity(), destroyEarth()).iterator()).cache();
		final AtomicReference<Throwable> error = new AtomicReference<>();

		//when
		operations.subscribe();
		operations.subscribe(
				__ -> {},
				error::set,
				() -> {}
		);

		//then
		assertThat(error.get(), nullValue());
	}

	private boolean killHumanity() {
		log.info("Killed");
		if (!killed.compareAndSet(false, true)) {
			throw new IllegalStateException("Already killed");
		}
		return true;
	}

	private boolean destroyEarth() {
		log.info("Destroyed");
		if (!destroyed.compareAndSet(false, true)) {
			throw new IllegalStateException("Already destroyed");
		}
		return true;
	}

}
