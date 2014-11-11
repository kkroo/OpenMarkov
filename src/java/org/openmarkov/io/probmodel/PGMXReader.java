/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.io.probmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.io.ProbNetReader;
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
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionManager;
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
import org.openmarkov.core.model.network.potential.treeadd.Threshold;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.core.model.network.type.plugin.NetworkTypeManager;
import org.openmarkov.core.oopn.Instance;
import org.openmarkov.core.oopn.Instance.ParameterArity;
import org.openmarkov.core.oopn.InstanceReferenceLink;
import org.openmarkov.core.oopn.NodeReferenceLink;
import org.openmarkov.core.oopn.OOPNet;
import org.openmarkov.core.oopn.ReferenceLink;
import org.openmarkov.core.oopn.exception.InstanceAlreadyExistsException;
import org.openmarkov.io.probmodel.strings.XMLAttributes;
import org.openmarkov.io.probmodel.strings.XMLTags;
import org.openmarkov.io.probmodel.strings.XMLValues;

/**
 * @author manuel
 * @author mpalacios Properties object was deprecated for basic
 *         additionalProperties. ProbNet initialization was fixed. Method to get
 *         network comments was added.
 */
@FormatType(name = "PGMXReader", extension = "pgmx", description = "OpenMarkov", role = "Reader")
public class PGMXReader implements ProbNetReader {
    // Attributes
    protected String version;

    /**
     * @param netName
     *            = path + network name + extension. <code>String</code>
     * @return The <code>ProbNet</code> readed or <code>null</code>
     */
    public ProbNetInfo loadProbNet(String netName)
            throws ParserException {
        FileInputStream file;
        try {
            file = new FileInputStream(netName);
        } catch (FileNotFoundException e) {
            throw new ParserException("File " + netName + " not found.");
        }
        return loadProbNet(file, netName);
    }

    /**
     * @param netName
     *            = path + network name + extension. <code>String</code>
     * @return The <code>ProbNet</code> readed or <code>null</code>
     */
    public ProbNetInfo loadProbNet(InputStream file, String netName)
            throws ParserException {
        SAXBuilder builder = new SAXBuilder();
        Document document = null;
        ProbNet probNet = null;
        List<EvidenceCase> evidence = null;
        try {
            document = builder.build(file);
        } catch (JDOMException e) {
            throw new ParserException("Can not parse XML document " + netName + ".");
        } catch (IOException e) {
            throw new ParserException("General Input/Output error reading " + netName + ".\n" + e.getMessage());
        }
        Element root = document.getRootElement();
        version = root.getAttributeValue(XMLAttributes.FORMAT_VERSION.toString());
        probNet = getProbNet(root, netName);
        evidence = getEvidence(root, probNet);
        getPolicies(root, probNet);
        return new ProbNetInfo(probNet, evidence);
    }

    /**
     * @param root
     * @param netName
     * @return
     * @throws ParserException
     */
    protected ProbNet getProbNet(Element root, String netName)
            throws ParserException {
        return getProbNet(root, netName, new HashMap<String, ProbNet>());
    }

    /**
     * @param root
     * @param netName
     * @param classes
     * @return
     * @throws ParserException
     */
    protected ProbNet getProbNet(Element root, String netName, Map<String, ProbNet> classes)
            throws ParserException {
        ProbNet probNet = null;
        Element xMLProbNet = root.getChild(XMLTags.PROB_NET.toString());
        if (xMLProbNet != null) {// Read prob net
                                 // =
                                 // xMLProbNet.getAttribute(XMLAttributes.TYPE.toString());
            NetworkType networkType = getNetworkType(xMLProbNet);
            // TODO OOPN start
            if (xMLProbNet.getChild(XMLTags.OOPN.toString()) != null) {
                probNet = new OOPNet(networkType);
            } else {
                // TODO OOPN end
                probNet = new ProbNet(networkType);
            }
            getAdditionalConstraints(probNet, xMLProbNet);
            // TODO Read Inference options
            // TODO Read Policies
            probNet = getConstraints(xMLProbNet, probNet);
            probNet.setComment(getComment(xMLProbNet));
            probNet.setName(FilenameUtils.getName(netName));
            getDecisionCriteria(xMLProbNet, probNet);
            getVariables(xMLProbNet, probNet);
            getLinks(xMLProbNet, probNet);
            getPotentials(xMLProbNet, probNet);
            getAgents(xMLProbNet, probNet);
            getAdditionalProperties(xMLProbNet, probNet);
            // TODO OOPN start
            getOOPN(netName, xMLProbNet, probNet, classes);
            // TODO OOPN end
        }
        return probNet;
    }

    protected void getAdditionalProperties(Element root, ProbNet probNet) {
        Element xmlAdditionalProperties = root.getChild(XMLTags.ADDITIONAL_PROPERTIES.toString());
        if (xmlAdditionalProperties != null) {
            List<Element> propertiesListElement = xmlAdditionalProperties.getChildren();
            if (propertiesListElement != null && propertiesListElement.size() > 0) {
                for (Element propertyElement : propertiesListElement) {
                    String propertyName = propertyElement.getAttributeValue(XMLAttributes.NAME.toString());
                    String propertyValue = propertyElement.getAttributeValue(XMLAttributes.VALUE.toString());
                    probNet.additionalProperties.put(propertyName, propertyValue);
                }
            }
        }
    }

    /**
     * @param root
     *            . <code>Element</code>
     * @param probNet
     *            . <code>ProbNet</code>
     */
    protected void getAgents(Element root, ProbNet probNet) {
        Element xmlAgentsRoot = root.getChild(XMLTags.AGENTS.toString());
        if (xmlAgentsRoot != null) {
            List<Element> xmlAgents = xmlAgentsRoot.getChildren();
            ArrayList<StringWithProperties> agents = new ArrayList<StringWithProperties>();
            for (Element agentElement : xmlAgents) {
                String agentName = agentElement.getAttributeValue(XMLAttributes.NAME.toString());
                StringWithProperties agent = new StringWithProperties(agentName);
                AdditionalProperties agentProperties = getAdditionalProperties(agentElement);
                agent.put(agentProperties);
                agents.add(agent);
            }
            probNet.setAgents(agents);
        }
    }

    /**
     * @param root
     *            . <code>Element</code>
     * @param probNet
     *            . <code>ProbNet</code>
     */
    protected void getDecisionCriteria(Element root, ProbNet probNet) {
        Element xmlCriteronRoot = root.getChild(XMLTags.DECISION_CRITERIA.toString());
        if (xmlCriteronRoot != null) {
            List<Element> xmlCriterion = xmlCriteronRoot.getChildren();
            ArrayList<StringWithProperties> criterias = new ArrayList<>();
            for (Element criterionElement : xmlCriterion) {
                String criterionName = criterionElement.getAttributeValue(XMLAttributes.NAME.toString());
                AdditionalProperties criterionProperties = getAdditionalProperties(criterionElement);
                StringWithProperties decisionCriteria = new StringWithProperties(criterionName);
                if (criterionProperties != null) {
                    decisionCriteria.put(criterionProperties);
                }
                criterias.add(decisionCriteria);
                // criterions.put(criterionName, criterionProperties);
            }
            probNet.setDecisionCriteria2(criterias);
        }
    }

