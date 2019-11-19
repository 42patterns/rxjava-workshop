package com.pttrn42.rx;

import com.pttrn42.rx.samples.Tuple;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class R072_Scan {

    private static final Logger log = LoggerFactory.getLogger(R072_Scan.class);

    @Test
    public void sumUsingScan() throws Exception {
        //given
        final Observable<Integer> nums = Observable.range(1, 10);

        //when
        final Observable<Integer> sum = nums.scan((l, r) -> {
            log.info("l = {}, r = {}", l, r);
            return l+r;
        });

        //then
        sum.test()
                .assertValues(1,
                        1 + 2,
                        3 + 3,
                        6 + 4,
                        10 + 5,
                        15 + 6,
                        21 + 7,
                        28 + 8,
                        36 + 9,
                        45 + 10)
                .assertComplete();
    }

    /**
     * TODO Compute running average from the beginning to current item.
     * E.g.: (10) / 1, (10, 20) / 2, (10, 20, 6) / 3, (10, 20, 6, 4) / 4, and so on.
     * Hint: use {@link Observable#scan(Object, BiFunction)} with an accumulator of type {@link Tuple}
     * where one value is sum so far and the other is total number of items (needed to compute average).
     */
    @Test
    public void computeAverageUsingScan() throws Exception {
        //given
        final Observable<Integer> nums = Observable.just(10, 20, 6, 4, 20, 24);

        //when
        final Observable<Double> avg = nums.scan(
                Tuple.of(0, 0),
                (tuple, i) -> Tuple.of(tuple.getKey()+i, tuple.getValue()+1)
        ).skip(1).map(t -> (double)(t.getKey() / t.getValue()));

        //then
        avg.test()
                .assertValues(10.0, 15.0, 12.0, 10.0, 12.0, 14.0)
                .assertComplete();
    }
}
