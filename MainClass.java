//imports for JSON
import org.json.simple.parser.*;
import org.json.simple.JSONObject;

//imports for Threading
import java.util.concurrent.Executors;

//imports for File
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//imports of util
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

//imports for CSV Writer
import com.opencsv.CSVWriter;

//Source 1
class FileProInfoDatabase
{
	String Category;
	String Format;
	String Developer;
	String Mime_Type;
	String pl;
}

//Source 2
class FileInfoDatabase
{
	String Description;
	String Category;
	String Format;
	String pl;
	String ps;
}

//Source 3
class TikaDatabase
{
	String Category;
	String Type;
}

public class MainClass {
	//Declaration of Maps for Source 1, Source 2, Source 3
    static ConcurrentHashMap<String , TikaDatabase> tikaMap; 
	static ConcurrentHashMap<String , FileInfoDatabase> fileInfoMap;
	static ConcurrentHashMap<String , FileProInfoDatabase> fileProInfoMap;
	static List<String[]> outputListTika;
	static List<String[]> outputListFileProInfo;
	static List<String[]> outputListFileInfo;
	
    public static void main(String[] args) throws IOException, InterruptedException {

		//Input: Reading input.csv file, and storing all the distinct extensions in a List  
        String inputFile = "Input/input2.csv";
        BufferedReader inputReader = new BufferedReader(new FileReader(inputFile));
		List<String> list = new ArrayList<>();	
		String fileExtension;
		while((fileExtension = inputReader.readLine()) != null) {
            int indexOfDot = fileExtension.lastIndexOf(".");
            String current = fileExtension.substring(indexOfDot).toLowerCase();
            if(!list.contains(current)) {
                list.add(current);
		    }	
        }
		inputReader.close();

		//Initialization of Maps, outputlist
		tikaMap = new ConcurrentHashMap<>();
		fileInfoMap = new ConcurrentHashMap<>();
        fileProInfoMap = new ConcurrentHashMap<>();
		outputListTika = new ArrayList<>();
		outputListFileProInfo = new ArrayList<>();
		outputListFileInfo = new ArrayList<>();

        //Reading Source 1, Source 2, Source 3 using a threadPool of 3
		ExecutorService readExec = Executors.newFixedThreadPool(3);
        Runnable tikaThread = new lookUpTable("Database/tikaSource.json");
		Runnable fileInfoThread = new lookUpTable("Database/fileinfo.json");
        Runnable fileProinfoThread = new lookUpTable("Database/fileproinfo.json");
        readExec.submit(tikaThread);
		readExec.submit(fileInfoThread);
        readExec.submit(fileProinfoThread);
        readExec.shutdownNow();
		//Checking whether all the threads in readExec are done
        if (!readExec.awaitTermination(60, TimeUnit.SECONDS)) {
            readExec.shutdownNow();
            if (!readExec.awaitTermination(60, TimeUnit.SECONDS))
                System.err.println("readExec did not terminate");
        }

		//CSV writer is used for writing outputList records to Output/TikaSourceOutput.csv
		String filePathTika = "Output/TikaSourceOutput.csv";
		File fileTika = new File(filePathTika);
		FileWriter outputfileTika = new FileWriter(fileTika);
		CSVWriter writerTika = new CSVWriter(outputfileTika);
		outputListTika.add(new String[] { "Extension", "Category", "Format"});

		//CSV writer is used for writing outputList records to Output/FileInfoSourceOutput.csv
		String filePathFileInfo = "Output/FileInfoSourceOutput.csv";
		File fileFileInfo = new File(filePathFileInfo);
		FileWriter outputfileFileInfo = new FileWriter(fileFileInfo);
		CSVWriter writerFileInfo = new CSVWriter(outputfileFileInfo);
		outputListFileInfo.add(new String[] {"Extension", "Description", "Category", "Format", "Programming Language", "Program Support"});
		
		//CSV writer is used for writing outputList records to Output/FileProSourceOutput.csv
		String filePathFileProInfo = "Output/FileProSourceOutput.csv";
		File fileFileProInfo = new File(filePathFileProInfo);
		FileWriter outputfileFileProInfo = new FileWriter(fileFileProInfo);
		CSVWriter writerFileProInfo = new CSVWriter(outputfileFileProInfo);
		outputListFileProInfo.add(new String[] {"Extension", "Category", "Format", "Developer", "Mime Type", "Programming Language"});
		
		//Output: Output generation is done using thread Pool of 4 and by dividing the `inputFile` in equal chunks
        int n = 4;
		int n_list = list.size();
        ExecutorService searchExec = Executors.newFixedThreadPool(n);
		int minItemsPerThread = n_list / n;
        int maxItemsPerThread = minItemsPerThread + 1;
        int threadsWithMaxItems = n_list - n * minItemsPerThread;
        int start = 0;
		for(int i=0; i<n; i++) {
			int itemsCount = (i < threadsWithMaxItems ? maxItemsPerThread : minItemsPerThread);
            int end = start + itemsCount;
			Runnable searchThread = new searchExtension(list.subList(start, end));
			searchExec.submit(searchThread);
			start = end;
		}
		searchExec.shutdown();
		//Checking whether all the threads in searchExec are done
		if (!searchExec.awaitTermination(60, TimeUnit.SECONDS)) {
            searchExec.shutdownNow();
            if (!searchExec.awaitTermination(60, TimeUnit.SECONDS))
                System.err.println("searchExec did not terminate");
        }
		
		//After the recorded in outputListTika list it is appended to TikaSourceOutput.csv
		writerTika.writeAll(outputListTika);
        writerTika.close();

		//After the recorded in outputListFileProInfo list it is appended to FileInfoSourceOutput.csv
		writerFileProInfo.writeAll(outputListFileProInfo);
		writerFileProInfo.close();

		//After the recorded in outputListFileInfo list it is appended to FileProSourceOutput.csv
		writerFileInfo.writeAll(outputListFileInfo);
		writerFileInfo.close();

    }
}

