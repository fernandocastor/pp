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

public class Main {
	
	HashMap<String, Number[]> map;
	List<String> file;
	
	public Main() {
		map = new HashMap<String, Number[]>();
		file = new ArrayList<String>();
	}
	
	private String getPackage(String fullQualifiedName) {
		int lastIndex = fullQualifiedName.lastIndexOf("\\");
		if (lastIndex == -1) lastIndex = 0;
		return fullQualifiedName.substring(0,lastIndex);
	}
	
	private void addToMap(String[] line) {
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
	private Number[] stringArrayToNumberArray(String[] array) {
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

	private void readCSV(String path) {
		BufferedReader bufferedReader = null;
        try {
			bufferedReader = new BufferedReader(new FileReader(path));
			bufferedReader.readLine(); // skip header
			String originalLine = bufferedReader.readLine();
			while (originalLine != null) {
				originalLine = originalLine.trim();
				file.add(originalLine);
				originalLine = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void matchPackages() {
		for(String line : file) {
			addToMap(line.split(";"));
		}
	}
	
	private void writeToFile(String output) {
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

	public static void main(String[] args) {
		Main main = new Main();
		if(args != null) {
			for(String path : args) {
				try {
					main.readCSV(path);
//					main.readCSV("sample1.csv");
				} catch (Exception e) {
					System.err.println("Unable to parse: " + path);
					e.printStackTrace(System.err);
				}
			}
			main.matchPackages();
			main.writeToFile("output.csv");
		}

	}

}
