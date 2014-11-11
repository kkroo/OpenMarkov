package org.openmarkov.core.gui.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;

@SuppressWarnings("serial")
public class CostEffectivenessProgressBar extends JDialog implements PropertyChangeListener {

    private Window parent;
    private JProgressBar progressBar;
    private JLabel elapsedTimeLabel;
    private JLabel remainingTimeLabel;
    private PSATask         task;
    private ProbabilisticCEA costEffectivenessAnalysis;
    private long startTime;

    class PSATask extends SwingWorker<Void, Void> {
        
        private Window parent;
        private ProbabilisticCEA ceAnalysis;
        private long startTime;
        private int progress = 0;;
        
        public PSATask(Window parent, ProbabilisticCEA ceAnalysis)
        {
            this.parent = parent;
            this.ceAnalysis = ceAnalysis;
        }
        
        @Override
        public Void doInBackground() {
            
            progress = ceAnalysis.getProgress();
            startTime = System.currentTimeMillis();
            new Thread(ceAnalysis).start();
            while (progress < 100) {
                setProgress(progress);
                // Sleep for 100 milliseconds.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                }
                // Make random progress.
                progress = ceAnalysis.getProgress();
            }
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            if(progress == 100)
            {
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                JOptionPane.showMessageDialog(null, "PSA took " + elapsedTime + " seconds.");          
                setCursor(null); // turn off the wait cursor
                JDialog ceaResultsDialog = new CostEffectivenessResultsDialog(parent,
                        costEffectivenessAnalysis);
                ceaResultsDialog.setVisible(true);
            }
        }
    }

    public CostEffectivenessProgressBar(Window window, ProbNet probNet, EvidenceCase evidence,
            CostEffectivenessDialog costEffectivenessDialog) {
        this.parent = window;
        
        JPanel panel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        
        elapsedTimeLabel = new JLabel("Time elapsed: 0 seconds.");
        remainingTimeLabel = new JLabel("Estimated remaining time: --.");

        panel.add(elapsedTimeLabel, BorderLayout.NORTH);
        panel.add(remainingTimeLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        setTitle("Running PSA...");
        setIconImage(null);
        add(panel, BorderLayout.PAGE_START);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(panel);
        pack();
        
        setResizable(false);
        
        Toolkit toolkit = Toolkit.getDefaultToolkit ();
        Dimension screenSize = toolkit.getScreenSize ();
        int x = (int) (screenSize.getWidth() - getSize().getWidth()) / 2;
        int y = (int) (screenSize.getHeight() - getSize().getHeight()) / 2;
        setLocation(new Point(x, y));
        
        costEffectivenessAnalysis = new ProbabilisticCEA(probNet,
                evidence,
                costEffectivenessDialog.getCostDiscount(),
                costEffectivenessDialog.getEffectivenessDiscount(),
                costEffectivenessDialog.getNumSlices(),
                costEffectivenessDialog.getNumSimulations(),
                costEffectivenessDialog.getInitialValues(),
                costEffectivenessDialog.getTransitionTime());
        
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                /* code run when component shown */
                runAnalysis();
            }
        });
        
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                if (task != null) {
                    task.cancel(true);
                }
            }

            public void windowClosed(WindowEvent e) {
                if (task != null) {
                    task.cancel(true);
                }
            }
        });
    }
    
    private void runAnalysis()
    {
        task = new PSATask(parent, costEffectivenessAnalysis);
        task.addPropertyChangeListener(this);
        startTime = System.currentTimeMillis();
        task.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            if(progress == 99)
            {
                setVisible(false);
            }
            long elapsedTime = (System.currentTimeMillis() - startTime)/1000;
            long remainingTime = (elapsedTime * 100 / progress) - elapsedTime;
            elapsedTimeLabel.setText("Time elapsed: " + elapsedTime + " seconds.");
            remainingTimeLabel.setText("Estimated remaining time: " + remainingTime + " seconds.");
        }
    }
}