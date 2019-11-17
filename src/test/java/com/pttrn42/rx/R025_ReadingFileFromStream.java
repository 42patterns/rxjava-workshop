package com.pttrn42.rx;

import com.pttrn42.rx.samples.NotFound;
import io.reactivex.Observable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import static com.pttrn42.rx.samples.ObservableStreamInterop.fromStream;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class R025_ReadingFileFromStream {

	private static final Logger log = LoggerFactory.getLogger(R025_ReadingFileFromStream.class);

	/**
	 * TODO Read <code>/logback-test.xml</code> file using {@link BufferedReader#lines()} and {@link Observable#fromIterable(Iterable)}
	 * <p>Hint: use {@link #open(String)} helper method</p>
	 */
	@Test
	public void readFileAsStreamOfLines() throws Exception {
		//when
		final Observable<String> lines = null;

		//then
		final Long count = lines
				.count()
				.blockingGet();

		assertThat(count, is(17l));
	}

	private BufferedReader open(String path) {
		final InputStream stream = getClass().getResourceAsStream(path);
		if (stream == null) {
			throw new NotFound(path);
		}
		return new BufferedReader(new InputStreamReader(stream));
	}

	/**
	 * TODO Use {@link Observable#defer(Callable)} in order to make eager stream lazy
	 */
	@Test
	public void readingFileShouldBeLazy() throws Exception {
		//when
		final Observable<String> lines = notFound();

		//then
		lines.test()
				.assertError(NotFound.class);
		}

	/**
	 * Don't change this method!
	 */
	private Observable<String> notFound() {
		return fromStream(open("404.txt").lines());
	}

}

