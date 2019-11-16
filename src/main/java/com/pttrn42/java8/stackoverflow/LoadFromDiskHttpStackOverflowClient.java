package com.pttrn42.java8.stackoverflow;

import com.google.common.base.Throwables;
import com.pttrn42.java8.stackoverflow.StackOverflowClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class LoadFromDiskHttpStackOverflowClient implements StackOverflowClient {

    @Override
    public String mostRecentQuestionAbout(String tag) {
        return fetchTitleOnline(tag);
    }

    @Override
    public Document mostRecentQuestionsAbout(String tag) {
        try {
            return Jsoup
                    .parse(
                            getClass().getResourceAsStream("/stackoverflow/"+ tag + "-questions.html"),
                            "UTF-8", "http://stackoverflow.com"
                    );
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

    }

    private String fetchTitleOnline(String tag) {
        return mostRecentQuestionsAbout(tag).select("a.question-hyperlink").get(0).text();
    }

}
