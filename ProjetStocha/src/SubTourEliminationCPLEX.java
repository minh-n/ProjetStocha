import java.util.ArrayList;
import java.util.HashMap;

import ilog.concert.*;

public class SubTourEliminationCPLEX extends IterativeAlgorithm
{
	private CPLEXTSP algo;
	
	public SubTourEliminationCPLEX(CPLEXTSP algo) {
		this.algo = algo; 
	}

	@Override
	protected SolutionTSP oracle() {
		SolutionTSP result = new SolutionTSP();
		try {
			algo.setFind(algo.getModel().solve());
			result = algo.castMatrixToSolution();
		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected SolutionTSP loop() {
		int counter = 1;
		SolutionTSP result = new SolutionTSP();
		try {
			result = algo.castMatrixToSolution();
			while(addConstraint1c(result)) {
				System.out.println("Subtour Elimination " + counter + ", please wait...");
				counter++;
				result = oracle();
				if(!algo.isFind())
					break;
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private boolean addConstraint1c(SolutionTSP solution) throws IloException {
		if(algo.isFind()) {
			ArrayList<ArrayList<Integer>> subtour = createSubtour(createLap(solution));
			if(subtour.size() > 1) {
				for(int i = 0; i < subtour.size(); i++) {
					IloLinearNumExpr constraint1c = algo.model.linearNumExpr();
					final ArrayList<Integer> tmp = subtour.get(i);
					for(int j = 0; j < tmp.size(); j++) {
						for(int k = 0; k < tmp.size(); k++) {
							constraint1c.addTerm(1.0, algo.getMatrixSolution()[tmp.get(j)][tmp.get(k)]);
						}
					}
					algo.model.addLe(constraint1c, tmp.size()-1);
				}
				return true;
			}
		}
		return false;
	}
	
	private HashMap<Integer, Integer> createLap(SolutionTSP solution) {
		HashMap<Integer, Integer> lap = new HashMap<Integer, Integer>();
		final int[][] matrixSolution = solution.getSol();
		int size = matrixSolution.length;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(matrixSolution[i][j] == 1) {
					lap.put(i, j);
					break;
				}
			}
			if(lap.size() != i+1)
				lap.put(i, -1);
		}
		return lap;
	}
	
	private ArrayList<ArrayList<Integer>> createSubtour(HashMap<Integer, Integer> lap) {
		Integer actualCity = 0;
		Integer firstCity = 0;
		Integer nextCity;
		ArrayList<ArrayList<Integer>> subtour = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> tour = new ArrayList<Integer>();
		ArrayList<Integer> cities = new ArrayList<Integer>();
		
		for(int i = 0; i < lap.size(); i++) {
			cities.add(i);
		}
		
		while(cities.size() > 0) {
			tour.add(actualCity);
			cities.remove(actualCity);
			nextCity = lap.get(actualCity);
			if(!firstCity.equals(nextCity) && nextCity.intValue() != -1) 
				actualCity = nextCity;
			else {
				subtour.add(tour);
				if(cities.size() > 0) {
					actualCity = cities.get(0);
					firstCity = actualCity;
					tour = new ArrayList<Integer>(); 
				}
			}
		}
		return subtour;
	}
}