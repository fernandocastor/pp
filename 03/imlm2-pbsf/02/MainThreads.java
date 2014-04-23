import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

public class MainThreads extends Thread {

	static HashMap<String, Number[]> map;
	List<String> file;
	List<String> filePath;

	public MainThreads(List<String> path) {
		map = new HashMap<String, Number[]>();
		file = new ArrayList<String>();
		this.filePath = path;
	}

	private static String getPackage(String fullQualifiedName) {
		int lastIndex = fullQualifiedName.lastIndexOf("\\");
		if (lastIndex == -1) lastIndex = 0;
		return fullQualifiedName.substring(0,lastIndex);
	}

	private static void addToMap(String[] line) {
		String key = getPackage(line[0]);
		if(map.containsKey(key)) {
			Number[] metrics = map.get(key);
			Number[] values = stringArrayToNumberArray(line);
			if(metrics.length < values.length) {
				Number[] temp = values;
				values = metrics;
				metrics = temp;
			}
			for(int i = 0; i < values.length; i++) {
				metrics[i] = metrics[i].intValue() + values[i].intValue();
			}
			map.put(key, metrics);
		} else {
			map.put(key,stringArrayToNumberArray(line));
		}
	}

	static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.FRANCE);
	private static Number[] stringArrayToNumberArray(String[] array) {
		Number[] output = new Number[array.length-1];
		for(int i = 1; i < array.length; i++) {
			try {
				if(array[i] == null || array[i].isEmpty()) {
					output[i-1] = 0;
				} else
					output[i-1] = FORMAT.parse(array[i].trim()).intValue();
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		return output;
	}

	public static List<String> readCSV(List<String> filesPaths) {
		List<String> output = new ArrayList<String>();
		for(String path : filesPaths) {
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new FileReader(path));
				bufferedReader.readLine(); // skip header
				String originalLine = bufferedReader.readLine();
				while (originalLine != null) {
					originalLine = originalLine.trim();
					output.add(originalLine);
					originalLine = bufferedReader.readLine();
				}
				bufferedReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}

	public static List<String> mergeFiles(List<String> file1, List<String> file2) {
		file1.addAll(file2);
		return file1;
	}

	public static void matchPackages(List<String> file) {
		for(String line : file) {
			addToMap(line.split(";"));
		}
	}

	public static void writeToFile(HashMap<String, Number[]> map, String output) {
		if(map != null) {
			FileWriter fw;
			try {
				fw = new FileWriter(output);
				for(String key : map.keySet()) {
					fw.write(key);
					Number[] metrics = map.get(key);
					String line = "";
					for(int i = 0; i < metrics.length; i++) {
						line += ";"+metrics[i];
					}
					fw.write(line);
					fw.write('\n');
				}
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		file = readCSV(filePath);
	}

	public static void main(String[] args) {
		if(args != null) {
			int numOfThreads = Integer.parseInt(args[0]);
			int numOfFiles = args.length-1;
			if(numOfThreads > numOfFiles) {
				throw new RuntimeException("Number of threads must be <= number of files");
			}
			MainThreads[] threads = new MainThreads[numOfThreads];
			int filesPerThread = (int) Math.ceil((double)numOfFiles/(double)numOfThreads);
			int counter = 0;
			List<String> files = new ArrayList<String>();
			int nextThread = 0;
			for(int i = 1; i < args.length; i++) {
				try {
					files.add(args[i]);
					counter++;
					if(i+1 == args.length || counter == filesPerThread) {
						List<String> temp = files;
						MainThreads main = new MainThreads(temp);
						main.start();
						threads[nextThread++] = main;
						counter = 0;
						files = new ArrayList<String>();
					}

				} catch (Exception e) {
					System.err.println("Unable to parse: " + args[i]);
					e.printStackTrace(System.err);
				}
			}
			List<String> outputFile = new ArrayList<String>();
			for(MainThreads thread : threads) {
				if(thread != null) {
					try {
						thread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					MainThreads.mergeFiles(outputFile, thread.file);
				}
			}
			MainThreads.matchPackages(outputFile);

			MainThreads.writeToFile(MainThreads.map,"output.csv");
		}

	}

}
