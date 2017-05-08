package rxjava;

import java.net.URL;

import io.reactivex.Observable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class YahooFinance {

	  final static String yahooURL = "https://ichart.finance.yahoo.com/table.csv?s=";
	  
  public static Observable<String> getPriceOrig(final String ticker) {
	  return Observable.create((subscriber)->{
    	
	  final String[] dataItems = getURLText(ticker).split(",");
      String price = ticker +  " " + dataItems[dataItems.length - 1]; 

	  System.out.println("getPriceOrig " + ticker);
      subscriber.onNext( price);
	  });
  }

   private static String getURLText(String ticker) throws IOException
   {
	   final URL url = new URL(yahooURL + ticker);
	   BufferedReader reader = new BufferedReader(new InputStreamReader( url.openStream()));
	   return reader.lines().skip(1).limit(1).findFirst().get();
   }
}
