package com.pttrn42.rx;

import io.reactivex.Maybe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
public class R020_MaybeSubscribing {

	private static final Logger log = LoggerFactory.getLogger(R020_MaybeSubscribing.class);

	@Test
	public void noWorkHappensWithoutSubscription() throws Exception {
		//given
		AtomicBoolean flag = new AtomicBoolean();

		//when
		log.info("About to create Maybe");
		Maybe.fromCallable(() -> {
			log.info("Doing hard work");
			flag.set(true);
			return 42;
		});
		log.info("Maybe was created");

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
		log.info("About to create Maybe");
		final Maybe<Integer> work = Maybe.fromCallable(() -> {
			log.info("Doing hard work");
			flag.set(true);
			return 42;
		});
		log.info("Maybe was created");

		final Integer result = null; //TODO: make the work happen (blocking??)

		log.info("Work is done");

		//then
		assertThat(flag.get(), is(true));
		assertThat(result, is(42));
	}

	@Test
	public void subscriptionTriggersWork() throws Exception {
		//given
		log.info("About to create Maybe");

		//when
		final Maybe<Integer> work = Maybe.fromCallable(() -> {
			log.info("Doing hard work");
			return 42;
		});

		//then
		log.info("Maybe was created");

		Disposable disposable = null; //subscription tiggers work

		log.info("Work is done");
		assertThat(disposable.isDisposed(), is(true));
	}

	@Test
	public void subscriptionOfManyNotifications() throws Exception {
		//given
		log.info("About to create Maybe");

		//when
		final Maybe<Integer> work = Maybe.fromCallable(() -> {
			log.info("Doing hard work");
			return 42;
		});

		//then
		log.info("Maybe was created");

		work.subscribe(
				i -> log.info("Received {}", i),
				ex -> log.error("Opps!", ex),
				() -> log.info("Maybe completed")
		);

		log.info("Work is done");
	}

	private final AtomicBoolean onNext = new AtomicBoolean();
	private final AtomicReference<Throwable> error = new AtomicReference<>();
	private final AtomicBoolean completed = new AtomicBoolean();

	/**
	 * TODO create a {@link Maybe} that completes with an error
	 */
	@Test
	public void maybeCompletingWithError() {
		//given

		//when
		final Maybe<Integer> work = Maybe.error(new IOException("Simulated"));

		//then
		work.subscribe(
				i -> { /*TODO: do something here */ },
				ex -> { /*TODO: do something here */ },
				() -> { /*TODO: do something here */ }
		);

		//Hint: you don't normally test streams like that! Don't get used to it
		assertThat(onNext.get(), is(false));
		assertThat(error.get(), instanceOf(IOException.class));
		assertThat(error.get(), hasMessage(is("Simulated")));
		assertThat(completed.get(), is(false));
	}

	/**
	 * TODO create a {@link Maybe} that completes normally without emitting any value
	 */
	@Test
	public void maybeCompletingWithoutAnyValue() throws Exception {
		//given

		//when
		final Maybe<Integer> work = Maybe.empty();

		//then
		work.subscribe(
				i -> { /*TODO: do something here */ },
				ex -> { /*TODO: do something here */ },
				() -> { /*TODO: do something here */ }
		);

		//Hint: you don't normally test streams like that! Don't get used to it
		assertThat(onNext.get(), is(false));
		assertThat(error.get(), nullValue());
		assertThat(completed.get(), is(true));
	}

	/**
	 * TODO create a {@link Maybe} that never completes
	 * What happens if you {@link Maybe#blockingGet()} on such {@link Maybe}?
	 * Hint: {@link Maybe#never()}
	 */
	@Test
	public void maybeThatNeverCompletesAtAll() throws Exception {
		//given

		//when
		final Maybe<Integer> work = Maybe.never();

		//then
		work.subscribe(
				i -> { /*TODO: do something here */ },
				ex -> { /*TODO: do something here */ },
				() -> { /*TODO: do something here */ }
		);

		//Hint: you don't normally test streams like that! Don't get used to it
		assertThat(onNext.get(), is(false));
		assertThat(error.get(), nullValue());
		assertThat(completed.get(), is(false));	}

}