    /**
     * @param agentElement
     *            . <code>Element</code>
     * @return <code>AdditionalProperties</code>
     */
    protected AdditionalProperties getAdditionalProperties(Element agentElement) {
        AdditionalProperties additionalProperties = null;
        List<Element> propertiesListElement = agentElement.getChildren();
        if (propertiesListElement != null && propertiesListElement.size() > 0) {
            additionalProperties = new AdditionalProperties();
            for (Element propertyElement : propertiesListElement) {
                String propertyName = propertyElement.getAttributeValue(XMLAttributes.NAME.toString());
                String propertyValue = propertyElement.getAttributeValue(XMLAttributes.VALUE.toString());
                additionalProperties.put(propertyName, propertyValue);
            }
        }
        return additionalProperties;
    }

    protected List<EvidenceCase> getEvidence(Element root, ProbNet probNet) {
        Element xMLEvidence = root.getChild(XMLTags.EVIDENCE.toString());
        List<EvidenceCase> evidence = new ArrayList<EvidenceCase>();
        if (xMLEvidence != null) {
            try {
                List<Element> xmlEvidenceCases = xMLEvidence.getChildren();
                for (Element xmlEvidenceCase : xmlEvidenceCases) {
                    EvidenceCase evidenceCase = new EvidenceCase();
                    List<Element> xmlFindings = xmlEvidenceCase.getChildren();
                    for (Element xmlFinding : xmlFindings) {
                        Variable variable = probNet.getVariable(xmlFinding.getAttributeValue("variable"));
                        Finding finding = null;
                        if (variable.getVariableType() == VariableType.DISCRETIZED
                                || variable.getVariableType() == VariableType.FINITE_STATES) {
                            String stateName = xmlFinding.getAttributeValue("state");
                            finding = new Finding(variable, variable.getStateIndex(stateName));
                        } else {
                            double numericalValue = Double.parseDouble(xmlFinding.getAttributeValue("numericalValue"));
                            finding = new Finding(variable, numericalValue);
                        }
                        evidenceCase.addFinding(finding);
                    }
                    evidence.add(evidenceCase);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return evidence;
    }

    protected void getAdditionalConstraints(ProbNet probNet, Element xMLProbNet) {
        if (parseXMLElement(xMLProbNet, XMLTags.ADDITIONAL_CONSTRAINTS)) {
        }
    }

    protected boolean parseXMLElement(Element xMLRoot, XMLTags additionalConstraints) {
        return false;
    }

    protected NetworkType getNetworkType(Element xMLProbNet)
            throws ParserException {
        String sType = xMLProbNet.getAttributeValue(XMLAttributes.TYPE.toString());
        if (sType == null || sType.isEmpty()) {
            throw new ParserException("No network type found in file");
        }
        NetworkTypeManager networkTypeManager = new NetworkTypeManager();
        NetworkType networkType = networkTypeManager.getNetworkType(sType);
        if (networkType == null) {
            throw new ParserException("Unknown network type: " + sType);
        }
        return networkType;
    }

    /**
     * @param root
     *            . <code>Element</code>
     * @param probNet
     *            . <code>ProbNet</code>
     */
    protected ProbNet getConstraints(Element root, ProbNet probNet)
            throws ParserException {
        // ProbNet network = null;
        Element xmlConstraintsRoot = root.getChild(XMLTags.ADDITIONAL_CONSTRAINTS.toString());
        if (xmlConstraintsRoot != null) {
            List<Element> xmlConstraints = xmlConstraintsRoot.getChildren();
            for (Element constraintElement : xmlConstraints) {
                String constraintName = constraintElement.getAttributeValue(XMLAttributes.NAME.toString());
                try {
                    probNet.addConstraint((PNConstraint) Class.forName(constraintName).newInstance());
                } catch (Exception e) {
                    throw new ParserException("Can not create an instance "
                            + "of constraint: "
                            + constraintName);
                }
            }
        }
        return probNet;
    }

    /**
     * @param root
     *            . <code>Element</code>
     */
    protected String getComment(Element root) {
        Element xmlComments = root.getChild(XMLTags.COMMENT.toString());
        if (xmlComments != null) {
            String comment = xmlComments.getText();
            return textToHtml(comment);
        }
        return "";
    }

    /**
     * Reads Nodes:
     * 
     * @param root
     *            . <code>Element</code>
     * @param probNet
     *            . <code>ProbNet</code>
     * @throws ParserException
     * @throws DataConversionException
     * @throws Exception
     */
    protected void getVariables(Element root, ProbNet probNet)
            throws ParserException {
        Element xmlVariablesRoot = root.getChild(XMLTags.VARIABLES.toString());
        if (xmlVariablesRoot != null) {
            List<Element> xmlVariables = xmlVariablesRoot.getChildren();
            for (Element variableElement : xmlVariables) { // Variables
                VariableType variableType = getXMLVariableType(variableElement);
                NodeType nodeType = getXMLNodeType(variableElement);
                String variableName = variableElement.getAttributeValue(XMLAttributes.NAME.toString());
                String stringTimeSlice = variableElement.getAttributeValue(XMLAttributes.TIMESLICE.toString());
                if (stringTimeSlice != null) {
                    variableName = variableName.replace(" [" + stringTimeSlice + "]", "");
                }
                Variable variable = null;
                // Coordinates
                int x = getXMLXCoordinate(variableElement);
                int y = getXMLYCoordinate(variableElement);
                Double precision = getXMLPrecision(variableElement);
                // States & intervals
                State[] states = null;
                if ((nodeType == NodeType.CHANCE) || (nodeType == NodeType.DECISION)) {
                    if ((variableType == VariableType.FINITE_STATES)
                            || (variableType == VariableType.DISCRETIZED)) {
                        Element statesElement = variableElement.getChild(XMLTags.STATES.toString());
                        if (statesElement != null) { // jlgozalo. 25/10/2009
                            states = getXMLStates(statesElement); // previously
                            // without null control
                        } else {
                            throw new ParserException("States list not found in finite states variable "
                                    + variableName);
                        }
                    }
                    if (variableType == VariableType.FINITE_STATES) {
                        variable = new Variable(variableName, states);
                    } else {
                        // Common part to CONTINUOUS and DISCRETIZED
                        try {
                            if (variableType == VariableType.NUMERIC) {
                                variable = getXMLContinuousVariable(variableElement, variableName);
                            } else { // DISCRETIZED variable. Read sub-intervals
                                Element thresholdsElement = variableElement.getChild(XMLTags.THRESHOLDS.toString());
                                variable = getXMLDiscretizedVariable(thresholdsElement,
                                        states,
                                        variableName);
                            }
                        } catch (DataConversionException e) {
                            throw new ParserException("Data conversion "
                                    + "problem with variable "
                                    + variableName
                                    + ".");
                        }
                    }
                } else { // utility only??
                    variable = new Variable(variableName);
                    // decision criteria
                    if (nodeType == NodeType.UTILITY) {
                        Element decisionCriteria = variableElement.getChild(XMLTags.DECISION_CRITERIA.toString());
                        if (decisionCriteria != null) {
                            for (StringWithProperties criteria : probNet.getDecisionCriteria()) {
                                if (criteria.getString().equals(decisionCriteria.getAttributeValue(XMLAttributes.NAME.toString()))) {
                                    variable.setDecisionCriteria(criteria);
                                    break;
                                }
                            }
                        }
                    }
                }
                // Set timeSlice
                if (stringTimeSlice != null) {
                    variable.setTimeSlice(Integer.parseInt(stringTimeSlice));
                }
                // Set unit
                Element xMLUnit = variableElement.getChild(XMLTags.UNIT.toString());
                if (xMLUnit != null) {
                    String unit = xMLUnit.getText();
                    variable.setUnit(new StringWithProperties(unit));
                }                
                // other additionalProperties
                HashMap<String, String> properties = getProperties(variableElement);
                // comment
                ProbNode probNode = probNet.addProbNode(variable, nodeType);
                // always Observed property
                Element alwaysObserved = variableElement.getChild(XMLTags.ALWAYS_OBSERVED.toString());
                if (alwaysObserved != null) {
                    probNode.setAlwaysObserved(true);
                }
                if (properties.get(XMLTags.PURPOSE.toString()) != null) {
                    probNode.setPurpose(properties.get(XMLTags.PURPOSE.toString()));
                    properties.remove(XMLTags.PURPOSE.toString());
                }
                // TODO revisar el posible error al convertir un string a número
                if (properties.get(XMLTags.RELEVANCE.toString()) != null) {
                    probNode.setRelevance(Double.valueOf(properties.get(XMLTags.RELEVANCE.toString())));
                    properties.remove(XMLTags.RELEVANCE.toString());
                }
                // TODO OOPN start
                if (variableElement.getAttribute(XMLAttributes.IS_INPUT.toString()) != null) {
                    boolean isInput = Boolean.parseBoolean(variableElement.getAttribute(XMLAttributes.IS_INPUT.toString()).getValue());
                    probNode.setInput(isInput);
                }
                // TODO OOPN end
                probNode.setComment(getComment(variableElement));
                // with the created probNode, put position (x, y)
                probNode.getNode().setCoordinateX(x);
                probNode.getNode().setCoordinateY(y);
                if (precision != null) {
                    probNode.getVariable().setPrecision(precision);
                }
                if (properties != null) {
                    for (String key : new ArrayList<String>(properties.keySet())) {
                        probNode.additionalProperties.put(key, properties.get(key));
                    }
                }
            }
        }
    }

    protected Variable getVariable(Element element, ProbNet probNet)
            throws ParserException {
        String variableName = element.getAttributeValue(XMLAttributes.NAME.toString());
        // strip the name from the time slice for backwards compatibility
        String timeSlice = element.getAttributeValue(XMLAttributes.TIMESLICE.toString());
        variableName = variableName.replace(" [" + timeSlice + "]", "");
        Variable variable = null;
        try {
            variable = (timeSlice == null) ? probNet.getVariable(variableName)
                    : probNet.getVariable(variableName, Integer.parseInt(timeSlice));
        } catch (ProbNodeNotFoundException e) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Unknown variable name ");
            errorMessage.append(variableName);
            if (timeSlice != null) {
                errorMessage.append(" [" + timeSlice + "]");
            }
            // TODO find out the line where it is
            throw new ParserException(errorMessage.toString());
        }
        return variable;
    }

    protected Double getXMLPrecision(Element variableElement) {
        Element precisionElement = variableElement.getChild(XMLTags.PRECISION.toString());
        Double precision = null;
        if (precisionElement != null) {
            String pString = precisionElement.getText();
            precision = new Double(pString);
        }
        return precision;
    }

    protected String getXMLPurpose(Element aditionalElement) {
        String purpose = "";
        if (aditionalElement != null) {
            Element purposeElement = aditionalElement.getChild(XMLTags.PURPOSE.toString());
            if (purposeElement != null) {
                purpose = purposeElement.getText();
            }
        }
        return purpose;
    }

    /**
     * @param aditionalElement
     *            . <code>Element</code>
     * @return Double
     */
    protected Double getXMLRelevance(Element aditionalElement) {
        double relevance = ProbNode.defaultRelevance;
        if (aditionalElement != null) {
            Element relevanceElement = aditionalElement.getChild(XMLTags.RELEVANCE.toString());
            if (relevanceElement != null) {
                String relevanceString = relevanceElement.getText();
                relevance = new Double(relevanceString);
            }
        }
        return relevance;
    }

    /**
     * Reads additionalProperties that have not a clear classification.
     * 
     * @param variableElement
     *            . <code>Element</code>
     * @return A <code>HashMap</code> with <code>key = String</code> and
     *         <code>value = Object</code>
     */
    protected HashMap<String, String> getProperties(Element variableElement) {
        HashMap<String, String> properties = new HashMap<String, String>();
        Element others = variableElement.getChild(XMLTags.ADDITIONAL_PROPERTIES.toString());
        if (others != null) {
            List<Element> xmlProperties = others.getChildren();
            for (Element xmlProperty : xmlProperties) { // additionalProperties
                String key = xmlProperty.getAttributeValue(XMLAttributes.NAME.toString());
                String value = xmlProperty.getAttributeValue(XMLAttributes.VALUE.toString());
                // try to discover the property type (double, boolean...)
                try { // try parse double
                    Double.parseDouble(value);
                    properties.put(key, value);
                } catch (NumberFormatException nd) {
                    try { // try parse int
                        Integer.parseInt(value);
                        properties.put(key, value);
                    } catch (NumberFormatException ni) { // try parse boolean
                        Boolean bTrue = Boolean.parseBoolean(value);
                        boolean bFalse = value.equalsIgnoreCase("false");
                        if (bTrue || bFalse) {
                            properties.put(key, bTrue.toString());
                        } else { // Nor double nor integer nor boolean -> String
                            properties.put(key, value);
                        }
                    }
                }
            }
        }
        return properties;
    }

    /**
     * @param variableElement
     *            . <code>Element</code>
     * @return variable type. <code>VariableType</code>
     */
    protected VariableType getXMLVariableType(Element variableElement) {
        VariableType variableType = null;
        String role = variableElement.getAttributeValue(XMLAttributes.TYPE.toString());
        if (role.contentEquals(VariableType.FINITE_STATES.toString())) {
            variableType = VariableType.FINITE_STATES;
        } else if (role.contentEquals(VariableType.NUMERIC.toString())) {
            variableType = VariableType.NUMERIC;
        } else if (role.contentEquals(VariableType.DISCRETIZED.toString())) {
            variableType = VariableType.DISCRETIZED;
        }
        return variableType;
    }

    /**
     * @param variableElement
     *            . <code>Element</code>
     * @return node type. <code>NodeType</code>
     */
    protected NodeType getXMLNodeType(Element variableElement) {
        NodeType nodeType = null;
        // int i=0;
        String type = variableElement.getAttributeValue(XMLAttributes.ROLE.toString());
        for (NodeType iNodeType : NodeType.values()) {
            if (type.contentEquals(iNodeType.toString())) {
                nodeType = iNodeType;
                break;
            }
        }
        return nodeType;
    }

    /**
     * @param variableElement
     *            . <code>Element</code>
     * @return Node X coordinate. <code>int</code>
     */
    protected int getXMLXCoordinate(Element variableElement) {
        Element coordinatesElement = variableElement.getChild(XMLTags.COORDINATES.toString());
        int xCoordinate = 0;
        if (coordinatesElement != null) {
            String xString = coordinatesElement.getAttributeValue(XMLAttributes.X.toString());
            xCoordinate = new Integer(xString);
        }
        return xCoordinate;
    }

    /**
     * @param variableElement
     *            . <code>Element</code>
     * @return Node Y coordinate. <code>int</code>
     */
    protected int getXMLYCoordinate(Element variableElement) {
        Element coordinatesElement = variableElement.getChild(XMLTags.COORDINATES.toString());
        int yCoordinate = 0;
        if (coordinatesElement != null) {
            String yString = coordinatesElement.getAttributeValue(XMLAttributes.Y.toString());
            yCoordinate = new Integer(yString);
        }
        return yCoordinate;
    }

    /**
     * @param variableElement
     *            . <code>Element</code>
     * @return variable states. <code>String[]</code>
     */
    protected State[] getXMLStates(Element variableElement) {
        List<Element> variableStatesElements = variableElement.getChildren();
        State[] states = new State[variableStatesElements.size()];
        int i = 0;
        for (Element stateElement : variableStatesElements) {
            String stateName = stateElement.getAttributeValue(XMLAttributes.NAME.toString());
            states[i++] = new State(stateName);
        }
        return states;
    }

    /**
     * @param variableElement
     *            . <code>Element</code>
     * @param variableName
     *            . <code>String</code>
     * @return A continuous variable. <code>Variable</code>
     */
    protected Variable getXMLContinuousVariable(Element variableElement, String variableName)
            throws DataConversionException {
        // Get thresholds
        List<Element> thresholds = null;
        Element thresholdsElement = variableElement.getChild(XMLTags.THRESHOLDS.toString());
        if (thresholdsElement != null) {
            thresholds = thresholdsElement.getChildren(XMLTags.THRESHOLD.toString());
        }
        boolean leftClosedDefined = false;
        boolean leftClosed = false;
        boolean rightClosedDefined = false;
        boolean rightClosed = false;
        double min = Double.NEGATIVE_INFINITY;
        double max = Double.POSITIVE_INFINITY;
        if ((thresholds != null) && (thresholds.size() > 1)) {
            Element leftThreshold = thresholds.get(0);
            String minString = leftThreshold.getAttributeValue(XMLAttributes.VALUE.toString());
            if (minString.contentEquals("-Infinity")) {
                min = Double.NEGATIVE_INFINITY;
                leftClosedDefined = true;
                leftClosed = false;
            } else {
                min = Double.parseDouble(minString);
            }
            if (!leftClosedDefined) {
                String leftClosedString = leftThreshold.getAttributeValue(XMLAttributes.BELONGS_TO.toString());
                if (leftClosedString != null) {
                    if (leftClosedString.contentEquals(XMLValues.LEFT.toString())) {
                        leftClosed = true;
                    } else if (leftClosedString.contentEquals(XMLValues.RIGHT.toString())) {
                        leftClosed = false;
                    }
                }
            }
            Element rightThreshold = thresholds.get(1);
            String maxString = rightThreshold.getAttributeValue(XMLAttributes.VALUE.toString());
            if (maxString.contentEquals("+Infinity")) {
                max = Double.POSITIVE_INFINITY;
                rightClosedDefined = true;
                rightClosed = false;
            } else {
                max = Double.parseDouble(maxString);
            }
            if (!rightClosedDefined) {
                String rightClosedString = rightThreshold.getAttributeValue(XMLAttributes.BELONGS_TO.toString());
                if (rightClosedString != null) {
                    if (rightClosedString.contentEquals(XMLValues.LEFT.toString())) {
                        rightClosed = true;
                    } else if (rightClosedString.contentEquals(XMLValues.RIGHT.toString())) {
                        rightClosed = false;
                    }
                }
            }
        }
        double precision = 0.0;
        Element xMLPrecision = variableElement.getChild(XMLTags.PRECISION.toString());
        if (xMLPrecision != null) {
            precision = Double.parseDouble(xMLPrecision.getText());
        }
        Variable variable = new Variable(variableName, leftClosed, min, max, rightClosed, precision);
        return variable;
    }

    /**
     * @param states
     * @param variableElement
     *            . <code>Element</code>
     * @param variableName
     *            . <code>String</code>
     * @return A discretized continuous variable. <code>Variable</code>
     * @throws DataConversionException
     */
    protected Variable getXMLDiscretizedVariable(Element variableElement,
            State[] states,
            String variableName)
            throws DataConversionException {
        Variable variable;
        // Continuous part. (continuous interval information is infered from
        // sub-intervals in discretized part (further on)
        if (variableElement != null) {
            List<Element> subIntervals = variableElement.getChildren();
            int numSubIntervals = subIntervals.size();
            double[] limits = new double[numSubIntervals];
            boolean[] belongsToLeftSide = new boolean[numSubIntervals];
            int numInterval = 0;
            for (Element subInterval : subIntervals) {
                limits[numInterval] = Double.valueOf(subInterval.getAttributeValue(XMLAttributes.VALUE.toString()));
                if (subInterval.getAttributeValue(XMLAttributes.BELONGS_TO.toString()).contentEquals("left"))
                    belongsToLeftSide[numInterval] = true;
                else
                    belongsToLeftSide[numInterval] = false;
                numInterval++;
                // TODO Seguir por aqui leyendo los thresholds
            }
            PartitionedInterval partitionedInterval = new PartitionedInterval(limits,
                    belongsToLeftSide);
            variable = new Variable(variableName, states);
            // partitionedInterval, precision);
            // the order of the next two statement are important
            variable.setVariableType(VariableType.DISCRETIZED);
            variable.setPartitionedInterval(partitionedInterval);
        } else
            variable = new Variable(variableName, states);
        return variable;
    }

    /**
     * @param root
     *            . <code>Element</code>
     * @param probNet
     *            . <code>ProbNet</code>
     * @throws ParserException
     * @throws Exception
     */
    protected void getLinks(Element root, ProbNet probNet)
            throws ParserException {
        Element xmlLinksRoot = root.getChild(XMLTags.LINKS.toString());
        if (xmlLinksRoot != null) {
            probNet.getGraph().makeLinksExplicit(true);
            List<Element> xmlLinks = xmlLinksRoot.getChildren();
            for (Element xmlLink : xmlLinks) {
                // get link information from xmlLink
                List<Element> variablesElement = xmlLink.getChildren(XMLTags.VARIABLE.toString());
                // TODO get timeSlice when it is implemented
                // for the time being the name contains the time slice
                try {
                    Variable variable1 = getVariable(variablesElement.get(0), probNet);
                    Variable variable2 = getVariable(variablesElement.get(1), probNet);
                    ProbNode node1 = probNet.getProbNode(variable1);
                    ProbNode node2 = probNet.getProbNode(variable2);
                    boolean directed = xmlLink.getAttribute(XMLAttributes.DIRECTED.toString()).getBooleanValue();
                    // create link
                    probNet.addLink(variable1, variable2, directed);
                    // read link restriction potential
                    Element xmlPotential = xmlLink.getChild(XMLTags.POTENTIAL.toString());
                    if (xmlPotential != null) {
                        Potential potential = getPotential(xmlPotential, probNet);
                        Link link = probNet.getGraph().getLink(node1.getNode(),
                                node2.getNode(),
                                directed);
                        link.initializesRestrictionsPotential();
                        link.setRestrictionsPotential(potential);
                    }
                    Element xmlRevelationCondition = xmlLink.getChild(XMLTags.REVELATION_CONDITIONS.toString());
                    if (xmlRevelationCondition != null) {
                        Link link = probNet.getGraph().getLink(node1.getNode(),
                                node2.getNode(),
                                directed);
                        getRevelationConditions(xmlRevelationCondition, link);
                    }
                } catch (DataConversionException e) {
                    throw new ParserException("Data conversion exception in PGMXReader.getLinks()");
                } catch (NodeNotFoundException e) {
                    throw new ParserException("Node not found in PGMXReader.getLinks()");
                }
            }
        }
    }

    protected void getRevelationConditions(Element root, Link link)
            throws ParserException {
        ProbNode node = (ProbNode) link.getNode1().getObject();
        Variable var = node.getVariable();
        List<Element> xmlStates = root.getChildren(XMLTags.STATE.toString());
        for (Element elementState : xmlStates) {
            String stateName = elementState.getAttributeValue(XMLAttributes.NAME.toString());
            int stateIndex;
            try {
                stateIndex = var.getStateIndex(stateName);
                link.addRevealingState(var.getStates()[stateIndex]);
            } catch (InvalidStateException e) {
                throw new ParserException("XMLReader exception in file line: ");
            }
        }
        List<Element> xmlThresholds = root.getChildren(XMLTags.THRESHOLD.toString());
        if (xmlThresholds.size() > 0) {
            for (int i = 0; i < xmlThresholds.size(); i += 2) {
                double[] limits = new double[2];
                boolean[] belongsToLeftSide = new boolean[2];
                for (int index = 0; index < 2; index++) {
                    Element subInterval = xmlThresholds.get(i + index);
                    limits[index] = Double.valueOf(subInterval.getAttributeValue(XMLAttributes.VALUE.toString()));
                    belongsToLeftSide[index] = subInterval.getAttributeValue(XMLAttributes.BELONGS_TO.toString()).contentEquals("left");
                }
                PartitionedInterval partitionedInterval = new PartitionedInterval(limits,
                        belongsToLeftSide);
                link.addRevealingInterval(partitionedInterval);
            }
        }
    }

    /**
     * @param root
     *            . <code>Element</code>
     * @param probNet
     *            . <code>ProbNet</code>
     * @throws ParserException
     */
    protected void getPotentials(Element root, ProbNet probNet)
            throws ParserException {
        // Pool of names for those potentials declared in this net
        Element xmlPotentialsRoot = root.getChild(XMLTags.POTENTIALS.toString());
        if (xmlPotentialsRoot != null) {
            List<Element> xmlPotentials = xmlPotentialsRoot.getChildren();
            for (Element xmlPotential : xmlPotentials) {
                Potential potential = getPotential(xmlPotential, probNet);
                probNet.addPotential(potential);
            }
        }
    }

    protected List<Variable> getReferencedVariables(Element xmlPotential, ProbNet probNet)
            throws ParserException {
        // get variables
        Element xmlRootVariables = xmlPotential.getChild(XMLTags.VARIABLES.toString());
        List<Variable> variables = new ArrayList<Variable>();
        // List of variables referenced in this potential
        if (xmlRootVariables != null) {
            List<Element> xmlVariables = xmlRootVariables.getChildren();
            int numVariables = xmlVariables.size();
            for (int i = 0; i < numVariables; i++) {
                Variable variable = getVariable(xmlVariables.get(i), probNet);
                if (!variables.contains(variable)) {
                    variables.add(variable);
                }
            }
        }
        return variables;
    }

    /**
     * @author myebra
     * @param xmlPotential
     * @param probNet
     * @return
     * @throws ParserException
     */
    protected List<TreeADDBranch> getTreeADDBranches(Element xmlPotential,
            ProbNet probNet,
            Variable topVariable,
            PotentialRole xmlRole,
            List<Variable> variables)
            throws ParserException {
        // get branches
        Element xmlRootBranches = xmlPotential.getChild(XMLTags.BRANCHES.toString());
        List<TreeADDBranch> branches = new ArrayList<TreeADDBranch>();
        List<Variable> parentVariables = new ArrayList<>(variables);
        parentVariables.remove(topVariable);
        // List of variables referenced in this potential
        if (xmlRootBranches != null) {
            List<Element> xmlBranches = xmlRootBranches.getChildren();
            int numBranches = xmlBranches.size();
            for (int i = 0; i < numBranches; i++) {
                Element xmlBranch = xmlBranches.get(i);
                Element xmlSubpotential = xmlBranch.getChild(XMLTags.POTENTIAL.toString());
                Element xmlReference = xmlBranch.getChild(XMLTags.REFERENCE.toString());
                Element xmlLabel = xmlBranch.getChild(XMLTags.LABEL.toString());
                Potential potential = null;
                String reference = null;
                if (xmlSubpotential != null) {
                    potential = getPotential(xmlSubpotential, probNet, xmlRole);
//                    // Hack for backwards compatibility
//                    if (potential.getVariables().isEmpty() && !parentVariables.isEmpty()) {
//                        potential.setVariables(parentVariables);
//                    }
                    //
                } else if (xmlReference != null) {
                    reference = xmlReference.getText();
                } else {
                    throw new ParserException("A branch should specify either a potential or a reference");
                }
                TreeADDBranch branch = null;
                if (topVariable.getVariableType() == VariableType.FINITE_STATES
                        || topVariable.getVariableType() == VariableType.DISCRETIZED) {
                    List<State> states = getBranchStates(xmlBranch, topVariable);
                    branch = (potential != null) ? new TreeADDBranch(states,
                            topVariable,
                            potential,
                            parentVariables) : new TreeADDBranch(states,
                            topVariable,
                            reference,
                            parentVariables);
                } else if (topVariable.getVariableType() == VariableType.NUMERIC) {
                    List<Threshold> thresholds = getThresholds(xmlBranch);
                    branch = (potential != null) ? new TreeADDBranch(thresholds.get(0),
                            thresholds.get(1),
                            topVariable,
                            potential,
                            parentVariables) : new TreeADDBranch(thresholds.get(0),
                            thresholds.get(1),
                            topVariable,
                            reference,
                            parentVariables);
                }
                if (xmlLabel != null) {
                    branch.setLabel(xmlLabel.getText());
                }
                branches.add(branch);
            }
        }
        return branches;
    }

    /**
     * @author myebra
     * @param xmlBranch
     * @return
     */
    protected List<Threshold> getThresholds(Element xmlBranch) {
        List<Threshold> thresholds = new ArrayList<Threshold>();
        Element xmlRootThresholds = xmlBranch.getChild(XMLTags.THRESHOLDS.toString());
        if (xmlRootThresholds != null) {
            List<Element> xmlThresholds = xmlRootThresholds.getChildren();
            int numThresholds = xmlThresholds.size();
            if (numThresholds != 2)
                try {
                    throw new ParserException("XMLReader exception: A branch can only have two thresholds");
                } catch (ParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            for (int i = 0; i < numThresholds; i++) {
                Element xmlThreshold = xmlThresholds.get(i);
                Float value = Float.parseFloat(xmlThreshold.getAttributeValue(XMLAttributes.VALUE.toString()));
                String belongsTo = xmlThreshold.getAttributeValue(XMLAttributes.BELONGS_TO.toString());
                boolean belongsToLeft = false;
                if (belongsTo.equals("left")) {
                    belongsToLeft = true;
                } else if (belongsTo.equals("right")) {
                    belongsToLeft = false;
                }
                Threshold threshold = new Threshold(value, belongsToLeft);
                thresholds.add(threshold);
            }
        }
        return thresholds;
    }

    /**
     * author mayebra
     * 
     * @param xmlBranch
     * @param topVariable
     * @return
     * @throws ParserException
     */
    protected List<State> getBranchStates(Element xmlBranch, Variable topVariable)
            throws ParserException {
        List<State> states = new ArrayList<State>();
        Element xmlRootStates = xmlBranch.getChild(XMLTags.STATES.toString());
        if (xmlRootStates != null) {
            List<Element> xmlStates = xmlRootStates.getChildren();
            int numStates = xmlStates.size();
            for (int i = 0; i < numStates; i++) {
                Element xmlState = xmlStates.get(i);
                String stateName = xmlState.getAttributeValue(XMLAttributes.NAME.toString());
                int stateIndex = -1;
                try {
                    stateIndex = topVariable.getStateIndex(stateName);
                } catch (InvalidStateException e) {
                    throw new ParserException("XMLReader exception: Unknown state name +'"
                            + stateName
                            + "'");
                }
                states.add(topVariable.getStates()[stateIndex]);
            }
        }
        return states;
    }

    /**
     * @param xmlPotential
     * @param probNet
     * @param xmlRole
     * @param variables
     * @return
     * @throws ParserException
     */
    protected UniformPotential getUniformPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables)
            throws ParserException {
        return new UniformPotential(variables, xmlRole);
    }

    protected ProductPotential getProductPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables)
            throws ParserException {
        return new ProductPotential(variables, xmlRole);
    }

    protected TablePotential getTablePotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables)
            throws ParserException {
        // get table
        Element xmlRootTable = xmlPotential.getChild(XMLTags.VALUES.toString());
        double[] table = parseDoubles(xmlRootTable.getTextNormalize());
        TablePotential tablePotential = new TablePotential(variables, xmlRole, table);
        Element xmlRootUncertainValues = xmlPotential.getChild(XMLTags.UNCERTAIN_VALUES.toString());
        if (xmlRootUncertainValues != null) {
            tablePotential.setUncertaintyTable(getUncertainValues(xmlRootUncertainValues));
        }
        return tablePotential;
    }

