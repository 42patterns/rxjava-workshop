package com.pttrn42.rx;

import io.reactivex.Observable;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@Ignore
public class R042_Casting {

	private static final Logger log = LoggerFactory.getLogger(R042_Casting.class);

	@Test
	public void castingElements() throws Exception {
		//given
		Observable<Number> nums = Observable.just(1, 2, 3);

		//when
		final Observable<Integer> ints = nums.cast(Integer.class);

		//then
		final List<Integer> list = ints.toList().blockingGet();
		assertThat(list, IsIterableContainingInOrder.contains(1, 2, 3));
	}

	@Test
	public void castingWithFiltering() throws Exception {
		//given
		Observable<Number> nums = Observable.just(1, 2.0, 3.0f);

		//when
		final Observable<Double> doubles = nums
				.filter(x -> Double.class.isInstance(x))
				.cast(Double.class);

		//then
		doubles.test()
				.assertValue(2.0)
				.assertComplete();
	}


}
