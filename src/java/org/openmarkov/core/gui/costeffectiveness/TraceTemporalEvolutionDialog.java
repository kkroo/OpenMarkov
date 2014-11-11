/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.apache.commons.io.FilenameUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openmarkov.core.exception.ImposedPoliciesException;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * Plot of temporal evolution of variables in CEA
 * @author myebra
 */
@SuppressWarnings("serial")
public class TraceTemporalEvolutionDialog extends JDialog
{
    private Map<Variable, TablePotential> temporalEvolution;
    private ChartPanel                        chartPanel;
    private JScrollPane                       tablePane;
    private JTabbedPane                       tabbedPane;
    private Variable                          variableOfInterest;
    private ProbNet                           expandedNetwork;
    private boolean                           isUtility;
    private boolean                           isCumulative;
    private CostEffectivenessAnalysis         costEffectivenessAnalysis;
    private StringDatabase                    stringDatabase = StringDatabase.getUniqueInstance ();

    public TraceTemporalEvolutionDialog (Window owner, ProbNode node, EvidenceCase evidence)
    {
        super (owner);
        ProbNet probNet = node.getProbNet ();
        this.isUtility = node.getNodeType () == NodeType.UTILITY;
        CostEffectivenessDialog costEffectivenessDialog = new CostEffectivenessDialog (owner,
                                                                                       probNet,
                                                                                       false,
                                                                                       true);
        if (costEffectivenessDialog.requestData () == CostEffectivenessDialog.OK_BUTTON)
        {
            // evidenceCase and cycleLegth null by the moment
            costEffectivenessAnalysis = new CostEffectivenessAnalysis (
                                                                       probNet,
                                                                       evidence,
                                                                       costEffectivenessDialog.getCostDiscount (),
                                                                       costEffectivenessDialog.getEffectivenessDiscount (),
                                                                       costEffectivenessDialog.getNumSlices (),
                                                                       costEffectivenessDialog.getInitialValues (),
                                                                       costEffectivenessDialog.getTransitionTime ());
            this.isCumulative = costEffectivenessDialog.isCumulative ();
            this.variableOfInterest = node.getVariable ();
            try
            {
                this.temporalEvolution = costEffectivenessAnalysis.traceTemporalEvolution (variableOfInterest);
                this.expandedNetwork = costEffectivenessAnalysis.getExpandedNetwork ();
                initialize ();
                Toolkit toolkit = Toolkit.getDefaultToolkit ();
                Dimension screenSize = toolkit.getScreenSize ();
                Rectangle bounds = owner.getBounds ();
                int width = screenSize.width / 2;
                int height = screenSize.height / 2;
                // center point of the owner window
                int x = bounds.x / 2 - width / 2;
                int y = bounds.y / 2 - height / 2;
                this.setBounds (x, y, width, height);
                setMinimumSize (new Dimension (width, height / 2));
                setLocationRelativeTo (owner);
                setResizable (true);
                repaint ();
                pack ();
                setVisible (true);
            }
            catch (ImposedPoliciesException e)
            {
                JOptionPane.showMessageDialog (owner, e.getMessage (), "Error",
                                               JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void initialize ()
    {
        setTitle (stringDatabase.getString ("TemporalEvolutionResultDialog.Title.Label") + " "
                  + variableOfInterest.getBaseName ());
        setContentPane (getJContentPane ());
        pack ();
    }

    /**
     * This method initialises jContentPane.
     * @return a new content panel.
     */
    private JPanel getJContentPane ()
    {
        JPanel jContentPane = new JPanel ();
        jContentPane.setLayout (new BorderLayout ());
        jContentPane.add (getComponentsPanel (), BorderLayout.CENTER);
        jContentPane.add (getBottomPanel (), BorderLayout.SOUTH);
        return jContentPane;
    }

    private JPanel getBottomPanel ()
    {
        JPanel buttonsPanel = new JPanel ();
        JButton jButtonSaveReport = new JButton ();
        jButtonSaveReport.setName ("jButtonSaveReport");
        jButtonSaveReport.setText (stringDatabase.getString ("Dialog.SaveReport.Label"));
        jButtonSaveReport.addActionListener (new ActionListener ()
            {
                public void actionPerformed (ActionEvent e)
                {
                    saveReport ();
                }
            });
        buttonsPanel.add (jButtonSaveReport);
        JButton jButtonClose = new JButton ();
        jButtonClose.setName ("jButtonClose");
        jButtonClose.setText (stringDatabase.getString ("Dialog.Close.Label"));
        jButtonClose.addActionListener (new ActionListener ()
            {
                public void actionPerformed (ActionEvent e)
                {
                    setVisible (false);
                    dispose ();
                }
            });
        buttonsPanel.add (jButtonClose);
        return buttonsPanel;
    }

    private Component getComponentsPanel ()
    {
        JPanel panel = new JPanel ();
        panel.setLayout (new BorderLayout (5, 5));
        panel.setMaximumSize (new Dimension (180, 40));
        panel.add (getTabbedPane ());
        pack ();
        return panel;
    }

    /**
     * This method initialises tabbedPane.
     * @return a new tabbed pane.
     */
    protected JTabbedPane getTabbedPane ()
    {
        if (tabbedPane == null)
        {
            tabbedPane = new JTabbedPane ();
            tabbedPane.setName ("TraceTemporalEvolutionTabbedPane");
            tabbedPane.addTab (stringDatabase.getString ("TemporalEvolutionChart.Title.Label"),
                               null, getChartsPanel (), null);
            tabbedPane.addTab (stringDatabase.getString ("TemporalEvolutionTable.Title.Label"),
                               null, getTablePane (), null);
        }
        return tabbedPane;
    }

    private ChartPanel getChartsPanel ()
    {
        if (chartPanel == null)
        {
            XYDataset dataset = createDataset ();
            JFreeChart chart = ChartFactory.createXYLineChart ("Temporal Evolution of: "
                                                                       + variableOfInterest.getBaseName (),
                                                               "t", "value", dataset,
                                                               PlotOrientation.VERTICAL, true,
                                                               true, true);
            // chart.getXYPlot().setRenderer(new XYSplineRenderer());
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer ();
            for (int i = 0; i < dataset.getSeriesCount (); i++)
            {
                renderer.setSeriesLinesVisible (i, true);
                renderer.setSeriesShapesVisible (i, true);
            }
            chart.getXYPlot ().setRenderer (renderer);
            // chart.getXYPlot().setRenderer(new XYSplineRenderer());
            chartPanel = new ChartPanel (chart);
            chartPanel.setAutoscrolls (true);
            chartPanel.setDisplayToolTips (true);
            chartPanel.setMouseZoomable (true);
            // XYPlot plot = (XYPlot) chart.getPlot ();
            // XYItemRenderer renderer = plot.getRenderer();
            XYToolTipGenerator generator = new StandardXYToolTipGenerator (
                                                                           "{0}: ({1}, {2})",
                                                                           new DecimalFormat (
                                                                                              "0.00"),
                                                                           new DecimalFormat (
                                                                                              "0.00"));
            renderer.setBaseToolTipGenerator (generator);
        }
        return chartPanel;
    }

    private XYDataset createDataset ()
    {
        XYSeriesCollection result = new XYSeriesCollection ();
        double value = 0.0;
        for (int i = 0; i < variableOfInterest.getNumStates (); i++)
        {
            XYSeries series = null;
            if (isUtility)
            {
                series = new XYSeries (variableOfInterest.getBaseName ());
            }
            else
            {
                series = new XYSeries (variableOfInterest.getStateName (i));
            }
            for (int j = 0; j <= costEffectivenessAnalysis.getNumSlices (); j++)
            {
                String basename = variableOfInterest.getBaseName ();
                List<ProbNode> probNodes = expandedNetwork.getProbNodes ();
                for (int k = 0; k < probNodes.size (); k++)
                {
                    if (probNodes.get (k).getVariable ().getBaseName ().equals (basename)
                        && probNodes.get (k).getVariable ().getTimeSlice () == j)
                    {
                        if (isUtility && isCumulative)
                        {
                            value += temporalEvolution.get (probNodes.get (k).getVariable ()).getValues ()[i];
                            int time = j;
                            series.add (time, value);
                        }
                        else
                        {
                            value = temporalEvolution.get (probNodes.get (k).getVariable ()).getValues ()[i];
                            int time = j;
                            series.add (time, value);
                        }
                    }
                }
            }
            result.addSeries (series);
        }
        return result;
    }

    private JScrollPane getTablePane ()
    {
        if (tablePane == null)
        {
            tablePane = new TemporalEvolutionTablePane (temporalEvolution, expandedNetwork,
                                                        variableOfInterest,
                                                        costEffectivenessAnalysis.getNumSlices (),
                                                        isUtility, isCumulative);
        }
        return tablePane;
    }

    private void saveReport ()
    {
        JFileChooser fileChooser = new JFileChooser ();
        String netName = FilenameUtils.getBaseName (expandedNetwork.getName ());
        fileChooser.setSelectedFile (new File (netName + "-" + variableOfInterest.getBaseName ()
                                               + "-temporalEvolution.xls"));
        if (fileChooser.showSaveDialog (this) == JFileChooser.APPROVE_OPTION)
        {
            String filename = fileChooser.getSelectedFile ().getAbsolutePath ();
            try
            {
                createExcel (filename);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog (this, "Error when trying to generate report in "
                                                     + filename);
            }
        }
    }

    private void createExcel (String filename)
        throws IOException
    {
        ExcelReport excel = new ExcelReport (costEffectivenessAnalysis);
        excel.createTemporalEvolutionReport (filename, temporalEvolution, expandedNetwork,
                                             costEffectivenessAnalysis.getNumSlices (),
                                             variableOfInterest);
    }
}