    protected UncertainValue[] getUncertainValues(Element xmlRootUncertainTable) {
        int valuesSize;
        List<Element> values;
        values = xmlRootUncertainTable.getChildren();
        valuesSize = values.size();
        UncertainValue[] uncertainTable = new UncertainValue[valuesSize];
        for (int i = 0; i < valuesSize; i++) {
            Element xmlUncertainValue = values.get(i);
            uncertainTable[i] = getUncertainValue(xmlUncertainValue);
        }
        return uncertainTable;
    }

    protected UncertainValue getUncertainValue(Element xmlUncertainValue) {
        UncertainValue auxUncertainValue = null;
        String functionName = xmlUncertainValue.getAttributeValue(XMLAttributes.DISTRIBUTION.toString());
        if (functionName != null) {
            String name = xmlUncertainValue.getAttributeValue(XMLAttributes.NAME.toString());
            String[] arguments = xmlUncertainValue.getTextNormalize().split(" ");
            double[] parameters = new double[arguments.length];
            for(int i=0; i<parameters.length;++i)
            {
                parameters[i] = Double.parseDouble(arguments[i]);
            }
            ProbDensFunction function = ProbDensFunctionManager.getUniqueInstance().newInstance(functionName, parameters);
            auxUncertainValue = new UncertainValue(function, name);
        }
        return auxUncertainValue;
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @param xmlPotential
     *            . <code>Element</code>
     * @param mapPotentialRefs
     *            A <code>HashMap</code> used as cache of names for the declared
     *            potentials of the net.
     * @return <code>Potential</code>
     * @throws ParserException
     */
    protected Potential getPotential(Element xmlPotential, ProbNet probNet)
            throws ParserException {
        String xmlPotentialRole = xmlPotential.getAttributeValue(XMLAttributes.ROLE.toString());
        PotentialRole xmlRole = PotentialRole.getEnumMember(xmlPotentialRole);
        return getPotential(xmlPotential, probNet, xmlRole);
    }

    /**
     * @param probNet
     *            . <code>ProbNet</code>
     * @param xmlPotential
     *            . <code>Element</code>
     * @param mapPotentialRefs
     *            A <code>HashMap</code> used as cache of names for the declared
     *            potentials of the net.
     * @return <code>Potential</code>
     * @throws ParserException
     */
    protected Potential getPotential(Element xmlPotential, ProbNet probNet, PotentialRole xmlRole)
            throws ParserException {
        Potential potential = null;
        // get type and role of potential
        String sXmlPotentialType = xmlPotential.getAttributeValue(XMLAttributes.TYPE.toString());
        PotentialType xmlType = PotentialType.getEnumMember(sXmlPotentialType);
        List<Variable> variables = getReferencedVariables(xmlPotential, probNet);
        switch (xmlType) {
        case UNIFORM:
            potential = getUniformPotential(xmlPotential, probNet, xmlRole, variables);
            break;
        case TABLE:
            potential = getTablePotential(xmlPotential, probNet, xmlRole, variables);
            break;
        case TREE_ADD:
            potential = getTreeADDPotential(xmlPotential, probNet, xmlRole, variables);
            break;
        case CYCLE_LENGTH_SHIFT:
            potential = getCycleLengthShiftPotential(xmlPotential, probNet, xmlRole, variables);
            break;
        case SAME_AS_PREVIOUS:
            potential = getSameAsPrevious(xmlPotential, probNet, xmlRole, variables);
            break;
        case SUM:
            potential = getSumPotential(xmlPotential, probNet, xmlRole, variables);
            break;
        case PRODUCT:
            potential = getProductPotential(xmlPotential, probNet, xmlRole, variables);
            break;
        case ICIMODEL:
            potential = getICIPotential(xmlPotential, probNet, xmlRole, variables);
            break;
        case WEIBULL_HAZARD:
            potential = getWeibullPotential(xmlPotential, probNet, xmlRole, variables);
            break;
        case EXPONENTIAL_HAZARD:
            potential = getExponentialHazardPotential(xmlPotential, probNet, xmlRole, variables);
            break;
        case LINEAR_REGRESSION:
            potential = getLinearRegressionPotential(xmlPotential, probNet, xmlRole, variables);
            break;
        case DELTA:
            potential = getDeltaPotential(xmlPotential, probNet, xmlRole, variables); 
            break;
        case EXPONENTIAL:
            potential = getExponentialPotential(xmlPotential, probNet, xmlRole, variables); 
            break;
        default:
            throw new ParserException("XMLReader exception: Potential type "
                    + xmlType.toString()
                    + " not supported");
        }

        Variable utilityVariable = null;
        if (xmlRole == PotentialRole.UTILITY) {
            Element xmlUtilityVariable = xmlPotential.getChild(XMLTags.UTILITY_VARIABLE.toString());
            if (xmlUtilityVariable != null) {
                utilityVariable = getVariable(xmlUtilityVariable, probNet);
                potential.setUtilityVariable(utilityVariable);
            }
        }
        
        Element xmlComment = xmlPotential.getChild(XMLTags.COMMENT.toString());
        if(xmlComment != null)
        {
            potential.setComment(xmlComment.getText());
        }
        return potential;
    }

    /**
     * @author myebra
     * @param xmlPotential
     * @param probNet
     * @param xmlRole
     * @param variables
     * @param mapPotentialRefs
     * @return
     * @throws ParserException
     */
    protected TreeADDPotential getTreeADDPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables)
            throws ParserException {
        Variable topVariable = null;
        // Read the PotentialRole: innerPotentials of this TreeADD must have the
        // same PotentialRole
        if (xmlRole != PotentialRole.UTILITY && xmlRole != PotentialRole.CONDITIONAL_PROBABILITY) {
            throw new ParserException("XMLReader exception: Potential role "
                    + xmlRole.toString()
                    + "not supported inside TreeADD potential");
        }
        Element xmlTopVariable = xmlPotential.getChild(XMLTags.TOP_VARIABLE.toString());
        if (xmlTopVariable != null) {
            topVariable = getVariable(xmlTopVariable, probNet);
        }
        // Recursive reading of tree structure
        List<TreeADDBranch> branches = getTreeADDBranches(xmlPotential,
                probNet,
                topVariable,
                xmlRole,
                variables);
        // Builds the tree potential from the graph
        TreeADDPotential treeADDPotential = new TreeADDPotential(variables,
                topVariable,
                xmlRole,
                branches);

        return treeADDPotential;
    }

