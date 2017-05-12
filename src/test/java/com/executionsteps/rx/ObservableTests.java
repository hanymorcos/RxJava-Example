package com.executionsteps.rx;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;
import rxjava.Helper;
import rxjava.YahooFinance;

public class ObservableTests {

	// ######################################
	@Test
	public void testSimple() {
		Observable<String> lc = Observable.fromArray("one", "two", "three", "four").delay(1, TimeUnit.SECONDS); // provides
																												// delay
		lc.subscribe((s) -> System.out.println(s));
		// sleep(1000); // Do you need the sleep?
	}

	// ######################################
	@Test
	public void testBuffered() {
		Observable<String> lc = Observable.fromArray("one", "two", "three", "four");
		lc.buffer(4).subscribe((s) -> System.out.println(s));
		// What will happen if you change 4 to 2
	}

	// ######################################

	@Test
	public void testGrouping() {
		Observable.range(1, 10).groupBy(n -> n % 2 == 0)
				.flatMap(grp -> grp.groupBy(n -> n > 5).flatMap(grp2 -> grp2.toList())).subscribe(System.out::println);
	}

	// ######################################

	@Test
	public void testFeed() {
		// Created an observer from Observable
		List<String> house = Arrays.asList("lamp", "tv", "chair", "microwave");

		Observable<String> feed = Observable.create(subscriber -> {
			for (int i = 0; i < house.size(); i++) {
				String item = house.get(i);
				subscriber.onNext(item);

				Helper.sleep(1000);
			}
		});

		Observable<String> mover1 = feed.flatMap((item) -> {
			System.out.println("mover1 boxed " + item);
			Observable<String> o = Observable.just(item);
			return o;
		});

		Disposable mover2 = mover1.subscribe(
		(item) -> {
			System.out.println("mover2 put " + item + " in truck ");
		}, 
		(err) -> {
			System.err.println("Item fell  " + err);
		}, 
		() -> {
			System.out.println("No more items");
		});

	}


	// ######################################

	@Test
	public void testThreads() {
		Observable.fromCallable(
		() -> {
			System.out.println("observer =>" + Helper.threadName());
			return 1;
		}).observeOn(Schedulers.computation())
		.map((arg0) -> {
			System.out.println("operator =>" + Helper.threadName());
			return String.valueOf(arg0);
		}).observeOn(Schedulers.computation())
		.subscribe((arg0) -> System.out.println("subscribe =>" + Helper.threadName()));
	}

	// #################
	@Test
	public void teststocks() {
		String[] symArray = { "AAPL", "NFLX", "Googl", "TSLA", "AMZN", "MSFT" };
		Observable<Observable<String>> mapped = Observable.fromArray(symArray).map((s) -> YahooFinance.getPriceOrig(s));

		ConnectableObservable<String> merged = Observable.merge(mapped).delay(1, TimeUnit.SECONDS)
				.observeOn(Schedulers.newThread()).publish();

		// ConnectableObservable<String> merged = Observable.merge(mapped).replay();

		merged.subscribe((a) -> {
			System.out.println("s1 =>" + a);
		});

		merged.connect();
		merged.subscribe((a) -> {
			System.out.println("s2 =>" + a);
		});
		Helper.sleep(10000);
	}
}
