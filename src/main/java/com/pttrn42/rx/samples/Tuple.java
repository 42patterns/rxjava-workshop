package com.pttrn42.rx.samples;

import java.util.AbstractMap;

public class Tuple<K, V> extends AbstractMap.SimpleEntry<K, V> {

    public Tuple(K key, V value) {
        super(key, value);
    }

    public static <K, V> Tuple<K, V> of(K key, V value) {
        return new Tuple(key, value);
    }
}
