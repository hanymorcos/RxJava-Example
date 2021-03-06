package com.executionsteps.rx;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;
import rxjava.Helper;
import rxjava.YahooFinance;

public class ObservableTests {

	 /**
	  *  NoFluffJustStuff topic of Reactive Programming
	  *  example https://github.com/hanymorcos/RxJava-Example
	  *  An observer subscribes(implements interface) to an Observable
	  *  Observable emits <T> java Generic T -- T can be a list 
	  *  OnNext is called to emit T item 
	  *  delay method is used to delay the emit of items by a TimeUnit
	  */
	
	@Test
	public void test1Simple() {
		Observable<String> lc = Observable.fromArray("one", "two", "three", "four")
										  .delay(1, TimeUnit.SECONDS); // provides
		lc.subscribe((s) -> System.out.println(s)); //Consumer<String> nxt = (s) -> System.out.println(s);
		 Helper.sleep(1000); 
	}

	
	
	
	
	
	
	/**
	 * buffer allows item to be queued and returned in a list of the buffer size 
	 * 
	 */
	@Test
	public void test2Buffered() {
		Observable<String> lc = Observable.fromArray("one", "two", "three", "four");
		lc.buffer(2).subscribe((s) -> System.out.println(s));
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 
	 *  flatMap maps an item to a group of items 
	 *  Group by number type (even/odd) and bigger or less than five
	 */

	@Test
	public void test3Grouping() {
		Observable.range(1, 10).groupBy(n -> n % 2 == 0)
				.flatMap(grp -> grp.groupBy(n -> n > 5)
				.flatMap(grp2 -> grp2.toList()))
				.subscribe(System.out::println);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	/*  Observable.create more granular control of emitting events
	 *  JavaRX is capturing processes and procedures developers 
	 *  Moving out of a house box and put in a truck
	 *  onComplete to complete emission
	 */

	@Test
	public void test4Feed() {
		// Created an observer from Observable
		List<String> house = Arrays.asList("lamp", "tv", "chair", "microwave");

		Observable<String> feed = Observable.create(emitter -> 
						{
							for (int i = 0; i < house.size(); i++) 
							{
								String item = house.get(i);
								emitter.onNext(item);	
							}
							 emitter.onComplete();
						});
		
		Observable<String> mover1 = feed.delay(1,TimeUnit.SECONDS)
										.flatMap((item) -> 
											{
												System.out.println("mover1 boxed " + item);
												Observable<String> o = Observable.just(item);
												return o;
											});

		Disposable mover2 = mover1.subscribe(
		(item) -> 
				{
					System.out.println("mover2 put " + item + " in truck ");
				}, 
		(err) -> 
				{
					System.err.println("Item fell  " + err);
				}, 
		() -> 
				{
					System.out.println("No more items");
				});


		 Helper.sleep(2000);
	}


	
	
	
	
	
	
	
	
	/*
	 * Observer.fromCallable create an Observable from a function
	 * Schedulers.computation or Schedulers.io
	 */

	@Test
	public void test5Threads() {
		Observable.fromCallable(
		() -> 
				{
					System.out.println("observer =>" + Helper.threadName());
					return 1;
				})
		.observeOn(Schedulers.computation())
		.map((arg0) -> 
					{
						System.out.println("operator =>" + Helper.threadName());
						return String.valueOf(arg0);
					})
		.observeOn(Schedulers.computation())
		.subscribe((arg0) -> System.out.println("subscribe =>" + Helper.threadName()));
	}

	
	
	
	
	
	
	
	/*
	 * Merging Observable into one stream. For every lookup, we return an Observable and then we merge into one
	 * ConnectableObservable Hot and Cold Subscription
	 * What does replay do? 
	 */
	@Test
	public void test6stocks() {
		String[] symArray = { "AAPL", "NFLX", "Googl", "TSLA", "AMZN", "MSFT" };
		Observable<Observable<String>> mapped = Observable.fromArray(symArray).map((s) -> YahooFinance.getPriceOrig(s));

		ConnectableObservable<String> merged = Observable.merge(mapped).delay(1, TimeUnit.SECONDS)
				.observeOn(Schedulers.newThread()).publish();

		// ConnectableObservable<String> merged = Observable.merge(mapped).replay();

		merged.subscribe((a) -> 
				{
					System.out.println("s1 =>" + a);
				});

		merged.connect();
		merged.subscribe((a) -> 
			{
				System.out.println("s2 =>" + a);
			});
		Helper.sleep(10000);
	}
}
