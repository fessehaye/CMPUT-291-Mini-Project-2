import java.io.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import java.util.*;

import com.sleepycat.db.*;

public class DataQuery {
	/*
	 * DataQuery asks a user to input a query. Then the string is broken up using the format class
	 * and then searches for the indexes for each individual query based of the first keyword and
	 * then returns a list of lists to be intersected. Afterward using the intersected list/set 
	 * print the text version of the xmls related to the indexes. 
	 */
	public static void main(String[] args) {

		Scanner in = new Scanner(System.in);
		String command = "";
		try {
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setSortedDuplicates(true);// allows us to have duplicate
			// keys in a database
			dbConfig.setType(DatabaseType.BTREE);
			dbConfig.setAllowCreate(false);// the option that allows to create a
			// db
			// file, if it doesn't exist

			DatabaseConfig dbConfig2 = new DatabaseConfig();
			dbConfig2.setSortedDuplicates(true);// allows us to have duplicate
			// keys in a database
			dbConfig2.setType(DatabaseType.HASH);
			dbConfig2.setAllowCreate(false);// the option that allows to create
			// a db
			// file, if it doesn't exist

			Database ad_db = new Database("ad.idx", null, dbConfig2);
			Database terms_db = new Database("te.idx", null, dbConfig);
			Database date_db = new Database("da.idx", null, dbConfig);
			Database price_db = new Database("pr.idx", null, dbConfig);

			System.out.println("Type <quit> to leave");
			//Start the loop to ask user input
			while (!command.equals("<quit>")) {

				System.out.print("Query:");
				//case-insenstive
				command = in.nextLine().toLowerCase();
				//format string to ind queries
				List<List<String>> All_keywords = format.format_string(command);
				List<List<String>> Combined = new ArrayList<List<String>>();
				//goes through each individual 
				for (List<String> keyword : All_keywords) {
					String Procedure = keyword.get(0);
					List<String> Key_of_Query = new ArrayList<String>();
					//checks if need term search
					if (Procedure.startsWith("b-")
							|| Procedure.startsWith("t-")) {
						//partial or full?
						if (Procedure.endsWith("%")) {
							keyword.set(0,keyword.get(0).substring(0,keyword.get(0).length() - 1));
							Key_of_Query = term_check_partial(keyword, terms_db);
						} else {
							Key_of_Query = term_check(keyword, terms_db);
						}
					} 
					//check for price search
					else if (Procedure.equals("price")) {

						Key_of_Query = prices.Start(keyword, price_db);

					}
					//check for date search
					else if (Procedure.equals("since")
							|| Procedure.equals("until")) {
						
						Key_of_Query = pdates.Start(keyword, date_db);
						
					}
					//title and body search
					else {
						//partial or full?
						if (Procedure.endsWith("%")) {
							String TEMP = keyword.get(0);
							keyword.set(0, "b-" + keyword.get(0));
							keyword.set(0,keyword.get(0).substring(0,keyword.get(0).length() - 1));
							Key_of_Query = term_check_partial(keyword, terms_db);
							keyword.set(0, "t-" + TEMP);
							keyword.set(0,keyword.get(0).substring(0,keyword.get(0).length() - 1));
							Key_of_Query.addAll(term_check_partial(keyword,
									terms_db));
						} else {
							String TEMP = keyword.get(0);
							keyword.set(0, "b-" + keyword.get(0));
							Key_of_Query = term_check(keyword, terms_db);
							keyword.set(0, "t-" + TEMP);
							Key_of_Query.addAll(term_check(keyword, terms_db));
						}
					}
					Combined.add(Key_of_Query);

				}

				if(!Combined.isEmpty()){

					List<String> Final_Answer = format.intersect_list(Combined);
					Set<String> set = new HashSet<String>(Final_Answer);

					ad_to_xml(set, ad_db);}

			}

			ad_db.close();
			terms_db.close();
			date_db.close();
			price_db.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/*
	 * Takes list of indexes. Searches for the xml ad thats relates to it in ads.txt
	 * Then uses the SAX parser to get each individual element fo the xml and prints all the info
	 * of each individual ad
	 */
	private static void ad_to_xml(Set<String> set, Database ad_db) {
		// TODO Auto-generated method stub
		Cursor std_cursor;
		List<String> XMLFORM = new ArrayList<String>();
		//Trying to find all related xmls for the indexes
		try {
			std_cursor = ad_db.openCursor(null, null);
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry data = new DatabaseEntry();

			if (std_cursor.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				String aKey = new String(key.getData());
				String aData = new String(data.getData());
				if (set.contains(aKey)) {
					if (!XMLFORM.contains(aData)) {
						XMLFORM.add(aData);
					}
				}

				key = new DatabaseEntry();
				data = new DatabaseEntry();
				while (std_cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

					aKey = new String(key.getData());
					aData = new String(data.getData());

					if (set.contains(aKey)) {
						if (!XMLFORM.contains(aData)) {
							XMLFORM.add(aData);
						}
					}

					key = new DatabaseEntry();
					data = new DatabaseEntry();
				}
			} else {
				System.out.println("The database is empty\n");
			}
			std_cursor.close();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Count:" + XMLFORM.size());
		for (String line : XMLFORM) {

			try {
				//Create temporary xml file to store xml string
				BufferedWriter temp = new BufferedWriter(new FileWriter("temp.xml"));
				temp.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				temp.newLine();
				temp.write(line);
				temp.close();
				//Sax procedure simliar to Phase 1
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
				Document doc = docBuilder.parse (new File("temp.xml"));


				doc.getDocumentElement().normalize();
				//Get Ad Element
				NodeList adslist = doc.getElementsByTagName("ad");
				Element adElement = (Element) adslist.item(0);
				//Get Title
				NodeList TitleList = adElement.getElementsByTagName("title");
				Element TitleElement = (Element) TitleList.item(0);

				NodeList titleFNList = TitleElement.getChildNodes();
				String Title = ((Node) titleFNList.item(0)).getNodeValue()
						.trim();
				// Get Body
				NodeList BodyList = adElement.getElementsByTagName("body");
				Element BodyElement = (Element) BodyList.item(0);

				NodeList BodyFNList = BodyElement.getChildNodes();
				// Obtain ID String from xml
				String Body = ((Node) BodyFNList.item(0)).getNodeValue().trim();
				
				// Get price
				NodeList priceList = adElement.getElementsByTagName("price");

				Element priceElement = (Element) priceList.item(0);

				NodeList textpriceList = priceElement.getChildNodes();
				// Check If Price exists for Ad
				String price = "0";
				if (textpriceList.item(0) != null) {
					// Obtain body String from xml
					price = ((Node) textpriceList.item(0)).getNodeValue()
							.trim();
				}
				// Get pdate
				NodeList pdateList = adElement.getElementsByTagName("pdate");

				Element pdateElement = (Element) pdateList.item(0);

				NodeList textpdateList = pdateElement.getChildNodes();
				// Store Pdate as String
				String pdate = ((Node) textpdateList.item(0)).getNodeValue()
						.trim();

				System.out.println("Title:" + Title);
				System.out.println("Body:" + Body);
				System.out.println("Price:" + price + " Date:" + pdate + "\n");
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	/*
	 * Function for full term searches any query involving body and title
	 */
	private static List<String> term_check(List<String> Terms, Database terms_db) {
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		String keyword = Terms.get(0);
		List<String> Key_of_Query = new ArrayList<String>();

		Cursor std_cursor;
		try {
			std_cursor = terms_db.openCursor(null, null);
			if (std_cursor.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				String aKey = new String(key.getData());
				String aData = new String(data.getData());
				//if the keyword equals aKey then it is added to the Key_ofQueries
				if (aKey.equals(keyword)) {
					Key_of_Query.add(aData);
				}

				key = new DatabaseEntry();
				data = new DatabaseEntry();
				while (std_cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
					//aKey is the term and aData is the key
					aKey = new String(key.getData());
					aData = new String(data.getData());

					if (aKey.equals(keyword)) {
						Key_of_Query.add(aData);
					}

					key = new DatabaseEntry();
					data = new DatabaseEntry();
				}
			} else {
				//exception handling for the database
				System.out.println("The database is empty\n");
			}
			//close the cursor
			std_cursor.close();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return the Key_of_query
		return Key_of_Query;

	}
	/*
	 * Function for partial searches any query involving % works similar to term_check
	 */
	private static List<String> term_check_partial(List<String> Terms, Database terms_db) {
		//set up the key and data
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		String keyword = Terms.get(0);
		List<String> Key_of_Query = new ArrayList<String>();

		Cursor std_cursor;
		//try for the db
		try {
			std_cursor = terms_db.openCursor(null, null);
			if (std_cursor.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				String aKey = new String(key.getData());
				String aData = new String(data.getData());
				//check to see if the keyword is in aKey(prefix)
				if (aKey.contains(keyword)) {
					Key_of_Query.add(aData);
				}
				//make new key and data
				key = new DatabaseEntry();
				data = new DatabaseEntry();
				while (std_cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

					aKey = new String(key.getData());
					aData = new String(data.getData());
					//check to see if keyword is in aKey and add it to the 
					if (aKey.contains(keyword)) {
						Key_of_Query.add(aData);
					}
					//get new key and data
					key = new DatabaseEntry();
					data = new DatabaseEntry();
				}
			} else {
				//some exception handling 
				System.out.println("The database is empty\n");
			}
			//close the cursor
			std_cursor.close();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return the Key_of_Query
		return Key_of_Query;

	}
}
