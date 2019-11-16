package com.pttrn42.rx.samples;

import io.reactivex.Observable;

import java.util.stream.Stream;

public class ObservableStreamInterop {

    public static <T> Observable<T> fromStream(Stream<T> stream) {

        return Observable.create(emitter -> {
            try {
                stream.forEach(emitter::onNext);
                emitter.onComplete();
            } catch (Throwable t) {
                emitter.onError(t);
            }
        });
    }

}
