package com.pttrn42.rx;

import com.pttrn42.rx.samples.Ping;
import com.pttrn42.rx.user.LoremIpsum;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

@Ignore
public class R041_Window {

    private static final Logger log = LoggerFactory.getLogger(R041_Window.class);

    @Test
    public void window() throws Exception {
        //given
        final Observable<Integer> nums = Observable.range(1, 10);

        //when
        final Observable<Observable<Integer>> windowsBadly = nums.window(3);
        final Observable<List<Integer>> windows = nums
                .window(3)
                .flatMapSingle(Observable::toList);

        //then
        windows.test()
                .assertValueAt(0, Arrays.asList(1, 2, 3))
                .assertValueAt(1, Arrays.asList(4, 5, 6))
                .assertValueAt(2, Arrays.asList(7, 8, 9))
                .assertValueAt(3, Arrays.asList(10))
                .assertComplete();
    }

    @Test
    public void overlapping() throws Exception {
        //given
        final Observable<Integer> nums = Observable.range(1, 8);

        //when
        final Observable<List<Integer>> windows = nums
                .window(3, 2)
                .flatMapSingle(Observable::toList);

        //then
        windows.test()
                .assertValueAt(0, Arrays.asList(1, 2, 3))
                .assertValueAt(1, Arrays.asList(3, 4, 5))
                .assertValueAt(2, Arrays.asList(5, 6, 7))
                .assertValueAt(3, Arrays.asList(7, 8))
                .assertComplete();
    }

    @Test
    public void gaps() throws Exception {
        //given
        final Observable<Integer> nums = Observable.range(1, 10);

        //when
        final Observable<List<Integer>> windows = nums
                .window(2, 3)
                .flatMapSingle(Observable::toList);

        //then
        windows.test()
                .assertValueAt(0, Arrays.asList(1, 2))
                .assertValueAt(1, Arrays.asList(4, 5))
                .assertValueAt(2, Arrays.asList(7, 8))
                .assertValueAt(3, Arrays.asList(10))
                .assertComplete();
    }

    /**
     * TODO Find every third word in a sentence using {@link Observable#window(long, long)}
     * <p>
     * Hint: {@link Observable#skip(long)} <i>may</i> also help, or maybe {@link Observable#singleElement()} that yields first element?
     * </p>
     */
    @Test
    public void everyThirdWord() throws Exception {
        //given
        final Observable<String> words = Observable
                .fromArray(LoremIpsum.words()).take(14);

        //when
        final Observable<String> third = words.window(3)
                .flatMapMaybe(o -> o.skip(2).singleElement());

        //then
        assertThat(third.toList().blockingGet(), contains("dolor", "consectetur", "Proin", "suscipit"));
    }

    /**
     * TODO Count how many frames there are approximately per second
     * <p>
     * Hint: use {@link Observable#window(long, TimeUnit)} and most likely {@link Observable#count()}
     * </p>
     */
    @Test
    public void countFramesPerSecond() throws Exception {
        //given
        final Observable<Long> frames = Observable
                .interval(16, TimeUnit.MILLISECONDS);

        //when
        //TODO operator here, add take(4)
        final Observable<Long> fps = frames.window(1, TimeUnit.SECONDS)
                .flatMapSingle(
                        Observable::count
                ).take(4);

        //then
        fps.test()
                .await()
                .assertValueAt(0, x -> x >= 55 && x <= 65)
                .assertValueAt(1, x -> x >= 55 && x <= 65)
                .assertValueAt(2, x -> x >= 55 && x <= 65)
                .assertValueAt(3, x -> x >= 55 && x <= 65)
                .assertComplete();
    }

    /**
     * TODO using moving, overlapping window discover three subsequent false values
     * <p>
     * Hint: use {@link Observable#window(long, TimeUnit)} and {@link Observable#doOnNext(Consumer)} to troubleshoot
     * </p>
     */
    @Test
    public void discoverIfThreeSubsequentPingsFailed() throws Exception {
        //given
        final Observable<Boolean> pings = Ping.checkConstantly("buggy.com");

        //when
        Observable<Boolean> windowPings = pings
                .window(3, 1)
                .flatMapSingle(p -> p.all(BooleanUtils::isFalse))
                .take(12);

        //then
        windowPings.test()
                .assertValueAt(0, false)   // true, true, true
                .assertValueAt(1, false)   // true, true, false
                .assertValueAt(2, false)   // true, false, true
                .assertValueAt(3, false)   // false, true, false
                .assertValueAt(4, false)   // true, false, false
                .assertValueAt(5, false)   // false, false, true
                .assertValueAt(6, false)   // false, true, true
                .assertValueAt(7, false)   // true, true, false
                .assertValueAt(8, false)   // true, false, false
                .assertValueAt(9, true)  // false, false, false
                .assertValueAt(10, false)   // false, false, true  <-- overflow, starting all over again
                .assertValueAt(11, false)   // false, true, true
                .assertComplete();
    }

}
