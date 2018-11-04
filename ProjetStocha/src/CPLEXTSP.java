import ilog.concert.*;

public class CPLEXTSP extends CPLEX{
	/**
	 * matrice representant la solution d'un probleme du voyageur de commerce
	 * true si l'arc represente par matrixSolution[villeI][villeJ] est retenu dans le circuit
	 */
	private IloNumVar[][] matrixSolution; 

	public CPLEXTSP(LinearProblem problem, boolean verbose) throws IloException {
		super(problem, verbose);
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
}