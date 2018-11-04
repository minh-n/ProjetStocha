import ilog.concert.*;
import ilog.cplex.*;

public abstract class CPLEX extends Solver{
	
	/**
	 * contient le modele du probleme lineaire a resoudre
	 */
	protected IloCplex model;
	
	/**
	 * fonction objective du probleme lineaire
	 */
	protected IloLinearNumExpr objective;
	
	/**
	 * true pour lancer la resolution deterministe du probleme avec CPLEX
	 */
	protected boolean isDeterministic;
	
	/**
	 * dans le cas stochastique, alpha represente la contrainte de risque a respecter 
	 */
	protected double alpha = 0.95d;
	
	/**
	 * variable a initialiser a la creation d'une instance CPLEX
	 * true pour afficher dans le canal outstream la sortie de la methode IloCplex.solve()
	 */
	protected boolean verbose;
	
	/**
	 * find permet de stocker le resultat de l'appel a la methode IloCplex.solve()
	 */
	protected boolean find;
	
	/**
	 * initialise l'instance CPLEX avec un LinearProblem
	 * initialise la variable model avec les variables, les contraintes et la fonction objective du probleme lineaire
	 * @param problem
	 * @param verbose
	 * @throws IloException
	 */
	public CPLEX(LinearProblem problem, boolean isDeterministic, boolean verbose) throws IloException {
		super(problem);
		model = new IloCplex();
		objective = model.linearNumExpr();
		this.isDeterministic = isDeterministic;
		this.verbose = verbose;
		addVariables();
		initializeObjective();
		minimizeOrMaximize();
		addConstraints();
	}
	
	/**
	 * initialise l'instance CPLEX avec un LinearProblem
	 * @param problem
	 * @param isDeterministic
	 * @param alpha
	 * @param verbose
	 * @throws IloException
	 */
	public CPLEX(LinearProblem problem, boolean isDeterministic, double alpha, boolean verbose) throws IloException {
		super(problem);
		initializeModel();
		this.isDeterministic = isDeterministic;
		this.alpha = alpha;
		this.verbose = verbose;
	}
	
	/**
	 * initialise la variable model avec les variables, les contraintes et la fonction objective du probleme lineaire
	 * @throws IloException
	 */
	protected void initializeModel() throws IloException {
		model = new IloCplex();
		objective = model.linearNumExpr();
		addVariables();
		initializeObjective();
		minimizeOrMaximize();
		addConstraints();
	}
	
	/**
	 * ajoute les variables du probleme lineaire au modele
	 * @throws IloException
	 */
	protected abstract void addVariables() throws IloException;
	
	/**
	 * initialise la fonction objective du probleme lineaire et l'ajoute au modele
	 * @throws IloException
	 */
	protected abstract void initializeObjective() throws IloException;
	
	/**
	 * ajoute les contraintes du probleme lineaire au modele
	 * @throws IloException
	 */
	protected abstract void addConstraints() throws IloException;
	
	/**
	 * transforme la variable contenant la solution de l'API CPLEX en une instance de Solution 
	 * @return 
	 * @throws IloException
	 */
	protected abstract Solution castMatrixToSolution() throws IloException;
	
	/**
	 * minimise ou maximise la fonction objective selon le probleme lineaire a resoudre
	 * @throws IloException
	 */
	protected void minimizeOrMaximize() throws IloException {
		if(problem.getMaxMin()) 
			model.addMaximize(this.objective); 
		else
			model.addMinimize(this.objective);
	}
	
	/**
	 * libere l'espace memoire pris par la variable model
	 * @throws IloException
	 */
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