package com.pttrn42.java8;

import com.pttrn42.java8.util.BaseFutureTest;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * <pre>thenCombine</pre>
 * <img width="640" src="https://raw.githubusercontent.com/kubamarchwicki/completablefuture-marbles/master/images/then-combine.png">
 *
 * <pre>acceptToEither</pre>
 * <img width="640" src="https://raw.githubusercontent.com/kubamarchwicki/completablefuture-marbles/master/images/apply-to-either.png">
 *
 * @link https://github.com/kubamarchwicki/completablefuture-marbles#applytoeither
 * @link https://github.com/kubamarchwicki/completablefuture-marbles#thencombine
 */
@Ignore
public class J25_ZipTest extends BaseFutureTest {

	private static final Logger log = LoggerFactory.getLogger(J25_ZipTest.class);

	@Test
	public void thenCombine() throws Exception {
		final CompletableFuture<String> java = questions("java");
		final CompletableFuture<String> scala = questions("scala");

		//map question to it's length
		//combine length of both questions
		final CompletableFuture<Integer> f1 = null;
		final CompletableFuture<Integer> f2 = null;

		final CompletableFuture<Integer> sum = null;

		sum.thenAccept(s -> log.debug("Sum: {}", s));
		assertThat(sum.get(), CoreMatchers.equalTo(137));
	}

	@Test
	public void applyToEither() throws Exception {
		final CompletableFuture<String> java = questions("java");
		final CompletableFuture<String> scala = questions("scala");

		final CompletableFuture<String> both = null;

		both.thenAccept(title -> log.debug("First: {}", title));

		assertThat(both.get(), either(is("WHY DON'T COMMON MAP IMPLEMENTATIONS CACHE THE RESULT OF MAP.CONTAINSKEY() FOR MAP.GET()"))
				.or(is("UPDATE A TIMESTAMP SETTINGKEY IN AN SBT 0.12 TASK"))
		);

	}


}

