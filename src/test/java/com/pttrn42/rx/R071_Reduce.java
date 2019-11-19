package com.pttrn42.rx;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

@Ignore
public class R071_Reduce {

    private static final Logger log = LoggerFactory.getLogger(R071_Reduce.class);

    @Test
    public void reduceIsEmptyOnEmptyStream() throws Exception {
        //given
        final Observable<Integer> nums = Observable.empty();

        //when
        final Maybe<Integer> sum = nums.reduce((l, r) -> {
            log.info("l = {}, r = {}", l, r);
            return l + r;
        });

        //then
        sum.test().assertComplete();
    }

    @Test
    public void reduceReturnsInitialValue() throws Exception {
        //given
        final Observable<Integer> nums = Observable.just(42);

        //when
        final Maybe<Integer> sum = nums.reduce((l, r) -> {
            log.info("l = {}, r = {}", l, r);
            return l + r;
        });

        //then
        sum.test()
                .assertValue(42)
                .assertComplete();
    }

    @Test
    public void sumUsingReduce() throws Exception {
        //given
        final Observable<Integer> nums = Observable.range(1, 10);

        //when
        //TODO: calculate real reduce (not the first value)
        final Maybe<Integer> sum = nums.firstElement();

        //then
        sum
                .test()
                .assertValue(1+2+3+4+5+6+7+8+9+10)
                .assertComplete();
    }

    /**
     * TODO Computer factorial (n!) using {@link Observable#reduce(BiFunction)}
     */
    @Test
    public void factorialUsingReduce() throws Exception {
        //given
        final Observable<Integer> nums = Observable.range(1, 10);

        //when
        final Maybe<Integer> factorial = nums.lastElement();

        //then
        factorial.test()
                .assertValue(1*2*3*4*5*6*7*8*9*10)
                .assertComplete();
    }

    public static final String FACTORIAL_100 = "93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000";

    @Test
    public void reduceWithCustomAccumulator() throws Exception {
        //given
        final Observable<Integer> nums = Observable.range(1, 100);

        //when
        final Single<BigInteger> factorial = nums.reduce(BigInteger.ONE, (state, x) -> state);

        //then
        factorial.test()
                .assertValue(new BigInteger(FACTORIAL_100))
                .assertComplete();
    }


}
