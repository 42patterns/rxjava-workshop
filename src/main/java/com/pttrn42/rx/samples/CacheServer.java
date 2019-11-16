package com.pttrn42.rx.samples;

import io.reactivex.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CacheServer {

	private static final Logger log = LoggerFactory.getLogger(CacheServer.class);

	private final String host;
	private final Duration delay;
	private final double failureProbability;

	public CacheServer(String host, Duration delay, double failureProbability) {
		this.host = host;
		this.delay = delay;
		this.failureProbability = failureProbability;
	}

	public Maybe<String> findBy(int id) {
		return Maybe.defer(() -> {
			final double jitter = ThreadLocalRandom.current().nextGaussian() * delay.toMillis() / 10;
			return Maybe
					.fromCallable(() -> findInternal(id))
					.doOnSubscribe(s -> log.debug("Fetching {} from {}", id, host))
					.doOnEvent((value, t) -> log.debug("Fetching {} from {}", id, host))
					.delay(delay.plus(Duration.ofMillis((long) jitter)).toMillis(), TimeUnit.MILLISECONDS);
		});
	}

	public String findBlocking(int id) {
		log.debug("Fetching {} from {}", id, host);
		Sleeper.sleepRandomly(delay);
		final String value = findInternal(id);
		log.debug("Returning {} from {}", value, host);
		return value;
	}

	private String findInternal(int id) {
		if (ThreadLocalRandom.current().nextDouble() < failureProbability) {
			throw new IllegalStateException("Simulated fault");
		}
		final String value = "Value-" + id + " from " + host;
		return "Value-" + id + " from " + host;
	}

}