//Runnable for Storing the data from the dataSource into the maps
class lookUpTable implements Runnable {
    
	//Declaration source file name
	private String FileName;

	//Constructor and initialization of FileName
	public lookUpTable(String FileName) {
		this.FileName = FileName;
	}

	@Override
	public void run() {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader(FileName)){
			Object obj = jsonParser.parse(reader);
			JSONObject fileTypeList = (JSONObject) obj;
			if(FileName.equals("Database/tikaSource.json")) {
				tikaMethod(fileTypeList);
			} else if(FileName.equals("Database/fileinfo.json")) {
				fileInfoMethod(fileTypeList);
			} else if(FileName.equals("Database/fileproinfo.json")) {
				fileproinfoMethod(fileTypeList);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Method for iterating through each object in the given data source(Tika) and storing it in respective maps
	private void tikaMethod(JSONObject fileTypeList) {
		for(Object s: fileTypeList.keySet()) {
			JSONObject ts = (JSONObject) fileTypeList.get(s);
			String key = s.toString().toLowerCase();
		    
			TikaDatabase current = new TikaDatabase();
		    current.Category = (String) ts.get("tikaComment");
			current.Type = (String) ts.get("tikaType");
			
            MainClass.tikaMap.putIfAbsent(key.toLowerCase(), current);
		}
	}

	//Method for iterating through each object in the given data source(fileProInfo) and storing it in respective maps
	private void fileproinfoMethod(JSONObject fileTypeList) {
		for(Object s: fileTypeList.keySet()) {
		    JSONObject fpi = (JSONObject) fileTypeList.get(s);
			String key = s.toString().toLowerCase();
		    
			FileProInfoDatabase current = new FileProInfoDatabase();
		    current.Category = (String) fpi.get("Category");
            current.Format = (String) fpi.get("Format");
		    current.Developer = (String) fpi.get("Developer");
		    current.Mime_Type = (String) fpi.get("Mime Type");
		    current.pl = (String) fpi.get("Programming Language");
		    
            MainClass.fileProInfoMap.putIfAbsent(key.toLowerCase(), current);
		}
	}

	//Method for iterating through each object in the given data source(fileProInfo) and storing it in respective maps
	private void fileInfoMethod(JSONObject fileTypeList) {
		for(Object s: fileTypeList.keySet()) {
		    JSONObject fi = (JSONObject) fileTypeList.get(s);
			String key = s.toString().toLowerCase();
		    
			FileInfoDatabase current = new FileInfoDatabase();
		    current.Category = (String) fi.get("Category");
            current.Format = (String) fi.get("Format");
		    current.Description = (String) fi.get("Description");
		    current.ps = (String) fi.get("Program Support");
		    current.pl = (String) fi.get("Programming Language");
		    
            MainClass.fileInfoMap.putIfAbsent(key.toLowerCase(), current);
		}
	}
}

//Runnable for Searching the input extenstion from the stored data in the maps
class searchExtension implements Runnable {

	//Declaration of the input List which has all the input extenstions given by the user
	private List<String> list;

	//Constructor 
	public searchExtension(List<String> list) {
		this.list = list;
	}

	@Override
	public void run() {
		//Searching for the extenstion and storing them in their corresponding outputlists
        for(String searchExe: list) {
			if (MainClass.tikaMap.containsKey(searchExe))
			{
				TikaDatabase td = MainClass.tikaMap.get(searchExe);
				MainClass.outputListTika.add(new String[]{searchExe, td.Type, td.Category});
			}
			if (MainClass.fileInfoMap.containsKey(searchExe))
			{
				FileInfoDatabase td = MainClass.fileInfoMap.get(searchExe);
				MainClass.outputListFileInfo.add(new String[]{searchExe, td.Description, td.Category, td.Format, td.pl, td.ps});
			}
			if (MainClass.fileProInfoMap.containsKey(searchExe))
			{
				FileProInfoDatabase td = MainClass.fileProInfoMap.get(searchExe);
				MainClass.outputListFileProInfo.add(new String[]{searchExe, td.Category, td.Format, td.Developer, td.Mime_Type, td.pl});
			}
			
		}	
	}
}
