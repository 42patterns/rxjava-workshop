package com.pttrn42.rx;

import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.Subject;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@Ignore
public class R091_Subject {

    private static final Logger log = LoggerFactory.getLogger(R091_Subject.class);

    @Test
    public void multipleSubscribers() throws Exception {
        //given
        final Subject<Long> proc = AsyncSubject.create();

        //when
        pushSomeEvents(proc, 0, 3);

        //then
        proc.subscribe(
                        x -> log.info("A: Got {}", x),
                        e -> log.error("A: Error", e),
                        () -> log.info("A: Complete")
        );
        proc.subscribe(
                        x -> log.info("B: Got {}", x),
                        e -> log.error("B: Error", e),
                        () -> log.info("B: Complete")
        );

        pushSomeEvents(proc, 10, 3);
        proc.onComplete();
        TimeUnit.SECONDS.sleep(1);
    }

    static void pushSomeEvents(Subject<Long> proc, int start, int count) {
        LongStream
                .range(start, start + count)
                .peek(x -> log.info("Pushing {}", x))
                .forEach(proc::onNext);
    }
}
