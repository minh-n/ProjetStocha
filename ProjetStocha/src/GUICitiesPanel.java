
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class GUICitiesPanel extends JPanel {

	private static final long serialVersionUID = 1L;	
	private Matrix matrixCost;
	private List<City> citiesPosition; 
	private int nbCity;
	
	public GUICitiesPanel()
	{
		citiesPosition = new ArrayList<City>();
	}


	//on get les donnees et on lance les calculs apres avoir obtenu les data
	public void getData(DataTSP data)
	{
		citiesPosition.clear();
		System.out.println("GUICitiesPanel: Getting data from class Data!");
		this.matrixCost =  new Matrix(data.getMatrixCost());
		this.nbCity = data.getNbCity();
		
		//System.out.println("\nGUICitiesPanel: Displaying the cost matrix below: ");
		
		//Matrix.print(nb of space for each column, number of digits after the .)
		//this.matrixCost.print(4, 1);
		
		System.out.println("\nGUICitiesPanel: Calculating cities position based on the cost matrix: ");
		
		this.getPositionsFromCost(true);
		
	}
	
	public void getPositionsFromCost(boolean test)
	{
		
		int id = 0;
		
		Matrix M = new Matrix(nbCity, nbCity);
		
		System.out.println("GUICitiesPanel (getPosFromCost): created a M matrix of dimension (" 
												+ M.getColumnDimension() +" ," + M.getRowDimension() + ").\n");
		for (int i = 0; i < nbCity; i++)
		{
			for (int j = 0; j < nbCity; j++)
			{
				double T = (
						Math.pow(matrixCost.get(0, j), 2)
						+ 
						Math.pow(matrixCost.get(i, 0), 2)
						- 
						Math.pow(matrixCost.get(i, j), 2)
						) / 2;
				M.set(i, j, T);
			}
		}
		
		//computing eigenvalues
		EigenvalueDecomposition eig = M.eig();
		Matrix eigV = eig.getV();
		Matrix eigD = eig.getD();

		//computing sqrt of eigD
		for (int i = 0; i < eigD.getColumnDimension(); i++)
		{
			for (int j = 0; j <  eigD.getRowDimension(); j++)
			{
				eigD.set(i, j,  Math.sqrt(eigD.get(i, j)));
			}
		}
		
		Matrix X = eigV.times(eigD);
		
		
		for (int i = 0; i < X.getColumnDimension(); i++)
		{
			for (int j = X.getRowDimension()-2; j < X.getRowDimension(); j=j+2)
			{
				double a = X.get(i, j);
				double b = X.get(i, j+1);
				City city = new City(a, b, id);
				citiesPosition.add(city);
				id++;
			}
		}
		
		
		//Testing the quality of the results
		if(test)
		{
			System.out.println("Launching value tests: % of difference between values in the XML data and the computed coordinates");
			
			double diff = 0;
			for (int i = 0; i < nbCity; i++)
			{
				for (int j = 0; j <  nbCity; j++)
				{
					
					double v1 = this.citiesPosition.get(i).getDistanceToAnotherCity(this.citiesPosition.get(j));
					double v2 = matrixCost.get(i, j);
					
					diff += Math.abs(v1 - v2)/((Math.abs(v1+v2)/2));
					
//					int rounded = (int) Math.round();
//					if(rounded == Math.round()
//					{
//						nbCorrect++;
//					}
				}
			}
			
			//double percentageCorrect =  100.*(((double) nbCorrect)/(nbCity*nbCity));
			
			diff = 100.*diff/(nbCity*nbCity);
			
			System.out.println("Mean % of difference between values is " + diff + "%.");
			
			System.out.println("SAMPLE: " + this.citiesPosition.get(3));
			System.out.println("SAMPLE: " + this.citiesPosition.get(15));
			System.out.println("SAMPLE: Computing distance between city3 and city15 = " + this.citiesPosition.get(3).getDistanceToAnotherCity(this.citiesPosition.get(15)));
			System.out.println("SAMPLE: Same value from the XML cost matrix = " + matrixCost.get(3, 15));
		
		}

	}
	
	
}
