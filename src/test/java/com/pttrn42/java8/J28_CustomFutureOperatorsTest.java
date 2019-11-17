package com.pttrn42.java8;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@Ignore
public class J28_CustomFutureOperatorsTest {

	private final ExecutorService pool = Executors.newFixedThreadPool(10);

	@After
	public void closePool() {
		pool.shutdownNow();
	}

	@Test
	public void shouldTimeoutIfUnderlyingFutureDoesNotResponse() throws Exception {
		//given
		CompletableFuture<String> never = FutureOps.never();

		//when
		try {
			never.get(100, MILLISECONDS);
			fail("Exception was not thrown " + TimeoutException.class);
		} catch (TimeoutException e) {
			//then
		}
	}

	/**
	 * If primary future does not complete in given time,  {@link CompletableFuture#handle(BiFunction)} timeout and return special value.
	 */
	@Test
	public void shouldTimeoutAfterSpecifiedTime() throws Exception {
		//given
		CompletableFuture<String> primary = FutureOps.never();
		CompletableFuture<String> timeout = FutureOps.timeoutAfter(Duration.ofMillis(100));
		CompletableFuture<String> any = primary
				.applyToEither(timeout, Function.identity())
				.handle((result, exception) ->
						result != null ? result : "Fallback");

		//when
		final String fallback = any.get(1, SECONDS);

		//then
		assertThat(fallback, CoreMatchers.equalTo("Fallback"));
	}

	@Test
	public void shouldConvertOldFutureToCompletableFuture() throws Exception {
		//given
		final Future<Integer> answer = pool.submit(() -> 42);

		//when
		final CompletableFuture<Integer> completableAnswer = FutureOps.toCompletable(answer);

		//then
		AtomicBoolean condition = new AtomicBoolean();
		completableAnswer.thenRun(() -> condition.set(true));
		await().untilAtomic(condition, is(true));
	}

	@Test
	public void shouldIgnoreFailures() throws Exception {
		//given
		final CompletableFuture<Integer> failed = FutureOps.failed(new UnsupportedOperationException("Don't panic!"));
		final CompletableFuture<Integer> first = completedFuture(42);
		final CompletableFuture<Integer> second = completedFuture(45);
		final CompletableFuture<Integer> broken = FutureOps.failed(new UnsupportedOperationException("Simulated"));

		//when
		final CompletableFuture<List<Integer>> succeeded = FutureOps.ignoreFailures(Arrays.asList(failed, first, second, broken));

		//then
		assertThat(succeeded.get(1, TimeUnit.SECONDS), CoreMatchers.hasItems(42, 45));
	}

	/**
	 * If it takes more than a second for future to complete, ignore it
	 */
	@Test
	public void shouldIgnoreFuturesRunningForTooLong() throws Exception {
		//given
		final CompletableFuture<Integer> later = FutureOps.delay(completedFuture(42), Duration.ofMillis(500));
		final CompletableFuture<Integer> tooLate = FutureOps.delay(completedFuture(17), Duration.ofDays(1));
		final CompletableFuture<Integer> immediately = completedFuture(45);
		final CompletableFuture<Integer> never = FutureOps.never();

		final List<CompletableFuture<Integer>> futures = Arrays.asList(later, tooLate, immediately, never);

		final List<CompletableFuture<Integer>> withTimeouts = futures
				.stream()
				.map(f -> {
					final CompletableFuture<Integer> timeout = FutureOps.timeoutAfter(Duration.ofSeconds(1));
					return f.applyToEither(timeout, Function.identity());
				})
				.collect(toList());

		//when
		CompletableFuture<List<Integer>> fastAndSuccess = FutureOps.ignoreFailures(withTimeouts);

		//then
		assertThat(fastAndSuccess.get(1, TimeUnit.SECONDS), hasItems(42, 45));
	}

}

class FutureOps {

	private static final ScheduledExecutorService pool = Executors.newScheduledThreadPool(10,
			new ThreadFactoryBuilder()
					.setDaemon(true)
					.setNameFormat("FutureOps-%d")
					.build()
	);

	public static <T> CompletableFuture<T> failed(Throwable t) {
		final CompletableFuture<T> future = new CompletableFuture<T>();
		future.completeExceptionally(t);
		return future;
	}

	public static <T> CompletableFuture<T> never() {
		return new CompletableFuture<>();
	}

	/**
	 * Fails with {@link TimeoutException} after given time
	 */
	public static <T> CompletableFuture<T> timeoutAfter(Duration duration) {
		final CompletableFuture<T> promise = new CompletableFuture<>();
		pool.schedule(
				() -> promise.completeExceptionally(new TimeoutException()),
				duration.toMillis(), TimeUnit.MILLISECONDS);
		return promise;
	}

	/**
	 * Should not block but return {@link CompletableFuture} immediately.
	 */
	public static <T> CompletableFuture<T> toCompletable(Future<T> future) {
		final CompletableFuture<T> promise = new CompletableFuture<>();
		pool.submit(() -> promise.complete(future.get()));
		return promise;
	}

	/**
	 * Filters out futures that failed. Preserves order of input, no matter what was the completion order
	 */
	public static <T> CompletableFuture<List<T>> ignoreFailures(List<CompletableFuture<T>> futures) {
		final CompletableFuture<List<T>> promise = new CompletableFuture<>();
		final List<T> results = new CopyOnWriteArrayList<>();
		IntStream.range(0, futures.size()).forEach(i -> results.add(null));
		waitForAllToCompleteOrFail(futures, promise, results);
		return promise;
	}

	private static <T> void waitForAllToCompleteOrFail(List<CompletableFuture<T>> futures, CompletableFuture<List<T>> promise, List<T> results) {
		final AtomicInteger counter = new AtomicInteger(futures.size());
		for (int i = 0; i < futures.size(); i++) {
			final int idx = i;
			futures.get(i).handle((BiFunction<T, Throwable, T>) (result, throwable) -> {
				if (result != null) {
					results.set(idx, result);
				}
				if (counter.decrementAndGet() == 0) {
					promise.complete(filterOutNulls(results));
				}
				return null;
			});
		}
	}

	private static <T> List<T> filterOutNulls(List<T> results) {
		return results
				.stream()
				.filter(x -> x != null)
				.collect(toList());
	}

	/**
	 * Takes a {@link CompletableFuture} and returns compatible future, but that completes with delay.
	 * E.g. if underlying future completes after 7 seconds, and we call this method with 2 seconds duration,
	 * resulting future will complete after 9 seconds.
	 * @return {@link CompletableFuture} which completes after underlying future with given duration
	 */
	public static <T> CompletableFuture<T> delay(CompletableFuture<T> future, Duration duration) {
		final CompletableFuture<T> promise = new CompletableFuture<>();
		future.thenAccept(result -> {
			pool.schedule(() -> promise.complete(result), duration.toMillis(), TimeUnit.MILLISECONDS);
		});
		return promise;
	}

}