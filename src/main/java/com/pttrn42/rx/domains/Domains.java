package com.pttrn42.rx.domains;

import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class Domains {

	/**
	 * TODO Convert file to stream of {@link Domain}
	 *
	 * @see Observable#fromIterable(Iterable) (Supplier)
	 * @see io.reactivex.Maybe#defaultIfEmpty(Object)
	 */
	public static Observable<Domain> all() {
		return Observable.fromIterable(() -> open().lines().iterator())
				.map(Domains::parse)
				.filter(Optional::isPresent)
				.map(Optional::get);
	}

	/**
	 * File from <a href="https://moz.com/top500">https://moz.com/top500</a>.
	 * Don't change this.
	 */
	private static BufferedReader open() {
		log.debug("Reading domains file");
		final InputStream stream = Domains.class.getResourceAsStream("/domains/top500.csv");
		return new BufferedReader(new InputStreamReader(stream));
	}

	/**
	 * Don't change this
	 */
	private static Optional<Domain> parse(String line) {
		final String[] columns = line.split(",");
		try {
			Integer.parseInt(columns[0]);
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
		try {
			final Domain domain = new Domain(
					new URL("http://" + columns[1].substring(1, columns[1].length() - 2)),
					Long.parseLong(columns[2]),
					Long.parseLong(columns[3]),
					Float.parseFloat(columns[4]),
					Float.parseFloat(columns[5])
			);
			return Optional.of(domain);
		} catch (Exception e) {
			throw new IllegalArgumentException("For line: " + line, e);
		}
	}

}
