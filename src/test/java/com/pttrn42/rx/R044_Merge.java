package com.pttrn42.rx;

import com.pttrn42.rx.samples.CacheServer;
import io.reactivex.CompletableSource;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofMillis;

@Ignore
public class R044_Merge {

	private static final Logger log = LoggerFactory.getLogger(R044_Merge.class);

	@Test
	public void mergeCombinesManyStreams() throws Exception {
		//given
		final Observable<String> fast = Observable
				.interval(90, TimeUnit.MILLISECONDS).map(x -> "F-" + x);
		final Observable<String> slow = Observable
				.interval(100, TimeUnit.MILLISECONDS).map(x -> "S-" + x);

		//when
		final Observable<String> merged = Observable.merge(
				fast,
				slow
		);

		//then
		merged.subscribe(log::info);
		TimeUnit.SECONDS.sleep(2);
	}

	@Test
	public void mergingMaybes() throws Exception {
		//given
		final Maybe<BigDecimal> fast = Maybe
				.just(BigDecimal.valueOf(1))
				.delay(200, TimeUnit.MILLISECONDS);

		final Maybe<BigDecimal> slow = Maybe
				.just(BigDecimal.valueOf(2))
				.delay(100, TimeUnit.MILLISECONDS);

		//when
		final Observable<BigDecimal> merged = Observable.merge(
				fast.toObservable(),
				slow.toObservable()
		);

		//then
		merged.subscribe(d -> log.info("Received {}", d));
		TimeUnit.SECONDS.sleep(2);
	}

	private CacheServer first = new CacheServer("foo.com", ofMillis(20), 0);
	private CacheServer second = new CacheServer("bar.com", ofMillis(20), 0);

	/**
	 * TODO Fetch data from first available cache server
	 * @see Observable#mergeWith(CompletableSource)
	 * @see Observable#firstElement()
	 */
	@Test
	public void fetchDataFromFirstAvailableServer() throws Exception {
		//given
		final Maybe<String> fv = Maybe.never();
		final Maybe<String> sv = Maybe.never();

		//when
		Maybe<String> fastest = fv.mergeWith(sv)
				.timeout(500, TimeUnit.MILLISECONDS)
				.firstElement();

		//then
		fastest
				.doOnEvent(log::info)
				.test()
				.await()
				.assertValue(v -> v.startsWith("Value-42"))
				.assertComplete();
	}

}
