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

	//######################################
	@Test
	public void test1Simple() {
	        Observable<String> observer = Observable.fromArray("one","two", "three", "four").delay(1,TimeUnit.SECONDS); // provides datea
	       observer.subscribe((s)->System.out.println(s));
	     //  sleep(1000); // Do you need the sleep?
	}
	

	//######################################
	@Test
	public void test2Buffered() {
	    Observable<String> observer = Observable.fromArray("one","two", "three", "four"); 
	       observer.buffer(4).blockingSubscribe((s)->System.out.println(s));
	       // What will happen if you change 4 to 2 
	}
	
	
	//######################################


	@Test
	public void test3Feed()
	{
		// Created an observer from Observable 
		List<String> house = Arrays.asList( "lamp","tv","chair","microwave");
		
				Observable<String> feed= Observable.create(subscriber -> {
	    	 for (int i =0; i < house.size();i++)
	    	 {
	    		    String item  = house.get(i);
	    		  	subscriber.onNext(item);
	    		  	
	    	        Helper.sleep(10);
	    	      }
	    });

				
	 Observable<String> mover1 =	feed.flatMap((item) -> {System.out.println("mover1 boxed " + item);
	 													Observable<String> o = Observable.just(item);
	 													return o;});
       
	Disposable mover2 = mover1.subscribe((item)->{System.out.println("mover2 put " + item + " in truck " );},
										 (err)->{System.err.println("Item fell  " + err);},
										 ()->{System.out.println("No more items");});
       
	}
	
	//######################################
	

	@Test
	public void test4Threads()
	{
		 // Create an Observable from a Callable
		 Observable.fromCallable(()->{System.out.println("observer =>" + Helper.threadName()); 
		 							  return 1;})
         .observeOn(Schedulers.newThread())  
         .map((arg0)->{System.out.println("operator =>" +Helper.threadName() );
         				return String.valueOf(arg0);})
         .observeOn(Schedulers.newThread())  
         .subscribe((arg0)->System.out.println("subscribe =>" + Helper.threadName()));
	}
	

	//######################################

	@Test
	public void test5Threads()
	{
		 Observable.fromCallable(()->{System.out.println("observer =>" + Helper.threadName()); 
		 							  return 1;})
         .observeOn(Schedulers.computation())  
         .map((arg0)->{System.out.println("operator =>" +Helper.threadName() );
         				return String.valueOf(arg0);})
         .observeOn(Schedulers.computation())  
         .subscribe((arg0)->System.out.println("subscribe =>" + Helper.threadName()));
	}
	
	//#################
	@Test
	public void test6stocks()
	{
		String[] symArray = {"AAPL","NFLX","Googl","TSLA","AMZN","MSFT"};
		Observable<Observable<String>> mapped = Observable.fromArray(symArray)
 			.map((s)-> YahooFinance.getPriceOrig(s));

		ConnectableObservable<String> merged = Observable.merge(mapped).delay(1,TimeUnit.SECONDS).observeOn(Schedulers.newThread()).publish();

		//ConnectableObservable<String> merged = Observable.merge(mapped).replay();

		 merged.subscribe((a)->{System.out.println("s1 =>" + a);});

		 merged.connect();
		 merged.subscribe((a)->{System.out.println("s2 =>" + a);});
		 Helper.sleep(10000);
	}
}
