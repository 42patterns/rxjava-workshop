package com.pttrn42.rx.samples;

import io.reactivex.Maybe;
import io.reactivex.Observable;

import java.util.concurrent.TimeUnit;

public class Ping {

	public static Maybe<Boolean> check(String host) {
		return Maybe
				.<Boolean>empty()
				.delaySubscription(10, TimeUnit.MILLISECONDS);
	}

	public static Observable<Boolean> checkConstantly(String host) {
		switch (host) {
			case "buggy.com":
				return Observable.fromArray(true, true, true, false, true, false, false, true, true, false, false, false).repeat();
			case "vary.com":
				return Observable.concat(
						Observable.just(true, true, true)
								.delay(10, TimeUnit.MILLISECONDS),
						Observable.just(true)
								.delay(20, TimeUnit.MILLISECONDS),  // 3, 11, 19, 27
						Observable.just(true, true, true)
								.delay(10, TimeUnit.MILLISECONDS),
						Observable.just(true)
								.delay(50, TimeUnit.MILLISECONDS)
				).repeat();
			default:
				return Observable.just(true, true, false, false).repeat();
		}
	}

}
