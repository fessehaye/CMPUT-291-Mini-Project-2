import java.io.*;
import java.util.Scanner;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class parser {
	/*
	 * Parse an XML file using event-based sequential access parser API
	 * developed by the XML-DEV mailing list for XML documents(SAX) to read
	 * information about the ad array and write info based of that xml to the
	 * four text files: 1.terms.txt- 2.pdates.txt- 3.prices.txt- 4.ads.txt-
	 */
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.print("Type name of xml file:");
		String xml_file = "";
		xml_file = in.nextLine();

		// Create Writer text files to store info
		BufferedWriter terms = null;
		BufferedWriter pdates = null;
		BufferedWriter prices = null;
		BufferedWriter ads = null;
		try {
			terms = new BufferedWriter(new FileWriter("terms.txt"));
			pdates = new BufferedWriter(new FileWriter("pdates.txt"));

			prices = new BufferedWriter(new FileWriter("prices.txt"));

			ads = new BufferedWriter(new FileWriter("ads.txt"));

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Inspired from examples from :
		// http://www.developerfusion.com/code/2064/a-simple-way-to-read-an-xml-file-in-java/
		BufferedReader br;
		// Read XML File using DocumentBuilder
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(xml_file));
			br = new BufferedReader(new FileReader(xml_file));
			br.readLine();
			br.readLine();
			doc.getDocumentElement().normalize();
			NodeList adslist = doc.getElementsByTagName("ad");

			// Goes through each Individual Ad and parses information

			for (int i = 0; i < adslist.getLength(); i++) {
				Node ad = adslist.item(i);
				if (ad.getNodeType() == Node.ELEMENT_NODE) {

					Element ADElement = (Element) ad;
					// Read ID And Store As String for future Reference
					// ------------------------------------------------------------------------
					NodeList firstNameList = ADElement
							.getElementsByTagName("id");
					Element firstNameElement = (Element) firstNameList.item(0);

					NodeList textFNList = firstNameElement.getChildNodes();
					// Obtain ID String from xml
					String id = ((Node) textFNList.item(0)).getNodeValue()
							.trim();

					// Read title And Store As String for future Reference
					// -------------------------------------------------------------------------
					NodeList lastNameList = ADElement
							.getElementsByTagName("title");
					Element lastNameElement = (Element) lastNameList.item(0);

					NodeList textLNList = lastNameElement.getChildNodes();
					// Obtain title String from xml
					String title = ((Node) textLNList.item(0)).getNodeValue()
							.trim();

					// term is a consecutive sequence of alphanumeric or
					// underscore '_' characters
					title = title.replaceAll("\\W", " ");
					// Splits the String to individual terms
					String Answer[] = title.split(" ");

					// Add Individual terms to terms.txt
					for (int j = 0; j < Answer.length; j++) {
						if (Answer[j].length() > 2) {
							terms.write("t-" + Answer[j].toLowerCase() + ":"
									+ id);
							terms.newLine();
						}
					}
					// Read body And Store As String for future Reference
					// -----------------------------------------------------------------------
					NodeList bodyList = ADElement.getElementsByTagName("body");
					Element bodyElement = (Element) bodyList.item(0);

					NodeList textbodyList = bodyElement.getChildNodes();
					// Obtain body String from xml
					String body = ((Node) textbodyList.item(0)).getNodeValue()
							.trim();

					// term is a consecutive sequence of alphanumeric or
					// underscore '_' characters
					body = body.replaceAll("\\W", " ");
					// Splits the String to individual terms
					String AnswerBody[] = body.split(" ");

					// Add Individual terms to terms.txt
					for (int j = 0; j < AnswerBody.length; j++) {
						if (AnswerBody[j].length() > 2) {
							terms.write("b-" + AnswerBody[j].toLowerCase()
									+ ":" + id);
							terms.newLine();
						}
					}

					// Read price And Store As String for future Reference
					// ------------------------------------------------------------------------
					NodeList priceList = ADElement
							.getElementsByTagName("price");

					Element priceElement = (Element) priceList.item(0);

					NodeList textpriceList = priceElement.getChildNodes();
					// Check If Price exists for Ad
					String price = "";
					if (textpriceList.item(0) != null) {
						// Obtain body String from xml
						price = ((Node) textpriceList.item(0)).getNodeValue()
								.trim();

						prices.write(price + ":" + id);
						prices.newLine();
					}

					// Read pdate And Store As String for future Reference
					// ------------------------------------------------------------------------
					NodeList pdateList = ADElement
							.getElementsByTagName("pdate");

					Element pdateElement = (Element) pdateList.item(0);

					NodeList textpdateList = pdateElement.getChildNodes();
					// Store Pdate as String
					String pdate = ((Node) textpdateList.item(0))
							.getNodeValue().trim();

					// Write to pdate.txt
					pdates.write(pdate + ":" + id);
					pdates.newLine();

					// --------------------------------------------------------------------------
					// Write the XML form of the ad in the ad.txt
					ads.write(id + ":" + br.readLine());
					ads.newLine();

				}// end of if clause

			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("file does not exist");
			System.exit(0);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			ads.close();
			prices.close();
			pdates.close();
			terms.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
