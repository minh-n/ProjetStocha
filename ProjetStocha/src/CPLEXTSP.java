import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.distribution.NormalDistribution;

import ilog.concert.*;
import ilog.cplex.IloCplex;

public class CPLEXTSP extends CPLEX{
	/**
	 * matrice representant la solution d'un probleme du voyageur de commerce
	 * true si l'arc represente par matrixSolution[villeI][villeJ] est retenu dans le circuit
	 */
	private IloNumVar[][] matrixSolution; 

	public CPLEXTSP(LinearProblem problem, boolean isDeterministic, boolean verbose) throws IloException {
		super(problem, isDeterministic, verbose);
	}
	
	public CPLEXTSP(LinearProblem problem, boolean isDeterministic, double alpha, boolean verbose) throws IloException {
		super(problem, isDeterministic, alpha, verbose);
	}

	/**
	 * resout le probleme du voyageur de commerce
	 * met a jour le cout et la solution du probleme lineaire represente par la variable problem
	 */
	@Override
	protected void solve() {
		SolutionTSP result = solveWithSubtourElimination(verbose); 
		try {
			if(find) {
				problem.setCost(model.getObjValue());
				LinearProblem.setSol(result);
			}
			endResolution();
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * resout le probleme du voyageur de commerce en ajoutant iterativement la contrainte sur les sous-tours
	 * @param verbose
	 * @return
	 */
	private SolutionTSP solveWithSubtourElimination(boolean verbose) {
		SolutionTSP result = new SolutionTSP(); 
		try {
			if(!verbose) {
				model.setOut(null);
				model.setWarning(null);
			}
			this.setFind(model.solve());
			result = castMatrixToSolution();
			result = new SubTourEliminationCPLEX(this).loop();
			if(!isDeterministic) {
				endResolution();
				model = new IloCplex();
				objective = model.linearNumExpr();
				addVariables();
				initializeObjective();
				minimizeOrMaximize();
				addConstraints();
				addConstraint1d(model.getObjValue()*1.25d, alpha);
				this.setFind(model.solve());
				result = castMatrixToSolution();
				result = new SubTourEliminationCPLEX(this).loop();
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * ajoute une matrice de boolean representant la solution du TSP au modele
	 */
	@Override
	protected void addVariables() throws IloException {
		int nbCities = ((DataTSP)problem.getData()).getNbCity();
		matrixSolution = new IloNumVar[nbCities][nbCities];
		for(int i = 0; i < nbCities; i++) {
			for(int j = 0; j < nbCities; j++) {
				matrixSolution[i][j] = model.boolVar();
			}
		}
	}

	/**
	 * initialise la fonction objective du probleme du voyageur de commerce
	 */
	@Override
	protected void initializeObjective() throws IloException {
		int nbCities = ((DataTSP)problem.getData()).getNbCity();
		final double[][] matrixCost = ((DataTSP)problem.getData()).getMatrixCost();
		for(int i = 0; i < nbCities; i++) {
			for(int j = 0; j < nbCities; j++) {
				if(j != i) 
					objective.addTerm(matrixCost[i][j], matrixSolution[i][j]);
			}
		}
	}

	/**
	 * ajoute la contrainte "un unique arc entrant par ville"
	 * ajoute la contrainte "un unique arc sortant par ville"
	 */
	@Override
	protected void addConstraints() throws IloException {
		int nbCities = ((DataTSP)problem.getData()).getNbCity();
		for(int j = 0; j < nbCities; j++) {
			IloLinearNumExpr constraint1a = model.linearNumExpr();
			for(int i = 0; i < nbCities; i++) {
				if(i != j)
					constraint1a.addTerm(1.d, matrixSolution[i][j]);
			}
			model.addEq(constraint1a, 1.d);
		}
		
		for(int i = 0; i < nbCities; i++) {
			IloLinearNumExpr constraint1b = model.linearNumExpr();
			for(int j = 0; j < nbCities; j++) {
				if(j != i)
					constraint1b.addTerm(1.d, matrixSolution[i][j]);
			}
			model.addEq(constraint1b, 1.d);
		}
	}
	
	private void addConstraint1d(double Z, double alpha) throws IloException {
		int nbCities = ((DataTSP)problem.getData()).getNbCity();
		final double[][] matrixCost = ((DataTSP)problem.getData()).getMatrixCost();
		ArrayList<HashMap<Integer, Double>> variances = getVariances(matrixCost);
		NormalDistribution normalDistribution = new NormalDistribution();
		double quantileAlpha = normalDistribution.inverseCumulativeProbability(alpha);
		for(int i = 0; i < nbCities; i++) {
			IloLinearNumExpr constraint1d = model.linearNumExpr();
			for(int j = 0; j < nbCities; j++) {
				if(j != i) 
					constraint1d.addTerm(matrixCost[i][j] + quantileAlpha*variances.get(i).get(j), matrixSolution[i][j]);	
			}
			model.addLe(constraint1d, Z);
		}
	}

	/**
	 * retourne une instance de SolutionTSP representant la solution du probleme
	 */
	@Override
	protected SolutionTSP castMatrixToSolution() throws IloException {
		if(find) {
			int nbCities = ((DataTSP)problem.getData()).getNbCity();
			int[][] solution = new int[nbCities][nbCities];
			for(int i = 0; i < nbCities; i++) {
				for(int j = 0; j < nbCities; j++) {
					if(j != i)
						solution[i][j] = (int) model.getValue(matrixSolution[i][j]);
					else
						solution[i][j] = 0;
				}
			}
			return new SolutionTSP(solution);
		}
		return null;
	}
	
	public IloNumVar[][] getMatrixSolution() {
		return matrixSolution;
	}
	
	private ArrayList<HashMap<Integer, Double>> getVariances(double[][] matrixCost) {
		ArrayList<HashMap<Integer, Double>> variances = new ArrayList<HashMap<Integer, Double>>();
		int nbCities = ((DataTSP)problem.getData()).getNbCity();
		for(int i = 0; i < nbCities; i++) {
			HashMap<Integer, Double> actualVariance = new HashMap<Integer, Double>();
			for(int j = 0; j < nbCities; j++) {
				if(j != i) 
					actualVariance.put(j, matrixCost[i][j]*0.2d);
			}
			variances.add(actualVariance);
		}
		return variances;
	}
}