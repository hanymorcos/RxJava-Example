package rxjava;

public class Helper {

	public static String threadName () { return Thread.currentThread().getName();}
	
	public static void sleep(int ms) {
		    try { Thread.sleep(ms); } catch(Exception ex) {}
		  }
}
