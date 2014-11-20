/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.model.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.modelUncertainty.Tools;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.potential.operation.Util;
import org.openmarkov.learning.core.util.LearningEditProposal;
import org.openmarkov.learning.core.util.ScoreEditMotivation;


/** A probabilistic node has a set of conditional probabilities, one variable, 
 * etc. The structural aspect of the underlying  graph is in the node
 * associated.  * @author marias * @author fjdiez
 * @since OpenMarkov 1.0 * @see openmarkov.graphs.Node * @see org.openmarkov.core.model.network.ProbNet * @version 1.0 */
public class ProbNode implements Cloneable, PotentialsContainer {
	
	// Constants
	public final static double defaultRelevance = 5.0;

	// Attributes/
	/** @frozen
	 * <code>node</code> that supplies the structural aspect. */
	protected Node node;

	/** @frozen */
	protected NodeType nodeType;
	
	/** @frozen */
	protected UtilityCombinationFunction utilityCombinationFunction;
	
	/** @frozen */
	protected ProbNet probNet;

    /** Each <code>probNode</code> has a list of potentials */
    protected List<Potential> potentials;
    
    /** The variable associated */
    protected Variable variable;
    
    /** Purpose of node */
    private String purpose = "";
    
    /** Relevance of node */
    private double relevance = defaultRelevance;
    
    /** Comment about node definition */
    private String comment = "";
    
    /** For ICI Models visualization. */
    private boolean canonicalParameters = false;
    
    /** Indicate how to visualize data: as values or not. */
    private boolean asValues = false;
    
    private PolicyType policyType = PolicyType.OPTIMAL;
    
    //TODO OOPN start
    /** Indicates whether this node is an input parameter */
    private boolean isInput = false;
    //TODO OOPN end
    

    private boolean alwaysObserved=false;    

    
    /** This object contains all the information that the parser reads from 
	 *  disk that does not have a direct connection with the attributes stored 
	 *  in the <code>ProbNode</code> object. */
	public Map<String, String> additionalProperties;

	private Collection<LearningEditProposal> proposedEdits = new PriorityQueue<LearningEditProposal>();

    // Constructor
    /** @param probNet. <code>ProbNet</code>
      * @param variable. <code>Variable</code> 
      * @param nodeType. <code>NodeType</code> */
	public ProbNode(ProbNet probNet, Variable variable, NodeType nodeType) {
    	this.probNet = probNet;
    	this.variable = variable;
    	if(nodeType == NodeType.UTILITY)
    	{
    	    this.variable.setVariableType (VariableType.NUMERIC);
    	}
        this.nodeType = nodeType;
        node = new Node(probNet.getGraph(), this);
        potentials = new ArrayList<Potential>();
        additionalProperties = new HashMap<String, String>();
        
	}
	/**
	 * Copy Constructor for the GUI
	 * @param probNode
	 */
	public ProbNode(ProbNode probNode) {
    	this.probNet = probNode.getProbNet();
    	this.variable = probNode.getVariable();
        this.nodeType = probNode.getNodeType();
       // node = new Node(probNet.getGraph(), this);
        node = probNode.getNode();
       // node = new Node(probNet.getGraph(), this);
        potentials = new ArrayList<Potential>(probNode.getPotentials());
        additionalProperties = new HashMap<String, String>();
        alwaysObserved = probNode.isAlwaysObserved (); 
	}	


   //Methods
    /** @return Potentials associated to this <code>ProbNode</code> that 
     *  contains the received variable.
     *  <code>ArrayList</code> of <code>Potential</code>
     * @param variable. <code>Variable</code> */
    public List<Potential> getPotentials(Variable variable) {
        List<Potential> clonedPotentials = new ArrayList<Potential>();
        for (Potential potential : clonedPotentials) {
            if (potential.getVariables().contains(variable)) {
                clonedPotentials.add(potential);
            }
        }
        return clonedPotentials;
    }
    
    /** @return The <code>Variable</code> associated to this 
     *   <code>probNode</code>.
     * @consultation */
    public Variable getVariable() {
    	return variable;
    }
    
    
    /** @return Variable name. <code>String</code> */
    public String getName() {
    	return getVariable().getName();
    }

    /** @return Type of function. <code>UtilityCombinationFunction</code> */
    public UtilityCombinationFunction getUtilityCombinationFunction() {
		return utilityCombinationFunction;
	}

    /** @param potential. <code>Potential</code> */
    public void addPotential(Potential potential) {
        this.potentials.add(potential);
    }
    