    /**
     * Creates an instance of ICIPotential given an XML node
     * 
     * @param xmlPotential
     * @param probNet
     * @param xmlRole
     * @param variables
     * @return
     * @throws ParserException
     */
    protected Potential getICIPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables)
            throws ParserException {
        Element xmlModel = xmlPotential.getChild(XMLTags.MODEL.toString());
        ICIPotential iciPotential = null;
        if (xmlModel.getText().equals(MaxPotential.class.getAnnotation(RelationPotentialType.class).name())
                || xmlModel.getText().equals("GeneralizedMax")) {
            iciPotential = new MaxPotential(variables);
        } else if (xmlModel.getText().equals(MinPotential.class.getAnnotation(RelationPotentialType.class).name())
                || xmlModel.getText().equals("GeneralizedMin")) {
            iciPotential = new MinPotential(variables);
        } else if (xmlModel.getText().equals(TuningPotential.class.getAnnotation(RelationPotentialType.class).name())) {
            iciPotential = new TuningPotential(variables);
        }
        for (Element subpotential : xmlPotential.getChild(XMLTags.SUBPOTENTIALS.toString()).getChildren()) {
            List<Element> subpotentialVariables = subpotential.getChild(XMLTags.VARIABLES.toString()).getChildren();
            double[] values = parseDoubles(subpotential.getChild(XMLTags.VALUES.toString()).getTextNormalize());
            if (subpotentialVariables.size() > 1) {
                Variable variable = getVariable(subpotentialVariables.get(1), probNet);
                iciPotential.setNoisyParameters(variable, values);
            } else {
                getVariable(subpotentialVariables.get(0), probNet);
                iciPotential.setLeakyParameters(values);
            }
        }
        return iciPotential;
    }

    /**
     * 
     * @param xmlPotential
     * @param probNet
     * @param xmlRole
     * @param variables
     * @return
     * @throws ParserException 
     */
    protected Potential getWeibullPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables) throws ParserException {
        WeibullHazardPotential potential = new WeibullHazardPotential(variables, xmlRole);
        Element xmlTimeVariable = xmlPotential.getChild(XMLTags.TIME_VARIABLE.toString());
        if(xmlTimeVariable != null)
        {
            String variableName = xmlTimeVariable.getAttributeValue(XMLAttributes.NAME.toString());
            String timeSlice = xmlTimeVariable.getAttributeValue(XMLAttributes.TIMESLICE.toString());
            try {
                Variable timeVariable = probNet.getVariable(variableName, Integer.parseInt(timeSlice));
                potential.setTimeVariable(timeVariable);
            } catch (ProbNodeNotFoundException e) {
                e.printStackTrace();
                throw new ParserException(e.getMessage());
            }
        }
        getRegressionPotential(xmlPotential, potential);
        return potential;
    }
    
    protected Potential getExponentialHazardPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables) {
        ExponentialHazardPotential potential = new ExponentialHazardPotential(variables, xmlRole);
        getRegressionPotential(xmlPotential, potential);
        return potential;
    }    
    
    private Potential getExponentialPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables) {
        ExponentialPotential potential = new ExponentialPotential(variables, xmlRole);
        getRegressionPotential(xmlPotential, potential);
        return potential;
    }    
    
    protected Potential getLinearRegressionPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables) {
        LinearRegressionPotential potential = new LinearRegressionPotential(variables, xmlRole);
        getRegressionPotential(xmlPotential, potential);
        return potential;
    }  
    
    protected void getRegressionPotential(Element xmlPotential, RegressionPotential potential) {
        Element xmlCoefficients = xmlPotential.getChild(XMLTags.COEFFICIENTS.toString());
        potential.setCoefficients(parseDoubles(xmlCoefficients.getText()));
        
        Element xmlCovariates = xmlPotential.getChild(XMLTags.COVARIATES.toString());
        if(xmlCovariates != null)
        {
            potential.setCovariates(getCovariates(xmlCovariates));
        }
        
        Element xmlCovarianceMatrix = xmlPotential.getChild(XMLTags.COVARIANCE_MATRIX.toString());
        Element xmlCholeskyDecomposition = xmlPotential.getChild(XMLTags.CHOLESKY_DECOMPOSITION.toString());
        if(xmlCovarianceMatrix != null)
        {
            potential.setCovarianceMatrix(parseDoubles(xmlCovarianceMatrix.getText()));
        }else if(xmlCholeskyDecomposition != null)
        {
            potential.setCholeskyDecomposition(parseDoubles(xmlCholeskyDecomposition.getText()));
        }        
    }
    
    /**
     * 
     * @param xmlPotential
     * @param probNet
     * @param role
     * @param variables
     * @return
     * @throws ParserException 
     */
    private Potential getDeltaPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole role,
            List<Variable> variables) throws ParserException {
        DeltaPotential deltaPotential = null; 
                
        Element xmlNumericValue = xmlPotential.getChild(XMLTags.NUMERIC_VALUE.toString());
        Element xmlState = xmlPotential.getChild(XMLTags.STATE.toString());
        Element xmlStateIndex = xmlPotential.getChild(XMLTags.STATE_INDEX.toString());
        if(xmlNumericValue != null)
        {
            double value = Double.parseDouble(xmlNumericValue.getText());
            deltaPotential = new DeltaPotential(variables, role, value);
        }else if(xmlState != null)
        {
            State state = new State(xmlState.getText());
            deltaPotential = new DeltaPotential(variables, role, state);
        }else if(xmlStateIndex != null)
        {
            int stateIndex = Integer.parseInt(xmlStateIndex.getText());
            State state = variables.get(0).getStates()[stateIndex];
            deltaPotential = new DeltaPotential(variables, role, state);
        }else
        {
            throw new ParserException("A delta potential has to specify either a State, a StateIndex or a NumericValue");
        }
        return deltaPotential;
    }    

    protected Potential getSumPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables)
            throws ParserException {
        return new SumPotential(variables, xmlRole);
    }

    protected Potential getSameAsPrevious(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables)
            throws ParserException {
        SameAsPrevious sameAsPrevious = null;
        Variable variable = variables.get(0);
        if (variable.isTemporal()) {
            // TODO revisar si el orden en que se cargan los potenciales no
            // afecta a la siguiente línea
            try {
                sameAsPrevious = new SameAsPrevious(probNet, variable, 1);
            } catch (ProbNodeNotFoundException e) {
                throw new ParserException("XMLReader exception: "
                        + "the SameAsPrevious potential cannot be created to "
                        + variable.getName()
                        + " variable");
            }
        } else {
            throw new ParserException("XMLReader exception: "
                    + "can not assign a SameAsPrevious potential to static variable");
        }
        return sameAsPrevious;
    }

    /***
     * Gets the
     * <code>CycleLengthShift<code> potential for temporal chance variable
     * 
     * @param xmlPotential
     * @param probNet
     * @param xmlRole
     * @param variables
     * @return
     * @throws ParserException
     */
    protected Potential getCycleLengthShiftPotential(Element xmlPotential,
            ProbNet probNet,
            PotentialRole xmlRole,
            List<Variable> variables)
            throws ParserException {
        Potential cycleLengthShift = null;
        if (xmlRole == PotentialRole.UTILITY) {
            throw new ParserException("XMLReader exception: "
                    + "can not assign a CycleLengthShift potential to utility variable");
        } else {
            Variable variable = variables.get(0);
            if (variable.isTemporal()) {
                cycleLengthShift = new CycleLengthShift(variables);
            } else {
                throw new ParserException("XMLReader exception: "
                        + "can not assign a CycleLengthShift potential: "
                        + variables
                        + " to a static variable: "
                        + variable);
            }
        }
        return cycleLengthShift;
    }

    protected double[] parseDoubles(String string) {
        String[] sValues = string.split(" ");
        double[] table = new double[sValues.length];
        int i = 0;
        for (String sValue : sValues) {
            table[i++] = Double.parseDouble(sValue);
        }
        return table;
    }
    
    private String[] getCovariates(Element xmlCovariates) {
        String[] covariates = new String[xmlCovariates.getChildren().size()];
        int i= 0;
        for(Element xmlCovariate : xmlCovariates.getChildren())
        {
            covariates[i++] = xmlCovariate.getText();
        }
        return covariates;
    }    

