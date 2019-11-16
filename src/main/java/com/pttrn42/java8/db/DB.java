package com.pttrn42.java8.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextLong;

public class DB {
    private static final Logger log = LoggerFactory.getLogger(DB.class);

    public String apply(Query query) {
        try {
            long l = nextLong(2, 4);
            log.debug("Sleeping: {} s", l);
            TimeUnit.SECONDS.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.format("%s_%s", randomAlphabetic(nextInt(4, 12)), query.getId());
    }
}
