package com.pttrn42.rx;

import com.pttrn42.rx.samples.CacheServer;
import com.pttrn42.rx.samples.Tuple;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.concurrent.TimeUnit;

@Ignore
public class R050_SubscribeOnPublishOn {

	private static final Logger log = LoggerFactory.getLogger(R050_SubscribeOnPublishOn.class);

	private final CacheServer reliable = new CacheServer("foo", Duration.ofMillis(1_000), 0);

	@Test
	public void sameThread() throws Exception {
		final Maybe<String> one = Maybe.fromCallable(() -> reliable.findBlocking(41));
		final Maybe<String> two = Maybe.fromCallable(() -> reliable.findBlocking(42));

		log.info("Starting");
		one.subscribe(x -> log.info("Got from one: {}", x));
		log.info("Got first response");
		two.subscribe(x -> log.info("Got from two: {}", x));
		log.info("Got second response");
	}

	@Test
	public void subscribeOn() throws Exception {
		Maybe
				.fromCallable(() -> reliable.findBlocking(41))
				.subscribeOn(Schedulers.computation())
				.doOnEvent((x,t) -> log.info("Received {}", x))
				.map(x -> {
					log.info("Mapping {}", x);
					return x;
				})
				.filter(x -> {
					log.info("Filtering {}", x);
					return true;
				})
				.doOnEvent((x, t) -> log.info("Still here {}", x))
				.subscribe(x -> log.info("Finally received {}", x));

		TimeUnit.SECONDS.sleep(2);
	}

	@Test
	public void manyStreamsButLastThreadWins() throws Exception {
		final Maybe<String> one = Maybe.fromCallable(() -> reliable.findBlocking(41));
		final Maybe<String> two = Maybe.fromCallable(() -> reliable.findBlocking(42));

		Maybe
				.zip(
						one.subscribeOn(Schedulers.computation()),
						two.subscribeOn(Schedulers.io()),
						Tuple::of
				)
				.doOnEvent((x, t) -> log.info("Received {}", x))
				.map(x -> {
					log.info("Mapping {}", x);
					return x;
				})
				.filter(x -> {
					log.info("Filtering {}", x);
					return true;
				})
				.doOnEvent((x,t) -> log.info("Still here {}", x))
				.subscribe(x -> log.info("Finally received {}", x));

		TimeUnit.SECONDS.sleep(2);
	}

	@Test
	public void publishOn() throws Exception {
		Maybe
				.fromCallable(() -> reliable.findBlocking(41))
				.subscribeOn(Schedulers.newThread())
				.doOnEvent((x, t) -> log.info("Received {}", x))
				.observeOn(Schedulers.newThread())
				.map(x -> {
					log.info("Mapping {}", x);
					return x;
				})
				.observeOn(Schedulers.newThread())
				.filter(x -> {
					log.info("Filtering {}", x);
					return true;
				})
				.observeOn(Schedulers.newThread())
				.doOnEvent((x,t) -> log.info("Still here {}", x))
				.observeOn(Schedulers.newThread())
				.subscribe(x -> log.info("Finally received {}", x));

		TimeUnit.SECONDS.sleep(2);
	}

	private static <K, V> AbstractMap.SimpleEntry<K, V> tuple(K key, V value) {
		return new AbstractMap.SimpleEntry<>(key, value);
	}

}
