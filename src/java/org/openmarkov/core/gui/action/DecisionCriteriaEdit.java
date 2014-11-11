
package org.openmarkov.core.gui.action;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.StringWithProperties;

@SuppressWarnings("serial")
public class DecisionCriteriaEdit extends SimplePNEdit
{
    private String                     criterionName;
    private StateAction                stateAction;
    // private StringsWithProperties lastAgents;
    private List<StringWithProperties> lastCriteria;
    private Object[][]                 dataTable;

    public DecisionCriteriaEdit (ProbNet probnet,
                                 StateAction stateAction,
                                 String newName,
                                 String agentName,
                                 Object[][] dataTable)
    {
        super (probnet);
        // probNet.getPNESupport().setWithUndo(true);
        this.criterionName = agentName;
        this.stateAction = stateAction;
        if (probnet.getAgents () != null)
        {
            this.lastCriteria = new ArrayList<StringWithProperties> (probnet.getDecisionCriteria ());
        }
        else
        {
            this.lastCriteria = probnet.getDecisionCriteria ();
        }
        this.dataTable = dataTable;
    }

    @Override
    public void doEdit ()
        throws DoEditException
    {
        // StringsWithProperties agents = probNet.getAgents();
        List<StringWithProperties> criteria = probNet.getDecisionCriteria ();
        StringWithProperties criterion = null;
        switch (stateAction)
        {
            case ADD :
                if (criteria == null)
                {
                    // agents = new StringsWithProperties();
                    criteria = new ArrayList<StringWithProperties> ();
                }
                criterion = new StringWithProperties (criterionName);
                // agents.put(agentName);
                criteria.add (criterion);
                probNet.setDecisionCriteria2 (criteria);
                break;
            case REMOVE :
                for (StringWithProperties criterio : criteria)
                {
                    if (criterio.getString ().equals (criterionName))
                    {
                        criterion = criterio;
                    }
                }
                criteria.remove (criterion);
                // TODO assign criteria to node
                // it is also necessary to delete this criteria from the node it
                // was assigned to
                /*
                 * if (criteria != null) { for (ProbNode node :
                 * probNet.getProbNodes()) { if
                 * (node.getVariable().getDecisionCriteria
                 * ().getString().equals(criteriaName)) {
                 * node.getVariable().setDecisionCriteria(null); } } }
                 */
                if (criteria.size () == 0)
                {
                    criteria = null;
                }
                probNet.setDecisionCriteria2 (criteria);
                break;
            case DOWN :
                // StringsWithProperties newAgentsDown = new
                // StringsWithProperties();
                ArrayList<StringWithProperties> newCriteriasDown = new ArrayList<StringWithProperties> ();
                for (int i = 0; i < dataTable.length; i++)
                {
                    // newAgentsDown.put((String)dataTable[i][0]);
                    newCriteriasDown.add (new StringWithProperties ((String) dataTable[i][0]));
                }
                probNet.setDecisionCriteria2 (newCriteriasDown);
                break;
            case UP :
                // StringsWithProperties newAgentsUp = new
                // StringsWithProperties();
                ArrayList<StringWithProperties> newCriteriasUp = new ArrayList<StringWithProperties> ();
                for (int i = 0; i < dataTable.length; i++)
                {
                    // newAgentsUp.put((String)dataTable[i][0]);
                    newCriteriasUp.add (new StringWithProperties ((String) dataTable[i][0]));
                }
                probNet.setDecisionCriteria2 (newCriteriasUp);
                break;
            case RENAME :
                // agents.rename(agentName, newName);
                // StringsWithProperties newAgentsRename = new
                // StringsWithProperties();
                ArrayList<StringWithProperties> newCriteriasRename = new ArrayList<StringWithProperties> ();
                for (int i = 0; i < dataTable.length; i++)
                {
                    // newAgentsRename.put((String)dataTable[i][0]);
                    newCriteriasRename.add (new StringWithProperties ((String) dataTable[i][0]));
                }
                probNet.setDecisionCriteria2 (newCriteriasRename);
                break;
        }
    }

    @Override
    public void undo ()
    {
        super.undo ();
        probNet.setDecisionCriteria2 (lastCriteria);
        // TODO restore criteria in nodes
    }
}
