package com.pttrn42.rx.user;

import com.devskiller.jfairy.Fairy;
import io.reactivex.Observable;

public class LoremIpsum {

	private static final Fairy fairy = Fairy.create();

	public static Observable<String> wordStream() {
		return Observable
				.fromArray(words())
				.map(word -> word.replaceAll("[.,]", ""));
	}

	public static String[] words() {
		return text().split("\\s");
	}

	public static String text() {
		return fairy.textProducer().loremIpsum();
	}

}
