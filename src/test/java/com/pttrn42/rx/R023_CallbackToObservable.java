package com.pttrn42.rx;

import com.pttrn42.rx.email.Email;
import com.pttrn42.rx.email.Inbox;
import io.reactivex.Observable;
import io.reactivex.ObservableConverter;
import io.reactivex.ObservableSource;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@Ignore
public class R023_CallbackToObservable {

    private static final Logger log = LoggerFactory.getLogger(R023_CallbackToObservable.class);

    private final Inbox inbox = new Inbox();

    @After
    public void closeInbox() throws Exception {
        inbox.close();
    }

    @Test
    public void oldSchoolCallbackApi() throws Exception {
        inbox.read("test@example.com", email ->
                log.info("You have e-mail\n{}", email)
        );
        TimeUnit.SECONDS.sleep(10);
    }

    /**
     * TODO Receive first five messages from any inbox
     * Hint: <code>emails</code> list must be thread safe
     * Notice: how do you stop subscription?
     */
    @Test(timeout = 5_000)
    public void getFirstFiveEmails() throws Exception {
        //given
        List<Email> emails = new CopyOnWriteArrayList<>();

        //when
        inbox.read("foo@example.com", email -> {
                    log.info("You have e-mail\n{}", email);
                }
        );
        inbox.read("bar@example.com", email -> {
                    log.info("You have e-mail\n{}", email);
                }
        );

        //then
        await().until(() -> emails, hasSize(5));
    }

    @Test(timeout = 3_000)
    public void convertCallbacksToStream() throws Exception {
        //given
        final Observable<Email> emails = Observable.create(sink -> {});

        //when
        final List<Email> list = emails
                //TODO: take just a few of the infinite stream
                .toList().blockingGet();

        //then
        assertThat(list, hasSize(5));
    }

    @Test(timeout = 3_000)
    public void testingUsingTestObservable() throws Exception {
        //given
        final Observable<Email> emails = Observable.create(sink -> {});

        //when
        emails.test()
                .assertComplete();
    }

    /**
     * TODO Merge two streams of messages into a single stream and {@link Observable#take(long)} the first 5.
     * Hint Static {@link Observable#merge(ObservableSource)}
     */
    @Test
    public void mergeMessagesFromTwoInboxes() throws Exception {
        //given
        final Observable<Email> foo = Observable.create(sink ->
                inbox.read("foo@example.com", sink::onNext));
        final Observable<Email> bar = Observable.create(sink ->
                inbox.read("bar@example.com", sink::onNext));

        //when
        Observable<Email> merged = null;

        //then
        merged.test()
				.await()
                .assertValueCount(5)
                .assertComplete();
    }

    @Test
    public void observableDoesNotAcceptNull() throws Exception {
        Observable
                .create(sink -> inbox.read("spam@example.com", sink::onNext))
                .blockingLast();
    }

    /**
     * TODO terminate the stream when <code>null</code> is received from callback.
     * Hint: <code>sink.complete()</code>
     */
    @Test
    public void handleNullAsEndOfStream() throws Exception {
        //when
        final Observable<Email> emails = Observable
                .create(sink -> {});

        //then
        emails
                .test()
                .await()
                .assertValueCount(2)
                .assertComplete();
    }

    /**
     * TODO use {@link Observable#as(ObservableConverter)} operator with {@link #toList(Observable)} method
     */
    @Test
    public void asOperator() throws Exception {
        //given
        final Observable<Email> foo = Observable.create(sink ->
                inbox.read("foo@example.com", sink::onNext));

        //when
        final List<Email> emails = null;

        //then
        assertThat(emails, hasSize(3));
    }

    List<Email> toList(Observable<Email> input) {
        return input
                .take(3)
                .toList().blockingGet();
    }

    @Test
    public void observableCreateIsNotCached() throws Exception {
        //given
        AtomicInteger subscriptionCount = new AtomicInteger();

        final Observable<Email> foo = Observable.create(sink -> {
            subscriptionCount.incrementAndGet();
            log.info("Subscribing to e-mails");
            inbox.read("foo@example.com", sink::onNext);
        });

        //when
        foo.subscribe();
        foo.subscribe();

        //then
        assertThat(subscriptionCount.get(), equalTo(1));
    }

}
