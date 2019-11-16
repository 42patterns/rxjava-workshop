package com.pttrn42.java8;

import com.pttrn42.java8.util.BaseFutureTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class A01_ExecutorService extends BaseFutureTest {

	private static final Logger log = LoggerFactory.getLogger(A01_ExecutorService.class);

	@Test
	public void traditionalCall() throws Exception {
		final String result = db.apply(query);     //blocks main thread
		log.debug("Query result: '{}'", result);
	}

	@Test
	public void executorService() throws Exception {
		final Callable<String> task = () -> db.apply(query);
		final Future<String> resultInFuture = executorService.submit(task);

		final String result = resultInFuture.get();   //still blocking
		log.debug("Query result: '{}'", result);
	}

}

