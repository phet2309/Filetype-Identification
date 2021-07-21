//imports for File
import java.io.File;
import java.io.FileWriter;

//imports for xml.parsers
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

//imports for doc
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

//imports for Json
import org.json.simple.JSONObject;


public class TikaParser {
	public static void main(String[] args) {
		try {
			// Opening the dource(tika.xml) file as Document
			File inputFile = new File("Database/tika.xml");
			DocumentBuilderFactory tikadb = DocumentBuilderFactory.newInstance();
			DocumentBuilder tikadBuilder = tikadb.newDocumentBuilder();
			Document tikaDoc = tikadBuilder.parse(inputFile);
			tikaDoc.getDocumentElement().normalize();
			
			// Selecting all mime-type as the reference tag in which we can get the rest values with extensions
			NodeList tikaList = tikaDoc.getElementsByTagName("mime-type");

			// JSON array to store tika data in json file.
			JSONObject finalObject = new JSONObject();
			
			// Iterate over each mime-type in xml file
			for (int current = 0; current < tikaList.getLength(); current++) {
				Node currentNode = tikaList.item(current);   
				
				// if mime-type is an element
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) currentNode;
					
					// Inside the mime-type get all the glob elements
					NodeList globList = element.getElementsByTagName("glob");
					String extension = "";
					String tikaComment = "";
					String tikaType="";
					
					if (globList != null) {
						int N = globList.getLength();
						
						// Iterate over each Glob element to get the extension
						for(int i=0; i<N; i++) {
							Node globNode = globList.item(i);
							extension = ((Element) globNode).getAttribute("pattern");
							tikaType = element.getAttribute("type");
							
							// Handling _comment element if it has multiple values or null values
							NodeList commentList = element.getElementsByTagName("_comment");
							if (commentList!=null) {
								Node commentNode = commentList.item(0);
								if (commentNode!=null)
									tikaComment = commentNode.getTextContent();
							}

							
							// JSON values (fileObjectDetails) to put in the file
							JSONObject fileObjectDetails = new JSONObject();
							fileObjectDetails.put("tikaType", tikaType);
							fileObjectDetails.put("tikaComment", tikaComment);
							
							// removing * from extension
							extension = extension.replaceAll("\\*", "");
							
							// JSON object for the key (extension) with fileObjectDetails as values
							JSONObject fileObject = new JSONObject();
							fileObject.put(extension, fileObjectDetails);
							
							// Adding each row to the json file.
							finalObject.putAll(fileObject);
						}
					}
				}
			}
			
			// Writing to JSON file all of the key values
	        try (FileWriter file = new FileWriter("Database/tikaSource.json")) {
	            file.write(finalObject.toJSONString());
	            file.flush();
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}