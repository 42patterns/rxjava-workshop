package com.pttrn42.rx;

import com.pttrn42.rx.samples.Ping;
import com.pttrn42.rx.samples.Tuple;
import com.pttrn42.rx.user.Order;
import com.pttrn42.rx.user.User;
import com.pttrn42.rx.user.UserOrders;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;

@Ignore
public class R043_Zip {

    private static final Logger log = LoggerFactory.getLogger(R043_Zip.class);

    /**
     * Use ({@link Tuple#of(Object, Object)}) as zipper</li>
     *
     * @throws Exception
     */
    @Test
    public void zipTwoStreams() throws Exception {
        //given
        final Observable<Integer> nums = Observable.just(1, 2, 3);
        final Observable<String> strs = Observable.just("a", "b");

        //when
        final Observable<String> pairs = Observable.zip(
                nums, strs,
                (i, s) -> s + i
        );
        final Observable<Tuple<Integer, String>> pairs2 = nums.zipWith(
                strs,
                Tuple::of
        );

        //then
        pairs.subscribe(p -> log.info("Pair: {}", p));
    }

    @Test
    public void customCombinator() throws Exception {
        //given
        final Observable<Integer> nums = Observable.just(1, 2, 3);
        final Observable<String> strs = Observable.just("a", "bc", "def");

        //when
        // take a number, times string length and multiply by two
        final Observable<Double> doubles = Observable.zip(
                nums, strs,
                (i, s) -> Double.valueOf(2 * i * s.length())
        );

        //then
        doubles.test()
                .assertValues(2.0, 8.0, 18.0)
                .assertComplete();
    }

    @Test
    public void pairwise() throws Exception {
        //given
        final Observable<Long> fast = Observable.interval(200, TimeUnit.MILLISECONDS);
        final Observable<Long> slow = Observable.interval(500, TimeUnit.MILLISECONDS);

        //when
        Observable.zip(
                fast, slow, Tuple::of
        ).subscribe(
                pair -> log.info("Got {}", pair)
        );

        //then
        TimeUnit.SECONDS.sleep(3);
    }

    /**
     * TODO Increase sleep at the end. You should see an exception after a while
     */
    @Test
    public void latest() throws Exception {
        //given
        final Observable<Long> fast = Observable.interval(100, TimeUnit.MILLISECONDS).delay(1, TimeUnit.SECONDS);
        final Observable<Long> slow = Observable.interval(250, TimeUnit.MILLISECONDS);

        //when
        Observable.combineLatest(
                fast, slow,
                Tuple::of
        ).subscribe(
                pair -> log.info("Got {}", pair)
        );

        //then
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void errorBreaksZip() throws Exception {
        //given
        final Observable<Integer> nums = Observable.just(1, 2, 3);
        final Observable<String> strs = Observable.concat(
                Observable.just("a", "b"),
                Observable.error(new RuntimeException("Opps"))
        );

        //when
        final Observable<Tuple<Integer, String>> pairs = nums.zipWith(
                strs, Tuple::of);

        //then
        pairs.test()
                .assertValues(Tuple.of(1, "a"), Tuple.of(2, "b"))
                .assertErrorMessage("Opps");
    }

    @Test
    public void maybeCompleted() throws Exception {
        //given
        final Maybe<Integer> num = Maybe.just(1);
        final Maybe<String> str = Maybe.just("a");

        //when
        final Maybe<Tuple<Integer, String>> pair = num.zipWith(str, Tuple::of);

        //then
        assertThat(pair.blockingGet(), is(Tuple.of(1, "a")));
    }

    @Test
    public void maybeOneEmpty() throws Exception {
        //given
        final Maybe<Integer> num = Maybe.just(1);
        final Maybe<String> str = Maybe.empty();

        //when
        final Maybe<Tuple<Integer, String>> pair = num.zipWith(str, Tuple::of);

        //then
        assertThat(pair.blockingGet(), nullValue());
    }

    @Test
    public void realLifeMaybe() throws Exception {
        //given
        final Maybe<Order> order = UserOrders.lastOrderOf(new User(20));
        final Maybe<Boolean> ping = Ping.checkConstantly("example.com").firstElement();

        //when
        final Maybe<Tuple<Order, Boolean>> result = Maybe.zip(
                order,
                ping,
                Tuple::of
        );

        //what just happened here?
        System.out.println("result = " + result.toSingle().blockingGet());
    }

}
