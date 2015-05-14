import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CountDown {

	 public static void main(String args[]) {

	        final CountDownLatch latch = new CountDownLatch(3);
	        Service service1 = new Service("1000nomes.txt", latch);
	        Service service2 = new Service("11188nomes.txt", latch);
	        Service service3 = new Service("2000nomes.txt", latch);

	        service1.start();
	        service2.start();
	        service3.start();
	        
	        try {
	            latch.await(); // main thread is waiting on CountDownLatch to finish
	            
	            Integer total = service1.getCountWords() + service2.getCountWords() + service3.getCountWords();
	            System.out.println("Palavras: "+ total);
	            
	            
	            System.out.println("All services are up, Application is starting now");
	        } catch (InterruptedException ie) {
	            ie.printStackTrace();
	        }
	    }
	}

	class Service extends Thread {
	    private final String path;
	    private final CountDownLatch latch;
	    private Integer countWords;
	    
	    public Service(String path, CountDownLatch latch) {
	        this.path = path;
	        this.latch = latch;
	        this.countWords = 0;
	    }

	    @Override
	    public void run() {
	        try {
				for (String line : Files.readAllLines(Paths.get(path))) {
					if(line.contains("palavra"))
						countWords++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				latch.countDown();
			}
	    }
	    
	    public Integer getCountWords() {
			return countWords;
		}
	}