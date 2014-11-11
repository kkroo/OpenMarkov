/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.graphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * This class implements the graphic representation of each state that a node
 * has.
 * @author asaez
 * @version 1.0
 */
public class VisualState extends VisualElement
{
    /**
     * Font type Helvetica, plain, size 11.
     */
    protected static final Font STATES_FONT           = new Font ("Helvetica", Font.PLAIN, 11);
    /**
     * Color for the text of the state's name.
     */
    private static final Color  TEXT_COLOR            = Color.BLACK;
    /**
     * Color associated to the Evidence Case number N+0 (where N = [0, 5, 10
     * ,...]).
     */
    public static final Color   EVIDENCE_CASE_0_COLOR = Color.RED;
    /**
     * Color associated to the Evidence Case number N+1 (where N = [0, 5, 10
     * ,...]).
     */
    public static final Color   EVIDENCE_CASE_1_COLOR = Color.BLUE;
    /**
     * Color associated to the Evidence Case number N+2 (where N = [0, 5, 10
     * ,...]).
     */
    public static final Color   EVIDENCE_CASE_2_COLOR = Color.ORANGE;
    /**
     * Color associated to the Evidence Case number N+3 (where N = [0, 5, 10
     * ,...]).
     */
    public static final Color   EVIDENCE_CASE_3_COLOR = Color.MAGENTA;
    /**
     * Color associated to the Evidence Case number N+4 (where N = [0, 5, 10
     * ,...]).
     */
    public static final Color   EVIDENCE_CASE_4_COLOR = Color.YELLOW;

    /**
     * Number of decimals
     */
    public static final int   NUMBER_OF_DECIMALS = 5;
    
    /**
     * The VisualNode this State is associated to.
     */
    private VisualNode          visualNode;
    /**
     * The order number assigned to this State. Determines in which position
     * will be painted this state.
     */
    private int                 stateNumber;
    /**
     * The name assigned to this State.
     */
    private String              stateName;
    /**
     * Array of values assigned to the state. There is one value for each
     * evidence case in memory.
     */
    private ArrayList<Double>   stateValues;
    /**
     * This variable indicates which is the position of the arrayList currently
     * selected (corresponding with the current evidence case).
     */
    private int                 currentStateValue;
    /**
     * Array of booleans that determine whether the state has evidence or not
     */
    private List<Boolean>       evidence              = new ArrayList<> ();

    /**
     * Formatting string for values shown in the visual state
     */
    private String formattingString = "0.";

    /**
     * Creates a new State.
     * @param visualNode visualNode to which this State is associated.
     * @param number order number to be assigned to this State inside the inner
     *            box.
     * @param name name of this state.
     * @param numValues Number of values that has to have each visual state.
     */
    public VisualState (VisualNode visualNode, int number, String name, int numValues)
    {
        this.visualNode = visualNode;
        this.stateNumber = number;
        this.stateName = name;
        stateValues = new ArrayList<Double> (numValues);
        for (int i = 0; i < numValues; i++)
        {
            stateValues.add (0.0);
        }
        evidence = new ArrayList<> ();
        evidence.add (false);
        currentStateValue = 0;
        StringBuilder sb = new StringBuilder(formattingString);
        for(int i=0; i < NUMBER_OF_DECIMALS;++i)
        {
            sb.append("0");
        }
        formattingString = sb.toString();
    }
    
    /**
     * Creates a new State.
     * @param visualNode visualNode to which this State is associated.
     * @param number order number to be assigned to this State inside the inner
     *            box.
     * @param name name of this state.
     */
    public VisualState (VisualNode visualNode, int number, String name)
    {
        this(visualNode, number, name, 1);
    }    

    /**
     * Returns the visualNode to which this sate is associated.
     * @return visualNode to which this sate is associated.
     */
    public VisualNode getVisualNode ()
    {
        return visualNode;
    }

    /**
     * Sets the visualNode to which this sate is associated.
     * @param visualNode the visualNode to which this sate is associated.
     */
    public void setVisualNode (VisualNode visualNode)
    {
        this.visualNode = visualNode;
    }

    /**
     * Returns the order number assigned to this state.
     * @return order number assigned to this state.
     */
    public int getStateNumber ()
    {
        return stateNumber;
    }

    /**
     * Sets the order number of this state.
     * @param stateNumber the order number of this state.
     */
    public void setStateNumber (int stateNumber)
    {
        this.stateNumber = stateNumber;
    }

