package com.pttrn42.java8.stackoverflow;

import org.jsoup.nodes.Document;

public class AlwaysFailHttpStackOverflowClient implements StackOverflowClient {

	@Override
	public String mostRecentQuestionAbout(String tag) {
		return fetchTitleOnline(tag);
	}

	@Override
	public Document mostRecentQuestionsAbout(String tag) {
		throw new RuntimeException("Always fail http client");
	}

	private String fetchTitleOnline(String tag) {
		return mostRecentQuestionsAbout(tag).select("a.question-hyperlink").get(0).text();
	}

}
