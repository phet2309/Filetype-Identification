// This is a program for Web Scraping from fileinfo.com
// Input: None
// Output: Stores the data in Database/fileinfo.json

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FileInfo {
	
	public static void main(String[] args) {
		// Parsing programming language data into HasMap from Database/pl.json
		HashMap<String,String> pl = new HashMap<String,String>();
		JSONParser jsonParserS1 = new JSONParser();
		try(FileReader reader = new FileReader("Database/pl.json"))	{
			Object obj = jsonParserS1.parse(reader);
			JSONObject o = (JSONObject)obj;
			for(Object s: o.keySet()) {
				pl.put((String)s,(String)o.get(s));
			}
		}
		catch(ParseException | IOException e) {
			e.printStackTrace();
		}
		
		// Scraping the list of all the extensions starting from a to z
		List<String> exs = new ArrayList<String>();		
		for(char c = 'a'; c <= 'z'; ++c) {
			String url = "https://fileinfo.com/filetypes/" + c;
			// Making connection to the page and getting html file
			Document doc = null;
		    try {
		        Connection con = Jsoup.connect(url);
		        Connection.Response response = con.execute();
		        if (response.statusCode() == 200) {
		            doc = con.get();
		        } else {
		            System.out.println(response.statusCode());
		            continue;
		        }
		    } catch (IOException e) {
		        continue;
		    }
			
		    // Fetching all the names of the extensions from the table
			String[] elements = doc.select("table.list.sortable.filetypes tr td.extcol a").text().toLowerCase().split(" ");
			exs.addAll(Arrays.asList(elements));
		}
		
		// Fetching data for each extension from its respective page 
		// Json Object to store fileinfo data		
		JSONObject fileObject = new JSONObject();
		for(String i: exs) {
			System.out.println(i);
			String url = "https://fileinfo.com/extension/" + i.substring(1);
			Document extension_doc = null;
			// Making connection to the page and getting html file
		    try {
		        Connection con = Jsoup.connect(url);
		        Connection.Response response = con.execute();
		        if (response.statusCode() == 200) {
		            extension_doc = con.get();
		        } else {
		            System.out.println(response.statusCode());
		            continue;
		        }
		    } catch (IOException e) {
		        continue;
		    }
			
		    // Web scraping through various tags, id and class of html
			Elements programs = extension_doc.select("div.program") != null ? extension_doc.select("div.program") : null;
			HashSet<String> set = new HashSet<String>();
			String tp = "";
			for (Element i1 : programs) {
				if(!set.contains(i1.text())) {
					set.add(i1.text());
					tp = tp + ", " + i1.text();
				}
			}
			
			String runnable = tp.length()>=2 ? tp.substring(2) : "";
			String cat = extension_doc.select("tr td ~ td a").first() != null ? extension_doc.select("tr td ~ td a").first().text() : "Not Found";
			String format = extension_doc.select("tr td ~ td a.formatButton").first() != null ? extension_doc.select("tr td ~ td a.formatButton").first().text() : "Not Found";
			String desc = extension_doc.select("div.infobox p").first() !=null ? extension_doc.select("div.infobox p").first().text() : "Not Found";
			String p_language = pl.getOrDefault(i, "Not Found");
			
			
			JSONObject fileDetails = new JSONObject();
			fileDetails.put("Program Support", runnable);
			fileDetails.put("Description", desc);
			fileDetails.put("Category", cat);
			fileDetails.put("Format", format);
			fileDetails.put("Programming Language", p_language);
			
			// Adding all the details to the map
			fileObject.put(i, fileDetails);
		}
		
		// Adding data into Json file 
		try (FileWriter file = new FileWriter("Database/fileinfo.json")) {
		    file.write(fileObject.toJSONString());
		    file.flush();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
