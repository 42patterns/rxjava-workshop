package com.pttrn42.rx;

import com.pttrn42.rx.user.LoremIpsum;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

@Ignore
public class R030_MapAndFilter {

	private static final Logger log = LoggerFactory.getLogger(R030_MapAndFilter.class);

	@Test
	public void mapTransformsItemsOnTheFly() throws Exception {
		//given
		final Observable<Integer> numbers = Observable.range(5, 4);

		//when
		final Observable<Integer> even = numbers.map(x -> x * 2);

		//then
		even
				.test()
				.assertValues(10, 12, 14, 16)
				.assertComplete();
	}

	@Test
	public void mapCanChangeType() throws Exception {
		//given
		final Observable<String> numbers = Observable.just(
				"Lorem", "ipsum", "dolor", "sit", "amet"
		);

		//when
		final Observable<Integer> lengths = numbers.map(String::length);

		//then
		lengths.test()
				.assertValues("Lorem".length(), "ipsum".length(), "dolor".length(), "sit".length(), "amet".length())
				.assertComplete();
	}

	/**
	 * TODO Use {@link Observable#filter(Predicate)} to choose words ending with 't'
	 */
	@Test
	public void filterSelectsOnlyMatchingElements() throws Exception {
		//given
		final Observable<String> words = Observable.just(
				"Excepteur", "sint", "occaecat", "cupidatat", "non", "proident"
		);

		//when
		final Observable<String> endingWithT = words.filter(
				s -> s.endsWith("t")
		);

		//then
		assertThat(endingWithT.blockingIterable(), contains(
				"sint", "occaecat", "cupidatat", "proident"
		));
	}

	/**
	 * TODO only pick words starting with 'e' and ending with 't'. But first remove comma or dot from word ending.
	 */
	@Test
	public void lengthOfAllWordsEndingWithT() throws Exception {
		//given
		final Observable<String> words = Observable.fromArray(LoremIpsum.words());

		//when
		final Observable<String> lengths = words
				.map(s -> s.replaceAll("[,\\.]", ""))
				.filter(s -> s.startsWith("e"))
				.filter(s -> s.endsWith("t"));

		//then
		lengths.test()
				.assertValues("elit", "elit", "est", "est", "eget", "et", "eget", "erat")
				.assertComplete();
	}

}
