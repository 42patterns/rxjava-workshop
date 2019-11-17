package com.pttrn42.rx;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

@Ignore
public class R020_SingleSubscribing {

	private static final Logger log = LoggerFactory.getLogger(R020_SingleSubscribing.class);

	@Test
	public void noWorkHappensWithoutSubscription() throws Exception {
		//given
		AtomicBoolean flag = new AtomicBoolean();

		//when
		log.info("About to create Single");
		Single.fromCallable(() -> {
			log.info("Doing hard work");
			flag.set(true);
			return 42;
		});
		log.info("Single was created");

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
		log.info("About to create Single");
		final Single<Integer> work = Single.fromCallable(() -> {
			log.info("Doing hard work");
			flag.set(true);
			return 42;
		});
		log.info("Single was created");

		final Integer result = work.blockingGet(); //TODO: make the work happen (blocking??)

		log.info("Work is done");

		//then
		assertThat(flag.get(), is(true));
		assertThat(result, is(42));
	}

	@Test
	public void subscriptionTriggersWork() throws Exception {
		//given
		log.info("About to create Single");

		//when
		final Single<Integer> work = Single.fromCallable(() -> {
			log.info("Doing hard work");
			return 42;
		});

		//then
		log.info("Single was created");

		Disposable disposable = work.subscribe(); //subscription tiggers work

		log.info("Work is done");
		assertThat(disposable.isDisposed(), is(true));
	}

	@Test
	public void subscriptionOfManyNotifications() throws Exception {
		//given
		log.info("About to create Single");

		//when
		final Single<Integer> work = Single.fromCallable(() -> {
			log.info("Doing hard work");
			return 42;
		});

		//then
		log.info("Single was created");

		work.subscribe(
				i -> log.info("Received {}", i),
				ex -> log.error("Opps!", ex)
		);

		log.info("Work is done");
	}

	private final AtomicBoolean onNext = new AtomicBoolean();
	private final AtomicReference<Throwable> error = new AtomicReference<>();

	/**
	 * TODO create a {@link Single} that completes with an error
	 */
	@Test
	public void SingleCompletingWithError() {
		//given

		//when
		final Single<Integer> work = Single.error(new IOException("Simulated"));

		//then
		work.subscribe(
				i -> { /*TODO: do something here */ },
				ex -> error.set(ex)
		);

		//Hint: you don't normally test streams like that! Don't get used to it
		assertThat(onNext.get(), is(false));
		assertThat(error.get(), instanceOf(IOException.class));
		assertThat(error.get(), hasMessage(is("Simulated")));
	}

	/**
	 * TODO create a {@link Single} that never completes
	 * What happens if you {@link Single#blockingGet()} on such {@link Single}?
	 * Hint: {@link Single#never()}
	 */
	@Test
	public void SingleThatNeverCompletesAtAll() throws Exception {
		//given

		//when
		final Single<Integer> work = Single.never();

		//then
		work.subscribe(
				i -> { /*TODO: do something here */ },
				ex -> { /*TODO: do something here */ }
		);

		//Hint: you don't normally test streams like that! Don't get used to it
		assertThat(onNext.get(), is(false));
		assertThat(error.get(), nullValue());
	}

}
