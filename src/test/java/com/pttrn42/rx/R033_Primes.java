package com.pttrn42.rx;

import com.pttrn42.rx.samples.Primes;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Predicate;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

@Ignore
public class R033_Primes {

	private static final Logger log = LoggerFactory.getLogger(R033_Primes.class);

	/**
	 * TODO Generate all numbers from 2 to (sqrt(x))
	 */
	@Test
	public void generateDividers() throws Exception {
		assertThat(dividersBlocking(2), empty());
		assertThat(dividersBlocking(3), empty());
		assertThat(dividersBlocking(4), contains(2));
		assertThat(dividersBlocking(5), contains(2));
		assertThat(dividersBlocking(6), contains(2));
		assertThat(dividersBlocking(7), contains(2));
		assertThat(dividersBlocking(8), contains(2));
		assertThat(dividersBlocking(9), contains(2,3));
		assertThat(dividersBlocking(15), contains(2,3));
		assertThat(dividersBlocking(16), contains(2,3,4));
		assertThat(dividersBlocking(17), contains(2,3,4));
		assertThat(dividersBlocking(24), contains(2,3,4));
		assertThat(dividersBlocking(25), contains(2,3,4,5));
		assertThat(dividersBlocking(26), contains(2,3,4,5));
		assertThat(dividersBlocking(99), contains(2, 3, 4, 5, 6, 7, 8, 9));
		assertThat(dividersBlocking(100), contains(2, 3, 4, 5, 6, 7, 8, 9, 10));
		assertThat(dividersBlocking(101), contains(2, 3, 4, 5, 6, 7, 8, 9, 10));
	}

	/**
	 * Do not change this method
	 */
	private List<Integer> dividersBlocking(long x) {
		return dividersOf(x)
				.toList().blockingGet();
	}

	/**
	 * TODO Dividers are numbers starting at 2 up to sqrt(x)
	 */
	private Observable<Integer> dividersOf(long x) {
		final int sqrt = (int) Math.sqrt(x);
		return Observable.range(2, sqrt - 1);
	}

	/**
	 * TODO Check if number is prime by implementing {@link #isPrime(long)}
	 */
	@Test
	public void isSmallIntegerPrime() throws Exception {
		Primes.PRIMES
				.forEach(prime ->
						assertThat(prime + " is a prime number", isPrime(prime).blockingGet(), is(true))
				);
	}

	@Test
	public void smallIntegerComposite() throws Exception {
		Primes.COMPOSITE
				.forEach(prime ->
						assertThat(prime + " is a composite number", isPrime(prime).blockingGet(), is(false))
				);
	}

	@Test
	public void isBigIntegerPrime() {
		//given
		final long notPrime = Primes.LARGE - 1;
		final long prime = Primes.LARGE;

		//then
		assertThat(isPrime(notPrime).blockingGet(), is(false));
		assertThat(isPrime(prime).blockingGet(), is(true));
	}

	/**
	 * TODO Does all ({@link Observable#all(Predicate)}) number divide <code>x</code>
	 * @see Observable#all(Predicate)
	 * @see Observable#any(Predicate)
	 */
	private Single<Boolean> isPrime(long x) {
		return dividersOf(x)
				.all(div -> x % div != 0);
	}

}
