import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class Data{

	protected Object data;
		
	private static double matrixCost[][];
	
	public Data(File f)
	{
	}

	public static void readInputFile(String filename, boolean verbose)
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
					
					if(verbose)System.out.println("Filling matrix[ "+ i + " ][ " + j + " ] = " + currentEdgeAttribute );
					
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
					if(verbose)System.out.println("\nCurrently parsing graph values for city number " + i + ".");
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
		  
	  
	  		System.out.println("Parsing completed sucessfully, data available in Data.matrixCost.");
		}

	public static double[][] getMatrixCost() {
		return matrixCost;
	}

	public static void setMatrixCost(double matrix_cost[][]) {
		matrixCost = matrix_cost;
	}
	

}