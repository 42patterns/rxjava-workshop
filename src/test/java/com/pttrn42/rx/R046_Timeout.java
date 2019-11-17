package com.pttrn42.rx;

import com.pttrn42.rx.samples.CacheServer;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.junit.Assert.assertThat;

@Ignore
public class R046_Timeout {

    private static final Logger log = LoggerFactory.getLogger(R046_Timeout.class);

    /**
     * TODO Add fallback to {@link Observable#timeout(long, TimeUnit)}
     * It should return -1 when timeout of 100ms occurs.
     */
    @Test
    public void timeout() throws Exception {
        //given
        final Maybe<Long> withoutTimeout = Maybe.timer(200, TimeUnit.MILLISECONDS);

        //when
        final Maybe<Long> withFallback = withoutTimeout
                //TODO: set timeout
                .onErrorReturn(t -> -1L);

        //then
        withFallback.test()
                .awaitDone(150, TimeUnit.MILLISECONDS)
                .assertValue(-1L)
                .assertComplete();
    }

    /**
     * TODO Add timeout of 80ms to {@link CacheServer#findBy(int)} method.
     * <p>
     * When timeout occurs, {@link Observable#retry()}. However, fail if retry takes more than 5 seconds.
     * </p>
     */
    @Test
    public void timeoutAndRetries() throws Exception {
        //given
        CacheServer cacheServer = new CacheServer("foo", ofMillis(100), 0);

        //when
        final Maybe<String> withTimeouts = cacheServer
                .findBy(1)
                //TODO Operators here
                ;

        //then
        withTimeouts.blockingGet();
    }

    /**
     * TODO Ask two {@link CacheServer}s for the same key 1.
     * <p>
     * Ask <code>first</code> server in the beginning.
     * If it doesn't respond within 200 ms, continue waiting, but ask <code>second</code> server.
     * Second server is much faster, but fails often. If it fails, swallow the exception and wait
     * for the first server anyway.
     * However, if the second server doesn't fail, you'll get the response faster.
     * Make sure to run the test a few times to make sure it works on both branches.
     * </p>
     *
     * @see Maybe#cache()
     * @see Maybe#onErrorResumeNext(Function)
     */
    @Test
    public void speculativeExecution() throws Exception {
        //given
        CacheServer slow = slowButReliable();
        CacheServer fast = fastButFlaky();

        //when
        final Maybe<String> response = null;

        //then
        assertThat(response.blockingGet(),
                CoreMatchers.startsWith("Value-1-from"));
    }

    private CacheServer slowButReliable() {
        return new CacheServer("foo", ofSeconds(1), 0);
    }

    private CacheServer fastButFlaky() {
        return new CacheServer("bar", ofMillis(100), 0.5);
    }

}
