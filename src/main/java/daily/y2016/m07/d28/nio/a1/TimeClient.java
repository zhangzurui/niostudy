package daily.y2016.m07.d28.nio.a1;

public class TimeClient {

	public static void main(String[] args) {
		new Thread(new TimeClientHandler("127.0.0.1", 8000), "TimeClient-001").start();
	}
}
