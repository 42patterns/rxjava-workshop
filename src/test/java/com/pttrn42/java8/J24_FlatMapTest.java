package com.pttrn42.java8;

import com.pttrn42.java8.stackoverflow.Question;
import com.pttrn42.java8.util.BaseFutureTest;
import org.hamcrest.CoreMatchers;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <img width="640" src="https://raw.githubusercontent.com/kubamarchwicki/completablefuture-marbles/master/images/then-compose.png">
 *
 * @link https://github.com/kubamarchwicki/completablefuture-marbles#thencompose
 */
@Ignore
public class J24_FlatMapTest extends BaseFutureTest {

	private static final Logger log = LoggerFactory.getLogger(J24_FlatMapTest.class);

	private CompletableFuture<Document> javaQuestions() {
		return CompletableFuture.supplyAsync(() ->
				client.mostRecentQuestionsAbout("java"),
				executorService);
	}

	private CompletableFuture<Question> findMostInterestingQuestion(Document document) {
		return CompletableFuture.completedFuture(new Question());
	}

	private CompletableFuture<String> googleAnswer(Question q) {
		return CompletableFuture.completedFuture("42");
	}

	private CompletableFuture<Integer> postAnswer(String answer) {
		return CompletableFuture.completedFuture(200);
	}

	@Test
	public void should_submit_answer_as_a_chain_of_operation() {
		//javaQuestions
		//findMostInterestingQuestion(Document)
		//googleAnswer(Question)
		//postAnswer(String)

		//what can I use??
		//user thenAccept or thenCompose method. which one is better??

		CompletableFuture<Integer> httpStatusFuture = javaQuestions()
				.thenCompose(this::findMostInterestingQuestion)
				.thenCompose(this::googleAnswer)
				.thenCompose(this::postAnswer);;

		httpStatusFuture.thenAccept(status -> {
			assertThat(status, CoreMatchers.equalTo(HttpStatus.OK.value()));
		});
	}

	enum HttpStatus {
		OK(200);

		private final int value;

		HttpStatus(int status) {
			this.value = status;
		}

		public int value() {
			return value;
		}
	}
}

