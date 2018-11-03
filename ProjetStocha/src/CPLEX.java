import ilog.concert.*;
import ilog.cplex.*;

public abstract class CPLEX extends Solver{
	protected IloCplex model;
	protected IloLinearNumExpr objective;
	protected boolean verbose;
	protected boolean find;
	
	public CPLEX(LinearProblem problem, boolean verbose) throws IloException {
		super(problem);
		model = new IloCplex();
		objective = model.linearNumExpr();
		this.verbose = verbose;
		addVariables();
		initializeObjective();
		minimizeOrMaximize();
		addConstraints();
	}
	
	protected abstract void addVariables() throws IloException;
	protected abstract void initializeObjective() throws IloException;
	protected abstract void addConstraints() throws IloException;
	protected abstract Solution castMatrixToSolution() throws IloException;
	
	private void minimizeOrMaximize() throws IloException {
		if(problem.getMaxMin()) 
			model.addMaximize(this.objective); 
		else
			model.addMinimize(this.objective);
	}
	
	protected void endResolution() throws IloException {
		model.end();
	}
	
	public boolean isFind() {
		return find;
	}

	public void setFind(boolean find) {
		this.find = find;
	}

	public IloCplex getModel() {
		return model;
	}
}