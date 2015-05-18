import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MultiFrequencyCounter implements Runnable{
	
	Hashtable<String, Integer> hashtable1 = new Hashtable<String, Integer>();
	private File [] files;//�ļ�����
	public static int [] length;//�ź������������Ʋ���   0.����û���߳�ִ�й��� 1.�������߳�ִ�й�
	private int count = 0; //������ ���� ���ƴ�ӡ��

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
	 * ͬ������
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
	 * ��ȡĳ���ļ�������
	 * @param f �ļ�
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
	 * �ַ���ӡ
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