/**
	 * transform a text in a HTML string, if possible by a full substitution of
	 * the special characters "SymbolLT" and "SymbolGT" in the equivalent "<" 
	 * and ">"
	 * 
	 * Please, pay attention that the equivalent format "&lt" and "&gt" are not
	 * used here as JDOM is using the character "&" to start a definition of an
	 * entity Ref class, so we need to avoid it.
	 */
    protected String textToHtml(String htmlSection) {
        String result = htmlSection;
        result = result.replaceAll("SymbolLT", "<");
        result = result.replaceAll("SymbolGT", ">");
        return result;
    }

    // TODO OOPN start
    /**
     * @param netName
     * @param root
     *            . <code>Element</code>
     * @param probNet
     *            . <code>ProbNet</code>
     * @param classes
     * @throws ParserException
     */
    protected void getOOPN(String netName,
            Element root,
            ProbNet probNet,
            Map<String, ProbNet> classes)
            throws ParserException {
        if (probNet instanceof OOPNet) {
            OOPNet ooNet = (OOPNet) probNet;
            Element xmlOONRoot = root.getChild(XMLTags.OOPN.toString());
            if (xmlOONRoot != null) {
                LinkedHashMap<String, ProbNet> localClasses = new LinkedHashMap<>();
                Element xmlClassesRoot = xmlOONRoot.getChild(XMLTags.CLASSES.toString());
                if (xmlClassesRoot != null) {
                    List<Element> xmlClasses = xmlClassesRoot.getChildren();
                    for (Element xmlClass : xmlClasses) {
                        String name = xmlClass.getAttributeValue("name");
                        localClasses.put(name, getProbNet(xmlClass, name, localClasses));
                    }
                    ooNet.setClasses(localClasses);
                }
                classes.putAll(localClasses);
                Element xmlInstancesRoot = xmlOONRoot.getChild(XMLTags.INSTANCES.toString());
                if (xmlInstancesRoot != null) {
                    List<Element> xmlInstances = xmlInstancesRoot.getChildren();
                    for (Element xmlInstance : xmlInstances) {
                        String name = xmlInstance.getAttributeValue("name");
                        boolean isInput = Boolean.parseBoolean(xmlInstance.getAttributeValue("isInput"));
                        String folder = new File(netName).getParent();
                        String className = xmlInstance.getAttributeValue("class");
                        if (!classes.containsKey(className)) {
                            classes.put(className,
                                    loadProbNet(folder + "\\" + className).getProbNet());
                        }
                        ProbNet classNet = classes.get(className);
                        List<ProbNode> instanceNodes = new ArrayList<ProbNode>();
                        // build this list from current node list and classNet
                        for (ProbNode probNode : classNet.getProbNodes()) {
                            try {
                                instanceNodes.add(probNet.getProbNode(name
                                        + "."
                                        + probNode.getName()));
                            } catch (ProbNodeNotFoundException e) {
                                throw new ParserException(e.getMessage());
                            }
                        }
                        Instance instance = new Instance(name, classNet, instanceNodes, isInput);
                        try {
                            ooNet.addInstance(instance);
                        } catch (InstanceAlreadyExistsException ignore) {
                        }
                        if (xmlInstance.getAttributeValue("arity") != null) {
                            ParameterArity arity = ParameterArity.parseArity(xmlInstance.getAttributeValue("arity"));
                            instance.setArity(arity);
                        }
                    }
                    Element xmlReferenceLinksRoot = xmlOONRoot.getChild(XMLTags.REFERENCE_LINKS.toString());
                    if (xmlReferenceLinksRoot != null) {
                        List<Element> xmlReferenceLinks = xmlReferenceLinksRoot.getChildren();
                        for (Element xmlReferenceLink : xmlReferenceLinks) {
                            String source = xmlReferenceLink.getAttributeValue("source");
                            String destination = xmlReferenceLink.getAttributeValue("destination");
                            String type = xmlReferenceLink.getAttributeValue("type");
                            ReferenceLink link = null;
                            if (type.equalsIgnoreCase("instance")) {
                                String paramName = xmlReferenceLink.getAttributeValue("parameter");
                                link = new InstanceReferenceLink(ooNet.getInstances().get(source),
                                        ooNet.getInstances().get(destination),
                                        ooNet.getInstances().get(destination).getSubInstances().get(paramName));
                            } else if (type.equalsIgnoreCase("node")) {
                                try {
                                    link = new NodeReferenceLink(ooNet.getProbNode(source),
                                            ooNet.getProbNode(destination));
                                } catch (ProbNodeNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            ooNet.addReferenceLink(link);
                        }
                    }
                }
            }
        }
    }

    // TODO OOPN end
    /**
     * @param root
     * @param probNet
     * @throws ParserException
     */
    private void getPolicies(Element root, ProbNet probNet)
            throws ParserException {
        Element policiesRoot = root.getChild(XMLTags.POLICIES.toString());
        if (policiesRoot != null) {
            List<Element> xmlPotentialPolicies = policiesRoot.getChildren();
            for (Element xmlPotential : xmlPotentialPolicies) {
                Potential potential = getPotential(xmlPotential, probNet);
                probNet.addPotential(potential);
            }
        }
    }
}