import java.util.ArrayList;
import java.util.HashMap;

import ilog.concert.*;

public class SubTourEliminationCPLEX extends IterativeAlgorithm
{
	/**
	 * instance de CPLEX sur laquelle l'ajout de la contrainte de sous-tours est faite
	 */
	private CPLEXTSP algo;
	
	public SubTourEliminationCPLEX(CPLEXTSP algo) {
		this.algo = algo; 
	}
	
	/**
	 * ajoute la contrainte de sous-tours au modele de cplex et le resout
	 */
	@Override
	protected boolean oracle() {
		boolean subtour = false;
		try {
			SolutionTSP result = algo.castMatrixToSolution();
			subtour = addConstraint1c(result);
			algo.setFind(algo.getModel().solve());
		} catch (IloException e) {
			e.printStackTrace();
		}
		return subtour;
	}

	/**
	 * ajoute la contrainte de sous-tours jusqu'a la resolution du probleme
	 */
	@Override
	protected SolutionTSP loop() {
		int counter = 1;
		SolutionTSP result = new SolutionTSP();
		try {
			System.out.println("Subtour Elimination " + counter + ", please wait...");
			while(oracle()) {
				if(!algo.isFind())
					break;
				counter++;
				System.out.println("Subtour Elimination " + counter + ", please wait...");
			}
			result = algo.castMatrixToSolution();
		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * ajoute la contrainte de sous-tours au modele du probleme  
	 * @param solution
	 * @return
	 * @throws IloException
	 */
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
	
	/**
	 * creee une hashmap contenant le numero de la ville en cle et le numero de la ville suivante dans circuit en valeur 
	 * @param solution
	 * @return
	 */
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
	
	/**
	 * creee une liste contenant les sous-tours d'un circuit
	 * chaque sous-tour contient les numeros de ville present dans le sous-tour
	 * @param lap
	 * @return
	 */
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