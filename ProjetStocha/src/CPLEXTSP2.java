import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.LazyConstraintCallback;

public class CPLEXTSP2 extends CPLEX{
	private IloNumVar[][] matrixSolution; 

	public CPLEXTSP2(LinearProblem problem) throws IloException {
		super(problem);
		this.solve();
	}

	@Override
	protected void solve() {
		try {
			SolutionTSP result = solveWithSubtourElimination(); 
			problem.setCost(model.getObjValue());
			endResolution();
			LinearProblem.setSol(result);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	private SolutionTSP solveWithSubtourElimination() throws IloException {
		SolutionTSP result = new SolutionTSP(); 
		model.use(new SubtourElimination());
		model.solve();
		System.out.println(model.getObjValue());
		result.setSol(castMatrixInInt());
		return result;
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
	
	private void addConstraint1c(IloCplex model, int[][] solution) throws IloException {
		ArrayList<ArrayList<Integer>> subtour = createSubtour(createLap(solution));
		int size = subtour.size();
		if(size > 1) {
			for(int i = 0; i < size; i++) {
				IloLinearNumExpr constraint1c = model.linearNumExpr();
				final ArrayList<Integer> tmp = subtour.get(i);
				for(int j = 0; j < tmp.size(); j++) {
					for(int k = 0; k < tmp.size(); k++) {
						constraint1c.addTerm(1.0, matrixSolution[tmp.get(j)][tmp.get(k)]);
					}
				}
				model.addLe(constraint1c, tmp.size()-1);
			}
		}
	}
	
	private ArrayList<Integer> createLap(int[][] solution) {
		ArrayList<Integer> lap = new ArrayList<Integer>();
		int size = solution.length;
		for(int i = 0; i < size; i++) {
			int tmp = -1;
			for(int j = 0; j < size; j++) {
				if(solution[i][j] == 1) {
					tmp = j;
					lap.add(tmp);
					break;
				}
			}
		}
		return lap;
	}
	
	private ArrayList<ArrayList<Integer>> createSubtour(ArrayList<Integer> lap) {
		int actualCity = 0;
		ArrayList<ArrayList<Integer>> subtour = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> tour = new ArrayList<Integer>();
		ArrayList<Integer> views = new ArrayList<Integer>();
		for(int i = 0; i < lap.size(); i++) {
			tour.add(actualCity);
			views.add(actualCity);
			if(!tour.contains(lap.get(actualCity)))
				actualCity = lap.get(actualCity);
			else {
				subtour.add(tour);
				if(i != lap.size()-1) {
					for(int j = 0; j < lap.size(); j++) {
						if(!views.contains(j)) {
							actualCity = j;
							break;
						}
					}
					tour = new ArrayList<Integer>(); 
				}
			}
		}
		return subtour;
	}
	
	private class SubtourElimination extends LazyConstraintCallback {

		@Override
		protected void main() throws IloException {
			addConstraint1c(model, castMatrixInInt()); 
		}
		
		protected int[][] castMatrixInInt() throws IloException {
			int nbCities = ((DataTSP)problem.getData()).getNbCity();
			int[][] solution = new int[nbCities][nbCities];
			for(int i = 0; i < nbCities; i++) {
				for(int j = 0; j < nbCities; j++) {
					if(j != i)
						solution[i][j] = (int) this.getValue(matrixSolution[i][j]);
					else
						solution[i][j] = 0;
				}
			}
			return solution;
		}
	}
}
