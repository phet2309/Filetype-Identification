import java.io.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//This is a program for Web Scraping from fileproinfo.com
//Input: None
//Output: Stores the data in Database/fileproinfo.json

public class FileProInfo {
	
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
			String url = "https://fileproinfo.com/file-types-list/" + c;	
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
		        e.printStackTrace();
		    }
			
		    // Fetching all the names of the extensions from the table
			String[] elements = doc.select("table#example tbody a b").text().toLowerCase().split(" ");
			exs.addAll(Arrays.asList(elements));
		}
		
		// JSON array to store fileproinfo data in json file.
		JSONObject fileObject = new JSONObject();
		
		// Fetching data for each extension from its respective page 
		for(String i: exs) {
			String tp;
			if(i.equals(".bin")) {
				tp = "https://fileproinfo.com/file-type/" + "bin=9033";
			} else {
				tp = "https://fileproinfo.com/file-type/" + i.substring(1);
			}
			String url = tp;
			
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
		        e.printStackTrace();
		    }
			
		    // Web scraping through various tags, id and class of html
			Elements elements = doc.select("div.col-lg-9 div.col-lg-9 td");
			Element temp = elements.select("td > b").first();
			if(temp!=null) {
				String ext = temp.text();
				String developer = elements.select("td > a").first() != null ? elements.select("td > a").first().text() : "Not Found";
				String mime = elements.select("td:contains(Mime Type)").first().nextElementSibling() != null ? elements.select("td:contains(Mime Type)").first().nextElementSibling().text() : "Not Found";
				String cat = elements.select("td:contains(Category)").first().nextElementSibling() != null ? elements.select("td:contains(Category)").first().nextElementSibling().text() : "Not Found";
				String format = elements.select("td:contains(Format)").first().nextElementSibling() != null ? elements.select("td:contains(Format)").first().nextElementSibling().text() : "Not Found";
				String p_language = pl.getOrDefault(i, "Not Found");
				
				JSONObject fileDetails = new JSONObject();
				fileDetails.put("Developer", developer);
				fileDetails.put("Mime Type", mime);
				fileDetails.put("Category", cat);
				fileDetails.put("Format", format);
				fileDetails.put("Programming Language", p_language);
				
				// Adding all the details to the map
				fileObject.put(ext, fileDetails);
			}
			
		    
		    System.out.println(i);
		}
		
		// Adding data into Json file 
		try (FileWriter file = new FileWriter("Database/fileproinfo.json")) {
		    file.write(fileObject.toJSONString());
		    file.flush();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
}