    /** @param potential. <code>Potential</code> */
    public void setPotential(Potential potential) {
        this.potentials.clear ();
        addPotential(potential);
    }    
    /** @param potential. <code>Potential</code> */
    public void setPotentials(List<Potential> potentials) {
        this.potentials = potentials;
    }
  

    /** @param potential. <code>Potential</code>
     * @return <code>true</code> if <code>potentialList</code> contained the
     *   specified element; otherwise <code>false</code>. */
    public boolean removePotential(Potential potential) {
        return potentials.remove(potential);
    }

	/** @consultation
	 * @return <code>NodeType</code> */
	public NodeType getNodeType() {
		return nodeType;
	}

    /** @return An <code>ArrayList</code> cloned with all the potentials 
     *   associated to this <code>ProbNode</code> */
	public List<Potential> getPotentials() {
		if (potentials != null) {
			return new ArrayList<Potential> (potentials);
		}
		return null;
    }

	/** @return node. <code>Node</code> */
	public Node getNode() {
		return node;
	}
	
	/** @return Number of potentials. <code>int</code> */
	public int getNumPotentials() {
		return potentials.size();
	}

	/** @return probNet. <code>ProbNet</code> */
	public ProbNet getProbNet() {
		return probNet;
	}
	
	/** @param utilityCombinationFunction. <code>UtilityCombinationFunction</code> */
 	public void setUtilityCombinationFunction(
			UtilityCombinationFunction utilityCombinationFunction) {
		this.utilityCombinationFunction = utilityCombinationFunction;
	}
	
	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append (variable.getName() + " (");
		switch(nodeType) {
		case CHANCE:   
			out.append("Chance"); 
			break;
		case DECISION:
            out.append("Decision"); 
			break;
		case UTILITY:
            out.append("Utility"); 
			break;
		case COST:
            out.append("Utility, Cost node"); 
			break;
		case EFFECTIVENESS:
            out.append("Utility, Effectiveness node"); 
			break;
		case CE:
            out.append("Utility, Cost-Effectiveness"); 
			break;
            case SV_PRODUCT :
                break;
            case SV_SUM :
                break;
            default :
                break;
		}
		out.append("): ");
        int numParents = node.getNumParents();
        int numChildren = node.getNumChildren();
        int numSiblings = node.getNumSiblings();
        int numNeighbors = numParents + numChildren + numSiblings;
        if (numNeighbors == 0) {
        	out.append("No neighbors - ");
        } else {
	        if (numParents > 0) {
        		out.append (((numParents == 1)? "Parent" : "Parents") + ": {");        		
        		List<Node> parents = node.getParents();
	        	for (int i = 0; i < parents.size(); i++) {
	        		ProbNode probNode =(ProbNode)parents.get(i).getObject(); 
	        		out.append(probNode.getVariable());
	        		if (i < parents.size() - 1) {
	        			out.append(", ");	        		
	        		}
	        	}
	            out.append("} - ");          
	    	}
	    	if (numChildren > 0) {
	    	    out.append (((numChildren == 1)? "Child" : "Children") + ": {");              
	    	    List<Node> children = node.getChildren();
	        	for (int i = 0; i < children.size(); i++) {
	        		ProbNode probNode =(ProbNode)children.get(i).getObject(); 
	        		out.append(probNode.getVariable());
	        		if (i < children.size() - 1) {
	        			out.append(", ");	        		
	        		}
	        	}
                out.append("} - ");          
	    	}
	    	if (numSiblings > 0) {
                out.append (((numSiblings == 1)? "Sibling" : "Siblings") + ": {");              
                List<Node> siblings = node.getSiblings();
	        	for (int i = 0; i < siblings.size(); i++) {
	        		ProbNode probNode =(ProbNode)siblings.get(i).getObject(); 
                    out.append(probNode.getVariable());
	        		if (i < siblings.size() - 1) {
                        out.append(", ");                   
	        		}
	        	}
                out.append("} - ");                   
	    	}
        }
        int numPotentials = potentials.size();
		if (numPotentials > 0) {
	        out.append ((numPotentials == 1)? "Potential: " : "Potentials (" + numPotentials + "): {");
			for (int i=0; i < potentials.size (); ++i) {
				out.append(potentials.get (i).toShortString ());
                if (i < potentials.size() - 1) {
                    out.append(", ");                   
                }
			}
			if(numPotentials>1)
			{
			    out.append ("}");
			}
		} else {
			out.append ("No potentials");
		}
		return out.toString ();
	}
	
	// TODO Comentar
	public void setUniformPotential() {
		
		ArrayList<Potential> newListPotentials = new ArrayList<Potential> ();
		ArrayList<Variable> variables = new ArrayList<Variable>();
		Variable thisVariable;
        // first, this variable. The potentials is not null
		if (this.getNodeType() == NodeType.UTILITY)
			thisVariable = potentials.get( 0 ).getUtilityVariable();
		else{
			thisVariable = potentials.get( 0 ).getVariable( 0 );
			variables.add(thisVariable);
		}
		
		int numOfCellsInTable = thisVariable.getNumStates();
		double initialValue = Util.round( 1 / (new Double(numOfCellsInTable)), 
				"0.01");
		    // add now all the parents 
		
		for (Node node: getNode().getParents()) {
			//TODO Revisar, ¿Solo se agrega/elimina un padre a la vez?
			//mpalacios
			//the set of variables could be changed, so , have to be updated.
			variables.add(((ProbNode)node.getObject()).getVariable());
			numOfCellsInTable *= ((ProbNode)node.getObject()).getVariable().
			getNumStates();
		}
		// sets a new table with new columns and with all the same values
		double[] table = new double[numOfCellsInTable] ;
		for (int i=0; i<numOfCellsInTable; i++) {
			table[i] = initialValue;
		}
		// and finally, create the potential and the list of potentials
		
		// TODO Comprobar que efectivamente es un CONDITIONAL_PROBABILITY
		TablePotential tablePotential =	new TablePotential(
				variables, PotentialRole.CONDITIONAL_PROBABILITY, table);
		newListPotentials.add( tablePotential );
		
		if (this.getNodeType() == NodeType.UTILITY){
			//tablePotential.getVariables().remove(0);
			tablePotential.setUtilityVariable(thisVariable);
		}
		potentials = newListPotentials;
		
	}

	public void setUniformPotential2ProbNode() {
		
	    List<Potential> newListPotentials = new ArrayList<Potential> ();
	    List<Variable> variables = new ArrayList<Variable>();
		Variable thisVariable;
		PotentialRole role = potentials.get(0).getPotentialRole();
        // first, this variable. The potentials is not null
		if (this.getNodeType() == NodeType.UTILITY)
			thisVariable = potentials.get( 0 ).getUtilityVariable();
		else{
			thisVariable = potentials.get( 0 ).getVariable( 0 );
			variables.add(thisVariable);
		}
		
		int numOfCellsInTable = thisVariable.getNumStates();
		double initialValue = Util.round( 1 / (new Double(numOfCellsInTable)), 
				"0.01");
		    // add now all the parents 
		
		for (Node node: getNode().getParents()) {
			//TODO Revisar, ¿Solo se agrega/elimina un padre a la vez?
			//mpalacios
			//the set of variables could be changed, so , have to be updated.
			variables.add(((ProbNode)node.getObject()).getVariable());
			numOfCellsInTable *= ((ProbNode)node.getObject()).getVariable().
			getNumStates();
		}
		// sets a new table with new columns and with all the same values
		double[] table = new double[numOfCellsInTable] ;
		for (int i=0; i<numOfCellsInTable; i++) {
			table[i] = initialValue;
		}
		// and finally, create the potential and the list of potentials
		
		// TODO Comprobar que efectivamente es un CONDITIONAL_PROBABILITY
		UniformPotential uniformPotetnial = new UniformPotential(variables, role);
		
		newListPotentials.add( uniformPotetnial );
		
		if (this.getNodeType() == NodeType.UTILITY && role == PotentialRole.UTILITY){
			//tablePotential.getVariables().remove(0);
			uniformPotetnial.setUtilityVariable(thisVariable);
		}
		potentials = newListPotentials;
		
	}
	
	/** @param purpose. <code>String</code>	 */
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	/** @return <code>String</code> */
	public String getPurpose() {
		return purpose;
	}

	/** @param relevance. <code>double</code> */
	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}

	/** @return <code>double</code> */
	public double getRelevance() {
		return relevance;
	}

	/** @param comment the comment to set. <code>String</code> */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/** @return the comment. <code>String</code> */
	public String getComment() {
		return comment;
	}

	/** @param <code>canonicalParameters</code>. <code>boolean</code>
	 */
	public void setCanonicalParameters(boolean canonicalParameters) {
		this.canonicalParameters = canonicalParameters;
	}

	/** @return the canonicalParameters. <code>boolean</code> */
	public boolean isCanonicalParameters() {
		return canonicalParameters;
	}

	/** @param asValues the asValues to set. <code>boolean</code> */
	public void setAsValues(boolean asValues) {
		this.asValues = asValues;
	}

	/** @return the asValues. <code>boolean</code> */
	public boolean isAsValues() {
		return asValues;
	}

	/** @param modelType the modelType to set. <code>PolicyType</code> */
	public void setPolicyType(PolicyType policyType) {
		this.policyType = policyType;
	}

	/** @return the modelType. <code>PolicyType</code> */
	public PolicyType getPolicyType() {
		return policyType;
	}

	/** @return <code>true</code> if it is a decision node with a non uniform potential.
	 *  <code>boolean</code> */
	public boolean hasPolicy() {
		return nodeType == NodeType.DECISION &&  
				potentials.size() != 0;
	}
	
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	/** @param simulationIndexVariable. <code>Variable</code> */
	public void samplePotentials() {
		for (int i = 0; i < potentials.size(); i++) {
			Potential originalPotential = potentials.get(i);
			potentials.set(i, 
					originalPotential.sample());
		}
	}
	
	/** @param node. <code>ProbNode</code>
	 * @return True if <code>node</code> is parent of <code>this</code> node */
	public boolean isParent(ProbNode node) {
        return this.getNode ().isParent (node.getNode ());
	}
	
	
	/**
	 * @return Approximates the maximum or the minimum of the utility function of the ProbNode. It is computed recursively by using the utility function
	 * of parent nodes. If 'computeMax' is true then it computes the maximum; otherwise it computes the minimum.
	 * For an exact computation of the maximum or the minimum of the utility function then it is required to use
	 * method 'getUtilityFunction' and computes the maximum or the minimum over the resulting potential.
	 * @throws NonProjectablePotentialException 
	 */
	private double getApproximateMaxOrMinUtilityFunction(boolean computeMax)
			throws NonProjectablePotentialException {
		double result;
		List<Potential> potentials = getPotentials();

		if ((potentials != null) && (potentials.size() > 0)) {
			Potential firstPotential = potentials.get(0);
			if (!isSuperValueNode(getVariable(), getProbNet())) {
				double[] values = null;
				try {
					values = firstPotential.tableProject(null, null).get(0).values;
				} catch (WrongCriterionException e) {
					e.printStackTrace();
				}
				result = computeMax ? Tools.max(values) : Tools.min(values);
			} else {
				double parentValues[];

				List<Node> parents = getNode().getParents();
				parentValues = new double[parents.size()];
				for (int i = 0; i < parents.size(); i++) {
					parentValues[i] = ((ProbNode) (parents.get(i).getObject()))
							.getApproximateMaxOrMinUtilityFunction(computeMax);
				}
				switch (firstPotential.getPotentialType()){
				case SUM:
					result = Tools.sum(parentValues);
					break;
				case PRODUCT:
					result = Tools.multiply(parentValues);
					break;
				default:
					throw new NonProjectablePotentialException("Super-value nodes must be sum or product.");
					
				}
			}
		} else {
			result = 0.0;
		}

		return result;
	}

	/**
	 * @return Approximates the maximum of the utility function of the ProbNode. It is computed recursively by using the utility function
	 * of parent nodes. For an exact computation of the maximum of the utility function then it is required to use
	 * method 'getUtilityFunction' and computes the maximum over the resulting potential.
	 * @throws NonProjectablePotentialException 
	 */
	public double getApproximateMaximumUtilityFunction() throws NonProjectablePotentialException{
		
		return getApproximateMaxOrMinUtilityFunction(true);
	}
	
	/**
	 * @return Approximates the maximum of the utility function of the ProbNode. It is computed recursively by using the utility function
	 * of parent nodes. For an exact computation of the maximum of the utility function then it is required to use
	 * method 'getUtilityFunction' and computes the maximum over the resulting potential.
	 * @throws NonProjectablePotentialException 
	 */
	public double getApproximateMinimumUtilityFunction() throws NonProjectablePotentialException{
		
		return getApproximateMaxOrMinUtilityFunction(false);
	}
	
	
	
	/**
	 * @return The utility function of a utility variable. If it is a super-value node
     * then it operates their parent's utility functions recursively.
	 * @throws NonProjectablePotentialException 
	 */
	public TablePotential getUtilityFunction() throws
			NonProjectablePotentialException, WrongCriterionException {
		ProbNode probNode;
		TablePotential result;
		List<Potential> potentials = getPotentials();

		if ((potentials != null) && (potentials.size() > 0)) {
			Potential firstPotential = potentials.get(0);
			if (!isSuperValueNode(getVariable(), getProbNet())) {
				result = firstPotential.tableProject(null, null).get(0);
			} else {
			    List<TablePotential> utilityFunctionsParents;
				utilityFunctionsParents = new ArrayList<TablePotential>();
				for (Node node : getNode().getParents()) {
					probNode = (ProbNode) node.getObject();
					utilityFunctionsParents.add(probNode.getUtilityFunction());
				}
				switch (firstPotential.getPotentialType()) {
				case SUM:
					result = DiscretePotentialOperations
							.sum(utilityFunctionsParents);
					break;
				case PRODUCT:
					result = DiscretePotentialOperations
							.multiply(utilityFunctionsParents);
					break;
				default:
					throw new NonProjectablePotentialException(
							"Super-value nodes must be sum or product.");

				}

			}
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * @param utilityVariable the variable to test
	 * @param probNet 
	 * @return true if the variable is a supervalue node. False if does not
	 */
	public boolean isSuperValueNode(Variable utilityVariable, ProbNet probNet) {
		ProbNode utilityProbNode = probNet.getProbNode( utilityVariable );
		Node utilityNode = utilityProbNode.getNode();
		int numOfUtilityParents = 0;
		for (Node parent:utilityNode.getParents()){
			if (( (ProbNode)parent.getObject() ).getNodeType() == NodeType.UTILITY ){
				//if the node has two or more utility parents then is a super value node
				if (( numOfUtilityParents ++) >= 1 ){
				  return true;
				}
			}
			
		}
		return false;
	}
	/**
	 * This method is used to 
	 * @return a list with utility parents
	 */
	public List<ProbNode> getUtilityParents() {
	    List<ProbNode> utilityParents =  new ArrayList<>();
		for (Node parent:this.getNode().getParents()){
			if (( (ProbNode)parent.getObject() ).getNodeType() == NodeType.UTILITY ){
				utilityParents.add((ProbNode)parent.getObject());
				
			}
		}
		return utilityParents;
	 }
	/**
	 * 
	 * @return true if a node has only utility parents
	 */
	public boolean checkOnlyUtilityparents() {
		return getUtilityParents().size() == this.getNode().getParents().size() ? true: false;
	}
	/**
	 * 
	 * @return
	 */
	public boolean onlyNumericalParents() {
	    List<ProbNode> numericalParents = new ArrayList<>();
	    List<ProbNode> finiteStatesOrDiscretizedParents = new ArrayList<>();
		
		for (Node parent:this.getNode().getParents()){
			if (((ProbNode)parent.getObject()).getVariable().getVariableType() == VariableType.NUMERIC ) {
				numericalParents.add((ProbNode)parent.getObject());
			} else if (((ProbNode)parent.getObject()).getVariable().getVariableType() == VariableType.FINITE_STATES ||
					((ProbNode)parent.getObject()).getVariable().getVariableType() == VariableType.DISCRETIZED ) {
				finiteStatesOrDiscretizedParents.add((ProbNode)parent.getObject());
			}
		}
		return  ((!numericalParents.isEmpty()) && finiteStatesOrDiscretizedParents.isEmpty()) ? true: false ;
	 }
	
    /**
     * Returns the isInput.
     * @return the isInput.
     */
    public boolean isInput ()
    {
        return isInput;
    }
    /**
     * Sets the isInput.
     * @param isInput the isInput to set.
     */
    public void setInput (boolean isInput)
    {
        this.isInput = isInput;
    }
	
    /**
     * @return the alwaysObserved
     */
    public boolean isAlwaysObserved() {
        return alwaysObserved;
    }

    /**
     * @param alwaysObserved the alwaysObserved to set
     */
    public void setAlwaysObserved(boolean alwaysObserved) {
        this.alwaysObserved = alwaysObserved;
    }
    
    /**
     * Sets a new variable
     * @param newVariable
     */
    public void setVariable (Variable newVariable)
    {
        this.variable = newVariable;
        // TODO update potentials
    }
	public void setProposedEdits(Collection<LearningEditProposal> proposals) {
		this.proposedEdits = proposals;
		
	}
	
	public Collection<LearningEditProposal> getProposedEdits() {
		return this.proposedEdits;
	}
	
	public double getBestEditMotivation() {
		PriorityQueue<LearningEditProposal> editQueue = (PriorityQueue<LearningEditProposal>) getProposedEdits();
		if (editQueue == null || editQueue.isEmpty()) {
			return 0;
		}
		return ((ScoreEditMotivation) editQueue.peek().getMotivation()).getScore();
	}
    
}