    /**
     * Returns the name assigned to this state.
     * @return name assigned to this state.
     */
    public String getStateName ()
    {
        return stateName;
    }

    /**
     * Sets the name of this state.
     * @param stateName the name of this state.
     */
    public void setStateName (String stateName)
    {
        this.stateName = stateName;
    }

    /**
     * Sets which is the position of the array of values that is selected.
     * @param currentStateValue the position of the array of values to be set.
     */
    public void setCurrentStateValue (int currentStateValue)
    {
        this.currentStateValue = currentStateValue;
    }

    /**
     * Creates a new position in the array of values of the visual state It is
     * initially assigned 0.0 to this new position
     */
    public void createNewStateValue ()
    {
        stateValues.add (0.0);
        evidence.add (false);
    }

    /**
     * Clears all the positions in the array of values of the visual state and
     * creates again the initial position assigning 0.0 to it
     */
    public void clearAllStateValues ()
    {
        stateValues.clear ();
        stateValues.add (0, 0.0);
        evidence.clear ();
        evidence.add (false);
    }

    /**
     * Sets the value of this state for the given position of the array (this
     * position matches the evidence case number). The value is truncated so it
     * only has NUMBER_OF_DECIMALS decimals
     * @param caseNumber the position in the array to be established
     * @param value the value to be set
     */
    public void setStateValue (int caseNumber, double value)
    {
        try
        {
            // Value is currently formatted fixely with 4 decimals
            double truncatedValue = (Math.rint(value * Math.pow(10, NUMBER_OF_DECIMALS)))
                    / Math.pow(10, NUMBER_OF_DECIMALS);
            stateValues.set (caseNumber, truncatedValue);
        }
        catch (Exception exc)
        {
            JOptionPane.showMessageDialog (null,
                                           "ERROR" + "\n\n" + exc.getMessage (),
                                           StringDatabase.getUniqueInstance ().getString ("ExceptionGeneric.Title.Label"),
                                           JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns the number of positions in the array. This number is the same
     * that the number of evidence cases in memory and the same that the number
     * of bars that should be painted
     * @return the number of bars to be painted for that state.
     */
    public int getNumberOfValues ()
    {
        return stateValues.size ();
    }

    /**
     * Calculates the position that this state occupies inside the inner box.
     * This position is reserve
     * @return the position that this state occupies inside the inner box
     */
    private int getStatePosition ()
    {
        InnerBox innerBox = (InnerBox) visualNode.getInnerBox ();
        if (innerBox instanceof FSVariableBox)
        {
            return (((FSVariableBox) innerBox).getNumStates () - stateNumber);
        }
        else
        {
            return 1;
        }
    }

    /**
     * Sets the color in which to paint depending on which is the associated
     * evidence case
     * @param caseNumber number of the evidence case
     * @param g graphics object where paint the node.
     */
    private void setColorCaseDependent (int caseNumber, Graphics2D g)
    {
        if (caseNumber % 5 == 0)
        {
            g.setPaint (EVIDENCE_CASE_0_COLOR);
        }
        else if (caseNumber % 5 == 1)
        {
            g.setPaint (EVIDENCE_CASE_1_COLOR);
        }
        else if (caseNumber % 5 == 2)
        {
            g.setPaint (EVIDENCE_CASE_2_COLOR);
        }
        else if (caseNumber % 5 == 3)
        {
            g.setPaint (EVIDENCE_CASE_3_COLOR);
        }
        else if (caseNumber % 5 == 4)
        {
            g.setPaint (EVIDENCE_CASE_4_COLOR);
        }
    }

    /**
     * Paint the representation of the state when it is its not compiled form
     * @param x x coordinate reference for painting
     * @param y y coordinate reference for painting
     * @param g graphics object where paint the node.
     */
    private void paintNotCompiled (Double x, Double y, Graphics2D g)
    {
        Double aux1 = x;
        int aux2 = new Double (InnerBox.BAR_FULL_LENGTH / 20).intValue ();
        while (aux1 < (x + InnerBox.BAR_FULL_LENGTH))
        {
            g.drawLine (aux1.intValue () + (aux2 / 2),
                        new Double (y + InnerBox.BAR_HEIGHT / 2).intValue (), aux1.intValue ()
                                                                              + aux2 + (aux2 / 2),
                        new Double (y + InnerBox.BAR_HEIGHT / 2).intValue ());
            aux1 += (aux2 * 2);
        }
    }

    /**
     * Returns a fictitious rectangular shape around the state. This shape has a
     * height equivalent to the sum of the height of all the bars of the state
     * (a narrow margin is added) and its width includes the text of the name
     * and the numerical value (a margin is also added).
     * @param g graphics object where paint the node.
     * @return shape of the State.
     */
    public Shape getShape (Graphics2D g)
    {
        Double x = visualNode.getUpperLeftCornerX (g) + InnerBox.INTERNAL_MARGIN
                   + InnerBox.STATES_INDENT - 1;
        Double w = InnerBox.BOX_WIDTH - (InnerBox.STATES_INDENT * 2) + 1;
        Double y = 0.0;
        Double h = 0.0;
        if (visualNode.getVisualNetwork ().isPropagationActive ())
        {
            y = visualNode.getUpperLeftCornerY (g) + visualNode.getTextHeight (g)
                + InnerBox.INTERNAL_MARGIN
                + (InnerBox.STATES_VERTICAL_SEPARATION * getStatePosition ())
                + ((stateValues.size () - 1) * InnerBox.BAR_HEIGHT * (getStatePosition () - 1))
                - InnerBox.BAR_HEIGHT - 4;
            h = (InnerBox.BAR_HEIGHT * stateValues.size ()) + 4;
        }
        else
        {
            y = visualNode.getUpperLeftCornerY (g) + visualNode.getTextHeight (g)
                + InnerBox.INTERNAL_MARGIN
                + (InnerBox.STATES_VERTICAL_SEPARATION * getStatePosition ()) - InnerBox.BAR_HEIGHT
                - 4;
            h = InnerBox.BAR_HEIGHT + 4;
        }
        return new Rectangle2D.Double (x, y, w, h);
    }

    /**
     * Paints the three parts of the visual representation of a state: - The
     * state's name. - Horizontal bars which lengths are proportional to the
     * values assigned to the state for each of the evidence cases in memory. -
     * The value assigned to the state for the current evidence case.
     * @param g graphics object where paint the node.
     */
    public void paint (Graphics2D g)
    {
        Double xName = 0.0;
        Double xBar = 0.0;
        Double xValue = 0.0;
        Double yText = 0.0;
        Double yFirstBar = 0.0;
        xName = visualNode.getUpperLeftCornerX (g) + InnerBox.INTERNAL_MARGIN
                + InnerBox.STATES_INDENT;
        if (visualNode instanceof VisualUtilityNode)
        {
            xBar = xName + InnerBox.BAR_HORIZONTAL_POSITION_UTILITY;
            xValue = xName + InnerBox.VALUE_HORIZONTAL_POSITION_UTILITY;
        }
        else
        {
            xBar = xName + InnerBox.BAR_HORIZONTAL_POSITION;
            xValue = xName + InnerBox.VALUE_HORIZONTAL_POSITION;
        }
        if (visualNode.getVisualNetwork ().isPropagationActive ())
        {
            yText = visualNode.getUpperLeftCornerY (g) + visualNode.getTextHeight (g)
                    + InnerBox.INTERNAL_MARGIN
                    + (InnerBox.STATES_VERTICAL_SEPARATION * getStatePosition ())
                    + ((stateValues.size () - 1) * InnerBox.BAR_HEIGHT * (getStatePosition () - 1))
                    + (((stateValues.size () - 1) * InnerBox.BAR_HEIGHT) / 2);
            yFirstBar = visualNode.getUpperLeftCornerY (g)
                        + visualNode.getTextHeight (g)
                        + InnerBox.INTERNAL_MARGIN
                        + (InnerBox.STATES_VERTICAL_SEPARATION * getStatePosition ())
                        + ((stateValues.size () - 1) * InnerBox.BAR_HEIGHT * (getStatePosition () - 1))
                        - InnerBox.BAR_HEIGHT - 1;
        }
        else
        {
            yText = visualNode.getUpperLeftCornerY (g) + visualNode.getTextHeight (g)
                    + InnerBox.INTERNAL_MARGIN
                    + (InnerBox.STATES_VERTICAL_SEPARATION * getStatePosition ());
            yFirstBar = visualNode.getUpperLeftCornerY (g) + visualNode.getTextHeight (g)
                        + InnerBox.INTERNAL_MARGIN
                        + (InnerBox.STATES_VERTICAL_SEPARATION * getStatePosition ())
                        - InnerBox.BAR_HEIGHT - 1;
        }
        g.setPaint (TEXT_COLOR);
        g.setFont (STATES_FONT);
        stateName = adjustText (stateName, InnerBox.BAR_HORIZONTAL_POSITION, 2, STATES_FONT, g);
        g.drawString (stateName, xName.intValue (), yText.intValue ());
        if (getVisualNode ().getVisualNetwork ().isPropagationActive ())
        {
            for (int i = 0; i < stateValues.size (); i++)
            {
                g.setPaint (Color.BLACK);
                g.drawLine (new Double (xBar - 1).intValue (),
                            new Double (yFirstBar + (i * InnerBox.BAR_HEIGHT) - 1).intValue (),
                            new Double (xBar - 1).intValue (),
                            new Double (yFirstBar + (i * InnerBox.BAR_HEIGHT) + InnerBox.BAR_HEIGHT).intValue ());
                g.drawLine (new Double (xBar + InnerBox.BAR_FULL_LENGTH).intValue (),
                            new Double (yFirstBar + (i * InnerBox.BAR_HEIGHT) - 1).intValue (),
                            new Double (xBar + InnerBox.BAR_FULL_LENGTH).intValue (),
                            new Double (yFirstBar + (i * InnerBox.BAR_HEIGHT) + InnerBox.BAR_HEIGHT).intValue ());
                setColorCaseDependent (i, g);
                double barLength = 0.0;
                if (visualNode instanceof VisualUtilityNode)
                {
                    InnerBox innerBox = visualNode.getInnerBox ();
                    Double minRange = ((ExpectedValueBox) innerBox).getMinUtilityRange ();
                    Double maxRange = ((ExpectedValueBox) innerBox).getMaxUtilityRange ();
                    Double range = maxRange - minRange;
                    Double value = stateValues.get (i) - minRange;
                    barLength = (value * 100) / range;
                }
                else
                {
                    barLength = (stateValues.get (i) * 10000) / InnerBox.BAR_FULL_LENGTH;
                }
                g.fill (new Rectangle2D.Double (xBar, yFirstBar + (i * InnerBox.BAR_HEIGHT),
                                                barLength, InnerBox.BAR_HEIGHT));
                setColorCaseDependent (currentStateValue, g);
                
                // Value is currently formatted fixely with 4 decimals
                DecimalFormat decimalFormat = new DecimalFormat (
                                                                 formattingString,
                                                                 new DecimalFormatSymbols (
                                                                                           Locale.US));
                String formattedValue = String.valueOf (decimalFormat.format (stateValues.get (currentStateValue)));
                g.drawString (formattedValue, (xValue.intValue ()), yText.intValue ());
            }
        }
        else
        {
            g.setPaint (Color.BLACK);
            g.drawLine (new Double (xBar - 1).intValue (), new Double (yFirstBar - 1).intValue (),
                        new Double (xBar - 1).intValue (),
                        new Double (yFirstBar + InnerBox.BAR_HEIGHT).intValue ());
            g.drawLine (new Double (xBar + InnerBox.BAR_FULL_LENGTH).intValue (),
                        new Double (yFirstBar - 1).intValue (),
                        new Double (xBar + InnerBox.BAR_FULL_LENGTH).intValue (),
                        new Double (yFirstBar + InnerBox.BAR_HEIGHT).intValue ());
            if (getVisualNode ().hasAnyFinding ())
            {
                if (evidence.get (currentStateValue))
                {
                    setColorCaseDependent (currentStateValue, g);
                    g.fill (new Rectangle2D.Double (xBar, yFirstBar, InnerBox.BAR_FULL_LENGTH,
                                                    InnerBox.BAR_HEIGHT));
                    g.setPaint (Color.BLACK);
                }
                else
                {
                    paintNotCompiled (xBar, yFirstBar, g);
                }
            }
            else
            {
                paintNotCompiled (xBar, yFirstBar, g);
            }
        }
        g.setPaint (TEXT_COLOR);
    }

    public void removeFinding ()
    {
        evidence.set (currentStateValue, false);
    }

    public void addFinding ()
    {
        evidence.set (currentStateValue, true);
    }
}
