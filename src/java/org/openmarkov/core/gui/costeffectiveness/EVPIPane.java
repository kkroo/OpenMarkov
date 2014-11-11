/*
 * Copyright 2012 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
 package org.openmarkov.core.gui.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openmarkov.core.gui.localize.StringDatabase;

@SuppressWarnings("serial")
public class EVPIPane extends JScrollPane implements FocusListener {
    
    private final static int DEFAULT_MAX_RATIO = 25000;
    private final static int DEFAULT_POPULATION_PER_ANNUM = 40000;
    private final static int DEFAULT_LIFETIME = 10;
    private final static double DEFAULT_DISCOUNT_RATE = 6;
    
    private XYSeriesCollection evpiDataset;
    private JTextField maxRatioTextField; 
    private JTextField yearlyPopulationTextField; 
    private JTextField lifetimeTextField; 
    private JTextField discountTextField; 
    private ProbabilisticCEA pCEA;
    private StringDatabase stringDatabase = StringDatabase.getUniqueInstance();

    public EVPIPane(ProbabilisticCEA costEffectivenessAnalysis) {
    
        this.pCEA = costEffectivenessAnalysis;
 
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        // Parameter panel
        JPanel parameterPanel = new JPanel();
        maxRatioTextField = new JTextField(7);
        maxRatioTextField.setText(""+DEFAULT_MAX_RATIO);
        maxRatioTextField.addFocusListener(this);
        JLabel maxRatioLabel = new JLabel(stringDatabase.getString("CostEffectivenessResults.EVPI.MaxRatio"));
        parameterPanel.add(maxRatioLabel);
        parameterPanel.add(maxRatioTextField);
        yearlyPopulationTextField  = new JTextField(7); 
        yearlyPopulationTextField.setText(""+DEFAULT_POPULATION_PER_ANNUM);
        yearlyPopulationTextField.addFocusListener(this);
        JLabel yearlyPopulationLabel = new JLabel(stringDatabase.getString("CostEffectivenessResults.EVPI.Population"));
        parameterPanel.add(yearlyPopulationLabel);
        parameterPanel.add(yearlyPopulationTextField);
        lifetimeTextField  = new JTextField(3);
        lifetimeTextField.setText(""+DEFAULT_LIFETIME);
        lifetimeTextField.addFocusListener(this);
        JLabel lifetimeLabel = new JLabel(stringDatabase.getString("CostEffectivenessResults.EVPI.Lifetime"));
        parameterPanel.add(lifetimeLabel);
        parameterPanel.add(lifetimeTextField);
        discountTextField  = new JTextField(3);
        discountTextField.setText(""+DEFAULT_DISCOUNT_RATE);
        discountTextField.addFocusListener(this);
        JLabel discountLabel = new JLabel(stringDatabase.getString("CostEffectivenessResults.EVPI.Discount"));
        parameterPanel.add(discountLabel);
        parameterPanel.add(discountTextField);
        panel.add(parameterPanel, BorderLayout.NORTH);
        
        // EVPI panel
        evpiDataset = createEVPIDataset (pCEA, DEFAULT_MAX_RATIO, DEFAULT_POPULATION_PER_ANNUM, DEFAULT_POPULATION_PER_ANNUM, DEFAULT_DISCOUNT_RATE);
        JFreeChart evpiChart = ChartFactory.createXYLineChart(
                stringDatabase.getString("CostEffectivenessResults.EVPI.Label"),
                stringDatabase.getString("CostEffectivenessResults.EVPI.Horizontal"),
                stringDatabase.getString("CostEffectivenessResults.EVPI.Vertical"), evpiDataset,
                PlotOrientation.VERTICAL, true, true, true);
        // chart.getXYPlot().setRenderer(new XYSplineRenderer());
        ChartPanel evpiPanel = new ChartPanel (evpiChart);
        evpiPanel.setAutoscrolls (true);
        evpiPanel.setDisplayToolTips (true);
        evpiPanel.setMouseZoomable (true);
        XYPlot plot = (XYPlot) evpiChart.getPlot ();
        XYItemRenderer renderer = plot.getRenderer ();
        NumberFormat format = new DecimalFormat ("0.00",new DecimalFormatSymbols (Locale.US));
        XYToolTipGenerator generator = new StandardXYToolTipGenerator("{0}: ({1}, {2})",
                format, format);
        renderer.setBaseToolTipGenerator (generator);  
        panel.add(evpiPanel, BorderLayout.CENTER);
        setViewportView(panel);
    }
    
    private XYSeriesCollection createEVPIDataset(ProbabilisticCEA costEffectivenessAnalysis, int maxRatio,
            int populationPerAnnum, int lifetime, double discountRate) {
        XYSeriesCollection result = new XYSeriesCollection ();
        ProbabilisticCEA pCEA = costEffectivenessAnalysis;
        Map<Integer, Double> evpi = pCEA.calculateEVPI(maxRatio, populationPerAnnum, lifetime, discountRate / 100);
        updateDataset(result, evpi);
        return result;
   }
    
    private void updateDataset(XYSeriesCollection seriesCollection, Map<Integer, Double> evpi)
    {
        List<Integer> ratios = new ArrayList<>(evpi.keySet());
        seriesCollection.removeAllSeries();
        XYSeries series = new XYSeries ("EVPI");
        for (int ratio : ratios)
        {
            series.add((double)ratio, evpi.get(ratio));
        }
        seriesCollection.addSeries (series);
    }

    @Override
    public void focusGained(FocusEvent event) {
        // Ignore
        
    }

    @Override
    public void focusLost(FocusEvent event) {
        try
        {
            int maxRatio = Integer.parseInt(maxRatioTextField.getText()); 
            int populationPerAnnum = Integer.parseInt(yearlyPopulationTextField.getText()); 
            int lifetime = Integer.parseInt(lifetimeTextField.getText()); 
            double discountRate = Double.parseDouble(discountTextField.getText());
            
            Map<Integer, Double> evpi = pCEA.calculateEVPI(maxRatio, populationPerAnnum, lifetime, discountRate / 100);
            updateDataset(evpiDataset, evpi);
            
        }catch(NumberFormatException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Please introduce proper number in the text fields", "Formatting error",
                    JOptionPane.ERROR_MESSAGE);
        }
        
    }    
}
