package com.pttrn42.rx;

import com.devskiller.jfairy.Fairy;
import com.pttrn42.rx.email.Email;
import com.pttrn42.rx.samples.Weather;
import com.pttrn42.rx.samples.WeatherService;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

@Ignore
public class R034_Distinct {

	private static final Logger log = LoggerFactory.getLogger(R034_Distinct.class);

	private static final Observable<String> words = Observable.just(
			"elit", "elit", "est", "est", "eget", "et", "eget", "erat"
	);

	@Test
	public void distinctWords() throws Exception {
		//when
		final Observable<String> distinct = words;

		//then
		distinct.test()
				//what would be the distinct values
				.assertValues("elit", "est", "eget", "et", "erat")
				.assertValueCount(5)
				.assertComplete();
	}

	@Test
	public void distinctLength() throws Exception {
		//given

		//when
		final Observable<String> distinct = words;

		//then
		distinct.test()
				.assertValues("elit", "est", "et")
				.assertComplete();
	}

	@Test
	public void distinctWordSequences() throws Exception {
		//when
		final Observable<String> distinct = words;

		//then
		distinct.test()
				.assertValues("elit", "est", "eget", "et", "eget", "erat")
				.assertComplete();
	}

	/**
	 * TODO Use {@link Observable#distinctUntilChanged()} to discover temperature changes
	 * greater than or equal 0.5
	 *
	 * @throws Exception
	 */
	@Test
	public void reportWeatherOnlyWhenItChangesEnough() throws Exception {
		//given
		final Observable<Weather> measurements = WeatherService.measurements();

		//when
		final Observable<Weather> changes = measurements;

		//then
		changes
				.map(Weather::getTemperature)
				.test()
				.assertValues(14.0, 16.0, 15.2, 14.0)
				.assertComplete();
	}

	/**
	 * TODO Create stream of {@link Email} messages using @{link {@link Email#random(Fairy)}} and {@link Observable#generate(Consumer)}
	 */
	@Test
	public void inboxAsStream() throws Exception {
		//given
		final Observable<Email> emails = emails();

		//when
		final Observable<Email> ten = emails.take(10);

		//then
		assertThat(ten.toList().blockingGet(), hasSize(10));

		//alternatively
		ten.test()
				.assertValueCount(10)
				.assertComplete();
	}

	Observable<Email> emails() {
		return emails(Fairy.create());
	}

	/**
	 * TODO Generate infinite stream of e-mails. Use {@link Email#random(Fairy)}
	 */
	Observable<Email> emails(Fairy fairy) {
		return Observable.empty();
	}

	/**
	 * TODO Find first 10 distinct e-mail sends from random sample
	 * <p>
	 * Hint: use parameterless {@link Observable#distinct()}
	 * </p>
	 */
	@Test
	public void findOnlyDistinctEmailSender() throws Exception {
		//given
		final Observable<Email> emails = emails();
		final int total = 10;

		//when
		final Observable<String> distinctSenders = emails.map(Email::getFrom);

		//then
		final HashSet<String> unique = new HashSet<>(distinctSenders.toList().blockingGet());
		assertThat(unique, hasSize(total));
	}

}
