/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.io.probmodel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.openmarkov.core.exception.NoFindingException;
import org.openmarkov.core.exception.WriterException;
import org.openmarkov.core.io.ProbNetWriter;
import org.openmarkov.core.io.format.annotation.FormatType;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.AdditionalProperties;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.StringWithProperties;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionType;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.CycleLengthShift;
import org.openmarkov.core.model.network.potential.DeltaPotential;
import org.openmarkov.core.model.network.potential.ExponentialHazardPotential;
import org.openmarkov.core.model.network.potential.ExponentialPotential;
import org.openmarkov.core.model.network.potential.LinearRegressionPotential;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.ProductPotential;
import org.openmarkov.core.model.network.potential.RegressionPotential;
import org.openmarkov.core.model.network.potential.SameAsPrevious;
import org.openmarkov.core.model.network.potential.SumPotential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.model.network.potential.WeibullHazardPotential;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.core.model.network.potential.canonical.MaxPotential;
import org.openmarkov.core.model.network.potential.canonical.MinPotential;
import org.openmarkov.core.model.network.potential.canonical.TuningPotential;
import org.openmarkov.core.model.network.potential.plugin.RelationPotentialType;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.core.model.network.type.plugin.NetworkTypeManager;
import org.openmarkov.core.oopn.Instance;
import org.openmarkov.core.oopn.InstanceReferenceLink;
import org.openmarkov.core.oopn.NodeReferenceLink;
import org.openmarkov.core.oopn.OOPNet;
import org.openmarkov.core.oopn.ReferenceLink;
import org.openmarkov.io.probmodel.strings.XMLAttributes;
import org.openmarkov.io.probmodel.strings.XMLTags;
import org.openmarkov.io.probmodel.strings.XMLValues;

/**
 * @author mkpalacio
 * @author marias
 * @version 1.0
 */
@FormatType(name = "PGMXWriter", extension = "pgmx", description = "OpenMarkov", role = "Writer")
public class PGMXWriter implements ProbNetWriter {

    // Attributes
    /** The version format */
    protected static String FORMAT_VERSION_NUMBER = "0.2.0";

    /**
     * @param netName
     *            = path + network name + extension. <code>String</code>
     * @param probNet
     *            . <code>ProbNet</code>
     */
    public void writeProbNet(String netName, ProbNet probNet) throws WriterException {
        writeProbNet(netName, probNet, null);
    }

