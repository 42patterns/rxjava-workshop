package com.pttrn42.rx;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertTrue;

@Ignore
public class R051_WhatIsAscheduler {

	private static final Logger log = LoggerFactory.getLogger(R051_WhatIsAscheduler.class);

	/**
	 * TODO Implement {@link #customScheduler()}
	 */
	@Test
	public void createCustomScheduler() throws Exception {
		//given
		AtomicReference<String> seenThread = new AtomicReference<>();
		final Maybe<Void> mono = Maybe.fromRunnable(() -> {
			seenThread.set(Thread.currentThread().getName());
		});

		//when
		mono
				.subscribeOn(customScheduler())
				.blockingGet();

		//then
		assertTrue(seenThread.get().matches("Custom-\\d+"));
	}

	/**
	 * TODO Implement custom bound scheduler.
	 * It must contain 10 threads named "Custom-" and a sequence number.
	 * @see Executors#newSingleThreadExecutor(ThreadFactory)
	 * @see ThreadFactoryBuilder
	 * @see Schedulers#from(Executor)
	 */
	private Scheduler customScheduler() {
		return Schedulers.from(
				Executors.newSingleThreadExecutor(
						new ThreadFactoryBuilder().setNameFormat("Custom-").build()
				)
		);
	}
}
