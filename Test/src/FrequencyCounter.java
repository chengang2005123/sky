import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrequencyCounter implements Runnable {
	private static int fileName = 1;
	Hashtable<String, Integer> hashtable1 = new Hashtable<String, Integer>();
	private String lock;

	public FrequencyCounter(String lock) {
		this.lock = lock;
	}

	public void run() {
		synchronized (lock) {
			try {
				BufferedReader in = new BufferedReader(new FileReader("Users/"
						+ fileName + ".txt"));
				String s;
				while ((s = in.readLine()) != null) {
					String[] ss = s.split(" ");
					for (String key : ss) {
						if (hashtable1.containsKey(key))
							hashtable1.put(key, hashtable1.get(key) + 1);
						else
							hashtable1.put(key, 1);
					}
				}

				for (String key : hashtable1.keySet()) {
					System.out.println(key + " " + hashtable1.get(key));
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {
		ExecutorService pool = Executors.newFixedThreadPool(1);
		String lock = new String("lock");
		for (int i = 0; i < 2; i++) {
			pool.submit(new FrequencyCounter(lock));
		}
		pool.shutdown();
	}

}
