
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class GUICitiesPanel extends JPanel implements MouseMotionListener, ComponentListener {

	private static final long serialVersionUID = 1L;
	private Matrix matrixCost;
	private List<City> citiesPosition;
	private List<City> citiesPositionForDisplay;

	private int nbCity;
	private Rectangle.Double windowSize = new Rectangle.Double();
	public boolean isPaintPath = true;

	public GUICitiesPanel() {
		citiesPosition = new ArrayList<City>();
		citiesPositionForDisplay = new ArrayList<City>();
		addComponentListener(this);
		addMouseMotionListener(this);
	}

	public void getData(DataTSP data) {
		
		//TODO timer
		//Timer t = new Timer();
	    //t.start();
	    
		System.out.println("\n---WARNING: Data cleared. CitiesPanel standing by.");

		citiesPosition.clear();
		System.out.println("GUICitiesPanel: Getting data from the class Data!");
		
		//getting raw data (the double[][] matrixCost) from the Data class
		this.matrixCost = new Matrix(data.getMatrixCost());
		this.nbCity = data.getNbCity();
		
		if(GUI.isGetTSP())
		{
			System.out.println("\nGUICitiesPanel: Getting cities position with the TSP file." );
			citiesPosition = DataTSP.parserTSP(GUI.getCurrentFilename().replace("xml", "tsp"));

			setWindowBorders();
			setCitiesPositionForDisplay();
		}
		else
		{
			System.out.println("\nGUICitiesPanel: Calculating cities position based on the cost matrix: ");
			this.getPositionsFromCost(true);
		}

		//System.out.println("\nGUICitiesPanel: Displaying the cost matrix below: ");
		//this.matrixCost.print(4, 1);

		// Matrix.print(nb of space for each column, number of digits after the .)

		
	}
	
	/**
	 * Paint the paths between the cities, using Graphics2D g from PaintComponent
	 * @param g Graphics2D element, created in PaintComponent
	 */
	public void paintPath(Graphics2D g) {
		System.out.println("\n--GUICitiesPanel: painting the paths.");
		g.setColor(Color.BLACK); 

		//TODO instance of c'est moche
		if(LinearProblem.getSol() instanceof SolutionTSP) 
		{
			final int[][] solution = ((SolutionTSP)LinearProblem.getSol()).getSol();
			for (int i = 0; i < nbCity; i++) {
				for (int j = 0; j < nbCity; j++) {
					if(solution[i][j] == 1)	//TODO replace with matriceSol[i][j]
					{
						//System.out.println("Debug: Link found between city " + i + " and " + j +".");
						City c1 = citiesPositionForDisplay.get(i);
						City c2 = citiesPositionForDisplay.get(j);
						//System.out.println(c1 + " LINKED TO " + c2);
	                    Shape l = new Line2D.Double(c1.getX()+2, c1.getY()+2, c2.getX()+2, c2.getY()+2);
	                    g.draw(l);
	      
						break;
					}
				}
			}
		}

	}

	/**
	 * Compute the cities position according to the min and max found earlier
	 */
	public void setCitiesPositionForDisplay() {
		citiesPositionForDisplay.clear();

		//getting the max panel size
		double maxSize = (this.getWidth() > this.getHeight()) ? this.getHeight() : this.getWidth();
		//computing the coefficient between maxsize and 
		double sideCoef = maxSize / (windowSize.height > windowSize.width ? windowSize.height : windowSize.width);

		int id = 0;
		// Iterating through list of cities, changing their position according to screen size
		//windowSize.x and y is the position of the window
		for (City c : citiesPosition) {
			citiesPositionForDisplay.add(new City(((c.getX() - windowSize.x) 
					* sideCoef), ((c.getY() - windowSize.y) * sideCoef), id));
			id++;
		}
	}

	/**
	 * Compute the limits in terms of pixels, from the window size and from the mix nad max city positions
	 */
	public void setWindowBorders() {

		System.out.println("\n--GUICitiesPanel: setting window borders.");
		City cityMin = new City(citiesPosition.get(0).getX(), citiesPosition.get(0).getY(), -1);
		City cityMax = new City(citiesPosition.get(0).getX(), citiesPosition.get(0).getY(), -2);

		// finding min and max values
		for (City c : citiesPosition) {
			if (c.getX() < cityMin.getX()) {
				cityMin.setX(c.getX());
			} else if (c.getX() > cityMax.getX()) {
				cityMax.setX(c.getX());
			}
			if (c.getY() < cityMin.getY()) {
				cityMin.setY(c.getY());
			} else if (c.getY() > cityMax.getY()) {
				cityMax.setY(c.getY());
			}
		}

//			System.out.println("cityMin = " + cityMin.getX() + cityMin.getY());
//			System.out.println("cityMax = " + cityMax.getX() + cityMax.getY());
//			//Adding padding for better centering
//			int margin = 10;
//			cityMin.setX(cityMin.getX()+margin);
//			cityMin.setY(cityMin.getY()+margin);
//			cityMax.setX(cityMax.getX()+margin);
//			cityMax.setY(cityMax.getY()+margin);

		// setting window size using the max and min found
		// the coefficients are not set in stone yet, this is still methode habile
		windowSize = new Rectangle.Double(cityMin.getX() - (cityMax.getX() - cityMin.getX()) * 0.02,

				cityMin.getY() - (cityMax.getY() - cityMin.getY()) * 0.02,

				cityMax.getX() - cityMin.getX() + (cityMax.getX() - cityMin.getX()) * 0.1,

				cityMax.getY() - cityMin.getY() + (cityMax.getY() - cityMin.getY()) * 0.1);

		// System.out.println("\nDebug: GUICitiesPanel: Window border " + windowSize);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics graphics) {

		//implementing graphics2d
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int sideSize = (this.getWidth() > this.getHeight()) ? this.getHeight() : this.getWidth();

		//filling available screen with light gray
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, sideSize, sideSize);

		//setting city color
		g.setColor(Color.RED);

		// Displaying each cities
		for (City c : citiesPositionForDisplay) {
			g.fillRect((int) c.getX(), (int) c.getY(), 4, 4);

		}
		if(isPaintPath)
		{
			this.paintPath(g);
		}
	}

	/**
	 * Compute the position of each cities, using its cost to the other cities
	 * @param test True if we want to test the quality of the result (calculated distances vs true coordinates
	 */
	public void getPositionsFromCost(boolean test) {

		int id = 0;

		Matrix M = new Matrix(nbCity, nbCity);

		System.out.println("--GUICitiesPanel (getPosFromCost): created a M matrix of dimension (" + M.getColumnDimension()
				+ ", " + M.getRowDimension() + ").\n");
		for (int i = 0; i < nbCity; i++) {
			for (int j = 0; j < nbCity; j++) {
				M.set(i, j, (Math.pow(matrixCost.get(0, j), 2) 
						+ Math.pow(matrixCost.get(i, 0), 2)
						- Math.pow(matrixCost.get(i, j), 2)) / 2);
			}
		}

		// computing eigenvalues
		EigenvalueDecomposition eig = M.eig();
		Matrix eigV = eig.getV();
		Matrix eigD = eig.getD();

		// computing sqrt of eigD
		for (int i = 0; i < eigD.getColumnDimension(); i++) {
			for (int j = 0; j < eigD.getRowDimension(); j++) {
				eigD.set(i, j, Math.sqrt(eigD.get(i, j)));
			}
		}

		//creating X matrix (V*D)
		Matrix X = eigV.times(eigD);

		// filling the cities position
		for (int i = 0; i < X.getColumnDimension(); i++) {
			//getting the cities position from the last two columns of X
			for (int j = X.getRowDimension() - 2; j < X.getRowDimension(); j = j + 2) {
				City city = new City(X.get(i, j), X.get(i, j + 1), id);
				citiesPosition.add(city);
				id++;
			}
		}

		// Testing the quality of the results
		if (test) {
			System.out.println(
					"Launching value tests: % of difference between values in the XML data and the computed coordinates");

			double diff = 0;
			for (int i = 0; i < nbCity; i++) {
				for (int j = 0; j < nbCity; j++) {

					double v1 = this.citiesPosition.get(i).getDistanceToAnotherCity(this.citiesPosition.get(j));
					double v2 = matrixCost.get(i, j);

					diff += Math.abs(v1 - v2) / ((Math.abs(v1 + v2) / 2));
				}
			}

			// double percentageCorrect = 100.*(((double) nbCorrect)/(nbCity*nbCity));

			diff = 100. * diff / (nbCity * nbCity);

			System.out.println("Mean % of difference between values is " + diff + "%.\n");
//			
//			System.out.println("SAMPLE: " + this.citiesPosition.get(3));
//			System.out.println("SAMPLE: " + this.citiesPosition.get(15));
//			System.out.println("SAMPLE: Computing distance between city3 and city15 = " + this.citiesPosition.get(3).getDistanceToAnotherCity(this.citiesPosition.get(15)));
//			System.out.println("SAMPLE: Same value from the XML cost matrix = " + matrixCost.get(3, 15));

			setWindowBorders();
			setCitiesPositionForDisplay();

		}

	}

	public Rectangle.Double getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Rectangle.Double windowSize) {
		this.windowSize = windowSize;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent arg0) {
		//on resize, compute the new coordinates with the new window size
		setCitiesPositionForDisplay();
		repaint();
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {

	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		repaint();

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		

	}

}
