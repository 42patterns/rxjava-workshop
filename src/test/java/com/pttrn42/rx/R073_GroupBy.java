package com.pttrn42.rx;

import com.pttrn42.rx.domains.Domain;
import com.pttrn42.rx.domains.Domains;
import com.pttrn42.rx.samples.Tuple;
import com.pttrn42.rx.user.LoremIpsum;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import static com.pttrn42.rx.samples.Tuple.of;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

@Ignore
public class R073_GroupBy {

    private static final Logger log = LoggerFactory.getLogger(R073_GroupBy.class);

    @Test
    public void groupWordsByLength() throws Exception {
        //given
        final Observable<GroupedObservable<Integer, String>> wordsByLength = LoremIpsum
                .wordStream()
                .groupBy(s -> ThreadLocalRandom.current().nextInt(50)); //TODO: replace random with real grouping function

        wordsByLength
                .subscribe(words -> words.toList()
                        .subscribe(list -> log.info("Words of length {} (size={}): {}", words.getKey(), list.size(), list)));

        //when
        final Observable<Long> numOfWords = wordsByLength
                .flatMapSingle(Observable::count);

        final Observable<Integer> wordLengths = wordsByLength
                .map(GroupedObservable::getKey);

        //then
        final List<Integer> uniqueWordLengths = wordLengths.toSortedList().blockingGet();
        assertThat(uniqueWordLengths, contains(1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12));

        final List<Long> numOfWordsSet = numOfWords.toList().blockingGet();
        assertThat(numOfWordsSet, contains(1L, 8L, 15L, 24L, 25L, 14L, 10L, 6L, 9L, 3L, 2L));

    }

    /**
     * TODO Count number of words of each length.
     *
     * Use {@link Tuple<Integer, Long>} to represent number of words in each count group
     * Use {@link Observable#groupBy(io.reactivex.functions.Function)}
     * Use {@link GroupedObservable#count()} for number of grouped elements
     */
    @Test
    public void countAndNumberOfWords() throws Exception {
        final Observable<GroupedObservable<Integer, String>> wordsByLength = LoremIpsum
                .wordStream()
                .groupBy(String::length);

        //when

        final Observable<Tuple<Integer, Long>> lenToCount = Observable.empty();

        //then
        final List<Tuple<Integer, Long>> pairs = lenToCount
                .toList().blockingGet();

        assertThat(pairs, hasItems(
                of(1, 1L),
                of(2, 8L),
                of(3, 15L),
                of(4, 24L),
                of(5, 25L),
                of(6, 14L),
                of(7, 10L),
                of(8, 6L),
                of(9, 9L),
                of(11, 3L),
                of(12, 2L)
        ));
    }

    /**
     * TODO Count total number of linking root domains ({@link Domain#getLinkingRootDomains()}) to each TLD ({@link Domain#getTld()}
     *
     * Sort from most to least number of linking root domains.
     * @see Domain#getTld() - group domains by TLD
     * @see Domain#getLinkingRootDomains()
     *
     * For each group, cound the number of linkingRootDomains
     * @see Observable#reduce(BiFunction) ()
     *
     * Map domain with the number of linkedRootDomains
     * @see Observable#map(io.reactivex.functions.Function)
     *
     * Sort the result
     * @see Observable#sorted(Comparator)
     * @see Comparator#comparing(Function)
     * @see Comparator#reversed()
     */
    @Test
    public void countDomainsInTld() throws Exception {
        //given
        final Observable<Domain> domains = Domains.all()
                .subscribeOn(Schedulers.io());

        //when
        final Observable<Tuple<String, Long>> tldToTotalLinkingRootDomains = Observable.empty();

        //then
        assertThat(tldToTotalLinkingRootDomains.toList().blockingGet(), equalTo(Arrays.asList(
                of("com", 87760745L),
                of("org", 9041936L),
                of("gov", 2331526L),
                of("uk", 1922036L),
                of("jp", 1869929L),
                of("net", 1627997L),
                of("edu", 1490589L),
                of("be", 1076391L),
                of("de", 1056153L),
                of("cn", 1051999L),
                of("gl", 995299L),
                of("ru", 917921L),
                of("ly", 738098L),
                of("co", 533094L),
                of("eu", 454617L),
                of("me", 424173L),
                of("fr", 404814L),
                of("ca", 233912L),
                of("es", 229458L),
                of("nl", 204242L),
                of("to", 181817L),
                of("la", 177604L),
                of("pl", 171095L),
                of("br", 158381L),
                of("it", 150409L),
                of("us", 142895L),
                of("au", 139500L),
                of("io", 124859L),
                of("tv", 105500L),
                of("int", 104755L),
                of("ch", 90364L),
                of("cz", 87073L),
                of("in", 55601L),
                of("se", 55181L),
                of("info", 37701L),
                of("no", 35631L))));
    }

}
