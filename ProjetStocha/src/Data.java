import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Data{

	protected Object data;
		
	private static double matrixCost[][];
	
	public Data(File f)
	{
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Starting the parser main...");
		read_input_file("./XMLData/att48.xml");
	}

	public static void read_input_file(String filename)
	{

	  try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

			int i = -1;
			int j = 0;
			
			
			boolean isGraphElement = false;
			boolean isVertexElement = false;
			boolean isDescription = false;

			boolean isEdgeElement = false;

			public void startElement(String uri, String localName,String qName, 
		                Attributes attributes) throws SAXException {

				if (qName.equalsIgnoreCase("travellingSalesmanProblemInstance")) {
					System.out.println("This is a TSP XML dataset! Commencing parsing operations...");
				}

				if (qName.equalsIgnoreCase("description")) {
					isDescription = true;
				}
				
				if (qName.equalsIgnoreCase("graph")) {
					isGraphElement = true;
				}

				if (qName.equalsIgnoreCase("vertex")) {
					isVertexElement = true;
				}

				if (qName.equalsIgnoreCase("edge")) {
					isEdgeElement = true;
					double currentEdgeAttribute = Double.parseDouble(attributes.getValue(0));
					matrixCost[i][j] = currentEdgeAttribute;
					System.out.println("Filling matrix[ "+ i + " ][ " + j + " ] = " + currentEdgeAttribute );
					
				}

			}

			public void endElement(String uri, String localName,
				String qName) throws SAXException {

				if (qName.equalsIgnoreCase("vertex")) {
					j = 0;
				}
				//System.out.println("End Element :" + qName);

			}

			public void characters(char ch[], int start, int length) throws SAXException {

				//init the cost matrix
				if (isDescription) {
					
					String numberString =  new String(ch, start, length);
					numberString = numberString.replaceAll("\\D+","");
					int nbCity = Integer.parseInt(numberString);
					System.out.println("Number of city in this dataset = " + nbCity);
					
					matrixCost = new double[nbCity][nbCity];
					isDescription = false;
				}
				
				
				if (isGraphElement) {
					//System.out.println("isGraphe? : " + new String(ch, start, length));
					isGraphElement = false;
				}

				if (isVertexElement) {
					//System.out.println("isVertex? : " + new String(ch, start, length));
					isVertexElement = false;
					i++;
					System.out.println("\nCurrently parsing graph values for city number " + i + ".");
				}

				if (isEdgeElement) {			
					//System.out.println("Edge number corresponding : " + Integer.valueOf(new String(ch, start, length)));
					isEdgeElement = false;
					j++;
				}

			}

		    };

		       saxParser.parse(filename, handler);
		 
		     } catch (Exception e) {
		       e.printStackTrace();
		     }
		  
		}

	public double[][] getMatrixCost() {
		return matrixCost;
	}

	public void setMatrixCost(double matrix_cost[][]) {
		this.matrixCost = matrix_cost;
	}
	

}