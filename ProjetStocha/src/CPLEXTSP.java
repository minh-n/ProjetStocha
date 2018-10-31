import ilog.concert.*;

public class CPLEXTSP extends CPLEX{
	private int nbCities;
	private double[][] matrixCost;
	private IloNumVar[][] matrixSolution; 

	public CPLEXTSP() throws IloException {
		super();
		nbCities = ((DataTSP)problem.getData()).getNbCity();
		matrixCost = ((DataTSP)problem.getData()).getMatrixCost();
	}
	
	protected int[][] castInIntMatrix() throws IloException {
		int[][] solution = new int[nbCities][nbCities];
		for(int i = 0; i < nbCities; i++) {
			for(int j = 0; j < nbCities; i++) {
				solution[i][j] = (int) model.getValue(matrixSolution[i][j]);
			}
		}
		return solution;
	}

	@Override
	protected Solution solve() {
		SolutionTSP result = solveWithSubtourElimination(); 
		try {
			endResolution();
		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private SolutionTSP solveWithSubtourElimination() {
		SolutionTSP result = new SolutionTSP(); 
		try {
			model.solve();
			result.setSol(castInIntMatrix());
			result = new IterativeAlgorithmTSP(this).loop();
		} catch (IloException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	protected void addVariables() throws IloException {
		matrixSolution = new IloNumVar[nbCities][];
		for(int i = 0; i < nbCities; i++) {
			matrixSolution[i] = model.boolVarArray(nbCities);
		}
	}

	@Override
	protected void initializeObjective() throws IloException {
		for(int i = 0; i < nbCities; i++) {
			for(int j = 0; j < nbCities; j++) {
				if(j != i) 
					objective.addTerm(matrixCost[i][j], matrixSolution[i][j]);
			}
		}
	}

	@Override
	protected void addConstraints() throws IloException {
		for(int j = 0; j < nbCities; j++) {
			IloLinearNumExpr constraint1a = model.linearNumExpr();
			for(int i = 0; i < nbCities; i++) {
				if(i != j)
					constraint1a.addTerm(1.0, matrixSolution[i][j]);
			}
			model.addEq(constraint1a, 1.0);
		}
		
		for(int i = 0; i < nbCities; i++) {
			IloLinearNumExpr constraint1b = model.linearNumExpr();
			for(int j = 0; j < nbCities; j++) {
				if(j != i)
					constraint1b.addTerm(1.0, matrixSolution[i][j]);
			}
			model.addEq(constraint1b, 1.0);
		}
	}
	
	public IloNumVar[][] getMatrixSolution() {
		return matrixSolution;
	}
}