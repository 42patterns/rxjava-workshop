package com.pttrn42.java8;

import com.pttrn42.java8.util.BaseFutureTest;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Imagine that you’re a top singer, and fans ask day and night for your upcoming single.
 *
 * To get some relief, you promise to send it to them when it’s published.
 * You give your fans a list. They can fill in their email addresses, so that
 * when the song becomes available, all subscribed parties instantly receive it.
 * And even if something goes very wrong, say, a fire in the studio, so that you
 * can’t publish the song, they will still be notified.
 *
 * This is a real-life analogy for things we often have in programming:
 *
 *  -  A “producing code” that does something and takes time.
 *     For instance, a code that loads the data over a network. That’s a “singer”.
 *  -  A “consuming code” that wants the result of the “producing code” once it’s ready.
 *     Many functions may need that result. These are the “fans”.
 *  -  A promise is an object that links the “producing code” and the “consuming code” together.
 *     In terms of our analogy: this is the “subscription list”.
 *     The “producing code” takes whatever time it needs to produce the promised result,
 *     and the “promise” makes that result available to all of the subscribed code when it’s ready.
 */
@Ignore
public class J27_PromisesTest extends BaseFutureTest {

	private static final Logger log = LoggerFactory.getLogger(J27_PromisesTest.class);

	@Test(timeout = 10000)
	public void wait_for_manually_created_file_on_desktop() throws Exception {
		final CompletableFuture<Path> future = newFilePromise();

		log.debug("New file found on desktop {}", future.get());
	}

	private CompletableFuture<Path> newFilePromise() {
		final CompletableFuture<Path> promise = new CompletableFuture<>();
		waitForNewFileInSeparateThread(promise);
		return promise;
	}

	private void waitForNewFileInSeparateThread(final CompletableFuture<Path> promise) {
		new Thread("FileSystemWatcher") {
			@Override
			public void run() {
				try {
					promise.complete(waitForNewFileOnDesktop());
				} catch (Exception e) {
					promise.completeExceptionally(e);
				}
			}
		}.start();
	}

	private Path waitForNewFileOnDesktop() throws IOException, InterruptedException {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		final WatchKey key = Paths.
				get(System.getProperty("user.home"), "Desktop").
				register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
		while (!Thread.currentThread().isInterrupted()) {
			final List<WatchEvent<?>> events = key.pollEvents();
			if (!events.isEmpty()) {
				return (Path) events.get(0).context();
			}
			key.reset();
		}
		throw new InterruptedException();
	}

}

