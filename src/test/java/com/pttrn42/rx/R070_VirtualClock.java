package com.pttrn42.rx;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class R070_VirtualClock {

	private static final Logger log = LoggerFactory.getLogger(R070_VirtualClock.class);

	/**
	 * Bad practice - tests take a long time to finish
	 *
	 */
	@Test
	public void realTime() throws Exception {
		longRunning().test()
				.assertSubscribed()
				.await()
				.assertValue("OK")
				.assertComplete();
	}

	/**
	 * TODO Apply {@link Maybe#timeout(long, TimeUnit)} of 1 second to a return value from {@link #longRunning()} method and verify it works
	 */
	@Test
	public void timeout() throws Exception {
		//TODO Write whole test :-)
		longRunning()
				.test()
				.assertValue("OK")
				.assertComplete();
	}

	/**
	 * Hint: use {@link TestScheduler} to manipulate time
	 * Hint: make sure you pass the right scheduler to {@link this#longRunning()}
	 * Hint: advance time with {@link TestScheduler#advanceTimeBy(long, TimeUnit)}
	 */
	@Test
	public void virtualTime() throws Exception {
		long start = System.currentTimeMillis();

		TestObserver<String> testObserver = longRunning()
				.test()
				.assertNoValues();

		testObserver.assertSubscribed()
				.await()
				.assertValue("OK")
				.assertComplete();

		assertTrue("Test too too long", System.currentTimeMillis() - start < 2_000);
	}

	Maybe<String> longRunning(Scheduler scheduler) {
		return Maybe
				.timer(2000, TimeUnit.MILLISECONDS, scheduler)
				.map(x -> "OK");
	}

	Maybe<String> longRunning() {
		return longRunning(Schedulers.io());
	}

}
