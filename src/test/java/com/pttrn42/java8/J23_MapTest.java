package com.pttrn42.java8;

import com.pttrn42.java8.util.BaseFutureTest;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * <img width="640" src="https://raw.githubusercontent.com/kubamarchwicki/completablefuture-marbles/master/images/then-apply.png">
 *
 * @link https://github.com/kubamarchwicki/completablefuture-marbles#thenapply
 */
@Ignore
public class J23_MapTest extends BaseFutureTest {

    private static final Logger log = LoggerFactory.getLogger(J23_MapTest.class);

    @Test
    public void should_return_length_of_java_question() {
        final CompletableFuture<Document> java = CompletableFuture.supplyAsync(() ->
                        client.mostRecentQuestionsAbout("java"));

        final Document document = java.join();       //blocks
        final Element element = document.select("a.question-hyperlink").get(0);
        final String title = element.text();
        final int length = title.length();

        log.debug("Length: {}", length);
        assertThat(length, equalTo(88));
    }

    @Test
    public void should_return_length_of_java_question_blocking_at_the_end() {
        final CompletableFuture<Document> java = CompletableFuture.supplyAsync(() ->
                        client.mostRecentQuestionsAbout("java"));

        final CompletableFuture<Integer> length = java.
                thenApply(doc -> doc.select("a.question-hyperlink").get(0)).
                thenApply(Element::text).
                thenApply(String::length);

        final int result = length.join();
        log.debug("Length: {}", result);
        assertThat(result, equalTo(88));
    }
}

