import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MultiFrequencyCounter implements Runnable{
	
	Hashtable<String, Integer> hashtable1 = new Hashtable<String, Integer>();
	private File [] files;//文件数组
	public static int [] length;//信号量，辅助控制并发   0.代表没有线程执行过， 1.代表有线程执行过
	private int count = 0; //计数器 用来 控制打印，

	public MultiFrequencyCounter(File [] files) {
		this.files = files;
	}

	public void run() {
		
		multFrequency();
		if(count == files.length) {
			printWord();
		} 
	}
	
	/**
	 * 同步控制
	 */
	public void multFrequency() {
		try {
			for(int i=0;i<files.length;i++) {
				if(length[i]==0) {
					synchronized (files[i]) {
						if(length[i]==0){
						    length[i] = 1;
						    try {
							files[i].wait(1000);
							} catch (Exception e) {
								e.printStackTrace();
							}
						    execute(files[i]);
						} else {
							files[i].notifyAll();
						}
					 } 
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取某个文件的内容
	 * @param f 文件
	 * @throws IOException
	 */
	public void execute(File f) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(f.getAbsolutePath()));
		String s;
		while ((s = in.readLine()) != null) {
			String[] ss = s.split(" ");
			for (String key : ss) {
				synchronized (hashtable1) {
				if (hashtable1.containsKey(key))
					hashtable1.put(key, hashtable1.get(key) + 1);
				else
					hashtable1.put(key, 1);
				}
			}
		}
		in.close();
		count = count + 1;
	}
	
	/**
	 * 字符打印
	 */
	public synchronized void printWord() {
		try {
			for (String key : hashtable1.keySet()) {
				System.out.println(key + " " + hashtable1.get(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	public static void main(String args[]) throws IOException {
		ExecutorService pool = Executors.newFixedThreadPool(2);
		File [] files = new File("C:\\Ericsson\\maworkspace\\Test\\Users").listFiles();
		MultiFrequencyCounter.length = new int [files.length];
		MultiFrequencyCounter multiFrequencyCounter = new MultiFrequencyCounter(files);
		for (int i = 0; i < 2; i++) {
			pool.submit(multiFrequencyCounter);
		}
		pool.shutdown();
	}
}
