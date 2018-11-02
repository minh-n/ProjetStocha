import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;

public class CPLEXTSP3 extends CPLEX{
	private IloNumVar[][] matrixSolution; 
	private IloNumVar[] u;

	public CPLEXTSP3(LinearProblem problem, boolean verbose) throws IloException {
		super(problem, verbose);
		this.solve();
	}

	@Override
	protected void solve() {
		try {
			SolutionTSP result = new SolutionTSP();
			if(!verbose)
				model.setOut(null);
			model.solve();
			result.setSol(castMatrixInInt());
			problem.setCost(model.getObjValue());
			LinearProblem.setSol(result);
			endResolution();
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void addVariables() throws IloException {
		int nbCities = ((DataTSP)problem.getData()).getNbCity();
		matrixSolution = new IloNumVar[nbCities][nbCities];
		for(int i = 0; i < nbCities; i++) {
			for(int j = 0; j < nbCities; j++) {
				matrixSolution[i][j] = model.boolVar();
			}
		}
		u = model.numVarArray(nbCities, 0, Double.MAX_VALUE);
	}

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
		
		for(int i = 1; i < nbCities; i++) {
			for(int j = 1; j < nbCities; j++) {
				if(j != i) {
					IloLinearNumExpr constraint1c = model.linearNumExpr();
					constraint1c.addTerm(1.0, u[i]);
					constraint1c.addTerm(-1.0, u[j]);
					constraint1c.addTerm(nbCities-1, matrixSolution[i][j]);
					model.addLe(constraint1c, nbCities-2);
				}
			}
		}
	}
	
	protected int[][] castMatrixInInt() throws IloException {
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
		return solution;
	}
}
