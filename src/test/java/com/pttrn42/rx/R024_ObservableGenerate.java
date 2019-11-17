package com.pttrn42.rx;

import com.pttrn42.rx.samples.Tuple;
import io.reactivex.Emitter;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@Ignore
public class R024_ObservableGenerate {

	private static final Logger log = LoggerFactory.getLogger(R024_ObservableGenerate.class);

	/**
	 * Backpressure-aware
	 */
	@Test(timeout = 1_000)
	public void generateRandomStreamOfNumbers() throws Exception {
		//given
		final Observable<Float> randoms = Observable
				.generate(sink -> {});

		//when
		final Observable<Float> twoRandoms = randoms.take(2); //how many should we take

		//then
		twoRandoms
				.test()
				.assertValueAt(0, x -> x >= 0f && x < 1f)
				.assertValueAt(1, x -> x >= 0f && x < 1f)
				.assertComplete();
	}

	/**
	 * TODO Generate a stream of random <code>true</code> and <code>false</code>
	 */
	@Test(timeout = 1_000)
	public void generateRandomBooleans() throws Exception {
		//given
		final int total = 1_000;
		final Observable<Boolean> randoms = Observable
				.generate(sink -> {
					sink.onNext(ThreadLocalRandom.current().nextBoolean());
				});

		//when
		final Observable<Boolean> thousandBooleans = randoms;

		//then
		final Long trues = thousandBooleans.filter(x -> x == true).count().blockingGet();
		final Long falses = thousandBooleans.filter(x -> x == false).count().blockingGet();

		assertThat(trues, is(both(greaterThan(400L)).and(lessThan(600L))));
		assertThat(falses, is(both(greaterThan(400L)).and(lessThan(600L))));

		//and
		//TODO Uncomment line below. Why does it fail? Fix it
//		assertThat(trues + falses).isEqualTo(total);
	}

	@Test(timeout = 1_000)
	public void statefulGenerate() throws Exception {
		//given
		final Observable<Integer> naturals = Observable.generate(
				() -> 0,
				(state, sink) -> {});

		//when
		final Observable<Integer> three = naturals
				.take(3);

		//then
		three.test()
				.assertValues(0, 1, 2)
				.assertComplete();
	}

	/**
	 * TODO Implement stream of powers of two (1, 2, 4, 8, 16)
	 */
	@Test(timeout = 1_000)
	public void powersOfTwo() throws Exception {
		//given
		final Observable<BigInteger> naturals = Observable.generate(() -> ONE,
				(state, sink) -> {});

		//when
		final Observable<BigInteger> three = naturals
				.skip(9)
				.take(3);

		//then
		three.test()
				.assertValues(BigInteger.valueOf(1024),
						BigInteger.valueOf(2048),
						BigInteger.valueOf(4096))
				.assertComplete();
	}

	/**
	 * TODO Generate Fibonacci sequence. Hints:
	 * <p><ul>
	 * <li>Your initial state is a pair <code>(0L, 1L)</code> ({@link com.pttrn42.rx.samples.Tuple#of(Object, Object)})</li>
	 * <li>The first item is the sum <code>0 + 1</code></li>
	 * <li>New state is the right value from a pair and a sum: <code>(a, b) -> (b, a + b)</code></li>
	 * </ul>
	 * </p>
	 * <table>
	 * <tr><td>(0, 1)</td> <td>1</td></tr>
	 * <tr><td>(1, 1)</td> <td>2</td></tr>
	 * <tr><td>(1, 2)</td> <td>3</td></tr>
	 * <tr><td>(2, 3)</td> <td>5</td></tr>
	 * <tr><td>(3, 5)</td> <td>8</td></tr>
	 * </table>
	 */
	@Test(timeout = 3_000)
	public void fibonacci() throws Exception {
		//given
		final Observable<Long> fib = null;

		//when
		final Observable<Long> first10 = fib.take(10);

		//then
		first10.test()
				.assertValues(1L, 2L, 3L, 5L, 8L, 13L, 21L, 34L, 55L, 89L)
				.assertComplete();
	}

	/**
	 * TODO Compute 1000th Fibonacci using {@link BigInteger}.
	 * <p>Hint: start by copy-pasting solution above</p>
	 *
	 * @see <a href="https://www.bigprimes.net/archive/fibonacci/999/">table</a>
	 */
	@Test(timeout = 1_000)
	public void compute1000thFibonacciUsingBigInteger() throws Exception {
		//given
		final Observable<BigInteger> fib = null;


		//when
		final Maybe<BigInteger> thousandth = fib
				.skip(997)  //off-by-one (two) - True Fibonacci starts with 0, 1, 1, 2
				.firstElement();

		//then
		thousandth.test()
				.assertValue(new BigInteger("26863810024485359386146727202142923967616609318986952340123175997617981700247881689338369654483356564191827856161443356312976673642210350324634850410377680367334151172899169723197082763985615764450078474174626"))
				.assertComplete();
	}

	/**
	 * TODO Fix exercise above by prepending first two elements (0 and 1) in the beginning
	 * <p>Hint: preferrably use {@link Observable#startWith(Object)}</p>
	 *
	 * @throws Exception
	 */
	@Test(timeout = 1_000)
	public void prependFirstTwoFibonacci() throws Exception {
		//given
		final Observable<BigInteger> fib = null;


		//when
		final Maybe<BigInteger> thousandth = fib
				.startWith(ZERO)
				.startWith(ONE)
				.skip(999)
				.firstElement();

		//then
		thousandth
				.test()
				.assertValue(new BigInteger("26863810024485359386146727202142923967616609318986952340123175997617981700247881689338369654483356564191827856161443356312976673642210350324634850410377680367334151172899169723197082763985615764450078474174626"))
				.assertComplete();
	}

	/**
	 * TODO Read a file line-by-line
	 * <p>Hints:
	 * <ul>
	 * <li>Your initial state should be a {@link BufferedReader}, see {@link #open(String)} helper method</li>
	 * <li>remember to close the file (third argument to {@link Observable#generate(Callable, BiFunction, Consumer)}</li>
	 * </ul>
	 * </p>
	 */
	@Test(timeout = 1_000)
	public void readingFileLineByLine() throws Exception {
		//given
		Observable<String> lines = Observable
				.generate(
						() -> (BufferedReader) null /* TODO initial state, read /logback-test.xml */,
						(reader, sink) -> {
							readLine(reader, sink);
							return reader;
						} /* TODO something here */);

		//when
		final Observable<String> first10lines = lines.take(10);

		//then
		final List<String> list = first10lines
				.toList()
				.blockingGet();

		assertThat(list, hasSize(10));
	}

	@Test(timeout = 1_000)
	public void makeSureWeUnderstandEndOfFile() throws Exception {
		//given
		Observable<String> lines = null;
		//when

		//then
		lines.test()
				.assertValueCount(17)
				.assertComplete();
	}

	private void closeQuitely(AutoCloseable closeable) {
		try {
			log.info("Closing {}", closeable);
			closeable.close();
		} catch (Exception e) {
			log.warn("Unable to close {}", closeable, e);
		}
	}

	private void readLine(BufferedReader file, Emitter<String> sink) {
		//TODO Implement, remember about end of file,
	}

	private BufferedReader open(String path) {
		return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path)));
	}

}
