/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.action;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openmarkov.core.action.CRemoveProbNodeEdit;
import org.openmarkov.core.action.CompoundPNEdit;
import org.openmarkov.core.action.RemoveLinkEdit;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.graphic.VisualLink;
import org.openmarkov.core.gui.graphic.VisualNetwork;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.oopn.VisualInstance;
import org.openmarkov.core.gui.oopn.VisualOONetwork;
import org.openmarkov.core.gui.oopn.VisualReferenceLink;
import org.openmarkov.core.oopn.action.RemoveInstanceEdit;
import org.openmarkov.core.oopn.action.RemoveReferenceLinkEdit;

@SuppressWarnings("serial")
/**
 * Compound edit that removes selected nodes and links
 * @author Iñigo
 *
 */
public class RemoveSelectedEdit extends CompoundPNEdit
{
    private List<VisualNode>          nodesToRemove;
    private List<VisualLink>          linksToRemove;
    // TODO OOPN start
    private List<VisualInstance>      instancesToRemove;
    private List<VisualReferenceLink> referenceLinksToRemove;

    // TODO OOPN end
    /**
     * Constructor for RemoveSelectedEdit.
     * @param visualNetwork
     */
    public RemoveSelectedEdit (VisualNetwork visualNetwork)
    {
        super (visualNetwork.getNetwork ());
        this.nodesToRemove = visualNetwork.getSelectedNodes ();
        // TODO OOPN start
        if (visualNetwork instanceof VisualOONetwork)
        {
            this.instancesToRemove = ((VisualOONetwork) visualNetwork).getSelectedInstances ();
            this.referenceLinksToRemove = ((VisualOONetwork) visualNetwork).getSelectedReferenceLinks ();
        }
        // TODO OOPN end
        this.linksToRemove = union (visualNetwork.getSelectedLinks (),
                                    visualNetwork.getLinksOfNodes (this.nodesToRemove));
    }

    @Override
    public void generateEdits ()
        throws NonProjectablePotentialException,
        WrongCriterionException
    {
        for (VisualLink link : linksToRemove)
        {
            try
            {
                edits.add (new RemoveLinkEdit (
                                               probNet,
                                               probNet.getVariable (link.getSourceNode ().getProbNode ().getName ()),
                                               probNet.getVariable (link.getDestinationNode ().getProbNode ().getName ()),
                                               link.getLink ().isDirected ()));
            }
            catch (ProbNodeNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace ();
                JOptionPane.showMessageDialog (null,
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               StringDatabase.getUniqueInstance ().getString (e.getMessage ()),
                                               JOptionPane.ERROR_MESSAGE);
            }
        }
        for (VisualNode node : nodesToRemove)
        {
            edits.add (new CRemoveProbNodeEdit (probNet, node.getProbNode ()));
        }
        // TODO OOPN start
        if (instancesToRemove != null)
        {
            for (VisualInstance instance : instancesToRemove)
            {
                edits.add (new RemoveInstanceEdit (getProbNet (), instance.getName ()));
            }
        }
        if (referenceLinksToRemove != null)
        {
            for (VisualReferenceLink visualLink : referenceLinksToRemove)
            {
                edits.add (new RemoveReferenceLinkEdit (getProbNet (),
                                                        visualLink.getReferenceLink ()));
            }
        }
        // TODO OOPN end
    }

    /**
     * This method makes an union operation on two lists of links.
     * @param list1 first list.
     * @param list2 second list.
     * @return a list that is the result of an union operation of two lists of
     *         links.
     */
    private List<VisualLink> union (List<VisualLink> list1, List<VisualLink> list2)
    {
        List<VisualLink> result = new ArrayList<VisualLink> ();
        result.addAll (list1);
        for (VisualLink o : list2)
        {
            if (!result.contains (o))
            {
                result.add (o);
            }
        }
        return result;
    }
}