    /**
     * @param netName
     *            = path + network name + extension.
     * @param probNet
     *            . <code>ProbNet</code> <code>String</code>
     * @param evidences
     *            list of evidence cases. <code>ArrayList</code> of
     *            <code>EvidenceCase</code>
     */
    public void writeProbNet(String netName, ProbNet probNet, List<EvidenceCase> evidences)
            throws WriterException {
        if (probNet != null) {
            // PrintWriter out = new PrintWriter(new FileOutputStream(netName));
            Element root = new Element("ProbModelXML");
            root.setAttribute(XMLAttributes.FORMAT_VERSION.toString(),
                    FORMAT_VERSION_NUMBER.toString());
            try {
                writeXMLProbNet(probNet, root);
                writeEvidence(probNet, evidences, root);
                writePolicies(probNet, root);
                Document document = new Document(root);
                XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
                FileOutputStream out;
                try {
                    out = new FileOutputStream(netName);
                } catch (FileNotFoundException e) {
                    throw new WriterException("Can not create: " + netName + " file.");
                }
                try {
                    xmlOutputter.output(document, out);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    throw new WriterException("General Input/Output error writing: " + netName
                            + ".");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new WriterException(e.getMessage());
            }
        }
    }

    /**
     * Removes from <code>evidence</code> the variables that are no longer
     * present in <code>probNet</code>
     * 
     * @param probNet
     *            . <code>ProbNet</code>
     * @param evidence
     *            . <code>EvidenceCase</code>
     */
    private void removeMissingVariablesFromEvidence(ProbNet probNet, List<EvidenceCase> evidence) {
        HashSet<Variable> probNetVariables = new HashSet<Variable>(probNet.getVariables());
        for (EvidenceCase evidenceCase : evidence) {
            List<Variable> evidenceVariables = evidenceCase.getVariables();
            for (Variable variable : evidenceVariables) {
                if (!probNetVariables.contains(variable)) {
                    try {
                        evidenceCase.removeFinding(variable.getName());
                    } catch (NoFindingException e) {
                        // Unreachable code
                    }
                }
            }
        }
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @param root
     *            . <code>Element</code>
     * @throws Exception. <code>Exception</code>
     */
    protected void writeXMLProbNet(ProbNet probNet, Element root) {
        Element probNetElement = new Element("ProbNet");
        probNetElement.setAttribute(XMLAttributes.TYPE.toString(), getXMLNetworkType(probNet));
        getProbNetChildren(probNet, probNetElement);
        root.addContent(probNetElement);
    }

    /**
     * Writes evidence nodes into XML format
     * 
     * @param evidence
     *            . <code>ArrayList</code> of <code>EvidenceCase</code>
     * @param root
     *            . <code>Element</code>
     */
    protected void writeEvidence(ProbNet probNet, List<EvidenceCase> evidence, Element root) {
        if (evidence != null && !evidence.isEmpty()) {
            removeMissingVariablesFromEvidence(probNet, evidence);
            Element evidenceElement = new Element(XMLTags.EVIDENCE.toString());
            for (EvidenceCase evidenceCase : evidence) {
                Element evidenceCaseElement = new Element(XMLTags.EVIDENCE_CASE.toString());
                for (Finding finding : evidenceCase.getFindings()) {
                    Element findingElement = new Element(XMLTags.EVIDENCE_CASE.toString());
                    findingElement.setAttribute("variable", finding.getVariable().getName());
                    if (finding.getVariable().getVariableType() == VariableType.DISCRETIZED
                            || finding.getVariable().getVariableType() == VariableType.FINITE_STATES) {
                        findingElement.setAttribute("state", finding.getState());
                    } else {
                        findingElement.setAttribute("numericValue",
                                new Double(finding.getNumericalValue()).toString());
                    }
                    evidenceCaseElement.addContent(findingElement);
                }
                evidenceElement.addContent(evidenceCaseElement);

            }
            root.addContent(evidenceElement);
        }
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @param root
     *            . <code>Element</code>
     */
    protected void writePolicies(ProbNet probNet, Element root) {
        Element policiesElement = new Element(XMLTags.POLICIES.toString());
        boolean existsAtLeastOnePolicy = false;
        List<Variable> decisionVariables = probNet.getVariables(NodeType.DECISION);
        for (Variable variable : decisionVariables) {
            ProbNode probNode = probNet.getProbNode(variable);
            if (probNode.hasPolicy()) {
                existsAtLeastOnePolicy = true;
                List<Potential> decisionPotentials = probNode.getPotentials();
                for (Potential decisionPotential : decisionPotentials) {
                    Element potentialElement = new Element(XMLTags.POTENTIAL.toString());
                    getPotential(probNet, decisionPotential, potentialElement);
                    potentialElement.setAttribute(XMLAttributes.ROLE.toString(),
                            decisionPotential.getPotentialRole().toString());
                    policiesElement.addContent(potentialElement);
                }
            }
        }
        if (existsAtLeastOnePolicy) {
            root.addContent(policiesElement);
        }
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @param probNetElement
     *            . <code>Element</code>
     * @throws Exception
     */
    protected void getProbNetChildren(ProbNet probNet, Element probNetElement) {
        getAdditionalConstraints(probNet, probNetElement, new Element(
                XMLTags.ADDITIONAL_CONSTRAINTS.toString()));
        getProbNetComment(probNet, probNetElement, new Element(XMLTags.COMMENT.toString()));
        getLanguage(probNet, probNetElement, new Element(XMLTags.LANGUAGE.toString()));
        getVariables(probNet, probNetElement, new Element(XMLTags.VARIABLES.toString()));
        getLinks(probNet, probNetElement, new Element(XMLTags.LINKS.toString()));
        getPotentials(probNet, probNetElement, new Element(XMLTags.POTENTIALS.toString()));
        getAgents(probNet, probNetElement, new Element(XMLTags.AGENTS.toString()));
        getDecisionCriteria(probNet, probNetElement,
                new Element(XMLTags.DECISION_CRITERIA.toString()));
        getAdditionalProperties(probNet, probNetElement,
                new Element(XMLTags.ADDITIONAL_PROPERTIES.toString()));
        // OOPN start
        if (probNet instanceof OOPNet)
            getOOPN((OOPNet) probNet, probNetElement, new Element(XMLTags.OOPN.toString()));
        // OOPN end
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @param probNetElement
     *            . <code>Element</code>
     * @param additionalPropertiesElement
     *            . <code>AdditionalProperties</code>
     */
    protected void getAdditionalProperties(ProbNet probNet, Element probNetElement,
            Element additionalPropertiesElement) {
        for (String propertyName : probNet.additionalProperties.keySet()) {
            if (probNet.additionalProperties.get(propertyName) != null) {
                Element propertyElement = new Element(XMLTags.PROPERTY.toString());
                propertyElement.setAttribute(XMLAttributes.NAME.toString(), propertyName);
                propertyElement.setAttribute(XMLAttributes.VALUE.toString(),
                        probNet.additionalProperties.get(propertyName).toString());
                additionalPropertiesElement.addContent(propertyElement);
            }
        }
        probNetElement.addContent(additionalPropertiesElement);
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @param probNetElement
     *            . <code>Element</code>
     * @param agentsElement
     *            . <code>Element</code>
     */
    protected void getAgents(ProbNet probNet, Element probNetElement, Element agentsElement) {
        List<StringWithProperties> agents = probNet.getAgents();
        if (agents != null && !agents.isEmpty()) {
            for (int i = 0; i < agents.size(); i++) {
                getAgent(agentsElement, new Element(XMLTags.AGENT.toString()), agents.get(i)
                        .getString(), agents.get(i).getAdditionalProperties());
            }

            /*
             * for (String agentName : agents.getString()) {
             * getAgent(agentsElement, new Element(XMLTags.AGENT.toString()),
             * agentName, agents.getProperties(agentName)); }
             */
            probNetElement.addContent(agentsElement);
        }
    }

    /**
     * @param agentsElement
     *            . <code>Element</code>
     * @param agentElement
     *            . <code>Element</code>
     * @param agentName
     *            . <code>String</code>
     * @param properties
     *            . <code>AdditionalProperties</code>
     */
    protected void getAgent(Element agentsElement, Element agentElement, String agentName,
            AdditionalProperties properties) {
        agentElement.setAttribute(XMLAttributes.NAME.toString(), agentName);
        if (properties != null && properties.size() > 0) {
            Element additionalPropertiesElement = getAdditionalPropertiesElement(properties);
            agentElement.addContent(additionalPropertiesElement);
        }
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @param probNetElement
     *            . <code>Element</code>
     * @param decisionCriteriaElement
     *            . <code>Element</code>
     */
    protected void getDecisionCriteria(ProbNet probNet, Element probNetElement,
            Element decisionCriteriaElement) {
        List<StringWithProperties> decisionCritera = probNet.getDecisionCriteria();
        if (decisionCritera != null && !decisionCritera.isEmpty()) {

            // for (String criterionName : decisionCritera.getNames()) {
            for (int i = 0; i < decisionCritera.size(); i++) {
                // getCriteria(decisionCriteriaElement, new
                // Element(XMLTags.CRITERION.toString()),
                // criterionName, decisionCritera.getProperties(criterionName));

                getCriteria(decisionCriteriaElement, new Element(XMLTags.CRITERION.toString()),
                        decisionCritera.get(i).getString(), decisionCritera.get(i)
                                .getAdditionalProperties());

            }
            probNetElement.addContent(decisionCriteriaElement);
        }
    }

    /**
     * @param decisionCriteriaElement
     *            . <code>Element</code>
     * @param criterionElement
     *            . <code>Element</code>
     * @param criterionName
     *            . <code>String</code>
     * @param properties
     *            . <code>AdditionalProperties</code>
     */
    protected void getCriteria(Element decisionCriteriaElement, Element criterionElement,
            String criterionName, AdditionalProperties properties) {
        criterionElement.setAttribute(XMLAttributes.NAME.toString(), criterionName);
        if (properties != null && properties.size() > 0) {
            Element additionalPropertiesElement = getAdditionalPropertiesElement(properties);
            criterionElement.addContent(additionalPropertiesElement);
        }
        decisionCriteriaElement.addContent(criterionElement);
    }

    /**
     * @param properties
     *            . <code>AdditionalProperties</code>
     * @return. <code>Element</code>
     */
    protected Element getAdditionalPropertiesElement(AdditionalProperties properties) {
        Element additionalPropertiesElement = new Element(XMLTags.ADDITIONAL_PROPERTIES.toString());
        for (String propertyName : properties.getKeySet()) {
            Element propertyElement = new Element(XMLTags.PROPERTY.toString());
            propertyElement.setAttribute(XMLAttributes.NAME.toString(), propertyName);
            propertyElement.setAttribute(XMLAttributes.VALUE.toString(),
                    properties.get(propertyName).toString());
            additionalPropertiesElement.addContent(propertyElement);
        }
        return additionalPropertiesElement;
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @param probNetElement
     *            . <code>Element</code>
     * @param constraintsElement
     *            . <code>Element</code>
     */
    protected void getAdditionalConstraints(ProbNet probNet, Element probNetElement,
            Element constraintsElement) {
        List<PNConstraint> constraints = probNet.getAdditionalConstraints();
        NetworkType networkType = probNet.getNetworkType();
        if (constraints.size() > 1) {
            for (int i = 1; i < constraints.size(); i++) {
                // TODO To implement the getArguments method in constraints
                /*
                 * if (constraints.get(i).getArguments() != null){ close =
                 * false; }
                 */
                PNConstraint constraint = constraints.get(i);
                if (!networkType.isApplicableConstraint(constraint)) {
                    constraintsElement.addContent(new Element(XMLTags.CONSTRAINT.toString())
                            .setAttribute(XMLAttributes.NAME.toString(), constraint.toString()));
                }
                // To be extended here when the arguments of the restrictions
                // are available
                // TODO Eliminar XMLBasicConstraints y XMLCompoundConstraints
                // TODO revisar que el toString de cada constraint sea correcto
            }
            probNetElement.addContent(constraintsElement);
        }
    }

    protected void getProbNetComment(ProbNet probNet, Element probNetElement, Element commentElement) {
        if (!probNet.getComment().isEmpty()) {
            CDATA cdata = new CDATA(probNet.getComment());
            /*
             * probNetElement.addContent( commentElement.setText( probNet.
             * getComment() ) );
             */
            probNetElement.addContent(commentElement.addContent(cdata));
        }
    }

    /**
     * @param probNet
     * @param probNetElement
     * @param languageElement
     */
    protected void getLanguage(ProbNet probNet, Element probNetElement, Element languageElement) {
        if (probNet.additionalProperties.get(XMLTags.LANGUAGE.toString()) != null)
            probNetElement.addContent(languageElement.setText(probNet.additionalProperties.get(
                    XMLTags.LANGUAGE.toString()).toString()));
    }

    /**
     * @param probNet
     * @param probNetElement
     * @param variablesElement
     */
    protected void getVariables(ProbNet probNet, Element probNetElement, Element variablesElement) {
        if (probNet.getNumNodes() > 0) {
            for (ProbNode probNode : probNet.getProbNodes()) {
                getVariable(variablesElement, new Element(XMLTags.VARIABLE.toString()), probNode);
            }
            probNetElement.addContent(variablesElement);
        }
    }

    /**
     * @param variablesElement
     * @param variableElement
     * @param probNode
     */
    protected void getVariable(Element variablesElement, Element variableElement, ProbNode probNode) {
        writeVariableName(probNode.getVariable(), variableElement);
        String variableType = probNode.getVariable().getVariableType().toString();
        variableElement.setAttribute(XMLAttributes.TYPE.toString(), variableType);
        String nodeType = probNode.getNodeType().toString();
        variableElement.setAttribute(XMLAttributes.ROLE.toString(), nodeType);
        // OOPN start
        String isInput = String.valueOf(probNode.isInput());
        variableElement.setAttribute(XMLAttributes.IS_INPUT.toString(), isInput);
        // OOPN end
        getVariableChildren(variableElement, probNode);
        variablesElement.addContent(variableElement);
    }

    /**
     * @param variableElement
     * @param probNode
     */
    protected void getVariableChildren(Element variableElement, ProbNode probNode) {
        getCommment(variableElement, probNode);
        // TODO verificar que las coordenadas sean validas no null
        getCoordinates(variableElement, probNode);
        getAdditionalProperties(variableElement,
                new Element(XMLTags.ADDITIONAL_PROPERTIES.toString()), probNode);
        String unit = probNode.getVariable().getUnit().getString();
        if (unit != null)
        {
            Element unitElement = new Element(XMLTags.UNIT.toString());
            unitElement.setText(String.valueOf(unit));
            variableElement.addContent(unitElement);
        }
        getAlwaysObservedAttribute(variableElement, probNode);
        Element precisionElement = new Element(XMLTags.PRECISION.toString());
        double precision = probNode.getVariable().getPrecision();
        precisionElement = precisionElement.setText(String.valueOf(precision));
        variableElement.addContent(precisionElement);
        NodeType nodeType = probNode.getNodeType();
        if (nodeType == NodeType.UTILITY) {
            Element decisionCriteriaElement = new Element(XMLTags.DECISION_CRITERIA.toString());
            getDecisionCriteria(variableElement, decisionCriteriaElement, probNode);
        }
        // states reading
        switch (probNode.getVariable().getVariableType()) {
        case NUMERIC:
        case DISCRETIZED:
            if (probNode.getNodeType() != NodeType.UTILITY)
            {
                getThresholds(variableElement, new Element(XMLTags.THRESHOLDS.toString()), probNode
                        .getVariable().getPartitionedInterval());
            }
        case FINITE_STATES:
            getStates(variableElement, new Element(XMLTags.STATES.toString()), probNode);
        }
    }

    protected void getThresholds(Element variableElement, Element thresholdsElement,
            PartitionedInterval partitionedInterval) {
        if (partitionedInterval.getLimits().length > 0) {
            int i = 0;
            for (double limit : partitionedInterval.getLimits()) {
                Element thresholdElement = new Element(XMLTags.THRESHOLD.toString());
                thresholdElement
                        .setAttribute(XMLAttributes.VALUE.toString(), String.valueOf(limit));
                thresholdElement.setAttribute(XMLAttributes.BELONGS_TO.toString(),
                        partitionedInterval.getBelongsTo(i));
                thresholdsElement.addContent(thresholdElement);
                i++;
            }
            variableElement.addContent(thresholdsElement);
        }
    }

    protected void getStates(Element variableElement, Element statesElement, ProbNode probNode) {
        // TODO revisar el caso para variables numéricas
        for (State singleState : probNode.getVariable().getStates()) {
            statesElement.addContent(new Element(XMLTags.STATE.toString()).setAttribute(
                    XMLAttributes.NAME.toString(), singleState.getName()));
        }
        variableElement.addContent(statesElement);
    }

    protected void getDecisionCriteria(Element variableElement, Element decisionCriteria,
            ProbNode probNode) {
        if (probNode.getVariable().getDecisionCriteria() != null) {
            decisionCriteria.setAttribute(XMLAttributes.NAME.toString(), probNode.getVariable()
                    .getDecisionCriteria().getString());
            if (probNode.getVariable().getDecisionCriteria().getAdditionalProperties() != null
                    && probNode.getVariable().getDecisionCriteria().getAdditionalProperties()
                            .size() > 0) {
                Element additionalProperties = new Element(XMLTags.ADDITIONAL_PROPERTIES.toString());
                decisionCriteria.addContent(additionalProperties);
            }
            variableElement.addContent(decisionCriteria);
        }
    }

    protected void getAdditionalProperties(Element variableElement, Element additionalElement,
            ProbNode probNode) {
        if (!probNode.getPurpose().isEmpty()) {
            Element propertyElement = new Element(XMLTags.PROPERTY.toString());
            propertyElement.setAttribute(XMLAttributes.NAME.toString(), XMLTags.PURPOSE.toString());
            propertyElement.setAttribute(XMLAttributes.VALUE.toString(), probNode.getPurpose());
            additionalElement.addContent(propertyElement);
        }
        if (probNode.getRelevance() != ProbNode.defaultRelevance) {
            Element propertyElement = new Element(XMLTags.PROPERTY.toString());
            propertyElement.setAttribute(XMLAttributes.NAME.toString(),
                    XMLTags.RELEVANCE.toString());
            propertyElement.setAttribute(XMLAttributes.VALUE.toString(),
                    String.valueOf(probNode.getRelevance()));
            additionalElement.addContent(propertyElement);
        }
        for (String propertyKey : probNode.additionalProperties.keySet()) {
            String propertyValue = probNode.additionalProperties.get(propertyKey);
            Element propertyElement = new Element(XMLTags.PROPERTY.toString());
            propertyElement.setAttribute(XMLAttributes.NAME.toString(), propertyKey);
            propertyElement.setAttribute(XMLAttributes.VALUE.toString(), propertyValue);
            additionalElement.addContent(propertyElement);
        }
        if (additionalElement.getChildren().size() > 0)
            variableElement.addContent(additionalElement);
    }

    protected void getCoordinates(Element variableElement, ProbNode probNode) {
        Element coordinatesElement = new Element(XMLTags.COORDINATES.toString());
        coordinatesElement.setAttribute(XMLAttributes.X.toString(),
                String.valueOf(new Double(probNode.getNode().getCoordinateX()).intValue()));
        coordinatesElement.setAttribute(XMLAttributes.Y.toString(),
                String.valueOf(new Double(probNode.getNode().getCoordinateY()).intValue()));
        variableElement.addContent(coordinatesElement);
    }

    protected void getCommment(Element variableElement, ProbNode probNode) {
        if (!probNode.getComment().isEmpty()) {
            CDATA cdata = new CDATA(probNode.getComment());
            variableElement.addContent(new Element(XMLTags.COMMENT.toString()).setContent(cdata));
        }
    }

    protected void getAlwaysObservedAttribute(Element variableElement, ProbNode probNode) {
        if (probNode.isAlwaysObserved()) {
            variableElement.addContent(new Element(XMLTags.ALWAYS_OBSERVED.toString()));
        }
    }

    protected void getLinks(ProbNet probNet, Element probNetElement, Element linksElement) {
        if (probNet.getGraph().getLinks().size() > 0) {
            for (Link link : probNet.getGraph().getLinks()) {
                Element linkElement = new Element(XMLTags.LINK.toString());
                Element variableElement1 = new Element(XMLTags.VARIABLE.toString());
                Variable variable1 = ((ProbNode) link.getNode1().getObject()).getVariable();
                writeVariableName(variable1, variableElement1);
                Element variableElement2 = new Element(XMLTags.VARIABLE.toString());
                Variable variable2 = ((ProbNode) link.getNode2().getObject()).getVariable();
                writeVariableName(variable2, variableElement2);
                linkElement.addContent(variableElement1);
                linkElement.addContent(variableElement2);
                /*
                 * linkElement.setAttribute( XMLAttributes.VAR1.toString(), ( (
                 * ProbNode )link.getNode1().getObject() ).getName() );
                 * linkElement.setAttribute( XMLAttributes.VAR2.toString(), ( (
                 * ProbNode )link.getNode2().getObject() ).getName() );
                 */
                linkElement.setAttribute(XMLAttributes.DIRECTED.toString(),
                        String.valueOf(link.isDirected()));
                if (link.hasRestrictions()) {
                    getLinkRestriction(link, linkElement);
                }
                if (link.hasRevealingConditions()) {
                    getRevelationConditions(link, linkElement);
                }
                // linkElement.addContent(varaibleElement)
                // TODO Write comment
                // TODO Write label
                // TODO Write additional additionalProperties
                linksElement.addContent(linkElement);
            }
            probNetElement.addContent(linksElement);
        }
    }

    /****
     * Writes the revelation conditions for the link.
     * 
     * @param link
     * @param linkElement
     */
    protected void getRevelationConditions(Link link, Element linkElement) {
        ProbNode node = (ProbNode) link.getNode1().getObject();
        VariableType varType = node.getVariable().getVariableType();
        Element revelationConditions = new Element(XMLTags.REVELATION_CONDITIONS.toString());
        if (varType == VariableType.NUMERIC) {
            List<PartitionedInterval> intervals = link.getRevealingIntervals();
            for (PartitionedInterval partitionedInterval : intervals) {
                if (partitionedInterval.getLimits().length > 0) {
                    int i = 0;
                    for (double limit : partitionedInterval.getLimits()) {
                        Element thresholdElement = new Element(XMLTags.THRESHOLD.toString());
                        thresholdElement.setAttribute(XMLAttributes.VALUE.toString(),
                                String.valueOf(limit));
                        thresholdElement.setAttribute(XMLAttributes.BELONGS_TO.toString(),
                                partitionedInterval.getBelongsTo(i));
                        revelationConditions.addContent(thresholdElement);
                        i++;
                    }
                }
            }
        } else {

            List<State> states = link.getRevealingStates();
            for (State state : states) {
                Element stateElement = new Element(XMLTags.STATE.toString());
                stateElement.setAttribute(XMLAttributes.NAME.toString(), state.getName());
                revelationConditions.addContent(stateElement);
            }
        }
        linkElement.addContent(revelationConditions);
    }

    /*****
     * Writes the link restriction
     * 
     * @param link
     * @param linkElement
     */
    protected void getLinkRestriction(Link link, Element linkElement) {
        double[] table = ((TablePotential) link.getRestrictionsPotential()).values;

        boolean hasRestriction = false;
        for (int i = 0; i < table.length; i++) {
            if (table[i] == 0.0) {
                hasRestriction = true;
            }
        }
        if (hasRestriction) {
            Potential potential = link.getRestrictionsPotential();
            Element restrictionPotential = new Element(XMLTags.POTENTIAL.toString());
            PotentialType potentialType = getPotentialType(link.getRestrictionsPotential());
            PotentialRole potentialRole = PotentialRole.LINK_RESTRICTION;
            restrictionPotential.setAttribute(XMLAttributes.TYPE.toString(),
                    potentialType.toString());
            restrictionPotential.setAttribute(XMLAttributes.ROLE.toString(),
                    potentialRole.toString());
            Element variables = new Element(XMLTags.VARIABLES.toString());
            Element variable1 = new Element(XMLTags.VARIABLE.toString());
            variable1.setAttribute(XMLAttributes.NAME.toString(),
                    potential.getVariable(0).getName());
            Element variable2 = new Element(XMLTags.VARIABLE.toString());
            variable2.setAttribute(XMLAttributes.NAME.toString(),
                    potential.getVariable(1).getName());
            variables.addContent(variable1);
            variables.addContent(variable2);
            Element valuesElement = new Element(XMLTags.VALUES.toString()).setText(getString(((TablePotential) link.getRestrictionsPotential()).values));
            restrictionPotential.addContent(variables);
            restrictionPotential.addContent(valuesElement);
            linkElement.addContent(restrictionPotential);
        }
    }

    /**
     * @param probNet
     * @param probNetElement
     * @param potentialsElement
     */
    protected void getPotentials(ProbNet probNet, Element probNetElement, Element potentialsElement) {
        // HashMap of declared TablePotentials
        List<Potential> potentials = probNet.getPotentials();
        for (Potential potential : potentials) {
            Variable potentialVariable = null;
            if (potential.getVariables().isEmpty()
                    || potential.getPotentialRole() == PotentialRole.UTILITY) {
                potentialVariable = potential.getUtilityVariable();
            } else {
                potentialVariable = potential.getVariable(0);
            }
            // Do not write here policies
            if ((probNet.getProbNode(potentialVariable).getNodeType() != NodeType.DECISION)
                    && (potential.getPotentialRole() != PotentialRole.POLICY)) {
                Element potentialElement = new Element(XMLTags.POTENTIAL.toString());
                getPotential(probNet, potential, potentialElement);
                PotentialRole potentialRole = potential.getPotentialRole();
                potentialElement.setAttribute(XMLAttributes.ROLE.toString(), potentialRole.toString());
                potentialsElement.addContent(potentialElement);
            }
        }
        probNetElement.addContent(potentialsElement);
    }

    /**
     * @param potential
     * @param potentialsElement
     * @param potentialElement
     * @param mapPotentialRefs
     */
    protected void getPotential(ProbNet probNet,
            Potential potential,
            Element potentialElement) {
        PotentialType potentialType = getPotentialType(potential);
        potentialElement.setAttribute(XMLAttributes.TYPE.toString(), potentialType.toString());
        
        // TODO add function attribute
        // add comment child
        if(potential.getComment() != null && !potential.getComment().isEmpty())
        {
            Element commentElement = new Element(XMLTags.COMMENT.toString());
            commentElement.setText(potential.getComment());
            potentialElement.addContent(commentElement);
        }
        
        // TODO add aditionalProperties child
        if (potential.getPotentialRole() == PotentialRole.UTILITY) {
            Variable utilityVariable = potential.getUtilityVariable();
            // it could be null in Branches potentials
            if (utilityVariable != null) {
                Element utilityElement = new Element(XMLTags.UTILITY_VARIABLE.toString());
                writeVariableName(utilityVariable, utilityElement);
                potentialElement.addContent(utilityElement);
            }
        }
        List<Variable> potentialVariables = potential.getVariables();
        if (!potentialVariables.isEmpty()) {
            writePotentialVariables(potentialVariables, potentialElement);
        }
        
        Element valuesElement = null;
        switch (potentialType) {
        case TABLE:
            valuesElement = new Element(XMLTags.VALUES.toString());
            valuesElement.setText(getString(((TablePotential) potential).values));
            // Write table values to the XML file
            potentialElement.addContent(valuesElement);
            if (((TablePotential) potential).getUncertaintyTable() != null) {
                Element uncertainValuesElement = getUncertainValuesElement(potential);
                potentialElement.addContent(uncertainValuesElement);
            }
            break;
        case TREE_ADD:
            TreeADDPotential treePotential = (TreeADDPotential) potential;
            // Get the root node of the TreeADD
            Variable topVariable = treePotential.getRootVariable();
            // union of intervals must cover the whole range of the
            // numeric variable
            // Write the variable of the root node as the top variable of
            // the potential
            Element topVarElement = new Element(XMLTags.TOP_VARIABLE.toString());
            writeVariableName(topVariable, topVarElement);
            potentialElement.addContent(topVarElement);
            // Branches of the topVariable
            Element branchesElement = new Element(XMLTags.BRANCHES.toString());
            for (TreeADDBranch branch : treePotential.getBranches()) {
                // Recursive writing of every branch in the treeADD structure
                branchesElement.addContent(getTreeADDBranch(branch, topVariable, probNet));
            }
            // Write var names of the table potential to the XML file
            potentialElement.addContent(branchesElement);
            break;
        case ICIMODEL:
            ICIPotential iciPotential = (ICIPotential) potential;
            // Model Element
            Element modelElement = new Element(XMLTags.MODEL.toString());
            modelElement.setText(iciPotential.getClass().getAnnotation(RelationPotentialType.class)
                    .name());
            potentialElement.addContent(modelElement);
            // Variables element
            Element varsElement = new Element(XMLTags.VARIABLES.toString());
            for (Variable variable : iciPotential.getVariables()) {
                Element variableElement = new Element(XMLTags.VARIABLE.toString());
                variableElement.setAttribute("name", variable.getName());
                varsElement.addContent(variableElement);
            }
            potentialElement.addContent(varsElement);
            // Subpotentials element
            Element subpotentialsElement = new Element(XMLTags.SUBPOTENTIALS.toString());
            Variable conditionedVariable = iciPotential.getVariables().get(0);
            // Noisy parameters
            for (int i = 1; i < iciPotential.getNumVariables(); ++i) {
                Variable parentVariable = iciPotential.getVariables().get(i);
                Element potentialChildElement = new Element(XMLTags.POTENTIAL.toString());
                potentialChildElement.setAttribute("type", "Table");
                Element variablesElement = new Element(XMLTags.VARIABLES.toString());
                Element conditionedVariableElement = new Element(XMLTags.VARIABLE.toString());
                conditionedVariableElement.setAttribute("name", conditionedVariable.getName());
                Element parentVariableElement = new Element(XMLTags.VARIABLE.toString());
                parentVariableElement.setAttribute("name", parentVariable.getName());
                variablesElement.addContent(conditionedVariableElement);
                variablesElement.addContent(parentVariableElement);
                Element parameterValuesElement = new Element(XMLTags.VALUES.toString());
                parameterValuesElement.setText(getString(iciPotential
                        .getNoisyParameters(parentVariable)));
                potentialChildElement.addContent(variablesElement);
                potentialChildElement.addContent(parameterValuesElement);
                subpotentialsElement.addContent(potentialChildElement);
            }
            // Leaky parameters
            Element potentialChildElement = new Element(XMLTags.POTENTIAL.toString());
            potentialChildElement.setAttribute("type", "Table");
            Element variablesElement = new Element(XMLTags.VARIABLES.toString());
            Element conditionedVariableElement = new Element(XMLTags.VARIABLE.toString());
            conditionedVariableElement.setAttribute("name", conditionedVariable.getName());
            variablesElement.addContent(conditionedVariableElement);
            Element parameterValuesElement = new Element(XMLTags.VALUES.toString());
            parameterValuesElement.setText(getString(iciPotential.getLeakyParameters()));
            potentialChildElement.addContent(variablesElement);
            potentialChildElement.addContent(parameterValuesElement);
            subpotentialsElement.addContent(potentialChildElement);
            potentialElement.addContent(subpotentialsElement);
            break;
        case WEIBULL_HAZARD:
            WeibullHazardPotential weibullPotential = (WeibullHazardPotential) potential;
            Variable timeVariable = weibullPotential.getTimeVariable();
            if(timeVariable != null)
            {
                Element timeVariableElement = new Element(XMLTags.TIME_VARIABLE.toString());
                timeVariableElement.setAttribute(XMLAttributes.NAME.toString(), timeVariable.getBaseName() + "");
                timeVariableElement.setAttribute(XMLAttributes.TIMESLICE.toString(), timeVariable.getTimeSlice() + "");
                potentialElement.addContent(timeVariableElement);
            }
            getRegressionPotential(potentialElement, weibullPotential);
            break;
        case EXPONENTIAL_HAZARD:
            ExponentialHazardPotential exponentialHazardPotential = (ExponentialHazardPotential) potential;
            getRegressionPotential(potentialElement, exponentialHazardPotential);
            break;
        case LINEAR_REGRESSION:
            LinearRegressionPotential linearRegressionPotential = (LinearRegressionPotential) potential;
            getRegressionPotential(potentialElement, linearRegressionPotential);
            break;
        case EXPONENTIAL:
            ExponentialPotential exponentialPotential = (ExponentialPotential) potential;
            getRegressionPotential(potentialElement, exponentialPotential);
            break;            
        case DELTA:
            DeltaPotential deltaPotential = (DeltaPotential)potential;
            if(deltaPotential.getConditionedVariable().getVariableType() == VariableType.NUMERIC)
            {
                Element numericValueElement = new Element(XMLTags.NUMERIC_VALUE.toString());
                numericValueElement.setText( String.valueOf(deltaPotential.getNumericValue()));
                potentialElement.addContent(numericValueElement);
            }else
            {
                Element stateElement = new Element(XMLTags.STATE.toString());
                stateElement.setText(deltaPotential.getState().getName());
                potentialElement.addContent(stateElement);
            }
            break;
        default:
            break;
        }
    }

    private Element getCoefficientsElement(double[] coefficients) {
        Element coefficientsElement = new Element(XMLTags.COEFFICIENTS.toString());
        coefficientsElement.setText(getString(coefficients));
        return coefficientsElement;
    }
    
    private Element getCovariatesElement(String[] covariates) {
        Element covariatesElement = new Element(XMLTags.COVARIATES.toString());
        for (String covariate : covariates) {
            Element covariateElement = new Element(XMLTags.COVARIATE.toString());
            covariateElement.setText(covariate);
            covariatesElement.addContent(covariateElement);
        }
        return covariatesElement;
    }

    private Element getCovarianceMatrixElement(double[] covarianceMatrix) {
        Element covarianceMatrixElement = new Element(XMLTags.COVARIANCE_MATRIX.toString());
        covarianceMatrixElement.setText(getString(covarianceMatrix));
        return covarianceMatrixElement;
    }

    private Element getCholeskyDecompositionElement(double[] choleskyDecomposition) {
        Element choleskyDecompositionElement = new Element(XMLTags.CHOLESKY_DECOMPOSITION.toString());
        choleskyDecompositionElement.setText(getString(choleskyDecomposition));
        return choleskyDecompositionElement;
    }
    
    private void getRegressionPotential(Element xmlElement, RegressionPotential potential)
    {
        xmlElement.addContent(getCoefficientsElement(potential.getCoefficients()));
        xmlElement.addContent(getCovariatesElement(potential.getCovariates()));
        if(potential.getCovarianceMatrix() != null)
        {
            xmlElement.addContent(getCovarianceMatrixElement(potential.getCovarianceMatrix()));
        }else if(potential.getCholeskyDecomposition() != null)
        {
            xmlElement.addContent(getCholeskyDecompositionElement(potential.getCholeskyDecomposition()));
        }
    }
    
    /**
     * 
     * @param potential
     * @return
     */
    protected Element getUncertainValuesElement(Potential potential) {
        Element uncertainValuesElement = new Element(XMLTags.UNCERTAIN_VALUES.toString());
        UncertainValue[] table = ((TablePotential) potential).getUncertaintyTable();
        int size = table.length;
        for (int i = 0; i < size; i++) {
            UncertainValue auxValue = table[i];
            Element auxUncertain = getUncertainValueElement(auxValue);
            uncertainValuesElement.addContent(auxUncertain);
        }
        return uncertainValuesElement;
    }

    /**
     * 
     * @param uncertainValue
     * @return
     */
    protected Element getUncertainValueElement(UncertainValue uncertainValue) {
        Element element = new Element(XMLTags.VALUE.toString());
        if (uncertainValue != null) {
            ProbDensFunction function = uncertainValue.getProbDensFunction();
            String functionName = function.getClass().getAnnotation(ProbDensFunctionType.class).name();
            element.setAttribute(XMLAttributes.DISTRIBUTION.toString(), functionName);
            String nameParam = uncertainValue.getName();
            if (nameParam != null) {
                element.setAttribute(XMLAttributes.NAME.toString(), nameParam);
            }
            element.setText(getString(uncertainValue.getProbDensFunction().getParameters()));
        }
        return element;
    }

    /**
     * @param potentialVariables
     * @param targetElement
     */
    protected void writePotentialVariables(List<Variable> potentialVariables, Element targetElement) {
        if (potentialVariables.size() > 0) {
            Element variablesElement = new Element(XMLTags.VARIABLES.toString());
            for (Variable variable : potentialVariables) {
                Element variableElement = new Element(XMLTags.VARIABLE.toString());
                writeVariableName(variable, variableElement);
                variablesElement.addContent(variableElement);
            }
            // Write var names of the table potential to the XML file
            targetElement.addContent(variablesElement);
        }
    }

    protected void writeVariableName(Variable variable, Element xmlElement) {
        xmlElement.setAttribute(XMLAttributes.NAME.toString(), variable.getBaseName());
        if (variable.getTimeSlice() >= 0) {
            xmlElement.setAttribute(XMLAttributes.TIMESLICE.toString(),
                    String.valueOf(variable.getTimeSlice()));
        }
    }

    /**
     * Writes recursively the structure of the subtree pointed by
     * <code>node</code> It's too large: split it?
     * 
     * @param parent
     * @param source
     * @param mapPotentialRefs
     * @param branchIndex
     * @return <code>Element</code> containing tree data
     */
    /*
     * protected Element getBranchElement(Node parent, Node source,
     * HashMap<Potential, String> mapPotentialRefs, int branchIndex) { Element
     * branchElement = new Element(XMLTags.BRANCH.toString()); // Read the
     * branch element Variable parentVar = (Variable) parent.getObject(); Link
     * link = source.getGraph().getLink(parent, source, true); // Is there any
     * information attached to this branch? if (link instanceof LabelledLink) {
     * LabelledLink labelledLink = (LabelledLink) link; if
     * (!(labelledLink.getLabel() instanceof BranchData)) { throw new
     * RuntimeException("Expected BranchData class: found " +
     * labelledLink.getLabel().getClass().getName()); } // Extract the branch
     * data BranchData branchData = (BranchData) labelledLink.getLabel(); // If
     * the variable is numeric, its intervals must be written if
     * (parentVar.getVariableType() == VariableType.NUMERIC) { Element
     * intervalElement = new Element( XMLTags.INTERVAL.toString());
     * HashSet<BranchInterval> branchIntervals = branchData
     * .getBranchIntervals(); if (branchIntervals.size() != 1) { throw new
     * RuntimeException( "Multiple intervals not supported yet"); } // TODO:
     * union of intervals must cover the whole range of the // numeric variable
     * // Export the left and right values of the interval. Closed/Open //
     * attribute must be taken into account for (BranchInterval o :
     * branchIntervals) { Element leftThresholdElement = new Element(
     * XMLTags.THRESHOLD.toString()); leftThresholdElement.setAttribute(
     * XMLAttributes.VALUE.toString(), String.valueOf(o.getLeft()));
     * leftThresholdElement.setAttribute(XMLAttributes.BELONGS_TO .toString(),
     * o.isLeftClosed() ? XMLValues.RIGHT.toString() :
     * XMLValues.LEFT.toString());
     * intervalElement.addContent(leftThresholdElement); Element
     * rightThresholdElement = new Element( XMLTags.THRESHOLD.toString());
     * rightThresholdElement.setAttribute( XMLAttributes.VALUE.toString(),
     * String.valueOf(o.getRight()));
     * rightThresholdElement.setAttribute(XMLAttributes.BELONGS_TO .toString(),
     * o.isRightClosed() ? XMLValues.LEFT.toString() :
     * XMLValues.RIGHT.toString());
     * intervalElement.addContent(rightThresholdElement); } // Append the
     * information to the element to be exported
     * branchElement.addContent(intervalElement); } else { // TODO: test
     * cardinality of variable states and InnerNode HashSet<State> branchStates
     * = branchData.getBranchStates(); if
     * (FORMAT_VERSION_NUMBER.contentEquals("0.0.1")) { for (State o :
     * branchStates) { String varStateName = o.getName();
     * branchElement.addContent(new Element(XMLTags.STATE
     * .toString()).setAttribute( XMLAttributes.NAME.toString(), varStateName));
     * } } else { Element states = new Element(XMLTags.STATES.toString()); for
     * (State o : branchStates) { String varStateName = o.getName();
     * states.addContent(new Element(XMLTags.STATE.toString())
     * .setAttribute(XMLAttributes.NAME.toString(), varStateName)); }
     * branchElement.addContent(states); } // Export every state } } else { //
     * The branch is not labeled, so states order of the branching // variable
     * is used String varStateName = parentVar.getStateName(branchIndex);
     * branchElement.addContent(new Element(XMLTags.STATE.toString())
     * .setAttribute(XMLAttributes.NAME.toString(), varStateName)); } // If this
     * branch leads to a new subtree... if (source.getObject().getClass() ==
     * Variable.class) { Variable topVar = (Variable) source.getObject(); //
     * Create a new subtree as a TreeADD Potential Element innerPotentialElement
     * = new Element( XMLTags.POTENTIAL.toString());
     * innerPotentialElement.setAttribute(XMLAttributes.TYPE.toString(),
     * PotentialType.TREE_ADD.toString()); // Write the branching variable of
     * this subtree Element topVarElement = new
     * Element(XMLTags.TOP_VARIABLE.toString());
     * topVarElement.setAttribute(XMLAttributes.NAME.toString(),
     * topVar.getName()); innerPotentialElement.addContent(topVarElement); //
     * Create every defined branch for this node Element branchesElement = new
     * Element(XMLTags.BRANCHES.toString()); int newBranchIndex = 0; for (Node
     * child : source.getChildren()) { // Recursive writing of the contents of
     * the subtree branchesElement.addContent(getBranchElement(source, child,
     * mapPotentialRefs, newBranchIndex++)); }
     * innerPotentialElement.addContent(branchesElement);
     * branchElement.addContent(innerPotentialElement); } else if
     * (source.getObject() instanceof Potential) { // Found a leaf of the
     * TreeADD structure Potential potential = (Potential) source.getObject();
     * PotentialType potentialType = getPotentialType(potential); Element
     * potentialElement = new Element(XMLTags.POTENTIAL.toString()); // Is this
     * potential already defined? if (mapPotentialRefs.containsKey(potential)) {
     * // Use REF tag, to reduce storage space String comment =
     * mapPotentialRefs.get(potential);
     * potentialElement.setAttribute(XMLAttributes.REF.toString(), comment); }
     * else { // Create a new potential
     * potentialElement.setAttribute(XMLAttributes.TYPE.toString(),
     * potentialType.toString()); if (source.getNumParents() > 1) { // Only
     * makes sense to assign a name to those potentials // referenced in more
     * than a branch String comment = potential.getComment(); if
     * (comment.length() == 0) { // If the potential have no name, a invented
     * one is // assigned comment = potential.getClass().getSimpleName() + "@@"
     * + String.valueOf(mapPotentialRefs.size()); } // Avoid duplicated names if
     * (mapPotentialRefs.containsValue(comment)) { throw new RuntimeException(
     * "Duplicated label on different potentials: " + comment); } // Store the
     * potential for future reuse mapPotentialRefs.put(potential, comment);
     * potentialElement.setAttribute( XMLAttributes.LABEL.toString(), comment);
     * } ArrayList<Variable> potentialVariables = potential .getVariables(); if
     * (potentialVariables.size() > 0) { Element variablesElement = new Element(
     * XMLTags.VARIABLES.toString()); for (Variable variable :
     * potentialVariables) { variablesElement.addContent(new Element(
     * XMLTags.VARIABLE.toString()).setAttribute( XMLAttributes.NAME.toString(),
     * variable.getName())); } // Write var names of the table potential to the
     * XML file potentialElement.addContent(variablesElement); } // Check if
     * this type of potential is supported if (potential instanceof
     * TablePotential) { // Write the table values Element valuesElement = new
     * Element( XMLTags.VALUES.toString()) .setText(getString(((TablePotential)
     * potential).values)); potentialElement.addContent(valuesElement); // Write
     * the uncertain values if (((TablePotential) potential).getUncertainTable()
     * != null) { Element uncertainValuesElement =
     * getUncertainValuesElement(potential);
     * potentialElement.addContent(uncertainValuesElement); } } else { throw new
     * RuntimeException(
     * "Potential type not supported for inner potentials of a TreeADD: " +
     * potential.getClass().getName()); } }
     * branchElement.addContent(potentialElement); } else { throw new
     * RuntimeException("Unexpected class: found " +
     * source.getClass().getName()); } return branchElement; }
     */
    /**
     * @author myebra
     * 
     * @param branch
     * @param topVariable
     * @return
     */
    protected Element getTreeADDBranch(TreeADDBranch branch, Variable topVariable, ProbNet probNet) {
        Element branchElement = new Element(XMLTags.BRANCH.toString());
        // Read the branch element

        if (topVariable.getVariableType() == VariableType.NUMERIC) {
            Element intervalElement = new Element(XMLTags.THRESHOLDS.toString());
            // Export the left and right values of the interval. Closed/Open
            // attribute must be taken into account
            Element minThresholdElement = new Element(XMLTags.THRESHOLD.toString());
            minThresholdElement.setAttribute(XMLAttributes.VALUE.toString(),
                    String.valueOf(branch.getLowerBound().getLimit()));
            minThresholdElement.setAttribute(XMLAttributes.BELONGS_TO.toString(), branch
                    .getLowerBound().belongsToLeft() ? XMLValues.LEFT.toString()
                    : XMLValues.RIGHT.toString());
            intervalElement.addContent(minThresholdElement);
            Element maxThresholdElement = new Element(XMLTags.THRESHOLD.toString());
            maxThresholdElement.setAttribute(XMLAttributes.VALUE.toString(),
                    String.valueOf(branch.getUpperBound().getLimit()));
            maxThresholdElement.setAttribute(XMLAttributes.BELONGS_TO.toString(), branch
                    .getUpperBound().belongsToLeft() ? XMLValues.LEFT.toString()
                    : XMLValues.RIGHT.toString());
            intervalElement.addContent(maxThresholdElement);

            // Append the information to the element to be exported
            branchElement.addContent(intervalElement);
        } else if (topVariable.getVariableType() == VariableType.FINITE_STATES
                || topVariable.getVariableType() == VariableType.DISCRETIZED) {
            // TODO: test cardinality of variable states and InnerNode
            List<State> branchStates = branch.getBranchStates();

            Element states = new Element(XMLTags.STATES.toString());
            for (State state : branchStates) {
                String varStateName = state.getName();
                states.addContent(new Element(XMLTags.STATE.toString()).setAttribute(
                        XMLAttributes.NAME.toString(), varStateName));
            }
            branchElement.addContent(states);
        }

        // label
        if(branch.getLabel() != null)
        {
            Element labelElement = new Element(XMLTags.LABEL.toString());
            labelElement.setText(branch.getLabel());
            branchElement.addContent(labelElement);
        }
        if(branch.getReference() != null)
        {
            Element labelElement = new Element(XMLTags.REFERENCE.toString());
            labelElement.setText(branch.getReference());
            branchElement.addContent(labelElement);
            
        }else
        {
            Potential potential = branch.getPotential();
            Element potentialElement = new Element(XMLTags.POTENTIAL.toString());
            // In trees potential role is assumed to be the same as the tree role
            // potential, so it is not necessary to indicate it
            getPotential(probNet, potential, potentialElement);
            branchElement.addContent(potentialElement);
        }
        return branchElement;
    }

    protected String getString(double[] table) {
        StringBuffer stringBuffer = new StringBuffer();
        for (double value : table) {
            stringBuffer.append(String.valueOf(value) + " ");
        }
        return stringBuffer.toString();
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @return <code>String</code>
     */
    protected String getXMLNetworkType(ProbNet probNet) {
        NetworkTypeManager networkTypeManager = new NetworkTypeManager();
        return networkTypeManager.getName(probNet.getNetworkType());
    }

     /**
     * @return XMLPotentialType
     * @version 1.0
     * @version 1.1 - Added TreeADDPotential detection (2011 04 21)
     */
    protected PotentialType getPotentialType(Potential potential) {
        @SuppressWarnings("rawtypes")
        Class potentialClass = potential.getClass();
        if (potentialClass == TablePotential.class) {
            return PotentialType.TABLE;
        } else if ((potentialClass == MaxPotential.class) || (potentialClass == MinPotential.class)
                || (potentialClass == TuningPotential.class)) {
            return PotentialType.ICIMODEL;
        } else if (potentialClass == TreeADDPotential.class) {
            return PotentialType.TREE_ADD;
        } else if (potentialClass == UniformPotential.class) {
            return PotentialType.UNIFORM;
        } else if (potentialClass == SameAsPrevious.class) {
            return PotentialType.SAME_AS_PREVIOUS;
        } else if (potentialClass == CycleLengthShift.class) {
            return PotentialType.CYCLE_LENGTH_SHIFT;
        } else if (potentialClass == SumPotential.class) {
            return PotentialType.SUM;
        } else if (potentialClass == ProductPotential.class) {
            return PotentialType.PRODUCT;
        } else if (potentialClass == WeibullHazardPotential.class) {
            return PotentialType.WEIBULL_HAZARD;
        } else if (potentialClass == ExponentialHazardPotential.class) {
            return PotentialType.EXPONENTIAL_HAZARD;
        } else if (potentialClass == LinearRegressionPotential.class) {
            return PotentialType.LINEAR_REGRESSION;
        } else if (potentialClass == DeltaPotential.class) {
            return PotentialType.DELTA;
        } else if (potentialClass == ExponentialPotential.class) {
            return PotentialType.EXPONENTIAL;
        }
        // TODO To be extended with more potentials types
        return null;
    }

    /**
     * transform a HTML string in a pure String by a full substitution of the
     * special HTML characters "<" and ">" in the equivalent "SymbolLT" and
     * "SymbolGT" Please, pay attention that the equivalent format "&lt" and
     * "&gt" are not used here as JDOM is using the character "&" to start a
     * definition of an entity Ref class, so we need to avoid it.
     */
    protected String htmlToText(String htmlSection) {
        String result = htmlSection;
        result = result.replaceAll("<", "SymbolLT");
        result = result.replaceAll(">", "SymbolGT");
        return result;
    }

    // TODO OOPN start
    protected void getOOPN(OOPNet OOPNet, Element probNetElement, Element oonElement) {
        if (OOPNet.getClasses().size() > 0) {
            Element classesElement = new Element(XMLTags.CLASSES.toString());
            for (String className : ((OOPNet) OOPNet).getClasses().keySet()) {
                Element classElement = new Element(XMLTags.CLASS.toString());
                classElement.setAttribute(new Attribute("name", className));
                writeXMLProbNet(((OOPNet) OOPNet).getClasses().get(className), classElement);
                classesElement.addContent(classElement);
            }
            oonElement.addContent(classesElement);
        }

        if (OOPNet.getInstances().size() > 0) {
            Element instancesElement = new Element(XMLTags.INSTANCES.toString());
            for (Instance instance : ((OOPNet) OOPNet).getInstances().values()) {
                Element instanceElement = new Element(XMLTags.INSTANCE.toString());
                instanceElement.setAttribute(new Attribute("name", instance.getName()));
                instanceElement.setAttribute(new Attribute("class", instance.getClassNet()
                        .getName()));
                instanceElement.setAttribute(new Attribute("isInput", Boolean.toString(instance
                        .isInput())));
                instanceElement
                        .setAttribute(new Attribute("arity", instance.getArity().toString()));
                instancesElement.addContent(instanceElement);
            }
            oonElement.addContent(instancesElement);
            if (OOPNet.getReferenceLinks().size() > 0) {
                Element instanceLinksElement = new Element(XMLTags.REFERENCE_LINKS.toString());
                for (ReferenceLink link : OOPNet.getReferenceLinks()) {
                    Element linkElement = new Element(XMLTags.REFERENCE_LINK.toString());
                    if (link instanceof InstanceReferenceLink) {
                        InstanceReferenceLink instanceLink = (InstanceReferenceLink) link;
                        linkElement.setAttribute(new Attribute("type", "instance"));
                        linkElement.setAttribute(new Attribute("source", instanceLink
                                .getSourceInstance().getName()));
                        linkElement.setAttribute(new Attribute("destination", instanceLink
                                .getDestInstance().getName()));
                        linkElement.setAttribute(new Attribute("parameter", instanceLink
                                .getDestSubInstance().getName()));
                    } else if (link instanceof NodeReferenceLink) {
                        NodeReferenceLink nodeLink = (NodeReferenceLink) link;
                        linkElement.setAttribute(new Attribute("type", "node"));
                        linkElement.setAttribute(new Attribute("source", nodeLink.getSourceNode()
                                .getName()));
                        linkElement.setAttribute(new Attribute("destination", nodeLink
                                .getDestinationNode().getName()));
                    }
                    instanceLinksElement.addContent(linkElement);
                }
                oonElement.addContent(instanceLinksElement);
            }
        }
        probNetElement.addContent(oonElement);
    }
    // TODO OOPN end
}