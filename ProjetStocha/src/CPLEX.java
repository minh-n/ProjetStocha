import ilog.concert.*;
import ilog.cplex.*;

public abstract class CPLEX extends Solver{
	protected IloCplex model;
	protected IloLinearNumExpr objective;
	protected boolean find;
	
	public CPLEX(LinearProblem problem) throws IloException {
		super(problem);
		model = new IloCplex();
		objective = model.linearNumExpr();
		addVariables();
		initializeObjective();
		minimizeOrMaximize();
		addConstraints();
	}
	
	protected abstract void addVariables() throws IloException;
	protected abstract void initializeObjective() throws IloException;
	protected abstract void addConstraints() throws IloException;
	
	private void minimizeOrMaximize() throws IloException {
		if(problem.getMaxMin()) 
			model.addMaximize(this.objective); 
		else
			model.addMinimize(this.objective);
	}
	
	protected void endResolution() throws IloException {
		model.end();
	}
}