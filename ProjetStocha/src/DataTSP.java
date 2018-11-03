import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class DataTSP extends Data{

	private static int nbCity;
	private double matrixCost[][];
	private static String filename;
	private static boolean verbose;
	
	public DataTSP()
	{

	}
	

	public void readInputFile(String file, boolean verb)
	{

	  try {

		  	//to avoid "Cannot refer to a non-final variable inside an inner class defined in a different method" errors, we copy the parameters into the application
		  	filename = file;
		  	verbose = verb;
		  	
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
					System.out.println("\nDataTSP: This is a TSP XML dataset! Commencing parsing operations...");
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
					if (i != j)
					{
						//System.out.println("i, j, current = " + i + ", " + j + "; " + currentEdgeAttribute);
						matrixCost[i][j] = currentEdgeAttribute;
					}
					else
					{
						if(currentEdgeAttribute == 9999.0) //the case of br17.xml, where a city is linked to itself with a weight of 9999 in the XML file
						{
							matrixCost[i][j] = -1;
						}
						else
						{
							//if(verbose)System.out.println("Debug: A city is linked to itself with a weight of -1.");
							matrixCost[i][j] = -1;
							matrixCost[i][j+1] = currentEdgeAttribute;
							j++;
						}

					}
					
					//if(verbose)System.out.println("DataTSP: Filling matrix[ "+ i + " ][ " + j + " ] = " + matrixCost[i][j] );
					//System.out.println("j = " + j);
				}

			}

			public void endElement(String uri, String localName,
				String qName) throws SAXException {
				if (qName.equalsIgnoreCase("vertex")) {

					j = 0;
				}

			}

			public void characters(char ch[], int start, int length) throws SAXException {

				//init the cost matrix
				if (isDescription) {
					
					String numberString =  new String(ch, start, length);
					numberString = numberString.replaceAll("\\D+",""); //replacing letters with ""
					if(numberString.equals(""))
					{
						System.out.println("####DataTSP: DEBUG Number of cities unknown! Now attempting to retrieve it in another way.");
						DataTSP.nbCity = Integer.parseInt(filename.replaceAll("\\D+",""));
					}
					else
					{
						System.out.println("Debug: number city from description : " + numberString);
						DataTSP.nbCity = Integer.parseInt(numberString);
					}
					System.out.println("DataTSP: Number of city in this dataset = " + nbCity + ".");
					
					matrixCost = new double[nbCity][nbCity];
					//last value is -1
					matrixCost[nbCity-1][nbCity-1] = -1;
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
					//if(verbose)System.out.println("\nCurrently parsing graph values for city number " + i + ".");
				}

				if (isEdgeElement) {			
					//System.out.println("Edge number corresponding : " + Integer.valueOf(new String(ch, start, length)));					
					j++;
					isEdgeElement = false;
				}
			}

		  };

		       saxParser.parse(filename, handler);
		 
		     } catch (Exception e) {
		       e.printStackTrace();
		     }
	  		System.out.println("DataTSP: Parsing completed sucessfully, data available in Data.matrixCost.");
		}


	public static List<City> parserTSP(String FILENAME){
		
		 List<City> listCity = new ArrayList<City>();
	
		FileReader f = null;
		try {
			f = new FileReader(FILENAME);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String currentLine;
		BufferedReader buff = new BufferedReader(f);
		int i = 0;
		
		try{
			for(i = 0; i < 4; i++)
				buff.readLine();
			
			//System.out.println("IIIIINNNt  = " + Integer.parseInt(buff.readLine().substring(11)));
	
			
			buff.readLine(); buff.readLine();
			
			i =0;
			while(true){
				currentLine = buff.readLine();
				if(currentLine.charAt(0) == 'E' && currentLine.charAt(1) == 'O' && currentLine.charAt(2) == 'F'){
					break;
					}
				City c = new City(Double.parseDouble(rmEmpty(currentLine).split("\\s+")[1]), 
						Double.parseDouble(rmEmpty(currentLine).split("\\s+")[2]), i);
				System.out.println("City " + i + " = " + c);
				listCity.add(c);
				i++;
			}

		} catch(IOException e){
			e.printStackTrace();
		};	
		
		return listCity;
	}
	
	private static String rmEmpty(String str){
		while(str.charAt(0) == ' ')
			str = str.substring(1);
		return str;
	}
	
	public void displayMatrix()
	{
		for(int i = 0; i< DataTSP.nbCity; i++)
		{
			System.out.println("City no " + i + "'s links to other cities:");
			for(int j = 0; j< DataTSP.nbCity; j++)
			{
				System.out.println("matrix[ "+ i + " ][ " + j + " ] = " + this.matrixCost[i][j] );
			}
			System.out.println("");
		}
	}
	
	public double[][] getMatrixCost() {
		return matrixCost;
	}

	public void setMatrixCost(double matrix_cost[][]) {
		matrixCost = matrix_cost;
	}
	
	public int getNbCity() {
		return nbCity;
	}
}